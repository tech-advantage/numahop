package fr.progilone.pgcn.service.exchange.digitallibrary;

import fr.progilone.pgcn.domain.administration.digitallibrary.DigitalLibraryConfiguration;
import fr.progilone.pgcn.domain.administration.viewsformat.ViewsFormatConfiguration;
import fr.progilone.pgcn.domain.document.DocPropertyType;
import fr.progilone.pgcn.domain.document.DocUnit;
import fr.progilone.pgcn.domain.dto.document.BibliographicRecordDcDTO;
import fr.progilone.pgcn.domain.dto.document.DocPropertyDTO;
import fr.progilone.pgcn.domain.library.Library;
import fr.progilone.pgcn.domain.storage.CheckSummedStoredFile;
import fr.progilone.pgcn.domain.storage.StoredFile;
import fr.progilone.pgcn.domain.workflow.DocUnitWorkflow;
import fr.progilone.pgcn.exception.PgcnTechnicalException;
import fr.progilone.pgcn.exception.PgcnUncheckedException;
import fr.progilone.pgcn.service.MailService;
import fr.progilone.pgcn.service.administration.digitallibrary.DigitalLibraryConfigurationService;
import fr.progilone.pgcn.service.document.DocPropertyTypeService;
import fr.progilone.pgcn.service.document.DocUnitService;
import fr.progilone.pgcn.service.document.ui.UIBibliographicRecordService;
import fr.progilone.pgcn.service.exchange.cines.ExportMetsService;
import fr.progilone.pgcn.service.exchange.csv.ExportCSVService;
import fr.progilone.pgcn.service.library.LibraryService;
import fr.progilone.pgcn.service.storage.AltoService;
import fr.progilone.pgcn.service.storage.BinaryStorageManager;
import fr.progilone.pgcn.service.util.CryptoService;
import fr.progilone.pgcn.service.util.DateUtils;
import fr.progilone.pgcn.service.workflow.WorkflowService;
import jakarta.persistence.EntityNotFoundException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DigitalLibraryDiffusionService {

    private static final Logger LOG = LoggerFactory.getLogger(DigitalLibraryDiffusionService.class);

    @Value("${services.digitalLibraryDiffusion.cache}")
    private String workingDir;

    private static final String EXTENSION_FORMAT_PDF = "PDF";
    private static final String CSV_COL_SEP = ",";
    private static final String CSV_REPEATED_FIELD_SEP = "|";
    private static final String NEW_LINE_SEPARATOR = "\n";
    private static final String MEDIA_DIR = "medias";
    private static final String ALTO_DIR = "-alto";
    private static final String JPG_DIR = "-jpg";
    private static final String PDF_DIR = "-pdf";
    private static final String MEDIA_HEADER = "media";
    private static final String ALTO_HEADER = "alto";
    private static final String PDF_HEADER = "pdf";

    private final DocUnitService docUnitService;
    private final DigitalLibraryConfigurationService digitalLibraryConfigurationService;
    private final WorkflowService workflowService;
    private final UIBibliographicRecordService uiBibliographicRecordService;
    private final CryptoService cryptoService;
    private final BinaryStorageManager bm;
    private final ExportMetsService exportMetsService;
    private final DocPropertyTypeService docPropertyTypeService;
    private final ExportCSVService exportCSVService;
    private final MailService mailService;
    private final AltoService altoService;
    private final LibraryService libraryService;

    public DigitalLibraryDiffusionService(final DocUnitService docUnitService,
                                          final DigitalLibraryConfigurationService digitalLibraryConfigurationService,
                                          final WorkflowService workflowService,
                                          final UIBibliographicRecordService uiBibliographicRecordService,
                                          final CryptoService cryptoService,
                                          final BinaryStorageManager bm,
                                          final ExportMetsService exportMetsService,
                                          final DocPropertyTypeService docPropertyTypeService,
                                          final ExportCSVService exportCSVService,
                                          final MailService mailService,
                                          final AltoService altoService,
                                          final LibraryService libraryService) {
        this.docUnitService = docUnitService;
        this.digitalLibraryConfigurationService = digitalLibraryConfigurationService;
        this.workflowService = workflowService;
        this.uiBibliographicRecordService = uiBibliographicRecordService;
        this.cryptoService = cryptoService;
        this.bm = bm;
        this.exportMetsService = exportMetsService;
        this.docPropertyTypeService = docPropertyTypeService;
        this.exportCSVService = exportCSVService;
        this.mailService = mailService;
        this.altoService = altoService;
        this.libraryService = libraryService;
    }

    @Transactional(readOnly = true)
    public List<DocUnit> findDocUnitsReadyForDigitalLibraryExportByLibrary() {
        final List<DocUnit> docsToExport = new ArrayList<>();
        final List<Library> libraries = libraryService.findAllByActive(true);
        libraries.stream().filter(lib -> CollectionUtils.isNotEmpty(digitalLibraryConfigurationService.findByLibraryAndActive(lib.getIdentifier(), true))).forEach(lib -> {
            docsToExport.addAll(workflowService.findDocUnitWorkflowsForDigitalLibraryExport(lib.getIdentifier())
                                               .stream()
                                               .map(DocUnitWorkflow::getDocUnit)
                                               .collect(Collectors.toList()));
        });
        LOG.debug("DigitalLibraryExport :  " + docsToExport.size()
                  + " Docs recuperes pour l'export");
        return docsToExport;
    }

    @Transactional
    public void updateDocsToDigitalLibraryStatus(final DocUnit doc, final DocUnit.ExportStatus status) {
        doc.setDigLibExportStatus(status);
        doc.setDigLibExportDate(LocalDateTime.now());
        docUnitService.saveWithoutValidation(doc);
    }

    @Transactional
    public boolean exportDocToDigitalLibrary(final DocUnit du, final boolean multiple, final boolean firstDoc, final boolean lastDoc) {
        boolean exported = false;

        final DocUnit doc = docUnitService.findOneWithAllDependencies(du.getIdentifier());
        updateDocsToDigitalLibraryStatus(doc, DocUnit.ExportStatus.IN_PROGRESS);

        final String library = doc.getLibrary().getIdentifier();
        Path mediasDir = Paths.get(workingDir, library, MEDIA_DIR);

        try {
            // Suppression du répertoire s'il existe
            if (firstDoc && Files.exists(mediasDir)) {
                LOG.warn("Le répertoire {} est supprimé car il existe déjà", mediasDir);
                FileUtils.deleteDirectory(mediasDir.toFile());
            }
            if (firstDoc) {
                mediasDir = Files.createDirectory(Paths.get(workingDir, library, MEDIA_DIR));
            }

            // Digital Library config.
            final DigitalLibraryConfiguration conf = digitalLibraryConfigurationService.findByLibraryAndActive(library, true).stream().findFirst().orElse(null);
            if (conf == null) {
                LOG.trace("Conf.de la diffusion sur Bibliothèque numérique introuvable => Library[{}] - diffusion sur Bibliothèque numérique impossible.", library);
                return exported;
            }

            final BibliographicRecordDcDTO metaDC = uiBibliographicRecordService.getBibliographicRecordDcDTOFromDocUnit(doc);
            if (metaDC == null) {
                LOG.trace("Diffusion sur Bibliothèque numérique - Notice introuvable => DocUnit[{}] - Diffusion sur Bibliothèque numérique impossible.", doc.getIdentifier());
                return exported;
            }

            final File csv;
            // Génération des fichiers / répertoires MEDIA
            csv = exportDocUnit(doc, metaDC, mediasDir, conf, multiple, firstDoc);

            // Tranferts du csv et dossier media
            if (mediasDir.toFile().exists() && csv.exists()) {

                final FTPClient ftpClient = new FTPClient();

                ftpClient.connect(conf.getAddress(), Integer.parseInt(conf.getPort()));

                final boolean success = ftpClient.login(conf.getLogin(), cryptoService.decrypt(conf.getPassword()));

                if (!success) {
                    LOG.error("Erreur de connexion ftp lors de l'export sur la Bibliothèque numérique");
                    exported = false;
                } else {
                    ftpClient.changeWorkingDirectory(conf.getDeliveryFolder());
                    if (!multiple || lastDoc) {
                        putPathRecursively(csv, ftpClient);
                    }
                    LOG.info("Envoi du document {}", mediasDir.getFileName());
                    putPathRecursively(mediasDir.toFile(), ftpClient);
                    exported = true;
                    ftpClient.logout();
                    ftpClient.disconnect();
                }
            }

            if (exported) {
                // Envoi mail avec le fichier csv.
                if (!multiple || lastDoc) {
                    sendCsvFile(csv, conf);

                    // suppression du fichier zip une fois l'envoi effectué
                    LOG.debug("Suppression du répertoire {}", mediasDir.toAbsolutePath().toString());
                    FileUtils.deleteQuietly(mediasDir.toFile());
                }
                LOG.debug("Export du document {} sur la bibliothèque numérique effectué", doc.getPgcnId());
                updateDocsToDigitalLibraryStatus(doc, DocUnit.ExportStatus.SENT);
            } else {
                LOG.debug("Export du document {} sur la bibliothèque numérique a échoué", doc.getPgcnId());
                updateDocsToDigitalLibraryStatus(doc, DocUnit.ExportStatus.FAILED);
            }
        } catch (final Exception e) {
            LOG.error("Erreur Export Bibliothèque numérique", e);
            updateDocsToDigitalLibraryStatus(doc, DocUnit.ExportStatus.FAILED);
            exported = false;
        }

        return exported;
    }

    @Transactional
    public File exportDocUnit(final DocUnit docUnit,
                              final BibliographicRecordDcDTO metaDc,
                              final Path mediaDir,
                              final DigitalLibraryConfiguration conf,
                              final boolean multiple,
                              final boolean firstDoc) throws IOException, PgcnTechnicalException {

        final Path root = mediaDir.resolve(docUnit.getPgcnId());
        // Suppression du répertoire s'il existe
        if (Files.exists(root)) {
            LOG.warn("Le répertoire {} est supprimé car il existe déjà", root);
            FileUtils.deleteDirectory(root.toFile());
        }

        final String format = docUnit.getLot().getRequiredFormat();

        // Création du répertoire
        LOG.trace("Création du répertoire {}", root);
        final Path depotPath = createDirectories(root, docUnit.getPgcnId(), conf);
        File csv;
        try {
            // Ajout des fichiers à archiver
            addDepotFiles(docUnit, depotPath, conf);
            // csv
            csv = createDocUnitsDigitalLibraryDiffusionCsv(docUnit, metaDc, conf, mediaDir, multiple, firstDoc);

        } catch (final UncheckedIOException e) {
            throw new IOException(e);
        } catch (final PgcnUncheckedException e) {
            throw new PgcnTechnicalException(e);
        }
        return csv;
    }

    private File createDocUnitsDigitalLibraryDiffusionCsv(final DocUnit docUnit,
                                                          final BibliographicRecordDcDTO metaDc,
                                                          final DigitalLibraryConfiguration conf,
                                                          final Path depotPath,
                                                          final boolean multiple,
                                                          final boolean firstDoc) {

        if (depotPath == null || !depotPath.toFile().canRead()) {
            return null;
        }

        final File csvDir = depotPath.getParent().toFile();
        final String csvName = DateUtils.formatDateToString(LocalDateTime.now(), "yyyyMMdd") + "-import.csv";
        if (multiple) {  // export de masse
            if (firstDoc) { // 1er doc de l'export de masse
                // supprime un eventuel ancien csv multiple qui aurait survecu...
                final File oldCsv = new File(csvDir, csvName);
                if (oldCsv.exists()) {
                    FileUtils.deleteQuietly(oldCsv);
                }
            }
        }

        final File csvFile = new File(csvDir, csvName);
        if (!multiple || !(csvFile.exists() && csvFile.canWrite())) {
            initializeCsvFile(csvFile, conf);
        }

        // Valeur par défaut pour les champs vides
        String emptyValue = conf.getDefaultValue();
        if (emptyValue == null) {
            emptyValue = StringUtils.EMPTY;
        }

        // Alimentation du CSV en append
        try (final Writer writer = new FileWriter(csvFile, true)) {
            writer.append(NEW_LINE_SEPARATOR);

            String pgcnId = docUnit.getPgcnId();
            if (conf.isExportPrint()) {
                writer.append(buildExportRelativePath(pgcnId, JPG_DIR, conf));
            }
            if (conf.isExportAlto()) {
                writer.append(buildExportRelativePath(pgcnId, ALTO_DIR, conf));
            }
            if (conf.isExportPdf()) {
                writer.append(buildExportRelativePath(pgcnId, PDF_DIR, conf));
            }

            final List<String> entetesDC = docPropertyTypeService.findAllBySuperType(DocPropertyType.DocPropertySuperType.DC)
                                                                 .stream()
                                                                 .map(DocPropertyType::getLabel)
                                                                 .collect(Collectors.toList());
            final List<DocPropertyType> entetesCustom = docPropertyTypeService.findAllBySuperType(DocPropertyType.DocPropertySuperType.CUSTOM);

            int i = 0;
            for (final String enteteDC : entetesDC) {
                switch (enteteDC) {
                    case "Title":
                        writer.append("\"").append(exportCSVService.getFormatedValues(metaDc.getTitle(), emptyValue, CSV_REPEATED_FIELD_SEP)).append("\"");
                        break;
                    case "Creator":
                        writer.append("\"").append(exportCSVService.getFormatedValues(metaDc.getCreator(), emptyValue, CSV_REPEATED_FIELD_SEP)).append("\"");
                        break;
                    case "Subject":
                        writer.append("\"").append(exportCSVService.getFormatedValues(metaDc.getSubject(), emptyValue, CSV_REPEATED_FIELD_SEP)).append("\"");
                        break;
                    case "Description":
                        writer.append("\"").append(exportCSVService.getFormatedValues(metaDc.getDescription(), emptyValue, CSV_REPEATED_FIELD_SEP)).append("\"");
                        break;
                    case "Publisher":
                        writer.append("\"").append(exportCSVService.getFormatedValues(metaDc.getPublisher(), emptyValue, CSV_REPEATED_FIELD_SEP)).append("\"");
                        break;
                    case "Contributor":
                        writer.append("\"").append(exportCSVService.getFormatedValues(metaDc.getContributor(), emptyValue, CSV_REPEATED_FIELD_SEP)).append("\"");
                        break;
                    case "Date":
                        writer.append("\"").append(exportCSVService.getFormatedValues(metaDc.getDate(), emptyValue, CSV_REPEATED_FIELD_SEP)).append("\"");
                        break;
                    case "Type":
                        writer.append("\"").append(exportCSVService.getFormatedValues(metaDc.getType(), emptyValue, CSV_REPEATED_FIELD_SEP)).append("\"");
                        break;
                    case "Format":
                        writer.append("\"").append(exportCSVService.getFormatedValues(metaDc.getFormat(), emptyValue, CSV_REPEATED_FIELD_SEP)).append("\"");
                        break;
                    case "Identifier":
                        writer.append("\"").append(exportCSVService.getFormatedValues(metaDc.getIdentifier(), emptyValue, CSV_REPEATED_FIELD_SEP)).append("\"");
                        break;
                    case "Source":
                        writer.append("\"").append(exportCSVService.getFormatedValues(metaDc.getSource(), emptyValue, CSV_REPEATED_FIELD_SEP)).append("\"");
                        break;
                    case "Language":
                        writer.append("\"").append(exportCSVService.getFormatedValues(metaDc.getLanguage(), emptyValue, CSV_REPEATED_FIELD_SEP)).append("\"");
                        break;
                    case "Relation":
                        writer.append("\"").append(exportCSVService.getFormatedValues(metaDc.getRelation(), emptyValue, CSV_REPEATED_FIELD_SEP)).append("\"");
                        break;
                    case "Coverage":
                        writer.append("\"").append(exportCSVService.getFormatedValues(metaDc.getCoverage(), emptyValue, CSV_REPEATED_FIELD_SEP)).append("\"");
                        break;
                    case "Rights":
                        writer.append("\"").append(exportCSVService.getFormatedValues(metaDc.getRights(), emptyValue, CSV_REPEATED_FIELD_SEP)).append("\"");
                        break;
                }
                i++;
                if (i != entetesDC.size() || !entetesCustom.isEmpty()) {
                    writer.append(CSV_COL_SEP);
                }
            }

            int j = 0;
            for (final DocPropertyType enteteCustom : entetesCustom) {
                if (metaDc.getCustomProperties() != null) {
                    boolean value = false;
                    for (final DocPropertyDTO customDC : metaDc.getCustomProperties()) {
                        if (customDC.getType().getLabel().equals(enteteCustom.getLabel())) {
                            writer.append("\"").append(customDC.getValue()).append("\"");
                            value = true;
                            break;
                        }
                    }
                    if (!value) {
                        writer.append("\"").append(emptyValue).append("\"");
                    }
                    j++;
                    if (j != entetesCustom.size()) {
                        writer.append(CSV_COL_SEP);
                    }
                }
            }

        } catch (final IOException e) {
            LOG.trace("Erreur ecriture fichier csv pour OMEKA", e);
        }

        return csvFile;
    }

    private void initializeCsvFile(final File csvFile, final DigitalLibraryConfiguration conf) {
        try (final Writer writer = new FileWriter(csvFile)) {
            // Entête
            if (conf.isExportPrint()) {
                writer.append(MEDIA_HEADER).append(CSV_COL_SEP);
            }
            if (conf.isExportAlto()) {
                writer.append(ALTO_HEADER).append(CSV_COL_SEP);
            }
            if (conf.isExportPdf()) {
                writer.append(MEDIA_HEADER).append(CSV_COL_SEP);
            }

            // Entête Dublin Core
            final List<String> entetesDC = docPropertyTypeService.findAllBySuperType(DocPropertyType.DocPropertySuperType.DC)
                                                                 .stream()
                                                                 .map(DocPropertyType::getLabel)
                                                                 .collect(Collectors.toList());

            // Entête champs personnalisés
            final List<String> entetesCustomDC = docPropertyTypeService.findAllBySuperType(DocPropertyType.DocPropertySuperType.CUSTOM)
                                                                       .stream()
                                                                       .map(DocPropertyType::getLabel)
                                                                       .collect(Collectors.toList());
            int i = 0;
            for (final String enteteDC : entetesDC) {
                writer.append(enteteDC);
                i++;
                if (i != entetesDC.size() || !entetesCustomDC.isEmpty()) {
                    writer.append(CSV_COL_SEP);
                }
            }

            int j = 0;
            for (final String enteteCustomDC : entetesCustomDC) {
                writer.append(enteteCustomDC);
                j++;
                if (j != entetesCustomDC.size()) {
                    writer.append(CSV_COL_SEP);
                }
            }

        } catch (final IOException e) {
            LOG.trace("Erreur ecriture entete fichier csv pour OMEKA", e);
        }
    }

    private Path createDirectories(final Path root, final String pgcnId, final DigitalLibraryConfiguration conf) throws IOException {
        final Path depotPath = Files.createDirectory(root);
        LOG.debug("Répertoire {} créé", depotPath.toString());
        if (this.hasMultipleExports(conf)) {
            LOG.debug("Plusieurs types d'exports requis, création des répertoires intermédiaires");
            if (conf.isExportPrint()) {
                Files.createDirectory(depotPath.resolve(pgcnId.concat(JPG_DIR)));
            }
            if (conf.isExportPdf()) {
                Files.createDirectory(depotPath.resolve(pgcnId.concat(PDF_DIR)));
            }
            if (conf.isExportAlto()) {
                Files.createDirectory(depotPath.resolve(pgcnId.concat(ALTO_DIR)));
            }
        }
        return depotPath;
    }

    /**
     * Vérifie si le nombre de types d'exports sélectionnés (Print, Pdf, Alto) est supérieur à 1
     *
     * @param conf
     *            la configuration de librairie numérique
     * @return true si plus d'un type d'export sélectionné dans la configuration
     */
    private boolean hasMultipleExports(final DigitalLibraryConfiguration conf) {
        int exportPrint = conf.isExportPrint() ? 1
                                               : 0;
        int exportPdf = conf.isExportPdf() ? 1
                                           : 0;
        int exportAlto = conf.isExportAlto() ? 1
                                             : 0;

        return exportPrint + exportPdf
               + exportAlto > 1;
    }

    /**
     * Génère le chemin de stockage des fichiers d'exports en fonction du nombre de types d'exports
     *
     * Insère le répertoire spécifique d'export exportPath si plusieurs types d'exports sont présents,
     * sinon renvoie le chemin racine.
     *
     * @param depotPath
     *            le chemin du dépôt de l'export
     * @param exportPath
     *            le nom du répertoire associé au type d'export
     * @param conf
     *            la configuration de librairie numérique
     * @return le chemin pour le dépôt de l'export généré
     */
    private Path resolveExportDepotPath(final Path depotPath, String exportPath, final DigitalLibraryConfiguration conf) {
        return hasMultipleExports(conf) ? depotPath.resolve(exportPath)
                                        : depotPath;
    }

    /**
     * Concatène les différents éléments des répertoires d'exports en fonction du nombre
     * de formats selectionnés dans la configuration
     *
     * si plus d'un type d'exports est présent, insère le répertoire spécifique d'export
     *
     * @param conf
     *            la configuration de librairie numérique
     * @param pgcnId
     *            l'identifiant PGCN - en préfixe du type d'export
     * @param exportName
     *            le nom du type d'export
     * @return la chaine de caractères du chemin relatif
     */
    private String buildExportRelativePath(String pgcnId, String exportName, final DigitalLibraryConfiguration conf) {
        StringBuilder stringBuilder = new StringBuilder(MEDIA_DIR).append("/").append(pgcnId);
        if (hasMultipleExports(conf)) {
            stringBuilder.append("/").append(pgcnId).append(exportName);
        }
        stringBuilder.append(CSV_COL_SEP);
        return stringBuilder.toString();
    }

    /**
     * DEPOT : ajout des fichiers à archiver
     *
     * @return la liste de checksum permettant d'éviter un recalcul
     */
    private List<CheckSummedStoredFile> addDepotFiles(final DocUnit docUnit, final Path depotPath, final DigitalLibraryConfiguration conf) {
        final List<CheckSummedStoredFile> checkSums = new ArrayList<>();
        final String libraryId = docUnit.getLibrary().getIdentifier();
        final String pgcnId = docUnit.getPgcnId();
        final String digitalId = docUnit.getDigitalDocuments().iterator().next().getDigitalId();

        final Path depotPrint = resolveExportDepotPath(depotPath, pgcnId.concat(JPG_DIR), conf);
        final Path depotPdf = resolveExportDepotPath(depotPath, pgcnId.concat(PDF_DIR), conf);

        docUnit.getDigitalDocuments().forEach(digitalDoc -> digitalDoc.getOrderedPages().forEach(page -> {
            // Si page standard (non pdfs)
            if (page.getNumber() != null && page.getNumber() != 0) {
                // Par défaut, export du format PRINT
                final Optional<StoredFile> print = page.getDerivedForFormat(ViewsFormatConfiguration.FileFormat.PRINT);

                if (print.isPresent()) {
                    final StoredFile printStoredFile = print.get();
                    final File sourceFile = bm.getFileForStoredFile(printStoredFile, libraryId);
                    final Path sourcePath = Paths.get(sourceFile.getAbsolutePath());

                    if (conf.isExportPrint() && page.getNumber() != null) {
                        try {
                            final Path destPath = Files.createFile(depotPrint.resolve(printStoredFile.getFilename()));
                            Files.copy(sourcePath, destPath, StandardCopyOption.REPLACE_EXISTING);
                            // On remplit la map pour optimiser le traitement ultérieur des métadonnées
                            checkSums.add(exportMetsService.getCheckSummedStoredFile(printStoredFile, sourceFile));
                        } catch (final IOException e) {
                            throw new UncheckedIOException(e);
                        }
                    }
                }
            } else if (conf.isExportPdf()) {
                // Page dont le number est null (pdf)
                try {
                    // On récupère le pdf, dans le format master
                    final Optional<StoredFile> pdf = page.getMaster();

                    if (pdf.isPresent() && MediaType.APPLICATION_PDF.toString().equals(pdf.get().getMimetype())) {
                        final StoredFile pdfStoredFile = pdf.get();
                        final File pdfSourceFile = bm.getFileForStoredFile(pdfStoredFile, libraryId);
                        final Path pdfSourcePath = Paths.get(pdfSourceFile.getAbsolutePath());
                        final Path destPath = Files.createFile(depotPdf.resolve(pdfStoredFile.getFilename()));
                        Files.copy(pdfSourcePath, destPath, StandardCopyOption.REPLACE_EXISTING);
                        // On remplit la map pour optimiser le traitement ultérieur des métadonnées
                        checkSums.add(exportMetsService.getCheckSummedStoredFile(pdfStoredFile, pdfSourceFile));
                    }
                } catch (final IOException e) {
                    throw new UncheckedIOException(e);
                } catch (final EntityNotFoundException nFE) {
                    LOG.error("File not found", nFE);
                }
            }
        }));
        if (conf.isExportAlto()) {
            final Path depotAlto = resolveExportDepotPath(depotPath, pgcnId.concat(ALTO_DIR), conf);
            try {
                final List<File> altoFiles = altoService.retrieveAlto(digitalId, libraryId, true, false);
                if (!altoFiles.isEmpty()) {
                    final File altoFile = altoFiles.stream().findFirst().get();
                    final Path destPath = Files.createFile(depotAlto.resolve(altoFile.getName()));
                    Files.copy(altoFile.toPath(), destPath, StandardCopyOption.REPLACE_EXISTING);
                }
            } catch (final IOException e) {
                throw new UncheckedIOException(e);
            }
        }
        return checkSums;

    }

    /**
     * Envoi du csv pour import Bib Numérique par mail.
     */
    private void sendCsvFile(final File csvFile, final DigitalLibraryConfiguration configuration) {

        LOG.info("Envoi du cvs par email");
        final String[] to = {configuration.getMail()};
        if (mailService.sendEmailWithAttachment(null, to, "Fichier CSV Import " + configuration.getLabel(), "Cf. ci-joint", csvFile, "text/plain", true, false)) {
            // OK on peut supprimer le csv
            FileUtils.deleteQuietly(csvFile);
        }
    }

    /**
     * Copie récursive d'un répertoire
     *
     */
    private void putPathRecursively(final File localSource, final FTPClient ftpclient) {
        final String targetName = localSource.getName();

        // Répertoire: création, ouverture et recopie du contenu
        if (localSource.isDirectory()) {
            try {
                LOG.debug("Création du répertoire distant {}", targetName);
                // Création du répertoire distant
                ftpclient.makeDirectory(targetName);
                ftpclient.changeWorkingDirectory(targetName);
                // Appel récursif sur le contenu du répertoire
                final String[] list = localSource.list();
                if (list != null) {
                    for (final String subSource : list) {
                        putPathRecursively(new File(localSource, subSource), ftpclient);
                    }
                }
                // On revient au répertoire précédent
                ftpclient.changeToParentDirectory();

            } catch (final IOException e) {
                LOG.error("Une erreur s'est produite lors du traitement du répertoire {}: {}", localSource.getAbsolutePath(), e.getMessage());
                LOG.error(e.getMessage(), e);
            }
        }
        // Fichier: transfert dans le répertoire distant courant
        else if (localSource.isFile()) {
            // Copie du fichier
            try (final FileInputStream in = new FileInputStream(localSource)) {
                LOG.trace("Envoi de {} vers {}", localSource.getAbsolutePath(), targetName);
                ftpclient.enterLocalPassiveMode();
                ftpclient.setFileType(FTP.BINARY_FILE_TYPE, FTP.BINARY_FILE_TYPE);
                ftpclient.storeFile(targetName, in);

            } catch (final IOException e) {
                LOG.error("Une erreur s'est produite lors de la copie du fichier {} vers {}: {}", localSource.getAbsolutePath(), targetName, e.getMessage());
                LOG.error(e.getMessage(), e);
            }
        }
    }

}
