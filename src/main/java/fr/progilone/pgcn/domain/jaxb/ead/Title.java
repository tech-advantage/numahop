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
 * <p>Classe Java pour title complex type.
 *
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 *
 * <pre>
 * &lt;complexType name="title">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;choice maxOccurs="unbounded" minOccurs="0">
 *         &lt;group ref="{urn:isbn:1-931666-22-9}m.phrase.bare"/>
 *         &lt;element name="date" type="{urn:isbn:1-931666-22-9}date"/>
 *         &lt;element name="num" type="{urn:isbn:1-931666-22-9}num"/>
 *       &lt;/choice>
 *       &lt;attGroup ref="{urn:isbn:1-931666-22-9}a.common"/>
 *       &lt;attGroup ref="{urn:isbn:1-931666-22-9}a.access"/>
 *       &lt;attGroup ref="{http://www.w3.org/1999/xlink}simpleLink"/>
 *       &lt;attribute name="render" type="{urn:isbn:1-931666-22-9}av.render" />
 *       &lt;attribute name="entityref" type="{http://www.w3.org/2001/XMLSchema}ENTITY" />
 *       &lt;attribute name="xpointer" type="{http://www.w3.org/2001/XMLSchema}anySimpleType" />
 *       &lt;attribute name="encodinganalog" type="{http://www.w3.org/2001/XMLSchema}anySimpleType" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "title", propOrder = {
    "content"
})
public class Title {

    @XmlElementRefs({
        @XmlElementRef(name = "date", namespace = "urn:isbn:1-931666-22-9", type = JAXBElement.class, required = false),
        @XmlElementRef(name = "emph", namespace = "urn:isbn:1-931666-22-9", type = JAXBElement.class, required = false),
        @XmlElementRef(name = "num", namespace = "urn:isbn:1-931666-22-9", type = JAXBElement.class, required = false),
        @XmlElementRef(name = "lb", namespace = "urn:isbn:1-931666-22-9", type = JAXBElement.class, required = false),
        @XmlElementRef(name = "ptr", namespace = "urn:isbn:1-931666-22-9", type = JAXBElement.class, required = false),
        @XmlElementRef(name = "extptr", namespace = "urn:isbn:1-931666-22-9", type = JAXBElement.class, required = false)
    })
    @XmlMixed
    protected List<Serializable> content;
    @XmlAttribute(name = "render")
    protected AvRender render;
    @XmlAttribute(name = "entityref")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlSchemaType(name = "ENTITY")
    protected String entityref;
    @XmlAttribute(name = "xpointer")
    @XmlSchemaType(name = "anySimpleType")
    protected String xpointer;
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
    @XmlAttribute(name = "source")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlSchemaType(name = "NMTOKEN")
    protected String source;
    @XmlAttribute(name = "rules")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlSchemaType(name = "NMTOKEN")
    protected String rules;
    @XmlAttribute(name = "authfilenumber")
    @XmlSchemaType(name = "anySimpleType")
    protected String authfilenumber;
    @XmlAttribute(name = "normal")
    @XmlSchemaType(name = "anySimpleType")
    protected String normal;
    @XmlAttribute(name = "type")
    protected String type;
    @XmlAttribute(name = "href", namespace = "http://www.w3.org/1999/xlink")
    @XmlSchemaType(name = "anyURI")
    protected String href;
    @XmlAttribute(name = "role")
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
     * {@link JAXBElement }{@code <}{@link Date }{@code >}
     * {@link JAXBElement }{@code <}{@link Emph }{@code >}
     * {@link JAXBElement }{@code <}{@link Num }{@code >}
     * {@link String }
     * {@link JAXBElement }{@code <}{@link Lb }{@code >}
     * {@link JAXBElement }{@code <}{@link Ptr }{@code >}
     * {@link JAXBElement }{@code <}{@link Extptr }{@code >}
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
     * Obtient la valeur de la propriété render.
     *
     * @return
     *     possible object is
     *     {@link AvRender }
     *
     */
    public AvRender getRender() {
        return render;
    }

    /**
     * Définit la valeur de la propriété render.
     *
     * @param value
     *     allowed object is
     *     {@link AvRender }
     *
     */
    public void setRender(final AvRender value) {
        this.render = value;
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
    public void setEntityref(final String value) {
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
    public void setXpointer(final String value) {
        this.xpointer = value;
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
    public void setEncodinganalog(final String value) {
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
    public void setId(final String value) {
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
    public void setAltrender(final String value) {
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
    public void setAudience(final String value) {
        this.audience = value;
    }

    /**
     * Obtient la valeur de la propriété source.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getSource() {
        return source;
    }

    /**
     * Définit la valeur de la propriété source.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setSource(final String value) {
        this.source = value;
    }

    /**
     * Obtient la valeur de la propriété rules.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getRules() {
        return rules;
    }

    /**
     * Définit la valeur de la propriété rules.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setRules(final String value) {
        this.rules = value;
    }

    /**
     * Obtient la valeur de la propriété authfilenumber.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getAuthfilenumber() {
        return authfilenumber;
    }

    /**
     * Définit la valeur de la propriété authfilenumber.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setAuthfilenumber(final String value) {
        this.authfilenumber = value;
    }

    /**
     * Obtient la valeur de la propriété normal.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getNormal() {
        return normal;
    }

    /**
     * Définit la valeur de la propriété normal.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setNormal(final String value) {
        this.normal = value;
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
    public void setType(final String value) {
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
    public void setHref(final String value) {
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
    public void setRole(final String value) {
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
    public void setArcrole(final String value) {
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
    public void setTitle(final String value) {
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
    public void setShow(final String value) {
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
    public void setActuate(final String value) {
        this.actuate = value;
    }

}
