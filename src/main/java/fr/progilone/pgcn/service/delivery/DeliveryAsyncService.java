package fr.progilone.pgcn.service.delivery;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import javax.xml.bind.JAXBElement;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOCase;
import org.apache.commons.io.filefilter.RegexFileFilter;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.apache.commons.lang3.StringUtils;
import org.apache.pdfbox.io.MemoryUsageSetting;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.concurrent.DelegatingSecurityContextCallable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionStatus;

import com.google.common.collect.Iterables;

import fr.progilone.pgcn.domain.AbstractDomainObject;
import fr.progilone.pgcn.domain.administration.viewsformat.ViewsFormatConfiguration;
import fr.progilone.pgcn.domain.check.AutomaticCheckResult;
import fr.progilone.pgcn.domain.check.AutomaticCheckResult.AutoCheckResult;
import fr.progilone.pgcn.domain.check.AutomaticCheckType.AutoCheckType;
import fr.progilone.pgcn.domain.checkconfiguration.AutomaticCheckRule;
import fr.progilone.pgcn.domain.delivery.DeliveredDocument;
import fr.progilone.pgcn.domain.delivery.Delivery;
import fr.progilone.pgcn.domain.delivery.Delivery.DeliveryStatus;
import fr.progilone.pgcn.domain.document.BibliographicRecord;
import fr.progilone.pgcn.domain.document.DigitalDocument;
import fr.progilone.pgcn.domain.document.DocPage;
import fr.progilone.pgcn.domain.document.DocProperty;
import fr.progilone.pgcn.domain.document.DocPropertyType;
import fr.progilone.pgcn.domain.document.DocUnit;
import fr.progilone.pgcn.domain.document.PhysicalDocument;
import fr.progilone.pgcn.domain.document.sample.Sample;
import fr.progilone.pgcn.domain.dto.check.SplitFilename;
import fr.progilone.pgcn.domain.dto.document.PreDeliveryDocumentFileDTO;
import fr.progilone.pgcn.domain.ftpconfiguration.FTPConfiguration;
import fr.progilone.pgcn.domain.jaxb.dc.SimpleLiteral;
import fr.progilone.pgcn.domain.jaxb.mets.MdSecType;
import fr.progilone.pgcn.domain.lot.Lot;
import fr.progilone.pgcn.domain.ocrlangconfiguration.OcrLanguage;
import fr.progilone.pgcn.domain.storage.StoredFile;
import fr.progilone.pgcn.domain.workflow.WorkflowStateKey;
import fr.progilone.pgcn.exception.PgcnException;
import fr.progilone.pgcn.exception.PgcnTechnicalException;
import fr.progilone.pgcn.repository.document.PhysicalDocumentRepository;
import fr.progilone.pgcn.repository.storage.BinaryRepository;
import fr.progilone.pgcn.service.check.AutomaticCheckService;
import fr.progilone.pgcn.service.check.MetaDatasCheckService;
import fr.progilone.pgcn.service.delivery.mapper.PrefixedDocumentsMapper;
import fr.progilone.pgcn.service.document.BibliographicRecordService;
import fr.progilone.pgcn.service.document.DigitalDocumentService;
import fr.progilone.pgcn.service.document.DocPageService;
import fr.progilone.pgcn.service.document.DocPropertyTypeService;
import fr.progilone.pgcn.service.document.DocUnitService;
import fr.progilone.pgcn.service.document.SlipService;
import fr.progilone.pgcn.service.document.ui.UIDigitalDocumentService;
import fr.progilone.pgcn.service.es.EsDeliveryService;
import fr.progilone.pgcn.service.lot.LotService;
import fr.progilone.pgcn.service.sample.SampleService;
import fr.progilone.pgcn.service.storage.BinaryStorageManager;
import fr.progilone.pgcn.service.storage.ImageMagickService;
import fr.progilone.pgcn.service.storage.TesseractService;
import fr.progilone.pgcn.service.util.DeliveryProgressService;
import fr.progilone.pgcn.service.util.JobRunner;
import fr.progilone.pgcn.service.util.transaction.TransactionService;
import fr.progilone.pgcn.service.workflow.WorkflowService;

/**
 * Service effectuant les tâches asynchrones de le livraison
 * <br/>
 * <ul>
 * <li>Réalisation des contrôles automatiques</li>
 * <li>Stockage des fichiers</li>
 * <li>Génération des fichiers dérivés</li>
 * </ul>
 *
 * @author jbrunet / erizet
 *         Créé le 16 mai 2017
 */
@Service
public class DeliveryAsyncService {

    private static final String EXTENSION_FORMAT_PDF = "PDF";

    private static final String NO_SAMPLING = "NO_SAMPLE";
    private static final String SAMPLING_DOC_DELIV = "SAMPLE_DOC_DELIV";
    private static final String SAMPLING_PAGE_ONE_DOC = "SAMPLE_PAGE_ONE_DOC";
    private static final String SAMPLING_PAGE_ALL_DOC = "SAMPLE_PAGE_ALL_DOC";

    private static final String TYP_MSG_INFO = "INFO";
    private static final String TYP_MSG_WARN = "WARN";
    private static final String TYP_MSG_SUCCESS = "SUCCESS";

    private static final int SAMPLING_MIN_SIZE_LIST = 3;

    private static final Logger LOG = LoggerFactory.getLogger(DeliveryAsyncService.class);

    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    private final DeliveryService deliveryService;
    private final EsDeliveryService esDeliveryService;
    private final PhysicalDocumentRepository physicalDocumentRepository;
    private final DocPageService docPageService;
    private final DigitalDocumentService digitalDocumentService;
    private final AutomaticCheckService autoCheckService;
    private final BinaryStorageManager bm;
    private final TransactionService transactionService;
    private final PrefixedDocumentsMapper mapper;
    private final MetaDatasCheckService metaDatasCheckService;
    private final LotService lotService;
    private final DeliveryReportingService reportService;
    private final ImageMagickService imService;
    private final SampleService sampleService;
    private final WorkflowService workflowService;
    private final BinaryRepository binaryRepository;
    private final DeliveryProgressService deliveryProgressService;
    private final SlipService slipService;
    private final DocPropertyTypeService docPropertyTypeService;
    private final BibliographicRecordService bibliographicRecordService;
    private final TesseractService tesseractService;
    private final DocUnitService docUnitService;
    private final UIDigitalDocumentService uidigitalDocumentService;

    @Autowired
    public DeliveryAsyncService(final DeliveryService deliveryService,
                                final EsDeliveryService esDeliveryService,
                                final PhysicalDocumentRepository physicalDocumentRepository,
                                final DocPageService docPageService,
                                final DigitalDocumentService digitalDocumentService,
                                final TransactionService transactionService,
                                final AutomaticCheckService autoCheckService,
                                final BinaryStorageManager bm,
                                final PrefixedDocumentsMapper mapper,
                                final MetaDatasCheckService metaDatasCheckService,
                                final LotService lotService,
                                final DeliveryReportingService reportService,
                                final ImageMagickService imService,
                                final SampleService sampleService,
                                final WorkflowService workflowService,
                                final BinaryRepository binaryRepository,
                                final DeliveryProgressService deliveryProgressService,
                                final SlipService slipService,
                                final DocPropertyTypeService docPropertyTypeService,
                                final BibliographicRecordService bibliographicRecordService,
                                final TesseractService tesseractService,
                                final DocUnitService docUnitService,
                                final UIDigitalDocumentService uidigitalDocumentService) {
        this.deliveryService = deliveryService;
        this.esDeliveryService = esDeliveryService;
        this.physicalDocumentRepository = physicalDocumentRepository;
        this.docPageService = docPageService;
        this.digitalDocumentService = digitalDocumentService;
        this.autoCheckService = autoCheckService;
        this.bm = bm;
        this.transactionService = transactionService;
        this.mapper = mapper;
        this.metaDatasCheckService = metaDatasCheckService;
        this.lotService = lotService;
        this.reportService = reportService;
        this.imService = imService;
        this.sampleService = sampleService;
        this.workflowService = workflowService;
        this.binaryRepository = binaryRepository;
        this.deliveryProgressService = deliveryProgressService;
        this.slipService = slipService;
        this.docPropertyTypeService = docPropertyTypeService;
        this.bibliographicRecordService = bibliographicRecordService;
        this.tesseractService = tesseractService;
        this.docUnitService = docUnitService;
        this.uidigitalDocumentService = uidigitalDocumentService;
    }

    /**
     * @return Map prefix, document
     */
    private Map<String, PrefixedDocuments> getDocumentsForPrefix(final DeliveryProcessResults processElement) {
        // Mapping inverse (peut être optimisé)
        final Map<String, PrefixedDocuments> documentsForPrefix = new HashMap<>();
        processElement.getDocumentsDTOForPrefix().forEach((prefix, prefixedDocDTO) -> {
            final PrefixedDocuments prefixedDoc = mapper.mapFromDTO(prefixedDocDTO);
            documentsForPrefix.put(prefix, prefixedDoc);
        });
        return documentsForPrefix;
    }

    /**
     * Initialisation de la Delivery avec ses dépendances en amont de la transaction.
     *
     * @param id
     *            delivery
     * @param documentsForPrefix
     *            Map
     * @return Delivery
     */
    private Delivery initializeDelivery(final String id, final Map<String, PrefixedDocuments> documentsForPrefix) {
        
        return transactionService.executeInNewTransactionWithReturn(() -> {
        
            final Delivery delivery = deliveryService.findOneWithDep(id);
            delivery.setDepositDate(LocalDate.now());
    
            documentsForPrefix.forEach((prefix, prefixedDoc) -> {
                final DigitalDocument unitializedDigitalDoc = Iterables.getOnlyElement(prefixedDoc.getDigitalDocuments());
                final DigitalDocument dd = digitalDocumentService.findOne(unitializedDigitalDoc.getIdentifier());
    
                final DeliveredDocument deliveredDocument = digitalDocumentService.getDeliveredDocument(dd, delivery);
                deliveredDocument.setDeliveryDate(dd.getDeliveryDate());
                deliveredDocument.setNbPages(dd.getNbPages());
                deliveredDocument.setTotalLength(dd.getTotalLength());
                deliveredDocument.setStatus(dd.getStatus());
                deliveredDocument.setDelivery(delivery);
                deliveredDocument.setDigitalDocument(dd);
                deliveredDocument.setStatus(DigitalDocument.DigitalDocumentStatus.DELIVERING);

                dd.setStatus(DigitalDocument.DigitalDocumentStatus.DELIVERING);

                digitalDocumentService.save(dd);
            });

            return deliveryService.save(delivery);
        });

    }


