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
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlID;
import jakarta.xml.bind.annotation.XmlIDREF;
import jakarta.xml.bind.annotation.XmlSchemaType;
import jakarta.xml.bind.annotation.XmlSeeAlso;
import jakarta.xml.bind.annotation.XmlType;
import jakarta.xml.bind.annotation.XmlValue;
import jakarta.xml.bind.annotation.adapters.CollapsedStringAdapter;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.xml.namespace.QName;

/**
 * metsType: Complex Type for METS Sections
 * A METS document consists of seven possible subsidiary sections: metsHdr (METS document header), dmdSec (descriptive metadata section), amdSec
 * (administrative metadata section), fileGrp (file inventory group), structLink (structural map linking), structMap (structural map) and behaviorSec
 * (behaviors section).
 *
 *
 * <p>
 * Classe Java pour metsType complex type.
 *
 * <p>
 * Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 *
 * <pre>
 * &lt;complexType name="metsType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="metsHdr" minOccurs="0"&gt;
 *           &lt;complexType&gt;
 *             &lt;complexContent&gt;
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *                 &lt;sequence&gt;
 *                   &lt;element name="agent" maxOccurs="unbounded" minOccurs="0"&gt;
 *                     &lt;complexType&gt;
 *                       &lt;complexContent&gt;
 *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *                           &lt;sequence&gt;
 *                             &lt;element name="name" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *                             &lt;element name="note" type="{http://www.w3.org/2001/XMLSchema}string" maxOccurs="unbounded" minOccurs="0"/&gt;
 *                           &lt;/sequence&gt;
 *                           &lt;attribute name="ID" type="{http://www.w3.org/2001/XMLSchema}ID" /&gt;
 *                           &lt;attribute name="ROLE" use="required"&gt;
 *                             &lt;simpleType&gt;
 *                               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *                                 &lt;enumeration value="CREATOR"/&gt;
 *                                 &lt;enumeration value="EDITOR"/&gt;
 *                                 &lt;enumeration value="ARCHIVIST"/&gt;
 *                                 &lt;enumeration value="PRESERVATION"/&gt;
 *                                 &lt;enumeration value="DISSEMINATOR"/&gt;
 *                                 &lt;enumeration value="CUSTODIAN"/&gt;
 *                                 &lt;enumeration value="IPOWNER"/&gt;
 *                                 &lt;enumeration value="OTHER"/&gt;
 *                               &lt;/restriction&gt;
 *                             &lt;/simpleType&gt;
 *                           &lt;/attribute&gt;
 *                           &lt;attribute name="OTHERROLE" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *                           &lt;attribute name="TYPE"&gt;
 *                             &lt;simpleType&gt;
 *                               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *                                 &lt;enumeration value="INDIVIDUAL"/&gt;
 *                                 &lt;enumeration value="ORGANIZATION"/&gt;
 *                                 &lt;enumeration value="OTHER"/&gt;
 *                               &lt;/restriction&gt;
 *                             &lt;/simpleType&gt;
 *                           &lt;/attribute&gt;
 *                           &lt;attribute name="OTHERTYPE" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *                         &lt;/restriction&gt;
 *                       &lt;/complexContent&gt;
 *                     &lt;/complexType&gt;
 *                   &lt;/element&gt;
 *                   &lt;element name="altRecordID" maxOccurs="unbounded" minOccurs="0"&gt;
 *                     &lt;complexType&gt;
 *                       &lt;simpleContent&gt;
 *                         &lt;extension base="&lt;http://www.w3.org/2001/XMLSchema&gt;string"&gt;
 *                           &lt;attribute name="ID" type="{http://www.w3.org/2001/XMLSchema}ID" /&gt;
 *                           &lt;attribute name="TYPE" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *                         &lt;/extension&gt;
 *                       &lt;/simpleContent&gt;
 *                     &lt;/complexType&gt;
 *                   &lt;/element&gt;
 *                   &lt;element name="metsDocumentID" minOccurs="0"&gt;
 *                     &lt;complexType&gt;
 *                       &lt;simpleContent&gt;
 *                         &lt;extension base="&lt;http://www.w3.org/2001/XMLSchema&gt;string"&gt;
 *                           &lt;attribute name="ID" type="{http://www.w3.org/2001/XMLSchema}ID" /&gt;
 *                           &lt;attribute name="TYPE" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *                         &lt;/extension&gt;
 *                       &lt;/simpleContent&gt;
 *                     &lt;/complexType&gt;
 *                   &lt;/element&gt;
 *                 &lt;/sequence&gt;
 *                 &lt;attribute name="ID" type="{http://www.w3.org/2001/XMLSchema}ID" /&gt;
 *                 &lt;attribute name="ADMID" type="{http://www.w3.org/2001/XMLSchema}IDREFS" /&gt;
 *                 &lt;attribute name="CREATEDATE" type="{http://www.w3.org/2001/XMLSchema}dateTime" /&gt;
 *                 &lt;attribute name="LASTMODDATE" type="{http://www.w3.org/2001/XMLSchema}dateTime" /&gt;
 *                 &lt;attribute name="RECORDSTATUS" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *                 &lt;anyAttribute processContents='lax' namespace='##other'/&gt;
 *               &lt;/restriction&gt;
 *             &lt;/complexContent&gt;
 *           &lt;/complexType&gt;
 *         &lt;/element&gt;
 *         &lt;element name="dmdSec" type="{http://www.loc.gov/METS/}mdSecType" maxOccurs="unbounded" minOccurs="0"/&gt;
 *         &lt;element name="amdSec" type="{http://www.loc.gov/METS/}amdSecType" maxOccurs="unbounded" minOccurs="0"/&gt;
 *         &lt;element name="fileSec" minOccurs="0"&gt;
 *           &lt;complexType&gt;
 *             &lt;complexContent&gt;
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *                 &lt;sequence&gt;
 *                   &lt;element name="fileGrp" maxOccurs="unbounded"&gt;
 *                     &lt;complexType&gt;
 *                       &lt;complexContent&gt;
 *                         &lt;extension base="{http://www.loc.gov/METS/}fileGrpType"&gt;
 *                           &lt;anyAttribute processContents='lax' namespace='##other'/&gt;
 *                         &lt;/extension&gt;
 *                       &lt;/complexContent&gt;
 *                     &lt;/complexType&gt;
 *                   &lt;/element&gt;
 *                 &lt;/sequence&gt;
 *                 &lt;attribute name="ID" type="{http://www.w3.org/2001/XMLSchema}ID" /&gt;
 *                 &lt;anyAttribute processContents='lax' namespace='##other'/&gt;
 *               &lt;/restriction&gt;
 *             &lt;/complexContent&gt;
 *           &lt;/complexType&gt;
 *         &lt;/element&gt;
 *         &lt;element name="structMap" type="{http://www.loc.gov/METS/}structMapType" maxOccurs="unbounded"/&gt;
 *         &lt;element name="structLink" minOccurs="0"&gt;
 *           &lt;complexType&gt;
 *             &lt;complexContent&gt;
 *               &lt;extension base="{http://www.loc.gov/METS/}structLinkType"&gt;
 *                 &lt;anyAttribute processContents='lax' namespace='##other'/&gt;
 *               &lt;/extension&gt;
 *             &lt;/complexContent&gt;
 *           &lt;/complexType&gt;
 *         &lt;/element&gt;
 *         &lt;element name="behaviorSec" type="{http://www.loc.gov/METS/}behaviorSecType" maxOccurs="unbounded" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *       &lt;attribute name="ID" type="{http://www.w3.org/2001/XMLSchema}ID" /&gt;
 *       &lt;attribute name="OBJID" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *       &lt;attribute name="LABEL" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *       &lt;attribute name="TYPE" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *       &lt;attribute name="PROFILE" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *       &lt;anyAttribute processContents='lax' namespace='##other'/&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "metsType",
         propOrder = {"metsHdr",
                      "dmdSec",
                      "amdSec",
                      "fileSec",
                      "structMap",
                      "structLink",
                      "behaviorSec"})
@XmlSeeAlso({Mets.class})
public class MetsType {

    protected MetsHdr metsHdr;
    protected List<MdSecType> dmdSec;
    protected List<AmdSecType> amdSec;
    protected FileSec fileSec;
    @XmlElement(required = true)
    protected List<StructMapType> structMap;
    protected StructLink structLink;
    protected List<BehaviorSecType> behaviorSec;
    @XmlAttribute(name = "ID")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlID
    @XmlSchemaType(name = "ID")
    protected String id;
    @XmlAttribute(name = "OBJID")
    protected String objid;
    @XmlAttribute(name = "LABEL")
    protected String label;
    @XmlAttribute(name = "TYPE")
    protected String type;
    @XmlAttribute(name = "PROFILE")
    protected String profile;
    @XmlAnyAttribute
    private Map<QName, String> otherAttributes = new HashMap<>();

    /**
     * Obtient la valeur de la propriété metsHdr.
     *
     * @return
     *         possible object is
     *         {@link MetsHdr }
     *
     */
    public MetsHdr getMetsHdr() {
        return metsHdr;
    }

    /**
     * Définit la valeur de la propriété metsHdr.
     *
     * @param value
     *            allowed object is
     *            {@link MetsHdr }
     *
     */
    public void setMetsHdr(MetsHdr value) {
        this.metsHdr = value;
    }

    /**
     * Gets the value of the dmdSec property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the dmdSec property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     *
     * <pre>
     * getDmdSec().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link MdSecType }
     *
     *
     */
    public List<MdSecType> getDmdSec() {
        if (dmdSec == null) {
            dmdSec = new ArrayList<>();
        }
        return this.dmdSec;
    }

    /**
     * Gets the value of the amdSec property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the amdSec property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     *
     * <pre>
     * getAmdSec().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link AmdSecType }
     *
     *
     */
    public List<AmdSecType> getAmdSec() {
        if (amdSec == null) {
            amdSec = new ArrayList<>();
        }
        return this.amdSec;
    }

    /**
     * Obtient la valeur de la propriété fileSec.
     *
     * @return
     *         possible object is
     *         {@link FileSec }
     *
     */
    public FileSec getFileSec() {
        return fileSec;
    }

    /**
     * Définit la valeur de la propriété fileSec.
     *
     * @param value
     *            allowed object is
     *            {@link FileSec }
     *
     */
    public void setFileSec(FileSec value) {
        this.fileSec = value;
    }

    /**
     * Gets the value of the structMap property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the structMap property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     *
     * <pre>
     * getStructMap().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link StructMapType }
     *
     *
     */
    public List<StructMapType> getStructMap() {
        if (structMap == null) {
            structMap = new ArrayList<>();
        }
        return this.structMap;
    }

    /**
     * Obtient la valeur de la propriété structLink.
     *
     * @return
     *         possible object is
     *         {@link StructLink }
     *
     */
    public StructLink getStructLink() {
        return structLink;
    }

    /**
     * Définit la valeur de la propriété structLink.
     *
     * @param value
     *            allowed object is
     *            {@link StructLink }
     *
     */
    public void setStructLink(StructLink value) {
        this.structLink = value;
    }

    /**
     * Gets the value of the behaviorSec property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the behaviorSec property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     *
     * <pre>
     * getBehaviorSec().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link BehaviorSecType }
     *
     *
     */
    public List<BehaviorSecType> getBehaviorSec() {
        if (behaviorSec == null) {
            behaviorSec = new ArrayList<>();
        }
        return this.behaviorSec;
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
     * Obtient la valeur de la propriété objid.
     *
     * @return
     *         possible object is
     *         {@link String }
     *
     */
    public String getOBJID() {
        return objid;
    }

    /**
     * Définit la valeur de la propriété objid.
     *
     * @param value
     *            allowed object is
     *            {@link String }
     *
     */
    public void setOBJID(String value) {
        this.objid = value;
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
     * Obtient la valeur de la propriété profile.
     *
     * @return
     *         possible object is
     *         {@link String }
     *
     */
    public String getPROFILE() {
        return profile;
    }

    /**
     * Définit la valeur de la propriété profile.
     *
     * @param value
     *            allowed object is
     *            {@link String }
     *
     */
    public void setPROFILE(String value) {
        this.profile = value;
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
     *       &lt;sequence&gt;
     *         &lt;element name="fileGrp" maxOccurs="unbounded"&gt;
     *           &lt;complexType&gt;
     *             &lt;complexContent&gt;
     *               &lt;extension base="{http://www.loc.gov/METS/}fileGrpType"&gt;
     *                 &lt;anyAttribute processContents='lax' namespace='##other'/&gt;
     *               &lt;/extension&gt;
     *             &lt;/complexContent&gt;
     *           &lt;/complexType&gt;
     *         &lt;/element&gt;
     *       &lt;/sequence&gt;
     *       &lt;attribute name="ID" type="{http://www.w3.org/2001/XMLSchema}ID" /&gt;
     *       &lt;anyAttribute processContents='lax' namespace='##other'/&gt;
     *     &lt;/restriction&gt;
     *   &lt;/complexContent&gt;
     * &lt;/complexType&gt;
     * </pre>
     *
     *
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {"fileGrp"})
    public static class FileSec {

        @XmlElement(required = true)
        protected List<FileGrp> fileGrp;
        @XmlAttribute(name = "ID")
        @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
        @XmlID
        @XmlSchemaType(name = "ID")
        protected String id;
        @XmlAnyAttribute
        private Map<QName, String> otherAttributes = new HashMap<>();

        /**
         * Gets the value of the fileGrp property.
         *
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the fileGrp property.
         *
         * <p>
         * For example, to add a new item, do as follows:
         *
         * <pre>
         * getFileGrp().add(newItem);
         * </pre>
         *
         *
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link FileGrp }
         *
         *
         */
        public List<FileGrp> getFileGrp() {
            if (fileGrp == null) {
                fileGrp = new ArrayList<>();
            }
            return this.fileGrp;
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
         *     &lt;extension base="{http://www.loc.gov/METS/}fileGrpType"&gt;
         *       &lt;anyAttribute processContents='lax' namespace='##other'/&gt;
         *     &lt;/extension&gt;
         *   &lt;/complexContent&gt;
         * &lt;/complexType&gt;
         * </pre>
         *
         *
         */
        @XmlAccessorType(XmlAccessType.FIELD)
        @XmlType(name = "")
        public static class FileGrp extends FileGrpType {

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
     *       &lt;sequence&gt;
     *         &lt;element name="agent" maxOccurs="unbounded" minOccurs="0"&gt;
     *           &lt;complexType&gt;
     *             &lt;complexContent&gt;
     *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
     *                 &lt;sequence&gt;
     *                   &lt;element name="name" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
     *                   &lt;element name="note" type="{http://www.w3.org/2001/XMLSchema}string" maxOccurs="unbounded" minOccurs="0"/&gt;
     *                 &lt;/sequence&gt;
     *                 &lt;attribute name="ID" type="{http://www.w3.org/2001/XMLSchema}ID" /&gt;
     *                 &lt;attribute name="ROLE" use="required"&gt;
     *                   &lt;simpleType&gt;
     *                     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
     *                       &lt;enumeration value="CREATOR"/&gt;
     *                       &lt;enumeration value="EDITOR"/&gt;
     *                       &lt;enumeration value="ARCHIVIST"/&gt;
     *                       &lt;enumeration value="PRESERVATION"/&gt;
     *                       &lt;enumeration value="DISSEMINATOR"/&gt;
     *                       &lt;enumeration value="CUSTODIAN"/&gt;
     *                       &lt;enumeration value="IPOWNER"/&gt;
     *                       &lt;enumeration value="OTHER"/&gt;
     *                     &lt;/restriction&gt;
     *                   &lt;/simpleType&gt;
     *                 &lt;/attribute&gt;
     *                 &lt;attribute name="OTHERROLE" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
     *                 &lt;attribute name="TYPE"&gt;
     *                   &lt;simpleType&gt;
     *                     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
     *                       &lt;enumeration value="INDIVIDUAL"/&gt;
     *                       &lt;enumeration value="ORGANIZATION"/&gt;
     *                       &lt;enumeration value="OTHER"/&gt;
     *                     &lt;/restriction&gt;
     *                   &lt;/simpleType&gt;
     *                 &lt;/attribute&gt;
     *                 &lt;attribute name="OTHERTYPE" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
     *               &lt;/restriction&gt;
     *             &lt;/complexContent&gt;
     *           &lt;/complexType&gt;
     *         &lt;/element&gt;
     *         &lt;element name="altRecordID" maxOccurs="unbounded" minOccurs="0"&gt;
     *           &lt;complexType&gt;
     *             &lt;simpleContent&gt;
     *               &lt;extension base="&lt;http://www.w3.org/2001/XMLSchema&gt;string"&gt;
     *                 &lt;attribute name="ID" type="{http://www.w3.org/2001/XMLSchema}ID" /&gt;
     *                 &lt;attribute name="TYPE" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
     *               &lt;/extension&gt;
     *             &lt;/simpleContent&gt;
     *           &lt;/complexType&gt;
     *         &lt;/element&gt;
     *         &lt;element name="metsDocumentID" minOccurs="0"&gt;
     *           &lt;complexType&gt;
     *             &lt;simpleContent&gt;
     *               &lt;extension base="&lt;http://www.w3.org/2001/XMLSchema&gt;string"&gt;
     *                 &lt;attribute name="ID" type="{http://www.w3.org/2001/XMLSchema}ID" /&gt;
     *                 &lt;attribute name="TYPE" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
     *               &lt;/extension&gt;
     *             &lt;/simpleContent&gt;
     *           &lt;/complexType&gt;
     *         &lt;/element&gt;
     *       &lt;/sequence&gt;
     *       &lt;attribute name="ID" type="{http://www.w3.org/2001/XMLSchema}ID" /&gt;
     *       &lt;attribute name="ADMID" type="{http://www.w3.org/2001/XMLSchema}IDREFS" /&gt;
     *       &lt;attribute name="CREATEDATE" type="{http://www.w3.org/2001/XMLSchema}dateTime" /&gt;
     *       &lt;attribute name="LASTMODDATE" type="{http://www.w3.org/2001/XMLSchema}dateTime" /&gt;
     *       &lt;attribute name="RECORDSTATUS" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
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
             propOrder = {"agent",
                          "altRecordID",
                          "metsDocumentID"})
    public static class MetsHdr {

        protected List<Agent> agent;
        protected List<AltRecordID> altRecordID;
        protected MetsDocumentID metsDocumentID;
        @XmlAttribute(name = "ID")
        @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
        @XmlID
        @XmlSchemaType(name = "ID")
        protected String id;
        @XmlAttribute(name = "ADMID")
        @XmlIDREF
        @XmlSchemaType(name = "IDREFS")
        protected List<Object> admid;
        @XmlAttribute(name = "CREATEDATE")
        @XmlSchemaType(name = "dateTime")
        protected LocalDateTime createdate;
        @XmlAttribute(name = "LASTMODDATE")
        @XmlSchemaType(name = "dateTime")
        protected LocalDateTime lastmoddate;
        @XmlAttribute(name = "RECORDSTATUS")
        protected String recordstatus;
        @XmlAnyAttribute
        private Map<QName, String> otherAttributes = new HashMap<>();

        /**
         * Gets the value of the agent property.
         *
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the agent property.
         *
         * <p>
         * For example, to add a new item, do as follows:
         *
         * <pre>
         * getAgent().add(newItem);
         * </pre>
         *
         *
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link Agent }
         *
         *
         */
        public List<Agent> getAgent() {
            if (agent == null) {
                agent = new ArrayList<>();
            }
            return this.agent;
        }

        /**
         * Gets the value of the altRecordID property.
         *
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the altRecordID property.
         *
         * <p>
         * For example, to add a new item, do as follows:
         *
         * <pre>
         * getAltRecordID().add(newItem);
         * </pre>
         *
         *
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link AltRecordID }
         *
         *
         */
        public List<AltRecordID> getAltRecordID() {
            if (altRecordID == null) {
                altRecordID = new ArrayList<>();
            }
            return this.altRecordID;
        }

        /**
         * Obtient la valeur de la propriété metsDocumentID.
         *
         * @return
         *         possible object is
         *         {@link MetsDocumentID }
         *
         */
        public MetsDocumentID getMetsDocumentID() {
            return metsDocumentID;
        }

        /**
         * Définit la valeur de la propriété metsDocumentID.
         *
         * @param value
         *            allowed object is
         *            {@link MetsDocumentID }
         *
         */
        public void setMetsDocumentID(MetsDocumentID value) {
            this.metsDocumentID = value;
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
         * Obtient la valeur de la propriété createdate.
         *
         * @return
         *         possible object is
         *         {@link LocalDateTime }
         *
         */
        public LocalDateTime getCREATEDATE() {
            return createdate;
        }

        /**
         * Définit la valeur de la propriété createdate.
         *
         * @param value
         *            allowed object is
         *            {@link LocalDateTime }
         *
         */
        public void setCREATEDATE(LocalDateTime value) {
            this.createdate = value;
        }

        /**
         * Obtient la valeur de la propriété lastmoddate.
         *
         * @return
         *         possible object is
         *         {@link LocalDateTime }
         *
         */
        public LocalDateTime getLASTMODDATE() {
            return lastmoddate;
        }

        /**
         * Définit la valeur de la propriété lastmoddate.
         *
         * @param value
         *            allowed object is
         *            {@link LocalDateTime }
         *
         */
        public void setLASTMODDATE(LocalDateTime value) {
            this.lastmoddate = value;
        }

        /**
         * Obtient la valeur de la propriété recordstatus.
         *
         * @return
         *         possible object is
         *         {@link String }
         *
         */
        public String getRECORDSTATUS() {
            return recordstatus;
        }

        /**
         * Définit la valeur de la propriété recordstatus.
         *
         * @param value
         *            allowed object is
         *            {@link String }
         *
         */
        public void setRECORDSTATUS(String value) {
            this.recordstatus = value;
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
         *       &lt;sequence&gt;
         *         &lt;element name="name" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
         *         &lt;element name="note" type="{http://www.w3.org/2001/XMLSchema}string" maxOccurs="unbounded" minOccurs="0"/&gt;
         *       &lt;/sequence&gt;
         *       &lt;attribute name="ID" type="{http://www.w3.org/2001/XMLSchema}ID" /&gt;
         *       &lt;attribute name="ROLE" use="required"&gt;
         *         &lt;simpleType&gt;
         *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
         *             &lt;enumeration value="CREATOR"/&gt;
         *             &lt;enumeration value="EDITOR"/&gt;
         *             &lt;enumeration value="ARCHIVIST"/&gt;
         *             &lt;enumeration value="PRESERVATION"/&gt;
         *             &lt;enumeration value="DISSEMINATOR"/&gt;
         *             &lt;enumeration value="CUSTODIAN"/&gt;
         *             &lt;enumeration value="IPOWNER"/&gt;
         *             &lt;enumeration value="OTHER"/&gt;
         *           &lt;/restriction&gt;
         *         &lt;/simpleType&gt;
         *       &lt;/attribute&gt;
         *       &lt;attribute name="OTHERROLE" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
         *       &lt;attribute name="TYPE"&gt;
         *         &lt;simpleType&gt;
         *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
         *             &lt;enumeration value="INDIVIDUAL"/&gt;
         *             &lt;enumeration value="ORGANIZATION"/&gt;
         *             &lt;enumeration value="OTHER"/&gt;
         *           &lt;/restriction&gt;
         *         &lt;/simpleType&gt;
         *       &lt;/attribute&gt;
         *       &lt;attribute name="OTHERTYPE" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
         *     &lt;/restriction&gt;
         *   &lt;/complexContent&gt;
         * &lt;/complexType&gt;
         * </pre>
         *
         *
         */
        @XmlAccessorType(XmlAccessType.FIELD)
        @XmlType(name = "",
                 propOrder = {"name",
                              "note"})
        public static class Agent {

            @XmlElement(required = true)
            protected String name;
            protected List<String> note;
            @XmlAttribute(name = "ID")
            @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
            @XmlID
            @XmlSchemaType(name = "ID")
            protected String id;
            @XmlAttribute(name = "ROLE", required = true)
            protected String role;
            @XmlAttribute(name = "OTHERROLE")
            protected String otherrole;
            @XmlAttribute(name = "TYPE")
            protected String type;
            @XmlAttribute(name = "OTHERTYPE")
            protected String othertype;

            /**
             * Obtient la valeur de la propriété name.
             *
             * @return
             *         possible object is
             *         {@link String }
             *
             */
            public String getName() {
                return name;
            }

            /**
             * Définit la valeur de la propriété name.
             *
             * @param value
             *            allowed object is
             *            {@link String }
             *
             */
            public void setName(String value) {
                this.name = value;
            }

            /**
             * Gets the value of the note property.
             *
             * <p>
             * This accessor method returns a reference to the live list,
             * not a snapshot. Therefore any modification you make to the
             * returned list will be present inside the JAXB object.
             * This is why there is not a <CODE>set</CODE> method for the note property.
             *
             * <p>
             * For example, to add a new item, do as follows:
             *
             * <pre>
             * getNote().add(newItem);
             * </pre>
             *
             *
             * <p>
             * Objects of the following type(s) are allowed in the list
             * {@link String }
             *
             *
             */
            public List<String> getNote() {
                if (note == null) {
                    note = new ArrayList<>();
                }
                return this.note;
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
             * Obtient la valeur de la propriété role.
             *
             * @return
             *         possible object is
             *         {@link String }
             *
             */
            public String getROLE() {
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
            public void setROLE(String value) {
                this.role = value;
            }

            /**
             * Obtient la valeur de la propriété otherrole.
             *
             * @return
             *         possible object is
             *         {@link String }
             *
             */
            public String getOTHERROLE() {
                return otherrole;
            }

            /**
             * Définit la valeur de la propriété otherrole.
             *
             * @param value
             *            allowed object is
             *            {@link String }
             *
             */
            public void setOTHERROLE(String value) {
                this.otherrole = value;
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
             * Obtient la valeur de la propriété othertype.
             *
             * @return
             *         possible object is
             *         {@link String }
             *
             */
            public String getOTHERTYPE() {
                return othertype;
            }

            /**
             * Définit la valeur de la propriété othertype.
             *
             * @param value
             *            allowed object is
             *            {@link String }
             *
             */
            public void setOTHERTYPE(String value) {
                this.othertype = value;
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
         *   &lt;simpleContent&gt;
         *     &lt;extension base="&lt;http://www.w3.org/2001/XMLSchema&gt;string"&gt;
         *       &lt;attribute name="ID" type="{http://www.w3.org/2001/XMLSchema}ID" /&gt;
         *       &lt;attribute name="TYPE" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
         *     &lt;/extension&gt;
         *   &lt;/simpleContent&gt;
         * &lt;/complexType&gt;
         * </pre>
         *
         *
         */
        @XmlAccessorType(XmlAccessType.FIELD)
        @XmlType(name = "", propOrder = {"value"})
        public static class AltRecordID {

            @XmlValue
            protected String value;
            @XmlAttribute(name = "ID")
            @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
            @XmlID
            @XmlSchemaType(name = "ID")
            protected String id;
            @XmlAttribute(name = "TYPE")
            protected String type;

            /**
             * Obtient la valeur de la propriété value.
             *
             * @return
             *         possible object is
             *         {@link String }
             *
             */
            public String getValue() {
                return value;
            }

            /**
             * Définit la valeur de la propriété value.
             *
             * @param value
             *            allowed object is
             *            {@link String }
             *
             */
            public void setValue(String value) {
                this.value = value;
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
         *   &lt;simpleContent&gt;
         *     &lt;extension base="&lt;http://www.w3.org/2001/XMLSchema&gt;string"&gt;
         *       &lt;attribute name="ID" type="{http://www.w3.org/2001/XMLSchema}ID" /&gt;
         *       &lt;attribute name="TYPE" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
         *     &lt;/extension&gt;
         *   &lt;/simpleContent&gt;
         * &lt;/complexType&gt;
         * </pre>
         *
         *
         */
        @XmlAccessorType(XmlAccessType.FIELD)
        @XmlType(name = "", propOrder = {"value"})
        public static class MetsDocumentID {

            @XmlValue
            protected String value;
            @XmlAttribute(name = "ID")
            @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
            @XmlID
            @XmlSchemaType(name = "ID")
            protected String id;
            @XmlAttribute(name = "TYPE")
            protected String type;

            /**
             * Obtient la valeur de la propriété value.
             *
             * @return
             *         possible object is
             *         {@link String }
             *
             */
            public String getValue() {
                return value;
            }

            /**
             * Définit la valeur de la propriété value.
             *
             * @param value
             *            allowed object is
             *            {@link String }
             *
             */
            public void setValue(String value) {
                this.value = value;
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
     *     &lt;extension base="{http://www.loc.gov/METS/}structLinkType"&gt;
     *       &lt;anyAttribute processContents='lax' namespace='##other'/&gt;
     *     &lt;/extension&gt;
     *   &lt;/complexContent&gt;
     * &lt;/complexType&gt;
     * </pre>
     *
     *
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "")
    public static class StructLink extends StructLinkType {

    }

}
