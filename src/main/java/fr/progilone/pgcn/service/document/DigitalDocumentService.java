package fr.progilone.pgcn.service.document;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.collections4.IterableUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import fr.progilone.pgcn.domain.AbstractDomainObject;
import fr.progilone.pgcn.domain.administration.viewsformat.ViewsFormatConfiguration;
import fr.progilone.pgcn.domain.administration.viewsformat.ViewsFormatConfiguration.FileFormat;
import fr.progilone.pgcn.domain.checkconfiguration.CheckConfiguration;
import fr.progilone.pgcn.domain.delivery.DeliveredDocument;
import fr.progilone.pgcn.domain.delivery.Delivery;
import fr.progilone.pgcn.domain.document.DigitalDocument;
import fr.progilone.pgcn.domain.document.DigitalDocument.DigitalDocumentStatus;
import fr.progilone.pgcn.domain.document.DocPage;
import fr.progilone.pgcn.domain.document.DocUnit;
import fr.progilone.pgcn.domain.dto.checkconfiguration.CheckConfigurationDTO;
import fr.progilone.pgcn.domain.storage.StoredFile;
import fr.progilone.pgcn.domain.workflow.WorkflowStateKey;
import fr.progilone.pgcn.exception.PgcnTechnicalException;
import fr.progilone.pgcn.repository.delivery.DeliveryRepository;
import fr.progilone.pgcn.repository.document.DigitalDocumentRepository;
import fr.progilone.pgcn.repository.document.DocPageRepository;
import fr.progilone.pgcn.repository.storage.BinaryRepository;
import fr.progilone.pgcn.service.MailService;
import fr.progilone.pgcn.service.checkconfiguration.mapper.CheckConfigurationMapper;
import fr.progilone.pgcn.service.storage.BinaryStorageManager;
import fr.progilone.pgcn.service.workflow.WorkflowService;

@Service
public class DigitalDocumentService {

    private static final Logger LOG = LoggerFactory.getLogger(DigitalDocumentService.class);

    private final DigitalDocumentRepository digitalDocumentRepository;
    private final DocPageRepository docPageRepository;
    private final BinaryRepository binaryRepository;
    private final BinaryStorageManager bm;
    private final WorkflowService workflowService;
    private final SlipService slipService;
    private final MailService mailService;
    private final DocUnitService docUnitService;
    private final DeliveryRepository deliveryRepository;


    @Value("${spring.mail.from}")
    private String mailFrom;

    @Autowired
    public DigitalDocumentService(final DigitalDocumentRepository digitalDocumentRepository,
                                         final DocPageRepository docPageRepository,
                                         final BinaryRepository binaryRepository,
                                         final BinaryStorageManager bm,
                                         final WorkflowService workflowService,
                                         final SlipService slipService,
                                         final MailService mailService,
                                         final DocUnitService docUnitService,
                                         final DeliveryRepository deliveryRepository) {
        this.digitalDocumentRepository = digitalDocumentRepository;
        this.docPageRepository = docPageRepository;
        this.binaryRepository = binaryRepository;
        this.bm = bm;
        this.workflowService = workflowService;
        this.slipService = slipService;
        this.mailService = mailService;
        this.docUnitService = docUnitService;
        this.deliveryRepository = deliveryRepository;
    }

    @Transactional
    public DigitalDocument save(final DigitalDocument digitalDocument) {
        return digitalDocumentRepository.save(digitalDocument);
    }

    @Transactional
    public void delete(final Set<DigitalDocument> dds) {
        digitalDocumentRepository.delete(dds);
    }

    @Transactional
    public void delete(final DigitalDocument dd) {
        digitalDocumentRepository.delete(dd);
    }

