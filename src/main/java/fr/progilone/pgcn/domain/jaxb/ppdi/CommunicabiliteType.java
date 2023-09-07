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
 * Classe Java pour communicabiliteType complex type.
 *
 * <p>
 * Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 *
 * <pre>
 * &lt;complexType name="communicabiliteType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="code" type="{http://www.w3.org/2001/XMLSchema}anyType"/>
 *         &lt;element name="dateDebut" type="{http://www.cines.fr/pac/ppdi}stringNotNULL"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
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
    protected Object code;
    @XmlElement(required = true)
    protected String dateDebut;

    /**
     * Obtient la valeur de la propriété code.
     *
     * @return
     *         possible object is
     *         {@link Object }
     *
     */
    public Object getCode() {
        return code;
    }

    /**
     * Définit la valeur de la propriété code.
     *
     * @param value
     *            allowed object is
     *            {@link Object }
     *
     */
    public void setCode(Object value) {
        this.code = value;
    }

    /**
     * Obtient la valeur de la propriété dateDebut.
     *
     * @return
     *         possible object is
     *         {@link String }
     *
     */
    public String getDateDebut() {
        return dateDebut;
    }

    /**
     * Définit la valeur de la propriété dateDebut.
     *
     * @param value
     *            allowed object is
     *            {@link String }
     *
     */
    public void setDateDebut(String value) {
        this.dateDebut = value;
    }

}
