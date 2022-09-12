package fr.progilone.pgcn.service.exchange.omeka;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.io.Writer;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.zip.ZipOutputStream;

import javax.annotation.PostConstruct;
import javax.xml.bind.JAXBException;

import fr.progilone.pgcn.service.util.CryptoService;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.xml.sax.SAXException;

import fr.progilone.pgcn.domain.administration.SftpConfiguration;
import fr.progilone.pgcn.domain.administration.omeka.OmekaConfiguration;
import fr.progilone.pgcn.domain.administration.viewsformat.ViewsFormatConfiguration;
import fr.progilone.pgcn.domain.document.BibliographicRecord;
import fr.progilone.pgcn.domain.document.DocPropertyType;
import fr.progilone.pgcn.domain.document.DocUnit;
import fr.progilone.pgcn.domain.document.DocUnit.ExportStatus;
import fr.progilone.pgcn.domain.document.ExportData;
import fr.progilone.pgcn.domain.document.ExportProperty;
import fr.progilone.pgcn.domain.dto.document.BibliographicRecordDcDTO;
import fr.progilone.pgcn.domain.dto.document.DocPropertyDTO;
import fr.progilone.pgcn.domain.library.Library;
import fr.progilone.pgcn.domain.storage.CheckSummedStoredFile;
import fr.progilone.pgcn.domain.storage.StoredFile;
import fr.progilone.pgcn.domain.workflow.WorkflowStateKey;
import fr.progilone.pgcn.exception.PgcnTechnicalException;
import fr.progilone.pgcn.exception.PgcnUncheckedException;
import fr.progilone.pgcn.service.MailService;
import fr.progilone.pgcn.service.administration.omeka.OmekaConfigurationService;
import fr.progilone.pgcn.service.document.DocPropertyTypeService;
import fr.progilone.pgcn.service.document.DocUnitService;
import fr.progilone.pgcn.service.document.ui.UIBibliographicRecordService;
import fr.progilone.pgcn.service.exchange.ssh.FTPClientService;
import fr.progilone.pgcn.service.exchange.ssh.SftpService;
import fr.progilone.pgcn.service.library.LibraryService;
import fr.progilone.pgcn.service.storage.BinaryStorageManager;
import fr.progilone.pgcn.service.storage.FileCleaningManager;
import fr.progilone.pgcn.service.util.ImageUtils;

@Service
public class OmekaService {

    private static final Logger LOG = LoggerFactory.getLogger(OmekaService.class);

    @Value("${instance.libraries}")
    private String[] instanceLibraries;

    @Value("${services.omeka.cache}")
    private String workingDir;

    private static final String CSV_COL_SEP = "\t";
    private static final String CSV_REPEATED_FIELD_SEP = "|";
    private static final String EMPTY_FIELD_VALUE = "Non renseigné";
    private static final String NEW_LINE_SEPARATOR = "\n";
    private static final String JPG_EXT = ".jpg";

    private static final String CSV_NAME_MASS_EXPORT = "docs_export.csv";

    private final LibraryService libraryService;
    private final BinaryStorageManager bm;
    private final DocUnitService docUnitService;
    private final OmekaConfigurationService omekaConfigurationService;
    private final UIBibliographicRecordService uiBibliographicRecordService;
    private final SftpService sftpService;
    private final FTPClientService ftpService;
    private final FileCleaningManager fileCleaningManager;
    private final MailService mailService;
    private final DocPropertyTypeService docPropertyTypeService;
    private final CryptoService cryptoService;

    @Autowired
    public OmekaService(final LibraryService libraryService,
                        final OmekaConfigurationService omekaConfigurationService,
                        final BinaryStorageManager bm, final DocUnitService docUnitService,
                        final UIBibliographicRecordService uiBibliographicRecordService,
                        final SftpService sftpService,
                        final FTPClientService ftpService,
                        final FileCleaningManager fileCleaningManager,
                        final MailService mailService,
                        final DocPropertyTypeService docPropertyTypeService,
                        final CryptoService cryptoService) {
        this.libraryService = libraryService;
        this.omekaConfigurationService = omekaConfigurationService;
        this.bm = bm;
        this.docUnitService = docUnitService;
        this.uiBibliographicRecordService = uiBibliographicRecordService;
        this.sftpService = sftpService;
        this.ftpService = ftpService;
        this.fileCleaningManager = fileCleaningManager;
        this.mailService = mailService;
        this.docPropertyTypeService = docPropertyTypeService;
        this.cryptoService = cryptoService;
    }

