package fr.progilone.pgcn.service.workflow;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import fr.progilone.pgcn.domain.document.DocUnit;
import fr.progilone.pgcn.domain.document.DocUnit.State;
import fr.progilone.pgcn.domain.document.conditionreport.ConditionReportDetail.Type;
import fr.progilone.pgcn.domain.lot.Lot;
import fr.progilone.pgcn.domain.lot.Lot.LotStatus;
import fr.progilone.pgcn.domain.project.Project;
import fr.progilone.pgcn.domain.project.Project.ProjectStatus;
import fr.progilone.pgcn.domain.train.Train.TrainStatus;
import fr.progilone.pgcn.domain.user.User;
import fr.progilone.pgcn.domain.workflow.DocUnitState;
import fr.progilone.pgcn.domain.workflow.DocUnitWorkflow;
import fr.progilone.pgcn.domain.workflow.WorkflowGroup;
import fr.progilone.pgcn.domain.workflow.WorkflowModel;
import fr.progilone.pgcn.domain.workflow.WorkflowStateKey;
import fr.progilone.pgcn.domain.workflow.WorkflowStateStatus;
import fr.progilone.pgcn.exception.PgcnBusinessException;
import fr.progilone.pgcn.exception.message.PgcnError;
import fr.progilone.pgcn.exception.message.PgcnErrorCode;
import fr.progilone.pgcn.service.document.BibliographicRecordService;
import fr.progilone.pgcn.service.document.DocCheckHistoryService;
import fr.progilone.pgcn.service.document.DocUnitService;
import fr.progilone.pgcn.service.document.conditionreport.ConditionReportService;
import fr.progilone.pgcn.service.lot.LotService;
import fr.progilone.pgcn.service.project.ProjectService;
import fr.progilone.pgcn.service.user.UserService;
import fr.progilone.pgcn.service.util.NumahopCollectors;

@Service
public class WorkflowService {

    private static final Logger LOG = LoggerFactory.getLogger(WorkflowService.class);

    private final DocUnitWorkflowService docUnitWorkflowService;
    private final WorkflowGroupService workflowGroupService;
    private final DocUnitService docUnitService;
    private final BibliographicRecordService recordService;
    private final UserService userService;
    private final ConditionReportService conditionReportService;
    private final LotService lotService;
    private final ProjectService projectService;
    private final DocCheckHistoryService docCheckHistoryService; 

    @Autowired
    public WorkflowService(final DocUnitWorkflowService docUnitWorkflowService,
                           final WorkflowGroupService workflowGroupService,
                           final DocUnitService docUnitService,
                           final BibliographicRecordService recordService,
                           final UserService userService,
                           final ConditionReportService conditionReportService,
                           final LotService lotService,
                           final ProjectService projectService,
                           final DocCheckHistoryService docCheckHistoryService) {
        this.docUnitWorkflowService = docUnitWorkflowService;
        this.workflowGroupService = workflowGroupService;
        this.docUnitService = docUnitService;
        this.recordService = recordService;
        this.userService = userService;
        this.conditionReportService = conditionReportService;
        this.lotService = lotService;
        this.projectService = projectService;
        this.docCheckHistoryService = docCheckHistoryService;
    }

    /**
     * Initialisation du workflow
     *
     * @param doc
     * @param model
     */
    @Transactional
    public DocUnitWorkflow initializeWorkflow(final DocUnit doc, final WorkflowModel model, LocalDateTime startDate) {
        final DocUnitWorkflow workflow = docUnitWorkflowService.createFromModel(model);
        doc.setWorkflow(workflow);
        workflow.setDocUnit(doc);
        // Date de départ
        if (startDate == null) {
            startDate = LocalDateTime.now();
        }
        // Initialisation des premières étapes
        final DocUnitState stateStart = workflow.getFutureOrRunningByKey(WorkflowStateKey.INITIALISATION_DOCUMENT);
        stateStart.initializeState(startDate, startDate, WorkflowStateStatus.FINISHED);

        // Constats d'état
        final DocUnitState validateConstatState = workflow.getFutureOrRunningByKey(WorkflowStateKey.VALIDATION_CONSTAT_ETAT);
        validateConstatState.initializeState(startDate, null, null);

        // Génération du bordereau
        final DocUnitState generateBordereauState = workflow.getFutureOrRunningByKey(WorkflowStateKey.GENERATION_BORDEREAU);
        generateBordereauState.initializeState(startDate, null, null);
        
        // Validation notice
        final DocUnitState validateBibliographicRecord = workflow.getFutureOrRunningByKey(WorkflowStateKey.VALIDATION_NOTICES);
        validateBibliographicRecord.initializeState(startDate, null, null);
        
        return workflow;
    }

