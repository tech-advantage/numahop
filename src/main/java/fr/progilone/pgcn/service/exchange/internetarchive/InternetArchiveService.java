package fr.progilone.pgcn.service.exchange.internetarchive;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.UriUtils;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.ClientConfiguration;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder.EndpointConfiguration;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;

import fr.progilone.pgcn.domain.AbstractDomainObject;
import fr.progilone.pgcn.domain.administration.InternetArchiveConfiguration;
import fr.progilone.pgcn.domain.administration.viewsformat.ViewsFormatConfiguration;
import fr.progilone.pgcn.domain.document.ArchiveCollection;
import fr.progilone.pgcn.domain.document.ArchiveHeader;
import fr.progilone.pgcn.domain.document.ArchiveItem;
import fr.progilone.pgcn.domain.document.ArchiveSubject;
import fr.progilone.pgcn.domain.document.BibliographicRecord;
import fr.progilone.pgcn.domain.document.DigitalDocument;
import fr.progilone.pgcn.domain.document.DocProperty;
import fr.progilone.pgcn.domain.document.DocPropertyType;
import fr.progilone.pgcn.domain.document.DocUnit;
import fr.progilone.pgcn.domain.exchange.internetarchive.InternetArchiveReport;
import fr.progilone.pgcn.domain.library.Library;
import fr.progilone.pgcn.domain.storage.StoredFile;
import fr.progilone.pgcn.domain.storage.StoredFile.StoredFileType;
import fr.progilone.pgcn.domain.workflow.WorkflowStateKey;
import fr.progilone.pgcn.exception.PgcnTechnicalException;
import fr.progilone.pgcn.repository.storage.BinaryRepository;
import fr.progilone.pgcn.service.administration.InternetArchiveConfigurationService;
import fr.progilone.pgcn.service.document.DocUnitService;
import fr.progilone.pgcn.service.document.common.LanguageCodeService;
import fr.progilone.pgcn.service.document.mapper.UIInternetArchiveItemMapper;
import fr.progilone.pgcn.service.exchange.internetarchive.InternetArchiveItemDTO.MediaType;
import fr.progilone.pgcn.service.exchange.internetarchive.mapper.ArchiveItemMapper;
import fr.progilone.pgcn.service.library.LibraryService;
import fr.progilone.pgcn.service.storage.BinaryStorageManager;
import fr.progilone.pgcn.service.util.CryptoService;
import fr.progilone.pgcn.service.util.DateIso8601Util;

/**
 * Service pour l'interface avec Internet Archive
 *
 * @author jbrunet
 * Créé le 18 avr. 2017
 */
@Service
public class InternetArchiveService {

    private static final Logger LOG = LoggerFactory.getLogger(InternetArchiveService.class);
    
    @Value("${services.archive.alto}")
    private String outputPath;

    private final BinaryStorageManager bm;
    private final DocUnitService docUnitService;
    private final LanguageCodeService languageCodeService;
    private final InternetArchiveConfigurationService confService;
    private final InternetArchiveReportService iaReportService;
    private final CryptoService cryptoService;
    private final UIInternetArchiveItemMapper uiInternetArchiveItemMapper;
    private final BinaryRepository binaryRepository;
    private final LibraryService libraryService;

    private final ArchiveItemMapper archiveItemMapper = ArchiveItemMapper.INSTANCE;

    @Autowired
    public InternetArchiveService(final BinaryStorageManager bm,
                                  final CryptoService cryptoService,
                                  final LanguageCodeService languageCodeService,
                                  final InternetArchiveConfigurationService confService,
                                  final InternetArchiveReportService iaReportService,
                                  final DocUnitService docUnitService,
                                  final UIInternetArchiveItemMapper uiInternetArchiveItemMapper,
                                  final BinaryRepository binaryRepository,
                                  final LibraryService libraryService) {
        this.bm = bm;
        this.cryptoService = cryptoService;
        this.docUnitService = docUnitService;
        this.languageCodeService = languageCodeService;
        this.confService = confService;
        this.iaReportService = iaReportService;
        this.uiInternetArchiveItemMapper = uiInternetArchiveItemMapper;
        this.binaryRepository = binaryRepository;
        this.libraryService = libraryService;
    }

