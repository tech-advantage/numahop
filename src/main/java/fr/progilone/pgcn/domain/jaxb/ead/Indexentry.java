//
// Ce fichier a été généré par l'implémentation de référence JavaTM Architecture for XML Binding (JAXB), v2.2.8-b130911.1802
// Voir <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a>
// Toute modification apportée à ce fichier sera perdue lors de la recompilation du schéma source.
// Généré le : 2017.05.16 à 10:56:55 AM CEST
//


package fr.progilone.pgcn.domain.jaxb.ead;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlID;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;


/**
 * <p>Classe Java pour indexentry complex type.
 *
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 *
 * <pre>
 * &lt;complexType name="indexentry">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;choice>
 *           &lt;element name="namegrp" type="{urn:isbn:1-931666-22-9}namegrp"/>
 *           &lt;group ref="{urn:isbn:1-931666-22-9}m.access.title"/>
 *         &lt;/choice>
 *         &lt;choice minOccurs="0">
 *           &lt;element name="ptrgrp" type="{urn:isbn:1-931666-22-9}ptrgrp"/>
 *           &lt;element name="ptr" type="{urn:isbn:1-931666-22-9}ptr"/>
 *           &lt;element name="ref" type="{urn:isbn:1-931666-22-9}ref"/>
 *         &lt;/choice>
 *         &lt;element name="indexentry" type="{urn:isbn:1-931666-22-9}indexentry" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attGroup ref="{urn:isbn:1-931666-22-9}a.common"/>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "indexentry", propOrder = {
    "namegrp",
    "corpname",
    "famname",
    "geogname",
    "name",
    "occupation",
    "persname",
    "subject",
    "genreform",
    "function",
    "title",
    "ptrgrp",
    "ptr",
    "ref",
    "indexentry"
})
public class Indexentry {

    protected Namegrp namegrp;
    protected Corpname corpname;
    protected Famname famname;
    protected Geogname geogname;
    protected Name name;
    protected Occupation occupation;
    protected Persname persname;
    protected Subject subject;
    protected Genreform genreform;
    protected Function function;
    protected Title title;
    protected Ptrgrp ptrgrp;
    protected Ptr ptr;
    protected Ref ref;
    protected List<Indexentry> indexentry;
    @XmlAttribute(name = "id")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlID
    @XmlSchemaType(name = "ID")
    protected String id;
    @XmlAttribute(name = "altrender")
    @XmlSchemaType(name = "anySimpleType")
    protected String altrender;
    @XmlAttribute(name = "audience")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    protected String audience;

    /**
     * Obtient la valeur de la propriété namegrp.
     *
     * @return
     *     possible object is
     *     {@link Namegrp }
     *
     */
    public Namegrp getNamegrp() {
        return namegrp;
    }

    /**
     * Définit la valeur de la propriété namegrp.
     *
     * @param value
     *     allowed object is
     *     {@link Namegrp }
     *
     */
    public void setNamegrp(Namegrp value) {
        this.namegrp = value;
    }

    /**
     * Obtient la valeur de la propriété corpname.
     *
     * @return
     *     possible object is
     *     {@link Corpname }
     *
     */
    public Corpname getCorpname() {
        return corpname;
    }

    /**
     * Définit la valeur de la propriété corpname.
     *
     * @param value
     *     allowed object is
     *     {@link Corpname }
     *
     */
    public void setCorpname(Corpname value) {
        this.corpname = value;
    }

    /**
     * Obtient la valeur de la propriété famname.
     *
     * @return
     *     possible object is
     *     {@link Famname }
     *
     */
    public Famname getFamname() {
        return famname;
    }

    /**
     * Définit la valeur de la propriété famname.
     *
     * @param value
     *     allowed object is
     *     {@link Famname }
     *
     */
    public void setFamname(Famname value) {
        this.famname = value;
    }

    /**
     * Obtient la valeur de la propriété geogname.
     *
     * @return
     *     possible object is
     *     {@link Geogname }
     *
     */
    public Geogname getGeogname() {
        return geogname;
    }

    /**
     * Définit la valeur de la propriété geogname.
     *
     * @param value
     *     allowed object is
     *     {@link Geogname }
     *
     */
    public void setGeogname(Geogname value) {
        this.geogname = value;
    }

    /**
     * Obtient la valeur de la propriété name.
     *
     * @return
     *     possible object is
     *     {@link Name }
     *
     */
    public Name getName() {
        return name;
    }

    /**
     * Définit la valeur de la propriété name.
     *
     * @param value
     *     allowed object is
     *     {@link Name }
     *
     */
    public void setName(Name value) {
        this.name = value;
    }

    /**
     * Obtient la valeur de la propriété occupation.
     *
     * @return
     *     possible object is
     *     {@link Occupation }
     *
     */
    public Occupation getOccupation() {
        return occupation;
    }

    /**
     * Définit la valeur de la propriété occupation.
     *
     * @param value
     *     allowed object is
     *     {@link Occupation }
     *
     */
    public void setOccupation(Occupation value) {
        this.occupation = value;
    }

    /**
     * Obtient la valeur de la propriété persname.
     *
     * @return
     *     possible object is
     *     {@link Persname }
     *
     */
    public Persname getPersname() {
        return persname;
    }

    /**
     * Définit la valeur de la propriété persname.
     *
     * @param value
     *     allowed object is
     *     {@link Persname }
     *
     */
    public void setPersname(Persname value) {
        this.persname = value;
    }

    /**
     * Obtient la valeur de la propriété subject.
     *
     * @return
     *     possible object is
     *     {@link Subject }
     *
     */
    public Subject getSubject() {
        return subject;
    }

    /**
     * Définit la valeur de la propriété subject.
     *
     * @param value
     *     allowed object is
     *     {@link Subject }
     *
     */
    public void setSubject(Subject value) {
        this.subject = value;
    }

    /**
     * Obtient la valeur de la propriété genreform.
     *
     * @return
     *     possible object is
     *     {@link Genreform }
     *
     */
    public Genreform getGenreform() {
        return genreform;
    }

    /**
     * Définit la valeur de la propriété genreform.
     *
     * @param value
     *     allowed object is
     *     {@link Genreform }
     *
     */
    public void setGenreform(Genreform value) {
        this.genreform = value;
    }

    /**
     * Obtient la valeur de la propriété function.
     *
     * @return
     *     possible object is
     *     {@link Function }
     *
     */
    public Function getFunction() {
        return function;
    }

    /**
     * Définit la valeur de la propriété function.
     *
     * @param value
     *     allowed object is
     *     {@link Function }
     *
     */
    public void setFunction(Function value) {
        this.function = value;
    }

    /**
     * Obtient la valeur de la propriété title.
     *
     * @return
     *     possible object is
     *     {@link Title }
     *
     */
    public Title getTitle() {
        return title;
    }

    /**
     * Définit la valeur de la propriété title.
     *
     * @param value
     *     allowed object is
     *     {@link Title }
     *
     */
    public void setTitle(Title value) {
        this.title = value;
    }

    /**
     * Obtient la valeur de la propriété ptrgrp.
     *
     * @return
     *     possible object is
     *     {@link Ptrgrp }
     *
     */
    public Ptrgrp getPtrgrp() {
        return ptrgrp;
    }

    /**
     * Définit la valeur de la propriété ptrgrp.
     *
     * @param value
     *     allowed object is
     *     {@link Ptrgrp }
     *
     */
    public void setPtrgrp(Ptrgrp value) {
        this.ptrgrp = value;
    }

    /**
     * Obtient la valeur de la propriété ptr.
     *
     * @return
     *     possible object is
     *     {@link Ptr }
     *
     */
    public Ptr getPtr() {
        return ptr;
    }

    /**
     * Définit la valeur de la propriété ptr.
     *
     * @param value
     *     allowed object is
     *     {@link Ptr }
     *
     */
    public void setPtr(Ptr value) {
        this.ptr = value;
    }

    /**
     * Obtient la valeur de la propriété ref.
     *
     * @return
     *     possible object is
     *     {@link Ref }
     *
     */
    public Ref getRef() {
        return ref;
    }

    /**
     * Définit la valeur de la propriété ref.
     *
     * @param value
     *     allowed object is
     *     {@link Ref }
     *
     */
    public void setRef(Ref value) {
        this.ref = value;
    }

    /**
     * Gets the value of the indexentry property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the indexentry property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getIndexentry().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Indexentry }
     *
     *
     */
    public List<Indexentry> getIndexentry() {
        if (indexentry == null) {
            indexentry = new ArrayList<>();
        }
        return this.indexentry;
    }

    /**
     * Obtient la valeur de la propriété id.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getId() {
        return id;
    }

    /**
     * Définit la valeur de la propriété id.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setId(String value) {
        this.id = value;
    }

    /**
     * Obtient la valeur de la propriété altrender.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getAltrender() {
        return altrender;
    }

    /**
     * Définit la valeur de la propriété altrender.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setAltrender(String value) {
        this.altrender = value;
    }

    /**
     * Obtient la valeur de la propriété audience.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getAudience() {
        return audience;
    }

    /**
     * Définit la valeur de la propriété audience.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setAudience(String value) {
        this.audience = value;
    }

}
