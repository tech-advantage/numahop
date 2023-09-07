package fr.progilone.pgcn.service.document.mapper;

import fr.progilone.pgcn.domain.dto.imagemetadata.ImageMetadataValuesDTO;
import fr.progilone.pgcn.domain.imagemetadata.ImageMetadataValue;
import fr.progilone.pgcn.repository.document.DocUnitRepository;
import fr.progilone.pgcn.repository.imagemetadata.ImageMetadataRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ImageMetadataValuesMapper {

    private DocUnitRepository docUnitRepository;
    private ImageMetadataRepository imageMetadataRepository;

    @Autowired
    public ImageMetadataValuesMapper(final DocUnitRepository docUnitRepository, final ImageMetadataRepository imageMetadataRepository) {
        this.docUnitRepository = docUnitRepository;
        this.imageMetadataRepository = imageMetadataRepository;
    }

    public void mapInto(final ImageMetadataValuesDTO valuesDto, final ImageMetadataValue values) {

        if (valuesDto.getIdentifier() != null) {
            values.setIdentifier(valuesDto.getIdentifier());
        }
        values.setDocUnit(docUnitRepository.getOne(valuesDto.getDocUnitId()));
        values.setMetadata(imageMetadataRepository.getOne(valuesDto.getMetadataId()));
        values.setValue(valuesDto.getValue());

    }
}