    /**
     * Récupération au format StoredFileFormat.LABEL_THUMBNAIL
     *
     * @param identifier DocPage
     * @param page page
     * @return File
     */
    @Transactional(readOnly = true)
    public File getThumbnail(final String identifier, final int page) {
        final DocPage dp = getPage(identifier, page);
        final StoredFile sf = binaryRepository.getOneByPageIdentifierAndFileFormat(dp.getIdentifier(), ViewsFormatConfiguration.FileFormat.THUMB);
        return bm.getFileForStoredFile(sf);
    }

    /**
     * Récupération au format "View"
     *
     * @param identifier
     * @param page
     * @return
     */
    @Transactional(readOnly = true)
    public File getView(final String identifier, final int page) {
        final DocPage dp = getPage(identifier, page);
        final StoredFile sf = binaryRepository.getOneByPageIdentifierAndFileFormat(dp.getIdentifier(), ViewsFormatConfiguration.FileFormat.VIEW);
        return bm.getFileForStoredFile(sf);
    }

    /**
     * Récupération au format "Print"
     *
     * @param identifier
     * @param page
     * @return
     */
    @Transactional(readOnly = true)
    public File getPrint(final String identifier, final int page) {
        final DocPage dp = getPage(identifier, page);
        final StoredFile sf = binaryRepository.getOneByPageIdentifierAndFileFormat(dp.getIdentifier(), ViewsFormatConfiguration.FileFormat.PRINT);
        return bm.getFileForStoredFile(sf);
    }

    /**
     * Récupération du "Master"
     *
     * @param identifier
     * @param page
     * @return
     */
    @Transactional(readOnly = true)
    public File getMaster(final String identifier, final int page) {
        final DocPage dp = getPage(identifier, page);
        final StoredFile sf = binaryRepository.getOneByPageIdentifierAndFileFormat(dp.getIdentifier(), ViewsFormatConfiguration.FileFormat.MASTER);
        return bm.getFileForStoredFile(sf);
    }

    /**
     * Récupération du "Master PDF".
     *
     * @param identifier
     * @return
     */
    @Transactional(readOnly = true)
    public File getPdfMaster(final String identifier) {
        final DocPage dp = getPdfPage(identifier);
        final StoredFile sf = binaryRepository.getOneByPageIdentifierAndFileFormat(dp.getIdentifier(), ViewsFormatConfiguration.FileFormat.MASTER);
        return bm.getFileForStoredFile(sf);
    }


    /**
     * Simple recuperation du format ZOOM.
     *
     * @param identifier
     * @param page
     * @return
     */
    @Transactional(readOnly = true)
    public File getZoom(final String identifier, final int page) {
        final DocPage dp = getPage(identifier, page);
        final StoredFile sf = binaryRepository.getOneByPageIdentifierAndFileFormat(dp.getIdentifier(), ViewsFormatConfiguration.FileFormat.ZOOM);
        return bm.getFileForStoredFile(sf);
    }

    /**
     * Récupération au format "ZOOM"/"XTRA_ZOOM"
     *
     * @param identifier
     * @param page
     * @param args
     * @return
     */
    @Transactional(readOnly = true)
    public File getZoomOrXtra(final String identifier, final int page, final String args) {
        final DocPage dp = getPage(identifier, page);
        StoredFile sf = null;

        if (dp.getMaster().isPresent()) {
            final StoredFile master = dp.getMaster().get();
            if (ArrayUtils.contains(args.split(","), String.valueOf(master.getWidth()))
                                || ArrayUtils.contains(args.split(","), String.valueOf(master.getHeight()))) {
                // XtraZoom demandé
                sf = binaryRepository.getOneByPageIdentifierAndFileFormat(dp.getIdentifier(), ViewsFormatConfiguration.FileFormat.XTRAZOOM);
            }
        }
        if (sf == null) {
            sf = binaryRepository.getOneByPageIdentifierAndFileFormat(dp.getIdentifier(), ViewsFormatConfiguration.FileFormat.ZOOM);
        }
        return bm.getFileForStoredFile(sf);
    }