    @PostConstruct
    public void initialize() {

     // 1 disk space per library
        Arrays.asList(instanceLibraries).forEach(lib -> {
            try {
                FileUtils.forceMkdir(new File(workingDir, lib));
            } catch (final IOException ex) {
                LOG.error(ex.getMessage(), ex);
            }
        });
    }

    /**
     * Retrouve les docUnit candidates pour diffusion vers Omeka.
     * (diffusable et non envoyée vers Omeka - avec notice contenant 1 propriete de type 'identifier' - workflow termine ou en attente de diffusion)
     *
     * @return
     */
    @Transactional(readOnly = true)
    public List<DocUnit> findDocUnitsReadyForOmekaExport(final Library lib) {

        final List<DocUnit> docsToExport = new ArrayList<>();

        final List<DocUnit> distribuables = docUnitService.findByLibraryWithOmekaExportDep(lib.getIdentifier());
                   distribuables.stream().filter(doc -> {
                         return CollectionUtils.isNotEmpty(doc.getRecords())
                               && (doc.getWorkflow().getCurrentStateByKey(WorkflowStateKey.DIFFUSION_DOCUMENT_OMEKA) != null
                                               && doc.getWorkflow().getCurrentStateByKey(WorkflowStateKey.DIFFUSION_DOCUMENT_OMEKA).isCurrentState());
                   })
                   .forEach(docsToExport::add);
        LOG.debug("OMEKA :  " + docsToExport.size()  +" Docs recuperes pour l'export pour la bibliothèque {}", lib.getName());
        return docsToExport;
    }


    @Transactional
    public boolean exportDocToOmeka(final DocUnit du, final boolean multiple, final boolean lastDoc, final boolean firstDoc) {

        boolean exported = false;
        // recharge docUnit
        final DocUnit doc = docUnitService.findOneWithAllDependencies(du.getIdentifier());
        updateDocUnitOmekaStatus(doc, ExportStatus.IN_PROGRESS);

        // Omeka config.
        OmekaConfiguration conf = null;
        if (doc.getLot().getOmekaConfiguration() != null) {
            conf = doc.getLot().getOmekaConfiguration();
        }
        if (conf == null) {
            conf = omekaConfigurationService.findByLibraryAndActive(doc.getLibrary(), true).stream().findFirst().orElse(null);
        }
        if (conf == null) {
            LOG.trace("Omeka Conf. introuvable => DocUnit[{}] - Export Omeka impossible.", doc.getIdentifier());
            updateDocUnitOmekaStatus(doc, ExportStatus.FAILED);
            return exported;
        }
        // Recup données de la notice.
        final BibliographicRecordDcDTO metaDC = getDataRecord(doc);
        if (metaDC == null) {
            LOG.trace("Omeka - Notice introuvable => DocUnit[{}] - Export Omeka impossible.", doc.getIdentifier());
            return exported;
        }
        Path path = null;
        try {
            // Génération des fichiers / répertoires OMEKA
            path = exportDocUnit(doc, metaDC, false, doc.getPgcnId(), conf, multiple, firstDoc);
            // on zippe le tout
            final String name = doc.getPgcnId();
            path = compressOmekaFiles(path.toFile(), name);

            // Tranferts du dossier généré
            if (path != null && path.toFile().exists()) {
                // Envoi des documents en SFTP
                if (conf.isSftp()) {
                    final SftpConfiguration sftpConf = new SftpConfiguration();
                    sftpConf.setActive(true);
                    sftpConf.setHost(conf.getStorageServer());
                    sftpConf.setPort(Integer.valueOf(conf.getPort()));
                    sftpConf.setUsername(conf.getLogin());
                    sftpConf.setPassword(cryptoService.encrypt(conf.getPassword()));
                    sftpConf.setTargetDir(conf.getAddress());
                    try {
                        sftpService.sftpPut(sftpConf, path);
                        exported = true;
                    } catch (final PgcnTechnicalException e) {
                        LOG.error("Erreur Export OMEKA", e);
                        exported = false;
                    }

                    // Envoi des documents en FTP
                } else {
                    try {
                        ftpService.ftpPut(conf.getStorageServer(), conf.getPort(), conf.getLogin(), conf.getPassword(), conf.getAddress(), path);
                        exported = true;
                    } catch (final PgcnTechnicalException e) {
                        LOG.error("Erreur Export OMEKA", e);
                        exported = false;
                    }
                }
            }
        } catch (final Exception e) {
            LOG.error("Erreur Export OMEKA", e);
            exported = false;
        }

        if (exported) {
            updateDocUnitOmekaStatus(doc, ExportStatus.SENT);
            // Suppression du répertoire généré, après le traitement si sent.
            LOG.debug("Suppression du répertoire {}", path.toAbsolutePath().toString());
            FileUtils.deleteQuietly(path.toFile());
        } else {
            updateDocUnitOmekaStatus(doc, ExportStatus.FAILED);
        }

        // Envoi mail avec le fichier csv.
        if (!multiple || lastDoc) {
            sendCsvFile(path.getParent(), conf.getMailCsv(), multiple, doc);
        }
        return exported;
    }

