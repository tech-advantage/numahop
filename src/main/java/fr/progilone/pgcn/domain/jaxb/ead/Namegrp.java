//
// Ce fichier a été généré par l'implémentation de référence JavaTM Architecture for XML Binding (JAXB), v2.2.8-b130911.1802
// Voir <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a>
// Toute modification apportée à ce fichier sera perdue lors de la recompilation du schéma source.
// Généré le : 2017.05.16 à 10:56:55 AM CEST
//


package fr.progilone.pgcn.domain.jaxb.ead;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlID;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;


/**
 * <p>Classe Java pour namegrp complex type.
 *
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 *
 * <pre>
 * &lt;complexType name="namegrp">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;choice maxOccurs="unbounded">
 *         &lt;group ref="{urn:isbn:1-931666-22-9}m.access.title"/>
 *         &lt;element name="note" type="{urn:isbn:1-931666-22-9}note"/>
 *       &lt;/choice>
 *       &lt;attGroup ref="{urn:isbn:1-931666-22-9}a.common"/>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "namegrp", propOrder = {
    "corpnameOrFamnameOrGeogname"
})
public class Namegrp {

    @XmlElements({
        @XmlElement(name = "corpname", type = Corpname.class),
        @XmlElement(name = "famname", type = Famname.class),
        @XmlElement(name = "geogname", type = Geogname.class),
        @XmlElement(name = "name", type = Name.class),
        @XmlElement(name = "occupation", type = Occupation.class),
        @XmlElement(name = "persname", type = Persname.class),
        @XmlElement(name = "subject", type = Subject.class),
        @XmlElement(name = "genreform", type = Genreform.class),
        @XmlElement(name = "function", type = Function.class),
        @XmlElement(name = "title", type = Title.class),
        @XmlElement(name = "note", type = Note.class)
    })
    protected List<Object> corpnameOrFamnameOrGeogname;
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
     * Gets the value of the corpnameOrFamnameOrGeogname property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the corpnameOrFamnameOrGeogname property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getCorpnameOrFamnameOrGeogname().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Corpname }
     * {@link Famname }
     * {@link Geogname }
     * {@link Name }
     * {@link Occupation }
     * {@link Persname }
     * {@link Subject }
     * {@link Genreform }
     * {@link Function }
     * {@link Title }
     * {@link Note }
     *
     *
     */
    public List<Object> getCorpnameOrFamnameOrGeogname() {
        if (corpnameOrFamnameOrGeogname == null) {
            corpnameOrFamnameOrGeogname = new ArrayList<>();
        }
        return this.corpnameOrFamnameOrGeogname;
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
