package fr.progilone.pgcn.domain.workflow.state;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

import fr.progilone.pgcn.domain.user.User;
import fr.progilone.pgcn.domain.workflow.DocUnitState;
import fr.progilone.pgcn.domain.workflow.WorkflowStateKey;
import fr.progilone.pgcn.domain.workflow.WorkflowStateStatus;

@Entity
@DiscriminatorValue(value = WorkflowStateKey.Values.CONTROLE_QUALITE_EN_COURS)
public class ControlesQualiteState extends DocUnitState {

    @Override
    public WorkflowStateKey getKey() {
        return WorkflowStateKey.CONTROLE_QUALITE_EN_COURS;
    }

    @Override
    public void process(final User user) {
        processEndDate();
        processUser(user);
        processStatus();

        // Initialisation de la prochaine étape si applicable (aucune étape en cours de la branche)
        final List<DocUnitState> currentStates = getWorkflow().getCurrentStates();
        if(currentStates.isEmpty()
                || isWaitingStateForWorkflow(currentStates) ) {
            final DocUnitState preRej = getWorkflow().getFutureOrRunningByKey(WorkflowStateKey.PREREJET_DOCUMENT);
            final DocUnitState preVal = getWorkflow().getFutureOrRunningByKey(WorkflowStateKey.PREVALIDATION_DOCUMENT);
            preRej.initializeState(null, null, WorkflowStateStatus.CANCELED);
            preVal.initializeState(null, null, null);

            // Si on passe directement à l'étape de validation, on la valide
            if(preVal.isSkippedOrCanceled()) {
                final DocUnitState valState = getWorkflow().getFutureOrRunningByKey(WorkflowStateKey.VALIDATION_DOCUMENT);
                if(valState != null) {
                    valState.initializeState(null, null, null);
                    valState.process(user);
                }
            }
        }
    }

    @Override
    protected List<DocUnitState> getNextStates() {
        final List<DocUnitState> states = new ArrayList<>();
        states.add(getWorkflow().getFutureOrRunningByKey(WorkflowStateKey.PREREJET_DOCUMENT));
        states.add(getWorkflow().getFutureOrRunningByKey(WorkflowStateKey.PREVALIDATION_DOCUMENT));
        cleanNullStates(states);
        return states;
    }

    @Override
    public void reject(final User user) {
        processEndDate();
        processUser(user);
        failStatus();
        final DocUnitState preRej = getWorkflow().getFutureOrRunningByKey(WorkflowStateKey.PREREJET_DOCUMENT);
        final DocUnitState preVal = getWorkflow().getFutureOrRunningByKey(WorkflowStateKey.PREVALIDATION_DOCUMENT);
        preVal.initializeState(null, null, WorkflowStateStatus.CANCELED);
        preRej.initializeState(null, null, null);

        // Si on passe directement à l'étape de rejet, c'est rejeté
        if(preRej.isSkippedOrCanceled()) {
            final DocUnitState val = getWorkflow().getFutureOrRunningByKey(WorkflowStateKey.VALIDATION_DOCUMENT);
            val.reject(user);
        }
    }

    private boolean isWaitingStateForWorkflow(final List<DocUnitState> currentStates) {
        boolean valNotice = false;
        boolean rapControles = false;
        for(final DocUnitState state : currentStates) {
            if(WorkflowStateKey.VALIDATION_NOTICES.equals(state.getKey())) {
                valNotice = true;
            }
            if(WorkflowStateKey.RAPPORT_CONTROLES.equals(state.getKey())) {
                rapControles = true;
            }
        }
        if(rapControles && valNotice) {
            return currentStates.size() == 2;
        } else if(rapControles != valNotice) {
            return currentStates.size() == 1;
        } else {
            return false;
        }
    }

}