    /**
     * Envoi du csv pour import Omeka par mail.
     *
     */
    public void sendCsvFile(final Path rootPath, final String mailTo, final boolean multiple, final DocUnit docUnit) {

        final String[] to = {mailTo};
        final File csvDir = rootPath.toFile();
        final String csvName = multiple ? CSV_NAME_MASS_EXPORT :
                                        docUnit.getPgcnId() + ".csv";
        final File csvFile = new File(csvDir, csvName);
        if (mailService.sendEmailWithAttachment(null, to, "Fichier CSV Import Omeka", "Cf. ci-joint", csvFile, "text/plain", true, false)) {
            // OK on peut supprimer le csv
            FileUtils.deleteQuietly(csvFile);
        }
    }


    /**
     * On zippe le contenu du repertoire à exporter.
     *
     */
    public Path compressOmekaFiles(final File destDir, final String name) {

        if (destDir == null || !destDir.exists() || !destDir.canWrite()) {
            // bien peu probable mais sait-on jamais...
            return null;
        }

        // Création du fichier
        final File zipFile =
            new File(destDir.getParentFile().getPath().concat(System.getProperty("file.separator")).concat(name).concat(".zip"));
        try {
            if (!zipFile.createNewFile()) {
                LOG.warn("Le fichier fichier {} existe deja, il sera remplacé", name+".zip");
            }
        } catch (final IOException e) {
            LOG.error(e.getMessage(), e);
            return null;
        }

        boolean zipped = false;
        try (final FileOutputStream fos = new FileOutputStream(zipFile); final ZipOutputStream zipOut = new ZipOutputStream(fos)) {

            fileCleaningManager.zip(destDir, destDir.getName(), zipOut);
            LOG.trace("Archive ZIP {} créée dans le dossier {}", zipFile.getName(), destDir.toString());
            zipped = true;

        } catch (final IOException | SecurityException e) {
            LOG.error("Erreur lors de la compression des fichiers sauvegardes", e);
        }

        // ZIP ok => nettoie le repertoire.
        if (zipped) {
            try (final Stream<Path> stream = Files.walk(destDir.toPath(), FileVisitOption.FOLLOW_LINKS)) {

                stream.sorted(Comparator.reverseOrder()).map(Path::toFile).forEach(File::delete);
                stream.close();
            } catch (final IOException | SecurityException e) {
                LOG.error("Erreur lors de la suppression des documents livrés dans {}", destDir.getName(), e);
            }
        }
        return zipFile.toPath();
    }


    @Transactional
    public void updateDocUnitOmekaStatus(final DocUnit docUnit, final ExportStatus status) {
        docUnit.setOmekaExportStatus(status);
        docUnit.setOmekaExportDate(LocalDateTime.now());
        docUnitService.saveWithoutValidation(docUnit);
    }


