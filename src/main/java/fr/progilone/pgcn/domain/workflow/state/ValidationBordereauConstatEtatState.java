package fr.progilone.pgcn.domain.workflow.state;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

import fr.progilone.pgcn.domain.user.User;
import fr.progilone.pgcn.domain.workflow.DocUnitState;
import fr.progilone.pgcn.domain.workflow.WorkflowStateKey;

@Entity
@DiscriminatorValue(value = WorkflowStateKey.Values.VALIDATION_BORDEREAU_CONSTAT_ETAT)
public class ValidationBordereauConstatEtatState extends DocUnitState {

    @Override
    public WorkflowStateKey getKey() {
        return WorkflowStateKey.VALIDATION_BORDEREAU_CONSTAT_ETAT;
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
        states.add(getWorkflow().getFutureOrRunningByKey(WorkflowStateKey.CONSTAT_ETAT_AVANT_NUMERISATION));
        cleanNullStates(states);
        return states;
    }

    @Override
    public void reject(final User user) {
        // TODO Auto-generated method stub

    }

}
