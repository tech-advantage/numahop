//
// Ce fichier a été généré par l'implémentation de référence JavaTM Architecture for XML Binding (JAXB), v2.2.8-b130911.1802
// Voir <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a>
// Toute modification apportée à ce fichier sera perdue lors de la recompilation du schéma source.
// Généré le : 2018.08.13 à 01:08:19 PM CEST
//


package fr.progilone.pgcn.domain.jaxb.oaipmh;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * A record has a header, a metadata part, and
 *         an optional about container
 *
 * <p>Classe Java pour recordType complex type.
 *
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 *
 * <pre>
 * &lt;complexType name="recordType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="header" type="{http://www.openarchives.org/OAI/2.0/}headerType"/>
 *         &lt;element name="metadata" type="{http://www.openarchives.org/OAI/2.0/}metadataType" minOccurs="0"/>
 *         &lt;element name="about" type="{http://www.openarchives.org/OAI/2.0/}aboutType" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "recordType", propOrder = {
    "header",
    "metadata",
    "about"
})
public class RecordType {

    @XmlElement(required = true)
    protected HeaderType header;
    protected MetadataType metadata;
    protected List<AboutType> about;

    /**
     * Obtient la valeur de la propriété header.
     *
     * @return
     *     possible object is
     *     {@link HeaderType }
     *
     */
    public HeaderType getHeader() {
        return header;
    }

    /**
     * Définit la valeur de la propriété header.
     *
     * @param value
     *     allowed object is
     *     {@link HeaderType }
     *
     */
    public void setHeader(HeaderType value) {
        this.header = value;
    }

    /**
     * Obtient la valeur de la propriété metadata.
     *
     * @return
     *     possible object is
     *     {@link MetadataType }
     *
     */
    public MetadataType getMetadata() {
        return metadata;
    }

    /**
     * Définit la valeur de la propriété metadata.
     *
     * @param value
     *     allowed object is
     *     {@link MetadataType }
     *
     */
    public void setMetadata(MetadataType value) {
        this.metadata = value;
    }

    /**
     * Gets the value of the about property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the about property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getAbout().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link AboutType }
     *
     *
     */
    public List<AboutType> getAbout() {
        if (about == null) {
            about = new ArrayList<>();
        }
        return this.about;
    }

}
