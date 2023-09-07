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
import jakarta.xml.bind.annotation.XmlSchemaType;
import jakarta.xml.bind.annotation.XmlType;
import java.util.ArrayList;
import java.util.List;
import javax.xml.datatype.XMLGregorianCalendar;

/**
 *
 * Présentation du contexte d'archivage des documents au CINES.
 *
 *
 * <p>
 * Classe Java pour ArchivageType complex type.
 *
 * <p>
 * Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 *
 * <pre>
 * &lt;complexType name="ArchivageType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element ref="{http://www.cines.fr/pac/ppdi}cadre" maxOccurs="unbounded"/>
 *         &lt;element ref="{http://www.cines.fr/pac/ppdi}dateArchivage"/>
 *         &lt;element ref="{http://www.cines.fr/pac/ppdi}contexteLegal" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element ref="{http://www.cines.fr/pac/ppdi}classeService" maxOccurs="unbounded"/>
 *         &lt;element ref="{http://www.cines.fr/pac/ppdi}infoPreserv" maxOccurs="unbounded"/>
 *         &lt;element ref="{http://www.cines.fr/pac/ppdi}acces" maxOccurs="unbounded"/>
 *         &lt;element ref="{http://www.cines.fr/pac/ppdi}reproduction" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ArchivageType",
         propOrder = {"cadre",
                      "dateArchivage",
                      "contexteLegal",
                      "classeService",
                      "infoPreserv",
                      "acces",
                      "reproduction"})
public class ArchivageType {

    @XmlElement(required = true)
    @XmlSchemaType(name = "string")
    protected List<CadreType> cadre;
    @XmlElement(required = true)
    @XmlSchemaType(name = "date")
    protected XMLGregorianCalendar dateArchivage;
    protected List<String> contexteLegal;
    @XmlElement(required = true)
    protected List<String> classeService;
    @XmlElement(required = true)
    protected List<String> infoPreserv;
    @XmlElement(required = true)
    protected List<String> acces;
    protected List<String> reproduction;

    /**
     * Gets the value of the cadre property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the cadre property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     *
     * <pre>
     * getCadre().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link CadreType }
     *
     *
     */
    public List<CadreType> getCadre() {
        if (cadre == null) {
            cadre = new ArrayList<>();
        }
        return this.cadre;
    }

    /**
     * Obtient la valeur de la propriété dateArchivage.
     *
     * @return
     *         possible object is
     *         {@link XMLGregorianCalendar }
     *
     */
    public XMLGregorianCalendar getDateArchivage() {
        return dateArchivage;
    }

    /**
     * Définit la valeur de la propriété dateArchivage.
     *
     * @param value
     *            allowed object is
     *            {@link XMLGregorianCalendar }
     *
     */
    public void setDateArchivage(final XMLGregorianCalendar value) {
        this.dateArchivage = value;
    }

    /**
     * Gets the value of the contexteLegal property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the contexteLegal property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     *
     * <pre>
     * getContexteLegal().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     *
     *
     */
    public List<String> getContexteLegal() {
        if (contexteLegal == null) {
            contexteLegal = new ArrayList<>();
        }
        return this.contexteLegal;
    }

    /**
     * Gets the value of the classeService property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the classeService property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     *
     * <pre>
     * getClasseService().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     *
     *
     */
    public List<String> getClasseService() {
        if (classeService == null) {
            classeService = new ArrayList<>();
        }
        return this.classeService;
    }

    /**
     * Gets the value of the infoPreserv property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the infoPreserv property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     *
     * <pre>
     * getInfoPreserv().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     *
     *
     */
    public List<String> getInfoPreserv() {
        if (infoPreserv == null) {
            infoPreserv = new ArrayList<>();
        }
        return this.infoPreserv;
    }

    /**
     * Gets the value of the acces property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the acces property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     *
     * <pre>
     * getAcces().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     *
     *
     */
    public List<String> getAcces() {
        if (acces == null) {
            acces = new ArrayList<>();
        }
        return this.acces;
    }

    /**
     * Gets the value of the reproduction property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the reproduction property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     *
     * <pre>
     * getReproduction().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     *
     *
     */
    public List<String> getReproduction() {
        if (reproduction == null) {
            reproduction = new ArrayList<>();
        }
        return this.reproduction;
    }

}
