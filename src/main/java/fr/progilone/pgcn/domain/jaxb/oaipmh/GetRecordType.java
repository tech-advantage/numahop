//
// Ce fichier a été généré par l'implémentation de référence JavaTM Architecture for XML Binding (JAXB), v2.2.8-b130911.1802
// Voir <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a>
// Toute modification apportée à ce fichier sera perdue lors de la recompilation du schéma source.
// Généré le : 2018.08.13 à 01:08:19 PM CEST
//


package fr.progilone.pgcn.domain.jaxb.oaipmh;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Classe Java pour GetRecordType complex type.
 *
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 *
 * <pre>
 * &lt;complexType name="GetRecordType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="record" type="{http://www.openarchives.org/OAI/2.0/}recordType"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "GetRecordType", propOrder = {
    "record"
})
public class GetRecordType {

    @XmlElement(required = true)
    protected RecordType record;

    /**
     * Obtient la valeur de la propriété record.
     *
     * @return
     *     possible object is
     *     {@link RecordType }
     *
     */
    public RecordType getRecord() {
        return record;
    }

    /**
     * Définit la valeur de la propriété record.
     *
     * @param value
     *     allowed object is
     *     {@link RecordType }
     *
     */
    public void setRecord(RecordType value) {
        this.record = value;
    }

}