    /**
     * Initialisation du workflow pour les lots numériques
     * Les étapes jusqu'au contrôles sont ignorées
     *
     * @param doc
     * @param model
     */
    @Transactional
    public DocUnitWorkflow initializeWorkflowForDigitalLot(final DocUnit doc, final WorkflowModel model, LocalDateTime startDate) {
        final DocUnitWorkflow workflow = docUnitWorkflowService.createFromModel(model);
        doc.setWorkflow(workflow);
        workflow.setDocUnit(doc);
        // Date de départ
        if (startDate == null) {
            startDate = LocalDateTime.now();
        }
        // Initialisation du workflow
        final DocUnitState stateStart = workflow.getFutureOrRunningByKey(WorkflowStateKey.INITIALISATION_DOCUMENT);
        stateStart.initializeState(startDate, startDate, WorkflowStateStatus.FINISHED);

        // On annule toutes les étapes jusqu'au contrôles qualité
        // Constats d'état
        final DocUnitState validateConstatState = workflow.getFutureOrRunningByKey(WorkflowStateKey.VALIDATION_CONSTAT_ETAT);
        validateConstatState.initializeState(startDate, startDate, WorkflowStateStatus.CANCELED);
        // Génération du bordereau
        final DocUnitState generateBordereauState = workflow.getFutureOrRunningByKey(WorkflowStateKey.GENERATION_BORDEREAU);
        generateBordereauState.initializeState(startDate, startDate, WorkflowStateStatus.CANCELED);
        // Validation bordereau
        final DocUnitState valBordState = workflow.getFutureOrRunningByKey(WorkflowStateKey.VALIDATION_BORDEREAU_CONSTAT_ETAT);
        valBordState.initializeState(startDate, startDate, WorkflowStateStatus.CANCELED);
        // Constats d'états
        final DocUnitState constatAvState = workflow.getFutureOrRunningByKey(WorkflowStateKey.CONSTAT_ETAT_AVANT_NUMERISATION);
        constatAvState.initializeState(startDate, startDate, WorkflowStateStatus.CANCELED);
        final DocUnitState constatApState = workflow.getFutureOrRunningByKey(WorkflowStateKey.CONSTAT_ETAT_APRES_NUMERISATION);
        constatApState.initializeState(startDate, startDate, WorkflowStateStatus.CANCELED);
        // Partie livraison
        final DocUnitState numWaitState = workflow.getFutureOrRunningByKey(WorkflowStateKey.NUMERISATION_EN_ATTENTE);
        numWaitState.initializeState(startDate, startDate, WorkflowStateStatus.CANCELED);
        final DocUnitState livraisonState = workflow.getFutureOrRunningByKey(WorkflowStateKey.LIVRAISON_DOCUMENT_EN_COURS);
        livraisonState.initializeState(startDate, startDate, WorkflowStateStatus.CANCELED);
        final DocUnitState controleAutoState = workflow.getFutureOrRunningByKey(WorkflowStateKey.CONTROLES_AUTOMATIQUES_EN_COURS);
        controleAutoState.initializeState(startDate, startDate, WorkflowStateStatus.CANCELED);

        // Démarrage des étapes en cours
        final DocUnitState controleQualiteState = workflow.getFutureOrRunningByKey(WorkflowStateKey.CONTROLE_QUALITE_EN_COURS);
        controleQualiteState.initializeState(startDate, null, null);
        final DocUnitState validateBibliographicRecord = workflow.getFutureOrRunningByKey(WorkflowStateKey.VALIDATION_NOTICES);
        validateBibliographicRecord.initializeState(startDate, null, null);

        return workflow;
    }

