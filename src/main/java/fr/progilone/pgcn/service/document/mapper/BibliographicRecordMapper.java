package fr.progilone.pgcn.service.document.mapper;

import org.mapstruct.DecoratedWith;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import fr.progilone.pgcn.domain.document.BibliographicRecord;
import fr.progilone.pgcn.domain.dto.document.BibliographicRecordDTO;
import fr.progilone.pgcn.domain.dto.document.DocUnitBibliographicRecordDTO;
import fr.progilone.pgcn.domain.dto.document.SimpleBibliographicRecordDTO;
import fr.progilone.pgcn.service.library.mapper.LibraryMapper;

@Mapper(uses = {DocPropertyMapper.class, SimpleDocUnitMapper.class, LibraryMapper.class})
@DecoratedWith(BibliographicRecordMapperDecorator.class)
public interface BibliographicRecordMapper {

    BibliographicRecordMapper INSTANCE = Mappers.getMapper(BibliographicRecordMapper.class);

    BibliographicRecordDTO bibliographicRecordToBibliographicRecordDTO(BibliographicRecord record);

    SimpleBibliographicRecordDTO bibliographicRecordToSimpleBibliographicRecordDTO(BibliographicRecord record);

    DocUnitBibliographicRecordDTO bibliographicRecordToDocUnitBibliographicRecordDTO(BibliographicRecord record);
}
