package fr.progilone.pgcn.service.exchange.ead;

import fr.progilone.pgcn.domain.jaxb.ead.C;
import fr.progilone.pgcn.domain.jaxb.ead.Eadheader;
import fr.progilone.pgcn.domain.jaxb.ead.ObjectFactory;
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

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.UnmarshallerHandler;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;
import java.io.File;
import java.io.IOException;
import java.util.Enumeration;
import java.util.function.BiConsumer;

/**
 * Handler traitant les éléments "c" de niveau supérieur d'un fichier EAD
 */
public class EadCEntityHandler {

    private static final Logger LOG = LoggerFactory.getLogger(EadCEntityHandler.class);

    private static final String NS_EAD = "urn:isbn:1-931666-22-9";
    private static final String TAG_C = "c";
    private static final String TAG_EADHEADER = "eadheader";

    private final BiConsumer<Eadheader, C> process;
    private Eadheader eadheader = null; // entête du fichier EAD parsé

    public EadCEntityHandler(final BiConsumer<Eadheader, C> process) {
        this.process = process;
    }

    /**
     * Parse le fichier xml d'entrée à la recherche d'éléments "c".
     * Chaque élément "c" de niveau supérieur donne lieu à un appel de process
     *
     * @param file
     * @throws JAXBException
     * @throws ParserConfigurationException
     * @throws SAXException
     * @throws IOException
     */
    public void parse(final File file) throws JAXBException, ParserConfigurationException, SAXException, IOException {
        final JAXBContext context = JAXBContext.newInstance(ObjectFactory.class); // EAD

        // XML parser
        final SAXParserFactory factory = SAXParserFactory.newInstance();
        factory.setNamespaceAware(true);
        final XMLReader reader = factory.newSAXParser().getXMLReader();

        // XML filter
        final XMLFilter filter = new DefaultNamespaceFilter(NS_EAD);
        filter.setParent(reader);
        filter.setContentHandler(new CSplitter(context));

        // Sécurisation du fichier d'entrée contre attaques XXE
        //        filter.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
        //        filter.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
        filter.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
        filter.setFeature("http://xml.org/sax/features/validation", false);

        // Lecture du fichier
        filter.parse(file.toURI().toURL().toExternalForm());
    }

    /**
     * This object implements XMLFilter and monitors the incoming SAX
     * events. Once it hits a "c" element, it creates a new
     * unmarshaller and unmarshals one {@link C}.
     * <p>
     * Once finished unmarshalling it, we will process it, then move
     * on to the next {@link C}.
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

            if (namespaceURI.equals(NS_EAD) && (TAG_C.equals(localName) || TAG_EADHEADER.equals(localName))) {
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

                final String defaultURI = namespaces.getURI(NS_EAD);
                if (defaultURI != null) {
                    unmarshallerHandler.startPrefixMapping(NS_EAD, defaultURI);
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

                    final String defaultURI = namespaces.getURI(NS_EAD);
                    if (defaultURI != null) {
                        unmarshallerHandler.endPrefixMapping(NS_EAD);
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

                        if (namespaceURI.equals(NS_EAD) && TAG_EADHEADER.equals(localName)) {
                            eadheader = (Eadheader) result;
                        } else {
                            process.accept(eadheader, (C) result);
                        }

                    } catch (JAXBException je) {
                        // error was found during the unmarshalling.
                        // you can either abort the processing by throwing a SAXException,
                        // or you can continue processing by returning from this method.
                        LOG.error("Erreur lors du traitement de l'élémment \"c\" à la ligne {}", locator.getLineNumber());
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
     * XMLFilter qui ajoute un namespace par défaut si celui-ci n'est pas renseigné
     */
    private static final class DefaultNamespaceFilter extends XMLFilterImpl {

        private final String defaultNamespace;

        private DefaultNamespaceFilter(final String defaultNamespace) {
            this.defaultNamespace = defaultNamespace;
        }

        @Override
        public void endElement(String uri, String localName, String qName) throws SAXException {
            if (StringUtils.isEmpty(uri)) {
                super.endElement(defaultNamespace, localName, qName);
            } else {
                super.endElement(uri, localName, qName);
            }
        }

        @Override
        public void startElement(String uri, String localName, String qName, Attributes atts) throws SAXException {
            if (StringUtils.isEmpty(uri)) {
                super.startElement(defaultNamespace, localName, qName, atts);
            } else {
                super.startElement(uri, localName, qName, atts);
            }
        }
    }
}
