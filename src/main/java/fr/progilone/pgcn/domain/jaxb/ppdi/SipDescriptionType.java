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
 *
 * Description de la maniére dont le service versant remplit les champs du fichier sip.xml. Les définitions de ces champs sont données dans le schéma
 * sip.xsd. Cet élément ne doit pas étre utilisé dans le cadre d'un archivage au format SEDA.
 *
 *
 * <p>
 * Classe Java pour SipDescriptionType complex type.
 *
 * <p>
 * Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 *
 * <pre>
 * &lt;complexType name="SipDescriptionType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element ref="{http://www.cines.fr/pac/ppdi}DocDCDescription"/>
 *         &lt;element ref="{http://www.cines.fr/pac/ppdi}DocMetaDescription"/>
 *         &lt;element ref="{http://www.cines.fr/pac/ppdi}FichMetaDescription"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "SipDescriptionType",
         propOrder = {"docDCDescription",
                      "docMetaDescription",
                      "fichMetaDescription"})
public class SipDescriptionType {

    @XmlElement(name = "DocDCDescription", required = true)
    protected DocDCDescriptionType docDCDescription;
    @XmlElement(name = "DocMetaDescription", required = true)
    protected DocMetaDescriptionType docMetaDescription;
    @XmlElement(name = "FichMetaDescription", required = true)
    protected FichMetaDescriptionType fichMetaDescription;

    /**
     * Obtient la valeur de la propriété docDCDescription.
     *
     * @return
     *         possible object is
     *         {@link DocDCDescriptionType }
     *
     */
    public DocDCDescriptionType getDocDCDescription() {
        return docDCDescription;
    }

    /**
     * Définit la valeur de la propriété docDCDescription.
     *
     * @param value
     *            allowed object is
     *            {@link DocDCDescriptionType }
     *
     */
    public void setDocDCDescription(DocDCDescriptionType value) {
        this.docDCDescription = value;
    }

    /**
     * Obtient la valeur de la propriété docMetaDescription.
     *
     * @return
     *         possible object is
     *         {@link DocMetaDescriptionType }
     *
     */
    public DocMetaDescriptionType getDocMetaDescription() {
        return docMetaDescription;
    }

    /**
     * Définit la valeur de la propriété docMetaDescription.
     *
     * @param value
     *            allowed object is
     *            {@link DocMetaDescriptionType }
     *
     */
    public void setDocMetaDescription(DocMetaDescriptionType value) {
        this.docMetaDescription = value;
    }

    /**
     * Obtient la valeur de la propriété fichMetaDescription.
     *
     * @return
     *         possible object is
     *         {@link FichMetaDescriptionType }
     *
     */
    public FichMetaDescriptionType getFichMetaDescription() {
        return fichMetaDescription;
    }

    /**
     * Définit la valeur de la propriété fichMetaDescription.
     *
     * @param value
     *            allowed object is
     *            {@link FichMetaDescriptionType }
     *
     */
    public void setFichMetaDescription(FichMetaDescriptionType value) {
        this.fichMetaDescription = value;
    }

}