    /**
     * Prépare un item pour Internet Archive. Les champs sont pré-remplis
     *
     * @return
     */
    @Transactional(readOnly = true)
    public InternetArchiveItemDTO prepareItem(final DocUnit docUnit) {
        InternetArchiveItemDTO item;
        final String libraryId = docUnit.getLibrary().getIdentifier();
        if (docUnit.getArchiveItem() != null) {
            item = archiveItemMapper.archiveItemToInternetArchiveItemDTO(docUnit.getArchiveItem());
            item.setSubjects(docUnit.getArchiveItem().getSubjects().stream().map(ArchiveSubject::getValue).collect(Collectors.toList()));
            item.setCollections(docUnit.getArchiveItem().getCollections().stream().map(ArchiveCollection::getValue).collect(Collectors.toList()));
            for (final ArchiveHeader archiveHeader : docUnit.getArchiveItem().getHeaders()) {
                final InternetArchiveItemDTO.CustomHeader customHeader = new InternetArchiveItemDTO.CustomHeader();
                customHeader.setValue(archiveHeader.getValue());
                customHeader.setType(archiveHeader.getType());
                item.getCustomHeaders().add(customHeader);
            }
            // TODO faire juste un count sur le type VIEW !!!
            item.setTotal(getFilesForDocUnit(docUnit, ViewsFormatConfiguration.FileFormat.VIEW, libraryId).size());
            return item;
        }

        item = new InternetArchiveItemDTO();
        
        
        if (docUnit.getCollectionIA() != null) {
            item.addCollection(docUnit.getCollectionIA().getName());
        }

        // rendre configurable
        item.setMediatype(MediaType.texts);

        if (!docUnit.getRecords().isEmpty()) {
            final BibliographicRecord record = docUnit.getRecords().iterator().next();

            // Récupération langue par défaut : avec le plus bas rang
            final Optional<DocProperty> languageProperty = record.getProperties().stream().filter(property -> {
                final DocPropertyType type = property.getType();
                return type.getSuperType() == DocPropertyType.DocPropertySuperType.DC && "language".equals(type.getIdentifier());
            }).reduce((a, b) -> a.getRank() < b.getRank() ? a : b);
            String languageIso = "fre";
            if (languageProperty.isPresent()) {
                languageIso = languageCodeService.getIso6393BForLanguage(languageProperty.get().getValue());
            }
            item.setLanguage(languageIso);

            // Remplissage avec les propriétés
            for (final DocProperty docProperty : record.getProperties()) {
                final DocPropertyType type = docProperty.getType();

                if (type.getSuperType() == DocPropertyType.DocPropertySuperType.DC) {
                    switch (type.getIdentifier()) {
                        case "title":
                            item.setTitle(docProperty.getValue());
                            break;
                        case "subject":
                            item.addSubject(docProperty.getValue());
                            break;
                        case "description":
                            item.addDescription(docProperty.getValue(), docProperty.getRank());
                            break;
                        case "publisher":
                            item.setPublisher(docProperty.getValue());
                            break;
                        case "contributor":
                            item.setContributor(docProperty.getValue());
                            break;
                        case "creator":
                            item.setCreator(docProperty.getValue());
                            break;
                        case "date":
                            item.setDate(DateIso8601Util.importedDateToIso8601(docProperty.getValue()));
                            break;
                        case "coverage":
                            item.setCoverage(docProperty.getValue());
                            break;
                        case "rights":
                            item.setRights(docProperty.getValue());
                            break;
                        case "identifier":
                            item.setArchiveIdentifier(docProperty.getValue());
                            break;
                        case "type":
                            item.setType(docProperty.getValue());
                            break;
                        case "source":
                            item.setSource(docProperty.getValue());
                            break;
                    }
                }
            }
        }
        item.setDescription(String.join("\n", item.getDescriptions().values()));
        // Ajout nb pages
        item.setTotal(getFilesForDocUnit(docUnit, ViewsFormatConfiguration.FileFormat.VIEW, libraryId).size());

        return item;
    }

