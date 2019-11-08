package fr.progilone.pgcn.service.workflow;

import static fr.progilone.pgcn.domain.workflow.WorkflowStateStatus.NOT_STARTED;
import static fr.progilone.pgcn.domain.workflow.WorkflowStateStatus.PENDING;
import static fr.progilone.pgcn.domain.workflow.WorkflowStateStatus.TO_SKIP;
import static fr.progilone.pgcn.domain.workflow.WorkflowStateStatus.TO_WAIT;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import fr.progilone.pgcn.domain.document.DocUnit;
import fr.progilone.pgcn.domain.workflow.DocUnitState;
import fr.progilone.pgcn.domain.workflow.DocUnitWorkflow;
import fr.progilone.pgcn.domain.workflow.WorkflowGroup;
import fr.progilone.pgcn.domain.workflow.WorkflowModel;
import fr.progilone.pgcn.domain.workflow.WorkflowModelState;
import fr.progilone.pgcn.domain.workflow.WorkflowStateKey;
import fr.progilone.pgcn.domain.workflow.WorkflowStateStatus;
import fr.progilone.pgcn.domain.workflow.state.ArchivageDocumentState;
import fr.progilone.pgcn.domain.workflow.state.ClotureDocumentState;
import fr.progilone.pgcn.domain.workflow.state.ConstatEtatApresNumerisationState;
import fr.progilone.pgcn.domain.workflow.state.ConstatEtatAvantNumerisationState;
import fr.progilone.pgcn.domain.workflow.state.ControlesAutomatiquesEnCoursState;
import fr.progilone.pgcn.domain.workflow.state.ControlesQualiteState;
import fr.progilone.pgcn.domain.workflow.state.DiffusionDocumentLocaleState;
import fr.progilone.pgcn.domain.workflow.state.DiffusionDocumentOmekaState;
import fr.progilone.pgcn.domain.workflow.state.DiffusionDocumentState;
import fr.progilone.pgcn.domain.workflow.state.GenerationBordereauState;
import fr.progilone.pgcn.domain.workflow.state.InitialisationDocumentState;
import fr.progilone.pgcn.domain.workflow.state.LivraisonDocumentEnCoursState;
import fr.progilone.pgcn.domain.workflow.state.NumerisationEnAttenteState;
import fr.progilone.pgcn.domain.workflow.state.PreRejetState;
import fr.progilone.pgcn.domain.workflow.state.PreValidationState;
import fr.progilone.pgcn.domain.workflow.state.RapportsControleState;
import fr.progilone.pgcn.domain.workflow.state.ReLivraisonDocumentEnCoursState;
import fr.progilone.pgcn.domain.workflow.state.ValidationBordereauConstatEtatState;
import fr.progilone.pgcn.domain.workflow.state.ValidationConstatEtatState;
import fr.progilone.pgcn.domain.workflow.state.ValidationDocumentState;
import fr.progilone.pgcn.domain.workflow.state.ValidationNoticesState;
import fr.progilone.pgcn.exception.PgcnValidationException;
import fr.progilone.pgcn.repository.workflow.DocUnitStateRepository;
import fr.progilone.pgcn.repository.workflow.DocUnitWorkflowRepository;
import fr.progilone.pgcn.repository.workflow.helper.DocUnitWorkflowSearchBuilder;

/**
 * Service de gestion des instances de workflow (étapes & workflow)
 *
 * @author jbrunet
 * Créé le 10 oct. 2017
 */
@Service
public class DocUnitWorkflowService {

    private static final Logger LOG = LoggerFactory.getLogger(DocUnitWorkflowService.class);

    private final DocUnitWorkflowRepository docUnitWorkflowRepository;
    private final DocUnitStateRepository docUnitStateRepository;

    @Autowired
    public DocUnitWorkflowService(final DocUnitWorkflowRepository docUnitWorkflowRepository, final DocUnitStateRepository docUnitStateRepository) {
        this.docUnitWorkflowRepository = docUnitWorkflowRepository;
        this.docUnitStateRepository = docUnitStateRepository;
    }

