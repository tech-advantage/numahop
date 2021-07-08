package fr.progilone.pgcn.service.document.conditionreport;

import fr.progilone.pgcn.domain.document.conditionreport.DescriptionProperty;
import fr.progilone.pgcn.domain.document.conditionreport.DescriptionValue;
import fr.progilone.pgcn.exception.PgcnValidationException;
import fr.progilone.pgcn.exception.message.PgcnErrorCode;
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

import static fr.progilone.pgcn.exception.message.PgcnErrorCode.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class DescriptionValueServiceTest {

    @Mock
    private DescriptionRepository descriptionRepository;
    @Mock
    private DescriptionValueRepository descriptionValueRepository;

    private DescriptionValueService service;

    @Before
    public void setUp() {
        service = new DescriptionValueService(descriptionRepository, descriptionValueRepository);
    }

    @Test
    public void testFindByProperty() {
        final DescriptionProperty property = new DescriptionProperty();
        final List<DescriptionValue> values = new ArrayList<>();
        when(descriptionValueRepository.findByProperty(property)).thenReturn(values);

        final List<DescriptionValue> actual = service.findByProperty(property);
        assertSame(values, actual);
    }

    @Test
    public void testFindAll() {
        final List<DescriptionValue> values = new ArrayList<>();
        when(descriptionValueRepository.findAll()).thenReturn(values);

        final List<DescriptionValue> actual = service.findAll();
        assertSame(values, actual);
    }

    @Test
    public void testDelete() {
        final String id = "ID-001";
        final DescriptionValue value = new DescriptionValue();
        when(descriptionValueRepository.findOne(id)).thenReturn(value);

        service.delete(id);

        verify(descriptionValueRepository).delete(value);
    }

    @Test
    public void testSave() {
        when(descriptionValueRepository.save(any(DescriptionValue.class))).then(new ReturnsArgumentAt(0));

        // #1
        try {
            final DescriptionValue value = new DescriptionValue();
            service.save(value);
            fail("testSave failed");
        } catch (PgcnValidationException e) {
            TestUtil.checkPgcnException(e, PgcnErrorCode.DESC_VALUE_LABEL_MANDATORY, DESC_VALUE_PROPERTY_MANDATORY);
        }

        // #2
        try {
            final DescriptionValue value = new DescriptionValue();
            value.setLabel("Label");
            value.setProperty(new DescriptionProperty());

            final DescriptionValue actual = service.save(value);
            assertSame(value, actual);

        } catch (PgcnValidationException e) {
            fail("testSave failed");
        }
    }
}
