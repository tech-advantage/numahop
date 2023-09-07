//
// Ce fichier a été généré par l'implémentation de référence JavaTM Architecture for XML Binding (JAXB), v2.2.8-b130911.1802
// Voir <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a>
// Toute modification apportée à ce fichier sera perdue lors de la recompilation du schéma source.
// Généré le : 2018.08.13 à 01:08:19 PM CEST
//

package fr.progilone.pgcn.domain.jaxb.oaipmh;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlSchemaType;
import jakarta.xml.bind.annotation.XmlType;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * Classe Java pour IdentifyType complex type.
 *
 * <p>
 * Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 *
 * <pre>
 * &lt;complexType name="IdentifyType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="repositoryName" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="baseURL" type="{http://www.w3.org/2001/XMLSchema}anyURI"/>
 *         &lt;element name="protocolVersion" type="{http://www.openarchives.org/OAI/2.0/}protocolVersionType"/>
 *         &lt;element name="adminEmail" type="{http://www.openarchives.org/OAI/2.0/}emailType" maxOccurs="unbounded"/>
 *         &lt;element name="earliestDatestamp" type="{http://www.openarchives.org/OAI/2.0/}UTCdatetimeType"/>
 *         &lt;element name="deletedRecord" type="{http://www.openarchives.org/OAI/2.0/}deletedRecordType"/>
 *         &lt;element name="granularity" type="{http://www.openarchives.org/OAI/2.0/}granularityType"/>
 *         &lt;element name="compression" type="{http://www.w3.org/2001/XMLSchema}string" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="description" type="{http://www.openarchives.org/OAI/2.0/}descriptionType" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "IdentifyType",
         propOrder = {"repositoryName",
                      "baseURL",
                      "protocolVersion",
                      "adminEmail",
                      "earliestDatestamp",
                      "deletedRecord",
                      "granularity",
                      "compression",
                      "description"})
public class IdentifyType {

    @XmlElement(required = true)
    protected String repositoryName;
    @XmlElement(required = true)
    @XmlSchemaType(name = "anyURI")
    protected String baseURL;
    @XmlElement(required = true)
    protected String protocolVersion;
    @XmlElement(required = true)
    protected List<String> adminEmail;
    @XmlElement(required = true)
    protected String earliestDatestamp;
    @XmlElement(required = true)
    @XmlSchemaType(name = "string")
    protected DeletedRecordType deletedRecord;
    @XmlElement(required = true)
    @XmlSchemaType(name = "string")
    protected GranularityType granularity;
    protected List<String> compression;
    protected List<DescriptionType> description;

    /**
     * Obtient la valeur de la propriété repositoryName.
     *
     * @return
     *         possible object is
     *         {@link String }
     *
     */
    public String getRepositoryName() {
        return repositoryName;
    }

    /**
     * Définit la valeur de la propriété repositoryName.
     *
     * @param value
     *            allowed object is
     *            {@link String }
     *
     */
    public void setRepositoryName(String value) {
        this.repositoryName = value;
    }

    /**
     * Obtient la valeur de la propriété baseURL.
     *
     * @return
     *         possible object is
     *         {@link String }
     *
     */
    public String getBaseURL() {
        return baseURL;
    }

    /**
     * Définit la valeur de la propriété baseURL.
     *
     * @param value
     *            allowed object is
     *            {@link String }
     *
     */
    public void setBaseURL(String value) {
        this.baseURL = value;
    }

    /**
     * Obtient la valeur de la propriété protocolVersion.
     *
     * @return
     *         possible object is
     *         {@link String }
     *
     */
    public String getProtocolVersion() {
        return protocolVersion;
    }

    /**
     * Définit la valeur de la propriété protocolVersion.
     *
     * @param value
     *            allowed object is
     *            {@link String }
     *
     */
    public void setProtocolVersion(String value) {
        this.protocolVersion = value;
    }

    /**
     * Gets the value of the adminEmail property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the adminEmail property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     *
     * <pre>
     * getAdminEmail().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     *
     *
     */
    public List<String> getAdminEmail() {
        if (adminEmail == null) {
            adminEmail = new ArrayList<>();
        }
        return this.adminEmail;
    }

    /**
     * Obtient la valeur de la propriété earliestDatestamp.
     *
     * @return
     *         possible object is
     *         {@link String }
     *
     */
    public String getEarliestDatestamp() {
        return earliestDatestamp;
    }

    /**
     * Définit la valeur de la propriété earliestDatestamp.
     *
     * @param value
     *            allowed object is
     *            {@link String }
     *
     */
    public void setEarliestDatestamp(String value) {
        this.earliestDatestamp = value;
    }

    /**
     * Obtient la valeur de la propriété deletedRecord.
     *
     * @return
     *         possible object is
     *         {@link DeletedRecordType }
     *
     */
    public DeletedRecordType getDeletedRecord() {
        return deletedRecord;
    }

    /**
     * Définit la valeur de la propriété deletedRecord.
     *
     * @param value
     *            allowed object is
     *            {@link DeletedRecordType }
     *
     */
    public void setDeletedRecord(DeletedRecordType value) {
        this.deletedRecord = value;
    }

    /**
     * Obtient la valeur de la propriété granularity.
     *
     * @return
     *         possible object is
     *         {@link GranularityType }
     *
     */
    public GranularityType getGranularity() {
        return granularity;
    }

    /**
     * Définit la valeur de la propriété granularity.
     *
     * @param value
     *            allowed object is
     *            {@link GranularityType }
     *
     */
    public void setGranularity(GranularityType value) {
        this.granularity = value;
    }

    /**
     * Gets the value of the compression property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the compression property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     *
     * <pre>
     * getCompression().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     *
     *
     */
    public List<String> getCompression() {
        if (compression == null) {
            compression = new ArrayList<>();
        }
        return this.compression;
    }

    /**
     * Gets the value of the description property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the description property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     *
     * <pre>
     * getDescription().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link DescriptionType }
     *
     *
     */
    public List<DescriptionType> getDescription() {
        if (description == null) {
            description = new ArrayList<>();
        }
        return this.description;
    }

}
