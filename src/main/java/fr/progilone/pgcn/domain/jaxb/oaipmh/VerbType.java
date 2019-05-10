//
// Ce fichier a été généré par l'implémentation de référence JavaTM Architecture for XML Binding (JAXB), v2.2.8-b130911.1802
// Voir <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a>
// Toute modification apportée à ce fichier sera perdue lors de la recompilation du schéma source.
// Généré le : 2018.08.13 à 01:08:19 PM CEST
//


package fr.progilone.pgcn.domain.jaxb.oaipmh;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Classe Java pour verbType.
 *
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 * <p>
 * <pre>
 * &lt;simpleType name="verbType">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="Identify"/>
 *     &lt;enumeration value="ListMetadataFormats"/>
 *     &lt;enumeration value="ListSets"/>
 *     &lt;enumeration value="GetRecord"/>
 *     &lt;enumeration value="ListIdentifiers"/>
 *     &lt;enumeration value="ListRecords"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 *
 */
@XmlType(name = "verbType")
@XmlEnum
public enum VerbType {

    @XmlEnumValue("Identify")
    IDENTIFY("Identify"),
    @XmlEnumValue("ListMetadataFormats")
    LIST_METADATA_FORMATS("ListMetadataFormats"),
    @XmlEnumValue("ListSets")
    LIST_SETS("ListSets"),
    @XmlEnumValue("GetRecord")
    GET_RECORD("GetRecord"),
    @XmlEnumValue("ListIdentifiers")
    LIST_IDENTIFIERS("ListIdentifiers"),
    @XmlEnumValue("ListRecords")
    LIST_RECORDS("ListRecords");
    private final String value;

    VerbType(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static VerbType fromValue(String v) {
        for (VerbType c: VerbType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
