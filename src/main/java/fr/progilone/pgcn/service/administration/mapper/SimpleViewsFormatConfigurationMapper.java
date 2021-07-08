package fr.progilone.pgcn.service.administration.mapper;

import java.util.Set;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import fr.progilone.pgcn.domain.administration.viewsformat.ViewsFormatConfiguration;
import fr.progilone.pgcn.domain.dto.administration.viewsFormat.SimpleViewsFormatConfigurationDTO;

@Mapper
public interface SimpleViewsFormatConfigurationMapper {
    
    SimpleViewsFormatConfigurationMapper INSTANCE = Mappers.getMapper(SimpleViewsFormatConfigurationMapper.class);
    
    SimpleViewsFormatConfigurationDTO formatConfigToDto(ViewsFormatConfiguration formatConfiguration);
    
    Set<SimpleViewsFormatConfigurationDTO> formatConfigToDtos(Set<ViewsFormatConfiguration> confs);

}
