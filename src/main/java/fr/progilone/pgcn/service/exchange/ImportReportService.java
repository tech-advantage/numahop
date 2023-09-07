package fr.progilone.pgcn.service.exchange;

import fr.progilone.pgcn.domain.document.DocUnit;
import fr.progilone.pgcn.domain.exchange.*;
import fr.progilone.pgcn.domain.library.Library;
import fr.progilone.pgcn.domain.lot.Lot;
import fr.progilone.pgcn.domain.project.Project;
import fr.progilone.pgcn.exception.PgcnTechnicalException;
import fr.progilone.pgcn.repository.document.DocUnitRepository;
import fr.progilone.pgcn.repository.document.conditionreport.ConditionReportRepository;
import fr.progilone.pgcn.repository.exchange.ImportReportRepository;
import fr.progilone.pgcn.repository.exchange.ImportedDocUnitRepository;
import fr.progilone.pgcn.repository.imagemetadata.ImageMetadataValuesRepository;
import fr.progilone.pgcn.security.SecurityUtils;
import fr.progilone.pgcn.service.storage.FileStorageManager;
import fr.progilone.pgcn.service.util.transaction.TransactionService;
import fr.progilone.pgcn.service.util.transaction.TransactionalJobRunner;
import fr.progilone.pgcn.web.websocket.WebsocketService;
import jakarta.annotation.PostConstruct;
import jakarta.xml.bind.UnmarshalException;
import java.io.File;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.*;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.marc4j.MarcException;
import org.marc4j.util.JsonParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.xml.sax.SAXParseException;

/**
 * Created by Sebastien on 08/12/2016.
 */
@Service
public class ImportReportService {

    private static final Logger LOG = LoggerFactory.getLogger(ImportReportService.class);

    private static final int BATCH_SIZE = 50;

    private final DocUnitRepository docUnitRepository;
    private final ConditionReportRepository conditionReportRepository;
    private final FileStorageManager fm;
    private final ImportReportRepository importReportRepository;
    private final ImportedDocUnitRepository importedDocUnitRepository;
    private final ImageMetadataValuesRepository imageMetadataValuesRepository;
    private final TransactionService transactionService;
    private final WebsocketService websocketService;

    // Stockage des fichiers importés
    @Value("${uploadPath.import}")
    private String importDir;

    @Autowired
    public ImportReportService(final DocUnitRepository docUnitRepository,
                               final FileStorageManager fm,
                               final ImportReportRepository importReportRepository,
                               final ImportedDocUnitRepository importedDocUnitRepository,
                               final TransactionService transactionService,
                               final WebsocketService websocketService,
                               final ConditionReportRepository conditionReportRepository,
                               final ImageMetadataValuesRepository imageMetadataValuesRepository) {
        this.docUnitRepository = docUnitRepository;
        this.conditionReportRepository = conditionReportRepository;
        this.fm = fm;
        this.importReportRepository = importReportRepository;
        this.importedDocUnitRepository = importedDocUnitRepository;
        this.imageMetadataValuesRepository = imageMetadataValuesRepository;
        this.transactionService = transactionService;
        this.websocketService = websocketService;
    }

    @PostConstruct
    @Transactional
    public void init() {
        // Création du répertoire d'historique s'il n'existe pas
        fm.initializeStorage(importDir);

        // Mise à jour du statut des rapports en cours d'exécution au démarrage de l'application
        final List<ImportReport> interruptedImports = importReportRepository.findByStatusIn(ImportReport.Status.PENDING,
                                                                                            ImportReport.Status.PRE_IMPORTING,
                                                                                            ImportReport.Status.DEDUPLICATING,
                                                                                            ImportReport.Status.IMPORTING);
        for (final ImportReport report : interruptedImports) {
            LOG.warn("L'import des fichiers {}, démarré le {}, a été interrompu au statut {}", report.getFilesAsString(), report.getStart(), report.getStatus());
            failReport(report, "L'import a été interrompu en cours d'exécution");
        }
    }

