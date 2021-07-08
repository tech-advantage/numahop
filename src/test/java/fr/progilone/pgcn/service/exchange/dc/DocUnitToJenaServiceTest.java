package fr.progilone.pgcn.service.exchange.dc;

import fr.progilone.pgcn.domain.document.BibliographicRecord;
import fr.progilone.pgcn.domain.document.DocProperty;
import fr.progilone.pgcn.domain.document.DocPropertyType;
import fr.progilone.pgcn.domain.document.DocUnit;
import org.apache.jena.rdf.model.Property;
import org.junit.Before;
import org.junit.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;

import static org.junit.Assert.assertEquals;

/**
 * Created by Sébastien on 23/12/2016.
 */
public class DocUnitToJenaServiceTest {

    private DocUnitToJenaService service;

    @Before
    public void setUp() {
        service = new DocUnitToJenaService();
        ReflectionTestUtils.setField(service, "defaultUri", "http://test/");
    }

    @Test
    public void testConvert() throws IOException {
        final DocUnit docUnit = getDocUnit();

        String actual = service.convert(docUnit, DocPropertyType.DocPropertySuperType.DC);
        //        System.out.println(actual);

        actual = actual.replaceAll("\\s+", " ").replaceAll("\\r|\\n", "").trim();
        String expected = "<rdf:RDF\n"
                          + "    xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\"\n"
                          + "    xmlns:dc=\"http://purl.org/dc/elements/1.1/\">\n"
                          + "  <rdf:Description rdf:about=\"http://test/REC-002\">\n"
                          + "    <dc:title>American Psycho</dc:title>\n"
                          + "    <dc:creator>Bret Easton Ellis</dc:creator>\n"
                          + "  </rdf:Description>\n"
                          + "  <rdf:Description rdf:about=\"http://test/REC-001\">\n"
                          + "    <dc:title>American Psycho</dc:title>\n"
                          + "    <dc:creator>Bret Easton Ellis</dc:creator>\n"
                          + "  </rdf:Description>\n"
                          + "</rdf:RDF>";
        expected = expected.replaceAll("\\s+", " ").replaceAll("\\r|\\n", "").trim();

        assertEquals(expected, actual);
    }

    private DocUnit getDocUnit() {
        final DocPropertyType dcCreator = new DocPropertyType();
        dcCreator.setIdentifier("creator");

        final DocProperty creator = new DocProperty();
        creator.setType(dcCreator);
        creator.setValue("Bret Easton Ellis");

        final DocPropertyType dcTitle = new DocPropertyType();
        dcTitle.setIdentifier("title");
        final DocProperty title = new DocProperty();
        title.setType(dcTitle);
        title.setValue("American Psycho");

        final BibliographicRecord record1 = new BibliographicRecord();
        record1.setIdentifier("REC-001");
        record1.addProperty(creator);
        record1.addProperty(title);

        final BibliographicRecord record2 = new BibliographicRecord();
        record2.setIdentifier("REC-002");
        record2.addProperty(creator);
        record2.addProperty(title);

        final DocUnit docUnit = new DocUnit();
        docUnit.setIdentifier("DOC-UNIT-001");
        docUnit.addRecord(record1);
        docUnit.addRecord(record2);
        return docUnit;
    }
}
