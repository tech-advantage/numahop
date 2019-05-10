package fr.progilone.pgcn.service.exchange.dc;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import fr.progilone.pgcn.domain.document.BibliographicRecord;
import fr.progilone.pgcn.domain.document.BibliographicRecord.PropertyOrder;
import fr.progilone.pgcn.domain.document.DocProperty;
import fr.progilone.pgcn.domain.document.DocPropertyType;
import fr.progilone.pgcn.domain.document.DocUnit;
import fr.progilone.pgcn.domain.document.PhysicalDocument;
import fr.progilone.pgcn.service.document.DocPropertyTypeService;
import fr.progilone.pgcn.service.document.DocUnitService;
import fr.progilone.pgcn.service.lot.LotService;

/**
 * Created by Sébastien on 27/12/2016.
 */
@RunWith(MockitoJUnitRunner.class)
public class DocUnitToCSVServiceTest {

    private static final char CSV_REPEATED_FIELD_SEPARATOR = ',';
    private static final char CSV_SEPARATOR = ';';

    @Mock
    private DocUnitService docUnitService;
    @Mock
    private DocPropertyTypeService docPropertyTypeService;
    @Mock
    private LotService lotService;

    private DocUnitToCSVService service;

    @Before
    public void setUp() {
        service = new DocUnitToCSVService(docPropertyTypeService, docUnitService, lotService);
    }

    @Test
    public void testConvert() throws IOException {
        final List<String> fields = new ArrayList<>();
        final List<String> docUnitFields = Collections.singletonList("pgcnId");
        final List<String> bibFields = Collections.singletonList("title");
        final List<String> physFields = Collections.singletonList("totalPage");
        final Set<String> docUnitIds = Collections.singleton("895a39b8-60ba-4e06-a5df-c365b41e216b");

        when(docUnitService.findAllByIdWithDependencies(docUnitIds)).thenReturn(Collections.singletonList(getDocUnit()));
        when(docPropertyTypeService.findAllByIdentifierIn(fields)).thenReturn(getPropertyTypes());

        String actual;
        try (final OutputStream out = new ByteArrayOutputStream(); final OutputStream bufOut = new BufferedOutputStream(out)) {
            service.convertFromList(bufOut, docUnitIds, fields, docUnitFields, bibFields, physFields, StandardCharsets.UTF_8.name(), CSV_SEPARATOR);
            actual = out.toString();
        }

        //        System.out.println(actual);
        actual = actual.replaceAll("\\s+", " ").replaceAll("[\\r\\n]", "").trim();

        String expected = "\"dc:title\""
                          + CSV_SEPARATOR
                          + "\"dc:creator\""
                          + CSV_SEPARATOR
                          + "\"pgcnId\""
                          + CSV_SEPARATOR
                          + "\"title\""
                          + CSV_SEPARATOR
                          + "\"totalPage\"\n"
                          + "\"American Psycho\""
                          + CSV_SEPARATOR
                          + "\"Bret Easton Ellis"
                          + CSV_REPEATED_FIELD_SEPARATOR
                          + "B.E.Ellis\""
                          + CSV_SEPARATOR
                          + "\"PGCN-ID-001\""
                          + CSV_SEPARATOR
                          + "\"L'inspecteur Harry\""
                          + CSV_SEPARATOR
                          + "\"14,244\"\n"
                          + "\"American Psycho\""
                          + CSV_SEPARATOR
                          + "\"Bret Easton Ellis\""
                          + CSV_SEPARATOR
                          + "\"PGCN-ID-001\""
                          + CSV_SEPARATOR
                          + "\"\""
                          + CSV_SEPARATOR
                          + "\"14,244\"\n";
        expected = expected.replaceAll("\\s+", " ").replaceAll("[\\r\\n]", "").trim();

        assertEquals(expected, actual);
    }

    private DocUnit getDocUnit() {
        final DocPropertyType dcCreator = new DocPropertyType();
        dcCreator.setIdentifier("creator");

        final DocProperty creator = new DocProperty();
        creator.setType(dcCreator);
        creator.setValue("Bret Easton Ellis");
        creator.setRank(3);

        final DocProperty creator2 = new DocProperty();
        creator2.setType(dcCreator);
        creator2.setValue("B.E.Ellis");
        creator2.setRank(12);

        final DocPropertyType dcTitle = new DocPropertyType();
        dcTitle.setIdentifier("title");
        final DocProperty title = new DocProperty();
        title.setType(dcTitle);
        title.setValue("American Psycho");

        final BibliographicRecord record1 = new BibliographicRecord();
        record1.setPropertyOrder(PropertyOrder.BY_PROPERTY_TYPE);
        record1.setIdentifier("REC-001");
        record1.setTitle("L'inspecteur Harry");
        record1.addProperty(creator);
        record1.addProperty(creator2);
        record1.addProperty(title);

        final BibliographicRecord record2 = new BibliographicRecord();
        record2.setPropertyOrder(PropertyOrder.BY_PROPERTY_TYPE);
        record2.setIdentifier("REC-002");
        record2.addProperty(creator);
        record2.addProperty(title);

        final PhysicalDocument physDoc = new PhysicalDocument();
        physDoc.setTotalPage(14);
        final PhysicalDocument physDoc2 = new PhysicalDocument();
        physDoc2.setTotalPage(244);

        final DocUnit docUnit = new DocUnit();
        docUnit.setIdentifier("DOC-UNIT-001");
        docUnit.setPgcnId("PGCN-ID-001");
        docUnit.addRecord(record1);
        docUnit.addRecord(record2);
        docUnit.addPhysicalDocument(physDoc);
        docUnit.addPhysicalDocument(physDoc2);
        return docUnit;
    }

    private List<DocPropertyType> getPropertyTypes() {
        final List<DocPropertyType> propertyTypes = new ArrayList<>();

        DocPropertyType ppty = new DocPropertyType();
        ppty.setIdentifier("creator");
        ppty.setRank(10);
        ppty.setSuperType(DocPropertyType.DocPropertySuperType.DC);
        propertyTypes.add(ppty);

        ppty = new DocPropertyType();
        ppty.setIdentifier("title");
        ppty.setSuperType(DocPropertyType.DocPropertySuperType.DC);
        ppty.setRank(1);
        propertyTypes.add(ppty);

        return propertyTypes;
    }
}