    @Transactional
    public InternetArchiveReport createItem(final String docUnitId) {
        final DocUnit docUnit = docUnitService.findOne(docUnitId);
        final InternetArchiveItemDTO item = prepareItem(docUnit);
        return createItem(docUnit, item);
    }

    @Transactional
    public InternetArchiveReport createItem(final DocUnit docUnit, final InternetArchiveItemDTO item) {

        // TODO FIXME : choix / config par défaut (à paramétrer)
        final Set<InternetArchiveConfiguration> confs = confService.findByLibraryAndActive(docUnit.getLibrary(), true);

        if (CollectionUtils.isNotEmpty(confs)) {
            final InternetArchiveConfiguration conf = confs.iterator().next();
            final InternetArchiveReport report = iaReportService.createInternetArchiveReport(docUnit, item.getArchiveIdentifier());
            return callS3(docUnit, item, conf, ViewsFormatConfiguration.FileFormat.VIEW, report);
        } else {
            LOG.warn("Aucune configuration pour Internet Archive : aucun export n'a été réalisé");
            return null;
        }
    }

    /**
     * Création d'un Bucket sur Archive contenant les metadata spécifiées pour le {@link DocUnit} indiqué et au bon format
     *
     * @param docUnit
     * @param item
     * @param conf
     * @param format
     * @param report
     * @return
     */
    private InternetArchiveReport callS3(final DocUnit docUnit,
                                         final InternetArchiveItemDTO item,
                                         final InternetArchiveConfiguration conf,
                                         final ViewsFormatConfiguration.FileFormat format,
                                         InternetArchiveReport report) {
        try {
            final BasicAWSCredentials awsCreds = new BasicAWSCredentials(conf.getAccessKey(), cryptoService.decrypt(conf.getSecretKey()));
            final AmazonS3 s3Client = AmazonS3ClientBuilder.standard()
                                                           .withCredentials(new AWSStaticCredentialsProvider(awsCreds))
                                                           .withClientConfiguration(new ClientConfiguration().withSignerOverride("S3SignerType"))
                                                           .withEndpointConfiguration(new EndpointConfiguration("http://s3.us.archive.org", ""))
                                                           .build();

            // Handle Metadata
            final ObjectMetadata metadata = new ObjectMetadata();
            metadata.setHeader("x-amz-auto-make-bucket", 1);

            metadata.setHeader("x-archive-meta-title", getUTF8String(item.getTitle()));
            if (StringUtils.isNotBlank(item.getContributor())) {
                metadata.setHeader("x-archive-meta-contributor", getUTF8String(item.getContributor()));
            }
            if (StringUtils.isNotBlank(item.getCoverage())) {
                metadata.setHeader("x-archive-meta-coverage", getUTF8String(item.getCoverage()));
            }
            if (StringUtils.isNotBlank(item.getCreator())) {
                metadata.setHeader("x-archive-meta-creator", getUTF8String(item.getCreator()));
            }
            if (StringUtils.isNotBlank(item.getCredits())) {
                metadata.setHeader("x-archive-meta-credits", getUTF8String(item.getCredits()));
            }
            if (StringUtils.isNotBlank(item.getDate())) {
                metadata.setHeader("x-archive-meta-date", getUTF8String(item.getDate()));
            }
            if (StringUtils.isNotBlank(item.getDescription())) {
                metadata.setHeader("x-archive-meta-description", getUTF8String(item.getDescription()));
            }
            if (StringUtils.isNotBlank(item.getLanguage())) {
                metadata.setHeader("x-archive-meta-language", getUTF8String(item.getLanguage()));
            }
            if (StringUtils.isNotBlank(item.getLicenseUrl())) {
                metadata.setHeader("x-archive-meta-licenseurl", getUTF8String(item.getLicenseUrl()));
            }
            // Mediatype
            if (item.getCustomMediatype() != null && StringUtils.isNotBlank(item.getCustomMediatype())) {
                metadata.setHeader("x-archive-meta-mediatype", getUTF8String(item.getCustomMediatype()));
            } else if (item.getMediatype() != null && StringUtils.isNotBlank(item.getMediatype().name())) {
                final String media = InternetArchiveItemDTO.MediaType.autre == item.getMediatype() ? "data" : item.getMediatype().name();
                metadata.setHeader("x-archive-meta-mediatype", media);
            }
            if (StringUtils.isNotBlank(item.getNotes())) {
                metadata.setHeader("x-archive-meta-notes", getUTF8String(item.getNotes()));
            }
            if (StringUtils.isNotBlank(item.getPublisher())) {
                metadata.setHeader("x-archive-meta-publisher", getUTF8String(item.getPublisher()));
            }
            if (StringUtils.isNotBlank(item.getRights())) {
                metadata.setHeader("x-archive-meta-rights", getUTF8String(item.getRights()));
            }
            if (StringUtils.isNotBlank(item.getType())) {
                metadata.setHeader("x-archive-meta-type", getUTF8String(item.getType()));
            }
            if (StringUtils.isNotBlank(item.getSource())) {
                metadata.setHeader("x-archive-meta-source", getUTF8String(item.getSource()));
            }

            // Collections
            int count = 1;
            for (final String collection : item.getCollections()) {
                if (count < 10) {
                    metadata.setHeader("x-archive-meta0" + count + "-collection", getUTF8String(collection));
                } else {
                    metadata.setHeader("x-archive-meta" + count + "-collection", getUTF8String(collection));
                }
                count++;
            }
            // Subjects
            if (item.getSubjects() != null && !item.getSubjects().isEmpty()) {
                metadata.setHeader("x-archive-meta-subject", getUTF8String(StringUtils.join(item.getSubjects(), ';')));
            }

            // Champs personnalisés
            if (item.getCustomHeaders() != null && !item.getCustomHeaders().isEmpty()) {
                item.getCustomHeaders().forEach(header -> {
                    metadata.setHeader("x-archive-meta-" + header.getType(), getUTF8String(header.getValue()));
                });
            }

            final String libraryId = docUnit.getLibrary().getIdentifier();
            
            // Upload
            // 1- des views
            final Map<String, File> files = getFilesForDocUnit(docUnit, format, libraryId);
            // 2- du pdf s'il est présent
            final Optional<File> pdf = retrievePdfInMasters(docUnit, libraryId);
            pdf.ifPresent(f -> files.put(f.getName(), f));
            // 3- du fichier alto.xml s'il existe
            final Optional<File> alto = retrieveAlto(docUnit, libraryId);

            report = iaReportService.setReportSending(report, files.size());

            for (final Entry<String, File> entry : files.entrySet()) {
                final String sfName = entry.getKey();
                final File file = entry.getValue();
                if (!file.exists()) {
                    throw new PgcnTechnicalException("Le fichier " + file.getAbsolutePath() + " n'existe pas");
                }

                s3Client.putObject(new PutObjectRequest(item.getArchiveIdentifier(), sfName, new FileInputStream(file), metadata));
                report = iaReportService.updateReport(report, 1);
            }

            report = iaReportService.setReportSent(report);

            final HttpClient client = HttpClientBuilder.create().build();
            final HttpGet request = new HttpGet("https://archive.org/metadata/" + report.getInternetArchiveIdentifier() + "/metadata");
            final HttpResponse response = client.execute(request);

            final String json = EntityUtils.toString(response.getEntity());
            final JSONObject temp = new JSONObject(json);

            if (temp.has("error")) {
                final String error = (String) temp.get("error");
                LOG.warn("docUnit: {}, error: {}", docUnit.getIdentifier(), error);
                report = iaReportService.failReport(report, error);

            } else {
                final JSONObject result = (JSONObject) temp.get("result");
                if (result.has("identifier-ark")) {
                    docUnit.setArkUrl((String) result.get("identifier-ark"));
                }
                if (result.has("fail-reasons")) {
                    final String failReasons = (String) result.get("fail-reasons");
                    LOG.warn("docUnit: {}, failReasons: {}", docUnit.getIdentifier(), failReasons);
                } else {
                    report = iaReportService.setReportArchived(report, LocalDateTime.now(), report.getInternetArchiveIdentifier());
                }

                docUnitService.save(docUnit);
            }

            // Close

        } catch (final AmazonServiceException ase) {
            LOG.error("Caught an AmazonServiceException, which "
                      + "means your request made it "
                      + "to Amazon S3, but was rejected with an error response"
                      + " for some reason.", ase);
            LOG.error("Error Message:    {}", ase.getMessage());
            LOG.error("HTTP Status Code: {}", ase.getStatusCode());
            LOG.error("AWS Error Code:   {}", ase.getErrorCode());
            LOG.error("Error Type:       {}", ase.getErrorType());
            LOG.error("Request ID:       {}", ase.getRequestId());
            report = iaReportService.failReport(report, ase.getMessage());

        } catch (final AmazonClientException ace) {
            LOG.error("Caught an AmazonClientException, which "
                      + "means the client encountered "
                      + "an internal error while trying to "
                      + "communicate with S3, "
                      + "such as not being able to access the network.", ace);
            LOG.error("Error Message: {}", ace.getMessage());
            report = iaReportService.failReport(report, ace.getMessage());

        } catch (final PgcnTechnicalException e) {
            LOG.error("Erreur de traitement: ", e);
            report = iaReportService.failReport(report, e.getMessage());

        } catch (final IOException e) {
            LOG.error("Unable to find file", e);
            report = iaReportService.failReport(report, "File System Error");

        } catch (final JSONException e) {
            LOG.error("Erreur lors du traitement des métadonnées IA", e);
            report = iaReportService.failReport(report, "Erreur lors du traitement des métadonnées d'Internet Archive");
        }
        return report;
    }

