package fr.progilone.pgcn.service.workflow;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import fr.progilone.pgcn.domain.workflow.WorkflowModelState;
import fr.progilone.pgcn.domain.workflow.WorkflowModelStateType;
import fr.progilone.pgcn.exception.PgcnValidationException;
import fr.progilone.pgcn.exception.message.PgcnError;
import fr.progilone.pgcn.exception.message.PgcnErrorCode;
import fr.progilone.pgcn.exception.message.PgcnList;
import fr.progilone.pgcn.repository.workflow.WorkflowModelStateRepository;

@Service
public class WorkflowModelStateService {

    private final WorkflowModelStateRepository repository;

    @Autowired
    public WorkflowModelStateService(final WorkflowModelStateRepository repository) {
        this.repository = repository;
    }

    /**
     * Sauvegarde un modele
     * 
     * @param state
     * @return
     * @throws PgcnValidationException
     */
    @Transactional
    public WorkflowModelState save(final WorkflowModelState state) throws PgcnValidationException {
        validate(state);
        return repository.save(state);
    }

    /**
     * Retourne un modele
     * 
     * @param identifier
     * @return
     */
    @Transactional(readOnly = true)
    public WorkflowModelState getOne(final String identifier) {
        return repository.getOne(identifier);
    }

    /**
     * Suppression de modele
     * 
     * @param identifier
     */
    @Transactional
    public void delete(final String identifier) {
        repository.delete(identifier);
    }
    
    /**
     * Validation
     * @param state
     * @return
     * @throws PgcnValidationException
     */
    @Transactional(readOnly = true)
    public PgcnList<PgcnError> validate(final WorkflowModelState state) throws PgcnValidationException {
        final PgcnList<PgcnError> errors = new PgcnList<>();
        final PgcnError.Builder builder = new PgcnError.Builder();

        // le groupe est obligatoire ssi l'étape est de type REQUIRED
        if (state.getGroup() == null && WorkflowModelStateType.REQUIRED.equals(state.getType())) {
            errors.add(builder.reinit().setCode(PgcnErrorCode.WORKFLOW_MODEL_STATE_GROUP_MANDATORY).build());
        }

        /** Retour **/
        if (!errors.isEmpty()) {
            state.setErrors(errors);
            throw new PgcnValidationException(state, errors);
        }
        return errors;
    }
}
