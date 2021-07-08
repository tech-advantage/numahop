package fr.progilone.pgcn.service.check;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.namespace.QName;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOCase;
import org.apache.commons.io.filefilter.RegexFileFilter;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.apache.commons.lang3.StringUtils;
import org.apache.pdfbox.io.RandomAccessFile;
import org.apache.pdfbox.pdfparser.PDFParser;
import org.apache.tika.Tika;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import fr.progilone.pgcn.domain.check.AutomaticCheckResult;
import fr.progilone.pgcn.domain.check.AutomaticCheckResult.AutoCheckResult;
import fr.progilone.pgcn.domain.delivery.Delivery;
import fr.progilone.pgcn.domain.dto.document.PreDeliveryDocumentFileDTO;
import fr.progilone.pgcn.domain.jaxb.mets.AmdSecType;
import fr.progilone.pgcn.domain.jaxb.mets.MdSecType;
import fr.progilone.pgcn.domain.jaxb.mets.Mets;
import fr.progilone.pgcn.domain.jaxb.mix.Mix;
import fr.progilone.pgcn.service.delivery.DeliveryReportingService;
import fr.progilone.pgcn.service.storage.FileStorageManager;

/**
 * Service de Validation des fichiers de metadonnees.
 *
 * @author erizet
 * Créé le 20 juillet 2017
 */
@Service
public class MetaDatasCheckService {

    private static final Logger LOG = LoggerFactory.getLogger(MetaDatasCheckService.class);

    public static final String METS_XML_FILE = "mets.xml";
    public static final String METS_FILE_FORMAT = "xml";
    public static final String METS_MIME_TYPE = "application/xml";

    private static final String TABLE_EXCEL_FILE = "toc.xlsx";
    private static final String TABLE_EXCEL_FILE2 = "toc.xls";
    public static final String EXCEL_FILE_FORMAT = "xlsx";
    public static final String EXCEL_MIME_TYPE = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
    public static final String EXCEL_FILE_FORMAT2 = "xls";
    public static final String EXCEL_MIME_TYPE2 = "application/vnd.ms-excel";

    public static final String PDF_FILE_FORMAT = "pdf";
    public static final String PDF_MIME_TYPE = "application/pdf";

    public static final String LABEL_MASTER = "master";

    private static final fr.progilone.pgcn.domain.jaxb.mets.ObjectFactory METS_FACTORY = new fr.progilone.pgcn.domain.jaxb.mets.ObjectFactory();

    private final FileStorageManager fm;
    private final DeliveryReportingService reportService;

    @Value("${instance.libraries}")
    private String[] instanceLibraries;

    @Value("${services.metaDatas.path}")
    private String metaDatasDirectory;

    @Autowired
    public MetaDatasCheckService(final FileStorageManager fm, final DeliveryReportingService reportService) {
        this.fm = fm;
        this.reportService = reportService;
    }

    @PostConstruct
    public void initialize() {

        // 1 disk space per library
        Arrays.asList(instanceLibraries).forEach(lib -> {
            try {
                FileUtils.forceMkdir(new File(metaDatasDirectory, lib));
            } catch (final IOException ex) {
                LOG.error(ex.getMessage(), ex);
            }
        });

    }

