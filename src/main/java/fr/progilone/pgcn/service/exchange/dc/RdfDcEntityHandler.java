package fr.progilone.pgcn.service.exchange.dc;

import fr.progilone.pgcn.domain.jaxb.rdf.ObjectFactory;
import fr.progilone.pgcn.domain.jaxb.rdf.RDF;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;
import java.io.File;
import java.io.IOException;

/**
 * Created by Sébastien on 02/01/2017.
 */
public class RdfDcEntityHandler {

    private final RDF.Listener descriptionListener;

    /**
     * @param descriptionListener listener qui sera appelé à chaque élément rdf:description rencontré
     */
    public RdfDcEntityHandler(final RDF.Listener descriptionListener) {
        this.descriptionListener = descriptionListener;
    }

    /**
     * Parse le fichier xml d'entrée, qui est de la forme:
     * <code>
     * &lt;rdf:RDF&gt;<br/>
     * &lt;rdf:Description&gt;<br/>
     * &lt;dc:title&gt;&lt;/dc:title&gt;<br/>
     * ...<br/>
     * &lt;/rdf:Description&gt;<br/>
     * &lt;/rdf:RDF&gt;<br/>
     * </code>
     *
     * @param file
     * @throws JAXBException
     * @throws ParserConfigurationException
     * @throws SAXException
     * @throws IOException
     */
    public void parse(final File file) throws JAXBException, ParserConfigurationException, SAXException, IOException {
        final JAXBContext context = JAXBContext.newInstance(ObjectFactory.class); // RDF

        // Unmarshaller
        final Unmarshaller unmarshaller = context.createUnmarshaller();

        // Listener qui va appeler le handler à chaque élément rdf:description
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

        // XML parser
        final SAXParserFactory factory = SAXParserFactory.newInstance();
        factory.setNamespaceAware(true);
        final XMLReader reader = factory.newSAXParser().getXMLReader();
        reader.setContentHandler(unmarshaller.getUnmarshallerHandler());

        // Sécurisation du fichier d'entrée contre attaques XXE
        reader.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
        reader.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);

        // Lecture du fichier
        reader.parse(file.toURI().toURL().toExternalForm());
    }
}
