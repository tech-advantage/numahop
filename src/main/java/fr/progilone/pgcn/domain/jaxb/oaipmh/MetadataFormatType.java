//
// Ce fichier a été généré par l'implémentation de référence JavaTM Architecture for XML Binding (JAXB), v2.2.8-b130911.1802
// Voir <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a>
// Toute modification apportée à ce fichier sera perdue lors de la recompilation du schéma source.
// Généré le : 2018.08.13 à 01:08:19 PM CEST
//

package fr.progilone.pgcn.domain.jaxb.oaipmh;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlSchemaType;
import jakarta.xml.bind.annotation.XmlType;

/**
 * <p>
 * Classe Java pour metadataFormatType complex type.
 *
 * <p>
 * Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 *
 * <pre>
 * &lt;complexType name="metadataFormatType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="metadataPrefix" type="{http://www.openarchives.org/OAI/2.0/}metadataPrefixType"/>
 *         &lt;element name="schema" type="{http://www.w3.org/2001/XMLSchema}anyURI"/>
 *         &lt;element name="metadataNamespace" type="{http://www.w3.org/2001/XMLSchema}anyURI"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "metadataFormatType",
         propOrder = {"metadataPrefix",
                      "schema",
                      "metadataNamespace"})
public class MetadataFormatType {

    @XmlElement(required = true)
    protected String metadataPrefix;
    @XmlElement(required = true)
    @XmlSchemaType(name = "anyURI")
    protected String schema;
    @XmlElement(required = true)
    @XmlSchemaType(name = "anyURI")
    protected String metadataNamespace;

    /**
     * Obtient la valeur de la propriété metadataPrefix.
     *
     * @return
     *         possible object is
     *         {@link String }
     *
     */
    public String getMetadataPrefix() {
        return metadataPrefix;
    }

    /**
     * Définit la valeur de la propriété metadataPrefix.
     *
     * @param value
     *            allowed object is
     *            {@link String }
     *
     */
    public void setMetadataPrefix(String value) {
        this.metadataPrefix = value;
    }

    /**
     * Obtient la valeur de la propriété schema.
     *
     * @return
     *         possible object is
     *         {@link String }
     *
     */
    public String getSchema() {
        return schema;
    }

    /**
     * Définit la valeur de la propriété schema.
     *
     * @param value
     *            allowed object is
     *            {@link String }
     *
     */
    public void setSchema(String value) {
        this.schema = value;
    }

    /**
     * Obtient la valeur de la propriété metadataNamespace.
     *
     * @return
     *         possible object is
     *         {@link String }
     *
     */
    public String getMetadataNamespace() {
        return metadataNamespace;
    }

    /**
     * Définit la valeur de la propriété metadataNamespace.
     *
     * @param value
     *            allowed object is
     *            {@link String }
     *
     */
    public void setMetadataNamespace(String value) {
        this.metadataNamespace = value;
    }

}