    /**
     * Vérification fichier :
     * - Extension
     * - Mime Type
     * - conformite / specs.
     *
     * @param toCheck
     * @param format
     * @param type
     * @param role
     * @return
     */
    public AutomaticCheckResult checkMetaDataFileFormat(final AutomaticCheckResult result,
                                                        final Optional<File> toCheck,
                                                        final String format,
                                                        final String type,
                                                        final PreDeliveryDocumentFileDTO.FileRoleEnum role,
                                                        final Map<String, List<MdSecType>> extractedDmdSec) {

        boolean checked = false;

        if (toCheck.isPresent()) {
            final String fileName = toCheck.get().getName();
            result.setResult(AutoCheckResult.OK);

            // Verifie que le fichier correspond au type attendu.
            if (StringUtils.endsWithIgnoreCase(fileName, format)) {
                try {
                    checked = StringUtils.equalsIgnoreCase(new Tika().detect(toCheck.get()), type);
                } catch (final IOException e) {
                    LOG.error(e.getMessage(), e);
                    // on va dire que c'est KO..
                    checked = false;
                }
            }
            if (checked) {

                switch (role) {
                    case METS:
                        // validation spec METS
                        try {
                            final Optional<Mets> mets = unmarshallMetsFile(toCheck.get());
                            LOG.info("le format du fichier METS {} est valide.", fileName);
                            if (extractedDmdSec != null && mets.isPresent()) {
                                extractedDmdSec.put(fileName, mets.get().getDmdSec());
                            }
                        } catch (final JAXBException e) {
                            LOG.error(e.getMessage(), e);
                            result.setResult(AutoCheckResult.OTHER);
                            result.setMessage(fileName.concat(" - METS invalide: ").concat(e.getMessage()));
                        }
                        break;
                    case PDF_MULTI:
                        RandomAccessFile raf = null;
                        try {
                            raf = new RandomAccessFile(toCheck.get(), "r");
                            final PDFParser parser = new PDFParser(raf);
                            parser.setLenient(false);
                            parser.parse();

                        } catch (final IOException e) {
                            LOG.error("[LIVRAISON] Probleme parsing du fichier pdf multi", e);
                            result.setResult(AutoCheckResult.OTHER);
                            result.setMessage(fileName.concat(" - PDF invalide: ").concat(e.getMessage()));
                        } finally {
                            if (raf != null && !raf.isClosed()) {
                                try {
                                    raf.close();
                                } catch (final IOException e) {
                                    // tant pis, on aura essayé...
                                    LOG.error(e.getMessage(), e);
                                }
                            }
                        }
                        break;
                    case OTHER:
                    case EXCEL:
                    default:
                        break;
                }

            } else {
                LOG.info("le format du fichier {}: {} est refuse.", role, fileName);
                result.setResult(AutoCheckResult.OTHER);
                result.setMessage(fileName.concat(" ne correspond pas au format attendu"));
            }
        } else {
            LOG.info("Fichier {} introuvable", role);
            result.setResult(AutoCheckResult.OTHER);
            result.setMessage("Fichier ".concat(role.toString()).concat(" introuvable"));
        }
        return result;
    }

    /**
     * Recuperation des fichiers de metadonnees dans les dossiers / sous dossiers de la livraison.
     *
     * @param delivery
     * @param metaDatasDTO
     * @return Map Listes de fichiers classés par document_id
     */
    public Map<String, List<File>> getMetadataFiles(final Delivery delivery,
                                                    final File[] subDirectories,
                                                    final Map<String, Set<PreDeliveryDocumentFileDTO>> metaDatasDTO) {

        LOG.info("Recuperation des fichiers de metadonnees classes par document");
        final Map<String, List<String>> fileNames = new HashMap<>();

        metaDatasDTO.forEach((docId, metas) -> {
            final List<String> listNames = new ArrayList<>();
            metas.forEach((dto) -> {
                listNames.add(dto.getName());
            });
            fileNames.put(docId, listNames);
        });

        final Map<String, List<File>> files = new HashMap<>();
        fileNames.forEach((idDoc, names) -> {
            final List<File> mdFiles = new ArrayList<>();
            names.forEach((fileName) -> {

                for (final File directory : subDirectories) {
                    // idDoc correspond au prefix => on ne cherche que ds les directories du document.
                    if (StringUtils.containsIgnoreCase(directory.getName(), idDoc)) {
                        final Collection<File> metaDataFiles = FileUtils.listFiles(directory,
                                                                                   new RegexFileFilter(fileName, IOCase.SENSITIVE),
                                                                                   TrueFileFilter.TRUE);
                        if (metaDataFiles.size() > 0) {
                            mdFiles.addAll(metaDataFiles);
                        }
                    }
                }
            });
            files.put(idDoc, mdFiles);
        });
        return files;
    }

