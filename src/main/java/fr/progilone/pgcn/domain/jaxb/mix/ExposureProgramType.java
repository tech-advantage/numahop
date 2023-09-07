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
 * Classe Java pour exposureProgramType.
 *
 * <p>
 * Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 * <p>
 *
 * <pre>
 * &lt;simpleType name="exposureProgramType">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="Not defined"/>
 *     &lt;enumeration value="Manual"/>
 *     &lt;enumeration value="Normal program"/>
 *     &lt;enumeration value="Aperture priority"/>
 *     &lt;enumeration value="Shutter priority"/>
 *     &lt;enumeration value="Creative program (biased toward depth of field)"/>
 *     &lt;enumeration value="Action program (biased toward fast shutter speed)"/>
 *     &lt;enumeration value="Portrait mode (for closeup photos with the background out of focus)"/>
 *     &lt;enumeration value="Landscape mode (for landscape photos with the background in focus)"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 *
 */
@XmlType(name = "exposureProgramType")
@XmlEnum
public enum ExposureProgramType {

    @XmlEnumValue("Not defined")
    NOT_DEFINED("Not defined"),
    @XmlEnumValue("Manual")
    MANUAL("Manual"),
    @XmlEnumValue("Normal program")
    NORMAL_PROGRAM("Normal program"),
    @XmlEnumValue("Aperture priority")
    APERTURE_PRIORITY("Aperture priority"),
    @XmlEnumValue("Shutter priority")
    SHUTTER_PRIORITY("Shutter priority"),
    @XmlEnumValue("Creative program (biased toward depth of field)")
    CREATIVE_PROGRAM_BIASED_TOWARD_DEPTH_OF_FIELD("Creative program (biased toward depth of field)"),
    @XmlEnumValue("Action program (biased toward fast shutter speed)")
    ACTION_PROGRAM_BIASED_TOWARD_FAST_SHUTTER_SPEED("Action program (biased toward fast shutter speed)"),
    @XmlEnumValue("Portrait mode (for closeup photos with the background out of focus)")
    PORTRAIT_MODE_FOR_CLOSEUP_PHOTOS_WITH_THE_BACKGROUND_OUT_OF_FOCUS("Portrait mode (for closeup photos with the background out of focus)"),
    @XmlEnumValue("Landscape mode (for landscape photos with the background in focus)")
    LANDSCAPE_MODE_FOR_LANDSCAPE_PHOTOS_WITH_THE_BACKGROUND_IN_FOCUS("Landscape mode (for landscape photos with the background in focus)");

    private final String value;

    ExposureProgramType(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static ExposureProgramType fromValue(String v) {
        for (ExposureProgramType c : ExposureProgramType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
