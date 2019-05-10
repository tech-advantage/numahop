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
 * <p>Classe Java pour samplingFrequencyPlaneType.
 * 
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 * <p>
 * <pre>
 * &lt;simpleType name="samplingFrequencyPlaneType">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="camera/scanner focal plane"/>
 *     &lt;enumeration value="object plane"/>
 *     &lt;enumeration value="source object plane"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "samplingFrequencyPlaneType")
@XmlEnum
public enum SamplingFrequencyPlaneType {

    @XmlEnumValue("camera/scanner focal plane")
    CAMERA_SCANNER_FOCAL_PLANE("camera/scanner focal plane"),
    @XmlEnumValue("object plane")
    OBJECT_PLANE("object plane"),
    @XmlEnumValue("source object plane")
    SOURCE_OBJECT_PLANE("source object plane");
    private final String value;

    SamplingFrequencyPlaneType(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static SamplingFrequencyPlaneType fromValue(String v) {
        for (SamplingFrequencyPlaneType c: SamplingFrequencyPlaneType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
