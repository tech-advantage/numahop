package fr.progilone.pgcn.service.checkconfiguration.mapper;

import fr.progilone.pgcn.domain.checkconfiguration.CheckConfiguration;
import fr.progilone.pgcn.domain.dto.checkconfiguration.SimpleCheckConfigurationDTO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

/**
 * Created by lebouchp on 06/02/2017.
 */
@Mapper
public interface SimpleCheckConfigurationMapper {

    SimpleCheckConfigurationMapper INSTANCE = Mappers.getMapper(SimpleCheckConfigurationMapper.class);

    SimpleCheckConfigurationDTO checkConfigurationToSimpleCheckConfigurationDTO(CheckConfiguration checkConfiguration);
}
