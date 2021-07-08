package fr.progilone.pgcn.service.exchange.dc;

import com.sun.xml.bind.marshaller.NamespacePrefixMapper;
import fr.progilone.pgcn.domain.jaxb.dc.SimpleLiteral;
import fr.progilone.pgcn.domain.jaxb.rdf.DescriptionType;
import fr.progilone.pgcn.domain.jaxb.rdf.ObjectFactory;
import fr.progilone.pgcn.domain.jaxb.rdf.RDF;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.junit.Ignore;
import org.junit.Test;
import org.xml.sax.Attributes;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.helpers.NamespaceSupport;
import org.xml.sax.helpers.XMLFilterImpl;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.UnmarshallerHandler;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

/**
 * Created by Sébastien on 02/01/2017.
 */
public class RdfDcEntityHandlerTest {

    @Test
    public void test() throws IOException, JAXBException, ParserConfigurationException, SAXException {
        final String xml = "<rdf:RDF\n"
                           + "    xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\"\n"
                           + "    xmlns:dc=\"http://purl.org/dc/elements/1.1/\">\n"
                           + "  <rdf:Description rdf:about=\"http://www-bsg.univ-paris1.fr/84b60cc1-274e-4334-ad22-6b5e0bb74fdf\">\n"
                           + "    <dc:title>Le petit prince</dc:title>\n"
                           + "  </rdf:Description>\n"
                           + "</rdf:RDF>";
        final File tmpFile = new File(FileUtils.getTempDirectory(), "DcEntityHandlerTest_test_" + System.currentTimeMillis() + ".xml");
        try (final FileWriter writer = new FileWriter(tmpFile)) {
            IOUtils.write(xml, writer);
        }
        try {
            final List<String> identifiers = new ArrayList<>();
            final List<String> titles = new ArrayList<>();

            new RdfDcEntityHandler((rdf, descriptionType) -> {
                identifiers.add(descriptionType.getAbout());

                final List<JAXBElement<SimpleLiteral>> literals = descriptionType.getAny();
                if (!literals.isEmpty()) {
                    final List<String> content = literals.get(0).getValue().getContent();

                    if (!content.isEmpty()) {
                        titles.add(content.get(0));
                    }
                }
            }).parse(tmpFile);

            assertEquals(1, identifiers.size());
            assertEquals("http://www-bsg.univ-paris1.fr/84b60cc1-274e-4334-ad22-6b5e0bb74fdf", identifiers.get(0));

            assertEquals(1, titles.size());
            assertEquals("Le petit prince", titles.get(0));
        } finally {
            FileUtils.deleteQuietly(tmpFile);
        }
    }

    // test de marshalling
    @Ignore
    @Test
    public void test0() throws JAXBException {
        final ObjectFactory rdfFactory = new ObjectFactory();
        final fr.progilone.pgcn.domain.jaxb.dc.ObjectFactory dcFactory = new fr.progilone.pgcn.domain.jaxb.dc.ObjectFactory();
        final RDF rdf = rdfFactory.createRDF();
        final DescriptionType desc = rdfFactory.createDescriptionType();

        desc.setAbout("toto");

        final SimpleLiteral simpleLiteral = new SimpleLiteral();
        final List<String> content = simpleLiteral.getContent();
        content.add("toto fait du vélo");
        final JAXBElement<SimpleLiteral> title = dcFactory.createTitle(simpleLiteral);
        desc.getAny().add(title);

        rdf.getDescription().add(desc);

        // Écriture du XML dans le flux de sortie
        JAXBContext context = JAXBContext.newInstance(ObjectFactory.class, fr.progilone.pgcn.domain.jaxb.dc.ObjectFactory.class);
        Marshaller m = context.createMarshaller();
        m.setProperty(Marshaller.JAXB_ENCODING, StandardCharsets.UTF_8.name());
        m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        m.setProperty("com.sun.xml.bind.namespacePrefixMapper", new NamespacePrefixMapper() {

            private final Map<String, String> namespaceMap = new HashMap<>();

            {
                namespaceMap.put("http://purl.org/dc/elements/1.1/", "dc");
                namespaceMap.put("http://www.w3.org/1999/02/22-rdf-syntax-ns#", "rdf");
            }

            @Override
            public String getPreferredPrefix(final String namespaceUri, final String suggestion, final boolean requirePrefix) {
                return namespaceMap.getOrDefault(namespaceUri, suggestion);
            }

            @Override
            public String[] getPreDeclaredNamespaceUris2() {
                // on déclare le namespace dc à la racine, et non sur chaque élément
                return new String[] {"dc", "http://purl.org/dc/elements/1.1/"};
            }
        });
        m.marshal(rdf, System.out);
    }

    // jaxb-ri\samples\partial-unmarshalling
    @Ignore
    @Test
    public void test1() throws JAXBException, ParserConfigurationException, SAXException, IOException {
        JAXBContext context = JAXBContext.newInstance(ObjectFactory.class);

        SAXParserFactory factory = SAXParserFactory.newInstance();
        factory.setNamespaceAware(true);
        XMLReader reader = factory.newSAXParser().getXMLReader();

        Splitter splitter = new Splitter(context);

        reader.setContentHandler(splitter);

        reader.parse(new File("C:\\Users\\Sébastien\\Desktop", "15c7a687-ac9e-4af1-8abb-25d41dd55bb3-dc.xml").toURI().toURL().toExternalForm());
    }

