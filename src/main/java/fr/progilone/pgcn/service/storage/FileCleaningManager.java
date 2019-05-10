package fr.progilone.pgcn.service.storage;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
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
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import fr.progilone.pgcn.domain.AbstractDomainObject;
import fr.progilone.pgcn.domain.administration.viewsformat.ViewsFormatConfiguration;
import fr.progilone.pgcn.domain.document.DocPage;
import fr.progilone.pgcn.domain.document.DocUnit;
import fr.progilone.pgcn.domain.dto.document.ValidatedDeliveredDocumentDTO;
import fr.progilone.pgcn.domain.filesgestion.FilesGestionConfig;
import fr.progilone.pgcn.domain.lot.Lot;
import fr.progilone.pgcn.domain.project.Project;
import fr.progilone.pgcn.domain.storage.StoredFile;
import fr.progilone.pgcn.repository.document.DocUnitRepository;
import fr.progilone.pgcn.repository.storage.BinaryRepository;
import fr.progilone.pgcn.repository.storage.StoredFileRepository;
import fr.progilone.pgcn.service.delivery.DeliveryService;
import fr.progilone.pgcn.service.document.DocPageService;
import fr.progilone.pgcn.service.exchange.cines.CinesRequestHandlerService;
import fr.progilone.pgcn.service.filesgestion.FilesGestionConfigService;
import fr.progilone.pgcn.service.lot.LotService;
import fr.progilone.pgcn.service.project.ProjectService;

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
                               final DocUnitRepository docUnitRepository) {
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
                } catch (IOException | SecurityException e) {
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
     * 
     * @param tmpRoot
     * @param timeLimit
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
                        final Map<String, List<DocPage>> pages = docPageService.getPagesByProjectId(p.getIdentifier());
                        if (processProjectFiles(conf, pages, StringUtils.isNotBlank(p.getName()) ? p.getName() : p.getIdentifier())) {
                            projectService.setFilesProjectArchived(p.getIdentifier());
                        }
                    });

                    break;
                case TYP_DECLENCH_LOT:

                    final List<Lot> lots = lotService.getClosedLotsByLibrary(conf.getLibrary().getIdentifier(), conf.getDelay());
                    lots.forEach(l -> {
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
        
        pages.keySet().forEach(pgcnId -> {

            final List<String> pagesId =
                pages.get(pgcnId).stream().filter(pg -> pg.getNumber() != null).map(AbstractDomainObject::getIdentifier).collect(Collectors.toList());
            final String pdfPageId =
                pages.get(pgcnId).stream().filter(pg -> pg.getNumber() == null).map(AbstractDomainObject::getIdentifier).findAny().orElse(null);

            final List<StoredFile> zooms = binaryRepository.getAllByPageIdentifiersAndFileFormat(pagesId, ViewsFormatConfiguration.FileFormat.ZOOM);
            suppressFiles(zooms, libraryId);
            final List<StoredFile> xtras =
                binaryRepository.getAllByPageIdentifiersAndFileFormat(pagesId, ViewsFormatConfiguration.FileFormat.XTRAZOOM);
            suppressFiles(xtras, libraryId);

            prepareSaving(conf, project, pgcnId, pagesId, pdfPageId);
        });

        return compressSavedFiles(conf, project);
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
                              final String pdfPageId) {

        if (!conf.isSaveMaster() && !conf.isSavePdf() && !conf.isSavePrint() && !conf.isSaveView() && !conf.isSaveThumb() && !conf.isSaveAipSip()) {
            // Aucune sauvegarde programmée, on quitte..
            return;
        }

        final Path dest = Paths.get(conf.getDestinationDir(), project, pgcnId);
        if (!dest.toFile().mkdirs()) {
            // Pb de droits d'ecriture
            LOG.error("Creation du repertoire impossible dans {} - Probleme de permissions ? ", conf.getDestinationDir());
            LOG.trace("Sauvegarde de {}/{} annulée", project, pgcnId);
            return;
        }
        
        final String libraryId = conf.getLibrary().getIdentifier();

        if (conf.isSaveMaster() || conf.isDeleteMaster()) {
            final List<StoredFile> masters =
                binaryRepository.getAllByPageIdentifiersAndFileFormat(pagesId, ViewsFormatConfiguration.FileFormat.MASTER);
            saveFiles(masters, conf.isSaveMaster(), dest, ViewsFormatConfiguration.FileFormat.MASTER, libraryId);
            if (conf.isDeleteMaster()) {
                suppressFiles(masters, libraryId);
            }
        }
        if ((conf.isSavePdf() || conf.isDeletePdf()) && pdfPageId != null) {
            final StoredFile sf = binaryRepository.getOneByPageIdentifierAndFileFormat(pdfPageId, ViewsFormatConfiguration.FileFormat.MASTER);
            if (conf.isSavePdf()) {
                savePdfMaster(sf, dest, libraryId);
            }
            if (conf.isDeletePdf()) {
                final List<StoredFile> pdfs = new ArrayList<>();
                pdfs.add(sf);
                suppressFiles(pdfs, libraryId);
            }
        }
        if (conf.isSavePrint() || conf.isDeletePrint()) {
            final List<StoredFile> prints = binaryRepository.getAllByPageIdentifiersAndFileFormat(pagesId, ViewsFormatConfiguration.FileFormat.PRINT);
            saveFiles(prints, conf.isSavePrint(), dest, ViewsFormatConfiguration.FileFormat.PRINT, libraryId);
            if (conf.isDeletePrint()) {
                suppressFiles(prints, libraryId);
            }
        }
        if (conf.isSaveView() || conf.isDeleteView()) {
            final List<StoredFile> views = binaryRepository.getAllByPageIdentifiersAndFileFormat(pagesId, ViewsFormatConfiguration.FileFormat.VIEW);
            saveFiles(views, conf.isSaveView(), dest, ViewsFormatConfiguration.FileFormat.VIEW, libraryId);
            if (conf.isDeleteView()) {
                suppressFiles(views, libraryId);
            }
        }
        if (conf.isSaveThumb() || conf.isDeleteThumb()) {
            final List<StoredFile> thumbnails =
                binaryRepository.getAllByPageIdentifiersAndFileFormat(pagesId, ViewsFormatConfiguration.FileFormat.THUMB);
            saveFiles(thumbnails, conf.isSaveThumb(), dest, ViewsFormatConfiguration.FileFormat.THUMB, libraryId);
            if (conf.isDeleteThumb()) {
                suppressFiles(thumbnails, libraryId);
            }
        }
        if (conf.isSaveAipSip()) {

            final DocUnit docUnit = docUnitRepository.getOneByPgcnIdAndState(pgcnId, DocUnit.State.AVAILABLE);
            if (docUnit != null) {
                final List<File> files = new ArrayList<>();
                files.add(cinesRequestHandlerService.retrieveAip(docUnit.getIdentifier()));
                files.add(cinesRequestHandlerService.retrieveSip(docUnit.getIdentifier(), false));
                saveFiles(files, dest, "aip_sip");
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
                    files.put(sf.getFilename(), bm.getFileForStoredFile(sf, libraryId));
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
    public boolean compressSavedFiles(final FilesGestionConfig conf, final String project) {

        final Path dest = Paths.get(conf.getDestinationDir(), project);
        if (!dest.toFile().exists() || !dest.toFile().canWrite()) {
            // bien peu probable mais sait-on jamais...
            return false;
        }

        // Création du fichier
        final File zipFile =
            new File(dest.toFile().getParentFile().getPath().concat(System.getProperty("file.separator")).concat(project).concat(".zip"));
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
            zip(toZip, toZip.getName(), zipOut);
            LOG.trace("Archive ZIP {} créée dans le dossier {}", toZip.getName(), dest.toString());
            zipped = true;

        } catch (IOException | SecurityException e) {
            LOG.error("Erreur lors de la compression des fichiers sauvegardes", e);
        }

        // ZIP ok => nettoie le repertoire.
        if (zipped) {
            try (final Stream<Path> stream = Files.walk(dest, FileVisitOption.FOLLOW_LINKS)) {

                stream.sorted(Comparator.reverseOrder()).map(Path::toFile).forEach(File::delete);
                stream.close();
            } catch (IOException | SecurityException e) {
                LOG.error("Erreur lors de la suppression des documents livrés dans {}", dest.toAbsolutePath().toString(), e);
            }
        }
        return zipped;
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
}
