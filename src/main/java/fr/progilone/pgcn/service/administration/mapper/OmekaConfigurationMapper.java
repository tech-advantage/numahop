package fr.progilone.pgcn.service.administration.mapper;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;

import fr.progilone.pgcn.domain.administration.omeka.OmekaConfiguration;
import fr.progilone.pgcn.domain.administration.omeka.OmekaList.ListType;
import fr.progilone.pgcn.domain.dto.administration.omeka.OmekaConfigurationDTO;
import fr.progilone.pgcn.domain.dto.administration.omeka.OmekaListDTO;
import fr.progilone.pgcn.service.library.mapper.SimpleLibraryMapper; 

/**
 *
 * @author Progilone 
 * Créé le 29 aout 2018
 */
@Mapper(uses = {SimpleLibraryMapper.class, OmekaListMapper.class})
public abstract class OmekaConfigurationMapper {

    public static final OmekaConfigurationMapper INSTANCE = Mappers.getMapper(OmekaConfigurationMapper.class);

    @Mappings({@Mapping(target = "omekaCollections", ignore = true),
                @Mapping(target = "omekaItems", ignore = true)})
    public abstract OmekaConfigurationDTO confOmekaToDto(OmekaConfiguration conf);

    public abstract Set<OmekaConfigurationDTO> confOmekaToDtos(Set<OmekaConfiguration> conf);
    
    public abstract OmekaConfiguration dtoToConfOmeka(OmekaConfigurationDTO dto);
    
    
    @AfterMapping
    protected void updateObj(final OmekaConfigurationDTO dto, @MappingTarget final OmekaConfiguration conf) {
        // Chargement depuis un dto : il nous manque l'objet confOmeka sur chaque OmekaList
        if (conf.getOmekaLists() != null) {
            conf.getOmekaLists().stream()
                    .filter(ol-> ol.getName()!= null)
                    .forEach(ol -> ol.setConfOmeka(conf));
        } 
       
    }
    
   
    @AfterMapping
    protected void updateDto(final OmekaConfiguration conf, @MappingTarget final OmekaConfigurationDTO dto) {
        if (conf.getOmekaLists() == null || conf.getOmekaLists().isEmpty()) {
            return;
        }
        // Repartit les elts de la liste selon le type
        final List<OmekaListDTO> collections = conf.getOmekaLists().stream()
                                                                .filter(ol -> ListType.COLLECTION == ol.getType())
                                                                .map(OmekaListMapper.INSTANCE::objToDto)
                                                                .collect(Collectors.toList());
        dto.setOmekaCollections(collections);
        
        final List<OmekaListDTO> items = conf.getOmekaLists().stream()
                                                            .filter(ol -> ListType.ITEM == ol.getType())
                                                            .map(OmekaListMapper.INSTANCE::objToDto)
                                                            .collect(Collectors.toList());
        dto.setOmekaItems(items);        
        
    }
}
