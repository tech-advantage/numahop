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
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlElements;
import jakarta.xml.bind.annotation.XmlID;
import jakarta.xml.bind.annotation.XmlSchemaType;
import jakarta.xml.bind.annotation.XmlType;
import jakarta.xml.bind.annotation.adapters.CollapsedStringAdapter;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.xml.namespace.QName;

/**
 * parType: Complex Type for Parallel Files
 * The <par> or parallel files element aggregates pointers to files, parts of files, and/or sequences of files or parts of files that must be played
 * or displayed simultaneously to manifest a block of digital content represented by an <fptr> element.
 *
 *
 * <p>
 * Classe Java pour parType complex type.
 *
 * <p>
 * Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 *
 * <pre>
 * &lt;complexType name="parType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;choice maxOccurs="unbounded"&gt;
 *         &lt;element name="area" type="{http://www.loc.gov/METS/}areaType" minOccurs="0"/&gt;
 *         &lt;element name="seq" type="{http://www.loc.gov/METS/}seqType" minOccurs="0"/&gt;
 *       &lt;/choice&gt;
 *       &lt;attGroup ref="{http://www.loc.gov/METS/}ORDERLABELS"/&gt;
 *       &lt;attribute name="ID" type="{http://www.w3.org/2001/XMLSchema}ID" /&gt;
 *       &lt;anyAttribute processContents='lax' namespace='##other'/&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "parType", propOrder = {"areaOrSeq"})
public class ParType {

    @XmlElements({@XmlElement(name = "area", type = AreaType.class),
                  @XmlElement(name = "seq", type = SeqType.class)})
    protected List<Object> areaOrSeq;
    @XmlAttribute(name = "ID")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlID
    @XmlSchemaType(name = "ID")
    protected String id;
    @XmlAttribute(name = "ORDER")
    protected BigInteger order;
    @XmlAttribute(name = "ORDERLABEL")
    protected String orderlabel;
    @XmlAttribute(name = "LABEL")
    protected String label;
    @XmlAnyAttribute
    private Map<QName, String> otherAttributes = new HashMap<>();

    /**
     * Gets the value of the areaOrSeq property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the areaOrSeq property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     *
     * <pre>
     * getAreaOrSeq().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link AreaType }
     * {@link SeqType }
     *
     *
     */
    public List<Object> getAreaOrSeq() {
        if (areaOrSeq == null) {
            areaOrSeq = new ArrayList<>();
        }
        return this.areaOrSeq;
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
     * Obtient la valeur de la propriété order.
     *
     * @return
     *         possible object is
     *         {@link BigInteger }
     *
     */
    public BigInteger getORDER() {
        return order;
    }

    /**
     * Définit la valeur de la propriété order.
     *
     * @param value
     *            allowed object is
     *            {@link BigInteger }
     *
     */
    public void setORDER(BigInteger value) {
        this.order = value;
    }

    /**
     * Obtient la valeur de la propriété orderlabel.
     *
     * @return
     *         possible object is
     *         {@link String }
     *
     */
    public String getORDERLABEL() {
        return orderlabel;
    }

    /**
     * Définit la valeur de la propriété orderlabel.
     *
     * @param value
     *            allowed object is
     *            {@link String }
     *
     */
    public void setORDERLABEL(String value) {
        this.orderlabel = value;
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
