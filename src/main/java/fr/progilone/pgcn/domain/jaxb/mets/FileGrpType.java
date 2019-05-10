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
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlID;
import javax.xml.bind.annotation.XmlIDREF;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import javax.xml.namespace.QName;


/**
 * fileGrpType: Complex Type for File Groups
 * 				The file group is used to cluster all of the digital files composing a digital library object in a hierarchical arrangement (fileGrp is recursively defined to enable the creation of the hierarchy).  Any file group may contain zero or more file elements.  File elements in turn can contain one or more FLocat elements (a pointer to a file containing content for this object) and/or a FContent element (the contents of the file, in either XML or  Base64 encoding).
 *
 *
 * <p>Classe Java pour fileGrpType complex type.
 *
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 *
 * <pre>
 * &lt;complexType name="fileGrpType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;choice&gt;
 *         &lt;element name="fileGrp" type="{http://www.loc.gov/METS/}fileGrpType" maxOccurs="unbounded" minOccurs="0"/&gt;
 *         &lt;element name="file" type="{http://www.loc.gov/METS/}fileType" maxOccurs="unbounded" minOccurs="0"/&gt;
 *       &lt;/choice&gt;
 *       &lt;attribute name="ID" type="{http://www.w3.org/2001/XMLSchema}ID" /&gt;
 *       &lt;attribute name="VERSDATE" type="{http://www.w3.org/2001/XMLSchema}dateTime" /&gt;
 *       &lt;attribute name="ADMID" type="{http://www.w3.org/2001/XMLSchema}IDREFS" /&gt;
 *       &lt;attribute name="USE" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *       &lt;anyAttribute processContents='lax' namespace='##other'/&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "fileGrpType", propOrder = {
    "fileGrp",
    "file"
})
@XmlSeeAlso({
    MetsType.FileSec.FileGrp.class
})
public class FileGrpType {

    protected List<FileGrpType> fileGrp;
    protected List<FileType> file;
    @XmlAttribute(name = "ID")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlID
    @XmlSchemaType(name = "ID")
    protected String id;
    @XmlAttribute(name = "VERSDATE")
    @XmlSchemaType(name = "dateTime")
    protected LocalDateTime versdate;
    @XmlAttribute(name = "ADMID")
    @XmlIDREF
    @XmlSchemaType(name = "IDREFS")
    protected List<Object> admid;
    @XmlAttribute(name = "USE")
    protected String use;
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
     * <pre>
     *    getFileGrp().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link FileGrpType }
     *
     *
     */
    public List<FileGrpType> getFileGrp() {
        if (fileGrp == null) {
            fileGrp = new ArrayList<>();
        }
        return this.fileGrp;
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
     * Obtient la valeur de la propriété versdate.
     *
     * @return
     *     possible object is
     *     {@link LocalDateTime }
     *
     */
    public LocalDateTime getVERSDATE() {
        return versdate;
    }

    /**
     * Définit la valeur de la propriété versdate.
     *
     * @param value
     *     allowed object is
     *     {@link LocalDateTime }
     *
     */
    public void setVERSDATE(LocalDateTime value) {
        this.versdate = value;
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

}
