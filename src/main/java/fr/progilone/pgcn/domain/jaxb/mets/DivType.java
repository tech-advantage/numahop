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
 * divType: Complex Type for Divisions
 * The METS standard represents a document structurally as a series of nested div elements, that is, as a hierarchy (e.g., a book, which is composed
 * of chapters, which are composed of subchapters, which are composed of text). Every div node in the structural map hierarchy may be connected (via
 * subsidiary mptr or fptr elements) to content files which represent that div's portion of the whole document.
 *
 * SPECIAL NOTE REGARDING DIV ATTRIBUTE VALUES:
 * to clarify the differences between the ORDER, ORDERLABEL, and LABEL attributes for the <div> element, imagine a text with 10 roman numbered pages
 * followed by 10 arabic numbered pages. Page iii would have an ORDER of "3", an ORDERLABEL of "iii" and a LABEL of "Page iii", while page 3 would
 * have an ORDER of "13", an ORDERLABEL of "3" and a LABEL of "Page 3".
 *
 *
 * <p>
 * Classe Java pour divType complex type.
 *
 * <p>
 * Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 *
 * <pre>
 * &lt;complexType name="divType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="mptr" maxOccurs="unbounded" minOccurs="0"&gt;
 *           &lt;complexType&gt;
 *             &lt;complexContent&gt;
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *                 &lt;attGroup ref="{http://www.w3.org/1999/xlink}simpleLink"/&gt;
 *                 &lt;attGroup ref="{http://www.loc.gov/METS/}LOCATION"/&gt;
 *                 &lt;attribute name="ID" type="{http://www.w3.org/2001/XMLSchema}ID" /&gt;
 *                 &lt;attribute name="CONTENTIDS" type="{http://www.loc.gov/METS/}URIs" /&gt;
 *               &lt;/restriction&gt;
 *             &lt;/complexContent&gt;
 *           &lt;/complexType&gt;
 *         &lt;/element&gt;
 *         &lt;element name="fptr" maxOccurs="unbounded" minOccurs="0"&gt;
 *           &lt;complexType&gt;
 *             &lt;complexContent&gt;
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *                 &lt;choice&gt;
 *                   &lt;element name="par" type="{http://www.loc.gov/METS/}parType" minOccurs="0"/&gt;
 *                   &lt;element name="seq" type="{http://www.loc.gov/METS/}seqType" minOccurs="0"/&gt;
 *                   &lt;element name="area" type="{http://www.loc.gov/METS/}areaType" minOccurs="0"/&gt;
 *                 &lt;/choice&gt;
 *                 &lt;attribute name="ID" type="{http://www.w3.org/2001/XMLSchema}ID" /&gt;
 *                 &lt;attribute name="FILEID" type="{http://www.w3.org/2001/XMLSchema}IDREF" /&gt;
 *                 &lt;attribute name="CONTENTIDS" type="{http://www.loc.gov/METS/}URIs" /&gt;
 *                 &lt;anyAttribute processContents='lax' namespace='##other'/&gt;
 *               &lt;/restriction&gt;
 *             &lt;/complexContent&gt;
 *           &lt;/complexType&gt;
 *         &lt;/element&gt;
 *         &lt;element name="div" type="{http://www.loc.gov/METS/}divType" maxOccurs="unbounded" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *       &lt;attGroup ref="{http://www.loc.gov/METS/}ORDERLABELS"/&gt;
 *       &lt;attribute name="ID" type="{http://www.w3.org/2001/XMLSchema}ID" /&gt;
 *       &lt;attribute name="DMDID" type="{http://www.w3.org/2001/XMLSchema}IDREFS" /&gt;
 *       &lt;attribute name="ADMID" type="{http://www.w3.org/2001/XMLSchema}IDREFS" /&gt;
 *       &lt;attribute name="TYPE" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *       &lt;attribute name="CONTENTIDS" type="{http://www.loc.gov/METS/}URIs" /&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "divType",
         propOrder = {"mptr",
                      "fptr",
                      "div"})
public class DivType {

    protected List<DivType.Mptr> mptr;
    protected List<DivType.Fptr> fptr;
    protected List<DivType> div;
    @XmlAttribute(name = "ID")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlID
    @XmlSchemaType(name = "ID")
    protected String id;
    @XmlAttribute(name = "DMDID")
    @XmlIDREF
    @XmlSchemaType(name = "IDREFS")
    protected List<Object> dmdid;
    @XmlAttribute(name = "ADMID")
    @XmlIDREF
    @XmlSchemaType(name = "IDREFS")
    protected List<Object> admid;
    @XmlAttribute(name = "TYPE")
    protected String type;
    @XmlAttribute(name = "CONTENTIDS")
    protected List<String> contentids;
    @XmlAttribute(name = "ORDER")
    protected BigInteger order;
    @XmlAttribute(name = "ORDERLABEL")
    protected String orderlabel;
    @XmlAttribute(name = "LABEL")
    protected String label;
    @XmlAttribute(name = "label", namespace = "http://www.w3.org/1999/xlink")
    protected String xLabel;

