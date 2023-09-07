package fr.progilone.pgcn.service.exchange.ead;

import fr.progilone.pgcn.domain.jaxb.ead.C;
import fr.progilone.pgcn.domain.jaxb.ead.Eadheader;
import jakarta.xml.bind.JAXBException;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import javax.xml.parsers.ParserConfigurationException;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.xml.sax.SAXException;

/**
 * Created by Sébastien on 01/06/2017.
 */
public class EadTestUtils {

    public static Pair<Eadheader, C> parseXml(final String xml) throws IOException, JAXBException, ParserConfigurationException, SAXException {
        final File tmpFile = new File(FileUtils.getTempDirectory(),
                                      "EadTestUtils_" + System.currentTimeMillis()
                                                                    + ".xml");
        try (final FileWriter writer = new FileWriter(tmpFile)) {
            IOUtils.write(xml.getBytes(), writer, StandardCharsets.UTF_8);
        }
        try {
            final List<Eadheader> headers = new ArrayList<>();
            final List<C> cs = new ArrayList<>();
            new EadCEntityHandler((h, c) -> {
                headers.add(h);
                cs.add(c);
            }).parse(tmpFile);

            final Eadheader header = !headers.isEmpty() ? headers.get(0)
                                                        : null;
            final C c = !cs.isEmpty() ? cs.get(0)
                                      : null;
            return Pair.of(header, c);

        } finally {
            FileUtils.deleteQuietly(tmpFile);
        }
    }

    public static C getCFromXml(final String xml) throws IOException, JAXBException, ParserConfigurationException, SAXException {
        return parseXml(xml).getRight();
    }

    public static Eadheader getEadheaderFromXml(final String xml) throws IOException, JAXBException, ParserConfigurationException, SAXException {
        return parseXml(xml).getLeft();
    }
}
