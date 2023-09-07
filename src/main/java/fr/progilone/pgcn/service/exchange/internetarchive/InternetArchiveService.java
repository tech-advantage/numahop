package fr.progilone.pgcn.service.exchange.internetarchive;

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
import fr.progilone.pgcn.domain.document.ArchiveContributor;
import fr.progilone.pgcn.domain.document.ArchiveCoverage;
import fr.progilone.pgcn.domain.document.ArchiveCreator;
import fr.progilone.pgcn.domain.document.ArchiveHeader;
import fr.progilone.pgcn.domain.document.ArchiveItem;
import fr.progilone.pgcn.domain.document.ArchiveLanguage;
import fr.progilone.pgcn.domain.document.ArchiveSubject;
import fr.progilone.pgcn.domain.document.BibliographicRecord;
import fr.progilone.pgcn.domain.document.DigitalDocument;
import fr.progilone.pgcn.domain.document.DocProperty;
import fr.progilone.pgcn.domain.document.DocPropertyType;
import fr.progilone.pgcn.domain.document.DocUnit;
import fr.progilone.pgcn.domain.exchange.internetarchive.InternetArchiveReport;
import fr.progilone.pgcn.domain.exchange.internetarchive.InternetArchiveReport.Status;
import fr.progilone.pgcn.domain.library.Library;
import fr.progilone.pgcn.domain.storage.StoredFile;
import fr.progilone.pgcn.domain.workflow.DocUnitWorkflow;
import fr.progilone.pgcn.domain.workflow.WorkflowStateKey;
import fr.progilone.pgcn.exception.PgcnTechnicalException;
import fr.progilone.pgcn.repository.document.DocPropertyTypeRepository;
import fr.progilone.pgcn.repository.storage.BinaryRepository;
import fr.progilone.pgcn.service.administration.InternetArchiveConfigurationService;
import fr.progilone.pgcn.service.document.DocUnitService;
import fr.progilone.pgcn.service.document.common.LanguageCodeService;
import fr.progilone.pgcn.service.document.mapper.UIInternetArchiveItemMapper;
import fr.progilone.pgcn.service.exchange.internetarchive.InternetArchiveItemDTO.CustomHeader;
import fr.progilone.pgcn.service.exchange.internetarchive.InternetArchiveItemDTO.MediaType;
import fr.progilone.pgcn.service.exchange.internetarchive.mapper.ArchiveItemMapper;
import fr.progilone.pgcn.service.library.LibraryService;
import fr.progilone.pgcn.service.storage.AltoService;
import fr.progilone.pgcn.service.storage.BinaryStorageManager;
import fr.progilone.pgcn.service.util.CryptoService;
import fr.progilone.pgcn.service.util.DateIso8601Util;
import fr.progilone.pgcn.service.util.transaction.TransactionService;
import fr.progilone.pgcn.service.workflow.WorkflowService;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.Normalizer;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.TimeUnit;
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