    @Transactional
    public DocUnitWorkflow createFromModel(final WorkflowModel model) {
        final DocUnitWorkflow instance = new DocUnitWorkflow();
        instance.setModel(model);
        instance.setStartDate(LocalDateTime.now());
        final DocUnitWorkflow savedInstance = save(instance);
        model.getModelStates().forEach(state -> {
            final DocUnitState stateInstance = createFromModelState(savedInstance, state);
            savedInstance.addState(stateInstance);
        });
        return savedInstance;
    }

    @Transactional
    public DocUnitState createFromModelState(final DocUnitWorkflow workflow, final WorkflowModelState modelState) {
        final DocUnitState instance;
        switch (modelState.getKey()) {
            case ARCHIVAGE_DOCUMENT:
                instance = new ArchivageDocumentState();
                break;
            case CLOTURE_DOCUMENT:
                instance = new ClotureDocumentState();
                break;
            case CONSTAT_ETAT_APRES_NUMERISATION:
                instance = new ConstatEtatApresNumerisationState();
                break;
            case CONSTAT_ETAT_AVANT_NUMERISATION:
                instance = new ConstatEtatAvantNumerisationState();
                break;
            case CONTROLES_AUTOMATIQUES_EN_COURS:
                instance = new ControlesAutomatiquesEnCoursState();
                break;
            case CONTROLE_QUALITE_EN_COURS:
                instance = new ControlesQualiteState();
                break;
            case DIFFUSION_DOCUMENT:
                instance = new DiffusionDocumentState();
                break;
            case DIFFUSION_DOCUMENT_OMEKA:
                instance = new DiffusionDocumentOmekaState();
                break;
            case DIFFUSION_DOCUMENT_LOCALE:
                instance = new DiffusionDocumentLocaleState();
                break;
            case GENERATION_BORDEREAU:
                instance = new GenerationBordereauState();
                break;
            case INITIALISATION_DOCUMENT:
                instance = new InitialisationDocumentState();
                break;
            case LIVRAISON_DOCUMENT_EN_COURS:
                instance = new LivraisonDocumentEnCoursState();
                break;
            case NUMERISATION_EN_ATTENTE:
                instance = new NumerisationEnAttenteState();
                break;
            case PREREJET_DOCUMENT:
                instance = new PreRejetState();
                break;
            case PREVALIDATION_DOCUMENT:
                instance = new PreValidationState();
                break;
            case RAPPORT_CONTROLES:
                instance = new RapportsControleState();
                break;
            case RELIVRAISON_DOCUMENT_EN_COURS:
                instance = new ReLivraisonDocumentEnCoursState();
                break;
            case VALIDATION_BORDEREAU_CONSTAT_ETAT:
                instance = new ValidationBordereauConstatEtatState();
                break;
            case VALIDATION_CONSTAT_ETAT:
                instance = new ValidationConstatEtatState();
                break;
            case VALIDATION_DOCUMENT:
                instance = new ValidationDocumentState();
                break;
            case VALIDATION_NOTICES:
                instance = new ValidationNoticesState();
                break;
            default:
                LOG.error("L'étape de workflow {} n'est pas gérée", modelState.getKey());
                return null;
        }
        switch (modelState.getType()) {
            case TO_SKIP:
                instance.setStatus(TO_SKIP);
                break;
            case TO_WAIT:
                instance.setStatus(TO_WAIT);
                break;
            case REQUIRED:
            default:
                instance.setStatus(NOT_STARTED);
                break;
        }
        instance.setModelState(modelState);
        instance.setWorkflow(workflow);
        return save(instance);
    }

