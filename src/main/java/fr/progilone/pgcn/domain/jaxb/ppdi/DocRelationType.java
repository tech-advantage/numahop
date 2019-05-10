//
// Ce fichier a été généré par l'implémentation de référence JavaTM Architecture for XML Binding (JAXB), v2.2.8-b130911.1802 
// Voir <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Toute modification apportée à fichier sera perdue lors de la recompilation du schéma source. 
// Généré le : 2017.12.08 à 03:23:15 PM CET 
//


package fr.progilone.pgcn.domain.jaxb.ppdi;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Classe Java pour docRelationType complex type.
 * 
 * <p>Le fragment de sch�ma suivant indique le contenu attendu figurant dans cette classe.
 * 
 * <pre>
 * &lt;complexType name="docRelationType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="typeRelation" type="{http://www.cines.fr/pac/ppdi}stringNotNULL"/>
 *         &lt;element name="sourceRelation" type="{http://www.cines.fr/pac/ppdi}stringNotNULL"/>
 *         &lt;element name="identifiantSourceRelation" type="{http://www.cines.fr/pac/ppdi}stringNotNULL"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
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
     * Obtient la valeur de la propri�t� typeRelation.
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
     * D�finit la valeur de la propri�t� typeRelation.
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
     * Obtient la valeur de la propri�t� sourceRelation.
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
     * D�finit la valeur de la propri�t� sourceRelation.
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
     * Obtient la valeur de la propri�t� identifiantSourceRelation.
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
     * D�finit la valeur de la propri�t� identifiantSourceRelation.
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
