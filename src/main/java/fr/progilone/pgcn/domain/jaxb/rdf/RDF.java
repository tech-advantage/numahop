//
// Ce fichier a été généré par l'implémentation de référence JavaTM Architecture for XML Binding (JAXB), v2.2.8-b130911.1802
// Voir <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a>
// Toute modification apportée à ce fichier sera perdue lors de la recompilation du schéma source.
// Généré le : 2017.01.02 à 04:49:01 PM CET
//

package fr.progilone.pgcn.domain.jaxb.rdf;

import javax.xml.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>Classe Java pour anonymous complex type.
 * <p>
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 * <p>
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="Description" type="{http://www.w3.org/1999/02/22-rdf-syntax-ns#}DescriptionType" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "description"
})
@XmlRootElement(name = "RDF", namespace = "http://www.w3.org/1999/02/22-rdf-syntax-ns#")
public class RDF {

    @XmlElement(name = "Description", namespace = "http://www.w3.org/1999/02/22-rdf-syntax-ns#")
    protected List<DescriptionType> description;

    /**
     * Gets the value of the description property.
     * <p>
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the description property.
     * <p>
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getDescription().add(newItem);
     * </pre>
     * <p>
     * <p>
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link DescriptionType }
     */
    public List<DescriptionType> getDescription() {
        if (description == null) {
            description = new ArrayList<>();
        }
        return this.description;
    }

    /**
     * Install a listener for descriptions on this object. If l is null, the listener
     * is removed again.
     */
    public void setDescriptionListener(final Listener l) {
        description = (l == null) ? null : new ArrayList<DescriptionType>() {
            public boolean add(DescriptionType o) {
                l.handleDescription(RDF.this, o);
                return false;
            }
        };
    }

    /**
     * This listener is invoked every time a new descriptions is unmarshalled.
     */
    public interface Listener {
        void handleDescription(RDF rdf, DescriptionType descriptionType);
    }
}
