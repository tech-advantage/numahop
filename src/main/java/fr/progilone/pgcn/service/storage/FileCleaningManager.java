package fr.progilone.pgcn.service.storage;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.zip.Deflater;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.xml.bind.JAXBException;

import fr.progilone.pgcn.domain.administration.ExportFTPDeliveryFolder;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.xml.sax.SAXException;

import fr.progilone.pgcn.domain.AbstractDomainObject;
import fr.progilone.pgcn.domain.administration.SftpConfiguration;
import fr.progilone.pgcn.domain.administration.viewsformat.ViewsFormatConfiguration;
import fr.progilone.pgcn.domain.document.BibliographicRecord;
import fr.progilone.pgcn.domain.document.DocPage;
import fr.progilone.pgcn.domain.document.DocUnit;
import fr.progilone.pgcn.domain.dto.document.BibliographicRecordDcDTO;
import fr.progilone.pgcn.domain.dto.document.ValidatedDeliveredDocumentDTO;
import fr.progilone.pgcn.domain.exportftpconfiguration.ExportFTPConfiguration;
import fr.progilone.pgcn.domain.filesgestion.FilesGestionConfig;
import fr.progilone.pgcn.domain.lot.Lot;
import fr.progilone.pgcn.domain.project.Project;
import fr.progilone.pgcn.domain.storage.CheckSummedStoredFile;
import fr.progilone.pgcn.domain.storage.StoredFile;
import fr.progilone.pgcn.exception.PgcnTechnicalException;
import fr.progilone.pgcn.repository.document.DocUnitRepository;
import fr.progilone.pgcn.repository.exportftpconfiguration.ExportFTPConfigurationRepository;
import fr.progilone.pgcn.repository.storage.BinaryRepository;
import fr.progilone.pgcn.repository.storage.StoredFileRepository;
import fr.progilone.pgcn.service.check.MetaDatasCheckService;
import fr.progilone.pgcn.service.delivery.DeliveryService;
import fr.progilone.pgcn.service.document.DocPageService;
import fr.progilone.pgcn.service.document.ui.UIBibliographicRecordService;
import fr.progilone.pgcn.service.exchange.cines.CinesRequestHandlerService;
import fr.progilone.pgcn.service.exchange.cines.ExportMetsService;
import fr.progilone.pgcn.service.exchange.ssh.SftpService;
import fr.progilone.pgcn.service.filesgestion.FilesGestionConfigService;
import fr.progilone.pgcn.service.lot.LotService;
import fr.progilone.pgcn.service.project.ProjectService;
import fr.progilone.pgcn.service.util.CryptoService;
import fr.progilone.pgcn.service.util.ImageUtils;
import fr.progilone.pgcn.service.util.transaction.TransactionService;

@Service
public class FileCleaningManager {

    private static final Logger LOG = LoggerFactory.getLogger(FileStorageManager.class);

    public final static String TYP_DECLENCH_PROJET = "PROJET";
    public final static String TYP_DECLENCH_LOT = "LOT";

    @Value("${instance.libraries}")
    private String[] instanceLibraries;

    @Value("${services.cines.cache}")
    private String cinesCacheDir;

    @Value("${services.omeka.cache}")
    private String omekaCacheDir;

    @Value("${services.ftpexport.cache}")
    private String ftpexportCacheDir;


    private final DeliveryService deliveryService;
    private final BinaryStorageManager binaryStorageManager;
    private final FilesGestionConfigService filesGestionConfigService;
    private final ProjectService projectService;
    private final LotService lotService;
    private final DocPageService docPageService;
    private final BinaryRepository binaryRepository;
    private final StoredFileRepository storedFileRepository;
    private final BinaryStorageManager bm;
    private final CinesRequestHandlerService cinesRequestHandlerService;
    private final DocUnitRepository docUnitRepository;
    private final ExportFTPConfigurationRepository exportFTPConfigurationRepository;
    private final CryptoService cryptoService;
    private final SftpService sftpService;
    private final ExportMetsService exportMetsService;
    private final UIBibliographicRecordService uiBibliographicRecordService;
    private final TransactionService transactionService;

