package fr.progilone.pgcn.service.workflow;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.stream.Collectors;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.AdditionalAnswers;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import fr.progilone.pgcn.domain.document.DocUnit;
import fr.progilone.pgcn.domain.user.User;
import fr.progilone.pgcn.domain.workflow.DocUnitState;
import fr.progilone.pgcn.domain.workflow.DocUnitWorkflow;
import fr.progilone.pgcn.domain.workflow.WorkflowModel;
import fr.progilone.pgcn.domain.workflow.WorkflowModelState;
import fr.progilone.pgcn.domain.workflow.WorkflowModelStateType;
import fr.progilone.pgcn.domain.workflow.WorkflowStateKey;
import fr.progilone.pgcn.domain.workflow.WorkflowStateStatus;
import fr.progilone.pgcn.repository.workflow.DocUnitStateRepository;
import fr.progilone.pgcn.repository.workflow.DocUnitWorkflowRepository;
import fr.progilone.pgcn.service.document.BibliographicRecordService;
import fr.progilone.pgcn.service.document.DocUnitService;
import fr.progilone.pgcn.service.document.conditionreport.ConditionReportService;
import fr.progilone.pgcn.service.user.UserService;
import fr.progilone.pgcn.service.util.NumahopCollectors;

/**
 * 
 * @author jbrunet
 * Créé le 16 oct. 2017
 */
@RunWith(MockitoJUnitRunner.class)
public class WorkflowServiceTest {
    
    private static final String LOGIN_USER1 = "user1";
    private static final String LOGIN_PRESTA = "presta";

    private final static String SYSTEM_LOGIN = "system";
    
    private WorkflowService service;
    
    private DocUnitWorkflowService docUnitWorkflowService;
    @Mock
    private DocUnitWorkflowRepository docUnitWorkflowRepository;
    @Mock
    private DocUnitStateRepository docUnitStateRepository;
    @Mock
    private WorkflowGroupService workflowGroupService;
    @Mock
    private DocUnitService docUnitService;
    @Mock
    private BibliographicRecordService recordService;
    @Mock
    private UserService userService;
    @Mock
    private ConditionReportService conditionReportService;

    @Before
    public void setUp() {
        docUnitWorkflowService = new DocUnitWorkflowService(docUnitWorkflowRepository, docUnitStateRepository);
        service = new WorkflowService(docUnitWorkflowService, workflowGroupService, docUnitService, recordService, userService, conditionReportService);
        when(docUnitWorkflowService.save(any(DocUnitWorkflow.class))).then(AdditionalAnswers.returnsFirstArg());
        when(docUnitWorkflowService.save(any(DocUnitState.class))).then(AdditionalAnswers.returnsFirstArg());
    }
    
    /**
     * Vérification de la première étape (validation des constats d'état) en attente
     */
    @Test
    public void initializeWorkflowOptionnal() {
        final DocUnit doc = generateDummyDocUnit();
        final WorkflowModel model = generateModelOptionnal();
        final DocUnitWorkflow workflowInstance = service.initializeWorkflow(doc, model, null);
        // Il y a 20 étapes en tout
        assertEquals(20, workflowInstance.getStates().size());
        // Une étape étant facultative, il doit en rester 2 en attente (on a en plus l'etape de validation notices dispo des le debut maintenant)
        assertEquals(2, workflowInstance.getCurrentStates().size());
        // Ce doit être l'étape VALIDATION_CONSTAT_ETAT
        validateStateIsPending(workflowInstance, WorkflowStateKey.VALIDATION_CONSTAT_ETAT);
        // L'étape GENERATION_BORDEREAU doit être terminée par system
        // On va donc la chercher dans les étapes déroulées, elle ne doit plus être présente dans les "en cours / future")
        validateCompletedState(workflowInstance, WorkflowStateKey.GENERATION_BORDEREAU, WorkflowStateStatus.SKIPPED, SYSTEM_LOGIN);
    }

