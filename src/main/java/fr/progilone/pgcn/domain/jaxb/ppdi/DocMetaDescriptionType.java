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
import javax.xml.datatype.Duration;

/**
 * <p>
 * Classe Java pour DocMetaDescriptionType complex type.
 *
 * <p>
 * Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 *
 * <pre>
 * &lt;complexType name="DocMetaDescriptionType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="dureeConservation" type="{http://www.w3.org/2001/XMLSchema}duration" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="identifiantDocProducteur" type="{http://www.cines.fr/pac/ppdi}stringNotNULL" maxOccurs="unbounded"/>
 *         &lt;element ref="{http://www.cines.fr/pac/ppdi}docRelation" maxOccurs="unbounded"/>
 *         &lt;element ref="{http://www.cines.fr/pac/ppdi}evaluation" maxOccurs="unbounded"/>
 *         &lt;element ref="{http://www.cines.fr/pac/ppdi}communicabilite" maxOccurs="unbounded"/>
 *         &lt;element name="noteDocument" type="{http://www.cines.fr/pac/ppdi}stringNotNULL" maxOccurs="unbounded"/>
 *         &lt;element name="serviceVersant" type="{http://www.cines.fr/pac/ppdi}stringNotNULL" maxOccurs="unbounded"/>
 *         &lt;element name="planClassement" type="{http://www.cines.fr/pac/ppdi}stringNotNULL" maxOccurs="unbounded"/>
 *         &lt;element name="structureDocument" type="{http://www.cines.fr/pac/ppdi}stringNotNULL" maxOccurs="unbounded"/>
 *         &lt;element name="version" type="{http://www.cines.fr/pac/ppdi}stringNotNULL" maxOccurs="unbounded"/>
 *         &lt;element name="versionPrecedente" type="{http://www.cines.fr/pac/ppdi}stringNotNULL" maxOccurs="unbounded"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "DocMetaDescriptionType",
         propOrder = {"dureeConservation",
                      "identifiantDocProducteur",
                      "docRelation",
                      "evaluation",
                      "communicabilite",
                      "noteDocument",
                      "serviceVersant",
                      "planClassement",
                      "structureDocument",
                      "version",
                      "versionPrecedente"})
public class DocMetaDescriptionType {

    protected List<Duration> dureeConservation;
    @XmlElement(required = true)
    protected List<String> identifiantDocProducteur;
    @XmlElement(required = true)
    protected List<DocRelationType> docRelation;
    @XmlElement(required = true)
    protected List<EvaluationType> evaluation;
    @XmlElement(required = true)
    protected List<CommunicabiliteType> communicabilite;
    @XmlElement(required = true)
    protected List<String> noteDocument;
    @XmlElement(required = true)
    protected List<String> serviceVersant;
    @XmlElement(required = true)
    protected List<String> planClassement;
    @XmlElement(required = true)
    protected List<String> structureDocument;
    @XmlElement(required = true)
    protected List<String> version;
    @XmlElement(required = true)
    protected List<String> versionPrecedente;

    /**
     * Gets the value of the dureeConservation property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the dureeConservation property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     *
     * <pre>
     * getDureeConservation().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Duration }
     *
     *
     */
    public List<Duration> getDureeConservation() {
        if (dureeConservation == null) {
            dureeConservation = new ArrayList<>();
        }
        return this.dureeConservation;
    }

    /**
     * Gets the value of the identifiantDocProducteur property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the identifiantDocProducteur property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     *
     * <pre>
     * getIdentifiantDocProducteur().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     *
     *
     */
    public List<String> getIdentifiantDocProducteur() {
        if (identifiantDocProducteur == null) {
            identifiantDocProducteur = new ArrayList<>();
        }
        return this.identifiantDocProducteur;
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
     * Gets the value of the evaluation property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the evaluation property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     *
     * <pre>
     * getEvaluation().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link EvaluationType }
     *
     *
     */
    public List<EvaluationType> getEvaluation() {
        if (evaluation == null) {
            evaluation = new ArrayList<>();
        }
        return this.evaluation;
    }

    /**
     * Gets the value of the communicabilite property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the communicabilite property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     *
     * <pre>
     * getCommunicabilite().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link CommunicabiliteType }
     *
     *
     */
    public List<CommunicabiliteType> getCommunicabilite() {
        if (communicabilite == null) {
            communicabilite = new ArrayList<>();
        }
        return this.communicabilite;
    }

    /**
     * Gets the value of the noteDocument property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the noteDocument property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     *
     * <pre>
     * getNoteDocument().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     *
     *
     */
    public List<String> getNoteDocument() {
        if (noteDocument == null) {
            noteDocument = new ArrayList<>();
        }
        return this.noteDocument;
    }

    /**
     * Gets the value of the serviceVersant property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the serviceVersant property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     *
     * <pre>
     * getServiceVersant().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     *
     *
     */
    public List<String> getServiceVersant() {
        if (serviceVersant == null) {
            serviceVersant = new ArrayList<>();
        }
        return this.serviceVersant;
    }

    /**
     * Gets the value of the planClassement property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the planClassement property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     *
     * <pre>
     * getPlanClassement().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     *
     *
     */
    public List<String> getPlanClassement() {
        if (planClassement == null) {
            planClassement = new ArrayList<>();
        }
        return this.planClassement;
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
     * Gets the value of the version property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the version property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     *
     * <pre>
     * getVersion().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     *
     *
     */
    public List<String> getVersion() {
        if (version == null) {
            version = new ArrayList<>();
        }
        return this.version;
    }

    /**
     * Gets the value of the versionPrecedente property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the versionPrecedente property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     *
     * <pre>
     * getVersionPrecedente().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     *
     *
     */
    public List<String> getVersionPrecedente() {
        if (versionPrecedente == null) {
            versionPrecedente = new ArrayList<>();
        }
        return this.versionPrecedente;
    }

}