    /**
     * Recupere la checkConfig active du doc.
     *
     * @param identifier
     * @return
     */
    @Transactional(readOnly = true)
    public CheckConfigurationDTO getConfigurationCheck(final String identifier) {

        final DigitalDocument dd = digitalDocumentRepository.getOneWithCheckConfiguration(identifier);
        final CheckConfiguration cc = dd.getDocUnit().getLot().getActiveCheckConfiguration();
        return  CheckConfigurationMapper.INSTANCE.checkConfigurationToCheckConfigurationDTO(cc);
    }

    @Transactional(readOnly = true)
    public String getDeliveryNotes(final String identifier) {
        final DigitalDocument doc = findOne(identifier);
        final DeliveredDocument deliv = Collections.max(doc.getDeliveries(), Comparator.comparing(d->d.getLastModifiedDate()));

        return deliv!=null?deliv.getDelivery().getDigitizingNotes():null;
    }

    @Transactional(readOnly = true)
    public String getFilename(final String identifier, final int page) {
        final DocPage dp = getPage(identifier, page);
        final StoredFile sf = binaryRepository.getOneByPageIdentifierAndFileFormat(dp.getIdentifier(), ViewsFormatConfiguration.FileFormat.THUMB);
        return sf.getFilename();
    }


    @Transactional(readOnly = true)
    public String[] getMasterPdfName(final String identifier) {
        final String[] res = new String[2];
        final DocPage master = docPageRepository.getMasterPdfByDigitalDocumentIdentifier(identifier);
        if (master != null) {
            final StoredFile sf = binaryRepository.getOneByPageIdentifierAndFileFormat(master.getIdentifier(), FileFormat.MASTER);
            res[0] = sf.getFilename();
            res[1] = String.valueOf(sf.getLength());
        }

        return res;
    }

    @Transactional(readOnly = true)
    public List<String> getFilenamesWithErrors(final String identifier) {
        final List<DocPage> dps = docPageRepository.getAllByDigitalDocumentIdentifier(identifier);
        if (!dps.isEmpty()) {
            final List<String> ids = dps.stream().map(AbstractDomainObject::getIdentifier).collect(Collectors.toList());
            final List<StoredFile> sfs = binaryRepository.getAllByPageIdentifiersAndFileFormat(ids, ViewsFormatConfiguration.FileFormat.THUMB);
            return sfs.stream().map(StoredFile::getFilename).collect(Collectors.toList());
        }
        return Collections.emptyList();
    }

    @Transactional(readOnly = true)
    public List<Integer> getFileNumbersWithErrors(final String identifier) {
        return docPageRepository.getPageNumbersByDigitalDocumentIdentifierWithErrors(identifier);
    }

    /**
     * Retourne un document avec les infos sur ses pages.
     *
     * @param id
     * @return
     */
    @Transactional(readOnly = true)
    public DigitalDocument getOneWithPages(final String id) {
        return digitalDocumentRepository.getOneWithPages(id);
    }

    /**
     * Retourne un document avec les infos sur ses pages + DocUnit.
     *
     * @param id
     * @return
     */
    @Transactional(readOnly = true)
    public DigitalDocument getOneWithDocUnitAndPages(final String id) {
        return digitalDocumentRepository.getOneWithDocUnitAndPages(id);
    }
    
    @Transactional(readOnly = true)
    public List<DigitalDocument> getAllByDigitalIdAndLotIdentifier(final String prefix, final String identifier) {
        return digitalDocumentRepository.getAllByDigitalIdAndLotIdentifier(prefix, identifier);
    }



