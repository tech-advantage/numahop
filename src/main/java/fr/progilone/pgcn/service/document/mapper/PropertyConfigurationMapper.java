package fr.progilone.pgcn.service.document.mapper;

import fr.progilone.pgcn.domain.document.conditionreport.DescriptionProperty;
import fr.progilone.pgcn.domain.document.conditionreport.PropertyConfiguration;
import fr.progilone.pgcn.domain.dto.document.conditionreport.PropertyConfigurationDTO;
import fr.progilone.pgcn.domain.library.Library;
import fr.progilone.pgcn.service.library.mapper.LibraryMapper;
import org.apache.commons.lang3.StringUtils;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;

@Mapper(uses = {LibraryMapper.class})
public abstract class PropertyConfigurationMapper {

    public static final PropertyConfigurationMapper INSTANCE = Mappers.getMapper(PropertyConfigurationMapper.class);

    @Mappings({@Mapping(source = "descProperty.identifier", target = "descPropertyId"),
               @Mapping(source = "descProperty.label", target = "descPropertyLabel"),
               @Mapping(target = "library", ignore = true)})
    public abstract PropertyConfigurationDTO confToDto(PropertyConfiguration propertyConfiguration);

    @Mappings({@Mapping(target = "library", ignore = true)})
    public abstract PropertyConfiguration dtoToConf(PropertyConfigurationDTO dto);

    @AfterMapping
    protected void updateConfiguration(PropertyConfiguration configuration, @MappingTarget final PropertyConfigurationDTO dto) {
        final Library library = configuration.getLibrary();
        if (library != null) {
            final PropertyConfigurationDTO.LibraryDTO libraryDTO = new PropertyConfigurationDTO.LibraryDTO();
            libraryDTO.setIdentifier(library.getIdentifier());
            libraryDTO.setName(library.getName());
            dto.setLibrary(libraryDTO);
        }
    }

    @AfterMapping
    protected void updateConfiguration(PropertyConfigurationDTO dto, @MappingTarget final PropertyConfiguration configuration) {
        if (StringUtils.isNotEmpty(dto.getDescPropertyId())) {
            final DescriptionProperty descProperty = new DescriptionProperty();
            descProperty.setIdentifier(dto.getDescPropertyId());
            configuration.setDescProperty(descProperty);
        }
        if (dto.getLibrary() != null) {
            final Library library = new Library();
            library.setIdentifier(dto.getLibrary().getIdentifier());
            configuration.setLibrary(library);
        }
    }
}
