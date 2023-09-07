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
 * Description générale des fichiers de métadonnées accompagnant les objets archivés ou du contenu de é OtherMetadata é dans le cadre de léarchivage
 * au format SEDA.
 *
 *
 * <p>
 * Classe Java pour mdMetierType complex type.
 *
 * <p>
 * Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 *
 * <pre>
 * &lt;complexType name="mdMetierType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element ref="{http://www.cines.fr/pac/ppdi}mdDesc"/>
 *         &lt;element ref="{http://www.cines.fr/pac/ppdi}mdFichier" maxOccurs="unbounded"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "mdMetierType",
         propOrder = {"mdDesc",
                      "mdFichier"})
public class MdMetierType {

    @XmlElement(required = true)
    protected String mdDesc;
    @XmlElement(required = true)
    protected List<String> mdFichier;

    /**
     * Obtient la valeur de la propriété mdDesc.
     *
     * @return
     *         possible object is
     *         {@link String }
     *
     */
    public String getMdDesc() {
        return mdDesc;
    }

    /**
     * Définit la valeur de la propriété mdDesc.
     *
     * @param value
     *            allowed object is
     *            {@link String }
     *
     */
    public void setMdDesc(String value) {
        this.mdDesc = value;
    }

    /**
     * Gets the value of the mdFichier property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the mdFichier property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     *
     * <pre>
     * getMdFichier().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     *
     *
     */
    public List<String> getMdFichier() {
        if (mdFichier == null) {
            mdFichier = new ArrayList<>();
        }
        return this.mdFichier;
    }

}
