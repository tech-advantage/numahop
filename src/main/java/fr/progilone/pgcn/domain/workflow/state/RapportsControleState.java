package fr.progilone.pgcn.domain.workflow.state;

import fr.progilone.pgcn.domain.user.User;
import fr.progilone.pgcn.domain.workflow.DocUnitState;
import fr.progilone.pgcn.domain.workflow.WorkflowStateKey;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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
        if (getWorkflow().getCurrentStates().isEmpty() && getWorkflow().isNoticeValidated()) {
            getNextStates().stream().filter(Objects::nonNull).forEach(state -> state.initializeState(null, null, null));
        }
    }

}
