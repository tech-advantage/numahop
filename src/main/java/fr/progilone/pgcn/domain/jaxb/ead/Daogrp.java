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
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlID;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;


/**
 * <p>Classe Java pour daogrp complex type.
 *
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 *
 * <pre>
 * &lt;complexType name="daogrp">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="daodesc" type="{urn:isbn:1-931666-22-9}daodesc" minOccurs="0"/>
 *         &lt;choice maxOccurs="unbounded">
 *           &lt;element name="daoloc" type="{urn:isbn:1-931666-22-9}daoloc"/>
 *           &lt;group ref="{urn:isbn:1-931666-22-9}extended.els"/>
 *         &lt;/choice>
 *       &lt;/sequence>
 *       &lt;attGroup ref="{urn:isbn:1-931666-22-9}a.common"/>
 *       &lt;attGroup ref="{http://www.w3.org/1999/xlink}extendedLink"/>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "daogrp", propOrder = {
    "daodesc",
    "daolocOrResourceOrArc"
})
public class Daogrp {

    protected Daodesc daodesc;
    @XmlElements({
        @XmlElement(name = "daoloc", type = Daoloc.class),
        @XmlElement(name = "resource", type = Resource.class),
        @XmlElement(name = "arc", type = Arc.class),
        @XmlElement(name = "ptrloc", type = Ptrloc.class),
        @XmlElement(name = "extptrloc", type = Extptrloc.class),
        @XmlElement(name = "refloc", type = Refloc.class),
        @XmlElement(name = "extrefloc", type = Extrefloc.class)
    })
    protected List<Object> daolocOrResourceOrArc;
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
    @XmlAttribute(name = "role", namespace = "http://www.w3.org/1999/xlink")
    protected String role;
    @XmlAttribute(name = "title", namespace = "http://www.w3.org/1999/xlink")
    protected String title;

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
    public void setDaodesc(Daodesc value) {
        this.daodesc = value;
    }

    /**
     * Gets the value of the daolocOrResourceOrArc property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the daolocOrResourceOrArc property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getDaolocOrResourceOrArc().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Daoloc }
     * {@link Resource }
     * {@link Arc }
     * {@link Ptrloc }
     * {@link Extptrloc }
     * {@link Refloc }
     * {@link Extrefloc }
     *
     *
     */
    public List<Object> getDaolocOrResourceOrArc() {
        if (daolocOrResourceOrArc == null) {
            daolocOrResourceOrArc = new ArrayList<>();
        }
        return this.daolocOrResourceOrArc;
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
            return "extended";
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

}
