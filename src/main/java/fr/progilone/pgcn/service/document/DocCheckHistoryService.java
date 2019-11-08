package fr.progilone.pgcn.service.document;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import fr.progilone.pgcn.domain.delivery.DeliveredDocument;
import fr.progilone.pgcn.domain.delivery.Delivery;
import fr.progilone.pgcn.domain.document.DigitalDocument;
import fr.progilone.pgcn.domain.document.DigitalDocument.DigitalDocumentStatus;
import fr.progilone.pgcn.domain.document.DocCheckHistory;
import fr.progilone.pgcn.domain.document.DocUnit;
import fr.progilone.pgcn.domain.document.PhysicalDocument;
import fr.progilone.pgcn.domain.dto.statistics.WorkflowUserProgressDTO;
import fr.progilone.pgcn.domain.lot.Lot;
import fr.progilone.pgcn.domain.train.Train;
import fr.progilone.pgcn.domain.user.User;
import fr.progilone.pgcn.domain.workflow.DocUnitState;
import fr.progilone.pgcn.domain.workflow.DocUnitWorkflow;
import fr.progilone.pgcn.domain.workflow.WorkflowStateKey;
import fr.progilone.pgcn.domain.workflow.WorkflowStateStatus;
import fr.progilone.pgcn.repository.document.DocCheckHistoryRepository;
import fr.progilone.pgcn.repository.document.DocUnitRepository;
import fr.progilone.pgcn.repository.lot.LotRepository;
import fr.progilone.pgcn.service.user.UserService;


@Service
public class DocCheckHistoryService {
    
    private static final Logger LOG = LoggerFactory.getLogger(DocCheckHistoryService.class);
    
    private final DocCheckHistoryRepository repository;
    private final DocUnitRepository docUnitRepository;
    private final LotRepository lotRepository;
    private final UserService userService;

    
    @Autowired
    public DocCheckHistoryService(final DocCheckHistoryRepository repository, final DocUnitRepository docUnitRepository, final LotRepository lotRepository, final UserService userService) {
        this.repository = repository;
        this.docUnitRepository = docUnitRepository;
        this.lotRepository = lotRepository;
        this.userService = userService;
    }
    
    
    /**
     * initialize check history line when doc checking starts. 
     * 
     * @param docUnit
     * @return
     */
    @Transactional
    public DocCheckHistory create(final String docUnitId) {
        
        final DocUnit docUnit = docUnitRepository.findOneWithAllDependenciesForHistory(docUnitId);
        
        final DocCheckHistory histo = new DocCheckHistory();
        histo.setPgcnId(docUnit.getPgcnId());
        histo.setLibraryId(docUnit.getLibrary().getIdentifier());
        histo.setLibraryLabel(docUnit.getLibrary().getName());
        histo.setProjectId(docUnit.getProject().getIdentifier());
        histo.setProjectLabel(docUnit.getProject().getName());
        histo.setLotId(docUnit.getLot().getIdentifier());
        histo.setLotLabel(docUnit.getLot().getLabel());
        
        final PhysicalDocument physDoc = docUnit.getPhysicalDocuments().stream().findFirst().orElse(null);
        if (physDoc != null && physDoc.getTrain() != null) {
            histo.setTrainId(physDoc.getTrain().getIdentifier());
            histo.setTrainLabel(physDoc.getTrain().getLabel());
        }
        final DigitalDocument ddoc = docUnit.getDigitalDocuments().stream().findFirst().orElse(null);
        if (ddoc != null) {
            if (ddoc.getTotalLength() != null) {
                histo.setTotalLength(ddoc.getTotalLength());
                histo.setPageNumber(ddoc.getNbPages());
            }

            final DeliveredDocument lastDeliveredDoc = ddoc.getDeliveries()
                    .stream()
                    .sorted(Collections.reverseOrder(Comparator.nullsLast(Comparator.comparing(DeliveredDocument::getCreatedDate))))
                    .findFirst().orElse(null);
            if (lastDeliveredDoc != null) {
                histo.setStatus(lastDeliveredDoc.getStatus());
                histo.setDeliveryId(lastDeliveredDoc.getDelivery().getIdentifier());
                histo.setDeliveryLabel(lastDeliveredDoc.getDelivery().getLabel());
            } else {
                return null;
            }
        } else {
            return null;
        }
        
        histo.setStartCheckDate(LocalDateTime.now());
        return repository.save(histo);
    }
    
