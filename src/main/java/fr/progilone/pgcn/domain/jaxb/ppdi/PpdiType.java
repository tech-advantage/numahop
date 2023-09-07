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

/**
 * <p>
 * Classe Java pour ppdiType complex type.
 *
 * <p>
 * Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 *
 * <pre>
 * &lt;complexType name="ppdiType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element ref="{http://www.cines.fr/pac/ppdi}Titre"/>
 *         &lt;element ref="{http://www.cines.fr/pac/ppdi}Contexte"/>
 *         &lt;element ref="{http://www.cines.fr/pac/ppdi}Caracteristiques"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ppdiType",
         propOrder = {"titre",
                      "contexte",
                      "caracteristiques"})
public class PpdiType {

    @XmlElement(name = "Titre", required = true)
    protected String titre;
    @XmlElement(name = "Contexte", required = true)
    protected ContexteType contexte;
    @XmlElement(name = "Caracteristiques", required = true)
    protected CaracteristiquesType caracteristiques;

    /**
     * Obtient la valeur de la propriété titre.
     *
     * @return
     *         possible object is
     *         {@link String }
     *
     */
    public String getTitre() {
        return titre;
    }

    /**
     * Définit la valeur de la propriété titre.
     *
     * @param value
     *            allowed object is
     *            {@link String }
     *
     */
    public void setTitre(String value) {
        this.titre = value;
    }

    /**
     * Obtient la valeur de la propriété contexte.
     *
     * @return
     *         possible object is
     *         {@link ContexteType }
     *
     */
    public ContexteType getContexte() {
        return contexte;
    }

    /**
     * Définit la valeur de la propriété contexte.
     *
     * @param value
     *            allowed object is
     *            {@link ContexteType }
     *
     */
    public void setContexte(ContexteType value) {
        this.contexte = value;
    }

    /**
     * Obtient la valeur de la propriété caracteristiques.
     *
     * @return
     *         possible object is
     *         {@link CaracteristiquesType }
     *
     */
    public CaracteristiquesType getCaracteristiques() {
        return caracteristiques;
    }

    /**
     * Définit la valeur de la propriété caracteristiques.
     *
     * @param value
     *            allowed object is
     *            {@link CaracteristiquesType }
     *
     */
    public void setCaracteristiques(CaracteristiquesType value) {
        this.caracteristiques = value;
    }

}