    @Autowired
    public FileCleaningManager(final DeliveryService deliveryService,
                               final BinaryStorageManager binaryStorageManager,
                               final FilesGestionConfigService filesGestionConfigService,
                               final ProjectService projectService,
                               final LotService lotService,
                               final DocPageService docPageService,
                               final BinaryRepository binaryRepository,
                               final StoredFileRepository storedFileRepository,
                               final BinaryStorageManager bm,
                               final CinesRequestHandlerService cinesRequestHandlerService,
                               final DocUnitRepository docUnitRepository,
                               final ExportFTPConfigurationRepository exportFTPConfigurationRepository,
                               final CryptoService cryptoService,
                               final SftpService sftpService,
                               final ExportMetsService exportMetsService,
                               final UIBibliographicRecordService uiBibliographicRecordService,
                               final TransactionService transactionService) {

        this.deliveryService = deliveryService;
        this.binaryStorageManager = binaryStorageManager;
        this.filesGestionConfigService = filesGestionConfigService;
        this.projectService = projectService;
        this.lotService = lotService;
        this.docPageService = docPageService;
        this.binaryRepository = binaryRepository;
        this.storedFileRepository = storedFileRepository;
        this.bm = bm;
        this.cinesRequestHandlerService = cinesRequestHandlerService;
        this.docUnitRepository = docUnitRepository;
        this.exportFTPConfigurationRepository = exportFTPConfigurationRepository;
        this.cryptoService = cryptoService;
        this.sftpService = sftpService;
        this.exportMetsService = exportMetsService;
        this.uiBibliographicRecordService = uiBibliographicRecordService;
        this.transactionService = transactionService;
    }

    /**
     * Purge les répertoires de livraison des documents validés
     * des livraisons validées avec date de livraison > j-5.
     * Traitement lancé chaque nuit à 01h00.
     */
    @Scheduled(cron = "${cron.cleanDeliveryFiles}")
    @Transactional
    public void cleanDeliveryFilesCron() {

        LOG.info("Lancement du cronjob cleanDeliveryFilesCron...");
        // recup les rep de livraison des documents valides
        //  de livraisons validees des 5 derniers jours (trt à 1h du mat)
        final LocalDate dateFrom = LocalDate.now().minusDays(5L);
        final Set<ValidatedDeliveredDocumentDTO> dtos = deliveryService.getValidatedDeliveredDocs(dateFrom);
        dtos.forEach(dto -> {

            final Path delivPath = Paths.get(dto.getDeliveryFolder(), dto.getFolderPath(), dto.getDigitalId());
            if (delivPath.toFile().exists() && delivPath.toFile().canWrite()) {
                LOG.debug(delivPath.toAbsolutePath().toString());
                try {
                    Files.walk(delivPath, FileVisitOption.FOLLOW_LINKS).sorted(Comparator.reverseOrder()).map(Path::toFile).forEach(File::delete);
                } catch (final IOException | SecurityException e) {
                    LOG.error("Erreur lors de la suppression des documents livrés dans {} - {}", delivPath.toAbsolutePath().toString(), e);
                }
            }
        });
    }

    /**
     * Purge des fichiers temporaires.
     */
    @Scheduled(cron = "${cron.cleanTemporaryFiles}")
    public void cleanTemporaryFilesCron() {

        LOG.info("Lancement du Job cleanTemporaryFilesCron...");
        // nettoie repertoires et fichiers temporaires dates de  + de 24h (trt à 2h du mat / defaut)
        final long limit = System.currentTimeMillis() - (24 * 3600000L);

        // 1 disk space per library
        Arrays.asList(instanceLibraries).forEach(lib -> {
            final Path tmpRoot = Paths.get(binaryStorageManager.getTmpDir(lib).getPath());
            purgeDirectory(tmpRoot, limit);
        });
    }