    /**
     * Creation nouvelle ligne d'historique si pre-rejet ou pre-validation. 
     * 
     * 
     * @param digDoc
     * @param user
     * @return
     */
    private DocCheckHistory create(final DigitalDocument digDoc, final User user, final Optional<DeliveredDocument> lastDeliveredDoc, final Train train) {
        
        final DocUnit docUnit = digDoc.getDocUnit();
        
        final DocCheckHistory histo = new DocCheckHistory();
        histo.setPgcnId(docUnit.getPgcnId());
        histo.setProjectId(docUnit.getProject().getIdentifier());
        histo.setLibraryId(docUnit.getLibrary().getIdentifier());
        histo.setLibraryLabel(docUnit.getLibrary().getName());
        histo.setProjectLabel(docUnit.getProject().getName());
        histo.setLotId(docUnit.getLot().getIdentifier());
        histo.setLotLabel(docUnit.getLot().getLabel());
        if (train != null) {
            histo.setTrainId(train.getIdentifier());
            histo.setTrainLabel(train.getLabel());
        }
        if (user != null) {
            histo.setUserLogin(user.getLogin());
        }
        
        if (lastDeliveredDoc.isPresent()) {
            histo.setDeliveryId(lastDeliveredDoc.get().getDelivery().getIdentifier());
            histo.setDeliveryLabel(lastDeliveredDoc.get().getDelivery().getLabel());
            histo.setStatus(lastDeliveredDoc.get().getStatus());   
        } else {
            return null;
        }
        
        histo.setTotalLength(digDoc.getTotalLength());
        histo.setPageNumber(digDoc.getNbPages());
        histo.setStartCheckDate(LocalDateTime.now());
        return repository.save(histo);
    }
    
    
    @Transactional
    public void update(final DigitalDocument digDoc, final User user, final Optional<DeliveredDocument> lastDeliveredDoc) {
               
        lastDeliveredDoc.ifPresent(delivered -> {
            
            final DocUnit docUnit = digDoc.getDocUnit();     
            final Train train = docUnit.getPhysicalDocuments().stream().findFirst()
                                    .map(PhysicalDocument::getTrain)
                                    .orElse(null);
            final Delivery delivery = delivered.getDelivery();
            if (delivery == null) {
                // ne devrait pas arriver mais ....
                return;
            }
            
            // retrieve DocCheckHistory
            final List<DocCheckHistory> histories;
            if (train == null) {
                histories = repository.findByPgcnIdAndLibraryIdAndProjectIdAndLotIdAndDeliveryIdAndEndCheckDateIsNull(docUnit.getPgcnId(), 
                                                                                    docUnit.getLibrary().getIdentifier(), 
                                                                                    docUnit.getProject().getIdentifier(), 
                                                                                    docUnit.getLot().getIdentifier(), 
                                                                                    delivery.getIdentifier());
            } else {
                histories = repository.findByPgcnIdAndLibraryIdAndProjectIdAndLotIdAndTrainIdAndDeliveryIdAndEndCheckDateIsNull(docUnit.getPgcnId(), 
                                                                                                docUnit.getLibrary().getIdentifier(), 
                                                                                                docUnit.getProject().getIdentifier(), 
                                                                                                docUnit.getLot().getIdentifier(),
                                                                                                train.getIdentifier(),
                                                                                                delivery.getIdentifier());
            }
            // finalise et persiste la ligne d'historique
            histories.stream().findFirst()
                                .ifPresent(histo -> {
                histo.setEndCheckDate(LocalDateTime.now());
                if (user != null) {
                    histo.setUserLogin(user.getLogin());
                }
                histo.setStatus(digDoc.getStatus());
                repository.save(histo);
            });
            
            // Selon le status, il faut creer 1 autre ligne dupliquée 
            if (digDoc.getStatus() == DigitalDocumentStatus.PRE_REJECTED 
                    || digDoc.getStatus() == DigitalDocumentStatus.PRE_VALIDATED) { 
                // creer 1 nvelle ligne dupliquee avec dt deb ctl
                create(digDoc, user, lastDeliveredDoc, train);
            }
            
        });    
        
    }
    
    
    /**
     * Reprise initiale de données pour alimenter la table d'historique des controles.
     * 
     * @param libraryId
     */
    @Transactional
    public void retrieveCheckHistory(final String libraryId) {
         
        final Set<String> idLots = lotRepository.findAllByProjectLibraryIdentifierIn(Arrays.asList(libraryId))
                                                .stream().map(Lot::getIdentifier).collect(Collectors.toSet());
    
        idLots.forEach(idLot -> {
            final Set<String> docIds = docUnitRepository.findAllByLotIdentifier(idLot)
                                        .stream().map(DocUnit::getIdentifier).collect(Collectors.toSet());
            
            LOG.info("Init History - lot {}", idLot);
            
            docIds.forEach(id -> {
                
                // recup les docUnits avec workflow
                // et controle auto FINISHED
                final DocUnit du = docUnitRepository.findOneWithAllDependenciesForHistory(id);
                final DocUnitWorkflow wkf = du.getWorkflow();
                
                if (wkf != null) {
                    
                    final Optional<DocUnitState> autoCtrlState = wkf.getByKey(WorkflowStateKey.CONTROLES_AUTOMATIQUES_EN_COURS).stream().findFirst();
                    
                    if (autoCtrlState.isPresent() && autoCtrlState.get().isDone() ) {
                        
                        final List<DocUnitState> relivs = wkf.getByKey(WorkflowStateKey.RELIVRAISON_DOCUMENT_EN_COURS).stream()
                                                    .sorted(Comparator.nullsLast(Comparator.comparing(DocUnitState::getStartDate)))
                                                    .collect(Collectors.toList());
                        
                        final Optional<DocUnitState> ctrlQualiteState = wkf.getByKey(WorkflowStateKey.CONTROLE_QUALITE_EN_COURS).stream().findFirst();
                        final Optional<DocUnitState> preRejectState = wkf.getByKey(WorkflowStateKey.PREREJET_DOCUMENT).stream().findFirst();
                        final Optional<DocUnitState> preValidState = wkf.getByKey(WorkflowStateKey.PREVALIDATION_DOCUMENT).stream().findFirst();
                        final Optional<DocUnitState> validationState = wkf.getByKey(WorkflowStateKey.VALIDATION_DOCUMENT).stream().findFirst();
                        
                        //************************
                        // CAS 1: 1 seule livraison - pas d'etape de relivraison
                        if (relivs.isEmpty()) {

                            // line 1 ds tous les cas
                            final DocCheckHistory line1 = create(id);
                            if (line1 != null) {
                                
                                if (autoCtrlState.get().isRejected()) {
                                    line1.setStartCheckDate(autoCtrlState.get().getStartDate());
                                    line1.setEndCheckDate(autoCtrlState.get().getEndDate());
                                    line1.setStatus(DigitalDocumentStatus.REJECTED);
                                    repository.save(line1);
                                
                                } else if (ctrlQualiteState.isPresent() && !ctrlQualiteState.get().isDone()) {
                                    line1.setStartCheckDate(ctrlQualiteState.get().getStartDate());
                                    line1.setStatus(DigitalDocumentStatus.CHECKING);
                                    repository.save(line1);
                                } 
                                else if (preRejectState.isPresent() && preRejectState.get().isDone()) {
                                    // PRE REJET  -> 2eme LIGNE HISTORY
                                    // ligne 2 : dt deb = dt deb etape validation et status = VALIDATED ou REJECTED selon state validation doc = FINISHED ou FAILED
                                    line1.setStartCheckDate(autoCtrlState.get().getEndDate());
                                    line1.setEndCheckDate(preRejectState.get().getEndDate());
                                    line1.setStatus(DigitalDocumentStatus.PRE_REJECTED);
                                    line1.setUserLogin(preRejectState.get().getUser());
                                    repository.save(line1);
                                    
                                    final DocCheckHistory line2 = create(id);
                                    if (line2 != null) {
                                        if (validationState.isPresent()) {
                                            line2.setStartCheckDate(validationState.get().getStartDate());
                                            
                                            if (validationState.get().isDone()) {
                                                line2.setEndCheckDate(validationState.get().getEndDate());
                                                line2.setUserLogin(validationState.get().getUser());
                                                if (WorkflowStateStatus.FINISHED == validationState.get().getStatus()) {
                                                    line2.setStatus(DigitalDocumentStatus.VALIDATED);
                                                } else if (WorkflowStateStatus.FAILED == validationState.get().getStatus()) {
                                                    line2.setStatus(DigitalDocumentStatus.REJECTED);
                                                }
                                            } else {
                                                line2.setStatus(DigitalDocumentStatus.CHECKING);
                                            } 
                                        }
                                        repository.save(line2);
                                    }
                                    
                                } else if (preValidState.isPresent() && preValidState.get().isDone()) {
                                    // PRE VALIDATION -> 2eme LIGNE HISTORY
                                    // ligne 1 : dt fin ctrl = dt fin de la state pre validation et status = PRE VALIDATED et status = VALIDATED ou REJECTED selon state validation doc = FINISHED ou FAILED  
                                    line1.setEndCheckDate(preValidState.get().getEndDate());
                                    line1.setStatus(DigitalDocumentStatus.PRE_VALIDATED);
                                    line1.setUserLogin(preValidState.get().getUser());
                                    repository.save(line1);
                                    
                                    final DocCheckHistory line2 = create(id);
                                    if (line2 != null) {
                                        if (validationState.isPresent()) {
                                            line2.setStartCheckDate(validationState.get().getStartDate());
                                            
                                            if (validationState.get().isDone()) {
                                                line2.setEndCheckDate(validationState.get().getEndDate());
                                                line2.setUserLogin(validationState.get().getUser());
                                                if (WorkflowStateStatus.FINISHED == validationState.get().getStatus()) {
                                                    line2.setStatus(DigitalDocumentStatus.VALIDATED);
                                                } else if (WorkflowStateStatus.FAILED == validationState.get().getStatus()) {
                                                    line2.setStatus(DigitalDocumentStatus.REJECTED);
                                                }
                                            } else {
                                                line2.setStatus(DigitalDocumentStatus.CHECKING);
                                            } 
                                        }
                                        repository.save(line2);
                                    }
                                    
                                    
                                } else if (validationState.isPresent()) {
                                    // VALIDATION SIMPLE -> 1 seule ligne
                                    line1.setStartCheckDate(validationState.get().getStartDate());
                                    
                                    if (validationState.get().isDone()) {
                                        line1.setEndCheckDate(validationState.get().getEndDate());
                                        line1.setUserLogin(preValidState.get().getUser());
                                        
                                        if (WorkflowStateStatus.FINISHED == validationState.get().getStatus()) {
                                            line1.setStatus(DigitalDocumentStatus.VALIDATED);
                                        } else if (WorkflowStateStatus.FAILED == validationState.get().getStatus()) {
                                            line1.setStatus(DigitalDocumentStatus.REJECTED);
                                        }
                                        
                                    } else {
                                        line1.setStatus(DigitalDocumentStatus.CHECKING);
                                    }
                                    repository.save(line1);
                                }
                            }
                            
                        } 
                        //************************
                        // CAS 2: 1 ou plusieurs relivraisons... !!! De ttes façons, on a perdu les infos intermédiaires !!!
                        // on cree 1 ligne1 pour la livraison initiale rejetee
                        // et une ligne 2 (et 3 eventuelle) pour le dernier controle qualité
                        else {
                            
                            // livraison initiale rejetée
                            final Optional<DocUnitState> deliveryState = wkf.getByKey(WorkflowStateKey.LIVRAISON_DOCUMENT_EN_COURS).stream().findFirst();
                            final Optional<DocUnitState> controlState = wkf.getByKey(WorkflowStateKey.CONTROLE_QUALITE_EN_COURS).stream().findFirst();
                            final DocUnitState firstRelivState = relivs.get(0);  // 1ere relivraison

                            // line 1 - rejet livraison initiale
                            if (deliveryState.isPresent()) {
                                final DocCheckHistory line1 = create(id);
                                if (line1 != null) {
                                    line1.setStartCheckDate(deliveryState.get().getEndDate());
                                    line1.setStatus(DigitalDocumentStatus.REJECTED);
                                    // on a perdu les vraies dates, et le login du 1er controle, donc on fait ce qu'on peut....
                                    line1.setEndCheckDate(firstRelivState.getStartDate());
                                    if (controlState.isPresent() && StringUtils.isNotBlank(controlState.get().getUser())) {
                                        line1.setUserLogin(controlState.get().getUser());
                                    } else {
                                        line1.setUserLogin(firstRelivState.getUser());
                                    }
                                    
                                    repository.save(line1);
                                }
                            }
                            
                            if (preRejectState.isPresent() && preRejectState.get().isDone()) {
                                // PRE REJET  -> 2eme LIGNE HISTORY
                                final DocCheckHistory line2 = create(id);
                                if (line2 != null) {
                                    line2.setStartCheckDate(preRejectState.get().getStartDate());
                                    line2.setEndCheckDate(preRejectState.get().getEndDate());
                                    line2.setUserLogin(preRejectState.get().getUser());
                                    line2.setStatus(DigitalDocumentStatus.PRE_REJECTED);
                                    repository.save(line2);
                                }
                            }  else if (preValidState.isPresent() && preValidState.get().isDone()) {
                                // PRE VALIDATION  -> 2eme LIGNE HISTORY
                                final DocCheckHistory line2 = create(id);
                                if (line2 != null) {
                                    line2.setStartCheckDate(preValidState.get().getStartDate());
                                    line2.setEndCheckDate(preValidState.get().getEndDate());
                                    line2.setUserLogin(preValidState.get().getUser());
                                    line2.setStatus(DigitalDocumentStatus.PRE_VALIDATED);
                                    repository.save(line2);
                                }
                            }
                            
                            if (validationState.isPresent()) {
                                // et 1 ligne pour la validation definitive
                                final DocCheckHistory line3 = create(id);
                                if (line3 != null) {
                                    line3.setStartCheckDate(validationState.get().getStartDate());
                                    if (validationState.get().isDone()) {
                                        line3.setEndCheckDate(validationState.get().getEndDate());
                                        line3.setUserLogin(validationState.get().getUser());
                                        
                                        if (WorkflowStateStatus.FAILED == validationState.get().getStatus()) {
                                            line3.setStatus(DigitalDocumentStatus.REJECTED);
                                        } else {
                                            line3.setStatus(DigitalDocumentStatus.VALIDATED);
                                        }
                                        
                                    }
                                    repository.save(line3);
                                }
                            }
                        }
                        
                    }

                }
                

            });
            
            
        });
        LOG.info("Fin traitement reprise données DOC_CHECK_HISTORY pour la lib {}", libraryId);
    }
    
    
    
