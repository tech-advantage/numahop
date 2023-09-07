//
// Ce fichier a été généré par l'implémentation de référence JavaTM Architecture for XML Binding (JAXB), v2.2.8-b130911.1802
// Voir <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a>
// Toute modification apportée à ce fichier sera perdue lors de la recompilation du schéma source.
// Généré le : 2018.08.13 à 01:08:19 PM CEST
//

package fr.progilone.pgcn.domain.jaxb.oaipmh;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlSchemaType;
import jakarta.xml.bind.annotation.XmlType;
import java.util.ArrayList;
import java.util.List;

/**
 * A header has a unique identifier, a datestamp,
 * and setSpec(s) in case the item from which
 * the record is disseminated belongs to set(s).
 * the header can carry a deleted status indicating
 * that the record is deleted.
 *
 * <p>
 * Classe Java pour headerType complex type.
 *
 * <p>
 * Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 *
 * <pre>
 * &lt;complexType name="headerType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="identifier" type="{http://www.openarchives.org/OAI/2.0/}identifierType"/>
 *         &lt;element name="datestamp" type="{http://www.openarchives.org/OAI/2.0/}UTCdatetimeType"/>
 *         &lt;element name="setSpec" type="{http://www.openarchives.org/OAI/2.0/}setSpecType" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="status" type="{http://www.openarchives.org/OAI/2.0/}statusType" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "headerType",
         propOrder = {"identifier",
                      "datestamp",
                      "setSpec"})
public class HeaderType {

    @XmlElement(required = true)
    @XmlSchemaType(name = "anyURI")
    protected String identifier;
    @XmlElement(required = true)
    protected String datestamp;
    protected List<String> setSpec;
    @XmlAttribute(name = "status")
    protected StatusType status;

    /**
     * Obtient la valeur de la propriété identifier.
     *
     * @return
     *         possible object is
     *         {@link String }
     *
     */
    public String getIdentifier() {
        return identifier;
    }

    /**
     * Définit la valeur de la propriété identifier.
     *
     * @param value
     *            allowed object is
     *            {@link String }
     *
     */
    public void setIdentifier(String value) {
        this.identifier = value;
    }

    /**
     * Obtient la valeur de la propriété datestamp.
     *
     * @return
     *         possible object is
     *         {@link String }
     *
     */
    public String getDatestamp() {
        return datestamp;
    }

    /**
     * Définit la valeur de la propriété datestamp.
     *
     * @param value
     *            allowed object is
     *            {@link String }
     *
     */
    public void setDatestamp(String value) {
        this.datestamp = value;
    }

    /**
     * Gets the value of the setSpec property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the setSpec property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     *
     * <pre>
     * getSetSpec().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     *
     *
     */
    public List<String> getSetSpec() {
        if (setSpec == null) {
            setSpec = new ArrayList<>();
        }
        return this.setSpec;
    }

    /**
     * Obtient la valeur de la propriété status.
     *
     * @return
     *         possible object is
     *         {@link StatusType }
     *
     */
    public StatusType getStatus() {
        return status;
    }

    /**
     * Définit la valeur de la propriété status.
     *
     * @param value
     *            allowed object is
     *            {@link StatusType }
     *
     */
    public void setStatus(StatusType value) {
        this.status = value;
    }

}
