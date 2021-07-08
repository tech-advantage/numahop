package fr.progilone.pgcn.service.checkconfiguration;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import fr.progilone.pgcn.domain.check.AutomaticCheckType;
import fr.progilone.pgcn.domain.checkconfiguration.AutomaticCheckRule;
import fr.progilone.pgcn.domain.checkconfiguration.CheckConfiguration;
import fr.progilone.pgcn.exception.PgcnValidationException;
import fr.progilone.pgcn.exception.message.PgcnError;
import fr.progilone.pgcn.exception.message.PgcnErrorCode;
import fr.progilone.pgcn.exception.message.PgcnList;
import fr.progilone.pgcn.repository.check.AutomaticCheckTypeRepository;
import fr.progilone.pgcn.repository.checkconfiguration.CheckConfigurationRepository;
import fr.progilone.pgcn.repository.library.LibraryRepository;
import fr.progilone.pgcn.repository.lot.LotRepository;
import fr.progilone.pgcn.repository.project.ProjectRepository;

/**
 * Created by lebouchp on 03/02/2017.
 */
@Service
public class CheckConfigurationService {

    @Autowired
    private CheckConfigurationRepository checkConfigurationRepository;
    @Autowired
    private LibraryRepository libraryRepository;
    @Autowired
    private LotRepository lotRepository;
    @Autowired
    private ProjectRepository projectRepository;
    @Autowired
    private AutomaticCheckRuleService automaticCheckRuleService;
    @Autowired
    private AutomaticCheckTypeRepository checkTypeRepository;

    /**
     * Suppression
     *
     * @param id
     */
    @Transactional
    public void delete(final String id) throws PgcnValidationException {
        // Validation de la suppression
        final CheckConfiguration conf = checkConfigurationRepository.getOne(id);
        validateDelete(conf);

        // Suppression
        checkConfigurationRepository.delete(id);
    }

    private void validateDelete(final CheckConfiguration conf) throws PgcnValidationException {
        final PgcnList<PgcnError> errors = new PgcnList<>();
        final PgcnError.Builder builder = new PgcnError.Builder();

        // Bibliothèque
        final Long libCount = libraryRepository.countByActiveCheckConfiguration(conf);
        if (libCount > 0) {
            errors.add(builder.reinit().setCode(PgcnErrorCode.CONF_CHECK_DEL_EXITS_LIB).setAdditionalComplement(libCount).build());
        }
        // Lot
        final Long lotCount = lotRepository.countByActiveCheckConfiguration(conf);
        if (lotCount > 0) {
            errors.add(builder.reinit().setCode(PgcnErrorCode.CONF_CHECK_DEL_EXITS_LOT).setAdditionalComplement(lotCount).build());
        }
        // Projet
        final Long projCount = projectRepository.countByActiveCheckConfiguration(conf);
        if (projCount > 0) {
            errors.add(builder.reinit().setCode(PgcnErrorCode.CONF_CHECK_DEL_EXITS_PROJECT).setAdditionalComplement(projCount).build());
        }

        if (!errors.isEmpty()) {
            conf.setErrors(errors);
            throw new PgcnValidationException(conf, errors);
        }
    }

    /**
     * Sauvegarde
     *
     * @param checkConfiguration
     * @return
     */
    @Transactional
    public CheckConfiguration save(final CheckConfiguration checkConfiguration) {
        return checkConfigurationRepository.save(checkConfiguration);
    }

    /**
     * Récupération d'une {@link CheckConfiguration}
     *
     * @param identifier
     * @return
     */
    @Transactional(readOnly = true)
    public CheckConfiguration findOne(final String identifier) {
        if (identifier == null) {
            return null;
        }
        return checkConfigurationRepository.findOneWithDependencies(identifier);
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
    public Page<CheckConfiguration> search(final String search, final List<String> libraries, final Pageable pageRequest) {
        return checkConfigurationRepository.search(search, libraries, pageRequest);
    }

    public CheckConfiguration duplicateCheckConfiguration(final String id) {
        final CheckConfiguration original = findOne(id);
        final CheckConfiguration duplicated = new CheckConfiguration();
        copyAttributes(original, duplicated);
        return duplicated;
    }

    private void copyAttributes(final CheckConfiguration source, final CheckConfiguration destination) {
        destination.setLibrary(source.getLibrary());
        destination.setMajorErrorRate(source.getMajorErrorRate());
        destination.setMinorErrorRate(source.getMinorErrorRate());
        destination.setSampleRate(source.getSampleRate());
        destination.setSeparators(source.getSeparators());
        destination.setAutomaticCheckRules(new ArrayList<>());
        for(final AutomaticCheckRule acr : source.getAutomaticCheckRules()) {
            destination.getAutomaticCheckRules().add(automaticCheckRuleService.duplicateAutomaticCheckRule(acr));
        }
    }

    /**
     * Permet d'ajouter le controle du radical dans toutes les configurations de contrôles
     */
    @Transactional
    public void updateCheckConfigurationAddRadicalControl() {

        final List<CheckConfiguration> checkConfigurations = checkConfigurationRepository.findAll();

        checkConfigurations.forEach(checkConfiguration -> {
            final List<AutomaticCheckRule> automaticCheckRules = checkConfiguration.getAutomaticCheckRules();
            final AutomaticCheckRule fileRadicalRule = automaticCheckRules.stream()
                                                                          .filter(rule -> rule.getAutomaticCheckType()
                                                                                              .getType()
                                                                                              .equals(AutomaticCheckType.AutoCheckType.FILE_RADICAL))
                                                                          .findAny()
                                                                          .orElse(null);

            if (fileRadicalRule == null) {
                final AutomaticCheckRule newFileRadicalRule = new AutomaticCheckRule();
                newFileRadicalRule.setActive(true);
                newFileRadicalRule.setBlocking(true);
                newFileRadicalRule.setAutomaticCheckType(checkTypeRepository.findOne("automatic_file_radical"));
                checkConfiguration.addAutomaticCheckRule(newFileRadicalRule);
            }
        });
    }
}
