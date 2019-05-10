package fr.progilone.pgcn.service.workflow.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import fr.progilone.pgcn.domain.dto.workflow.SimpleWorkflowGroupDTO;
import fr.progilone.pgcn.domain.dto.workflow.SimpleWorkflowModelDTO;
import fr.progilone.pgcn.domain.dto.workflow.SimpleWorkflowModelStateDTO;
import fr.progilone.pgcn.domain.workflow.WorkflowGroup;
import fr.progilone.pgcn.domain.workflow.WorkflowModel;
import fr.progilone.pgcn.domain.workflow.WorkflowModelState;

@Mapper
public interface SimpleWorkflowMapper {

    SimpleWorkflowMapper INSTANCE = Mappers.getMapper(SimpleWorkflowMapper.class);

    SimpleWorkflowGroupDTO groupToSimpleGroupDTO(WorkflowGroup group);
    
    SimpleWorkflowModelDTO modelToSimpleModelDTO(WorkflowModel model);
    
    SimpleWorkflowModelStateDTO modelStateToSimpleModelState(WorkflowModelState state);
}
