package fr.progilone.pgcn.service.ftpconfiguration.mapper;

import fr.progilone.pgcn.domain.dto.ftpconfiguration.SimpleFTPConfigurationDTO;
import fr.progilone.pgcn.domain.ftpconfiguration.FTPConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

/**
 * Created by lebouchp on 06/02/2017.
 */
@Mapper
public interface SimpleFTPConfigurationMapper {

    SimpleFTPConfigurationMapper INSTANCE = Mappers.getMapper(SimpleFTPConfigurationMapper.class);

    SimpleFTPConfigurationDTO ftpConfigurationToSimpleFTPConfigurationDTO(FTPConfiguration ftpConfiguration);
}
