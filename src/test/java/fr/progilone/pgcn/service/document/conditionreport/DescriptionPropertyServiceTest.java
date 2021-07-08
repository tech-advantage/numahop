package fr.progilone.pgcn.service.document.conditionreport;

import fr.progilone.pgcn.domain.document.conditionreport.DescriptionProperty;
import fr.progilone.pgcn.exception.PgcnValidationException;
import fr.progilone.pgcn.exception.message.PgcnErrorCode;
import fr.progilone.pgcn.repository.document.conditionreport.DescriptionPropertyRepository;
import fr.progilone.pgcn.repository.document.conditionreport.DescriptionRepository;
import fr.progilone.pgcn.repository.document.conditionreport.DescriptionValueRepository;
import fr.progilone.pgcn.util.TestUtil;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.internal.stubbing.answers.ReturnsArgumentAt;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class DescriptionPropertyServiceTest {

    @Mock
    private DescriptionRepository descriptionRepository;
    @Mock
    private DescriptionPropertyRepository descPropertyRepository;
    @Mock
    private DescriptionValueRepository descValueRepository;

    private DescriptionPropertyService service;

    @Before
    public void setUp() {
        service = new DescriptionPropertyService(descriptionRepository, descPropertyRepository, descValueRepository);
    }

    @Test
    public void testFindByProperty() {
        final List<DescriptionProperty> values = new ArrayList<>();
        when(descPropertyRepository.findAll()).thenReturn(values);

        final List<DescriptionProperty> actual = service.findAll();
        assertSame(values, actual);
    }

    @Test
    public void testDelete() {
        final String id = "ID-001";
        service.delete(id);
        verify(descPropertyRepository).delete(id);
    }

    @Test
    public void testSave() {
        when(descPropertyRepository.save(any(DescriptionProperty.class))).then(new ReturnsArgumentAt(0));

        // #1
        try {
            final DescriptionProperty value = new DescriptionProperty();
            service.save(value);
            fail("testSave failed");
        } catch (PgcnValidationException e) {
            TestUtil.checkPgcnException(e, PgcnErrorCode.DESC_PROPERTY_LABEL_MANDATORY, PgcnErrorCode.DESC_PROPERTY_TYPE_MANDATORY);
        }

        // #2
        try {
            final DescriptionProperty value = new DescriptionProperty();
            value.setLabel("Label");
            value.setType(DescriptionProperty.Type.DESCRIPTION);

            final DescriptionProperty actual = service.save(value);
            assertSame(value, actual);

        } catch (PgcnValidationException e) {
            fail("testSave failed");
        }
    }
}
