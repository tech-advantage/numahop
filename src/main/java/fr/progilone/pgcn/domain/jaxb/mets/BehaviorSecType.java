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
import jakarta.xml.bind.annotation.XmlID;
import jakarta.xml.bind.annotation.XmlSchemaType;
import jakarta.xml.bind.annotation.XmlType;
import jakarta.xml.bind.annotation.adapters.CollapsedStringAdapter;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.xml.namespace.QName;

/**
 * behaviorSecType: Complex Type for Behavior Sections
 * Behaviors are executable code which can be associated with parts of a METS object. The behaviorSec element is used to group individual behaviors
 * within a hierarchical structure. Such grouping can be useful to organize families of behaviors together or to indicate other relationships between
 * particular behaviors.
 *
 *
 * <p>
 * Classe Java pour behaviorSecType complex type.
 *
 * <p>
 * Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 *
 * <pre>
 * &lt;complexType name="behaviorSecType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="behaviorSec" type="{http://www.loc.gov/METS/}behaviorSecType" maxOccurs="unbounded" minOccurs="0"/&gt;
 *         &lt;element name="behavior" type="{http://www.loc.gov/METS/}behaviorType" maxOccurs="unbounded" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *       &lt;attribute name="ID" type="{http://www.w3.org/2001/XMLSchema}ID" /&gt;
 *       &lt;attribute name="CREATED" type="{http://www.w3.org/2001/XMLSchema}dateTime" /&gt;
 *       &lt;attribute name="LABEL" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *       &lt;anyAttribute processContents='lax' namespace='##other'/&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "behaviorSecType",
         propOrder = {"behaviorSec",
                      "behavior"})
public class BehaviorSecType {

    protected List<BehaviorSecType> behaviorSec;
    protected List<BehaviorType> behavior;
    @XmlAttribute(name = "ID")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlID
    @XmlSchemaType(name = "ID")
    protected String id;
    @XmlAttribute(name = "CREATED")
    @XmlSchemaType(name = "dateTime")
    protected LocalDateTime created;
    @XmlAttribute(name = "LABEL")
    protected String label;
    @XmlAnyAttribute
    private Map<QName, String> otherAttributes = new HashMap<>();

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
     * Gets the value of the behavior property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the behavior property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     *
     * <pre>
     * getBehavior().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link BehaviorType }
     *
     *
     */
    public List<BehaviorType> getBehavior() {
        if (behavior == null) {
            behavior = new ArrayList<>();
        }
        return this.behavior;
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
     * Obtient la valeur de la propriété created.
     *
     * @return
     *         possible object is
     *         {@link LocalDateTime }
     *
     */
    public LocalDateTime getCREATED() {
        return created;
    }

    /**
     * Définit la valeur de la propriété created.
     *
     * @param value
     *            allowed object is
     *            {@link LocalDateTime }
     *
     */
    public void setCREATED(LocalDateTime value) {
        this.created = value;
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

}