    /**
     * <h1>Export OMEKA</h1>
     * <p>La PGCN génère un dossier contenant les views du document et un fichier csv.</p>
     * <p>1 dossier par document numérique (le nom du dossier est l’identifiant)</p>
     *
     * <p>L'envoi doit se faire via un transfert SFTP. </p>
     * <p>Chaque bibliothèque est considérée comme un propre service versant. Ainsi, elle a son propre espace de connexion.</p>
     *
     * @throws NoSuchAlgorithmException
     * @throws PgcnTechnicalException
     * @throws SAXException
     */
    @Transactional
    public Path exportDocUnit(final DocUnit docUnit, final BibliographicRecordDcDTO metaDc, final boolean metaEad,
                               final String directory, final OmekaConfiguration conf, final boolean multiple, final boolean firstDoc) throws IOException,
                                                                                                                    JAXBException,
                                                                                                                    PgcnTechnicalException,
                                                                                                                    SAXException,
                                                                                                                    NoSuchAlgorithmException {

        final String libraryId = docUnit.getLibrary().getIdentifier();
        final Path root = Paths.get(workingDir, libraryId, directory);
        // Suppression du répertoire s'il existe
        if (Files.exists(root)) {
            LOG.warn("Le répertoire {} est supprimé car il existe déjà", root);
            FileUtils.deleteDirectory(root.toFile());
        }
        // Création du répertoire
        LOG.debug("Création du répertoire {}", root);
        final Path depotPath = createDirectories(root, conf);

        try {
            // Ajout des fichiers à archiver
            final List<CheckSummedStoredFile> listWithCheckSummedStoredFiles = addDepotFiles(docUnit, depotPath, conf);
            // csv omeka
            createDocUnitOmekaCsv(docUnit, metaDc, conf, depotPath, listWithCheckSummedStoredFiles, multiple, firstDoc);

        } catch (final UncheckedIOException e) {
            throw new IOException(e);
        } catch (final PgcnUncheckedException e) {
            throw new PgcnTechnicalException(e);
        }
        return root;
    }