    public List<File> getMetadataFilesByDoc(final Delivery delivery,
                                            final File[] subDirectories,
                                            final String prefix,
                                            final Set<PreDeliveryDocumentFileDTO> metaDatasDTO) {

        LOG.info("Recuperation des fichiers de metadonnees du document");
        final List<String> listNames = new ArrayList<>();
        metaDatasDTO.forEach(dto -> {
            listNames.add(dto.getName());
        });

        final List<File> mdFiles = new ArrayList<>();
        listNames.forEach((fileName) -> {
            for (final File directory : subDirectories) {
                final Collection<File> metaDataFiles = FileUtils.listFiles(directory,
                                                                           new RegexFileFilter(fileName, IOCase.SENSITIVE),
                                                                           TrueFileFilter.TRUE);
                if (metaDataFiles.size() > 0) {
                    mdFiles.addAll(metaDataFiles);
                }
            }
        });
        return mdFiles;
    }

    /**
     * Recuperation du fichier Excel pour TOC.
     *
     * @param digitalId
     * @return
     */
    public Optional<File> getMetaDataExcelFile(final String digitalId, final String libraryId) {

        final Path root = Paths.get(metaDatasDirectory, libraryId, digitalId);
        Path excelPath = root.resolve(TABLE_EXCEL_FILE);
        File excelFile = excelPath.toFile();
        if (excelFile == null) {
            excelPath = root.resolve(TABLE_EXCEL_FILE2);
            excelFile = excelPath.toFile();
        }
        // ni .xls, ni .xlsx ...
        if (excelFile == null) {
            return Optional.empty();
        }
        return Optional.of(excelFile);
    }

    /**
     * Recuperation du fichier mets brut.
     * @param digitalId
     * @param libraryId
     * @return
     */
    public Optional<File> getMetsXmlFile(final String digitalId, final String libraryId) {
        Optional<File> metsXml = Optional.empty();
        final Path root = Paths.get(metaDatasDirectory, libraryId, digitalId);
        final Path metsPath = root.resolve(METS_XML_FILE);
        if (metsPath != null) {
            metsXml = Optional.of(metsPath.toFile());
        }
        return metsXml;
    }

    /**
     * Recuperation de l'objet Mets pour TOC.
     *
     * @param digitalId
     * @return
     */
    public Optional<Mets> getMetaDataMetsFile(final String digitalId, final String libraryId) {

        Optional<Mets> mets;
        final Optional<File> metsFile = getMetsXmlFile(digitalId, libraryId);
        try {
            mets = metsFile.isPresent() ? unmarshallMetsFile(metsFile.get()) : Optional.empty();
        } catch (final JAXBException e) {
            LOG.error("JAXB : fichier METS illisible - {}", e.getLocalizedMessage());
            mets = Optional.empty();
        }
        return mets;
    }

    /**
     * Construit un objet Mets à partir du fichier xml.
     *
     * @param file
     * @return un objet Mets si ok, null si ko
     * @throws JAXBException
     */
    private Optional<Mets> unmarshallMetsFile(final File file) throws JAXBException {
        Mets mets = null;
        if (file.exists() && file.canRead()) {
            final JAXBContext context = JAXBContext.newInstance(fr.progilone.pgcn.domain.jaxb.mets.ObjectFactory.class,
                                                                Mix.class,
                                                                QName.class,
                                                                fr.progilone.pgcn.domain.jaxb.dc.ObjectFactory.class,
                                                                fr.progilone.pgcn.domain.jaxb.dc.ElementContainer.class,
                                                                fr.progilone.pgcn.domain.jaxb.dc.SimpleLiteral.class);
            final Unmarshaller unmarshaller = context.createUnmarshaller();
            mets = (Mets) unmarshaller.unmarshal(file);
        }
        if (mets == null) {
            return Optional.empty();
        }
        return Optional.of(mets);
    }

