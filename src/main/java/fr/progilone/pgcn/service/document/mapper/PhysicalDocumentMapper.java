package fr.progilone.pgcn.service.document.mapper;

import fr.progilone.pgcn.domain.document.PhysicalDocument;
import fr.progilone.pgcn.domain.dto.document.ListPhysicalDocumentDTO;
import fr.progilone.pgcn.domain.dto.document.PhysicalDocumentDTO;
import fr.progilone.pgcn.domain.dto.document.SimplePhysicalDocumentDTO;
import fr.progilone.pgcn.service.project.mapper.ProjectMapper;
import fr.progilone.pgcn.service.train.mapper.SimpleTrainMapper;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(uses = {ProjectMapper.class, SimpleTrainMapper.class, SimpleDocUnitMapper.class})
public interface PhysicalDocumentMapper {

    PhysicalDocumentMapper INSTANCE = Mappers.getMapper(PhysicalDocumentMapper.class);

    SimplePhysicalDocumentDTO physicalDocumentToSimplePhysicalDocumentDTO(PhysicalDocument physicalDocument);

    PhysicalDocumentDTO physicalDocumentToPhysicalDocumentDTO(PhysicalDocument physicalDocument);

    ListPhysicalDocumentDTO physicalDocumentToListPhysicalDocumentDTO(PhysicalDocument pd);
}
