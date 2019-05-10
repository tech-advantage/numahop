//
// Ce fichier a été généré par l'implémentation de référence JavaTM Architecture for XML Binding (JAXB), v2.2.11 
// Voir <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Toute modification apportée à ce fichier sera perdue lors de la recompilation du schéma source. 
// Généré le : 2017.06.16 à 03:03:54 PM CEST 
//


package fr.progilone.pgcn.domain.jaxb.oaidc;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import fr.progilone.pgcn.domain.jaxb.dc.ElementContainer;


/**
 * <p>Classe Java pour oai_dcType complex type.
 * 
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 * 
 * <pre>
 * &lt;complexType name="oai_dcType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;choice maxOccurs="unbounded" minOccurs="0"&gt;
 *         &lt;element ref="{http://purl.org/dc/elements/1.1/}title"/&gt;
 *         &lt;element ref="{http://purl.org/dc/elements/1.1/}creator"/&gt;
 *         &lt;element ref="{http://purl.org/dc/elements/1.1/}subject"/&gt;
 *         &lt;element ref="{http://purl.org/dc/elements/1.1/}description"/&gt;
 *         &lt;element ref="{http://purl.org/dc/elements/1.1/}publisher"/&gt;
 *         &lt;element ref="{http://purl.org/dc/elements/1.1/}contributor"/&gt;
 *         &lt;element ref="{http://purl.org/dc/elements/1.1/}date"/&gt;
 *         &lt;element ref="{http://purl.org/dc/elements/1.1/}type"/&gt;
 *         &lt;element ref="{http://purl.org/dc/elements/1.1/}format"/&gt;
 *         &lt;element ref="{http://purl.org/dc/elements/1.1/}identifier"/&gt;
 *         &lt;element ref="{http://purl.org/dc/elements/1.1/}source"/&gt;
 *         &lt;element ref="{http://purl.org/dc/elements/1.1/}language"/&gt;
 *         &lt;element ref="{http://purl.org/dc/elements/1.1/}relation"/&gt;
 *         &lt;element ref="{http://purl.org/dc/elements/1.1/}coverage"/&gt;
 *         &lt;element ref="{http://purl.org/dc/elements/1.1/}rights"/&gt;
 *       &lt;/choice&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "")
@XmlRootElement(name = "dc", namespace = "http://www.openarchives.org/OAI/2.0/oai_dc/")
public class OaiDcType extends ElementContainer {

}