    /**
     * Purge des fichiers temporaires.
     */
    @Scheduled(cron = "${cron.cleanExportCaches}")
    public void cleanCacheDirsCron() {

        LOG.info("Lancement du Job cleanCacheDirsCron ...");
        // nettoie repertoires de cache d'export dates de  + de 24h
        final long limit = System.currentTimeMillis() - (24 * 3600000L);

        // 1 disk space per library
        Arrays.asList(instanceLibraries).forEach(lib -> {
            // purge caches CINES - OMEKA - FTPEXPORT
            purgeDirectory(Paths.get(cinesCacheDir, lib), limit);
            purgeDirectory(Paths.get(omekaCacheDir, lib), limit);
            purgeDirectory(Paths.get(ftpexportCacheDir, lib), limit);
        });
    }


    /**
     * Purge directory.
     */
    private void purgeDirectory(final Path dirRoot, final long timeLimit) {

        try (final Stream<Path> balad = Files.walk(dirRoot, FileVisitOption.FOLLOW_LINKS)) {
            balad.sorted(Comparator.reverseOrder())
                 .filter(p -> !p.equals(dirRoot))
                 .map(Path::toFile)
                 .filter(f -> f.lastModified() < timeLimit)
                 .forEach(File::delete);
            balad.close();
        } catch (final IOException e) {
            LOG.error("Erreur lors de la purge du dossier {} ", dirRoot.getFileName(),  e);
        }
    }


    /**
     * Purge / sauvegarde des fichiers apres archivage.
     */
    @Scheduled(cron = "${cron.gestDeliveredFiles}")
    @Transactional
    public void gestDeliveredFilesCron() {

        LOG.debug("Lancement du cronjob gestDeliveredFilesCron...");

        final List<FilesGestionConfig> configs = filesGestionConfigService.getConfigs();
        configs.stream().filter(conf -> Arrays.asList(instanceLibraries).contains(conf.getLibrary().getIdentifier())).forEach(conf -> {

            switch (conf.getTriggerType()) {
                case TYP_DECLENCH_PROJET:

                    final List<Project> projets = projectService.getClosedProjectsByLibrary(conf.getLibrary().getIdentifier(), conf.getDelay());
                    projets.forEach(p -> {
                        LOG.debug("Suppression / sauvegarde des fichiers après clôture du projet {}", p.getName());
                        final Map<String, List<DocPage>> pages = docPageService.getPagesByProjectId(p.getIdentifier());
                        final String proj = StringUtils.isNotBlank(p.getName()) ? p.getName() : p.getIdentifier();

                        if (processProjectFiles(conf, pages, proj)) {
                            projectService.setFilesProjectArchived(p.getIdentifier());
                            cleanTempFiles(conf, proj);
                        }
                    });

                    break;
                case TYP_DECLENCH_LOT:

                    final List<Lot> lots = lotService.getClosedLotsByLibrary(conf.getLibrary().getIdentifier(), conf.getDelay());
                    lots.forEach(l -> {
                        LOG.debug("Suppression / sauvegarde des fichiers après clôture du lot {}", l.getLabel());
                        final Map<String, List<DocPage>> pages = docPageService.getPagesByLotId(l.getIdentifier());
                        String proj = "";
                        if (StringUtils.isBlank(l.getProject().getName())) {
                            proj = l.getProject().getIdentifier();
                        } else {
                            proj = l.getProject().getName();
                        }
                        proj = proj.concat("_").concat(StringUtils.isBlank(l.getLabel()) ? l.getIdentifier() : l.getLabel());
                        if (processProjectFiles(conf, pages, proj)) {
                            lotService.setFilesLotArchived(l.getIdentifier());
                            cleanTempFiles(conf, proj);
                        }
                    });

                    break;
                default:
                    // nothing
                    break;
            }

        });
    }