    /**
     * Test avec validation de la première étape
     */
    @Test
    public void FirstStepWorkflowOptionnal() {
        final DocUnit doc = generateDummyDocUnit();
        final WorkflowModel model = generateModelOptionnal();
        final DocUnitWorkflow workflowInstance = service.initializeWorkflow(doc, model, null);
        
        processState(workflowInstance, WorkflowStateKey.VALIDATION_NOTICES, generateDummyUser());
        
        // Récupération de l'étape en cours (PENDING)
        validateStateIsPending(workflowInstance, WorkflowStateKey.VALIDATION_CONSTAT_ETAT);
        
        // On valide les constats d'état (première étape obligatoire)
        processState(workflowInstance, WorkflowStateKey.VALIDATION_CONSTAT_ETAT, generateDummyUser());
        
        // On vérifie que l'étape est bien terminée
        validateCompletedState(workflowInstance, WorkflowStateKey.VALIDATION_CONSTAT_ETAT, WorkflowStateStatus.FINISHED, LOGIN_USER1);
        
        // On vérifie que les étapes Validation bordereau et constat d'état avant numérisation sont bien passées
        validateCompletedState(workflowInstance, WorkflowStateKey.VALIDATION_BORDEREAU_CONSTAT_ETAT, WorkflowStateStatus.SKIPPED, SYSTEM_LOGIN);
        validateCompletedState(workflowInstance, WorkflowStateKey.CONSTAT_ETAT_AVANT_NUMERISATION, WorkflowStateStatus.SKIPPED, SYSTEM_LOGIN);
        
        // En attente de numérisation doit être l'étape en cours
        validateStateIsWaitingNextCompleted(workflowInstance, WorkflowStateKey.NUMERISATION_EN_ATTENTE);
    }
    
    /**
     * Test jusqu'à la livraison
     */
    @Test
    public void DeliveryWorkflowOptionnal() {
        final DocUnit doc = generateDummyDocUnit();
        final WorkflowModel model = generateModelOptionnal();
        final DocUnitWorkflow workflowInstance = service.initializeWorkflow(doc, model, null);
        
        // on doit pouvoir valider la notice tt de suite (ert dec 2018)
        processState(workflowInstance, WorkflowStateKey.VALIDATION_NOTICES, generateDummyUser());
        
        // Récupération de l'étape en cours (PENDING)
        validateStateIsPending(workflowInstance, WorkflowStateKey.VALIDATION_CONSTAT_ETAT);
        
        // On valide les constats d'état (première étape obligatoire)
        processState(workflowInstance, WorkflowStateKey.VALIDATION_CONSTAT_ETAT, generateDummyUser());
        
        // On vérifie que l'étape est bien terminée
        validateCompletedState(workflowInstance, WorkflowStateKey.VALIDATION_CONSTAT_ETAT, WorkflowStateStatus.FINISHED, LOGIN_USER1);
        
        // On vérifie que les étapes Validation bordereau et constat d'état avant numérisation sont bien passées
        validateCompletedState(workflowInstance, WorkflowStateKey.VALIDATION_BORDEREAU_CONSTAT_ETAT, WorkflowStateStatus.SKIPPED, SYSTEM_LOGIN);
        validateCompletedState(workflowInstance, WorkflowStateKey.CONSTAT_ETAT_AVANT_NUMERISATION, WorkflowStateStatus.SKIPPED, SYSTEM_LOGIN);
        
        // En attente de numérisation doit être une étape en attente d'autre étape
        validateStateIsWaitingNextCompleted(workflowInstance, WorkflowStateKey.NUMERISATION_EN_ATTENTE);
        // La livraison doit être l'étape en cours
        validateStateIsPending(workflowInstance, WorkflowStateKey.LIVRAISON_DOCUMENT_EN_COURS);
        
        // On valide la livraison
        processState(workflowInstance, WorkflowStateKey.LIVRAISON_DOCUMENT_EN_COURS, generateDummyPrestataire());
        // On vérifie que l'étape est bien terminée
        validateCompletedState(workflowInstance, WorkflowStateKey.LIVRAISON_DOCUMENT_EN_COURS, WorkflowStateStatus.FINISHED, LOGIN_PRESTA);
        // On vérifie que l'étape de numérisation par ricochet est bien terminée
        validateCompletedState(workflowInstance, WorkflowStateKey.NUMERISATION_EN_ATTENTE, WorkflowStateStatus.FINISHED, SYSTEM_LOGIN);
        
        // Contrôles automatiques doit être l'étape en cours
        validateStateIsPending(workflowInstance, WorkflowStateKey.CONTROLES_AUTOMATIQUES_EN_COURS);
        
        // On valide l'étpe de contrôle
        processState(workflowInstance, WorkflowStateKey.CONTROLES_AUTOMATIQUES_EN_COURS, null);
        // On vérifie que l'étape est bien terminée
        validateCompletedState(workflowInstance, WorkflowStateKey.CONTROLES_AUTOMATIQUES_EN_COURS, WorkflowStateStatus.FINISHED, SYSTEM_LOGIN);
        
        // Il doit y avoir une étape en cours : contrôle qualité (renommage est passée)
        validateStateIsPending(workflowInstance, WorkflowStateKey.CONTROLE_QUALITE_EN_COURS);
    }
    
