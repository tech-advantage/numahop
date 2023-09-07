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

/**
 * <p>
 * Classe Java pour filedesc complex type.
 *
 * <p>
 * Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 *
 * <pre>
 * &lt;complexType name="filedesc">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="titlestmt" type="{urn:isbn:1-931666-22-9}titlestmt"/>
 *         &lt;element name="editionstmt" type="{urn:isbn:1-931666-22-9}editionstmt" minOccurs="0"/>
 *         &lt;element name="publicationstmt" type="{urn:isbn:1-931666-22-9}publicationstmt" minOccurs="0"/>
 *         &lt;element name="seriesstmt" type="{urn:isbn:1-931666-22-9}seriesstmt" minOccurs="0"/>
 *         &lt;element name="notestmt" type="{urn:isbn:1-931666-22-9}notestmt" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attGroup ref="{urn:isbn:1-931666-22-9}a.common"/>
 *       &lt;attribute name="encodinganalog" type="{http://www.w3.org/2001/XMLSchema}anySimpleType" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "filedesc",
         propOrder = {"titlestmt",
                      "editionstmt",
                      "publicationstmt",
                      "seriesstmt",
                      "notestmt"})
public class Filedesc {

    @XmlElement(required = true)
    protected Titlestmt titlestmt;
    protected Editionstmt editionstmt;
    protected Publicationstmt publicationstmt;
    protected Seriesstmt seriesstmt;
    protected Notestmt notestmt;
    @XmlAttribute(name = "encodinganalog")
    @XmlSchemaType(name = "anySimpleType")
    protected String encodinganalog;
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
     * Obtient la valeur de la propriété titlestmt.
     *
     * @return
     *         possible object is
     *         {@link Titlestmt }
     *
     */
    public Titlestmt getTitlestmt() {
        return titlestmt;
    }

    /**
     * Définit la valeur de la propriété titlestmt.
     *
     * @param value
     *            allowed object is
     *            {@link Titlestmt }
     *
     */
    public void setTitlestmt(Titlestmt value) {
        this.titlestmt = value;
    }

    /**
     * Obtient la valeur de la propriété editionstmt.
     *
     * @return
     *         possible object is
     *         {@link Editionstmt }
     *
     */
    public Editionstmt getEditionstmt() {
        return editionstmt;
    }

    /**
     * Définit la valeur de la propriété editionstmt.
     *
     * @param value
     *            allowed object is
     *            {@link Editionstmt }
     *
     */
    public void setEditionstmt(Editionstmt value) {
        this.editionstmt = value;
    }

    /**
     * Obtient la valeur de la propriété publicationstmt.
     *
     * @return
     *         possible object is
     *         {@link Publicationstmt }
     *
     */
    public Publicationstmt getPublicationstmt() {
        return publicationstmt;
    }

    /**
     * Définit la valeur de la propriété publicationstmt.
     *
     * @param value
     *            allowed object is
     *            {@link Publicationstmt }
     *
     */
    public void setPublicationstmt(Publicationstmt value) {
        this.publicationstmt = value;
    }

    /**
     * Obtient la valeur de la propriété seriesstmt.
     *
     * @return
     *         possible object is
     *         {@link Seriesstmt }
     *
     */
    public Seriesstmt getSeriesstmt() {
        return seriesstmt;
    }

    /**
     * Définit la valeur de la propriété seriesstmt.
     *
     * @param value
     *            allowed object is
     *            {@link Seriesstmt }
     *
     */
    public void setSeriesstmt(Seriesstmt value) {
        this.seriesstmt = value;
    }

    /**
     * Obtient la valeur de la propriété notestmt.
     *
     * @return
     *         possible object is
     *         {@link Notestmt }
     *
     */
    public Notestmt getNotestmt() {
        return notestmt;
    }

    /**
     * Définit la valeur de la propriété notestmt.
     *
     * @param value
     *            allowed object is
     *            {@link Notestmt }
     *
     */
    public void setNotestmt(Notestmt value) {
        this.notestmt = value;
    }

    /**
     * Obtient la valeur de la propriété encodinganalog.
     *
     * @return
     *         possible object is
     *         {@link String }
     *
     */
    public String getEncodinganalog() {
        return encodinganalog;
    }

    /**
     * Définit la valeur de la propriété encodinganalog.
     *
     * @param value
     *            allowed object is
     *            {@link String }
     *
     */
    public void setEncodinganalog(String value) {
        this.encodinganalog = value;
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
