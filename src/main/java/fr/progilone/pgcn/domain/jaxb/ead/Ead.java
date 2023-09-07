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
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlSchemaType;
import jakarta.xml.bind.annotation.XmlType;
import jakarta.xml.bind.annotation.adapters.CollapsedStringAdapter;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

/**
 * <p>
 * Classe Java pour anonymous complex type.
 *
 * <p>
 * Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 *
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="eadheader" type="{urn:isbn:1-931666-22-9}eadheader"/>
 *         &lt;element name="frontmatter" type="{urn:isbn:1-931666-22-9}frontmatter" minOccurs="0"/>
 *         &lt;element name="archdesc" type="{urn:isbn:1-931666-22-9}archdesc"/>
 *       &lt;/sequence>
 *       &lt;attGroup ref="{urn:isbn:1-931666-22-9}a.common"/>
 *       &lt;attribute name="relatedencoding" type="{http://www.w3.org/2001/XMLSchema}anySimpleType" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "",
         propOrder = {"eadheader",
                      "frontmatter",
                      "archdesc"})
@XmlRootElement(name = "ead")
public class Ead {

    @XmlElement(required = true)
    protected Eadheader eadheader;
    protected Frontmatter frontmatter;
    @XmlElement(required = true)
    protected Archdesc archdesc;
    @XmlAttribute(name = "relatedencoding")
    @XmlSchemaType(name = "anySimpleType")
    protected String relatedencoding;
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
     * Obtient la valeur de la propriété eadheader.
     *
     * @return
     *         possible object is
     *         {@link Eadheader }
     *
     */
    public Eadheader getEadheader() {
        return eadheader;
    }

    /**
     * Définit la valeur de la propriété eadheader.
     *
     * @param value
     *            allowed object is
     *            {@link Eadheader }
     *
     */
    public void setEadheader(Eadheader value) {
        this.eadheader = value;
    }

    /**
     * Obtient la valeur de la propriété frontmatter.
     *
     * @return
     *         possible object is
     *         {@link Frontmatter }
     *
     */
    public Frontmatter getFrontmatter() {
        return frontmatter;
    }

    /**
     * Définit la valeur de la propriété frontmatter.
     *
     * @param value
     *            allowed object is
     *            {@link Frontmatter }
     *
     */
    public void setFrontmatter(Frontmatter value) {
        this.frontmatter = value;
    }

    /**
     * Obtient la valeur de la propriété archdesc.
     *
     * @return
     *         possible object is
     *         {@link Archdesc }
     *
     */
    public Archdesc getArchdesc() {
        return archdesc;
    }

    /**
     * Définit la valeur de la propriété archdesc.
     *
     * @param value
     *            allowed object is
     *            {@link Archdesc }
     *
     */
    public void setArchdesc(Archdesc value) {
        this.archdesc = value;
    }

    /**
     * Obtient la valeur de la propriété relatedencoding.
     *
     * @return
     *         possible object is
     *         {@link String }
     *
     */
    public String getRelatedencoding() {
        return relatedencoding;
    }

    /**
     * Définit la valeur de la propriété relatedencoding.
     *
     * @param value
     *            allowed object is
     *            {@link String }
     *
     */
    public void setRelatedencoding(String value) {
        this.relatedencoding = value;
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