    /**
     * Gets the value of the mptr property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the mptr property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     *
     * <pre>
     * getMptr().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link DivType.Mptr }
     *
     *
     */
    public List<DivType.Mptr> getMptr() {
        if (mptr == null) {
            mptr = new ArrayList<>();
        }
        return this.mptr;
    }

    /**
     * Gets the value of the fptr property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the fptr property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     *
     * <pre>
     * getFptr().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link DivType.Fptr }
     *
     *
     */
    public List<DivType.Fptr> getFptr() {
        if (fptr == null) {
            fptr = new ArrayList<>();
        }
        return this.fptr;
    }

    /**
     * Gets the value of the div property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the div property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     *
     * <pre>
     * getDiv().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link DivType }
     *
     *
     */
    public List<DivType> getDiv() {
        if (div == null) {
            div = new ArrayList<>();
        }
        return this.div;
    }

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
     * Gets the value of the dmdid property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the dmdid property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     *
     * <pre>
     * getDMDID().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Object }
     *
     *
     */
    public List<Object> getDMDID() {
        if (dmdid == null) {
            dmdid = new ArrayList<>();
        }
        return this.dmdid;
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
     * Obtient la valeur de la propriété type.
     *
     * @return
     *         possible object is
     *         {@link String }
     *
     */
    public String getTYPE() {
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
    public void setTYPE(String value) {
        this.type = value;
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

    public String getXLabel() {
        return xLabel;
    }

    public void setXLabel(final String xLabel) {
        this.xLabel = xLabel;
    }

    /**
     * <p>
     * Classe Java pour anonymous complex type.
     *
     * <p>
     * Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
     *
     * <pre>
     * &lt;complexType&gt;
     *   &lt;complexContent&gt;
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
     *       &lt;choice&gt;
     *         &lt;element name="par" type="{http://www.loc.gov/METS/}parType" minOccurs="0"/&gt;
     *         &lt;element name="seq" type="{http://www.loc.gov/METS/}seqType" minOccurs="0"/&gt;
     *         &lt;element name="area" type="{http://www.loc.gov/METS/}areaType" minOccurs="0"/&gt;
     *       &lt;/choice&gt;
     *       &lt;attribute name="ID" type="{http://www.w3.org/2001/XMLSchema}ID" /&gt;
     *       &lt;attribute name="FILEID" type="{http://www.w3.org/2001/XMLSchema}IDREF" /&gt;
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
    @XmlType(name = "",
             propOrder = {"par",
                          "seq",
                          "area"})
    public static class Fptr {

        protected ParType par;
        protected SeqType seq;
        protected AreaType area;
        @XmlAttribute(name = "ID")
        @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
        @XmlID
        @XmlSchemaType(name = "ID")
        protected String id;
        @XmlAttribute(name = "FILEID")
        @XmlIDREF
        @XmlSchemaType(name = "IDREF")
        protected Object fileid;
        @XmlAttribute(name = "CONTENTIDS")
        protected List<String> contentids;
        @XmlAnyAttribute
        private Map<QName, String> otherAttributes = new HashMap<>();

        /**
         * Obtient la valeur de la propriété par.
         *
         * @return
         *         possible object is
         *         {@link ParType }
         *
         */
        public ParType getPar() {
            return par;
        }

        /**
         * Définit la valeur de la propriété par.
         *
         * @param value
         *            allowed object is
         *            {@link ParType }
         *
         */
        public void setPar(ParType value) {
            this.par = value;
        }

        /**
         * Obtient la valeur de la propriété seq.
         *
         * @return
         *         possible object is
         *         {@link SeqType }
         *
         */
        public SeqType getSeq() {
            return seq;
        }

        /**
         * Définit la valeur de la propriété seq.
         *
         * @param value
         *            allowed object is
         *            {@link SeqType }
         *
         */
        public void setSeq(SeqType value) {
            this.seq = value;
        }

        /**
         * Obtient la valeur de la propriété area.
         *
         * @return
         *         possible object is
         *         {@link AreaType }
         *
         */
        public AreaType getArea() {
            return area;
        }

        /**
         * Définit la valeur de la propriété area.
         *
         * @param value
         *            allowed object is
         *            {@link AreaType }
         *
         */
        public void setArea(AreaType value) {
            this.area = value;
        }

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

    /**
     * <p>
     * Classe Java pour anonymous complex type.
     *
     * <p>
     * Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
     *
     * <pre>
     * &lt;complexType&gt;
     *   &lt;complexContent&gt;
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
     *       &lt;attGroup ref="{http://www.w3.org/1999/xlink}simpleLink"/&gt;
     *       &lt;attGroup ref="{http://www.loc.gov/METS/}LOCATION"/&gt;
     *       &lt;attribute name="ID" type="{http://www.w3.org/2001/XMLSchema}ID" /&gt;
     *       &lt;attribute name="CONTENTIDS" type="{http://www.loc.gov/METS/}URIs" /&gt;
     *     &lt;/restriction&gt;
     *   &lt;/complexContent&gt;
     * &lt;/complexType&gt;
     * </pre>
     *
     *
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "")
    public static class Mptr {

        @XmlAttribute(name = "ID")
        @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
        @XmlID
        @XmlSchemaType(name = "ID")
        protected String id;
        @XmlAttribute(name = "CONTENTIDS")
        protected List<String> contentids;
        @XmlAttribute(name = "type", namespace = "http://www.w3.org/1999/xlink")
        protected String type;
        @XmlAttribute(name = "href", namespace = "http://www.w3.org/1999/xlink")
        @XmlSchemaType(name = "anyURI")
        protected String href;
        @XmlAttribute(name = "role", namespace = "http://www.w3.org/1999/xlink")
        protected String role;
        @XmlAttribute(name = "arcrole", namespace = "http://www.w3.org/1999/xlink")
        protected String arcrole;
        @XmlAttribute(name = "title", namespace = "http://www.w3.org/1999/xlink")
        protected String title;
        @XmlAttribute(name = "show", namespace = "http://www.w3.org/1999/xlink")
        protected String show;
        @XmlAttribute(name = "actuate", namespace = "http://www.w3.org/1999/xlink")
        protected String actuate;
        @XmlAttribute(name = "LOCTYPE", required = true)
        protected String loctype;
        @XmlAttribute(name = "OTHERLOCTYPE")
        protected String otherloctype;

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
         * Obtient la valeur de la propriété type.
         *
         * @return
         *         possible object is
         *         {@link String }
         *
         */
        public String getType() {
            if (type == null) {
                return "simple";
            } else {
                return type;
            }
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
         * Obtient la valeur de la propriété href.
         *
         * @return
         *         possible object is
         *         {@link String }
         *
         */
        public String getHref() {
            return href;
        }

        /**
         * Définit la valeur de la propriété href.
         *
         * @param value
         *            allowed object is
         *            {@link String }
         *
         */
        public void setHref(String value) {
            this.href = value;
        }

        /**
         * Obtient la valeur de la propriété role.
         *
         * @return
         *         possible object is
         *         {@link String }
         *
         */
        public String getRole() {
            return role;
        }

        /**
         * Définit la valeur de la propriété role.
         *
         * @param value
         *            allowed object is
         *            {@link String }
         *
         */
        public void setRole(String value) {
            this.role = value;
        }

        /**
         * Obtient la valeur de la propriété arcrole.
         *
         * @return
         *         possible object is
         *         {@link String }
         *
         */
        public String getArcrole() {
            return arcrole;
        }

        /**
         * Définit la valeur de la propriété arcrole.
         *
         * @param value
         *            allowed object is
         *            {@link String }
         *
         */
        public void setArcrole(String value) {
            this.arcrole = value;
        }

        /**
         * Obtient la valeur de la propriété title.
         *
         * @return
         *         possible object is
         *         {@link String }
         *
         */
        public String getTitle() {
            return title;
        }

        /**
         * Définit la valeur de la propriété title.
         *
         * @param value
         *            allowed object is
         *            {@link String }
         *
         */
        public void setTitle(String value) {
            this.title = value;
        }

        /**
         * Obtient la valeur de la propriété show.
         *
         * @return
         *         possible object is
         *         {@link String }
         *
         */
        public String getShow() {
            return show;
        }

        /**
         * Définit la valeur de la propriété show.
         *
         * @param value
         *            allowed object is
         *            {@link String }
         *
         */
        public void setShow(String value) {
            this.show = value;
        }

        /**
         * Obtient la valeur de la propriété actuate.
         *
         * @return
         *         possible object is
         *         {@link String }
         *
         */
        public String getActuate() {
            return actuate;
        }

        /**
         * Définit la valeur de la propriété actuate.
         *
         * @param value
         *            allowed object is
         *            {@link String }
         *
         */
        public void setActuate(String value) {
            this.actuate = value;
        }

        /**
         * Obtient la valeur de la propriété loctype.
         *
         * @return
         *         possible object is
         *         {@link String }
         *
         */
        public String getLOCTYPE() {
            return loctype;
        }

        /**
         * Définit la valeur de la propriété loctype.
         *
         * @param value
         *            allowed object is
         *            {@link String }
         *
         */
        public void setLOCTYPE(String value) {
            this.loctype = value;
        }

        /**
         * Obtient la valeur de la propriété otherloctype.
         *
         * @return
         *         possible object is
         *         {@link String }
         *
         */
        public String getOTHERLOCTYPE() {
            return otherloctype;
        }

        /**
         * Définit la valeur de la propriété otherloctype.
         *
         * @param value
         *            allowed object is
         *            {@link String }
         *
         */
        public void setOTHERLOCTYPE(String value) {
            this.otherloctype = value;
        }

    }

}