    /**
     * Traitement de la livraison technique : contrôles, stockage des fichiers, dérivés.
     *
     * Passe par ExecutorService => empêche les livraisons successives de se téléscoper.
     *
     * @param identifier
     *            delivery
     * @param processElement
     *            DeliveryProcessResults
     */
    public void processDelivery(final String identifier, final DeliveryProcessResults processElement) throws PgcnTechnicalException {

        executorService.submit(new DelegatingSecurityContextCallable<>(Executors.callable(() -> {

            final String libraryId = processElement.getLibraryId();

            final Map<String, PrefixedDocuments> documentsForPrefix = getDocumentsForPrefix(processElement);
            final Delivery delivery = initializeDelivery(identifier, documentsForPrefix);
            final LocalDateTime dtHrDeliv = LocalDateTime.now();

            final String expectedFormat = delivery.getLot().getRequiredFormat();
            final boolean isPdfDelivery = EXTENSION_FORMAT_PDF.equalsIgnoreCase(expectedFormat);
            final String seqSeparator = delivery.getLot().getActiveCheckConfiguration().getSeparators();

            final AutomaticCheckRule radicalRule = processElement.getCheckingRules().get(AutoCheckType.FILE_RADICAL);
            final boolean isRadicalActive = radicalRule != null && radicalRule.isActive();

            final Map<String, Integer> expectedPagesByPrefix = new HashMap<>();

            // Recup/stockage données DmdSec pour création notice éventuelle.
            final Map<String, List<MdSecType>> extractedDmdSec;
            if (Lot.Type.DIGITAL.equals(delivery.getLot().getType())) {
                extractedDmdSec = new HashMap<>();
            } else {
                extractedDmdSec = null;
            }

            try {
                // Recuperation des metadatas via IM en amont de la transaction (evite pbs de timeout).
                final Map<File, Optional<Map<String, String>>> fileMetadatasForCheck = getFileMetadatasForCheck(delivery,
                                                                                                                documentsForPrefix,
                                                                                                                seqSeparator,
                                                                                                                isPdfDelivery,
                                                                                                                isRadicalActive,
                                                                                                                expectedFormat);
                deliveryProgressService.deliveryProgress(delivery,
                                                         null,
                                                         TYP_MSG_INFO,
                                                         "DELIVERING",
                                                         20,
                                                         "Fin Collecte Métadonnées : " + fileMetadatasForCheck.size() + " fichiers traités");

                final AutomaticCheckRule masterRule = processElement.getCheckingRules().get(AutoCheckType.WITH_MASTER);
                final boolean isMasterActive = masterRule != null && masterRule.isActive();

                final AutomaticCheckRule ocrGenerateRule = processElement.getCheckingRules().get(AutoCheckType.GENER_PDF_OCR);
                final boolean isOcrPdfGeneration = ocrGenerateRule != null && ocrGenerateRule.isActive();

                Map<String, List<AutomaticCheckResult>> mapResults = new HashMap<>();
                Map<String, List<File>> multiPdfs = new HashMap<>();
                Map<String, List<File>> tocFiles = new HashMap<>();
                Map<String, Map<Integer, String>> extractedOcr = null;

                if (!isPdfDelivery) {
                    // Recuperation des fichiers de TOC et pdf multis / OCR.
                    final Map<String, Map<String, List<File>>> tocOcrFiles = prepareTocAndOcrTreatment(delivery,
                                                                                                       processElement.getMetadatasDTOForPrefix());
                    tocFiles = tocOcrFiles.get("tocFiles");
                    // Si on ne doit pas generer les pdf ocr => on traite tout de suite
                    if (!isOcrPdfGeneration) {
                        multiPdfs = tocOcrFiles.get("multiPdfs");
                        // Extraction text ocr des pdfs multi.
                        extractedOcr = extractOcrText(multiPdfs, documentsForPrefix.size());
                    }

                }

                /*
                 * Lancement des CONTROLES AUTOMATIQUES sur les docUnit de la livraison
                 */
                toggleAllDeliveryFlags(delivery, true);
                if (isMasterActive) {
                    LOG.info("Lancement des contrôles automatiques");
                    mapResults = automaticChecks(delivery, processElement, documentsForPrefix, fileMetadatasForCheck, expectedPagesByPrefix);
                    LOG.info("Fin des contrôles automatiques");
                } else {
                    LOG.info("Livraison sans masters : tous les controles / masters sont shuntés!");
                    deliveryProgressService.deliveryProgress(delivery, null, TYP_MSG_INFO, "DELIVERING", 25, "Livraison sans contrôle/masters", true);
                }

                /*
                 * CONTROLES des fichiers de METADONNEES.
                 * (Table des matières METS/EXCEL - PDF/A pour OCR)
                 */
                LOG.info("Controle Fichiers Table des matières et/ou PDF");
                final List<AutomaticCheckResult> metaResults = checkFileMetaDatas(delivery, processElement, extractedDmdSec);

                deliveryProgressService.deliveryProgress(delivery, null, TYP_MSG_INFO, "DELIVERING", 25, "Fin Controles Automatiques", true);

                // reconstruit une liste globale pour flagger la delivery.
                final List<AutomaticCheckResult> checkResults = new ArrayList<>();
                mapResults.values()
                          .forEach(checkResults::addAll);
                final List<AutomaticCheckResult> globalResults = new ArrayList<>(checkResults);
                globalResults.addAll(metaResults);
                handleDeliveryCheckState(delivery, globalResults);
                // Etat global de la livraison
                final boolean deliveryRefused = refuseDelivery(delivery, processElement.getCheckingRules());

                /* WORKFLOW : Results by document */
                final Map<String, PrefixedDocuments> documentsToReject = new HashMap<>();
                final Map<String, PrefixedDocuments> documentsToTreat = new HashMap<>();
                mapResults.forEach((key, value) -> {
                    // Récupération de l'identifiant du docUnit
                    final String docUnitId =
                            digitalDocumentService.findDocUnitByIdentifier(Iterables.getOnlyElement(documentsForPrefix.get(key)
                                                                                                    .getDigitalDocuments()).getIdentifier())
                                                                                               .getIdentifier();
                    if (value.stream().anyMatch(res -> AutoCheckResult.KO.equals(res.getResult()))) {
                        documentsToReject.put(key, documentsForPrefix.get(key));
                        // # 3644 Lot numerique : on peut avoir 1 workflow si relivraison !
                        if (workflowService.isWorkflowRunning(docUnitId)) {
                            // On rejete la tâche
                            workflowService.rejectAutomaticState(
                                                                 docUnitId,
                                                                 WorkflowStateKey.CONTROLES_AUTOMATIQUES_EN_COURS);
                        }
                    } else {
                        documentsToTreat.put(key, documentsForPrefix.get(key));
                        // # 3644 Lot numerique : on peut avoir 1 workflow si relivraison !
                        if (workflowService.isWorkflowRunning(docUnitId)) {
                            // On valide la tâche
                            workflowService.processAutomaticState(
                                                                  docUnitId,
                                                                  WorkflowStateKey.CONTROLES_AUTOMATIQUES_EN_COURS);
                        }
                    }
                });

                // Delivery Report.
                reportService.createReport(delivery,
                                           dtHrDeliv,
                                           deliveryRefused,
                                           documentsToTreat,
                                           documentsToReject.size(),
                                           globalResults,
                                           processElement.getCheckingRules(),
                                           expectedPagesByPrefix,
                                           seqSeparator,
                                           libraryId);

                Map<String, List<File>> filesForOcr = new HashMap<>();
                // Views treatment.
                if (isMasterActive) {
                    // On ajoute au système de fichiers
                    final LocalDateTime startTrt = LocalDateTime.now();
                    LOG.info("Ajout des fichiers au système de fichiers");
                    reportService.updateReport(delivery, Optional.empty(), Optional.of(LocalDateTime.now()),
                                               "DEBUT GENERATION FICHIERS DERIVES", libraryId);

                    if (isPdfDelivery) {
                        filesForOcr = managePdfFiles(delivery,
                                                     checkResults,
                                                     documentsToTreat,
                                                     extractedDmdSec,
                                                     libraryId);
                    } else {
                        manageFiles(delivery,
                                    processElement.getSplitNames(),
                                    checkResults,
                                    documentsToTreat,
                                    fileMetadatasForCheck,
                                    extractedOcr,
                                    extractedDmdSec,
                                    libraryId);
                    }
                    LOG.info("Fin de l'ajout des fichiers au système de fichiers");
                    reportService.updateReport(delivery, Optional.of(startTrt), Optional.of(LocalDateTime.now()),
                                               "FIN GENERATION FICHIERS DERIVES", libraryId);
                    deliveryProgressService.deliveryProgress(delivery, null, TYP_MSG_INFO, "DELIVERING", 0, "Fin Traitement Images");
                }

                // TOC/OCR treatment.
                // Ajout des fichiers de metadonnees TOC.
                LOG.info("Ajout des fichiers de table des matieres au système de fichiers");
                reportService.updateReport(delivery,
                                           Optional.empty(),
                                           Optional.of(LocalDateTime.now()),
                                           "DEBUT TRAITEMENT TABLE DES MATIERES / OCR",
                                           libraryId);
                if (isPdfDelivery) {
                    extractTextFromPdf(filesForOcr, delivery, libraryId);
                } else {
                    // Avec génération PDF/OCR.
                    if (isOcrPdfGeneration) {
                        multiPdfs = generateOcrPdf(documentsToTreat, libraryId);

                        // Extraction text ocr des pdfs multi.
                        extractTextFromPdf(multiPdfs, delivery, libraryId);
                    }

                    storeMetaDataFiles(delivery, processElement.getMetadatasDTOForPrefix(), multiPdfs, tocFiles, libraryId, documentsToTreat.keySet());
                }
                reportService.updateReport(delivery,
                                           Optional.empty(),
                                           Optional.of(LocalDateTime.now()),
                                           "FIN TRAITEMENT TABLE DES MATIERES / OCR",
                                           libraryId);
                deliveryProgressService.deliveryProgress(delivery, null, TYP_MSG_INFO, "DELIVERING", 95, "Fin Traitement Table des Matières");
                
                if (!isMasterActive) {
                    // Livraison sans master => il faut qd mm finaliser la livraison des documents non rejetes.
                    documentsToTreat.forEach((prefix, prefixedDoc) -> {
                        final DigitalDocument unitializedDigitalDoc = Iterables.getOnlyElement(prefixedDoc.getDigitalDocuments());
                        final DigitalDocument digitalDoc = digitalDocumentService.findOne(unitializedDigitalDoc.getIdentifier());
                        digitalDoc.setStatus(DigitalDocument.DigitalDocumentStatus.TO_CHECK);
                        finalizeDelivery(digitalDoc, delivery, isPdfDelivery, extractedDmdSec, libraryId);
                    });
                    deliveryProgressService.deliveryProgress(delivery,
                                                             null,
                                                             TYP_MSG_SUCCESS,
                                                             DeliveryStatus.TO_BE_CONTROLLED.toString(),
                                                             100,
                                                             "Livraison acceptée");
                    LOG.info("Livraison sans master : Finalisation livraison");
                }

                /*
                 * Livraison refusee automatiquement ou pas ?
                 */
                if (deliveryRefused) {
                    LOG.info("Livraison rejetée");
                    final Delivery savedDelivery = autoRejectDelivery(delivery, documentsToReject.keySet());
                    // Rejet auto : il faut forcer la creation du bordereau de controle
                    slipService.createCheckSlip(savedDelivery.getIdentifier());
                    uidigitalDocumentService.endAutoChecks(savedDelivery);
                    reportService.updateReport(delivery, Optional.of(dtHrDeliv), Optional.of(LocalDateTime.now()),
                                               "LIVRAISON TERMINEE", libraryId);

                } else {
                    delivery.setStatus(DeliveryStatus.TO_BE_CONTROLLED);
                    if (isMasterActive && !NO_SAMPLING.equals(processElement.getSamplingMode())) {
                        LOG.info("Echantillonnage  [{}]", processElement.getSamplingMode());
                        createSample(delivery, processElement, documentsToTreat);
                        reportService.updateReport(delivery, Optional.empty(), Optional.of(LocalDateTime.now()),
                                                   "ECHANTILLON CREE", libraryId);
                    } else {
                        reportService.updateReport(delivery, Optional.empty(), Optional.of(LocalDateTime.now()),
                                                   "PAS D'ECHANTILLONNAGE", libraryId);
                    }

                    // et on sauvegarde la livraison
                    final Delivery savedDelivery = deliveryService.save(delivery);
                    LOG.info("Livraison acceptée");
                    deliveryProgressService.deliveryProgress(savedDelivery,
                                                             null,
                                                             TYP_MSG_SUCCESS,
                                                             DeliveryStatus.TO_BE_CONTROLLED.toString(),
                                                             100,
                                                             "Livraison acceptée",
                                                             true);
                    reportService.updateReport(savedDelivery, Optional.of(dtHrDeliv), Optional.of(LocalDateTime.now()),
                                               "LIVRAISON TERMINEE", libraryId);
                }

            } catch (final Exception e) {
                // a reprendre plus propre...
                LOG.info("[LIVRAISON] REJET - Exception au cours de la livraison : {}", e.getLocalizedMessage());
                reportService.updateReport(delivery, Optional.of(dtHrDeliv), Optional.of(LocalDateTime.now()),
                                           "LIVRAISON REJETEE - ERREUR: " +e.getLocalizedMessage(), libraryId);
                LOG.error(e.getMessage(), e);
                setDeliveryError(delivery);
            }

            // Moteur de recherche
            esDeliveryService.indexAsync(identifier);

        }), SecurityContextHolder.getContext()));
    }

