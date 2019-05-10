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
 * Pr�sentation des caract�ristiques du projet d'archives.
 *
 *
 * <p>Classe Java pour CaracteristiquesType complex type.
 *
 * <p>Le fragment de sch�ma suivant indique le contenu attendu figurant dans cette classe.
 *
 * <pre>
 * &lt;complexType name="CaracteristiquesType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element ref="{http://www.cines.fr/pac/ppdi}Documents" maxOccurs="unbounded"/>
 *         &lt;element ref="{http://www.cines.fr/pac/ppdi}SipDescription" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "CaracteristiquesType", propOrder = {
    "documents",
    "sipDescription"
})
public class CaracteristiquesType {

    @XmlElement(name = "Documents", required = true)
    protected List<DocumentsType> documents;
    @XmlElement(name = "SipDescription")
    protected List<SipDescriptionType> sipDescription;

    /**
     * Gets the value of the documents property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the documents property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getDocuments().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link DocumentsType }
     *
     *
     */
    public List<DocumentsType> getDocuments() {
        if (documents == null) {
            documents = new ArrayList<>();
        }
        return this.documents;
    }

    /**
     * Gets the value of the sipDescription property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the sipDescription property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getSipDescription().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link SipDescriptionType }
     *
     *
     */
    public List<SipDescriptionType> getSipDescription() {
        if (sipDescription == null) {
            sipDescription = new ArrayList<>();
        }
        return this.sipDescription;
    }

}
