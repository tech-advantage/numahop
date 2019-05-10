//
// Ce fichier a été généré par l'implémentation de référence JavaTM Architecture for XML Binding (JAXB), v2.2.8-b130911.1802
// Voir <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a>
// Toute modification apportée à ce fichier sera perdue lors de la recompilation du schéma source.
// Généré le : 2018.08.13 à 01:08:19 PM CEST
//


package fr.progilone.pgcn.domain.jaxb.oaipmh;

import java.math.BigInteger;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlValue;
import javax.xml.datatype.XMLGregorianCalendar;


/**
 * A resumptionToken may have 3 optional attributes
 *        and can be used in ListSets, ListIdentifiers, ListRecords
 *        responses.
 *
 * <p>Classe Java pour resumptionTokenType complex type.
 *
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 *
 * <pre>
 * &lt;complexType name="resumptionTokenType">
 *   &lt;simpleContent>
 *     &lt;extension base="&lt;http://www.w3.org/2001/XMLSchema>string">
 *       &lt;attribute name="expirationDate" type="{http://www.w3.org/2001/XMLSchema}dateTime" />
 *       &lt;attribute name="completeListSize" type="{http://www.w3.org/2001/XMLSchema}positiveInteger" />
 *       &lt;attribute name="cursor" type="{http://www.w3.org/2001/XMLSchema}nonNegativeInteger" />
 *     &lt;/extension>
 *   &lt;/simpleContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "resumptionTokenType", propOrder = {
    "value"
})
public class ResumptionTokenType {

    @XmlValue
    protected String value;
    @XmlAttribute(name = "expirationDate")
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar expirationDate;
    @XmlAttribute(name = "completeListSize")
    @XmlSchemaType(name = "positiveInteger")
    protected BigInteger completeListSize;
    @XmlAttribute(name = "cursor")
    @XmlSchemaType(name = "nonNegativeInteger")
    protected BigInteger cursor;

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
     * Obtient la valeur de la propriété expirationDate.
     *
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *
     */
    public XMLGregorianCalendar getExpirationDate() {
        return expirationDate;
    }

    /**
     * Définit la valeur de la propriété expirationDate.
     *
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *
     */
    public void setExpirationDate(XMLGregorianCalendar value) {
        this.expirationDate = value;
    }

    /**
     * Obtient la valeur de la propriété completeListSize.
     *
     * @return
     *     possible object is
     *     {@link BigInteger }
     *
     */
    public BigInteger getCompleteListSize() {
        return completeListSize;
    }

    /**
     * Définit la valeur de la propriété completeListSize.
     *
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *
     */
    public void setCompleteListSize(BigInteger value) {
        this.completeListSize = value;
    }

    /**
     * Obtient la valeur de la propriété cursor.
     *
     * @return
     *     possible object is
     *     {@link BigInteger }
     *
     */
    public BigInteger getCursor() {
        return cursor;
    }

    /**
     * Définit la valeur de la propriété cursor.
     *
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *
     */
    public void setCursor(BigInteger value) {
        this.cursor = value;
    }

}