    /**
     * Gestion de la relivraison
     * Ajoute une étape spécifique de relivraison (hors modèle) au workflow et
     * Reinitialise l'étape de contrôle automatique (modèle)
     *
     * @param instance
     */
    @Transactional
    public void createRedeliverStateForInstance(final DocUnitWorkflow instance) {

        final DocUnitState state = new ReLivraisonDocumentEnCoursState();
        state.setModelState(instance.getModel().getModelStateByKey(WorkflowStateKey.LIVRAISON_DOCUMENT_EN_COURS));
        state.initializeState(null, null, PENDING);
        instance.addState(state);
        state.setWorkflow(instance);
        save(state);

        // reinitialise le workflow pour les controles auto en cours
        reinitializeState(instance, WorkflowStateKey.CONTROLES_AUTOMATIQUES_EN_COURS, NOT_STARTED);

        // reinitialise aussi un eventuel controle qualité en cours
        reinitializeState(instance, WorkflowStateKey.CONTROLE_QUALITE_EN_COURS, NOT_STARTED);
        
        resetPostDeliveryStates(instance.getDocUnit());
        save(instance);
    }

    @Transactional
    public void resetDeliverStatesForDetachedDoc(final DocUnit docUnit) {

        if (docUnit.getWorkflow() != null) {
            final DocUnitWorkflow workflow = docUnit.getWorkflow();
            final WorkflowModel model = workflow.getModel();
    
            final DocUnitState state = new ReLivraisonDocumentEnCoursState();
            state.setModelState(model.getModelStateByKey(WorkflowStateKey.LIVRAISON_DOCUMENT_EN_COURS));
            state.initializeState(null, null, PENDING);
            workflow.addState(state);
            save(state);
            // reinitialise le workflow pour les controles auto en cours
            reinitializeState(workflow, WorkflowStateKey.CONTROLES_AUTOMATIQUES_EN_COURS, NOT_STARTED);
            // reinitialise aussi un eventuel controle qualité en cours
            reinitializeState(workflow, WorkflowStateKey.CONTROLE_QUALITE_EN_COURS, NOT_STARTED);
    
            save(workflow);
        }
    }

    @Transactional
    public void resetPostDeliveryStates(final DocUnit docUnit) {

        final DocUnitWorkflow workflow = docUnit.getWorkflow();
        if (workflow != null) {
            // reinitialise du workflow pour les etapes post livraisons.
            reinitializeState(workflow, WorkflowStateKey.RAPPORT_CONTROLES, NOT_STARTED);
            reinitializeState(workflow, WorkflowStateKey.VALIDATION_DOCUMENT, NOT_STARTED);
            reinitializeState(workflow, WorkflowStateKey.PREVALIDATION_DOCUMENT, NOT_STARTED);
            reinitializeState(workflow, WorkflowStateKey.PREREJET_DOCUMENT, NOT_STARTED);
            workflow.setEndDate(null);
            save(workflow);
        }

    }

    private void reinitializeState(final DocUnitWorkflow workflow, final WorkflowStateKey key, final WorkflowStateStatus status) {

        final DocUnitState state = workflow.getStates()
                                           .stream()
                                           .filter(st -> st != null && key == st.getKey())
                                           .sorted(Collections.reverseOrder(Comparator.nullsLast(Comparator.comparing(DocUnitState::getStartDate))))
                                           .findFirst()
                                           .orElse(null);

        if (state != null) {
            state.setStatus(status);
            state.setEndDate(null);
        }
    }

    /**
     * Termine une instance de workflow : annule toutes les étapes et passe à clotûré
     * L'instance doit être correctement configurée
     *
     * @param instance
     */
    @Transactional
    public void endWorkflow(final DocUnitWorkflow instance) {
        final List<DocUnitState> statesToClose = instance.getFutureOrRunning();
        final LocalDateTime startDate = LocalDateTime.now();
        LOG.debug("Fin d'un workflow et annulation de {} étapes", statesToClose.size());
        statesToClose.forEach(state -> {
            if (WorkflowStateKey.CLOTURE_DOCUMENT.equals(state.getKey())) {
                state.initializeState(startDate, startDate, WorkflowStateStatus.FINISHED);
            } else {
                state.initializeState(startDate, startDate, WorkflowStateStatus.CANCELED);
            }
        });
    }

