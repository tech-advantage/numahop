package fr.progilone.pgcn.service.document.ui;

import java.text.NumberFormat;
import java.time.LocalDate;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import fr.progilone.pgcn.domain.delivery.DeliveredDocument;
import fr.progilone.pgcn.domain.delivery.Delivery;
import fr.progilone.pgcn.domain.document.DigitalDocument;
import fr.progilone.pgcn.domain.document.DigitalDocument.DigitalDocumentStatus;
import fr.progilone.pgcn.domain.document.DocUnit;
import fr.progilone.pgcn.domain.document.conditionreport.ConditionReportDetail;
import fr.progilone.pgcn.domain.dto.document.DigitalDocumentDTO;
import fr.progilone.pgcn.domain.dto.document.LightDeliveredDigitalDocDTO;
import fr.progilone.pgcn.domain.dto.document.SimpleDigitalDocumentDTO;
import fr.progilone.pgcn.domain.dto.document.SimpleDocPageDTO;
import fr.progilone.pgcn.domain.dto.document.SimpleListDigitalDocumentDTO;
import fr.progilone.pgcn.domain.dto.document.conditionreport.LightCondReportDetailDTO;
import fr.progilone.pgcn.domain.dto.sample.SampleDTO;
import fr.progilone.pgcn.domain.user.User;
import fr.progilone.pgcn.domain.util.CustomUserDetails;
import fr.progilone.pgcn.domain.workflow.WorkflowStateKey;
import fr.progilone.pgcn.repository.delivery.DeliveryRepository;
import fr.progilone.pgcn.security.SecurityUtils;
import fr.progilone.pgcn.service.document.DigitalDocumentService;
import fr.progilone.pgcn.service.document.DocCheckHistoryService;
import fr.progilone.pgcn.service.document.DocUnitService;
import fr.progilone.pgcn.service.document.conditionreport.ConditionReportDetailService;
import fr.progilone.pgcn.service.document.mapper.DigitalDocumentMapper;
import fr.progilone.pgcn.service.document.mapper.DocPageMapper;
import fr.progilone.pgcn.service.document.mapper.UIDigitalDocumentMapper;
import fr.progilone.pgcn.service.sample.SampleService;
import fr.progilone.pgcn.service.user.UserService;
import fr.progilone.pgcn.service.util.SortUtils;
import fr.progilone.pgcn.service.util.transaction.VersionValidationService;
import fr.progilone.pgcn.service.workflow.WorkflowService;

/**
 * Service dédié à les gestion des vues des documents numériques
 *
 * @author jbrunet
 */
@Service
public class UIDigitalDocumentService {

    private final DigitalDocumentService digitalDocumentService;
    private final UIDigitalDocumentMapper uiDigitalDocumentMapper;
    private final ConditionReportDetailService reportDetailService; 
    private final SampleService sampleService;
    private final UserService userService;
    private final WorkflowService workflowService;
    private final DocUnitService docUnitService;
    private final DeliveryRepository deliveryRepository;
    private final DocCheckHistoryService docCheckHistoryService;

    private final NumberFormat numFormatter = NumberFormat.getNumberInstance(Locale.FRENCH);
    
    @Autowired
    public UIDigitalDocumentService(final DigitalDocumentService digitalDocumentService, 
                                    final UIDigitalDocumentMapper uiDigitalDocumentMapper,
                                    final ConditionReportDetailService reportDetailService,
                                    final SampleService sampleService,
                                    final UserService userService,
                                    final WorkflowService workflowService,
                                    final DocUnitService docUnitService,
                                    final DeliveryRepository deliveryRepository,
                                    final DocCheckHistoryService docCheckHistoryService) {
        this.digitalDocumentService = digitalDocumentService;
        this.uiDigitalDocumentMapper = uiDigitalDocumentMapper;
        this.reportDetailService = reportDetailService;
        this.sampleService = sampleService;
        this.workflowService = workflowService;
        this.docUnitService = docUnitService;
        this.deliveryRepository = deliveryRepository;
        this.userService = userService;
        this.docCheckHistoryService = docCheckHistoryService;
    }

    /**
     * Retourne un document avec ses pages
     *
     * @param identifier
     * @return
     */
    @Transactional(readOnly = true)
    public DigitalDocumentDTO getDigitalDocumentWithNumberOfPages(final String identifier) {
        final DigitalDocument dd = digitalDocumentService.getOneWithPages(identifier);
        return DigitalDocumentMapper.INSTANCE.digitalDocumentToDigitalDocumentDTO(dd);
    }

    @Transactional
    public DigitalDocumentDTO update(final DigitalDocumentDTO dto) {
        final DigitalDocument doc = digitalDocumentService.findOne(dto.getIdentifier());

        // Contrôle d'accès concurrents
        VersionValidationService.checkForStateObject(doc, dto);

        uiDigitalDocumentMapper.mapInto(dto, doc);

        final DigitalDocument savedDoc = digitalDocumentService.save(doc);
        return getDigitalDocumentWithNumberOfPages(savedDoc.getIdentifier());
    }

