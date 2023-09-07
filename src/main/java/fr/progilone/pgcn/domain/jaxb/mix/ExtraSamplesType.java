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
 * Classe Java pour extraSamplesType.
 *
 * <p>
 * Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 * <p>
 *
 * <pre>
 * &lt;simpleType name="extraSamplesType">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="unspecified data"/>
 *     &lt;enumeration value="associated alpha data (with pre-multiplied color)"/>
 *     &lt;enumeration value="unassociated alpha data"/>
 *     &lt;enumeration value="range or depth data"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 *
 */
@XmlType(name = "extraSamplesType")
@XmlEnum
public enum ExtraSamplesType {

    @XmlEnumValue("unspecified data")
    UNSPECIFIED_DATA("unspecified data"),
    @XmlEnumValue("associated alpha data (with pre-multiplied color)")
    ASSOCIATED_ALPHA_DATA_WITH_PRE_MULTIPLIED_COLOR("associated alpha data (with pre-multiplied color)"),
    @XmlEnumValue("unassociated alpha data")
    UNASSOCIATED_ALPHA_DATA("unassociated alpha data"),
    @XmlEnumValue("range or depth data")
    RANGE_OR_DEPTH_DATA("range or depth data");

    private final String value;

    ExtraSamplesType(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static ExtraSamplesType fromValue(String v) {
        for (ExtraSamplesType c : ExtraSamplesType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