    /**
     * Recupere le fichier pdf s'il existe.
     *
     * @param doc
     * @return
     * @throws IOException
     */
    public Optional<File> retrievePdfInMasters(final DocUnit doc, final String libraryId) throws IOException {
        final Optional<File> pdfFile;

        if (CollectionUtils.isEmpty(doc.getDigitalDocuments())) {
            pdfFile = Optional.empty();

        } else {
            final DigitalDocument dd = doc.getDigitalDocuments().iterator().next();
            final String pgId =
                dd.getPages().stream().filter(pg -> pg.getNumber() == null).map(AbstractDomainObject::getIdentifier).findFirst().orElse(null);

            if (pgId != null) {
                final StoredFile sfPdf = binaryRepository.getOneByPageIdentifierAndFileFormat(pgId, ViewsFormatConfiguration.FileFormat.MASTER);
                final File renamed = new File(bm.getTmpDir(libraryId), sfPdf.getFilename());
                FileUtils.copyFile(bm.getFileForStoredFile(sfPdf, libraryId), renamed);
                pdfFile = Optional.of(renamed);

            } else {
                pdfFile = Optional.empty();
            }
        }
        return pdfFile;
    }
    
    public Optional<File> retrieveAlto(final DocUnit doc, final String libraryId) throws IOException {
        final Optional<File> altoFile;
        if (CollectionUtils.isEmpty(doc.getDigitalDocuments())) {
            altoFile = Optional.empty();
        } else {
            final DigitalDocument dd = doc.getDigitalDocuments().iterator().next();            
            final File xmlAlto = new File(Paths.get(outputPath, libraryId,
                                                    dd.getDigitalId()).toFile(), "alto.xml");
            if(xmlAlto != null && xmlAlto.isFile() && xmlAlto.canRead()) {
                altoFile = Optional.of(xmlAlto);
            } else {
                altoFile = Optional.empty();
            }
        }
        
        return altoFile;
    }