    /**
     * Retourne la liste des documents à contrôler
     *
     * @return
     */
    @Transactional(readOnly = true)
    public Collection<SimpleDigitalDocumentDTO> getAllDocumentsToCheck() {
        final Set<DigitalDocument> digitalDocuments = digitalDocumentService.getAllDocumentsToCheck();
        return digitalDocuments.stream().map(DigitalDocumentMapper.INSTANCE::digitalDocumentToSimpleDigitalDocumentDTO).collect(Collectors.toSet());
    }

    /**
     * Récupère une page
     *
     * @param identifier
     * @param pageNumber
     * @return
     */
    @Transactional(readOnly = true)
    public SimpleDocPageDTO getPage(final String identifier, final int pageNumber) {
        return DocPageMapper.INSTANCE.docPageToSimpleDocPageDTO(digitalDocumentService.getPageByOrder(identifier, pageNumber));
    }

    /**
     * Recherche par filtres.
     *
     * @param search
     * @param status
     * @param filteredLibraries
     * @param projects
     * @param lots
     * @param trains
     * @param libraries
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
     * @param validated
     *@param page
     * @param size
     * @param sorts    @return
     */
    @Transactional(readOnly = true)
    public Page<SimpleListDigitalDocumentDTO> search(final String search,
                                                     final List<String> status,
                                                     final List<String> filteredLibraries,
                                                     final List<String> projects,
                                                     final List<String> lots,
                                                     final List<String> trains,
                                                     final List<String> libraries,
                                                     final LocalDate dateFrom,
                                                     final LocalDate dateTo,
                                                     final LocalDate dateLimitFrom,
                                                     final LocalDate dateLimitTo,
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
                                                     final Integer page,
                                                     final Integer size,
                                                     final List<String> sorts) {

        final Sort sort = SortUtils.getSort(sorts);
        final Pageable pageRequest = new PageRequest(page, size, sort);

        // Statut en attente de relivraison
        boolean relivraison = false;
        if(status != null && status.contains("RELIVRAISON_DOCUMENT_EN_COURS")){
            relivraison = true;
            // Le statut en attente de relivraison n'est pas un statut de document numérique de l'unité documentaire
            status.remove("RELIVRAISON_DOCUMENT_EN_COURS");
        }

        final Page<DigitalDocument> docs = digitalDocumentService.search(search, status, filteredLibraries, projects, lots, trains, libraries,
                                                                         dateFrom, dateTo, dateLimitFrom, dateLimitTo, relivraison,
                                                                         searchPgcnId, searchTitre, searchRadical, searchFileFormats, searchMaxAngles,
                                                                         searchPageFrom, searchPageTo, searchPageCheckFrom, searchPageCheckTo, searchMinSize, searchMaxSize, validated, pageRequest);
        

        final Page<SimpleListDigitalDocumentDTO> docDtos = docs.map(DigitalDocumentMapper.INSTANCE::digitalDocumentToSimpleListDigitalDocumentDTO);
        docDtos.forEach(doc-> {
            final Optional<ConditionReportDetail> det = reportDetailService.getLastDetailByDocUnitId(doc.getDocUnit().getIdentifier());
            det.ifPresent(conditionReportDetail -> doc.setReportDetail(getLightReportDetail(conditionReportDetail)));
            final List<LightDeliveredDigitalDocDTO> deliveredDigitalDocDTOS = doc.getDeliveries()
                                                                                 .stream()
                                                                                 .sorted(Comparator.comparing(LightDeliveredDigitalDocDTO::getDeliveryDate)
                                                                                                   .reversed())
                                                                                 .collect(Collectors.toList());
            doc.setDeliveries(deliveredDigitalDocDTOS);
        });
        return docDtos;
    }
    
    
    private LightCondReportDetailDTO getLightReportDetail(final ConditionReportDetail detail) {
        final LightCondReportDetailDTO detailDto = new LightCondReportDetailDTO();
        detailDto.setDim1(detail.getDim1());
        detailDto.setDim2(detail.getDim2());
        detailDto.setDim3(detail.getDim3());
        detailDto.setNbViewTotal(detail.getNbViewTotal());
        if (detail.getInsurance() != null ) {
            detailDto.setInsurance(numFormatter.format(detail.getInsurance()));
        } else {
            detailDto.setInsurance("0,00");
        }
        return detailDto;
    }
    
