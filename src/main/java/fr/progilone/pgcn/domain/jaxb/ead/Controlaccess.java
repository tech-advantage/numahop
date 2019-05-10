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
 * <p>Classe Java pour controlaccess complex type.
 *
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 *
 * <pre>
 * &lt;complexType name="controlaccess">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="head" type="{urn:isbn:1-931666-22-9}head" minOccurs="0"/>
 *         &lt;choice maxOccurs="unbounded">
 *           &lt;group ref="{urn:isbn:1-931666-22-9}m.blocks"/>
 *           &lt;group ref="{urn:isbn:1-931666-22-9}m.access.title"/>
 *           &lt;element name="controlaccess" type="{urn:isbn:1-931666-22-9}controlaccess"/>
 *         &lt;/choice>
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
@XmlType(name = "controlaccess", propOrder = {
    "head",
    "addressOrChronlistOrList"
})
public class Controlaccess {

    protected Head head;
    @XmlElements({
        @XmlElement(name = "address", type = Address.class),
        @XmlElement(name = "chronlist", type = Chronlist.class),
        @XmlElement(name = "list", type = List.class),
        @XmlElement(name = "note", type = Note.class),
        @XmlElement(name = "table", type = Table.class),
        @XmlElement(name = "blockquote", type = Blockquote.class),
        @XmlElement(name = "p", type = P.class),
        @XmlElement(name = "corpname", type = Corpname.class),
        @XmlElement(name = "famname", type = Famname.class),
        @XmlElement(name = "geogname", type = Geogname.class),
        @XmlElement(name = "name", type = Name.class),
        @XmlElement(name = "occupation", type = Occupation.class),
        @XmlElement(name = "persname", type = Persname.class),
        @XmlElement(name = "subject", type = Subject.class),
        @XmlElement(name = "genreform", type = Genreform.class),
        @XmlElement(name = "function", type = Function.class),
        @XmlElement(name = "title", type = Title.class),
        @XmlElement(name = "controlaccess", type = Controlaccess.class)
    })
    protected java.util.List<Object> addressOrChronlistOrList;
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
     * Obtient la valeur de la propriété head.
     *
     * @return
     *     possible object is
     *     {@link Head }
     *
     */
    public Head getHead() {
        return head;
    }

    /**
     * Définit la valeur de la propriété head.
     *
     * @param value
     *     allowed object is
     *     {@link Head }
     *
     */
    public void setHead(Head value) {
        this.head = value;
    }

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
     * {@link Corpname }
     * {@link Famname }
     * {@link Geogname }
     * {@link Name }
     * {@link Occupation }
     * {@link Persname }
     * {@link Subject }
     * {@link Genreform }
     * {@link Function }
     * {@link Title }
     * {@link Controlaccess }
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
