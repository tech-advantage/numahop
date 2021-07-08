package fr.progilone.pgcn.service.document.conditionreport;

import fr.progilone.pgcn.domain.document.conditionreport.DescriptionProperty;
import fr.progilone.pgcn.domain.document.conditionreport.PropertyConfiguration;
import fr.progilone.pgcn.domain.library.Library;
import fr.progilone.pgcn.repository.document.conditionreport.DescriptionPropertyRepository;
import fr.progilone.pgcn.repository.document.conditionreport.PropertyConfigurationRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static fr.progilone.pgcn.domain.document.conditionreport.PropertyConfiguration.InternalProperty.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class PropertyConfigurationServiceTest {

    @Mock
    private DescriptionPropertyRepository descriptionPropertyRepository;
    @Mock
    private PropertyConfigurationRepository propertyConfigurationRepository;

    private PropertyConfigurationService service;

    @Before
    public void setUp() {
        service = new PropertyConfigurationService(descriptionPropertyRepository, propertyConfigurationRepository);
    }

    @Test
    public void testFindByLibrary() {
        final Library library = new Library();
        final List<PropertyConfiguration> confs = new ArrayList<>();
        when(propertyConfigurationRepository.findByLibrary(library)).thenReturn(confs);

        final List<PropertyConfiguration> actual = service.findByLibrary(library);
        assertSame(confs, actual);
    }

    @Test
    public void testFindByDescPropertyAndLibrary() {
        final DescriptionProperty property = new DescriptionProperty();
        property.setIdentifier("b1133757-ca6c-42da-acf8-1614004141e7");
        property.setAllowComment(true);
        final Library library = new Library();

        final PropertyConfiguration conf = new PropertyConfiguration();
        final List<PropertyConfiguration> confs = Collections.singletonList(conf);

        when(propertyConfigurationRepository.findByDescPropertyAndLibrary(property, library)).thenReturn(Collections.emptyList()).thenReturn(confs);
        when(descriptionPropertyRepository.findOne(property.getIdentifier())).thenReturn(property);

        // conf pas trouvée
        PropertyConfiguration actual = service.findByDescPropertyAndLibrary(property, library);
        assertNotSame(conf, actual);
        assertEquals(actual.getDescProperty(), property);
        assertEquals(actual.getLibrary(), library);
        assertTrue(actual.isAllowComment());
        assertFalse(actual.isRequired());

        // conf trouvée
        actual = service.findByDescPropertyAndLibrary(property, library);
        assertSame(conf, actual);
    }

    @Test
    public void testFindByInternalPropertyAndLibrary() {
        final Library library = new Library();
        final PropertyConfiguration.InternalProperty property = BINDING_DESC;

        final PropertyConfiguration conf = new PropertyConfiguration();
        final List<PropertyConfiguration> confs = Collections.singletonList(conf);

        when(propertyConfigurationRepository.findByInternalPropertyAndLibrary(property, library)).thenReturn(Collections.emptyList())
                                                                                                 .thenReturn(confs);

        // conf pas trouvée
        PropertyConfiguration actual = service.findByInternalPropertyAndLibrary(property, library);
        assertNotSame(conf, actual);
        assertEquals(actual.getInternalProperty(), property);
        assertEquals(actual.getLibrary(), library);
        assertFalse(actual.isRequired());

        // conf trouvée
        actual = service.findByInternalPropertyAndLibrary(property, library);
        assertSame(conf, actual);
    }

    @Test
    public void testDelete() {
        final String identifier = "1283b8f2-03f1-4201-ac2d-45d96c05be93";
        service.delete(identifier);
        verify(propertyConfigurationRepository).delete(identifier);
    }

    @Test
    public void save() {
        final PropertyConfiguration conf = new PropertyConfiguration();
        conf.setIdentifier("9ba79bdf-e955-4bc4-b00b-05d591a7950d");

        when(propertyConfigurationRepository.save(conf)).thenReturn(conf);
        when(propertyConfigurationRepository.findWithDependencies(conf.getIdentifier())).thenReturn(conf);

        final PropertyConfiguration actual = service.save(conf);

        assertSame(conf, actual);
    }
}
