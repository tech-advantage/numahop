//
// Ce fichier a été généré par l'implémentation de référence JavaTM Architecture for XML Binding (JAXB), v2.2.11
// Voir <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a>
// Toute modification apportée à ce fichier sera perdue lors de la recompilation du schéma source.
// Généré le : 2016.12.29 à 11:54:46 AM CET
//


package fr.progilone.pgcn.domain.jaxb.mets;

import java.math.BigInteger;
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
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlID;
import javax.xml.bind.annotation.XmlIDREF;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import javax.xml.namespace.QName;
import org.w3c.dom.Element;


/**
 * fileType: Complex Type for Files
 * 				The file element provides access to content files for a METS object.  A file element may contain one or more FLocat elements, which provide pointers to a content file, and/or an FContent element, which wraps an encoded version of the file. Note that ALL FLocat and FContent elements underneath a single file element should identify/contain identical copies of a single file.
 *
 *
 * <p>Classe Java pour fileType complex type.
 *
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 *
 * <pre>
 * &lt;complexType name="fileType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="FLocat" maxOccurs="unbounded" minOccurs="0"&gt;
 *           &lt;complexType&gt;
 *             &lt;complexContent&gt;
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *                 &lt;attGroup ref="{http://www.loc.gov/METS/}LOCATION"/&gt;
 *                 &lt;attGroup ref="{http://www.w3.org/1999/xlink}simpleLink"/&gt;
 *                 &lt;attribute name="ID" type="{http://www.w3.org/2001/XMLSchema}ID" /&gt;
 *                 &lt;attribute name="USE" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *               &lt;/restriction&gt;
 *             &lt;/complexContent&gt;
 *           &lt;/complexType&gt;
 *         &lt;/element&gt;
 *         &lt;element name="FContent" minOccurs="0"&gt;
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
 *                 &lt;attribute name="ID" type="{http://www.w3.org/2001/XMLSchema}ID" /&gt;
 *                 &lt;attribute name="USE" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *               &lt;/restriction&gt;
 *             &lt;/complexContent&gt;
 *           &lt;/complexType&gt;
 *         &lt;/element&gt;
 *         &lt;element name="stream" maxOccurs="unbounded" minOccurs="0"&gt;
 *           &lt;complexType&gt;
 *             &lt;complexContent&gt;
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *                 &lt;attribute name="ID" type="{http://www.w3.org/2001/XMLSchema}ID" /&gt;
 *                 &lt;attribute name="streamType" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *                 &lt;attribute name="OWNERID" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *                 &lt;attribute name="ADMID" type="{http://www.w3.org/2001/XMLSchema}IDREFS" /&gt;
 *                 &lt;attribute name="DMDID" type="{http://www.w3.org/2001/XMLSchema}IDREFS" /&gt;
 *                 &lt;attribute name="BEGIN" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *                 &lt;attribute name="END" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *                 &lt;attribute name="BETYPE"&gt;
 *                   &lt;simpleType&gt;
 *                     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *                       &lt;enumeration value="BYTE"/&gt;
 *                     &lt;/restriction&gt;
 *                   &lt;/simpleType&gt;
 *                 &lt;/attribute&gt;
 *               &lt;/restriction&gt;
 *             &lt;/complexContent&gt;
 *           &lt;/complexType&gt;
 *         &lt;/element&gt;
 *         &lt;element name="transformFile" maxOccurs="unbounded" minOccurs="0"&gt;
 *           &lt;complexType&gt;
 *             &lt;complexContent&gt;
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *                 &lt;attribute name="ID" type="{http://www.w3.org/2001/XMLSchema}ID" /&gt;
 *                 &lt;attribute name="TRANSFORMTYPE" use="required"&gt;
 *                   &lt;simpleType&gt;
 *                     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *                       &lt;enumeration value="decompression"/&gt;
 *                       &lt;enumeration value="decryption"/&gt;
 *                     &lt;/restriction&gt;
 *                   &lt;/simpleType&gt;
 *                 &lt;/attribute&gt;
 *                 &lt;attribute name="TRANSFORMALGORITHM" use="required" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *                 &lt;attribute name="TRANSFORMKEY" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *                 &lt;attribute name="TRANSFORMBEHAVIOR" type="{http://www.w3.org/2001/XMLSchema}IDREF" /&gt;
 *                 &lt;attribute name="TRANSFORMORDER" use="required" type="{http://www.w3.org/2001/XMLSchema}positiveInteger" /&gt;
 *               &lt;/restriction&gt;
 *             &lt;/complexContent&gt;
 *           &lt;/complexType&gt;
 *         &lt;/element&gt;
 *         &lt;element name="file" type="{http://www.loc.gov/METS/}fileType" maxOccurs="unbounded" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *       &lt;attGroup ref="{http://www.loc.gov/METS/}FILECORE"/&gt;
 *       &lt;attribute name="ID" use="required" type="{http://www.w3.org/2001/XMLSchema}ID" /&gt;
 *       &lt;attribute name="SEQ" type="{http://www.w3.org/2001/XMLSchema}int" /&gt;
 *       &lt;attribute name="OWNERID" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *       &lt;attribute name="ADMID" type="{http://www.w3.org/2001/XMLSchema}IDREFS" /&gt;
 *       &lt;attribute name="DMDID" type="{http://www.w3.org/2001/XMLSchema}IDREFS" /&gt;
 *       &lt;attribute name="GROUPID" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *       &lt;attribute name="USE" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *       &lt;attribute name="BEGIN" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *       &lt;attribute name="END" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *       &lt;attribute name="BETYPE"&gt;
 *         &lt;simpleType&gt;
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *             &lt;enumeration value="BYTE"/&gt;
 *           &lt;/restriction&gt;
 *         &lt;/simpleType&gt;
 *       &lt;/attribute&gt;
 *       &lt;anyAttribute processContents='lax' namespace='##other'/&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "fileType", propOrder = {
    "fLocat",
    "fContent",
    "stream",
    "transformFile",
    "file"
})
public class FileType {

    @XmlElement(name = "FLocat")
    protected List<FLocat> fLocat;
    @XmlElement(name = "FContent")
    protected FContent fContent;
    protected List<Stream> stream;
    protected List<TransformFile> transformFile;
    protected List<FileType> file;
    @XmlAttribute(name = "ID", required = true)
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlID
    @XmlSchemaType(name = "ID")
    protected String id;
    @XmlAttribute(name = "SEQ")
    protected Integer seq;
    @XmlAttribute(name = "OWNERID")
    protected String ownerid;
    @XmlAttribute(name = "ADMID")
    @XmlIDREF
    @XmlSchemaType(name = "IDREFS")
    protected List<Object> admid;
    @XmlAttribute(name = "DMDID")
    @XmlIDREF
    @XmlSchemaType(name = "IDREFS")
    protected List<Object> dmdid;
    @XmlAttribute(name = "GROUPID")
    protected String groupid;
    @XmlAttribute(name = "USE")
    protected String use;
    @XmlAttribute(name = "BEGIN")
    protected String begin;
    @XmlAttribute(name = "END")
    protected String end;
    @XmlAttribute(name = "BETYPE")
    protected String betype;
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
    @XmlAnyAttribute
    private Map<QName, String> otherAttributes = new HashMap<>();

    /**
     * Gets the value of the fLocat property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the fLocat property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getFLocat().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link FLocat }
     *
     *
     */
    public List<FLocat> getFLocat() {
        if (fLocat == null) {
            fLocat = new ArrayList<>();
        }
        return this.fLocat;
    }

    /**
     * Obtient la valeur de la propriété fContent.
     *
     * @return
     *     possible object is
     *     {@link FContent }
     *
     */
    public FContent getFContent() {
        return fContent;
    }

    /**
     * Définit la valeur de la propriété fContent.
     *
     * @param value
     *     allowed object is
     *     {@link FContent }
     *
     */
    public void setFContent(FContent value) {
        this.fContent = value;
    }

    /**
     * Gets the value of the stream property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the stream property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getStream().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Stream }
     *
     *
     */
    public List<Stream> getStream() {
        if (stream == null) {
            stream = new ArrayList<>();
        }
        return this.stream;
    }

    /**
     * Gets the value of the transformFile property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the transformFile property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getTransformFile().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link TransformFile }
     *
     *
     */
    public List<TransformFile> getTransformFile() {
        if (transformFile == null) {
            transformFile = new ArrayList<>();
        }
        return this.transformFile;
    }

    /**
     * Gets the value of the file property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the file property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getFile().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link FileType }
     *
     *
     */
    public List<FileType> getFile() {
        if (file == null) {
            file = new ArrayList<>();
        }
        return this.file;
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
     * Obtient la valeur de la propriété seq.
     *
     * @return
     *     possible object is
     *     {@link Integer }
     *
     */
    public Integer getSEQ() {
        return seq;
    }

    /**
     * Définit la valeur de la propriété seq.
     *
     * @param value
     *     allowed object is
     *     {@link Integer }
     *
     */
    public void setSEQ(Integer value) {
        this.seq = value;
    }

    /**
     * Obtient la valeur de la propriété ownerid.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getOWNERID() {
        return ownerid;
    }

    /**
     * Définit la valeur de la propriété ownerid.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setOWNERID(String value) {
        this.ownerid = value;
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
     * <pre>
     *    getDMDID().add(newItem);
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
     * Obtient la valeur de la propriété use.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getUSE() {
        return use;
    }

    /**
     * Définit la valeur de la propriété use.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setUSE(String value) {
        this.use = value;
    }

    /**
     * Obtient la valeur de la propriété begin.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getBEGIN() {
        return begin;
    }

    /**
     * Définit la valeur de la propriété begin.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setBEGIN(String value) {
        this.begin = value;
    }

    /**
     * Obtient la valeur de la propriété end.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getEND() {
        return end;
    }

    /**
     * Définit la valeur de la propriété end.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setEND(String value) {
        this.end = value;
    }

    /**
     * Obtient la valeur de la propriété betype.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getBETYPE() {
        return betype;
    }

    /**
     * Définit la valeur de la propriété betype.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setBETYPE(String value) {
        this.betype = value;
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
     *       &lt;attribute name="ID" type="{http://www.w3.org/2001/XMLSchema}ID" /&gt;
     *       &lt;attribute name="USE" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
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
    public static class FContent {

        protected byte[] binData;
        protected XmlData xmlData;
        @XmlAttribute(name = "ID")
        @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
        @XmlID
        @XmlSchemaType(name = "ID")
        protected String id;
        @XmlAttribute(name = "USE")
        protected String use;

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
         * Obtient la valeur de la propriété use.
         *
         * @return
         *     possible object is
         *     {@link String }
         *
         */
        public String getUSE() {
            return use;
        }

        /**
         * Définit la valeur de la propriété use.
         *
         * @param value
         *     allowed object is
         *     {@link String }
         *
         */
        public void setUSE(String value) {
            this.use = value;
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
     *       &lt;attribute name="ID" type="{http://www.w3.org/2001/XMLSchema}ID" /&gt;
     *       &lt;attribute name="USE" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
     *     &lt;/restriction&gt;
     *   &lt;/complexContent&gt;
     * &lt;/complexType&gt;
     * </pre>
     *
     *
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "")
    public static class FLocat {

        @XmlAttribute(name = "ID")
        @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
        @XmlID
        @XmlSchemaType(name = "ID")
        protected String id;
        @XmlAttribute(name = "USE")
        protected String use;
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
         * Obtient la valeur de la propriété use.
         *
         * @return
         *     possible object is
         *     {@link String }
         *
         */
        public String getUSE() {
            return use;
        }

        /**
         * Définit la valeur de la propriété use.
         *
         * @param value
         *     allowed object is
         *     {@link String }
         *
         */
        public void setUSE(String value) {
            this.use = value;
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
     *       &lt;attribute name="ID" type="{http://www.w3.org/2001/XMLSchema}ID" /&gt;
     *       &lt;attribute name="streamType" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
     *       &lt;attribute name="OWNERID" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
     *       &lt;attribute name="ADMID" type="{http://www.w3.org/2001/XMLSchema}IDREFS" /&gt;
     *       &lt;attribute name="DMDID" type="{http://www.w3.org/2001/XMLSchema}IDREFS" /&gt;
     *       &lt;attribute name="BEGIN" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
     *       &lt;attribute name="END" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
     *       &lt;attribute name="BETYPE"&gt;
     *         &lt;simpleType&gt;
     *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
     *             &lt;enumeration value="BYTE"/&gt;
     *           &lt;/restriction&gt;
     *         &lt;/simpleType&gt;
     *       &lt;/attribute&gt;
     *     &lt;/restriction&gt;
     *   &lt;/complexContent&gt;
     * &lt;/complexType&gt;
     * </pre>
     *
     *
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "")
    public static class Stream {

        @XmlAttribute(name = "ID")
        @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
        @XmlID
        @XmlSchemaType(name = "ID")
        protected String id;
        @XmlAttribute(name = "streamType")
        protected String streamType;
        @XmlAttribute(name = "OWNERID")
        protected String ownerid;
        @XmlAttribute(name = "ADMID")
        @XmlIDREF
        @XmlSchemaType(name = "IDREFS")
        protected List<Object> admid;
        @XmlAttribute(name = "DMDID")
        @XmlIDREF
        @XmlSchemaType(name = "IDREFS")
        protected List<Object> dmdid;
        @XmlAttribute(name = "BEGIN")
        protected String begin;
        @XmlAttribute(name = "END")
        protected String end;
        @XmlAttribute(name = "BETYPE")
        protected String betype;

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
         * Obtient la valeur de la propriété streamType.
         *
         * @return
         *     possible object is
         *     {@link String }
         *
         */
        public String getStreamType() {
            return streamType;
        }

        /**
         * Définit la valeur de la propriété streamType.
         *
         * @param value
         *     allowed object is
         *     {@link String }
         *
         */
        public void setStreamType(String value) {
            this.streamType = value;
        }

        /**
         * Obtient la valeur de la propriété ownerid.
         *
         * @return
         *     possible object is
         *     {@link String }
         *
         */
        public String getOWNERID() {
            return ownerid;
        }

        /**
         * Définit la valeur de la propriété ownerid.
         *
         * @param value
         *     allowed object is
         *     {@link String }
         *
         */
        public void setOWNERID(String value) {
            this.ownerid = value;
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
         * <pre>
         *    getDMDID().add(newItem);
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
         * Obtient la valeur de la propriété begin.
         *
         * @return
         *     possible object is
         *     {@link String }
         *
         */
        public String getBEGIN() {
            return begin;
        }

        /**
         * Définit la valeur de la propriété begin.
         *
         * @param value
         *     allowed object is
         *     {@link String }
         *
         */
        public void setBEGIN(String value) {
            this.begin = value;
        }

        /**
         * Obtient la valeur de la propriété end.
         *
         * @return
         *     possible object is
         *     {@link String }
         *
         */
        public String getEND() {
            return end;
        }

        /**
         * Définit la valeur de la propriété end.
         *
         * @param value
         *     allowed object is
         *     {@link String }
         *
         */
        public void setEND(String value) {
            this.end = value;
        }

        /**
         * Obtient la valeur de la propriété betype.
         *
         * @return
         *     possible object is
         *     {@link String }
         *
         */
        public String getBETYPE() {
            return betype;
        }

        /**
         * Définit la valeur de la propriété betype.
         *
         * @param value
         *     allowed object is
         *     {@link String }
         *
         */
        public void setBETYPE(String value) {
            this.betype = value;
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
     *       &lt;attribute name="ID" type="{http://www.w3.org/2001/XMLSchema}ID" /&gt;
     *       &lt;attribute name="TRANSFORMTYPE" use="required"&gt;
     *         &lt;simpleType&gt;
     *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
     *             &lt;enumeration value="decompression"/&gt;
     *             &lt;enumeration value="decryption"/&gt;
     *           &lt;/restriction&gt;
     *         &lt;/simpleType&gt;
     *       &lt;/attribute&gt;
     *       &lt;attribute name="TRANSFORMALGORITHM" use="required" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
     *       &lt;attribute name="TRANSFORMKEY" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
     *       &lt;attribute name="TRANSFORMBEHAVIOR" type="{http://www.w3.org/2001/XMLSchema}IDREF" /&gt;
     *       &lt;attribute name="TRANSFORMORDER" use="required" type="{http://www.w3.org/2001/XMLSchema}positiveInteger" /&gt;
     *     &lt;/restriction&gt;
     *   &lt;/complexContent&gt;
     * &lt;/complexType&gt;
     * </pre>
     *
     *
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "")
    public static class TransformFile {

        @XmlAttribute(name = "ID")
        @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
        @XmlID
        @XmlSchemaType(name = "ID")
        protected String id;
        @XmlAttribute(name = "TRANSFORMTYPE", required = true)
        protected String transformtype;
        @XmlAttribute(name = "TRANSFORMALGORITHM", required = true)
        protected String transformalgorithm;
        @XmlAttribute(name = "TRANSFORMKEY")
        protected String transformkey;
        @XmlAttribute(name = "TRANSFORMBEHAVIOR")
        @XmlIDREF
        @XmlSchemaType(name = "IDREF")
        protected Object transformbehavior;
        @XmlAttribute(name = "TRANSFORMORDER", required = true)
        @XmlSchemaType(name = "positiveInteger")
        protected BigInteger transformorder;

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
         * Obtient la valeur de la propriété transformtype.
         *
         * @return
         *     possible object is
         *     {@link String }
         *
         */
        public String getTRANSFORMTYPE() {
            return transformtype;
        }

        /**
         * Définit la valeur de la propriété transformtype.
         *
         * @param value
         *     allowed object is
         *     {@link String }
         *
         */
        public void setTRANSFORMTYPE(String value) {
            this.transformtype = value;
        }

        /**
         * Obtient la valeur de la propriété transformalgorithm.
         *
         * @return
         *     possible object is
         *     {@link String }
         *
         */
        public String getTRANSFORMALGORITHM() {
            return transformalgorithm;
        }

        /**
         * Définit la valeur de la propriété transformalgorithm.
         *
         * @param value
         *     allowed object is
         *     {@link String }
         *
         */
        public void setTRANSFORMALGORITHM(String value) {
            this.transformalgorithm = value;
        }

        /**
         * Obtient la valeur de la propriété transformkey.
         *
         * @return
         *     possible object is
         *     {@link String }
         *
         */
        public String getTRANSFORMKEY() {
            return transformkey;
        }

        /**
         * Définit la valeur de la propriété transformkey.
         *
         * @param value
         *     allowed object is
         *     {@link String }
         *
         */
        public void setTRANSFORMKEY(String value) {
            this.transformkey = value;
        }

        /**
         * Obtient la valeur de la propriété transformbehavior.
         *
         * @return
         *     possible object is
         *     {@link Object }
         *
         */
        public Object getTRANSFORMBEHAVIOR() {
            return transformbehavior;
        }

        /**
         * Définit la valeur de la propriété transformbehavior.
         *
         * @param value
         *     allowed object is
         *     {@link Object }
         *
         */
        public void setTRANSFORMBEHAVIOR(Object value) {
            this.transformbehavior = value;
        }

        /**
         * Obtient la valeur de la propriété transformorder.
         *
         * @return
         *     possible object is
         *     {@link BigInteger }
         *
         */
        public BigInteger getTRANSFORMORDER() {
            return transformorder;
        }

        /**
         * Définit la valeur de la propriété transformorder.
         *
         * @param value
         *     allowed object is
         *     {@link BigInteger }
         *
         */
        public void setTRANSFORMORDER(BigInteger value) {
            this.transformorder = value;
        }

    }

}
