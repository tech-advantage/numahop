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
 * Classe Java pour ImageAssessmentMetadataType complex type.
 *
 * <p>
 * Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 *
 * <pre>
 * &lt;complexType name="ImageAssessmentMetadataType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="SpatialMetrics" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="samplingFrequencyPlane" type="{http://www.loc.gov/mix/v20}typeOfSamplingFrequencyPlaneType" minOccurs="0"/>
 *                   &lt;element name="samplingFrequencyUnit" type="{http://www.loc.gov/mix/v20}typeOfSamplingFrequencyUnitType" minOccurs="0"/>
 *                   &lt;element name="xSamplingFrequency" type="{http://www.loc.gov/mix/v20}rationalType" minOccurs="0"/>
 *                   &lt;element name="ySamplingFrequency" type="{http://www.loc.gov/mix/v20}rationalType" minOccurs="0"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="ImageColorEncoding" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="BitsPerSample" minOccurs="0">
 *                     &lt;complexType>
 *                       &lt;complexContent>
 *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                           &lt;sequence>
 *                             &lt;element name="bitsPerSampleValue" type="{http://www.loc.gov/mix/v20}positiveIntegerType" maxOccurs="unbounded" minOccurs="0"/>
 *                             &lt;element name="bitsPerSampleUnit" type="{http://www.loc.gov/mix/v20}typeOfBitsPerSampleUnitType" minOccurs="0"/>
 *                           &lt;/sequence>
 *                         &lt;/restriction>
 *                       &lt;/complexContent>
 *                     &lt;/complexType>
 *                   &lt;/element>
 *                   &lt;element name="samplesPerPixel" type="{http://www.loc.gov/mix/v20}positiveIntegerType" minOccurs="0"/>
 *                   &lt;element name="extraSamples" type="{http://www.loc.gov/mix/v20}typeOfExtraSamplesType" maxOccurs="unbounded" minOccurs="0"/>
 *                   &lt;element name="Colormap" minOccurs="0">
 *                     &lt;complexType>
 *                       &lt;complexContent>
 *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                           &lt;sequence>
 *                             &lt;element name="colormapReference" type="{http://www.loc.gov/mix/v20}URIType" minOccurs="0"/>
 *                             &lt;element name="embeddedColormap" type="{http://www.loc.gov/mix/v20}base64BinaryType" minOccurs="0"/>
 *                           &lt;/sequence>
 *                         &lt;/restriction>
 *                       &lt;/complexContent>
 *                     &lt;/complexType>
 *                   &lt;/element>
 *                   &lt;element name="GrayResponse" minOccurs="0">
 *                     &lt;complexType>
 *                       &lt;complexContent>
 *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                           &lt;sequence>
 *                             &lt;element name="grayResponseCurve" type="{http://www.loc.gov/mix/v20}nonNegativeIntegerType" maxOccurs="unbounded" minOccurs="0"/>
 *                             &lt;element name="grayResponseUnit" type="{http://www.loc.gov/mix/v20}typeOfGrayResponseUnitType" minOccurs="0"/>
 *                           &lt;/sequence>
 *                         &lt;/restriction>
 *                       &lt;/complexContent>
 *                     &lt;/complexType>
 *                   &lt;/element>
 *                   &lt;element name="WhitePoint" maxOccurs="unbounded" minOccurs="0">
 *                     &lt;complexType>
 *                       &lt;complexContent>
 *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                           &lt;sequence>
 *                             &lt;element name="whitePointXValue" type="{http://www.loc.gov/mix/v20}rationalType" minOccurs="0"/>
 *                             &lt;element name="whitePointYValue" type="{http://www.loc.gov/mix/v20}rationalType" minOccurs="0"/>
 *                           &lt;/sequence>
 *                         &lt;/restriction>
 *                       &lt;/complexContent>
 *                     &lt;/complexType>
 *                   &lt;/element>
 *                   &lt;element name="PrimaryChromaticities" maxOccurs="unbounded" minOccurs="0">
 *                     &lt;complexType>
 *                       &lt;complexContent>
 *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                           &lt;sequence>
 *                             &lt;element name="primaryChromaticitiesRedX" type="{http://www.loc.gov/mix/v20}rationalType" minOccurs="0"/>
 *                             &lt;element name="primaryChromaticitiesRedY" type="{http://www.loc.gov/mix/v20}rationalType" minOccurs="0"/>
 *                             &lt;element name="primaryChromaticitiesGreenX" type="{http://www.loc.gov/mix/v20}rationalType" minOccurs="0"/>
 *                             &lt;element name="primaryChromaticitiesGreenY" type="{http://www.loc.gov/mix/v20}rationalType" minOccurs="0"/>
 *                             &lt;element name="primaryChromaticitiesBlueX" type="{http://www.loc.gov/mix/v20}rationalType" minOccurs="0"/>
 *                             &lt;element name="primaryChromaticitiesBlueY" type="{http://www.loc.gov/mix/v20}rationalType" minOccurs="0"/>
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
 *         &lt;element name="TargetData" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="targetType" type="{http://www.loc.gov/mix/v20}typeOfTargetTypeType" maxOccurs="unbounded" minOccurs="0"/>
 *                   &lt;element name="TargetID" maxOccurs="unbounded" minOccurs="0">
 *                     &lt;complexType>
 *                       &lt;complexContent>
 *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                           &lt;sequence>
 *                             &lt;element name="targetManufacturer" type="{http://www.loc.gov/mix/v20}stringType" minOccurs="0"/>
 *                             &lt;element name="targetName" type="{http://www.loc.gov/mix/v20}stringType" minOccurs="0"/>
 *                             &lt;element name="targetNo" type="{http://www.loc.gov/mix/v20}stringType" minOccurs="0"/>
 *                             &lt;element name="targetMedia" type="{http://www.loc.gov/mix/v20}stringType" minOccurs="0"/>
 *                           &lt;/sequence>
 *                         &lt;/restriction>
 *                       &lt;/complexContent>
 *                     &lt;/complexType>
 *                   &lt;/element>
 *                   &lt;element name="externalTarget" type="{http://www.loc.gov/mix/v20}URIType" maxOccurs="unbounded" minOccurs="0"/>
 *                   &lt;element name="performanceData" type="{http://www.loc.gov/mix/v20}URIType" maxOccurs="unbounded" minOccurs="0"/>
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
@XmlType(name = "ImageAssessmentMetadataType",
         propOrder = {"spatialMetrics",
                      "imageColorEncoding",
                      "targetData"})
public class ImageAssessmentMetadataType {

    @XmlElement(name = "SpatialMetrics")
    protected ImageAssessmentMetadataType.SpatialMetrics spatialMetrics;
    @XmlElement(name = "ImageColorEncoding")
    protected ImageAssessmentMetadataType.ImageColorEncoding imageColorEncoding;
    @XmlElement(name = "TargetData")
    protected ImageAssessmentMetadataType.TargetData targetData;

    /**
     * Obtient la valeur de la propriété spatialMetrics.
     *
     * @return
     *         possible object is
     *         {@link ImageAssessmentMetadataType.SpatialMetrics }
     *
     */
    public ImageAssessmentMetadataType.SpatialMetrics getSpatialMetrics() {
        return spatialMetrics;
    }

    /**
     * Définit la valeur de la propriété spatialMetrics.
     *
     * @param value
     *            allowed object is
     *            {@link ImageAssessmentMetadataType.SpatialMetrics }
     *
     */
    public void setSpatialMetrics(ImageAssessmentMetadataType.SpatialMetrics value) {
        this.spatialMetrics = value;
    }

    /**
     * Obtient la valeur de la propriété imageColorEncoding.
     *
     * @return
     *         possible object is
     *         {@link ImageAssessmentMetadataType.ImageColorEncoding }
     *
     */
    public ImageAssessmentMetadataType.ImageColorEncoding getImageColorEncoding() {
        return imageColorEncoding;
    }

    /**
     * Définit la valeur de la propriété imageColorEncoding.
     *
     * @param value
     *            allowed object is
     *            {@link ImageAssessmentMetadataType.ImageColorEncoding }
     *
     */
    public void setImageColorEncoding(ImageAssessmentMetadataType.ImageColorEncoding value) {
        this.imageColorEncoding = value;
    }

    /**
     * Obtient la valeur de la propriété targetData.
     *
     * @return
     *         possible object is
     *         {@link ImageAssessmentMetadataType.TargetData }
     *
     */
    public ImageAssessmentMetadataType.TargetData getTargetData() {
        return targetData;
    }

    /**
     * Définit la valeur de la propriété targetData.
     *
     * @param value
     *            allowed object is
     *            {@link ImageAssessmentMetadataType.TargetData }
     *
     */
    public void setTargetData(ImageAssessmentMetadataType.TargetData value) {
        this.targetData = value;
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
     *         &lt;element name="BitsPerSample" minOccurs="0">
     *           &lt;complexType>
     *             &lt;complexContent>
     *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *                 &lt;sequence>
     *                   &lt;element name="bitsPerSampleValue" type="{http://www.loc.gov/mix/v20}positiveIntegerType" maxOccurs="unbounded" minOccurs="0"/>
     *                   &lt;element name="bitsPerSampleUnit" type="{http://www.loc.gov/mix/v20}typeOfBitsPerSampleUnitType" minOccurs="0"/>
     *                 &lt;/sequence>
     *               &lt;/restriction>
     *             &lt;/complexContent>
     *           &lt;/complexType>
     *         &lt;/element>
     *         &lt;element name="samplesPerPixel" type="{http://www.loc.gov/mix/v20}positiveIntegerType" minOccurs="0"/>
     *         &lt;element name="extraSamples" type="{http://www.loc.gov/mix/v20}typeOfExtraSamplesType" maxOccurs="unbounded" minOccurs="0"/>
     *         &lt;element name="Colormap" minOccurs="0">
     *           &lt;complexType>
     *             &lt;complexContent>
     *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *                 &lt;sequence>
     *                   &lt;element name="colormapReference" type="{http://www.loc.gov/mix/v20}URIType" minOccurs="0"/>
     *                   &lt;element name="embeddedColormap" type="{http://www.loc.gov/mix/v20}base64BinaryType" minOccurs="0"/>
     *                 &lt;/sequence>
     *               &lt;/restriction>
     *             &lt;/complexContent>
     *           &lt;/complexType>
     *         &lt;/element>
     *         &lt;element name="GrayResponse" minOccurs="0">
     *           &lt;complexType>
     *             &lt;complexContent>
     *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *                 &lt;sequence>
     *                   &lt;element name="grayResponseCurve" type="{http://www.loc.gov/mix/v20}nonNegativeIntegerType" maxOccurs="unbounded" minOccurs="0"/>
     *                   &lt;element name="grayResponseUnit" type="{http://www.loc.gov/mix/v20}typeOfGrayResponseUnitType" minOccurs="0"/>
     *                 &lt;/sequence>
     *               &lt;/restriction>
     *             &lt;/complexContent>
     *           &lt;/complexType>
     *         &lt;/element>
     *         &lt;element name="WhitePoint" maxOccurs="unbounded" minOccurs="0">
     *           &lt;complexType>
     *             &lt;complexContent>
     *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *                 &lt;sequence>
     *                   &lt;element name="whitePointXValue" type="{http://www.loc.gov/mix/v20}rationalType" minOccurs="0"/>
     *                   &lt;element name="whitePointYValue" type="{http://www.loc.gov/mix/v20}rationalType" minOccurs="0"/>
     *                 &lt;/sequence>
     *               &lt;/restriction>
     *             &lt;/complexContent>
     *           &lt;/complexType>
     *         &lt;/element>
     *         &lt;element name="PrimaryChromaticities" maxOccurs="unbounded" minOccurs="0">
     *           &lt;complexType>
     *             &lt;complexContent>
     *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *                 &lt;sequence>
     *                   &lt;element name="primaryChromaticitiesRedX" type="{http://www.loc.gov/mix/v20}rationalType" minOccurs="0"/>
     *                   &lt;element name="primaryChromaticitiesRedY" type="{http://www.loc.gov/mix/v20}rationalType" minOccurs="0"/>
     *                   &lt;element name="primaryChromaticitiesGreenX" type="{http://www.loc.gov/mix/v20}rationalType" minOccurs="0"/>
     *                   &lt;element name="primaryChromaticitiesGreenY" type="{http://www.loc.gov/mix/v20}rationalType" minOccurs="0"/>
     *                   &lt;element name="primaryChromaticitiesBlueX" type="{http://www.loc.gov/mix/v20}rationalType" minOccurs="0"/>
     *                   &lt;element name="primaryChromaticitiesBlueY" type="{http://www.loc.gov/mix/v20}rationalType" minOccurs="0"/>
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
             propOrder = {"bitsPerSample",
                          "samplesPerPixel",
                          "extraSamples",
                          "colormap",
                          "grayResponse",
                          "whitePoint",
                          "primaryChromaticities"})
    public static class ImageColorEncoding {

        @XmlElement(name = "BitsPerSample")
        protected ImageAssessmentMetadataType.ImageColorEncoding.BitsPerSample bitsPerSample;
        protected PositiveIntegerType samplesPerPixel;
        protected List<TypeOfExtraSamplesType> extraSamples;
        @XmlElement(name = "Colormap")
        protected ImageAssessmentMetadataType.ImageColorEncoding.Colormap colormap;
        @XmlElement(name = "GrayResponse")
        protected ImageAssessmentMetadataType.ImageColorEncoding.GrayResponse grayResponse;
        @XmlElement(name = "WhitePoint")
        protected List<ImageAssessmentMetadataType.ImageColorEncoding.WhitePoint> whitePoint;
        @XmlElement(name = "PrimaryChromaticities")
        protected List<ImageAssessmentMetadataType.ImageColorEncoding.PrimaryChromaticities> primaryChromaticities;

        /**
         * Obtient la valeur de la propriété bitsPerSample.
         *
         * @return
         *         possible object is
         *         {@link ImageAssessmentMetadataType.ImageColorEncoding.BitsPerSample }
         *
         */
        public ImageAssessmentMetadataType.ImageColorEncoding.BitsPerSample getBitsPerSample() {
            return bitsPerSample;
        }

        /**
         * Définit la valeur de la propriété bitsPerSample.
         *
         * @param value
         *            allowed object is
         *            {@link ImageAssessmentMetadataType.ImageColorEncoding.BitsPerSample }
         *
         */
        public void setBitsPerSample(ImageAssessmentMetadataType.ImageColorEncoding.BitsPerSample value) {
            this.bitsPerSample = value;
        }

        /**
         * Obtient la valeur de la propriété samplesPerPixel.
         *
         * @return
         *         possible object is
         *         {@link PositiveIntegerType }
         *
         */
        public PositiveIntegerType getSamplesPerPixel() {
            return samplesPerPixel;
        }

        /**
         * Définit la valeur de la propriété samplesPerPixel.
         *
         * @param value
         *            allowed object is
         *            {@link PositiveIntegerType }
         *
         */
        public void setSamplesPerPixel(PositiveIntegerType value) {
            this.samplesPerPixel = value;
        }

        /**
         * Gets the value of the extraSamples property.
         *
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the extraSamples property.
         *
         * <p>
         * For example, to add a new item, do as follows:
         *
         * <pre>
         * getExtraSamples().add(newItem);
         * </pre>
         *
         *
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link TypeOfExtraSamplesType }
         *
         *
         */
        public List<TypeOfExtraSamplesType> getExtraSamples() {
            if (extraSamples == null) {
                extraSamples = new ArrayList<>();
            }
            return this.extraSamples;
        }

        /**
         * Obtient la valeur de la propriété colormap.
         *
         * @return
         *         possible object is
         *         {@link ImageAssessmentMetadataType.ImageColorEncoding.Colormap }
         *
         */
        public ImageAssessmentMetadataType.ImageColorEncoding.Colormap getColormap() {
            return colormap;
        }

        /**
         * Définit la valeur de la propriété colormap.
         *
         * @param value
         *            allowed object is
         *            {@link ImageAssessmentMetadataType.ImageColorEncoding.Colormap }
         *
         */
        public void setColormap(ImageAssessmentMetadataType.ImageColorEncoding.Colormap value) {
            this.colormap = value;
        }

        /**
         * Obtient la valeur de la propriété grayResponse.
         *
         * @return
         *         possible object is
         *         {@link ImageAssessmentMetadataType.ImageColorEncoding.GrayResponse }
         *
         */
        public ImageAssessmentMetadataType.ImageColorEncoding.GrayResponse getGrayResponse() {
            return grayResponse;
        }

        /**
         * Définit la valeur de la propriété grayResponse.
         *
         * @param value
         *            allowed object is
         *            {@link ImageAssessmentMetadataType.ImageColorEncoding.GrayResponse }
         *
         */
        public void setGrayResponse(ImageAssessmentMetadataType.ImageColorEncoding.GrayResponse value) {
            this.grayResponse = value;
        }

        /**
         * Gets the value of the whitePoint property.
         *
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the whitePoint property.
         *
         * <p>
         * For example, to add a new item, do as follows:
         *
         * <pre>
         * getWhitePoint().add(newItem);
         * </pre>
         *
         *
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link ImageAssessmentMetadataType.ImageColorEncoding.WhitePoint }
         *
         *
         */
        public List<ImageAssessmentMetadataType.ImageColorEncoding.WhitePoint> getWhitePoint() {
            if (whitePoint == null) {
                whitePoint = new ArrayList<>();
            }
            return this.whitePoint;
        }

        /**
         * Gets the value of the primaryChromaticities property.
         *
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the primaryChromaticities property.
         *
         * <p>
         * For example, to add a new item, do as follows:
         *
         * <pre>
         * getPrimaryChromaticities().add(newItem);
         * </pre>
         *
         *
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link ImageAssessmentMetadataType.ImageColorEncoding.PrimaryChromaticities }
         *
         *
         */
        public List<ImageAssessmentMetadataType.ImageColorEncoding.PrimaryChromaticities> getPrimaryChromaticities() {
            if (primaryChromaticities == null) {
                primaryChromaticities = new ArrayList<>();
            }
            return this.primaryChromaticities;
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
         *         &lt;element name="bitsPerSampleValue" type="{http://www.loc.gov/mix/v20}positiveIntegerType" maxOccurs="unbounded" minOccurs="0"/>
         *         &lt;element name="bitsPerSampleUnit" type="{http://www.loc.gov/mix/v20}typeOfBitsPerSampleUnitType" minOccurs="0"/>
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
                 propOrder = {"bitsPerSampleValue",
                              "bitsPerSampleUnit"})
        public static class BitsPerSample {

            protected List<PositiveIntegerType> bitsPerSampleValue;
            protected TypeOfBitsPerSampleUnitType bitsPerSampleUnit;

            /**
             * Gets the value of the bitsPerSampleValue property.
             *
             * <p>
             * This accessor method returns a reference to the live list,
             * not a snapshot. Therefore any modification you make to the
             * returned list will be present inside the JAXB object.
             * This is why there is not a <CODE>set</CODE> method for the bitsPerSampleValue property.
             *
             * <p>
             * For example, to add a new item, do as follows:
             *
             * <pre>
             * getBitsPerSampleValue().add(newItem);
             * </pre>
             *
             *
             * <p>
             * Objects of the following type(s) are allowed in the list
             * {@link PositiveIntegerType }
             *
             *
             */
            public List<PositiveIntegerType> getBitsPerSampleValue() {
                if (bitsPerSampleValue == null) {
                    bitsPerSampleValue = new ArrayList<>();
                }
                return this.bitsPerSampleValue;
            }

            /**
             * Obtient la valeur de la propriété bitsPerSampleUnit.
             *
             * @return
             *         possible object is
             *         {@link TypeOfBitsPerSampleUnitType }
             *
             */
            public TypeOfBitsPerSampleUnitType getBitsPerSampleUnit() {
                return bitsPerSampleUnit;
            }

            /**
             * Définit la valeur de la propriété bitsPerSampleUnit.
             *
             * @param value
             *            allowed object is
             *            {@link TypeOfBitsPerSampleUnitType }
             *
             */
            public void setBitsPerSampleUnit(TypeOfBitsPerSampleUnitType value) {
                this.bitsPerSampleUnit = value;
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
         *         &lt;element name="colormapReference" type="{http://www.loc.gov/mix/v20}URIType" minOccurs="0"/>
         *         &lt;element name="embeddedColormap" type="{http://www.loc.gov/mix/v20}base64BinaryType" minOccurs="0"/>
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
                 propOrder = {"colormapReference",
                              "embeddedColormap"})
        public static class Colormap {

            protected URIType colormapReference;
            protected Base64BinaryType embeddedColormap;

            /**
             * Obtient la valeur de la propriété colormapReference.
             *
             * @return
             *         possible object is
             *         {@link URIType }
             *
             */
            public URIType getColormapReference() {
                return colormapReference;
            }

            /**
             * Définit la valeur de la propriété colormapReference.
             *
             * @param value
             *            allowed object is
             *            {@link URIType }
             *
             */
            public void setColormapReference(URIType value) {
                this.colormapReference = value;
            }

            /**
             * Obtient la valeur de la propriété embeddedColormap.
             *
             * @return
             *         possible object is
             *         {@link Base64BinaryType }
             *
             */
            public Base64BinaryType getEmbeddedColormap() {
                return embeddedColormap;
            }

            /**
             * Définit la valeur de la propriété embeddedColormap.
             *
             * @param value
             *            allowed object is
             *            {@link Base64BinaryType }
             *
             */
            public void setEmbeddedColormap(Base64BinaryType value) {
                this.embeddedColormap = value;
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
         *         &lt;element name="grayResponseCurve" type="{http://www.loc.gov/mix/v20}nonNegativeIntegerType" maxOccurs="unbounded" minOccurs="0"/>
         *         &lt;element name="grayResponseUnit" type="{http://www.loc.gov/mix/v20}typeOfGrayResponseUnitType" minOccurs="0"/>
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
                 propOrder = {"grayResponseCurve",
                              "grayResponseUnit"})
        public static class GrayResponse {

            protected List<NonNegativeIntegerType> grayResponseCurve;
            protected TypeOfGrayResponseUnitType grayResponseUnit;

            /**
             * Gets the value of the grayResponseCurve property.
             *
             * <p>
             * This accessor method returns a reference to the live list,
             * not a snapshot. Therefore any modification you make to the
             * returned list will be present inside the JAXB object.
             * This is why there is not a <CODE>set</CODE> method for the grayResponseCurve property.
             *
             * <p>
             * For example, to add a new item, do as follows:
             *
             * <pre>
             * getGrayResponseCurve().add(newItem);
             * </pre>
             *
             *
             * <p>
             * Objects of the following type(s) are allowed in the list
             * {@link NonNegativeIntegerType }
             *
             *
             */
            public List<NonNegativeIntegerType> getGrayResponseCurve() {
                if (grayResponseCurve == null) {
                    grayResponseCurve = new ArrayList<>();
                }
                return this.grayResponseCurve;
            }

            /**
             * Obtient la valeur de la propriété grayResponseUnit.
             *
             * @return
             *         possible object is
             *         {@link TypeOfGrayResponseUnitType }
             *
             */
            public TypeOfGrayResponseUnitType getGrayResponseUnit() {
                return grayResponseUnit;
            }

            /**
             * Définit la valeur de la propriété grayResponseUnit.
             *
             * @param value
             *            allowed object is
             *            {@link TypeOfGrayResponseUnitType }
             *
             */
            public void setGrayResponseUnit(TypeOfGrayResponseUnitType value) {
                this.grayResponseUnit = value;
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
         *         &lt;element name="primaryChromaticitiesRedX" type="{http://www.loc.gov/mix/v20}rationalType" minOccurs="0"/>
         *         &lt;element name="primaryChromaticitiesRedY" type="{http://www.loc.gov/mix/v20}rationalType" minOccurs="0"/>
         *         &lt;element name="primaryChromaticitiesGreenX" type="{http://www.loc.gov/mix/v20}rationalType" minOccurs="0"/>
         *         &lt;element name="primaryChromaticitiesGreenY" type="{http://www.loc.gov/mix/v20}rationalType" minOccurs="0"/>
         *         &lt;element name="primaryChromaticitiesBlueX" type="{http://www.loc.gov/mix/v20}rationalType" minOccurs="0"/>
         *         &lt;element name="primaryChromaticitiesBlueY" type="{http://www.loc.gov/mix/v20}rationalType" minOccurs="0"/>
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
                 propOrder = {"primaryChromaticitiesRedX",
                              "primaryChromaticitiesRedY",
                              "primaryChromaticitiesGreenX",
                              "primaryChromaticitiesGreenY",
                              "primaryChromaticitiesBlueX",
                              "primaryChromaticitiesBlueY"})
        public static class PrimaryChromaticities {

            protected RationalType primaryChromaticitiesRedX;
            protected RationalType primaryChromaticitiesRedY;
            protected RationalType primaryChromaticitiesGreenX;
            protected RationalType primaryChromaticitiesGreenY;
            protected RationalType primaryChromaticitiesBlueX;
            protected RationalType primaryChromaticitiesBlueY;

            /**
             * Obtient la valeur de la propriété primaryChromaticitiesRedX.
             *
             * @return
             *         possible object is
             *         {@link RationalType }
             *
             */
            public RationalType getPrimaryChromaticitiesRedX() {
                return primaryChromaticitiesRedX;
            }

            /**
             * Définit la valeur de la propriété primaryChromaticitiesRedX.
             *
             * @param value
             *            allowed object is
             *            {@link RationalType }
             *
             */
            public void setPrimaryChromaticitiesRedX(RationalType value) {
                this.primaryChromaticitiesRedX = value;
            }

            /**
             * Obtient la valeur de la propriété primaryChromaticitiesRedY.
             *
             * @return
             *         possible object is
             *         {@link RationalType }
             *
             */
            public RationalType getPrimaryChromaticitiesRedY() {
                return primaryChromaticitiesRedY;
            }

            /**
             * Définit la valeur de la propriété primaryChromaticitiesRedY.
             *
             * @param value
             *            allowed object is
             *            {@link RationalType }
             *
             */
            public void setPrimaryChromaticitiesRedY(RationalType value) {
                this.primaryChromaticitiesRedY = value;
            }

            /**
             * Obtient la valeur de la propriété primaryChromaticitiesGreenX.
             *
             * @return
             *         possible object is
             *         {@link RationalType }
             *
             */
            public RationalType getPrimaryChromaticitiesGreenX() {
                return primaryChromaticitiesGreenX;
            }

            /**
             * Définit la valeur de la propriété primaryChromaticitiesGreenX.
             *
             * @param value
             *            allowed object is
             *            {@link RationalType }
             *
             */
            public void setPrimaryChromaticitiesGreenX(RationalType value) {
                this.primaryChromaticitiesGreenX = value;
            }

            /**
             * Obtient la valeur de la propriété primaryChromaticitiesGreenY.
             *
             * @return
             *         possible object is
             *         {@link RationalType }
             *
             */
            public RationalType getPrimaryChromaticitiesGreenY() {
                return primaryChromaticitiesGreenY;
            }

            /**
             * Définit la valeur de la propriété primaryChromaticitiesGreenY.
             *
             * @param value
             *            allowed object is
             *            {@link RationalType }
             *
             */
            public void setPrimaryChromaticitiesGreenY(RationalType value) {
                this.primaryChromaticitiesGreenY = value;
            }

            /**
             * Obtient la valeur de la propriété primaryChromaticitiesBlueX.
             *
             * @return
             *         possible object is
             *         {@link RationalType }
             *
             */
            public RationalType getPrimaryChromaticitiesBlueX() {
                return primaryChromaticitiesBlueX;
            }

            /**
             * Définit la valeur de la propriété primaryChromaticitiesBlueX.
             *
             * @param value
             *            allowed object is
             *            {@link RationalType }
             *
             */
            public void setPrimaryChromaticitiesBlueX(RationalType value) {
                this.primaryChromaticitiesBlueX = value;
            }

            /**
             * Obtient la valeur de la propriété primaryChromaticitiesBlueY.
             *
             * @return
             *         possible object is
             *         {@link RationalType }
             *
             */
            public RationalType getPrimaryChromaticitiesBlueY() {
                return primaryChromaticitiesBlueY;
            }

            /**
             * Définit la valeur de la propriété primaryChromaticitiesBlueY.
             *
             * @param value
             *            allowed object is
             *            {@link RationalType }
             *
             */
            public void setPrimaryChromaticitiesBlueY(RationalType value) {
                this.primaryChromaticitiesBlueY = value;
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
         *         &lt;element name="whitePointXValue" type="{http://www.loc.gov/mix/v20}rationalType" minOccurs="0"/>
         *         &lt;element name="whitePointYValue" type="{http://www.loc.gov/mix/v20}rationalType" minOccurs="0"/>
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
                 propOrder = {"whitePointXValue",
                              "whitePointYValue"})
        public static class WhitePoint {

            protected RationalType whitePointXValue;
            protected RationalType whitePointYValue;

            /**
             * Obtient la valeur de la propriété whitePointXValue.
             *
             * @return
             *         possible object is
             *         {@link RationalType }
             *
             */
            public RationalType getWhitePointXValue() {
                return whitePointXValue;
            }

            /**
             * Définit la valeur de la propriété whitePointXValue.
             *
             * @param value
             *            allowed object is
             *            {@link RationalType }
             *
             */
            public void setWhitePointXValue(RationalType value) {
                this.whitePointXValue = value;
            }

            /**
             * Obtient la valeur de la propriété whitePointYValue.
             *
             * @return
             *         possible object is
             *         {@link RationalType }
             *
             */
            public RationalType getWhitePointYValue() {
                return whitePointYValue;
            }

            /**
             * Définit la valeur de la propriété whitePointYValue.
             *
             * @param value
             *            allowed object is
             *            {@link RationalType }
             *
             */
            public void setWhitePointYValue(RationalType value) {
                this.whitePointYValue = value;
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
     *         &lt;element name="samplingFrequencyPlane" type="{http://www.loc.gov/mix/v20}typeOfSamplingFrequencyPlaneType" minOccurs="0"/>
     *         &lt;element name="samplingFrequencyUnit" type="{http://www.loc.gov/mix/v20}typeOfSamplingFrequencyUnitType" minOccurs="0"/>
     *         &lt;element name="xSamplingFrequency" type="{http://www.loc.gov/mix/v20}rationalType" minOccurs="0"/>
     *         &lt;element name="ySamplingFrequency" type="{http://www.loc.gov/mix/v20}rationalType" minOccurs="0"/>
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
             propOrder = {"samplingFrequencyPlane",
                          "samplingFrequencyUnit",
                          "xSamplingFrequency",
                          "ySamplingFrequency"})
    public static class SpatialMetrics {

        protected TypeOfSamplingFrequencyPlaneType samplingFrequencyPlane;
        protected TypeOfSamplingFrequencyUnitType samplingFrequencyUnit;
        protected RationalType xSamplingFrequency;
        protected RationalType ySamplingFrequency;

        /**
         * Obtient la valeur de la propriété samplingFrequencyPlane.
         *
         * @return
         *         possible object is
         *         {@link TypeOfSamplingFrequencyPlaneType }
         *
         */
        public TypeOfSamplingFrequencyPlaneType getSamplingFrequencyPlane() {
            return samplingFrequencyPlane;
        }

        /**
         * Définit la valeur de la propriété samplingFrequencyPlane.
         *
         * @param value
         *            allowed object is
         *            {@link TypeOfSamplingFrequencyPlaneType }
         *
         */
        public void setSamplingFrequencyPlane(TypeOfSamplingFrequencyPlaneType value) {
            this.samplingFrequencyPlane = value;
        }

        /**
         * Obtient la valeur de la propriété samplingFrequencyUnit.
         *
         * @return
         *         possible object is
         *         {@link TypeOfSamplingFrequencyUnitType }
         *
         */
        public TypeOfSamplingFrequencyUnitType getSamplingFrequencyUnit() {
            return samplingFrequencyUnit;
        }

        /**
         * Définit la valeur de la propriété samplingFrequencyUnit.
         *
         * @param value
         *            allowed object is
         *            {@link TypeOfSamplingFrequencyUnitType }
         *
         */
        public void setSamplingFrequencyUnit(TypeOfSamplingFrequencyUnitType value) {
            this.samplingFrequencyUnit = value;
        }

        /**
         * Obtient la valeur de la propriété xSamplingFrequency.
         *
         * @return
         *         possible object is
         *         {@link RationalType }
         *
         */
        public RationalType getXSamplingFrequency() {
            return xSamplingFrequency;
        }

        /**
         * Définit la valeur de la propriété xSamplingFrequency.
         *
         * @param value
         *            allowed object is
         *            {@link RationalType }
         *
         */
        public void setXSamplingFrequency(RationalType value) {
            this.xSamplingFrequency = value;
        }

        /**
         * Obtient la valeur de la propriété ySamplingFrequency.
         *
         * @return
         *         possible object is
         *         {@link RationalType }
         *
         */
        public RationalType getYSamplingFrequency() {
            return ySamplingFrequency;
        }

        /**
         * Définit la valeur de la propriété ySamplingFrequency.
         *
         * @param value
         *            allowed object is
         *            {@link RationalType }
         *
         */
        public void setYSamplingFrequency(RationalType value) {
            this.ySamplingFrequency = value;
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
     *         &lt;element name="targetType" type="{http://www.loc.gov/mix/v20}typeOfTargetTypeType" maxOccurs="unbounded" minOccurs="0"/>
     *         &lt;element name="TargetID" maxOccurs="unbounded" minOccurs="0">
     *           &lt;complexType>
     *             &lt;complexContent>
     *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *                 &lt;sequence>
     *                   &lt;element name="targetManufacturer" type="{http://www.loc.gov/mix/v20}stringType" minOccurs="0"/>
     *                   &lt;element name="targetName" type="{http://www.loc.gov/mix/v20}stringType" minOccurs="0"/>
     *                   &lt;element name="targetNo" type="{http://www.loc.gov/mix/v20}stringType" minOccurs="0"/>
     *                   &lt;element name="targetMedia" type="{http://www.loc.gov/mix/v20}stringType" minOccurs="0"/>
     *                 &lt;/sequence>
     *               &lt;/restriction>
     *             &lt;/complexContent>
     *           &lt;/complexType>
     *         &lt;/element>
     *         &lt;element name="externalTarget" type="{http://www.loc.gov/mix/v20}URIType" maxOccurs="unbounded" minOccurs="0"/>
     *         &lt;element name="performanceData" type="{http://www.loc.gov/mix/v20}URIType" maxOccurs="unbounded" minOccurs="0"/>
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
             propOrder = {"targetType",
                          "targetID",
                          "externalTarget",
                          "performanceData"})
    public static class TargetData {

        protected List<TypeOfTargetTypeType> targetType;
        @XmlElement(name = "TargetID")
        protected List<ImageAssessmentMetadataType.TargetData.TargetID> targetID;
        protected List<URIType> externalTarget;
        protected List<URIType> performanceData;

        /**
         * Gets the value of the targetType property.
         *
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the targetType property.
         *
         * <p>
         * For example, to add a new item, do as follows:
         *
         * <pre>
         * getTargetType().add(newItem);
         * </pre>
         *
         *
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link TypeOfTargetTypeType }
         *
         *
         */
        public List<TypeOfTargetTypeType> getTargetType() {
            if (targetType == null) {
                targetType = new ArrayList<>();
            }
            return this.targetType;
        }

        /**
         * Gets the value of the targetID property.
         *
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the targetID property.
         *
         * <p>
         * For example, to add a new item, do as follows:
         *
         * <pre>
         * getTargetID().add(newItem);
         * </pre>
         *
         *
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link ImageAssessmentMetadataType.TargetData.TargetID }
         *
         *
         */
        public List<ImageAssessmentMetadataType.TargetData.TargetID> getTargetID() {
            if (targetID == null) {
                targetID = new ArrayList<>();
            }
            return this.targetID;
        }

        /**
         * Gets the value of the externalTarget property.
         *
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the externalTarget property.
         *
         * <p>
         * For example, to add a new item, do as follows:
         *
         * <pre>
         * getExternalTarget().add(newItem);
         * </pre>
         *
         *
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link URIType }
         *
         *
         */
        public List<URIType> getExternalTarget() {
            if (externalTarget == null) {
                externalTarget = new ArrayList<>();
            }
            return this.externalTarget;
        }

        /**
         * Gets the value of the performanceData property.
         *
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the performanceData property.
         *
         * <p>
         * For example, to add a new item, do as follows:
         *
         * <pre>
         * getPerformanceData().add(newItem);
         * </pre>
         *
         *
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link URIType }
         *
         *
         */
        public List<URIType> getPerformanceData() {
            if (performanceData == null) {
                performanceData = new ArrayList<>();
            }
            return this.performanceData;
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
         *         &lt;element name="targetManufacturer" type="{http://www.loc.gov/mix/v20}stringType" minOccurs="0"/>
         *         &lt;element name="targetName" type="{http://www.loc.gov/mix/v20}stringType" minOccurs="0"/>
         *         &lt;element name="targetNo" type="{http://www.loc.gov/mix/v20}stringType" minOccurs="0"/>
         *         &lt;element name="targetMedia" type="{http://www.loc.gov/mix/v20}stringType" minOccurs="0"/>
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
                 propOrder = {"targetManufacturer",
                              "targetName",
                              "targetNo",
                              "targetMedia"})
        public static class TargetID {

            protected StringType targetManufacturer;
            protected StringType targetName;
            protected StringType targetNo;
            protected StringType targetMedia;

            /**
             * Obtient la valeur de la propriété targetManufacturer.
             *
             * @return
             *         possible object is
             *         {@link StringType }
             *
             */
            public StringType getTargetManufacturer() {
                return targetManufacturer;
            }

            /**
             * Définit la valeur de la propriété targetManufacturer.
             *
             * @param value
             *            allowed object is
             *            {@link StringType }
             *
             */
            public void setTargetManufacturer(StringType value) {
                this.targetManufacturer = value;
            }

            /**
             * Obtient la valeur de la propriété targetName.
             *
             * @return
             *         possible object is
             *         {@link StringType }
             *
             */
            public StringType getTargetName() {
                return targetName;
            }

            /**
             * Définit la valeur de la propriété targetName.
             *
             * @param value
             *            allowed object is
             *            {@link StringType }
             *
             */
            public void setTargetName(StringType value) {
                this.targetName = value;
            }

            /**
             * Obtient la valeur de la propriété targetNo.
             *
             * @return
             *         possible object is
             *         {@link StringType }
             *
             */
            public StringType getTargetNo() {
                return targetNo;
            }

            /**
             * Définit la valeur de la propriété targetNo.
             *
             * @param value
             *            allowed object is
             *            {@link StringType }
             *
             */
            public void setTargetNo(StringType value) {
                this.targetNo = value;
            }

            /**
             * Obtient la valeur de la propriété targetMedia.
             *
             * @return
             *         possible object is
             *         {@link StringType }
             *
             */
            public StringType getTargetMedia() {
                return targetMedia;
            }

            /**
             * Définit la valeur de la propriété targetMedia.
             *
             * @param value
             *            allowed object is
             *            {@link StringType }
             *
             */
            public void setTargetMedia(StringType value) {
                this.targetMedia = value;
            }

        }

    }

}
