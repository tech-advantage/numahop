//
// Ce fichier a été généré par l'implémentation de référence JavaTM Architecture for XML Binding (JAXB), v2.2.8-b130911.1802
// Voir <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a>
// Toute modification apportée à ce fichier sera perdue lors de la recompilation du schéma source.
// Généré le : 2018.08.13 à 01:08:19 PM CEST
//


package fr.progilone.pgcn.domain.jaxb.oaipmh;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;


/**
 * <p>Classe Java pour OAI-PMHtype complex type.
 *
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 *
 * <pre>
 * &lt;complexType name="OAI-PMHtype">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="responseDate" type="{http://www.w3.org/2001/XMLSchema}dateTime"/>
 *         &lt;element name="request" type="{http://www.openarchives.org/OAI/2.0/}requestType"/>
 *         &lt;choice>
 *           &lt;element name="error" type="{http://www.openarchives.org/OAI/2.0/}OAI-PMHerrorType" maxOccurs="unbounded"/>
 *           &lt;element name="Identify" type="{http://www.openarchives.org/OAI/2.0/}IdentifyType"/>
 *           &lt;element name="ListMetadataFormats" type="{http://www.openarchives.org/OAI/2.0/}ListMetadataFormatsType"/>
 *           &lt;element name="ListSets" type="{http://www.openarchives.org/OAI/2.0/}ListSetsType"/>
 *           &lt;element name="GetRecord" type="{http://www.openarchives.org/OAI/2.0/}GetRecordType"/>
 *           &lt;element name="ListIdentifiers" type="{http://www.openarchives.org/OAI/2.0/}ListIdentifiersType"/>
 *           &lt;element name="ListRecords" type="{http://www.openarchives.org/OAI/2.0/}ListRecordsType"/>
 *         &lt;/choice>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "OAI-PMHtype", propOrder = {
    "responseDate",
    "request",
    "error",
    "identify",
    "listMetadataFormats",
    "listSets",
    "getRecord",
    "listIdentifiers",
    "listRecords"
})
public class OAIPMHtype {

    @XmlElement(required = true)
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar responseDate;
    @XmlElement(required = true)
    protected RequestType request;
    protected List<OAIPMHerrorType> error;
    @XmlElement(name = "Identify")
    protected IdentifyType identify;
    @XmlElement(name = "ListMetadataFormats")
    protected ListMetadataFormatsType listMetadataFormats;
    @XmlElement(name = "ListSets")
    protected ListSetsType listSets;
    @XmlElement(name = "GetRecord")
    protected GetRecordType getRecord;
    @XmlElement(name = "ListIdentifiers")
    protected ListIdentifiersType listIdentifiers;
    @XmlElement(name = "ListRecords")
    protected ListRecordsType listRecords;

    /**
     * Obtient la valeur de la propriété responseDate.
     *
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *
     */
    public XMLGregorianCalendar getResponseDate() {
        return responseDate;
    }

    /**
     * Définit la valeur de la propriété responseDate.
     *
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *
     */
    public void setResponseDate(XMLGregorianCalendar value) {
        this.responseDate = value;
    }

    /**
     * Obtient la valeur de la propriété request.
     *
     * @return
     *     possible object is
     *     {@link RequestType }
     *
     */
    public RequestType getRequest() {
        return request;
    }

    /**
     * Définit la valeur de la propriété request.
     *
     * @param value
     *     allowed object is
     *     {@link RequestType }
     *
     */
    public void setRequest(RequestType value) {
        this.request = value;
    }

    /**
     * Gets the value of the error property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the error property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getError().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link OAIPMHerrorType }
     *
     *
     */
    public List<OAIPMHerrorType> getError() {
        if (error == null) {
            error = new ArrayList<>();
        }
        return this.error;
    }

    /**
     * Obtient la valeur de la propriété identify.
     *
     * @return
     *     possible object is
     *     {@link IdentifyType }
     *
     */
    public IdentifyType getIdentify() {
        return identify;
    }

    /**
     * Définit la valeur de la propriété identify.
     *
     * @param value
     *     allowed object is
     *     {@link IdentifyType }
     *
     */
    public void setIdentify(IdentifyType value) {
        this.identify = value;
    }

    /**
     * Obtient la valeur de la propriété listMetadataFormats.
     *
     * @return
     *     possible object is
     *     {@link ListMetadataFormatsType }
     *
     */
    public ListMetadataFormatsType getListMetadataFormats() {
        return listMetadataFormats;
    }

    /**
     * Définit la valeur de la propriété listMetadataFormats.
     *
     * @param value
     *     allowed object is
     *     {@link ListMetadataFormatsType }
     *
     */
    public void setListMetadataFormats(ListMetadataFormatsType value) {
        this.listMetadataFormats = value;
    }

    /**
     * Obtient la valeur de la propriété listSets.
     *
     * @return
     *     possible object is
     *     {@link ListSetsType }
     *
     */
    public ListSetsType getListSets() {
        return listSets;
    }

    /**
     * Définit la valeur de la propriété listSets.
     *
     * @param value
     *     allowed object is
     *     {@link ListSetsType }
     *
     */
    public void setListSets(ListSetsType value) {
        this.listSets = value;
    }

    /**
     * Obtient la valeur de la propriété getRecord.
     *
     * @return
     *     possible object is
     *     {@link GetRecordType }
     *
     */
    public GetRecordType getGetRecord() {
        return getRecord;
    }

    /**
     * Définit la valeur de la propriété getRecord.
     *
     * @param value
     *     allowed object is
     *     {@link GetRecordType }
     *
     */
    public void setGetRecord(GetRecordType value) {
        this.getRecord = value;
    }

    /**
     * Obtient la valeur de la propriété listIdentifiers.
     *
     * @return
     *     possible object is
     *     {@link ListIdentifiersType }
     *
     */
    public ListIdentifiersType getListIdentifiers() {
        return listIdentifiers;
    }

    /**
     * Définit la valeur de la propriété listIdentifiers.
     *
     * @param value
     *     allowed object is
     *     {@link ListIdentifiersType }
     *
     */
    public void setListIdentifiers(ListIdentifiersType value) {
        this.listIdentifiers = value;
    }

    /**
     * Obtient la valeur de la propriété listRecords.
     *
     * @return
     *     possible object is
     *     {@link ListRecordsType }
     *
     */
    public ListRecordsType getListRecords() {
        return listRecords;
    }

    /**
     * Définit la valeur de la propriété listRecords.
     *
     * @param value
     *     allowed object is
     *     {@link ListRecordsType }
     *
     */
    public void setListRecords(ListRecordsType value) {
        this.listRecords = value;
    }

}
