package fr.progilone.pgcn.web.util;

import fr.progilone.pgcn.domain.document.conditionreport.ConditionReportDetail.Type;
import fr.progilone.pgcn.domain.dto.workflow.StateIsDoneDTO;
import fr.progilone.pgcn.domain.lot.Lot;
import fr.progilone.pgcn.domain.workflow.WorkflowGroup;
import fr.progilone.pgcn.domain.workflow.WorkflowStateKey;
import fr.progilone.pgcn.exception.PgcnBusinessException;
import fr.progilone.pgcn.exception.message.PgcnError;
import fr.progilone.pgcn.exception.message.PgcnErrorCode;
import fr.progilone.pgcn.service.lot.LotService;
import fr.progilone.pgcn.service.workflow.WorkflowGroupService;
import fr.progilone.pgcn.service.workflow.WorkflowService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * Regroupement des vérifications de traitement lié au workflow (déconnecté de l'utilisateur)
 */
@Component
public class WorkflowAccessHelper {

    private final LotService lotService;
    private final WorkflowService workflowService;
    private final WorkflowGroupService workflowGroupService;

    @Autowired
    public WorkflowAccessHelper(final LotService lotService, final WorkflowService workflowService, final WorkflowGroupService workflowGroupService) {
        this.lotService = lotService;
        this.workflowService = workflowService;
        this.workflowGroupService = workflowGroupService;
    }

    /**
     * Permet de déterminer si le document est livrable (étape du workflow à livraison ou pas de workflow)
     *
     * @param docUnitId
     * @return
     */
    @Transactional(readOnly = true)
    public boolean canDocUnitBeDelivered(final String docUnitId) {
        return !workflowService.isWorkflowRunning(docUnitId) || workflowService.isStateRunning(docUnitId, WorkflowStateKey.LIVRAISON_DOCUMENT_EN_COURS)
               || workflowService.isStateRunning(docUnitId, WorkflowStateKey.RELIVRAISON_DOCUMENT_EN_COURS);
    }

    /**
     * Permet de voir si un lot peut être validé
     *
     * @param lotId
     * @return
     */
    @Transactional(readOnly = true)
    public boolean canLotBeValidated(final String lotId) {
        final Lot lot = lotService.findByIdentifier(lotId);

        if (lot == null) {
            return false;
        }
        // Si le lot n'a pas de workflow associé on ne peut pas le valider
        if (lotService.getWorkflowModel(lot) == null) {
            final PgcnError.Builder builder = new PgcnError.Builder();
            throw new PgcnBusinessException(builder.reinit().setCode(PgcnErrorCode.WORKFLOW_MODEL_MISSING).build());
        }
        // Si le lot ne contient pas de docUnit on ne peut pas le valider
        if (lot.getDocUnits() == null || lot.getDocUnits().isEmpty()) {
            final PgcnError.Builder builder = new PgcnError.Builder();
            throw new PgcnBusinessException(builder.reinit().setCode(PgcnErrorCode.WORKFLOW_LOT_DOCUNIT_MISSING).build());
        }
        return Lot.LotStatus.CREATED.equals(lot.getStatus());
    }

    /**
     * Autorisation de suppression d'un constat d'état si le workflow n'est pas avancé dessus
     *
     * @param docUnitId
     * @return
     */
    @Transactional(readOnly = true)
    public boolean canConstatBeDeleted(final String docUnitId) {

        if (!isDocUnitLockedByWorkflow(docUnitId)) {
            return true;
        }
        return workflowService.isStateSkippedOrRunning(docUnitId, WorkflowStateKey.VALIDATION_CONSTAT_ETAT) && workflowService.isStateSkippedOrRunning(docUnitId,
                                                                                                                                                       WorkflowStateKey.CONSTAT_ETAT_AVANT_NUMERISATION)
               && workflowService.isStateSkippedOrRunning(docUnitId, WorkflowStateKey.CONSTAT_ETAT_APRES_NUMERISATION);
    }

    /**
     * Vérifie si un élément lié à une étape peut être supprimée, ie l'étape n'est pas terminée
     *
     * @param docUnitId
     * @param key
     * @return
     */
    @Transactional(readOnly = true)
    public boolean canElementLinkToStateBeDeleted(final String docUnitId, final WorkflowStateKey key) {
        return !isDocUnitLockedByWorkflow(docUnitId) || workflowService.isStateSkippedOrRunning(docUnitId, key);
    }

    /**
     * Vérifie si une étape de constat peut être supprimée ou modifiée
     *
     * @param docUnitId
     * @param type
     * @return
     */
    @Transactional(readOnly = true)
    public boolean canConstatDetailBeModified(final String docUnitId, final Type type) {
        switch (type) {
            case LIBRARY_LEAVING:
                return canElementLinkToStateBeDeleted(docUnitId, WorkflowStateKey.VALIDATION_CONSTAT_ETAT);
            case PROVIDER_RECEPTION:
                return canElementLinkToStateBeDeleted(docUnitId, WorkflowStateKey.CONSTAT_ETAT_AVANT_NUMERISATION);
            case DIGITALIZATION:
                return canElementLinkToStateBeDeleted(docUnitId, WorkflowStateKey.CONSTAT_ETAT_APRES_NUMERISATION);
            case LIBRARY_BACK:
                return true;
            default:
                return true;

        }
    }

    /**
     * Vérifie si la notice peut encore être modifiée
     *
     * @param docUnitId
     * @return
     */
    @Transactional(readOnly = true)
    public boolean canRecordBeModified(final String docUnitId) {
        return canElementLinkToStateBeDeleted(docUnitId, WorkflowStateKey.VALIDATION_NOTICES);
    }

    /**
     * Vérifie si les contrôles peuvent être réalisés
     *
     * @param docUnitId
     * @return
     */
    @Transactional(readOnly = true)
    public boolean canCheckBePerformed(final String docUnitId) {
        return workflowService.isStateRunning(docUnitId, WorkflowStateKey.CONTROLE_QUALITE_EN_COURS) || workflowService.isStateRunning(docUnitId,
                                                                                                                                       WorkflowStateKey.VALIDATION_DOCUMENT);
    }

    /**
     * Détermine si un groupe de workflow peut être supprimé
     *
     * @param id
     * @return
     */
    @Transactional(readOnly = true)
    public boolean canWorkflowGroupBeDeleted(final String id) {
        final WorkflowGroup group = workflowGroupService.getOne(id);
        return workflowService.findAllRunningOrPendingStateForGroup(group).isEmpty();
    }

    @Transactional(readOnly = true)
    public boolean isDocUnitLockedByWorkflow(final String id) {
        return workflowService.isWorkflowRunning(id);
    }

    /**
     * Permet de savoir si on peut toujours affecter la docUnit
     * liée au constat à un train
     * (1 constat et 1 workflow non encore demarré ou n'ayant pas depassé l'etape 'En attente de numerisation)
     *
     * @param identifier
     * @return
     */
    @Transactional(readOnly = true)
    public StateIsDoneDTO canChangeTrain(final String identifier) {
        final StateIsDoneDTO result = new StateIsDoneDTO();
        result.setDone((!workflowService.isWorkflowRunning(identifier)) || (workflowService.isWorkflowRunning(identifier) && workflowService.areStatesRunning(identifier,
                                                                                                                                                              WorkflowStateKey.GENERATION_BORDEREAU,
                                                                                                                                                              WorkflowStateKey.VALIDATION_CONSTAT_ETAT,
                                                                                                                                                              WorkflowStateKey.VALIDATION_BORDEREAU_CONSTAT_ETAT,
                                                                                                                                                              WorkflowStateKey.CONSTAT_ETAT_AVANT_NUMERISATION)));
        return result;
    }

}