    /**
     * Finalise le process de livraison en cas de rejet automatique.
     *
     * @param delivery
     */
    @Transactional
    public void endAutoChecks(final Delivery delivery) {

        // si livraison rejetee automatiquement (donc tous ses docs sont rejetes)
        if (Delivery.DeliveryStatus.AUTOMATICALLY_REJECTED.equals(delivery.getStatus())) {

            final Optional<DeliveredDocument> oneDoc = delivery.getDocuments().stream().findFirst();
            if (oneDoc.isPresent()) {
                final String docUnitId = oneDoc.get().getDigitalDocument().getDocUnit().getIdentifier();
                digitalDocumentService.generateAndSendCheckSlip(docUnitId, oneDoc);
            }
        }
    }
    
    /**
     * Fin de controle d'un échantillon.
     * 
     * @param identifier
     * @param checksOK
     */
    @Transactional
    public void endChecksForSampling(final String identifier, final boolean checksOK) {
        final SampleDTO sample = sampleService.getOne(identifier);
        sample.getDocuments().forEach(doc->endChecks(doc.getIdentifier(), checksOK));
    }
    
    
    /**
     * Fin de controle d'un document.
     * 
     * @param identifier
     * @param checksOK
     */
    @Transactional
    public void endChecks(final String identifier, final boolean checksOK) {
        
        final DigitalDocument digitalDocument = digitalDocumentService.findOne(identifier);
        final CustomUserDetails currentUser = SecurityUtils.getCurrentUser();
        final String docUnitId = digitalDocument.getDocUnit().getIdentifier();

        final DocUnit docUnit = docUnitService.findOneWithAllDependenciesForWorkflow(docUnitId);
        final User user = userService.findByIdentifier(currentUser.getIdentifier());

        if (checksOK) {  // Accepter
            if(workflowService.isStateRunning(docUnit, WorkflowStateKey.CONTROLE_QUALITE_EN_COURS)) {
                workflowService.processState(docUnit, WorkflowStateKey.CONTROLE_QUALITE_EN_COURS, user);
            }
            if(workflowService.isStateRunning(docUnit, WorkflowStateKey.PREVALIDATION_DOCUMENT)) {
                digitalDocument.setStatus(DigitalDocumentStatus.PRE_VALIDATED);
                workflowService.processState(docUnit, WorkflowStateKey.PREVALIDATION_DOCUMENT, user);
            } else if (workflowService.isStateRunning(docUnit, WorkflowStateKey.VALIDATION_DOCUMENT)) {
                digitalDocument.setStatus(DigitalDocumentStatus.VALIDATED);
                workflowService.processState(docUnit, WorkflowStateKey.VALIDATION_DOCUMENT, user);
            } else {
                digitalDocument.setStatus(DigitalDocumentStatus.VALIDATED);
            }
            
        } else { // Rejeter
            if(workflowService.isStateRunning(docUnit, WorkflowStateKey.CONTROLE_QUALITE_EN_COURS)) {
                workflowService.rejectState(docUnit, WorkflowStateKey.CONTROLE_QUALITE_EN_COURS, user);
            }
            if(workflowService.isStateRunning(docUnit, WorkflowStateKey.PREREJET_DOCUMENT)) {
                digitalDocument.setStatus(DigitalDocumentStatus.PRE_REJECTED);
                workflowService.processState(docUnit, WorkflowStateKey.PREREJET_DOCUMENT, user);
            } else if (workflowService.isStateRunning(docUnit, WorkflowStateKey.VALIDATION_DOCUMENT)) {
                digitalDocument.setStatus(DigitalDocumentStatus.REJECTED);
                workflowService.rejectState(docUnit, WorkflowStateKey.VALIDATION_DOCUMENT, user);
            } else {
                digitalDocument.setStatus(DigitalDocumentStatus.REJECTED);
            }
        }
        
        // on met à jour le doc livré le plus récent
        final Optional<DeliveredDocument> lastDeliveredDoc = digitalDocument.getDeliveries()
                                                                            .stream()
                                                                            .max(Comparator.nullsLast(Comparator.comparing(DeliveredDocument::getCreatedDate)));


        lastDeliveredDoc.ifPresent(doc -> {
                           doc.setStatus(digitalDocument.getStatus());
                           if (DigitalDocumentStatus.PRE_REJECTED != doc.getStatus()) {
                               final Set<DeliveredDocument> documents = deliveryRepository.findSimpleDeliveredDocumentsByDeliveryIdentifier(doc.getDelivery().getIdentifier());
                               if (digitalDocumentService.allDeliveryDocsChecked(documents, doc)) {
                                   doc.getDelivery().setStatus(Delivery.DeliveryStatus.TREATED);
                               }
                           }
                           deliveryRepository.save(doc.getDelivery());
                       });
        
        // MAJ historique.
        docCheckHistoryService.update(digitalDocument, user, lastDeliveredDoc);

        digitalDocumentService.generateAndSendCheckSlip(docUnitId, lastDeliveredDoc);
        digitalDocumentService.save(digitalDocument);
    }
    
}