    // jaxb-ri\samples\streaming-unmarshalling
    @Ignore
    @Test
    public void test2() throws JAXBException, ParserConfigurationException, SAXException, IOException {
        JAXBContext context = JAXBContext.newInstance(ObjectFactory.class);
        Unmarshaller unmarshaller = context.createUnmarshaller();

        // purchase order notification callback
        final RDF.Listener descriptionListener = (rdf, descriptionType) -> System.out.println(descriptionType.getAbout());

        // install the callback on all PurchaseOrders instances
        unmarshaller.setListener(new Unmarshaller.Listener() {
            public void beforeUnmarshal(Object target, Object parent) {
                if (target instanceof RDF) {
                    ((RDF) target).setDescriptionListener(descriptionListener);
                }
            }

            public void afterUnmarshal(Object target, Object parent) {
                if (target instanceof RDF) {
                    ((RDF) target).setDescriptionListener(null);
                }
            }
        });

        // create a new XML parser
        SAXParserFactory factory = SAXParserFactory.newInstance();
        factory.setNamespaceAware(true);
        XMLReader reader = factory.newSAXParser().getXMLReader();
        reader.setContentHandler(unmarshaller.getUnmarshallerHandler());

        reader.parse(new File("C:\\Users\\Sébastien\\Desktop", "15c7a687-ac9e-4af1-8abb-25d41dd55bb3-dc.xml").toURI().toURL().toExternalForm());

    }

    private static class Splitter extends XMLFilterImpl {

        private final JAXBContext context;
        private int depth;
        private UnmarshallerHandler unmarshallerHandler;
        private Locator locator;
        private NamespaceSupport namespaces = new NamespaceSupport();

        public Splitter(JAXBContext context) {
            this.context = context;
        }

        @Override
        public void startElement(final String namespaceURI, final String localName, final String qName, final Attributes atts) throws SAXException {
            if (depth != 0) {
                // we are in the middle of forwarding events.
                // continue to do so.
                depth++;
                super.startElement(namespaceURI, localName, qName, atts);
                return;
            }

            if (namespaceURI.equals("http://www.w3.org/1999/02/22-rdf-syntax-ns#") && localName.equals("Description")) {
                // start a new unmarshaller
                Unmarshaller unmarshaller;
                try {
                    unmarshaller = context.createUnmarshaller();
                } catch (JAXBException e) {
                    // there's no way to recover from this error.
                    // we will abort the processing.
                    throw new SAXException(e);
                }
                unmarshallerHandler = unmarshaller.getUnmarshallerHandler();

                // set it as the content handler so that it will receive
                // SAX events from now on.
                setContentHandler(unmarshallerHandler);

                // fire SAX events to emulate the start of a new document.
                unmarshallerHandler.startDocument();
                unmarshallerHandler.setDocumentLocator(locator);

                Enumeration e = namespaces.getPrefixes();
                while (e.hasMoreElements()) {
                    String prefix = (String) e.nextElement();
                    String uri = namespaces.getURI(prefix);

                    unmarshallerHandler.startPrefixMapping(prefix, uri);
                }
                String defaultURI = namespaces.getURI("http://www.w3.org/1999/02/22-rdf-syntax-ns#");
                if (defaultURI != null) {
                    unmarshallerHandler.startPrefixMapping("http://www.w3.org/1999/02/22-rdf-syntax-ns#", defaultURI);
                }

                super.startElement(namespaceURI, localName, qName, atts);

                // count the depth of elements and we will know when to stop.
                depth = 1;
            }
        }

        @Override
        public void endElement(final String namespaceURI, final String localName, final String qName) throws SAXException {
            // forward this event
            super.endElement(namespaceURI, localName, qName);

            if (depth != 0) {
                depth--;
                if (depth == 0) {
                    // just finished sending one chunk.

                    // emulate the end of a document.
                    Enumeration e = namespaces.getPrefixes();
                    while (e.hasMoreElements()) {
                        String prefix = (String) e.nextElement();
                        unmarshallerHandler.endPrefixMapping(prefix);
                    }
                    String defaultURI = namespaces.getURI("http://www.w3.org/1999/02/22-rdf-syntax-ns#");
                    if (defaultURI != null) {
                        unmarshallerHandler.endPrefixMapping("http://www.w3.org/1999/02/22-rdf-syntax-ns#");
                    }
                    unmarshallerHandler.endDocument();

                    // stop forwarding events by setting a dummy handler.
                    // XMLFilter doesn't accept null, so we have to give it something,
                    // hence a DefaultHandler, which does nothing.
                    setContentHandler(new DefaultHandler());

                    // then retrieve the fully unmarshalled object
                    try {
                        //                        RDF result =
                        //                            (RDF) unmarshallerHandler.getResult();
                        JAXBElement<DescriptionType> result = (JAXBElement<DescriptionType>) unmarshallerHandler.getResult();

                        // process this new purchase order
                        process(result.getValue());
                        //                        System.out.println(result);

                    } catch (JAXBException je) {
                        // error was found during the unmarshalling.
                        // you can either abort the processing by throwing a SAXException,
                        // or you can continue processing by returning from this method.
                        System.err.println("unable to process an order at line " + locator.getLineNumber());
                        return;
                    }

                    unmarshallerHandler = null;
                }
            }
        }

        private void process(final DescriptionType description) {
            System.out.println(description.getAbout());
        }

        public void setDocumentLocator(Locator locator) {
            super.setDocumentLocator(locator);
            this.locator = locator;
        }

        public void startPrefixMapping(String prefix, String uri) throws SAXException {
            namespaces.pushContext();
            namespaces.declarePrefix(prefix, uri);
            super.startPrefixMapping(prefix, uri);
        }

        public void endPrefixMapping(String prefix) throws SAXException {
            namespaces.popContext();
            super.endPrefixMapping(prefix);
        }
    }
}
