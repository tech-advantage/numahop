//
// Ce fichier a été généré par l'implémentation de référence JavaTM Architecture for XML Binding (JAXB), v2.2.11
// Voir <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a>
// Toute modification apportée à ce fichier sera perdue lors de la recompilation du schéma source.
// Généré le : 2017.02.10 à 05:24:22 PM CET
//


package fr.progilone.pgcn.domain.jaxb.facile;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Classe Java pour validatorType complex type.
 *
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 *
 * <pre>
 * &lt;complexType name="validatorType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element ref="{http://facile.cines.fr}fileName"/&gt;
 *         &lt;element ref="{http://facile.cines.fr}fileSize"/&gt;
 *         &lt;element ref="{http://facile.cines.fr}version"/&gt;
 *         &lt;element ref="{http://facile.cines.fr}wellformed"/&gt;
 *         &lt;element ref="{http://facile.cines.fr}valid"/&gt;
 *         &lt;element ref="{http://facile.cines.fr}message"/&gt;
 *         &lt;element ref="{http://facile.cines.fr}md5sum"/&gt;
 *         &lt;element ref="{http://facile.cines.fr}sha256sum"/&gt;
 *         &lt;element ref="{http://facile.cines.fr}note" maxOccurs="unbounded" minOccurs="0"/&gt;
 *         &lt;element ref="{http://facile.cines.fr}archivable"/&gt;
 *         &lt;element ref="{http://facile.cines.fr}format"/&gt;
 *         &lt;element ref="{http://facile.cines.fr}encoding"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "validatorType", propOrder = {
    "fileName",
    "fileSize",
    "version",
    "wellformed",
    "valid",
    "message",
    "md5Sum",
    "sha256Sum",
    "note",
    "archivable",
    "format",
    "encoding"
})
public class ValidatorType {

    @XmlElement(required = true)
    protected String fileName;
    protected int fileSize;
    @XmlElement(required = true)
    protected String version;
    protected boolean wellformed;
    protected boolean valid;
    @XmlElement(required = true)
    protected String message;
    @XmlElement(name = "md5sum", required = true)
    protected String md5Sum;
    @XmlElement(name = "sha256sum", required = true)
    protected String sha256Sum;
    protected List<String> note;
    protected boolean archivable;
    @XmlElement(required = true)
    protected String format;
    @XmlElement(required = true)
    protected String encoding;

    /**
     * Obtient la valeur de la propriété fileName.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getFileName() {
        return fileName;
    }

    /**
     * Définit la valeur de la propriété fileName.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setFileName(String value) {
        this.fileName = value;
    }

    /**
     * Obtient la valeur de la propriété fileSize.
     *
     */
    public int getFileSize() {
        return fileSize;
    }

    /**
     * Définit la valeur de la propriété fileSize.
     *
     */
    public void setFileSize(int value) {
        this.fileSize = value;
    }

    /**
     * Obtient la valeur de la propriété version.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getVersion() {
        return version;
    }

    /**
     * Définit la valeur de la propriété version.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setVersion(String value) {
        this.version = value;
    }

    /**
     * Obtient la valeur de la propriété wellformed.
     *
     */
    public boolean isWellformed() {
        return wellformed;
    }

    /**
     * Définit la valeur de la propriété wellformed.
     *
     */
    public void setWellformed(boolean value) {
        this.wellformed = value;
    }

    /**
     * Obtient la valeur de la propriété valid.
     *
     */
    public boolean isValid() {
        return valid;
    }

    /**
     * Définit la valeur de la propriété valid.
     *
     */
    public void setValid(boolean value) {
        this.valid = value;
    }

    /**
     * Obtient la valeur de la propriété message.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getMessage() {
        return message;
    }

    /**
     * Définit la valeur de la propriété message.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setMessage(String value) {
        this.message = value;
    }

    /**
     * Obtient la valeur de la propriété md5Sum.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getMd5Sum() {
        return md5Sum;
    }

    /**
     * Définit la valeur de la propriété md5Sum.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setMd5Sum(String value) {
        this.md5Sum = value;
    }

    /**
     * Obtient la valeur de la propriété sha256Sum.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getSha256Sum() {
        return sha256Sum;
    }

    /**
     * Définit la valeur de la propriété sha256Sum.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setSha256Sum(String value) {
        this.sha256Sum = value;
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
     * <pre>
     *    getNote().add(newItem);
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
     * Obtient la valeur de la propriété archivable.
     *
     */
    public boolean isArchivable() {
        return archivable;
    }

    /**
     * Définit la valeur de la propriété archivable.
     *
     */
    public void setArchivable(boolean value) {
        this.archivable = value;
    }

    /**
     * Obtient la valeur de la propriété format.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getFormat() {
        return format;
    }

    /**
     * Définit la valeur de la propriété format.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setFormat(String value) {
        this.format = value;
    }

    /**
     * Obtient la valeur de la propriété encoding.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getEncoding() {
        return encoding;
    }

    /**
     * Définit la valeur de la propriété encoding.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setEncoding(String value) {
        this.encoding = value;
    }

}
