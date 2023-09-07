//
// Ce fichier a été généré par l'implémentation de référence JavaTM Architecture for XML Binding (JAXB), v2.2.11
// Voir <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a>
// Toute modification apportée à ce fichier sera perdue lors de la recompilation du schéma source.
// Généré le : 2017.01.03 à 11:16:35 AM CET
//

package fr.progilone.pgcn.domain.jaxb.aip;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;
import java.util.ArrayList;
import java.util.List;

/**
 * Métadonnées descriptives du document sur une base Dublin Core
 *
 * <p>
 * Classe Java pour DocDCType complex type.
 *
 * <p>
 * Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 *
 * <pre>
 * &lt;complexType name="DocDCType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element ref="{http://www.cines.fr/pac/test/aip}title" maxOccurs="unbounded"/&gt;
 *         &lt;element ref="{http://www.cines.fr/pac/test/aip}creator" maxOccurs="unbounded"/&gt;
 *         &lt;element ref="{http://www.cines.fr/pac/test/aip}subject" maxOccurs="unbounded"/&gt;
 *         &lt;element ref="{http://www.cines.fr/pac/test/aip}description" maxOccurs="unbounded"/&gt;
 *         &lt;element ref="{http://www.cines.fr/pac/test/aip}publisher" maxOccurs="unbounded"/&gt;
 *         &lt;element ref="{http://www.cines.fr/pac/test/aip}contributor" maxOccurs="unbounded" minOccurs="0"/&gt;
 *         &lt;element ref="{http://www.cines.fr/pac/test/aip}date"/&gt;
 *         &lt;element ref="{http://www.cines.fr/pac/test/aip}type" maxOccurs="unbounded"/&gt;
 *         &lt;element ref="{http://www.cines.fr/pac/test/aip}format" maxOccurs="unbounded"/&gt;
 *         &lt;element ref="{http://www.cines.fr/pac/test/aip}identifier"/&gt;
 *         &lt;element ref="{http://www.cines.fr/pac/test/aip}source" maxOccurs="unbounded" minOccurs="0"/&gt;
 *         &lt;element ref="{http://www.cines.fr/pac/test/aip}language" maxOccurs="unbounded"/&gt;
 *         &lt;element ref="{http://www.cines.fr/pac/test/aip}relation" maxOccurs="unbounded" minOccurs="0"/&gt;
 *         &lt;element ref="{http://www.cines.fr/pac/test/aip}coverage" maxOccurs="unbounded" minOccurs="0"/&gt;
 *         &lt;element ref="{http://www.cines.fr/pac/test/aip}rights" maxOccurs="unbounded"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "DocDCType",
         propOrder = {"title",
                      "creator",
                      "subject",
                      "description",
                      "publisher",
                      "contributor",
                      "date",
                      "type",
                      "format",
                      "identifier",
                      "source",
                      "language",
                      "relation",
                      "coverage",
                      "rights"})
public class DocDCType {

    @XmlElement(required = true)
    protected List<StringNotNULLtext> title;
    @XmlElement(required = true)
    protected List<String> creator;
    @XmlElement(required = true)
    protected List<StringNotNULLtext> subject;
    @XmlElement(required = true)
    protected List<StringNotNULLtext> description;
    @XmlElement(required = true)
    protected List<String> publisher;
    protected List<String> contributor;
    @XmlElement(required = true)
    protected String date;
    @XmlElement(required = true)
    protected List<StringNotNULLtext> type;
    @XmlElement(required = true)
    protected List<StringNotNULLtext> format;
    @XmlElement(required = true)
    protected String identifier;
    protected List<StringNotNULLtext> source;
    @XmlElement(required = true)
    protected List<String> language;
    protected List<StringNotNULLtext> relation;
    protected List<StringNotNULLtext> coverage;
    @XmlElement(required = true)
    protected List<StringNotNULLtext> rights;

    /**
     * Gets the value of the title property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the title property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     *
     * <pre>
     * getTitle().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link StringNotNULLtext }
     *
     *
     */
    public List<StringNotNULLtext> getTitle() {
        if (title == null) {
            title = new ArrayList<>();
        }
        return this.title;
    }

    /**
     * Gets the value of the creator property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the creator property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     *
     * <pre>
     * getCreator().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     *
     *
     */
    public List<String> getCreator() {
        if (creator == null) {
            creator = new ArrayList<>();
        }
        return this.creator;
    }

    /**
     * Gets the value of the subject property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the subject property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     *
     * <pre>
     * getSubject().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link StringNotNULLtext }
     *
     *
     */
    public List<StringNotNULLtext> getSubject() {
        if (subject == null) {
            subject = new ArrayList<>();
        }
        return this.subject;
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
     * {@link StringNotNULLtext }
     *
     *
     */
    public List<StringNotNULLtext> getDescription() {
        if (description == null) {
            description = new ArrayList<>();
        }
        return this.description;
    }

    /**
     * Gets the value of the publisher property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the publisher property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     *
     * <pre>
     * getPublisher().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     *
     *
     */
    public List<String> getPublisher() {
        if (publisher == null) {
            publisher = new ArrayList<>();
        }
        return this.publisher;
    }

    /**
     * Gets the value of the contributor property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the contributor property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     *
     * <pre>
     * getContributor().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     *
     *
     */
    public List<String> getContributor() {
        if (contributor == null) {
            contributor = new ArrayList<>();
        }
        return this.contributor;
    }

    /**
     * Obtient la valeur de la propriété date.
     *
     * @return
     *         possible object is
     *         {@link String }
     *
     */
    public String getDate() {
        return date;
    }

    /**
     * Définit la valeur de la propriété date.
     *
     * @param value
     *            allowed object is
     *            {@link String }
     *
     */
    public void setDate(String value) {
        this.date = value;
    }

    /**
     * Gets the value of the type property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the type property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     *
     * <pre>
     * getType().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link StringNotNULLtext }
     *
     *
     */
    public List<StringNotNULLtext> getType() {
        if (type == null) {
            type = new ArrayList<>();
        }
        return this.type;
    }

    /**
     * Gets the value of the format property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the format property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     *
     * <pre>
     * getFormat().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link StringNotNULLtext }
     *
     *
     */
    public List<StringNotNULLtext> getFormat() {
        if (format == null) {
            format = new ArrayList<>();
        }
        return this.format;
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
     * Gets the value of the source property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the source property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     *
     * <pre>
     * getSource().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link StringNotNULLtext }
     *
     *
     */
    public List<StringNotNULLtext> getSource() {
        if (source == null) {
            source = new ArrayList<>();
        }
        return this.source;
    }

    /**
     * Gets the value of the language property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the language property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     *
     * <pre>
     * getLanguage().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     *
     *
     */
    public List<String> getLanguage() {
        if (language == null) {
            language = new ArrayList<>();
        }
        return this.language;
    }

    /**
     * Gets the value of the relation property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the relation property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     *
     * <pre>
     * getRelation().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link StringNotNULLtext }
     *
     *
     */
    public List<StringNotNULLtext> getRelation() {
        if (relation == null) {
            relation = new ArrayList<>();
        }
        return this.relation;
    }

    /**
     * Gets the value of the coverage property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the coverage property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     *
     * <pre>
     * getCoverage().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link StringNotNULLtext }
     *
     *
     */
    public List<StringNotNULLtext> getCoverage() {
        if (coverage == null) {
            coverage = new ArrayList<>();
        }
        return this.coverage;
    }

    /**
     * Gets the value of the rights property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the rights property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     *
     * <pre>
     * getRights().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link StringNotNULLtext }
     *
     *
     */
    public List<StringNotNULLtext> getRights() {
        if (rights == null) {
            rights = new ArrayList<>();
        }
        return this.rights;
    }

}
