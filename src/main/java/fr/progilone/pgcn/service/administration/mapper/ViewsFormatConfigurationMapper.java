package fr.progilone.pgcn.service.administration.mapper;

import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

import fr.progilone.pgcn.domain.administration.viewsformat.ViewsFormatConfiguration;
import fr.progilone.pgcn.domain.dto.administration.viewsFormat.ViewsFormatConfigurationDTO;
import fr.progilone.pgcn.service.library.mapper.LibraryMapper;

@Mapper(uses = {LibraryMapper.class})
public abstract class ViewsFormatConfigurationMapper {
    
    public static final ViewsFormatConfigurationMapper INSTANCE = Mappers.getMapper(ViewsFormatConfigurationMapper.class);
    
    public abstract ViewsFormatConfigurationDTO formatConfigToDto(ViewsFormatConfiguration formatConfiguration);
    
    public abstract ViewsFormatConfiguration dtoToFormatConfig(ViewsFormatConfigurationDTO formatConfigDto);
    
    
    @AfterMapping
    protected void updateDto(final ViewsFormatConfiguration conf, @MappingTarget final ViewsFormatConfigurationDTO dto) {
        
        if (conf.getDefaultFormats() != null) {
            dto.setThumbDefaultValue(String.valueOf(conf.getDefaultFormats().getDefThumbHeight())
                                 .concat(" x ")
                                 .concat(String.valueOf(conf.getDefaultFormats().getDefThumbWidth())));
        
            dto.setViewDefaultValue(String.valueOf(conf.getDefaultFormats().getDefViewHeight())
                                 .concat(" x ")
                                 .concat(String.valueOf(conf.getDefaultFormats().getDefViewWidth())));
        
            dto.setPrintDefaultValue(String.valueOf(conf.getDefaultFormats().getDefPrintHeight())
                                .concat(" x ")
                                .concat(String.valueOf(conf.getDefaultFormats().getDefPrintWidth())));
        }
    }

}
