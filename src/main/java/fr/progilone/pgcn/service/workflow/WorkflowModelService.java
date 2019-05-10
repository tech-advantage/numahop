package fr.progilone.pgcn.service.workflow;

import fr.progilone.pgcn.domain.workflow.WorkflowModel;
import fr.progilone.pgcn.exception.PgcnValidationException;
import fr.progilone.pgcn.exception.message.PgcnError;
import fr.progilone.pgcn.exception.message.PgcnErrorCode;
import fr.progilone.pgcn.exception.message.PgcnList;
import fr.progilone.pgcn.repository.lot.LotRepository;
import fr.progilone.pgcn.repository.project.ProjectRepository;
import fr.progilone.pgcn.repository.workflow.DocUnitWorkflowRepository;
import fr.progilone.pgcn.repository.workflow.WorkflowModelRepository;
import fr.progilone.pgcn.security.SecurityUtils;
import fr.progilone.pgcn.service.util.SortUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;

@Service
public class WorkflowModelService {

    private final DocUnitWorkflowRepository docUnitWorkflowRepository;
    private final LotRepository lotRepository;
    private final ProjectRepository projectRepository;
    private final WorkflowModelRepository repository;
    private final WorkflowModelStateService workflowModelStateService;

    @Autowired
    public WorkflowModelService(final DocUnitWorkflowRepository docUnitWorkflowRepository,
                                final LotRepository lotRepository,
                                final ProjectRepository projectRepository,
                                final WorkflowModelRepository repository,
                                final WorkflowModelStateService workflowModelStateService) {
        this.docUnitWorkflowRepository = docUnitWorkflowRepository;
        this.lotRepository = lotRepository;
        this.projectRepository = projectRepository;
        this.repository = repository;
        this.workflowModelStateService = workflowModelStateService;
    }

    /**
     * Sauvegarde un modele
     *
     * @param model
     * @return
     * @throws PgcnValidationException
     */
    @Transactional
    public WorkflowModel save(final WorkflowModel model) throws PgcnValidationException {
        validate(model);
        WorkflowModel savedModel = repository.save(model);
        model.getModelStates().forEach(workflowModelStateService::save);
        return savedModel;
    }

    /**
     * Retourne un modele
     *
     * @param identifier
     * @return
     */
    @Transactional(readOnly = true)
    public WorkflowModel getOne(final String identifier) {
        return repository.getOne(identifier);
    }

    @Transactional(readOnly = true)
    public Page<WorkflowModel> search(String search, final String initiale, List<String> libraries, Integer page, Integer size, List<String> sorts) {

        final Sort sort = SortUtils.getSort(sorts);
        final Pageable pageRequest = new PageRequest(page, size, sort);

        if (libraries.isEmpty() && SecurityUtils.getCurrentUser().getLibraryId() != null) {
            libraries.add(SecurityUtils.getCurrentUser().getLibraryId());
        }

        return repository.search(search, initiale, libraries, pageRequest);
    }

    /**
     * Suppression de modele
     *
     * @param identifier
     */
    @Transactional
    public void delete(final String identifier) {
        final WorkflowModel model = repository.findOne(identifier);
        validateDelete(model);

        repository.delete(model);
    }

    /**
     * Validation avant sauvegarde
     *
     * @param model
     * @return
     * @throws PgcnValidationException
     */
    @Transactional(readOnly = true)
    private PgcnList<PgcnError> validate(final WorkflowModel model) throws PgcnValidationException {
        final PgcnList<PgcnError> errors = new PgcnList<>();
        final PgcnError.Builder builder = new PgcnError.Builder();
        final String name = model.getName();

        // le nom est obligatoire
        if (StringUtils.isBlank(name)) {
            errors.add(builder.reinit().setCode(PgcnErrorCode.WORKFLOW_MODEL_NAME_MANDATORY).setField("name").build());
        }
        // nom unique
        else {
            final Long countDuplicates =
                model.getIdentifier() == null ? repository.countByName(name) : repository.countByNameAndIdentifierNot(name, model.getIdentifier());
            if (countDuplicates > 0) {
                errors.add(builder.reinit().setCode(PgcnErrorCode.WORKFLOW_MODEL_DUPLICATE_NAME).setField("name").build());
            }
        }

        /* Retour */
        if (!errors.isEmpty()) {
            model.setErrors(errors);
            throw new PgcnValidationException(model, errors);
        }
        return errors;
    }

    /**
     * Validation avant suppression
     *
     * @param model
     * @throws PgcnValidationException
     */
    private void validateDelete(final WorkflowModel model) throws PgcnValidationException {
        final PgcnList<PgcnError> errors = new PgcnList<>();
        final PgcnError.Builder builder = new PgcnError.Builder();

        // DocWorkflow
        final Long docCount = docUnitWorkflowRepository.countByModel(model);
        if (docCount > 0) {
            errors.add(builder.reinit().setCode(PgcnErrorCode.WORKFLOW_MODEL_DEL_EXITS_DOC).setAdditionalComplement(docCount).build());
        }
        // Lot
        final Long lotCount = lotRepository.countByWorkflowModel(model);
        if (lotCount > 0) {
            errors.add(builder.reinit().setCode(PgcnErrorCode.WORKFLOW_MODEL_DEL_EXITS_LOT).setAdditionalComplement(lotCount).build());
        }
        // Project
        final Long projCount = projectRepository.countByWorkflowModel(model);
        if (projCount > 0) {
            errors.add(builder.reinit().setCode(PgcnErrorCode.WORKFLOW_MODEL_DEL_EXITS_PROJ).setAdditionalComplement(projCount).build());
        }

        if (!errors.isEmpty()) {
            model.setErrors(errors);
            throw new PgcnValidationException(model, errors);
        }
    }

    @Transactional(readOnly = true)
    public Collection<WorkflowModel> findAllForLibrary(String identifier) {
        return repository.findAllByLibraryIdentifierAndActive(identifier, true);
    }
}
