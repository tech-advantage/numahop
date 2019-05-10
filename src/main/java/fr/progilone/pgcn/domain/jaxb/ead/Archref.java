//
// Ce fichier a été généré par l'implémentation de référence JavaTM Architecture for XML Binding (JAXB), v2.2.8-b130911.1802
// Voir <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a>
// Toute modification apportée à ce fichier sera perdue lors de la recompilation du schéma source.
// Généré le : 2017.05.16 à 10:56:55 AM CEST
//


package fr.progilone.pgcn.domain.jaxb.ead;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
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
 * <p>Classe Java pour archref complex type.
 *
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 *
 * <pre>
 * &lt;complexType name="archref">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;choice maxOccurs="unbounded" minOccurs="0">
 *         &lt;group ref="{urn:isbn:1-931666-22-9}m.phrase.basic.norefs"/>
 *         &lt;element name="bibref" type="{urn:isbn:1-931666-22-9}bibref"/>
 *         &lt;element name="ref" type="{urn:isbn:1-931666-22-9}ref"/>
 *         &lt;element name="title" type="{urn:isbn:1-931666-22-9}title"/>
 *         &lt;element name="extref" type="{urn:isbn:1-931666-22-9}extref"/>
 *         &lt;group ref="{urn:isbn:1-931666-22-9}m.did"/>
 *       &lt;/choice>
 *       &lt;attGroup ref="{urn:isbn:1-931666-22-9}a.common"/>
 *       &lt;attGroup ref="{http://www.w3.org/1999/xlink}simpleLink"/>
 *       &lt;attribute name="entityref" type="{http://www.w3.org/2001/XMLSchema}ENTITY" />
 *       &lt;attribute name="xpointer" type="{http://www.w3.org/2001/XMLSchema}anySimpleType" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "archref", propOrder = {
    "content"
})
public class Archref {

