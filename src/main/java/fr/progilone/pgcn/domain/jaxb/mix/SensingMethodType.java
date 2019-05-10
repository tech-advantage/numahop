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
 * <p>Classe Java pour sensingMethodType.
 * 
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 * <p>
 * <pre>
 * &lt;simpleType name="sensingMethodType">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="Not defined"/>
 *     &lt;enumeration value="One-chip color area sensor"/>
 *     &lt;enumeration value="Two-chip color area sensor"/>
 *     &lt;enumeration value="Three-chip color area sensor"/>
 *     &lt;enumeration value="Color sequential area sensor"/>
 *     &lt;enumeration value="Trilinear sensor"/>
 *     &lt;enumeration value="Color sequential linear sensor"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "sensingMethodType")
@XmlEnum
public enum SensingMethodType {

    @XmlEnumValue("Not defined")
    NOT_DEFINED("Not defined"),
    @XmlEnumValue("One-chip color area sensor")
    ONE_CHIP_COLOR_AREA_SENSOR("One-chip color area sensor"),
    @XmlEnumValue("Two-chip color area sensor")
    TWO_CHIP_COLOR_AREA_SENSOR("Two-chip color area sensor"),
    @XmlEnumValue("Three-chip color area sensor")
    THREE_CHIP_COLOR_AREA_SENSOR("Three-chip color area sensor"),
    @XmlEnumValue("Color sequential area sensor")
    COLOR_SEQUENTIAL_AREA_SENSOR("Color sequential area sensor"),
    @XmlEnumValue("Trilinear sensor")
    TRILINEAR_SENSOR("Trilinear sensor"),
    @XmlEnumValue("Color sequential linear sensor")
    COLOR_SEQUENTIAL_LINEAR_SENSOR("Color sequential linear sensor");
    private final String value;

    SensingMethodType(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static SensingMethodType fromValue(String v) {
        for (SensingMethodType c: SensingMethodType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