    @Transactional
    public void initializeWorkflow(final Lot lot, final WorkflowModel model) {
        final List<DocUnit> docs = docUnitService.findAllByLotId(lot.getIdentifier());
        final LocalDateTime startDate = LocalDateTime.now();
        // Traitement selon le type de lot
        if (Lot.Type.PHYSICAL.equals(lot.getType())) {
            docs.forEach(doc -> {
                initializeWorkflow(doc, model, startDate);
            });
        } else {
            docs.forEach(doc -> {
                initializeWorkflowForDigitalLot(doc, model, startDate);
            });
        }
    }

    /**
     * Met fin à un workflow. Toutes les étapes en cours et à venir passe au statut {@link WorkflowStateStatus#CANCELED}
     * sauf l'étape {@link WorkflowStateKey#CLOTURE_DOCUMENT} qui passe à l'état {@link WorkflowStateStatus#FINISHED}
     *
     * @param docUnitId
     */
    @Transactional
    public void endWorkflowForDocUnit(final String docUnitId) {
        final DocUnit docUnit = docUnitService.findOneWithAllDependenciesForWorkflow(docUnitId);
        if (docUnit.getWorkflow() == null) {
            return;
        }
        docUnitWorkflowService.endWorkflow(docUnit.getWorkflow());
        // MAJ des infos nécéssaires
        handleStatus(docUnit, docUnit.getWorkflow());
    }

    /**
     * Termine les workflows des docs du projet annulé,
     * et pose les statuts correspondants sur les entites concernees.
     *
     * @param docUnits
     */
    @Transactional
    public void endWorkflowForCancelingProject(final Set<DocUnit> docUnits) {

        // On termine les workflows des docUnits du projet annnulé.
        docUnits.forEach(du -> {
            final DocUnit docUnit = docUnitService.findOneWithAllDependenciesForWorkflow(du.getIdentifier());
            docUnitWorkflowService.endWorkflow(docUnit.getWorkflow());

            final DocUnitState state =
                docUnit.getWorkflow().getByKey(WorkflowStateKey.CLOTURE_DOCUMENT).stream().collect(NumahopCollectors.singletonCollector());
            // Si terminé, on maj la date de fin et le statut.
            if (state.isDone()) {
                // Date de référence
                final LocalDateTime endDate = state.getEndDate();
                docUnit.getWorkflow().setEndDate(endDate);
                docUnit.setState(State.CANCELED);
            }
        });

    }

    @Transactional
    public void resetToNumWaitingState(final List<String> docUnits) {

        docUnits.forEach(id -> {
            final DocUnit docUnit = docUnitService.findOneWithAllDependenciesForWorkflow(id);
            final DocUnitWorkflow workflow = docUnit.getWorkflow();
            // # 3862 - on verifie qu'une state de relivraison n'est pas deja lancée...
            if (workflow.getCurrentStateByKey(WorkflowStateKey.RELIVRAISON_DOCUMENT_EN_COURS) == null) {
                docUnitWorkflowService.resetPostDeliveryStates(docUnit);
                docUnitWorkflowService.createRedeliverStateForInstance(workflow);
            }
        });


    }

    /**
     * Réalisation d'une tâche par un utilisateur
     *
     * @param doc
     * @param key
     * @param user
     */
    @Transactional
    public void processState(final DocUnit doc, final WorkflowStateKey key, final User user) {
        final DocUnitWorkflow workflow = doc.getWorkflow();
        if (workflow == null) {
            LOG.warn("Aucun workflow sur le document : impossible de réaliser l'étape {}", key.name());
        } else {
            DocUnitState currentState = workflow.getCurrentStateByKey(key);
            final String userId = user != null ? user.getIdentifier() : null;

            // Cas particulier CONSTAT ETAT HORS WORKFLOW : on peut valider  l'etape non requise (skipped)
            if (currentState == null &&
                        (WorkflowStateKey.VALIDATION_CONSTAT_ETAT == key || WorkflowStateKey.CONSTAT_ETAT_AVANT_NUMERISATION == key
                            || WorkflowStateKey.CONSTAT_ETAT_APRES_NUMERISATION == key || WorkflowStateKey.VALIDATION_BORDEREAU_CONSTAT_ETAT == key ) ) {
                currentState = workflow.getStates().stream()
                                        .filter(st-> key == st.getKey()
                                                && WorkflowStateStatus.SKIPPED.equals(st.getStatus()))
                                        .findFirst().orElse(null);

                if (currentState != null && currentState.getModelState().getGroup() == null) {
                    // on a une state mais sans group de workflow => on force l'excecution de la tache (shunt controles )
                    currentState.process(user);
                    return;
                }
            }  // fin cas particulier CONSTAT ETAT HORS WORKFLOW

            if (!canUserProcessTask(userId, currentState) || !canStateBeProcessed(doc, currentState)) {
                final PgcnError.Builder builder = new PgcnError.Builder();
                throw new PgcnBusinessException(builder.reinit().setCode(PgcnErrorCode.WORKFLOW_PROCESS_NO_RIGHTS).build());

            } else if(currentState != null){
                currentState.process(user);
            }
            handleStatus(doc, workflow);
        }
    }

