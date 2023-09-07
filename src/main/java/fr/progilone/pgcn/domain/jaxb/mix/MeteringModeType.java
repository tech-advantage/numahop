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
 * Classe Java pour meteringModeType.
 *
 * <p>
 * Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 * <p>
 *
 * <pre>
 * &lt;simpleType name="meteringModeType">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="Average"/>
 *     &lt;enumeration value="Center weighted average"/>
 *     &lt;enumeration value="Spot"/>
 *     &lt;enumeration value="Multispot"/>
 *     &lt;enumeration value="Pattern"/>
 *     &lt;enumeration value="Partial"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 *
 */
@XmlType(name = "meteringModeType")
@XmlEnum
public enum MeteringModeType {

    @XmlEnumValue("Average")
    AVERAGE("Average"),
    @XmlEnumValue("Center weighted average")
    CENTER_WEIGHTED_AVERAGE("Center weighted average"),
    @XmlEnumValue("Spot")
    SPOT("Spot"),
    @XmlEnumValue("Multispot")
    MULTISPOT("Multispot"),
    @XmlEnumValue("Pattern")
    PATTERN("Pattern"),
    @XmlEnumValue("Partial")
    PARTIAL("Partial");

    private final String value;

    MeteringModeType(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static MeteringModeType fromValue(String v) {
        for (MeteringModeType c : MeteringModeType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