    /**
     * Initialisation et sauvegarde d'un rapport d'exécution d'import
     *
     * @param fileName
     * @param fileSize
     * @param fileFormat
     * @param dataEncoding
     * @param libraryId
     * @param mappingId
     * @return
     */
    @Transactional
    public ImportReport createSimpleImportReport(final String fileName,
                                                 final Long fileSize,
                                                 final FileFormat fileFormat,
                                                 final DataEncoding dataEncoding,
                                                 final String libraryId,
                                                 final String projectId,
                                                 final String lotId,
                                                 final String mappingId) {

        final ImportReport report = new ImportReport();
        report.setType(ImportReport.Type.SIMPLE);
        report.setFileFormat(fileFormat);
        report.setDataEncoding(dataEncoding);
        report.addFile(new ImportReport.ImportedFile(fileName, fileSize));
        report.setStatus(ImportReport.Status.PENDING);
        report.setRunBy(SecurityUtils.getCurrentLogin());

        final Library library = new Library();
        library.setIdentifier(libraryId);
        report.setLibrary(library);

        if (mappingId != null) {
            final Mapping mapping = new Mapping();
            mapping.setIdentifier(mappingId);
            report.setMapping(mapping);
        }
        if (projectId != null) {
            final Project project = new Project();
            project.setIdentifier(projectId);
            report.setProject(project);
        }
        if (lotId != null) {
            final Lot lot = new Lot();
            lot.setIdentifier(lotId);
            report.setLot(lot);
        }
        return importReportRepository.save(report);
    }

    @Transactional
    public ImportReport createImportReport(final List<MultipartFile> files,
                                           final ImportReport.Type type,
                                           final FileFormat fileFormat,
                                           final DataEncoding dataEncoding,
                                           final String libraryId,
                                           final String projectId,
                                           final String lotId,
                                           final String mappingId,
                                           final String mappingChildrenId,
                                           final String parentReportId,
                                           final String join) {

        final ImportReport report = new ImportReport();
        report.setType(type);
        report.setFileFormat(fileFormat);
        report.setDataEncoding(dataEncoding);
        report.setStatus(ImportReport.Status.PENDING);
        report.setRunBy(SecurityUtils.getCurrentLogin());
        report.setJoinExpression(join);

        files.stream().map(this::getImportedFile).forEach(report::addFile);

        final Library library = new Library();
        library.setIdentifier(libraryId);
        report.setLibrary(library);

        final Mapping mapping = new Mapping();
        mapping.setIdentifier(mappingId);
        report.setMapping(mapping);

        if (mappingChildrenId != null) {
            final Mapping mappingChildren = new Mapping();
            mappingChildren.setIdentifier(mappingChildrenId);
            report.setMappingChildren(mappingChildren);
        }
        if (parentReportId != null) {
            final ImportReport parentReport = new ImportReport();
            parentReport.setIdentifier(parentReportId);
            report.setParentReport(parentReport);
        }
        if (projectId != null) {
            final Project project = new Project();
            project.setIdentifier(projectId);
            report.setProject(project);
        }
        if (lotId != null) {
            final Lot lot = new Lot();
            lot.setIdentifier(lotId);
            report.setLot(lot);
        }
        return importReportRepository.save(report);
    }

