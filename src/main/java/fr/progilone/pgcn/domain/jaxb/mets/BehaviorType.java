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
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlID;
import jakarta.xml.bind.annotation.XmlIDREF;
import jakarta.xml.bind.annotation.XmlSchemaType;
import jakarta.xml.bind.annotation.XmlType;
import jakarta.xml.bind.annotation.adapters.CollapsedStringAdapter;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * behaviorType: Complex Type for Behaviors
 * A behavior can be used to associate executable behaviors with content in the METS object. A behavior element has an interface definition element
 * that represents an abstract definition of the set of behaviors represented by a particular behavior. A behavior element also has an behavior
 * mechanism which is a module of executable code that implements and runs the behavior defined abstractly by the interface definition.
 *
 *
 * <p>
 * Classe Java pour behaviorType complex type.
 *
 * <p>
 * Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 *
 * <pre>
 * &lt;complexType name="behaviorType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="interfaceDef" type="{http://www.loc.gov/METS/}objectType" minOccurs="0"/&gt;
 *         &lt;element name="mechanism" type="{http://www.loc.gov/METS/}objectType"/&gt;
 *       &lt;/sequence&gt;
 *       &lt;attribute name="ID" type="{http://www.w3.org/2001/XMLSchema}ID" /&gt;
 *       &lt;attribute name="STRUCTID" type="{http://www.w3.org/2001/XMLSchema}IDREFS" /&gt;
 *       &lt;attribute name="BTYPE" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *       &lt;attribute name="CREATED" type="{http://www.w3.org/2001/XMLSchema}dateTime" /&gt;
 *       &lt;attribute name="LABEL" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *       &lt;attribute name="GROUPID" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *       &lt;attribute name="ADMID" type="{http://www.w3.org/2001/XMLSchema}IDREFS" /&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "behaviorType",
         propOrder = {"interfaceDef",
                      "mechanism"})
public class BehaviorType {

    protected ObjectType interfaceDef;
    @XmlElement(required = true)
    protected ObjectType mechanism;
    @XmlAttribute(name = "ID")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlID
    @XmlSchemaType(name = "ID")
    protected String id;
    @XmlAttribute(name = "STRUCTID")
    @XmlIDREF
    @XmlSchemaType(name = "IDREFS")
    protected List<Object> structid;
    @XmlAttribute(name = "BTYPE")
    protected String btype;
    @XmlAttribute(name = "CREATED")
    @XmlSchemaType(name = "dateTime")
    protected LocalDateTime created;
    @XmlAttribute(name = "LABEL")
    protected String label;
    @XmlAttribute(name = "GROUPID")
    protected String groupid;
    @XmlAttribute(name = "ADMID")
    @XmlIDREF
    @XmlSchemaType(name = "IDREFS")
    protected List<Object> admid;

    /**
     * Obtient la valeur de la propriété interfaceDef.
     *
     * @return
     *         possible object is
     *         {@link ObjectType }
     *
     */
    public ObjectType getInterfaceDef() {
        return interfaceDef;
    }

    /**
     * Définit la valeur de la propriété interfaceDef.
     *
     * @param value
     *            allowed object is
     *            {@link ObjectType }
     *
     */
    public void setInterfaceDef(ObjectType value) {
        this.interfaceDef = value;
    }

    /**
     * Obtient la valeur de la propriété mechanism.
     *
     * @return
     *         possible object is
     *         {@link ObjectType }
     *
     */
    public ObjectType getMechanism() {
        return mechanism;
    }

    /**
     * Définit la valeur de la propriété mechanism.
     *
     * @param value
     *            allowed object is
     *            {@link ObjectType }
     *
     */
    public void setMechanism(ObjectType value) {
        this.mechanism = value;
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
     * Gets the value of the structid property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the structid property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     *
     * <pre>
     * getSTRUCTID().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Object }
     *
     *
     */
    public List<Object> getSTRUCTID() {
        if (structid == null) {
            structid = new ArrayList<>();
        }
        return this.structid;
    }

    /**
     * Obtient la valeur de la propriété btype.
     *
     * @return
     *         possible object is
     *         {@link String }
     *
     */
    public String getBTYPE() {
        return btype;
    }

    /**
     * Définit la valeur de la propriété btype.
     *
     * @param value
     *            allowed object is
     *            {@link String }
     *
     */
    public void setBTYPE(String value) {
        this.btype = value;
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
     * Obtient la valeur de la propriété groupid.
     *
     * @return
     *         possible object is
     *         {@link String }
     *
     */
    public String getGROUPID() {
        return groupid;
    }

    /**
     * Définit la valeur de la propriété groupid.
     *
     * @param value
     *            allowed object is
     *            {@link String }
     *
     */
    public void setGROUPID(String value) {
        this.groupid = value;
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

}
