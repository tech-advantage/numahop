//
// Ce fichier a été généré par l'implémentation de référence JavaTM Architecture for XML Binding (JAXB), v2.2.11 
// Voir <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Toute modification apportée à ce fichier sera perdue lors de la recompilation du schéma source. 
// Généré le : 2017.01.03 à 11:16:35 AM CET 
//


package fr.progilone.pgcn.domain.jaxb.aip;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * 
 * 				
 * <pre>
 * &lt;?xml version="1.0" encoding="UTF-8"?&gt;&lt;description xmlns="http://www.cines.fr/pac/test/aip" xmlns:ISO-639-3="urn:un:unece:uncefact:codelist:draft:DAF:languageCode:2011-10-07" xmlns:RA="urn:un:unece:uncefact:codelist:draft:DAF:accessRestrictionCode:2009-08-18" xmlns:xsd="http://www.w3.org/2001/XMLSchema"&gt;caractérise des relations entre les documents&lt;/description&gt;
 * </pre>
 * 
 * 			
 * 
 * <p>Classe Java pour docRelationType complex type.
 * 
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 * 
 * <pre>
 * &lt;complexType name="docRelationType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element ref="{http://www.cines.fr/pac/test/aip}typeRelation"/&gt;
 *         &lt;element ref="{http://www.cines.fr/pac/test/aip}sourceRelation"/&gt;
 *         &lt;element ref="{http://www.cines.fr/pac/test/aip}identifiantSourceRelation"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "docRelationType", propOrder = {
    "typeRelation",
    "sourceRelation",
    "identifiantSourceRelation"
})
public class DocRelationType {

    @XmlElement(required = true)
    protected String typeRelation;
    @XmlElement(required = true)
    protected String sourceRelation;
    @XmlElement(required = true)
    protected String identifiantSourceRelation;

    /**
     * Obtient la valeur de la propriété typeRelation.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTypeRelation() {
        return typeRelation;
    }

    /**
     * Définit la valeur de la propriété typeRelation.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTypeRelation(String value) {
        this.typeRelation = value;
    }

    /**
     * Obtient la valeur de la propriété sourceRelation.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSourceRelation() {
        return sourceRelation;
    }

    /**
     * Définit la valeur de la propriété sourceRelation.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSourceRelation(String value) {
        this.sourceRelation = value;
    }

    /**
     * Obtient la valeur de la propriété identifiantSourceRelation.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getIdentifiantSourceRelation() {
        return identifiantSourceRelation;
    }

    /**
     * Définit la valeur de la propriété identifiantSourceRelation.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setIdentifiantSourceRelation(String value) {
        this.identifiantSourceRelation = value;
    }

}
