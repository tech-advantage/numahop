package fr.progilone.pgcn.service.document.conditionreport.ui;

import fr.progilone.pgcn.domain.document.conditionreport.DescriptionProperty;
import fr.progilone.pgcn.domain.document.conditionreport.PropertyConfiguration;
import fr.progilone.pgcn.domain.dto.document.conditionreport.PropertyConfigurationDTO;
import fr.progilone.pgcn.domain.library.Library;
import fr.progilone.pgcn.repository.library.LibraryRepository;
import fr.progilone.pgcn.service.document.conditionreport.PropertyConfigurationService;
import fr.progilone.pgcn.service.document.mapper.PropertyConfigurationMapper;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UiPropertyConfigurationService {

    private static final PropertyConfigurationMapper MAPPER = PropertyConfigurationMapper.INSTANCE;

    private final PropertyConfigurationService propertyConfigurationService;
    private final LibraryRepository libraryRepository;

    @Autowired
    public UiPropertyConfigurationService(final PropertyConfigurationService propertyConfigurationService, final LibraryRepository libraryRepository) {
        this.propertyConfigurationService = propertyConfigurationService;
        this.libraryRepository = libraryRepository;
    }

    @Transactional
    public PropertyConfigurationDTO save(final PropertyConfigurationDTO value) {
        final PropertyConfiguration conf = MAPPER.dtoToConf(value);
        final PropertyConfiguration savedConf = propertyConfigurationService.save(conf);
        return MAPPER.confToDto(savedConf);
    }

    @Transactional
    public void delete(final String identifier) {
        propertyConfigurationService.delete(identifier);
    }

    @Transactional(readOnly = true)
    public List<PropertyConfigurationDTO> findByLibrary(final Library library) {
        final List<PropertyConfiguration> confs = propertyConfigurationService.findByLibrary(library);
        return confs.stream().map(MAPPER::confToDto).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<PropertyConfigurationDTO> findByLibraryAndNotShowOnCreation(final String libraryId) {
        Library library = libraryRepository.findOneWithDependencies(libraryId);
        return findByLibrary(library).stream().filter(prop -> !prop.isShowOnCreation()).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public PropertyConfigurationDTO findByDescPropertyAndLibrary(final DescriptionProperty property, final Library library) {
        final PropertyConfiguration conf = propertyConfigurationService.findByDescPropertyAndLibrary(property, library);
        return MAPPER.confToDto(conf);
    }

    @Transactional(readOnly = true)
    public PropertyConfigurationDTO findByInternalPropertyAndLibrary(final PropertyConfiguration.InternalProperty property, final Library library) {
        final PropertyConfiguration conf = propertyConfigurationService.findByInternalPropertyAndLibrary(property, library);
        return MAPPER.confToDto(conf);
    }
}