    /**
     * Assure le stockage des fichiers de metadonnees
     * dans leurs répertoires respectifs.
     *
     * @param metaDatasDTO
     * @param metaDataFiles
     */
    public void handleMetaDataFiles(final Map<String, Set<PreDeliveryDocumentFileDTO>> metaDatasDTO,
                                    final Map<String, List<File>> metaDataFiles,
                                    final Delivery delivery,
                                    final String libraryId,
                                    final Set<String> prefixToTreat) {

        final Path root = Paths.get(metaDatasDirectory, libraryId);

        metaDatasDTO.forEach((idDoc, dtos) -> {

            final List<File> files = metaDataFiles.get(idDoc);
            final String[] dirsToAdd = {idDoc};

            // on ne recupere que les TDM des doc à traiter.
            if (prefixToTreat.contains(idDoc)) {

                dtos.forEach((dto) -> {
                    String targetName = null;
                    switch (dto.getRole()) {
                        case METS:
                            targetName = METS_XML_FILE;
                            break;
                        case EXCEL:
                            targetName = TABLE_EXCEL_FILE;
                            break;
                        default:
                            // do nothing for others..
                            LOG.debug("another role (not TOC) : {}", dto.getRole());
                            break;
                    }
    
                    // retrieve file
                    final Optional<File> toCheck = files == null ?
                                                   Optional.empty() :
                                                   files.stream().filter(file -> StringUtils.equalsIgnoreCase(file.getName(), dto.getName())).findFirst();
                    if (toCheck.isPresent() && StringUtils.isNotBlank(targetName)) {
                        try (final BufferedInputStream input = new BufferedInputStream(new FileInputStream(toCheck.get()))) {
                            fm.copyInputStreamToFileWithOtherDirs(input, root.toFile(), Arrays.asList(dirsToAdd), targetName, true, false);
                            reportService.updateReport(delivery, Optional.empty(), Optional.empty(), dto.getName().concat(" enregistré"), libraryId);
    
                        } catch (final FileNotFoundException e) {
                            LOG.error("Fichier {} non trouvé.", dto.getName());
                            reportService.updateReport(delivery, Optional.empty(), Optional.empty(), dto.getName().concat(" introuvable"), libraryId);
                        } catch (final IOException e) {
                            LOG.error("Erreur {} lors du traitement du fichier {}.", e.getMessage(), dto.getName());
                            reportService.updateReport(delivery,
                                                       Optional.empty(),
                                                       Optional.empty(),
                                                       "Erreur lors du traitement du fichier ".concat(dto.getName()),
                                                       libraryId);
                        }
                    } else {
                        LOG.debug("Pas de TDM pour {} dans {}", dto.getName(), files);
                    }
                    
                });
            }
        });
    }

    /**
     * Retourne la liste des AmdSecType des masters.
     *
     * @param mets
     * @return
     */
    public List<AmdSecType> getMasterAmdSec(final Mets mets) {

        final List<String> masterIds = new ArrayList<>();
        // Recuperation des id des masters.
        mets.getFileSec()
            .getFileGrp()
            .stream()
            .filter(fgMaster -> StringUtils.containsIgnoreCase(fgMaster.getUSE(), LABEL_MASTER))
            .forEach(fgMaster -> {
                fgMaster.getFile().forEach(f -> {
                    final MdSecType t = (MdSecType) f.getADMID().get(0);
                    masterIds.add(t.getID());
                });
            });

        // Recuperation des objets AmdSecType correspondant aux masters.
        final List<AmdSecType> listAmdSec = new ArrayList<>();
        for (final String id : masterIds) {
            mets.getAmdSec().forEach(asec -> {

                asec.getTechMD().stream().filter(t -> StringUtils.equals(t.getID(), id)).findFirst().ifPresent(t -> {
                    // on reconstruit => elimine les techMD inutiles (mire, etc..)
                    final AmdSecType ast = METS_FACTORY.createAmdSecType();
                    ast.getTechMD().add(t);
                    ast.getRightsMD().addAll(asec.getRightsMD());
                    listAmdSec.add(ast);
                });
            });
        }
        return listAmdSec;
    }

}
