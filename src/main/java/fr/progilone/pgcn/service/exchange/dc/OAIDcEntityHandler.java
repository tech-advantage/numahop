package fr.progilone.pgcn.service.exchange.dc;

import fr.progilone.pgcn.domain.jaxb.oaidc.OaiDcType;
import fr.progilone.pgcn.domain.jaxb.oaidc.ObjectFactory;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.Attributes;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.XMLFilter;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.helpers.NamespaceSupport;
import org.xml.sax.helpers.XMLFilterImpl;

import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.UnmarshallerHandler;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;
import java.io.File;
import java.io.IOException;
import java.util.Enumeration;
import java.util.function.Consumer;

/**
 * Handler parcourant un document XML à la recherche des éléments oai_dc
 */
public class OAIDcEntityHandler {

    private static final Logger LOG = LoggerFactory.getLogger(OAIDcEntityHandler.class);

    private static final String NS_OAI_DC = "http://www.openarchives.org/OAI/2.0/oai_dc/";
    private static final String NS_DC = "http://purl.org/dc/elements/1.1/";

    private static final String TAG_DC = "dc";
    private static final String TAG_OAI_DC = "oai_dc";

    private final Consumer<OaiDcType> process;

    public OAIDcEntityHandler(final Consumer<OaiDcType> process) {
        this.process = process;
    }

    /**
     * Parse le fichier xml d'entrée à la recherche d'éléments TAG_DC.
     * Chaque élément TAG_DC de niveau supérieur donne lieu à un appel de process
     *
     * @param file
     * @throws JAXBException
     * @throws ParserConfigurationException
     * @throws SAXException
     * @throws IOException
     */
    public void parse(final File file) throws JAXBException, ParserConfigurationException, SAXException, IOException {
        final JAXBContext context = JAXBContext.newInstance(ObjectFactory.class); // OAI_DC

        // XML parser
        final SAXParserFactory factory = SAXParserFactory.newInstance();
        factory.setNamespaceAware(true);
        final XMLReader reader = factory.newSAXParser().getXMLReader();

        // XML filter
        final XMLFilter filter = new OaiDcNamespaceFilter(NS_OAI_DC, NS_DC);
        filter.setParent(reader);
        filter.setContentHandler(new CSplitter(context));

        // Sécurisation du fichier d'entrée contre attaques XXE
        filter.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
        filter.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
        //        filter.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
        //        filter.setFeature("http://xml.org/sax/features/validation", false);

        // Lecture du fichier
        filter.parse(file.toURI().toURL().toExternalForm());
    }

    /**
     * This object implements XMLFilter and monitors the incoming SAX
     * events. Once it hits a "c" element, it creates a new
     * unmarshaller and unmarshals one {@link OaiDcType}.
     * <p>
     * Once finished unmarshalling it, we will process it, then move
     * on to the next {@link OaiDcType}.
     * <p>
     * cf. \jaxb-ri\samples\partial-unmarshalling\src\Splitter.java
     * Created by Sébastien on 16/05/2017.
     */
    private class CSplitter extends XMLFilterImpl {

        private final JAXBContext context;
        private int depth;
        private UnmarshallerHandler unmarshallerHandler;
        private Locator locator;
        private NamespaceSupport namespaces = new NamespaceSupport();