    /**
     * Rejet d'une tâche par un utilisateur
     *
     * @param doc
     * @param key
     * @param user
     */
    @Transactional
    public void rejectState(final DocUnit doc, final WorkflowStateKey key, final User user) {
        final DocUnitWorkflow workflow = doc.getWorkflow();
        if (workflow == null) {
            LOG.warn("Aucun workflow sur le document : impossible de rejeter l'étape {}", key.name());
        } else {
            final DocUnitState currentState = workflow.getCurrentStateByKey(key);
            final String userId = user != null ? user.getIdentifier() : null;

            if (!canUserProcessTask(userId, currentState) || !canStateBeProcessed(doc, currentState)) {
                final PgcnError.Builder builder = new PgcnError.Builder();
                throw new PgcnBusinessException(builder.reinit().setCode(PgcnErrorCode.WORKFLOW_PROCESS_NO_RIGHTS).build());
            } else {
                currentState.reject(user);
            }
            handleStatus(doc, workflow);
        }
    }

    /**
     * Réalisation d'une tâche automatique
     *
     * @param docUnitId
     * @param key
     */
    @Transactional
    public void processAutomaticState(final String docUnitId, final WorkflowStateKey key) {
        final DocUnit doc = docUnitService.findOneWithAllDependenciesForWorkflow(docUnitId);
        final DocUnitWorkflow workflow = doc.getWorkflow();
        if (workflow == null) {
            LOG.warn("Aucun workflow sur le document : impossible de réaliser automatiquement l'étape {}", key.name());
        } else {
            final DocUnitState currentState = workflow.getCurrentStateByKey(key);
            if (currentState != null && currentState.isWaiting()) {
                currentState.process(null);
            } else {
                LOG.error("Impossible de valider automatiquement la tâche");
            }
            // init historique controle.
            if (key == WorkflowStateKey.CONTROLES_AUTOMATIQUES_EN_COURS) {
                //create docCheckHistory line.
                docCheckHistoryService.create(doc.getIdentifier());
            }
            
            handleStatus(doc, workflow);
        }
    }

    /**
     * Echec d'une tâche automatique
     *
     * @param docUnitId
     * @param key
     */
    @Transactional
    public void rejectAutomaticState(final String docUnitId, final WorkflowStateKey key) {
        final DocUnit doc = docUnitService.findOneWithAllDependenciesForWorkflow(docUnitId);
        final DocUnitWorkflow workflow = doc.getWorkflow();
        if (workflow == null) {
            LOG.warn("Aucun workflow sur le document : impossible de rejeter automatiquement l'étape {}", key.name());
        } else {
            final DocUnitState currentState = workflow.getCurrentStateByKey(key);
            if (currentState != null && currentState.isWaiting()) {
                currentState.reject(null);
            } else {
                LOG.error("Workflow : Impossible de refuser automatiquement la tâche");
            }
            handleStatus(doc, workflow);
        }
    }

