package fr.progilone.pgcn.service.exportftpconfiguration;

import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import fr.progilone.pgcn.domain.exportftpconfiguration.ExportFTPConfiguration;
import fr.progilone.pgcn.domain.library.Library;
import fr.progilone.pgcn.exception.PgcnTechnicalException;
import fr.progilone.pgcn.exception.PgcnValidationException;
import fr.progilone.pgcn.exception.message.PgcnError;
import fr.progilone.pgcn.exception.message.PgcnErrorCode;
import fr.progilone.pgcn.exception.message.PgcnList;
import fr.progilone.pgcn.repository.exportftpconfiguration.ExportFTPConfigurationRepository;
import fr.progilone.pgcn.repository.library.LibraryRepository;
import fr.progilone.pgcn.repository.lot.LotRepository;
import fr.progilone.pgcn.repository.project.ProjectRepository;

@Service
public class ExportFTPConfigurationService {

    private final ExportFTPConfigurationRepository exportFtpConfigurationRepository;
    private final LibraryRepository libraryRepository;
    private final LotRepository lotRepository;
    private final ProjectRepository projectRepository;

    @Autowired
    public ExportFTPConfigurationService(final ExportFTPConfigurationRepository exportFtpConfigurationRepository,
                                   final LibraryRepository libraryRepository,
                                   final LotRepository lotRepository,
                                   final ProjectRepository projectRepository) {
        this.exportFtpConfigurationRepository = exportFtpConfigurationRepository;
        this.libraryRepository = libraryRepository;
        this.lotRepository = lotRepository;
        this.projectRepository = projectRepository;
    }

    @Transactional
    public ExportFTPConfiguration save(final ExportFTPConfiguration conf) throws PgcnTechnicalException {
        return exportFtpConfigurationRepository.save(conf);
    }

    @Transactional(readOnly = true)
    public ExportFTPConfiguration getOne(final String identifier) {
        if (identifier == null) {
            return null;
        }
        return exportFtpConfigurationRepository.findOneWithDependencies(identifier);
    }
    
    public Set<ExportFTPConfiguration> findByLibraryAndActive(final Library library, final boolean active) {
        return exportFtpConfigurationRepository.findByLibraryAndActive(library, active);
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
    public Page<ExportFTPConfiguration> search(final String search, final List<String> libraries, final Pageable pageRequest) {
        return exportFtpConfigurationRepository.search(search, libraries, pageRequest);
    }
    
    @Transactional
    public void delete(final String id) throws PgcnValidationException {
        // Validation de la suppression
        final ExportFTPConfiguration conf = exportFtpConfigurationRepository.getOne(id);
        validateDelete(conf);

        // Suppression
        exportFtpConfigurationRepository.delete(id);
    }
    
    private void validateDelete(final ExportFTPConfiguration conf) throws PgcnValidationException {
        final PgcnList<PgcnError> errors = new PgcnList<>();
        final PgcnError.Builder builder = new PgcnError.Builder();

        // Bibliothèque
        final Long libCount = libraryRepository.countByActiveExportFTPConfiguration(conf);
        if (libCount > 0) {
            errors.add(builder.reinit().setCode(PgcnErrorCode.CONF_FTP_DEL_EXITS_LIB).setAdditionalComplement(libCount).build());
        }
        // Lot
        final Long lotCount = lotRepository.countByActiveExportFTPConfiguration(conf);
        if (lotCount > 0) {
            errors.add(builder.reinit().setCode(PgcnErrorCode.CONF_FTP_DEL_EXITS_LOT).setAdditionalComplement(lotCount).build());
        }
        // Projet
        final Long projCount = projectRepository.countByActiveExportFTPConfiguration(conf);
        if (projCount > 0) {
            errors.add(builder.reinit().setCode(PgcnErrorCode.CONF_FTP_DEL_EXITS_PROJECT).setAdditionalComplement(projCount).build());
        }

        if (!errors.isEmpty()) {
            conf.setErrors(errors);
            throw new PgcnValidationException(conf, errors);
        }
    }
}
