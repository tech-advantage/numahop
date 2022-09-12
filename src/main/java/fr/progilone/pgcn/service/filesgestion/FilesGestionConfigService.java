package fr.progilone.pgcn.service.filesgestion;

import java.util.List;

import fr.progilone.pgcn.domain.dto.filesgestion.FilesGestionConfigDTO;
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
    public FilesGestionConfigDTO getConfigByLibrary(final String libraryIdentifier) {
        return FilesGestionConfigMapper.INSTANCE.configToConfigDto(filesGestionConfigRepository.getOneByLibraryIdentifier(libraryIdentifier));
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
    public FilesGestionConfigDTO save(final FilesGestionConfig config) throws PgcnValidationException {
        final FilesGestionConfig saved =  filesGestionConfigRepository.save(config);

        return FilesGestionConfigMapper.INSTANCE.configToConfigDto(getOne(saved.getIdentifier()));
    }
}
