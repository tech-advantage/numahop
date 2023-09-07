package fr.progilone.pgcn.service.administration;

import fr.progilone.pgcn.domain.administration.MailboxConfiguration;
import fr.progilone.pgcn.domain.dto.administration.MailboxConfigurationDTO;
import fr.progilone.pgcn.domain.library.Library;
import fr.progilone.pgcn.exception.PgcnTechnicalException;
import fr.progilone.pgcn.exception.PgcnValidationException;
import fr.progilone.pgcn.exception.message.PgcnError;
import fr.progilone.pgcn.exception.message.PgcnErrorCode;
import fr.progilone.pgcn.exception.message.PgcnList;
import fr.progilone.pgcn.repository.administration.MailboxConfigurationRepository;
import fr.progilone.pgcn.service.administration.mapper.MailboxConfigurationMapper;
import fr.progilone.pgcn.service.util.CryptoService;
import java.util.List;
import java.util.Set;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by Sébastien on 30/12/2016.
 */
@Service
public class MailboxConfigurationService {

    private final MailboxConfigurationRepository mailboxConfigurationRepository;
    private final CryptoService cryptoService;

    @Autowired
    public MailboxConfigurationService(final MailboxConfigurationRepository mailboxConfigurationRepository, final CryptoService cryptoService) {
        this.mailboxConfigurationRepository = mailboxConfigurationRepository;
        this.cryptoService = cryptoService;
    }

    @Transactional(readOnly = true)
    public Set<MailboxConfiguration> findAll(final boolean active) {
        return Boolean.TRUE.equals(active) ? mailboxConfigurationRepository.findByActiveWithDependencies(true)
                                           : mailboxConfigurationRepository.findAllWithDependencies();
    }

    @Transactional(readOnly = true)
    public List<MailboxConfigurationDTO> findAllDto(final Boolean active) {
        final Set<MailboxConfiguration> confs = Boolean.TRUE.equals(active) ? mailboxConfigurationRepository.findByActiveWithDependencies(true)
                                                                            : mailboxConfigurationRepository.findAllWithDependencies();
        return MailboxConfigurationMapper.INSTANCE.mailboxToDtos(confs);
    }

    @Transactional(readOnly = true)
    public Set<MailboxConfiguration> findByLibrary(final String libraryId, final boolean active) {
        final Library library = new Library();
        library.setIdentifier(libraryId);
        return Boolean.TRUE.equals(active) ? mailboxConfigurationRepository.findByLibrary(library)
                                           : mailboxConfigurationRepository.findByLibraryAndActive(library, true);
    }

    @Transactional(readOnly = true)
    public List<MailboxConfigurationDTO> search(final String search, final List<String> libraries, final boolean active) {
        final List<MailboxConfiguration> results = mailboxConfigurationRepository.search(search, libraries, active);
        return MailboxConfigurationMapper.INSTANCE.mailboxToDtos(results);
    }

    @Transactional(readOnly = true)
    public MailboxConfiguration findOne(final String id) {
        return mailboxConfigurationRepository.findOneWithDependencies(id);
    }

    @Transactional
    public void delete(final String id) {
        mailboxConfigurationRepository.deleteById(id);
    }

    @Transactional
    public MailboxConfiguration save(final MailboxConfiguration conf) throws PgcnValidationException, PgcnTechnicalException {
        setDefaultValues(conf);
        validate(conf);
        final MailboxConfiguration savedConf = mailboxConfigurationRepository.save(conf);
        return mailboxConfigurationRepository.findOneWithDependencies(savedConf.getIdentifier());
    }

    private void setDefaultValues(final MailboxConfiguration conf) throws PgcnTechnicalException {
        // Cryptage du mot de passe
        if (conf.getPassword() != null) {
            final String encryptedPassword = cryptoService.encrypt(conf.getPassword());
            conf.setPassword(encryptedPassword);
        }
        // Sinon on reprend le mot de passe existant
        else if (conf.getIdentifier() != null) {
            final String currentPassword = mailboxConfigurationRepository.findPasswordByIdentifier(conf.getIdentifier());
            conf.setPassword(currentPassword);
        }
    }

    private PgcnList<PgcnError> validate(final MailboxConfiguration conf) throws PgcnValidationException {
        final PgcnList<PgcnError> errors = new PgcnList<>();
        final PgcnError.Builder builder = new PgcnError.Builder();

        // le libellé est obligatoire
        if (StringUtils.isEmpty(conf.getLabel())) {
            errors.add(builder.reinit().setCode(PgcnErrorCode.CONF_SFTP_LABEL_MANDATORY).setField("label").build());
        }
        // la bibliothèque est obligatoire
        if (conf.getLibrary() == null) {
            errors.add(builder.reinit().setCode(PgcnErrorCode.CONF_SFTP_LIBRARY_MANDATORY).setField("library").build());
        }
        // Retour
        if (!errors.isEmpty()) {
            conf.setErrors(errors);
            throw new PgcnValidationException(conf, errors);
        }
        return errors;
    }
}
