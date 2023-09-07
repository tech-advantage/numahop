package fr.progilone.pgcn.service.document.ui;

import fr.progilone.pgcn.domain.document.DocPropertyType;
import fr.progilone.pgcn.domain.dto.document.DocPropertyTypeDTO;
import fr.progilone.pgcn.service.document.DocPropertyTypeService;
import fr.progilone.pgcn.service.document.mapper.DocPropertyTypeMapper;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service dédié à les gestion des vues des types de propriétés
 *
 * @author jbrunet
 */
@Service
public class UIDocPropertyTypeService {

    private final DocPropertyTypeService docPropertyTypeService;

    @Autowired
    public UIDocPropertyTypeService(final DocPropertyTypeService docPropertyTypeService) {
        this.docPropertyTypeService = docPropertyTypeService;
    }

    @Transactional(readOnly = true)
    public List<DocPropertyTypeDTO> findAllDTO() {
        final List<DocPropertyType> propertyTypes = docPropertyTypeService.findAll();
        return propertyTypes.stream().map(DocPropertyTypeMapper.INSTANCE::docPropertyTypeToDocPropertyTypeDTO).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<DocPropertyTypeDTO> findCustomDTO() {
        final List<DocPropertyType> propertyTypes = docPropertyTypeService.findCustom();
        return propertyTypes.stream().map(DocPropertyTypeMapper.INSTANCE::docPropertyTypeToDocPropertyTypeDTO).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<DocPropertyTypeDTO> findAllDTOBySuperType(final DocPropertyType.DocPropertySuperType superType) {
        final List<DocPropertyType> propertyTypes = docPropertyTypeService.findAllBySuperType(superType);
        return propertyTypes.stream().map(DocPropertyTypeMapper.INSTANCE::docPropertyTypeToDocPropertyTypeDTO).collect(Collectors.toList());
    }
}
