//
// Ce fichier a été généré par l'implémentation de référence JavaTM Architecture for XML Binding (JAXB), v2.2.8-b130911.1802
// Voir <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a>
// Toute modification apportée à ce fichier sera perdue lors de la recompilation du schéma source.
// Généré le : 2017.01.02 à 04:49:01 PM CET
//


package fr.progilone.pgcn.domain.jaxb.rdf;

import fr.progilone.pgcn.domain.jaxb.dc.ElementContainer;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Classe Java pour DescriptionType complex type.
 *
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 *
 * <pre>
 * &lt;complexType name="DescriptionType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://purl.org/dc/elements/1.1/}elementContainer">
 *       &lt;attribute name="about" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "DescriptionType", namespace = "http://www.w3.org/1999/02/22-rdf-syntax-ns#")
public class DescriptionType
    extends ElementContainer
{

    @XmlAttribute(name = "about", namespace = "http://www.w3.org/1999/02/22-rdf-syntax-ns#")
    protected String about;

    /**
     * Obtient la valeur de la propriété about.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getAbout() {
        return about;
    }

    /**
     * Définit la valeur de la propriété about.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setAbout(String value) {
        this.about = value;
    }

}