    public boolean processProjectFiles(final FilesGestionConfig conf, final Map<String, List<DocPage>> pages, final String project) {

        final String libraryId = conf.getLibrary().getIdentifier();
        boolean processed = true;
        for (final String pgcnId : pages.keySet()) {

            final List<String> pagesId =
                    pages.get(pgcnId).stream().filter(pg -> pg.getNumber() != null).map(AbstractDomainObject::getIdentifier).collect(Collectors.toList());
                final String pdfPageId =
                    pages.get(pgcnId).stream().filter(pg -> pg.getNumber() == null).map(AbstractDomainObject::getIdentifier).findAny().orElse(null);

                final List<StoredFile> zooms = binaryRepository.getAllByPageIdentifiersAndFileFormat(pagesId, ViewsFormatConfiguration.FileFormat.ZOOM);
                suppressFiles(zooms, libraryId);
                final List<StoredFile> xtras =
                    binaryRepository.getAllByPageIdentifiersAndFileFormat(pagesId, ViewsFormatConfiguration.FileFormat.XTRAZOOM);
                suppressFiles(xtras, libraryId);

                try {
                    prepareSaving(conf, project, pgcnId, pagesId, pdfPageId);
                } catch(final IOException e) {
                    LOG.error("Error in prepare saving for document {}", pgcnId, e);
                    // let's continue....
                }

                processed = processed
                        && compressSavedFiles(conf, project, pgcnId);

                if (processed && !conf.isDeleteMaster()) {
                    // si on conserve les masters, on nettoie l'ocr ds les storedFiles.
                    cleanOcr(pgcnId, pagesId);
                }
        }
        return processed;
    }

    /**
     *
     */
    private void cleanOcr(final String pgcnId, final List<String> pagesId) {

        transactionService.executeInNewTransaction(() -> {

                // on nettoie au moins l'ocr devenu inutile qui peut etre tres lourd...
                final List<StoredFile> masters =
                        binaryRepository.getAllByPageIdentifiersAndFileFormat(pagesId, ViewsFormatConfiguration.FileFormat.MASTER);
                final List<StoredFile> cleanedSfs = masters.stream().map(StoredFile::getWithoutOcrText).collect(Collectors.toList());
                storedFileRepository.save(cleanedSfs);
        });
    }