    /**
     * Vérification d'un enchaînement à partir de deux étapes
     */
    @Test
    public void initializeWorkflowComplete() {
        final DocUnit doc = generateDummyDocUnit();
        final WorkflowModel model = generateModelComplete();
        final DocUnitWorkflow workflowInstance = service.initializeWorkflow(doc, model, null);
        // Il y a 20 étapes en tout
        assertEquals(20, workflowInstance.getStates().size());
        // Il doit y avoir 3 étapes en attente
        assertEquals(3, workflowInstance.getCurrentStates().size());
        // Ce doit être l'étape VALIDATION_CONSTAT_ETAT et l'étape GENERATION_BORDEREAU et VALIDATION_NOTICES
        validateStateIsPending(workflowInstance, WorkflowStateKey.VALIDATION_CONSTAT_ETAT);
        validateStateIsPending(workflowInstance, WorkflowStateKey.GENERATION_BORDEREAU);
        validateStateIsPending(workflowInstance, WorkflowStateKey.VALIDATION_NOTICES);
        
        // On va valider la génération du bordereau et vérifier que validation constat d'état est toujours en attente
        processState(workflowInstance, WorkflowStateKey.GENERATION_BORDEREAU, generateDummyUser());
        
        // Il doit y avoir 2 étapes en attente
        assertEquals(2, workflowInstance.getCurrentStates().size());
        // Ce doit être l'étape VALIDATION_CONSTAT_ETAT 
        validateStateIsPending(workflowInstance, WorkflowStateKey.VALIDATION_CONSTAT_ETAT);
        
        // On va valider la suite et vérifier le passage à la nouvelle étape
        processState(workflowInstance, WorkflowStateKey.VALIDATION_CONSTAT_ETAT, generateDummyUser());
        
        // Il doit y avoir 2 étapes en attente
        assertEquals(2, workflowInstance.getCurrentStates().size());
        // Ce doit être l'étape VALIDATION_BORDEREAU_CONSTAT_ETAT  et VALIDATION_NOTICES
        validateStateIsPending(workflowInstance, WorkflowStateKey.VALIDATION_BORDEREAU_CONSTAT_ETAT);
        validateStateIsPending(workflowInstance, WorkflowStateKey.VALIDATION_NOTICES);
    }
    
    /**
     * Test d'annulation à partir d'un workflow complet
     */
    @Test
    public void cancelCompleteWorkflow() {
        final WorkflowModel model = generateModelComplete();
        cancelWorkflow(model);
    }
    /**
     * Test d'annulation à partir d'un workflow avec des étapes optionnelles
     */
    @Test
    public void cancelOptionnalWorkflow() {
        final WorkflowModel model = generateModelOptionnal();
        cancelWorkflow(model);
    }
    
