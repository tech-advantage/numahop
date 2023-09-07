package fr.progilone.pgcn.service.ocrlangconfiguration.mapper;

import fr.progilone.pgcn.domain.dto.ocrlangconfiguration.OcrLanguageDTO;
import fr.progilone.pgcn.domain.ocrlangconfiguration.OcrLanguage;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface OcrLanguageMapper {

    OcrLanguageMapper INSTANCE = Mappers.getMapper(OcrLanguageMapper.class);

    OcrLanguageDTO objToDTO(OcrLanguage object);

    OcrLanguage dtoToObject(OcrLanguageDTO dto);

    List<OcrLanguageDTO> objsToDtos(List<OcrLanguage> objects);

}
