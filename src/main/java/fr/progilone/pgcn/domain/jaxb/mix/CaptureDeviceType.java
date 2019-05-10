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
 * <p>Classe Java pour captureDeviceType.
 * 
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 * <p>
 * <pre>
 * &lt;simpleType name="captureDeviceType">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="transmission scanner"/>
 *     &lt;enumeration value="reflection print scanner"/>
 *     &lt;enumeration value="digital still camera"/>
 *     &lt;enumeration value="still from video"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "captureDeviceType")
@XmlEnum
public enum CaptureDeviceType {

    @XmlEnumValue("transmission scanner")
    TRANSMISSION_SCANNER("transmission scanner"),
    @XmlEnumValue("reflection print scanner")
    REFLECTION_PRINT_SCANNER("reflection print scanner"),
    @XmlEnumValue("digital still camera")
    DIGITAL_STILL_CAMERA("digital still camera"),
    @XmlEnumValue("still from video")
    STILL_FROM_VIDEO("still from video");
    private final String value;

    CaptureDeviceType(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static CaptureDeviceType fromValue(String v) {
        for (CaptureDeviceType c: CaptureDeviceType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