    /**
     * Génération du bordereau de contrôles et envoi au prestataire si la livraison est integralement traitée.
     *
     * @param docUnitId
     * @param lastDeliveredDoc
     * @return
     */
    @Transactional
    public void generateAndSendCheckSlip(final String docUnitId, final Optional<DeliveredDocument> lastDeliveredDoc) {

        // Génération du bordereau de contrôles et envoi au prestataire si la livraison est traitée.
        if(workflowService.isStateRunning(docUnitId, WorkflowStateKey.RAPPORT_CONTROLES)) {
            // Récupération du rapport
            if(lastDeliveredDoc.isPresent()) {
                final Delivery delivery = lastDeliveredDoc.get().getDelivery();
                // creation bordereau
                boolean created = true;
                slipService.createCheckSlip(delivery.getIdentifier());
                final ByteArrayOutputStream report = new ByteArrayOutputStream();
                try {
                    // generation du pdf
                    slipService.writePdfCheckSlip(report, delivery.getIdentifier());
                } catch (final PgcnTechnicalException e) {
                    LOG.warn("Erreur lors de la création du bordereau de controle - Livraison {}", delivery.getIdentifier(), e);
                    created = false;
                }


                // Envoi du bordereau au prestataire si tous les docs sont traités.
                final Set<DeliveredDocument> documents = deliveryRepository.findSimpleDeliveredDocumentsByDeliveryIdentifier(delivery.getIdentifier());
                if (allDeliveryDocsChecked(documents, lastDeliveredDoc.get())) {

                    final List<String> mailTo = new ArrayList<>();
                    final String providerMail = docUnitService.getProviderMail(docUnitId);

                    if (StringUtils.isNotBlank(providerMail)) {
                        mailTo.add(providerMail);
                    }
                    final String[] to = new String[mailTo.size()];
                    mailTo.toArray(to);

                    final String dtDelivery = DateTimeFormatter.ofPattern("dd/MM/yyyy").format(lastDeliveredDoc.get().getDeliveryDate());
                    final String contentText = "Rapport de contrôles (Livraison ".concat(delivery.getLabel())
                                                                                    .concat(" - ")
                                                                                    .concat(dtDelivery)
                                                                                    .concat(")");

                    final boolean sent = created && mailService.sendEmailWithStreamAttachment(mailFrom, to,
                                                                                   docUnitService.getResponsibleMail(docUnitId),
                                                                                   contentText,
                                                                                   contentText,
                                                                                   "RapportControles-livraison ".concat(dtDelivery).concat(".pdf"),
                                                                                   new ByteArrayInputStream(report.toByteArray()),
                                                                                   "application/pdf", true, true);
                    if (! sent) {
                        LOG.trace(created ?"L'envoi du bordereau de controle échoué":"La génération du bordereau de controle a échoué");
                        if (mailTo.isEmpty()) {
                            LOG.trace("Echec envoi mail : mail prestataire non trouvé!");
                        }
                    }

                    documents.stream()
                            .map(doc -> doc.getDigitalDocument().getIdentifier())
                            .filter(ddId -> ddId != null)
                            .map(ddId -> findDocUnitByIdentifier(ddId))
                            .forEach(dUnit -> {
                        
//                        final DocUnit du = findDocUnitByIdentifier(doc.getIdentifier());
                        
                        if(sent) {
                            workflowService.processAutomaticState(dUnit.getIdentifier(), WorkflowStateKey.RAPPORT_CONTROLES);
                        } else {
                            workflowService.rejectAutomaticState(dUnit.getIdentifier(), WorkflowStateKey.RAPPORT_CONTROLES);
                        }
                    });
                }
            }
        }
    }

    /**
     * Vrai si il ne reste pas d'autre doc à traiter.
     *
     * @param documents
     * @param current
     * @return
     */
    public boolean allDeliveryDocsChecked(final Set<DeliveredDocument> documents, final DeliveredDocument current) {
        final Optional<DeliveredDocument> deliv = documents.stream()
                                        .filter(doc-> !StringUtils.equals(current.getIdentifier(), doc.getIdentifier())
                                                && DigitalDocumentStatus.VALIDATED != doc.getStatus()
                                                && DigitalDocumentStatus.REJECTED != doc.getStatus() )
                                        .findFirst();
        return ! deliv.isPresent();
    }