    /**
     * Génération d'un csv simple (1 entete, 1 ligne) pour traitement par Omeka.
     *
     */
    private File createDocUnitOmekaCsv(final DocUnit docUnit, final BibliographicRecordDcDTO metaDc,
                                       final OmekaConfiguration conf, final Path depotPath,
                                       final List<CheckSummedStoredFile> listWithCheckSummedStoredFiles,
                                       final boolean multiple, final boolean firstDoc) {

        final List<DocPropertyType> entetesCustom = docPropertyTypeService.findAllBySuperType(DocPropertyType.DocPropertySuperType.CUSTOM);
        entetesCustom.addAll(docPropertyTypeService.findAllBySuperType(DocPropertyType.DocPropertySuperType.CUSTOM_OMEKA));

        if (depotPath == null || !depotPath.toFile().canRead()) {
            return null;
        }

        final File csvDir = depotPath.getParent().toFile();
        final String csvName;
        if (multiple) {  // export de masse
            csvName = CSV_NAME_MASS_EXPORT;
        } else {
            csvName = docUnit.getPgcnId() + ".csv";
        }

        File csvFile = new File(csvDir, csvName);
        if (!multiple || !(csvFile.exists() && csvFile.canWrite()) ) {
            csvFile = initializeCsvFile(csvFile, conf, metaDc);
        }
        final String depositAdress = conf.getAccessUrl();

        // Alimentation du CSV en append
        try (final Writer writer = new FileWriter(csvFile, true)) {

            writer.append(NEW_LINE_SEPARATOR);
            // champs DC
            for (final String entete : getEntetesDC()) {
                switch (entete) {
                    case "Title":
                        writer.append(getFormatedValues(metaDc.getTitle())).append(CSV_COL_SEP);
                        break;
                    case "Creator":
                        writer.append(getFormatedValues(metaDc.getCreator())).append(CSV_COL_SEP);
                        break;
                    case "Subject":
                        writer.append(getFormatedValues(metaDc.getSubject())).append(CSV_COL_SEP);
                        break;
                    case "Description":
                        writer.append(getFormatedValues(metaDc.getDescription())).append(CSV_COL_SEP);
                        break;
                    case "Publisher":
                        writer.append(getFormatedValues(metaDc.getPublisher())).append(CSV_COL_SEP);
                        break;
                    case "Contributor":
                        writer.append(getFormatedValues(metaDc.getContributor())).append(CSV_COL_SEP);
                        break;
                    case "Date":
                        writer.append(getFormatedValues(metaDc.getDate())).append(CSV_COL_SEP);
                        break;
                    case "Type":
                        writer.append(getFormatedValues(metaDc.getType())).append(CSV_COL_SEP);
                        break;
                    case "Format":
                        writer.append(getFormatedValues(metaDc.getFormat())).append(CSV_COL_SEP);
                        break;
                    case "Identifier":
                        writer.append(getFormatedValues(metaDc.getIdentifier())).append(CSV_COL_SEP);
                        break;
                    case "Source":
                        writer.append(getFormatedValues(metaDc.getSource())).append(CSV_COL_SEP);
                        break;
                    case "Language":
                        writer.append(getFormatedValues(metaDc.getLanguage())).append(CSV_COL_SEP);
                        break;
                    case "Relation":
                        writer.append(getFormatedValues(metaDc.getRelation())).append(CSV_COL_SEP);
                        break;
                    case "Coverage":
                        writer.append(getFormatedValues(metaDc.getCoverage())).append(CSV_COL_SEP);
                        break;
                    case "Rights":
                        writer.append(getFormatedValues(metaDc.getRights())).append(CSV_COL_SEP);
                        break;
                }
            }

            // custom proerties
            for (final DocPropertyType enteteCustom : entetesCustom) {
                if (metaDc.getCustomProperties() != null) {
                    boolean value = false;
                    for (final DocPropertyDTO customDC : metaDc.getCustomProperties()) {
                        if (customDC.getType().getLabel().equals(enteteCustom.getLabel())) {
                            writer.append(getFormatedValues(Collections.singletonList(customDC.getValue()))).append(CSV_COL_SEP);
                            value = true;
                            break;
                        }
                    }
                    if (!value) {
                        writer.append(EMPTY_FIELD_VALUE).append(CSV_COL_SEP);
                    }
                }
            }
            // collection omeka
            if (docUnit.getOmekaCollection() != null) {
                writer.append(docUnit.getOmekaCollection().getName());
            } else if (docUnit.getLot().getOmekaCollection() != null) {
                writer.append(docUnit.getLot().getOmekaCollection().getName());
            }
            writer.append(CSV_COL_SEP);
            // item omeka
            if (docUnit.getOmekaItem() != null) {
                writer.append(docUnit.getOmekaItem().getName());
            } else if (docUnit.getLot().getOmekaItem() != null) {
                writer.append(docUnit.getLot().getOmekaItem().getName());
            }
            writer.append(CSV_COL_SEP);
            // liste des fichiers
            writer.append(getFilesAdress(docUnit.getPgcnId(),
                                         listWithCheckSummedStoredFiles,
                                         depositAdress));

            //tag  (todo : docUnit.getTag - simple string à ajouter...)
            //writer.append(docUnit.getTag());
            // writer.append(EMPTY_FIELD_VALUE);

        } catch (final IOException e) {
            LOG.trace("Erreur ecriture fichier csv pour OMEKA", e);
        }

        return csvFile;

    }

    /**
     * Init du fichier csv avec son entete.
     *
     * @param csvFile
     * @return
     */
    private File initializeCsvFile(final File csvFile, final OmekaConfiguration conf, final BibliographicRecordDcDTO metaDC) {

        // Entête
        try (final Writer writer = new FileWriter(csvFile)) {
            for (final String entete : getEntetesDC()) {
                writer.append(entete).append(CSV_COL_SEP);
            }

            // Entête champs personnalisés
            final List<String> entetesCustomDC = docPropertyTypeService.findAllBySuperType(DocPropertyType.DocPropertySuperType.CUSTOM)
                                                                       .stream()
                                                                       .map(DocPropertyType::getLabel)
                                                                       .collect(Collectors.toList());
            entetesCustomDC.addAll(docPropertyTypeService.findAllBySuperType(DocPropertyType.DocPropertySuperType.CUSTOM_OMEKA)
                .stream()
                .map(DocPropertyType::getLabel)
                .collect(Collectors.toList()));


            if(metaDC.getCustomProperties() != null){
                for (final String enteteCustomDC : entetesCustomDC) {
                    writer.append(enteteCustomDC).append(CSV_COL_SEP);
                }
            }

            writer.append("collection").append(CSV_COL_SEP);
            writer.append("Item Type").append(CSV_COL_SEP);
            writer.append("file");

        } catch (final IOException e) {
            LOG.trace("Erreur ecriture entete fichier csv pour OMEKA", e);
        }
        return csvFile;
    }

