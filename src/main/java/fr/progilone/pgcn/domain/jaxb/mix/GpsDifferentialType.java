//
// Ce fichier a été généré par l'implémentation de référence JavaTM Architecture for XML Binding (JAXB), v2.2.8-b130911.1802
// Voir <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a>
// Toute modification apportée à ce fichier sera perdue lors de la recompilation du schéma source.
// Généré le : 2017.08.25 à 03:15:17 PM CEST
//

package fr.progilone.pgcn.domain.jaxb.mix;

import jakarta.xml.bind.annotation.XmlEnum;
import jakarta.xml.bind.annotation.XmlEnumValue;
import jakarta.xml.bind.annotation.XmlType;

/**
 * <p>
 * Classe Java pour gpsDifferentialType.
 *
 * <p>
 * Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 * <p>
 *
 * <pre>
 * &lt;simpleType name="gpsDifferentialType">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="Measurement without differential correction"/>
 *     &lt;enumeration value="Differential correction applied"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 *
 */
@XmlType(name = "gpsDifferentialType")
@XmlEnum
public enum GpsDifferentialType {

    @XmlEnumValue("Measurement without differential correction")
    MEASUREMENT_WITHOUT_DIFFERENTIAL_CORRECTION("Measurement without differential correction"),
    @XmlEnumValue("Differential correction applied")
    DIFFERENTIAL_CORRECTION_APPLIED("Differential correction applied");

    private final String value;

    GpsDifferentialType(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static GpsDifferentialType fromValue(String v) {
        for (GpsDifferentialType c : GpsDifferentialType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
