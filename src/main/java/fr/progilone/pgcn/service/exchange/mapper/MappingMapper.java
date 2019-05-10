package fr.progilone.pgcn.service.exchange.mapper;

import fr.progilone.pgcn.domain.dto.exchange.MappingDTO;
import fr.progilone.pgcn.domain.exchange.Mapping;
import fr.progilone.pgcn.service.library.mapper.LibraryMapper;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.Set;

/**
 * Created by Sebastien on 07/12/2016.
 */
@Mapper(uses = {LibraryMapper.class})
public interface MappingMapper {

    MappingMapper INSTANCE = Mappers.getMapper(MappingMapper.class);

    MappingDTO mappingToDto(Mapping mapping);

    Set<MappingDTO> mappingToDtos(Set<Mapping> mappings);
}