    private String getFormatedValues(final List<String> values) throws IOException {

        if (values == null || values.isEmpty()) {
            return EMPTY_FIELD_VALUE;
        }

        final StringBuilder sb = new StringBuilder();
        values.forEach(val -> {
            sb.append(docUnitService.deleteUnwantedCrLf(val))
                .append(CSV_REPEATED_FIELD_SEP);
        });
        final String aggreg = sb.toString();
        if (aggreg.endsWith(CSV_REPEATED_FIELD_SEP)) {
            return aggreg.substring(0, aggreg.length()-1);
        } else {
            return aggreg;
        }
    }


    private String getFilesAdress(final String docId, final List<CheckSummedStoredFile> listWithCheckSummedStoredFiles, final String adress) throws IOException {

        final String startAdr = adress + "/" + docId + "/view/";

        final StringBuilder sb = new StringBuilder();
        listWithCheckSummedStoredFiles.forEach(sf -> {
            final String fileName = sf.getStoredFile().getFilename();
            sb.append(startAdr + fileName.substring(0, fileName.lastIndexOf(".")) + JPG_EXT + CSV_REPEATED_FIELD_SEP);
        });
        final String aggreg = sb.toString();
        if (aggreg.endsWith(CSV_REPEATED_FIELD_SEP)) {
            return aggreg.substring(0, aggreg.length()-1);
        } else {
            return aggreg;
        }
    }



    /**
     * On a besoin de la notice.
     *
     * @param docUnit
     * @return
     */
    @Transactional
    //FIXME: FACTORISE THIS METHOD TO AVOID DUPLICATE CODE (UIBibligraphixRecordService: method getBibliographicRecordDcDTOFromDocUnit)
    public BibliographicRecordDcDTO getDataRecord(final DocUnit docUnit) {
        final BibliographicRecordDcDTO dto;

        if (docUnit.getRecords().iterator().hasNext()) {
            final BibliographicRecord record = docUnit.getRecords().iterator().next();
            dto = uiBibliographicRecordService.getOneDc(record.getIdentifier());
        } else {
            dto = null;
        }

        return dto;
    }

    /**
     * DEPOT: fichiers à archiver
     *
     */
    private Path createDirectories(final Path root, final OmekaConfiguration conf) throws IOException {

        final Path depotPath = Files.createDirectory(root);
        LOG.debug("Répertoire {} créé", depotPath.toString());
        if (conf.isExportMaster()) {
            Files.createDirectory(depotPath.resolve("master"));
        }
        if (conf.isExportView()) {
            Files.createDirectory(depotPath.resolve("view"));
        }
        if (conf.isExportThumb()) {
            Files.createDirectory(depotPath.resolve("thumbnail"));
        }
        return depotPath;
    }

