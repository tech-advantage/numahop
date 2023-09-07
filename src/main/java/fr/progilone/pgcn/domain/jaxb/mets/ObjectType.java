//
// Ce fichier a été généré par l'implémentation de référence JavaTM Architecture for XML Binding (JAXB), v2.2.11
// Voir <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a>
// Toute modification apportée à ce fichier sera perdue lors de la recompilation du schéma source.
// Généré le : 2016.12.29 à 11:54:46 AM CET
//

package fr.progilone.pgcn.domain.jaxb.mets;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlID;
import jakarta.xml.bind.annotation.XmlSchemaType;
import jakarta.xml.bind.annotation.XmlType;
import jakarta.xml.bind.annotation.adapters.CollapsedStringAdapter;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

/**
 * objectType: complexType for interfaceDef and mechanism elements
 * The mechanism and behavior elements point to external objects--an interface definition object or an executable code object respectively--which
 * together constitute a behavior that can be applied to one or more <div> elements in a <structMap>.
 *
 *
 * <p>
 * Classe Java pour objectType complex type.
 *
 * <p>
 * Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 *
 * <pre>
 * &lt;complexType name="objectType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;attGroup ref="{http://www.w3.org/1999/xlink}simpleLink"/&gt;
 *       &lt;attGroup ref="{http://www.loc.gov/METS/}LOCATION"/&gt;
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
@XmlType(name = "objectType")
public class ObjectType {

    @XmlAttribute(name = "ID")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlID
    @XmlSchemaType(name = "ID")
    protected String id;
    @XmlAttribute(name = "LABEL")
    protected String label;
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
    @XmlAttribute(name = "LOCTYPE", required = true)
    protected String loctype;
    @XmlAttribute(name = "OTHERLOCTYPE")
    protected String otherloctype;

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
     *            allowed object is
     *            {@link String }
     *
     */
    public void setType(String value) {
        this.type = value;
    }

    /**
     * Obtient la valeur de la propriété href.
     *
     * @return
     *         possible object is
     *         {@link String }
     *
     */
    public String getHref() {
        return href;
    }

    /**
     * Définit la valeur de la propriété href.
     *
     * @param value
     *            allowed object is
     *            {@link String }
     *
     */
    public void setHref(String value) {
        this.href = value;
    }

    /**
     * Obtient la valeur de la propriété role.
     *
     * @return
     *         possible object is
     *         {@link String }
     *
     */
    public String getRole() {
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
    public void setRole(String value) {
        this.role = value;
    }

    /**
     * Obtient la valeur de la propriété arcrole.
     *
     * @return
     *         possible object is
     *         {@link String }
     *
     */
    public String getArcrole() {
        return arcrole;
    }

    /**
     * Définit la valeur de la propriété arcrole.
     *
     * @param value
     *            allowed object is
     *            {@link String }
     *
     */
    public void setArcrole(String value) {
        this.arcrole = value;
    }

    /**
     * Obtient la valeur de la propriété title.
     *
     * @return
     *         possible object is
     *         {@link String }
     *
     */
    public String getTitle() {
        return title;
    }

    /**
     * Définit la valeur de la propriété title.
     *
     * @param value
     *            allowed object is
     *            {@link String }
     *
     */
    public void setTitle(String value) {
        this.title = value;
    }

    /**
     * Obtient la valeur de la propriété show.
     *
     * @return
     *         possible object is
     *         {@link String }
     *
     */
    public String getShow() {
        return show;
    }

    /**
     * Définit la valeur de la propriété show.
     *
     * @param value
     *            allowed object is
     *            {@link String }
     *
     */
    public void setShow(String value) {
        this.show = value;
    }

    /**
     * Obtient la valeur de la propriété actuate.
     *
     * @return
     *         possible object is
     *         {@link String }
     *
     */
    public String getActuate() {
        return actuate;
    }

    /**
     * Définit la valeur de la propriété actuate.
     *
     * @param value
     *            allowed object is
     *            {@link String }
     *
     */
    public void setActuate(String value) {
        this.actuate = value;
    }

    /**
     * Obtient la valeur de la propriété loctype.
     *
     * @return
     *         possible object is
     *         {@link String }
     *
     */
    public String getLOCTYPE() {
        return loctype;
    }

    /**
     * Définit la valeur de la propriété loctype.
     *
     * @param value
     *            allowed object is
     *            {@link String }
     *
     */
    public void setLOCTYPE(String value) {
        this.loctype = value;
    }

    /**
     * Obtient la valeur de la propriété otherloctype.
     *
     * @return
     *         possible object is
     *         {@link String }
     *
     */
    public String getOTHERLOCTYPE() {
        return otherloctype;
    }

    /**
     * Définit la valeur de la propriété otherloctype.
     *
     * @param value
     *            allowed object is
     *            {@link String }
     *
     */
    public void setOTHERLOCTYPE(String value) {
        this.otherloctype = value;
    }

}
