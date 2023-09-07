//
// Ce fichier a été généré par l'implémentation de référence JavaTM Architecture for XML Binding (JAXB), v2.2.11
// Voir <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a>
// Toute modification apportée à ce fichier sera perdue lors de la recompilation du schéma source.
// Généré le : 2017.01.03 à 11:16:35 AM CET
//

package fr.progilone.pgcn.domain.jaxb.aip;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlSchemaType;
import jakarta.xml.bind.annotation.XmlType;
import java.math.BigInteger;
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
 *         &lt;element ref="{http://www.cines.fr/pac/test/aip}idFichier" minOccurs="0"/&gt;
 *         &lt;element ref="{http://www.cines.fr/pac/test/aip}nomFichier"/&gt;
 *         &lt;element ref="{http://www.cines.fr/pac/test/aip}compression" minOccurs="0"/&gt;
 *         &lt;element ref="{http://www.cines.fr/pac/test/aip}encodage" minOccurs="0"/&gt;
 *         &lt;element ref="{http://www.cines.fr/pac/test/aip}formatFichier"/&gt;
 *         &lt;element ref="{http://www.cines.fr/pac/test/aip}noteFichier" minOccurs="0"/&gt;
 *         &lt;element ref="{http://www.cines.fr/pac/test/aip}structureFichier" maxOccurs="unbounded" minOccurs="0"/&gt;
 *         &lt;element ref="{http://www.cines.fr/pac/test/aip}versionFormatFichier" minOccurs="0"/&gt;
 *         &lt;element ref="{http://www.cines.fr/pac/test/aip}empreinte"/&gt;
 *         &lt;element ref="{http://www.cines.fr/pac/test/aip}empreinteOri" minOccurs="0"/&gt;
 *         &lt;element ref="{http://www.cines.fr/pac/test/aip}idDocument" minOccurs="0"/&gt;
 *         &lt;element ref="{http://www.cines.fr/pac/test/aip}migration" minOccurs="0"/&gt;
 *         &lt;element ref="{http://www.cines.fr/pac/test/aip}tailleEnOctets"/&gt;
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
         propOrder = {"idFichier",
                      "nomFichier",
                      "compression",
                      "encodage",
                      "formatFichier",
                      "noteFichier",
                      "structureFichier",
                      "versionFormatFichier",
                      "empreinte",
                      "empreinteOri",
                      "idDocument",
                      "migration",
                      "tailleEnOctets"})
public class FichMetaType {

    protected String idFichier;
    @XmlElement(required = true)
    protected String nomFichier;
    protected String compression;
    protected String encodage;
    @XmlElement(required = true)
    protected String formatFichier;
    protected StringNotNULLtext noteFichier;
    protected List<StructureFichier> structureFichier;
    protected String versionFormatFichier;
    @XmlElement(required = true)
    protected EmpreinteType empreinte;
    protected EmpreinteType empreinteOri;
    @XmlSchemaType(name = "nonNegativeInteger")
    protected BigInteger idDocument;
    protected String migration;
    @XmlElement(required = true)
    @XmlSchemaType(name = "nonNegativeInteger")
    protected BigInteger tailleEnOctets;

    /**
     * Obtient la valeur de la propriété idFichier.
     *
     * @return
     *         possible object is
     *         {@link String }
     *
     */
    public String getIdFichier() {
        return idFichier;
    }

    /**
     * Définit la valeur de la propriété idFichier.
     *
     * @param value
     *            allowed object is
     *            {@link String }
     *
     */
    public void setIdFichier(String value) {
        this.idFichier = value;
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

    /**
     * Obtient la valeur de la propriété versionFormatFichier.
     *
     * @return
     *         possible object is
     *         {@link String }
     *
     */
    public String getVersionFormatFichier() {
        return versionFormatFichier;
    }

    /**
     * Définit la valeur de la propriété versionFormatFichier.
     *
     * @param value
     *            allowed object is
     *            {@link String }
     *
     */
    public void setVersionFormatFichier(String value) {
        this.versionFormatFichier = value;
    }

    /**
     * Obtient la valeur de la propriété empreinte.
     *
     * @return
     *         possible object is
     *         {@link EmpreinteType }
     *
     */
    public EmpreinteType getEmpreinte() {
        return empreinte;
    }

    /**
     * Définit la valeur de la propriété empreinte.
     *
     * @param value
     *            allowed object is
     *            {@link EmpreinteType }
     *
     */
    public void setEmpreinte(EmpreinteType value) {
        this.empreinte = value;
    }

    /**
     * Obtient la valeur de la propriété empreinteOri.
     *
     * @return
     *         possible object is
     *         {@link EmpreinteType }
     *
     */
    public EmpreinteType getEmpreinteOri() {
        return empreinteOri;
    }

    /**
     * Définit la valeur de la propriété empreinteOri.
     *
     * @param value
     *            allowed object is
     *            {@link EmpreinteType }
     *
     */
    public void setEmpreinteOri(EmpreinteType value) {
        this.empreinteOri = value;
    }

    /**
     * Obtient la valeur de la propriété idDocument.
     *
     * @return
     *         possible object is
     *         {@link BigInteger }
     *
     */
    public BigInteger getIdDocument() {
        return idDocument;
    }

    /**
     * Définit la valeur de la propriété idDocument.
     *
     * @param value
     *            allowed object is
     *            {@link BigInteger }
     *
     */
    public void setIdDocument(BigInteger value) {
        this.idDocument = value;
    }

    /**
     * Obtient la valeur de la propriété migration.
     *
     * @return
     *         possible object is
     *         {@link String }
     *
     */
    public String getMigration() {
        return migration;
    }

    /**
     * Définit la valeur de la propriété migration.
     *
     * @param value
     *            allowed object is
     *            {@link String }
     *
     */
    public void setMigration(String value) {
        this.migration = value;
    }

    /**
     * Obtient la valeur de la propriété tailleEnOctets.
     *
     * @return
     *         possible object is
     *         {@link BigInteger }
     *
     */
    public BigInteger getTailleEnOctets() {
        return tailleEnOctets;
    }

    /**
     * Définit la valeur de la propriété tailleEnOctets.
     *
     * @param value
     *            allowed object is
     *            {@link BigInteger }
     *
     */
    public void setTailleEnOctets(BigInteger value) {
        this.tailleEnOctets = value;
    }

}
