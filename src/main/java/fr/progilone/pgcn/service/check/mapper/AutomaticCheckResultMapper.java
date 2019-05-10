package fr.progilone.pgcn.service.check.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import fr.progilone.pgcn.domain.check.AutomaticCheckResult;
import fr.progilone.pgcn.domain.check.AutomaticCheckType;
import fr.progilone.pgcn.domain.dto.check.AutomaticCheckResultDTO;
import fr.progilone.pgcn.domain.dto.check.AutomaticCheckTypeDTO;

@Mapper
public interface AutomaticCheckResultMapper {

	AutomaticCheckResultMapper INSTANCE = Mappers.getMapper(AutomaticCheckResultMapper.class);
	
	AutomaticCheckTypeDTO automaticCheckTypeToAutomaticCheckTypeDTO(AutomaticCheckType type);

    AutomaticCheckResultDTO automaticCheckResultToAutomaticCheckResultDTO(AutomaticCheckResult result);
}