    @Transactional(readOnly = true)
    public Collection<WorkflowUserProgressDTO> getWorkflowUsersStatistics(final List<String> libraries,
                                                                          final List<String> projects,
                                                                          final List<String> lots,
                                                                          final List<String> deliveries,
                                                                          final LocalDate fromDate,
                                                                          final LocalDate toDate) {
        
        final List<DocCheckHistory> histories = repository.findDocCheckHistories(libraries, projects, lots, deliveries, fromDate, toDate);
        
        return histories.stream()
                    .filter(histo -> DigitalDocumentStatus.DELIVERING != histo.getStatus())
                    .filter(histo -> DigitalDocumentStatus.CHECKING != histo.getStatus())
                    .map(histo -> new LibraryUserProgressRegroup(histo.getLibraryId(), histo.getUserLogin(), histo))
                    // regroupement par [library + user] identiques
                    .collect(Collectors.groupingBy(LibraryUserProgressRegroup::getKey,
                                               Collectors.collectingAndThen(Collectors.toList(), list -> {
                                                   
                                                   final String library = list.get(0).getLibrary();
                                                       
                                                   final String userLogin = list.get(0).getUser();
                                                   final User user = StringUtils.isNotBlank(userLogin) ?
                                                                     userService.findByLogin(userLogin) :
                                                                     null;
                                                   final DocCheckHistory dch = list.get(0).getHistory();                  

                                                   // construction des DTOs
                                                   final WorkflowUserProgressDTO dto = new WorkflowUserProgressDTO();
                                                   // library
                                                   dto.setLibraryIdentifier(library);
                                                   dto.setLibraryName(dch.getLibraryLabel());
                                                   // user
                                                   dto.setUserLogin(userLogin);
                                                   if (user != null) {
                                                       dto.setUserIdentifier(user.getIdentifier());
                                                       dto.setUserFullName(user.getFullName());
                                                   }
                                                   // calcul des stats
                                                   setUserProgressDTO(dto, list);

                                                   return dto;
                                               })))
                .values();
        
    }
    
    
    private void setUserProgressDTO(final WorkflowUserProgressDTO dto, final List<LibraryUserProgressRegroup> list) {
        
        // Nombre d'unités documentaires contrôlées  
        dto.setNbDocUnit(list.stream().map(g -> g.getHistory().getPgcnId()).distinct().count());
        // pre-rejets
        final long nbPreRej = list.stream()
                .filter(g -> DigitalDocumentStatus.PRE_REJECTED == g.getHistory().getStatus())
                .filter(g -> g.getHistory().getEndCheckDate() != null)
                .map(g ->  g.getHistory().getPgcnId())
                .distinct().count();
        // pre-validations
        final long nbPreVal = list.stream()
                .filter(g -> DigitalDocumentStatus.PRE_VALIDATED == g.getHistory().getStatus())
                .filter(g -> g.getHistory().getEndCheckDate() != null)
                .map(g ->  g.getHistory().getPgcnId())
                .distinct().count();
        // rejets
        final long nbRej = list.stream()
                .filter(g -> DigitalDocumentStatus.REJECTED == g.getHistory().getStatus())
                .filter(g -> g.getHistory().getEndCheckDate() != null)
                .map(g ->  g.getHistory().getPgcnId())
                .distinct().count();
        // validations
        final long nbVal = list.stream()
                                .filter(g -> DigitalDocumentStatus.VALIDATED == g.getHistory().getStatus())
                                .filter(g -> g.getHistory().getEndCheckDate() != null)
                                .map(g -> g.getHistory().getPgcnId())
                                .distinct().count();
        
        dto.setNbRejectedDocUnit(nbRej);
        dto.setNbValidatedDocUnit(nbVal);
        dto.setNbPreRejectedDocUnit(nbPreRej);
        dto.setNbPreValidatedDocUnit(nbPreVal);
        
        // Nombre moyen de pages        
        list.stream()
            .mapToInt(g-> g.getHistory().getPageNumber() != null ? g.getHistory().getPageNumber() : 0)
            .average()
            .ifPresent(avg -> dto.setAvgTotalPages((int) avg));

        // Délai moyen de contrôle
        list.stream()
            .map(LibraryUserProgressRegroup::getHistory)
            .filter(h -> h.getStartCheckDate() != null && h.getEndCheckDate() != null)
            .mapToLong(histo -> histo.getStartCheckDate().until(histo.getEndCheckDate(), ChronoUnit.SECONDS))
            .average()
            .ifPresent(avg -> dto.setAvgDuration((long) avg));
    }

    
    
    /**
     * Regroupement des stats par library / user
     */
    private static final class LibraryUserProgressRegroup {

        final String library;
        final String user;
        final DocCheckHistory history;

        private LibraryUserProgressRegroup(final String library, final String user, final DocCheckHistory history) {
            this.library = library;
            this.user = user;
            this.history = history;
        }

        public String getLibrary() {
            return library;
        }

        public String getUser() {
            return user;
        }

        public String getKey() {
            return library + "#" + user;
        }
        
        public DocCheckHistory getHistory() {
            return history;
        }
        
        
    }
}