    /**
     * Initialisation et sauvegarde d'un rapport d'exécution d'import
     *
     * @param files
     * @param type
     * @param fileFormat
     * @param dataEncoding
     * @param libraryId
     * @param mappingId
     * @param join
     * @return
     */
    @Transactional
    public ImportReport createCSVImportReport(final List<MultipartFile> files,
                                              final ImportReport.Type type,
                                              final FileFormat fileFormat,
                                              final DataEncoding dataEncoding,
                                              final String libraryId,
                                              final String projectId,
                                              final String lotId,
                                              final String mappingId,
                                              final String parentReportId,
                                              final String join) {
        final ImportReport report = new ImportReport();
        report.setType(type);
        report.setFileFormat(fileFormat);
        report.setDataEncoding(dataEncoding);
        report.setStatus(ImportReport.Status.PENDING);
        report.setRunBy(SecurityUtils.getCurrentLogin());
        report.setJoinExpression(join);

        final CSVMapping mapping = new CSVMapping();
        mapping.setIdentifier(mappingId);
        report.setCsvMapping(mapping);

        final Library library = new Library();
        library.setIdentifier(libraryId);
        report.setLibrary(library);

        if (parentReportId != null) {
            final ImportReport parentReport = new ImportReport();
            parentReport.setIdentifier(parentReportId);
            report.setParentReport(parentReport);
        }
        if (projectId != null) {
            final Project project = new Project();
            project.setIdentifier(projectId);
            report.setProject(project);
        }
        if (lotId != null) {
            final Lot lot = new Lot();
            lot.setIdentifier(lotId);
            report.setLot(lot);
        }

        files.stream().map(this::getImportedFile).forEach(report::addFile);

        return importReportRepository.save(report);
    }

    public ImportReport createDCImportReport(final List<MultipartFile> files,
                                             final ImportReport.Type type,
                                             final FileFormat fileFormat,
                                             final DataEncoding dataEncoding,
                                             final String libraryId,
                                             final String projectId,
                                             final String lotId,
                                             final String mapping,
                                             final String join) {
        final Library library = new Library();
        library.setIdentifier(libraryId);

        final ImportReport report = new ImportReport();
        report.setType(type);
        report.setFileFormat(fileFormat);
        report.setDataEncoding(dataEncoding);
        report.setLibrary(library);
        report.setAdditionnalMapping(mapping);
        report.setStatus(ImportReport.Status.PENDING);
        report.setRunBy(SecurityUtils.getCurrentLogin());
        report.setJoinExpression(join);

        if (projectId != null) {
            final Project project = new Project();
            project.setIdentifier(projectId);
            report.setProject(project);
        }
        if (lotId != null) {
            final Lot lot = new Lot();
            lot.setIdentifier(lotId);
            report.setLot(lot);
        }

        files.stream().map(this::getImportedFile).forEach(report::addFile);

        return importReportRepository.save(report);
    }

    /**
     * Début de l'import
     *
     * @param importReport
     * @return
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public ImportReport startReport(final ImportReport importReport) {
        importReport.setStart(LocalDateTime.now());
        importReport.setStatus(ImportReport.Status.PRE_IMPORTING);

        final ImportReport savedReport = importReportRepository.save(importReport);
        sendUpdate(savedReport);
        return savedReport;
    }

    /**
     * Mise à jour du statut du rapport
     *
     * @param importReport
     * @param status
     * @return
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public ImportReport setReportStatus(final ImportReport importReport, final ImportReport.Status status) {
        importReport.setStatus(status);

        final ImportReport savedReport = importReportRepository.save(importReport);
        sendUpdate(savedReport);
        return savedReport;
    }

    /**
     * Fin de l'import: COMPLETED
     *
     * @param importReport
     * @return
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public ImportReport endReport(final ImportReport importReport) {
        final LocalDateTime now = LocalDateTime.now();
        if (importReport.getStatus() != ImportReport.Status.FAILED) {
            importReport.setStatus(ImportReport.Status.COMPLETED);
        }
        if (importReport.getStart() == null) {
            importReport.setStart(now);
        }
        importReport.setEnd(now);

        final ImportReport savedReport = importReportRepository.save(importReport);
        sendUpdate(savedReport);
        return savedReport;
    }

    /**
     * Fin de l'import: FAILED
     *
     * @param importReport
     * @return
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public ImportReport failReport(ImportReport importReport, final String message) {
        // Rechargement du rapport
        importReport = importReportRepository.findByIdentifier(importReport.getIdentifier());

        final LocalDateTime now = LocalDateTime.now();
        if (importReport.getStart() == null) {
            importReport.setStart(now);
        }
        importReport.setEnd(now);
        importReport.setMessage("Arrêt imprévu du traitement au statut " + importReport.getStatus()
                                + " avec l'erreur: "
                                + message);
        importReport.setStatus(ImportReport.Status.FAILED);

        final ImportReport savedReport = importReportRepository.save(importReport);
        sendUpdate(savedReport);
        return savedReport;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public ImportReport failReport(final ImportReport importReport, final Throwable e) {
        String message = findKnownException(e);
        if (message == null && e != null) {
            message = e.toString();
        }
        return failReport(importReport, message);
    }

    /**
     * Envoie la mise à jour du rapport par websocket
     *
     * @param report
     */
    private void sendUpdate(final ImportReport report) {
        websocketService.sendObject(report.getIdentifier(), getStatus(report));
    }