    /**
     * Gestion des implications de l'avancement du workflow sur le statut des entités liées (lot, projet)
     *
     * @param doc
     * @param workflow
     */
    private void handleStatus(final DocUnit doc, final DocUnitWorkflow workflow) {
        
        // contrôle de l'état (le workflow doit être fini pour impacter les conteneurs)
        final DocUnitState state = workflow.getByKey(WorkflowStateKey.CLOTURE_DOCUMENT).stream().collect(NumahopCollectors.singletonCollector());
        // Si l'étape de fin est finie, on regarde si on peut cloturer lot/projet.
        if (state.isDone()) {
            // Date de référence
            final LocalDateTime endDate = state.getEndDate();
            workflow.setEndDate(endDate);
            
            // Gestion du lot
            // LOT : cloturé uniquement si tout OK (tout terminé et surtout sans erreur: tous les docs doivent etre validés)
            final Lot lot = lotService.findByDocUnitIdentifier(doc.getIdentifier());
            final List<DocUnit> processingDus = new ArrayList<>();
            
            lot.getDocUnits().stream()
                                .forEach(du -> {
                                    
                                    if (processingDus.isEmpty()) {
                                    
                                        if (du.getWorkflow() == null || !du.getWorkflow().isDone()) {
                                            // workflow non démarré ou non terminé
                                            processingDus.add(du);
                                        } else {
                                            // workflow terminé et controle qualite non terminé ou attente de relivraison
                                            final DocUnitState controlState = du.getWorkflow()
                                                                                    .getStates()
                                                                                    .stream()
                                                                                    .filter(st -> WorkflowStateKey.CONTROLE_QUALITE_EN_COURS == st.getKey())
                                                                                    .findFirst().orElse(null);
                                            
                                            final DocUnitState relivState = du.getWorkflow()
                                                    .getStates()
                                                    .stream()
                                                    .filter(st -> WorkflowStateKey.RELIVRAISON_DOCUMENT_EN_COURS == st.getKey())
                                                    .findFirst().orElse(null);
                                            // Cas tordus à prendre en compte .... 
                                            if ( (controlState != null && controlState.getStatus() != WorkflowStateStatus.FINISHED)
                                                    || (relivState != null && relivState.getStatus() != WorkflowStateStatus.FINISHED) ) {
                                                processingDus.add(du);
                                            } 
                                        }
                                    }
                                });
            
            if (processingDus.isEmpty()) {
                // Tous les documents du lots sont finis et validés, -> on peut fermer le lot
                lot.setRealEndDate(endDate.toLocalDate());
                lot.setStatus(LotStatus.CLOSED);
                lot.setActive(false);
                lotService.save(lot);
                LOG.info("Workflow : Mise a jour du lot {} => lot status : CLOSED", lot.getLabel());
            }
            // Gestion du projet
            final Project project = projectService.findByDocUnitIdentifier(doc.getIdentifier());
            final List<Lot> processingLots =
                project.getLots().stream().filter(lp -> !LotStatus.CLOSED.equals(lp.getStatus())).collect(Collectors.toList());
            if (processingLots.isEmpty()) {
                // Tous les lots sont finis, on termine aussi le projet et les trains du coup
                project.setRealEndDate(endDate.toLocalDate());
                project.setStatus(ProjectStatus.CLOSED);
                project.setActive(false);
                project.getTrains().forEach(train -> {
                    train.setActive(false);
                    train.setStatus(TrainStatus.CLOSED);
                });
                projectService.save(project);
                LOG.info("Workflow : Mise à jour du project {} => project status : CLOSED", project.getName());
            }
        }
    }

    /**
     * Vérification du droit de l'utilisateur de réaliser la tâche avec la {@link WorkflowStateKey} fournie
     *
     * @param userId
     * @param docUnitId
     * @param key
     * @return
     */
    @Transactional(readOnly = true)
    public boolean canUserProcessState(final String userId, final String docUnitId, final WorkflowStateKey key) {
        if (userId != null && docUnitId != null) {
            final DocUnit docUnit = docUnitService.findOneWithAllDependenciesForWorkflow(docUnitId);
            final DocUnitWorkflow workflow = docUnit.getWorkflow();
            if (workflow == null) {
                LOG.debug("Aucun workflow sur le document : impossible de vérifier la faisabilité de l'étape {}", key.name());
            } else {
                final DocUnitState currentState = workflow.getCurrentStateByKey(key);
                return canUserProcessTask(userId, currentState);
            }
        }
        return false;
    }

    /**
     * Vérification du droit de l'utilisateur de réaliser la tâche
     *
     * @param userId
     * @param state
     * @return
     */
    @Transactional(readOnly = true)
    public boolean canUserProcessTask(final String userId, final DocUnitState state) {
        if (userId != null && state != null) {
            return workflowGroupService.isUserAuthorized(state.getModelState().getGroup(), userId);
        }
        return false;
    }

