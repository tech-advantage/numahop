package fr.progilone.pgcn.service.workflow.ui;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import fr.progilone.pgcn.domain.dto.workflow.SimpleWorkflowGroupDTO;
import fr.progilone.pgcn.domain.dto.workflow.WorkflowGroupDTO;
import fr.progilone.pgcn.domain.workflow.WorkflowGroup;
import fr.progilone.pgcn.exception.PgcnBusinessException;
import fr.progilone.pgcn.exception.PgcnValidationException;
import fr.progilone.pgcn.exception.message.PgcnError;
import fr.progilone.pgcn.exception.message.PgcnErrorCode;
import fr.progilone.pgcn.service.util.transaction.VersionValidationService;
import fr.progilone.pgcn.service.workflow.WorkflowGroupService;
import fr.progilone.pgcn.service.workflow.mapper.SimpleWorkflowMapper;
import fr.progilone.pgcn.service.workflow.mapper.UIWorkflowGroupMapper;
import fr.progilone.pgcn.service.workflow.mapper.WorkflowMapper;

/**
 * Service dédié à les gestion des vues des groupes de workflow
 */
@Service
public class UIWorkflowGroupService {

    private final WorkflowGroupService service;
    private final UIWorkflowGroupMapper mapper;

    @Autowired
    public UIWorkflowGroupService(final WorkflowGroupService service,
                        final UIWorkflowGroupMapper mapper) {
        this.service = service;
        this.mapper = mapper;
    }

    @Transactional
    public WorkflowGroupDTO create(final WorkflowGroupDTO dto) throws PgcnValidationException {
        final WorkflowGroup group = new WorkflowGroup();
        mapper.mapInto(dto, group);
        try {
            return WorkflowMapper.INSTANCE.groupToGroupDTO(service.save(group));
        } catch (PgcnBusinessException e) {
            e.getErrors().forEach(semanthequeError -> dto.addError(buildError(semanthequeError.getCode())));
            throw new PgcnValidationException(dto);
        }
    }

    /**
     * Mise à jour d'un lot
     *
     * @param dto un objet contenant les informations necessaires à l'enregistrement d'un lot
     * @return le lot nouvellement créée ou mise à jour
     * @throws PgcnValidationException
     */
    @Transactional
    public WorkflowGroupDTO update(final WorkflowGroupDTO dto) throws PgcnValidationException {
        final WorkflowGroup group = service.getOne(dto.getIdentifier());

        // Contrôle d'accès concurrents
        VersionValidationService.checkForStateObject(group, dto);

        mapper.mapInto(dto, group);
        try {
            return WorkflowMapper.INSTANCE.groupToGroupDTO(service.save(group));
        } catch (PgcnBusinessException e) {
            e.getErrors().forEach(semanthequeError -> dto.addError(buildError(semanthequeError.getCode())));
            throw new PgcnValidationException(dto);
        }
    }

    private PgcnError buildError(PgcnErrorCode pgcnErrorCode) {
        final PgcnError.Builder builder = new PgcnError.Builder();
        switch (pgcnErrorCode) {
            case WORKFLOW_GROUP_DUPLICATE_NAME:
                builder.setCode(pgcnErrorCode)
                       .setField("name");
                break;
            default:
                break;
        }
        return builder.build();
    }

    @Transactional(readOnly = true)
    public WorkflowGroupDTO getOne(String id) {
        return WorkflowMapper.INSTANCE.groupToGroupDTO(service.getOne(id));
    }

    @Transactional(readOnly = true)
    public Page<SimpleWorkflowGroupDTO> search(String search, String initiale, List<String> libraries, Integer page, Integer size, List<String> sorts) {
        final Page<WorkflowGroup> groups = service.search(search, initiale, libraries, page, size, sorts);
        return groups.map(SimpleWorkflowMapper.INSTANCE::groupToSimpleGroupDTO);
    }

    /**
     * Retourne les groupes de workflow liés à une biliothèque
     * @param identifier
     * @return
     */
    @Transactional(readOnly = true)
    public Collection<SimpleWorkflowGroupDTO> findAllForLibrary(String identifier) {
        return service.findAllForLibrary(identifier)
                .stream()
                .map(SimpleWorkflowMapper.INSTANCE::groupToSimpleGroupDTO)
                .collect(Collectors.toList());
    }
}
