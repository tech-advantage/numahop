package fr.progilone.pgcn.service.workflow;

import fr.progilone.pgcn.domain.user.User;
import fr.progilone.pgcn.domain.workflow.WorkflowGroup;
import fr.progilone.pgcn.exception.PgcnValidationException;
import fr.progilone.pgcn.repository.workflow.WorkflowGroupRepository;
import fr.progilone.pgcn.service.util.SortUtils;
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
        final Pageable pageRequest = PageRequest.of(page, size, sort);

        return repository.search(search, initiale, libraries, pageRequest);
    }

    /**
     * Suppression de groupe
     *
     * @param identifier
     */
    @Transactional
    public void delete(final String identifier) {
        repository.deleteById(identifier);
    }

    @Transactional(readOnly = true)
    public Collection<WorkflowGroup> findAllForLibrary(final String identifier) {
        return repository.findAllByLibraryIdentifier(identifier);
    }

    public List<WorkflowGroup> findAllGroupsByUser(final User user) {
        return repository.findAllGroupsByUser(user);
    }
}
