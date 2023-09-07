//
// Ce fichier a été généré par l'implémentation de référence JavaTM Architecture for XML Binding (JAXB), v2.2.8-b130911.1802
// Voir <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a>
// Toute modification apportée à ce fichier sera perdue lors de la recompilation du schéma source.
// Généré le : 2017.05.16 à 10:56:55 AM CEST
//

package fr.progilone.pgcn.domain.jaxb.ead;

import jakarta.xml.bind.annotation.XmlEnum;
import jakarta.xml.bind.annotation.XmlEnumValue;
import jakarta.xml.bind.annotation.XmlType;

/**
 * <p>
 * Classe Java pour av.render.
 *
 * <p>
 * Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 * <p>
 *
 * <pre>
 * &lt;simpleType name="av.render">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}token">
 *     &lt;enumeration value="altrender"/>
 *     &lt;enumeration value="bold"/>
 *     &lt;enumeration value="bolddoublequote"/>
 *     &lt;enumeration value="bolditalic"/>
 *     &lt;enumeration value="boldsinglequote"/>
 *     &lt;enumeration value="boldsmcaps"/>
 *     &lt;enumeration value="boldunderline"/>
 *     &lt;enumeration value="doublequote"/>
 *     &lt;enumeration value="italic"/>
 *     &lt;enumeration value="nonproport"/>
 *     &lt;enumeration value="singlequote"/>
 *     &lt;enumeration value="smcaps"/>
 *     &lt;enumeration value="sub"/>
 *     &lt;enumeration value="super"/>
 *     &lt;enumeration value="underline"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 *
 */
@XmlType(name = "av.render")
@XmlEnum
public enum AvRender {

    @XmlEnumValue("altrender")
    ALTRENDER("altrender"),
    @XmlEnumValue("bold")
    BOLD("bold"),
    @XmlEnumValue("bolddoublequote")
    BOLDDOUBLEQUOTE("bolddoublequote"),
    @XmlEnumValue("bolditalic")
    BOLDITALIC("bolditalic"),
    @XmlEnumValue("boldsinglequote")
    BOLDSINGLEQUOTE("boldsinglequote"),
    @XmlEnumValue("boldsmcaps")
    BOLDSMCAPS("boldsmcaps"),
    @XmlEnumValue("boldunderline")
    BOLDUNDERLINE("boldunderline"),
    @XmlEnumValue("doublequote")
    DOUBLEQUOTE("doublequote"),
    @XmlEnumValue("italic")
    ITALIC("italic"),
    @XmlEnumValue("nonproport")
    NONPROPORT("nonproport"),
    @XmlEnumValue("singlequote")
    SINGLEQUOTE("singlequote"),
    @XmlEnumValue("smcaps")
    SMCAPS("smcaps"),
    @XmlEnumValue("sub")
    SUB("sub"),
    @XmlEnumValue("super")
    SUPER("super"),
    @XmlEnumValue("underline")
    UNDERLINE("underline");

    private final String value;

    AvRender(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static AvRender fromValue(String v) {
        for (AvRender c : AvRender.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