    private void cancelWorkflow(final WorkflowModel model) {
        final DocUnit doc = generateDummyDocUnit();
        final DocUnitWorkflow workflowInstance = service.initializeWorkflow(doc, model, null);
        // Il y a 20 étapes en tout
        assertEquals(20, workflowInstance.getStates().size());
        validateStateIsPending(workflowInstance, WorkflowStateKey.VALIDATION_CONSTAT_ETAT);
        
        // Annulation du workflow
        docUnitWorkflowService.endWorkflow(workflowInstance);
        
        // On vérifie qu'aucune étape n'est en cours
        assertEquals(0, workflowInstance.getCurrentStates().size());
        // Vérification que l'étape de fin est terminée
        validateStateIsFinished(workflowInstance, WorkflowStateKey.CLOTURE_DOCUMENT);
        // Vérification d'étapes annulées
        validateStateIsCanceled(workflowInstance, WorkflowStateKey.LIVRAISON_DOCUMENT_EN_COURS);
        validateStateIsCanceled(workflowInstance, WorkflowStateKey.VALIDATION_CONSTAT_ETAT);
        validateStateIsCanceled(workflowInstance, WorkflowStateKey.PREVALIDATION_DOCUMENT);
    }
    
    @Test
    public void isStateDone() {
        final DocUnit doc = generateDummyDocUnit();
        final WorkflowModel model = generateModelComplete();
        final DocUnitWorkflow workflowInstance = service.initializeWorkflow(doc, model, null);
        processState(workflowInstance, WorkflowStateKey.GENERATION_BORDEREAU, generateDummyUser());
        when(docUnitService.findOneWithAllDependenciesForWorkflow(any(String.class))).thenReturn(doc);
        // Etat non franchi
        assertFalse(service.isStateDone(doc.getIdentifier(), WorkflowStateKey.VALIDATION_CONSTAT_ETAT));
        processState(workflowInstance, WorkflowStateKey.VALIDATION_CONSTAT_ETAT, generateDummyUser());
        
        // Mise à jour du doc avec le workflow
        doc.setWorkflow(workflowInstance);
        assertTrue(service.isStateDone(doc.getIdentifier(), WorkflowStateKey.VALIDATION_CONSTAT_ETAT));
    }

    /**
     * Vérification de la bonne complétion d'une tâche
     * @param workflow
     * @param key
     * @param status
     * @param user
     */
    private void validateCompletedState(final DocUnitWorkflow workflow, final WorkflowStateKey key, final WorkflowStateStatus status, final String user) {
        assertNull(workflow.getFutureOrRunningByKey(key));
        final List<DocUnitState> completedStates = workflow.getByKey(key);
        assertEquals(1, completedStates.size());
        final DocUnitState completedState = completedStates.get(0);
        assertEquals(user, completedState.getUser());
        assertNotNull(completedState.getStartDate());
        assertNotNull(completedState.getEndDate());
        assertEquals(status, completedState.getStatus());
    }
    
    /**
     * Vérification que l'étape est en cours
     * @param workflow
     * @param key
     */
    private void validateStateIsPending(final DocUnitWorkflow workflow, final WorkflowStateKey key) {
        checkStepState(workflow, key, WorkflowStateStatus.PENDING);
    }
    
    /**
     * Vérification que l'étape est en attente
     * @param workflow
     * @param key
     */
    private void validateStateIsWaiting(final DocUnitWorkflow workflow, final WorkflowStateKey key) {
        checkStepState(workflow, key, WorkflowStateStatus.WAITING);
    }
    
    /**
     * Vérification que l'étape est annulée
     * @param workflow
     * @param key
     */
    private void validateStateIsCanceled(final DocUnitWorkflow workflow, final WorkflowStateKey key) {
        checkStepStateEndedWithStatus(workflow, key, WorkflowStateStatus.CANCELED);
    }
    
    /**
     * Vérification que l'étape s'est terminée
     * @param workflow
     * @param key
     */
    private void validateStateIsFinished(final DocUnitWorkflow workflow, final WorkflowStateKey key) {
        checkStepStateEndedWithStatus(workflow, key, WorkflowStateStatus.FINISHED);
    }
    
    /**
     * Vérification que l'étape de numéristation est en attente passive
     * @param workflow
     * @param key
     */
    private void validateStateIsWaitingNextCompleted(final DocUnitWorkflow workflow, final WorkflowStateKey key) {
        final DocUnitState state = workflow.getByKey(key).stream().collect(NumahopCollectors.singletonCollector());
        assertNotNull(state.getStartDate());
        assertNull(state.getEndDate());
        assertEquals(WorkflowStateStatus.WAITING_NEXT_COMPLETED, state.getStatus());
    }
    
