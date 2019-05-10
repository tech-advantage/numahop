//
// Ce fichier a été généré par l'implémentation de référence JavaTM Architecture for XML Binding (JAXB), v2.2.8-b130911.1802
// Voir <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a>
// Toute modification apportée à ce fichier sera perdue lors de la recompilation du schéma source.
// Généré le : 2018.08.13 à 01:08:19 PM CEST
//


package fr.progilone.pgcn.domain.jaxb.oaipmh;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlValue;


/**
 * <p>Classe Java pour OAI-PMHerrorType complex type.
 *
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 *
 * <pre>
 * &lt;complexType name="OAI-PMHerrorType">
 *   &lt;simpleContent>
 *     &lt;extension base="&lt;http://www.w3.org/2001/XMLSchema>string">
 *       &lt;attribute name="code" use="required" type="{http://www.openarchives.org/OAI/2.0/}OAI-PMHerrorcodeType" />
 *     &lt;/extension>
 *   &lt;/simpleContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "OAI-PMHerrorType", propOrder = {
    "value"
})
public class OAIPMHerrorType {

    @XmlValue
    protected String value;
    @XmlAttribute(name = "code", required = true)
    protected OAIPMHerrorcodeType code;

    /**
     * Obtient la valeur de la propriété value.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getValue() {
        return value;
    }

    /**
     * Définit la valeur de la propriété value.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setValue(String value) {
        this.value = value;
    }

    /**
     * Obtient la valeur de la propriété code.
     *
     * @return
     *     possible object is
     *     {@link OAIPMHerrorcodeType }
     *
     */
    public OAIPMHerrorcodeType getCode() {
        return code;
    }

    /**
     * Définit la valeur de la propriété code.
     *
     * @param value
     *     allowed object is
     *     {@link OAIPMHerrorcodeType }
     *
     */
    public void setCode(OAIPMHerrorcodeType value) {
        this.code = value;
    }

}