    /**
     * Etape de preparation à la sauvegarde.
     *
     * @param conf
     * @param project
     * @param pgcnId
     * @param pagesId
     * @param pdfPageId
     *         identifiant du master pdf si present
     */
    public void prepareSaving(final FilesGestionConfig conf,
                              final String project,
                              final String pgcnId,
                              final List<String> pagesId,
                              final String pdfPageId) throws IOException {

        if (!conf.isSaveMaster() && !conf.isDeleteMaster()
                && !conf.isSavePdf() && !conf.isDeletePdf()
                && !conf.isSavePrint() && !conf.isDeletePrint()
                && !conf.isSaveView() && !conf.isDeleteView()
                && !conf.isSaveThumb() && !conf.isDeleteThumb()
                && !conf.isSaveAipSip()) {
            // Aucune sauvegarde programmée, on quitte..
            return;
        }

        final String libraryId = conf.getLibrary().getIdentifier();
        final Path dest;
        if (conf.isUseExportFtp()) {
            // vers le ftp parametre ds 'Configurations exports FTP'
            dest = Paths.get(ftpexportCacheDir, libraryId, project, pgcnId);

        } else {
            // ds 1 dir sur le serveur
            if (StringUtils.isNotBlank(conf.getDestinationDir())) {
                dest = Paths.get(conf.getDestinationDir(), project, pgcnId);
            } else {
                dest = Paths.get(bm.getTmpDir(libraryId).getAbsolutePath(), project, pgcnId);
            }
        }

        if (!dest.toFile().mkdirs()) {
            // Pb de droits d'ecriture
            LOG.error("Creation du repertoire impossible dans {} - Probleme de permissions ? ", dest.toAbsolutePath());
            LOG.trace("Sauvegarde de {}/{} annulée", project, pgcnId);
            return;
        }

        final List<StoredFile> masters =
                binaryRepository.getAllByPageIdentifiersAndFileFormat(pagesId, ViewsFormatConfiguration.FileFormat.MASTER);

        if (conf.isSaveMaster() || conf.isDeleteMaster()) {
            if (conf.isSaveMaster()) {
                saveFiles(masters, conf.isSaveMaster(), dest, ViewsFormatConfiguration.FileFormat.MASTER, libraryId);
            }
            if (conf.isDeleteMaster()) {
                suppressFiles(masters, libraryId);
            }
        }
        if ((conf.isSavePdf() || conf.isDeletePdf()) && pdfPageId != null) {
            final StoredFile sf = binaryRepository.getOneByPageIdentifierAndFileFormat(pdfPageId, ViewsFormatConfiguration.FileFormat.MASTER);
            if (sf != null) {
                if (conf.isSavePdf()) {
                    savePdfMaster(sf, dest, libraryId);
                }
                if (conf.isDeletePdf()) {
                    final List<StoredFile> pdfs = new ArrayList<>();
                    pdfs.add(sf);
                    suppressFiles(pdfs, libraryId);
                }
            }
        }
        if (conf.isSavePrint() || conf.isDeletePrint()) {
            final List<StoredFile> prints = binaryRepository.getAllByPageIdentifiersAndFileFormat(pagesId, ViewsFormatConfiguration.FileFormat.PRINT);
            if (conf.isSavePrint()) {
                saveFiles(prints, conf.isSavePrint(), dest, ViewsFormatConfiguration.FileFormat.PRINT, libraryId);
            }
            if (conf.isDeletePrint()) {
                suppressFiles(prints, libraryId);
            }
        }
        if (conf.isSaveView() || conf.isDeleteView()) {
            final List<StoredFile> views = binaryRepository.getAllByPageIdentifiersAndFileFormat(pagesId, ViewsFormatConfiguration.FileFormat.VIEW);
            if (conf.isSaveView()) {
                saveFiles(views, conf.isSaveView(), dest, ViewsFormatConfiguration.FileFormat.VIEW, libraryId);
            }
            if (conf.isDeleteView()) {
                suppressFiles(views, libraryId);
            }
        }
        if (conf.isSaveThumb() || conf.isDeleteThumb()) {
            final List<StoredFile> thumbnails =
                binaryRepository.getAllByPageIdentifiersAndFileFormat(pagesId, ViewsFormatConfiguration.FileFormat.THUMB);
            if (conf.isSaveThumb()) {
                saveFiles(thumbnails, conf.isSaveThumb(), dest, ViewsFormatConfiguration.FileFormat.THUMB, libraryId);
            }
            if (conf.isDeleteThumb()) {
                suppressFiles(thumbnails, libraryId);
            }
        }

        final DocUnit docUnit = docUnitRepository.getOneByPgcnId(pgcnId);
        if (docUnit != null) {

            if (conf.isSaveAipSip()) {
                    final List<File> files = new ArrayList<>();
                    files.add(cinesRequestHandlerService.retrieveAip(docUnit.getIdentifier()));
                    files.add(cinesRequestHandlerService.retrieveSip(docUnit.getIdentifier(), false));
                    saveFiles(files, dest, "aip_sip");
            }

            // mets
            final BibliographicRecord record = docUnit.getRecords().iterator().next();
            final BibliographicRecordDcDTO noticeDto = uiBibliographicRecordService.getOneDc(record.getIdentifier());

            final Path metsPath = Files.createFile(dest.resolve(MetaDatasCheckService.METS_XML_FILE));

            final List<CheckSummedStoredFile> checkSums = new ArrayList<>();
            masters.stream()
                .filter(sf -> sf.getPage().getNumber() != null)
                .forEach(sf -> {
                    final File sourceFile = bm.getFileForStoredFile(sf, libraryId);
                    // On remplit la map pour optimiser le traitement ultérieur des métadonnées
                    try {
                        checkSums.add(exportMetsService.getCheckSummedStoredFile(sf, sourceFile));
                    } catch(final IOException e) {

                    }
                });

            try (final OutputStream out = new FileOutputStream(metsPath.toFile()); final OutputStream bufOut = new BufferedOutputStream(out)) {
                exportMetsService.writeMetadata(bufOut, docUnit, noticeDto, false, checkSums);
                bufOut.flush();
            } catch (final JAXBException | SAXException e) {
                LOG.error("Error when generating mets file for doc {}", docUnit.getPgcnId());
            }
        }

    }

