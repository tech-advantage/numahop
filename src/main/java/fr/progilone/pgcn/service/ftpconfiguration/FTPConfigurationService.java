package fr.progilone.pgcn.service.ftpconfiguration;

import fr.progilone.pgcn.domain.ftpconfiguration.FTPConfiguration;
import fr.progilone.pgcn.exception.PgcnTechnicalException;
import fr.progilone.pgcn.exception.PgcnValidationException;
import fr.progilone.pgcn.exception.message.PgcnError;
import fr.progilone.pgcn.exception.message.PgcnErrorCode;
import fr.progilone.pgcn.exception.message.PgcnList;
import fr.progilone.pgcn.repository.ftpconfiguration.FTPConfigurationRepository;
import fr.progilone.pgcn.repository.library.LibraryRepository;
import fr.progilone.pgcn.repository.lot.LotRepository;
import fr.progilone.pgcn.repository.project.ProjectRepository;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by lebouchp on 03/02/2017.
 */
@Service
public class FTPConfigurationService {

    private final FTPConfigurationRepository ftpConfigurationRepository;
    private final LibraryRepository libraryRepository;
    private final LotRepository lotRepository;
    private final ProjectRepository projectRepository;

    @Autowired
    public FTPConfigurationService(final FTPConfigurationRepository ftpConfigurationRepository,
                                   final LibraryRepository libraryRepository,
                                   final LotRepository lotRepository,
                                   final ProjectRepository projectRepository) {
        this.ftpConfigurationRepository = ftpConfigurationRepository;
        this.libraryRepository = libraryRepository;
        this.lotRepository = lotRepository;
        this.projectRepository = projectRepository;
    }

    @Transactional
    public void delete(final String id) throws PgcnValidationException {
        // Validation de la suppression
        final FTPConfiguration conf = ftpConfigurationRepository.getOne(id);
        validateDelete(conf);

        // Suppression
        ftpConfigurationRepository.deleteById(id);
    }

    private void validateDelete(final FTPConfiguration conf) throws PgcnValidationException {
        final PgcnList<PgcnError> errors = new PgcnList<>();
        final PgcnError.Builder builder = new PgcnError.Builder();

        // Bibliothèque
        final Long libCount = libraryRepository.countByActiveFTPConfiguration(conf);
        if (libCount > 0) {
            errors.add(builder.reinit().setCode(PgcnErrorCode.CONF_FTP_DEL_EXITS_LIB).setAdditionalComplement(libCount).build());
        }
        // Lot
        final Long lotCount = lotRepository.countByActiveFTPConfiguration(conf);
        if (lotCount > 0) {
            errors.add(builder.reinit().setCode(PgcnErrorCode.CONF_FTP_DEL_EXITS_LOT).setAdditionalComplement(lotCount).build());
        }
        // Projet
        final Long projCount = projectRepository.countByActiveFTPConfiguration(conf);
        if (projCount > 0) {
            errors.add(builder.reinit().setCode(PgcnErrorCode.CONF_FTP_DEL_EXITS_PROJECT).setAdditionalComplement(projCount).build());
        }

        if (!errors.isEmpty()) {
            conf.setErrors(errors);
            throw new PgcnValidationException(conf, errors);
        }
    }

    @Transactional
    public FTPConfiguration save(final FTPConfiguration ftpConfiguration) throws PgcnTechnicalException {
        return ftpConfigurationRepository.save(ftpConfiguration);
    }

    @Transactional(readOnly = true)
    public FTPConfiguration getOne(final String identifier) {
        if (identifier == null) {
            return null;
        }
        return ftpConfigurationRepository.findOneWithDependencies(identifier);
    }

    /**
     * Recherche paginée
     *
     * @param search
     * @param libraries
     * @param pageRequest
     * @return
     */
    @Transactional(readOnly = true)
    public Page<FTPConfiguration> search(final String search, final List<String> libraries, final Pageable pageRequest) {
        return ftpConfigurationRepository.search(search, libraries, pageRequest);
    }
}
