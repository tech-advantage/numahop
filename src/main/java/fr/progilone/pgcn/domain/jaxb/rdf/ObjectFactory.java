//
// Ce fichier a été généré par l'implémentation de référence JavaTM Architecture for XML Binding (JAXB), v2.2.8-b130911.1802
// Voir <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a>
// Toute modification apportée à ce fichier sera perdue lors de la recompilation du schéma source.
// Généré le : 2017.01.02 à 04:49:01 PM CET
//

package fr.progilone.pgcn.domain.jaxb.rdf;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;

/**
 * This object contains factory methods for each
 * Java content interface and Java element interface
 * generated in the fr.progilone.pgcn.domain.jaxb.rdf package.
 * <p>An ObjectFactory allows you to programatically
 * construct new instances of the Java representation
 * for XML content. The Java representation of XML
 * content can consist of schema derived interfaces
 * and classes representing the binding of schema
 * type definitions, element declarations and model
 * groups.  Factory methods for each of these are
 * provided in this class.
 */
@XmlRegistry
public class ObjectFactory {

    private final static QName _Description_QNAME = new QName("http://www.w3.org/1999/02/22-rdf-syntax-ns#", "Description");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: fr.progilone.pgcn.domain.jaxb.rdf
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link RDF }
     */
    public RDF createRDF() {
        return new RDF();
    }

    /**
     * Create an instance of {@link DescriptionType }
     */
    public DescriptionType createDescriptionType() {
        return new DescriptionType();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link DescriptionType }{@code >}}
     */
    @XmlElementDecl(namespace = "http://www.w3.org/1999/02/22-rdf-syntax-ns#", name = "Description")
    public JAXBElement<DescriptionType> createRdfDescription(DescriptionType value) {
        return new JAXBElement<>(_Description_QNAME, DescriptionType.class, null, value);
    }
}
