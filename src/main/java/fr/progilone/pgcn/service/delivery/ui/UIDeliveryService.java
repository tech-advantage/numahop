package fr.progilone.pgcn.service.delivery.ui;

import fr.progilone.pgcn.domain.checkconfiguration.CheckConfiguration;
import fr.progilone.pgcn.domain.delivery.DeliveredDocument;
import fr.progilone.pgcn.domain.delivery.Delivery;
import fr.progilone.pgcn.domain.delivery.Delivery.DeliveryStatus;
import fr.progilone.pgcn.domain.document.DigitalDocument;
import fr.progilone.pgcn.domain.document.DigitalDocument.DigitalDocumentStatus;
import fr.progilone.pgcn.domain.document.DocUnit;
import fr.progilone.pgcn.domain.dto.audit.AuditDeliveryRevisionDTO;
import fr.progilone.pgcn.domain.dto.checkconfiguration.CheckConfigurationDTO;
import fr.progilone.pgcn.domain.dto.delivery.DeliveryDTO;
import fr.progilone.pgcn.domain.dto.delivery.ManualDeliveryDTO;
import fr.progilone.pgcn.domain.dto.delivery.PreDeliveryDTO;
import fr.progilone.pgcn.domain.dto.delivery.SimpleDeliveryDTO;
import fr.progilone.pgcn.domain.dto.delivery.SimpleDeliveryForViewerDTO;
import fr.progilone.pgcn.domain.dto.document.DigitalDocumentDTO;
import fr.progilone.pgcn.domain.dto.document.PreDeliveryDocumentDTO;
import fr.progilone.pgcn.domain.dto.document.SimpleDeliveredDigitalDocDTO;
import fr.progilone.pgcn.domain.dto.statistics.StatisticsProviderDeliveryDTO;
import fr.progilone.pgcn.domain.library.Library;
import fr.progilone.pgcn.domain.lot.Lot;
import fr.progilone.pgcn.domain.project.Project;
import fr.progilone.pgcn.domain.user.User;
import fr.progilone.pgcn.domain.util.CustomUserDetails;
import fr.progilone.pgcn.exception.PgcnTechnicalException;
import fr.progilone.pgcn.exception.PgcnValidationException;
import fr.progilone.pgcn.exception.message.PgcnError;
import fr.progilone.pgcn.exception.message.PgcnErrorCode;
import fr.progilone.pgcn.exception.message.PgcnList;
import fr.progilone.pgcn.security.SecurityUtils;
import fr.progilone.pgcn.service.checkconfiguration.mapper.CheckConfigurationMapper;
import fr.progilone.pgcn.service.delivery.DeliveryAsyncService;
import fr.progilone.pgcn.service.delivery.DeliveryProcessResults;
import fr.progilone.pgcn.service.delivery.DeliveryProcessService;
import fr.progilone.pgcn.service.delivery.DeliveryService;
import fr.progilone.pgcn.service.delivery.mapper.DeliveryMapper;
import fr.progilone.pgcn.service.delivery.mapper.UIDeliveryMapper;
import fr.progilone.pgcn.service.document.DigitalDocumentService;
import fr.progilone.pgcn.service.document.DocUnitService;
import fr.progilone.pgcn.service.document.mapper.DeliveredDocumentMapper;
import fr.progilone.pgcn.service.es.EsDeliveryService;
import fr.progilone.pgcn.service.es.EsDocUnitService;
import fr.progilone.pgcn.service.workflow.DocUnitWorkflowService;
import fr.progilone.pgcn.web.util.AccessHelper;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service dédié à les gestion des vues des lots
 */
@Service
public class UIDeliveryService {

    private final DeliveryService deliveryService;
    private final DeliveryAsyncService deliveryAsyncService;
    private final DeliveryProcessService deliveryProcessService;
    private final EsDeliveryService esDeliveryService;
    private final EsDocUnitService esDocUnitService;
    private final UIDeliveryMapper uiDeliveryMapper;
    private final DocUnitService docUnitService;
    private final DocUnitWorkflowService docUnitWorkflowService;
    private final DigitalDocumentService digitalDocumentService;
    private final AccessHelper accessHelper;

