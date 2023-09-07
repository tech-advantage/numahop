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
 * Classe Java pour flashType.
 *
 * <p>
 * Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 * <p>
 *
 * <pre>
 * &lt;simpleType name="flashType">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="Flash did not fire"/>
 *     &lt;enumeration value="Flash fired"/>
 *     &lt;enumeration value="Strobe return light not detected"/>
 *     &lt;enumeration value="Strobe return light detected"/>
 *     &lt;enumeration value="Flash fired, compulsory flash mode"/>
 *     &lt;enumeration value="Flash fired, compulsory flash mode, return light not detected"/>
 *     &lt;enumeration value="Flash fired, compulsory flash mode, return light detected"/>
 *     &lt;enumeration value="Flash did not fire, compulsory flash mode"/>
 *     &lt;enumeration value="Flash did not fire, auto mode"/>
 *     &lt;enumeration value="Flash fired, auto mode"/>
 *     &lt;enumeration value="Flash fired, auto mode, return light not detected"/>
 *     &lt;enumeration value="Flash fired, auto mode, return light detected"/>
 *     &lt;enumeration value="No flash function"/>
 *     &lt;enumeration value="Flash fired, red-eye reduction mode"/>
 *     &lt;enumeration value="Flash fired, red-eye reduction mode, return light not detected"/>
 *     &lt;enumeration value="Flash fired, red-eye reduction mode, return light detected"/>
 *     &lt;enumeration value="Flash fired, compulsory flash mode, red-eye reduction mode"/>
 *     &lt;enumeration value="Flash fired, compulsory flash mode, red-eye reduction mode, return light not detected"/>
 *     &lt;enumeration value="Flash fired, compulsory flash mode, red-eye reduction mode, return light detected"/>
 *     &lt;enumeration value="Flash fired, auto mode, red-eye reduction mode"/>
 *     &lt;enumeration value="Flash fired, auto mode, return light not detected, red-eye reduction mode"/>
 *     &lt;enumeration value="Flash fired, auto mode, return light detected, red-eye reduction mode"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 *
 */
@XmlType(name = "flashType")
@XmlEnum
public enum FlashType {

    @XmlEnumValue("Flash did not fire")
    FLASH_DID_NOT_FIRE("Flash did not fire"),
    @XmlEnumValue("Flash fired")
    FLASH_FIRED("Flash fired"),
    @XmlEnumValue("Strobe return light not detected")
    STROBE_RETURN_LIGHT_NOT_DETECTED("Strobe return light not detected"),
    @XmlEnumValue("Strobe return light detected")
    STROBE_RETURN_LIGHT_DETECTED("Strobe return light detected"),
    @XmlEnumValue("Flash fired, compulsory flash mode")
    FLASH_FIRED_COMPULSORY_FLASH_MODE("Flash fired, compulsory flash mode"),
    @XmlEnumValue("Flash fired, compulsory flash mode, return light not detected")
    FLASH_FIRED_COMPULSORY_FLASH_MODE_RETURN_LIGHT_NOT_DETECTED("Flash fired, compulsory flash mode, return light not detected"),
    @XmlEnumValue("Flash fired, compulsory flash mode, return light detected")
    FLASH_FIRED_COMPULSORY_FLASH_MODE_RETURN_LIGHT_DETECTED("Flash fired, compulsory flash mode, return light detected"),
    @XmlEnumValue("Flash did not fire, compulsory flash mode")
    FLASH_DID_NOT_FIRE_COMPULSORY_FLASH_MODE("Flash did not fire, compulsory flash mode"),
    @XmlEnumValue("Flash did not fire, auto mode")
    FLASH_DID_NOT_FIRE_AUTO_MODE("Flash did not fire, auto mode"),
    @XmlEnumValue("Flash fired, auto mode")
    FLASH_FIRED_AUTO_MODE("Flash fired, auto mode"),
    @XmlEnumValue("Flash fired, auto mode, return light not detected")
    FLASH_FIRED_AUTO_MODE_RETURN_LIGHT_NOT_DETECTED("Flash fired, auto mode, return light not detected"),
    @XmlEnumValue("Flash fired, auto mode, return light detected")
    FLASH_FIRED_AUTO_MODE_RETURN_LIGHT_DETECTED("Flash fired, auto mode, return light detected"),
    @XmlEnumValue("No flash function")
    NO_FLASH_FUNCTION("No flash function"),
    @XmlEnumValue("Flash fired, red-eye reduction mode")
    FLASH_FIRED_RED_EYE_REDUCTION_MODE("Flash fired, red-eye reduction mode"),
    @XmlEnumValue("Flash fired, red-eye reduction mode, return light not detected")
    FLASH_FIRED_RED_EYE_REDUCTION_MODE_RETURN_LIGHT_NOT_DETECTED("Flash fired, red-eye reduction mode, return light not detected"),
    @XmlEnumValue("Flash fired, red-eye reduction mode, return light detected")
    FLASH_FIRED_RED_EYE_REDUCTION_MODE_RETURN_LIGHT_DETECTED("Flash fired, red-eye reduction mode, return light detected"),
    @XmlEnumValue("Flash fired, compulsory flash mode, red-eye reduction mode")
    FLASH_FIRED_COMPULSORY_FLASH_MODE_RED_EYE_REDUCTION_MODE("Flash fired, compulsory flash mode, red-eye reduction mode"),
    @XmlEnumValue("Flash fired, compulsory flash mode, red-eye reduction mode, return light not detected")
    FLASH_FIRED_COMPULSORY_FLASH_MODE_RED_EYE_REDUCTION_MODE_RETURN_LIGHT_NOT_DETECTED("Flash fired, compulsory flash mode, red-eye reduction mode, return light not detected"),
    @XmlEnumValue("Flash fired, compulsory flash mode, red-eye reduction mode, return light detected")
    FLASH_FIRED_COMPULSORY_FLASH_MODE_RED_EYE_REDUCTION_MODE_RETURN_LIGHT_DETECTED("Flash fired, compulsory flash mode, red-eye reduction mode, return light detected"),
    @XmlEnumValue("Flash fired, auto mode, red-eye reduction mode")
    FLASH_FIRED_AUTO_MODE_RED_EYE_REDUCTION_MODE("Flash fired, auto mode, red-eye reduction mode"),
    @XmlEnumValue("Flash fired, auto mode, return light not detected, red-eye reduction mode")
    FLASH_FIRED_AUTO_MODE_RETURN_LIGHT_NOT_DETECTED_RED_EYE_REDUCTION_MODE("Flash fired, auto mode, return light not detected, red-eye reduction mode"),
    @XmlEnumValue("Flash fired, auto mode, return light detected, red-eye reduction mode")
    FLASH_FIRED_AUTO_MODE_RETURN_LIGHT_DETECTED_RED_EYE_REDUCTION_MODE("Flash fired, auto mode, return light detected, red-eye reduction mode");

    private final String value;

    FlashType(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static FlashType fromValue(String v) {
        for (FlashType c : FlashType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
