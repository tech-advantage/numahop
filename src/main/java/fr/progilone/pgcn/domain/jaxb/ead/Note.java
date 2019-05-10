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
 * <p>Classe Java pour note complex type.
 *
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 *
 * <pre>
 * &lt;complexType name="note">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;group ref="{urn:isbn:1-931666-22-9}m.blocks" maxOccurs="unbounded"/>
 *       &lt;attGroup ref="{urn:isbn:1-931666-22-9}a.common"/>
 *       &lt;attribute name="type" type="{http://www.w3.org/2001/XMLSchema}anySimpleType" />
 *       &lt;attribute name="label" type="{http://www.w3.org/2001/XMLSchema}anySimpleType" />
 *       &lt;attribute name="show">
 *         &lt;simpleType>
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}token">
 *             &lt;enumeration value="embed"/>
 *             &lt;enumeration value="new"/>
 *           &lt;/restriction>
 *         &lt;/simpleType>
 *       &lt;/attribute>
 *       &lt;attribute name="actuate">
 *         &lt;simpleType>
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}token">
 *             &lt;enumeration value="onload"/>
 *             &lt;enumeration value="onrequest"/>
 *           &lt;/restriction>
 *         &lt;/simpleType>
 *       &lt;/attribute>
 *       &lt;attribute name="encodinganalog" type="{http://www.w3.org/2001/XMLSchema}anySimpleType" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "note", propOrder = {
    "mBlocks"
})
public class Note {

    @XmlElements({
        @XmlElement(name = "address", type = Address.class),
        @XmlElement(name = "chronlist", type = Chronlist.class),
        @XmlElement(name = "list", type = List.class),
        @XmlElement(name = "note", type = Note.class),
        @XmlElement(name = "table", type = Table.class),
        @XmlElement(name = "blockquote", type = Blockquote.class),
        @XmlElement(name = "p", type = P.class)
    })
    protected java.util.List<Object> mBlocks;
    @XmlAttribute(name = "type")
    @XmlSchemaType(name = "anySimpleType")
    protected String type;
    @XmlAttribute(name = "label")
    @XmlSchemaType(name = "anySimpleType")
    protected String label;
    @XmlAttribute(name = "show")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    protected String show;
    @XmlAttribute(name = "actuate")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    protected String actuate;
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
     * Gets the value of the mBlocks property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the mBlocks property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getMBlocks().add(newItem);
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
     *
     *
     */
    public java.util.List<Object> getMBlocks() {
        if (mBlocks == null) {
            mBlocks = new ArrayList<>();
        }
        return this.mBlocks;
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
        return type;
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
