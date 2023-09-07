package fr.progilone.pgcn.service.workflow.mapper;

import fr.progilone.pgcn.domain.dto.workflow.DocUnitStateDTO;
import fr.progilone.pgcn.domain.dto.workflow.DocUnitWorkflowDTO;
import fr.progilone.pgcn.domain.dto.workflow.WorkflowGroupDTO;
import fr.progilone.pgcn.domain.dto.workflow.WorkflowModelDTO;
import fr.progilone.pgcn.domain.dto.workflow.WorkflowModelStateDTO;
import fr.progilone.pgcn.domain.workflow.DocUnitState;
import fr.progilone.pgcn.domain.workflow.DocUnitWorkflow;
import fr.progilone.pgcn.domain.workflow.WorkflowGroup;
import fr.progilone.pgcn.domain.workflow.WorkflowModel;
import fr.progilone.pgcn.domain.workflow.WorkflowModelState;
import fr.progilone.pgcn.service.library.mapper.SimpleLibraryMapper;
import fr.progilone.pgcn.service.user.mapper.UserMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(uses = {SimpleLibraryMapper.class,
                UserMapper.class,
                SimpleWorkflowMapper.class})
public interface WorkflowMapper {

    WorkflowMapper INSTANCE = Mappers.getMapper(WorkflowMapper.class);

    WorkflowGroupDTO groupToGroupDTO(WorkflowGroup group);

    @Mapping(target = "states", source = "model.modelStates")
    WorkflowModelDTO modelToModelDTO(WorkflowModel model);

    WorkflowModelStateDTO modelStateToModelStateDTO(WorkflowModelState state);

    DocUnitWorkflowDTO workflowToWorkflowDTO(DocUnitWorkflow workflow);

    DocUnitStateDTO workflowStateToWorkflowStateDTO(DocUnitState state);
}
