package fr.progilone.pgcn.service.document.conditionreport;

import fr.progilone.pgcn.domain.document.DocUnit;
import fr.progilone.pgcn.domain.document.conditionreport.DescriptionProperty;
import fr.progilone.pgcn.domain.document.conditionreport.PropertyConfiguration;
import fr.progilone.pgcn.domain.library.Library;
import fr.progilone.pgcn.repository.document.conditionreport.DescriptionPropertyRepository;
import fr.progilone.pgcn.repository.document.conditionreport.PropertyConfigurationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PropertyConfigurationService {

    private final DescriptionPropertyRepository descriptionPropertyRepository;
    private final PropertyConfigurationRepository propertyConfigurationRepository;

    @Autowired
    public PropertyConfigurationService(final DescriptionPropertyRepository descriptionPropertyRepository,
                                        final PropertyConfigurationRepository propertyConfigurationRepository) {
        this.descriptionPropertyRepository = descriptionPropertyRepository;
        this.propertyConfigurationRepository = propertyConfigurationRepository;
    }

    @Transactional(readOnly = true)
    public List<PropertyConfiguration> findByLibrary(final Library library) {
        return propertyConfigurationRepository.findByLibrary(library);
    }

    @Transactional(readOnly = true)
    public PropertyConfiguration findByDescPropertyAndLibrary(final DescriptionProperty property, final Library library) {
        final List<PropertyConfiguration> confs = propertyConfigurationRepository.findByDescPropertyAndLibrary(property, library);

        final PropertyConfiguration configuration;
        if (confs.isEmpty()) {
            final DescriptionProperty dbProperty = descriptionPropertyRepository.findOne(property.getIdentifier());
            configuration = new PropertyConfiguration();
            configuration.setDescProperty(property);
            configuration.setLibrary(library);
            configuration.setAllowComment(dbProperty.isAllowComment());
            configuration.setRequired(false);
            configuration.setTypes(Arrays.stream(DocUnit.CondReportType.values()).collect(Collectors.toSet()));
        } else {
            configuration = confs.get(0);
        }
        return configuration;
    }

    @Transactional(readOnly = true)
    public PropertyConfiguration findByInternalPropertyAndLibrary(final PropertyConfiguration.InternalProperty property, final Library library) {
        final List<PropertyConfiguration> confs = propertyConfigurationRepository.findByInternalPropertyAndLibrary(property, library);

        final PropertyConfiguration configuration;
        if (confs.isEmpty()) {
            configuration = new PropertyConfiguration();
            configuration.setInternalProperty(property);
            configuration.setLibrary(library);
            configuration.setRequired(false);
            configuration.setTypes(Arrays.stream(DocUnit.CondReportType.values()).collect(Collectors.toSet()));
        } else {
            configuration = confs.get(0);
        }
        return configuration;
    }

    @Transactional
    public void delete(final String identifier) {
        propertyConfigurationRepository.delete(identifier);
    }

    @Transactional
    public PropertyConfiguration save(final PropertyConfiguration conf) {
        final PropertyConfiguration savedConf = propertyConfigurationRepository.save(conf);
        return propertyConfigurationRepository.findWithDependencies(savedConf.getIdentifier());
    }
}
