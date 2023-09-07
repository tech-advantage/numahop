//
// Ce fichier a été généré par l'implémentation de référence JavaTM Architecture for XML Binding (JAXB), v2.2.8-b130911.1802
// Voir <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a>
// Toute modification apportée à fichier sera perdue lors de la recompilation du schéma source.
// Généré le : 2017.12.08 à 03:23:15 PM CET
//

package fr.progilone.pgcn.domain.jaxb.ppdi;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * Classe Java pour DocDCDescriptionType complex type.
 *
 * <p>
 * Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 *
 * <pre>
 * &lt;complexType name="DocDCDescriptionType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="title" type="{http://www.cines.fr/pac/ppdi}stringNotNULL" maxOccurs="unbounded"/>
 *         &lt;element name="creator" type="{http://www.cines.fr/pac/ppdi}stringNotNULL" maxOccurs="unbounded"/>
 *         &lt;element name="subject" type="{http://www.cines.fr/pac/ppdi}stringNotNULL" maxOccurs="unbounded"/>
 *         &lt;element name="description" type="{http://www.cines.fr/pac/ppdi}stringNotNULL" maxOccurs="unbounded"/>
 *         &lt;element name="publisher" type="{http://www.cines.fr/pac/ppdi}stringNotNULL" maxOccurs="unbounded"/>
 *         &lt;element name="contributor" type="{http://www.cines.fr/pac/ppdi}stringNotNULL" maxOccurs="unbounded"/>
 *         &lt;element name="date" type="{http://www.cines.fr/pac/ppdi}stringNotNULL" maxOccurs="unbounded"/>
 *         &lt;element name="type" type="{http://www.cines.fr/pac/ppdi}stringNotNULL" maxOccurs="unbounded"/>
 *         &lt;element name="format" type="{http://www.cines.fr/pac/ppdi}stringNotNULL" maxOccurs="unbounded"/>
 *         &lt;element name="source" type="{http://www.cines.fr/pac/ppdi}stringNotNULL" maxOccurs="unbounded"/>
 *         &lt;element name="language" type="{http://www.cines.fr/pac/ppdi}stringNotNULL" maxOccurs="unbounded"/>
 *         &lt;element name="relation" type="{http://www.cines.fr/pac/ppdi}stringNotNULL" maxOccurs="unbounded"/>
 *         &lt;element name="coverage" type="{http://www.cines.fr/pac/ppdi}stringNotNULL" maxOccurs="unbounded"/>
 *         &lt;element name="rights" type="{http://www.cines.fr/pac/ppdi}stringNotNULL" maxOccurs="unbounded"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "DocDCDescriptionType",
         propOrder = {"title",
                      "creator",
                      "subject",
                      "description",
                      "publisher",
                      "contributor",
                      "date",
                      "type",
                      "format",
                      "source",
                      "language",
                      "relation",
                      "coverage",
                      "rights"})
public class DocDCDescriptionType {

    @XmlElement(required = true)
    protected List<String> title;
    @XmlElement(required = true)
    protected List<String> creator;
    @XmlElement(required = true)
    protected List<String> subject;
    @XmlElement(required = true)
    protected List<String> description;
    @XmlElement(required = true)
    protected List<String> publisher;
    @XmlElement(required = true)
    protected List<String> contributor;
    @XmlElement(required = true)
    protected List<String> date;
    @XmlElement(required = true)
    protected List<String> type;
    @XmlElement(required = true)
    protected List<String> format;
    @XmlElement(required = true)
    protected List<String> source;
    @XmlElement(required = true)
    protected List<String> language;
    @XmlElement(required = true)
    protected List<String> relation;
    @XmlElement(required = true)
    protected List<String> coverage;
    @XmlElement(required = true)
    protected List<String> rights;

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
     * {@link String }
     *
     *
     */
    public List<String> getTitle() {
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
     * {@link String }
     *
     *
     */
    public List<String> getSubject() {
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
     * {@link String }
     *
     *
     */
    public List<String> getDescription() {
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
     * Gets the value of the date property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the date property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     *
     * <pre>
     * getDate().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     *
     *
     */
    public List<String> getDate() {
        if (date == null) {
            date = new ArrayList<>();
        }
        return this.date;
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
     * {@link String }
     *
     *
     */
    public List<String> getType() {
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
     * {@link String }
     *
     *
     */
    public List<String> getFormat() {
        if (format == null) {
            format = new ArrayList<>();
        }
        return this.format;
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
     * {@link String }
     *
     *
     */
    public List<String> getSource() {
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
     * {@link String }
     *
     *
     */
    public List<String> getRelation() {
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
     * {@link String }
     *
     *
     */
    public List<String> getCoverage() {
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
     * {@link String }
     *
     *
     */
    public List<String> getRights() {
        if (rights == null) {
            rights = new ArrayList<>();
        }
        return this.rights;
    }

}