    /**
     * Recuperation des metadatas via IM ou exifTool pour l'ensemble des fichiers de la livraison.
     */
    private Map<File, Optional<Map<String, String>>> getFileMetadatasForCheck(final Delivery delivery,
                                                                              final Map<String, PrefixedDocuments> documentsForPrefix,
                                                                              final String seqSeparator,
                                                                              final boolean isPdfDelivery,
                                                                              final boolean isRadicalActive,
                                                                              final String expectedFormat) {
        // Pas de transaction => evite les timeOut.
        final List<File> allDelivFiles = getAllMastersForDelivery(delivery,
                                                                  documentsForPrefix,
                                                                  seqSeparator,
                                                                  isPdfDelivery,
                                                                  isRadicalActive,
                                                                  expectedFormat);
        final String format = delivery.getLot().getRequiredFormat();
        return getMetadatas(allDelivFiles, format, delivery);
    }

    /**
     * Recuperation des metadatas via IM ou Exif pour l'ensemble des fichiers masters de la livraison.
     *
     */
    private Map<File, Optional<Map<String, String>>> getMetadatas(final List<File> files, final String format, final Delivery delivery) {

        final int step = files.size() > 500 ? 50 :
                                            files.size() > 200 ? 20 : files.size() > 100 ? 10 : 5;

        final Map<File, Optional<Map<String, String>>> metadatasByFiles = new HashMap<>();

        // JobRunner plutot que parallelStream => on maitrise le nbre de proc utilises
        new JobRunner<File>(files.iterator()).autoSetMaxThreads().setElementName("fichiers").forEach(file -> {
            if (autoCheckService.checkIfNameIsCorrect(file.getName(), format)) {
                 // end switch
                try {
                    metadatasByFiles.put(file, bm.getMetadatas(file));
                } catch (final PgcnTechnicalException e) {
                    LOG.error("Can't collect metadatas of file: {} - ", file.getName(), e);
                }
                final int processed = metadatasByFiles.size();
                if (processed % step == 0) {
                    final int progress = (processed / files.size() * 15) + 5;
                    deliveryProgressService.deliveryProgress(delivery,
                                                             null,
                                                             TYP_MSG_INFO,
                                                             "DELIVERING",
                                                             progress,
                                                             "Collecte Métadonnées : " + processed + " fichiers traités");
                }
                return true;
            }
            return false;
        }).process();
        return metadatasByFiles;
    }

    /**
     * Génération du pdf océrisé
     */
    private Map<String, List<File>> generateOcrPdf(final Map<String, PrefixedDocuments> documentsToTreat, final String libraryId) {

        final Map<String, List<File>> mapPdfs = new HashMap<>();
        final List<Future<?>> futures = new ArrayList<>();

        documentsToTreat.forEach((prefix, prefixedDoc) -> {

            final DigitalDocument unitializedDigitalDoc = Iterables.getOnlyElement(prefixedDoc.getDigitalDocuments());
            final DigitalDocument digitalDoc = digitalDocumentService.getOneWithDocUnitAndPages(unitializedDigitalDoc.getIdentifier());
            final List<String> pagesIds = digitalDoc.getPages().stream().map(AbstractDomainObject::getIdentifier).collect(Collectors.toList());
            final List<StoredFile> storedFiles = binaryRepository.getAllByPageIdentifiersAndFileFormat(pagesIds,
                                                                                                       ViewsFormatConfiguration.FileFormat.ZOOM);

            final List<File> pdfs = new ArrayList<>();
            mapPdfs.put(digitalDoc.getDigitalId(), pdfs);

            final OcrLanguage language = docUnitService.getActiveOcrLanguage(digitalDoc.getDocUnit());
            final String codeLang = language == null ? "fra" : language.getCode();

            // Generation pdf - ocr
            final Path tmpDir = getTemporaryDirectory(prefix, libraryId);
            final File listNamesFile;
            final String destPath;
            try {
                listNamesFile = createTextPathsFile(prefix, tmpDir.toString(), storedFiles, libraryId);
                destPath = listNamesFile.getParent() + "/" + prefix;

                final File imgFile = new File(listNamesFile.getPath());
                if (imgFile.exists() && imgFile.canRead()) {
                    futures.add(tesseractService.buildPdf(imgFile,
                                                          listNamesFile.getParent(),
                                                          prefix,
                                                          destPath,
                                                          codeLang,
                                                          pdfs,
                                                          true,
                                                          libraryId));
                }
            } catch (final IOException | PgcnTechnicalException e) {
                LOG.error("Erreur lors de la génération du PDF par Tesseract", e);
            }
        });

        futures.forEach(f -> {
            try {
                f.get();
            } catch (final InterruptedException | ExecutionException e) {
                LOG.error("Erreur lors de la génération du PDF par Tesseract", e);
            }
        });
        return mapPdfs;
    }

    /**
     * Creation fichier txt contenant la liste des images à OCRiser.
     */
    private File createTextPathsFile(final String prefix, final String tmpDir,
                                     final List<StoredFile> storedFiles, final String libraryId) throws IOException {

        // recopie/renomme les fichiers
        final List<String> filesPath = storedFiles.stream()
                                                  .map(sf -> bm.getFileForStoredFile(sf, libraryId))
                                                  .map(File::getAbsolutePath)
                                                  .collect(Collectors.toList());
        // liste les paths dans un simple fichier texte.
        final File tmpFile = new File(tmpDir, prefix + "_input.txt");
        try (final FileWriter writer = new FileWriter(tmpFile)) {
            for (final String p : filesPath) {
                writer.write(p + System.lineSeparator());
            }
        }
        return tmpFile;
    }

    /**
     * Récupération des fichiers masters
     */
    private List<File> getAllMastersForDelivery(final Delivery delivery,
                                                final Map<String, PrefixedDocuments> documentsForPrefix,
                                                final String seqSeparator,
                                                final boolean isPdfDelivery,
                                                final boolean isRadicalActive,
                                                final String expectedFormat) {

        final List<File> allDeliveryFiles = new ArrayList<>();

        final String deliveryFolder = getFolderPath(delivery);
        if (deliveryFolder == null) {
            return allDeliveryFiles;
        }

        final File[] subDirectories = new File(deliveryFolder).listFiles(File::isDirectory);
        if (subDirectories == null) {
            return allDeliveryFiles;
        }

        for (final File directory : subDirectories) {
            getAllMastersFromDirectory(allDeliveryFiles, directory, documentsForPrefix, seqSeparator, isPdfDelivery, isRadicalActive, expectedFormat);
        }
        return allDeliveryFiles;
    }

    /**
     *
     */
    private void getAllMastersFromDirectory(final List<File> deliveryFiles,
                                            final File directory,
                                            final Map<String, PrefixedDocuments> documentsForPrefix,
                                            final String seqSeparator,
                                            final boolean isPdfDelivery,
                                            final boolean isRadicalActive,
                                            final String expectedFormat) {

        // Vérification du fait que le prefix corresponde au radical d'un des documents associés à cette livraison
        final String prefix = getPrefixForDirectory(directory, documentsForPrefix, seqSeparator);
        /*
         * Préfixe non trouvé : dossier ignoré
         */
        if (prefix == null) {
            return;
        }

        final boolean isJustOneFileEstampe = isJustOneFileEstampe(directory, prefix, expectedFormat);

        if (isRadicalActive) {
            // Filtre sur le préfixe avec separateur de seq - sensible à la casse -
            deliveryFiles.addAll(FileUtils.listFiles(directory,
                                                     getPrefixFilter(prefix, seqSeparator, isPdfDelivery, isJustOneFileEstampe),
                                                     TrueFileFilter.TRUE)
                                          .stream()
                                          .sorted(Comparator.comparing(File::getName))
                                          .collect(Collectors.toList()));
        } else {
            deliveryFiles.addAll(FileUtils.listFiles(directory,
                                                     getFormatFilter(expectedFormat),
                                                     TrueFileFilter.TRUE)
                                          .stream()
                                          .sorted(Comparator.comparing(File::getName))
                                          .collect(Collectors.toList()));
        }

        if (deliveryFiles.isEmpty()) {
            LOG.info("Le répertoire {} pour le préfixe {} ne contient pas de livrables : ignoré ", directory.getName(), prefix);
        }
    }

    /**
     * Controles format des fichiers de métadonnées :
     * - extension
     * - mime type
     * - validation jaxb pour METS
     *
     */
    private List<AutomaticCheckResult> checkFileMetaDatas(final Delivery delivery,
                                                          final DeliveryProcessResults processElement,
                                                          final Map<String, List<MdSecType>> extractedDmdSec) {

        final Map<String, Set<PreDeliveryDocumentFileDTO>> metaDatasDTO = processElement.getMetadatasDTOForPrefix();
        final AutomaticCheckRule tocRule = processElement.getCheckingRules().get(AutoCheckType.METADATA_FILE);
        final AutomaticCheckRule pdfRule = processElement.getCheckingRules().get(AutoCheckType.FILE_PDF_MULTI);

        final String folderPath = getFolderPath(delivery);
        if (folderPath == null) {
            return Collections.emptyList();
        }
        final File[] subDirectories = new File(folderPath).listFiles(File::isDirectory);
        // Recuperation fichiers ad-hoc.
        final Map<String, List<File>> metaDataFiles = metaDatasCheckService.getMetadataFiles(delivery, subDirectories, metaDatasDTO);

        /*
         * Contrôles format
         * On en profite pour extraire du METS les infos pour la notice si lot numerique (DC only)
         */
        final List<AutomaticCheckResult> allResults = new ArrayList<>();
        if ((tocRule != null && tocRule.isActive())
            || (pdfRule != null && pdfRule.isActive())) {
            metaDataFiles.forEach((digitalIdDoc, files) -> {
                allResults.addAll(autoCheckService.checkMetaDataFilesFormat(delivery,
                                                                            digitalIdDoc,
                                                                            metaDatasDTO.get(digitalIdDoc),
                                                                            files,
                                                                            tocRule != null && tocRule.isBlocking(),
                                                                            pdfRule.isBlocking(),
                                                                            extractedDmdSec));
            });
        }
        return allResults;
    }

    /**
     * Recuperation des fichiers metas avant de lancer le process de livraison principal.
     *
     */
    private Map<String, Map<String, List<File>>> prepareTocAndOcrTreatment(final Delivery delivery,
                                                                           final Map<String, Set<PreDeliveryDocumentFileDTO>> metaDatasDTO) {

        // Recuperation des fichiers concernés (mets, excel, pdf multi)
        final String folderPath = getFolderPath(delivery);
        if (folderPath == null) {
            return null;
        }
        final File[] subDirectories = new File(folderPath).listFiles(File::isDirectory);
        // Recuperation fichiers ad-hoc.
        final Map<String, List<File>> metaDataFiles = metaDatasCheckService.getMetadataFiles(delivery, subDirectories, metaDatasDTO);

        // on separe pdfs d'ocr / fichiers de TOC
        final Map<String, List<File>> multiPdfs = new HashMap<>();
        final Map<String, List<File>> tocFiles = new HashMap<>();

        metaDataFiles.forEach((key, value) -> {
            final List<File> pdfFiles = new ArrayList<>();
            final List<File> tocs = new ArrayList<>();
            value.forEach(f -> {
                if (StringUtils.endsWithIgnoreCase(f.getName(), EXTENSION_FORMAT_PDF)) {
                    pdfFiles.add(f);
                } else {
                    tocs.add(f);
                }
            });
            if (CollectionUtils.isNotEmpty(pdfFiles)) {
                multiPdfs.put(key, pdfFiles);
            }
            if (CollectionUtils.isNotEmpty(tocs)) {
                tocFiles.put(key, tocs);
            }
        });

        final Map<String, Map<String, List<File>>> tocOcrFiles = new HashMap<>();
        tocOcrFiles.put("multiPdfs", multiPdfs);
        tocOcrFiles.put("tocFiles", tocFiles);

        return tocOcrFiles;
    }

    /**
     * Enregistrement physique des fichiers.
     */
    private void storeMetaDataFiles(final Delivery delivery,
                                    final Map<String, Set<PreDeliveryDocumentFileDTO>> metaDatasDTO,
                                    final Map<String, List<File>> multiPdfs,
                                    final Map<String, List<File>> tocFiles,
                                    final String libraryId,
                                    final Set<String> prefixToTreat) {

        if (CollectionUtils.isEmpty(tocFiles.keySet())) {
            reportService.updateReport(delivery, Optional.empty(), Optional.empty(),
                                       "Aucun fichier de table des matières trouvé.", libraryId);
        } else {
            // enregistrement fichiers TOC.
            metaDatasCheckService.handleMetaDataFiles(metaDatasDTO, tocFiles, delivery, libraryId, prefixToTreat);
        }

        if (CollectionUtils.isEmpty(multiPdfs.keySet())) {
            reportService.updateReport(delivery, Optional.empty(), Optional.empty(),
                                       "Aucun fichier pdf d'ocr trouvé.", libraryId);
        } else {
            // enregistrement pdf ocr.
            managePdfOcrFile(multiPdfs, delivery, libraryId, prefixToTreat);
        }
    }

