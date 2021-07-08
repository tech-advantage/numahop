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
import javax.xml.bind.annotation.XmlID;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;


/**
 * <p>Classe Java pour dao complex type.
 * 
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 * 
 * <pre>
 * &lt;complexType name="dao">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="daodesc" type="{urn:isbn:1-931666-22-9}daodesc" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attGroup ref="{urn:isbn:1-931666-22-9}a.external.ptr"/>
 *       &lt;attGroup ref="{urn:isbn:1-931666-22-9}a.common"/>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "dao", propOrder = {
    "daodesc"
})
public class Dao {

    protected Daodesc daodesc;
    @XmlAttribute(name = "entityref")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlSchemaType(name = "ENTITY")
    protected String entityref;
    @XmlAttribute(name = "xpointer")
    @XmlSchemaType(name = "anySimpleType")
    protected String xpointer;
    @XmlAttribute(name = "type", namespace = "http://www.w3.org/1999/xlink")
    protected String type;
    @XmlAttribute(name = "href")
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
     * Obtient la valeur de la propriété daodesc.
     * 
     * @return
     *     possible object is
     *     {@link Daodesc }
     *     
     */
    public Daodesc getDaodesc() {
        return daodesc;
    }

    /**
     * Définit la valeur de la propriété daodesc.
     * 
     * @param value
     *     allowed object is
     *     {@link Daodesc }
     *     
     */
    public void setDaodesc(final Daodesc value) {
        this.daodesc = value;
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

}
