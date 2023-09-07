package fr.progilone.pgcn.service.document.mapper;

import fr.progilone.pgcn.domain.document.DocProperty;
import fr.progilone.pgcn.domain.dto.document.DocPropertyDTO;
import fr.progilone.pgcn.repository.document.DocPropertyTypeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UIDocPropertyMapper {

    private final DocPropertyTypeRepository docPropertyTypeRepository;

    @Autowired
    public UIDocPropertyMapper(final DocPropertyTypeRepository docPropertyTypeRepository) {
        this.docPropertyTypeRepository = docPropertyTypeRepository;
    }

    public void mapInto(final DocPropertyDTO propertyDTO, final DocProperty property) {
        property.setValue(propertyDTO.getValue());
        property.setRank(propertyDTO.getRank() != null ? propertyDTO.getRank()
                                                       : propertyDTO.getType().getRank());
        // Type (obligatoire)
        if (propertyDTO.getType() != null && propertyDTO.getType().getIdentifier() != null) {
            property.setType(docPropertyTypeRepository.findById(propertyDTO.getType().getIdentifier()).orElse(null));
        }
    }
}