    /**
     * Extraction de text du pdf rattaché lors de la pré-livraison.
     */
    private Map<String, Map<Integer, String>> extractOcrText(final Map<String, List<File>> multiPdfs, final int nbFiles) {

        final Map<String, Map<Integer, String>> extractedOcrByPage = new HashMap<>();

        multiPdfs.forEach((key, value) -> {

            if (value.size() > 0) {
                // en vrai, on ne peut avoir qu'un pdf multi par doc.
                final File pdfFile = value.get(0);
                final Map<Integer, String> textByPage = new HashMap<>();

                // passe par un fichier temporaire pour eviter d'exploser la ram.
                try (final PDDocument doc = PDDocument.load(pdfFile, MemoryUsageSetting.setupTempFileOnly())) {
                    final PDFTextStripper stripper = new PDFTextStripper();
                    if (!doc.isEncrypted()) {
                        doc.getPages().forEach(pg -> {

                            if (pg.hasContents()) {
                                final Integer noPage = doc.getPages().indexOf(pg) + 1;
                                stripper.setStartPage(noPage);
                                stripper.setEndPage(noPage);
                                try {
                                    textByPage.put(noPage, stripper.getText(doc));
                                    if (textByPage.size() == nbFiles) {
                                        // pour éviter de traiter un pdf trop grand si pas nécessaire..
                                        return;
                                    }
                                } catch (final IOException e) {
                                    // erreur, on essaie d'avancer
                                    LOG.error("[LIVRAISON - OCR] Erreur lors de l'extraction de texte du PDF : {}", e.getLocalizedMessage());
                                }
                            }
                        });
                        extractedOcrByPage.put(key, textByPage);
                    }
                    // PDDocument : il gere les close proprement à priori
                    doc.close();
                } catch (final IOException e) {
                    LOG.error("[LIVRAISON - OCR] Erreur lors de l'extraction de texte du PDF : {}", e.getLocalizedMessage(), e);
                }
            } else {
                LOG.error("PDF {} non trouvé", key);
            }

        });

        return extractedOcrByPage;
    }

    /**
     * Extrait le texte par page du doc pdf multicouches.
     */
    private void extractTextFromPdf(final Map<String, List<File>> multiPdfs, final Delivery delivery, final String libraryId) {

        final TransactionStatus status = transactionService.startTransaction(false);

            multiPdfs.forEach((key, value) -> {

                final List<DigitalDocument> docs = digitalDocumentService.getAllByDigitalIdAndLotIdentifier(key, delivery.getLot().getIdentifier());
                final DigitalDocument dDoc = Iterables.getOnlyElement(docs);
                final List<String> pageIds = docPageService.getAllPageIdsByDigitalDocumentId(dDoc.getIdentifier());
                final List<StoredFile> masters =
                                               pageIds.isEmpty() ? Collections.emptyList()
                                                                 : binaryRepository.getAllByPageIdentifiersAndFileFormat(pageIds,
                                                                                                                         ViewsFormatConfiguration.FileFormat.MASTER);

                if (value.size() == 1) {

                    final File pdfFile = Iterables.getOnlyElement(value);
                    // passe par un fichier temporaire pour eviter d'exploser la ram.
                    try (final PDDocument doc = PDDocument.load(pdfFile, MemoryUsageSetting.setupTempFileOnly())) {
                        final PDFTextStripper stripper = new PDFTextStripper();
                        if (!doc.isEncrypted()) {
                            doc.getPages().forEach(pg -> {

                                if (pg.hasContents()) {
                                    final int noPage = doc.getPages().indexOf(pg) + 1;
                                    stripper.setStartPage(noPage);
                                    stripper.setEndPage(noPage);
                                    final Optional<StoredFile> sf = masters.stream().filter(f -> f.getPage().getNumber() == noPage).findAny();

                                    if (sf.isPresent()) {
                                        try {
                                            sf.get().setTextOcr(stripper.getText(doc));
                                            binaryRepository.save(sf.get());
                                        } catch (final IOException e) {
                                            // erreur, on essaie d'avancer
                                            LOG.error("[LIVRAISON - OCR] Erreur lors de l'extraction de texte du PDF : {}", e.getLocalizedMessage());
                                        }
                                    }
                                }

                            });
                        } else {
                            reportService.updateReport(delivery,
                                                       Optional.empty(),
                                                       Optional.of(LocalDateTime.now()),
                                                       "LE  PDF " + pdfFile.getName() + " EST CRYPTE - RECUPERATION DE L'OCR IMPOSSIBLE",
                                                       libraryId);
                        }
                        doc.close();
                    } catch (final IOException e) {
                        LOG.error("[LIVRAISON - OCR] Erreur lors de l'extraction de texte du PDF : {}", e.getLocalizedMessage(), e);
                        transactionService.rollbackTransaction(status);
    
                        reportService.updateReport(delivery,
                                                   Optional.empty(),
                                                   Optional.of(LocalDateTime.now()),
                                                   "Erreur lors de l'extraction de texte du PDF :" + pdfFile.getName(),
                                                   libraryId);
                    }
                }

        });
        transactionService.commitTransaction(status);
    }

    /**
     * Création de l'échantillon
     */
    private void createSample(final Delivery deliv,
                              final DeliveryProcessResults processElements,
                              final Map<String, PrefixedDocuments> documentsToTreat) {

        transactionService.executeInNewTransaction(() -> {

            final Delivery delivery = deliveryService.findOneWithDep(deliv.getIdentifier());
            final Sample sample = new Sample();
            sample.setDelivery(delivery);
            sample.setSamplingMode(processElements.getSamplingMode());

            final List<DocPage> pages = new ArrayList<>();
            switch (processElements.getSamplingMode()) {
                case SAMPLING_DOC_DELIV:
                    // preleve des docs
                    final List<?> samples = randomCollect(new ArrayList<>(documentsToTreat.keySet()), processElements.getSamplingRate());
                    documentsToTreat.entrySet()
                                    .stream()
                                    .filter(entry -> (samples.contains(entry.getKey())))
                                    .forEach((entry) -> {
                                        final DigitalDocument unitializedDigitalDoc = Iterables.getOnlyElement(entry.getValue().getDigitalDocuments());
                                        final DigitalDocument digitalDoc = digitalDocumentService.findOne(unitializedDigitalDoc.getIdentifier());
                                        final List<DocPage> filtered = digitalDoc.getPages()
                                                                                 .stream()
                                                                                 .filter(p -> p.getNumber() != null)  // evite la page du master pdf
                                                                                 .collect(Collectors.toList());
                                        pages.addAll(filtered);
                                    });
                    break;
                case SAMPLING_PAGE_ALL_DOC:
                    final List<DocPage> totalPages = new ArrayList<>();
                    // preleve x% sur l'ens. des pages
                    documentsToTreat.forEach((prefix, prefixedDoc) -> {
                        final DigitalDocument unitializedDigitalDoc = Iterables.getOnlyElement(prefixedDoc.getDigitalDocuments());
                        final DigitalDocument digitalDoc = digitalDocumentService.findOne(unitializedDigitalDoc.getIdentifier());
                        totalPages.addAll(digitalDoc.getPages());
                    });
                    randomCollect(totalPages, processElements.getSamplingRate())
                                                                                .stream()
                                                                                .filter(docPage -> ((DocPage) docPage).getNumber() != null) // evite la
                                                                                                                                            // page du
                                                                                                                                            // master pdf
                                                                                .forEach(docPage -> pages.add((DocPage) docPage));
                    break;
                case SAMPLING_PAGE_ONE_DOC:
                    // preleve x% de chaque doc
                    documentsToTreat.forEach((prefix, prefixedDoc) -> {
                        final DigitalDocument unitializedDigitalDoc = Iterables.getOnlyElement(prefixedDoc.getDigitalDocuments());
                        final DigitalDocument digitalDoc = digitalDocumentService.findOne(unitializedDigitalDoc.getIdentifier());

                        randomCollect(new ArrayList<>(digitalDoc.getPages()), processElements.getSamplingRate())
                                                                                                                .stream()
                                                                                                                .filter(docPage -> ((DocPage) docPage).getNumber() != null)
                                                                                                                .forEach(docPage -> pages.add((DocPage) docPage));
                    });
                    break;
                case NO_SAMPLING:
                    // nothing...
                default:
                    break;
            }

            sample.getPages().addAll(pages);
            pages.forEach(p -> p.setSample(sample));
            sampleService.save(sample);
        });
    }

    /**
     * Effectue une sélection aléatoire dans la liste en entrée.
     */
    private List<?> randomCollect(final List<?> srcList, final Double rate) {

        final int total = srcList.size();
        if (total < SAMPLING_MIN_SIZE_LIST) {
            // definir une bonne valeur pour ce min size ?...
            return srcList;
        }
        final long sizeLimit = Double.valueOf(total * rate).longValue();
        final List<?> targetList =
                                 srcList.stream()
                                        .collect(Collectors.collectingAndThen(Collectors.toList(), collected -> {
                                            Collections.shuffle(collected);
                                            return collected.stream();
                                        }))
                                        .limit(sizeLimit < 1 ? 1 : sizeLimit)
                                        .collect(Collectors.toList());
        return targetList;
    }

    /**
     * Traitement spécifique des fichiers PDF.
     *
     * - extraction des pages du pdf => *.jpg
     * - enregistrement du master pdf
     * - generation des images derivees depuis les images jpg extraites.
     */
    private Map<String, List<File>> managePdfFiles(final Delivery delivery,
                                                   final List<AutomaticCheckResult> results,
                                                   final Map<String, PrefixedDocuments> documentsForPrefix,
                                                   final Map<String, List<MdSecType>> extractedDmdSec,
                                                   final String libraryId) throws PgcnTechnicalException {

        final List<Path> directoriesToTreat = new ArrayList<>();
        final Map<String, List<File>> filesForOcr = new HashMap<>();

        documentsForPrefix.forEach((prefix, prefixedDoc) -> {

            transactionService.executeInNewTransaction(() -> {

                final File srcFile = Iterables.getOnlyElement(prefixedDoc.getFiles());
                final Path tmpDir = getTemporaryDirectory(prefix, libraryId);
                final String destFile = tmpDir.toString()
                                              .concat(System.getProperty("file.separator"))
                                              .concat(prefix)
                                              .concat("_%03d.jpg");

                directoriesToTreat.add(tmpDir);

                // decoupage du pdf en images JPG
                final List<File> files = imService.extractImgFromPdf(srcFile, destFile);
                final DigitalDocument unitializedDigitalDoc = Iterables.getOnlyElement(prefixedDoc.getDigitalDocuments());
                final DigitalDocument digitalDoc = digitalDocumentService.findOne(unitializedDigitalDoc.getIdentifier());
                deliveryProgressService.deliveryProgress(delivery,
                                                         digitalDoc.getDigitalId(),
                                                         TYP_MSG_INFO,
                                                         "DELIVERING",
                                                         30,
                                                         "Decoupe du PDF images JPG.");
                for (final DocPage page : digitalDoc.getPages()) {
                    try {
                        docPageService.delete(page, libraryId);
                    } catch (final PgcnTechnicalException e) {
                        LOG.error("Suppression impossible des documents liés à la page", e);
                    }
                }
                digitalDoc.setPages(new HashSet<>());
                digitalDoc.setTotalLength(srcFile.length());

                final List<File> pdfs = new ArrayList<>();
                pdfs.add(srcFile);
                filesForOcr.put(digitalDoc.getDigitalId(), pdfs);

                final Map<Integer, File> filesForDoc = new LinkedHashMap<>();
                int cpt = 0;
                for (final File f : files) {
                    filesForDoc.put(cpt, f);
                    cpt++;
                }

                // Creation des pages logiques
                prepareCreatePagesPdf(srcFile, filesForDoc, digitalDoc, libraryId);

                // Mapping digitalDoc / physicalDoc
                final Set<PhysicalDocument> physicalDocumentsForLot =
                                                                    physicalDocumentRepository.getAllByDigitalIdAndLotIdentifier(prefix,
                                                                                                                                 delivery.getLot()
                                                                                                                                         .getIdentifier());
                for (final PhysicalDocument physicalDoc : physicalDocumentsForLot) {
                    // Mapping result / digital Doc
                    physicalDoc.addDigitalDocument(digitalDoc);
                    digitalDoc.setDocUnit(physicalDoc.getDocUnit());
                    for (final AutomaticCheckResult result : results) {
                        if (physicalDoc.getIdentifier().equals(result.getPhysicalDocument().getIdentifier())) {
                            result.setDigitalDocument(digitalDoc);
                            result.setDocUnit(physicalDoc.getDocUnit());
                        }
                    }
                }
                finalizeDelivery(digitalDoc, delivery, true, extractedDmdSec, libraryId);
            });
        });

        // Traitement images et ajout au système de fichiers
        final Set<DeliveredDocument> documents = deliveryService.getDigitalDocumentsByDelivery(delivery.getIdentifier());
        documentsForPrefix.forEach((prefix, prefixedDoc) -> {
            final DigitalDocument unitialized = Iterables.getOnlyElement(prefixedDoc.getDigitalDocuments());
            final Optional<DeliveredDocument> delivDoc = documents.stream()
                                                                  .filter(doc -> StringUtils.equals(unitialized.getIdentifier(),
                                                                                                    doc.getDigitalDocument().getIdentifier()))
                                                                  .findFirst();
            final Map<Integer, File> filesForDoc = new HashMap<>();
            if (delivDoc.isPresent()) {
                final DigitalDocument digitalDoc = delivDoc.get().getDigitalDocument();
                directoriesToTreat.stream()
                                  .filter(p -> StringUtils.containsIgnoreCase(p.getFileName().toString(), prefix))
                                  .forEach(p -> {

                                      List<File> files = new ArrayList<>();
                                      try {
                                          files = Files.list(p)
                                                       .filter(Files::isRegularFile)
                                                       .filter(f -> f.toString().endsWith(".jpg"))
                                                       .map(Path::toFile)
                                                       .collect(Collectors.toList());
                                      } catch (final IOException e) {
                                          LOG.error("[LIVRAISON PDF] Erreur lors de la récupération des fichiers extraits du pdf", e);
                                      }
                                      sortWithSequence(files);
                                      int i = 1;
                                      for (final File f : files) {
                                          filesForDoc.put(i, f);
                                          i++;
                                      }
                                      if (filesForDoc.size() > 0) {
                                          final double progress = 65 / (filesForDoc.size());
                                          createPagesFromDeliveryPdfFiles(filesForDoc, digitalDoc, Double.valueOf(progress).intValue(), delivery, libraryId);
                                      }
                                  });

                // MAJ => statut du DeliveredDoc sur l'ecran de livraison.
                changeDocumentStatus(digitalDoc, delivery);
            }
        });
        // Text Extraction
        return filesForOcr;
    }