        public CSplitter(JAXBContext context) {
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

            if (namespaceURI.equals(NS_OAI_DC) && TAG_DC.equals(localName)) {
                // start a new unmarshaller
                final Unmarshaller unmarshaller;
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

                final Enumeration e = namespaces.getPrefixes();
                while (e.hasMoreElements()) {
                    final String prefix = (String) e.nextElement();
                    final String uri = namespaces.getURI(prefix);
                    unmarshallerHandler.startPrefixMapping(prefix, uri);
                }

                final String defaultURI = namespaces.getURI(NS_OAI_DC);
                if (defaultURI != null) {
                    unmarshallerHandler.startPrefixMapping(NS_OAI_DC, defaultURI);
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
                    final Enumeration e = namespaces.getPrefixes();
                    while (e.hasMoreElements()) {
                        String prefix = (String) e.nextElement();
                        unmarshallerHandler.endPrefixMapping(prefix);
                    }

                    final String defaultURI = namespaces.getURI(NS_OAI_DC);
                    if (defaultURI != null) {
                        unmarshallerHandler.endPrefixMapping(NS_OAI_DC);
                    }

                    unmarshallerHandler.endDocument();

                    // stop forwarding events by setting a dummy handler.
                    // XMLFilter doesn't accept null, so we have to give it something,
                    // hence a DefaultHandler, which does nothing.
                    setContentHandler(new DefaultHandler());

                    // then retrieve the fully unmarshalled object
                    try {
                        // process this new result
                        final Object result = unmarshallerHandler.getResult();
                        process.accept((OaiDcType) result);

                    } catch (JAXBException je) {
                        // error was found during the unmarshalling.
                        // you can either abort the processing by throwing a SAXException,
                        // or you can continue processing by returning from this method.
                        LOG.error("Erreur lors du traitement de l'élémment \"oai_dc:dc\" à la ligne {}", locator.getLineNumber());
                        return;
                    }

                    unmarshallerHandler = null;
                }
            }
        }

        @Override
        public void setDocumentLocator(Locator locator) {
            super.setDocumentLocator(locator);
            this.locator = locator;
        }

        @Override
        public void startPrefixMapping(String prefix, String uri) throws SAXException {
            namespaces.pushContext();
            namespaces.declarePrefix(prefix, uri);
            super.startPrefixMapping(prefix, uri);
        }

        @Override
        public void endPrefixMapping(String prefix) throws SAXException {
            namespaces.popContext();
            super.endPrefixMapping(prefix);
        }
    }

    /**
     * XMLFilter qui corrige le format invalide des fichiers de Calames:
     * -> les déclarations des namespaces sont incorrectes (dc) ou absentes (oai_dc)
     * -> oai_dc (namespace) est utilisé au lieu de oai_dc:dc (élément dc du namespace oai_dc)
     */
    private static final class OaiDcNamespaceFilter extends XMLFilterImpl {

        private final String oaiDcNamespace;
        private final String dcNamespace;
        private boolean oaiDc = false;  // la balise oai_dc:dc est ouverte

        private OaiDcNamespaceFilter(final String oaiDcNamespace, final String dcNamespace) {
            this.oaiDcNamespace = oaiDcNamespace;
            this.dcNamespace = dcNamespace;
        }

        @Override
        public void endElement(String uri, String localName, String qName) throws SAXException {
            final String namespace;
            if (oaiDc) {
                // fermeture de l'élément oai_dc, namspace = oai_dc
                if (StringUtils.isEmpty(uri) && StringUtils.equals(localName, TAG_OAI_DC)) {
                    namespace = oaiDcNamespace;
                    oaiDc = false;
                }
                // namespace = dc
                else {
                    namespace = dcNamespace;
                }
            }
            // sinon on garde le namespace
            else {
                namespace = uri;
            }
            super.endElement(namespace, localName, qName);
        }

        @Override
        public void startElement(String uri, String localName, String qName, Attributes atts) throws SAXException {
            final String corrUri, corrLocalName;
            // on est dans oai_dc => corrUri = dc
            if (oaiDc) {
                corrUri = dcNamespace;
                corrLocalName = localName;
            }
            // le corrUri n'est pas renseigné, et l'élément est oai_dc => corrUri = oai_dc
            else if (StringUtils.isEmpty(uri) && StringUtils.equals(localName, TAG_OAI_DC)) {
                corrUri = oaiDcNamespace;
                corrLocalName = TAG_DC;
                oaiDc = true;
            }
            // sinon on garde le corrUri
            else {
                corrUri = uri;
                corrLocalName = localName;
            }
            super.startElement(corrUri, corrLocalName, qName, atts);
        }
    }
}
