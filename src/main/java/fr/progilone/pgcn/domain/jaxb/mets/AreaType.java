//
// Ce fichier a été généré par l'implémentation de référence JavaTM Architecture for XML Binding (JAXB), v2.2.11
// Voir <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a>
// Toute modification apportée à ce fichier sera perdue lors de la recompilation du schéma source.
// Généré le : 2016.12.29 à 11:54:46 AM CET
//

package fr.progilone.pgcn.domain.jaxb.mets;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAnyAttribute;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlID;
import jakarta.xml.bind.annotation.XmlIDREF;
import jakarta.xml.bind.annotation.XmlSchemaType;
import jakarta.xml.bind.annotation.XmlType;
import jakarta.xml.bind.annotation.adapters.CollapsedStringAdapter;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.xml.namespace.QName;

/**
 * areaType: Complex Type for Area Linking
 * The area element provides for more sophisticated linking between a div element and content files representing that div, be they text, image, audio,
 * or video files. An area element can link a div to a point within a file, to a one-dimension segment of a file (e.g., text segment, image line,
 * audio/video clip), or a two-dimensional section of a file (e.g, subsection of an image, or a subsection of the video display of a video file. The
 * area element has no content; all information is recorded within its various attributes.
 *
 *
 * <p>
 * Classe Java pour areaType complex type.
 *
 * <p>
 * Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 *
 * <pre>
 * &lt;complexType name="areaType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;attGroup ref="{http://www.loc.gov/METS/}ORDERLABELS"/&gt;
 *       &lt;attribute name="ID" type="{http://www.w3.org/2001/XMLSchema}ID" /&gt;
 *       &lt;attribute name="FILEID" use="required" type="{http://www.w3.org/2001/XMLSchema}IDREF" /&gt;
 *       &lt;attribute name="SHAPE"&gt;
 *         &lt;simpleType&gt;
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *             &lt;enumeration value="RECT"/&gt;
 *             &lt;enumeration value="CIRCLE"/&gt;
 *             &lt;enumeration value="POLY"/&gt;
 *           &lt;/restriction&gt;
 *         &lt;/simpleType&gt;
 *       &lt;/attribute&gt;
 *       &lt;attribute name="COORDS" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *       &lt;attribute name="BEGIN" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *       &lt;attribute name="END" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *       &lt;attribute name="BETYPE"&gt;
 *         &lt;simpleType&gt;
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *             &lt;enumeration value="BYTE"/&gt;
 *             &lt;enumeration value="IDREF"/&gt;
 *             &lt;enumeration value="SMIL"/&gt;
 *             &lt;enumeration value="MIDI"/&gt;
 *             &lt;enumeration value="SMPTE-25"/&gt;
 *             &lt;enumeration value="SMPTE-24"/&gt;
 *             &lt;enumeration value="SMPTE-DF30"/&gt;
 *             &lt;enumeration value="SMPTE-NDF30"/&gt;
 *             &lt;enumeration value="SMPTE-DF29.97"/&gt;
 *             &lt;enumeration value="SMPTE-NDF29.97"/&gt;
 *             &lt;enumeration value="TIME"/&gt;
 *             &lt;enumeration value="TCF"/&gt;
 *             &lt;enumeration value="XPTR"/&gt;
 *           &lt;/restriction&gt;
 *         &lt;/simpleType&gt;
 *       &lt;/attribute&gt;
 *       &lt;attribute name="EXTENT" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *       &lt;attribute name="EXTTYPE"&gt;
 *         &lt;simpleType&gt;
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *             &lt;enumeration value="BYTE"/&gt;
 *             &lt;enumeration value="SMIL"/&gt;
 *             &lt;enumeration value="MIDI"/&gt;
 *             &lt;enumeration value="SMPTE-25"/&gt;
 *             &lt;enumeration value="SMPTE-24"/&gt;
 *             &lt;enumeration value="SMPTE-DF30"/&gt;
 *             &lt;enumeration value="SMPTE-NDF30"/&gt;
 *             &lt;enumeration value="SMPTE-DF29.97"/&gt;
 *             &lt;enumeration value="SMPTE-NDF29.97"/&gt;
 *             &lt;enumeration value="TIME"/&gt;
 *             &lt;enumeration value="TCF"/&gt;
 *           &lt;/restriction&gt;
 *         &lt;/simpleType&gt;
 *       &lt;/attribute&gt;
 *       &lt;attribute name="ADMID" type="{http://www.w3.org/2001/XMLSchema}IDREFS" /&gt;
 *       &lt;attribute name="CONTENTIDS" type="{http://www.loc.gov/METS/}URIs" /&gt;
 *       &lt;anyAttribute processContents='lax' namespace='##other'/&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "areaType")
public class AreaType {

    @XmlAttribute(name = "ID")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlID
    @XmlSchemaType(name = "ID")
    protected String id;
    @XmlAttribute(name = "FILEID", required = true)
    @XmlIDREF
    @XmlSchemaType(name = "IDREF")
    protected Object fileid;
    @XmlAttribute(name = "SHAPE")
    protected String shape;
    @XmlAttribute(name = "COORDS")
    protected String coords;
    @XmlAttribute(name = "BEGIN")
    protected String begin;
    @XmlAttribute(name = "END")
    protected String end;
    @XmlAttribute(name = "BETYPE")
    protected String betype;
    @XmlAttribute(name = "EXTENT")
    protected String extent;
    @XmlAttribute(name = "EXTTYPE")
    protected String exttype;
    @XmlAttribute(name = "ADMID")
    @XmlIDREF
    @XmlSchemaType(name = "IDREFS")
    protected List<Object> admid;
    @XmlAttribute(name = "CONTENTIDS")
    protected List<String> contentids;
    @XmlAttribute(name = "ORDER")
    protected BigInteger order;
    @XmlAttribute(name = "ORDERLABEL")
    protected String orderlabel;
    @XmlAttribute(name = "LABEL")
    protected String label;
    @XmlAnyAttribute
    private Map<QName, String> otherAttributes = new HashMap<>();

    /**
     * Obtient la valeur de la propriété id.
     *
     * @return
     *         possible object is
     *         {@link String }
     *
     */
    public String getID() {
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
    public void setID(String value) {
        this.id = value;
    }

    /**
     * Obtient la valeur de la propriété fileid.
     *
     * @return
     *         possible object is
     *         {@link Object }
     *
     */
    public Object getFILEID() {
        return fileid;
    }

    /**
     * Définit la valeur de la propriété fileid.
     *
     * @param value
     *            allowed object is
     *            {@link Object }
     *
     */
    public void setFILEID(Object value) {
        this.fileid = value;
    }

    /**
     * Obtient la valeur de la propriété shape.
     *
     * @return
     *         possible object is
     *         {@link String }
     *
     */
    public String getSHAPE() {
        return shape;
    }

    /**
     * Définit la valeur de la propriété shape.
     *
     * @param value
     *            allowed object is
     *            {@link String }
     *
     */
    public void setSHAPE(String value) {
        this.shape = value;
    }

    /**
     * Obtient la valeur de la propriété coords.
     *
     * @return
     *         possible object is
     *         {@link String }
     *
     */
    public String getCOORDS() {
        return coords;
    }

    /**
     * Définit la valeur de la propriété coords.
     *
     * @param value
     *            allowed object is
     *            {@link String }
     *
     */
    public void setCOORDS(String value) {
        this.coords = value;
    }

    /**
     * Obtient la valeur de la propriété begin.
     *
     * @return
     *         possible object is
     *         {@link String }
     *
     */
    public String getBEGIN() {
        return begin;
    }

    /**
     * Définit la valeur de la propriété begin.
     *
     * @param value
     *            allowed object is
     *            {@link String }
     *
     */
    public void setBEGIN(String value) {
        this.begin = value;
    }

    /**
     * Obtient la valeur de la propriété end.
     *
     * @return
     *         possible object is
     *         {@link String }
     *
     */
    public String getEND() {
        return end;
    }

    /**
     * Définit la valeur de la propriété end.
     *
     * @param value
     *            allowed object is
     *            {@link String }
     *
     */
    public void setEND(String value) {
        this.end = value;
    }

    /**
     * Obtient la valeur de la propriété betype.
     *
     * @return
     *         possible object is
     *         {@link String }
     *
     */
    public String getBETYPE() {
        return betype;
    }

    /**
     * Définit la valeur de la propriété betype.
     *
     * @param value
     *            allowed object is
     *            {@link String }
     *
     */
    public void setBETYPE(String value) {
        this.betype = value;
    }

    /**
     * Obtient la valeur de la propriété extent.
     *
     * @return
     *         possible object is
     *         {@link String }
     *
     */
    public String getEXTENT() {
        return extent;
    }

    /**
     * Définit la valeur de la propriété extent.
     *
     * @param value
     *            allowed object is
     *            {@link String }
     *
     */
    public void setEXTENT(String value) {
        this.extent = value;
    }

    /**
     * Obtient la valeur de la propriété exttype.
     *
     * @return
     *         possible object is
     *         {@link String }
     *
     */
    public String getEXTTYPE() {
        return exttype;
    }

    /**
     * Définit la valeur de la propriété exttype.
     *
     * @param value
     *            allowed object is
     *            {@link String }
     *
     */
    public void setEXTTYPE(String value) {
        this.exttype = value;
    }

    /**
     * Gets the value of the admid property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the admid property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     *
     * <pre>
     * getADMID().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Object }
     *
     *
     */
    public List<Object> getADMID() {
        if (admid == null) {
            admid = new ArrayList<>();
        }
        return this.admid;
    }

    /**
     * Gets the value of the contentids property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the contentids property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     *
     * <pre>
     * getCONTENTIDS().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     *
     *
     */
    public List<String> getCONTENTIDS() {
        if (contentids == null) {
            contentids = new ArrayList<>();
        }
        return this.contentids;
    }

    /**
     * Obtient la valeur de la propriété order.
     *
     * @return
     *         possible object is
     *         {@link BigInteger }
     *
     */
    public BigInteger getORDER() {
        return order;
    }

    /**
     * Définit la valeur de la propriété order.
     *
     * @param value
     *            allowed object is
     *            {@link BigInteger }
     *
     */
    public void setORDER(BigInteger value) {
        this.order = value;
    }

    /**
     * Obtient la valeur de la propriété orderlabel.
     *
     * @return
     *         possible object is
     *         {@link String }
     *
     */
    public String getORDERLABEL() {
        return orderlabel;
    }

    /**
     * Définit la valeur de la propriété orderlabel.
     *
     * @param value
     *            allowed object is
     *            {@link String }
     *
     */
    public void setORDERLABEL(String value) {
        this.orderlabel = value;
    }

    /**
     * Obtient la valeur de la propriété label.
     *
     * @return
     *         possible object is
     *         {@link String }
     *
     */
    public String getLABEL() {
        return label;
    }

    /**
     * Définit la valeur de la propriété label.
     *
     * @param value
     *            allowed object is
     *            {@link String }
     *
     */
    public void setLABEL(String value) {
        this.label = value;
    }

    /**
     * Gets a map that contains attributes that aren't bound to any typed property on this class.
     *
     * <p>
     * the map is keyed by the name of the attribute and
     * the value is the string value of the attribute.
     *
     * the map returned by this method is live, and you can add new attribute
     * by updating the map directly. Because of this design, there's no setter.
     *
     *
     * @return
     *         always non-null
     */
    public Map<QName, String> getOtherAttributes() {
        return otherAttributes;
    }

}
