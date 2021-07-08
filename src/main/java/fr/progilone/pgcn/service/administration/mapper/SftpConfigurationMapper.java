package fr.progilone.pgcn.service.administration.mapper;

import java.util.Set;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import fr.progilone.pgcn.domain.administration.SftpConfiguration;
import fr.progilone.pgcn.domain.dto.administration.SftpConfigurationDTO;
import fr.progilone.pgcn.service.library.mapper.SimpleLibraryMapper;

/**
 * Created by Sébastien on 30/12/2016.
 */
@Mapper(uses = {SimpleLibraryMapper.class,
                CinesPACMapper.class})
public interface SftpConfigurationMapper {

    SftpConfigurationMapper INSTANCE = Mappers.getMapper(SftpConfigurationMapper.class);

    SftpConfigurationDTO configurationSftpToDto(SftpConfiguration sftpConfiguration);

    Set<SftpConfigurationDTO> configurationSftpToDtos(Set<SftpConfiguration> sftpConfiguration);
}
