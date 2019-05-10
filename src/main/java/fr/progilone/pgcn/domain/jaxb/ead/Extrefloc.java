//
// Ce fichier a été généré par l'implémentation de référence JavaTM Architecture for XML Binding (JAXB), v2.2.8-b130911.1802
// Voir <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a>
// Toute modification apportée à ce fichier sera perdue lors de la recompilation du schéma source.
// Généré le : 2017.05.16 à 10:56:55 AM CEST
//


package fr.progilone.pgcn.domain.jaxb.ead;

import java.io.Serializable;
import java.util.ArrayList;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlElementRefs;
import javax.xml.bind.annotation.XmlID;
import javax.xml.bind.annotation.XmlMixed;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;


/**
 * <p>Classe Java pour extrefloc complex type.
 *
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 *
 * <pre>
 * &lt;complexType name="extrefloc">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;group ref="{urn:isbn:1-931666-22-9}m.para.content.norefs" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;attGroup ref="{urn:isbn:1-931666-22-9}a.loc.external.ptr"/>
 *       &lt;attGroup ref="{urn:isbn:1-931666-22-9}a.common"/>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "extrefloc", propOrder = {
    "content"
})
public class Extrefloc {

    @XmlElementRefs({
        @XmlElementRef(name = "origination", namespace = "urn:isbn:1-931666-22-9", type = JAXBElement.class, required = false),
        @XmlElementRef(name = "repository", namespace = "urn:isbn:1-931666-22-9", type = JAXBElement.class, required = false),
        @XmlElementRef(name = "expan", namespace = "urn:isbn:1-931666-22-9", type = JAXBElement.class, required = false),
        @XmlElementRef(name = "subject", namespace = "urn:isbn:1-931666-22-9", type = JAXBElement.class, required = false),
        @XmlElementRef(name = "ptr", namespace = "urn:isbn:1-931666-22-9", type = JAXBElement.class, required = false),
        @XmlElementRef(name = "function", namespace = "urn:isbn:1-931666-22-9", type = JAXBElement.class, required = false),
        @XmlElementRef(name = "corpname", namespace = "urn:isbn:1-931666-22-9", type = JAXBElement.class, required = false),
        @XmlElementRef(name = "occupation", namespace = "urn:isbn:1-931666-22-9", type = JAXBElement.class, required = false),
        @XmlElementRef(name = "unitdate", namespace = "urn:isbn:1-931666-22-9", type = JAXBElement.class, required = false),
        @XmlElementRef(name = "extptr", namespace = "urn:isbn:1-931666-22-9", type = JAXBElement.class, required = false),
        @XmlElementRef(name = "table", namespace = "urn:isbn:1-931666-22-9", type = JAXBElement.class, required = false),
        @XmlElementRef(name = "abbr", namespace = "urn:isbn:1-931666-22-9", type = JAXBElement.class, required = false),
        @XmlElementRef(name = "address", namespace = "urn:isbn:1-931666-22-9", type = JAXBElement.class, required = false),
        @XmlElementRef(name = "num", namespace = "urn:isbn:1-931666-22-9", type = JAXBElement.class, required = false),
        @XmlElementRef(name = "blockquote", namespace = "urn:isbn:1-931666-22-9", type = JAXBElement.class, required = false),
        @XmlElementRef(name = "unittitle", namespace = "urn:isbn:1-931666-22-9", type = JAXBElement.class, required = false),
        @XmlElementRef(name = "note", namespace = "urn:isbn:1-931666-22-9", type = JAXBElement.class, required = false),
        @XmlElementRef(name = "lb", namespace = "urn:isbn:1-931666-22-9", type = JAXBElement.class, required = false),
        @XmlElementRef(name = "name", namespace = "urn:isbn:1-931666-22-9", type = JAXBElement.class, required = false),
        @XmlElementRef(name = "persname", namespace = "urn:isbn:1-931666-22-9", type = JAXBElement.class, required = false),
        @XmlElementRef(name = "genreform", namespace = "urn:isbn:1-931666-22-9", type = JAXBElement.class, required = false),
        @XmlElementRef(name = "emph", namespace = "urn:isbn:1-931666-22-9", type = JAXBElement.class, required = false),
        @XmlElementRef(name = "date", namespace = "urn:isbn:1-931666-22-9", type = JAXBElement.class, required = false),
        @XmlElementRef(name = "chronlist", namespace = "urn:isbn:1-931666-22-9", type = JAXBElement.class, required = false),
        @XmlElementRef(name = "list", namespace = "urn:isbn:1-931666-22-9", type = JAXBElement.class, required = false),
        @XmlElementRef(name = "famname", namespace = "urn:isbn:1-931666-22-9", type = JAXBElement.class, required = false),
        @XmlElementRef(name = "geogname", namespace = "urn:isbn:1-931666-22-9", type = JAXBElement.class, required = false)
    })
    @XmlMixed
    protected java.util.List<Serializable> content;
    @XmlAttribute(name = "entityref")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlSchemaType(name = "ENTITY")
    protected String entityref;
    @XmlAttribute(name = "xpointer")
    @XmlSchemaType(name = "anySimpleType")
    protected String xpointer;
    @XmlAttribute(name = "type", namespace = "http://www.w3.org/1999/xlink")
    protected String type;
    @XmlAttribute(name = "href", namespace = "http://www.w3.org/1999/xlink", required = true)
    @XmlSchemaType(name = "anyURI")
    protected String href;
    @XmlAttribute(name = "role", namespace = "http://www.w3.org/1999/xlink")
    protected String role;
    @XmlAttribute(name = "title", namespace = "http://www.w3.org/1999/xlink")
    protected String title;
    @XmlAttribute(name = "label", namespace = "http://www.w3.org/1999/xlink")
    protected String label;
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
     * Gets the value of the content property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the content property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getContent().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link JAXBElement }{@code <}{@link Origination }{@code >}
     * {@link JAXBElement }{@code <}{@link Repository }{@code >}
     * {@link JAXBElement }{@code <}{@link Expan }{@code >}
     * {@link JAXBElement }{@code <}{@link Subject }{@code >}
     * {@link JAXBElement }{@code <}{@link Ptr }{@code >}
     * {@link JAXBElement }{@code <}{@link Function }{@code >}
     * {@link JAXBElement }{@code <}{@link Corpname }{@code >}
     * {@link JAXBElement }{@code <}{@link Occupation }{@code >}
     * {@link JAXBElement }{@code <}{@link Unitdate }{@code >}
     * {@link JAXBElement }{@code <}{@link Extptr }{@code >}
     * {@link JAXBElement }{@code <}{@link Table }{@code >}
     * {@link String }
     * {@link JAXBElement }{@code <}{@link Abbr }{@code >}
     * {@link JAXBElement }{@code <}{@link Address }{@code >}
     * {@link JAXBElement }{@code <}{@link Num }{@code >}
     * {@link JAXBElement }{@code <}{@link Blockquote }{@code >}
     * {@link JAXBElement }{@code <}{@link Unittitle }{@code >}
     * {@link JAXBElement }{@code <}{@link Note }{@code >}
     * {@link JAXBElement }{@code <}{@link Lb }{@code >}
     * {@link JAXBElement }{@code <}{@link Name }{@code >}
     * {@link JAXBElement }{@code <}{@link Persname }{@code >}
     * {@link JAXBElement }{@code <}{@link Genreform }{@code >}
     * {@link JAXBElement }{@code <}{@link Emph }{@code >}
     * {@link JAXBElement }{@code <}{@link Date }{@code >}
     * {@link JAXBElement }{@code <}{@link Chronlist }{@code >}
     * {@link JAXBElement }{@code <}{@link List }{@code >}
     * {@link JAXBElement }{@code <}{@link Famname }{@code >}
     * {@link JAXBElement }{@code <}{@link Geogname }{@code >}
     *
     *
     */
    public java.util.List<Serializable> getContent() {
        if (content == null) {
            content = new ArrayList<>();
        }
        return this.content;
    }

    /**
     * Obtient la valeur de la propriété entityref.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getEntityref() {
        return entityref;
    }

    /**
     * Définit la valeur de la propriété entityref.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setEntityref(String value) {
        this.entityref = value;
    }

    /**
     * Obtient la valeur de la propriété xpointer.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getXpointer() {
        return xpointer;
    }

    /**
     * Définit la valeur de la propriété xpointer.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setXpointer(String value) {
        this.xpointer = value;
    }

    /**
     * Obtient la valeur de la propriété type.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getType() {
        if (type == null) {
            return "locator";
        } else {
            return type;
        }
    }

    /**
     * Définit la valeur de la propriété type.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setType(String value) {
        this.type = value;
    }

    /**
     * Obtient la valeur de la propriété href.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getHref() {
        return href;
    }

    /**
     * Définit la valeur de la propriété href.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setHref(String value) {
        this.href = value;
    }

    /**
     * Obtient la valeur de la propriété role.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getRole() {
        return role;
    }

    /**
     * Définit la valeur de la propriété role.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setRole(String value) {
        this.role = value;
    }

    /**
     * Obtient la valeur de la propriété title.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getTitle() {
        return title;
    }

    /**
     * Définit la valeur de la propriété title.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setTitle(String value) {
        this.title = value;
    }

    /**
     * Obtient la valeur de la propriété label.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getLabel() {
        return label;
    }

    /**
     * Définit la valeur de la propriété label.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setLabel(String value) {
        this.label = value;
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