    /**
     * Recopie les fichiers vers le repertoire de sauvegarde : /SVG_DEST/project/pgcnId.
     *
     * @param sfs
     * @param formatToSave
     * @param dest
     */
    public void saveFiles(final List<StoredFile> sfs, final boolean formatToSave, final Path dest,
                          final ViewsFormatConfiguration.FileFormat format, final String libraryId) {

        if (formatToSave) {
            // on cree un ss-repertoire par format sauvegardé
            final Path formatDest = Paths.get(dest.toFile().getPath(), format.label());
            if (formatDest.toFile().mkdirs()) {

                final Map<String, File> files = new HashMap<>();
                sfs.forEach(sf -> {

                    if (ViewsFormatConfiguration.FileFormat.MASTER == format) {
                        files.put(sf.getFilename(), bm.getFileForStoredFile(sf, libraryId));
                    } else {
                        final String fileName = sf.getFilename().substring(0, sf.getFilename().lastIndexOf(".")+1)
                                        + ImageUtils.FORMAT_JPG;
                        files.put(fileName, bm.getFileForStoredFile(sf, libraryId));
                    }

                });

                files.forEach((k, f) -> {
                    final Path copied = Paths.get(formatDest.toFile().getPath(), k);
                    try {
                        Files.copy(f.toPath(), copied);
                    } catch (final IOException e) {
                        LOG.error("Erreur lors de la sauvegarde du fichier {}", k, e);
                        // on continue ...
                    }
                });
            }
        }
    }

    /**
     * Recopie le master pdf dans un sous repertoire dédié.
     *
     * @param pdfSf
     * @param dest
     */
    public void savePdfMaster(final StoredFile pdfSf, final Path dest, final String libraryId) {
        final Path formatDest = Paths.get(dest.toFile().getPath(), "pdf");
        if (formatDest.toFile().mkdirs()) {
            final Path saved = Paths.get(formatDest.toFile().getPath(), pdfSf.getFilename());
            final File pdfFile = bm.getFileForStoredFile(pdfSf, libraryId);

            try {
                Files.copy(pdfFile.toPath(), saved);
            } catch (final IOException e) {
                LOG.error("Erreur lors de la sauvegarde du fichier {}", pdfSf.getFilename(), e);
                // on continue ...
            }
        }
    }

    public void saveFiles(final List<File> files, final Path dest, final String dir) {
        final Path dirDest = Paths.get(dest.toFile().getPath(), dir);
        if (dirDest.toFile().mkdirs()) {
            files.stream().filter(Objects::nonNull).forEach(f -> {
                final Path saved = Paths.get(dirDest.toFile().getPath(), f.getName());
                try {
                    Files.copy(f.toPath(), saved);
                } catch (final IOException e) {
                    LOG.error("Erreur lors de la sauvegarde du fichier {}", f.getName(), e);
                    // on continue ...
                }
            });
        }
    }

    /**
     * On zippe le contenu du repertoire de sauvegarde.
     *
     * @param conf
     * @param project
     */
    public boolean compressSavedFiles(final FilesGestionConfig conf, final String project, final String pgcnId) {


        final String libraryId = conf.getLibrary().getIdentifier();
        final Path dest;
        if (conf.isUseExportFtp()) {
            // vers le ftp parametre ds 'Configurations exports FTP'
            dest = Paths.get(ftpexportCacheDir, libraryId, project, pgcnId);

        } else {
            // ds 1 dir sur le serveur
            dest = Paths.get(conf.getDestinationDir(), project, pgcnId);
        }
        if (!dest.toFile().exists() || !dest.toFile().canWrite()) {
            // bien peu probable mais sait-on jamais...
            return false;
        }

        // Création du fichier
        final String zipName = project + "_" + pgcnId + ".zip";
        final File zipFile =
            new File(dest.toFile().getParentFile().getPath().concat(System.getProperty("file.separator")).concat(zipName));
        try {
            if (!zipFile.createNewFile()) {
                LOG.warn("Probleme à la creation du zip : le fichier {} existe deja!", project+".zip");
            }
        } catch (final IOException e) {
            LOG.error(e.getMessage(), e);
            return false;
        }

        boolean zipped = false;
        try (final FileOutputStream fos = new FileOutputStream(zipFile); final ZipOutputStream zipOut = new ZipOutputStream(fos)) {

            final File toZip = dest.toFile();
            zipOut.setLevel(Deflater.NO_COMPRESSION);
            zip(toZip, toZip.getName(), zipOut);
            LOG.trace("Archive ZIP {} créée dans le dossier {}", toZip.getName(), dest.toString());
            zipped = true;

        } catch (final IOException | SecurityException e) {
            LOG.error("Erreur lors de la compression des fichiers sauvegardes", e);
            // on ne bloque pas le process
        }

        // ZIP ok :
        // => export FTP du zip si prévu
        // => nettoie le repertoire si c'est bien parti.
        boolean exported = false;
        if (zipped) {
            // export
            if (conf.isUseExportFtp()) {
                 exported = ftpExport(conf, zipFile.toPath());
            }
            // Cleaning zip
            if (exported && zipFile.exists() && zipFile.canWrite()) {
                FileUtils.deleteQuietly(zipFile);
            }
        }
        return zipped;
    }


