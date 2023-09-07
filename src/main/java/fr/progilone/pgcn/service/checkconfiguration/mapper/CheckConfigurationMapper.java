package fr.progilone.pgcn.service.checkconfiguration.mapper;

import fr.progilone.pgcn.domain.checkconfiguration.CheckConfiguration;
import fr.progilone.pgcn.domain.dto.checkconfiguration.CheckConfigurationDTO;
import fr.progilone.pgcn.service.check.mapper.AutomaticCheckTypeMapper;
import fr.progilone.pgcn.service.library.mapper.SimpleLibraryMapper;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

/**
 * Created by lebouchp on 03/02/2017.
 */
@Mapper(uses = {AutomaticCheckRuleMapper.class,
                AutomaticCheckTypeMapper.class,
                SimpleLibraryMapper.class})
public interface CheckConfigurationMapper {

    CheckConfigurationMapper INSTANCE = Mappers.getMapper(CheckConfigurationMapper.class);

    CheckConfigurationDTO checkConfigurationToCheckConfigurationDTO(CheckConfiguration checkConfiguration);

}
