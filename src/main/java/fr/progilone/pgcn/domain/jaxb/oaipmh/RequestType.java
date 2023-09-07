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
import jakarta.xml.bind.annotation.XmlSchemaType;
import jakarta.xml.bind.annotation.XmlType;
import jakarta.xml.bind.annotation.XmlValue;

/**
 * Define requestType, indicating the protocol request that
 * led to the response. Element content is BASE-URL, attributes are arguments
 * of protocol request, attribute-values are values of arguments of protocol
 * request
 *
 * <p>
 * Classe Java pour requestType complex type.
 *
 * <p>
 * Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 *
 * <pre>
 * &lt;complexType name="requestType">
 *   &lt;simpleContent>
 *     &lt;extension base="&lt;http://www.w3.org/2001/XMLSchema>anyURI">
 *       &lt;attribute name="verb" type="{http://www.openarchives.org/OAI/2.0/}verbType" />
 *       &lt;attribute name="identifier" type="{http://www.openarchives.org/OAI/2.0/}identifierType" />
 *       &lt;attribute name="metadataPrefix" type="{http://www.openarchives.org/OAI/2.0/}metadataPrefixType" />
 *       &lt;attribute name="from" type="{http://www.openarchives.org/OAI/2.0/}UTCdatetimeType" />
 *       &lt;attribute name="until" type="{http://www.openarchives.org/OAI/2.0/}UTCdatetimeType" />
 *       &lt;attribute name="set" type="{http://www.openarchives.org/OAI/2.0/}setSpecType" />
 *       &lt;attribute name="resumptionToken" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/extension>
 *   &lt;/simpleContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "requestType", propOrder = {"value"})
public class RequestType {

    @XmlValue
    @XmlSchemaType(name = "anyURI")
    protected String value;
    @XmlAttribute(name = "verb")
    protected VerbType verb;
    @XmlAttribute(name = "identifier")
    protected String identifier;
    @XmlAttribute(name = "metadataPrefix")
    protected String metadataPrefix;
    @XmlAttribute(name = "from")
    protected String from;
    @XmlAttribute(name = "until")
    protected String until;
    @XmlAttribute(name = "set")
    protected String set;
    @XmlAttribute(name = "resumptionToken")
    protected String resumptionToken;

    /**
     * Obtient la valeur de la propriété value.
     *
     * @return
     *         possible object is
     *         {@link String }
     *
     */
    public String getValue() {
        return value;
    }

    /**
     * Définit la valeur de la propriété value.
     *
     * @param value
     *            allowed object is
     *            {@link String }
     *
     */
    public void setValue(String value) {
        this.value = value;
    }

    /**
     * Obtient la valeur de la propriété verb.
     *
     * @return
     *         possible object is
     *         {@link VerbType }
     *
     */
    public VerbType getVerb() {
        return verb;
    }

    /**
     * Définit la valeur de la propriété verb.
     *
     * @param value
     *            allowed object is
     *            {@link VerbType }
     *
     */
    public void setVerb(VerbType value) {
        this.verb = value;
    }

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
     * Obtient la valeur de la propriété metadataPrefix.
     *
     * @return
     *         possible object is
     *         {@link String }
     *
     */
    public String getMetadataPrefix() {
        return metadataPrefix;
    }

    /**
     * Définit la valeur de la propriété metadataPrefix.
     *
     * @param value
     *            allowed object is
     *            {@link String }
     *
     */
    public void setMetadataPrefix(String value) {
        this.metadataPrefix = value;
    }

    /**
     * Obtient la valeur de la propriété from.
     *
     * @return
     *         possible object is
     *         {@link String }
     *
     */
    public String getFrom() {
        return from;
    }

    /**
     * Définit la valeur de la propriété from.
     *
     * @param value
     *            allowed object is
     *            {@link String }
     *
     */
    public void setFrom(String value) {
        this.from = value;
    }

    /**
     * Obtient la valeur de la propriété until.
     *
     * @return
     *         possible object is
     *         {@link String }
     *
     */
    public String getUntil() {
        return until;
    }

    /**
     * Définit la valeur de la propriété until.
     *
     * @param value
     *            allowed object is
     *            {@link String }
     *
     */
    public void setUntil(String value) {
        this.until = value;
    }

    /**
     * Obtient la valeur de la propriété set.
     *
     * @return
     *         possible object is
     *         {@link String }
     *
     */
    public String getSet() {
        return set;
    }

    /**
     * Définit la valeur de la propriété set.
     *
     * @param value
     *            allowed object is
     *            {@link String }
     *
     */
    public void setSet(String value) {
        this.set = value;
    }

    /**
     * Obtient la valeur de la propriété resumptionToken.
     *
     * @return
     *         possible object is
     *         {@link String }
     *
     */
    public String getResumptionToken() {
        return resumptionToken;
    }

    /**
     * Définit la valeur de la propriété resumptionToken.
     *
     * @param value
     *            allowed object is
     *            {@link String }
     *
     */
    public void setResumptionToken(String value) {
        this.resumptionToken = value;
    }

}
