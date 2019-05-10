//
// Ce fichier a été généré par l'implémentation de référence JavaTM Architecture for XML Binding (JAXB), v2.2.8-b130911.1802
// Voir <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a>
// Toute modification apportée à ce fichier sera perdue lors de la recompilation du schéma source.
// Généré le : 2017.05.16 à 10:56:55 AM CEST
//


package fr.progilone.pgcn.domain.jaxb.ead;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlID;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;


/**
 * <p>Classe Java pour titlestmt complex type.
 *
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 *
 * <pre>
 * &lt;complexType name="titlestmt">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="titleproper" type="{urn:isbn:1-931666-22-9}titleproper" maxOccurs="unbounded"/>
 *         &lt;element name="subtitle" type="{urn:isbn:1-931666-22-9}subtitle" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="author" type="{urn:isbn:1-931666-22-9}author" minOccurs="0"/>
 *         &lt;element name="sponsor" type="{urn:isbn:1-931666-22-9}sponsor" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attGroup ref="{urn:isbn:1-931666-22-9}a.common"/>
 *       &lt;attribute name="encodinganalog" type="{http://www.w3.org/2001/XMLSchema}anySimpleType" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "titlestmt", propOrder = {
    "titleproper",
    "subtitle",
    "author",
    "sponsor"
})
public class Titlestmt {

    @XmlElement(required = true)
    protected List<Titleproper> titleproper;
    protected List<Subtitle> subtitle;
    protected Author author;
    protected Sponsor sponsor;
    @XmlAttribute(name = "encodinganalog")
    @XmlSchemaType(name = "anySimpleType")
    protected String encodinganalog;
    @XmlAttribute(name = "id")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlID
    @XmlSchemaType(name = "ID")
    protected String id;
    @XmlAttribute(name = "altrender")
    @XmlSchemaType(name = "anySimpleType")
    protected String altrender;
    @XmlAttribute(name = "audience")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    protected String audience;

    /**
     * Gets the value of the titleproper property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the titleproper property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getTitleproper().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Titleproper }
     *
     *
     */
    public List<Titleproper> getTitleproper() {
        if (titleproper == null) {
            titleproper = new ArrayList<>();
        }
        return this.titleproper;
    }

    /**
     * Gets the value of the subtitle property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the subtitle property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getSubtitle().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Subtitle }
     *
     *
     */
    public List<Subtitle> getSubtitle() {
        if (subtitle == null) {
            subtitle = new ArrayList<>();
        }
        return this.subtitle;
    }

    /**
     * Obtient la valeur de la propriété author.
     *
     * @return
     *     possible object is
     *     {@link Author }
     *
     */
    public Author getAuthor() {
        return author;
    }

    /**
     * Définit la valeur de la propriété author.
     *
     * @param value
     *     allowed object is
     *     {@link Author }
     *
     */
    public void setAuthor(Author value) {
        this.author = value;
    }

    /**
     * Obtient la valeur de la propriété sponsor.
     *
     * @return
     *     possible object is
     *     {@link Sponsor }
     *
     */
    public Sponsor getSponsor() {
        return sponsor;
    }

    /**
     * Définit la valeur de la propriété sponsor.
     *
     * @param value
     *     allowed object is
     *     {@link Sponsor }
     *
     */
    public void setSponsor(Sponsor value) {
        this.sponsor = value;
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
     * Obtient la valeur de la propriété id.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getId() {
        return id;
    }

    /**
     * Définit la valeur de la propriété id.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setId(String value) {
        this.id = value;
    }

    /**
     * Obtient la valeur de la propriété altrender.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getAltrender() {
        return altrender;
    }

    /**
     * Définit la valeur de la propriété altrender.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setAltrender(String value) {
        this.altrender = value;
    }

    /**
     * Obtient la valeur de la propriété audience.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getAudience() {
        return audience;
    }

    /**
     * Définit la valeur de la propriété audience.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setAudience(String value) {
        this.audience = value;
    }

}
