//
// Ce fichier a été généré par l'implémentation de référence JavaTM Architecture for XML Binding (JAXB), v2.2.8-b130911.1802
// Voir <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a>
// Toute modification apportée à fichier sera perdue lors de la recompilation du schéma source.
// Généré le : 2017.12.08 à 03:23:15 PM CET
//


package fr.progilone.pgcn.domain.jaxb.ppdi;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 *
 * Informations de description du contexte du projet d'archives au CINES
 *
 *
 * <p>Classe Java pour ContexteType complex type.
 *
 * <p>Le fragment de sch�ma suivant indique le contenu attendu figurant dans cette classe.
 *
 * <pre>
 * &lt;complexType name="ContexteType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element ref="{http://www.cines.fr/pac/ppdi}Fonds" maxOccurs="unbounded"/>
 *         &lt;element ref="{http://www.cines.fr/pac/ppdi}Producteur" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element ref="{http://www.cines.fr/pac/ppdi}ServiceVersant" maxOccurs="unbounded"/>
 *         &lt;element ref="{http://www.cines.fr/pac/ppdi}CircuitProduction" maxOccurs="unbounded"/>
 *         &lt;element ref="{http://www.cines.fr/pac/ppdi}Archivage" maxOccurs="unbounded"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ContexteType", propOrder = {
    "fonds",
    "producteur",
    "serviceVersant",
    "circuitProduction",
    "archivage"
})
public class ContexteType {

    @XmlElement(name = "Fonds", required = true)
    protected List<FondsType> fonds;
    @XmlElement(name = "Producteur")
    protected List<ProducteurType> producteur;
    @XmlElement(name = "ServiceVersant", required = true)
    protected List<ServiceVersantType> serviceVersant;
    @XmlElement(name = "CircuitProduction", required = true)
    protected List<CircuitProductionType> circuitProduction;
    @XmlElement(name = "Archivage", required = true)
    protected List<ArchivageType> archivage;

    /**
     * Gets the value of the fonds property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the fonds property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getFonds().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link FondsType }
     *
     *
     */
    public List<FondsType> getFonds() {
        if (fonds == null) {
            fonds = new ArrayList<>();
        }
        return this.fonds;
    }

    /**
     * Gets the value of the producteur property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the producteur property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getProducteur().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link ProducteurType }
     *
     *
     */
    public List<ProducteurType> getProducteur() {
        if (producteur == null) {
            producteur = new ArrayList<>();
        }
        return this.producteur;
    }

    /**
     * Gets the value of the serviceVersant property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the serviceVersant property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getServiceVersant().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link ServiceVersantType }
     *
     *
     */
    public List<ServiceVersantType> getServiceVersant() {
        if (serviceVersant == null) {
            serviceVersant = new ArrayList<>();
        }
        return this.serviceVersant;
    }

    /**
     * Gets the value of the circuitProduction property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the circuitProduction property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getCircuitProduction().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link CircuitProductionType }
     *
     *
     */
    public List<CircuitProductionType> getCircuitProduction() {
        if (circuitProduction == null) {
            circuitProduction = new ArrayList<>();
        }
        return this.circuitProduction;
    }

    /**
     * Gets the value of the archivage property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the archivage property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getArchivage().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link ArchivageType }
     *
     *
     */
    public List<ArchivageType> getArchivage() {
        if (archivage == null) {
            archivage = new ArrayList<>();
        }
        return this.archivage;
    }

}
