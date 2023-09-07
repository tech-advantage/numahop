//
// Ce fichier a été généré par l'implémentation de référence JavaTM Architecture for XML Binding (JAXB), v2.2.8-b130911.1802
// Voir <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a>
// Toute modification apportée à ce fichier sera perdue lors de la recompilation du schéma source.
// Généré le : 2018.08.13 à 01:08:19 PM CEST
//

package fr.progilone.pgcn.domain.jaxb.oaipmh;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAnyElement;
import jakarta.xml.bind.annotation.XmlType;

/**
 * Data "about" the record must be expressed in XML
 * that is compliant with an XML Schema defined by a community.
 *
 * <p>
 * Classe Java pour aboutType complex type.
 *
 * <p>
 * Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 *
 * <pre>
 * &lt;complexType name="aboutType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;any namespace='##other'/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "aboutType", propOrder = {"any"})
public class AboutType {

    @XmlAnyElement(lax = true)
    protected Object any;

    /**
     * Obtient la valeur de la propriété any.
     *
     * @return
     *         possible object is
     *         {@link Object }
     *
     */
    public Object getAny() {
        return any;
    }

    /**
     * Définit la valeur de la propriété any.
     *
     * @param value
     *            allowed object is
     *            {@link Object }
     *
     */
    public void setAny(Object value) {
        this.any = value;
    }

}