    /**
     * DEPOT : ajout des fichiers à archiver
     *
     * @param docUnit
     * @param depotPath
     * @return la liste de checksum permettant d'éviter un recalcul
     */
    private List<CheckSummedStoredFile> addDepotFiles(final DocUnit docUnit, final Path depotPath, final OmekaConfiguration conf) {
        final List<CheckSummedStoredFile> checkSums = new ArrayList<>();
        final String libraryId = docUnit.getLibrary().getIdentifier();
        docUnit.getDigitalDocuments().forEach(digitalDoc -> {
            digitalDoc.getOrderedPages()
                        .forEach(page -> {

                            final Optional<StoredFile> master = page.getMaster();

                            if (master.isPresent()) {

                                final StoredFile masterStoredFile = master.get();
                                final File sourceFile = bm.getFileForStoredFile(masterStoredFile, libraryId);
                                final Path sourcePath = Paths.get(sourceFile.getAbsolutePath());

                                if (conf.isExportMaster() && page.getNumber() != null) {
                                    final Path depotMaster = depotPath.resolve("master");
                                    try {
                                        final Path destPath = Files.createFile(depotMaster.resolve(masterStoredFile.getFilename()));
                                        Files.copy(sourcePath, destPath, StandardCopyOption.REPLACE_EXISTING);
                                        // On remplit la map pour optimiser le traitement ultérieur des métadonnées
                                        checkSums.add(getCheckSummedStoredFile(masterStoredFile, sourceFile));
                                    } catch (final IOException e) {
                                        throw new UncheckedIOException(e);
                                    }
                                }
                                if (conf.isExportPdf() && page.getNumber() == null) {
                                    try {
                                        final Path destPath = Files.createFile(depotPath.resolve(masterStoredFile.getFilename()));
                                        Files.copy(sourcePath, destPath, StandardCopyOption.REPLACE_EXISTING);
                                    } catch (final IOException e) {
                                        throw new UncheckedIOException(e);
                                    }
                                }

                                if (conf.isExportView()) {
                                    final Path depotView = depotPath.resolve("view");
                                    final Optional<StoredFile> view = page.getDerivedForFormat(ViewsFormatConfiguration.FileFormat.VIEW);
                                    if (view.isPresent()) {
                                        final StoredFile sf = view.get();
                                        final File file = bm.getFileForStoredFile(sf, libraryId);
                                        final Path srcPath = Paths.get(file.getAbsolutePath());
                                        final String fileName = sf.getFilename().substring(0, sf.getFilename().lastIndexOf(".")+1) + ImageUtils.FORMAT_JPG;
                                        try {
                                            final Path destPath = Files.createFile(depotView.resolve(fileName));
                                            Files.copy(srcPath, destPath, StandardCopyOption.REPLACE_EXISTING);
                                            // On remplit la map pour optimiser le traitement ultérieur des métadonnées
                                            checkSums.add(getCheckSummedStoredFile(sf, file));
                                        } catch (final IOException e) {
                                            throw new UncheckedIOException(e);
                                        }
                                    }
                                }

                                if (conf.isExportThumb()) {
                                    final Path depotThumb = depotPath.resolve("thumbnail");
                                    final Optional<StoredFile> thumb = page.getDerivedForFormat(ViewsFormatConfiguration.FileFormat.THUMB);
                                    if (thumb.isPresent()) {
                                        final StoredFile sf = thumb.get();
                                        final File file = bm.getFileForStoredFile(sf, libraryId);
                                        final Path srcPath = Paths.get(file.getAbsolutePath());
                                        final String fileName = sf.getFilename().substring(0, sf.getFilename().lastIndexOf(".")+1) + ImageUtils.FORMAT_JPG;
                                        try {
                                            final Path destPath = Files.createFile(depotThumb.resolve(fileName));
                                            Files.copy(srcPath, destPath, StandardCopyOption.REPLACE_EXISTING);
                                        } catch (final IOException e) {
                                            throw new UncheckedIOException(e);
                                        }

                                    }
                                }

                            }

            });
        });
        return checkSums;
    }

    /**
    *
    * @param storedFile
    * @param sourceFile
    * @return
    * @throws IOException
    */
   public CheckSummedStoredFile getCheckSummedStoredFile(final StoredFile storedFile, final File sourceFile) throws IOException {
       final CheckSummedStoredFile checkSummed = new CheckSummedStoredFile();
       checkSummed.setStoredFile(storedFile);
       checkSummed.setCheckSumType(fr.progilone.pgcn.service.util.FileUtils.CheckSumType.MD5);
       try {
           checkSummed.setCheckSum(fr.progilone.pgcn.service.util.FileUtils.checkSum(sourceFile, fr.progilone.pgcn.service.util.FileUtils.CheckSumType.MD5));
       } catch (final NoSuchAlgorithmException e) {
               LOG.error(e.getMessage(), e);
               throw new PgcnUncheckedException(e);
       }
       return checkSummed;
   }

    private List<String> getEntetesDC() {
        return docPropertyTypeService.findAllBySuperType(DocPropertyType.DocPropertySuperType.DC)
                                     .stream()
                                     .map(DocPropertyType::getLabel)
                                     .collect(Collectors.toList());
    }
}
