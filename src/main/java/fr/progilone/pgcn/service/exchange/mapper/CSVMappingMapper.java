package fr.progilone.pgcn.service.exchange.mapper;

import fr.progilone.pgcn.domain.dto.exchange.CSVMappingDTO;
import fr.progilone.pgcn.domain.exchange.CSVMapping;
import fr.progilone.pgcn.service.library.mapper.SimpleLibraryMapper;
import java.util.Set;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

/**
 * Created by Sebastien on 07/12/2016.
 */
@Mapper(uses = {SimpleLibraryMapper.class})
public interface CSVMappingMapper {

    CSVMappingMapper INSTANCE = Mappers.getMapper(CSVMappingMapper.class);

    CSVMappingDTO mappingToDto(CSVMapping mapping);

    Set<CSVMappingDTO> mappingToDtos(Set<CSVMapping> mappings);
}
