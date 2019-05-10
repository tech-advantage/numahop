package fr.progilone.pgcn.service.administration.viewsformat;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import fr.progilone.pgcn.domain.administration.viewsformat.ViewsFormatConfiguration;
import fr.progilone.pgcn.domain.lot.Lot;
import fr.progilone.pgcn.exception.PgcnValidationException;
import fr.progilone.pgcn.exception.message.PgcnError;
import fr.progilone.pgcn.exception.message.PgcnErrorCode;
import fr.progilone.pgcn.exception.message.PgcnList;
import fr.progilone.pgcn.repository.administration.viewsformat.ViewsFormatConfigurationRepository;
import fr.progilone.pgcn.repository.library.LibraryRepository;
import fr.progilone.pgcn.repository.lot.LotRepository;
import fr.progilone.pgcn.repository.project.ProjectRepository;
import fr.progilone.pgcn.service.util.DefaultFileFormats;

@Service
public class ViewsFormatConfigurationService {
    
    private final ViewsFormatConfigurationRepository viewsFormatConfigurationRepository;
    private final LibraryRepository libraryRepository;
    private final ProjectRepository projectRepository;
    private final LotRepository lotRepository;
    private final DefaultFileFormats defaultFormats;
    
    @Autowired
    public ViewsFormatConfigurationService(final ViewsFormatConfigurationRepository viewsFormatConfigurationRepository,
                                           final LibraryRepository libraryRepository,
                                           final ProjectRepository projectRepository,
                                           final LotRepository lotRepository,
                                           final DefaultFileFormats defaultFormats) {
        this.viewsFormatConfigurationRepository = viewsFormatConfigurationRepository;
        this.libraryRepository = libraryRepository;
        this.projectRepository = projectRepository;
        this.lotRepository = lotRepository;
        this.defaultFormats = defaultFormats;
        
    }
    
    /**
     * Récupération d'une {@link ViewsFormatConfiguration}
     *
     * @param identifier
     * @return
     */
    @Transactional(readOnly = true)
    public ViewsFormatConfiguration findOne(final String identifier) {
        if (identifier == null) {
            return null;
        }
        return viewsFormatConfigurationRepository.findOneWithDependencies(identifier);
    }
    
    /**
     * Récupération d'une {@link ViewsFormatConfiguration} par l'id de lot.
     *
     * @param identifier
     * @return
     */
    @Transactional(readOnly = true)
    public ViewsFormatConfiguration getOneByLot(final String lotIdentifier) {
        if (lotIdentifier == null) {
            return null;
        }
        final Lot lot = lotRepository.findOneWithActiveCheckConfiguration(lotIdentifier);
        // retrouve la conf de format d'images dans la hierarchie
        ViewsFormatConfiguration formatConf = null;
        if (lot.getActiveFormatConfiguration() == null || lot.getActiveFormatConfiguration().getIdentifier() == null) {
            formatConf = lot.getProject().getActiveFormatConfiguration();
            if (formatConf == null) {
                formatConf = lot.getProject().getLibrary().getActiveFormatConfiguration();
            }
        } else {
            formatConf = lot.getActiveFormatConfiguration();
        }
        if (formatConf != null) {
            formatConf.setDefaultFormats(defaultFormats);
        }
        return formatConf;
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
    public Page<ViewsFormatConfiguration> search(final String search, final List<String> libraries, final Pageable pageRequest) {
        return viewsFormatConfigurationRepository.search(search, libraries, pageRequest);
    }
    
    /**
     * Sauvegarde
     *
     * @param checkConfiguration
     * @return
     */
    @Transactional
    public ViewsFormatConfiguration save(final ViewsFormatConfiguration formatConfiguration) {
        return viewsFormatConfigurationRepository.save(formatConfiguration);
    }

    /**
     * Suppression apres controle.
     *
     * @param id
     */
    @Transactional
    public void delete(final String id) throws PgcnValidationException {
        // Validation de la suppression
        final ViewsFormatConfiguration conf = viewsFormatConfigurationRepository.getOne(id);
        validateDelete(conf);
        // Suppression
        viewsFormatConfigurationRepository.delete(id);
    }

    private void validateDelete(final ViewsFormatConfiguration conf) throws PgcnValidationException {
        final PgcnList<PgcnError> errors = new PgcnList<>();
        final PgcnError.Builder builder = new PgcnError.Builder();

        // Bibliothèque
        final Long libCount = libraryRepository.countByActiveFormatConfiguration(conf);
        if (libCount > 0) {
            errors.add(builder.reinit().setCode(PgcnErrorCode.CONF_CHECK_DEL_EXITS_LIB).setAdditionalComplement(libCount).build());
        }
     // Projet
        final Long projCount = projectRepository.countByActiveFormatConfiguration(conf);
        if (projCount > 0) {
            errors.add(builder.reinit().setCode(PgcnErrorCode.CONF_CHECK_DEL_EXITS_PROJECT).setAdditionalComplement(projCount).build());
        }
        // Lot
        final Long lotCount = lotRepository.countByActiveFormatConfiguration(conf);
        if (lotCount > 0) {
            errors.add(builder.reinit().setCode(PgcnErrorCode.CONF_CHECK_DEL_EXITS_LOT).setAdditionalComplement(lotCount).build());
        }
        
        if (!errors.isEmpty()) {
            conf.setErrors(errors);
            throw new PgcnValidationException(conf, errors);
        }
    }

}
