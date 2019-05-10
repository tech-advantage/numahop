package fr.progilone.pgcn.service.filesgestion;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import fr.progilone.pgcn.domain.filesgestion.FilesGestionConfig;
import fr.progilone.pgcn.exception.PgcnValidationException;
import fr.progilone.pgcn.repository.filesgestion.FilesGestionConfigRepository;

@Service
public class FilesGestionConfigService {
    
    
    @Autowired
    private FilesGestionConfigRepository filesGestionConfigRepository;
    
    
    
    @Transactional
    public FilesGestionConfig getConfigByLibrary(final String libraryIdentifier) {
        return filesGestionConfigRepository.getOneByLibraryIdentifier(libraryIdentifier);
    }
    
    @Transactional
    public List<FilesGestionConfig> getConfigs() {
        return filesGestionConfigRepository.findAll();
    }
    
    @Transactional
    public FilesGestionConfig getOne(final String id) {
        return filesGestionConfigRepository.findOne(id);
    }
    
    @Transactional
    public FilesGestionConfig save(final FilesGestionConfig config) throws PgcnValidationException {
        final FilesGestionConfig saved =  filesGestionConfigRepository.save(config);
        return getOne(saved.getIdentifier());   
    }
    

}
