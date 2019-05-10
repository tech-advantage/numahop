//
// Ce fichier a été généré par l'implémentation de référence JavaTM Architecture for XML Binding (JAXB), v2.2.8-b130911.1802
// Voir <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a>
// Toute modification apportée à ce fichier sera perdue lors de la recompilation du schéma source.
// Généré le : 2018.08.13 à 01:08:19 PM CEST
//


package fr.progilone.pgcn.domain.jaxb.oaipmh;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Classe Java pour setType complex type.
 *
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 *
 * <pre>
 * &lt;complexType name="setType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="setSpec" type="{http://www.openarchives.org/OAI/2.0/}setSpecType"/>
 *         &lt;element name="setName" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="setDescription" type="{http://www.openarchives.org/OAI/2.0/}descriptionType" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "setType", propOrder = {
    "setSpec",
    "setName",
    "setDescription"
})
public class SetType {

    @XmlElement(required = true)
    protected String setSpec;
    @XmlElement(required = true)
    protected String setName;
    protected List<DescriptionType> setDescription;

    /**
     * Obtient la valeur de la propriété setSpec.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getSetSpec() {
        return setSpec;
    }

    /**
     * Définit la valeur de la propriété setSpec.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setSetSpec(String value) {
        this.setSpec = value;
    }

    /**
     * Obtient la valeur de la propriété setName.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getSetName() {
        return setName;
    }

    /**
     * Définit la valeur de la propriété setName.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setSetName(String value) {
        this.setName = value;
    }

    /**
     * Gets the value of the setDescription property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the setDescription property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getSetDescription().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link DescriptionType }
     *
     *
     */
    public List<DescriptionType> getSetDescription() {
        if (setDescription == null) {
            setDescription = new ArrayList<>();
        }
        return this.setDescription;
    }

}