    /**
     * Vérification de la possibilité de traiter la tâche (vérification des préconditions)
     *
     * @param state
     * @return
     */
    @Transactional(readOnly = true)
    public boolean canStateBeProcessed(final DocUnit doc, final DocUnitState state) {
        switch (state.getKey()) {
            case ARCHIVAGE_DOCUMENT: // Possible si le document est prêt
                return docUnitService.isDocumentReadyForArchivage(doc.getIdentifier());
            case CLOTURE_DOCUMENT:
                break;
            case CONTROLES_AUTOMATIQUES_EN_COURS:
                break;
            case CONTROLE_QUALITE_EN_COURS:
                return true;
            case DIFFUSION_DOCUMENT:
            case DIFFUSION_DOCUMENT_OMEKA:
            case DIFFUSION_DOCUMENT_LOCALE:
                return docUnitService.isDocumentReadyForDiffusion(doc.getIdentifier());
            case GENERATION_BORDEREAU:
                break;
            case INITIALISATION_DOCUMENT:
                break;
            // toujours possible
            case LIVRAISON_DOCUMENT_EN_COURS:
                return true;
            case NUMERISATION_EN_ATTENTE:
                break;

            case PREREJET_DOCUMENT:
            case PREVALIDATION_DOCUMENT:
            case VALIDATION_DOCUMENT:
                // OK si controle terminé
                return isStateDone(doc.getIdentifier(), WorkflowStateKey.CONTROLE_QUALITE_EN_COURS);
            case RAPPORT_CONTROLES:
                break;
            case RELIVRAISON_DOCUMENT_EN_COURS:
                // OK si livraison initiale terminee
                return isStateDone(doc.getIdentifier(), WorkflowStateKey.LIVRAISON_DOCUMENT_EN_COURS);
            case VALIDATION_CONSTAT_ETAT:
                // Le constat d'état de type LIBRARY_LEAVING doit être présent
                return conditionReportService.isConditionReportDetailPresentInDocUnit(doc.getIdentifier(), Type.LIBRARY_LEAVING);
            case VALIDATION_BORDEREAU_CONSTAT_ETAT:
                // Le constat d'état de type LIBRARY_LEAVING doit être validé
                return isStateDone(doc.getIdentifier(), WorkflowStateKey.VALIDATION_CONSTAT_ETAT);
            case CONSTAT_ETAT_AVANT_NUMERISATION:
            case CONSTAT_ETAT_APRES_NUMERISATION:
                return true;

                // La notice doit être présente
            case VALIDATION_NOTICES:
                return !recordService.findAllByDocUnitId(doc.getIdentifier()).isEmpty();
            default:
                break;
        }
        return false;
    }

    /**
     * Vérifie si la state est finie. Valable uniquement pour les states non répétables
     *
     * @param docUnitId
     * @param key
     * @return
     */
    @Transactional(readOnly = true)
    public boolean isStateDone(final String docUnitId, final WorkflowStateKey key) {
        if (docUnitId != null) {
            final DocUnit docUnit = docUnitService.findOneWithAllDependenciesForWorkflow(docUnitId);

            if (docUnit != null && docUnit.getWorkflow() != null) {
                final List<DocUnitState> statesForKey = docUnit.getWorkflow().getByKey(key);
                if (statesForKey.size() == 1) {
                    return statesForKey.get(0).isDone();
                }
            }
        }
        return false;
    }

    /**
     * Vérifie si la state est ignoree ou en cours. Valable uniquement pour les states non répétables
     *
     * @param docUnitId
     * @param key
     * @return
     */
    @Transactional(readOnly = true)
    public boolean isStateSkippedOrRunning(final String docUnitId, final WorkflowStateKey key) {
        if (docUnitId != null) {
            final DocUnit docUnit = docUnitService.findOneWithAllDependenciesForWorkflow(docUnitId);

            if (docUnit != null && docUnit.getWorkflow() != null) {
                final List<DocUnitState> statesForKey = docUnit.getWorkflow().getByKey(key);
                if (statesForKey.size() == 1) {
                    final DocUnitState state = statesForKey.get(0);
                    return state.isSkipped() || state.isRunning();
                }
            }
        }
        return false;
    }

