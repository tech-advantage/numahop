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
 * Pr�sentation du service versant responsable du versement ou du d�p�t du fonds dans le syst�me d'archivage �lectronique du CINES.
 *
 *
 * <p>Classe Java pour ServiceVersantType complex type.
 *
 * <p>Le fragment de sch�ma suivant indique le contenu attendu figurant dans cette classe.
 *
 * <pre>
 * &lt;complexType name="ServiceVersantType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element ref="{http://www.cines.fr/pac/ppdi}nomSV"/>
 *         &lt;element ref="{http://www.cines.fr/pac/ppdi}datesSV"/>
 *         &lt;element ref="{http://www.cines.fr/pac/ppdi}historiqueSV" maxOccurs="unbounded"/>
 *         &lt;element ref="{http://www.cines.fr/pac/ppdi}relationSvProd" maxOccurs="unbounded"/>
 *         &lt;element ref="{http://www.cines.fr/pac/ppdi}notesSV" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ServiceVersantType", propOrder = {
    "nomSV",
    "datesSV",
    "historiqueSV",
    "relationSvProd",
    "notesSV"
})
public class ServiceVersantType {

    @XmlElement(required = true)
    protected String nomSV;
    @XmlElement(required = true)
    protected String datesSV;
    @XmlElement(required = true)
    protected List<String> historiqueSV;
    @XmlElement(required = true)
    protected List<String> relationSvProd;
    protected List<String> notesSV;

    /**
     * Obtient la valeur de la propri�t� nomSV.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getNomSV() {
        return nomSV;
    }

    /**
     * D�finit la valeur de la propri�t� nomSV.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setNomSV(String value) {
        this.nomSV = value;
    }

    /**
     * Obtient la valeur de la propri�t� datesSV.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getDatesSV() {
        return datesSV;
    }

    /**
     * D�finit la valeur de la propri�t� datesSV.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setDatesSV(String value) {
        this.datesSV = value;
    }

    /**
     * Gets the value of the historiqueSV property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the historiqueSV property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getHistoriqueSV().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     *
     *
     */
    public List<String> getHistoriqueSV() {
        if (historiqueSV == null) {
            historiqueSV = new ArrayList<>();
        }
        return this.historiqueSV;
    }

    /**
     * Gets the value of the relationSvProd property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the relationSvProd property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getRelationSvProd().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     *
     *
     */
    public List<String> getRelationSvProd() {
        if (relationSvProd == null) {
            relationSvProd = new ArrayList<>();
        }
        return this.relationSvProd;
    }

    /**
     * Gets the value of the notesSV property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the notesSV property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getNotesSV().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     *
     *
     */
    public List<String> getNotesSV() {
        if (notesSV == null) {
            notesSV = new ArrayList<>();
        }
        return this.notesSV;
    }

}
