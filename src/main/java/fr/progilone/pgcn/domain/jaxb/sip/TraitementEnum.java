//
// Ce fichier a été généré par l'implémentation de référence JavaTM Architecture for XML Binding (JAXB), v2.2.11 
// Voir <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Toute modification apportée à ce fichier sera perdue lors de la recompilation du schéma source. 
// Généré le : 2016.12.29 à 11:53:40 AM CET 
//


package fr.progilone.pgcn.domain.jaxb.sip;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Classe Java pour traitementEnum.
 * 
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 * <p>
 * <pre>
 * &lt;simpleType name="traitementEnum"&gt;
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *     &lt;enumeration value="conservation définitive"/&gt;
 *     &lt;enumeration value="élimination"/&gt;
 *   &lt;/restriction&gt;
 * &lt;/simpleType&gt;
 * </pre>
 * 
 */
@XmlType(name = "traitementEnum")
@XmlEnum
public enum TraitementEnum {

    @XmlEnumValue("conservation d\u00e9finitive")
    CONSERVATION_DÉFINITIVE("conservation d\u00e9finitive"),
    @XmlEnumValue("\u00e9limination")
    ÉLIMINATION("\u00e9limination");
    private final String value;

    TraitementEnum(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static TraitementEnum fromValue(String v) {
        for (TraitementEnum c: TraitementEnum.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