    /**
     * Vérifie si le rejet est definitif.
     *
     * @param docUnitId
     * @return
     */
    @Transactional(readOnly = true)
    public boolean isRejectDefinitive(final String docUnitId) {
        if (docUnitId != null) {
            final DocUnit docUnit = docUnitService.findOneWithAllDependenciesForWorkflow(docUnitId);
            if (docUnit != null && docUnit.getWorkflow() != null) {

                final DocUnitState prerej = docUnit.getWorkflow()
                                                   .getStates()
                                                   .stream()
                                                   .filter(st -> WorkflowStateKey.PREREJET_DOCUMENT == st.getKey())
                                                   .findFirst()
                                                   .orElse(null);
                final DocUnitState preval = docUnit.getWorkflow()
                                                   .getStates()
                                                   .stream()
                                                   .filter(st -> WorkflowStateKey.PREVALIDATION_DOCUMENT == st.getKey())
                                                   .findFirst()
                                                   .orElse(null);
                if (prerej == null || preval == null) {
                    return false;
                }
                return prerej.isDone() || preval.isDone() || WorkflowStateStatus.TO_SKIP == prerej.getStatus();
            }
        }
        return false;
    }

    /**
     * Vérifie si la state est finie et validée. Valable uniquement pour les states non répétables
     *
     * @param docUnitId
     * @param key
     * @return
     */
    @Transactional(readOnly = true)
    public boolean isStateValidated(final String docUnitId, final WorkflowStateKey key) {
        if (docUnitId != null) {
            final DocUnit docUnit = docUnitService.findOneWithAllDependenciesForWorkflow(docUnitId);

            if (docUnit != null && docUnit.getWorkflow() != null) {
                final List<DocUnitState> statesForKey = docUnit.getWorkflow().getByKey(key);
                if (statesForKey.size() == 1) {
                    return statesForKey.get(0).isValidated();
                }
            }
        }
        return false;
    }

    /**
     * Vérifie si la state est en cours
     *
     * @param docUnitId
     * @param key
     * @return
     */
    @Transactional(readOnly = true)
    public boolean isStateRunning(final String docUnitId, final WorkflowStateKey key) {

        if (StringUtils.isNotBlank(docUnitId)) {
            final DocUnit docUnit = docUnitService.findOneWithAllDependenciesForWorkflow(docUnitId);
            return isStateRunning(docUnit, key);
        }
        return false;
    }

    /**
     * Vérifie si au moins une des states est en cours.
     *
     * @param docUnitId
     * @param keys
     * @return
     */
    @Transactional(readOnly = true)
    public boolean areStatesRunning(final String docUnitId, final WorkflowStateKey... keys) {

        boolean result = false;
        if (StringUtils.isNotBlank(docUnitId)) {
            final DocUnit docUnit = docUnitService.findOneWithAllDependenciesForWorkflow(docUnitId);
            for (final WorkflowStateKey key: Arrays.asList(keys)) {
                if (isStateRunning(docUnit, key)) {
                    result = true;
                    break;
                }
            }
        }
        return result;
    }

    /**
     * Vérifie si la state est en cours
     *
     * @param docUnit
     * @param key
     * @return
     */
    @Transactional(readOnly = true)
    public boolean isStateRunning(final DocUnit docUnit, final WorkflowStateKey key) {

        if (docUnit != null && docUnit.getWorkflow() != null) {
            final List<DocUnitState> statesForKey = docUnit.getWorkflow().getByKey(key);
            
            if (statesForKey.size() == 1) {
                return statesForKey.get(0).isRunning();
            } else if (WorkflowStateKey.RELIVRAISON_DOCUMENT_EN_COURS == key) {
                // cas particulier re-livraison : plusieurs etapes possibles
                // on veut la + recente en cours
                final Optional<DocUnitState> state = statesForKey.stream()
                                                    .filter(st -> st.getEndDate() == null)
                                                        .max(Comparator.comparing(DocUnitState::getStartDate));
                            
                return state.isPresent() && state.get().isRunning();
            }
            
        }

        return false;
    }

    /**
     * Retourne un {@link DocUnitWorkflow} à partir de son identifiant
     *
     * @param identifier
     * @return
     */
    @Transactional(readOnly = true)
    public DocUnitWorkflow findOneWorkflow(final String identifier) {
        return docUnitWorkflowService.getOneWorkflow(identifier);
    }