    @Autowired
    public UIDeliveryService(final DeliveryService deliveryService,
                             final DeliveryAsyncService deliveryAsyncService,
                             final DeliveryProcessService deliveryProcessService,
                             final EsDeliveryService esDeliveryService,
                             final EsDocUnitService esDocUnitService,
                             final UIDeliveryMapper uiDeliveryMapper,
                             final DocUnitService docUnitService,
                             final DocUnitWorkflowService docUnitWorkflowService,
                             final DigitalDocumentService digitalDocumentService,
                             final AccessHelper accessHelper) {
        this.deliveryService = deliveryService;
        this.deliveryAsyncService = deliveryAsyncService;
        this.deliveryProcessService = deliveryProcessService;
        this.esDeliveryService = esDeliveryService;
        this.esDocUnitService = esDocUnitService;
        this.uiDeliveryMapper = uiDeliveryMapper;
        this.docUnitService = docUnitService;
        this.docUnitWorkflowService = docUnitWorkflowService;
        this.digitalDocumentService = digitalDocumentService;
        this.accessHelper = accessHelper;
    }

    @Transactional(readOnly = true)
    public DeliveryDTO getOne(final String id) {
        final Delivery delivery = deliveryService.getOne(id);
        return DeliveryMapper.INSTANCE.deliveryToDeliveryDTO(delivery);
    }

    @Transactional
    public Set<SimpleDeliveredDigitalDocDTO> getSimpleDigitalDocuments(final String id) {
        final Set<DeliveredDocument> digitalDocuments = deliveryService.getSimpleDigitalDocumentsByDelivery(id);
        return DeliveredDocumentMapper.INSTANCE.docToSimpleDTOs(digitalDocuments);
    }

    public PreDeliveryDTO predeliver(final String id, final boolean createDocs) {
        final Delivery delivery = deliveryService.findOneWithDep(id);
        return deliveryProcessService.predeliver(delivery, createDocs);
    }

    @Transactional
    public DeliveryDTO update(final ManualDeliveryDTO request) throws PgcnValidationException {
        validate(request);
        final Delivery delivery = deliveryService.getOne(request.getIdentifier());
        uiDeliveryMapper.mapInto(request, delivery);
        final Delivery savedDelivery = deliveryService.save(delivery);
        final Delivery deliveryWithProperties = deliveryService.getOne(savedDelivery.getIdentifier());
        return DeliveryMapper.INSTANCE.deliveryToDeliveryDTO(deliveryWithProperties);
    }

    /**
     * Infos espace disque / bibliothèque.
     */
    public Map<String, Long> getDiskInfos(final String libIdentifier) {

        return deliveryProcessService.getDiskInfos(libIdentifier);
    }

    /**
     * Réalisation de la livraison avec une partie synchrone (initialisation) et une
     * partie asynchrone (réalisation)
     */
    public void deliver(final String identifier,
                        final List<String> lockedDocs,
                        final List<PreDeliveryDocumentDTO> metaDatas,
                        final boolean createDocs,
                        final List<String> prefixToExclude) throws PgcnTechnicalException {

        final CustomUserDetails user = SecurityUtils.getCurrentUser();
        final String libraryId = user != null ? user.getLibraryId()
                                              : null;

        // Synchrone
        if (createDocs) {
            final List<DocUnit> docs = deliveryProcessService.createDocs(identifier, metaDatas, prefixToExclude);
            esDocUnitService.indexAsync(docs.stream().map(DocUnit::getIdentifier).collect(Collectors.toList()));
        }
        final DeliveryProcessResults processElement = deliveryProcessService.deliver(identifier, lockedDocs, prefixToExclude, metaDatas, libraryId);

        // Asynchrone
        esDeliveryService.indexAsync(identifier);
        deliveryAsyncService.processDelivery(identifier, processElement);
    }

