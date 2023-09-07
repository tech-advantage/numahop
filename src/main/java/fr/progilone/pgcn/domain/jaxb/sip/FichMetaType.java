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
 * Métadonnées des fichiers composant le document
 *
 * <p>
 * Classe Java pour FichMetaType complex type.
 *
 * <p>
 * Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 *
 * <pre>
 * &lt;complexType name="FichMetaType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element ref="{http://www.cines.fr/pac/sip}compression" minOccurs="0"/&gt;
 *         &lt;element ref="{http://www.cines.fr/pac/sip}encodage" minOccurs="0"/&gt;
 *         &lt;element ref="{http://www.cines.fr/pac/sip}formatFichier"/&gt;
 *         &lt;element ref="{http://www.cines.fr/pac/sip}nomFichier"/&gt;
 *         &lt;element ref="{http://www.cines.fr/pac/sip}empreinteOri"/&gt;
 *         &lt;element ref="{http://www.cines.fr/pac/sip}noteFichier" minOccurs="0"/&gt;
 *         &lt;element ref="{http://www.cines.fr/pac/sip}structureFichier" maxOccurs="unbounded" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "FichMetaType",
         propOrder = {"compression",
                      "encodage",
                      "formatFichier",
                      "nomFichier",
                      "empreinteOri",
                      "noteFichier",
                      "structureFichier"})
public class FichMetaType {

    protected String compression;
    protected String encodage;
    @XmlElement(required = true)
    protected String formatFichier;
    @XmlElement(required = true)
    protected String nomFichier;
    @XmlElement(required = true)
    protected EmpreinteOri empreinteOri;
    protected StringNotNULLtext noteFichier;
    protected List<StructureFichier> structureFichier;

    /**
     * Obtient la valeur de la propriété compression.
     *
     * @return
     *         possible object is
     *         {@link String }
     *
     */
    public String getCompression() {
        return compression;
    }

    /**
     * Définit la valeur de la propriété compression.
     *
     * @param value
     *            allowed object is
     *            {@link String }
     *
     */
    public void setCompression(String value) {
        this.compression = value;
    }

    /**
     * Obtient la valeur de la propriété encodage.
     *
     * @return
     *         possible object is
     *         {@link String }
     *
     */
    public String getEncodage() {
        return encodage;
    }

    /**
     * Définit la valeur de la propriété encodage.
     *
     * @param value
     *            allowed object is
     *            {@link String }
     *
     */
    public void setEncodage(String value) {
        this.encodage = value;
    }

    /**
     * Obtient la valeur de la propriété formatFichier.
     *
     * @return
     *         possible object is
     *         {@link String }
     *
     */
    public String getFormatFichier() {
        return formatFichier;
    }

    /**
     * Définit la valeur de la propriété formatFichier.
     *
     * @param value
     *            allowed object is
     *            {@link String }
     *
     */
    public void setFormatFichier(String value) {
        this.formatFichier = value;
    }

    /**
     * Obtient la valeur de la propriété nomFichier.
     *
     * @return
     *         possible object is
     *         {@link String }
     *
     */
    public String getNomFichier() {
        return nomFichier;
    }

    /**
     * Définit la valeur de la propriété nomFichier.
     *
     * @param value
     *            allowed object is
     *            {@link String }
     *
     */
    public void setNomFichier(String value) {
        this.nomFichier = value;
    }

    /**
     * Obtient la valeur de la propriété empreinteOri.
     *
     * @return
     *         possible object is
     *         {@link EmpreinteOri }
     *
     */
    public EmpreinteOri getEmpreinteOri() {
        return empreinteOri;
    }

    /**
     * Définit la valeur de la propriété empreinteOri.
     *
     * @param value
     *            allowed object is
     *            {@link EmpreinteOri }
     *
     */
    public void setEmpreinteOri(EmpreinteOri value) {
        this.empreinteOri = value;
    }

    /**
     * Obtient la valeur de la propriété noteFichier.
     *
     * @return
     *         possible object is
     *         {@link StringNotNULLtext }
     *
     */
    public StringNotNULLtext getNoteFichier() {
        return noteFichier;
    }

    /**
     * Définit la valeur de la propriété noteFichier.
     *
     * @param value
     *            allowed object is
     *            {@link StringNotNULLtext }
     *
     */
    public void setNoteFichier(StringNotNULLtext value) {
        this.noteFichier = value;
    }

    /**
     * Gets the value of the structureFichier property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the structureFichier property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     *
     * <pre>
     * getStructureFichier().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link StructureFichier }
     *
     *
     */
    public List<StructureFichier> getStructureFichier() {
        if (structureFichier == null) {
            structureFichier = new ArrayList<>();
        }
        return this.structureFichier;
    }

}
