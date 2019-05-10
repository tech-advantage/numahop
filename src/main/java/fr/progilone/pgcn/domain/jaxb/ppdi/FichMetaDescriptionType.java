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
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlValue;


/**
 * <p>Classe Java pour FichMetaDescriptionType complex type.
 *
 * <p>Le fragment de sch�ma suivant indique le contenu attendu figurant dans cette classe.
 *
 * <pre>
 * &lt;complexType name="FichMetaDescriptionType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="compression" type="{http://www.cines.fr/pac/ppdi}stringNotNULL" maxOccurs="unbounded"/>
 *         &lt;element name="encodage" type="{http://www.cines.fr/pac/ppdi}stringNotNULL" maxOccurs="unbounded"/>
 *         &lt;element name="formatFichier" type="{http://www.cines.fr/pac/ppdi}stringNotNULL" maxOccurs="unbounded"/>
 *         &lt;element name="nomFichier" type="{http://www.cines.fr/pac/ppdi}stringNotNULL" maxOccurs="unbounded"/>
 *         &lt;element name="empreinteOri" type="{http://www.cines.fr/pac/ppdi}stringNotNULL" maxOccurs="unbounded"/>
 *         &lt;element name="noteFichier" type="{http://www.cines.fr/pac/ppdi}stringNotNULL" maxOccurs="unbounded"/>
 *         &lt;element name="structureFichier" maxOccurs="unbounded">
 *           &lt;complexType>
 *             &lt;simpleContent>
 *               &lt;extension base="&lt;http://www.cines.fr/pac/ppdi>stringNotNULL">
 *                 &lt;attribute name="type" type="{http://www.w3.org/2001/XMLSchema}anySimpleType" />
 *               &lt;/extension>
 *             &lt;/simpleContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "FichMetaDescriptionType", propOrder = {
    "compression",
    "encodage",
    "formatFichier",
    "nomFichier",
    "empreinteOri",
    "noteFichier",
    "structureFichier"
})
public class FichMetaDescriptionType {

    @XmlElement(required = true)
    protected List<String> compression;
    @XmlElement(required = true)
    protected List<String> encodage;
    @XmlElement(required = true)
    protected List<String> formatFichier;
    @XmlElement(required = true)
    protected List<String> nomFichier;
    @XmlElement(required = true)
    protected List<String> empreinteOri;
    @XmlElement(required = true)
    protected List<String> noteFichier;
    @XmlElement(required = true)
    protected List<FichMetaDescriptionType.StructureFichier> structureFichier;

    /**
     * Gets the value of the compression property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the compression property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getCompression().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     *
     *
     */
    public List<String> getCompression() {
        if (compression == null) {
            compression = new ArrayList<>();
        }
        return this.compression;
    }

    /**
     * Gets the value of the encodage property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the encodage property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getEncodage().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     *
     *
     */
    public List<String> getEncodage() {
        if (encodage == null) {
            encodage = new ArrayList<>();
        }
        return this.encodage;
    }

    /**
     * Gets the value of the formatFichier property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the formatFichier property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getFormatFichier().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     *
     *
     */
    public List<String> getFormatFichier() {
        if (formatFichier == null) {
            formatFichier = new ArrayList<>();
        }
        return this.formatFichier;
    }

    /**
     * Gets the value of the nomFichier property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the nomFichier property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getNomFichier().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     *
     *
     */
    public List<String> getNomFichier() {
        if (nomFichier == null) {
            nomFichier = new ArrayList<>();
        }
        return this.nomFichier;
    }

    /**
     * Gets the value of the empreinteOri property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the empreinteOri property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getEmpreinteOri().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     *
     *
     */
    public List<String> getEmpreinteOri() {
        if (empreinteOri == null) {
            empreinteOri = new ArrayList<>();
        }
        return this.empreinteOri;
    }

    /**
     * Gets the value of the noteFichier property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the noteFichier property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getNoteFichier().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     *
     *
     */
    public List<String> getNoteFichier() {
        if (noteFichier == null) {
            noteFichier = new ArrayList<>();
        }
        return this.noteFichier;
    }

    /**
     * Gets the value of the structureFichier property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the structureFichier property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getStructureFichier().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link FichMetaDescriptionType.StructureFichier }
     *
     *
     */
    public List<FichMetaDescriptionType.StructureFichier> getStructureFichier() {
        if (structureFichier == null) {
            structureFichier = new ArrayList<>();
        }
        return this.structureFichier;
    }


    /**
     * <p>Classe Java pour anonymous complex type.
     *
     * <p>Le fragment de sch�ma suivant indique le contenu attendu figurant dans cette classe.
     *
     * <pre>
     * &lt;complexType>
     *   &lt;simpleContent>
     *     &lt;extension base="&lt;http://www.cines.fr/pac/ppdi>stringNotNULL">
     *       &lt;attribute name="type" type="{http://www.w3.org/2001/XMLSchema}anySimpleType" />
     *     &lt;/extension>
     *   &lt;/simpleContent>
     * &lt;/complexType>
     * </pre>
     *
     *
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "value"
    })
    public static class StructureFichier {

        @XmlValue
        protected String value;
        @XmlAttribute(name = "type")
        @XmlSchemaType(name = "anySimpleType")
        protected String type;

        /**
         *
         * Chaine de caract�res de type xsd:string et de longueur non nulle.
         *
         *
         * @return
         *     possible object is
         *     {@link String }
         *
         */
        public String getValue() {
            return value;
        }

        /**
         * D�finit la valeur de la propri�t� value.
         *
         * @param value
         *     allowed object is
         *     {@link String }
         *
         */
        public void setValue(String value) {
            this.value = value;
        }

        /**
         * Obtient la valeur de la propri�t� type.
         *
         * @return
         *     possible object is
         *     {@link String }
         *
         */
        public String getType() {
            return type;
        }

        /**
         * D�finit la valeur de la propri�t� type.
         *
         * @param value
         *     allowed object is
         *     {@link String }
         *
         */
        public void setType(String value) {
            this.type = value;
        }

    }

}
