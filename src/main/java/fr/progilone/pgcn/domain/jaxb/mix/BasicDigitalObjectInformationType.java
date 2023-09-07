//
// Ce fichier a été généré par l'implémentation de référence JavaTM Architecture for XML Binding (JAXB), v2.2.8-b130911.1802
// Voir <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a>
// Toute modification apportée à ce fichier sera perdue lors de la recompilation du schéma source.
// Généré le : 2017.08.25 à 03:15:17 PM CEST
//

package fr.progilone.pgcn.domain.jaxb.mix;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * Classe Java pour BasicDigitalObjectInformationType complex type.
 *
 * <p>
 * Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 *
 * <pre>
 * &lt;complexType name="BasicDigitalObjectInformationType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="ObjectIdentifier" maxOccurs="unbounded" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="objectIdentifierType" type="{http://www.loc.gov/mix/v20}stringType" minOccurs="0"/>
 *                   &lt;element name="objectIdentifierValue" type="{http://www.loc.gov/mix/v20}stringType" minOccurs="0"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="fileSize" type="{http://www.loc.gov/mix/v20}nonNegativeIntegerType" minOccurs="0"/>
 *         &lt;element name="FormatDesignation" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="formatName" type="{http://www.loc.gov/mix/v20}stringType" minOccurs="0"/>
 *                   &lt;element name="formatVersion" type="{http://www.loc.gov/mix/v20}stringType" minOccurs="0"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="FormatRegistry" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="formatRegistryName" type="{http://www.loc.gov/mix/v20}stringType" minOccurs="0"/>
 *                   &lt;element name="formatRegistryKey" type="{http://www.loc.gov/mix/v20}stringType" minOccurs="0"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="byteOrder" type="{http://www.loc.gov/mix/v20}typeOfByteOrderType" minOccurs="0"/>
 *         &lt;element name="Compression" maxOccurs="unbounded" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="compressionScheme" type="{http://www.loc.gov/mix/v20}stringType" minOccurs="0"/>
 *                   &lt;element name="compressionSchemeLocalList" type="{http://www.loc.gov/mix/v20}URIType" minOccurs="0"/>
 *                   &lt;element name="compressionSchemeLocalValue" type="{http://www.loc.gov/mix/v20}stringType" minOccurs="0"/>
 *                   &lt;element name="compressionRatio" type="{http://www.loc.gov/mix/v20}rationalType" minOccurs="0"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="Fixity" maxOccurs="unbounded" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="messageDigestAlgorithm" type="{http://www.loc.gov/mix/v20}typeOfMessageDigestAlgorithmType" minOccurs="0"/>
 *                   &lt;element name="messageDigest" type="{http://www.loc.gov/mix/v20}stringType" minOccurs="0"/>
 *                   &lt;element name="messageDigestOriginator" type="{http://www.loc.gov/mix/v20}stringType" minOccurs="0"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
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
@XmlType(name = "BasicDigitalObjectInformationType",
         propOrder = {"objectIdentifier",
                      "fileSize",
                      "formatDesignation",
                      "formatRegistry",
                      "byteOrder",
                      "compression",
                      "fixity"})
public class BasicDigitalObjectInformationType {

    @XmlElement(name = "ObjectIdentifier")
    protected List<BasicDigitalObjectInformationType.ObjectIdentifier> objectIdentifier;
    protected NonNegativeIntegerType fileSize;
    @XmlElement(name = "FormatDesignation")
    protected BasicDigitalObjectInformationType.FormatDesignation formatDesignation;
    @XmlElement(name = "FormatRegistry")
    protected BasicDigitalObjectInformationType.FormatRegistry formatRegistry;
    protected TypeOfByteOrderType byteOrder;
    @XmlElement(name = "Compression")
    protected List<BasicDigitalObjectInformationType.Compression> compression;
    @XmlElement(name = "Fixity")
    protected List<BasicDigitalObjectInformationType.Fixity> fixity;

    /**
     * Gets the value of the objectIdentifier property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the objectIdentifier property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     *
     * <pre>
     * getObjectIdentifier().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link BasicDigitalObjectInformationType.ObjectIdentifier }
     *
     *
     */
    public List<BasicDigitalObjectInformationType.ObjectIdentifier> getObjectIdentifier() {
        if (objectIdentifier == null) {
            objectIdentifier = new ArrayList<>();
        }
        return this.objectIdentifier;
    }

    /**
     * Obtient la valeur de la propriété fileSize.
     *
     * @return
     *         possible object is
     *         {@link NonNegativeIntegerType }
     *
     */
    public NonNegativeIntegerType getFileSize() {
        return fileSize;
    }

    /**
     * Définit la valeur de la propriété fileSize.
     *
     * @param value
     *            allowed object is
     *            {@link NonNegativeIntegerType }
     *
     */
    public void setFileSize(NonNegativeIntegerType value) {
        this.fileSize = value;
    }

    /**
     * Obtient la valeur de la propriété formatDesignation.
     *
     * @return
     *         possible object is
     *         {@link BasicDigitalObjectInformationType.FormatDesignation }
     *
     */
    public BasicDigitalObjectInformationType.FormatDesignation getFormatDesignation() {
        return formatDesignation;
    }

    /**
     * Définit la valeur de la propriété formatDesignation.
     *
     * @param value
     *            allowed object is
     *            {@link BasicDigitalObjectInformationType.FormatDesignation }
     *
     */
    public void setFormatDesignation(BasicDigitalObjectInformationType.FormatDesignation value) {
        this.formatDesignation = value;
    }

    /**
     * Obtient la valeur de la propriété formatRegistry.
     *
     * @return
     *         possible object is
     *         {@link BasicDigitalObjectInformationType.FormatRegistry }
     *
     */
    public BasicDigitalObjectInformationType.FormatRegistry getFormatRegistry() {
        return formatRegistry;
    }

    /**
     * Définit la valeur de la propriété formatRegistry.
     *
     * @param value
     *            allowed object is
     *            {@link BasicDigitalObjectInformationType.FormatRegistry }
     *
     */
    public void setFormatRegistry(BasicDigitalObjectInformationType.FormatRegistry value) {
        this.formatRegistry = value;
    }

    /**
     * Obtient la valeur de la propriété byteOrder.
     *
     * @return
     *         possible object is
     *         {@link TypeOfByteOrderType }
     *
     */
    public TypeOfByteOrderType getByteOrder() {
        return byteOrder;
    }

    /**
     * Définit la valeur de la propriété byteOrder.
     *
     * @param value
     *            allowed object is
     *            {@link TypeOfByteOrderType }
     *
     */
    public void setByteOrder(TypeOfByteOrderType value) {
        this.byteOrder = value;
    }

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
     *
     * <pre>
     * getCompression().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link BasicDigitalObjectInformationType.Compression }
     *
     *
     */
    public List<BasicDigitalObjectInformationType.Compression> getCompression() {
        if (compression == null) {
            compression = new ArrayList<>();
        }
        return this.compression;
    }

    /**
     * Gets the value of the fixity property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the fixity property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     *
     * <pre>
     * getFixity().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link BasicDigitalObjectInformationType.Fixity }
     *
     *
     */
    public List<BasicDigitalObjectInformationType.Fixity> getFixity() {
        if (fixity == null) {
            fixity = new ArrayList<>();
        }
        return this.fixity;
    }

    /**
     * <p>
     * Classe Java pour anonymous complex type.
     *
     * <p>
     * Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
     *
     * <pre>
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *       &lt;sequence>
     *         &lt;element name="compressionScheme" type="{http://www.loc.gov/mix/v20}stringType" minOccurs="0"/>
     *         &lt;element name="compressionSchemeLocalList" type="{http://www.loc.gov/mix/v20}URIType" minOccurs="0"/>
     *         &lt;element name="compressionSchemeLocalValue" type="{http://www.loc.gov/mix/v20}stringType" minOccurs="0"/>
     *         &lt;element name="compressionRatio" type="{http://www.loc.gov/mix/v20}rationalType" minOccurs="0"/>
     *       &lt;/sequence>
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     *
     *
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "",
             propOrder = {"compressionScheme",
                          "compressionSchemeLocalList",
                          "compressionSchemeLocalValue",
                          "compressionRatio"})
    public static class Compression {

        protected StringType compressionScheme;
        protected URIType compressionSchemeLocalList;
        protected StringType compressionSchemeLocalValue;
        protected RationalType compressionRatio;

        /**
         * Obtient la valeur de la propriété compressionScheme.
         *
         * @return
         *         possible object is
         *         {@link StringType }
         *
         */
        public StringType getCompressionScheme() {
            return compressionScheme;
        }

        /**
         * Définit la valeur de la propriété compressionScheme.
         *
         * @param value
         *            allowed object is
         *            {@link StringType }
         *
         */
        public void setCompressionScheme(StringType value) {
            this.compressionScheme = value;
        }

        /**
         * Obtient la valeur de la propriété compressionSchemeLocalList.
         *
         * @return
         *         possible object is
         *         {@link URIType }
         *
         */
        public URIType getCompressionSchemeLocalList() {
            return compressionSchemeLocalList;
        }

        /**
         * Définit la valeur de la propriété compressionSchemeLocalList.
         *
         * @param value
         *            allowed object is
         *            {@link URIType }
         *
         */
        public void setCompressionSchemeLocalList(URIType value) {
            this.compressionSchemeLocalList = value;
        }

        /**
         * Obtient la valeur de la propriété compressionSchemeLocalValue.
         *
         * @return
         *         possible object is
         *         {@link StringType }
         *
         */
        public StringType getCompressionSchemeLocalValue() {
            return compressionSchemeLocalValue;
        }

        /**
         * Définit la valeur de la propriété compressionSchemeLocalValue.
         *
         * @param value
         *            allowed object is
         *            {@link StringType }
         *
         */
        public void setCompressionSchemeLocalValue(StringType value) {
            this.compressionSchemeLocalValue = value;
        }

        /**
         * Obtient la valeur de la propriété compressionRatio.
         *
         * @return
         *         possible object is
         *         {@link RationalType }
         *
         */
        public RationalType getCompressionRatio() {
            return compressionRatio;
        }

        /**
         * Définit la valeur de la propriété compressionRatio.
         *
         * @param value
         *            allowed object is
         *            {@link RationalType }
         *
         */
        public void setCompressionRatio(RationalType value) {
            this.compressionRatio = value;
        }

    }

    /**
     * <p>
     * Classe Java pour anonymous complex type.
     *
     * <p>
     * Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
     *
     * <pre>
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *       &lt;sequence>
     *         &lt;element name="messageDigestAlgorithm" type="{http://www.loc.gov/mix/v20}typeOfMessageDigestAlgorithmType" minOccurs="0"/>
     *         &lt;element name="messageDigest" type="{http://www.loc.gov/mix/v20}stringType" minOccurs="0"/>
     *         &lt;element name="messageDigestOriginator" type="{http://www.loc.gov/mix/v20}stringType" minOccurs="0"/>
     *       &lt;/sequence>
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     *
     *
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "",
             propOrder = {"messageDigestAlgorithm",
                          "messageDigest",
                          "messageDigestOriginator"})
    public static class Fixity {

        protected TypeOfMessageDigestAlgorithmType messageDigestAlgorithm;
        protected StringType messageDigest;
        protected StringType messageDigestOriginator;

        /**
         * Obtient la valeur de la propriété messageDigestAlgorithm.
         *
         * @return
         *         possible object is
         *         {@link TypeOfMessageDigestAlgorithmType }
         *
         */
        public TypeOfMessageDigestAlgorithmType getMessageDigestAlgorithm() {
            return messageDigestAlgorithm;
        }

        /**
         * Définit la valeur de la propriété messageDigestAlgorithm.
         *
         * @param value
         *            allowed object is
         *            {@link TypeOfMessageDigestAlgorithmType }
         *
         */
        public void setMessageDigestAlgorithm(TypeOfMessageDigestAlgorithmType value) {
            this.messageDigestAlgorithm = value;
        }

        /**
         * Obtient la valeur de la propriété messageDigest.
         *
         * @return
         *         possible object is
         *         {@link StringType }
         *
         */
        public StringType getMessageDigest() {
            return messageDigest;
        }

        /**
         * Définit la valeur de la propriété messageDigest.
         *
         * @param value
         *            allowed object is
         *            {@link StringType }
         *
         */
        public void setMessageDigest(StringType value) {
            this.messageDigest = value;
        }

        /**
         * Obtient la valeur de la propriété messageDigestOriginator.
         *
         * @return
         *         possible object is
         *         {@link StringType }
         *
         */
        public StringType getMessageDigestOriginator() {
            return messageDigestOriginator;
        }

        /**
         * Définit la valeur de la propriété messageDigestOriginator.
         *
         * @param value
         *            allowed object is
         *            {@link StringType }
         *
         */
        public void setMessageDigestOriginator(StringType value) {
            this.messageDigestOriginator = value;
        }

    }

    /**
     * <p>
     * Classe Java pour anonymous complex type.
     *
     * <p>
     * Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
     *
     * <pre>
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *       &lt;sequence>
     *         &lt;element name="formatName" type="{http://www.loc.gov/mix/v20}stringType" minOccurs="0"/>
     *         &lt;element name="formatVersion" type="{http://www.loc.gov/mix/v20}stringType" minOccurs="0"/>
     *       &lt;/sequence>
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     *
     *
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "",
             propOrder = {"formatName",
                          "formatVersion"})
    public static class FormatDesignation {

        protected StringType formatName;
        protected StringType formatVersion;

        /**
         * Obtient la valeur de la propriété formatName.
         *
         * @return
         *         possible object is
         *         {@link StringType }
         *
         */
        public StringType getFormatName() {
            return formatName;
        }

        /**
         * Définit la valeur de la propriété formatName.
         *
         * @param value
         *            allowed object is
         *            {@link StringType }
         *
         */
        public void setFormatName(StringType value) {
            this.formatName = value;
        }

        /**
         * Obtient la valeur de la propriété formatVersion.
         *
         * @return
         *         possible object is
         *         {@link StringType }
         *
         */
        public StringType getFormatVersion() {
            return formatVersion;
        }

        /**
         * Définit la valeur de la propriété formatVersion.
         *
         * @param value
         *            allowed object is
         *            {@link StringType }
         *
         */
        public void setFormatVersion(StringType value) {
            this.formatVersion = value;
        }

    }

    /**
     * <p>
     * Classe Java pour anonymous complex type.
     *
     * <p>
     * Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
     *
     * <pre>
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *       &lt;sequence>
     *         &lt;element name="formatRegistryName" type="{http://www.loc.gov/mix/v20}stringType" minOccurs="0"/>
     *         &lt;element name="formatRegistryKey" type="{http://www.loc.gov/mix/v20}stringType" minOccurs="0"/>
     *       &lt;/sequence>
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     *
     *
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "",
             propOrder = {"formatRegistryName",
                          "formatRegistryKey"})
    public static class FormatRegistry {

        protected StringType formatRegistryName;
        protected StringType formatRegistryKey;

        /**
         * Obtient la valeur de la propriété formatRegistryName.
         *
         * @return
         *         possible object is
         *         {@link StringType }
         *
         */
        public StringType getFormatRegistryName() {
            return formatRegistryName;
        }

        /**
         * Définit la valeur de la propriété formatRegistryName.
         *
         * @param value
         *            allowed object is
         *            {@link StringType }
         *
         */
        public void setFormatRegistryName(StringType value) {
            this.formatRegistryName = value;
        }

        /**
         * Obtient la valeur de la propriété formatRegistryKey.
         *
         * @return
         *         possible object is
         *         {@link StringType }
         *
         */
        public StringType getFormatRegistryKey() {
            return formatRegistryKey;
        }

        /**
         * Définit la valeur de la propriété formatRegistryKey.
         *
         * @param value
         *            allowed object is
         *            {@link StringType }
         *
         */
        public void setFormatRegistryKey(StringType value) {
            this.formatRegistryKey = value;
        }

    }

    /**
     * <p>
     * Classe Java pour anonymous complex type.
     *
     * <p>
     * Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
     *
     * <pre>
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *       &lt;sequence>
     *         &lt;element name="objectIdentifierType" type="{http://www.loc.gov/mix/v20}stringType" minOccurs="0"/>
     *         &lt;element name="objectIdentifierValue" type="{http://www.loc.gov/mix/v20}stringType" minOccurs="0"/>
     *       &lt;/sequence>
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     *
     *
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "",
             propOrder = {"objectIdentifierType",
                          "objectIdentifierValue"})
    public static class ObjectIdentifier {

        protected StringType objectIdentifierType;
        protected StringType objectIdentifierValue;

        /**
         * Obtient la valeur de la propriété objectIdentifierType.
         *
         * @return
         *         possible object is
         *         {@link StringType }
         *
         */
        public StringType getObjectIdentifierType() {
            return objectIdentifierType;
        }

        /**
         * Définit la valeur de la propriété objectIdentifierType.
         *
         * @param value
         *            allowed object is
         *            {@link StringType }
         *
         */
        public void setObjectIdentifierType(StringType value) {
            this.objectIdentifierType = value;
        }

        /**
         * Obtient la valeur de la propriété objectIdentifierValue.
         *
         * @return
         *         possible object is
         *         {@link StringType }
         *
         */
        public StringType getObjectIdentifierValue() {
            return objectIdentifierValue;
        }

        /**
         * Définit la valeur de la propriété objectIdentifierValue.
         *
         * @param value
         *            allowed object is
         *            {@link StringType }
         *
         */
        public void setObjectIdentifierValue(StringType value) {
            this.objectIdentifierValue = value;
        }

    }

}