    /**
     * Enregistrement des pdfs multi pour OCR
     */
    private void managePdfOcrFile(final Map<String, List<File>> multiPdfs, final Delivery delivery, final String libraryId, final Set<String> prefixToTreat) {

        LOG.info("Enregistrement PDF OCR");
        multiPdfs.forEach((key, value) -> {

            if (prefixToTreat.contains(key)) {

                transactionService.executeInNewTransaction(() -> {

                    if (value.size() > 0) {
                        final File srcFile = value.get(0);
                        final List<DigitalDocument> docs =
                                                         digitalDocumentService.getAllByDigitalIdAndLotIdentifier(key,
                                                                                                                  delivery.getLot().getIdentifier());
                        // on evite de traiter des pdf de prefixes exclus de la livraison
                        if (CollectionUtils.isNotEmpty(docs)) {
                            final DigitalDocument digitalDoc = Iterables.getOnlyElement(docs);

                            deliveryProgressService.deliveryProgress(delivery,
                                                                     digitalDoc.getDigitalId(),
                                                                     TYP_MSG_INFO,
                                                                     "DELIVERING",
                                                                     97,
                                                                     "Traitement du PDF pour ocr.");

                            // le pdf - persisté comme page de number null - pas de derived
                            final DocPage masterPage = new DocPage();
                            digitalDoc.addPage(masterPage);
                            digitalDoc.addLength(srcFile.length());

                            bm.createFromFileForPage(masterPage,
                                                     srcFile,
                                                     StoredFile.StoredFileType.MASTER,
                                                     ViewsFormatConfiguration.FileFormat.MASTER,
                                                     Optional.empty(),
                                                     libraryId);
                            reportService.updateReport(delivery,
                                                       Optional.empty(),
                                                       Optional.empty(),
                                                       srcFile.getName().concat(" enregistré"),
                                                       libraryId);
                        }
                    } else {
                        LOG.error("PDF {} non trouvé", prefixToTreat);
                    }
                });
           }

        });
    }

    /**
     * Creation d'un repertoire temp pour le prefixe donné.
     */
    private Path getTemporaryDirectory(final String prefix, final String libraryId) {
        Path tmpDir = null;
        try {
            tmpDir = Files.createTempDirectory(Paths.get(bm.getTmpDir(libraryId).getAbsolutePath()), prefix);
            tmpDir.toFile().setWritable(true);
        } catch (final IOException e) {
            LOG.error("[Livraison] Impossible de creer un repertoire temporaire " + e);
        }
        if (tmpDir == null) {
            tmpDir = Paths.get(bm.getTmpDir(libraryId).getAbsolutePath());
        }
        return tmpDir;
    }

    /**
     * Ajoute les fichiers au système de fichiers (BM)
     */
    private void manageFiles(final Delivery delivery,
                             final Map<String, Optional<SplitFilename>> splitNames,
                             final List<AutomaticCheckResult> results,
                             final Map<String, PrefixedDocuments> documentsForPrefix,
                             final Map<File, Optional<Map<String, String>>> fileMetadatas,
                             final Map<String, Map<Integer, String>> extractedOcr,
                             final Map<String, List<MdSecType>> extractedDmdSec,
                             final String libraryId) {

        documentsForPrefix.forEach((prefix, prefixedDoc) -> {

            transactionService.executeInNewTransaction(() -> {

                final List<File> filesForDigitalDoc = prefixedDoc.getFiles();
                final DigitalDocument unitializedDigitalDoc = Iterables.getOnlyElement(prefixedDoc.getDigitalDocuments());
                final DigitalDocument digitalDoc = digitalDocumentService.findOne(unitializedDigitalDoc.getIdentifier());

                for (final DocPage page : digitalDoc.getPages()) {
                    try {
                        docPageService.delete(page, libraryId);
                    } catch (final PgcnTechnicalException e) {
                        LOG.warn("Suppression impossible des documents liés à la page", e);
                    }
                }
                digitalDoc.setPages(new HashSet<>());
                digitalDoc.setTotalLength(0L);

                // Creation logique des pages en DB
                prepareCreatePages(filesForDigitalDoc, digitalDoc, splitNames);

                // Mapping digitalDoc / physicalDoc
                final Set<PhysicalDocument> physicalDocumentsForLot =
                                                                    physicalDocumentRepository.getAllByDigitalIdAndLotIdentifier(prefix,
                                                                                                                                 delivery.getLot()
                                                                                                                                         .getIdentifier());
                for (final PhysicalDocument physicalDoc : physicalDocumentsForLot) {
                    // Mapping result / digital Doc
                    physicalDoc.addDigitalDocument(digitalDoc);
                    digitalDoc.setDocUnit(physicalDoc.getDocUnit());
                    for (final AutomaticCheckResult result : results) {
                        if (physicalDoc.getIdentifier().equals(result.getPhysicalDocument().getIdentifier())) {
                            result.setDigitalDocument(digitalDoc);
                            result.setDocUnit(physicalDoc.getDocUnit());
                        }
                    }
                }

                finalizeDelivery(digitalDoc, delivery, false, extractedDmdSec, libraryId);
            });
        });

        // Traitement images et ajout au système de fichiers
        final Set<DeliveredDocument> documents = deliveryService.getDigitalDocumentsByDelivery(delivery.getIdentifier());
        documentsForPrefix.forEach((prefix, prefixedDoc) -> {
            final List<File> filesForDigitalDoc = prefixedDoc.getFiles();
            final DigitalDocument unitialized = Iterables.getOnlyElement(prefixedDoc.getDigitalDocuments());

            final Optional<Map<Integer, String>> ocrByPage = extractedOcr != null
                                                             && extractedOcr.get(prefix) != null ? Optional.of(extractedOcr.get(prefix))
                                                                                                 : Optional.empty();

            final Optional<DeliveredDocument> delivDoc = documents.stream()
                                                                  .filter(doc -> StringUtils.equals(unitialized.getIdentifier(),
                                                                                                    doc.getDigitalDocument().getIdentifier()))
                                                                  .findFirst();
            if (delivDoc.isPresent()) {
                final DigitalDocument digitalDoc = delivDoc.get().getDigitalDocument();
                final String startMsg = "Début Traitement Document " + digitalDoc.getDigitalId()
                                        + " ("
                                        + digitalDoc.getNbPages()
                                        + " pages)";
                deliveryProgressService.deliveryProgress(delivery, digitalDoc.getDigitalId(), TYP_MSG_INFO, "DELIVERING", 30, startMsg);
                createPagesFromDeliveryFiles(filesForDigitalDoc, digitalDoc, splitNames, fileMetadatas, delivery, ocrByPage, libraryId);

                // MAJ => statut du DeliveredDoc sur l'ecran de livraison.
                changeDocumentStatus(digitalDoc, delivery);
            }

        });
    }

    /**
     * Changement du statut des document à A contrôler
     */
    private void changeDocumentStatus(final DigitalDocument digitalDoc, final Delivery delivery) {

        transactionService.executeInNewTransaction(() -> {
            final DigitalDocument doc = digitalDocumentService.findOne(digitalDoc.getIdentifier());
            if(digitalDoc.getPageNumber() > 0){
                doc.setStatus(DigitalDocument.DigitalDocumentStatus.TO_CHECK);
            } else {
                doc.setStatus(DigitalDocument.DigitalDocumentStatus.DELIVERING_ERROR);
            }
            final DeliveredDocument deliveredDoc = digitalDocumentService.getDeliveredDocument(doc, delivery);
            if(digitalDoc.getPageNumber() > 0){
                deliveredDoc.setStatus(DigitalDocument.DigitalDocumentStatus.TO_CHECK);
            } else {
                deliveredDoc.setStatus(DigitalDocument.DigitalDocumentStatus.DELIVERING_ERROR);
            }
            digitalDocumentService.save(doc);
        });
    }

    /**
     * Creation notices depuis dmdsec DC extrait du mets (si Lot numérique avec 1 mets).
     */
    private BibliographicRecord createBibliographicRecord(final DigitalDocument digitalDoc, final Map<String, List<MdSecType>> extractedDmdSec) {

        final Optional<String> key = extractedDmdSec.keySet()
                                                    .stream()
                                                    .filter(k -> k.contains(digitalDoc.getDigitalId()))
                                                    .findFirst();

        final BibliographicRecord bibRecord;
        // Chargement des types de propriété
        final Map<String, DocPropertyType> propertyTypes =
                                                         docPropertyTypeService.findAll()
                                                                               .stream()
                                                                               .collect(Collectors.toMap(DocPropertyType::getIdentifier,
                                                                                                         Function.identity()));

        if (key.isPresent()) {

            final List<MdSecType> dmdSecs = extractedDmdSec.get(key.get());

            final DocUnit docUnit = digitalDoc.getDocUnit();
            bibRecord = new BibliographicRecord();
            bibRecord.setLibrary(docUnit.getLibrary());
            bibRecord.setDocUnit(docUnit);
            bibRecord.setDocUnitId(docUnit.getIdentifier());

            dmdSecs.stream()
                   .filter(mdsType -> StringUtils.equals(mdsType.getMdWrap().getMDTYPE(), "DC"))
                   .forEach(mdsType -> mdsType.getMdWrap().getXmlData().getAny().forEach(e -> {

                       final DocProperty dcProp = new DocProperty();

                       final String propName = ((JAXBElement) e).getName().getLocalPart();
                       final DocPropertyType propertyType = propertyTypes.get(propName);
                       dcProp.setType(propertyType);

                       int rank = (int) bibRecord.getProperties()
                                                 .stream()
                                                 .filter(p -> StringUtils.equals(p.getType().getIdentifier(), propertyType.getIdentifier()))
                                                 .count();

                       dcProp.setRecord(bibRecord);
                       final StringBuilder value = new StringBuilder();

                       final SimpleLiteral vals = (SimpleLiteral) ((JAXBElement) e).getValue();
                       vals.getContent().forEach(value::append);
                       if (StringUtils.equalsIgnoreCase("title", propName)) {
                           bibRecord.setTitle(bibRecord.getTitle() == null ? value.toString()
                                                                           : bibRecord.getTitle()
                                                                                      .concat(" ")
                                                                                      .concat(value.toString()));
                       }
                       dcProp.setValue(value.toString());
                       dcProp.setRank(rank++);
                       bibRecord.addProperty(dcProp);

                   }));

        } else {
            bibRecord = null;
        }

        return bibRecord;
    }

