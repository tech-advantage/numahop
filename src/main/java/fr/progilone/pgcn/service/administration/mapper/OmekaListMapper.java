package fr.progilone.pgcn.service.administration.mapper;

import java.util.List;
import java.util.Set;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import fr.progilone.pgcn.domain.administration.omeka.OmekaList;
import fr.progilone.pgcn.domain.dto.administration.omeka.OmekaListDTO;

@Mapper()
public interface OmekaListMapper {
    

    OmekaListMapper INSTANCE = Mappers.getMapper(OmekaListMapper.class);

    OmekaListDTO objToDto(OmekaList conf);
    
    OmekaList dtoToObj(OmekaListDTO dto);

    Set<OmekaListDTO> objsToDtos(Set<OmekaList> conf);
    
    List<OmekaList> dtosToObjs(List<OmekaListDTO> dto);

}
