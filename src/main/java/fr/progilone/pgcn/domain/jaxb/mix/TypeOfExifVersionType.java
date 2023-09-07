//
// Ce fichier a été généré par l'implémentation de référence JavaTM Architecture for XML Binding (JAXB), v2.2.8-b130911.1802
// Voir <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a>
// Toute modification apportée à ce fichier sera perdue lors de la recompilation du schéma source.
// Généré le : 2017.08.25 à 03:15:17 PM CEST
//

package fr.progilone.pgcn.domain.jaxb.mix;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlType;
import jakarta.xml.bind.annotation.XmlValue;

/**
 * <p>
 * Classe Java pour typeOfExifVersionType complex type.
 *
 * <p>
 * Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 *
 * <pre>
 * &lt;complexType name="typeOfExifVersionType">
 *   &lt;simpleContent>
 *     &lt;extension base="&lt;http://www.loc.gov/mix/v20>exifVersionType">
 *       &lt;attribute name="use" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/extension>
 *   &lt;/simpleContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "typeOfExifVersionType", propOrder = {"value"})
public class TypeOfExifVersionType {

    @XmlValue
    protected String value;
    @XmlAttribute(name = "use")
    protected String use;

    /**
     * Obtient la valeur de la propriété value.
     *
     * @return
     *         possible object is
     *         {@link String }
     *
     */
    public String getValue() {
        return value;
    }

    /**
     * Définit la valeur de la propriété value.
     *
     * @param value
     *            allowed object is
     *            {@link String }
     *
     */
    public void setValue(String value) {
        this.value = value;
    }

    /**
     * Obtient la valeur de la propriété use.
     *
     * @return
     *         possible object is
     *         {@link String }
     *
     */
    public String getUse() {
        return use;
    }

    /**
     * Définit la valeur de la propriété use.
     *
     * @param value
     *            allowed object is
     *            {@link String }
     *
     */
    public void setUse(String value) {
        this.use = value;
    }

}
