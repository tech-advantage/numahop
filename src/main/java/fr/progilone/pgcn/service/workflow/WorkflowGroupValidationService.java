package fr.progilone.pgcn.service.workflow;

import fr.progilone.pgcn.domain.library.Library;
import fr.progilone.pgcn.domain.user.User;
import fr.progilone.pgcn.domain.workflow.WorkflowGroup;
import fr.progilone.pgcn.exception.PgcnValidationException;
import fr.progilone.pgcn.exception.message.PgcnError;
import fr.progilone.pgcn.exception.message.PgcnErrorCode;
import fr.progilone.pgcn.exception.message.PgcnList;
import fr.progilone.pgcn.repository.workflow.WorkflowGroupRepository;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class WorkflowGroupValidationService {

    private final WorkflowGroupRepository repository;

    @Autowired
    public WorkflowGroupValidationService(final WorkflowGroupRepository repository) {
        this.repository = repository;
    }

    /**
     * Validation
     *
     * @param group
     * @return
     * @throws PgcnValidationException
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW, readOnly = true)
    public PgcnList<PgcnError> validate(final WorkflowGroup group) throws PgcnValidationException {
        final PgcnList<PgcnError> errors = new PgcnList<>();
        final PgcnError.Builder builder = new PgcnError.Builder();
        final String name = group.getName();

        // le nom est obligatoire
        if (StringUtils.isBlank(name)) {
            errors.add(builder.reinit().setCode(PgcnErrorCode.WORKFLOW_GROUP_NAME_MANDATORY).setField("name").build());
        }
        // nom unique
        else {
            final Long countDuplicates =
                group.getIdentifier() == null ? repository.countByName(name) : repository.countByNameAndIdentifierNot(name, group.getIdentifier());

            if (countDuplicates > 0) {
                errors.add(builder.reinit().setCode(PgcnErrorCode.WORKFLOW_GROUP_DUPLICATE_NAME).setField("name").build());
            }
        }

        // Vérification des utilisateurs (même bibliothèque)
//        if (!group.getUsers().isEmpty()) {
//            Library lib = null;
//            for (User user : group.getUsers()) {
//                if (lib == null) {
//                    lib = user.getLibrary();
//                } else {
//                    if (!StringUtils.equals(lib.getIdentifier(), user.getLibrary().getIdentifier())) {
//                        errors.add(builder.reinit().setCode(PgcnErrorCode.WORKFLOW_GROUP_MIXED_USERS).build());
//                        break;
//                    }
//                }
//            }
//        }

        /* Retour **/
        if (!errors.isEmpty()) {
            group.setErrors(errors);
            throw new PgcnValidationException(group, errors);
        }
        return errors;
    }
}
