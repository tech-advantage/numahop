//
// Ce fichier a été généré par l'implémentation de référence JavaTM Architecture for XML Binding (JAXB), v2.2.8-b130911.1802
// Voir <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a>
// Toute modification apportée à ce fichier sera perdue lors de la recompilation du schéma source.
// Généré le : 2017.05.16 à 10:56:55 AM CEST
//

package fr.progilone.pgcn.domain.jaxb.ead;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlID;
import jakarta.xml.bind.annotation.XmlSchemaType;
import jakarta.xml.bind.annotation.XmlType;
import jakarta.xml.bind.annotation.adapters.CollapsedStringAdapter;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * Classe Java pour table complex type.
 *
 * <p>
 * Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 *
 * <pre>
 * &lt;complexType name="table">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="head" type="{urn:isbn:1-931666-22-9}head" minOccurs="0"/>
 *         &lt;element name="tgroup" type="{urn:isbn:1-931666-22-9}tgroup" maxOccurs="unbounded"/>
 *       &lt;/sequence>
 *       &lt;attGroup ref="{urn:isbn:1-931666-22-9}a.common"/>
 *       &lt;attribute name="frame">
 *         &lt;simpleType>
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}token">
 *             &lt;enumeration value="top"/>
 *             &lt;enumeration value="bottom"/>
 *             &lt;enumeration value="topbot"/>
 *             &lt;enumeration value="all"/>
 *             &lt;enumeration value="sides"/>
 *             &lt;enumeration value="none"/>
 *           &lt;/restriction>
 *         &lt;/simpleType>
 *       &lt;/attribute>
 *       &lt;attribute name="colsep" type="{urn:isbn:1-931666-22-9}yesorno" />
 *       &lt;attribute name="rowsep" type="{urn:isbn:1-931666-22-9}yesorno" />
 *       &lt;attribute name="pgwide" type="{urn:isbn:1-931666-22-9}yesorno" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "table",
         propOrder = {"head",
                      "tgroup"})
public class Table {

    protected Head head;
    @XmlElement(required = true)
    protected List<Tgroup> tgroup;
    @XmlAttribute(name = "frame")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    protected String frame;
    @XmlAttribute(name = "colsep")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    protected String colsep;
    @XmlAttribute(name = "rowsep")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    protected String rowsep;
    @XmlAttribute(name = "pgwide")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    protected String pgwide;
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
     *         possible object is
     *         {@link Head }
     *
     */
    public Head getHead() {
        return head;
    }

    /**
     * Définit la valeur de la propriété head.
     *
     * @param value
     *            allowed object is
     *            {@link Head }
     *
     */
    public void setHead(Head value) {
        this.head = value;
    }

    /**
     * Gets the value of the tgroup property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the tgroup property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     *
     * <pre>
     * getTgroup().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Tgroup }
     *
     *
     */
    public List<Tgroup> getTgroup() {
        if (tgroup == null) {
            tgroup = new ArrayList<>();
        }
        return this.tgroup;
    }

    /**
     * Obtient la valeur de la propriété frame.
     *
     * @return
     *         possible object is
     *         {@link String }
     *
     */
    public String getFrame() {
        return frame;
    }

    /**
     * Définit la valeur de la propriété frame.
     *
     * @param value
     *            allowed object is
     *            {@link String }
     *
     */
    public void setFrame(String value) {
        this.frame = value;
    }

    /**
     * Obtient la valeur de la propriété colsep.
     *
     * @return
     *         possible object is
     *         {@link String }
     *
     */
    public String getColsep() {
        return colsep;
    }

    /**
     * Définit la valeur de la propriété colsep.
     *
     * @param value
     *            allowed object is
     *            {@link String }
     *
     */
    public void setColsep(String value) {
        this.colsep = value;
    }

    /**
     * Obtient la valeur de la propriété rowsep.
     *
     * @return
     *         possible object is
     *         {@link String }
     *
     */
    public String getRowsep() {
        return rowsep;
    }

    /**
     * Définit la valeur de la propriété rowsep.
     *
     * @param value
     *            allowed object is
     *            {@link String }
     *
     */
    public void setRowsep(String value) {
        this.rowsep = value;
    }

    /**
     * Obtient la valeur de la propriété pgwide.
     *
     * @return
     *         possible object is
     *         {@link String }
     *
     */
    public String getPgwide() {
        return pgwide;
    }

    /**
     * Définit la valeur de la propriété pgwide.
     *
     * @param value
     *            allowed object is
     *            {@link String }
     *
     */
    public void setPgwide(String value) {
        this.pgwide = value;
    }

    /**
     * Obtient la valeur de la propriété id.
     *
     * @return
     *         possible object is
     *         {@link String }
     *
     */
    public String getId() {
        return id;
    }

    /**
     * Définit la valeur de la propriété id.
     *
     * @param value
     *            allowed object is
     *            {@link String }
     *
     */
    public void setId(String value) {
        this.id = value;
    }

    /**
     * Obtient la valeur de la propriété altrender.
     *
     * @return
     *         possible object is
     *         {@link String }
     *
     */
    public String getAltrender() {
        return altrender;
    }

    /**
     * Définit la valeur de la propriété altrender.
     *
     * @param value
     *            allowed object is
     *            {@link String }
     *
     */
    public void setAltrender(String value) {
        this.altrender = value;
    }

    /**
     * Obtient la valeur de la propriété audience.
     *
     * @return
     *         possible object is
     *         {@link String }
     *
     */
    public String getAudience() {
        return audience;
    }

    /**
     * Définit la valeur de la propriété audience.
     *
     * @param value
     *            allowed object is
     *            {@link String }
     *
     */
    public void setAudience(String value) {
        this.audience = value;
    }

}
