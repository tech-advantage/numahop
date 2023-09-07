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
import jakarta.xml.bind.annotation.XmlID;
import jakarta.xml.bind.annotation.XmlSchemaType;
import jakarta.xml.bind.annotation.XmlType;
import jakarta.xml.bind.annotation.adapters.CollapsedStringAdapter;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.util.ArrayList;

/**
 * <p>
 * Classe Java pour list complex type.
 *
 * <p>
 * Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 *
 * <pre>
 * &lt;complexType name="list">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="head" type="{urn:isbn:1-931666-22-9}head" minOccurs="0"/>
 *         &lt;choice>
 *           &lt;element name="item" type="{urn:isbn:1-931666-22-9}item" maxOccurs="unbounded"/>
 *           &lt;sequence>
 *             &lt;element name="listhead" type="{urn:isbn:1-931666-22-9}listhead" minOccurs="0"/>
 *             &lt;element name="defitem" type="{urn:isbn:1-931666-22-9}defitem" maxOccurs="unbounded"/>
 *           &lt;/sequence>
 *         &lt;/choice>
 *       &lt;/sequence>
 *       &lt;attGroup ref="{urn:isbn:1-931666-22-9}a.common"/>
 *       &lt;attribute name="type">
 *         &lt;simpleType>
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}token">
 *             &lt;enumeration value="simple"/>
 *             &lt;enumeration value="deflist"/>
 *             &lt;enumeration value="marked"/>
 *             &lt;enumeration value="ordered"/>
 *           &lt;/restriction>
 *         &lt;/simpleType>
 *       &lt;/attribute>
 *       &lt;attribute name="mark" type="{http://www.w3.org/2001/XMLSchema}anySimpleType" />
 *       &lt;attribute name="numeration">
 *         &lt;simpleType>
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}token">
 *             &lt;enumeration value="arabic"/>
 *             &lt;enumeration value="upperalpha"/>
 *             &lt;enumeration value="loweralpha"/>
 *             &lt;enumeration value="upperroman"/>
 *             &lt;enumeration value="lowerroman"/>
 *           &lt;/restriction>
 *         &lt;/simpleType>
 *       &lt;/attribute>
 *       &lt;attribute name="continuation">
 *         &lt;simpleType>
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}token">
 *             &lt;enumeration value="continues"/>
 *             &lt;enumeration value="starts"/>
 *           &lt;/restriction>
 *         &lt;/simpleType>
 *       &lt;/attribute>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "list",
         propOrder = {"head",
                      "item",
                      "listhead",
                      "defitem"})
public class List {

    protected Head head;
    protected java.util.List<Item> item;
    protected Listhead listhead;
    protected java.util.List<Defitem> defitem;
    @XmlAttribute(name = "type")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    protected String type;
    @XmlAttribute(name = "mark")
    @XmlSchemaType(name = "anySimpleType")
    protected String mark;
    @XmlAttribute(name = "numeration")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    protected String numeration;
    @XmlAttribute(name = "continuation")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    protected String continuation;
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
     * Gets the value of the item property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the item property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     *
     * <pre>
     * getItem().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Item }
     *
     *
     */
    public java.util.List<Item> getItem() {
        if (item == null) {
            item = new ArrayList<>();
        }
        return this.item;
    }

    /**
     * Obtient la valeur de la propriété listhead.
     *
     * @return
     *         possible object is
     *         {@link Listhead }
     *
     */
    public Listhead getListhead() {
        return listhead;
    }

    /**
     * Définit la valeur de la propriété listhead.
     *
     * @param value
     *            allowed object is
     *            {@link Listhead }
     *
     */
    public void setListhead(Listhead value) {
        this.listhead = value;
    }

    /**
     * Gets the value of the defitem property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the defitem property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     *
     * <pre>
     * getDefitem().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Defitem }
     *
     *
     */
    public java.util.List<Defitem> getDefitem() {
        if (defitem == null) {
            defitem = new ArrayList<>();
        }
        return this.defitem;
    }

    /**
     * Obtient la valeur de la propriété type.
     *
     * @return
     *         possible object is
     *         {@link String }
     *
     */
    public String getType() {
        return type;
    }

    /**
     * Définit la valeur de la propriété type.
     *
     * @param value
     *            allowed object is
     *            {@link String }
     *
     */
    public void setType(String value) {
        this.type = value;
    }

    /**
     * Obtient la valeur de la propriété mark.
     *
     * @return
     *         possible object is
     *         {@link String }
     *
     */
    public String getMark() {
        return mark;
    }

    /**
     * Définit la valeur de la propriété mark.
     *
     * @param value
     *            allowed object is
     *            {@link String }
     *
     */
    public void setMark(String value) {
        this.mark = value;
    }

    /**
     * Obtient la valeur de la propriété numeration.
     *
     * @return
     *         possible object is
     *         {@link String }
     *
     */
    public String getNumeration() {
        return numeration;
    }

    /**
     * Définit la valeur de la propriété numeration.
     *
     * @param value
     *            allowed object is
     *            {@link String }
     *
     */
    public void setNumeration(String value) {
        this.numeration = value;
    }

    /**
     * Obtient la valeur de la propriété continuation.
     *
     * @return
     *         possible object is
     *         {@link String }
     *
     */
    public String getContinuation() {
        return continuation;
    }

    /**
     * Définit la valeur de la propriété continuation.
     *
     * @param value
     *            allowed object is
     *            {@link String }
     *
     */
    public void setContinuation(String value) {
        this.continuation = value;
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
