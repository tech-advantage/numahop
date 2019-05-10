//
// Ce fichier a été généré par l'implémentation de référence JavaTM Architecture for XML Binding (JAXB), v2.2.11
// Voir <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a>
// Toute modification apportée à ce fichier sera perdue lors de la recompilation du schéma source.
// Généré le : 2017.01.03 à 11:16:18 AM CET
//


package fr.progilone.pgcn.domain.jaxb.avis;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Classe Java pour pac_avisType complex type.
 *
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 *
 * <pre>
 * &lt;complexType name="pac_avisType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element ref="{http://www.cines.fr/pac/test/avis}serveur"/&gt;
 *         &lt;element ref="{http://www.cines.fr/pac/test/avis}id" minOccurs="0"/&gt;
 *         &lt;element ref="{http://www.cines.fr/pac/test/avis}id_versement" minOccurs="0"/&gt;
 *         &lt;element ref="{http://www.cines.fr/pac/test/avis}id_demande" minOccurs="0"/&gt;
 *         &lt;element ref="{http://www.cines.fr/pac/test/avis}title" maxOccurs="unbounded" minOccurs="0"/&gt;
 *         &lt;element ref="{http://www.cines.fr/pac/test/avis}dateArchivage" minOccurs="0"/&gt;
 *         &lt;element ref="{http://www.cines.fr/pac/test/avis}dateDemande" minOccurs="0"/&gt;
 *         &lt;element ref="{http://www.cines.fr/pac/test/avis}dateCommunication" minOccurs="0"/&gt;
 *         &lt;element ref="{http://www.cines.fr/pac/test/avis}identifiantDocPac" minOccurs="0"/&gt;
 *         &lt;element ref="{http://www.cines.fr/pac/test/avis}identifiantDocProducteur" minOccurs="0"/&gt;
 *         &lt;element ref="{http://www.cines.fr/pac/test/avis}codeErreur" minOccurs="0"/&gt;
 *         &lt;element ref="{http://www.cines.fr/pac/test/avis}commentaire"/&gt;
 *         &lt;element ref="{http://www.cines.fr/pac/test/avis}erreurValidation" minOccurs="0"/&gt;
 *         &lt;element ref="{http://www.cines.fr/pac/test/avis}contrles" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "pac_avisType", propOrder = {
    "serveur",
    "id",
    "idVersement",
    "idDemande",
    "title",
    "dateArchivage",
    "dateDemande",
    "dateCommunication",
    "identifiantDocPac",
    "identifiantDocProducteur",
    "codeErreur",
    "commentaire",
    "erreurValidation",
    "contrles"
})
public class PacAvisType {

    @XmlElement(required = true)
    protected String serveur;
    protected String id;
    @XmlElement(name = "id_versement")
    protected String idVersement;
    @XmlElement(name = "id_demande")
    protected String idDemande;
    protected List<String> title;
    @XmlSchemaType(name = "dateTime")
    protected LocalDateTime dateArchivage;
    @XmlSchemaType(name = "dateTime")
    protected LocalDateTime dateDemande;
    @XmlSchemaType(name = "dateTime")
    protected LocalDateTime dateCommunication;
    protected String identifiantDocPac;
    protected String identifiantDocProducteur;
    @XmlSchemaType(name = "string")
    protected CodeErreurType codeErreur;
    @XmlElement(required = true)
    protected String commentaire;
    protected String erreurValidation;
    protected String contrles;

    /**
     * Obtient la valeur de la propriété serveur.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getServeur() {
        return serveur;
    }

    /**
     * Définit la valeur de la propriété serveur.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setServeur(String value) {
        this.serveur = value;
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
     * Obtient la valeur de la propriété idVersement.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getIdVersement() {
        return idVersement;
    }

    /**
     * Définit la valeur de la propriété idVersement.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setIdVersement(String value) {
        this.idVersement = value;
    }

    /**
     * Obtient la valeur de la propriété idDemande.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getIdDemande() {
        return idDemande;
    }

    /**
     * Définit la valeur de la propriété idDemande.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setIdDemande(String value) {
        this.idDemande = value;
    }

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
     * <pre>
     *    getTitle().add(newItem);
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
     * Obtient la valeur de la propriété dateArchivage.
     *
     * @return
     *     possible object is
     *     {@link LocalDateTime }
     *
     */
    public LocalDateTime getDateArchivage() {
        return dateArchivage;
    }

    /**
     * Définit la valeur de la propriété dateArchivage.
     *
     * @param value
     *     allowed object is
     *     {@link LocalDateTime }
     *
     */
    public void setDateArchivage(LocalDateTime value) {
        this.dateArchivage = value;
    }

    /**
     * Obtient la valeur de la propriété dateDemande.
     *
     * @return
     *     possible object is
     *     {@link LocalDateTime }
     *
     */
    public LocalDateTime getDateDemande() {
        return dateDemande;
    }

    /**
     * Définit la valeur de la propriété dateDemande.
     *
     * @param value
     *     allowed object is
     *     {@link LocalDateTime }
     *
     */
    public void setDateDemande(LocalDateTime value) {
        this.dateDemande = value;
    }

    /**
     * Obtient la valeur de la propriété dateCommunication.
     *
     * @return
     *     possible object is
     *     {@link LocalDateTime }
     *
     */
    public LocalDateTime getDateCommunication() {
        return dateCommunication;
    }

    /**
     * Définit la valeur de la propriété dateCommunication.
     *
     * @param value
     *     allowed object is
     *     {@link LocalDateTime }
     *
     */
    public void setDateCommunication(LocalDateTime value) {
        this.dateCommunication = value;
    }

    /**
     * Obtient la valeur de la propriété identifiantDocPac.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getIdentifiantDocPac() {
        return identifiantDocPac;
    }

    /**
     * Définit la valeur de la propriété identifiantDocPac.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setIdentifiantDocPac(String value) {
        this.identifiantDocPac = value;
    }

    /**
     * Obtient la valeur de la propriété identifiantDocProducteur.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getIdentifiantDocProducteur() {
        return identifiantDocProducteur;
    }

    /**
     * Définit la valeur de la propriété identifiantDocProducteur.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setIdentifiantDocProducteur(String value) {
        this.identifiantDocProducteur = value;
    }

    /**
     * Obtient la valeur de la propriété codeErreur.
     *
     * @return
     *     possible object is
     *     {@link CodeErreurType }
     *
     */
    public CodeErreurType getCodeErreur() {
        return codeErreur;
    }

    /**
     * Définit la valeur de la propriété codeErreur.
     *
     * @param value
     *     allowed object is
     *     {@link CodeErreurType }
     *
     */
    public void setCodeErreur(CodeErreurType value) {
        this.codeErreur = value;
    }

    /**
     * Obtient la valeur de la propriété commentaire.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getCommentaire() {
        return commentaire;
    }

    /**
     * Définit la valeur de la propriété commentaire.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setCommentaire(String value) {
        this.commentaire = value;
    }

    /**
     * Obtient la valeur de la propriété erreurValidation.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getErreurValidation() {
        return erreurValidation;
    }

    /**
     * Définit la valeur de la propriété erreurValidation.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setErreurValidation(String value) {
        this.erreurValidation = value;
    }

    /**
     * Obtient la valeur de la propriété contrles.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getContrles() {
        return contrles;
    }

    /**
     * Définit la valeur de la propriété contrles.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setContrles(String value) {
        this.contrles = value;
    }

}
