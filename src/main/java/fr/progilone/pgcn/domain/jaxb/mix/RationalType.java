//
// Ce fichier a été généré par l'implémentation de référence JavaTM Architecture for XML Binding (JAXB), v2.2.8-b130911.1802
// Voir <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a>
// Toute modification apportée à ce fichier sera perdue lors de la recompilation du schéma source.
// Généré le : 2017.08.25 à 03:15:17 PM CEST
//

package fr.progilone.pgcn.domain.jaxb.mix;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlType;
import java.math.BigInteger;

/**
 * <p>
 * Classe Java pour rationalType complex type.
 *
 * <p>
 * Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 *
 * <pre>
 * &lt;complexType name="rationalType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="numerator" type="{http://www.w3.org/2001/XMLSchema}integer" minOccurs="0"/>
 *         &lt;element name="denominator" type="{http://www.w3.org/2001/XMLSchema}integer" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="use" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "rationalType",
         propOrder = {"numerator",
                      "denominator"})
public class RationalType {

    protected BigInteger numerator;
    protected BigInteger denominator;
    @XmlAttribute(name = "use")
    protected String use;

    /**
     * Obtient la valeur de la propriété numerator.
     *
     * @return
     *         possible object is
     *         {@link BigInteger }
     *
     */
    public BigInteger getNumerator() {
        return numerator;
    }

    /**
     * Définit la valeur de la propriété numerator.
     *
     * @param value
     *            allowed object is
     *            {@link BigInteger }
     *
     */
    public void setNumerator(BigInteger value) {
        this.numerator = value;
    }

    /**
     * Obtient la valeur de la propriété denominator.
     *
     * @return
     *         possible object is
     *         {@link BigInteger }
     *
     */
    public BigInteger getDenominator() {
        return denominator;
    }

    /**
     * Définit la valeur de la propriété denominator.
     *
     * @param value
     *            allowed object is
     *            {@link BigInteger }
     *
     */
    public void setDenominator(BigInteger value) {
        this.denominator = value;
    }

    /**
     * Obtient la valeur de la propriété use.
     *
     * @return
     *         possible object is
     *         {@link String }
     *
     */
    public String getUse() {
        return use;
    }

    /**
     * Définit la valeur de la propriété use.
     *
     * @param value
     *            allowed object is
     *            {@link String }
     *
     */
    public void setUse(String value) {
        this.use = value;
    }

}
