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
 * Classe Java pour tgroup complex type.
 *
 * <p>
 * Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 *
 * <pre>
 * &lt;complexType name="tgroup">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="colspec" type="{urn:isbn:1-931666-22-9}colspec" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="thead" type="{urn:isbn:1-931666-22-9}thead" minOccurs="0"/>
 *         &lt;element name="tbody" type="{urn:isbn:1-931666-22-9}tbody"/>
 *       &lt;/sequence>
 *       &lt;attGroup ref="{urn:isbn:1-931666-22-9}a.common"/>
 *       &lt;attribute name="cols" use="required" type="{http://www.w3.org/2001/XMLSchema}NMTOKEN" />
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
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "tgroup",
         propOrder = {"colspec",
                      "thead",
                      "tbody"})
public class Tgroup {

    protected List<Colspec> colspec;
    protected Thead thead;
    @XmlElement(required = true)
    protected Tbody tbody;
    @XmlAttribute(name = "cols", required = true)
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlSchemaType(name = "NMTOKEN")
    protected String cols;
    @XmlAttribute(name = "colsep")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    protected String colsep;
    @XmlAttribute(name = "rowsep")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    protected String rowsep;
    @XmlAttribute(name = "align")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    protected String align;
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
     * Gets the value of the colspec property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the colspec property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     *
     * <pre>
     * getColspec().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Colspec }
     *
     *
     */
    public List<Colspec> getColspec() {
        if (colspec == null) {
            colspec = new ArrayList<>();
        }
        return this.colspec;
    }

    /**
     * Obtient la valeur de la propriété thead.
     *
     * @return
     *         possible object is
     *         {@link Thead }
     *
     */
    public Thead getThead() {
        return thead;
    }

    /**
     * Définit la valeur de la propriété thead.
     *
     * @param value
     *            allowed object is
     *            {@link Thead }
     *
     */
    public void setThead(Thead value) {
        this.thead = value;
    }

    /**
     * Obtient la valeur de la propriété tbody.
     *
     * @return
     *         possible object is
     *         {@link Tbody }
     *
     */
    public Tbody getTbody() {
        return tbody;
    }

    /**
     * Définit la valeur de la propriété tbody.
     *
     * @param value
     *            allowed object is
     *            {@link Tbody }
     *
     */
    public void setTbody(Tbody value) {
        this.tbody = value;
    }

    /**
     * Obtient la valeur de la propriété cols.
     *
     * @return
     *         possible object is
     *         {@link String }
     *
     */
    public String getCols() {
        return cols;
    }

    /**
     * Définit la valeur de la propriété cols.
     *
     * @param value
     *            allowed object is
     *            {@link String }
     *
     */
    public void setCols(String value) {
        this.cols = value;
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
