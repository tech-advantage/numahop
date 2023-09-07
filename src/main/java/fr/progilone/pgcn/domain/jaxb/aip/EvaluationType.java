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
import java.time.LocalDateTime;
import javax.xml.datatype.Duration;

/**
 *
 *
 * <pre>
 * &lt;?xml version="1.0" encoding="UTF-8"?&gt;&lt;description xmlns="http://www.cines.fr/pac/test/aip" xmlns:ISO-639-3="urn:un:unece:uncefact:codelist:draft:DAF:languageCode:2011-10-07" xmlns:RA="urn:un:unece:uncefact:codelist:draft:DAF:accessRestrictionCode:2009-08-18" xmlns:xsd="http://www.w3.org/2001/XMLSchema"&gt;pour les archives intermédiaires, permet d'indiquer la durée d'utilité administrative du document et le traitement à appliquer à l'issue de cette durée&lt;/description&gt;
 * </pre>
 *
 *
 *
 * <p>
 * Classe Java pour evaluationType complex type.
 *
 * <p>
 * Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 *
 * <pre>
 * &lt;complexType name="evaluationType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element ref="{http://www.cines.fr/pac/test/aip}DUA"/&gt;
 *         &lt;element ref="{http://www.cines.fr/pac/test/aip}traitement"/&gt;
 *         &lt;element ref="{http://www.cines.fr/pac/test/aip}dateDebut"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "evaluationType",
         propOrder = {"dua",
                      "traitement",
                      "dateDebut"})
public class EvaluationType {

    @XmlElement(name = "DUA", required = true)
    protected Duration dua;
    @XmlElement(required = true)
    protected TraitementType traitement;
    @XmlElement(required = true)
    @XmlSchemaType(name = "date")
    protected LocalDateTime dateDebut;

    /**
     * Obtient la valeur de la propriété dua.
     *
     * @return
     *         possible object is
     *         {@link Duration }
     *
     */
    public Duration getDUA() {
        return dua;
    }

    /**
     * Définit la valeur de la propriété dua.
     *
     * @param value
     *            allowed object is
     *            {@link Duration }
     *
     */
    public void setDUA(Duration value) {
        this.dua = value;
    }

    /**
     * Obtient la valeur de la propriété traitement.
     *
     * @return
     *         possible object is
     *         {@link TraitementType }
     *
     */
    public TraitementType getTraitement() {
        return traitement;
    }

    /**
     * Définit la valeur de la propriété traitement.
     *
     * @param value
     *            allowed object is
     *            {@link TraitementType }
     *
     */
    public void setTraitement(TraitementType value) {
        this.traitement = value;
    }

    /**
     * Obtient la valeur de la propriété dateDebut.
     *
     * @return
     *         possible object is
     *         {@link LocalDateTime }
     *
     */
    public LocalDateTime getDateDebut() {
        return dateDebut;
    }

    /**
     * Définit la valeur de la propriété dateDebut.
     *
     * @param value
     *            allowed object is
     *            {@link LocalDateTime }
     *
     */
    public void setDateDebut(LocalDateTime value) {
        this.dateDebut = value;
    }

}
