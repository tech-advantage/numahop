//
// Ce fichier a été généré par l'implémentation de référence JavaTM Architecture for XML Binding (JAXB), v2.2.8-b130911.1802 
// Voir <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Toute modification apportée à ce fichier sera perdue lors de la recompilation du schéma source. 
// Généré le : 2017.05.16 à 10:56:55 AM CEST 
//


package fr.progilone.pgcn.domain.jaxb.ead;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Classe Java pour av.level.
 * 
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 * <p>
 * <pre>
 * &lt;simpleType name="av.level">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}token">
 *     &lt;enumeration value="class"/>
 *     &lt;enumeration value="collection"/>
 *     &lt;enumeration value="file"/>
 *     &lt;enumeration value="fonds"/>
 *     &lt;enumeration value="item"/>
 *     &lt;enumeration value="otherlevel"/>
 *     &lt;enumeration value="recordgrp"/>
 *     &lt;enumeration value="series"/>
 *     &lt;enumeration value="subfonds"/>
 *     &lt;enumeration value="subgrp"/>
 *     &lt;enumeration value="subseries"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "av.level")
@XmlEnum
public enum AvLevel {

    @XmlEnumValue("class")
    CLASS("class"),
    @XmlEnumValue("collection")
    COLLECTION("collection"),
    @XmlEnumValue("file")
    FILE("file"),
    @XmlEnumValue("fonds")
    FONDS("fonds"),
    @XmlEnumValue("item")
    ITEM("item"),
    @XmlEnumValue("otherlevel")
    OTHERLEVEL("otherlevel"),
    @XmlEnumValue("recordgrp")
    RECORDGRP("recordgrp"),
    @XmlEnumValue("series")
    SERIES("series"),
    @XmlEnumValue("subfonds")
    SUBFONDS("subfonds"),
    @XmlEnumValue("subgrp")
    SUBGRP("subgrp"),
    @XmlEnumValue("subseries")
    SUBSERIES("subseries");
    private final String value;

    AvLevel(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static AvLevel fromValue(String v) {
        for (AvLevel c: AvLevel.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