    /**
     * Construction d'une {@link Map} contenant les informations sur le statut du rapport d'import
     *
     * @param report
     * @return
     */
    public Map<String, Object> getStatus(final ImportReport report) {
        final Map<String, Object> response = new HashMap<>();
        response.put("identifier", report.getIdentifier());
        response.put("status", report.getStatus());
        response.put("nbImp", report.getNbImp());

        if (report.getStart() != null) {
            response.put("start", report.getStart());
        }
        if (report.getEnd() != null) {
            response.put("end", report.getEnd());
        }
        return response;
    }

    @Transactional(readOnly = true)
    public List<ImportReport> findAll() {
        return importReportRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Page<ImportReport> findAllByLibraryIn(final List<String> libraries, final int page, final int size) {
        final Sort order = Sort.by(Sort.Direction.DESC, "createdDate");
        final Pageable pageable = PageRequest.of(page, size, order);

        if (CollectionUtils.isEmpty(libraries)) {
            return importReportRepository.findAll(pageable);
        } else {
            return importReportRepository.findByLibraryIdentifierIn(libraries, pageable);
        }
    }

    @Transactional(readOnly = true)
    public ImportReport findByIdentifier(final String identifier) {
        return importReportRepository.findByIdentifier(identifier);
    }

    @Transactional(readOnly = true)
    public Page<ImportReport> search(final String search,
                                     final List<String> users,
                                     final List<ImportReport.Status> status,
                                     final List<String> libraries,
                                     final Integer page,
                                     final Integer size) {
        final Pageable pageRequest = PageRequest.of(page, size);
        return importReportRepository.search(search, users, status, libraries, pageRequest);
    }

    /**
     * Suppression d'un rapport
     *
     * @param identifier
     */
    // @Transactional
    public void delete(final String identifier) {
        final ImportReport report = importReportRepository.findByIdentifier(identifier);
        if (report == null) {
            LOG.warn("Rapport d'import {} non trouvé. La suppression est annulée", identifier);
            return;
        }

        final Path path = Paths.get(importDir, report.getLibrary().getIdentifier());
        // Suppression du fichier d'import
        final File importedDir = new File(path.toFile(), report.getIdentifier());
        LOG.debug("Suppression du répertoire {}", importedDir.getAbsolutePath());
        FileUtils.deleteQuietly(importedDir);

        // Suppression des liens rapport parent / enfant
        transactionService.executeInNewTransaction(() -> {
            importReportRepository.setParentNull(identifier);
        });

        // Suppression des dépendances exc_doc_unit + doc_unit[NOT_AVAILABLE]
        final List<String> importedIds = importReportRepository.findImportIdentifiersByReport(identifier);
        final List<String> docUnitIds = importReportRepository.findDocUnitIdentifiersByReportAndDocUnitState(identifier, DocUnit.State.NOT_AVAILABLE);

        LOG.debug("Suppression des références aux unités documentaires dans le rapport d'import {}", identifier);
        new TransactionalJobRunner<>(importedIds, transactionService).setCommit(BATCH_SIZE).forEachGroup(BATCH_SIZE, ids -> {
            importedDocUnitRepository.deleteDuplicatedUnitsByImportedDocUnitIds(ids);
            importedDocUnitRepository.deleteMessagesByImportedDocUnitIds(ids);
            importedDocUnitRepository.deleteByIds(ids);
            return true;
        }).process();
        LOG.debug("Suppression des unités documentaires liées au rapport d'import {}", identifier);
        new TransactionalJobRunner<>(docUnitIds, transactionService).setCommit(BATCH_SIZE).forEachGroup(BATCH_SIZE, ids -> {
            docUnitRepository.setParentNullByParentIdIn(ids);
            return true;
        }).process();
        new TransactionalJobRunner<>(docUnitIds, transactionService).setCommit(BATCH_SIZE).forEachGroup(BATCH_SIZE, ids -> {
            final List<DocUnit> docUnits = docUnitRepository.findByIdentifierIn(ids);
            docUnitRepository.deleteAll(docUnits);
            return true;
        }).process();

        // Suppression du rapport
        LOG.debug("Suppression du rapport d'import {}", identifier);
        importReportRepository.deleteById(identifier);
    }

    /**
     * Suppression de tous les imports d'une bibliothèque
     *
     * @param identifier
     */
    public void deleteByLibrary(final String identifier) {
        importReportRepository.findByLibraryIdentifier(identifier).forEach(report -> delete(report.getIdentifier()));
    }

    @Transactional
    public void setLotNull(final List<String> lotIds) {
        importReportRepository.setLotNullByLotIdIn(lotIds);
    }

    @Transactional
    public void setProjectNull(final List<String> projectIds) {
        importReportRepository.setProjectNullByProjectIdIn(projectIds);
    }

    /**
     * Récupère le fichier importé
     *
     * @param report
     * @param fileName
     * @return
     */
    public File getImportFile(final ImportReport report, final String fileName) {
        return fm.retrieveFile(importDir, report, fileName);
    }

    /**
     * Sauvegarde le fichier importé
     *
     * @param source
     * @param importReport
     * @param fileName
     * @return
     */
    public File saveImportFile(final InputStream source, final ImportReport importReport, final String fileName) {

        final Path root = Paths.get(importDir);
        final String destName = Base64.getEncoder().encodeToString(fileName.getBytes(StandardCharsets.UTF_8));

        return fm.copyInputStreamToFileWithOtherDirs(source, root.toFile(), Arrays.asList(importReport.getIdentifier()), destName, true, true);
    }

    /**
     * Renvoie un message plus explicite pour les erreurs les plus courantes
     *
     * @param e
     * @return
     */
    private String findKnownException(final Throwable e) {
        if (e == null) {
            return null;
        }
        String message = null;

        if (e instanceof PgcnTechnicalException) {
            message = findKnownException(e.getCause());

            if (StringUtils.isBlank(message)) {
                message = e.getMessage();
            }
            return message;

        } else if (e instanceof MarcException) {
            message = "la lecture du fichier MARC a échoué";

        } else if (e instanceof SAXParseException) {
            message = "la lecture du fichier XML a échoué";

        } else if (e instanceof UnmarshalException && e.getCause() == null) {
            message = "la lecture du fichier XML a échoué";

        } else if (e instanceof JsonParser.Escape) {
            message = "la lecture du fichier JSON a échoué";

        } else if (e instanceof DataIntegrityViolationException) {
            if (StringUtils.contains(e.getMessage(), "uniq_doc_unit_pgcn_id_state")) {
                message = "l'import a généré plusieurs unités documentaires avec un PgcnId identique";
            }
        }
        if (StringUtils.isNotBlank(message)) {
            message += " (message original: " + StringUtils.join(getStackMessages(e), ", ")
                       + ")";
            return message;
        }
        return findKnownException(e.getCause());
    }

    /**
     * Récupération des messages de la stacktrace
     *
     * @param e
     * @return
     */
    private List<String> getStackMessages(final Throwable e) {
        if (e != null) {
            final Throwable cause = e.getCause();
            final List<String> messages = new ArrayList<>(getStackMessages(cause));

            if (StringUtils.isNotEmpty(e.getMessage())) {
                messages.add(0, e.getMessage());
            }
            return messages;
        }
        return Collections.emptyList();
    }

    private ImportReport.ImportedFile getImportedFile(final MultipartFile file) {
        return new ImportReport.ImportedFile(new File(file.getOriginalFilename()).getName(), file.getSize());
    }
}