/**
 * Service pour l'interface avec Internet Archive
 *
 * @author jbrunet
 *         Créé le 18 avr. 2017
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
    private final TransactionService transactionService;
    private final WorkflowService workflowService;
    private final DocPropertyTypeRepository docPropertyTypeRepository;
    private final AltoService altoService;

    private final ArchiveItemMapper archiveItemMapper = ArchiveItemMapper.INSTANCE;

    private final long TIME_SECONDS_BEFORE_RETRY = 60;

    @Autowired
    public InternetArchiveService(final BinaryStorageManager bm,
                                  final CryptoService cryptoService,
                                  final LanguageCodeService languageCodeService,
                                  final InternetArchiveConfigurationService confService,
                                  final InternetArchiveReportService iaReportService,
                                  final DocUnitService docUnitService,
                                  final UIInternetArchiveItemMapper uiInternetArchiveItemMapper,
                                  final BinaryRepository binaryRepository,
                                  final LibraryService libraryService,
                                  final TransactionService transactionService,
                                  final WorkflowService workflowService,
                                  final DocPropertyTypeRepository docPropertyTypeRepository,
                                  final AltoService altoService) {
        this.bm = bm;
        this.cryptoService = cryptoService;
        this.docUnitService = docUnitService;
        this.languageCodeService = languageCodeService;
        this.confService = confService;
        this.iaReportService = iaReportService;
        this.uiInternetArchiveItemMapper = uiInternetArchiveItemMapper;
        this.binaryRepository = binaryRepository;
        this.libraryService = libraryService;
        this.transactionService = transactionService;
        this.workflowService = workflowService;
        this.docPropertyTypeRepository = docPropertyTypeRepository;
        this.altoService = altoService;
    }

    /**
     * Prépare un item pour Internet Archive. Les champs sont pré-remplis
     *
     * @return
     */
    public InternetArchiveItemDTO prepareItem(final String docUnitId) {

        final DocUnit docUnit = docUnitService.findOneWithAllDependencies(docUnitId);

        final InternetArchiveItemDTO item;
        final String libraryId = docUnit.getLibrary().getIdentifier();
        if (docUnit.getArchiveItem() != null) {
            item = archiveItemMapper.archiveItemToInternetArchiveItemDTO(docUnit.getArchiveItem());
            item.setSubjects(docUnit.getArchiveItem().getSubjects().stream().map(ArchiveSubject::getValue).collect(Collectors.toList()));
            item.setCollections(docUnit.getArchiveItem().getCollections().stream().map(ArchiveCollection::getValue).collect(Collectors.toList()));
            item.setCoverages(docUnit.getArchiveItem().getCoverages().stream().map(ArchiveCoverage::getValue).collect(Collectors.toList()));
            item.setContributors(docUnit.getArchiveItem().getContributors().stream().map(ArchiveContributor::getValue).collect(Collectors.toList()));
            item.setCreators(docUnit.getArchiveItem().getCreators().stream().map(ArchiveCreator::getValue).collect(Collectors.toList()));
            item.setLanguages(docUnit.getArchiveItem().getLanguages().stream().map(ArchiveLanguage::getValue).collect(Collectors.toList()));
            for (final ArchiveHeader archiveHeader : docUnit.getArchiveItem().getHeaders()) {
                final InternetArchiveItemDTO.CustomHeader customHeader = new InternetArchiveItemDTO.CustomHeader();
                customHeader.setValue(archiveHeader.getValue());
                customHeader.setType(archiveHeader.getType());
                item.getCustomHeaders().add(customHeader);
            }

            // Nombre de pages
            final DigitalDocument dd = docUnit.getDigitalDocuments().stream().findAny().orElse(null);
            if (dd != null) {
                final long total = dd.getPages().stream().filter(p -> p.getNumber() != null).count();
                try {
                    item.setTotal(Math.toIntExact(total));
                } catch (final ArithmeticException e) {
                    LOG.error(e.getMessage(), e);
                    // on fait plus long du coup...
                    item.setTotal(getFilesForDocUnit(docUnit, ViewsFormatConfiguration.FileFormat.VIEW, libraryId).size());
                }
            }

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
            final List<DocProperty> languageProperties = record.getProperties().stream().filter(property -> {
                final DocPropertyType type = property.getType();
                return type.getSuperType() == DocPropertyType.DocPropertySuperType.DC && "language".equals(type.getIdentifier());
            }).collect(Collectors.toList());
            final String languageIso = "fre";
            item.setLanguages(new ArrayList<>());
            if (!languageProperties.isEmpty()) {
                languageProperties.forEach(docProperty -> item.addLanguage(languageCodeService.getIso6393BForLanguage(docProperty.getValue())));
            } else {
                item.addLanguage(languageIso);
            }

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
                            item.addContributor(docProperty.getValue());
                            break;
                        case "creator":
                            item.addCreator(docProperty.getValue());
                            break;
                        case "date":
                            item.setDate(DateIso8601Util.importedDateToIso8601(docProperty.getValue()));
                            break;
                        case "coverage":
                            item.addCoverage(docProperty.getValue());
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
                } else if (type.getSuperType() == DocPropertyType.DocPropertySuperType.CUSTOM_ARCHIVE || type.getSuperType() == DocPropertyType.DocPropertySuperType.CUSTOM) {
                    // on charge les dependances du type
                    final Set<DocProperty> dProperties = docPropertyTypeRepository.findOneWithDependencies(type.getIdentifier()).getDocProperties();
                    dProperties.stream()
                               .filter(dp -> StringUtils.equals(record.getIdentifier(), dp.getRecord().getIdentifier()))
                               .filter(dp -> StringUtils.equals(dp.getType().getIdentifier(), type.getIdentifier()))
                               .sorted(Comparator.comparing(DocProperty::getRank))
                               .forEach(dp -> {
                                   final CustomHeader header = new CustomHeader();
                                   header.setType(normalizeLabel(dp.getType().getLabel()));
                                   header.setValue(dp.getValue());
                                   item.getCustomHeaders().add(header);
                               });
                }
            }
        }
        item.setDescription(String.join("\n", item.getDescriptions().values()));
        // Ajout nb pages
        item.setTotal(getFilesForDocUnit(docUnit, ViewsFormatConfiguration.FileFormat.VIEW, libraryId).size());
        // Ajout URL de la license
        if (docUnit.getProject().getLicenseUrl() != null) {
            item.setLicenseUrl(docUnit.getProject().getLicenseUrl());
        }

        return item;
    }

    /**
     * Supprime espace, accents etc..
     *
     * @param label
     * @return
     */
    private String normalizeLabel(final String label) {
        String norm = Normalizer.normalize(label, Normalizer.Form.NFD);
        norm = norm.replaceAll("[^\\p{ASCII}]", "");
        norm.trim().replace("_", "-");
        return norm.trim().replace(" ", "-");
    }

    public InternetArchiveReport createItem(final String docUnitId, final boolean automaticExport) {
        final DocUnit docUnit = docUnitService.findOneWithAllDependencies(docUnitId);
        final InternetArchiveItemDTO item = transactionService.executeInNewTransactionWithReturn(() -> prepareItem(docUnitId));
        // pas de transaction pour les upload...
        return createItem(docUnit, item, true, null);
    }

    public InternetArchiveReport createItem(final String docUnitId, final InternetArchiveItemDTO item, final String userId) {
        final DocUnit docUnit = docUnitService.findOneWithAllDependencies(docUnitId);
        // pas de transaction pour les upload...
        return createItem(docUnit, item, true, userId);
    }

    public InternetArchiveReport createItem(final DocUnit docUnit, final InternetArchiveItemDTO item, final boolean automaticEport, final String userId) {

        // TODO FIXME : choix / config par défaut (à paramétrer)
        final Set<InternetArchiveConfiguration> confs = confService.findByLibraryAndActive(docUnit.getLibrary(), true);
        final InternetArchiveReport resultReport;

        if (CollectionUtils.isNotEmpty(confs)) {
            final InternetArchiveConfiguration conf = confs.iterator().next();
            final InternetArchiveReport report = iaReportService.createInternetArchiveReport(docUnit, item.getArchiveIdentifier());
            // en dehors d'une transaction sinon timeout exception possible...
            resultReport = callS3(docUnit, item, conf, ViewsFormatConfiguration.FileFormat.VIEW, report);
        } else {
            LOG.warn("Aucune configuration pour Internet Archive : aucun export n'a été réalisé");
            return null;
        }

        if (resultReport.getStatus() == Status.ARCHIVED) {
            // On ouvre une transation en fin d'export du doc si success.
            transactionService.executeInNewTransaction(() -> {

                if (StringUtils.isNotBlank(resultReport.getArkUrl())) {
                    docUnit.setArkUrl(resultReport.getArkUrl());
                    docUnitService.save(docUnit);
                }
                if (workflowService.isStateRunning(docUnit.getIdentifier(), WorkflowStateKey.DIFFUSION_DOCUMENT)) {
                    if (automaticEport) {
                        workflowService.processAutomaticState(docUnit.getIdentifier(), WorkflowStateKey.DIFFUSION_DOCUMENT);
                    } else {
                        workflowService.processState(docUnit.getIdentifier(), WorkflowStateKey.DIFFUSION_DOCUMENT, userId);
                    }
                }
            });

        }
        return resultReport;
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
            if (item.getContributors() != null && !item.getContributors().isEmpty()) {
                metadata.setHeader("x-archive-meta-contributor", getUTF8String(StringUtils.join(item.getContributors(), ';')));
            }
            if (item.getCoverages() != null && !item.getCoverages().isEmpty()) {
                metadata.setHeader("x-archive-meta-coverage", getUTF8String(StringUtils.join(item.getCoverages(), ';')));
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
            if (item.getLanguages() != null && !item.getLanguages().isEmpty()) {
                metadata.setHeader("x-archive-meta-language", getUTF8String(StringUtils.join(item.getLanguages(), ';')));
            }
            if (StringUtils.isNotBlank(item.getLicenseUrl())) {
                metadata.setHeader("x-archive-meta-licenseurl", getUTF8String(item.getLicenseUrl()));
            }
            // Mediatype
            if (item.getCustomMediatype() != null && StringUtils.isNotBlank(item.getCustomMediatype())) {
                metadata.setHeader("x-archive-meta-mediatype", getUTF8String(item.getCustomMediatype()));
            } else if (item.getMediatype() != null && StringUtils.isNotBlank(item.getMediatype().name())) {
                final String media = InternetArchiveItemDTO.MediaType.autre == item.getMediatype() ? "data"
                                                                                                   : item.getMediatype().name();
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

            // Creator
            int countCreator = 1;
            for (final String creator : item.getCreators()) {
                if (countCreator < 10) {
                    metadata.setHeader("x-archive-meta0" + countCreator
                                       + "-creator",
                                       getUTF8String(creator));
                } else {
                    metadata.setHeader("x-archive-meta" + countCreator
                                       + "-creator",
                                       getUTF8String(creator));
                }
                countCreator++;
            }

            // Collections
            int count = 1;
            for (final String collection : item.getCollections()) {
                if (count < 10) {
                    metadata.setHeader("x-archive-meta0" + count
                                       + "-collection",
                                       getUTF8String(collection));
                } else {
                    metadata.setHeader("x-archive-meta" + count
                                       + "-collection",
                                       getUTF8String(collection));
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
            final Optional<File> pdf = retrievePdfInMasters(docUnit, libraryId);
            // Upload
            // 1- des views
            final Map<String, File> files = getFilesForDocUnit(docUnit, format, libraryId);
            // 2- du pdf s'il est présent
            pdf.ifPresent(f -> files.put(f.getName(), f));
            // 3- du fichier alto.xml s'il existe
            final Optional<File> alto = retrieveAlto(docUnit, libraryId);
            alto.ifPresent(f -> files.put(f.getName(), f));

            report = iaReportService.setReportSending(report, files.size());

            LOG.info("Export => Internet Archive: Debut upload doc {} - docType: {}", docUnit.getPgcnId(), item.getType());
            int cpt = 0;

            for (final Entry<String, File> entry : files.entrySet()) {
                cpt++;
                final String sfName = entry.getKey();
                final File file = entry.getValue();
                if (!file.exists()) {
                    throw new PgcnTechnicalException("Le fichier " + file.getAbsolutePath()
                                                     + " n'existe pas");
                }
                if (cpt % 10 == 0) {
                    LOG.info("Export => Internet Archive: {} fichiers traités", cpt);
                }
                try (final FileInputStream in = new FileInputStream(file)) {
                    metadata.setContentLength(in.available());
                    if (file.length() != in.available()) {
                        LOG.info("Attention: difference file.length : {} vs input stream available : {}", file.length(), metadata.getContentLength());
                    }
                    final PutObjectRequest por = new PutObjectRequest(item.getArchiveIdentifier(), sfName, in, metadata);
                    try {

                        s3Client.putObject(por);
                    } catch (final AmazonServiceException ase) {
                        // cas particulier erreur 503 SlowDown => on pause 30sec et on re essaie 1 fois....
                        if (ase.getStatusCode() == 503 && StringUtils.contains(ase.getErrorCode(), "SlowDown")) {

                            LOG.error("Caught AmazonServiceException 503 : SlowDown - retries to put file {} in {} seconds", sfName, TIME_SECONDS_BEFORE_RETRY);
                            try {
                                TimeUnit.SECONDS.sleep(TIME_SECONDS_BEFORE_RETRY);
                            } catch (final InterruptedException e) {
                                LOG.error("InterruptedException when retries to put {} in S3", sfName);
                                throw ase;
                            }
                            // tentative 2 apres 1 pause
                            s3Client.putObject(por);
                            LOG.info("{} put successfully in S3 after retry", sfName);

                        } else {
                            throw ase;
                        }
                    }
                }
                report = iaReportService.updateReport(report, 1);
            }

            LOG.info("Export => Internet Archive: Fin export doc {}", docUnit.getPgcnId());
            report = iaReportService.setReportSent(report);

            final HttpClient client = HttpClientBuilder.create().build();
            final HttpGet request = new HttpGet("https://archive.org/metadata/" + report.getInternetArchiveIdentifier()
                                                + "/metadata");
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
                    // docUnit.setArkUrl((String) result.get("identifier-ark"));
                    report.setArkUrl((String) result.get("identifier-ark"));
                }
                if (result.has("fail-reasons")) {
                    final String failReasons = (String) result.get("fail-reasons");
                    LOG.warn("docUnit: {}, failReasons: {}", docUnit.getIdentifier(), failReasons);
                } else {
                    report = iaReportService.setReportArchived(report, LocalDateTime.now(), report.getInternetArchiveIdentifier());
                }

            }

            // Close

        } catch (final AmazonServiceException ase) {
            LOG.error("Caught an AmazonServiceException, which " + "means your request made it "
                      + "to Amazon S3, but was rejected with an error response"
                      + " for some reason.",
                      ase);
            LOG.error("Error Message:    {}", ase.getMessage());
            LOG.error("HTTP Status Code: {}", ase.getStatusCode());
            LOG.error("AWS Error Code:   {}", ase.getErrorCode());
            LOG.error("Error Type:       {}", ase.getErrorType());
            LOG.error("Request ID:       {}", ase.getRequestId());
            report = iaReportService.failReport(report, ase.getMessage());

        } catch (final AmazonClientException ace) {
            LOG.error("Caught an AmazonClientException, which " + "means the client encountered "
                      + "an internal error while trying to "
                      + "communicate with S3, "
                      + "such as not being able to access the network.",
                      ace);
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
            final String pgId = dd.getPages().stream().filter(pg -> pg.getNumber() == null).map(AbstractDomainObject::getIdentifier).findFirst().orElse(null);

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
            final List<File> xmlAlto = altoService.retrieveAlto(dd.getDigitalId(), libraryId, true, false);
            if (!xmlAlto.isEmpty()) {
                altoFile = xmlAlto.stream().findFirst();
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
        return "uri(" + UriUtils.encode(string, "UTF-8")
               + ")";
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

                final StoredFile storedFile = binaryRepository.getOneByPageIdentifierAndFileFormat(page.getIdentifier(), format);

                if (storedFile != null) {
                    // les derives sont forcement en jpg => on renomme.
                    final int idx = StringUtils.lastIndexOf(storedFile.getFilename(), ".");
                    final String sfName;
                    if (idx < 0) {
                        sfName = storedFile.getFilename();
                    } else {
                        sfName = storedFile.getFilename().substring(0, idx).concat(BinaryStorageManager.EXTENSION_JPG);
                    }
                    results.put(sfName, bm.getFileForStoredFile(storedFile, libraryId));
                }
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
     * (diffusable et non diffusee - avec notice contenant 1 propriete de type 'identifier' - workflow en attente de diffusion)
     *
     * @return
     */
    @Transactional(readOnly = true)
    public List<String> findDocUnitsReadyForArchiveExport() {
        final List<String> docsToExport = new ArrayList<>();
        final List<Library> libraries = libraryService.findAllByActive(true);

        libraries.stream().filter(lib -> CollectionUtils.isNotEmpty(confService.findByLibraryAndActive(lib, true))).forEach(lib -> {

            final List<String> archivableDocIds = workflowService.findDocUnitWorkflowsForArchiveExport(lib.getIdentifier())
                                                                 .stream()
                                                                 .map(DocUnitWorkflow::getDocUnit)
                                                                 .map(DocUnit::getIdentifier)
                                                                 .collect(Collectors.toList());

            if (!archivableDocIds.isEmpty()) {
                final List<DocUnit> archivables = docUnitService.findDocUnitWithArchiveExportDepIn(archivableDocIds);

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
                    return iaReportService.findByDocUnit(doc.getIdentifier())
                                          .stream()
                                          .filter(iar -> InternetArchiveReport.Status.ARCHIVED == iar.getStatus())
                                          .collect(Collectors.toList())
                                          .isEmpty();
                }).map(DocUnit::getIdentifier).forEach(docsToExport::add);
            }
        });
        return docsToExport;
    }

}
