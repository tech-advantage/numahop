package fr.progilone.pgcn.service.document.conditionreport;

import fr.progilone.pgcn.domain.document.DocUnit;
import fr.progilone.pgcn.domain.document.conditionreport.DescriptionProperty;
import fr.progilone.pgcn.domain.document.conditionreport.DescriptionValue;
import fr.progilone.pgcn.service.document.DocUnitService;
import fr.progilone.pgcn.service.exchange.template.MessageService;
import org.apache.commons.lang3.StringUtils;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.internal.stubbing.answers.ReturnsArgumentAt;
import org.mockito.runners.MockitoJUnitRunner;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.mockito.Mockito.*;

@Ignore
@RunWith(MockitoJUnitRunner.class)
public class ConditionReportExportServiceTest {

    @Mock
    private DescriptionPropertyService descBindingPropertyService;
    @Mock
    private DescriptionValueService descriptionValueService;
    @Mock
    private DocUnitService docUnitService;
    @Mock
    private MessageService messageService;
    @Mock
    private PropertyConfigurationService propertyConfigurationService;

    private ConditionReportExportService service;

    @Before
    public void setUp() {
        service = new ConditionReportExportService(descBindingPropertyService,
                                                   descriptionValueService,
                                                   docUnitService,
                                                   messageService,
                                                   propertyConfigurationService);
        when(messageService.getMessage(anyString(), anyString())).then(new ReturnsArgumentAt(1));
    }

    @Test
    public void testWriteReportTemplate() throws IOException {
        final List<String> identifiers = Arrays.asList("1495f953-bdac-4742-8a2b-18944c677caf", "bc3f9cab-8cdb-40b4-b343-002619d0732e");
        final Set<DocUnit> docUnits = identifiers.stream().map(this::getDocUnit).collect(Collectors.toSet());
        final DescriptionProperty property = new DescriptionProperty();
        property.setIdentifier("1fb290f8-4808-4b2e-87bc-ff9d183a6c2e");
        property.setLabel("Traces de réparation");
        property.setAllowComment(true);

        final DescriptionValue bValue1 = new DescriptionValue();
        bValue1.setLabel("Cornichon");
        bValue1.setProperty(property);
        final DescriptionValue bValue2 = new DescriptionValue();
        bValue2.setLabel("Patate");
        bValue2.setProperty(property);

        when(docUnitService.findAllById(identifiers)).thenReturn(docUnits);
        when(descBindingPropertyService.findAll()).thenReturn(Collections.singletonList(property));
        when(descriptionValueService.findAll()).thenReturn(Arrays.asList(bValue1, bValue2));

        try (final OutputStream out = new FileOutputStream("C:\\Users\\Sébastien\\Desktop\\test.xlsx")) {
            service.writeReportTemplate(out, identifiers, ConditionReportExportService.WorkbookFormat.XLSX);
        }
    }

    private DocUnit getDocUnit(final String identifier) {
        final DocUnit docUnit = new DocUnit();
        docUnit.setIdentifier(identifier);
        docUnit.setPgcnId("PGCN-" + StringUtils.substringBefore(identifier, "-"));
        docUnit.setLabel("Le crime de l'Orient express");
        return docUnit;
    }
}
