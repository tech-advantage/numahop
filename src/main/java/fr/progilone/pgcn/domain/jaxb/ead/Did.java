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
 * <p>Classe Java pour did complex type.
 *
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 *
 * <pre>
 * &lt;complexType name="did">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="head" type="{urn:isbn:1-931666-22-9}head" minOccurs="0"/>
 *         &lt;group ref="{urn:isbn:1-931666-22-9}m.did" maxOccurs="unbounded"/>
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
@XmlType(name = "did", propOrder = {
    "head",
    "mDid"
})
public class Did {

    protected Head head;
    @XmlElements({
        @XmlElement(name = "abstract", type = Abstract.class),
        @XmlElement(name = "container", type = Container.class),
        @XmlElement(name = "dao", type = Dao.class),
        @XmlElement(name = "daogrp", type = Daogrp.class),
        @XmlElement(name = "langmaterial", type = Langmaterial.class),
        @XmlElement(name = "materialspec", type = Materialspec.class),
        @XmlElement(name = "note", type = Note.class),
        @XmlElement(name = "origination", type = Origination.class),
        @XmlElement(name = "physdesc", type = Physdesc.class),
        @XmlElement(name = "physloc", type = Physloc.class),
        @XmlElement(name = "repository", type = Repository.class),
        @XmlElement(name = "unitdate", type = Unitdate.class),
        @XmlElement(name = "unitid", type = Unitid.class),
        @XmlElement(name = "unittitle", type = Unittitle.class)
    })
    protected List<Object> mDid;
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
     * Gets the value of the mDid property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the mDid property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getMDid().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Abstract }
     * {@link Container }
     * {@link Dao }
     * {@link Daogrp }
     * {@link Langmaterial }
     * {@link Materialspec }
     * {@link Note }
     * {@link Origination }
     * {@link Physdesc }
     * {@link Physloc }
     * {@link Repository }
     * {@link Unitdate }
     * {@link Unitid }
     * {@link Unittitle }
     *
     *
     */
    public List<Object> getMDid() {
        if (mDid == null) {
            mDid = new ArrayList<>();
        }
        return this.mDid;
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
