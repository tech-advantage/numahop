package fr.progilone.pgcn.service.administration.mapper;

import java.util.Collection;

import org.mapstruct.Mapper;

import fr.progilone.pgcn.domain.administration.digitallibrary.DigitalLibraryConfiguration;
import fr.progilone.pgcn.domain.dto.administration.digitallibrary.DigitalLibraryConfigurationDTO;

@Mapper(componentModel = "spring")
public interface DigitalLibraryConfigurationMapper {

    DigitalLibraryConfigurationDTO configurationToDto(DigitalLibraryConfiguration conf);

    Collection<DigitalLibraryConfigurationDTO> configurationsToDtos(Collection<DigitalLibraryConfiguration> confs);

    DigitalLibraryConfiguration dtoToConfiguration(DigitalLibraryConfigurationDTO dto);
}
