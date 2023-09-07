package fr.progilone.pgcn.domain.workflow.state;

import fr.progilone.pgcn.domain.user.User;
import fr.progilone.pgcn.domain.workflow.DocUnitState;
import fr.progilone.pgcn.domain.workflow.WorkflowStateKey;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import java.util.ArrayList;
import java.util.List;

@Entity
@DiscriminatorValue(value = WorkflowStateKey.Values.LIVRAISON_DOCUMENT_EN_COURS)
public class LivraisonDocumentEnCoursState extends DocUnitState {

    @Override
    public WorkflowStateKey getKey() {
        return WorkflowStateKey.LIVRAISON_DOCUMENT_EN_COURS;
    }

    @Override
    public void process(final User user) {
        processEndDate();
        processUser(user);
        processStatus();
        // Si cette étape est validée, il faut valider NUMERISATION_EN_ATTENTE
        final DocUnitState numState = getWorkflow().getCurrentStateByKey(WorkflowStateKey.NUMERISATION_EN_ATTENTE);
        if (numState != null) {
            numState.process(null);
        }
        // Initialisation de la prochaine étape si applicable (aucune étape en cours)
        final List<DocUnitState> currentStates = getWorkflow().getCurrentStates();
        if (currentStates.isEmpty() || (currentStates.size() == 1 && WorkflowStateKey.VALIDATION_NOTICES == currentStates.get(0).getKey())) {
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
