package fr.progilone.pgcn.service.workflow;

import java.util.Collection;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import fr.progilone.pgcn.domain.library.Library;
import fr.progilone.pgcn.domain.user.User;
import fr.progilone.pgcn.domain.workflow.WorkflowGroup;
import fr.progilone.pgcn.exception.PgcnValidationException;
import fr.progilone.pgcn.exception.message.PgcnError;
import fr.progilone.pgcn.exception.message.PgcnErrorCode;
import fr.progilone.pgcn.exception.message.PgcnList;
import fr.progilone.pgcn.repository.workflow.WorkflowGroupRepository;
import fr.progilone.pgcn.security.SecurityUtils;
import fr.progilone.pgcn.service.util.SortUtils;

@Service
public class WorkflowGroupService {

    private final WorkflowGroupRepository repository;
    private final WorkflowGroupValidationService validationService;

    @Autowired
    public WorkflowGroupService(final WorkflowGroupRepository repository, final WorkflowGroupValidationService validationService) {
        this.repository = repository;
        this.validationService = validationService;
    }

    /**
     * Sauvegarde un groupe
     *
     * @param group
     * @return
     * @throws PgcnValidationException
     */
    @Transactional
    public WorkflowGroup save(final WorkflowGroup group) throws PgcnValidationException {
        validationService.validate(group);
        final WorkflowGroup savedGroup = repository.save(group);
        return getOne(savedGroup.getIdentifier());
    }

    /**
     * Retourne un groupe
     *
     * @param identifier
     * @return
     */
    @Transactional(readOnly = true)
    public WorkflowGroup getOne(final String identifier) {
        return repository.findByIdentifier(identifier);
    }

    /**
     * Indique si un utilisateur est présent dans le groupe
     *
     * @param identifier
     * @param user
     * @return
     */
    @Transactional(readOnly = true)
    public boolean isUserAuthorized(final String identifier, final User user) {
        return isUserAuthorized(getOne(identifier), user.getIdentifier());
    }

    /**
     * Retourne vrai si le userId appartient au {@link WorkflowGroup}
     *
     * @param group
     * @param userId
     * @return
     */
    @Transactional(readOnly = true)
    public boolean isUserAuthorized(final WorkflowGroup group, final String userId) {
        if (userId == null || group == null) {
            return false;
        }
        for (final User userInGroup : group.getUsers()) {
            if (StringUtils.equalsIgnoreCase(userId, userInGroup.getIdentifier())) {
                return true;
            }
        }
        return false;
    }

    @Transactional(readOnly = true)
    public Page<WorkflowGroup> search(final String search, final String initiale, final List<String> libraries, final Integer page, final Integer size, final List<String> sorts) {

        final Sort sort = SortUtils.getSort(sorts);
        final Pageable pageRequest = new PageRequest(page, size, sort);

        if (libraries.isEmpty() && SecurityUtils.getCurrentUser().getLibraryId() != null) {
            libraries.add(SecurityUtils.getCurrentUser().getLibraryId());
        }

        return repository.search(search, initiale, libraries, pageRequest);
    }

    /**
     * Suppression de groupe
     *
     * @param identifier
     */
    @Transactional
    public void delete(final String identifier) {
        repository.delete(identifier);
    }
    
    /**
     * Validation
     * @param group
     * @return
     * @throws PgcnValidationException
     */
    @Transactional(readOnly = true)
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
            final Long countDuplicates = group.getIdentifier() == null ?
                    repository.countByName(name) :
                    repository.countByNameAndIdentifierNot(name, group.getIdentifier());
            if (countDuplicates > 0) {
                errors.add(builder.reinit()
                                  .setCode(PgcnErrorCode.WORKFLOW_GROUP_DUPLICATE_NAME)
                                  .setField("name")
                                  .build());
            }
        }
        
        // Vérification des utilisateurs (même bibliothèque)
        if(!group.getUsers().isEmpty()) {
            Library lib = null;
            for(final User user : group.getUsers()) {
                if(lib == null) {
                    lib = user.getLibrary();
                } else {
                    if(!StringUtils.equals(lib.getIdentifier(), user.getLibrary().getIdentifier())) {
                        errors.add(builder.reinit()
                                .setCode(PgcnErrorCode.WORKFLOW_GROUP_MIXED_USERS)
                                .build());
                        break;
                    }
                }
            }
        }

        /** Retour **/
        if (!errors.isEmpty()) {
            group.setErrors(errors);
            throw new PgcnValidationException(group, errors);
        }
        return errors;
    }

    @Transactional(readOnly = true)
    public Collection<WorkflowGroup> findAllForLibrary(final String identifier) {
        return repository.findAllByLibraryIdentifier(identifier);
    }
    
    public List<WorkflowGroup> findAllGroupsByUser(final User user) {
        return repository.findAllGroupsByUser(user);
    }
}
