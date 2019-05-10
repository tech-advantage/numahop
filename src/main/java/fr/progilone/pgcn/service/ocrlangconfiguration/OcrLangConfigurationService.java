package fr.progilone.pgcn.service.ocrlangconfiguration;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import fr.progilone.pgcn.domain.ocrlangconfiguration.OcrLangConfiguration;
import fr.progilone.pgcn.exception.PgcnValidationException;
import fr.progilone.pgcn.exception.message.PgcnError;
import fr.progilone.pgcn.exception.message.PgcnErrorCode;
import fr.progilone.pgcn.exception.message.PgcnList;
import fr.progilone.pgcn.repository.library.LibraryRepository;
import fr.progilone.pgcn.repository.ocrlangconfiguration.OcrLangConfigurationRepository;

@Service
public class OcrLangConfigurationService {

    private final OcrLangConfigurationRepository ocrLangConfigurationRepository;
    private final LibraryRepository libraryRepository;
    
    @Autowired
    public OcrLangConfigurationService(final OcrLangConfigurationRepository ocrLangConfigurationRepository,
                                       final LibraryRepository libraryRepository) {
        this.ocrLangConfigurationRepository = ocrLangConfigurationRepository;
        this.libraryRepository = libraryRepository;
    }
    
    
    /**
     * Suppression d'une conf.
     *
     * @param id
     */
    @Transactional
    public void delete(final String id) throws PgcnValidationException {
        // Validation de la suppression
        final OcrLangConfiguration conf = ocrLangConfigurationRepository.getOne(id);
        validateDelete(conf);

        // Suppression
        ocrLangConfigurationRepository.delete(id);
    }

    private void validateDelete(final OcrLangConfiguration conf) throws PgcnValidationException {
        final PgcnList<PgcnError> errors = new PgcnList<>();
        final PgcnError.Builder builder = new PgcnError.Builder();
        // Bibliothèque
        final Long libCount = libraryRepository.countByActiveOcrLangConfiguration(conf);
        if (libCount > 0) {
            errors.add(builder.reinit().setCode(PgcnErrorCode.CONF_CHECK_DEL_EXITS_LIB).setAdditionalComplement(libCount).build());
        }
        if (!errors.isEmpty()) {
            conf.setErrors(errors);
            throw new PgcnValidationException(conf, errors);
        }
    }
    
    
    /**
     * Sauvegarde
     *
     * @param OcrLangConfiguration
     * @return
     */
    @Transactional
    public OcrLangConfiguration save(final OcrLangConfiguration configuration) {
        return ocrLangConfigurationRepository.save(configuration);
    }

    /**
     * Récupération d'une {@link OcrLangConfiguration}
     *
     * @param identifier
     * @return
     */
    @Transactional(readOnly = true)
    public OcrLangConfiguration findOne(final String identifier) {
        if (identifier == null) {
            return null;
        }
        return ocrLangConfigurationRepository.findOneWithDependencies(identifier);
    }
    
    
    /**
     * Recherche paginée paramétrée
     *
     * @param search
     * @param libraries
     * @param pageRequest
     * @return
     */
    @Transactional(readOnly = true)
    public Page<OcrLangConfiguration> search(final String search, final List<String> libraries, final Pageable pageRequest) {
        return ocrLangConfigurationRepository.search(search, libraries, pageRequest);
    }
}
