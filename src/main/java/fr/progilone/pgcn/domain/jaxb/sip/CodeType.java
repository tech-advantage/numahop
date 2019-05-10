//
// Ce fichier a été généré par l'implémentation de référence JavaTM Architecture for XML Binding (JAXB), v2.2.11
// Voir <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a>
// Toute modification apportée à ce fichier sera perdue lors de la recompilation du schéma source.
// Généré le : 2016.12.29 à 11:53:40 AM CET
//


package fr.progilone.pgcn.domain.jaxb.sip;

import fr.progilone.pgcn.domain.jaxb.urn.AccessRestrictionCodeType;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlValue;


/**
 * <p>Classe Java pour codeType complex type.
 *
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 *
 * <pre>
 * &lt;complexType name="codeType"&gt;
 *   &lt;simpleContent&gt;
 *     &lt;extension base="&lt;urn:un:unece:uncefact:codelist:draft:DAF:accessRestrictionCode:2009-08-18&gt;AccessRestrictionCodeType"&gt;
 *       &lt;attribute name="type" use="required" type="{http://www.w3.org/2001/XMLSchema}anySimpleType" fixed="SEDA" /&gt;
 *     &lt;/extension&gt;
 *   &lt;/simpleContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "codeType", propOrder = {
    "value"
})
public class CodeType {

    @XmlValue
    protected AccessRestrictionCodeType value;
    @XmlAttribute(name = "type", required = true)
    @XmlSchemaType(name = "anySimpleType")
    protected String type;

    /**
     *
     *
     * <pre>
     * &lt;?xml version="1.0" encoding="UTF-8"?&gt;&lt;ccts:Name xmlns:ccts="urn:un:unece:uncefact:documentation:standard:CoreComponentsTechnicalSpecification:2" xmlns="urn:un:unece:uncefact:codelist:draft:DAF:accessRestrictionCode:2009-08-18" xmlns:clmDAFAccessRestrictionCode="urn:un:unece:uncefact:codelist:draft:DAF:accessRestrictionCode:2009-08-18" xmlns:xsd="http://www.w3.org/2001/XMLSchema"&gt;Table des codes pour la restriction d'accès&lt;/ccts:Name&gt;
     * </pre>
     *
     *
     * <pre>
     * &lt;?xml version="1.0" encoding="UTF-8"?&gt;&lt;ccts:Description xmlns:ccts="urn:un:unece:uncefact:documentation:standard:CoreComponentsTechnicalSpecification:2" xmlns="urn:un:unece:uncefact:codelist:draft:DAF:accessRestrictionCode:2009-08-18" xmlns:clmDAFAccessRestrictionCode="urn:un:unece:uncefact:codelist:draft:DAF:accessRestrictionCode:2009-08-18" xmlns:xsd="http://www.w3.org/2001/XMLSchema"&gt;Les différentes valeurs de cette table sont tirées de la loi archive.&lt;/ccts:Description&gt;
     * </pre>
     *
     *
     *
     * @return
     *     possible object is
     *     {@link AccessRestrictionCodeType }
     *
     */
    public AccessRestrictionCodeType getValue() {
        return value;
    }

    /**
     * Définit la valeur de la propriété value.
     *
     * @param value
     *     allowed object is
     *     {@link AccessRestrictionCodeType }
     *
     */
    public void setValue(AccessRestrictionCodeType value) {
        this.value = value;
    }

    /**
     * Obtient la valeur de la propriété type.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getType() {
        if (type == null) {
            return "SEDA";
        } else {
            return type;
        }
    }

    /**
     * Définit la valeur de la propriété type.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setType(String value) {
        this.type = value;
    }

}