    @XmlElementRefs({
        @XmlElementRef(name = "ptr", namespace = "urn:isbn:1-931666-22-9", type = JAXBElement.class, required = false),
        @XmlElementRef(name = "physdesc", namespace = "urn:isbn:1-931666-22-9", type = JAXBElement.class, required = false),
        @XmlElementRef(name = "langmaterial", namespace = "urn:isbn:1-931666-22-9", type = JAXBElement.class, required = false),
        @XmlElementRef(name = "title", namespace = "urn:isbn:1-931666-22-9", type = JAXBElement.class, required = false),
        @XmlElementRef(name = "container", namespace = "urn:isbn:1-931666-22-9", type = JAXBElement.class, required = false),
        @XmlElementRef(name = "extptr", namespace = "urn:isbn:1-931666-22-9", type = JAXBElement.class, required = false),
        @XmlElementRef(name = "daogrp", namespace = "urn:isbn:1-931666-22-9", type = JAXBElement.class, required = false),
        @XmlElementRef(name = "abstract", namespace = "urn:isbn:1-931666-22-9", type = JAXBElement.class, required = false),
        @XmlElementRef(name = "materialspec", namespace = "urn:isbn:1-931666-22-9", type = JAXBElement.class, required = false),
        @XmlElementRef(name = "abbr", namespace = "urn:isbn:1-931666-22-9", type = JAXBElement.class, required = false),
        @XmlElementRef(name = "expan", namespace = "urn:isbn:1-931666-22-9", type = JAXBElement.class, required = false),
        @XmlElementRef(name = "bibref", namespace = "urn:isbn:1-931666-22-9", type = JAXBElement.class, required = false),
        @XmlElementRef(name = "unitdate", namespace = "urn:isbn:1-931666-22-9", type = JAXBElement.class, required = false),
        @XmlElementRef(name = "physloc", namespace = "urn:isbn:1-931666-22-9", type = JAXBElement.class, required = false),
        @XmlElementRef(name = "unitid", namespace = "urn:isbn:1-931666-22-9", type = JAXBElement.class, required = false),
        @XmlElementRef(name = "lb", namespace = "urn:isbn:1-931666-22-9", type = JAXBElement.class, required = false),
        @XmlElementRef(name = "unittitle", namespace = "urn:isbn:1-931666-22-9", type = JAXBElement.class, required = false),
        @XmlElementRef(name = "origination", namespace = "urn:isbn:1-931666-22-9", type = JAXBElement.class, required = false),
        @XmlElementRef(name = "emph", namespace = "urn:isbn:1-931666-22-9", type = JAXBElement.class, required = false),
        @XmlElementRef(name = "extref", namespace = "urn:isbn:1-931666-22-9", type = JAXBElement.class, required = false),
        @XmlElementRef(name = "note", namespace = "urn:isbn:1-931666-22-9", type = JAXBElement.class, required = false),
        @XmlElementRef(name = "ref", namespace = "urn:isbn:1-931666-22-9", type = JAXBElement.class, required = false),
        @XmlElementRef(name = "dao", namespace = "urn:isbn:1-931666-22-9", type = JAXBElement.class, required = false),
        @XmlElementRef(name = "repository", namespace = "urn:isbn:1-931666-22-9", type = JAXBElement.class, required = false)
    })
    @XmlMixed
    protected List<Serializable> content;
    @XmlAttribute(name = "entityref")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlSchemaType(name = "ENTITY")
    protected String entityref;
    @XmlAttribute(name = "xpointer")
    @XmlSchemaType(name = "anySimpleType")
    protected String xpointer;
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
    @XmlAttribute(name = "type", namespace = "http://www.w3.org/1999/xlink")
    protected String type;
    @XmlAttribute(name = "href", namespace = "http://www.w3.org/1999/xlink")
    @XmlSchemaType(name = "anyURI")
    protected String href;
    @XmlAttribute(name = "role", namespace = "http://www.w3.org/1999/xlink")
    protected String role;
    @XmlAttribute(name = "arcrole", namespace = "http://www.w3.org/1999/xlink")
    protected String arcrole;
    @XmlAttribute(name = "title", namespace = "http://www.w3.org/1999/xlink")
    protected String title;
    @XmlAttribute(name = "show", namespace = "http://www.w3.org/1999/xlink")
    protected String show;
    @XmlAttribute(name = "actuate", namespace = "http://www.w3.org/1999/xlink")
    protected String actuate;

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
     * {@link JAXBElement }{@code <}{@link Physdesc }{@code >}
     * {@link JAXBElement }{@code <}{@link Langmaterial }{@code >}
     * {@link JAXBElement }{@code <}{@link Title }{@code >}
     * {@link JAXBElement }{@code <}{@link Container }{@code >}
     * {@link JAXBElement }{@code <}{@link Extptr }{@code >}
     * {@link JAXBElement }{@code <}{@link Daogrp }{@code >}
     * {@link JAXBElement }{@code <}{@link Abbr }{@code >}
     * {@link JAXBElement }{@code <}{@link Unitdate }{@code >}
     * {@link JAXBElement }{@code <}{@link Physloc }{@code >}
     * {@link JAXBElement }{@code <}{@link Unittitle }{@code >}
     * {@link String }
     * {@link JAXBElement }{@code <}{@link Extref }{@code >}
     * {@link JAXBElement }{@code <}{@link Note }{@code >}
     * {@link JAXBElement }{@code <}{@link Dao }{@code >}
     * {@link JAXBElement }{@code <}{@link Repository }{@code >}
     * {@link JAXBElement }{@code <}{@link Ptr }{@code >}
     * {@link JAXBElement }{@code <}{@link Abstract }{@code >}
     * {@link JAXBElement }{@code <}{@link Materialspec }{@code >}
     * {@link JAXBElement }{@code <}{@link Expan }{@code >}
     * {@link JAXBElement }{@code <}{@link Bibref }{@code >}
     * {@link JAXBElement }{@code <}{@link Unitid }{@code >}
     * {@link JAXBElement }{@code <}{@link Lb }{@code >}
     * {@link JAXBElement }{@code <}{@link Origination }{@code >}
     * {@link JAXBElement }{@code <}{@link Emph }{@code >}
     * {@link JAXBElement }{@code <}{@link Ref }{@code >}
     *
     *
     */
    public List<Serializable> getContent() {
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
            return "simple";
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
     * Obtient la valeur de la propriété arcrole.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getArcrole() {
        return arcrole;
    }

    /**
     * Définit la valeur de la propriété arcrole.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setArcrole(String value) {
        this.arcrole = value;
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
     * Obtient la valeur de la propriété show.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getShow() {
        return show;
    }

    /**
     * Définit la valeur de la propriété show.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setShow(String value) {
        this.show = value;
    }

    /**
     * Obtient la valeur de la propriété actuate.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getActuate() {
        return actuate;
    }

    /**
     * Définit la valeur de la propriété actuate.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setActuate(String value) {
        this.actuate = value;
    }

}
