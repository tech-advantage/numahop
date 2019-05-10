package fr.progilone.pgcn.domain.workflow.state;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

import fr.progilone.pgcn.domain.user.User;
import fr.progilone.pgcn.domain.workflow.DocUnitState;
import fr.progilone.pgcn.domain.workflow.WorkflowStateKey;

@Entity
@DiscriminatorValue(value = WorkflowStateKey.Values.RELIVRAISON_DOCUMENT_EN_COURS)
public class ReLivraisonDocumentEnCoursState extends DocUnitState {

    @Override
    public WorkflowStateKey getKey() {
        return WorkflowStateKey.RELIVRAISON_DOCUMENT_EN_COURS;
    }

    @Override
    public void process(final User user) {
        processEndDate();
        processUser(user);
        processStatus();

        // Initialisation de la prochaine étape si applicable (aucune étape en cours)
        final List<DocUnitState> currentStates = getWorkflow().getCurrentStates();
        if(currentStates.isEmpty() 
                || (currentStates.size() == 1 
                        && WorkflowStateKey.VALIDATION_NOTICES == currentStates.get(0).getKey())) {
            getNextStates().forEach(state -> state.initializeState(null, null, null));
        }
    }

    @Override
    protected List<DocUnitState> getNextStates() {
        final List<DocUnitState> states = new ArrayList<>();
        states.add(getWorkflow().getFutureOrRunningByKey(WorkflowStateKey.CONTROLES_AUTOMATIQUES_EN_COURS));
        cleanNullStates(states);
        return states;
    }

    @Override
    public void reject(final User user) {
        // TODO Auto-generated method stub

    }

}