    /**
     * Finalisation de la livraison.
     */
    private void finalizeDelivery(final DigitalDocument digitalDoc,
                                  final Delivery delivery,
                                  final boolean isPdfDelivery,
                                  final Map<String, List<MdSecType>> extractedDmdSec,
                                  final String libraryId) {

        digitalDoc.setTotalDelivery(digitalDoc.getTotalDelivery() + 1);
        digitalDoc.setDeliveryDate(LocalDate.now());

        final DeliveredDocument deliveredDocument = digitalDocumentService.getDeliveredDocument(digitalDoc, delivery);
        deliveredDocument.setDeliveryDate(digitalDoc.getDeliveryDate());
        deliveredDocument.setNbPages(digitalDoc.getNbPages());
        deliveredDocument.setTotalLength(digitalDoc.getTotalLength());

        if (Lot.Type.DIGITAL.equals(delivery.getLot().getType())
            && CollectionUtils.isNotEmpty(extractedDmdSec.keySet())) {
            // Creation de notice depuis donnees du Mets.
            final DocUnit docUnit = digitalDoc.getDocUnit();
            final BibliographicRecord bibRecord = createBibliographicRecord(digitalDoc, extractedDmdSec);
            if (bibRecord != null) {
                docUnit.addRecord(bibRecord);
                bibliographicRecordService.save(bibRecord);
            }

        }

        reportService.updateReport(delivery,
                                   Optional.empty(),
                                   Optional.empty(),
                                   "+++ DOCUMENT ".concat(digitalDoc.getDocUnit().getPgcnId()).concat(" +++"),
                                   libraryId);
        reportService.updateReport(delivery,
                                   Optional.empty(),
                                   Optional.empty(),
                                   " => ".concat(String.valueOf(digitalDoc.getNbPages())).concat(" fichiers traités."),
                                   libraryId);

        digitalDocumentService.save(digitalDoc);
        deliveryProgressService.deliveryProgress(delivery,
                                                 digitalDoc.getDigitalId(),
                                                 TYP_MSG_INFO,
                                                 "DELIVERING",
                                                 0,
                                                 "Document validé: " + digitalDoc.getDigitalId(),
                                                 true);
    }

    /**
     * Finalisation livraison en cas de rejet.
     *
     * @param deliv
     */
    private Delivery autoRejectDelivery(final Delivery deliv, final Set<String> rejectedDocs) {

        return transactionService.executeInNewTransactionWithReturn(() -> {

            // il faut recharger
            final Delivery delivery = deliveryService.findOneWithDep(deliv.getIdentifier());
            // et mettre à jour les flags de deliv => delivery
            updateCheckFlags(deliv, delivery);

            final Set<DeliveredDocument> documents = delivery.getDocuments();
            if (documents.size() == rejectedDocs.size()) {
                // tous les docs sont rejetes
                delivery.setStatus(DeliveryStatus.AUTOMATICALLY_REJECTED);
            } else {
                // au moins un doc à controler..
                delivery.setStatus(DeliveryStatus.TO_BE_CONTROLLED);
            }

            documents.stream()
                     .filter(doc -> rejectedDocs.contains(doc.getDigitalDocument().getDigitalId()))
                     .forEach(doc -> {
                         final DigitalDocument dd = doc.getDigitalDocument();
                         dd.setTotalDelivery(dd.getTotalDelivery() + 1);
                         dd.setDeliveryDate(LocalDate.now());
                         dd.setStatus(DigitalDocument.DigitalDocumentStatus.REJECTED);
                         doc.setDeliveryDate(dd.getDeliveryDate());
                         doc.setStatus(dd.getStatus());
                         doc.setNbPages(dd.getNbPages());
                         doc.setTotalLength(dd.getTotalLength());
                         digitalDocumentService.save(dd);
                         // # 3644 Lot numerique : on peut avoir 1 workflow si relivraison !
                         if (workflowService.isWorkflowRunning(dd.getDocUnit().getIdentifier())) {
                             workflowService.rejectAutomaticState(
                                                                  digitalDocumentService.findDocUnitByIdentifier(dd.getIdentifier()).getIdentifier(),
                                                                  WorkflowStateKey.CONTROLES_AUTOMATIQUES_EN_COURS);
                         }
                         deliveryProgressService.deliveryProgress(delivery,
                                                                  dd.getDigitalId(),
                                                                  TYP_MSG_WARN,
                                                                  DeliveryStatus.AUTOMATICALLY_REJECTED.toString(),
                                                                  30,
                                                                  "Rejet du document " + dd.getDigitalId(),
                                                                  true);
                     });

            return deliveryService.save(delivery);
        });
    }


    private void updateCheckFlags(final Delivery src, final Delivery target) {
        target.setAltoPresent(src.isAltoPresent());
        target.setColorspaceOK(src.isColorspaceOK());
        target.setCompressionRateOK(src.isCompressionRateOK());
        target.setCompressionTypeOK(src.isCompressionTypeOK());
        target.setFileBibPrefixOK(src.isFileBibPrefixOK());
        target.setFileCaseOK(src.isFileCaseOK());
        target.setFileFormatOK(src.isFileFormatOK());
        target.setFileIntegrityOk(src.isFileIntegrityOk());
        target.setMireOK(src.isMireOK());
        target.setMirePresent(src.isMirePresent());
        target.setNumberOfFilesMatching(src.isNumberOfFilesMatching());
        target.setNumberOfFilesOK(src.isNumberOfFilesOK());
        target.setPdfMultiOK(src.isPdfMultiOK());
        target.setPdfMultiPresent(src.isPdfMultiPresent());
        target.setResolutionOK(src.isResolutionOK());
        target.setSequentialNumbers(src.isSequentialNumbers());
        target.setTableOfContentsOK(src.isTableOfContentsOK());
        target.setTableOfContentsPresent(src.isTableOfContentsPresent());
        target.setFileRadicalOK(src.isFileRadicalOK());
    }

    /**
     * Finalisation en cas d'erreur inattendue.
     */
    private void setDeliveryError(final Delivery deliv) {

        transactionService.executeInNewTransaction(() -> {

            // il faut recharger
            final Delivery delivery = deliveryService.findOneWithDep(deliv.getIdentifier());
            final Set<DeliveredDocument> documents = delivery.getDocuments();
            delivery.setStatus(DeliveryStatus.DELIVERING_ERROR);

            // On passe en erreur les docs non encore passés
            documents.stream()
                     .filter(doc -> DigitalDocument.DigitalDocumentStatus.DELIVERING == doc.getStatus()
                                    || DigitalDocument.DigitalDocumentStatus.CREATING == doc.getStatus())
                     .forEach(doc -> {
                         final DigitalDocument dd = doc.getDigitalDocument();
                         dd.setTotalDelivery(dd.getTotalDelivery() + 1);
                         dd.setDeliveryDate(LocalDate.now());
                         dd.setStatus(DigitalDocument.DigitalDocumentStatus.DELIVERING_ERROR);
                         doc.setDeliveryDate(dd.getDeliveryDate());
                         doc.setStatus(dd.getStatus());
                         doc.setNbPages(dd.getNbPages());
                         doc.setTotalLength(dd.getTotalLength());
                         digitalDocumentService.save(dd);
                     });

            deliveryService.save(delivery);
        });
    }

    /**
     * Creation logique des pages du document pdf.
     */
    private void prepareCreatePagesPdf(final File pdfFile,
                                       final Map<Integer, File> filesForDigitalDoc,
                                       final DigitalDocument digitalDoc,
                                       final String libraryId) {
        // le master dans un 1er temps (le pdf - persisté comme page de number null - pas de derived)
        final DocPage masterPage = new DocPage();
        digitalDoc.addPage(masterPage);
        digitalDoc.addLength(pdfFile.length());

        // on peut enregistrer et stocker le master original
        bm.createFromFileForPage(masterPage,
                                 pdfFile,
                                 StoredFile.StoredFileType.MASTER,
                                 ViewsFormatConfiguration.FileFormat.MASTER,
                                 Optional.empty(),
                                 libraryId);

        // Puis on cree les pages pour les images à extraire du pdf..
        filesForDigitalDoc.forEach((key, file) -> {
            final DocPage page = new DocPage();
            page.setNumber(key + 1);
            digitalDoc.addPage(page);
            docPageService.save(page);
        });

        LOG.debug("PDF_DEBUG - Nbre Pages ajoutees au doc digital = {}", digitalDoc.getPages().size());
    }

    /**
     * Creation logique des pages du document.
     */
    private void prepareCreatePages(final List<File> filesForDigitalDoc,
                                    final DigitalDocument digitalDoc,
                                    final Map<String, Optional<SplitFilename>> splitNames) {

        final AtomicInteger i = new AtomicInteger();
        filesForDigitalDoc.forEach(file -> {
            if (splitNames.get(file.getName()).isPresent()) {
                final SplitFilename splitName = splitNames.get(file.getName()).get();
                final int nbPieces = SplitFilename.getPiecesFromDirectory(splitNames, splitName.getDirectory()).size();
                DocPage page = new DocPage();

                if (nbPieces > 1) {
                    page.setNumber(i.get());
                    page.setPieceNumber(splitName.getNumber());
                    page.setPiece(splitName.getPiece());
                } else {
                    page.setNumber(splitName.getNumber());
                    page.setPieceNumber(splitName.getNumber());
                    page.setPiece(splitName.getPiece());
                }
                page = docPageService.save(page);
                digitalDoc.addPage(page);
                digitalDoc.addLength(file.length());

                i.getAndIncrement();

            } else {
                LOG.warn("Fichier à ajouter mais non découpé : {}", file.getName());
            }
        });
    }