    /**
     * Unicode to UTF8
     *
     * @param string
     * @return
     */
    private String getUTF8String(final String string) {
        try {
            return "uri(" + UriUtils.encode(string, "UTF-8") + ")";

        } catch (final UnsupportedEncodingException e) {
            LOG.debug("Conversion non supportée", e);
        }
        return string;
    }

    /**
     * Récupère les fichiers dérivés attachés à un {@link DocUnit}.
     * Ils doivent correspondre au format spécifié
     *
     * @param docUnit
     * @param format
     * @return
     */
    private Map<String, File> getFilesForDocUnit(DocUnit docUnit, final ViewsFormatConfiguration.FileFormat format, final String libraryId) {
        
        docUnit = docUnitService.findOneWithAllDependencies(docUnit.getIdentifier());
        
        final Map<String, File> results = new LinkedHashMap<>();
        docUnit.getDigitalDocuments().forEach(digitalDoc -> {
            digitalDoc.getOrderedPages().forEach(page -> {
                final Optional<StoredFile> stFl = page.getFiles()
                                                      .stream()
                                                      .filter(file -> StoredFileType.DERIVED.equals(file.getType()) && StringUtils.equalsIgnoreCase(
                                                          format.identifier(),
                                                          file.getFileFormat().identifier()))
                                                      .findFirst();
                stFl.ifPresent(storedFile -> {
                    // les derives sont forcement en jpg => on renomme.
                    final int idx = StringUtils.lastIndexOf(storedFile.getFilename(), ".");
                    final String sfName;
                    if (idx < 0) {
                        sfName = storedFile.getFilename();
                    } else {
                        sfName = storedFile.getFilename().substring(0, idx).concat(BinaryStorageManager.EXTENSION_JPG);
                    }
                    results.put(sfName, bm.getFileForStoredFile(storedFile, libraryId));
                });
            });
        });
        return results;
    }

