//
// Ce fichier a été généré par l'implémentation de référence JavaTM Architecture for XML Binding (JAXB), v2.2.8-b130911.1802
// Voir <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a>
// Toute modification apportée à fichier sera perdue lors de la recompilation du schéma source.
// Généré le : 2017.12.08 à 03:23:15 PM CET
//


package fr.progilone.pgcn.domain.jaxb.ppdi;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 *
 *
 * <pre>
 * &lt;?xml version="1.0" encoding="UTF-8"?&gt;&lt;description xmlns="http://www.cines.fr/pac/ppdi" xmlns:ISO-639-3="urn:un:unece:uncefact:codelist:draft:DAF:languageCode:2011-10-07" xmlns:RA="urn:un:unece:uncefact:codelist:draft:DAF:accessRestrictionCode:2009-08-18" xmlns:xsd="http://www.w3.org/2001/XMLSchema"&gt;Pr�sentation du fonds � archiver&lt;/description&gt;
 * </pre>
 *
 *
 * <pre>
 * &lt;?xml version="1.0" encoding="UTF-8"?&gt;&lt;commentaires xmlns="http://www.cines.fr/pac/ppdi" xmlns:ISO-639-3="urn:un:unece:uncefact:codelist:draft:DAF:languageCode:2011-10-07" xmlns:RA="urn:un:unece:uncefact:codelist:draft:DAF:accessRestrictionCode:2009-08-18" xmlns:xsd="http://www.w3.org/2001/XMLSchema"&gt;
 * On entend par fonds, un ensemble de documents � archiver dont le regroupement a un sens intellectuel. On peut �galement parler de corpus.
 * &lt;/commentaires&gt;
 * </pre>
 *
 *
 *
 * <p>Classe Java pour FondsType complex type.
 *
 * <p>Le fragment de sch�ma suivant indique le contenu attendu figurant dans cette classe.
 *
 * <pre>
 * &lt;complexType name="FondsType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element ref="{http://www.cines.fr/pac/ppdi}intitule"/>
 *         &lt;element ref="{http://www.cines.fr/pac/ppdi}contenu"/>
 *         &lt;element ref="{http://www.cines.fr/pac/ppdi}langue" maxOccurs="unbounded"/>
 *         &lt;element ref="{http://www.cines.fr/pac/ppdi}structure" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element ref="{http://www.cines.fr/pac/ppdi}quantite" maxOccurs="unbounded"/>
 *         &lt;element ref="{http://www.cines.fr/pac/ppdi}sortFinal" maxOccurs="unbounded"/>
 *         &lt;element ref="{http://www.cines.fr/pac/ppdi}original" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element ref="{http://www.cines.fr/pac/ppdi}communauteCible" maxOccurs="unbounded"/>
 *         &lt;element ref="{http://www.cines.fr/pac/ppdi}sourcesComplementaires" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element ref="{http://www.cines.fr/pac/ppdi}relationFdsProd" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element ref="{http://www.cines.fr/pac/ppdi}notesFonds" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "FondsType", propOrder = {
    "intitule",
    "contenu",
    "langue",
    "structure",
    "quantite",
    "sortFinal",
    "original",
    "communauteCible",
    "sourcesComplementaires",
    "relationFdsProd",
    "notesFonds"
})
public class FondsType {

    @XmlElement(required = true)
    protected String intitule;
    @XmlElement(required = true)
    protected String contenu;
    @XmlElement(required = true)
    protected List<String> langue;
    protected List<String> structure;
    @XmlElement(required = true)
    protected List<String> quantite;
    @XmlElement(required = true)
    protected List<String> sortFinal;
    protected List<String> original;
    @XmlElement(required = true)
    protected List<String> communauteCible;
    protected List<String> sourcesComplementaires;
    protected List<String> relationFdsProd;
    protected List<String> notesFonds;

    /**
     * Obtient la valeur de la propri�t� intitule.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getIntitule() {
        return intitule;
    }

    /**
     * D�finit la valeur de la propri�t� intitule.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setIntitule(String value) {
        this.intitule = value;
    }

    /**
     * Obtient la valeur de la propri�t� contenu.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getContenu() {
        return contenu;
    }

    /**
     * D�finit la valeur de la propri�t� contenu.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setContenu(String value) {
        this.contenu = value;
    }

    /**
     * Gets the value of the langue property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the langue property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getLangue().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     *
     *
     */
    public List<String> getLangue() {
        if (langue == null) {
            langue = new ArrayList<>();
        }
        return this.langue;
    }

    /**
     * Gets the value of the structure property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the structure property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getStructure().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     *
     *
     */
    public List<String> getStructure() {
        if (structure == null) {
            structure = new ArrayList<>();
        }
        return this.structure;
    }

    /**
     * Gets the value of the quantite property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the quantite property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getQuantite().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     *
     *
     */
    public List<String> getQuantite() {
        if (quantite == null) {
            quantite = new ArrayList<>();
        }
        return this.quantite;
    }

    /**
     * Gets the value of the sortFinal property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the sortFinal property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getSortFinal().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     *
     *
     */
    public List<String> getSortFinal() {
        if (sortFinal == null) {
            sortFinal = new ArrayList<>();
        }
        return this.sortFinal;
    }

    /**
     * Gets the value of the original property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the original property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getOriginal().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     *
     *
     */
    public List<String> getOriginal() {
        if (original == null) {
            original = new ArrayList<>();
        }
        return this.original;
    }

    /**
     * Gets the value of the communauteCible property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the communauteCible property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getCommunauteCible().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     *
     *
     */
    public List<String> getCommunauteCible() {
        if (communauteCible == null) {
            communauteCible = new ArrayList<>();
        }
        return this.communauteCible;
    }

    /**
     * Gets the value of the sourcesComplementaires property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the sourcesComplementaires property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getSourcesComplementaires().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     *
     *
     */
    public List<String> getSourcesComplementaires() {
        if (sourcesComplementaires == null) {
            sourcesComplementaires = new ArrayList<>();
        }
        return this.sourcesComplementaires;
    }

    /**
     * Gets the value of the relationFdsProd property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the relationFdsProd property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getRelationFdsProd().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     *
     *
     */
    public List<String> getRelationFdsProd() {
        if (relationFdsProd == null) {
            relationFdsProd = new ArrayList<>();
        }
        return this.relationFdsProd;
    }

    /**
     * Gets the value of the notesFonds property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the notesFonds property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getNotesFonds().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     *
     *
     */
    public List<String> getNotesFonds() {
        if (notesFonds == null) {
            notesFonds = new ArrayList<>();
        }
        return this.notesFonds;
    }

}