    /**
     * Renvoie les documents à contrôler
     *
     * @return
     */
    @Transactional(readOnly = true)
    public Set<DigitalDocument> getAllDocumentsToCheck() {
        return digitalDocumentRepository.getAllByStatusIn(DigitalDocument.DigitalDocumentStatus.PRE_REJECTED,
                                                          DigitalDocument.DigitalDocumentStatus.TO_CHECK,
                                                          DigitalDocument.DigitalDocumentStatus.CHECKING);
    }

    /**
     * Récupère une page par son numéro et son identifiant de document numérique
     *
     * @param identifier
     * @param pageNumber
     * @return
     */
    @Transactional  //(readOnly = true)
    public DocPage getPage(final String identifier, final int pageNumber) {

        final DigitalDocument doc = digitalDocumentRepository.getOne(identifier);
        final Optional<DocPage> page = doc.getOrderedPages().stream()
                                        .filter(p -> p.getNumber() != null && pageNumber == p.getNumber())
                                        .findFirst();
        if (page.isPresent()) {
            return page.get();
        }
        return null;
    }

    /**
     * Récupère une page de format pdf (number null) par son identifiant de document numérique.
     *
     * @param identifier
     * @return
     */
    @Transactional(readOnly = true)
    public DocPage getPdfPage(final String identifier) {

        final DigitalDocument doc = digitalDocumentRepository.getOne(identifier);
        final Optional<DocPage> page = doc.getOrderedPages().stream()
                                        .filter(p -> p.getNumber() == null)
                                        .findFirst();
        if (page.isPresent()) {
            return page.get();
        }
        return null;
    }

    /**
     * Récupère une page par son numéro et son identifiant de document numérique
     *
     * @param identifier
     * @param orderNumber
     * @return
     */
    @Transactional
    public DocPage getPageByOrder(final String identifier, final int orderNumber) {

        final List<DocPage> pages = docPageRepository.getAllByDigitalDocumentIdentifier(identifier);
        pages.sort(new DigitalDocument.ComparableComparator<>());
        final int firstIndex = pages.get(0).getNumber();
        // rattrape decalage si l'ordre debute à 1
        int index = orderNumber-firstIndex;
        if (index < 0 ) {
            index = 0;
        }
        // fixe le pb d'index qd Mirador s'emballe ...
        index = index > pages.size()-1 ? pages.size()-1 : index;
        final DocPage pg = pages.get(index);

        return pg;
    }



    /**
     * Récupère une page par son numéro et son identifiant de sample.
     *
     * @param identifier
     * @param pageNumber
     * @return
     */
    @Transactional(readOnly = true)
    public DocPage getSampledPage(final String identifier, final int pageNumber) {
        return docPageRepository.getOneByNumberAndSampleIdentifier(pageNumber, identifier);
    }

