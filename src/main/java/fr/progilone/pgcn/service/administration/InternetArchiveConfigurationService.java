package fr.progilone.pgcn.service.administration;

import fr.progilone.pgcn.domain.administration.InternetArchiveConfiguration;
import fr.progilone.pgcn.domain.dto.administration.InternetArchiveConfigurationDTO;
import fr.progilone.pgcn.domain.library.Library;
import fr.progilone.pgcn.exception.PgcnTechnicalException;
import fr.progilone.pgcn.exception.PgcnValidationException;
import fr.progilone.pgcn.exception.message.PgcnError;
import fr.progilone.pgcn.exception.message.PgcnErrorCode;
import fr.progilone.pgcn.exception.message.PgcnList;
import fr.progilone.pgcn.repository.administration.InternetArchiveConfigurationRepository;
import fr.progilone.pgcn.service.administration.mapper.InternetArchiveConfigurationMapper;
import fr.progilone.pgcn.service.util.CryptoService;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service de gestion des {@link InternetArchiveConfiguration}
 *
 * @author jbrunet
 *         Créé le 19 avr. 2017
 */
@Service
public class InternetArchiveConfigurationService {

    private final InternetArchiveConfigurationRepository iaConfigurationRepository;
    private final CryptoService cryptoService;

    @Autowired
    public InternetArchiveConfigurationService(final InternetArchiveConfigurationRepository iaConfigurationRepository, final CryptoService cryptoService) {
        this.iaConfigurationRepository = iaConfigurationRepository;
        this.cryptoService = cryptoService;
    }

    @Transactional(readOnly = true)
    public Set<InternetArchiveConfigurationDTO> findAllDto(final Boolean active) {
        final Set<InternetArchiveConfiguration> confs = Boolean.TRUE.equals(active) ? iaConfigurationRepository.findByActiveWithDependencies(true)
                                                                                    : iaConfigurationRepository.findAllWithDependencies();
        return InternetArchiveConfigurationMapper.INSTANCE.configurationIAToDtos(confs);
    }

    /**
     * Liste par bibliothèque
     *
     * @param libraryId
     * @return
     */
    @Transactional(readOnly = true)
    public Set<InternetArchiveConfiguration> findByLibrary(final String libraryId) {
        final Library library = new Library();
        library.setIdentifier(libraryId);
        return iaConfigurationRepository.findByLibrary(library);
    }

    /**
     * Liste par bibliothèque et actif/inactif
     *
     * @param library
     * @param active
     * @return
     */
    @Transactional(readOnly = true)
    public Set<InternetArchiveConfiguration> findByLibraryAndActive(final Library library, final boolean active) {
        return Boolean.TRUE.equals(active) ? iaConfigurationRepository.findByLibraryAndActive(library, true)
                                           : iaConfigurationRepository.findByLibrary(library);

    }

    /**
     * Récupération sous la forme de DTO
     *
     * @param library
     * @param active
     * @return
     */
    @Transactional(readOnly = true)
    public Set<InternetArchiveConfigurationDTO> findDtoByLibrary(final Library library, final Boolean active) {
        final Set<InternetArchiveConfiguration> confs = Boolean.TRUE.equals(active) ? iaConfigurationRepository.findByLibraryAndActive(library, true)
                                                                                    : iaConfigurationRepository.findByLibrary(library);
        return InternetArchiveConfigurationMapper.INSTANCE.configurationIAToDtos(confs);
    }

    /**
     * Récupération d'un élément
     *
     * @param id
     * @return
     */
    @Transactional(readOnly = true)
    public InternetArchiveConfiguration findOne(final String id) {
        return iaConfigurationRepository.findOneWithDependencies(id);
    }

    /**
     * Récupération des résultats de recherche
     *
     * @param search
     * @param libraries
     * @param page
     * @param size
     * @return
     */
    @Transactional(readOnly = true)
    public Page<InternetArchiveConfigurationDTO> search(String search, List<String> libraries, Integer page, Integer size) {

        final Pageable pageRequest = PageRequest.of(page, size);

        Page<InternetArchiveConfiguration> configurations = iaConfigurationRepository.search(search, libraries, pageRequest);

        List<InternetArchiveConfigurationDTO> results = configurations.getContent()
                                                                      .stream()
                                                                      .map(InternetArchiveConfigurationMapper.INSTANCE::configurationIAToDto)
                                                                      .collect(Collectors.toList());

        return new PageImpl<>(results, PageRequest.of(configurations.getNumber(), configurations.getSize(), configurations.getSort()), configurations.getTotalElements());
    }

    @Transactional
    public void delete(final String id) {
        iaConfigurationRepository.deleteById(id);
    }

    /**
     * Sauvegarde avec validation
     *
     * @param conf
     * @return
     * @throws PgcnValidationException
     * @throws PgcnTechnicalException
     */
    @Transactional
    public InternetArchiveConfiguration save(final InternetArchiveConfiguration conf) throws PgcnValidationException, PgcnTechnicalException {
        setDefaultValues(conf);
        validate(conf);
        final InternetArchiveConfiguration savedConf = iaConfigurationRepository.save(conf);
        return iaConfigurationRepository.findOneWithDependencies(savedConf.getIdentifier());
    }

    /**
     * Cryptage de la clé au besoin
     *
     * @param conf
     * @throws PgcnTechnicalException
     */
    private void setDefaultValues(final InternetArchiveConfiguration conf) throws PgcnTechnicalException {
        // Cryptage de la clé secrète S3
        if (conf.getSecretKey() != null) {
            final String encryptedPassword = cryptoService.encrypt(conf.getSecretKey());
            conf.setSecretKey(encryptedPassword);
        }
        // Sinon on reprend la clé secrète S3 existante
        else if (conf.getIdentifier() != null) {
            final String currentPassword = iaConfigurationRepository.findSecretKeyByIdentifier(conf.getIdentifier());
            conf.setSecretKey(currentPassword);
        }
    }

    /**
     * Validation des champs requis
     *
     * @param conf
     * @return
     * @throws PgcnValidationException
     */
    private PgcnList<PgcnError> validate(final InternetArchiveConfiguration conf) throws PgcnValidationException {
        final PgcnList<PgcnError> errors = new PgcnList<>();
        final PgcnError.Builder builder = new PgcnError.Builder();

        // le libellé est obligatoire
        if (StringUtils.isEmpty(conf.getLabel())) {
            errors.add(builder.reinit().setCode(PgcnErrorCode.CONF_IA_LABEL_MANDATORY).setField("label").build());
        }
        // la bibliothèque est obligatoire
        if (conf.getLibrary() == null) {
            errors.add(builder.reinit().setCode(PgcnErrorCode.CONF_IA_LIBRARY_MANDATORY).setField("library").build());
        }
        // Retour
        if (!errors.isEmpty()) {
            conf.setErrors(errors);
            throw new PgcnValidationException(conf, errors);
        }
        return errors;
    }
}
