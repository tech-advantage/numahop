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
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;
import java.util.zip.ZipOutputStream;

import javax.annotation.PostConstruct;
import javax.xml.bind.JAXBException;

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
import fr.progilone.pgcn.domain.library.Library;
import fr.progilone.pgcn.domain.storage.CheckSummedStoredFile;
import fr.progilone.pgcn.domain.storage.StoredFile;
import fr.progilone.pgcn.domain.workflow.WorkflowStateKey;
import fr.progilone.pgcn.exception.PgcnTechnicalException;
import fr.progilone.pgcn.exception.PgcnUncheckedException;
import fr.progilone.pgcn.service.MailService;
import fr.progilone.pgcn.service.administration.omeka.OmekaConfigurationService;
import fr.progilone.pgcn.service.document.DocUnitService;
import fr.progilone.pgcn.service.document.ui.UIBibliographicRecordService;
import fr.progilone.pgcn.service.exchange.ssh.SftpService;
import fr.progilone.pgcn.service.library.LibraryService;
import fr.progilone.pgcn.service.storage.BinaryStorageManager;
import fr.progilone.pgcn.service.storage.FileCleaningManager;
import fr.progilone.pgcn.service.util.CryptoService;
import fr.progilone.pgcn.service.util.DateUtils;
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
    
    private static final String FILE_HEADER = "Dublin Core:Identifier" + CSV_COL_SEP + "Dublin Core:Title" + CSV_COL_SEP + "Dublin Core:Subject" + CSV_COL_SEP + "Dublin Core:Publisher" + CSV_COL_SEP 
            + "Dublin Core:Date" + CSV_COL_SEP + "Dublin Core:Creator" + CSV_COL_SEP + "Dublin Core:Description" + CSV_COL_SEP + "Dublin Core:Source" + CSV_COL_SEP + "Dublin Core:Rights" + CSV_COL_SEP 
            + "Dublin Core:Format" + CSV_COL_SEP + "Dublin Core:Language" + CSV_COL_SEP + "Dublin Core:Type" + CSV_COL_SEP + "collection" + CSV_COL_SEP + "Item type" + CSV_COL_SEP + "file" + CSV_COL_SEP + "tags";

    
    private final LibraryService libraryService;
    private final BinaryStorageManager bm;
    private final DocUnitService docUnitService;
    private final OmekaConfigurationService omekaConfigurationService;
    private final UIBibliographicRecordService uiBibliographicRecordService;
    private final SftpService sftpService;
    private final CryptoService cryptoService;
    private final FileCleaningManager fileCleaningManager;
    private final MailService mailService;
    
    @Autowired
    public OmekaService(final LibraryService libraryService, final OmekaConfigurationService omekaConfigurationService, 
                        final BinaryStorageManager bm, final DocUnitService docUnitService,
                        final UIBibliographicRecordService uiBibliographicRecordService,
                        final SftpService sftpService, final CryptoService cryptoService, 
                        final FileCleaningManager fileCleaningManager, final MailService mailService) {
        this.libraryService = libraryService;
        this.omekaConfigurationService = omekaConfigurationService;
        this.bm = bm;
        this.docUnitService = docUnitService;
        this.uiBibliographicRecordService = uiBibliographicRecordService;
        this.sftpService = sftpService;
        this.cryptoService = cryptoService;
        this.fileCleaningManager = fileCleaningManager;
        this.mailService = mailService;
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
    public List<DocUnit> findDocUnitsReadyForOmekaExport() {
        
        final List<DocUnit> docsToExport = new ArrayList<>();
        
        final List<Library> libraries = libraryService.findAllByActive(true);
        libraries.stream()
                .filter(lib-> CollectionUtils.isNotEmpty(omekaConfigurationService.findByLibraryAndActive(lib, true)))
                .forEach(lib -> {
                    
                   final List<DocUnit> distribuables = docUnitService.findByLibraryWithOmekaExportDep(lib.getIdentifier());
                   distribuables.stream().filter(doc -> {
                       
                       return doc.getOmekaExportStatus() ==  DocUnit.ExportStatus.NONE
                               && CollectionUtils.isNotEmpty(doc.getRecords())
                               && (doc.getWorkflow().getCurrentStateByKey(WorkflowStateKey.DIFFUSION_DOCUMENT_OMEKA) != null 
                                               && doc.getWorkflow().getCurrentStateByKey(WorkflowStateKey.DIFFUSION_DOCUMENT_OMEKA).isCurrentState());
                   })
                                       .forEach(doc-> {
                                           docsToExport.add(doc);
                                       });
        });
        LOG.debug("OMEKA :  " + docsToExport.size()  +" Docs recuperes pour l'export");
        return docsToExport;
    }
    

    @Transactional
    public boolean exportDocToOmeka(final DocUnit du, final boolean multiple, final boolean lastDoc, final boolean firstDoc) {
        
        boolean exported = false;
        // recharge docUnit
        final DocUnit doc = docUnitService.findOneWithAllDependencies(du.getIdentifier());
        updateDocUnitOmekaStatus(doc, ExportStatus.IN_PROGRESS);
        
        // Omeka config.
        final OmekaConfiguration conf = omekaConfigurationService.findByLibraryAndActive(doc.getLibrary(), true).stream().findFirst().orElse(null);
        if (conf == null) {
            LOG.trace("Omeka Conf. introuvable => DocUnit[{}] - Export Omeka impossible.", doc.getIdentifier());
            return exported;
        }   
        // Recup données de la notice.
        final BibliographicRecordDcDTO metaDC = getDataRecord(doc);
        if (metaDC == null) {
            LOG.trace("Omeka - Notice introuvable => DocUnit[{}] - Export Omeka impossible.", doc.getIdentifier());
            return exported;
        }
        Path path = null;
        Path zipPath = null;
        try {
            // Génération des fichiers / répertoires OMEKA
            path = exportDocUnit(doc, metaDC, false, doc.getPgcnId(), conf, multiple, firstDoc);
            // on zippe le tout
            final String name = DateUtils.formatDateToString(LocalDateTime.now(), "yyyy-MM-dd HH-mm-ss") + "_" + doc.getPgcnId(); 
            zipPath = compressOmekaFiles(path.toFile(), name);
            
            // Tranferts du zip généré
            if (zipPath != null && zipPath.toFile().exists()) {
                
                final SftpConfiguration sftpConf = new SftpConfiguration();
                sftpConf.setActive(true);
                sftpConf.setHost(conf.getStorageServer());
                sftpConf.setPort(Integer.valueOf(conf.getPort()));
                sftpConf.setUsername(conf.getLogin());
                sftpConf.setPassword(cryptoService.encrypt(conf.getPassword()));
                sftpConf.setTargetDir(conf.getAddress());
                try {
                    sftpService.sftpPut(sftpConf, zipPath);
                    exported = true;
                } 
                catch(final PgcnTechnicalException e) {
                    LOG.error("Erreur Export OMEKA", e);
                    exported = false;
                }
            }           
        } catch (final Exception e) {
            LOG.error("Erreur Export OMEKA", e);
            exported = false;
        }
        
        // Suppression du répertoire généré, après le traitement si sent.
        LOG.debug("Suppression du répertoire {}", path.toAbsolutePath().toString());
        FileUtils.deleteQuietly(path.toFile());
        
        if (exported) {
            updateDocUnitOmekaStatus(doc, ExportStatus.SENT);
            // suppression du fichier zip une fois l'envoi effectué
            FileUtils.deleteQuietly(zipPath.toFile());
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
     * @param delivery
     * @param mailTo
     */
    public void sendCsvFile(final Path rootPath, final String mailTo, final boolean multiple, final DocUnit docUnit) {
        
        final String[] to = {mailTo};
        final File csvDir = rootPath.toFile();
        final String csvName = multiple ? CSV_NAME_MASS_EXPORT : 
                                    docUnit.getLabel() + "_" + docUnit.getIdentifier() + ".csv";  
        final File csvFile = new File(csvDir, csvName);
        if (mailService.sendEmailWithAttachment(null, to, "Fichier CSV Import Omeka", "Cf. ci-joint", csvFile, "text/plain", true, false)) {
            // OK on peut supprimer le csv
            FileUtils.deleteQuietly(csvFile);
        }
    }
    
    
    /**
     * On zippe le contenu du repertoire à exporter.
     *
     * @param conf
     * @param project
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
                LOG.warn("Probleme à la creation du zip : le fichier {} existe deja!", name+".zip");
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

        } catch (IOException | SecurityException e) {
            LOG.error("Erreur lors de la compression des fichiers sauvegardes", e);
        }

        // ZIP ok => nettoie le repertoire.
        if (zipped) {
            try (final Stream<Path> stream = Files.walk(destDir.toPath(), FileVisitOption.FOLLOW_LINKS)) {

                stream.sorted(Comparator.reverseOrder()).map(Path::toFile).forEach(File::delete);
                stream.close();
            } catch (IOException | SecurityException e) {
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
     * @param docUnit
     * @param metaDc
     * @param metaEad
     * @param directory
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
     * @param docUnit
     * @param metaDc
     * @param conf
     * @param depotPath
     * @param listWithCheckSummedStoredFiles
     * @param multiple 
     * @param firstDoc 
     * @return
     */
    private File createDocUnitOmekaCsv(final DocUnit docUnit, final BibliographicRecordDcDTO metaDc, 
                                       final OmekaConfiguration conf, final Path depotPath,
                                       final List<CheckSummedStoredFile> listWithCheckSummedStoredFiles,
                                       final boolean multiple, final boolean firstDoc) {
        
        if (depotPath == null 
            || depotPath.toFile() == null 
                || !depotPath.toFile().canRead()) {
            return null;
        }
        
        final File csvDir = depotPath.getParent().toFile();
        final String csvName;
        if (multiple) {  // export de masse
            csvName = CSV_NAME_MASS_EXPORT;
            if (firstDoc) { // 1er doc de l'export de masse
               // supprime un eventuel ancien csv multiple qui aurait survecu...
               final File oldCsv = new File(csvDir, csvName);
               if (oldCsv.exists()) {
                   FileUtils.deleteQuietly(oldCsv);
               }
            }
        } else {
            csvName = docUnit.getLabel() + "_" + docUnit.getIdentifier() + ".csv";
        }
        
        File csvFile = new File(csvDir, csvName);
        if (!multiple || !(csvFile.exists() && csvFile.canWrite()) ) {
            csvFile = initializeCsvFile(csvFile);
        }
        final String depositAdress = conf.getAccessUrl();
               
        // Alimentation du CSV en append
        try (final Writer writer = new FileWriter(csvFile, true)) {            

            writer.append(NEW_LINE_SEPARATOR);
            // champs DC
            writer.append(docUnit.getPgcnId()).append(CSV_COL_SEP);
            writer.append(getFormatedValues(metaDc.getTitle())).append(CSV_COL_SEP);
            writer.append(getFormatedValues(metaDc.getSubject())).append(CSV_COL_SEP);
            writer.append(getFormatedValues(metaDc.getPublisher())).append(CSV_COL_SEP);
            writer.append(getFormatedValues(metaDc.getDate())).append(CSV_COL_SEP);
            writer.append(getFormatedValues(metaDc.getCreator())).append(CSV_COL_SEP);
            writer.append(getFormatedValues(metaDc.getDescription())).append(CSV_COL_SEP);
            writer.append(getFormatedValues(metaDc.getSource())).append(CSV_COL_SEP);
            writer.append(getFormatedValues(metaDc.getRights())).append(CSV_COL_SEP);
            writer.append(getFormatedValues(metaDc.getFormat())).append(CSV_COL_SEP);
            writer.append(getFormatedValues(metaDc.getLanguage())).append(CSV_COL_SEP);
            writer.append(getFormatedValues(metaDc.getType())).append(CSV_COL_SEP);
            
            // collection omeka
            if (docUnit.getOmekaCollection().getName() != null) {
                writer.append(docUnit.getOmekaCollection().getName());
            }
            writer.append(CSV_COL_SEP);
            // item omeka
            if (docUnit.getOmekaItem().getName() != null) {
                writer.append(docUnit.getOmekaItem().getName());
            }
            writer.append(CSV_COL_SEP);
            // liste des fichiers
            writer.append(getFilesAdress(docUnit.getIdentifier(), 
                                         listWithCheckSummedStoredFiles, 
                                         depositAdress)).append(CSV_COL_SEP);
            
            //tag  (todo : docUnit.getTag - simple string à ajouter...)
            //writer.append(docUnit.getTag());
            writer.append(EMPTY_FIELD_VALUE);

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
    private File initializeCsvFile(final File csvFile) {
        
        try (final Writer writer = new FileWriter(csvFile)) {
            // Entête
            writer.append(FILE_HEADER.toString());
            
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
        
        final String startAdr = adress + docId + "/view/";
        
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
    public BibliographicRecordDcDTO getDataRecord(final DocUnit docUnit) {
        final ExportData ed = docUnit.getExportData();
        BibliographicRecordDcDTO dto;
        if(ed != null) {
            dto = new BibliographicRecordDcDTO();
            ed.getProperties()
                .stream()
                .filter(p -> p.getType().getSuperType() == DocPropertyType.DocPropertySuperType.DC)
                .sorted(Comparator.comparing(ExportProperty::getRank))
                .forEach(p -> {
                    try {
                        final String dcProperty = p.getType().getIdentifier();
                        final List<String> current = (List<String>) PropertyUtils.getSimpleProperty(dto, dcProperty);
                        current.add(p.getValue());

                    } catch (ReflectiveOperationException | IllegalArgumentException e) {
                        LOG.error(e.getMessage(), e);
                    }
                });

        } else {
            if (docUnit.getRecords().iterator().hasNext()) {
                final BibliographicRecord record = docUnit.getRecords().iterator().next();
                dto = uiBibliographicRecordService.getOneDc(record.getIdentifier());
            } else {
                dto = null;
            }
        }
        return dto;
    }
    
    /**
     * DEPOT: fichiers à archiver
     *
     * @param root
     * @param dirDepot
     * @return Path du répertoire créé
     * @throws IOException
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
                        .stream()
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
}
