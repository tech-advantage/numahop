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
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlID;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.util.ArrayList;

/**
 * <p>Classe Java pour dsc complex type.
 * <p>
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 * <p>
 * <pre>
 * &lt;complexType name="dsc">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;sequence>
 *           &lt;element name="head" type="{urn:isbn:1-931666-22-9}head" minOccurs="0"/>
 *           &lt;group ref="{urn:isbn:1-931666-22-9}m.blocks" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;/sequence>
 *         &lt;choice>
 *           &lt;sequence>
 *             &lt;element name="thead" type="{urn:isbn:1-931666-22-9}thead" minOccurs="0"/>
 *             &lt;choice>
 *               &lt;sequence maxOccurs="unbounded">
 *                 &lt;element name="c" type="{urn:isbn:1-931666-22-9}c"/>
 *                 &lt;element name="thead" type="{urn:isbn:1-931666-22-9}thead" minOccurs="0"/>
 *               &lt;/sequence>
 *               &lt;sequence maxOccurs="unbounded">
 *                 &lt;element name="c01" type="{urn:isbn:1-931666-22-9}c01"/>
 *                 &lt;element name="thead" type="{urn:isbn:1-931666-22-9}thead" minOccurs="0"/>
 *               &lt;/sequence>
 *             &lt;/choice>
 *           &lt;/sequence>
 *           &lt;element name="dsc" type="{urn:isbn:1-931666-22-9}dsc" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;/choice>
 *       &lt;/sequence>
 *       &lt;attGroup ref="{urn:isbn:1-931666-22-9}am.dsctab.tpattern"/>
 *       &lt;attGroup ref="{urn:isbn:1-931666-22-9}a.common"/>
 *       &lt;attribute name="type">
 *         &lt;simpleType>
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}token">
 *             &lt;enumeration value="analyticover"/>
 *             &lt;enumeration value="combined"/>
 *             &lt;enumeration value="in-depth"/>
 *             &lt;enumeration value="othertype"/>
 *           &lt;/restriction>
 *         &lt;/simpleType>
 *       &lt;/attribute>
 *       &lt;attribute name="othertype" type="{http://www.w3.org/2001/XMLSchema}NMTOKEN" />
 *       &lt;attribute name="encodinganalog" type="{http://www.w3.org/2001/XMLSchema}anySimpleType" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "dsc", propOrder = {"head", "mBlocks", "thead", "cAndThead", "c01AndThead", "dsc"})
public class Dsc {

    protected Head head;
    @XmlElements({@XmlElement(name = "address", type = Address.class),
                  @XmlElement(name = "chronlist", type = Chronlist.class),
                  @XmlElement(name = "list", type = fr.progilone.pgcn.domain.jaxb.ead.List.class),
                  @XmlElement(name = "note", type = Note.class),
                  @XmlElement(name = "table", type = Table.class),
                  @XmlElement(name = "blockquote", type = Blockquote.class),
                  @XmlElement(name = "p", type = P.class)})
    protected java.util.List<Object> mBlocks;
    protected Thead thead;
    @XmlElements({@XmlElement(name = "c", type = C.class), @XmlElement(name = "thead", type = Thead.class)})
    protected java.util.List<Object> cAndThead;
    @XmlElements({@XmlElement(name = "c01", type = C01.class), @XmlElement(name = "thead", type = Thead.class)})
    protected java.util.List<Object> c01AndThead;
    protected java.util.List<Dsc> dsc;
    @XmlAttribute(name = "type")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    protected String type;
    @XmlAttribute(name = "othertype")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlSchemaType(name = "NMTOKEN")
    protected String othertype;
    @XmlAttribute(name = "encodinganalog")
    @XmlSchemaType(name = "anySimpleType")
    protected String encodinganalog;
    @XmlAttribute(name = "tpattern")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlSchemaType(name = "NMTOKEN")
    protected String tpattern;
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
     * @return possible object is
     * {@link Head }
     */
    public Head getHead() {
        return head;
    }

    /**
     * Définit la valeur de la propriété head.
     *
     * @param value
     *         allowed object is
     *         {@link Head }
     */
    public void setHead(Head value) {
        this.head = value;
    }

    /**
     * Gets the value of the mBlocks property.
     * <p>
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the mBlocks property.
     * <p>
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getMBlocks().add(newItem);
     * </pre>
     * <p>
     * <p>
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Address }
     * {@link Chronlist }
     * {@link fr.progilone.pgcn.domain.jaxb.ead.List }
     * {@link Note }
     * {@link Table }
     * {@link Blockquote }
     * {@link P }
     */
    public java.util.List<Object> getMBlocks() {
        if (mBlocks == null) {
            mBlocks = new ArrayList<>();
        }
        return this.mBlocks;
    }

    /**
     * Obtient la valeur de la propriété thead.
     *
     * @return possible object is
     * {@link Thead }
     */
    public Thead getThead() {
        return thead;
    }

    /**
     * Définit la valeur de la propriété thead.
     *
     * @param value
     *         allowed object is
     *         {@link Thead }
     */
    public void setThead(Thead value) {
        this.thead = value;
    }

    /**
     * Gets the value of the cAndThead property.
     * <p>
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the cAndThead property.
     * <p>
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getCAndThead().add(newItem);
     * </pre>
     * <p>
     * <p>
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link C }
     * {@link Thead }
     */
    public java.util.List<Object> getCAndThead() {
        if (cAndThead == null) {
            cAndThead = new ArrayList<>();
        }
        return this.cAndThead;
    }

    /**
     * Gets the value of the c01AndThead property.
     * <p>
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the c01AndThead property.
     * <p>
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getC01AndThead().add(newItem);
     * </pre>
     * <p>
     * <p>
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link C01 }
     * {@link Thead }
     */
    public java.util.List<Object> getC01AndThead() {
        if (c01AndThead == null) {
            c01AndThead = new ArrayList<>();
        }
        return this.c01AndThead;
    }

    /**
     * Gets the value of the dsc property.
     * <p>
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the dsc property.
     * <p>
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getDsc().add(newItem);
     * </pre>
     * <p>
     * <p>
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Dsc }
     */
    public java.util.List<Dsc> getDsc() {
        if (dsc == null) {
            dsc = new ArrayList<>();
        }
        return this.dsc;
    }

    /**
     * Obtient la valeur de la propriété type.
     *
     * @return possible object is
     * {@link String }
     */
    public String getType() {
        return type;
    }

    /**
     * Définit la valeur de la propriété type.
     *
     * @param value
     *         allowed object is
     *         {@link String }
     */
    public void setType(String value) {
        this.type = value;
    }

    /**
     * Obtient la valeur de la propriété othertype.
     *
     * @return possible object is
     * {@link String }
     */
    public String getOthertype() {
        return othertype;
    }

    /**
     * Définit la valeur de la propriété othertype.
     *
     * @param value
     *         allowed object is
     *         {@link String }
     */
    public void setOthertype(String value) {
        this.othertype = value;
    }

    /**
     * Obtient la valeur de la propriété encodinganalog.
     *
     * @return possible object is
     * {@link String }
     */
    public String getEncodinganalog() {
        return encodinganalog;
    }

    /**
     * Définit la valeur de la propriété encodinganalog.
     *
     * @param value
     *         allowed object is
     *         {@link String }
     */
    public void setEncodinganalog(String value) {
        this.encodinganalog = value;
    }

    /**
     * Obtient la valeur de la propriété tpattern.
     *
     * @return possible object is
     * {@link String }
     */
    public String getTpattern() {
        return tpattern;
    }

    /**
     * Définit la valeur de la propriété tpattern.
     *
     * @param value
     *         allowed object is
     *         {@link String }
     */
    public void setTpattern(String value) {
        this.tpattern = value;
    }

    /**
     * Obtient la valeur de la propriété id.
     *
     * @return possible object is
     * {@link String }
     */
    public String getId() {
        return id;
    }

    /**
     * Définit la valeur de la propriété id.
     *
     * @param value
     *         allowed object is
     *         {@link String }
     */
    public void setId(String value) {
        this.id = value;
    }

    /**
     * Obtient la valeur de la propriété altrender.
     *
     * @return possible object is
     * {@link String }
     */
    public String getAltrender() {
        return altrender;
    }

    /**
     * Définit la valeur de la propriété altrender.
     *
     * @param value
     *         allowed object is
     *         {@link String }
     */
    public void setAltrender(String value) {
        this.altrender = value;
    }

    /**
     * Obtient la valeur de la propriété audience.
     *
     * @return possible object is
     * {@link String }
     */
    public String getAudience() {
        return audience;
    }

    /**
     * Définit la valeur de la propriété audience.
     *
     * @param value
     *         allowed object is
     *         {@link String }
     */
    public void setAudience(String value) {
        this.audience = value;
    }
}
