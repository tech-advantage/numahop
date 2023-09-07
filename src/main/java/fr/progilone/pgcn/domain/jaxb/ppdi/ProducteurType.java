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

/**
 *
 *
 * <pre>
 * &lt;?xml version="1.0" encoding="UTF-8"?&gt;&lt;description xmlns="http://www.cines.fr/pac/ppdi" xmlns:ISO-639-3="urn:un:unece:uncefact:codelist:draft:DAF:languageCode:2011-10-07" xmlns:RA="urn:un:unece:uncefact:codelist:draft:DAF:accessRestrictionCode:2009-08-18" xmlns:xsd="http://www.w3.org/2001/XMLSchema"&gt;Présentation du producteur du fonds&lt;/description&gt;
 * </pre>
 *
 *
 * <pre>
 * &lt;?xml version="1.0" encoding="UTF-8"?&gt;&lt;commentaires xmlns="http://www.cines.fr/pac/ppdi" xmlns:ISO-639-3="urn:un:unece:uncefact:codelist:draft:DAF:languageCode:2011-10-07" xmlns:RA="urn:un:unece:uncefact:codelist:draft:DAF:accessRestrictionCode:2009-08-18" xmlns:xsd="http://www.w3.org/2001/XMLSchema"&gt;
 * Le producteur est l'entité (organisme(s), personne ou groupe de personnes) responsable de la création du fonds. Dans le cas de documents numérisés, il faut comprendre par producteur, le producteur du fonds original et non le responsable de la numérisation. Dans tous les cas, si le producteur est le service versant, ne pas remplir cette partie. S'il existe une multitude de producteurs qui ne sont pas clairement identifiables, ne pas remplir cette partie et renseigner dans le détail, le circuit de production et de collecte.
 * &lt;/commentaires&gt;
 * </pre>
 *
 *
 *
 * <p>
 * Classe Java pour ProducteurType complex type.
 *
 * <p>
 * Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 *
 * <pre>
 * &lt;complexType name="ProducteurType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element ref="{http://www.cines.fr/pac/ppdi}nomProd"/>
 *         &lt;element ref="{http://www.cines.fr/pac/ppdi}datesProd"/>
 *         &lt;element ref="{http://www.cines.fr/pac/ppdi}historiqueProd" maxOccurs="unbounded"/>
 *         &lt;element ref="{http://www.cines.fr/pac/ppdi}notesProd" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ProducteurType",
         propOrder = {"nomProd",
                      "datesProd",
                      "historiqueProd",
                      "notesProd"})
public class ProducteurType {

    @XmlElement(required = true)
    protected String nomProd;
    @XmlElement(required = true)
    protected String datesProd;
    @XmlElement(required = true)
    protected List<String> historiqueProd;
    protected List<String> notesProd;

    /**
     * Obtient la valeur de la propriété nomProd.
     *
     * @return
     *         possible object is
     *         {@link String }
     *
     */
    public String getNomProd() {
        return nomProd;
    }

    /**
     * Définit la valeur de la propriété nomProd.
     *
     * @param value
     *            allowed object is
     *            {@link String }
     *
     */
    public void setNomProd(String value) {
        this.nomProd = value;
    }

    /**
     * Obtient la valeur de la propriété datesProd.
     *
     * @return
     *         possible object is
     *         {@link String }
     *
     */
    public String getDatesProd() {
        return datesProd;
    }

    /**
     * Définit la valeur de la propriété datesProd.
     *
     * @param value
     *            allowed object is
     *            {@link String }
     *
     */
    public void setDatesProd(String value) {
        this.datesProd = value;
    }

    /**
     * Gets the value of the historiqueProd property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the historiqueProd property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     *
     * <pre>
     * getHistoriqueProd().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     *
     *
     */
    public List<String> getHistoriqueProd() {
        if (historiqueProd == null) {
            historiqueProd = new ArrayList<>();
        }
        return this.historiqueProd;
    }

    /**
     * Gets the value of the notesProd property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the notesProd property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     *
     * <pre>
     * getNotesProd().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     *
     *
     */
    public List<String> getNotesProd() {
        if (notesProd == null) {
            notesProd = new ArrayList<>();
        }
        return this.notesProd;
    }

}