    @Transactional
    public DeliveryDTO create(final ManualDeliveryDTO request) throws PgcnValidationException {
        validate(request);
        final Delivery delivery = new Delivery();
        uiDeliveryMapper.mapInto(request, delivery);
        final Delivery savedLot = deliveryService.save(delivery);
        final Delivery deliveryWithProperties = deliveryService.getOne(savedLot.getIdentifier());
        return DeliveryMapper.INSTANCE.deliveryToDeliveryDTO(deliveryWithProperties);
    }

    @Transactional(readOnly = true)
    public List<SimpleDeliveryDTO> findPreviousDeliveriesForCheckSlips(final String digitalDocId) {
        final List<SimpleDeliveryDTO> previousDeliveriesForSlips = new ArrayList<>();
        final List<DeliveredDocument> slips = deliveryService.findDeliveredWithCheckSlipsByDigitalDoc(digitalDocId);
        if (CollectionUtils.isNotEmpty(slips) && slips.size() > 1) {
            slips.subList(1, slips.size()).stream().filter(doc -> DigitalDocumentStatus.REJECTED.equals(doc.getStatus()) && doc.getCheckSlip() != null).forEach(doc -> {
                final SimpleDeliveryDTO dto = new SimpleDeliveryDTO();
                dto.setIdentifier(doc.getDelivery().getIdentifier());
                dto.setLabel(doc.getDelivery().getLabel());
                dto.setStatus(DeliveryStatus.REJECTED);
                previousDeliveriesForSlips.add(dto);
            });
        }
        return previousDeliveriesForSlips;
    }

    /**
     * Validation des champs unique au niveau du DTO avant le merge
     */
    private PgcnList<PgcnError> validate(final ManualDeliveryDTO dto) throws PgcnValidationException {
        final PgcnList<PgcnError> errors = new PgcnList<>();
        final PgcnError.Builder builder = new PgcnError.Builder();

        // Le label est unique
        if (StringUtils.isNotBlank(dto.getLabel())) {
            final Delivery duplicate = deliveryService.findOneByLabel(dto.getLabel());

            if (duplicate != null && (dto.getIdentifier() == null || !duplicate.getIdentifier().equalsIgnoreCase(dto.getIdentifier()))) {
                errors.add(builder.reinit().setCode(PgcnErrorCode.DELIVERY_DUPLICATE_LABEL).setField("label").build());
            }
        }
        // Le lot est obligatoire
        if (dto.getLot() == null) {
            errors.add(builder.reinit().setCode(PgcnErrorCode.DELIVERY_LOT_MANDATORY).setField("lot").build());
        }

        // Retour
        if (!errors.isEmpty()) {
            dto.setErrors(errors);
            throw new PgcnValidationException(dto, errors);
        }
        return errors;
    }

    @Transactional(readOnly = true)
    public List<SimpleDeliveryDTO> findByLot(final String lotId) {
        final List<Delivery> deliveries = deliveryService.findByLot(lotId);
        return deliveries.stream().map(DeliveryMapper.INSTANCE::deliveryToSimpleDeliveryDTO).collect(Collectors.toList());
    }

    /**
     * Recherche paramétrée
     */
    public Page<SimpleDeliveryDTO> search(final String search,
                                          final List<String> libraries,
                                          final List<String> projects,
                                          final List<String> lots,
                                          final List<String> providers,
                                          final List<DeliveryStatus> status,
                                          final LocalDate dateFrom,
                                          final LocalDate dateTo,
                                          final Integer page,
                                          final Integer size) {

        final Page<Delivery> deliveries = deliveryService.search(search, libraries, projects, lots, providers, status, dateFrom, dateTo, page, size);
        return deliveries.map(DeliveryMapper.INSTANCE::deliveryToSimpleDeliveryDTO);
    }

