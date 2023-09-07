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
import jakarta.xml.bind.annotation.XmlSchemaType;
import jakarta.xml.bind.annotation.XmlType;
import jakarta.xml.bind.annotation.adapters.CollapsedStringAdapter;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

/**
 * <p>
 * Classe Java pour colspec complex type.
 *
 * <p>
 * Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 *
 * <pre>
 * &lt;complexType name="colspec">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;attribute name="colnum" type="{http://www.w3.org/2001/XMLSchema}NMTOKEN" />
 *       &lt;attribute name="colname" type="{http://www.w3.org/2001/XMLSchema}NMTOKEN" />
 *       &lt;attribute name="colwidth" type="{http://www.w3.org/2001/XMLSchema}anySimpleType" />
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
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "colspec")
public class Colspec {

    @XmlAttribute(name = "colnum")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlSchemaType(name = "NMTOKEN")
    protected String colnum;
    @XmlAttribute(name = "colname")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlSchemaType(name = "NMTOKEN")
    protected String colname;
    @XmlAttribute(name = "colwidth")
    @XmlSchemaType(name = "anySimpleType")
    protected String colwidth;
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

    /**
     * Obtient la valeur de la propriété colnum.
     *
     * @return
     *         possible object is
     *         {@link String }
     *
     */
    public String getColnum() {
        return colnum;
    }

    /**
     * Définit la valeur de la propriété colnum.
     *
     * @param value
     *            allowed object is
     *            {@link String }
     *
     */
    public void setColnum(String value) {
        this.colnum = value;
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
     * Obtient la valeur de la propriété colwidth.
     *
     * @return
     *         possible object is
     *         {@link String }
     *
     */
    public String getColwidth() {
        return colwidth;
    }

    /**
     * Définit la valeur de la propriété colwidth.
     *
     * @param value
     *            allowed object is
     *            {@link String }
     *
     */
    public void setColwidth(String value) {
        this.colwidth = value;
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

}
