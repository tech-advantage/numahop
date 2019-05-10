package fr.progilone.pgcn.service.ocrlangconfiguration.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import fr.progilone.pgcn.domain.dto.ocrlangconfiguration.OcrLanguageDTO;
import fr.progilone.pgcn.domain.ocrlangconfiguration.OcrLanguage;

@Mapper
public interface OcrLanguageMapper {
    
    OcrLanguageMapper INSTANCE = Mappers.getMapper(OcrLanguageMapper.class);
    
    OcrLanguageDTO objToDTO(OcrLanguage object);
    
    OcrLanguage dtoToObject(OcrLanguageDTO dto);

    List<OcrLanguageDTO> objsToDtos(List<OcrLanguage> objects);
    
}
