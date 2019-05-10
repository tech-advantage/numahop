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
import javax.xml.bind.annotation.XmlID;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;


/**
 * <p>Classe Java pour eadheader complex type.
 *
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 *
 * <pre>
 * &lt;complexType name="eadheader">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="eadid" type="{urn:isbn:1-931666-22-9}eadid"/>
 *         &lt;element name="filedesc" type="{urn:isbn:1-931666-22-9}filedesc"/>
 *         &lt;element name="profiledesc" type="{urn:isbn:1-931666-22-9}profiledesc" minOccurs="0"/>
 *         &lt;element name="revisiondesc" type="{urn:isbn:1-931666-22-9}revisiondesc" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attGroup ref="{urn:isbn:1-931666-22-9}a.common"/>
 *       &lt;attribute name="langencoding" type="{http://www.w3.org/2001/XMLSchema}NMTOKEN" default="iso639-2b" />
 *       &lt;attribute name="scriptencoding" type="{http://www.w3.org/2001/XMLSchema}NMTOKEN" default="iso15924" />
 *       &lt;attribute name="dateencoding" type="{http://www.w3.org/2001/XMLSchema}NMTOKEN" default="iso8601" />
 *       &lt;attribute name="countryencoding" type="{http://www.w3.org/2001/XMLSchema}NMTOKEN" default="iso3166-1" />
 *       &lt;attribute name="repositoryencoding" type="{http://www.w3.org/2001/XMLSchema}NMTOKEN" default="iso15511" />
 *       &lt;attribute name="relatedencoding" type="{http://www.w3.org/2001/XMLSchema}anySimpleType" />
 *       &lt;attribute name="findaidstatus" type="{http://www.w3.org/2001/XMLSchema}NMTOKEN" />
 *       &lt;attribute name="encodinganalog" type="{http://www.w3.org/2001/XMLSchema}anySimpleType" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "eadheader", propOrder = {
    "eadid",
    "filedesc",
    "profiledesc",
    "revisiondesc"
})
@XmlRootElement(name = "eadheader")
public class Eadheader {

    @XmlElement(required = true)
    protected Eadid eadid;
    @XmlElement(required = true)
    protected Filedesc filedesc;
    protected Profiledesc profiledesc;
    protected Revisiondesc revisiondesc;
    @XmlAttribute(name = "langencoding")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlSchemaType(name = "NMTOKEN")
    protected String langencoding;
    @XmlAttribute(name = "scriptencoding")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlSchemaType(name = "NMTOKEN")
    protected String scriptencoding;
    @XmlAttribute(name = "dateencoding")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlSchemaType(name = "NMTOKEN")
    protected String dateencoding;
    @XmlAttribute(name = "countryencoding")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlSchemaType(name = "NMTOKEN")
    protected String countryencoding;
    @XmlAttribute(name = "repositoryencoding")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlSchemaType(name = "NMTOKEN")
    protected String repositoryencoding;
    @XmlAttribute(name = "relatedencoding")
    @XmlSchemaType(name = "anySimpleType")
    protected String relatedencoding;
    @XmlAttribute(name = "findaidstatus")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlSchemaType(name = "NMTOKEN")
    protected String findaidstatus;
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
     * Obtient la valeur de la propriété eadid.
     *
     * @return
     *     possible object is
     *     {@link Eadid }
     *
     */
    public Eadid getEadid() {
        return eadid;
    }

    /**
     * Définit la valeur de la propriété eadid.
     *
     * @param value
     *     allowed object is
     *     {@link Eadid }
     *
     */
    public void setEadid(Eadid value) {
        this.eadid = value;
    }

    /**
     * Obtient la valeur de la propriété filedesc.
     *
     * @return
     *     possible object is
     *     {@link Filedesc }
     *
     */
    public Filedesc getFiledesc() {
        return filedesc;
    }

    /**
     * Définit la valeur de la propriété filedesc.
     *
     * @param value
     *     allowed object is
     *     {@link Filedesc }
     *
     */
    public void setFiledesc(Filedesc value) {
        this.filedesc = value;
    }

    /**
     * Obtient la valeur de la propriété profiledesc.
     *
     * @return
     *     possible object is
     *     {@link Profiledesc }
     *
     */
    public Profiledesc getProfiledesc() {
        return profiledesc;
    }

    /**
     * Définit la valeur de la propriété profiledesc.
     *
     * @param value
     *     allowed object is
     *     {@link Profiledesc }
     *
     */
    public void setProfiledesc(Profiledesc value) {
        this.profiledesc = value;
    }

    /**
     * Obtient la valeur de la propriété revisiondesc.
     *
     * @return
     *     possible object is
     *     {@link Revisiondesc }
     *
     */
    public Revisiondesc getRevisiondesc() {
        return revisiondesc;
    }

    /**
     * Définit la valeur de la propriété revisiondesc.
     *
     * @param value
     *     allowed object is
     *     {@link Revisiondesc }
     *
     */
    public void setRevisiondesc(Revisiondesc value) {
        this.revisiondesc = value;
    }

    /**
     * Obtient la valeur de la propriété langencoding.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getLangencoding() {
        if (langencoding == null) {
            return "iso639-2b";
        } else {
            return langencoding;
        }
    }

    /**
     * Définit la valeur de la propriété langencoding.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setLangencoding(String value) {
        this.langencoding = value;
    }

    /**
     * Obtient la valeur de la propriété scriptencoding.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getScriptencoding() {
        if (scriptencoding == null) {
            return "iso15924";
        } else {
            return scriptencoding;
        }
    }

    /**
     * Définit la valeur de la propriété scriptencoding.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setScriptencoding(String value) {
        this.scriptencoding = value;
    }

    /**
     * Obtient la valeur de la propriété dateencoding.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getDateencoding() {
        if (dateencoding == null) {
            return "iso8601";
        } else {
            return dateencoding;
        }
    }

    /**
     * Définit la valeur de la propriété dateencoding.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setDateencoding(String value) {
        this.dateencoding = value;
    }

    /**
     * Obtient la valeur de la propriété countryencoding.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getCountryencoding() {
        if (countryencoding == null) {
            return "iso3166-1";
        } else {
            return countryencoding;
        }
    }

    /**
     * Définit la valeur de la propriété countryencoding.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setCountryencoding(String value) {
        this.countryencoding = value;
    }

    /**
     * Obtient la valeur de la propriété repositoryencoding.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getRepositoryencoding() {
        if (repositoryencoding == null) {
            return "iso15511";
        } else {
            return repositoryencoding;
        }
    }

    /**
     * Définit la valeur de la propriété repositoryencoding.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setRepositoryencoding(String value) {
        this.repositoryencoding = value;
    }

    /**
     * Obtient la valeur de la propriété relatedencoding.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getRelatedencoding() {
        return relatedencoding;
    }

    /**
     * Définit la valeur de la propriété relatedencoding.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setRelatedencoding(String value) {
        this.relatedencoding = value;
    }

    /**
     * Obtient la valeur de la propriété findaidstatus.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getFindaidstatus() {
        return findaidstatus;
    }

    /**
     * Définit la valeur de la propriété findaidstatus.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setFindaidstatus(String value) {
        this.findaidstatus = value;
    }

    /**
     * Obtient la valeur de la propriété encodinganalog.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getEncodinganalog() {
        return encodinganalog;
    }

    /**
     * Définit la valeur de la propriété encodinganalog.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setEncodinganalog(String value) {
        this.encodinganalog = value;
    }

    /**
     * Obtient la valeur de la propriété id.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getId() {
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
    public void setId(String value) {
        this.id = value;
    }

    /**
     * Obtient la valeur de la propriété altrender.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getAltrender() {
        return altrender;
    }

    /**
     * Définit la valeur de la propriété altrender.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setAltrender(String value) {
        this.altrender = value;
    }

    /**
     * Obtient la valeur de la propriété audience.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getAudience() {
        return audience;
    }

    /**
     * Définit la valeur de la propriété audience.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setAudience(String value) {
        this.audience = value;
    }

}
