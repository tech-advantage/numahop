package fr.progilone.pgcn.service.check.mapper;

import fr.progilone.pgcn.domain.check.AutomaticCheckType;
import fr.progilone.pgcn.domain.dto.check.AutomaticCheckTypeDTO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

/**
 * Created by lebouchp on 03/02/2017.
 */
@Mapper
public interface AutomaticCheckTypeMapper {

    AutomaticCheckTypeMapper INSTANCE = Mappers.getMapper(AutomaticCheckTypeMapper.class);

    AutomaticCheckTypeDTO objToDto(AutomaticCheckType type);

    AutomaticCheckType dtoToObj(AutomaticCheckTypeDTO dto);
}
