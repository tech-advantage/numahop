package fr.progilone.pgcn.service.statistics.mapper;

import fr.progilone.pgcn.domain.dto.statistics.WorkflowProfileActivityDTO;
import fr.progilone.pgcn.domain.dto.statistics.WorkflowUserActivityDTO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface StatisticsWorkflowMapper {

    WorkflowProfileActivityDTO userToProfileDto(WorkflowUserActivityDTO userDto);
}
