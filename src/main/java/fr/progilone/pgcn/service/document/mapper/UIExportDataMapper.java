package fr.progilone.pgcn.service.document.mapper;

import fr.progilone.pgcn.domain.document.DocPropertyType;
import fr.progilone.pgcn.domain.document.ExportData;
import fr.progilone.pgcn.domain.document.ExportProperty;
import fr.progilone.pgcn.domain.dto.document.BibliographicRecordDcDTO;
import fr.progilone.pgcn.repository.document.DocPropertyTypeRepository;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UIExportDataMapper {

    private final DocPropertyTypeRepository docPropertyTypeRepository;

    @Autowired
    public UIExportDataMapper(final DocPropertyTypeRepository docPropertyTypeRepository) {
        this.docPropertyTypeRepository = docPropertyTypeRepository;
    }

    public void mapInto(final BibliographicRecordDcDTO recordDTO, final ExportData record) {
        DocPropertyType propertyType = docPropertyTypeRepository.findById("title").orElse(null);
        mapProperties(recordDTO.getTitle(), propertyType, record);
        propertyType = docPropertyTypeRepository.findById("creator").orElse(null);
        mapProperties(recordDTO.getCreator(), propertyType, record);
        propertyType = docPropertyTypeRepository.findById("subject").orElse(null);
        mapProperties(recordDTO.getSubject(), propertyType, record);
        propertyType = docPropertyTypeRepository.findById("description").orElse(null);
        mapProperties(recordDTO.getDescription(), propertyType, record);
        propertyType = docPropertyTypeRepository.findById("publisher").orElse(null);
        mapProperties(recordDTO.getPublisher(), propertyType, record);
        propertyType = docPropertyTypeRepository.findById("contributor").orElse(null);
        mapProperties(recordDTO.getContributor(), propertyType, record);
        propertyType = docPropertyTypeRepository.findById("date").orElse(null);
        mapProperties(recordDTO.getDate(), propertyType, record);
        propertyType = docPropertyTypeRepository.findById("type").orElse(null);
        mapProperties(recordDTO.getType(), propertyType, record);
        propertyType = docPropertyTypeRepository.findById("format").orElse(null);
        mapProperties(recordDTO.getFormat(), propertyType, record);
        propertyType = docPropertyTypeRepository.findById("identifier").orElse(null);
        mapProperties(recordDTO.getIdentifier(), propertyType, record);
        propertyType = docPropertyTypeRepository.findById("source").orElse(null);
        mapProperties(recordDTO.getSource(), propertyType, record);
        propertyType = docPropertyTypeRepository.findById("language").orElse(null);
        mapProperties(recordDTO.getLanguage(), propertyType, record);
        propertyType = docPropertyTypeRepository.findById("relation").orElse(null);
        mapProperties(recordDTO.getRelation(), propertyType, record);
        propertyType = docPropertyTypeRepository.findById("coverage").orElse(null);
        mapProperties(recordDTO.getCoverage(), propertyType, record);
        propertyType = docPropertyTypeRepository.findById("rights").orElse(null);
        mapProperties(recordDTO.getRights(), propertyType, record);

    }

    public void mapProperties(final List<String> values, final DocPropertyType type, final ExportData record) {
        int rank = 0;
        for (final String value : values) {
            rank++;
            final ExportProperty property = new ExportProperty();
            property.setValue(value);
            property.setType(type);
            property.setRank(rank);
            property.setRecord(record);
            record.addProperty(property);
        }
    }
}
