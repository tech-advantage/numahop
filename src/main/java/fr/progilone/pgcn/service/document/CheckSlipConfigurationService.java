package fr.progilone.pgcn.service.document;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import fr.progilone.pgcn.domain.document.CheckSlipConfiguration;
import fr.progilone.pgcn.domain.library.Library;
import fr.progilone.pgcn.service.library.LibraryService;

@Service
public class CheckSlipConfigurationService {
    
    private final LibraryService libraryService;

    @Autowired
    public CheckSlipConfigurationService(final LibraryService libraryService) {
        this.libraryService = libraryService;
    }

    @Transactional
    public Optional<CheckSlipConfiguration> getOneByLibrary(final String identifier) {
        final Library library = libraryService.findOne(identifier);
        if (library == null) {
            return Optional.empty();
        }
        CheckSlipConfiguration config = library.getCheckSlipConfiguration();
        if(config == null) {
            config = getDefaultConfig();
            library.setCheckSlipConfiguration(config);
            config.setLibrary(library);
            libraryService.save(library);
            final Library savedLibrary = libraryService.findOneWithDependencies(identifier);
            config = savedLibrary.getCheckSlipConfiguration();
        }
        return Optional.of(config);
    }

    @Transactional
    public CheckSlipConfiguration update(final CheckSlipConfiguration checkSlipConfiguration) {
        final Library library = libraryService.findOne(checkSlipConfiguration.getLibrary().getIdentifier());
        library.setCheckSlipConfiguration(checkSlipConfiguration);
        libraryService.save(library);
        return checkSlipConfiguration;
    }
    
    
    private CheckSlipConfiguration getDefaultConfig() {
        final CheckSlipConfiguration config = new CheckSlipConfiguration();
        config.setPgcnId(true);
        config.setTitle(true);
        config.setState(true);
        config.setErrors(true);
        config.setNbPages(true);
        config.setNbPagesToBill(true);
        
        return config;
    }

}
