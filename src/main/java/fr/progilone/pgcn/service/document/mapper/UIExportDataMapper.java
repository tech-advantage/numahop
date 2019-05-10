package fr.progilone.pgcn.service.document.mapper;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import fr.progilone.pgcn.domain.document.DocPropertyType;
import fr.progilone.pgcn.domain.document.ExportData;
import fr.progilone.pgcn.domain.document.ExportProperty;
import fr.progilone.pgcn.domain.dto.document.BibliographicRecordDcDTO;
import fr.progilone.pgcn.repository.document.DocPropertyTypeRepository;

@Service
public class UIExportDataMapper {

    private DocPropertyTypeRepository docPropertyTypeRepository;

    @Autowired
    public UIExportDataMapper(final DocPropertyTypeRepository docPropertyTypeRepository) {
        this.docPropertyTypeRepository = docPropertyTypeRepository;
    }

    
    public void mapInto(final BibliographicRecordDcDTO recordDTO, final ExportData record) {
        DocPropertyType propertyType = docPropertyTypeRepository.findOne("title");
        mapProperties(recordDTO.getTitle(), propertyType, record);
        propertyType = docPropertyTypeRepository.findOne("creator");
        mapProperties(recordDTO.getCreator(), propertyType, record);
        propertyType = docPropertyTypeRepository.findOne("subject");
        mapProperties(recordDTO.getSubject(), propertyType, record);
        propertyType = docPropertyTypeRepository.findOne("description");
        mapProperties(recordDTO.getDescription(), propertyType, record);
        propertyType = docPropertyTypeRepository.findOne("publisher");
        mapProperties(recordDTO.getPublisher(), propertyType, record);
        propertyType = docPropertyTypeRepository.findOne("contributor");
        mapProperties(recordDTO.getContributor(), propertyType, record);
        propertyType = docPropertyTypeRepository.findOne("date");
        mapProperties(recordDTO.getDate(), propertyType, record);
        propertyType = docPropertyTypeRepository.findOne("type");
        mapProperties(recordDTO.getType(), propertyType, record);
        propertyType = docPropertyTypeRepository.findOne("format");
        mapProperties(recordDTO.getFormat(), propertyType, record);
        propertyType = docPropertyTypeRepository.findOne("identifier");
        mapProperties(recordDTO.getIdentifier(), propertyType, record);
        propertyType = docPropertyTypeRepository.findOne("source");
        mapProperties(recordDTO.getSource(), propertyType, record);
        propertyType = docPropertyTypeRepository.findOne("language");
        mapProperties(recordDTO.getLanguage(), propertyType, record);
        propertyType = docPropertyTypeRepository.findOne("relation");
        mapProperties(recordDTO.getRelation(), propertyType, record);
        propertyType = docPropertyTypeRepository.findOne("coverage");
        mapProperties(recordDTO.getCoverage(), propertyType, record);
        propertyType = docPropertyTypeRepository.findOne("rights");
        mapProperties(recordDTO.getRights(), propertyType, record);
        
    }

    public void mapProperties(final List<String> values, final DocPropertyType type, final ExportData record) {
        int rank = 0;
        for(final String value : values) {
            rank ++;
            final ExportProperty property = new ExportProperty();
            property.setValue(value);
            property.setType(type);
            property.setRank(rank);
            property.setRecord(record);
            record.addProperty(property);
        }
    }
}