    /**
     * Vérification de l'étape (en cours)
     * @param workflow
     * @param key
     * @param status
     */
    private void checkStepState(final DocUnitWorkflow workflow, final WorkflowStateKey key, final WorkflowStateStatus status) {
        final DocUnitState state = workflow.getCurrentStates().stream().filter(step -> key.equals(step.getKey())).collect(NumahopCollectors.singletonCollector());
        assertNotNull(state.getStartDate());
        assertNull(state.getEndDate());
        assertEquals(status, state.getStatus());
    }
    
    /**
     * Vérification du déroulement d'une étape
     * @param workflow
     * @param key
     * @param status
     */
    private void checkStepStateEndedWithStatus(final DocUnitWorkflow workflow, final WorkflowStateKey key, final WorkflowStateStatus status) {
        final List<DocUnitState> states = workflow.getStates().stream()
                .filter(step -> key.equals(step.getKey()))
                .collect(Collectors.toList());
        states.forEach(state -> {
            assertNotNull(state.getStartDate());
            assertNotNull(state.getEndDate());
            assertEquals(status, state.getStatus());
        });
    }
    
    /**
     * Réalisation de la tâche
     * 
     * @param workflow
     * @param key
     * @param user
     */
    private void processState(final DocUnitWorkflow workflow, final WorkflowStateKey key, final User user) {
        final DocUnitState state = workflow.getFutureOrRunningByKey(key);
        if(state == null) {
            fail("Unable to process : " + key);
        }
        state.process(user);
    }

    /**
     * Génère un workflow avec le minimum d'étape possible
     * @return
     */
    private WorkflowModel generateModelOptionnal() {
        final WorkflowModel model = new WorkflowModel();
        model.addModelState(generateModelState(model, WorkflowStateKey.INITIALISATION_DOCUMENT, false));
        model.addModelState(generateModelState(model, WorkflowStateKey.GENERATION_BORDEREAU, true));
        model.addModelState(generateModelState(model, WorkflowStateKey.VALIDATION_CONSTAT_ETAT, false));
        model.addModelState(generateModelState(model, WorkflowStateKey.VALIDATION_BORDEREAU_CONSTAT_ETAT, true));
        model.addModelState(generateModelState(model, WorkflowStateKey.CONSTAT_ETAT_AVANT_NUMERISATION, true));
        model.addModelState(generateWaitingModelState(model, WorkflowStateKey.NUMERISATION_EN_ATTENTE));
        model.addModelState(generateModelState(model, WorkflowStateKey.CONSTAT_ETAT_APRES_NUMERISATION, true));
        model.addModelState(generateModelState(model, WorkflowStateKey.LIVRAISON_DOCUMENT_EN_COURS, false));
        model.addModelState(generateModelState(model, WorkflowStateKey.CONTROLES_AUTOMATIQUES_EN_COURS, false));
        model.addModelState(generateModelState(model, WorkflowStateKey.CONTROLE_QUALITE_EN_COURS, false));
        model.addModelState(generateModelState(model, WorkflowStateKey.PREREJET_DOCUMENT, true));
        model.addModelState(generateModelState(model, WorkflowStateKey.PREVALIDATION_DOCUMENT, true));
        model.addModelState(generateModelState(model, WorkflowStateKey.VALIDATION_DOCUMENT, false));
        model.addModelState(generateModelState(model, WorkflowStateKey.VALIDATION_NOTICES, false));
        model.addModelState(generateModelState(model, WorkflowStateKey.RAPPORT_CONTROLES, false));
        model.addModelState(generateModelState(model, WorkflowStateKey.ARCHIVAGE_DOCUMENT, true));
        model.addModelState(generateModelState(model, WorkflowStateKey.DIFFUSION_DOCUMENT, true));
        model.addModelState(generateModelState(model, WorkflowStateKey.DIFFUSION_DOCUMENT_OMEKA, true));
        model.addModelState(generateModelState(model, WorkflowStateKey.DIFFUSION_DOCUMENT_LOCALE, true));
        model.addModelState(generateModelState(model, WorkflowStateKey.CLOTURE_DOCUMENT, false));
        return model;
    }
    
