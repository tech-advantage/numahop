package fr.progilone.pgcn.service.ftpconfiguration.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import fr.progilone.pgcn.domain.dto.ftpconfiguration.FTPConfigurationDTO;
import fr.progilone.pgcn.domain.ftpconfiguration.FTPConfiguration;
import fr.progilone.pgcn.service.library.mapper.SimpleLibraryMapper;

/**
 * Created by lebouchp on 03/02/2017.
 */
@Mapper(uses = {SimpleLibraryMapper.class})
public interface FTPConfigurationMapper {

    FTPConfigurationMapper INSTANCE = Mappers.getMapper(FTPConfigurationMapper.class);

    FTPConfigurationDTO ftpConfigurationToFTPConfigurationDTO(FTPConfiguration ftpConfiguration);
}
