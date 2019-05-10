//
// Ce fichier a été généré par l'implémentation de référence JavaTM Architecture for XML Binding (JAXB), v2.2.11
// Voir <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a>
// Toute modification apportée à ce fichier sera perdue lors de la recompilation du schéma source.
// Généré le : 2016.12.29 à 11:53:40 AM CET
//


package fr.progilone.pgcn.domain.jaxb.sip;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Classe Java pour pacType complex type.
 *
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 *
 * <pre>
 * &lt;complexType name="pacType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element ref="{http://www.cines.fr/pac/sip}DocDC"/&gt;
 *         &lt;element ref="{http://www.cines.fr/pac/sip}DocMeta"/&gt;
 *         &lt;element ref="{http://www.cines.fr/pac/sip}FichMeta" maxOccurs="unbounded" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "pacType", propOrder = {
    "docDC",
    "docMeta",
    "fichMeta"
})
public class PacType {

    @XmlElement(name = "DocDC", required = true)
    protected DocDCType docDC;
    @XmlElement(name = "DocMeta", required = true)
    protected DocMetaType docMeta;
    @XmlElement(name = "FichMeta")
    protected List<FichMetaType> fichMeta;

    /**
     * Obtient la valeur de la propriété docDC.
     *
     * @return
     *     possible object is
     *     {@link DocDCType }
     *
     */
    public DocDCType getDocDC() {
        return docDC;
    }

    /**
     * Définit la valeur de la propriété docDC.
     *
     * @param value
     *     allowed object is
     *     {@link DocDCType }
     *
     */
    public void setDocDC(DocDCType value) {
        this.docDC = value;
    }

    /**
     * Obtient la valeur de la propriété docMeta.
     *
     * @return
     *     possible object is
     *     {@link DocMetaType }
     *
     */
    public DocMetaType getDocMeta() {
        return docMeta;
    }

    /**
     * Définit la valeur de la propriété docMeta.
     *
     * @param value
     *     allowed object is
     *     {@link DocMetaType }
     *
     */
    public void setDocMeta(DocMetaType value) {
        this.docMeta = value;
    }

    /**
     * Gets the value of the fichMeta property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the fichMeta property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getFichMeta().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link FichMetaType }
     *
     *
     */
    public List<FichMetaType> getFichMeta() {
        if (fichMeta == null) {
            fichMeta = new ArrayList<>();
        }
        return this.fichMeta;
    }

}
