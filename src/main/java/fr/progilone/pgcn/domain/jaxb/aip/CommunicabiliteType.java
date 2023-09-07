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

/**
 *
 *
 * <pre>
 * &lt;?xml version="1.0" encoding="UTF-8"?&gt;&lt;description xmlns="http://www.cines.fr/pac/test/aip" xmlns:ISO-639-3="urn:un:unece:uncefact:codelist:draft:DAF:languageCode:2011-10-07" xmlns:RA="urn:un:unece:uncefact:codelist:draft:DAF:accessRestrictionCode:2009-08-18" xmlns:xsd="http://www.w3.org/2001/XMLSchema"&gt;pour les archives publiques, précise si le document est librement communicable ou soumis à un délai (cf. régime de communication prévu par le Code du Patrimoine, art. L213-1 à L213-8)&lt;/description&gt;
 * </pre>
 *
 *
 *
 * <p>
 * Classe Java pour communicabiliteType complex type.
 *
 * <p>
 * Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 *
 * <pre>
 * &lt;complexType name="communicabiliteType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element ref="{http://www.cines.fr/pac/test/aip}code"/&gt;
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
@XmlType(name = "communicabiliteType",
         propOrder = {"code",
                      "dateDebut"})
public class CommunicabiliteType {

    @XmlElement(required = true)
    protected CodeType code;
    @XmlElement(required = true)
    @XmlSchemaType(name = "date")
    protected LocalDateTime dateDebut;

    /**
     * Obtient la valeur de la propriété code.
     *
     * @return
     *         possible object is
     *         {@link CodeType }
     *
     */
    public CodeType getCode() {
        return code;
    }

    /**
     * Définit la valeur de la propriété code.
     *
     * @param value
     *            allowed object is
     *            {@link CodeType }
     *
     */
    public void setCode(CodeType value) {
        this.code = value;
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
