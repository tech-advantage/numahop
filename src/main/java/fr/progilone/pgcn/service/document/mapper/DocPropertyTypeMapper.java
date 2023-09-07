package fr.progilone.pgcn.service.document.mapper;

import fr.progilone.pgcn.domain.document.DocPropertyType;
import fr.progilone.pgcn.domain.dto.document.DocPropertyTypeDTO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(uses = {})
public interface DocPropertyTypeMapper {

    DocPropertyTypeMapper INSTANCE = Mappers.getMapper(DocPropertyTypeMapper.class);

    DocPropertyTypeDTO docPropertyTypeToDocPropertyTypeDTO(DocPropertyType propertyType);
}
