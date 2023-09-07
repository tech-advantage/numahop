//
// Ce fichier a été généré par l'implémentation de référence JavaTM Architecture for XML Binding (JAXB), v2.2.8-b130911.1802
// Voir <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a>
// Toute modification apportée à ce fichier sera perdue lors de la recompilation du schéma source.
// Généré le : 2017.05.16 à 10:56:55 AM CEST
//

package fr.progilone.pgcn.domain.jaxb.ead;

import jakarta.xml.bind.JAXBElement;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElementRef;
import jakarta.xml.bind.annotation.XmlElementRefs;
import jakarta.xml.bind.annotation.XmlID;
import jakarta.xml.bind.annotation.XmlMixed;
import jakarta.xml.bind.annotation.XmlSchemaType;
import jakarta.xml.bind.annotation.XmlType;
import jakarta.xml.bind.annotation.adapters.CollapsedStringAdapter;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.io.Serializable;
import java.util.ArrayList;

/**
 * <p>
 * Classe Java pour entry complex type.
 *
 * <p>
 * Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 *
 * <pre>
 * &lt;complexType name="entry">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;choice maxOccurs="unbounded" minOccurs="0">
 *         &lt;group ref="{urn:isbn:1-931666-22-9}m.phrase.plus"/>
 *         &lt;element name="address" type="{urn:isbn:1-931666-22-9}address"/>
 *         &lt;element name="list" type="{urn:isbn:1-931666-22-9}list"/>
 *         &lt;element name="note" type="{urn:isbn:1-931666-22-9}note"/>
 *       &lt;/choice>
 *       &lt;attGroup ref="{urn:isbn:1-931666-22-9}a.common"/>
 *       &lt;attribute name="colname" type="{http://www.w3.org/2001/XMLSchema}NMTOKEN" />
 *       &lt;attribute name="namest" type="{http://www.w3.org/2001/XMLSchema}NMTOKEN" />
 *       &lt;attribute name="nameend" type="{http://www.w3.org/2001/XMLSchema}NMTOKEN" />
 *       &lt;attribute name="morerows" type="{http://www.w3.org/2001/XMLSchema}NMTOKEN" />
 *       &lt;attribute name="colsep" type="{urn:isbn:1-931666-22-9}yesorno" />
 *       &lt;attribute name="rowsep" type="{urn:isbn:1-931666-22-9}yesorno" />
 *       &lt;attribute name="align">
 *         &lt;simpleType>
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}token">
 *             &lt;enumeration value="left"/>
 *             &lt;enumeration value="right"/>
 *             &lt;enumeration value="center"/>
 *             &lt;enumeration value="justify"/>
 *             &lt;enumeration value="char"/>
 *           &lt;/restriction>
 *         &lt;/simpleType>
 *       &lt;/attribute>
 *       &lt;attribute name="char" type="{http://www.w3.org/2001/XMLSchema}anySimpleType" />
 *       &lt;attribute name="charoff" type="{http://www.w3.org/2001/XMLSchema}NMTOKEN" />
 *       &lt;attribute name="valign">
 *         &lt;simpleType>
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}token">
 *             &lt;enumeration value="top"/>
 *             &lt;enumeration value="middle"/>
 *             &lt;enumeration value="bottom"/>
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
@XmlType(name = "entry", propOrder = {"content"})
public class Entry {

    @XmlElementRefs({@XmlElementRef(name = "expan", namespace = "urn:isbn:1-931666-22-9", type = JAXBElement.class, required = false),
                     @XmlElementRef(name = "note", namespace = "urn:isbn:1-931666-22-9", type = JAXBElement.class, required = false),
                     @XmlElementRef(name = "function", namespace = "urn:isbn:1-931666-22-9", type = JAXBElement.class, required = false),
                     @XmlElementRef(name = "emph", namespace = "urn:isbn:1-931666-22-9", type = JAXBElement.class, required = false),
                     @XmlElementRef(name = "name", namespace = "urn:isbn:1-931666-22-9", type = JAXBElement.class, required = false),
                     @XmlElementRef(name = "persname", namespace = "urn:isbn:1-931666-22-9", type = JAXBElement.class, required = false),
                     @XmlElementRef(name = "lb", namespace = "urn:isbn:1-931666-22-9", type = JAXBElement.class, required = false),
                     @XmlElementRef(name = "date", namespace = "urn:isbn:1-931666-22-9", type = JAXBElement.class, required = false),
                     @XmlElementRef(name = "famname", namespace = "urn:isbn:1-931666-22-9", type = JAXBElement.class, required = false),
                     @XmlElementRef(name = "abbr", namespace = "urn:isbn:1-931666-22-9", type = JAXBElement.class, required = false),
                     @XmlElementRef(name = "corpname", namespace = "urn:isbn:1-931666-22-9", type = JAXBElement.class, required = false),
                     @XmlElementRef(name = "num", namespace = "urn:isbn:1-931666-22-9", type = JAXBElement.class, required = false),
                     @XmlElementRef(name = "genreform", namespace = "urn:isbn:1-931666-22-9", type = JAXBElement.class, required = false),
                     @XmlElementRef(name = "repository", namespace = "urn:isbn:1-931666-22-9", type = JAXBElement.class, required = false),
                     @XmlElementRef(name = "unitdate", namespace = "urn:isbn:1-931666-22-9", type = JAXBElement.class, required = false),
                     @XmlElementRef(name = "unittitle", namespace = "urn:isbn:1-931666-22-9", type = JAXBElement.class, required = false),
                     @XmlElementRef(name = "extref", namespace = "urn:isbn:1-931666-22-9", type = JAXBElement.class, required = false),
                     @XmlElementRef(name = "title", namespace = "urn:isbn:1-931666-22-9", type = JAXBElement.class, required = false),
                     @XmlElementRef(name = "address", namespace = "urn:isbn:1-931666-22-9", type = JAXBElement.class, required = false),
                     @XmlElementRef(name = "archref", namespace = "urn:isbn:1-931666-22-9", type = JAXBElement.class, required = false),
                     @XmlElementRef(name = "geogname", namespace = "urn:isbn:1-931666-22-9", type = JAXBElement.class, required = false),
                     @XmlElementRef(name = "subject", namespace = "urn:isbn:1-931666-22-9", type = JAXBElement.class, required = false),
                     @XmlElementRef(name = "occupation", namespace = "urn:isbn:1-931666-22-9", type = JAXBElement.class, required = false),
                     @XmlElementRef(name = "linkgrp", namespace = "urn:isbn:1-931666-22-9", type = JAXBElement.class, required = false),
                     @XmlElementRef(name = "ptr", namespace = "urn:isbn:1-931666-22-9", type = JAXBElement.class, required = false),
                     @XmlElementRef(name = "origination", namespace = "urn:isbn:1-931666-22-9", type = JAXBElement.class, required = false),
                     @XmlElementRef(name = "bibref", namespace = "urn:isbn:1-931666-22-9", type = JAXBElement.class, required = false),
                     @XmlElementRef(name = "ref", namespace = "urn:isbn:1-931666-22-9", type = JAXBElement.class, required = false),
                     @XmlElementRef(name = "extptr", namespace = "urn:isbn:1-931666-22-9", type = JAXBElement.class, required = false),
                     @XmlElementRef(name = "list", namespace = "urn:isbn:1-931666-22-9", type = JAXBElement.class, required = false)})
    @XmlMixed
    protected java.util.List<Serializable> content;
    @XmlAttribute(name = "colname")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlSchemaType(name = "NMTOKEN")
    protected String colname;
    @XmlAttribute(name = "namest")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlSchemaType(name = "NMTOKEN")
    protected String namest;
    @XmlAttribute(name = "nameend")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlSchemaType(name = "NMTOKEN")
    protected String nameend;
    @XmlAttribute(name = "morerows")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlSchemaType(name = "NMTOKEN")
    protected String morerows;
    @XmlAttribute(name = "colsep")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    protected String colsep;
    @XmlAttribute(name = "rowsep")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    protected String rowsep;
    @XmlAttribute(name = "align")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    protected String align;
    @XmlAttribute(name = "char")
    @XmlSchemaType(name = "anySimpleType")
    protected String _char;
    @XmlAttribute(name = "charoff")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlSchemaType(name = "NMTOKEN")
    protected String charoff;
    @XmlAttribute(name = "valign")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    protected String valign;
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
     *
     * <pre>
     * getContent().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link JAXBElement }{@code <}{@link Expan }{@code >}
     * {@link JAXBElement }{@code <}{@link Note }{@code >}
     * {@link JAXBElement }{@code <}{@link Function }{@code >}
     * {@link JAXBElement }{@code <}{@link Emph }{@code >}
     * {@link JAXBElement }{@code <}{@link Name }{@code >}
     * {@link JAXBElement }{@code <}{@link Persname }{@code >}
     * {@link JAXBElement }{@code <}{@link Lb }{@code >}
     * {@link JAXBElement }{@code <}{@link Date }{@code >}
     * {@link JAXBElement }{@code <}{@link Famname }{@code >}
     * {@link String }
     * {@link JAXBElement }{@code <}{@link Abbr }{@code >}
     * {@link JAXBElement }{@code <}{@link Corpname }{@code >}
     * {@link JAXBElement }{@code <}{@link Num }{@code >}
     * {@link JAXBElement }{@code <}{@link Genreform }{@code >}
     * {@link JAXBElement }{@code <}{@link Repository }{@code >}
     * {@link JAXBElement }{@code <}{@link Unitdate }{@code >}
     * {@link JAXBElement }{@code <}{@link Unittitle }{@code >}
     * {@link JAXBElement }{@code <}{@link Extref }{@code >}
     * {@link JAXBElement }{@code <}{@link Title }{@code >}
     * {@link JAXBElement }{@code <}{@link Address }{@code >}
     * {@link JAXBElement }{@code <}{@link Archref }{@code >}
     * {@link JAXBElement }{@code <}{@link Geogname }{@code >}
     * {@link JAXBElement }{@code <}{@link Subject }{@code >}
     * {@link JAXBElement }{@code <}{@link Occupation }{@code >}
     * {@link JAXBElement }{@code <}{@link Linkgrp }{@code >}
     * {@link JAXBElement }{@code <}{@link Ptr }{@code >}
     * {@link JAXBElement }{@code <}{@link Origination }{@code >}
     * {@link JAXBElement }{@code <}{@link Bibref }{@code >}
     * {@link JAXBElement }{@code <}{@link Ref }{@code >}
     * {@link JAXBElement }{@code <}{@link Extptr }{@code >}
     * {@link JAXBElement }{@code <}{@link List }{@code >}
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
     * Obtient la valeur de la propriété colname.
     *
     * @return
     *         possible object is
     *         {@link String }
     *
     */
    public String getColname() {
        return colname;
    }

    /**
     * Définit la valeur de la propriété colname.
     *
     * @param value
     *            allowed object is
     *            {@link String }
     *
     */
    public void setColname(String value) {
        this.colname = value;
    }

    /**
     * Obtient la valeur de la propriété namest.
     *
     * @return
     *         possible object is
     *         {@link String }
     *
     */
    public String getNamest() {
        return namest;
    }

    /**
     * Définit la valeur de la propriété namest.
     *
     * @param value
     *            allowed object is
     *            {@link String }
     *
     */
    public void setNamest(String value) {
        this.namest = value;
    }

    /**
     * Obtient la valeur de la propriété nameend.
     *
     * @return
     *         possible object is
     *         {@link String }
     *
     */
    public String getNameend() {
        return nameend;
    }

    /**
     * Définit la valeur de la propriété nameend.
     *
     * @param value
     *            allowed object is
     *            {@link String }
     *
     */
    public void setNameend(String value) {
        this.nameend = value;
    }

    /**
     * Obtient la valeur de la propriété morerows.
     *
     * @return
     *         possible object is
     *         {@link String }
     *
     */
    public String getMorerows() {
        return morerows;
    }

    /**
     * Définit la valeur de la propriété morerows.
     *
     * @param value
     *            allowed object is
     *            {@link String }
     *
     */
    public void setMorerows(String value) {
        this.morerows = value;
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
     * Obtient la valeur de la propriété align.
     *
     * @return
     *         possible object is
     *         {@link String }
     *
     */
    public String getAlign() {
        return align;
    }

    /**
     * Définit la valeur de la propriété align.
     *
     * @param value
     *            allowed object is
     *            {@link String }
     *
     */
    public void setAlign(String value) {
        this.align = value;
    }

    /**
     * Obtient la valeur de la propriété char.
     *
     * @return
     *         possible object is
     *         {@link String }
     *
     */
    public String getChar() {
        return _char;
    }

    /**
     * Définit la valeur de la propriété char.
     *
     * @param value
     *            allowed object is
     *            {@link String }
     *
     */
    public void setChar(String value) {
        this._char = value;
    }

    /**
     * Obtient la valeur de la propriété charoff.
     *
     * @return
     *         possible object is
     *         {@link String }
     *
     */
    public String getCharoff() {
        return charoff;
    }

    /**
     * Définit la valeur de la propriété charoff.
     *
     * @param value
     *            allowed object is
     *            {@link String }
     *
     */
    public void setCharoff(String value) {
        this.charoff = value;
    }

    /**
     * Obtient la valeur de la propriété valign.
     *
     * @return
     *         possible object is
     *         {@link String }
     *
     */
    public String getValign() {
        return valign;
    }

    /**
     * Définit la valeur de la propriété valign.
     *
     * @param value
     *            allowed object is
     *            {@link String }
     *
     */
    public void setValign(String value) {
        this.valign = value;
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
