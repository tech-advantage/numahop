package fr.progilone.pgcn.domain.workflow.state;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

import fr.progilone.pgcn.domain.user.User;
import fr.progilone.pgcn.domain.workflow.DocUnitState;
import fr.progilone.pgcn.domain.workflow.WorkflowStateKey;

@Entity
@DiscriminatorValue(value = WorkflowStateKey.Values.VALIDATION_NOTICES)
public class ValidationNoticesState extends DocUnitState {

    @Override
    public WorkflowStateKey getKey() {
        return WorkflowStateKey.VALIDATION_NOTICES;
    }

    @Override
    public void process(final User user) {
        processEndDate();
        processUser(user);
        processStatus();

        // Initialisation de la prochaine étape si applicable (aucune étape en cours)
        if(getWorkflow().getCurrentStates().isEmpty()) {
            getNextStates().forEach(state -> state.initializeState(null, null, null));
        }
    }

    @Override
    protected List<DocUnitState> getNextStates() {
        final List<DocUnitState> states = new ArrayList<>();
        states.add(getWorkflow().getFutureOrRunningByKey(WorkflowStateKey.ARCHIVAGE_DOCUMENT));
        states.add(getWorkflow().getFutureOrRunningByKey(WorkflowStateKey.DIFFUSION_DOCUMENT));
        states.add(getWorkflow().getFutureOrRunningByKey(WorkflowStateKey.DIFFUSION_DOCUMENT_OMEKA));
        states.add(getWorkflow().getFutureOrRunningByKey(WorkflowStateKey.DIFFUSION_DOCUMENT_LOCALE));
        cleanNullStates(states);
        return states;
    }

    @Override
    public void reject(final User user) {
        // TODO Auto-generated method stub

    }

}
