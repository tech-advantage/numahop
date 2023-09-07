//
// Ce fichier a été généré par l'implémentation de référence JavaTM Architecture for XML Binding (JAXB), v2.2.11
// Voir <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a>
// Toute modification apportée à ce fichier sera perdue lors de la recompilation du schéma source.
// Généré le : 2017.01.03 à 11:16:35 AM CET
//

package fr.progilone.pgcn.domain.jaxb.aip;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;
import jakarta.xml.bind.annotation.XmlValue;

/**
 *
 *
 * <pre>
 * &lt;?xml version="1.0" encoding="UTF-8"?&gt;&lt;description xmlns="http://www.cines.fr/pac/test/aip" xmlns:ISO-639-3="urn:un:unece:uncefact:codelist:draft:DAF:languageCode:2011-10-07" xmlns:RA="urn:un:unece:uncefact:codelist:draft:DAF:accessRestrictionCode:2009-08-18" xmlns:xsd="http://www.w3.org/2001/XMLSchema"&gt;description de la structure du fichier&lt;/description&gt;
 * </pre>
 *
 *
 * <pre>
 * &lt;?xml version="1.0" encoding="UTF-8"?&gt;&lt;exemple xmlns="http://www.cines.fr/pac/test/aip" xmlns:ISO-639-3="urn:un:unece:uncefact:codelist:draft:DAF:languageCode:2011-10-07" xmlns:RA="urn:un:unece:uncefact:codelist:draft:DAF:accessRestrictionCode:2009-08-18" xmlns:xsd="http://www.w3.org/2001/XMLSchema"&gt;nom d’un fichier associé qui en décrit la structure, texte libre, URI du schéma xsd&lt;/exemple&gt;
 * </pre>
 *
 *
 *
 * <p>
 * Classe Java pour anonymous complex type.
 *
 * <p>
 * Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 *
 * <pre>
 * &lt;complexType&gt;
 *   &lt;simpleContent&gt;
 *     &lt;extension base="&lt;http://www.cines.fr/pac/test/aip&gt;stringNotNULL"&gt;
 *       &lt;attribute name="type"&gt;
 *         &lt;simpleType&gt;
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *             &lt;enumeration value="DTD"/&gt;
 *             &lt;enumeration value="XSD"/&gt;
 *             &lt;enumeration value="RNG"/&gt;
 *             &lt;enumeration value="unknown"/&gt;
 *           &lt;/restriction&gt;
 *         &lt;/simpleType&gt;
 *       &lt;/attribute&gt;
 *       &lt;attribute name="hash" type="{http://www.cines.fr/pac/test/aip}stringNotNULL" /&gt;
 *     &lt;/extension&gt;
 *   &lt;/simpleContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {"value"})
@XmlRootElement(name = "structureFichier")
public class StructureFichier {

    @XmlValue
    protected String value;
    @XmlAttribute(name = "type")
    protected String type;
    @XmlAttribute(name = "hash")
    protected String hash;

    /**
     * Chaine de caractères composée d'au moins 1 caractère imprimable ou non exclusivement composé d'espace et/ou de tabulations et/ou de nouvelle
     * ligne et/ou de retour chariot
     *
     * @return
     *         possible object is
     *         {@link String }
     *
     */
    public String getValue() {
        return value;
    }

    /**
     * Définit la valeur de la propriété value.
     *
     * @param value
     *            allowed object is
     *            {@link String }
     *
     */
    public void setValue(String value) {
        this.value = value;
    }

    /**
     * Obtient la valeur de la propriété type.
     *
     * @return
     *         possible object is
     *         {@link String }
     *
     */
    public String getType() {
        return type;
    }

    /**
     * Définit la valeur de la propriété type.
     *
     * @param value
     *            allowed object is
     *            {@link String }
     *
     */
    public void setType(String value) {
        this.type = value;
    }

    /**
     * Obtient la valeur de la propriété hash.
     *
     * @return
     *         possible object is
     *         {@link String }
     *
     */
    public String getHash() {
        return hash;
    }

    /**
     * Définit la valeur de la propriété hash.
     *
     * @param value
     *            allowed object is
     *            {@link String }
     *
     */
    public void setHash(String value) {
        this.hash = value;
    }

}
