package fr.progilone.pgcn.service.administration.omeka;

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

import fr.progilone.pgcn.domain.administration.omeka.OmekaConfiguration;
import fr.progilone.pgcn.domain.dto.administration.omeka.OmekaConfigurationDTO;
import fr.progilone.pgcn.domain.library.Library;
import fr.progilone.pgcn.exception.PgcnTechnicalException;
import fr.progilone.pgcn.exception.PgcnValidationException;
import fr.progilone.pgcn.exception.message.PgcnError;
import fr.progilone.pgcn.exception.message.PgcnErrorCode;
import fr.progilone.pgcn.exception.message.PgcnList;
import fr.progilone.pgcn.repository.administration.omeka.OmekaConfigurationRepository;
import fr.progilone.pgcn.service.administration.mapper.OmekaConfigurationMapper;


@Service
public class OmekaConfigurationService {

    private final OmekaConfigurationRepository omekaConfigurationRepository;

    @Autowired
    public OmekaConfigurationService(final OmekaConfigurationRepository repository) {
        this.omekaConfigurationRepository = repository;
    }
    
    @Transactional(readOnly = true)
    public Set<OmekaConfigurationDTO> findAllDto(final Boolean active) {
        final Set<OmekaConfiguration> confs = Boolean.TRUE.equals(active) ? omekaConfigurationRepository.findByActiveWithDependencies(true)
                                                                         : omekaConfigurationRepository.findAllWithDependencies();
        
        return OmekaConfigurationMapper.INSTANCE.confOmekaToDtos(confs);
    }

    
    /**
     * Liste par bibliothèque
     * @param libraryId
     * @return
     */
    @Transactional(readOnly = true)
    public Set<OmekaConfiguration> findByLibrary(final String libraryId) {
        final Library library = new Library();
        library.setIdentifier(libraryId);
        return omekaConfigurationRepository.findByLibrary(library);
    }

    /**
     * Liste par bibliothèque et actif/inactif
     * @param library
     * @param active
     * @return
     */
    @Transactional(readOnly = true)
    public Set<OmekaConfiguration> findByLibraryAndActive(final Library library, final boolean active) {
        return Boolean.TRUE.equals(active) ? omekaConfigurationRepository.findByLibraryAndActive(library, true)
                                           : omekaConfigurationRepository.findByLibrary(library);

    }

    /**
     * Récupération sous la forme de DTO
     * @param library
     * @param active
     * @return
     */
    @Transactional(readOnly = true)
    public Set<OmekaConfigurationDTO> findDtoByLibrary(final Library library, final Boolean active) {
        final Set<OmekaConfiguration> confs = Boolean.TRUE.equals(active) ? omekaConfigurationRepository.findByLibraryAndActive(library, true)
                                                                          : omekaConfigurationRepository.findByLibrary(library);
        return OmekaConfigurationMapper.INSTANCE.confOmekaToDtos(confs);
    }

    /**
     * Récupération d'un élément
     * @param id
     * @return
     */
    @Transactional(readOnly = true)
    public OmekaConfiguration findOne(final String id) {
        return omekaConfigurationRepository.findOneWithDependencies(id);
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
    public Page<OmekaConfigurationDTO> search(final String search, final List<String> libraries, final Integer page, final Integer size) {

        final Pageable pageRequest = new PageRequest(page, size);

        final Page<OmekaConfiguration> configurations = omekaConfigurationRepository.search(search, libraries, pageRequest);

        final List<OmekaConfigurationDTO>
                results =
                        configurations.getContent().stream().map(OmekaConfigurationMapper.INSTANCE::confOmekaToDto).collect(Collectors.toList());

        return new PageImpl<>(results,
                              new PageRequest(configurations.getNumber(), configurations.getSize(), configurations.getSort()),
                              configurations.getTotalElements());
    }

    @Transactional
    public void delete(final String id) {
        omekaConfigurationRepository.delete(id);
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
    public OmekaConfiguration save(final OmekaConfiguration conf) throws PgcnValidationException, PgcnTechnicalException {
        validate(conf);
        final OmekaConfiguration savedConf = omekaConfigurationRepository.save(conf);
        return omekaConfigurationRepository.findOneWithDependencies(savedConf.getIdentifier());
    }



    /**
     * Validation des champs requis
     * 
     * @param conf
     * @return
     * @throws PgcnValidationException
     */
    private PgcnList<PgcnError> validate(final OmekaConfiguration conf) throws PgcnValidationException {
        final PgcnList<PgcnError> errors = new PgcnList<>();
        final PgcnError.Builder builder = new PgcnError.Builder();

        // le libellé est obligatoire
        if (StringUtils.isEmpty(conf.getLabel())) {
            errors.add(builder.reinit().setCode(PgcnErrorCode.CONF_OMEKA_LABEL_MANDATORY).setField("label").build());
        }
        // la bibliothèque est obligatoire
        if (conf.getLibrary() == null) {
            errors.add(builder.reinit().setCode(PgcnErrorCode.CONF_OMEKA_LIBRARY_MANDATORY).setField("library").build());
        }
        // Retour
        if (!errors.isEmpty()) {
            conf.setErrors(errors);
            throw new PgcnValidationException(conf, errors);
        }
        return errors;
    }

}
