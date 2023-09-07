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
 * Classe Java pour grayResponseUnitType.
 *
 * <p>
 * Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 * <p>
 *
 * <pre>
 * &lt;simpleType name="grayResponseUnitType">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="Number represents tenths of a unit"/>
 *     &lt;enumeration value="Number represents hundredths of a unit"/>
 *     &lt;enumeration value="Number represents thousandths of a unit"/>
 *     &lt;enumeration value="Number represents ten-thousandths of a unit"/>
 *     &lt;enumeration value="Number represents hundred-thousandths of a unit"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 *
 */
@XmlType(name = "grayResponseUnitType")
@XmlEnum
public enum GrayResponseUnitType {

    @XmlEnumValue("Number represents tenths of a unit")
    NUMBER_REPRESENTS_TENTHS_OF_A_UNIT("Number represents tenths of a unit"),
    @XmlEnumValue("Number represents hundredths of a unit")
    NUMBER_REPRESENTS_HUNDREDTHS_OF_A_UNIT("Number represents hundredths of a unit"),
    @XmlEnumValue("Number represents thousandths of a unit")
    NUMBER_REPRESENTS_THOUSANDTHS_OF_A_UNIT("Number represents thousandths of a unit"),
    @XmlEnumValue("Number represents ten-thousandths of a unit")
    NUMBER_REPRESENTS_TEN_THOUSANDTHS_OF_A_UNIT("Number represents ten-thousandths of a unit"),
    @XmlEnumValue("Number represents hundred-thousandths of a unit")
    NUMBER_REPRESENTS_HUNDRED_THOUSANDTHS_OF_A_UNIT("Number represents hundred-thousandths of a unit");

    private final String value;

    GrayResponseUnitType(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static GrayResponseUnitType fromValue(String v) {
        for (GrayResponseUnitType c : GrayResponseUnitType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
