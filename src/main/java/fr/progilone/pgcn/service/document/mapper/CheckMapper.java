package fr.progilone.pgcn.service.document.mapper;

import fr.progilone.pgcn.domain.document.Check;
import fr.progilone.pgcn.domain.dto.document.CheckDTO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(uses = {DocPageMapper.class})
public interface CheckMapper {

    CheckMapper INSTANCE = Mappers.getMapper(CheckMapper.class);

    CheckDTO checkToCheckDTO(Check check);
}
