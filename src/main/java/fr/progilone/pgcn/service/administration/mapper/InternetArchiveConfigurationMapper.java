package fr.progilone.pgcn.service.administration.mapper;

import java.util.Set;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import fr.progilone.pgcn.domain.administration.InternetArchiveConfiguration;
import fr.progilone.pgcn.domain.dto.administration.InternetArchiveConfigurationDTO;
import fr.progilone.pgcn.service.library.mapper.SimpleLibraryMapper;

/**
 *
 * @author jbrunet
 * Créé le 19 avr. 2017
 */
@Mapper(uses = {SimpleLibraryMapper.class,
InternetArchiveCollectionMapper.class})
public interface InternetArchiveConfigurationMapper {

    InternetArchiveConfigurationMapper INSTANCE = Mappers.getMapper(InternetArchiveConfigurationMapper.class);

    InternetArchiveConfigurationDTO configurationIAToDto(InternetArchiveConfiguration conf);

    Set<InternetArchiveConfigurationDTO> configurationIAToDtos(Set<InternetArchiveConfiguration> conf);
}
