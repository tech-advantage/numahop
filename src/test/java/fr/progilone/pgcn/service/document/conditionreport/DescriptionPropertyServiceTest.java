package fr.progilone.pgcn.service.document.conditionreport;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import fr.progilone.pgcn.domain.document.conditionreport.DescriptionProperty;
import fr.progilone.pgcn.exception.PgcnValidationException;
import fr.progilone.pgcn.exception.message.PgcnErrorCode;
import fr.progilone.pgcn.repository.document.conditionreport.DescriptionPropertyRepository;
import fr.progilone.pgcn.repository.document.conditionreport.DescriptionRepository;
import fr.progilone.pgcn.repository.document.conditionreport.DescriptionValueRepository;
import fr.progilone.pgcn.util.TestUtil;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.internal.stubbing.answers.ReturnsArgumentAt;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class DescriptionPropertyServiceTest {

    @Mock
    private DescriptionRepository descriptionRepository;
    @Mock
    private DescriptionPropertyRepository descPropertyRepository;
    @Mock
    private DescriptionValueRepository descValueRepository;

    private DescriptionPropertyService service;

    @BeforeEach
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
    public void testSave() {
        when(descPropertyRepository.save(any(DescriptionProperty.class))).then(new ReturnsArgumentAt(0));

        // #1
        try {
            final DescriptionProperty value = new DescriptionProperty();
            service.save(value);
            fail("testSave failed");
        } catch (final PgcnValidationException e) {
            TestUtil.checkPgcnException(e, PgcnErrorCode.DESC_PROPERTY_LABEL_MANDATORY, PgcnErrorCode.DESC_PROPERTY_TYPE_MANDATORY);
        }

        // #2
        try {
            final DescriptionProperty value = new DescriptionProperty();
            value.setLabel("Label");
            value.setType(DescriptionProperty.Type.DESCRIPTION);

            final DescriptionProperty actual = service.save(value);
            assertSame(value, actual);

        } catch (final PgcnValidationException e) {
            fail("testSave failed");
        }
    }
}
