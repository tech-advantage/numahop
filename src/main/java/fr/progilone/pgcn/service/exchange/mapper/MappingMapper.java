package fr.progilone.pgcn.service.exchange.mapper;

import java.util.Set;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import fr.progilone.pgcn.domain.dto.exchange.MappingDTO;
import fr.progilone.pgcn.domain.exchange.Mapping;
import fr.progilone.pgcn.service.library.mapper.SimpleLibraryMapper;

/**
 * Created by Sebastien on 07/12/2016.
 */
@Mapper(uses = {SimpleLibraryMapper.class})
public interface MappingMapper {

    MappingMapper INSTANCE = Mappers.getMapper(MappingMapper.class);

    MappingDTO mappingToDto(Mapping mapping);

    Set<MappingDTO> mappingToDtos(Set<Mapping> mappings);
}
