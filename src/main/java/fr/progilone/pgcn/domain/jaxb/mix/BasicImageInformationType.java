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
 * Classe Java pour BasicImageInformationType complex type.
 *
 * <p>
 * Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 *
 * <pre>
 * &lt;complexType name="BasicImageInformationType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="BasicImageCharacteristics" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="imageWidth" type="{http://www.loc.gov/mix/v20}positiveIntegerType" minOccurs="0"/>
 *                   &lt;element name="imageHeight" type="{http://www.loc.gov/mix/v20}positiveIntegerType" minOccurs="0"/>
 *                   &lt;element name="PhotometricInterpretation" minOccurs="0">
 *                     &lt;complexType>
 *                       &lt;complexContent>
 *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                           &lt;sequence>
 *                             &lt;element name="colorSpace" type="{http://www.loc.gov/mix/v20}stringType" minOccurs="0"/>
 *                             &lt;element name="ColorProfile" minOccurs="0">
 *                               &lt;complexType>
 *                                 &lt;complexContent>
 *                                   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                                     &lt;sequence>
 *                                       &lt;element name="IccProfile" minOccurs="0">
 *                                         &lt;complexType>
 *                                           &lt;complexContent>
 *                                             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                                               &lt;sequence>
 *                                                 &lt;element name="iccProfileName" type="{http://www.loc.gov/mix/v20}stringType" minOccurs="0"/>
 *                                                 &lt;element name="iccProfileVersion" type="{http://www.loc.gov/mix/v20}stringType" minOccurs="0"/>
 *                                                 &lt;element name="iccProfileURI" type="{http://www.loc.gov/mix/v20}URIType" minOccurs="0"/>
 *                                               &lt;/sequence>
 *                                             &lt;/restriction>
 *                                           &lt;/complexContent>
 *                                         &lt;/complexType>
 *                                       &lt;/element>
 *                                       &lt;element name="LocalProfile" minOccurs="0">
 *                                         &lt;complexType>
 *                                           &lt;complexContent>
 *                                             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                                               &lt;sequence>
 *                                                 &lt;element name="localProfileName" type="{http://www.loc.gov/mix/v20}stringType" minOccurs="0"/>
 *                                                 &lt;element name="localProfileURL" type="{http://www.loc.gov/mix/v20}URIType" minOccurs="0"/>
 *                                               &lt;/sequence>
 *                                             &lt;/restriction>
 *                                           &lt;/complexContent>
 *                                         &lt;/complexType>
 *                                       &lt;/element>
 *                                       &lt;element name="embeddedProfile" type="{http://www.loc.gov/mix/v20}base64BinaryType" minOccurs="0"/>
 *                                     &lt;/sequence>
 *                                   &lt;/restriction>
 *                                 &lt;/complexContent>
 *                               &lt;/complexType>
 *                             &lt;/element>
 *                             &lt;element name="YCbCr" minOccurs="0">
 *                               &lt;complexType>
 *                                 &lt;complexContent>
 *                                   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                                     &lt;sequence>
 *                                       &lt;element name="YCbCrSubSampling" minOccurs="0">
 *                                         &lt;complexType>
 *                                           &lt;complexContent>
 *                                             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                                               &lt;sequence>
 *                                                 &lt;element name="yCbCrSubsampleHoriz" type="{http://www.loc.gov/mix/v20}typeOfYCbCrSubsampleHorizType" minOccurs="0"/>
 *                                                 &lt;element name="yCbCrSubsampleVert" type="{http://www.loc.gov/mix/v20}typeOfYCbCrSubsampleVertType" minOccurs="0"/>
 *                                               &lt;/sequence>
 *                                             &lt;/restriction>
 *                                           &lt;/complexContent>
 *                                         &lt;/complexType>
 *                                       &lt;/element>
 *                                       &lt;element name="yCbCrPositioning" type="{http://www.loc.gov/mix/v20}typeOfYCbCrPositioningType" minOccurs="0"/>
 *                                       &lt;element name="YCbCrCoefficients" minOccurs="0">
 *                                         &lt;complexType>
 *                                           &lt;complexContent>
 *                                             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                                               &lt;sequence>
 *                                                 &lt;element name="lumaRed" type="{http://www.loc.gov/mix/v20}rationalType" minOccurs="0"/>
 *                                                 &lt;element name="lumaGreen" type="{http://www.loc.gov/mix/v20}rationalType" minOccurs="0"/>
 *                                                 &lt;element name="lumaBlue" type="{http://www.loc.gov/mix/v20}rationalType" minOccurs="0"/>
 *                                               &lt;/sequence>
 *                                             &lt;/restriction>
 *                                           &lt;/complexContent>
 *                                         &lt;/complexType>
 *                                       &lt;/element>
 *                                     &lt;/sequence>
 *                                   &lt;/restriction>
 *                                 &lt;/complexContent>
 *                               &lt;/complexType>
 *                             &lt;/element>
 *                             &lt;element name="ReferenceBlackWhite" maxOccurs="unbounded" minOccurs="0">
 *                               &lt;complexType>
 *                                 &lt;complexContent>
 *                                   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                                     &lt;sequence>
 *                                       &lt;element name="Component" maxOccurs="unbounded" minOccurs="0">
 *                                         &lt;complexType>
 *                                           &lt;complexContent>
 *                                             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                                               &lt;sequence>
 *                                                 &lt;element name="componentPhotometricInterpretation" type="{http://www.loc.gov/mix/v20}typeOfComponentPhotometricInterpretationType"/>
 *                                                 &lt;element name="footroom" type="{http://www.loc.gov/mix/v20}rationalType"/>
 *                                                 &lt;element name="headroom" type="{http://www.loc.gov/mix/v20}rationalType"/>
 *                                               &lt;/sequence>
 *                                             &lt;/restriction>
 *                                           &lt;/complexContent>
 *                                         &lt;/complexType>
 *                                       &lt;/element>
 *                                     &lt;/sequence>
 *                                   &lt;/restriction>
 *                                 &lt;/complexContent>
 *                               &lt;/complexType>
 *                             &lt;/element>
 *                           &lt;/sequence>
 *                         &lt;/restriction>
 *                       &lt;/complexContent>
 *                     &lt;/complexType>
 *                   &lt;/element>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="SpecialFormatCharacteristics" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="JPEG2000" minOccurs="0">
 *                     &lt;complexType>
 *                       &lt;complexContent>
 *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                           &lt;sequence>
 *                             &lt;element name="CodecCompliance" minOccurs="0">
 *                               &lt;complexType>
 *                                 &lt;complexContent>
 *                                   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                                     &lt;sequence>
 *                                       &lt;element name="codec" type="{http://www.loc.gov/mix/v20}stringType" minOccurs="0"/>
 *                                       &lt;element name="codecVersion" type="{http://www.loc.gov/mix/v20}stringType" minOccurs="0"/>
 *                                       &lt;element name="codestreamProfile" type="{http://www.loc.gov/mix/v20}stringType" minOccurs="0"/>
 *                                       &lt;element name="complianceClass" type="{http://www.loc.gov/mix/v20}stringType" minOccurs="0"/>
 *                                     &lt;/sequence>
 *                                   &lt;/restriction>
 *                                 &lt;/complexContent>
 *                               &lt;/complexType>
 *                             &lt;/element>
 *                             &lt;element name="EncodingOptions" minOccurs="0">
 *                               &lt;complexType>
 *                                 &lt;complexContent>
 *                                   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                                     &lt;sequence>
 *                                       &lt;element name="Tiles" minOccurs="0">
 *                                         &lt;complexType>
 *                                           &lt;complexContent>
 *                                             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                                               &lt;sequence>
 *                                                 &lt;element name="tileWidth" type="{http://www.loc.gov/mix/v20}positiveIntegerType" minOccurs="0"/>
 *                                                 &lt;element name="tileHeight" type="{http://www.loc.gov/mix/v20}positiveIntegerType" minOccurs="0"/>
 *                                               &lt;/sequence>
 *                                             &lt;/restriction>
 *                                           &lt;/complexContent>
 *                                         &lt;/complexType>
 *                                       &lt;/element>
 *                                       &lt;element name="qualityLayers" type="{http://www.loc.gov/mix/v20}positiveIntegerType" minOccurs="0"/>
 *                                       &lt;element name="resolutionLevels" type="{http://www.loc.gov/mix/v20}positiveIntegerType" minOccurs="0"/>
 *                                     &lt;/sequence>
 *                                   &lt;/restriction>
 *                                 &lt;/complexContent>
 *                               &lt;/complexType>
 *                             &lt;/element>
 *                           &lt;/sequence>
 *                         &lt;/restriction>
 *                       &lt;/complexContent>
 *                     &lt;/complexType>
 *                   &lt;/element>
 *                   &lt;element name="MrSID" minOccurs="0">
 *                     &lt;complexType>
 *                       &lt;complexContent>
 *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                           &lt;sequence>
 *                             &lt;element name="zoomLevels" type="{http://www.loc.gov/mix/v20}positiveIntegerType" minOccurs="0"/>
 *                           &lt;/sequence>
 *                         &lt;/restriction>
 *                       &lt;/complexContent>
 *                     &lt;/complexType>
 *                   &lt;/element>
 *                   &lt;element name="Djvu" minOccurs="0">
 *                     &lt;complexType>
 *                       &lt;complexContent>
 *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                           &lt;sequence>
 *                             &lt;element name="djvuFormat" type="{http://www.loc.gov/mix/v20}typeOfDjvuFormatType" minOccurs="0"/>
 *                           &lt;/sequence>
 *                         &lt;/restriction>
 *                       &lt;/complexContent>
 *                     &lt;/complexType>
 *                   &lt;/element>
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
@XmlType(name = "BasicImageInformationType",
         propOrder = {"basicImageCharacteristics",
                      "specialFormatCharacteristics"})
public class BasicImageInformationType {

    @XmlElement(name = "BasicImageCharacteristics")
    protected BasicImageInformationType.BasicImageCharacteristics basicImageCharacteristics;
    @XmlElement(name = "SpecialFormatCharacteristics")
    protected BasicImageInformationType.SpecialFormatCharacteristics specialFormatCharacteristics;

    /**
     * Obtient la valeur de la propriété basicImageCharacteristics.
     *
     * @return
     *         possible object is
     *         {@link BasicImageInformationType.BasicImageCharacteristics }
     *
     */
    public BasicImageInformationType.BasicImageCharacteristics getBasicImageCharacteristics() {
        return basicImageCharacteristics;
    }

    /**
     * Définit la valeur de la propriété basicImageCharacteristics.
     *
     * @param value
     *            allowed object is
     *            {@link BasicImageInformationType.BasicImageCharacteristics }
     *
     */
    public void setBasicImageCharacteristics(BasicImageInformationType.BasicImageCharacteristics value) {
        this.basicImageCharacteristics = value;
    }

    /**
     * Obtient la valeur de la propriété specialFormatCharacteristics.
     *
     * @return
     *         possible object is
     *         {@link BasicImageInformationType.SpecialFormatCharacteristics }
     *
     */
    public BasicImageInformationType.SpecialFormatCharacteristics getSpecialFormatCharacteristics() {
        return specialFormatCharacteristics;
    }

    /**
     * Définit la valeur de la propriété specialFormatCharacteristics.
     *
     * @param value
     *            allowed object is
     *            {@link BasicImageInformationType.SpecialFormatCharacteristics }
     *
     */
    public void setSpecialFormatCharacteristics(BasicImageInformationType.SpecialFormatCharacteristics value) {
        this.specialFormatCharacteristics = value;
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
     *         &lt;element name="imageWidth" type="{http://www.loc.gov/mix/v20}positiveIntegerType" minOccurs="0"/>
     *         &lt;element name="imageHeight" type="{http://www.loc.gov/mix/v20}positiveIntegerType" minOccurs="0"/>
     *         &lt;element name="PhotometricInterpretation" minOccurs="0">
     *           &lt;complexType>
     *             &lt;complexContent>
     *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *                 &lt;sequence>
     *                   &lt;element name="colorSpace" type="{http://www.loc.gov/mix/v20}stringType" minOccurs="0"/>
     *                   &lt;element name="ColorProfile" minOccurs="0">
     *                     &lt;complexType>
     *                       &lt;complexContent>
     *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *                           &lt;sequence>
     *                             &lt;element name="IccProfile" minOccurs="0">
     *                               &lt;complexType>
     *                                 &lt;complexContent>
     *                                   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *                                     &lt;sequence>
     *                                       &lt;element name="iccProfileName" type="{http://www.loc.gov/mix/v20}stringType" minOccurs="0"/>
     *                                       &lt;element name="iccProfileVersion" type="{http://www.loc.gov/mix/v20}stringType" minOccurs="0"/>
     *                                       &lt;element name="iccProfileURI" type="{http://www.loc.gov/mix/v20}URIType" minOccurs="0"/>
     *                                     &lt;/sequence>
     *                                   &lt;/restriction>
     *                                 &lt;/complexContent>
     *                               &lt;/complexType>
     *                             &lt;/element>
     *                             &lt;element name="LocalProfile" minOccurs="0">
     *                               &lt;complexType>
     *                                 &lt;complexContent>
     *                                   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *                                     &lt;sequence>
     *                                       &lt;element name="localProfileName" type="{http://www.loc.gov/mix/v20}stringType" minOccurs="0"/>
     *                                       &lt;element name="localProfileURL" type="{http://www.loc.gov/mix/v20}URIType" minOccurs="0"/>
     *                                     &lt;/sequence>
     *                                   &lt;/restriction>
     *                                 &lt;/complexContent>
     *                               &lt;/complexType>
     *                             &lt;/element>
     *                             &lt;element name="embeddedProfile" type="{http://www.loc.gov/mix/v20}base64BinaryType" minOccurs="0"/>
     *                           &lt;/sequence>
     *                         &lt;/restriction>
     *                       &lt;/complexContent>
     *                     &lt;/complexType>
     *                   &lt;/element>
     *                   &lt;element name="YCbCr" minOccurs="0">
     *                     &lt;complexType>
     *                       &lt;complexContent>
     *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *                           &lt;sequence>
     *                             &lt;element name="YCbCrSubSampling" minOccurs="0">
     *                               &lt;complexType>
     *                                 &lt;complexContent>
     *                                   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *                                     &lt;sequence>
     *                                       &lt;element name="yCbCrSubsampleHoriz" type="{http://www.loc.gov/mix/v20}typeOfYCbCrSubsampleHorizType" minOccurs="0"/>
     *                                       &lt;element name="yCbCrSubsampleVert" type="{http://www.loc.gov/mix/v20}typeOfYCbCrSubsampleVertType" minOccurs="0"/>
     *                                     &lt;/sequence>
     *                                   &lt;/restriction>
     *                                 &lt;/complexContent>
     *                               &lt;/complexType>
     *                             &lt;/element>
     *                             &lt;element name="yCbCrPositioning" type="{http://www.loc.gov/mix/v20}typeOfYCbCrPositioningType" minOccurs="0"/>
     *                             &lt;element name="YCbCrCoefficients" minOccurs="0">
     *                               &lt;complexType>
     *                                 &lt;complexContent>
     *                                   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *                                     &lt;sequence>
     *                                       &lt;element name="lumaRed" type="{http://www.loc.gov/mix/v20}rationalType" minOccurs="0"/>
     *                                       &lt;element name="lumaGreen" type="{http://www.loc.gov/mix/v20}rationalType" minOccurs="0"/>
     *                                       &lt;element name="lumaBlue" type="{http://www.loc.gov/mix/v20}rationalType" minOccurs="0"/>
     *                                     &lt;/sequence>
     *                                   &lt;/restriction>
     *                                 &lt;/complexContent>
     *                               &lt;/complexType>
     *                             &lt;/element>
     *                           &lt;/sequence>
     *                         &lt;/restriction>
     *                       &lt;/complexContent>
     *                     &lt;/complexType>
     *                   &lt;/element>
     *                   &lt;element name="ReferenceBlackWhite" maxOccurs="unbounded" minOccurs="0">
     *                     &lt;complexType>
     *                       &lt;complexContent>
     *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *                           &lt;sequence>
     *                             &lt;element name="Component" maxOccurs="unbounded" minOccurs="0">
     *                               &lt;complexType>
     *                                 &lt;complexContent>
     *                                   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *                                     &lt;sequence>
     *                                       &lt;element name="componentPhotometricInterpretation" type="{http://www.loc.gov/mix/v20}typeOfComponentPhotometricInterpretationType"/>
     *                                       &lt;element name="footroom" type="{http://www.loc.gov/mix/v20}rationalType"/>
     *                                       &lt;element name="headroom" type="{http://www.loc.gov/mix/v20}rationalType"/>
     *                                     &lt;/sequence>
     *                                   &lt;/restriction>
     *                                 &lt;/complexContent>
     *                               &lt;/complexType>
     *                             &lt;/element>
     *                           &lt;/sequence>
     *                         &lt;/restriction>
     *                       &lt;/complexContent>
     *                     &lt;/complexType>
     *                   &lt;/element>
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
    @XmlType(name = "",
             propOrder = {"imageWidth",
                          "imageHeight",
                          "photometricInterpretation"})
    public static class BasicImageCharacteristics {

        protected PositiveIntegerType imageWidth;
        protected PositiveIntegerType imageHeight;
        @XmlElement(name = "PhotometricInterpretation")
        protected BasicImageInformationType.BasicImageCharacteristics.PhotometricInterpretation photometricInterpretation;

        /**
         * Obtient la valeur de la propriété imageWidth.
         *
         * @return
         *         possible object is
         *         {@link PositiveIntegerType }
         *
         */
        public PositiveIntegerType getImageWidth() {
            return imageWidth;
        }

        /**
         * Définit la valeur de la propriété imageWidth.
         *
         * @param value
         *            allowed object is
         *            {@link PositiveIntegerType }
         *
         */
        public void setImageWidth(PositiveIntegerType value) {
            this.imageWidth = value;
        }

        /**
         * Obtient la valeur de la propriété imageHeight.
         *
         * @return
         *         possible object is
         *         {@link PositiveIntegerType }
         *
         */
        public PositiveIntegerType getImageHeight() {
            return imageHeight;
        }

        /**
         * Définit la valeur de la propriété imageHeight.
         *
         * @param value
         *            allowed object is
         *            {@link PositiveIntegerType }
         *
         */
        public void setImageHeight(PositiveIntegerType value) {
            this.imageHeight = value;
        }

        /**
         * Obtient la valeur de la propriété photometricInterpretation.
         *
         * @return
         *         possible object is
         *         {@link BasicImageInformationType.BasicImageCharacteristics.PhotometricInterpretation }
         *
         */
        public BasicImageInformationType.BasicImageCharacteristics.PhotometricInterpretation getPhotometricInterpretation() {
            return photometricInterpretation;
        }

        /**
         * Définit la valeur de la propriété photometricInterpretation.
         *
         * @param value
         *            allowed object is
         *            {@link BasicImageInformationType.BasicImageCharacteristics.PhotometricInterpretation }
         *
         */
        public void setPhotometricInterpretation(BasicImageInformationType.BasicImageCharacteristics.PhotometricInterpretation value) {
            this.photometricInterpretation = value;
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
         *         &lt;element name="colorSpace" type="{http://www.loc.gov/mix/v20}stringType" minOccurs="0"/>
         *         &lt;element name="ColorProfile" minOccurs="0">
         *           &lt;complexType>
         *             &lt;complexContent>
         *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
         *                 &lt;sequence>
         *                   &lt;element name="IccProfile" minOccurs="0">
         *                     &lt;complexType>
         *                       &lt;complexContent>
         *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
         *                           &lt;sequence>
         *                             &lt;element name="iccProfileName" type="{http://www.loc.gov/mix/v20}stringType" minOccurs="0"/>
         *                             &lt;element name="iccProfileVersion" type="{http://www.loc.gov/mix/v20}stringType" minOccurs="0"/>
         *                             &lt;element name="iccProfileURI" type="{http://www.loc.gov/mix/v20}URIType" minOccurs="0"/>
         *                           &lt;/sequence>
         *                         &lt;/restriction>
         *                       &lt;/complexContent>
         *                     &lt;/complexType>
         *                   &lt;/element>
         *                   &lt;element name="LocalProfile" minOccurs="0">
         *                     &lt;complexType>
         *                       &lt;complexContent>
         *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
         *                           &lt;sequence>
         *                             &lt;element name="localProfileName" type="{http://www.loc.gov/mix/v20}stringType" minOccurs="0"/>
         *                             &lt;element name="localProfileURL" type="{http://www.loc.gov/mix/v20}URIType" minOccurs="0"/>
         *                           &lt;/sequence>
         *                         &lt;/restriction>
         *                       &lt;/complexContent>
         *                     &lt;/complexType>
         *                   &lt;/element>
         *                   &lt;element name="embeddedProfile" type="{http://www.loc.gov/mix/v20}base64BinaryType" minOccurs="0"/>
         *                 &lt;/sequence>
         *               &lt;/restriction>
         *             &lt;/complexContent>
         *           &lt;/complexType>
         *         &lt;/element>
         *         &lt;element name="YCbCr" minOccurs="0">
         *           &lt;complexType>
         *             &lt;complexContent>
         *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
         *                 &lt;sequence>
         *                   &lt;element name="YCbCrSubSampling" minOccurs="0">
         *                     &lt;complexType>
         *                       &lt;complexContent>
         *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
         *                           &lt;sequence>
         *                             &lt;element name="yCbCrSubsampleHoriz" type="{http://www.loc.gov/mix/v20}typeOfYCbCrSubsampleHorizType" minOccurs="0"/>
         *                             &lt;element name="yCbCrSubsampleVert" type="{http://www.loc.gov/mix/v20}typeOfYCbCrSubsampleVertType" minOccurs="0"/>
         *                           &lt;/sequence>
         *                         &lt;/restriction>
         *                       &lt;/complexContent>
         *                     &lt;/complexType>
         *                   &lt;/element>
         *                   &lt;element name="yCbCrPositioning" type="{http://www.loc.gov/mix/v20}typeOfYCbCrPositioningType" minOccurs="0"/>
         *                   &lt;element name="YCbCrCoefficients" minOccurs="0">
         *                     &lt;complexType>
         *                       &lt;complexContent>
         *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
         *                           &lt;sequence>
         *                             &lt;element name="lumaRed" type="{http://www.loc.gov/mix/v20}rationalType" minOccurs="0"/>
         *                             &lt;element name="lumaGreen" type="{http://www.loc.gov/mix/v20}rationalType" minOccurs="0"/>
         *                             &lt;element name="lumaBlue" type="{http://www.loc.gov/mix/v20}rationalType" minOccurs="0"/>
         *                           &lt;/sequence>
         *                         &lt;/restriction>
         *                       &lt;/complexContent>
         *                     &lt;/complexType>
         *                   &lt;/element>
         *                 &lt;/sequence>
         *               &lt;/restriction>
         *             &lt;/complexContent>
         *           &lt;/complexType>
         *         &lt;/element>
         *         &lt;element name="ReferenceBlackWhite" maxOccurs="unbounded" minOccurs="0">
         *           &lt;complexType>
         *             &lt;complexContent>
         *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
         *                 &lt;sequence>
         *                   &lt;element name="Component" maxOccurs="unbounded" minOccurs="0">
         *                     &lt;complexType>
         *                       &lt;complexContent>
         *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
         *                           &lt;sequence>
         *                             &lt;element name="componentPhotometricInterpretation" type="{http://www.loc.gov/mix/v20}typeOfComponentPhotometricInterpretationType"/>
         *                             &lt;element name="footroom" type="{http://www.loc.gov/mix/v20}rationalType"/>
         *                             &lt;element name="headroom" type="{http://www.loc.gov/mix/v20}rationalType"/>
         *                           &lt;/sequence>
         *                         &lt;/restriction>
         *                       &lt;/complexContent>
         *                     &lt;/complexType>
         *                   &lt;/element>
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
        @XmlType(name = "",
                 propOrder = {"colorSpace",
                              "colorProfile",
                              "yCbCr",
                              "referenceBlackWhite"})
        public static class PhotometricInterpretation {

            protected StringType colorSpace;
            @XmlElement(name = "ColorProfile")
            protected BasicImageInformationType.BasicImageCharacteristics.PhotometricInterpretation.ColorProfile colorProfile;
            @XmlElement(name = "YCbCr")
            protected BasicImageInformationType.BasicImageCharacteristics.PhotometricInterpretation.YCbCr yCbCr;
            @XmlElement(name = "ReferenceBlackWhite")
            protected List<BasicImageInformationType.BasicImageCharacteristics.PhotometricInterpretation.ReferenceBlackWhite> referenceBlackWhite;

            /**
             * Obtient la valeur de la propriété colorSpace.
             *
             * @return
             *         possible object is
             *         {@link StringType }
             *
             */
            public StringType getColorSpace() {
                return colorSpace;
            }

            /**
             * Définit la valeur de la propriété colorSpace.
             *
             * @param value
             *            allowed object is
             *            {@link StringType }
             *
             */
            public void setColorSpace(StringType value) {
                this.colorSpace = value;
            }

            /**
             * Obtient la valeur de la propriété colorProfile.
             *
             * @return
             *         possible object is
             *         {@link BasicImageInformationType.BasicImageCharacteristics.PhotometricInterpretation.ColorProfile }
             *
             */
            public BasicImageInformationType.BasicImageCharacteristics.PhotometricInterpretation.ColorProfile getColorProfile() {
                return colorProfile;
            }

            /**
             * Définit la valeur de la propriété colorProfile.
             *
             * @param value
             *            allowed object is
             *            {@link BasicImageInformationType.BasicImageCharacteristics.PhotometricInterpretation.ColorProfile }
             *
             */
            public void setColorProfile(BasicImageInformationType.BasicImageCharacteristics.PhotometricInterpretation.ColorProfile value) {
                this.colorProfile = value;
            }

            /**
             * Obtient la valeur de la propriété yCbCr.
             *
             * @return
             *         possible object is
             *         {@link BasicImageInformationType.BasicImageCharacteristics.PhotometricInterpretation.YCbCr }
             *
             */
            public BasicImageInformationType.BasicImageCharacteristics.PhotometricInterpretation.YCbCr getYCbCr() {
                return yCbCr;
            }

            /**
             * Définit la valeur de la propriété yCbCr.
             *
             * @param value
             *            allowed object is
             *            {@link BasicImageInformationType.BasicImageCharacteristics.PhotometricInterpretation.YCbCr }
             *
             */
            public void setYCbCr(BasicImageInformationType.BasicImageCharacteristics.PhotometricInterpretation.YCbCr value) {
                this.yCbCr = value;
            }

            /**
             * Gets the value of the referenceBlackWhite property.
             *
             * <p>
             * This accessor method returns a reference to the live list,
             * not a snapshot. Therefore any modification you make to the
             * returned list will be present inside the JAXB object.
             * This is why there is not a <CODE>set</CODE> method for the referenceBlackWhite property.
             *
             * <p>
             * For example, to add a new item, do as follows:
             *
             * <pre>
             * getReferenceBlackWhite().add(newItem);
             * </pre>
             *
             *
             * <p>
             * Objects of the following type(s) are allowed in the list
             * {@link BasicImageInformationType.BasicImageCharacteristics.PhotometricInterpretation.ReferenceBlackWhite }
             *
             *
             */
            public List<BasicImageInformationType.BasicImageCharacteristics.PhotometricInterpretation.ReferenceBlackWhite> getReferenceBlackWhite() {
                if (referenceBlackWhite == null) {
                    referenceBlackWhite = new ArrayList<>();
                }
                return this.referenceBlackWhite;
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
             *         &lt;element name="IccProfile" minOccurs="0">
             *           &lt;complexType>
             *             &lt;complexContent>
             *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
             *                 &lt;sequence>
             *                   &lt;element name="iccProfileName" type="{http://www.loc.gov/mix/v20}stringType" minOccurs="0"/>
             *                   &lt;element name="iccProfileVersion" type="{http://www.loc.gov/mix/v20}stringType" minOccurs="0"/>
             *                   &lt;element name="iccProfileURI" type="{http://www.loc.gov/mix/v20}URIType" minOccurs="0"/>
             *                 &lt;/sequence>
             *               &lt;/restriction>
             *             &lt;/complexContent>
             *           &lt;/complexType>
             *         &lt;/element>
             *         &lt;element name="LocalProfile" minOccurs="0">
             *           &lt;complexType>
             *             &lt;complexContent>
             *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
             *                 &lt;sequence>
             *                   &lt;element name="localProfileName" type="{http://www.loc.gov/mix/v20}stringType" minOccurs="0"/>
             *                   &lt;element name="localProfileURL" type="{http://www.loc.gov/mix/v20}URIType" minOccurs="0"/>
             *                 &lt;/sequence>
             *               &lt;/restriction>
             *             &lt;/complexContent>
             *           &lt;/complexType>
             *         &lt;/element>
             *         &lt;element name="embeddedProfile" type="{http://www.loc.gov/mix/v20}base64BinaryType" minOccurs="0"/>
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
                     propOrder = {"iccProfile",
                                  "localProfile",
                                  "embeddedProfile"})
            public static class ColorProfile {

                @XmlElement(name = "IccProfile")
                protected BasicImageInformationType.BasicImageCharacteristics.PhotometricInterpretation.ColorProfile.IccProfile iccProfile;
                @XmlElement(name = "LocalProfile")
                protected BasicImageInformationType.BasicImageCharacteristics.PhotometricInterpretation.ColorProfile.LocalProfile localProfile;
                protected Base64BinaryType embeddedProfile;

                /**
                 * Obtient la valeur de la propriété iccProfile.
                 *
                 * @return
                 *         possible object is
                 *         {@link BasicImageInformationType.BasicImageCharacteristics.PhotometricInterpretation.ColorProfile.IccProfile }
                 *
                 */
                public BasicImageInformationType.BasicImageCharacteristics.PhotometricInterpretation.ColorProfile.IccProfile getIccProfile() {
                    return iccProfile;
                }

                /**
                 * Définit la valeur de la propriété iccProfile.
                 *
                 * @param value
                 *            allowed object is
                 *            {@link BasicImageInformationType.BasicImageCharacteristics.PhotometricInterpretation.ColorProfile.IccProfile }
                 *
                 */
                public void setIccProfile(BasicImageInformationType.BasicImageCharacteristics.PhotometricInterpretation.ColorProfile.IccProfile value) {
                    this.iccProfile = value;
                }

                /**
                 * Obtient la valeur de la propriété localProfile.
                 *
                 * @return
                 *         possible object is
                 *         {@link BasicImageInformationType.BasicImageCharacteristics.PhotometricInterpretation.ColorProfile.LocalProfile }
                 *
                 */
                public BasicImageInformationType.BasicImageCharacteristics.PhotometricInterpretation.ColorProfile.LocalProfile getLocalProfile() {
                    return localProfile;
                }

                /**
                 * Définit la valeur de la propriété localProfile.
                 *
                 * @param value
                 *            allowed object is
                 *            {@link BasicImageInformationType.BasicImageCharacteristics.PhotometricInterpretation.ColorProfile.LocalProfile }
                 *
                 */
                public void setLocalProfile(BasicImageInformationType.BasicImageCharacteristics.PhotometricInterpretation.ColorProfile.LocalProfile value) {
                    this.localProfile = value;
                }

                /**
                 * Obtient la valeur de la propriété embeddedProfile.
                 *
                 * @return
                 *         possible object is
                 *         {@link Base64BinaryType }
                 *
                 */
                public Base64BinaryType getEmbeddedProfile() {
                    return embeddedProfile;
                }

                /**
                 * Définit la valeur de la propriété embeddedProfile.
                 *
                 * @param value
                 *            allowed object is
                 *            {@link Base64BinaryType }
                 *
                 */
                public void setEmbeddedProfile(Base64BinaryType value) {
                    this.embeddedProfile = value;
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
                 *         &lt;element name="iccProfileName" type="{http://www.loc.gov/mix/v20}stringType" minOccurs="0"/>
                 *         &lt;element name="iccProfileVersion" type="{http://www.loc.gov/mix/v20}stringType" minOccurs="0"/>
                 *         &lt;element name="iccProfileURI" type="{http://www.loc.gov/mix/v20}URIType" minOccurs="0"/>
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
                         propOrder = {"iccProfileName",
                                      "iccProfileVersion",
                                      "iccProfileURI"})
                public static class IccProfile {

                    protected StringType iccProfileName;
                    protected StringType iccProfileVersion;
                    protected URIType iccProfileURI;

                    /**
                     * Obtient la valeur de la propriété iccProfileName.
                     *
                     * @return
                     *         possible object is
                     *         {@link StringType }
                     *
                     */
                    public StringType getIccProfileName() {
                        return iccProfileName;
                    }

                    /**
                     * Définit la valeur de la propriété iccProfileName.
                     *
                     * @param value
                     *            allowed object is
                     *            {@link StringType }
                     *
                     */
                    public void setIccProfileName(StringType value) {
                        this.iccProfileName = value;
                    }

                    /**
                     * Obtient la valeur de la propriété iccProfileVersion.
                     *
                     * @return
                     *         possible object is
                     *         {@link StringType }
                     *
                     */
                    public StringType getIccProfileVersion() {
                        return iccProfileVersion;
                    }

                    /**
                     * Définit la valeur de la propriété iccProfileVersion.
                     *
                     * @param value
                     *            allowed object is
                     *            {@link StringType }
                     *
                     */
                    public void setIccProfileVersion(StringType value) {
                        this.iccProfileVersion = value;
                    }

                    /**
                     * Obtient la valeur de la propriété iccProfileURI.
                     *
                     * @return
                     *         possible object is
                     *         {@link URIType }
                     *
                     */
                    public URIType getIccProfileURI() {
                        return iccProfileURI;
                    }

                    /**
                     * Définit la valeur de la propriété iccProfileURI.
                     *
                     * @param value
                     *            allowed object is
                     *            {@link URIType }
                     *
                     */
                    public void setIccProfileURI(URIType value) {
                        this.iccProfileURI = value;
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
                 *         &lt;element name="localProfileName" type="{http://www.loc.gov/mix/v20}stringType" minOccurs="0"/>
                 *         &lt;element name="localProfileURL" type="{http://www.loc.gov/mix/v20}URIType" minOccurs="0"/>
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
                         propOrder = {"localProfileName",
                                      "localProfileURL"})
                public static class LocalProfile {

                    protected StringType localProfileName;
                    protected URIType localProfileURL;

                    /**
                     * Obtient la valeur de la propriété localProfileName.
                     *
                     * @return
                     *         possible object is
                     *         {@link StringType }
                     *
                     */
                    public StringType getLocalProfileName() {
                        return localProfileName;
                    }

                    /**
                     * Définit la valeur de la propriété localProfileName.
                     *
                     * @param value
                     *            allowed object is
                     *            {@link StringType }
                     *
                     */
                    public void setLocalProfileName(StringType value) {
                        this.localProfileName = value;
                    }

                    /**
                     * Obtient la valeur de la propriété localProfileURL.
                     *
                     * @return
                     *         possible object is
                     *         {@link URIType }
                     *
                     */
                    public URIType getLocalProfileURL() {
                        return localProfileURL;
                    }

                    /**
                     * Définit la valeur de la propriété localProfileURL.
                     *
                     * @param value
                     *            allowed object is
                     *            {@link URIType }
                     *
                     */
                    public void setLocalProfileURL(URIType value) {
                        this.localProfileURL = value;
                    }

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
             *         &lt;element name="Component" maxOccurs="unbounded" minOccurs="0">
             *           &lt;complexType>
             *             &lt;complexContent>
             *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
             *                 &lt;sequence>
             *                   &lt;element name="componentPhotometricInterpretation" type="{http://www.loc.gov/mix/v20}typeOfComponentPhotometricInterpretationType"/>
             *                   &lt;element name="footroom" type="{http://www.loc.gov/mix/v20}rationalType"/>
             *                   &lt;element name="headroom" type="{http://www.loc.gov/mix/v20}rationalType"/>
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
            @XmlType(name = "", propOrder = {"component"})
            public static class ReferenceBlackWhite {

                @XmlElement(name = "Component")
                protected List<BasicImageInformationType.BasicImageCharacteristics.PhotometricInterpretation.ReferenceBlackWhite.Component> component;

                /**
                 * Gets the value of the component property.
                 *
                 * <p>
                 * This accessor method returns a reference to the live list,
                 * not a snapshot. Therefore any modification you make to the
                 * returned list will be present inside the JAXB object.
                 * This is why there is not a <CODE>set</CODE> method for the component property.
                 *
                 * <p>
                 * For example, to add a new item, do as follows:
                 *
                 * <pre>
                 * getComponent().add(newItem);
                 * </pre>
                 *
                 *
                 * <p>
                 * Objects of the following type(s) are allowed in the list
                 * {@link BasicImageInformationType.BasicImageCharacteristics.PhotometricInterpretation.ReferenceBlackWhite.Component }
                 *
                 *
                 */
                public List<BasicImageInformationType.BasicImageCharacteristics.PhotometricInterpretation.ReferenceBlackWhite.Component> getComponent() {
                    if (component == null) {
                        component = new ArrayList<>();
                    }
                    return this.component;
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
                 *         &lt;element name="componentPhotometricInterpretation" type="{http://www.loc.gov/mix/v20}typeOfComponentPhotometricInterpretationType"/>
                 *         &lt;element name="footroom" type="{http://www.loc.gov/mix/v20}rationalType"/>
                 *         &lt;element name="headroom" type="{http://www.loc.gov/mix/v20}rationalType"/>
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
                         propOrder = {"componentPhotometricInterpretation",
                                      "footroom",
                                      "headroom"})
                public static class Component {

                    @XmlElement(required = true)
                    protected TypeOfComponentPhotometricInterpretationType componentPhotometricInterpretation;
                    @XmlElement(required = true)
                    protected RationalType footroom;
                    @XmlElement(required = true)
                    protected RationalType headroom;

                    /**
                     * Obtient la valeur de la propriété componentPhotometricInterpretation.
                     *
                     * @return
                     *         possible object is
                     *         {@link TypeOfComponentPhotometricInterpretationType }
                     *
                     */
                    public TypeOfComponentPhotometricInterpretationType getComponentPhotometricInterpretation() {
                        return componentPhotometricInterpretation;
                    }

                    /**
                     * Définit la valeur de la propriété componentPhotometricInterpretation.
                     *
                     * @param value
                     *            allowed object is
                     *            {@link TypeOfComponentPhotometricInterpretationType }
                     *
                     */
                    public void setComponentPhotometricInterpretation(TypeOfComponentPhotometricInterpretationType value) {
                        this.componentPhotometricInterpretation = value;
                    }

                    /**
                     * Obtient la valeur de la propriété footroom.
                     *
                     * @return
                     *         possible object is
                     *         {@link RationalType }
                     *
                     */
                    public RationalType getFootroom() {
                        return footroom;
                    }

                    /**
                     * Définit la valeur de la propriété footroom.
                     *
                     * @param value
                     *            allowed object is
                     *            {@link RationalType }
                     *
                     */
                    public void setFootroom(RationalType value) {
                        this.footroom = value;
                    }

                    /**
                     * Obtient la valeur de la propriété headroom.
                     *
                     * @return
                     *         possible object is
                     *         {@link RationalType }
                     *
                     */
                    public RationalType getHeadroom() {
                        return headroom;
                    }

                    /**
                     * Définit la valeur de la propriété headroom.
                     *
                     * @param value
                     *            allowed object is
                     *            {@link RationalType }
                     *
                     */
                    public void setHeadroom(RationalType value) {
                        this.headroom = value;
                    }

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
             *         &lt;element name="YCbCrSubSampling" minOccurs="0">
             *           &lt;complexType>
             *             &lt;complexContent>
             *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
             *                 &lt;sequence>
             *                   &lt;element name="yCbCrSubsampleHoriz" type="{http://www.loc.gov/mix/v20}typeOfYCbCrSubsampleHorizType" minOccurs="0"/>
             *                   &lt;element name="yCbCrSubsampleVert" type="{http://www.loc.gov/mix/v20}typeOfYCbCrSubsampleVertType" minOccurs="0"/>
             *                 &lt;/sequence>
             *               &lt;/restriction>
             *             &lt;/complexContent>
             *           &lt;/complexType>
             *         &lt;/element>
             *         &lt;element name="yCbCrPositioning" type="{http://www.loc.gov/mix/v20}typeOfYCbCrPositioningType" minOccurs="0"/>
             *         &lt;element name="YCbCrCoefficients" minOccurs="0">
             *           &lt;complexType>
             *             &lt;complexContent>
             *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
             *                 &lt;sequence>
             *                   &lt;element name="lumaRed" type="{http://www.loc.gov/mix/v20}rationalType" minOccurs="0"/>
             *                   &lt;element name="lumaGreen" type="{http://www.loc.gov/mix/v20}rationalType" minOccurs="0"/>
             *                   &lt;element name="lumaBlue" type="{http://www.loc.gov/mix/v20}rationalType" minOccurs="0"/>
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
            @XmlType(name = "",
                     propOrder = {"yCbCrSubSampling",
                                  "yCbCrPositioning",
                                  "yCbCrCoefficients"})
            public static class YCbCr {

                @XmlElement(name = "YCbCrSubSampling")
                protected BasicImageInformationType.BasicImageCharacteristics.PhotometricInterpretation.YCbCr.YCbCrSubSampling yCbCrSubSampling;
                protected TypeOfYCbCrPositioningType yCbCrPositioning;
                @XmlElement(name = "YCbCrCoefficients")
                protected BasicImageInformationType.BasicImageCharacteristics.PhotometricInterpretation.YCbCr.YCbCrCoefficients yCbCrCoefficients;

                /**
                 * Obtient la valeur de la propriété yCbCrSubSampling.
                 *
                 * @return
                 *         possible object is
                 *         {@link BasicImageInformationType.BasicImageCharacteristics.PhotometricInterpretation.YCbCr.YCbCrSubSampling }
                 *
                 */
                public BasicImageInformationType.BasicImageCharacteristics.PhotometricInterpretation.YCbCr.YCbCrSubSampling getYCbCrSubSampling() {
                    return yCbCrSubSampling;
                }

                /**
                 * Définit la valeur de la propriété yCbCrSubSampling.
                 *
                 * @param value
                 *            allowed object is
                 *            {@link BasicImageInformationType.BasicImageCharacteristics.PhotometricInterpretation.YCbCr.YCbCrSubSampling }
                 *
                 */
                public void setYCbCrSubSampling(BasicImageInformationType.BasicImageCharacteristics.PhotometricInterpretation.YCbCr.YCbCrSubSampling value) {
                    this.yCbCrSubSampling = value;
                }

                /**
                 * Obtient la valeur de la propriété yCbCrPositioning.
                 *
                 * @return
                 *         possible object is
                 *         {@link TypeOfYCbCrPositioningType }
                 *
                 */
                public TypeOfYCbCrPositioningType getYCbCrPositioning() {
                    return yCbCrPositioning;
                }

                /**
                 * Définit la valeur de la propriété yCbCrPositioning.
                 *
                 * @param value
                 *            allowed object is
                 *            {@link TypeOfYCbCrPositioningType }
                 *
                 */
                public void setYCbCrPositioning(TypeOfYCbCrPositioningType value) {
                    this.yCbCrPositioning = value;
                }

                /**
                 * Obtient la valeur de la propriété yCbCrCoefficients.
                 *
                 * @return
                 *         possible object is
                 *         {@link BasicImageInformationType.BasicImageCharacteristics.PhotometricInterpretation.YCbCr.YCbCrCoefficients }
                 *
                 */
                public BasicImageInformationType.BasicImageCharacteristics.PhotometricInterpretation.YCbCr.YCbCrCoefficients getYCbCrCoefficients() {
                    return yCbCrCoefficients;
                }

                /**
                 * Définit la valeur de la propriété yCbCrCoefficients.
                 *
                 * @param value
                 *            allowed object is
                 *            {@link BasicImageInformationType.BasicImageCharacteristics.PhotometricInterpretation.YCbCr.YCbCrCoefficients }
                 *
                 */
                public void setYCbCrCoefficients(BasicImageInformationType.BasicImageCharacteristics.PhotometricInterpretation.YCbCr.YCbCrCoefficients value) {
                    this.yCbCrCoefficients = value;
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
                 *         &lt;element name="lumaRed" type="{http://www.loc.gov/mix/v20}rationalType" minOccurs="0"/>
                 *         &lt;element name="lumaGreen" type="{http://www.loc.gov/mix/v20}rationalType" minOccurs="0"/>
                 *         &lt;element name="lumaBlue" type="{http://www.loc.gov/mix/v20}rationalType" minOccurs="0"/>
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
                         propOrder = {"lumaRed",
                                      "lumaGreen",
                                      "lumaBlue"})
                public static class YCbCrCoefficients {

                    protected RationalType lumaRed;
                    protected RationalType lumaGreen;
                    protected RationalType lumaBlue;

                    /**
                     * Obtient la valeur de la propriété lumaRed.
                     *
                     * @return
                     *         possible object is
                     *         {@link RationalType }
                     *
                     */
                    public RationalType getLumaRed() {
                        return lumaRed;
                    }

                    /**
                     * Définit la valeur de la propriété lumaRed.
                     *
                     * @param value
                     *            allowed object is
                     *            {@link RationalType }
                     *
                     */
                    public void setLumaRed(RationalType value) {
                        this.lumaRed = value;
                    }

                    /**
                     * Obtient la valeur de la propriété lumaGreen.
                     *
                     * @return
                     *         possible object is
                     *         {@link RationalType }
                     *
                     */
                    public RationalType getLumaGreen() {
                        return lumaGreen;
                    }

                    /**
                     * Définit la valeur de la propriété lumaGreen.
                     *
                     * @param value
                     *            allowed object is
                     *            {@link RationalType }
                     *
                     */
                    public void setLumaGreen(RationalType value) {
                        this.lumaGreen = value;
                    }

                    /**
                     * Obtient la valeur de la propriété lumaBlue.
                     *
                     * @return
                     *         possible object is
                     *         {@link RationalType }
                     *
                     */
                    public RationalType getLumaBlue() {
                        return lumaBlue;
                    }

                    /**
                     * Définit la valeur de la propriété lumaBlue.
                     *
                     * @param value
                     *            allowed object is
                     *            {@link RationalType }
                     *
                     */
                    public void setLumaBlue(RationalType value) {
                        this.lumaBlue = value;
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
                 *         &lt;element name="yCbCrSubsampleHoriz" type="{http://www.loc.gov/mix/v20}typeOfYCbCrSubsampleHorizType" minOccurs="0"/>
                 *         &lt;element name="yCbCrSubsampleVert" type="{http://www.loc.gov/mix/v20}typeOfYCbCrSubsampleVertType" minOccurs="0"/>
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
                         propOrder = {"yCbCrSubsampleHoriz",
                                      "yCbCrSubsampleVert"})
                public static class YCbCrSubSampling {

                    protected TypeOfYCbCrSubsampleHorizType yCbCrSubsampleHoriz;
                    protected TypeOfYCbCrSubsampleVertType yCbCrSubsampleVert;

                    /**
                     * Obtient la valeur de la propriété yCbCrSubsampleHoriz.
                     *
                     * @return
                     *         possible object is
                     *         {@link TypeOfYCbCrSubsampleHorizType }
                     *
                     */
                    public TypeOfYCbCrSubsampleHorizType getYCbCrSubsampleHoriz() {
                        return yCbCrSubsampleHoriz;
                    }

                    /**
                     * Définit la valeur de la propriété yCbCrSubsampleHoriz.
                     *
                     * @param value
                     *            allowed object is
                     *            {@link TypeOfYCbCrSubsampleHorizType }
                     *
                     */
                    public void setYCbCrSubsampleHoriz(TypeOfYCbCrSubsampleHorizType value) {
                        this.yCbCrSubsampleHoriz = value;
                    }

                    /**
                     * Obtient la valeur de la propriété yCbCrSubsampleVert.
                     *
                     * @return
                     *         possible object is
                     *         {@link TypeOfYCbCrSubsampleVertType }
                     *
                     */
                    public TypeOfYCbCrSubsampleVertType getYCbCrSubsampleVert() {
                        return yCbCrSubsampleVert;
                    }

                    /**
                     * Définit la valeur de la propriété yCbCrSubsampleVert.
                     *
                     * @param value
                     *            allowed object is
                     *            {@link TypeOfYCbCrSubsampleVertType }
                     *
                     */
                    public void setYCbCrSubsampleVert(TypeOfYCbCrSubsampleVertType value) {
                        this.yCbCrSubsampleVert = value;
                    }

                }

            }

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
     *         &lt;element name="JPEG2000" minOccurs="0">
     *           &lt;complexType>
     *             &lt;complexContent>
     *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *                 &lt;sequence>
     *                   &lt;element name="CodecCompliance" minOccurs="0">
     *                     &lt;complexType>
     *                       &lt;complexContent>
     *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *                           &lt;sequence>
     *                             &lt;element name="codec" type="{http://www.loc.gov/mix/v20}stringType" minOccurs="0"/>
     *                             &lt;element name="codecVersion" type="{http://www.loc.gov/mix/v20}stringType" minOccurs="0"/>
     *                             &lt;element name="codestreamProfile" type="{http://www.loc.gov/mix/v20}stringType" minOccurs="0"/>
     *                             &lt;element name="complianceClass" type="{http://www.loc.gov/mix/v20}stringType" minOccurs="0"/>
     *                           &lt;/sequence>
     *                         &lt;/restriction>
     *                       &lt;/complexContent>
     *                     &lt;/complexType>
     *                   &lt;/element>
     *                   &lt;element name="EncodingOptions" minOccurs="0">
     *                     &lt;complexType>
     *                       &lt;complexContent>
     *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *                           &lt;sequence>
     *                             &lt;element name="Tiles" minOccurs="0">
     *                               &lt;complexType>
     *                                 &lt;complexContent>
     *                                   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *                                     &lt;sequence>
     *                                       &lt;element name="tileWidth" type="{http://www.loc.gov/mix/v20}positiveIntegerType" minOccurs="0"/>
     *                                       &lt;element name="tileHeight" type="{http://www.loc.gov/mix/v20}positiveIntegerType" minOccurs="0"/>
     *                                     &lt;/sequence>
     *                                   &lt;/restriction>
     *                                 &lt;/complexContent>
     *                               &lt;/complexType>
     *                             &lt;/element>
     *                             &lt;element name="qualityLayers" type="{http://www.loc.gov/mix/v20}positiveIntegerType" minOccurs="0"/>
     *                             &lt;element name="resolutionLevels" type="{http://www.loc.gov/mix/v20}positiveIntegerType" minOccurs="0"/>
     *                           &lt;/sequence>
     *                         &lt;/restriction>
     *                       &lt;/complexContent>
     *                     &lt;/complexType>
     *                   &lt;/element>
     *                 &lt;/sequence>
     *               &lt;/restriction>
     *             &lt;/complexContent>
     *           &lt;/complexType>
     *         &lt;/element>
     *         &lt;element name="MrSID" minOccurs="0">
     *           &lt;complexType>
     *             &lt;complexContent>
     *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *                 &lt;sequence>
     *                   &lt;element name="zoomLevels" type="{http://www.loc.gov/mix/v20}positiveIntegerType" minOccurs="0"/>
     *                 &lt;/sequence>
     *               &lt;/restriction>
     *             &lt;/complexContent>
     *           &lt;/complexType>
     *         &lt;/element>
     *         &lt;element name="Djvu" minOccurs="0">
     *           &lt;complexType>
     *             &lt;complexContent>
     *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *                 &lt;sequence>
     *                   &lt;element name="djvuFormat" type="{http://www.loc.gov/mix/v20}typeOfDjvuFormatType" minOccurs="0"/>
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
    @XmlType(name = "",
             propOrder = {"jpeg2000",
                          "mrSID",
                          "djvu"})
    public static class SpecialFormatCharacteristics {

        @XmlElement(name = "JPEG2000")
        protected BasicImageInformationType.SpecialFormatCharacteristics.JPEG2000 jpeg2000;
        @XmlElement(name = "MrSID")
        protected BasicImageInformationType.SpecialFormatCharacteristics.MrSID mrSID;
        @XmlElement(name = "Djvu")
        protected BasicImageInformationType.SpecialFormatCharacteristics.Djvu djvu;

        /**
         * Obtient la valeur de la propriété jpeg2000.
         *
         * @return
         *         possible object is
         *         {@link BasicImageInformationType.SpecialFormatCharacteristics.JPEG2000 }
         *
         */
        public BasicImageInformationType.SpecialFormatCharacteristics.JPEG2000 getJPEG2000() {
            return jpeg2000;
        }

        /**
         * Définit la valeur de la propriété jpeg2000.
         *
         * @param value
         *            allowed object is
         *            {@link BasicImageInformationType.SpecialFormatCharacteristics.JPEG2000 }
         *
         */
        public void setJPEG2000(BasicImageInformationType.SpecialFormatCharacteristics.JPEG2000 value) {
            this.jpeg2000 = value;
        }

        /**
         * Obtient la valeur de la propriété mrSID.
         *
         * @return
         *         possible object is
         *         {@link BasicImageInformationType.SpecialFormatCharacteristics.MrSID }
         *
         */
        public BasicImageInformationType.SpecialFormatCharacteristics.MrSID getMrSID() {
            return mrSID;
        }

        /**
         * Définit la valeur de la propriété mrSID.
         *
         * @param value
         *            allowed object is
         *            {@link BasicImageInformationType.SpecialFormatCharacteristics.MrSID }
         *
         */
        public void setMrSID(BasicImageInformationType.SpecialFormatCharacteristics.MrSID value) {
            this.mrSID = value;
        }

        /**
         * Obtient la valeur de la propriété djvu.
         *
         * @return
         *         possible object is
         *         {@link BasicImageInformationType.SpecialFormatCharacteristics.Djvu }
         *
         */
        public BasicImageInformationType.SpecialFormatCharacteristics.Djvu getDjvu() {
            return djvu;
        }

        /**
         * Définit la valeur de la propriété djvu.
         *
         * @param value
         *            allowed object is
         *            {@link BasicImageInformationType.SpecialFormatCharacteristics.Djvu }
         *
         */
        public void setDjvu(BasicImageInformationType.SpecialFormatCharacteristics.Djvu value) {
            this.djvu = value;
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
         *         &lt;element name="djvuFormat" type="{http://www.loc.gov/mix/v20}typeOfDjvuFormatType" minOccurs="0"/>
         *       &lt;/sequence>
         *     &lt;/restriction>
         *   &lt;/complexContent>
         * &lt;/complexType>
         * </pre>
         *
         *
         */
        @XmlAccessorType(XmlAccessType.FIELD)
        @XmlType(name = "", propOrder = {"djvuFormat"})
        public static class Djvu {

            protected TypeOfDjvuFormatType djvuFormat;

            /**
             * Obtient la valeur de la propriété djvuFormat.
             *
             * @return
             *         possible object is
             *         {@link TypeOfDjvuFormatType }
             *
             */
            public TypeOfDjvuFormatType getDjvuFormat() {
                return djvuFormat;
            }

            /**
             * Définit la valeur de la propriété djvuFormat.
             *
             * @param value
             *            allowed object is
             *            {@link TypeOfDjvuFormatType }
             *
             */
            public void setDjvuFormat(TypeOfDjvuFormatType value) {
                this.djvuFormat = value;
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
         *         &lt;element name="CodecCompliance" minOccurs="0">
         *           &lt;complexType>
         *             &lt;complexContent>
         *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
         *                 &lt;sequence>
         *                   &lt;element name="codec" type="{http://www.loc.gov/mix/v20}stringType" minOccurs="0"/>
         *                   &lt;element name="codecVersion" type="{http://www.loc.gov/mix/v20}stringType" minOccurs="0"/>
         *                   &lt;element name="codestreamProfile" type="{http://www.loc.gov/mix/v20}stringType" minOccurs="0"/>
         *                   &lt;element name="complianceClass" type="{http://www.loc.gov/mix/v20}stringType" minOccurs="0"/>
         *                 &lt;/sequence>
         *               &lt;/restriction>
         *             &lt;/complexContent>
         *           &lt;/complexType>
         *         &lt;/element>
         *         &lt;element name="EncodingOptions" minOccurs="0">
         *           &lt;complexType>
         *             &lt;complexContent>
         *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
         *                 &lt;sequence>
         *                   &lt;element name="Tiles" minOccurs="0">
         *                     &lt;complexType>
         *                       &lt;complexContent>
         *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
         *                           &lt;sequence>
         *                             &lt;element name="tileWidth" type="{http://www.loc.gov/mix/v20}positiveIntegerType" minOccurs="0"/>
         *                             &lt;element name="tileHeight" type="{http://www.loc.gov/mix/v20}positiveIntegerType" minOccurs="0"/>
         *                           &lt;/sequence>
         *                         &lt;/restriction>
         *                       &lt;/complexContent>
         *                     &lt;/complexType>
         *                   &lt;/element>
         *                   &lt;element name="qualityLayers" type="{http://www.loc.gov/mix/v20}positiveIntegerType" minOccurs="0"/>
         *                   &lt;element name="resolutionLevels" type="{http://www.loc.gov/mix/v20}positiveIntegerType" minOccurs="0"/>
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
        @XmlType(name = "",
                 propOrder = {"codecCompliance",
                              "encodingOptions"})
        public static class JPEG2000 {

            @XmlElement(name = "CodecCompliance")
            protected BasicImageInformationType.SpecialFormatCharacteristics.JPEG2000.CodecCompliance codecCompliance;
            @XmlElement(name = "EncodingOptions")
            protected BasicImageInformationType.SpecialFormatCharacteristics.JPEG2000.EncodingOptions encodingOptions;

            /**
             * Obtient la valeur de la propriété codecCompliance.
             *
             * @return
             *         possible object is
             *         {@link BasicImageInformationType.SpecialFormatCharacteristics.JPEG2000 .CodecCompliance }
             *
             */
            public BasicImageInformationType.SpecialFormatCharacteristics.JPEG2000.CodecCompliance getCodecCompliance() {
                return codecCompliance;
            }

            /**
             * Définit la valeur de la propriété codecCompliance.
             *
             * @param value
             *            allowed object is
             *            {@link BasicImageInformationType.SpecialFormatCharacteristics.JPEG2000 .CodecCompliance }
             *
             */
            public void setCodecCompliance(BasicImageInformationType.SpecialFormatCharacteristics.JPEG2000.CodecCompliance value) {
                this.codecCompliance = value;
            }

            /**
             * Obtient la valeur de la propriété encodingOptions.
             *
             * @return
             *         possible object is
             *         {@link BasicImageInformationType.SpecialFormatCharacteristics.JPEG2000 .EncodingOptions }
             *
             */
            public BasicImageInformationType.SpecialFormatCharacteristics.JPEG2000.EncodingOptions getEncodingOptions() {
                return encodingOptions;
            }

            /**
             * Définit la valeur de la propriété encodingOptions.
             *
             * @param value
             *            allowed object is
             *            {@link BasicImageInformationType.SpecialFormatCharacteristics.JPEG2000 .EncodingOptions }
             *
             */
            public void setEncodingOptions(BasicImageInformationType.SpecialFormatCharacteristics.JPEG2000.EncodingOptions value) {
                this.encodingOptions = value;
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
             *         &lt;element name="codec" type="{http://www.loc.gov/mix/v20}stringType" minOccurs="0"/>
             *         &lt;element name="codecVersion" type="{http://www.loc.gov/mix/v20}stringType" minOccurs="0"/>
             *         &lt;element name="codestreamProfile" type="{http://www.loc.gov/mix/v20}stringType" minOccurs="0"/>
             *         &lt;element name="complianceClass" type="{http://www.loc.gov/mix/v20}stringType" minOccurs="0"/>
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
                     propOrder = {"codec",
                                  "codecVersion",
                                  "codestreamProfile",
                                  "complianceClass"})
            public static class CodecCompliance {

                protected StringType codec;
                protected StringType codecVersion;
                protected StringType codestreamProfile;
                protected StringType complianceClass;

                /**
                 * Obtient la valeur de la propriété codec.
                 *
                 * @return
                 *         possible object is
                 *         {@link StringType }
                 *
                 */
                public StringType getCodec() {
                    return codec;
                }

                /**
                 * Définit la valeur de la propriété codec.
                 *
                 * @param value
                 *            allowed object is
                 *            {@link StringType }
                 *
                 */
                public void setCodec(StringType value) {
                    this.codec = value;
                }

                /**
                 * Obtient la valeur de la propriété codecVersion.
                 *
                 * @return
                 *         possible object is
                 *         {@link StringType }
                 *
                 */
                public StringType getCodecVersion() {
                    return codecVersion;
                }

                /**
                 * Définit la valeur de la propriété codecVersion.
                 *
                 * @param value
                 *            allowed object is
                 *            {@link StringType }
                 *
                 */
                public void setCodecVersion(StringType value) {
                    this.codecVersion = value;
                }

                /**
                 * Obtient la valeur de la propriété codestreamProfile.
                 *
                 * @return
                 *         possible object is
                 *         {@link StringType }
                 *
                 */
                public StringType getCodestreamProfile() {
                    return codestreamProfile;
                }

                /**
                 * Définit la valeur de la propriété codestreamProfile.
                 *
                 * @param value
                 *            allowed object is
                 *            {@link StringType }
                 *
                 */
                public void setCodestreamProfile(StringType value) {
                    this.codestreamProfile = value;
                }

                /**
                 * Obtient la valeur de la propriété complianceClass.
                 *
                 * @return
                 *         possible object is
                 *         {@link StringType }
                 *
                 */
                public StringType getComplianceClass() {
                    return complianceClass;
                }

                /**
                 * Définit la valeur de la propriété complianceClass.
                 *
                 * @param value
                 *            allowed object is
                 *            {@link StringType }
                 *
                 */
                public void setComplianceClass(StringType value) {
                    this.complianceClass = value;
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
             *         &lt;element name="Tiles" minOccurs="0">
             *           &lt;complexType>
             *             &lt;complexContent>
             *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
             *                 &lt;sequence>
             *                   &lt;element name="tileWidth" type="{http://www.loc.gov/mix/v20}positiveIntegerType" minOccurs="0"/>
             *                   &lt;element name="tileHeight" type="{http://www.loc.gov/mix/v20}positiveIntegerType" minOccurs="0"/>
             *                 &lt;/sequence>
             *               &lt;/restriction>
             *             &lt;/complexContent>
             *           &lt;/complexType>
             *         &lt;/element>
             *         &lt;element name="qualityLayers" type="{http://www.loc.gov/mix/v20}positiveIntegerType" minOccurs="0"/>
             *         &lt;element name="resolutionLevels" type="{http://www.loc.gov/mix/v20}positiveIntegerType" minOccurs="0"/>
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
                     propOrder = {"tiles",
                                  "qualityLayers",
                                  "resolutionLevels"})
            public static class EncodingOptions {

                @XmlElement(name = "Tiles")
                protected BasicImageInformationType.SpecialFormatCharacteristics.JPEG2000.EncodingOptions.Tiles tiles;
                protected PositiveIntegerType qualityLayers;
                protected PositiveIntegerType resolutionLevels;

                /**
                 * Obtient la valeur de la propriété tiles.
                 *
                 * @return
                 *         possible object is
                 *         {@link BasicImageInformationType.SpecialFormatCharacteristics.JPEG2000 .EncodingOptions.Tiles }
                 *
                 */
                public BasicImageInformationType.SpecialFormatCharacteristics.JPEG2000.EncodingOptions.Tiles getTiles() {
                    return tiles;
                }

                /**
                 * Définit la valeur de la propriété tiles.
                 *
                 * @param value
                 *            allowed object is
                 *            {@link BasicImageInformationType.SpecialFormatCharacteristics.JPEG2000 .EncodingOptions.Tiles }
                 *
                 */
                public void setTiles(BasicImageInformationType.SpecialFormatCharacteristics.JPEG2000.EncodingOptions.Tiles value) {
                    this.tiles = value;
                }

                /**
                 * Obtient la valeur de la propriété qualityLayers.
                 *
                 * @return
                 *         possible object is
                 *         {@link PositiveIntegerType }
                 *
                 */
                public PositiveIntegerType getQualityLayers() {
                    return qualityLayers;
                }

                /**
                 * Définit la valeur de la propriété qualityLayers.
                 *
                 * @param value
                 *            allowed object is
                 *            {@link PositiveIntegerType }
                 *
                 */
                public void setQualityLayers(PositiveIntegerType value) {
                    this.qualityLayers = value;
                }

                /**
                 * Obtient la valeur de la propriété resolutionLevels.
                 *
                 * @return
                 *         possible object is
                 *         {@link PositiveIntegerType }
                 *
                 */
                public PositiveIntegerType getResolutionLevels() {
                    return resolutionLevels;
                }

                /**
                 * Définit la valeur de la propriété resolutionLevels.
                 *
                 * @param value
                 *            allowed object is
                 *            {@link PositiveIntegerType }
                 *
                 */
                public void setResolutionLevels(PositiveIntegerType value) {
                    this.resolutionLevels = value;
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
                 *         &lt;element name="tileWidth" type="{http://www.loc.gov/mix/v20}positiveIntegerType" minOccurs="0"/>
                 *         &lt;element name="tileHeight" type="{http://www.loc.gov/mix/v20}positiveIntegerType" minOccurs="0"/>
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
                         propOrder = {"tileWidth",
                                      "tileHeight"})
                public static class Tiles {

                    protected PositiveIntegerType tileWidth;
                    protected PositiveIntegerType tileHeight;

                    /**
                     * Obtient la valeur de la propriété tileWidth.
                     *
                     * @return
                     *         possible object is
                     *         {@link PositiveIntegerType }
                     *
                     */
                    public PositiveIntegerType getTileWidth() {
                        return tileWidth;
                    }

                    /**
                     * Définit la valeur de la propriété tileWidth.
                     *
                     * @param value
                     *            allowed object is
                     *            {@link PositiveIntegerType }
                     *
                     */
                    public void setTileWidth(PositiveIntegerType value) {
                        this.tileWidth = value;
                    }

                    /**
                     * Obtient la valeur de la propriété tileHeight.
                     *
                     * @return
                     *         possible object is
                     *         {@link PositiveIntegerType }
                     *
                     */
                    public PositiveIntegerType getTileHeight() {
                        return tileHeight;
                    }

                    /**
                     * Définit la valeur de la propriété tileHeight.
                     *
                     * @param value
                     *            allowed object is
                     *            {@link PositiveIntegerType }
                     *
                     */
                    public void setTileHeight(PositiveIntegerType value) {
                        this.tileHeight = value;
                    }

                }

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
         *         &lt;element name="zoomLevels" type="{http://www.loc.gov/mix/v20}positiveIntegerType" minOccurs="0"/>
         *       &lt;/sequence>
         *     &lt;/restriction>
         *   &lt;/complexContent>
         * &lt;/complexType>
         * </pre>
         *
         *
         */
        @XmlAccessorType(XmlAccessType.FIELD)
        @XmlType(name = "", propOrder = {"zoomLevels"})
        public static class MrSID {

            protected PositiveIntegerType zoomLevels;

            /**
             * Obtient la valeur de la propriété zoomLevels.
             *
             * @return
             *         possible object is
             *         {@link PositiveIntegerType }
             *
             */
            public PositiveIntegerType getZoomLevels() {
                return zoomLevels;
            }

            /**
             * Définit la valeur de la propriété zoomLevels.
             *
             * @param value
             *            allowed object is
             *            {@link PositiveIntegerType }
             *
             */
            public void setZoomLevels(PositiveIntegerType value) {
                this.zoomLevels = value;
            }

        }

    }

}
