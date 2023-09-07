package fr.progilone.pgcn.service.document.conditionreport;

import fr.progilone.pgcn.domain.document.conditionreport.ConditionReportSlipConfiguration;
import fr.progilone.pgcn.domain.library.Library;
import fr.progilone.pgcn.service.library.LibraryService;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ConditionReportSlipConfigurationService {

    private final LibraryService libraryService;

    @Autowired
    public ConditionReportSlipConfigurationService(final LibraryService libraryService) {
        this.libraryService = libraryService;
    }

    @Transactional
    public Optional<ConditionReportSlipConfiguration> getOneByLibrary(final String identifier) {
        final Library library = libraryService.findOne(identifier);
        if (library == null) {
            return Optional.empty();
        }
        ConditionReportSlipConfiguration config = library.getCondReportSlipConfiguration();
        if (config == null) {
            config = getDefaultConfig();
            library.setCondReportSlipConfiguration(config);
            config.setLibrary(library);
            libraryService.save(library);
            final Library savedLibrary = libraryService.findOneWithDependencies(identifier);
            config = savedLibrary.getCondReportSlipConfiguration();
        }
        return Optional.of(config);
    }

    @Transactional
    public ConditionReportSlipConfiguration update(final ConditionReportSlipConfiguration condSlipConfiguration) {
        final Library library = libraryService.findOne(condSlipConfiguration.getLibrary().getIdentifier());
        library.setCondReportSlipConfiguration(condSlipConfiguration);
        libraryService.save(library);
        return condSlipConfiguration;
    }

    private ConditionReportSlipConfiguration getDefaultConfig() {
        final ConditionReportSlipConfiguration config = new ConditionReportSlipConfiguration();
        config.setPgcnId(true);
        config.setTitle(true);
        config.setNbPages(true);
        config.setGlobalReport(true);

        return config;
    }
}
