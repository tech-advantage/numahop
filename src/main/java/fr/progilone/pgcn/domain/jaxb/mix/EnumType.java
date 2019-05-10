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
 * <p>Classe Java pour enumType.
 * 
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 * <p>
 * <pre>
 * &lt;simpleType name="enumType">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="value1"/>
 *     &lt;enumeration value="value2"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "enumType")
@XmlEnum
public enum EnumType {

    @XmlEnumValue("value1")
    VALUE_1("value1"),
    @XmlEnumValue("value2")
    VALUE_2("value2");
    private final String value;

    EnumType(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static EnumType fromValue(String v) {
        for (EnumType c: EnumType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
