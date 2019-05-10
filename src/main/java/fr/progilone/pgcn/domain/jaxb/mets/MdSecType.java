//
// Ce fichier a été généré par l'implémentation de référence JavaTM Architecture for XML Binding (JAXB), v2.2.11
// Voir <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a>
// Toute modification apportée à ce fichier sera perdue lors de la recompilation du schéma source.
// Généré le : 2016.12.29 à 11:54:46 AM CET
//


package fr.progilone.pgcn.domain.jaxb.mets;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAnyAttribute;
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlID;
import javax.xml.bind.annotation.XmlIDREF;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import javax.xml.namespace.QName;
import org.w3c.dom.Element;


/**
 * mdSecType: Complex Type for Metadata Sections
 * 			A generic framework for pointing to/including metadata within a METS document, a la Warwick Framework.
 *
 *
 * <p>Classe Java pour mdSecType complex type.
 *
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 *
 * <pre>
 * &lt;complexType name="mdSecType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;all&gt;
 *         &lt;element name="mdRef" minOccurs="0"&gt;
 *           &lt;complexType&gt;
 *             &lt;complexContent&gt;
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *                 &lt;attGroup ref="{http://www.loc.gov/METS/}LOCATION"/&gt;
 *                 &lt;attGroup ref="{http://www.w3.org/1999/xlink}simpleLink"/&gt;
 *                 &lt;attGroup ref="{http://www.loc.gov/METS/}METADATA"/&gt;
 *                 &lt;attGroup ref="{http://www.loc.gov/METS/}FILECORE"/&gt;
 *                 &lt;attribute name="ID" type="{http://www.w3.org/2001/XMLSchema}ID" /&gt;
 *                 &lt;attribute name="LABEL" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *                 &lt;attribute name="XPTR" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *               &lt;/restriction&gt;
 *             &lt;/complexContent&gt;
 *           &lt;/complexType&gt;
 *         &lt;/element&gt;
 *         &lt;element name="mdWrap" minOccurs="0"&gt;
 *           &lt;complexType&gt;
 *             &lt;complexContent&gt;
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *                 &lt;choice&gt;
 *                   &lt;element name="binData" type="{http://www.w3.org/2001/XMLSchema}base64Binary" minOccurs="0"/&gt;
 *                   &lt;element name="xmlData" minOccurs="0"&gt;
 *                     &lt;complexType&gt;
 *                       &lt;complexContent&gt;
 *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *                           &lt;sequence&gt;
 *                             &lt;any processContents='lax' maxOccurs="unbounded"/&gt;
 *                           &lt;/sequence&gt;
 *                         &lt;/restriction&gt;
 *                       &lt;/complexContent&gt;
 *                     &lt;/complexType&gt;
 *                   &lt;/element&gt;
 *                 &lt;/choice&gt;
 *                 &lt;attGroup ref="{http://www.loc.gov/METS/}FILECORE"/&gt;
 *                 &lt;attGroup ref="{http://www.loc.gov/METS/}METADATA"/&gt;
 *                 &lt;attribute name="ID" type="{http://www.w3.org/2001/XMLSchema}ID" /&gt;
 *                 &lt;attribute name="LABEL" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *               &lt;/restriction&gt;
 *             &lt;/complexContent&gt;
 *           &lt;/complexType&gt;
 *         &lt;/element&gt;
 *       &lt;/all&gt;
 *       &lt;attribute name="ID" use="required" type="{http://www.w3.org/2001/XMLSchema}ID" /&gt;
 *       &lt;attribute name="GROUPID" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *       &lt;attribute name="ADMID" type="{http://www.w3.org/2001/XMLSchema}IDREFS" /&gt;
 *       &lt;attribute name="CREATED" type="{http://www.w3.org/2001/XMLSchema}dateTime" /&gt;
 *       &lt;attribute name="STATUS" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *       &lt;anyAttribute processContents='lax' namespace='##other'/&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "mdSecType", propOrder = {

})
public class MdSecType {

    protected MdRef mdRef;
    protected MdWrap mdWrap;
    @XmlAttribute(name = "ID", required = true)
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlID
    @XmlSchemaType(name = "ID")
    protected String id;
    @XmlAttribute(name = "GROUPID")
    protected String groupid;
    @XmlAttribute(name = "ADMID")
    @XmlIDREF
    @XmlSchemaType(name = "IDREFS")
    protected List<Object> admid;
    @XmlAttribute(name = "CREATED")
    @XmlSchemaType(name = "dateTime")
    protected LocalDateTime created;
    @XmlAttribute(name = "STATUS")
    protected String status;
    @XmlAnyAttribute
    private Map<QName, String> otherAttributes = new HashMap<>();

    /**
     * Obtient la valeur de la propriété mdRef.
     *
     * @return
     *     possible object is
     *     {@link MdRef }
     *
     */
    public MdRef getMdRef() {
        return mdRef;
    }

    /**
     * Définit la valeur de la propriété mdRef.
     *
     * @param value
     *     allowed object is
     *     {@link MdRef }
     *
     */
    public void setMdRef(MdRef value) {
        this.mdRef = value;
    }

    /**
     * Obtient la valeur de la propriété mdWrap.
     *
     * @return
     *     possible object is
     *     {@link MdWrap }
     *
     */
    public MdWrap getMdWrap() {
        return mdWrap;
    }

    /**
     * Définit la valeur de la propriété mdWrap.
     *
     * @param value
     *     allowed object is
     *     {@link MdWrap }
     *
     */
    public void setMdWrap(MdWrap value) {
        this.mdWrap = value;
    }

    /**
     * Obtient la valeur de la propriété id.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getID() {
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
    public void setID(String value) {
        this.id = value;
    }

    /**
     * Obtient la valeur de la propriété groupid.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getGROUPID() {
        return groupid;
    }

    /**
     * Définit la valeur de la propriété groupid.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setGROUPID(String value) {
        this.groupid = value;
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
     * <pre>
     *    getADMID().add(newItem);
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
     * Obtient la valeur de la propriété created.
     *
     * @return
     *     possible object is
     *     {@link LocalDateTime }
     *
     */
    public LocalDateTime getCREATED() {
        return created;
    }

    /**
     * Définit la valeur de la propriété created.
     *
     * @param value
     *     allowed object is
     *     {@link LocalDateTime }
     *
     */
    public void setCREATED(LocalDateTime value) {
        this.created = value;
    }

    /**
     * Obtient la valeur de la propriété status.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getSTATUS() {
        return status;
    }

    /**
     * Définit la valeur de la propriété status.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setSTATUS(String value) {
        this.status = value;
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
     *     always non-null
     */
    public Map<QName, String> getOtherAttributes() {
        return otherAttributes;
    }


    /**
     * <p>Classe Java pour anonymous complex type.
     *
     * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
     *
     * <pre>
     * &lt;complexType&gt;
     *   &lt;complexContent&gt;
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
     *       &lt;attGroup ref="{http://www.loc.gov/METS/}LOCATION"/&gt;
     *       &lt;attGroup ref="{http://www.w3.org/1999/xlink}simpleLink"/&gt;
     *       &lt;attGroup ref="{http://www.loc.gov/METS/}METADATA"/&gt;
     *       &lt;attGroup ref="{http://www.loc.gov/METS/}FILECORE"/&gt;
     *       &lt;attribute name="ID" type="{http://www.w3.org/2001/XMLSchema}ID" /&gt;
     *       &lt;attribute name="LABEL" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
     *       &lt;attribute name="XPTR" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
     *     &lt;/restriction&gt;
     *   &lt;/complexContent&gt;
     * &lt;/complexType&gt;
     * </pre>
     *
     *
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "")
    public static class MdRef {

        @XmlAttribute(name = "ID")
        @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
        @XmlID
        @XmlSchemaType(name = "ID")
        protected String id;
        @XmlAttribute(name = "LABEL")
        protected String label;
        @XmlAttribute(name = "XPTR")
        protected String xptr;
        @XmlAttribute(name = "LOCTYPE", required = true)
        protected String loctype;
        @XmlAttribute(name = "OTHERLOCTYPE")
        protected String otherloctype;
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
        @XmlAttribute(name = "MDTYPE", required = true)
        protected String mdtype;
        @XmlAttribute(name = "OTHERMDTYPE")
        protected String othermdtype;
        @XmlAttribute(name = "MDTYPEVERSION")
        protected String mdtypeversion;
        @XmlAttribute(name = "MIMETYPE")
        protected String mimetype;
        @XmlAttribute(name = "SIZE")
        protected Long size;
        @XmlAttribute(name = "CREATED")
        @XmlSchemaType(name = "dateTime")
        protected LocalDateTime created;
        @XmlAttribute(name = "CHECKSUM")
        protected String checksum;
        @XmlAttribute(name = "CHECKSUMTYPE")
        protected String checksumtype;

        /**
         * Obtient la valeur de la propriété id.
         *
         * @return
         *     possible object is
         *     {@link String }
         *
         */
        public String getID() {
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
        public void setID(String value) {
            this.id = value;
        }

        /**
         * Obtient la valeur de la propriété label.
         *
         * @return
         *     possible object is
         *     {@link String }
         *
         */
        public String getLABEL() {
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
        public void setLABEL(String value) {
            this.label = value;
        }

        /**
         * Obtient la valeur de la propriété xptr.
         *
         * @return
         *     possible object is
         *     {@link String }
         *
         */
        public String getXPTR() {
            return xptr;
        }

        /**
         * Définit la valeur de la propriété xptr.
         *
         * @param value
         *     allowed object is
         *     {@link String }
         *
         */
        public void setXPTR(String value) {
            this.xptr = value;
        }

        /**
         * Obtient la valeur de la propriété loctype.
         *
         * @return
         *     possible object is
         *     {@link String }
         *
         */
        public String getLOCTYPE() {
            return loctype;
        }

        /**
         * Définit la valeur de la propriété loctype.
         *
         * @param value
         *     allowed object is
         *     {@link String }
         *
         */
        public void setLOCTYPE(String value) {
            this.loctype = value;
        }

        /**
         * Obtient la valeur de la propriété otherloctype.
         *
         * @return
         *     possible object is
         *     {@link String }
         *
         */
        public String getOTHERLOCTYPE() {
            return otherloctype;
        }

        /**
         * Définit la valeur de la propriété otherloctype.
         *
         * @param value
         *     allowed object is
         *     {@link String }
         *
         */
        public void setOTHERLOCTYPE(String value) {
            this.otherloctype = value;
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
         *     allowed object is
         *     {@link String }
         *
         */
        public void setType(String value) {
            this.type = value;
        }

        /**
         * Obtient la valeur de la propriété href.
         *
         * @return
         *     possible object is
         *     {@link String }
         *
         */
        public String getHref() {
            return href;
        }

        /**
         * Définit la valeur de la propriété href.
         *
         * @param value
         *     allowed object is
         *     {@link String }
         *
         */
        public void setHref(String value) {
            this.href = value;
        }

        /**
         * Obtient la valeur de la propriété role.
         *
         * @return
         *     possible object is
         *     {@link String }
         *
         */
        public String getRole() {
            return role;
        }

        /**
         * Définit la valeur de la propriété role.
         *
         * @param value
         *     allowed object is
         *     {@link String }
         *
         */
        public void setRole(String value) {
            this.role = value;
        }

        /**
         * Obtient la valeur de la propriété arcrole.
         *
         * @return
         *     possible object is
         *     {@link String }
         *
         */
        public String getArcrole() {
            return arcrole;
        }

        /**
         * Définit la valeur de la propriété arcrole.
         *
         * @param value
         *     allowed object is
         *     {@link String }
         *
         */
        public void setArcrole(String value) {
            this.arcrole = value;
        }

        /**
         * Obtient la valeur de la propriété title.
         *
         * @return
         *     possible object is
         *     {@link String }
         *
         */
        public String getTitle() {
            return title;
        }

        /**
         * Définit la valeur de la propriété title.
         *
         * @param value
         *     allowed object is
         *     {@link String }
         *
         */
        public void setTitle(String value) {
            this.title = value;
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
         * Obtient la valeur de la propriété mdtype.
         *
         * @return
         *     possible object is
         *     {@link String }
         *
         */
        public String getMDTYPE() {
            return mdtype;
        }

        /**
         * Définit la valeur de la propriété mdtype.
         *
         * @param value
         *     allowed object is
         *     {@link String }
         *
         */
        public void setMDTYPE(String value) {
            this.mdtype = value;
        }

        /**
         * Obtient la valeur de la propriété othermdtype.
         *
         * @return
         *     possible object is
         *     {@link String }
         *
         */
        public String getOTHERMDTYPE() {
            return othermdtype;
        }

        /**
         * Définit la valeur de la propriété othermdtype.
         *
         * @param value
         *     allowed object is
         *     {@link String }
         *
         */
        public void setOTHERMDTYPE(String value) {
            this.othermdtype = value;
        }

        /**
         * Obtient la valeur de la propriété mdtypeversion.
         *
         * @return
         *     possible object is
         *     {@link String }
         *
         */
        public String getMDTYPEVERSION() {
            return mdtypeversion;
        }

        /**
         * Définit la valeur de la propriété mdtypeversion.
         *
         * @param value
         *     allowed object is
         *     {@link String }
         *
         */
        public void setMDTYPEVERSION(String value) {
            this.mdtypeversion = value;
        }

        /**
         * Obtient la valeur de la propriété mimetype.
         *
         * @return
         *     possible object is
         *     {@link String }
         *
         */
        public String getMIMETYPE() {
            return mimetype;
        }

        /**
         * Définit la valeur de la propriété mimetype.
         *
         * @param value
         *     allowed object is
         *     {@link String }
         *
         */
        public void setMIMETYPE(String value) {
            this.mimetype = value;
        }

        /**
         * Obtient la valeur de la propriété size.
         *
         * @return
         *     possible object is
         *     {@link Long }
         *
         */
        public Long getSIZE() {
            return size;
        }

        /**
         * Définit la valeur de la propriété size.
         *
         * @param value
         *     allowed object is
         *     {@link Long }
         *
         */
        public void setSIZE(Long value) {
            this.size = value;
        }

        /**
         * Obtient la valeur de la propriété created.
         *
         * @return
         *     possible object is
         *     {@link LocalDateTime }
         *
         */
        public LocalDateTime getCREATED() {
            return created;
        }

        /**
         * Définit la valeur de la propriété created.
         *
         * @param value
         *     allowed object is
         *     {@link LocalDateTime }
         *
         */
        public void setCREATED(LocalDateTime value) {
            this.created = value;
        }

        /**
         * Obtient la valeur de la propriété checksum.
         *
         * @return
         *     possible object is
         *     {@link String }
         *
         */
        public String getCHECKSUM() {
            return checksum;
        }

        /**
         * Définit la valeur de la propriété checksum.
         *
         * @param value
         *     allowed object is
         *     {@link String }
         *
         */
        public void setCHECKSUM(String value) {
            this.checksum = value;
        }

        /**
         * Obtient la valeur de la propriété checksumtype.
         *
         * @return
         *     possible object is
         *     {@link String }
         *
         */
        public String getCHECKSUMTYPE() {
            return checksumtype;
        }

        /**
         * Définit la valeur de la propriété checksumtype.
         *
         * @param value
         *     allowed object is
         *     {@link String }
         *
         */
        public void setCHECKSUMTYPE(String value) {
            this.checksumtype = value;
        }

    }


    /**
     * <p>Classe Java pour anonymous complex type.
     *
     * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
     *
     * <pre>
     * &lt;complexType&gt;
     *   &lt;complexContent&gt;
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
     *       &lt;choice&gt;
     *         &lt;element name="binData" type="{http://www.w3.org/2001/XMLSchema}base64Binary" minOccurs="0"/&gt;
     *         &lt;element name="xmlData" minOccurs="0"&gt;
     *           &lt;complexType&gt;
     *             &lt;complexContent&gt;
     *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
     *                 &lt;sequence&gt;
     *                   &lt;any processContents='lax' maxOccurs="unbounded"/&gt;
     *                 &lt;/sequence&gt;
     *               &lt;/restriction&gt;
     *             &lt;/complexContent&gt;
     *           &lt;/complexType&gt;
     *         &lt;/element&gt;
     *       &lt;/choice&gt;
     *       &lt;attGroup ref="{http://www.loc.gov/METS/}FILECORE"/&gt;
     *       &lt;attGroup ref="{http://www.loc.gov/METS/}METADATA"/&gt;
     *       &lt;attribute name="ID" type="{http://www.w3.org/2001/XMLSchema}ID" /&gt;
     *       &lt;attribute name="LABEL" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
     *     &lt;/restriction&gt;
     *   &lt;/complexContent&gt;
     * &lt;/complexType&gt;
     * </pre>
     *
     *
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "binData",
        "xmlData"
    })
    public static class MdWrap {

        protected byte[] binData;
        protected XmlData xmlData;
        @XmlAttribute(name = "ID")
        @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
        @XmlID
        @XmlSchemaType(name = "ID")
        protected String id;
        @XmlAttribute(name = "LABEL")
        protected String label;
        @XmlAttribute(name = "MIMETYPE")
        protected String mimetype;
        @XmlAttribute(name = "SIZE")
        protected Long size;
        @XmlAttribute(name = "CREATED")
        @XmlSchemaType(name = "dateTime")
        protected LocalDateTime created;
        @XmlAttribute(name = "CHECKSUM")
        protected String checksum;
        @XmlAttribute(name = "CHECKSUMTYPE")
        protected String checksumtype;
        @XmlAttribute(name = "MDTYPE", required = true)
        protected String mdtype;
        @XmlAttribute(name = "OTHERMDTYPE")
        protected String othermdtype;
        @XmlAttribute(name = "MDTYPEVERSION")
        protected String mdtypeversion;

        /**
         * Obtient la valeur de la propriété binData.
         *
         * @return
         *     possible object is
         *     byte[]
         */
        public byte[] getBinData() {
            return binData;
        }

        /**
         * Définit la valeur de la propriété binData.
         *
         * @param value
         *     allowed object is
         *     byte[]
         */
        public void setBinData(byte[] value) {
            this.binData = value;
        }

        /**
         * Obtient la valeur de la propriété xmlData.
         *
         * @return
         *     possible object is
         *     {@link XmlData }
         *
         */
        public XmlData getXmlData() {
            return xmlData;
        }

        /**
         * Définit la valeur de la propriété xmlData.
         *
         * @param value
         *     allowed object is
         *     {@link XmlData }
         *
         */
        public void setXmlData(XmlData value) {
            this.xmlData = value;
        }

        /**
         * Obtient la valeur de la propriété id.
         *
         * @return
         *     possible object is
         *     {@link String }
         *
         */
        public String getID() {
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
        public void setID(String value) {
            this.id = value;
        }

        /**
         * Obtient la valeur de la propriété label.
         *
         * @return
         *     possible object is
         *     {@link String }
         *
         */
        public String getLABEL() {
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
        public void setLABEL(String value) {
            this.label = value;
        }

        /**
         * Obtient la valeur de la propriété mimetype.
         *
         * @return
         *     possible object is
         *     {@link String }
         *
         */
        public String getMIMETYPE() {
            return mimetype;
        }

        /**
         * Définit la valeur de la propriété mimetype.
         *
         * @param value
         *     allowed object is
         *     {@link String }
         *
         */
        public void setMIMETYPE(String value) {
            this.mimetype = value;
        }

        /**
         * Obtient la valeur de la propriété size.
         *
         * @return
         *     possible object is
         *     {@link Long }
         *
         */
        public Long getSIZE() {
            return size;
        }

        /**
         * Définit la valeur de la propriété size.
         *
         * @param value
         *     allowed object is
         *     {@link Long }
         *
         */
        public void setSIZE(Long value) {
            this.size = value;
        }

        /**
         * Obtient la valeur de la propriété created.
         *
         * @return
         *     possible object is
         *     {@link LocalDateTime }
         *
         */
        public LocalDateTime getCREATED() {
            return created;
        }

        /**
         * Définit la valeur de la propriété created.
         *
         * @param value
         *     allowed object is
         *     {@link LocalDateTime }
         *
         */
        public void setCREATED(LocalDateTime value) {
            this.created = value;
        }

        /**
         * Obtient la valeur de la propriété checksum.
         *
         * @return
         *     possible object is
         *     {@link String }
         *
         */
        public String getCHECKSUM() {
            return checksum;
        }

        /**
         * Définit la valeur de la propriété checksum.
         *
         * @param value
         *     allowed object is
         *     {@link String }
         *
         */
        public void setCHECKSUM(String value) {
            this.checksum = value;
        }

        /**
         * Obtient la valeur de la propriété checksumtype.
         *
         * @return
         *     possible object is
         *     {@link String }
         *
         */
        public String getCHECKSUMTYPE() {
            return checksumtype;
        }

        /**
         * Définit la valeur de la propriété checksumtype.
         *
         * @param value
         *     allowed object is
         *     {@link String }
         *
         */
        public void setCHECKSUMTYPE(String value) {
            this.checksumtype = value;
        }

        /**
         * Obtient la valeur de la propriété mdtype.
         *
         * @return
         *     possible object is
         *     {@link String }
         *
         */
        public String getMDTYPE() {
            return mdtype;
        }

        /**
         * Définit la valeur de la propriété mdtype.
         *
         * @param value
         *     allowed object is
         *     {@link String }
         *
         */
        public void setMDTYPE(String value) {
            this.mdtype = value;
        }

        /**
         * Obtient la valeur de la propriété othermdtype.
         *
         * @return
         *     possible object is
         *     {@link String }
         *
         */
        public String getOTHERMDTYPE() {
            return othermdtype;
        }

        /**
         * Définit la valeur de la propriété othermdtype.
         *
         * @param value
         *     allowed object is
         *     {@link String }
         *
         */
        public void setOTHERMDTYPE(String value) {
            this.othermdtype = value;
        }

        /**
         * Obtient la valeur de la propriété mdtypeversion.
         *
         * @return
         *     possible object is
         *     {@link String }
         *
         */
        public String getMDTYPEVERSION() {
            return mdtypeversion;
        }

        /**
         * Définit la valeur de la propriété mdtypeversion.
         *
         * @param value
         *     allowed object is
         *     {@link String }
         *
         */
        public void setMDTYPEVERSION(String value) {
            this.mdtypeversion = value;
        }


        /**
         * <p>Classe Java pour anonymous complex type.
         *
         * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
         *
         * <pre>
         * &lt;complexType&gt;
         *   &lt;complexContent&gt;
         *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
         *       &lt;sequence&gt;
         *         &lt;any processContents='lax' maxOccurs="unbounded"/&gt;
         *       &lt;/sequence&gt;
         *     &lt;/restriction&gt;
         *   &lt;/complexContent&gt;
         * &lt;/complexType&gt;
         * </pre>
         *
         *
         */
        @XmlAccessorType(XmlAccessType.FIELD)
        @XmlType(name = "", propOrder = {
            "any"
        })
        public static class XmlData {

            @XmlAnyElement(lax = true)
            protected List<Object> any;

            /**
             * Gets the value of the any property.
             *
             * <p>
             * This accessor method returns a reference to the live list,
             * not a snapshot. Therefore any modification you make to the
             * returned list will be present inside the JAXB object.
             * This is why there is not a <CODE>set</CODE> method for the any property.
             *
             * <p>
             * For example, to add a new item, do as follows:
             * <pre>
             *    getAny().add(newItem);
             * </pre>
             *
             *
             * <p>
             * Objects of the following type(s) are allowed in the list
             * {@link Object }
             * {@link Element }
             *
             *
             */
            public List<Object> getAny() {
                if (any == null) {
                    any = new ArrayList<>();
                }
                return this.any;
            }

        }

    }

}
