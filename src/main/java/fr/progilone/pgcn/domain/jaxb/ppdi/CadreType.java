//
// Ce fichier a été généré par l'implémentation de référence JavaTM Architecture for XML Binding (JAXB), v2.2.8-b130911.1802
// Voir <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a>
// Toute modification apportée à fichier sera perdue lors de la recompilation du schéma source.
// Généré le : 2017.12.08 à 03:23:15 PM CET
//

package fr.progilone.pgcn.domain.jaxb.ppdi;

import jakarta.xml.bind.annotation.XmlEnum;
import jakarta.xml.bind.annotation.XmlEnumValue;
import jakarta.xml.bind.annotation.XmlType;

/**
 * <p>
 * Classe Java pour cadreType.
 *
 * <p>
 * Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 * <p>
 *
 * <pre>
 * &lt;simpleType name="cadreType">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="Archivage patrimonial"/>
 *     &lt;enumeration value="Tiers archivage"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 *
 */
@XmlType(name = "cadreType")
@XmlEnum
public enum CadreType {

    @XmlEnumValue("Archivage patrimonial")
    ARCHIVAGE_PATRIMONIAL("Archivage patrimonial"),
    @XmlEnumValue("Tiers archivage")
    TIERS_ARCHIVAGE("Tiers archivage");

    private final String value;

    CadreType(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static CadreType fromValue(String v) {
        for (CadreType c : CadreType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
