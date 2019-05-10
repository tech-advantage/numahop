//
// Ce fichier a été généré par l'implémentation de référence JavaTM Architecture for XML Binding (JAXB), v2.2.8-b130911.1802
// Voir <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a>
// Toute modification apportée à ce fichier sera perdue lors de la recompilation du schéma source.
// Généré le : 2017.05.16 à 10:56:55 AM CEST
//


package fr.progilone.pgcn.domain.jaxb.ead;

import java.util.ArrayList;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlID;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;


/**
 * <p>Classe Java pour titlepage complex type.
 *
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 *
 * <pre>
 * &lt;complexType name="titlepage">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;choice maxOccurs="unbounded">
 *         &lt;group ref="{urn:isbn:1-931666-22-9}m.blocks"/>
 *         &lt;element name="author" type="{urn:isbn:1-931666-22-9}author"/>
 *         &lt;element name="date" type="{urn:isbn:1-931666-22-9}date"/>
 *         &lt;element name="edition" type="{urn:isbn:1-931666-22-9}edition"/>
 *         &lt;element name="num" type="{urn:isbn:1-931666-22-9}num"/>
 *         &lt;element name="publisher" type="{urn:isbn:1-931666-22-9}publisher"/>
 *         &lt;element name="bibseries" type="{urn:isbn:1-931666-22-9}bibseries"/>
 *         &lt;element name="sponsor" type="{urn:isbn:1-931666-22-9}sponsor"/>
 *         &lt;element name="titleproper" type="{urn:isbn:1-931666-22-9}titleproper"/>
 *         &lt;element name="subtitle" type="{urn:isbn:1-931666-22-9}subtitle"/>
 *       &lt;/choice>
 *       &lt;attGroup ref="{urn:isbn:1-931666-22-9}a.common"/>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "titlepage", propOrder = {
    "addressOrChronlistOrList"
})
public class Titlepage {

    @XmlElements({
        @XmlElement(name = "address", type = Address.class),
        @XmlElement(name = "chronlist", type = Chronlist.class),
        @XmlElement(name = "list", type = List.class),
        @XmlElement(name = "note", type = Note.class),
        @XmlElement(name = "table", type = Table.class),
        @XmlElement(name = "blockquote", type = Blockquote.class),
        @XmlElement(name = "p", type = P.class),
        @XmlElement(name = "author", type = Author.class),
        @XmlElement(name = "date", type = Date.class),
        @XmlElement(name = "edition", type = Edition.class),
        @XmlElement(name = "num", type = Num.class),
        @XmlElement(name = "publisher", type = Publisher.class),
        @XmlElement(name = "bibseries", type = Bibseries.class),
        @XmlElement(name = "sponsor", type = Sponsor.class),
        @XmlElement(name = "titleproper", type = Titleproper.class),
        @XmlElement(name = "subtitle", type = Subtitle.class)
    })
    protected java.util.List<Object> addressOrChronlistOrList;
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
     * Gets the value of the addressOrChronlistOrList property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the addressOrChronlistOrList property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getAddressOrChronlistOrList().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Address }
     * {@link Chronlist }
     * {@link List }
     * {@link Note }
     * {@link Table }
     * {@link Blockquote }
     * {@link P }
     * {@link Author }
     * {@link Date }
     * {@link Edition }
     * {@link Num }
     * {@link Publisher }
     * {@link Bibseries }
     * {@link Sponsor }
     * {@link Titleproper }
     * {@link Subtitle }
     *
     *
     */
    public java.util.List<Object> getAddressOrChronlistOrList() {
        if (addressOrChronlistOrList == null) {
            addressOrChronlistOrList = new ArrayList<>();
        }
        return this.addressOrChronlistOrList;
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
