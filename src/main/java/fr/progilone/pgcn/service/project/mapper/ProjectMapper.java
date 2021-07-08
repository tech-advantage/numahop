package fr.progilone.pgcn.service.project.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import fr.progilone.pgcn.domain.dto.project.ProjectDTO;
import fr.progilone.pgcn.domain.dto.project.ProjectSearchDTO;
import fr.progilone.pgcn.domain.dto.statistics.StatisticsProjectDTO;
import fr.progilone.pgcn.domain.project.Project;
import fr.progilone.pgcn.service.administration.mapper.CinesPACMapper;
import fr.progilone.pgcn.service.administration.mapper.InternetArchiveCollectionMapper;
import fr.progilone.pgcn.service.administration.mapper.OmekaListMapper;
import fr.progilone.pgcn.service.administration.mapper.SimpleViewsFormatConfigurationMapper;
import fr.progilone.pgcn.service.checkconfiguration.mapper.SimpleCheckConfigurationMapper;
import fr.progilone.pgcn.service.document.mapper.SimpleDocUnitMapper;
import fr.progilone.pgcn.service.ftpconfiguration.mapper.SimpleFTPConfigurationMapper;
import fr.progilone.pgcn.service.library.mapper.SimpleLibraryMapper;
import fr.progilone.pgcn.service.lot.mapper.LotMapper;
import fr.progilone.pgcn.service.train.mapper.SimpleTrainMapper;
import fr.progilone.pgcn.service.user.mapper.AddressMapper;
import fr.progilone.pgcn.service.user.mapper.UserMapper;
import fr.progilone.pgcn.service.workflow.mapper.SimpleWorkflowMapper;

@Mapper(uses = {AddressMapper.class,
                SimpleLibraryMapper.class,
                SimpleDocUnitMapper.class,
                LotMapper.class,
                SimpleTrainMapper.class,
                SimpleFTPConfigurationMapper.class,
                SimpleCheckConfigurationMapper.class,
                SimpleViewsFormatConfigurationMapper.class,
                SimpleWorkflowMapper.class,
                UserMapper.class,
                InternetArchiveCollectionMapper.class,
                CinesPACMapper.class,
                OmekaListMapper.class})
public interface ProjectMapper {

    ProjectMapper INSTANCE = Mappers.getMapper(ProjectMapper.class);

    ProjectSearchDTO projectToProjectSearchDTO(Project project);

    ProjectDTO projectToProjectDTO(Project project);

    StatisticsProjectDTO projectToStatProjectDTO(Project project);
}
