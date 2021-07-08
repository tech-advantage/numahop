package fr.progilone.pgcn.service.ocrlangconfiguration.ui;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import fr.progilone.pgcn.domain.dto.ocrlangconfiguration.OcrLangConfigurationDTO;
import fr.progilone.pgcn.domain.dto.ocrlangconfiguration.OcrLanguageDTO;
import fr.progilone.pgcn.domain.ocrlangconfiguration.ActivatedOcrLanguage;
import fr.progilone.pgcn.domain.ocrlangconfiguration.OcrLangConfiguration;
import fr.progilone.pgcn.service.library.mapper.SimpleLibraryMapper;
import fr.progilone.pgcn.service.ocrlangconfiguration.mapper.OcrLanguageMapper;

@Component
public class UIOcrLangConfigMapper {
    
   public void mapDtoToConfig(final OcrLangConfigurationDTO dto, final OcrLangConfiguration updated) {
       updated.setIdentifier(dto.getIdentifier());
       updated.setLabel(dto.getLabel());
       updated.setActive(dto.isActive());
       updated.setLibrary(SimpleLibraryMapper.INSTANCE.dtoToLibrary(dto.getLibrary()));
       updated.setVersion(dto.getVersion());
       
        final Set<ActivatedOcrLanguage> list = new HashSet<>();
       
       dto.getOcrLanguages().stream()
                            .map(OcrLanguageMapper.INSTANCE::dtoToObject)
                            .forEach(lng -> {
                                final ActivatedOcrLanguage actLang = new ActivatedOcrLanguage();
                                actLang.setOcrLangConfiguration(updated);
                                actLang.setOcrLanguage(lng);
                                list.add(actLang);
                            });
       updated.setActivatedOcrLanguages(list);
   }
   
   public OcrLangConfigurationDTO mapConfigToDto(final OcrLangConfiguration config, final OcrLangConfigurationDTO dto) {
       dto.setIdentifier(config.getIdentifier());
       dto.setLabel(config.getLabel());
       dto.setActive(config.isActive());
       dto.setLibrary(SimpleLibraryMapper.INSTANCE.libraryToSimpleLibraryDTO(config.getLibrary()));
       dto.setVersion(config.getVersion());
       
       final List<OcrLanguageDTO> languages = config.getActivatedOcrLanguages().stream()
                                               .map(actLang->actLang.getOcrLanguage())
                                               .map(OcrLanguageMapper.INSTANCE::objToDTO)
                                               .collect(Collectors.toList());
       dto.setOcrLanguages(languages);
       return dto;
   }

}