    /**
     * Création des pages pour un dossier.
     * Invoquee après la finalisation de la livraison (en dehors de la transaction globale)
     * pour pouvoir paralléliser les traitements.
     */
    private void createPagesFromDeliveryFiles(final List<File> filesForDigitalDoc,
                                              final DigitalDocument digitalDoc,
                                              final Map<String, Optional<SplitFilename>> splitNames,
                                              final Map<File, Optional<Map<String, String>>> fileMetadatas,
                                              final Delivery delivery,
                                              final Optional<Map<Integer, String>> ocrByPage,
                                              final String libraryId) {

        // Regroupement des page par piece
        final Map<String, List<DocPage>> docPieces = digitalDoc.getPages().stream().collect(Collectors.groupingBy(DocPage::getPiece));
        final int stepProgress =
                               filesForDigitalDoc.size() > 500 ? 50 : filesForDigitalDoc.size() > 200 ? 20 : filesForDigitalDoc.size() > 50 ? 10 : 5;
        final double pgProgress = 65 / (filesForDigitalDoc.size() + 1);
        final AtomicInteger globalCpt = new AtomicInteger(0);


       // JobRunner plutot que parallelStream => on maitrise le nbre de proc utilises
       new JobRunner<File>(filesForDigitalDoc.iterator()).autoSetMaxThreads().setElementName("fichiers").forEach(file -> {

           if (splitNames.get(file.getName()).isPresent()) {
               final SplitFilename splitName = splitNames.get(file.getName()).get();
                // Liste des pages associées à une pièce
                final List<DocPage> pagesPieces = docPieces.get(splitName.getPiece());
                final Map<Integer, List<DocPage>> docPages = pagesPieces.stream().collect(Collectors.groupingBy(DocPage::getPieceNumber));
               final List<DocPage> pages = docPages.get(splitName.getNumber());

               if (pages.size() == 1) {
                   StoredFile master;
                   try {
                       master = bm.createFromFileForPage(pages.get(0),
                                                         file,
                                                         StoredFile.StoredFileType.MASTER,
                                                         ViewsFormatConfiguration.FileFormat.MASTER,
                                                         ocrByPage,
                                                         fileMetadatas,
                                                         libraryId);
                   } catch (final PgcnException e) {
                       master = null;
                       LOG.error("Erreur lors de l'enregistrement du master {} : {}", file.getName(), e.getLocalizedMessage());
                       deliveryProgressService.deliveryProgress(delivery,
                                                                digitalDoc.getDigitalId(),
                                                                TYP_MSG_WARN,
                                                                "DELIVERING",
                                                                0,
                                                                digitalDoc.getDigitalId() + " - "
                                                                   + file.getName()
                                                                   + " : Erreur lors de l'enregistrement du master");
                   }

                   final int cpt = globalCpt.incrementAndGet();

                   for (final ViewsFormatConfiguration.FileFormat sff : ViewsFormatConfiguration.FileFormat.values()) {
                       if (!ViewsFormatConfiguration.FileFormat.MASTER.equals(sff)) {
                           double progression = 0.0d;
                           if (cpt % stepProgress == 0
                               && ViewsFormatConfiguration.FileFormat.ZOOM.equals(sff)) {
                               progression = (pgProgress * cpt) + 30d;
                           }

                           try {
                               bm.generateDerivedThumbnailForPage(pages.get(0), master, sff, fileMetadatas, progression, delivery, libraryId);
                           } catch (final PgcnException e) {
                               LOG.error("Erreur lors de la generation des derivées {} : {}",
                                         file.getName(),
                                         e.getLocalizedMessage());
                               deliveryProgressService.deliveryProgress(delivery,
                                                                        digitalDoc.getDigitalId(),
                                                                        TYP_MSG_WARN,
                                                                        "DELIVERING",
                                                                        0,
                                                                        digitalDoc.getDigitalId() + " - "
                                                                           + file.getName()
                                                                           + " : Erreur lors de la génération des images derivées");
                           }
                       }
                   }
               }
               return true;
           }
           return false;
       }).process();

    }

    /**
     * Création des pages pour un document pdf.
     */
    private void createPagesFromDeliveryPdfFiles(final Map<Integer, File> filesForDigitalDoc,
                                                 final DigitalDocument digitalDoc,
                                                 final int progress,
                                                 final Delivery delivery,
                                                 final String libraryId) {

        // Rechercher les metadatas
        final Map<File, Optional<Map<String, String>>> metadatas = getMetadatas(new ArrayList<>(filesForDigitalDoc.values()), "JPG", null);
        final Set<DocPage> docPages = digitalDoc.getPages();
        final Set<DocPage> treated = new HashSet<>();

        // Puis on derive les images extraites du pdf..
        // JobRunner plutot que parallelStream => on maitrise le nbre de proc utilises
        new JobRunner<Entry<Integer, File>>(filesForDigitalDoc.entrySet().iterator())
                                            .autoSetMaxThreads().setElementName("fichiers").forEach(entry -> {

            final Optional<DocPage> page = docPages.stream()
                    .filter(pg -> pg.getNumber() != null
                                  && entry.getKey().intValue() == pg.getNumber().intValue())
                    .findFirst();
            // on doit qd mm enregistrer les masters de chaque image.
            if (page.isPresent()) {
                final StoredFile master = bm.createFromFileForPage(page.get(),
                                                entry.getValue(),
                                                StoredFile.StoredFileType.MASTER,
                                                ViewsFormatConfiguration.FileFormat.MASTER,
                                                Optional.empty(),
                                                libraryId);
                for (final ViewsFormatConfiguration.FileFormat sff : ViewsFormatConfiguration.FileFormat.values()) {
                    if (!ViewsFormatConfiguration.FileFormat.MASTER.equals(sff)) {
                        double progression = 0.0;
                        if (ViewsFormatConfiguration.FileFormat.ZOOM.equals(sff)) {
                        progression = (progress * treated.size()) + 30;
                        treated.add(page.get());
                        LOG.debug("progression page {} : " + progression, page.get().getNumber());
                        }
                        bm.generateDerivedThumbnailForPage(page.get(), master, sff, metadatas, progression, delivery, libraryId);
                        LOG.debug("$$PDF_DEBUG - Derivation de la page {}", page.get().getNumber());
                    }
                }
                return true;
            }
            return false;
        }).process();

    }

    /**
     * Renseigne tous les indicateurs de la livraison.
     */
    private void toggleAllDeliveryFlags(final Delivery delivery, final boolean state) {
        delivery.setFileFormatOK(state);
        delivery.setSequentialNumbers(state);
        delivery.setNumberOfFilesOK(state);
        delivery.setCompressionTypeOK(state);
        delivery.setCompressionRateOK(state);
        delivery.setResolutionOK(state);
        delivery.setColorspaceOK(state);
        delivery.setFileIntegrityOk(state);
        delivery.setTableOfContentsOK(state);
        delivery.setFileBibPrefixOK(state);
        delivery.setFileCaseOK(state);
        delivery.setFileRadicalOK(state);
    }

    /**
     * Contrôles :
     * Format des fichiers
     * Séquence cohérente des fichiers
     * Nombre de fichiers correct
     *
     * @param documentsForPrefix
     *            la liste à construire des fichiers/préfixes
     * @param fileMetadatasForCheck
     *            metadatas de chaque fichier master de la livraison
     */
    private Map<String, List<AutomaticCheckResult>> automaticChecks(final Delivery delivery,
                                                                    final DeliveryProcessResults processElement,
                                                                    final Map<String, PrefixedDocuments> documentsForPrefix,
                                                                    final Map<File, Optional<Map<String, String>>> fileMetadatasForCheck,
                                                                    final Map<String, Integer> expectedPagesByPrefix) {

        final String format = delivery.getLot().getRequiredFormat();
        // Recup du separateur de seq | prefixe BIB
        final Lot lot = lotService.getOneWithConfigRules(delivery.getLot().getIdentifier());
        final String seqSeparator = lot.getActiveCheckConfiguration().getSeparators();
        final String bibPrefix = lot.getProject().getLibrary().getPrefix();
        final boolean isPdfDelivery = EXTENSION_FORMAT_PDF.equalsIgnoreCase(format);

        final String folderPath = getFolderPath(delivery);
        if (folderPath == null) {
            return Collections.emptyMap();
        }
        final File[] subDirectories = new File(folderPath).listFiles(File::isDirectory);

        final Map<String, Optional<SplitFilename>> splitNames = processElement.getSplitNames();
        final Map<AutoCheckType, AutomaticCheckRule> checkingRules = processElement.getCheckingRules();

        // Contrôles du radical des fichiers
        final AutomaticCheckRule radicalRule = checkingRules.get(AutoCheckType.FILE_RADICAL);
        final boolean isRadicalActive = radicalRule != null && radicalRule.isActive();

        /*
         * Résultats des contrôles
         */
        final Map<String, List<AutomaticCheckResult>> mapResults = new HashMap<>();
        /*
         * Pas de répertoire, pas de livraison
         */
        if (subDirectories == null) {
            toggleAllDeliveryFlags(delivery, false);
            return mapResults;
        }
        /*
         * Chaque répertoire doit correspondre à une unité doc
         */
        for (final File directory : subDirectories) {
            // Vérification du fait que le prefix corresponde au radical d'un des documents associés à cette livraison
            final String prefix = getPrefixForDirectory(directory, documentsForPrefix, seqSeparator);

            if (prefix == null) {
                continue;
            }

            final boolean isJustOneFileEstampe = isJustOneFileEstampe(directory, prefix, format);

            final List<File> files = new ArrayList<>();
            getAllMastersFromDirectory(files, directory, documentsForPrefix, seqSeparator, isPdfDelivery, isRadicalActive, format);

            /*
             * Contrôle des fichiers masters
             */
            final List<AutomaticCheckResult> allResults = new ArrayList<>();
            List<String> fileNames = autoCheckService.findMastersOnly(files, format);
            final PrefixedDocuments prefixedDoc = documentsForPrefix.get(prefix);

            // Radical
            final AutomaticCheckResult resultRadical = autoCheckService.initializeAutomaticCheckResult(AutoCheckType.FILE_RADICAL);
            handleLinkResult(resultRadical, delivery, prefixedDoc);
            allResults.add(autoCheckService.checkRadicalFile(resultRadical, files, radicalRule, prefix));

            // Préfixe Bibliotheque
            final AutomaticCheckRule bibPrefixRule = checkingRules.get(AutoCheckType.FILE_BIB_PREFIX);
            final AutomaticCheckResult resultBib = autoCheckService.initializeAutomaticCheckResult(AutoCheckType.FILE_BIB_PREFIX);
            handleLinkResult(resultBib, delivery, prefixedDoc);
            allResults.add(autoCheckService.checkFileAgainstBibPrefix(resultBib, files, format, fileNames, bibPrefixRule, bibPrefix, seqSeparator));

            // Format de fichier
            final AutomaticCheckRule fileFormatRule = checkingRules.get(AutoCheckType.FILE_FORMAT);
            final AutomaticCheckResult resultFormat = autoCheckService.initializeAutomaticCheckResult(AutoCheckType.FILE_FORMAT);
            handleLinkResult(resultFormat, delivery, prefixedDoc);
            // TODO à voir en reprenant les TUs !
            fileNames = new ArrayList<>();
            allResults.add(autoCheckService.checkFileNamesAgainstFormat(resultFormat, files, format, fileNames, fileFormatRule));
            // fileNames est correctement construit, on associe les fichiers trouvés
            associateFilesWithPrefix(files, fileNames, prefixedDoc);

            expectedPagesByPrefix.put(prefix, prefixedDoc.getFiles().size());

            // Cohérence de séquence
            final AutomaticCheckRule fileSequenceRule = checkingRules.get(AutoCheckType.FILE_SEQUENCE);
            if (isPdfDelivery) {
                fileSequenceRule.setActive(false);
            }
            final AutomaticCheckResult resultSequence = autoCheckService.initializeAutomaticCheckResult(AutoCheckType.FILE_SEQUENCE);
            final boolean bibPrefixMandatory = bibPrefixRule.isActive() && checkingRules.get(AutoCheckType.FILE_BIB_PREFIX).isBlocking();
            handleLinkResult(resultSequence, delivery, prefixedDoc);
            allResults.add(autoCheckService.checkSequenceNumber(resultSequence,
                                                                fileNames,
                                                                splitNames,
                                                                fileSequenceRule,
                                                                bibPrefixMandatory,
                                                                seqSeparator,
                                                                isPdfDelivery,
                                                                isJustOneFileEstampe,
                                                                prefix));

            // Controle de la casse.
            final AutomaticCheckRule caseRule = checkingRules.get(AutoCheckType.FILE_CASE_SENSITIVE);
            final AutomaticCheckResult resultCase = autoCheckService.initializeAutomaticCheckResult(AutoCheckType.FILE_CASE_SENSITIVE);
            handleLinkResult(resultCase, delivery, prefixedDoc);
            allResults.add(autoCheckService.checkFileCase(resultCase, splitNames, caseRule, bibPrefixRule, bibPrefix, seqSeparator, prefix));

            // vérification nombre de fichiers
            final AutomaticCheckRule nbFilesRule = checkingRules.get(AutoCheckType.FILE_TOTAL_NUMBER);
            if (nbFilesRule.isActive()) {
                final AutomaticCheckResult resultNumber = autoCheckService.initializeAutomaticCheckResult(AutoCheckType.FILE_TOTAL_NUMBER);
                handleLinkResult(resultNumber, delivery, prefixedDoc);
                allResults.add(autoCheckService.checkTotalFileNumber(resultNumber, fileNames, prefixedDoc, nbFilesRule));
            }

            // vérification des metadonnées / aux prérequis (compression, resolution, colorspace)
            allResults.addAll(autoCheckService.checkMetadataOfFiles(initializeCheckMetadataResults(delivery, prefixedDoc),
                                                                    checkingRules,
                                                                    files,
                                                                    format,
                                                                    delivery.getLot(),
                                                                    fileMetadatasForCheck));

            mapResults.put(prefix, allResults);
        }
        return mapResults;
    }

