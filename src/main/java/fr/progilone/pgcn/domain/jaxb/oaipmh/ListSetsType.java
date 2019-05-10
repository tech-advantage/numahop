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
 * <p>Classe Java pour ListSetsType complex type.
 *
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 *
 * <pre>
 * &lt;complexType name="ListSetsType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="set" type="{http://www.openarchives.org/OAI/2.0/}setType" maxOccurs="unbounded"/>
 *         &lt;element name="resumptionToken" type="{http://www.openarchives.org/OAI/2.0/}resumptionTokenType" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ListSetsType", propOrder = {
    "set",
    "resumptionToken"
})
public class ListSetsType {

    @XmlElement(required = true)
    protected List<SetType> set;
    protected ResumptionTokenType resumptionToken;

    /**
     * Gets the value of the set property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the set property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getSet().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link SetType }
     *
     *
     */
    public List<SetType> getSet() {
        if (set == null) {
            set = new ArrayList<>();
        }
        return this.set;
    }

    /**
     * Obtient la valeur de la propriété resumptionToken.
     *
     * @return
     *     possible object is
     *     {@link ResumptionTokenType }
     *
     */
    public ResumptionTokenType getResumptionToken() {
        return resumptionToken;
    }

    /**
     * Définit la valeur de la propriété resumptionToken.
     *
     * @param value
     *     allowed object is
     *     {@link ResumptionTokenType }
     *
     */
    public void setResumptionToken(ResumptionTokenType value) {
        this.resumptionToken = value;
    }

}
