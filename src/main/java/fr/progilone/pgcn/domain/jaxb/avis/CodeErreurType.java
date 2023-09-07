//
// Ce fichier a été généré par l'implémentation de référence JavaTM Architecture for XML Binding (JAXB), v2.2.11
// Voir <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a>
// Toute modification apportée à ce fichier sera perdue lors de la recompilation du schéma source.
// Généré le : 2017.01.03 à 11:16:18 AM CET
//

package fr.progilone.pgcn.domain.jaxb.avis;

import jakarta.xml.bind.annotation.XmlEnum;
import jakarta.xml.bind.annotation.XmlEnumValue;
import jakarta.xml.bind.annotation.XmlType;

/**
 * <p>
 * Classe Java pour codeErreurType.
 *
 * <p>
 * Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 * <p>
 *
 * <pre>
 * &lt;simpleType name="codeErreurType"&gt;
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *     &lt;enumeration value="E0001"/&gt;
 *     &lt;enumeration value="E0002"/&gt;
 *     &lt;enumeration value="E0003"/&gt;
 *     &lt;enumeration value="E0004"/&gt;
 *     &lt;enumeration value="E0005"/&gt;
 *     &lt;enumeration value="E0006"/&gt;
 *     &lt;enumeration value="E0007"/&gt;
 *     &lt;enumeration value="E0008"/&gt;
 *     &lt;enumeration value="E0009"/&gt;
 *     &lt;enumeration value="E0010"/&gt;
 *     &lt;enumeration value="E0011"/&gt;
 *     &lt;enumeration value="E0100"/&gt;
 *     &lt;enumeration value="E0101"/&gt;
 *     &lt;enumeration value="E0102"/&gt;
 *     &lt;enumeration value="E0103"/&gt;
 *     &lt;enumeration value="E0104"/&gt;
 *     &lt;enumeration value="E0105"/&gt;
 *     &lt;enumeration value="E0106"/&gt;
 *   &lt;/restriction&gt;
 * &lt;/simpleType&gt;
 * </pre>
 *
 */
@XmlType(name = "codeErreurType")
@XmlEnum
public enum CodeErreurType {

    @XmlEnumValue("E0001")
    E_0001("E0001"),
    @XmlEnumValue("E0002")
    E_0002("E0002"),
    @XmlEnumValue("E0003")
    E_0003("E0003"),
    @XmlEnumValue("E0004")
    E_0004("E0004"),
    @XmlEnumValue("E0005")
    E_0005("E0005"),
    @XmlEnumValue("E0006")
    E_0006("E0006"),
    @XmlEnumValue("E0007")
    E_0007("E0007"),
    @XmlEnumValue("E0008")
    E_0008("E0008"),
    @XmlEnumValue("E0009")
    E_0009("E0009"),
    @XmlEnumValue("E0010")
    E_0010("E0010"),
    @XmlEnumValue("E0011")
    E_0011("E0011"),
    @XmlEnumValue("E0100")
    E_0100("E0100"),
    @XmlEnumValue("E0101")
    E_0101("E0101"),
    @XmlEnumValue("E0102")
    E_0102("E0102"),
    @XmlEnumValue("E0103")
    E_0103("E0103"),
    @XmlEnumValue("E0104")
    E_0104("E0104"),
    @XmlEnumValue("E0105")
    E_0105("E0105"),
    @XmlEnumValue("E0106")
    E_0106("E0106");

    private final String value;

    CodeErreurType(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static CodeErreurType fromValue(String v) {
        for (CodeErreurType c : CodeErreurType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