    @Transactional
    public void saveItem(final DocUnit docUnit, final InternetArchiveItemDTO item) {
        final ArchiveItem archiveItem;
        if (docUnit.getArchiveItem() == null) {
            archiveItem = new ArchiveItem();
        } else {
            archiveItem = docUnit.getArchiveItem();
        }
        uiInternetArchiveItemMapper.mapInto(item, archiveItem);
        archiveItem.setDocUnit(docUnit);
        docUnit.setArchiveItem(archiveItem);
        docUnitService.save(docUnit);
    }

    /**
     * Retrouve les docUnit candidates pour diffusion vers Archive.
     * (diffusable et non diffusee - avec notice contenant 1 propriete de type 'identifier' - workflow termine ou en attente de diffusion)
     *
     * @return
     */
    @Transactional(readOnly = true)
    public List<DocUnit> findDocUnitsReadyForArchiveExport() {
        final List<DocUnit> docsToExport = new ArrayList<>();
        final List<Library> libraries = libraryService.findAllByActive(true);
        libraries.stream().filter(lib -> CollectionUtils.isNotEmpty(confService.findByLibraryAndActive(lib, true))).forEach(lib -> {

            final List<DocUnit> archivables = docUnitService.findByLibraryWithArchiveExportDep(lib.getIdentifier());
            archivables.stream().filter(doc -> CollectionUtils.isNotEmpty(doc.getRecords())).filter(doc -> {
                // la propriete identifier doit etre renseignee => l'id de l'export ds Archive
                final BibliographicRecord record = doc.getRecords().iterator().next();
                final DocProperty prop = record.getProperties()
                                               .stream()
                                               .filter(p -> p.getType().getSuperType() == DocPropertyType.DocPropertySuperType.DC)
                                               .filter(p -> StringUtils.equals("identifier", p.getType().getIdentifier()))
                                               .findFirst()
                                               .orElse(null);
                return prop != null && StringUtils.isNotBlank(prop.getValue());
            }).filter(doc -> {
                final boolean notDistributed = iaReportService.findByDocUnit(doc.getIdentifier())
                                                              .stream()
                                                              .filter(iar -> InternetArchiveReport.Status.ARCHIVED == iar.getStatus())
                                                              .collect(Collectors.toList())
                                                              .isEmpty();
                return notDistributed && (doc.getWorkflow().getCurrentStateByKey(WorkflowStateKey.DIFFUSION_DOCUMENT)
                                                                         != null && doc.getWorkflow()
                                                                                       .getCurrentStateByKey(WorkflowStateKey.DIFFUSION_DOCUMENT)
                                                                                       .isCurrentState());

            }).forEach(docsToExport::add);
        });
        return docsToExport;
    }

}
