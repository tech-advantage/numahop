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
 * <p>Classe Java pour evaluationType complex type.
 * 
 * <p>Le fragment de sch�ma suivant indique le contenu attendu figurant dans cette classe.
 * 
 * <pre>
 * &lt;complexType name="evaluationType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="DUA" type="{http://www.cines.fr/pac/ppdi}stringNotNULL"/>
 *         &lt;element name="traitement" type="{http://www.cines.fr/pac/ppdi}stringNotNULL"/>
 *         &lt;element name="dateDebut" type="{http://www.cines.fr/pac/ppdi}stringNotNULL"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "evaluationType", propOrder = {
    "dua",
    "traitement",
    "dateDebut"
})
public class EvaluationType {

    @XmlElement(name = "DUA", required = true)
    protected String dua;
    @XmlElement(required = true)
    protected String traitement;
    @XmlElement(required = true)
    protected String dateDebut;

    /**
     * Obtient la valeur de la propri�t� dua.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDUA() {
        return dua;
    }

    /**
     * D�finit la valeur de la propri�t� dua.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDUA(String value) {
        this.dua = value;
    }

    /**
     * Obtient la valeur de la propri�t� traitement.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTraitement() {
        return traitement;
    }

    /**
     * D�finit la valeur de la propri�t� traitement.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTraitement(String value) {
        this.traitement = value;
    }

    /**
     * Obtient la valeur de la propri�t� dateDebut.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDateDebut() {
        return dateDebut;
    }

    /**
     * D�finit la valeur de la propri�t� dateDebut.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDateDebut(String value) {
        this.dateDebut = value;
    }

}
