package fr.progilone.pgcn.service.ocrlangconfiguration;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import fr.progilone.pgcn.domain.dto.ocrlangconfiguration.OcrLanguageDTO;
import fr.progilone.pgcn.domain.ocrlangconfiguration.OcrLanguage;
import fr.progilone.pgcn.repository.ocrlangconfiguration.OcrLanguageRepository;
import fr.progilone.pgcn.service.ocrlangconfiguration.mapper.OcrLanguageMapper;

@Service
public class OcrLanguageService {
    
    private final OcrLanguageRepository ocrLanguageRepository; 
    
    public OcrLanguageService(final OcrLanguageRepository ocrLanguageRepository) {
        this.ocrLanguageRepository = ocrLanguageRepository;
    }
    
    
    @Transactional(readOnly = true)
    public List<OcrLanguageDTO> findAll() {
        final List<OcrLanguage> langs = ocrLanguageRepository.findAllByOrderByLabelAsc();
        return OcrLanguageMapper.INSTANCE.objsToDtos(langs);
    }
    
    @Transactional(readOnly = true)
    public OcrLanguageDTO getLanguageDTO(final String id) {
        return OcrLanguageMapper.INSTANCE.objToDTO(ocrLanguageRepository.getOne(id));
    }
    
    @Transactional(readOnly = true)
    public OcrLanguage getOne(final String id) {
        return ocrLanguageRepository.getOne(id);
    }
    
//    @Transactional(readOnly = true)
//    public List<OcrLanguageDTO> findLangsByLibrary(final String libraryId) {
//        
//        
//        //final List<OcrLanguage> langs = ocrLanguageRepository.findLanguagesByLibrary(libraryId);
//        
//        final List<ActivatedOcrLanguage> langs = ocrLanguageRepository.findLanguagesByLibrary(libraryId);
//        
//        return null; //OcrLanguageMapper.INSTANCE.objsToDtos(langs);
//    }

}
