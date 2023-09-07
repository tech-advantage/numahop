package fr.progilone.pgcn.service.administration.digitallibrary;

import fr.progilone.pgcn.domain.administration.digitallibrary.DigitalLibraryConfiguration;
import fr.progilone.pgcn.domain.dto.administration.digitallibrary.DigitalLibraryConfigurationDTO;
import fr.progilone.pgcn.domain.library.Library;
import fr.progilone.pgcn.exception.PgcnTechnicalException;
import fr.progilone.pgcn.exception.PgcnValidationException;
import fr.progilone.pgcn.exception.message.PgcnError;
import fr.progilone.pgcn.exception.message.PgcnErrorCode;
import fr.progilone.pgcn.exception.message.PgcnList;
import fr.progilone.pgcn.repository.administration.digitallibrary.DigitalLibraryConfigurationRepository;
import fr.progilone.pgcn.service.administration.mapper.DigitalLibraryConfigurationMapper;
import fr.progilone.pgcn.service.library.LibraryService;
import fr.progilone.pgcn.service.util.CryptoService;
import java.util.List;
import java.util.Set;
import org.apache.commons.lang3.StringUtils;
import org.mapstruct.factory.Mappers;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service de gestion des DigitalLibraryConfiguration
 */
@Service
public class DigitalLibraryConfigurationService {

    private final DigitalLibraryConfigurationRepository repository;
    private final LibraryService libraryService;
    private final CryptoService cryptoService;

    private final DigitalLibraryConfigurationMapper mapper = Mappers.getMapper(DigitalLibraryConfigurationMapper.class);

    public DigitalLibraryConfigurationService(final DigitalLibraryConfigurationRepository repository, final LibraryService libraryService, final CryptoService cryptoService) {
        this.repository = repository;
        this.libraryService = libraryService;
        this.cryptoService = cryptoService;
    }

    @Transactional(readOnly = true)
    public List<DigitalLibraryConfiguration> findAll() {
        return repository.findAll();
    }

    @Transactional(readOnly = true)
    public List<DigitalLibraryConfigurationDTO> findAllDto() {
        return (List<DigitalLibraryConfigurationDTO>) mapper.configurationsToDtos(repository.findAll());
    }

    @Transactional(readOnly = true)
    public DigitalLibraryConfiguration findOne(final String identifier) {
        return repository.findById(identifier).orElse(null);
    }

    @Transactional(readOnly = true)
    public DigitalLibraryConfiguration findOneWithDependencies(final String identifier) {
        return repository.findOneWithDependencies(identifier);
    }

    @Transactional(readOnly = true)
    public DigitalLibraryConfigurationDTO getById(final String identifier) {
        return mapper.configurationToDto(repository.findById(identifier).orElse(null));
    }

    /**
     * Liste par bibliothèque
     */
    @Transactional(readOnly = true)
    public Set<DigitalLibraryConfiguration> findByLibrary(final String libraryId) {
        return repository.findByLibrary(libraryId);
    }

    /**
     * Liste par bibliothèque et actif/inactif
     */
    @Transactional(readOnly = true)
    public Set<DigitalLibraryConfigurationDTO> findByLibraryAndActiveDTO(final Library library, final boolean active) {
        return (Set<DigitalLibraryConfigurationDTO>) mapper.configurationsToDtos(findByLibraryAndActive(library.getIdentifier(), active));

    }

    /**
     * Liste par bibliothèque et actif/inactif
     */
    @Transactional(readOnly = true)
    public Set<DigitalLibraryConfiguration> findByLibraryAndActive(final String library, final boolean active) {
        return Boolean.TRUE.equals(active) ? repository.findByLibraryAndActive(libraryService.findOne(library), true)
                                           : repository.findByLibrary(library);
    }

    @Transactional
    public void delete(final String id) {
        repository.deleteById(id);
    }

    /**
     * Sauvegarde avec validation
     */
    @Transactional
    public DigitalLibraryConfiguration save(final DigitalLibraryConfiguration conf) throws PgcnValidationException, PgcnTechnicalException {
        setDefaultValues(conf);
        validate(conf);
        final DigitalLibraryConfiguration savedConf = repository.save(conf);
        return repository.findById(savedConf.getIdentifier()).orElse(null);
    }

    private void setDefaultValues(final DigitalLibraryConfiguration conf) throws PgcnTechnicalException {
        // Cryptage du mot de passe
        if (conf.getPassword() != null) {
            final String encryptedPassword = cryptoService.encrypt(conf.getPassword());
            conf.setPassword(encryptedPassword);
        }
        // Sinon on reprend le mot de passe existant
        else if (conf.getIdentifier() != null) {
            final String currentPassword = repository.findPasswordByIdentifier(conf.getIdentifier());
            conf.setPassword(currentPassword);
        }
    }

    /**
     * Validation des champs requis
     */
    private void validate(final DigitalLibraryConfiguration conf) throws PgcnValidationException {
        final PgcnList<PgcnError> errors = new PgcnList<>();
        final PgcnError.Builder builder = new PgcnError.Builder();

        // le libellé est obligatoire
        if (StringUtils.isEmpty(conf.getLabel())) {
            errors.add(builder.reinit().setCode(PgcnErrorCode.CONF_DIGITAL_LIBRARY_LABEL_MANDATORY).setField("label").build());
        }
        // la bibliothèque est obligatoire
        if (conf.getLibrary() == null) {
            errors.add(builder.reinit().setCode(PgcnErrorCode.CONF_DIGITAL_LIBRARY_LIBRARY_MANDATORY).setField("library").build());
        }
        // Retour
        if (!errors.isEmpty()) {
            conf.setErrors(errors);
            throw new PgcnValidationException(conf, errors);
        }
    }

    public DigitalLibraryConfiguration create(final DigitalLibraryConfiguration configuration) throws PgcnTechnicalException {
        return repository.save(configuration);
    }

    public Page<DigitalLibraryConfigurationDTO> search(final String search, final List<String> filteredLibraries, final Integer page, final Integer size) {
        final Pageable pageRequest = PageRequest.of(page, size);

        return repository.search(search, filteredLibraries, pageRequest).map(mapper::configurationToDto);
    }
}