    /**
     * Recherche par filtres.
     *
     * @param search
     * @param status
     * @param libraries
     * @param projects
     * @param lots
     * @param trains
     * @param deliveries
     * @param dateFrom
     * @param dateTo
     * @param dateLimitFrom
     * @param dateLimitTo
     * @param searchPgcnId
     * @param searchTitre
     * @param searchRadical
     * @param searchPageFrom
     * @param searchPageTo
     * @param searchPageCheckFrom
     * @param searchPageCheckTo
     * @param searchMinSize
     * @param searchMaxSize
     * @param pageRequest
     * @return
     */
    @Transactional(readOnly = true)
    public Page<DigitalDocument> search(final String search,
                                        final List<String> status,
                                        final List<String> libraries,
                                        final List<String> projects,
                                        final List<String> lots,
                                        final List<String> trains,
                                        final List<String> deliveries,
                                        final LocalDate dateFrom,
                                        final LocalDate dateTo,
                                        final LocalDate dateLimitFrom,
                                        final LocalDate dateLimitTo,
                                        final boolean relivraison,
                                        final String searchPgcnId,
                                        final String searchTitre,
                                        final String searchRadical,
                                        final List<String> searchFileFormats,
                                        final List<String> searchMaxAngles,
                                        final Integer searchPageFrom,
                                        final Integer searchPageTo,
                                        final Integer searchPageCheckFrom,
                                        final Integer searchPageCheckTo,
                                        final Double searchMinSize,
                                        final Double searchMaxSize,
                                        final boolean validated,
                                        final Pageable pageRequest) {

        final List<DigitalDocument.DigitalDocumentStatus> statuses;
        if (status != null) {
            statuses = status.stream().map(DigitalDocument.DigitalDocumentStatus::valueOf).collect(Collectors.toList());
        } else {
            statuses = null;
        }
        return digitalDocumentRepository.search(search,
                                                statuses,
                                                libraries,
                                                projects,
                                                lots,
                                                trains,
                                                deliveries,
                                                dateFrom,
                                                dateTo,
                                                dateLimitFrom,
                                                dateLimitTo,
                                                relivraison,
                                                searchPgcnId,
                                                searchTitre,
                                                searchRadical,
                                                searchFileFormats,
                                                searchMaxAngles,
                                                searchPageFrom,
                                                searchPageTo,
                                                searchPageCheckFrom,
                                                searchPageCheckTo,
                                                searchMinSize,
                                                searchMaxSize,
                                                validated,
                                                pageRequest);
    }

    @Transactional(readOnly = true)
    public DigitalDocument findOne(final String identifier) {
        return digitalDocumentRepository.findOne(identifier);
    }
    
    @Transactional(readOnly = true)
    public List<DigitalDocument> getAllByDigitalId(final String digitalId) {
        return digitalDocumentRepository.getAllByDigitalId(digitalId);
    }

    @Transactional(readOnly = true)
    public List<DigitalDocument> findAll(final Iterable<String> identifiers) {
        if (IterableUtils.isEmpty(identifiers)) {
            return Collections.emptyList();
        }
        return digitalDocumentRepository.findAll(identifiers);
    }

    /**
     * Retourne ou créé le DeliveredDocument correspondant à la livraison delivery
     *
     * @param digitalDocument
     * @param delivery
     * @return
     */
    @Transactional(readOnly = true)
    public DeliveredDocument getDeliveredDocument(final DigitalDocument digitalDocument, final Delivery delivery) {
        return getDeliveredDocumentIfExists(digitalDocument, delivery)
            // ou Création d'un nouveau DeliveredDocument
            .orElseGet(() -> {
                final DeliveredDocument deliveredDocument = new DeliveredDocument();
                deliveredDocument.setDelivery(delivery);
                deliveredDocument.setDeliveryDate(digitalDocument.getDeliveryDate());
                deliveredDocument.setNbPages(digitalDocument.getNbPages());
                deliveredDocument.setStatus(digitalDocument.getStatus());
                deliveredDocument.setTotalLength(digitalDocument.getTotalLength());
                digitalDocument.addDelivery(deliveredDocument);
                return deliveredDocument;
            });
    }

    /**
     * Retourne ou créé le DeliveredDocument correspondant à la livraison delivery
     *
     * @param digitalDocument
     * @param delivery
     * @return
     */
    @Transactional(readOnly = true)
    public Optional<DeliveredDocument> getDeliveredDocumentIfExists(final DigitalDocument digitalDocument, final Delivery delivery) {
        return digitalDocument.getDeliveries()
                              .stream()
                              .filter(doc -> StringUtils.equals(doc.getDelivery().getIdentifier(), delivery.getIdentifier()))
                              .findAny();
    }

    @Transactional(readOnly = true)
    public DocUnit findDocUnitByIdentifier(final String identifier) {
        return digitalDocumentRepository.findDocUnitByIdentifier(identifier);
    }
}
