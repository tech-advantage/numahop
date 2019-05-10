package fr.progilone.pgcn.service.exchange.ead;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.junit.Test;
import org.xml.sax.SAXException;

import javax.xml.bind.JAXBException;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Created by Sébastien on 16/05/2017.
 */
public class EadCEntityHandlerTest {

    @Test
    public void test() throws IOException, JAXBException, ParserConfigurationException, SAXException {
        final String xml = "<toto>"
                           + "<c id=\"toplevel-001\">"
                           + "<c id=\"sublevel-001-a\"></c>"
                           + "<c id=\"sublevel-001-b\"></c>"
                           + "</c>"
                           + "<c id=\"toplevel-002\">"
                           + "<c id=\"sublevel-002-a\"></c>"
                           + "</c>"
                           + "</toto>";
        final File tmpFile = new File(FileUtils.getTempDirectory(), "EadCEntityHandlerTest_test_" + System.currentTimeMillis() + ".xml");
        try (final FileWriter writer = new FileWriter(tmpFile)) {
            IOUtils.write(xml, writer);
        }
        try {
            final List<String> identifiers = new ArrayList<>();

            new EadCEntityHandler((h, c) -> {
                identifiers.add(c.getId());
            }).parse(tmpFile);

            assertEquals(2, identifiers.size());
            assertEquals("toplevel-001", identifiers.get(0));
            assertEquals("toplevel-002", identifiers.get(1));
        } finally {
            FileUtils.deleteQuietly(tmpFile);
        }
    }
}
