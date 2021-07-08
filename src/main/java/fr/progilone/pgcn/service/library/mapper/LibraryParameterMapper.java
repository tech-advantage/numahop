package fr.progilone.pgcn.service.library.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;

import fr.progilone.pgcn.domain.dto.library.AbstractLibraryParameterValueDTO;
import fr.progilone.pgcn.domain.dto.library.LibraryParameterDTO;
import fr.progilone.pgcn.domain.dto.library.LibraryParameterValueCinesDTO;
import fr.progilone.pgcn.domain.dto.library.LibraryParameterValuedDTO;
import fr.progilone.pgcn.domain.library.AbstractLibraryParameterValue;
import fr.progilone.pgcn.domain.library.LibraryParameter;
import fr.progilone.pgcn.domain.library.LibraryParameterValueCines;

@Mapper(uses = {SimpleLibraryMapper.class})
public interface LibraryParameterMapper {

    LibraryParameterMapper INSTANCE = Mappers.getMapper(LibraryParameterMapper.class);

    LibraryParameterValueCinesDTO libParamCinesValueToDTO(LibraryParameterValueCines libraryParameterValueCines);

    LibraryParameterDTO libraryParameterToLibraryParameterDTO(LibraryParameter libraryParameter);
    
    @Mappings({@Mapping(target = "values", ignore = true)})
    LibraryParameterValuedDTO libParamToLibParamValuedDTO(LibraryParameter libraryParameter);
    
    AbstractLibraryParameterValueDTO abstractParameterValueToDTO(AbstractLibraryParameterValue libraryParameterValue);
    
}