    /**
     * Sauvegarde une instance de workflow
     *
     * @param workflow
     * @return
     * @throws PgcnValidationException
     */
    @Transactional
    public DocUnitWorkflow save(final DocUnitWorkflow workflow) throws PgcnValidationException {
        //validate(workflow);
        return docUnitWorkflowRepository.save(workflow);
    }

    /**
     * Sauvegarde une étape de workflow
     *
     * @param state
     * @return
     * @throws PgcnValidationException
     */
    @Transactional
    public DocUnitState save(final DocUnitState state) throws PgcnValidationException {
        // validate(state);
        return docUnitStateRepository.save(state);
    }

    /**
     * Retourne une instance de workflow
     *
     * @param identifier
     * @return
     */
    @Transactional(readOnly = true)
    public DocUnitWorkflow getOneWorkflow(final String identifier) {
        return docUnitWorkflowRepository.getOne(identifier);
    }

    /**
     * Retourne une étape de workflow
     *
     * @param identifier
     * @return
     */
    @Transactional(readOnly = true)
    public DocUnitState getOneState(final String identifier) {
        return docUnitStateRepository.getOne(identifier);
    }

    /**
     * Suppression d'une instance de workflow
     *
     * @param identifier
     */
    @Transactional
    public void deleteWorkflow(final String identifier) {
        docUnitWorkflowRepository.delete(identifier);
    }

    /**
     * Suppression d'une instance d'étape
     *
     * @param identifier
     */
    @Transactional
    public void deleteState(final String identifier) {
        docUnitStateRepository.delete(identifier);
    }

    /**
     * Retourne les {@link DocUnitState} liés à un {@link WorkflowGroup} en cours ou à venir
     *
     * @param group
     * @return
     */
    @Transactional(readOnly = true)
    public List<DocUnitState> findAllRunningOrPendingStateForGroup(final WorkflowGroup group) {
        return docUnitStateRepository.findAllStateInStatusForGroupIdentifier(Arrays.asList(NOT_STARTED, PENDING, TO_SKIP, TO_WAIT), group);
    }

    /**
     * Recherche des étapes de workflow des ud
     *
     * @param libraries
     * @param projects
     * @param lots
     * @param deliveries
     * @param pendingStates
     * @return
     */
    @Transactional(readOnly = true)
    public List<DocUnitWorkflow> findAll(final List<String> libraries,
                                         final List<String> projects,
                                         final List<String> lots,
                                         final List<String> deliveries,
                                         final List<WorkflowStateKey> pendingStates) {
        return docUnitWorkflowRepository.findPendingDocUnitWorkflows(libraries, projects, lots, deliveries, pendingStates, null);
    }

    @Transactional(readOnly = true)
    public List<DocUnitWorkflow> findDocUnitWorkflowsInControl(final List<String> libraries,
                                                               final List<String> projects,
                                                               final List<String> lots,
                                                               final List<String> deliveries) {
        return docUnitWorkflowRepository.findDocUnitWorkflowsInControl(libraries, projects, lots, deliveries);
    }

    @Transactional(readOnly = true)
    public List<DocUnitWorkflow> findAll(final List<String> libraries,
                                         final List<String> projects,
                                         final List<String> lots,
                                         final List<String> deliveries,
                                         final LocalDate fromDate,
                                         final LocalDate toDate) {
        return docUnitWorkflowRepository.findDocUnitWorkflows(new DocUnitWorkflowSearchBuilder().setLibraries(libraries)
                                                                                                .setProjects(projects)
                                                                                                .setLots(lots)
                                                                                                .setDeliveries(deliveries)
                                                                                                .setFromDate(fromDate)
                                                                                                .setToDate(toDate));
    }
    
    
    public List<DocUnitWorkflow> findDocUnitWorkflowsForArchiveExport(final String library) {
        return docUnitWorkflowRepository.findDocUnitWorkflowsForArchiveExport(library);
    }
}
