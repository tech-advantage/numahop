package fr.progilone.pgcn.domain.workflow.state;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

import fr.progilone.pgcn.domain.user.User;
import fr.progilone.pgcn.domain.workflow.DocUnitState;
import fr.progilone.pgcn.domain.workflow.WorkflowStateKey;

@Entity
@DiscriminatorValue(value = WorkflowStateKey.Values.CONSTAT_ETAT_APRES_NUMERISATION)
public class ConstatEtatApresNumerisationState extends DocUnitState {

    @Override
    public WorkflowStateKey getKey() {
        return WorkflowStateKey.CONSTAT_ETAT_APRES_NUMERISATION;
    }

    @Override
    public void process(final User user) {
        processEndDate();
        processUser(user);
        processStatus();
        // Si cette étape est validée, il faut valider NUMERISATION_EN_ATTENTE si besoin
        if (getWorkflow().getCurrentStateByKey(WorkflowStateKey.NUMERISATION_EN_ATTENTE) != null) {
            getWorkflow().getCurrentStateByKey(WorkflowStateKey.NUMERISATION_EN_ATTENTE).process(null);
        }
        
        // Initialisation de la prochaine étape si applicable (aucune étape en cours)
        if(getWorkflow().getCurrentStates().isEmpty()) {
            getNextStates().forEach(state -> state.initializeState(null, null, null));
        }
    }

    @Override
    protected List<DocUnitState> getNextStates() {
        final List<DocUnitState> states = new ArrayList<>();
        states.add(getWorkflow().getFutureOrRunningByKey(WorkflowStateKey.LIVRAISON_DOCUMENT_EN_COURS));
        cleanNullStates(states);
        return states;
    }

    @Override
    public void reject(final User user) {
        // TODO Auto-generated method stub

    }

}
