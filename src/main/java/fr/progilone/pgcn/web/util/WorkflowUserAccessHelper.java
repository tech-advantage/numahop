package fr.progilone.pgcn.web.util;

import fr.progilone.pgcn.domain.util.CustomUserDetails;
import fr.progilone.pgcn.domain.workflow.WorkflowStateKey;
import fr.progilone.pgcn.exception.PgcnBusinessException;
import fr.progilone.pgcn.exception.message.PgcnError;
import fr.progilone.pgcn.exception.message.PgcnErrorCode;
import fr.progilone.pgcn.security.SecurityUtils;
import fr.progilone.pgcn.service.workflow.WorkflowService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * Regroupement des vérifications d'accès pour le workflow
 */
@Component
public class WorkflowUserAccessHelper {

    private final AccessHelper accessHelper;
    private final WorkflowService workflowService;

    @Autowired
    public WorkflowUserAccessHelper(final AccessHelper accessHelper, final WorkflowService workflowService) {
        this.accessHelper = accessHelper;
        this.workflowService = workflowService;
    }

    @Transactional(readOnly = true)
    public boolean canCurrentUserProcessTask(String docUnitId, WorkflowStateKey key) {
        final CustomUserDetails currentUser = SecurityUtils.getCurrentUser();
        return accessHelper.checkCurrentUser(currentUser).orElseGet(() -> {
            if (!workflowService.canUserProcessState(currentUser.getIdentifier(), docUnitId, key)) {
                final PgcnError.Builder builder = new PgcnError.Builder();
                throw new PgcnBusinessException(builder.reinit().setCode(PgcnErrorCode.WORKFLOW_PROCESS_NO_RIGHTS).build());
            }
            return true;
        });
    }

    @Transactional(readOnly = true)
    public boolean canCurrentUserProcessCheck(String identifier) {
        if (workflowService.isStateRunning(identifier, WorkflowStateKey.CONTROLE_QUALITE_EN_COURS)) {
            return canCurrentUserProcessTask(identifier, WorkflowStateKey.CONTROLE_QUALITE_EN_COURS);
        } else {
            return (workflowService.isStateDone(identifier, WorkflowStateKey.PREVALIDATION_DOCUMENT) || workflowService.isStateDone(identifier, WorkflowStateKey.PREREJET_DOCUMENT))
                   && canCurrentUserProcessTask(identifier, WorkflowStateKey.VALIDATION_DOCUMENT);
        }

    }
}
