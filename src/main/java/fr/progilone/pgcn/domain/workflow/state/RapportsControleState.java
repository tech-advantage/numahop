package fr.progilone.pgcn.domain.workflow.state;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

import fr.progilone.pgcn.domain.user.User;
import fr.progilone.pgcn.domain.workflow.DocUnitState;
import fr.progilone.pgcn.domain.workflow.WorkflowStateKey;
import fr.progilone.pgcn.domain.workflow.WorkflowStateStatus;

@Entity
@DiscriminatorValue(value = WorkflowStateKey.Values.RAPPORT_CONTROLES)
public class RapportsControleState extends DocUnitState {

    @Override
    public WorkflowStateKey getKey() {
        return WorkflowStateKey.RAPPORT_CONTROLES;
    }

    @Override
    public void process(final User user) {
        processEndDate();
        processUser(user);
        processStatus();

        handleWorkflow();
    }

    @Override
    protected List<DocUnitState> getNextStates() {
        final List<DocUnitState> states = new ArrayList<>();
        
        if (getWorkflow().isDocumentValidated()) {
            states.add(getWorkflow().getFutureOrRunningByKey(WorkflowStateKey.ARCHIVAGE_DOCUMENT));
            states.add(getWorkflow().getFutureOrRunningByKey(WorkflowStateKey.DIFFUSION_DOCUMENT));
            states.add(getWorkflow().getFutureOrRunningByKey(WorkflowStateKey.DIFFUSION_DOCUMENT_OMEKA));
            states.add(getWorkflow().getFutureOrRunningByKey(WorkflowStateKey.DIFFUSION_DOCUMENT_DIGITAL_LIBRARY));
            states.add(getWorkflow().getFutureOrRunningByKey(WorkflowStateKey.DIFFUSION_DOCUMENT_LOCALE));
        }
        cleanNullStates(states);
        return states;
    }

    @Override
    public void reject(final User user) {
        processEndDate();
        processUser(user);
        failStatus();

        handleWorkflow();
    }

    private void handleWorkflow() {
        if(getWorkflow().isDocumentRejected()) {
            // On annule toutes les étapes (courantes et à venir)
            getWorkflow().getCurrentStates().forEach(state -> state.initializeState(null, null, WorkflowStateStatus.CANCELED));
            getNextStates().forEach(state -> state.initializeState(null, null, WorkflowStateStatus.CANCELED));
            // On clôture le workflow
            final DocUnitState cloture = getWorkflow().getFutureOrRunningByKey(WorkflowStateKey.CLOTURE_DOCUMENT);
            cloture.initializeState(null, null, WorkflowStateStatus.FINISHED);
        } else {
            // Initialisation de la prochaine étape si applicable (aucune étape en cours)
            if(getWorkflow().getCurrentStates().isEmpty() 
                    && getWorkflow().isDocumentValidated() 
                    && getWorkflow().isNoticeValidated()) {
                getNextStates().stream()
                    .filter(Objects::nonNull)
                    .forEach(state -> state.initializeState(null, null, null));
            }
        }
    }

}
