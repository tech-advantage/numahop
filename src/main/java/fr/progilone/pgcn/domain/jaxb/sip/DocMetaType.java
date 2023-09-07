//
// Ce fichier a été généré par l'implémentation de référence JavaTM Architecture for XML Binding (JAXB), v2.2.11
// Voir <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a>
// Toute modification apportée à ce fichier sera perdue lors de la recompilation du schéma source.
// Généré le : 2016.12.29 à 11:53:40 AM CET
//

package fr.progilone.pgcn.domain.jaxb.sip;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;
import java.util.ArrayList;
import java.util.List;

/**
 * Métadonnées de gestion du document
 *
 * <p>
 * Classe Java pour DocMetaType complex type.
 *
 * <p>
 * Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 *
 * <pre>
 * &lt;complexType name="DocMetaType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element ref="{http://www.cines.fr/pac/sip}identifiantDocProducteur"/&gt;
 *         &lt;element ref="{http://www.cines.fr/pac/sip}docRelation" maxOccurs="unbounded" minOccurs="0"/&gt;
 *         &lt;element ref="{http://www.cines.fr/pac/sip}evaluation" minOccurs="0"/&gt;
 *         &lt;element ref="{http://www.cines.fr/pac/sip}communicabilite" minOccurs="0"/&gt;
 *         &lt;element ref="{http://www.cines.fr/pac/sip}noteDocument" minOccurs="0"/&gt;
 *         &lt;element ref="{http://www.cines.fr/pac/sip}serviceVersant"/&gt;
 *         &lt;element ref="{http://www.cines.fr/pac/sip}planClassement" minOccurs="0"/&gt;
 *         &lt;element ref="{http://www.cines.fr/pac/sip}structureDocument" maxOccurs="unbounded" minOccurs="0"/&gt;
 *         &lt;element ref="{http://www.cines.fr/pac/sip}version" minOccurs="0"/&gt;
 *         &lt;element ref="{http://www.cines.fr/pac/sip}versionPrecedente" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "DocMetaType",
         propOrder = {"identifiantDocProducteur",
                      "docRelation",
                      "evaluation",
                      "communicabilite",
                      "noteDocument",
                      "serviceVersant",
                      "planClassement",
                      "structureDocument",
                      "version",
                      "versionPrecedente"})
public class DocMetaType {

    @XmlElement(required = true)
    protected String identifiantDocProducteur;
    protected List<DocRelationType> docRelation;
    protected EvaluationType evaluation;
    protected CommunicabiliteType communicabilite;
    protected StringNotNULLtext noteDocument;
    @XmlElement(required = true)
    protected String serviceVersant;
    protected StringNotNULLtext planClassement;
    protected List<String> structureDocument;
    protected String version;
    protected String versionPrecedente;

    /**
     * Obtient la valeur de la propriété identifiantDocProducteur.
     *
     * @return
     *         possible object is
     *         {@link String }
     *
     */
    public String getIdentifiantDocProducteur() {
        return identifiantDocProducteur;
    }

    /**
     * Définit la valeur de la propriété identifiantDocProducteur.
     *
     * @param value
     *            allowed object is
     *            {@link String }
     *
     */
    public void setIdentifiantDocProducteur(String value) {
        this.identifiantDocProducteur = value;
    }

    /**
     * Gets the value of the docRelation property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the docRelation property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     *
     * <pre>
     * getDocRelation().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link DocRelationType }
     *
     *
     */
    public List<DocRelationType> getDocRelation() {
        if (docRelation == null) {
            docRelation = new ArrayList<>();
        }
        return this.docRelation;
    }

    /**
     * Obtient la valeur de la propriété evaluation.
     *
     * @return
     *         possible object is
     *         {@link EvaluationType }
     *
     */
    public EvaluationType getEvaluation() {
        return evaluation;
    }

    /**
     * Définit la valeur de la propriété evaluation.
     *
     * @param value
     *            allowed object is
     *            {@link EvaluationType }
     *
     */
    public void setEvaluation(EvaluationType value) {
        this.evaluation = value;
    }

    /**
     * Obtient la valeur de la propriété communicabilite.
     *
     * @return
     *         possible object is
     *         {@link CommunicabiliteType }
     *
     */
    public CommunicabiliteType getCommunicabilite() {
        return communicabilite;
    }

    /**
     * Définit la valeur de la propriété communicabilite.
     *
     * @param value
     *            allowed object is
     *            {@link CommunicabiliteType }
     *
     */
    public void setCommunicabilite(CommunicabiliteType value) {
        this.communicabilite = value;
    }

    /**
     * Obtient la valeur de la propriété noteDocument.
     *
     * @return
     *         possible object is
     *         {@link StringNotNULLtext }
     *
     */
    public StringNotNULLtext getNoteDocument() {
        return noteDocument;
    }

    /**
     * Définit la valeur de la propriété noteDocument.
     *
     * @param value
     *            allowed object is
     *            {@link StringNotNULLtext }
     *
     */
    public void setNoteDocument(StringNotNULLtext value) {
        this.noteDocument = value;
    }

    /**
     * Obtient la valeur de la propriété serviceVersant.
     *
     * @return
     *         possible object is
     *         {@link String }
     *
     */
    public String getServiceVersant() {
        return serviceVersant;
    }

    /**
     * Définit la valeur de la propriété serviceVersant.
     *
     * @param value
     *            allowed object is
     *            {@link String }
     *
     */
    public void setServiceVersant(String value) {
        this.serviceVersant = value;
    }

    /**
     * Obtient la valeur de la propriété planClassement.
     *
     * @return
     *         possible object is
     *         {@link StringNotNULLtext }
     *
     */
    public StringNotNULLtext getPlanClassement() {
        return planClassement;
    }

    /**
     * Définit la valeur de la propriété planClassement.
     *
     * @param value
     *            allowed object is
     *            {@link StringNotNULLtext }
     *
     */
    public void setPlanClassement(StringNotNULLtext value) {
        this.planClassement = value;
    }

    /**
     * Gets the value of the structureDocument property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the structureDocument property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     *
     * <pre>
     * getStructureDocument().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     *
     *
     */
    public List<String> getStructureDocument() {
        if (structureDocument == null) {
            structureDocument = new ArrayList<>();
        }
        return this.structureDocument;
    }

    /**
     * Obtient la valeur de la propriété version.
     *
     * @return
     *         possible object is
     *         {@link String }
     *
     */
    public String getVersion() {
        return version;
    }

    /**
     * Définit la valeur de la propriété version.
     *
     * @param value
     *            allowed object is
     *            {@link String }
     *
     */
    public void setVersion(String value) {
        this.version = value;
    }

    /**
     * Obtient la valeur de la propriété versionPrecedente.
     *
     * @return
     *         possible object is
     *         {@link String }
     *
     */
    public String getVersionPrecedente() {
        return versionPrecedente;
    }

    /**
     * Définit la valeur de la propriété versionPrecedente.
     *
     * @param value
     *            allowed object is
     *            {@link String }
     *
     */
    public void setVersionPrecedente(String value) {
        this.versionPrecedente = value;
    }

}