    /**
     * Génère un workflow avec le maximum d'étape possible
     * @return
     */
    private WorkflowModel generateModelComplete() {
        final WorkflowModel model = new WorkflowModel();
        model.addModelState(generateModelState(model, WorkflowStateKey.INITIALISATION_DOCUMENT, false));
        model.addModelState(generateModelState(model, WorkflowStateKey.GENERATION_BORDEREAU, false));
        model.addModelState(generateModelState(model, WorkflowStateKey.VALIDATION_CONSTAT_ETAT, false));
        model.addModelState(generateModelState(model, WorkflowStateKey.VALIDATION_BORDEREAU_CONSTAT_ETAT, false));
        model.addModelState(generateModelState(model, WorkflowStateKey.CONSTAT_ETAT_AVANT_NUMERISATION, false));
        model.addModelState(generateWaitingModelState(model, WorkflowStateKey.NUMERISATION_EN_ATTENTE));
        model.addModelState(generateModelState(model, WorkflowStateKey.CONSTAT_ETAT_APRES_NUMERISATION, false));
        model.addModelState(generateModelState(model, WorkflowStateKey.LIVRAISON_DOCUMENT_EN_COURS, false));
        model.addModelState(generateModelState(model, WorkflowStateKey.CONTROLES_AUTOMATIQUES_EN_COURS, false));
        model.addModelState(generateModelState(model, WorkflowStateKey.CONTROLE_QUALITE_EN_COURS, false));
        model.addModelState(generateModelState(model, WorkflowStateKey.PREREJET_DOCUMENT, false));
        model.addModelState(generateModelState(model, WorkflowStateKey.PREVALIDATION_DOCUMENT, false));
        model.addModelState(generateModelState(model, WorkflowStateKey.VALIDATION_DOCUMENT, false));
        model.addModelState(generateModelState(model, WorkflowStateKey.VALIDATION_NOTICES, false));
        model.addModelState(generateModelState(model, WorkflowStateKey.RAPPORT_CONTROLES, false));
        model.addModelState(generateModelState(model, WorkflowStateKey.ARCHIVAGE_DOCUMENT, false));
        model.addModelState(generateModelState(model, WorkflowStateKey.DIFFUSION_DOCUMENT, false));
        model.addModelState(generateModelState(model, WorkflowStateKey.DIFFUSION_DOCUMENT_OMEKA, false));
        model.addModelState(generateModelState(model, WorkflowStateKey.DIFFUSION_DOCUMENT_LOCALE, false));
        model.addModelState(generateModelState(model, WorkflowStateKey.CLOTURE_DOCUMENT, false));
        return model;
    }

    /**
     * Génère un modèle d'étape
     * @param model
     * @param key
     * @param toSkip
     * @return
     */
    private WorkflowModelState generateModelState(final WorkflowModel model, final WorkflowStateKey key, final boolean toSkip) {
        final WorkflowModelState state = new WorkflowModelState();
        state.setKey(key);
        if(toSkip) {
            state.setType(WorkflowModelStateType.TO_SKIP);
        } else {
            state.setType(WorkflowModelStateType.REQUIRED);
        }
        state.setModel(model);
        return state;
    }
    
    /**
     * Génère une étape d'attente
     * @param model
     * @param key
     * @return
     */
    private WorkflowModelState generateWaitingModelState(final WorkflowModel model, final WorkflowStateKey key) {
        final WorkflowModelState state = new WorkflowModelState();
        state.setKey(key);
        state.setType(WorkflowModelStateType.TO_WAIT);
        state.setModel(model);
        return state;
    }

    /**
     * Génère une {@link DocUnit} de test
     * @return
     */
    private DocUnit generateDummyDocUnit() {
        final DocUnit doc = new DocUnit();
        doc.setIdentifier("id");
        return doc;
    }
    
    /**
     * gènère un {@link User} de test
     * @return
     */
    private User generateDummyUser() {
        final User user = new User();
        user.setLogin(LOGIN_USER1);
        return user;
    }
    
    /**
     * génère un {@link User} de test de type prestataire
     * @return
     */
    private User generateDummyPrestataire() {
        final User user = new User();
        user.setLogin(LOGIN_PRESTA);
        return user;
    }
}