    private void cleanTempFiles(final FilesGestionConfig conf, final String project) {

        if (conf.isUseExportFtp()) {

            final String libraryId = conf.getLibrary().getIdentifier();
            final Path dest;

            // vers le ftp parametre ds 'Configurations exports FTP'
            dest = Paths.get(ftpexportCacheDir, libraryId, project);

            // Cleaning tmp files
            try (final Stream<Path> stream = Files.walk(dest, FileVisitOption.FOLLOW_LINKS)) {

                stream.sorted(Comparator.reverseOrder()).map(Path::toFile).forEach(File::delete);
                stream.close();
            } catch (final IOException | SecurityException e) {
                LOG.error("Erreur lors de la suppression des documents livrés dans {}", dest.toAbsolutePath().toString(), e);
            }
        }
    }


    private boolean ftpExport(final FilesGestionConfig conf, final Path zipPath) {
        ExportFTPConfiguration confExport = conf.getActiveExportFTPConfiguration();
        ExportFTPDeliveryFolder deliveryFolder = conf.getActiveExportFTPDeliveryFolder();

        if (confExport != null && deliveryFolder != null) {
                final SftpConfiguration sftpConf = new SftpConfiguration();
                sftpConf.setActive(true);
                sftpConf.setHost(confExport.getStorageServer());
                sftpConf.setPort(Integer.valueOf(confExport.getPort()));
                sftpConf.setUsername(confExport.getLogin());
                sftpConf.setTargetDir(confExport.getAddress().concat(deliveryFolder.getName()));

                try {
                    sftpConf.setPassword(cryptoService.encrypt(confExport.getPassword()));
                    sftpService.sftpPut(sftpConf, zipPath);
                } catch (final PgcnTechnicalException e) {
                    LOG.error("Erreur Export FTP du fichier {}", zipPath.getFileName(), e);
                    return false;
                }
                return true;
        } else {
            return false;
        }
    }

    /**
     * Suppression physique + logique des storedFiles.
     *
     * @param storedFiles
     */
    public void suppressFiles(final List<StoredFile> storedFiles, final String libraryId) {
        storedFiles.forEach(sf -> bm.deleteFileFromStoredFile(sf, libraryId));
        storedFileRepository.delete(storedFiles);
    }

    /**
     * Comme son nom l'indique (! recursif).
     *
     * @param fileToZip
     * @param fileName
     * @param zipOut
     * @throws IOException
     */
    public void zip(final File fileToZip, final String fileName, final ZipOutputStream zipOut) throws IOException {
        if (fileToZip.isHidden()) {
            return;
        }
        // process directories
        if (fileToZip.isDirectory()) {
            final File[] children = fileToZip.listFiles();
            if (children != null) {
                for (final File childFile : children) {
                    zip(childFile, fileName + "/" + childFile.getName(), zipOut);
                }
            }
            return;
        }
        // process files
        try (final FileInputStream fis = new FileInputStream(fileToZip)) {

            final ZipEntry zipEntry = new ZipEntry(fileName);
            zipOut.putNextEntry(zipEntry);

            final byte[] bytes = new byte[1024];
            int length;

            while ((length = fis.read(bytes)) >= 0) {
                zipOut.write(bytes, 0, length);
            }
        }
    }


    /**
     * Delete files with no line in DB storedFile.
     *
     * @param libraryId
     */
    public void cleanOrphanFiles(final String libraryId) {

        bm.deleteOrphanFiles(libraryId);
        // todo delete empty directories...

    }
}