    /**
     * Retourne une map avec les AutomaticCheckResult initialisés.
     *
     */
    private Map<AutoCheckType, AutomaticCheckResult> initializeCheckMetadataResults(final Delivery delivery,
                                                                                    final PrefixedDocuments prefixedDoc) {
        final Map<AutoCheckType, AutomaticCheckResult> results = new HashMap<>();
        results.put(AutoCheckType.FILE_TYPE_COMPR, autoCheckService.initializeAutomaticCheckResult(AutoCheckType.FILE_TYPE_COMPR));
        results.put(AutoCheckType.FILE_TAUX_COMPR, autoCheckService.initializeAutomaticCheckResult(AutoCheckType.FILE_TAUX_COMPR));
        results.put(AutoCheckType.FILE_RESOLUTION, autoCheckService.initializeAutomaticCheckResult(AutoCheckType.FILE_RESOLUTION));
        results.put(AutoCheckType.FILE_COLORSPACE, autoCheckService.initializeAutomaticCheckResult(AutoCheckType.FILE_COLORSPACE));
        results.put(AutoCheckType.FILE_INTEGRITY, autoCheckService.initializeAutomaticCheckResult(AutoCheckType.FILE_INTEGRITY));
        results.forEach((key, r) -> handleLinkResult(r, delivery, prefixedDoc));
        return results;
    }

    /**
     * Construit la liste des fichiers à utiliser par préfixe
     *
     */
    private void associateFilesWithPrefix(final Collection<File> files,
                                          final List<String> fileNames,
                                          final PrefixedDocuments prefixedDoc) {
        prefixedDoc.setFiles(files.stream()
                                  .filter(file -> fileNames.contains(file.getName()))
                                  .collect(Collectors.toList()));
    }

    /**
     * Indique si la livraison est refusée => Vrai.
     *
     */
    private boolean refuseDelivery(final Delivery delivery, final Map<AutoCheckType, AutomaticCheckRule> checkingRules) {

        return !delivery.isFileIntegrityOk()
               || refuseDeliveryForRule(checkingRules.get(AutoCheckType.FILE_TOTAL_NUMBER), delivery.isNumberOfFilesOK())
               || refuseDeliveryForRule(checkingRules.get(AutoCheckType.FILE_SEQUENCE), delivery.isSequentialNumbers())
               || refuseDeliveryForRule(checkingRules.get(AutoCheckType.FILE_FORMAT), delivery.isFileFormatOK())
               || refuseDeliveryForRule(checkingRules.get(AutoCheckType.FILE_BIB_PREFIX), delivery.isFileBibPrefixOK())
               || refuseDeliveryForRule(checkingRules.get(AutoCheckType.FILE_CASE_SENSITIVE), delivery.isFileCaseOK())
               || refuseDeliveryForRule(checkingRules.get(AutoCheckType.FILE_TYPE_COMPR), delivery.isCompressionTypeOK())
               || refuseDeliveryForRule(checkingRules.get(AutoCheckType.FILE_TAUX_COMPR), delivery.isCompressionRateOK())
               || refuseDeliveryForRule(checkingRules.get(AutoCheckType.FILE_RESOLUTION), delivery.isResolutionOK())
               || refuseDeliveryForRule(checkingRules.get(AutoCheckType.FILE_COLORSPACE), delivery.isColorspaceOK())
               || refuseDeliveryForRule(checkingRules.get(AutoCheckType.FILE_RADICAL), delivery.isFileRadicalOK());
    }

    /**
     * Indicateur vrai si la livraison est refusée pour un controle donné.
     *
     */
    private boolean refuseDeliveryForRule(final AutomaticCheckRule rule, final boolean deliveryResultOK) {
        return rule != null && rule.isActive()
               && rule.isBlocking()
               && !deliveryResultOK;
    }

    /**
     * Remonte les infos individuelles des dossiers de la livraison dans la livraison pour l'état global
     */
    private void handleDeliveryCheckState(final Delivery delivery, final List<AutomaticCheckResult> allResults) {

        if (allResults
                      .stream()
                      .anyMatch(result -> AutoCheckType.FILE_RADICAL.equals(result.getType()) && AutoCheckResult.KO.equals(result.getResult()))) {
            delivery.setFileRadicalOK(false);
        }
        if (allResults
                      .stream()
                      .anyMatch(result -> AutoCheckType.FILE_FORMAT.equals(result.getType()) && AutoCheckResult.KO.equals(result.getResult()))) {
            delivery.setFileFormatOK(false);
        }
        if (allResults
                      .stream()
                      .anyMatch(result -> AutoCheckType.FILE_BIB_PREFIX.equals(result.getType()) && !AutoCheckResult.OK.equals(result.getResult()))) {
            delivery.setFileBibPrefixOK(false);
        }
        if (allResults
                      .stream()
                      .anyMatch(result -> AutoCheckType.FILE_CASE_SENSITIVE.equals(result.getType())
                                          && !AutoCheckResult.OK.equals(result.getResult()))) {
            delivery.setFileCaseOK(false);
        }
        if (allResults
                      .stream()
                      .anyMatch(result -> AutoCheckType.FILE_SEQUENCE.equals(result.getType()) && AutoCheckResult.KO.equals(result.getResult()))) {
            delivery.setSequentialNumbers(false);
        }
        if (allResults
                      .stream()
                      .anyMatch(result -> AutoCheckType.FILE_TOTAL_NUMBER.equals(result.getType())
                                          && AutoCheckResult.KO.equals(result.getResult()))) {
            delivery.setNumberOfFilesOK(false);
        }
        if (allResults
                      .stream()
                      .anyMatch(result -> AutoCheckType.METADATA_FILE.equals(result.getType()) && AutoCheckResult.KO.equals(result.getResult()))) {
            delivery.setTableOfContentsOK(false);
        }
        if (allResults
                      .stream()
                      .anyMatch(result -> AutoCheckType.FILE_TYPE_COMPR.equals(result.getType()) && !AutoCheckResult.OK.equals(result.getResult()))) {
            delivery.setCompressionTypeOK(false);
        }
        if (allResults
                      .stream()
                      .anyMatch(result -> AutoCheckType.FILE_TAUX_COMPR.equals(result.getType()) && !AutoCheckResult.OK.equals(result.getResult()))) {
            delivery.setCompressionRateOK(false);
        }
        if (allResults
                      .stream()
                      .anyMatch(result -> AutoCheckType.FILE_RESOLUTION.equals(result.getType()) && !AutoCheckResult.OK.equals(result.getResult()))) {
            delivery.setResolutionOK(false);
        }
        if (allResults
                      .stream()
                      .anyMatch(result -> AutoCheckType.FILE_COLORSPACE.equals(result.getType()) && !AutoCheckResult.OK.equals(result.getResult()))) {
            delivery.setColorspaceOK(false);
        }
        if (allResults
                      .stream()
                      .anyMatch(result -> AutoCheckType.FILE_INTEGRITY.equals(result.getType()) && AutoCheckResult.KO.equals(result.getResult()))) {
            delivery.setFileIntegrityOk(false);
        }
        if (allResults
                      .stream()
                      .anyMatch(result -> AutoCheckType.FILE_PDF_MULTI.equals(result.getType()) && !AutoCheckResult.OK.equals(result.getResult()))) {
            delivery.setPdfMultiOK(false);
        }
    }

    /**
     * Retrouve le radical des fichiers à livrer dans le dossier
     * Le radical est issu des documents physiques et est retourné s'il est contenu dans le nom du dossier
     *
     * @param directory
     *            Sous-dossier de livraison
     * @param documentsForPrefix
     *            Un set de préfixes possibles pour le lot livré
     * @return un préfixe ou null si aucun match dans le dossier
     */
    private String getPrefixForDirectory(final File directory, final Map<String, PrefixedDocuments> documentsForPrefix, final String seqSeparator) {
        if (directory.isDirectory()) {
            for (final String prefix : documentsForPrefix.keySet()) {
                // #2522 : bazar ds les prefixes =>
                // soit le nom == prefix
                // soit le nom contient prefix + le separateur de sequence
                if (StringUtils.equals(directory.getName(), prefix)
                    || StringUtils.contains(directory.getName(), prefix.concat(seqSeparator))) {
                    return prefix;
                }
            }
        }
        return null;
    }

    /**
     * Filtre pour récupération de tous les fichiers de nom contenant le prefix suivi du separateur de sequence (si pas PDF).
     * Attention : sensible à la casse
     */
    public RegexFileFilter
           getPrefixFilter(final String prefix, final String seqSeparator, final boolean isPdfDelivery, final boolean isJustOneFileEstampe) {

        final String searchPattern = (isPdfDelivery || isJustOneFileEstampe) ? Pattern.quote(prefix)
                                                                             : Pattern.quote(prefix).concat(".*").concat(Pattern.quote(seqSeparator));
        return new RegexFileFilter(".*"
                                       .concat(searchPattern)
                                       .concat(".*"),
                                   IOCase.SENSITIVE);
    }

    /**
     * Filtre pour récupération de tous les fichiers de nom contenant seulement le format
     * insensible à la casse
     *
     */
    public RegexFileFilter getFormatFilter(final String format) {

        return new RegexFileFilter(".*"
                                       .concat(Pattern.quote(format)),
                                   IOCase.INSENSITIVE);
    }

    /**
     * Retourne le chemin complet correspondant à la livraison
     */
    private String getFolderPath(final Delivery delivery) {
        final FTPConfiguration activeFTPConfiguration = lotService.getActiveFTPConfiguration(delivery.getLot());
        if (activeFTPConfiguration == null) {
            LOG.error("Aucune configuration FTP active n'a été trouvée pour la livraison du lot {}", delivery.getLot());
            return null;
        }
        return Paths.get(activeFTPConfiguration.getDeliveryFolder(), delivery.getFolderPath()).toString();
    }

    /**
     * Remplit les champs pour chaque résultat (delivery, physicalDoc et DigitalDoc)
     *
     */
    private void handleLinkResult(final AutomaticCheckResult result,
                                  final Delivery delivery,
                                  final PrefixedDocuments prefixedDocuments) {
        result.setDelivery(delivery);
        if (!prefixedDocuments.getPhysicalDocuments().isEmpty()) {
            result.setPhysicalDocument(prefixedDocuments.getPhysicalDocuments().get(0));
        }
        if (!prefixedDocuments.getDigitalDocuments().isEmpty()) {
            result.setDigitalDocument(prefixedDocuments.getDigitalDocuments().get(0));
        }
    }

    public boolean isJustOneFileEstampe(final File directory, final String prefix, final String format) {
        /**
         * Récupération de tous les fichiers dans tous les dossiers qui contiennent le prefix
         * FIXME : indiquer de manière précise le rôle des différents dossiers ? Quid des fichiers ignorés intéressant (ex sip.xml)
         * Il pourra être bon de récupérer unitairement les fichiers marqués, ce qui laisse ce filtre se charger du gros oeuvre uniquement
         */
        // #4993 - Cas des estampes : 1 seul fichier master, pas forcément besoin de sequence..
        final Collection<File> filesToHandle = FileUtils.listFiles(directory, TrueFileFilter.INSTANCE, TrueFileFilter.INSTANCE);
        final List<String> realNames = autoCheckService.findMastersOnly(filesToHandle, format);
        return realNames.size() == 1 && StringUtils.equalsIgnoreCase(realNames.get(0), (prefix + "." + format));
    }

    /**
     * Tri une liste de fichiers en fonction de sa séquence
     * @param files
     */
    private void sortWithSequence(final List<File> files){
        // Tri des fichiers (alphanumérique, image_001, image_002 ...)
        final Pattern p = Pattern.compile("\\d+");
        files.sort((f1, f2) -> {
            String name1 = f1.getName();
            String name2 = f2.getName();
            Matcher m = p.matcher(name1);
            Integer number1 = null;
            if (!m.find()) {
                return name1.compareTo(name2);
            } else {
                Integer number2 = null;
                while (m.find()) {
                    number1 = Integer.parseInt(m.group());
                }
                m = p.matcher(name2);
                if (!m.find()) {
                    return name1.compareTo(name2);
                } else {
                    while (m.find()) {
                        number2 = Integer.parseInt(m.group());
                    }
                    int comparaison = 0;
                    if(number2 != null && number1 != null){
                        comparaison = number1.compareTo(number2);
                    }
                    if (comparaison != 0) {
                        return comparaison;
                    } else {
                        return name1.compareTo(name2);
                    }
                }
            }
        });
    }
}