    /**
     * Retourne un {@link DocUnitWorkflow} à partir de son {@link DocUnit}
     *
     * @param identifier
     * @return
     */
    @Transactional(readOnly = true)
    public DocUnitWorkflow findOneWorkflowByDocUnit(final String identifier) {
        final DocUnit doc = docUnitService.findOne(identifier);
        if (doc != null && doc.getWorkflow() != null) {
            return docUnitWorkflowService.getOneWorkflow(doc.getWorkflow().getIdentifier());
        }
        return null;
    }

    /**
     * Retourne les {@link DocUnitState} liés à un {@link WorkflowGroup} en cours ou à venir
     *
     * @param group
     * @return
     */
    @Transactional(readOnly = true)
    public List<DocUnitState> findAllRunningOrPendingStateForGroup(final WorkflowGroup group) {
        return docUnitWorkflowService.findAllRunningOrPendingStateForGroup(group);
    }

    /**
     * Réalisation d'une étape
     *
     * @param docUnitId
     * @param key
     * @param userId
     */
    @Transactional
    public void processState(final String docUnitId, final WorkflowStateKey key, final String userId) {
        processState(docUnitService.findOneWithAllDependenciesForWorkflow(docUnitId), key, userService.findByIdentifier(userId));
    }

    /**
     * Rejet d'une étape
     *
     * @param docUnitId
     * @param key
     * @param userId
     */
    @Transactional
    public void rejectState(final String docUnitId, final WorkflowStateKey key, final String userId) {
        rejectState(docUnitService.findOneWithAllDependenciesForWorkflow(docUnitId), key, userService.findByIdentifier(userId));
    }

    /**
     * Permet de déterminer si un workflow est en cours sur le document (impact sur les actions)
     *
     * @param docUnitId
     * @return
     */
    @Transactional(readOnly = true)
    public boolean isWorkflowRunning(final String docUnitId) {
        final DocUnit docUnit = docUnitService.findOneWithAllDependenciesForWorkflow(docUnitId);
        return docUnit != null && docUnit.getWorkflow() != null;
    }

    /**
     * Permet de déterminer si un workflow est démarré et sur les contrôles
     *
     * @param docUnitId
     * @return
     */
    @Transactional(readOnly = true)
    public boolean isCheckInProgress(final String docUnitId) {
        final DocUnit docUnit = docUnitService.findOneWithAllDependenciesForWorkflow(docUnitId);
        if (docUnit != null && docUnit.getWorkflow() != null) {
            final DocUnitWorkflow workflow = docUnit.getWorkflow();
            final ArrayList<WorkflowStateKey> keys = new ArrayList<>(Arrays.asList(WorkflowStateKey.CONTROLE_QUALITE_EN_COURS,
                                                                                   WorkflowStateKey.PREVALIDATION_DOCUMENT,
                                                                                   WorkflowStateKey.PREREJET_DOCUMENT,
                                                                                   WorkflowStateKey.VALIDATION_DOCUMENT));
            for (final WorkflowStateKey key : keys) {
                final List<DocUnitState> statesForKey = workflow.getByKey(key);
                if (statesForKey.size() == 1) {
                    if (statesForKey.get(0).isRunning()) {
                        return true;
                    }
                }
            }

        }
        return false;
    }
    
    /**
     * Permet de déterminer si un workflow est démarré et le doc en attente de relivraison
     *
     * @param docUnitId
     * @return
     */
    @Transactional(readOnly = true)
    public boolean isWaitingForRedelivering(final String docUnitId) {
        final DocUnit docUnit = docUnitService.findOneWithAllDependenciesForWorkflow(docUnitId);
        if (docUnit != null && docUnit.getWorkflow() != null) {
            final DocUnitWorkflow workflow = docUnit.getWorkflow();
            final List<DocUnitState> statesForKey = workflow.getByKey(WorkflowStateKey.RELIVRAISON_DOCUMENT_EN_COURS);
            if (statesForKey.size() == 1) {
                if (statesForKey.get(0).isRunning()) {
                    return true;
                }
            }
        }
        return false;
    }

    @Transactional(readOnly = true)
    public boolean hasCondReportToValidate(final String docUnitId) {
        return isStateRunning(docUnitId, WorkflowStateKey.VALIDATION_CONSTAT_ETAT)
                || isStateRunning(docUnitId, WorkflowStateKey.CONSTAT_ETAT_AVANT_NUMERISATION)
                || isStateRunning(docUnitId, WorkflowStateKey.CONSTAT_ETAT_APRES_NUMERISATION);
    }
    
}
