package fr.progilone.pgcn.service.library.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;

import fr.progilone.pgcn.domain.dto.library.SimpleLibraryDTO;
import fr.progilone.pgcn.domain.library.Library;
import fr.progilone.pgcn.service.user.mapper.RoleMapper;

@Mapper(uses = {RoleMapper.class})
public interface SimpleLibraryMapper {

    SimpleLibraryMapper INSTANCE = Mappers.getMapper(SimpleLibraryMapper.class);

    SimpleLibraryDTO libraryToSimpleLibraryDTO(Library library);

    @Mappings({@Mapping(ignore=true, target="defaultRole")})
    Library dtoToLibrary(SimpleLibraryDTO dto);
}
