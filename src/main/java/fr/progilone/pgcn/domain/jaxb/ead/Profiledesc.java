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
import jakarta.xml.bind.annotation.XmlID;
import jakarta.xml.bind.annotation.XmlSchemaType;
import jakarta.xml.bind.annotation.XmlType;
import jakarta.xml.bind.annotation.adapters.CollapsedStringAdapter;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

/**
 * <p>
 * Classe Java pour profiledesc complex type.
 *
 * <p>
 * Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 *
 * <pre>
 * &lt;complexType name="profiledesc">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="creation" type="{urn:isbn:1-931666-22-9}creation" minOccurs="0"/>
 *         &lt;element name="langusage" type="{urn:isbn:1-931666-22-9}langusage" minOccurs="0"/>
 *         &lt;element name="descrules" type="{urn:isbn:1-931666-22-9}descrules" minOccurs="0"/>
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
@XmlType(name = "profiledesc",
         propOrder = {"creation",
                      "langusage",
                      "descrules"})
public class Profiledesc {

    protected Creation creation;
    protected Langusage langusage;
    protected Descrules descrules;
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
     * Obtient la valeur de la propriété creation.
     *
     * @return
     *         possible object is
     *         {@link Creation }
     *
     */
    public Creation getCreation() {
        return creation;
    }

    /**
     * Définit la valeur de la propriété creation.
     *
     * @param value
     *            allowed object is
     *            {@link Creation }
     *
     */
    public void setCreation(Creation value) {
        this.creation = value;
    }

    /**
     * Obtient la valeur de la propriété langusage.
     *
     * @return
     *         possible object is
     *         {@link Langusage }
     *
     */
    public Langusage getLangusage() {
        return langusage;
    }

    /**
     * Définit la valeur de la propriété langusage.
     *
     * @param value
     *            allowed object is
     *            {@link Langusage }
     *
     */
    public void setLangusage(Langusage value) {
        this.langusage = value;
    }

    /**
     * Obtient la valeur de la propriété descrules.
     *
     * @return
     *         possible object is
     *         {@link Descrules }
     *
     */
    public Descrules getDescrules() {
        return descrules;
    }

    /**
     * Définit la valeur de la propriété descrules.
     *
     * @param value
     *            allowed object is
     *            {@link Descrules }
     *
     */
    public void setDescrules(Descrules value) {
        this.descrules = value;
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
