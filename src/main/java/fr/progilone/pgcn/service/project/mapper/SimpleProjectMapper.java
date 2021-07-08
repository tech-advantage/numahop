package fr.progilone.pgcn.service.project.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import fr.progilone.pgcn.domain.dto.project.SimpleProjectDTO;
import fr.progilone.pgcn.domain.project.Project;
import fr.progilone.pgcn.service.library.mapper.SimpleLibraryMapper;

@Mapper(uses = {SimpleLibraryMapper.class})
public interface SimpleProjectMapper {

    SimpleProjectMapper INSTANCE = Mappers.getMapper(SimpleProjectMapper.class);

    SimpleProjectDTO projectToSimpleProjectDTO(Project project);
}
