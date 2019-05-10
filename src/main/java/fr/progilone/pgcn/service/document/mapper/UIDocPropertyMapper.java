package fr.progilone.pgcn.service.document.mapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import fr.progilone.pgcn.domain.document.DocProperty;
import fr.progilone.pgcn.domain.dto.document.DocPropertyDTO;
import fr.progilone.pgcn.repository.document.DocPropertyTypeRepository;

@Service
public class UIDocPropertyMapper {
	private DocPropertyTypeRepository docPropertyTypeRepository;
	
	@Autowired
	public UIDocPropertyMapper(final DocPropertyTypeRepository docPropertyTypeRepository) {
		this.docPropertyTypeRepository = docPropertyTypeRepository;
	}
	
	public void mapInto(final DocPropertyDTO propertyDTO, final DocProperty property) {
		property.setValue(propertyDTO.getValue());
		property.setRank(propertyDTO.getRank());
		// Type (obligatoire)
		if (propertyDTO.getType() != null && propertyDTO.getType().getIdentifier() != null) {
			property.setType(docPropertyTypeRepository.findOne(propertyDTO.getType().getIdentifier()));
		}
    }
}
