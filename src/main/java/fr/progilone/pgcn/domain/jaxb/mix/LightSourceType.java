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
 * Classe Java pour lightSourceType.
 *
 * <p>
 * Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 * <p>
 *
 * <pre>
 * &lt;simpleType name="lightSourceType">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="Daylight"/>
 *     &lt;enumeration value="Fluorescent"/>
 *     &lt;enumeration value="Tungsten (incandescent light)"/>
 *     &lt;enumeration value="Flash"/>
 *     &lt;enumeration value="Fine weather"/>
 *     &lt;enumeration value="Cloudy weather"/>
 *     &lt;enumeration value="Shade"/>
 *     &lt;enumeration value="Daylight fluorescent (D 5700 - 7100K)"/>
 *     &lt;enumeration value="Day white fluorescent (N 4600 - 5400K)"/>
 *     &lt;enumeration value="Cool white fluorescent (W 3900 - 4500K)"/>
 *     &lt;enumeration value="White fluorescent (WW 3200 - 3700K)"/>
 *     &lt;enumeration value="Standard light A"/>
 *     &lt;enumeration value="Standard light B"/>
 *     &lt;enumeration value="Standard light C"/>
 *     &lt;enumeration value="D55"/>
 *     &lt;enumeration value="D65"/>
 *     &lt;enumeration value="D75"/>
 *     &lt;enumeration value="D50"/>
 *     &lt;enumeration value="ISO studio tungsten"/>
 *     &lt;enumeration value="other light source"/>
 *     &lt;enumeration value="unknown"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 *
 */
@XmlType(name = "lightSourceType")
@XmlEnum
public enum LightSourceType {

    @XmlEnumValue("Daylight")
    DAYLIGHT("Daylight"),
    @XmlEnumValue("Fluorescent")
    FLUORESCENT("Fluorescent"),
    @XmlEnumValue("Tungsten (incandescent light)")
    TUNGSTEN_INCANDESCENT_LIGHT("Tungsten (incandescent light)"),
    @XmlEnumValue("Flash")
    FLASH("Flash"),
    @XmlEnumValue("Fine weather")
    FINE_WEATHER("Fine weather"),
    @XmlEnumValue("Cloudy weather")
    CLOUDY_WEATHER("Cloudy weather"),
    @XmlEnumValue("Shade")
    SHADE("Shade"),
    @XmlEnumValue("Daylight fluorescent (D 5700 - 7100K)")
    DAYLIGHT_FLUORESCENT_D_5700_7100_K("Daylight fluorescent (D 5700 - 7100K)"),
    @XmlEnumValue("Day white fluorescent (N 4600 - 5400K)")
    DAY_WHITE_FLUORESCENT_N_4600_5400_K("Day white fluorescent (N 4600 - 5400K)"),
    @XmlEnumValue("Cool white fluorescent (W 3900 - 4500K)")
    COOL_WHITE_FLUORESCENT_W_3900_4500_K("Cool white fluorescent (W 3900 - 4500K)"),
    @XmlEnumValue("White fluorescent (WW 3200 - 3700K)")
    WHITE_FLUORESCENT_WW_3200_3700_K("White fluorescent (WW 3200 - 3700K)"),
    @XmlEnumValue("Standard light A")
    STANDARD_LIGHT_A("Standard light A"),
    @XmlEnumValue("Standard light B")
    STANDARD_LIGHT_B("Standard light B"),
    @XmlEnumValue("Standard light C")
    STANDARD_LIGHT_C("Standard light C"),
    @XmlEnumValue("D55")
    D_55("D55"),
    @XmlEnumValue("D65")
    D_65("D65"),
    @XmlEnumValue("D75")
    D_75("D75"),
    @XmlEnumValue("D50")
    D_50("D50"),
    @XmlEnumValue("ISO studio tungsten")
    ISO_STUDIO_TUNGSTEN("ISO studio tungsten"),
    @XmlEnumValue("other light source")
    OTHER_LIGHT_SOURCE("other light source"),
    @XmlEnumValue("unknown")
    UNKNOWN("unknown");

    private final String value;

    LightSourceType(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static LightSourceType fromValue(String v) {
        for (LightSourceType c : LightSourceType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