    @Transactional(readOnly = true)
    public List<AuditDeliveryRevisionDTO> getDeliveriesForWidget(final LocalDate fromDate,
                                                                 final List<String> libraries,
                                                                 final List<String> projects,
                                                                 final List<String> lots,
                                                                 final List<Delivery.DeliveryStatus> status,
                                                                 final boolean sampled) {

        final List<Delivery> deliveries = deliveryService.findDeliveriesForWidget(fromDate, libraries, projects, lots, status, sampled);
        final List<AuditDeliveryRevisionDTO> revs = new ArrayList<>();

        deliveries.stream().filter(del -> accessHelper.checkDelivery(del.getIdentifier())).forEach(del -> {
            final AuditDeliveryRevisionDTO dto = new AuditDeliveryRevisionDTO();
            dto.setIdentifier(del.getIdentifier());
            dto.setLabel(del.getLabel());
            dto.setLotIdentifier(del.getLot().getIdentifier());
            dto.setLotLabel(del.getLot().getLabel());
            dto.setStatus(del.getStatus());
            dto.setTimestamp(del.getDepositDate().atStartOfDay().toInstant(ZoneOffset.ofTotalSeconds(0)).toEpochMilli());
            revs.add(dto);
        });
        return revs;
    }

    @Transactional(readOnly = true)
    public List<DeliveryDTO> findAllForProject(final String projectId) {
        final List<Delivery> deliveries = deliveryService.findAllByProjectId(projectId);
        return deliveries.stream().map(DeliveryMapper.INSTANCE::deliveryToDeliveryDTO).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public DeliveryDTO findLatestDelivery(final String docUnitId) {
        final Delivery delivery = deliveryService.findLatestDelivery(docUnitId);
        return DeliveryMapper.INSTANCE.deliveryToDeliveryDTO(delivery);
    }

    @Transactional
    public DeliveryDTO duplicate(final String id) {
        final Delivery duplicated = deliveryService.duplicate(id);
        return DeliveryMapper.INSTANCE.deliveryToDeliveryDTO(duplicated);
    }

    @Transactional(readOnly = true)
    public CheckConfigurationDTO getActiveCheckConfiguration(final String deliveryId) {
        final Delivery deliv = deliveryService.getWithActiveCheckConfiguration(deliveryId);
        final Lot lot = deliv.getLot();
        CheckConfiguration cc = null;

        if (lot != null) {
            cc = lot.getActiveCheckConfiguration();

            if (cc == null) {
                final Project project = lot.getProject();

                if (project != null) {
                    cc = project.getActiveCheckConfiguration();
                }
            }
        }
        return CheckConfigurationMapper.INSTANCE.checkConfigurationToCheckConfigurationDTO(cc);
    }

    @Transactional(readOnly = true)
    public SimpleDeliveryForViewerDTO getSimpleWithLot(final String id) {
        final Delivery delivery = deliveryService.getSimpleWithLot(id);
        return DeliveryMapper.INSTANCE.delivToDtoForViewer(delivery);
    }

    @Transactional(readOnly = true)
    public List<StatisticsProviderDeliveryDTO> getProviderDeliveryStats(final List<String> libraries,
                                                                        final List<String> providers,
                                                                        final LocalDate fromDate,
                                                                        final LocalDate toDate) {
        return deliveryService.findByProviders(libraries,
                                               providers,
                                               Arrays.asList(DeliveryStatus.TO_BE_CONTROLLED,
                                                             DeliveryStatus.DELIVERING,
                                                             DeliveryStatus.DELIVERING_ERROR,
                                                             DeliveryStatus.AUTOMATICALLY_REJECTED),
                                               fromDate,
                                               toDate)
                              .stream()
                              // la livraison a un lot, un projet, une bibliothèque
                              .filter(dlv -> dlv.getLot() != null && dlv.getLot().getProject() != null
                                             && dlv.getLot().getProject().getLibrary() != null)

                              // Regroupement des livraisons par bibliothèque et prestataire
                              .collect(Collectors.groupingBy(dlv -> {
                                  final Lot lot = dlv.getLot();
                                  final Project project = lot.getProject();
                                  final Library library = project.getLibrary();
                                  return Pair.of(library, lot.getProvider());

                              }, Collectors.toList()))
                              .entrySet()
                              .stream()
                              // Pour chaque bibliothèque + prestataire: création d'un DTO
                              .map(e -> {
                                  final Library library = e.getKey().getLeft();
                                  final User provider = e.getKey().getRight();
                                  final List<Delivery> deliveries = e.getValue();

                                  final StatisticsProviderDeliveryDTO dto = new StatisticsProviderDeliveryDTO();
                                  dto.setLibraryIdentifier(library.getIdentifier());
                                  dto.setLibraryName(library.getName());

                                  if (provider != null) {
                                      dto.setProviderIdentifier(provider.getIdentifier());
                                      dto.setProviderLogin(provider.getLogin());
                                      dto.setProviderFullName(provider.getFullName());
                                  }

                                  dto.setNbDelivery(deliveries.size());
                                  dto.setNbLot(deliveries.stream().map(Delivery::getLot).distinct().count());

                                  return dto;

                              })
                              .collect(Collectors.toList());
    }

    /**
     * Retourne le vrai statut de la livraison une fois que tous les documents ont ete livres.
     */
    public Map<String, Object> getDeliveryStatus(final Delivery delivery) {

        final Map<String, Object> response = new HashMap<>();
        response.put("identifier", delivery.getIdentifier());

        final Optional<DeliveredDocument> opt = delivery.getDocuments().stream().filter(doc -> doc.getStatus() == DigitalDocument.DigitalDocumentStatus.DELIVERING).findAny();
        if (opt.isPresent()) {
            response.put("status", DigitalDocument.DigitalDocumentStatus.DELIVERING);
        } else {
            response.put("status", delivery.getStatus());
        }
        return response;
    }

    /**
     * Détache 1 digitalDocument de la livraison.
     * => debloquer la livraison
     * => permettre une relivraison du doc bloqué
     */
    @Transactional
    public DeliveryDTO detachDigitalDoc(final String deliveryId, final DigitalDocumentDTO docToDetach) {

        // suppression du deliveredDocument qui bloque
        deliveryService.deleteDeliveredDocument(deliveryId, docToDetach.getIdentifier());

        // update status of related digital document
        final DigitalDocument digitDoc = digitalDocumentService.findOne(docToDetach.getIdentifier());
        digitDoc.setStatus(DigitalDocumentStatus.CANCELED);
        digitalDocumentService.save(digitDoc);

        // Workflow : retour à un etat d'avant livraison pour le doc bloqué
        final DocUnit docUnit = docUnitService.findOneWithAllDependenciesForWorkflow(docToDetach.getDocUnit().getIdentifier());
        docUnitWorkflowService.resetDeliverStatesForDetachedDoc(docUnit);

        // Voir s'il faut finaliser la livraison ou la laisser en l'état
        final Set<DeliveredDocument> docsToDeliver = deliveryService.getSimpleDigitalDocumentsByDelivery(deliveryId);
        final DeliveredDocument deliveredDoc = docsToDeliver.stream().findFirst().orElse(null);

        if (deliveredDoc == null) {
            // plus rien à livrer
            final Delivery delivery = deliveryService.getOne(deliveryId);
            delivery.setStatus(DeliveryStatus.DELIVERING_ERROR);
            deliveryService.save(delivery);
        } else {
            final String docUnitId = deliveredDoc.getDigitalDocument().getDocUnit().getIdentifier();
            // Tous les docs traités => on doit finaliser les contrôles (bordereau, envoi au prestataire)..
            if (digitalDocumentService.allDeliveryDocsChecked(docsToDeliver, deliveredDoc)) {
                // ok, on doit finaliser
                digitalDocumentService.generateAndSendCheckSlip(docUnitId, Optional.of(deliveredDoc));
                deliveredDoc.getDelivery().setStatus(DeliveryStatus.TREATED);
                digitalDocumentService.save(deliveredDoc.getDigitalDocument());
            }
        }

        return getOne(deliveryId);
    }
}
