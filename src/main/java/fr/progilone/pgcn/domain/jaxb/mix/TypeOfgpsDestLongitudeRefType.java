//
// Ce fichier a été généré par l'implémentation de référence JavaTM Architecture for XML Binding (JAXB), v2.2.8-b130911.1802 
// Voir <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Toute modification apportée à ce fichier sera perdue lors de la recompilation du schéma source. 
// Généré le : 2017.08.25 à 03:15:17 PM CEST 
//


package fr.progilone.pgcn.domain.jaxb.mix;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlValue;


/**
 * <p>Classe Java pour typeOfgpsDestLongitudeRefType complex type.
 * 
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 * 
 * <pre>
 * &lt;complexType name="typeOfgpsDestLongitudeRefType">
 *   &lt;simpleContent>
 *     &lt;extension base="&lt;http://www.loc.gov/mix/v20>gpsDestLongitudeRefType">
 *       &lt;attribute name="use" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/extension>
 *   &lt;/simpleContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "typeOfgpsDestLongitudeRefType", propOrder = {
    "value"
})
public class TypeOfgpsDestLongitudeRefType {

    @XmlValue
    protected GpsDestLongitudeRefType value;
    @XmlAttribute(name = "use")
    protected String use;

    /**
     * Obtient la valeur de la propriété value.
     * 
     * @return
     *     possible object is
     *     {@link GpsDestLongitudeRefType }
     *     
     */
    public GpsDestLongitudeRefType getValue() {
        return value;
    }

    /**
     * Définit la valeur de la propriété value.
     * 
     * @param value
     *     allowed object is
     *     {@link GpsDestLongitudeRefType }
     *     
     */
    public void setValue(GpsDestLongitudeRefType value) {
        this.value = value;
    }

    /**
     * Obtient la valeur de la propriété use.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getUse() {
        return use;
    }

    /**
     * Définit la valeur de la propriété use.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setUse(String value) {
        this.use = value;
    }

}
