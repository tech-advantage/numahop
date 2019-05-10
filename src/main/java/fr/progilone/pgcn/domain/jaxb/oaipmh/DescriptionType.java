//
// Ce fichier a été généré par l'implémentation de référence JavaTM Architecture for XML Binding (JAXB), v2.2.8-b130911.1802
// Voir <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a>
// Toute modification apportée à ce fichier sera perdue lors de la recompilation du schéma source.
// Généré le : 2018.08.13 à 01:08:19 PM CEST
//


package fr.progilone.pgcn.domain.jaxb.oaipmh;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlType;


/**
 * The descriptionType is used for the description
 *       element in Identify and for setDescription element in ListSets.
 *       Content must be compliant with an XML Schema defined by a
 *       community.
 *
 * <p>Classe Java pour descriptionType complex type.
 *
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 *
 * <pre>
 * &lt;complexType name="descriptionType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;any namespace='##other'/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "descriptionType", propOrder = {
    "any"
})
public class DescriptionType {

    @XmlAnyElement(lax = true)
    protected Object any;

    /**
     * Obtient la valeur de la propriété any.
     *
     * @return
     *     possible object is
     *     {@link Object }
     *
     */
    public Object getAny() {
        return any;
    }

    /**
     * Définit la valeur de la propriété any.
     *
     * @param value
     *     allowed object is
     *     {@link Object }
     *
     */
    public void setAny(Object value) {
        this.any = value;
    }

}
