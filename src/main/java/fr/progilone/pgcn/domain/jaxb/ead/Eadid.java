//
// Ce fichier a été généré par l'implémentation de référence JavaTM Architecture for XML Binding (JAXB), v2.2.8-b130911.1802 
// Voir <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Toute modification apportée à ce fichier sera perdue lors de la recompilation du schéma source. 
// Généré le : 2017.05.16 à 10:56:55 AM CEST 
//


package fr.progilone.pgcn.domain.jaxb.ead;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlValue;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;


/**
 * <p>Classe Java pour eadid complex type.
 * 
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 * 
 * <pre>
 * &lt;complexType name="eadid">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;attGroup ref="{urn:isbn:1-931666-22-9}am.countrycode"/>
 *       &lt;attribute name="publicid" type="{http://www.w3.org/2001/XMLSchema}anySimpleType" />
 *       &lt;attribute name="urn" type="{http://www.w3.org/2001/XMLSchema}anySimpleType" />
 *       &lt;attribute name="url" type="{http://www.w3.org/2001/XMLSchema}anySimpleType" />
 *       &lt;attribute name="mainagencycode" type="{urn:isbn:1-931666-22-9}data.repositorycode" />
 *       &lt;attribute name="identifier" type="{http://www.w3.org/2001/XMLSchema}anySimpleType" />
 *       &lt;attribute name="encodinganalog" type="{http://www.w3.org/2001/XMLSchema}anySimpleType" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "eadid", propOrder = {
    "content"
})
public class Eadid {

    @XmlValue
    protected String content;
    @XmlAttribute(name = "publicid")
    @XmlSchemaType(name = "anySimpleType")
    protected String publicid;
    @XmlAttribute(name = "urn")
    @XmlSchemaType(name = "anySimpleType")
    protected String urn;
    @XmlAttribute(name = "url")
    @XmlSchemaType(name = "anySimpleType")
    protected String url;
    @XmlAttribute(name = "mainagencycode")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    protected String mainagencycode;
    @XmlAttribute(name = "identifier")
    @XmlSchemaType(name = "anySimpleType")
    protected String identifier;
    @XmlAttribute(name = "encodinganalog")
    @XmlSchemaType(name = "anySimpleType")
    protected String encodinganalog;
    @XmlAttribute(name = "countrycode")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    protected String countrycode;

    /**
     * Obtient la valeur de la propriété content.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getContent() {
        return content;
    }

    /**
     * Définit la valeur de la propriété content.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setContent(String value) {
        this.content = value;
    }

    /**
     * Obtient la valeur de la propriété publicid.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPublicid() {
        return publicid;
    }

    /**
     * Définit la valeur de la propriété publicid.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPublicid(String value) {
        this.publicid = value;
    }

    /**
     * Obtient la valeur de la propriété urn.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getUrn() {
        return urn;
    }

    /**
     * Définit la valeur de la propriété urn.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setUrn(String value) {
        this.urn = value;
    }

    /**
     * Obtient la valeur de la propriété url.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getUrl() {
        return url;
    }

    /**
     * Définit la valeur de la propriété url.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setUrl(String value) {
        this.url = value;
    }

    /**
     * Obtient la valeur de la propriété mainagencycode.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMainagencycode() {
        return mainagencycode;
    }

    /**
     * Définit la valeur de la propriété mainagencycode.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMainagencycode(String value) {
        this.mainagencycode = value;
    }

    /**
     * Obtient la valeur de la propriété identifier.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getIdentifier() {
        return identifier;
    }

    /**
     * Définit la valeur de la propriété identifier.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setIdentifier(String value) {
        this.identifier = value;
    }

    /**
     * Obtient la valeur de la propriété encodinganalog.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getEncodinganalog() {
        return encodinganalog;
    }

    /**
     * Définit la valeur de la propriété encodinganalog.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setEncodinganalog(String value) {
        this.encodinganalog = value;
    }

    /**
     * Obtient la valeur de la propriété countrycode.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCountrycode() {
        return countrycode;
    }

    /**
     * Définit la valeur de la propriété countrycode.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCountrycode(String value) {
        this.countrycode = value;
    }

}
