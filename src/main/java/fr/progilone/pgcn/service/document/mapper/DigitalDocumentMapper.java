package fr.progilone.pgcn.service.document.mapper;

import fr.progilone.pgcn.domain.document.DigitalDocument;
import fr.progilone.pgcn.domain.dto.document.DigitalDocumentDTO;
import fr.progilone.pgcn.domain.dto.document.SimpleDigitalDocumentDTO;
import fr.progilone.pgcn.domain.dto.document.SimpleListDigitalDocumentDTO;
import fr.progilone.pgcn.service.check.mapper.AutomaticCheckResultMapper;
import fr.progilone.pgcn.service.delivery.mapper.DeliveryMapper;
import fr.progilone.pgcn.service.lot.mapper.SimpleLotMapper;
import fr.progilone.pgcn.service.project.mapper.SimpleProjectMapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;

@Mapper(uses = {AutomaticCheckResultMapper.class, SimpleDocUnitMapper.class, 
                SimpleLotMapper.class, SimpleProjectMapper.class, 
                DeliveredDocumentMapper.class, DeliveryMapper.class})
public interface DigitalDocumentMapper {

    DigitalDocumentMapper INSTANCE = Mappers.getMapper(DigitalDocumentMapper.class);

    @Mapping(target = "docUnit", ignore=true)
    SimpleDigitalDocumentDTO digitalDocumentToSimpleDigitalDocumentDTO(DigitalDocument dd);

    DigitalDocumentDTO digitalDocumentToDigitalDocumentDTO(DigitalDocument dd);
    
    @Mappings({@Mapping(source = "dd.docUnit.pgcnId", target = "pgcnId"),
        @Mapping(source = "dd.docUnit.label", target = "label"),
        @Mapping(source = "dd.docUnit.lot", target = "lot"),
        @Mapping(source = "dd.docUnit.project", target = "project"),
        @Mapping(source = "dd.deliveries", target = "deliveries")})
    SimpleListDigitalDocumentDTO digitalDocumentToSimpleListDigitalDocumentDTO(DigitalDocument dd);
}
