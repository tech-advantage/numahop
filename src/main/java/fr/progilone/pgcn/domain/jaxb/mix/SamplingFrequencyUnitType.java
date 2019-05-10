//
// Ce fichier a été généré par l'implémentation de référence JavaTM Architecture for XML Binding (JAXB), v2.2.8-b130911.1802 
// Voir <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Toute modification apportée à ce fichier sera perdue lors de la recompilation du schéma source. 
// Généré le : 2017.08.25 à 03:15:17 PM CEST 
//


package fr.progilone.pgcn.domain.jaxb.mix;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Classe Java pour samplingFrequencyUnitType.
 * 
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 * <p>
 * <pre>
 * &lt;simpleType name="samplingFrequencyUnitType">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="no absolute unit of measurement"/>
 *     &lt;enumeration value="in."/>
 *     &lt;enumeration value="cm"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "samplingFrequencyUnitType")
@XmlEnum
public enum SamplingFrequencyUnitType {

    @XmlEnumValue("no absolute unit of measurement")
    NO_ABSOLUTE_UNIT_OF_MEASUREMENT("no absolute unit of measurement"),
    @XmlEnumValue("in.")
    IN("in."),
    @XmlEnumValue("cm")
    CM("cm");
    private final String value;

    SamplingFrequencyUnitType(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static SamplingFrequencyUnitType fromValue(String v) {
        for (SamplingFrequencyUnitType c: SamplingFrequencyUnitType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
