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
 * Classe Java pour ImageCaptureMetadataType complex type.
 *
 * <p>
 * Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 *
 * <pre>
 * &lt;complexType name="ImageCaptureMetadataType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="SourceInformation" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="sourceType" type="{http://www.loc.gov/mix/v20}stringType" minOccurs="0"/>
 *                   &lt;element name="SourceID" maxOccurs="unbounded" minOccurs="0">
 *                     &lt;complexType>
 *                       &lt;complexContent>
 *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                           &lt;sequence>
 *                             &lt;element name="sourceIDType" type="{http://www.loc.gov/mix/v20}stringType" minOccurs="0"/>
 *                             &lt;element name="sourceIDValue" type="{http://www.loc.gov/mix/v20}stringType" minOccurs="0"/>
 *                           &lt;/sequence>
 *                         &lt;/restriction>
 *                       &lt;/complexContent>
 *                     &lt;/complexType>
 *                   &lt;/element>
 *                   &lt;element name="SourceSize" minOccurs="0">
 *                     &lt;complexType>
 *                       &lt;complexContent>
 *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                           &lt;sequence>
 *                             &lt;element name="SourceXDimension" minOccurs="0">
 *                               &lt;complexType>
 *                                 &lt;complexContent>
 *                                   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                                     &lt;sequence>
 *                                       &lt;element name="sourceXDimensionValue" type="{http://www.loc.gov/mix/v20}typeOfNonNegativeRealType" minOccurs="0"/>
 *                                       &lt;element name="sourceXDimensionUnit" type="{http://www.loc.gov/mix/v20}typeOfsourceDimensionUnitType" minOccurs="0"/>
 *                                     &lt;/sequence>
 *                                   &lt;/restriction>
 *                                 &lt;/complexContent>
 *                               &lt;/complexType>
 *                             &lt;/element>
 *                             &lt;element name="SourceYDimension" minOccurs="0">
 *                               &lt;complexType>
 *                                 &lt;complexContent>
 *                                   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                                     &lt;sequence>
 *                                       &lt;element name="sourceYDimensionValue" type="{http://www.loc.gov/mix/v20}typeOfNonNegativeRealType" minOccurs="0"/>
 *                                       &lt;element name="sourceYDimensionUnit" type="{http://www.loc.gov/mix/v20}typeOfsourceDimensionUnitType" minOccurs="0"/>
 *                                     &lt;/sequence>
 *                                   &lt;/restriction>
 *                                 &lt;/complexContent>
 *                               &lt;/complexType>
 *                             &lt;/element>
 *                             &lt;element name="SourceZDimension" minOccurs="0">
 *                               &lt;complexType>
 *                                 &lt;complexContent>
 *                                   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                                     &lt;sequence>
 *                                       &lt;element name="sourceZDimensionValue" type="{http://www.loc.gov/mix/v20}typeOfNonNegativeRealType" minOccurs="0"/>
 *                                       &lt;element name="sourceZDimensionUnit" type="{http://www.loc.gov/mix/v20}typeOfsourceDimensionUnitType" minOccurs="0"/>
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
 *         &lt;element name="GeneralCaptureInformation" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="dateTimeCreated" type="{http://www.loc.gov/mix/v20}typeOfDateType" minOccurs="0"/>
 *                   &lt;element name="imageProducer" type="{http://www.loc.gov/mix/v20}stringType" maxOccurs="unbounded" minOccurs="0"/>
 *                   &lt;element name="captureDevice" type="{http://www.loc.gov/mix/v20}typeOfCaptureDeviceType" minOccurs="0"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="ScannerCapture" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="scannerManufacturer" type="{http://www.loc.gov/mix/v20}stringType" minOccurs="0"/>
 *                   &lt;element name="ScannerModel" minOccurs="0">
 *                     &lt;complexType>
 *                       &lt;complexContent>
 *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                           &lt;sequence>
 *                             &lt;element name="scannerModelName" type="{http://www.loc.gov/mix/v20}stringType" minOccurs="0"/>
 *                             &lt;element name="scannerModelNumber" type="{http://www.loc.gov/mix/v20}stringType" minOccurs="0"/>
 *                             &lt;element name="scannerModelSerialNo" type="{http://www.loc.gov/mix/v20}stringType" minOccurs="0"/>
 *                           &lt;/sequence>
 *                         &lt;/restriction>
 *                       &lt;/complexContent>
 *                     &lt;/complexType>
 *                   &lt;/element>
 *                   &lt;element name="MaximumOpticalResolution" minOccurs="0">
 *                     &lt;complexType>
 *                       &lt;complexContent>
 *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                           &lt;sequence>
 *                             &lt;element name="xOpticalResolution" type="{http://www.loc.gov/mix/v20}positiveIntegerType" minOccurs="0"/>
 *                             &lt;element name="yOpticalResolution" type="{http://www.loc.gov/mix/v20}positiveIntegerType" minOccurs="0"/>
 *                             &lt;element name="opticalResolutionUnit" type="{http://www.loc.gov/mix/v20}typeOfOpticalResolutionUnitType" minOccurs="0"/>
 *                           &lt;/sequence>
 *                         &lt;/restriction>
 *                       &lt;/complexContent>
 *                     &lt;/complexType>
 *                   &lt;/element>
 *                   &lt;element name="scannerSensor" type="{http://www.loc.gov/mix/v20}typeOfScannerSensorType" minOccurs="0"/>
 *                   &lt;element name="ScanningSystemSoftware" minOccurs="0">
 *                     &lt;complexType>
 *                       &lt;complexContent>
 *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                           &lt;sequence>
 *                             &lt;element name="scanningSoftwareName" type="{http://www.loc.gov/mix/v20}stringType" minOccurs="0"/>
 *                             &lt;element name="scanningSoftwareVersionNo" type="{http://www.loc.gov/mix/v20}stringType" minOccurs="0"/>
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
 *         &lt;element name="DigitalCameraCapture" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="digitalCameraManufacturer" type="{http://www.loc.gov/mix/v20}stringType" minOccurs="0"/>
 *                   &lt;element name="DigitalCameraModel" minOccurs="0">
 *                     &lt;complexType>
 *                       &lt;complexContent>
 *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                           &lt;sequence>
 *                             &lt;element name="digitalCameraModelName" type="{http://www.loc.gov/mix/v20}stringType" minOccurs="0"/>
 *                             &lt;element name="digitalCameraModelNumber" type="{http://www.loc.gov/mix/v20}stringType" minOccurs="0"/>
 *                             &lt;element name="digitalCameraModelSerialNo" type="{http://www.loc.gov/mix/v20}stringType" minOccurs="0"/>
 *                           &lt;/sequence>
 *                         &lt;/restriction>
 *                       &lt;/complexContent>
 *                     &lt;/complexType>
 *                   &lt;/element>
 *                   &lt;element name="cameraSensor" type="{http://www.loc.gov/mix/v20}typeOfCameraSensorType" minOccurs="0"/>
 *                   &lt;element name="CameraCaptureSettings" minOccurs="0">
 *                     &lt;complexType>
 *                       &lt;complexContent>
 *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                           &lt;sequence>
 *                             &lt;element name="ImageData" minOccurs="0">
 *                               &lt;complexType>
 *                                 &lt;complexContent>
 *                                   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                                     &lt;sequence>
 *                                       &lt;element name="fNumber" type="{http://www.loc.gov/mix/v20}typeOfNonNegativeRealType" minOccurs="0"/>
 *                                       &lt;element name="exposureTime" type="{http://www.loc.gov/mix/v20}typeOfNonNegativeRealType" minOccurs="0"/>
 *                                       &lt;element name="exposureProgram" type="{http://www.loc.gov/mix/v20}typeOfExposureProgramType" minOccurs="0"/>
 *                                       &lt;element name="spectralSensitivity" type="{http://www.loc.gov/mix/v20}stringType" maxOccurs="unbounded" minOccurs="0"/>
 *                                       &lt;element name="isoSpeedRatings" type="{http://www.loc.gov/mix/v20}positiveIntegerType" minOccurs="0"/>
 *                                       &lt;element name="oECF" type="{http://www.loc.gov/mix/v20}rationalType" minOccurs="0"/>
 *                                       &lt;element name="exifVersion" type="{http://www.loc.gov/mix/v20}typeOfExifVersionType" minOccurs="0"/>
 *                                       &lt;element name="shutterSpeedValue" type="{http://www.loc.gov/mix/v20}rationalType" minOccurs="0"/>
 *                                       &lt;element name="apertureValue" type="{http://www.loc.gov/mix/v20}rationalType" minOccurs="0"/>
 *                                       &lt;element name="brightnessValue" type="{http://www.loc.gov/mix/v20}rationalType" minOccurs="0"/>
 *                                       &lt;element name="exposureBiasValue" type="{http://www.loc.gov/mix/v20}rationalType" minOccurs="0"/>
 *                                       &lt;element name="maxApertureValue" type="{http://www.loc.gov/mix/v20}rationalType" minOccurs="0"/>
 *                                       &lt;element name="SubjectDistance" minOccurs="0">
 *                                         &lt;complexType>
 *                                           &lt;complexContent>
 *                                             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                                               &lt;sequence>
 *                                                 &lt;element name="distance" type="{http://www.loc.gov/mix/v20}typeOfNonNegativeDecimalType" minOccurs="0"/>
 *                                                 &lt;element name="MinMaxDistance" minOccurs="0">
 *                                                   &lt;complexType>
 *                                                     &lt;complexContent>
 *                                                       &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                                                         &lt;sequence>
 *                                                           &lt;element name="minDistance" type="{http://www.loc.gov/mix/v20}typeOfNonNegativeDecimalType" minOccurs="0"/>
 *                                                           &lt;element name="maxDistance" type="{http://www.loc.gov/mix/v20}typeOfNonNegativeDecimalType" minOccurs="0"/>
 *                                                         &lt;/sequence>
 *                                                       &lt;/restriction>
 *                                                     &lt;/complexContent>
 *                                                   &lt;/complexType>
 *                                                 &lt;/element>
 *                                               &lt;/sequence>
 *                                             &lt;/restriction>
 *                                           &lt;/complexContent>
 *                                         &lt;/complexType>
 *                                       &lt;/element>
 *                                       &lt;element name="meteringMode" type="{http://www.loc.gov/mix/v20}typeOfMeteringModeType" minOccurs="0"/>
 *                                       &lt;element name="lightSource" type="{http://www.loc.gov/mix/v20}typeOfLightSourceType" minOccurs="0"/>
 *                                       &lt;element name="flash" type="{http://www.loc.gov/mix/v20}typeOfFlashType" minOccurs="0"/>
 *                                       &lt;element name="focalLength" type="{http://www.loc.gov/mix/v20}typeOfNonNegativeDecimalType" minOccurs="0"/>
 *                                       &lt;element name="flashEnergy" type="{http://www.loc.gov/mix/v20}rationalType" minOccurs="0"/>
 *                                       &lt;element name="backLight" type="{http://www.loc.gov/mix/v20}typeOfBackLightType" minOccurs="0"/>
 *                                       &lt;element name="exposureIndex" type="{http://www.loc.gov/mix/v20}typeOfPositiveRealType" minOccurs="0"/>
 *                                       &lt;element name="sensingMethod" type="{http://www.loc.gov/mix/v20}typeOfSensingMethodType" minOccurs="0"/>
 *                                       &lt;element name="cfaPattern" type="{http://www.loc.gov/mix/v20}integerType" minOccurs="0"/>
 *                                       &lt;element name="autoFocus" type="{http://www.loc.gov/mix/v20}typeOfAutoFocusType" minOccurs="0"/>
 *                                       &lt;element name="PrintAspectRatio" minOccurs="0">
 *                                         &lt;complexType>
 *                                           &lt;complexContent>
 *                                             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                                               &lt;sequence>
 *                                                 &lt;element name="xPrintAspectRatio" type="{http://www.loc.gov/mix/v20}typeOfNonNegativeRealType" minOccurs="0"/>
 *                                                 &lt;element name="yPrintAspectRatio" type="{http://www.loc.gov/mix/v20}typeOfNonNegativeRealType" minOccurs="0"/>
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
 *                             &lt;element name="GPSData" minOccurs="0">
 *                               &lt;complexType>
 *                                 &lt;complexContent>
 *                                   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                                     &lt;sequence>
 *                                       &lt;element name="gpsVersionID" type="{http://www.loc.gov/mix/v20}stringType" minOccurs="0"/>
 *                                       &lt;element name="gpsLatitudeRef" type="{http://www.loc.gov/mix/v20}typeOfgpsLatitudeRefType" minOccurs="0"/>
 *                                       &lt;element name="GPSLatitude" minOccurs="0">
 *                                         &lt;complexType>
 *                                           &lt;complexContent>
 *                                             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                                               &lt;group ref="{http://www.loc.gov/mix/v20}gpsGroup"/>
 *                                             &lt;/restriction>
 *                                           &lt;/complexContent>
 *                                         &lt;/complexType>
 *                                       &lt;/element>
 *                                       &lt;element name="gpsLongitudeRef" type="{http://www.loc.gov/mix/v20}typeOfgpsLongitudeRefType" minOccurs="0"/>
 *                                       &lt;element name="GPSLongitude" minOccurs="0">
 *                                         &lt;complexType>
 *                                           &lt;complexContent>
 *                                             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                                               &lt;group ref="{http://www.loc.gov/mix/v20}gpsGroup"/>
 *                                             &lt;/restriction>
 *                                           &lt;/complexContent>
 *                                         &lt;/complexType>
 *                                       &lt;/element>
 *                                       &lt;element name="gpsAltitudeRef" type="{http://www.loc.gov/mix/v20}typeOfgpsAltitudeRefType" minOccurs="0"/>
 *                                       &lt;element name="gpsAltitude" type="{http://www.loc.gov/mix/v20}rationalType" minOccurs="0"/>
 *                                       &lt;element name="gpsTimeStamp" type="{http://www.loc.gov/mix/v20}stringType" minOccurs="0"/>
 *                                       &lt;element name="gpsSatellites" type="{http://www.loc.gov/mix/v20}stringType" minOccurs="0"/>
 *                                       &lt;element name="gpsStatus" type="{http://www.loc.gov/mix/v20}typeOfgpsStatusType" minOccurs="0"/>
 *                                       &lt;element name="gpsMeasureMode" type="{http://www.loc.gov/mix/v20}typeOfgpsMeasureModeType" minOccurs="0"/>
 *                                       &lt;element name="gpsDOP" type="{http://www.loc.gov/mix/v20}rationalType" minOccurs="0"/>
 *                                       &lt;element name="gpsSpeedRef" type="{http://www.loc.gov/mix/v20}typeOfgpsSpeedRefType" minOccurs="0"/>
 *                                       &lt;element name="gpsSpeed" type="{http://www.loc.gov/mix/v20}rationalType" minOccurs="0"/>
 *                                       &lt;element name="gpsTrackRef" type="{http://www.loc.gov/mix/v20}typeOfgpsTrackRefType" minOccurs="0"/>
 *                                       &lt;element name="gpsTrack" type="{http://www.loc.gov/mix/v20}rationalType" minOccurs="0"/>
 *                                       &lt;element name="gpsImgDirectionRef" type="{http://www.loc.gov/mix/v20}typeOfgpsImgDirectionRefType" minOccurs="0"/>
 *                                       &lt;element name="gpsImgDirection" type="{http://www.loc.gov/mix/v20}rationalType" minOccurs="0"/>
 *                                       &lt;element name="gpsMapDatum" type="{http://www.loc.gov/mix/v20}stringType" minOccurs="0"/>
 *                                       &lt;element name="gpsDestLatitudeRef" type="{http://www.loc.gov/mix/v20}typeOfgpsDestLatitudeRefType" minOccurs="0"/>
 *                                       &lt;element name="GPSDestLatitude" minOccurs="0">
 *                                         &lt;complexType>
 *                                           &lt;complexContent>
 *                                             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                                               &lt;group ref="{http://www.loc.gov/mix/v20}gpsGroup"/>
 *                                             &lt;/restriction>
 *                                           &lt;/complexContent>
 *                                         &lt;/complexType>
 *                                       &lt;/element>
 *                                       &lt;element name="gpsDestLongitudeRef" type="{http://www.loc.gov/mix/v20}typeOfgpsDestLongitudeRefType" minOccurs="0"/>
 *                                       &lt;element name="GPSDestLongitude" minOccurs="0">
 *                                         &lt;complexType>
 *                                           &lt;complexContent>
 *                                             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                                               &lt;group ref="{http://www.loc.gov/mix/v20}gpsGroup"/>
 *                                             &lt;/restriction>
 *                                           &lt;/complexContent>
 *                                         &lt;/complexType>
 *                                       &lt;/element>
 *                                       &lt;element name="gpsDestBearingRef" type="{http://www.loc.gov/mix/v20}typeOfgpsDestBearingRefType" minOccurs="0"/>
 *                                       &lt;element name="gpsDestBearing" type="{http://www.loc.gov/mix/v20}rationalType" minOccurs="0"/>
 *                                       &lt;element name="gpsDestDistanceRef" type="{http://www.loc.gov/mix/v20}typeOfgpsDestDistanceRefType" minOccurs="0"/>
 *                                       &lt;element name="gpsDestDistance" type="{http://www.loc.gov/mix/v20}rationalType" minOccurs="0"/>
 *                                       &lt;element name="gpsProcessingMethod" type="{http://www.loc.gov/mix/v20}stringType" minOccurs="0"/>
 *                                       &lt;element name="gpsAreaInformation" type="{http://www.loc.gov/mix/v20}stringType" minOccurs="0"/>
 *                                       &lt;element name="gpsDateStamp" type="{http://www.loc.gov/mix/v20}dateType" minOccurs="0"/>
 *                                       &lt;element name="gpsDifferential" type="{http://www.loc.gov/mix/v20}typeOfgpsDifferentialType" minOccurs="0"/>
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
 *         &lt;element name="orientation" type="{http://www.loc.gov/mix/v20}typeOfOrientationType" minOccurs="0"/>
 *         &lt;element name="methodology" type="{http://www.loc.gov/mix/v20}stringType" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ImageCaptureMetadataType",
         propOrder = {"sourceInformation",
                      "generalCaptureInformation",
                      "scannerCapture",
                      "digitalCameraCapture",
                      "orientation",
                      "methodology"})
public class ImageCaptureMetadataType {

    @XmlElement(name = "SourceInformation")
    protected ImageCaptureMetadataType.SourceInformation sourceInformation;
    @XmlElement(name = "GeneralCaptureInformation")
    protected ImageCaptureMetadataType.GeneralCaptureInformation generalCaptureInformation;
    @XmlElement(name = "ScannerCapture")
    protected ImageCaptureMetadataType.ScannerCapture scannerCapture;
    @XmlElement(name = "DigitalCameraCapture")
    protected ImageCaptureMetadataType.DigitalCameraCapture digitalCameraCapture;
    protected TypeOfOrientationType orientation;
    protected StringType methodology;

    /**
     * Obtient la valeur de la propriété sourceInformation.
     *
     * @return
     *         possible object is
     *         {@link ImageCaptureMetadataType.SourceInformation }
     *
     */
    public ImageCaptureMetadataType.SourceInformation getSourceInformation() {
        return sourceInformation;
    }

    /**
     * Définit la valeur de la propriété sourceInformation.
     *
     * @param value
     *            allowed object is
     *            {@link ImageCaptureMetadataType.SourceInformation }
     *
     */
    public void setSourceInformation(ImageCaptureMetadataType.SourceInformation value) {
        this.sourceInformation = value;
    }

    /**
     * Obtient la valeur de la propriété generalCaptureInformation.
     *
     * @return
     *         possible object is
     *         {@link ImageCaptureMetadataType.GeneralCaptureInformation }
     *
     */
    public ImageCaptureMetadataType.GeneralCaptureInformation getGeneralCaptureInformation() {
        return generalCaptureInformation;
    }

    /**
     * Définit la valeur de la propriété generalCaptureInformation.
     *
     * @param value
     *            allowed object is
     *            {@link ImageCaptureMetadataType.GeneralCaptureInformation }
     *
     */
    public void setGeneralCaptureInformation(ImageCaptureMetadataType.GeneralCaptureInformation value) {
        this.generalCaptureInformation = value;
    }

    /**
     * Obtient la valeur de la propriété scannerCapture.
     *
     * @return
     *         possible object is
     *         {@link ImageCaptureMetadataType.ScannerCapture }
     *
     */
    public ImageCaptureMetadataType.ScannerCapture getScannerCapture() {
        return scannerCapture;
    }

    /**
     * Définit la valeur de la propriété scannerCapture.
     *
     * @param value
     *            allowed object is
     *            {@link ImageCaptureMetadataType.ScannerCapture }
     *
     */
    public void setScannerCapture(ImageCaptureMetadataType.ScannerCapture value) {
        this.scannerCapture = value;
    }

    /**
     * Obtient la valeur de la propriété digitalCameraCapture.
     *
     * @return
     *         possible object is
     *         {@link ImageCaptureMetadataType.DigitalCameraCapture }
     *
     */
    public ImageCaptureMetadataType.DigitalCameraCapture getDigitalCameraCapture() {
        return digitalCameraCapture;
    }

    /**
     * Définit la valeur de la propriété digitalCameraCapture.
     *
     * @param value
     *            allowed object is
     *            {@link ImageCaptureMetadataType.DigitalCameraCapture }
     *
     */
    public void setDigitalCameraCapture(ImageCaptureMetadataType.DigitalCameraCapture value) {
        this.digitalCameraCapture = value;
    }

    /**
     * Obtient la valeur de la propriété orientation.
     *
     * @return
     *         possible object is
     *         {@link TypeOfOrientationType }
     *
     */
    public TypeOfOrientationType getOrientation() {
        return orientation;
    }

    /**
     * Définit la valeur de la propriété orientation.
     *
     * @param value
     *            allowed object is
     *            {@link TypeOfOrientationType }
     *
     */
    public void setOrientation(TypeOfOrientationType value) {
        this.orientation = value;
    }

    /**
     * Obtient la valeur de la propriété methodology.
     *
     * @return
     *         possible object is
     *         {@link StringType }
     *
     */
    public StringType getMethodology() {
        return methodology;
    }

    /**
     * Définit la valeur de la propriété methodology.
     *
     * @param value
     *            allowed object is
     *            {@link StringType }
     *
     */
    public void setMethodology(StringType value) {
        this.methodology = value;
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
     *         &lt;element name="digitalCameraManufacturer" type="{http://www.loc.gov/mix/v20}stringType" minOccurs="0"/>
     *         &lt;element name="DigitalCameraModel" minOccurs="0">
     *           &lt;complexType>
     *             &lt;complexContent>
     *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *                 &lt;sequence>
     *                   &lt;element name="digitalCameraModelName" type="{http://www.loc.gov/mix/v20}stringType" minOccurs="0"/>
     *                   &lt;element name="digitalCameraModelNumber" type="{http://www.loc.gov/mix/v20}stringType" minOccurs="0"/>
     *                   &lt;element name="digitalCameraModelSerialNo" type="{http://www.loc.gov/mix/v20}stringType" minOccurs="0"/>
     *                 &lt;/sequence>
     *               &lt;/restriction>
     *             &lt;/complexContent>
     *           &lt;/complexType>
     *         &lt;/element>
     *         &lt;element name="cameraSensor" type="{http://www.loc.gov/mix/v20}typeOfCameraSensorType" minOccurs="0"/>
     *         &lt;element name="CameraCaptureSettings" minOccurs="0">
     *           &lt;complexType>
     *             &lt;complexContent>
     *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *                 &lt;sequence>
     *                   &lt;element name="ImageData" minOccurs="0">
     *                     &lt;complexType>
     *                       &lt;complexContent>
     *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *                           &lt;sequence>
     *                             &lt;element name="fNumber" type="{http://www.loc.gov/mix/v20}typeOfNonNegativeRealType" minOccurs="0"/>
     *                             &lt;element name="exposureTime" type="{http://www.loc.gov/mix/v20}typeOfNonNegativeRealType" minOccurs="0"/>
     *                             &lt;element name="exposureProgram" type="{http://www.loc.gov/mix/v20}typeOfExposureProgramType" minOccurs="0"/>
     *                             &lt;element name="spectralSensitivity" type="{http://www.loc.gov/mix/v20}stringType" maxOccurs="unbounded" minOccurs="0"/>
     *                             &lt;element name="isoSpeedRatings" type="{http://www.loc.gov/mix/v20}positiveIntegerType" minOccurs="0"/>
     *                             &lt;element name="oECF" type="{http://www.loc.gov/mix/v20}rationalType" minOccurs="0"/>
     *                             &lt;element name="exifVersion" type="{http://www.loc.gov/mix/v20}typeOfExifVersionType" minOccurs="0"/>
     *                             &lt;element name="shutterSpeedValue" type="{http://www.loc.gov/mix/v20}rationalType" minOccurs="0"/>
     *                             &lt;element name="apertureValue" type="{http://www.loc.gov/mix/v20}rationalType" minOccurs="0"/>
     *                             &lt;element name="brightnessValue" type="{http://www.loc.gov/mix/v20}rationalType" minOccurs="0"/>
     *                             &lt;element name="exposureBiasValue" type="{http://www.loc.gov/mix/v20}rationalType" minOccurs="0"/>
     *                             &lt;element name="maxApertureValue" type="{http://www.loc.gov/mix/v20}rationalType" minOccurs="0"/>
     *                             &lt;element name="SubjectDistance" minOccurs="0">
     *                               &lt;complexType>
     *                                 &lt;complexContent>
     *                                   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *                                     &lt;sequence>
     *                                       &lt;element name="distance" type="{http://www.loc.gov/mix/v20}typeOfNonNegativeDecimalType" minOccurs="0"/>
     *                                       &lt;element name="MinMaxDistance" minOccurs="0">
     *                                         &lt;complexType>
     *                                           &lt;complexContent>
     *                                             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *                                               &lt;sequence>
     *                                                 &lt;element name="minDistance" type="{http://www.loc.gov/mix/v20}typeOfNonNegativeDecimalType" minOccurs="0"/>
     *                                                 &lt;element name="maxDistance" type="{http://www.loc.gov/mix/v20}typeOfNonNegativeDecimalType" minOccurs="0"/>
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
     *                             &lt;element name="meteringMode" type="{http://www.loc.gov/mix/v20}typeOfMeteringModeType" minOccurs="0"/>
     *                             &lt;element name="lightSource" type="{http://www.loc.gov/mix/v20}typeOfLightSourceType" minOccurs="0"/>
     *                             &lt;element name="flash" type="{http://www.loc.gov/mix/v20}typeOfFlashType" minOccurs="0"/>
     *                             &lt;element name="focalLength" type="{http://www.loc.gov/mix/v20}typeOfNonNegativeDecimalType" minOccurs="0"/>
     *                             &lt;element name="flashEnergy" type="{http://www.loc.gov/mix/v20}rationalType" minOccurs="0"/>
     *                             &lt;element name="backLight" type="{http://www.loc.gov/mix/v20}typeOfBackLightType" minOccurs="0"/>
     *                             &lt;element name="exposureIndex" type="{http://www.loc.gov/mix/v20}typeOfPositiveRealType" minOccurs="0"/>
     *                             &lt;element name="sensingMethod" type="{http://www.loc.gov/mix/v20}typeOfSensingMethodType" minOccurs="0"/>
     *                             &lt;element name="cfaPattern" type="{http://www.loc.gov/mix/v20}integerType" minOccurs="0"/>
     *                             &lt;element name="autoFocus" type="{http://www.loc.gov/mix/v20}typeOfAutoFocusType" minOccurs="0"/>
     *                             &lt;element name="PrintAspectRatio" minOccurs="0">
     *                               &lt;complexType>
     *                                 &lt;complexContent>
     *                                   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *                                     &lt;sequence>
     *                                       &lt;element name="xPrintAspectRatio" type="{http://www.loc.gov/mix/v20}typeOfNonNegativeRealType" minOccurs="0"/>
     *                                       &lt;element name="yPrintAspectRatio" type="{http://www.loc.gov/mix/v20}typeOfNonNegativeRealType" minOccurs="0"/>
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
     *                   &lt;element name="GPSData" minOccurs="0">
     *                     &lt;complexType>
     *                       &lt;complexContent>
     *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *                           &lt;sequence>
     *                             &lt;element name="gpsVersionID" type="{http://www.loc.gov/mix/v20}stringType" minOccurs="0"/>
     *                             &lt;element name="gpsLatitudeRef" type="{http://www.loc.gov/mix/v20}typeOfgpsLatitudeRefType" minOccurs="0"/>
     *                             &lt;element name="GPSLatitude" minOccurs="0">
     *                               &lt;complexType>
     *                                 &lt;complexContent>
     *                                   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *                                     &lt;group ref="{http://www.loc.gov/mix/v20}gpsGroup"/>
     *                                   &lt;/restriction>
     *                                 &lt;/complexContent>
     *                               &lt;/complexType>
     *                             &lt;/element>
     *                             &lt;element name="gpsLongitudeRef" type="{http://www.loc.gov/mix/v20}typeOfgpsLongitudeRefType" minOccurs="0"/>
     *                             &lt;element name="GPSLongitude" minOccurs="0">
     *                               &lt;complexType>
     *                                 &lt;complexContent>
     *                                   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *                                     &lt;group ref="{http://www.loc.gov/mix/v20}gpsGroup"/>
     *                                   &lt;/restriction>
     *                                 &lt;/complexContent>
     *                               &lt;/complexType>
     *                             &lt;/element>
     *                             &lt;element name="gpsAltitudeRef" type="{http://www.loc.gov/mix/v20}typeOfgpsAltitudeRefType" minOccurs="0"/>
     *                             &lt;element name="gpsAltitude" type="{http://www.loc.gov/mix/v20}rationalType" minOccurs="0"/>
     *                             &lt;element name="gpsTimeStamp" type="{http://www.loc.gov/mix/v20}stringType" minOccurs="0"/>
     *                             &lt;element name="gpsSatellites" type="{http://www.loc.gov/mix/v20}stringType" minOccurs="0"/>
     *                             &lt;element name="gpsStatus" type="{http://www.loc.gov/mix/v20}typeOfgpsStatusType" minOccurs="0"/>
     *                             &lt;element name="gpsMeasureMode" type="{http://www.loc.gov/mix/v20}typeOfgpsMeasureModeType" minOccurs="0"/>
     *                             &lt;element name="gpsDOP" type="{http://www.loc.gov/mix/v20}rationalType" minOccurs="0"/>
     *                             &lt;element name="gpsSpeedRef" type="{http://www.loc.gov/mix/v20}typeOfgpsSpeedRefType" minOccurs="0"/>
     *                             &lt;element name="gpsSpeed" type="{http://www.loc.gov/mix/v20}rationalType" minOccurs="0"/>
     *                             &lt;element name="gpsTrackRef" type="{http://www.loc.gov/mix/v20}typeOfgpsTrackRefType" minOccurs="0"/>
     *                             &lt;element name="gpsTrack" type="{http://www.loc.gov/mix/v20}rationalType" minOccurs="0"/>
     *                             &lt;element name="gpsImgDirectionRef" type="{http://www.loc.gov/mix/v20}typeOfgpsImgDirectionRefType" minOccurs="0"/>
     *                             &lt;element name="gpsImgDirection" type="{http://www.loc.gov/mix/v20}rationalType" minOccurs="0"/>
     *                             &lt;element name="gpsMapDatum" type="{http://www.loc.gov/mix/v20}stringType" minOccurs="0"/>
     *                             &lt;element name="gpsDestLatitudeRef" type="{http://www.loc.gov/mix/v20}typeOfgpsDestLatitudeRefType" minOccurs="0"/>
     *                             &lt;element name="GPSDestLatitude" minOccurs="0">
     *                               &lt;complexType>
     *                                 &lt;complexContent>
     *                                   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *                                     &lt;group ref="{http://www.loc.gov/mix/v20}gpsGroup"/>
     *                                   &lt;/restriction>
     *                                 &lt;/complexContent>
     *                               &lt;/complexType>
     *                             &lt;/element>
     *                             &lt;element name="gpsDestLongitudeRef" type="{http://www.loc.gov/mix/v20}typeOfgpsDestLongitudeRefType" minOccurs="0"/>
     *                             &lt;element name="GPSDestLongitude" minOccurs="0">
     *                               &lt;complexType>
     *                                 &lt;complexContent>
     *                                   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *                                     &lt;group ref="{http://www.loc.gov/mix/v20}gpsGroup"/>
     *                                   &lt;/restriction>
     *                                 &lt;/complexContent>
     *                               &lt;/complexType>
     *                             &lt;/element>
     *                             &lt;element name="gpsDestBearingRef" type="{http://www.loc.gov/mix/v20}typeOfgpsDestBearingRefType" minOccurs="0"/>
     *                             &lt;element name="gpsDestBearing" type="{http://www.loc.gov/mix/v20}rationalType" minOccurs="0"/>
     *                             &lt;element name="gpsDestDistanceRef" type="{http://www.loc.gov/mix/v20}typeOfgpsDestDistanceRefType" minOccurs="0"/>
     *                             &lt;element name="gpsDestDistance" type="{http://www.loc.gov/mix/v20}rationalType" minOccurs="0"/>
     *                             &lt;element name="gpsProcessingMethod" type="{http://www.loc.gov/mix/v20}stringType" minOccurs="0"/>
     *                             &lt;element name="gpsAreaInformation" type="{http://www.loc.gov/mix/v20}stringType" minOccurs="0"/>
     *                             &lt;element name="gpsDateStamp" type="{http://www.loc.gov/mix/v20}dateType" minOccurs="0"/>
     *                             &lt;element name="gpsDifferential" type="{http://www.loc.gov/mix/v20}typeOfgpsDifferentialType" minOccurs="0"/>
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
             propOrder = {"digitalCameraManufacturer",
                          "digitalCameraModel",
                          "cameraSensor",
                          "cameraCaptureSettings"})
    public static class DigitalCameraCapture {

        protected StringType digitalCameraManufacturer;
        @XmlElement(name = "DigitalCameraModel")
        protected ImageCaptureMetadataType.DigitalCameraCapture.DigitalCameraModel digitalCameraModel;
        protected TypeOfCameraSensorType cameraSensor;
        @XmlElement(name = "CameraCaptureSettings")
        protected ImageCaptureMetadataType.DigitalCameraCapture.CameraCaptureSettings cameraCaptureSettings;

        /**
         * Obtient la valeur de la propriété digitalCameraManufacturer.
         *
         * @return
         *         possible object is
         *         {@link StringType }
         *
         */
        public StringType getDigitalCameraManufacturer() {
            return digitalCameraManufacturer;
        }

        /**
         * Définit la valeur de la propriété digitalCameraManufacturer.
         *
         * @param value
         *            allowed object is
         *            {@link StringType }
         *
         */
        public void setDigitalCameraManufacturer(StringType value) {
            this.digitalCameraManufacturer = value;
        }

        /**
         * Obtient la valeur de la propriété digitalCameraModel.
         *
         * @return
         *         possible object is
         *         {@link ImageCaptureMetadataType.DigitalCameraCapture.DigitalCameraModel }
         *
         */
        public ImageCaptureMetadataType.DigitalCameraCapture.DigitalCameraModel getDigitalCameraModel() {
            return digitalCameraModel;
        }

        /**
         * Définit la valeur de la propriété digitalCameraModel.
         *
         * @param value
         *            allowed object is
         *            {@link ImageCaptureMetadataType.DigitalCameraCapture.DigitalCameraModel }
         *
         */
        public void setDigitalCameraModel(ImageCaptureMetadataType.DigitalCameraCapture.DigitalCameraModel value) {
            this.digitalCameraModel = value;
        }

        /**
         * Obtient la valeur de la propriété cameraSensor.
         *
         * @return
         *         possible object is
         *         {@link TypeOfCameraSensorType }
         *
         */
        public TypeOfCameraSensorType getCameraSensor() {
            return cameraSensor;
        }

        /**
         * Définit la valeur de la propriété cameraSensor.
         *
         * @param value
         *            allowed object is
         *            {@link TypeOfCameraSensorType }
         *
         */
        public void setCameraSensor(TypeOfCameraSensorType value) {
            this.cameraSensor = value;
        }

        /**
         * Obtient la valeur de la propriété cameraCaptureSettings.
         *
         * @return
         *         possible object is
         *         {@link ImageCaptureMetadataType.DigitalCameraCapture.CameraCaptureSettings }
         *
         */
        public ImageCaptureMetadataType.DigitalCameraCapture.CameraCaptureSettings getCameraCaptureSettings() {
            return cameraCaptureSettings;
        }

        /**
         * Définit la valeur de la propriété cameraCaptureSettings.
         *
         * @param value
         *            allowed object is
         *            {@link ImageCaptureMetadataType.DigitalCameraCapture.CameraCaptureSettings }
         *
         */
        public void setCameraCaptureSettings(ImageCaptureMetadataType.DigitalCameraCapture.CameraCaptureSettings value) {
            this.cameraCaptureSettings = value;
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
         *         &lt;element name="ImageData" minOccurs="0">
         *           &lt;complexType>
         *             &lt;complexContent>
         *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
         *                 &lt;sequence>
         *                   &lt;element name="fNumber" type="{http://www.loc.gov/mix/v20}typeOfNonNegativeRealType" minOccurs="0"/>
         *                   &lt;element name="exposureTime" type="{http://www.loc.gov/mix/v20}typeOfNonNegativeRealType" minOccurs="0"/>
         *                   &lt;element name="exposureProgram" type="{http://www.loc.gov/mix/v20}typeOfExposureProgramType" minOccurs="0"/>
         *                   &lt;element name="spectralSensitivity" type="{http://www.loc.gov/mix/v20}stringType" maxOccurs="unbounded" minOccurs="0"/>
         *                   &lt;element name="isoSpeedRatings" type="{http://www.loc.gov/mix/v20}positiveIntegerType" minOccurs="0"/>
         *                   &lt;element name="oECF" type="{http://www.loc.gov/mix/v20}rationalType" minOccurs="0"/>
         *                   &lt;element name="exifVersion" type="{http://www.loc.gov/mix/v20}typeOfExifVersionType" minOccurs="0"/>
         *                   &lt;element name="shutterSpeedValue" type="{http://www.loc.gov/mix/v20}rationalType" minOccurs="0"/>
         *                   &lt;element name="apertureValue" type="{http://www.loc.gov/mix/v20}rationalType" minOccurs="0"/>
         *                   &lt;element name="brightnessValue" type="{http://www.loc.gov/mix/v20}rationalType" minOccurs="0"/>
         *                   &lt;element name="exposureBiasValue" type="{http://www.loc.gov/mix/v20}rationalType" minOccurs="0"/>
         *                   &lt;element name="maxApertureValue" type="{http://www.loc.gov/mix/v20}rationalType" minOccurs="0"/>
         *                   &lt;element name="SubjectDistance" minOccurs="0">
         *                     &lt;complexType>
         *                       &lt;complexContent>
         *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
         *                           &lt;sequence>
         *                             &lt;element name="distance" type="{http://www.loc.gov/mix/v20}typeOfNonNegativeDecimalType" minOccurs="0"/>
         *                             &lt;element name="MinMaxDistance" minOccurs="0">
         *                               &lt;complexType>
         *                                 &lt;complexContent>
         *                                   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
         *                                     &lt;sequence>
         *                                       &lt;element name="minDistance" type="{http://www.loc.gov/mix/v20}typeOfNonNegativeDecimalType" minOccurs="0"/>
         *                                       &lt;element name="maxDistance" type="{http://www.loc.gov/mix/v20}typeOfNonNegativeDecimalType" minOccurs="0"/>
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
         *                   &lt;element name="meteringMode" type="{http://www.loc.gov/mix/v20}typeOfMeteringModeType" minOccurs="0"/>
         *                   &lt;element name="lightSource" type="{http://www.loc.gov/mix/v20}typeOfLightSourceType" minOccurs="0"/>
         *                   &lt;element name="flash" type="{http://www.loc.gov/mix/v20}typeOfFlashType" minOccurs="0"/>
         *                   &lt;element name="focalLength" type="{http://www.loc.gov/mix/v20}typeOfNonNegativeDecimalType" minOccurs="0"/>
         *                   &lt;element name="flashEnergy" type="{http://www.loc.gov/mix/v20}rationalType" minOccurs="0"/>
         *                   &lt;element name="backLight" type="{http://www.loc.gov/mix/v20}typeOfBackLightType" minOccurs="0"/>
         *                   &lt;element name="exposureIndex" type="{http://www.loc.gov/mix/v20}typeOfPositiveRealType" minOccurs="0"/>
         *                   &lt;element name="sensingMethod" type="{http://www.loc.gov/mix/v20}typeOfSensingMethodType" minOccurs="0"/>
         *                   &lt;element name="cfaPattern" type="{http://www.loc.gov/mix/v20}integerType" minOccurs="0"/>
         *                   &lt;element name="autoFocus" type="{http://www.loc.gov/mix/v20}typeOfAutoFocusType" minOccurs="0"/>
         *                   &lt;element name="PrintAspectRatio" minOccurs="0">
         *                     &lt;complexType>
         *                       &lt;complexContent>
         *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
         *                           &lt;sequence>
         *                             &lt;element name="xPrintAspectRatio" type="{http://www.loc.gov/mix/v20}typeOfNonNegativeRealType" minOccurs="0"/>
         *                             &lt;element name="yPrintAspectRatio" type="{http://www.loc.gov/mix/v20}typeOfNonNegativeRealType" minOccurs="0"/>
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
         *         &lt;element name="GPSData" minOccurs="0">
         *           &lt;complexType>
         *             &lt;complexContent>
         *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
         *                 &lt;sequence>
         *                   &lt;element name="gpsVersionID" type="{http://www.loc.gov/mix/v20}stringType" minOccurs="0"/>
         *                   &lt;element name="gpsLatitudeRef" type="{http://www.loc.gov/mix/v20}typeOfgpsLatitudeRefType" minOccurs="0"/>
         *                   &lt;element name="GPSLatitude" minOccurs="0">
         *                     &lt;complexType>
         *                       &lt;complexContent>
         *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
         *                           &lt;group ref="{http://www.loc.gov/mix/v20}gpsGroup"/>
         *                         &lt;/restriction>
         *                       &lt;/complexContent>
         *                     &lt;/complexType>
         *                   &lt;/element>
         *                   &lt;element name="gpsLongitudeRef" type="{http://www.loc.gov/mix/v20}typeOfgpsLongitudeRefType" minOccurs="0"/>
         *                   &lt;element name="GPSLongitude" minOccurs="0">
         *                     &lt;complexType>
         *                       &lt;complexContent>
         *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
         *                           &lt;group ref="{http://www.loc.gov/mix/v20}gpsGroup"/>
         *                         &lt;/restriction>
         *                       &lt;/complexContent>
         *                     &lt;/complexType>
         *                   &lt;/element>
         *                   &lt;element name="gpsAltitudeRef" type="{http://www.loc.gov/mix/v20}typeOfgpsAltitudeRefType" minOccurs="0"/>
         *                   &lt;element name="gpsAltitude" type="{http://www.loc.gov/mix/v20}rationalType" minOccurs="0"/>
         *                   &lt;element name="gpsTimeStamp" type="{http://www.loc.gov/mix/v20}stringType" minOccurs="0"/>
         *                   &lt;element name="gpsSatellites" type="{http://www.loc.gov/mix/v20}stringType" minOccurs="0"/>
         *                   &lt;element name="gpsStatus" type="{http://www.loc.gov/mix/v20}typeOfgpsStatusType" minOccurs="0"/>
         *                   &lt;element name="gpsMeasureMode" type="{http://www.loc.gov/mix/v20}typeOfgpsMeasureModeType" minOccurs="0"/>
         *                   &lt;element name="gpsDOP" type="{http://www.loc.gov/mix/v20}rationalType" minOccurs="0"/>
         *                   &lt;element name="gpsSpeedRef" type="{http://www.loc.gov/mix/v20}typeOfgpsSpeedRefType" minOccurs="0"/>
         *                   &lt;element name="gpsSpeed" type="{http://www.loc.gov/mix/v20}rationalType" minOccurs="0"/>
         *                   &lt;element name="gpsTrackRef" type="{http://www.loc.gov/mix/v20}typeOfgpsTrackRefType" minOccurs="0"/>
         *                   &lt;element name="gpsTrack" type="{http://www.loc.gov/mix/v20}rationalType" minOccurs="0"/>
         *                   &lt;element name="gpsImgDirectionRef" type="{http://www.loc.gov/mix/v20}typeOfgpsImgDirectionRefType" minOccurs="0"/>
         *                   &lt;element name="gpsImgDirection" type="{http://www.loc.gov/mix/v20}rationalType" minOccurs="0"/>
         *                   &lt;element name="gpsMapDatum" type="{http://www.loc.gov/mix/v20}stringType" minOccurs="0"/>
         *                   &lt;element name="gpsDestLatitudeRef" type="{http://www.loc.gov/mix/v20}typeOfgpsDestLatitudeRefType" minOccurs="0"/>
         *                   &lt;element name="GPSDestLatitude" minOccurs="0">
         *                     &lt;complexType>
         *                       &lt;complexContent>
         *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
         *                           &lt;group ref="{http://www.loc.gov/mix/v20}gpsGroup"/>
         *                         &lt;/restriction>
         *                       &lt;/complexContent>
         *                     &lt;/complexType>
         *                   &lt;/element>
         *                   &lt;element name="gpsDestLongitudeRef" type="{http://www.loc.gov/mix/v20}typeOfgpsDestLongitudeRefType" minOccurs="0"/>
         *                   &lt;element name="GPSDestLongitude" minOccurs="0">
         *                     &lt;complexType>
         *                       &lt;complexContent>
         *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
         *                           &lt;group ref="{http://www.loc.gov/mix/v20}gpsGroup"/>
         *                         &lt;/restriction>
         *                       &lt;/complexContent>
         *                     &lt;/complexType>
         *                   &lt;/element>
         *                   &lt;element name="gpsDestBearingRef" type="{http://www.loc.gov/mix/v20}typeOfgpsDestBearingRefType" minOccurs="0"/>
         *                   &lt;element name="gpsDestBearing" type="{http://www.loc.gov/mix/v20}rationalType" minOccurs="0"/>
         *                   &lt;element name="gpsDestDistanceRef" type="{http://www.loc.gov/mix/v20}typeOfgpsDestDistanceRefType" minOccurs="0"/>
         *                   &lt;element name="gpsDestDistance" type="{http://www.loc.gov/mix/v20}rationalType" minOccurs="0"/>
         *                   &lt;element name="gpsProcessingMethod" type="{http://www.loc.gov/mix/v20}stringType" minOccurs="0"/>
         *                   &lt;element name="gpsAreaInformation" type="{http://www.loc.gov/mix/v20}stringType" minOccurs="0"/>
         *                   &lt;element name="gpsDateStamp" type="{http://www.loc.gov/mix/v20}dateType" minOccurs="0"/>
         *                   &lt;element name="gpsDifferential" type="{http://www.loc.gov/mix/v20}typeOfgpsDifferentialType" minOccurs="0"/>
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
                 propOrder = {"imageData",
                              "gpsData"})
        public static class CameraCaptureSettings {

            @XmlElement(name = "ImageData")
            protected ImageCaptureMetadataType.DigitalCameraCapture.CameraCaptureSettings.ImageData imageData;
            @XmlElement(name = "GPSData")
            protected ImageCaptureMetadataType.DigitalCameraCapture.CameraCaptureSettings.GPSData gpsData;

            /**
             * Obtient la valeur de la propriété imageData.
             *
             * @return
             *         possible object is
             *         {@link ImageCaptureMetadataType.DigitalCameraCapture.CameraCaptureSettings.ImageData }
             *
             */
            public ImageCaptureMetadataType.DigitalCameraCapture.CameraCaptureSettings.ImageData getImageData() {
                return imageData;
            }

            /**
             * Définit la valeur de la propriété imageData.
             *
             * @param value
             *            allowed object is
             *            {@link ImageCaptureMetadataType.DigitalCameraCapture.CameraCaptureSettings.ImageData }
             *
             */
            public void setImageData(ImageCaptureMetadataType.DigitalCameraCapture.CameraCaptureSettings.ImageData value) {
                this.imageData = value;
            }

            /**
             * Obtient la valeur de la propriété gpsData.
             *
             * @return
             *         possible object is
             *         {@link ImageCaptureMetadataType.DigitalCameraCapture.CameraCaptureSettings.GPSData }
             *
             */
            public ImageCaptureMetadataType.DigitalCameraCapture.CameraCaptureSettings.GPSData getGPSData() {
                return gpsData;
            }

            /**
             * Définit la valeur de la propriété gpsData.
             *
             * @param value
             *            allowed object is
             *            {@link ImageCaptureMetadataType.DigitalCameraCapture.CameraCaptureSettings.GPSData }
             *
             */
            public void setGPSData(ImageCaptureMetadataType.DigitalCameraCapture.CameraCaptureSettings.GPSData value) {
                this.gpsData = value;
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
             *         &lt;element name="gpsVersionID" type="{http://www.loc.gov/mix/v20}stringType" minOccurs="0"/>
             *         &lt;element name="gpsLatitudeRef" type="{http://www.loc.gov/mix/v20}typeOfgpsLatitudeRefType" minOccurs="0"/>
             *         &lt;element name="GPSLatitude" minOccurs="0">
             *           &lt;complexType>
             *             &lt;complexContent>
             *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
             *                 &lt;group ref="{http://www.loc.gov/mix/v20}gpsGroup"/>
             *               &lt;/restriction>
             *             &lt;/complexContent>
             *           &lt;/complexType>
             *         &lt;/element>
             *         &lt;element name="gpsLongitudeRef" type="{http://www.loc.gov/mix/v20}typeOfgpsLongitudeRefType" minOccurs="0"/>
             *         &lt;element name="GPSLongitude" minOccurs="0">
             *           &lt;complexType>
             *             &lt;complexContent>
             *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
             *                 &lt;group ref="{http://www.loc.gov/mix/v20}gpsGroup"/>
             *               &lt;/restriction>
             *             &lt;/complexContent>
             *           &lt;/complexType>
             *         &lt;/element>
             *         &lt;element name="gpsAltitudeRef" type="{http://www.loc.gov/mix/v20}typeOfgpsAltitudeRefType" minOccurs="0"/>
             *         &lt;element name="gpsAltitude" type="{http://www.loc.gov/mix/v20}rationalType" minOccurs="0"/>
             *         &lt;element name="gpsTimeStamp" type="{http://www.loc.gov/mix/v20}stringType" minOccurs="0"/>
             *         &lt;element name="gpsSatellites" type="{http://www.loc.gov/mix/v20}stringType" minOccurs="0"/>
             *         &lt;element name="gpsStatus" type="{http://www.loc.gov/mix/v20}typeOfgpsStatusType" minOccurs="0"/>
             *         &lt;element name="gpsMeasureMode" type="{http://www.loc.gov/mix/v20}typeOfgpsMeasureModeType" minOccurs="0"/>
             *         &lt;element name="gpsDOP" type="{http://www.loc.gov/mix/v20}rationalType" minOccurs="0"/>
             *         &lt;element name="gpsSpeedRef" type="{http://www.loc.gov/mix/v20}typeOfgpsSpeedRefType" minOccurs="0"/>
             *         &lt;element name="gpsSpeed" type="{http://www.loc.gov/mix/v20}rationalType" minOccurs="0"/>
             *         &lt;element name="gpsTrackRef" type="{http://www.loc.gov/mix/v20}typeOfgpsTrackRefType" minOccurs="0"/>
             *         &lt;element name="gpsTrack" type="{http://www.loc.gov/mix/v20}rationalType" minOccurs="0"/>
             *         &lt;element name="gpsImgDirectionRef" type="{http://www.loc.gov/mix/v20}typeOfgpsImgDirectionRefType" minOccurs="0"/>
             *         &lt;element name="gpsImgDirection" type="{http://www.loc.gov/mix/v20}rationalType" minOccurs="0"/>
             *         &lt;element name="gpsMapDatum" type="{http://www.loc.gov/mix/v20}stringType" minOccurs="0"/>
             *         &lt;element name="gpsDestLatitudeRef" type="{http://www.loc.gov/mix/v20}typeOfgpsDestLatitudeRefType" minOccurs="0"/>
             *         &lt;element name="GPSDestLatitude" minOccurs="0">
             *           &lt;complexType>
             *             &lt;complexContent>
             *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
             *                 &lt;group ref="{http://www.loc.gov/mix/v20}gpsGroup"/>
             *               &lt;/restriction>
             *             &lt;/complexContent>
             *           &lt;/complexType>
             *         &lt;/element>
             *         &lt;element name="gpsDestLongitudeRef" type="{http://www.loc.gov/mix/v20}typeOfgpsDestLongitudeRefType" minOccurs="0"/>
             *         &lt;element name="GPSDestLongitude" minOccurs="0">
             *           &lt;complexType>
             *             &lt;complexContent>
             *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
             *                 &lt;group ref="{http://www.loc.gov/mix/v20}gpsGroup"/>
             *               &lt;/restriction>
             *             &lt;/complexContent>
             *           &lt;/complexType>
             *         &lt;/element>
             *         &lt;element name="gpsDestBearingRef" type="{http://www.loc.gov/mix/v20}typeOfgpsDestBearingRefType" minOccurs="0"/>
             *         &lt;element name="gpsDestBearing" type="{http://www.loc.gov/mix/v20}rationalType" minOccurs="0"/>
             *         &lt;element name="gpsDestDistanceRef" type="{http://www.loc.gov/mix/v20}typeOfgpsDestDistanceRefType" minOccurs="0"/>
             *         &lt;element name="gpsDestDistance" type="{http://www.loc.gov/mix/v20}rationalType" minOccurs="0"/>
             *         &lt;element name="gpsProcessingMethod" type="{http://www.loc.gov/mix/v20}stringType" minOccurs="0"/>
             *         &lt;element name="gpsAreaInformation" type="{http://www.loc.gov/mix/v20}stringType" minOccurs="0"/>
             *         &lt;element name="gpsDateStamp" type="{http://www.loc.gov/mix/v20}dateType" minOccurs="0"/>
             *         &lt;element name="gpsDifferential" type="{http://www.loc.gov/mix/v20}typeOfgpsDifferentialType" minOccurs="0"/>
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
                     propOrder = {"gpsVersionID",
                                  "gpsLatitudeRef",
                                  "gpsLatitude",
                                  "gpsLongitudeRef",
                                  "gpsLongitude",
                                  "gpsAltitudeRef",
                                  "gpsAltitude",
                                  "gpsTimeStamp",
                                  "gpsSatellites",
                                  "gpsStatus",
                                  "gpsMeasureMode",
                                  "gpsDOP",
                                  "gpsSpeedRef",
                                  "gpsSpeed",
                                  "gpsTrackRef",
                                  "gpsTrack",
                                  "gpsImgDirectionRef",
                                  "gpsImgDirection",
                                  "gpsMapDatum",
                                  "gpsDestLatitudeRef",
                                  "gpsDestLatitude",
                                  "gpsDestLongitudeRef",
                                  "gpsDestLongitude",
                                  "gpsDestBearingRef",
                                  "gpsDestBearing",
                                  "gpsDestDistanceRef",
                                  "gpsDestDistance",
                                  "gpsProcessingMethod",
                                  "gpsAreaInformation",
                                  "gpsDateStamp",
                                  "gpsDifferential"})
            public static class GPSData {

                protected StringType gpsVersionID;
                protected TypeOfgpsLatitudeRefType gpsLatitudeRef;
                @XmlElement(name = "GPSLatitude")
                protected ImageCaptureMetadataType.DigitalCameraCapture.CameraCaptureSettings.GPSData.GPSLatitude gpsLatitude;
                protected TypeOfgpsLongitudeRefType gpsLongitudeRef;
                @XmlElement(name = "GPSLongitude")
                protected ImageCaptureMetadataType.DigitalCameraCapture.CameraCaptureSettings.GPSData.GPSLongitude gpsLongitude;
                protected TypeOfgpsAltitudeRefType gpsAltitudeRef;
                protected RationalType gpsAltitude;
                protected StringType gpsTimeStamp;
                protected StringType gpsSatellites;
                protected TypeOfgpsStatusType gpsStatus;
                protected TypeOfgpsMeasureModeType gpsMeasureMode;
                protected RationalType gpsDOP;
                protected TypeOfgpsSpeedRefType gpsSpeedRef;
                protected RationalType gpsSpeed;
                protected TypeOfgpsTrackRefType gpsTrackRef;
                protected RationalType gpsTrack;
                protected TypeOfgpsImgDirectionRefType gpsImgDirectionRef;
                protected RationalType gpsImgDirection;
                protected StringType gpsMapDatum;
                protected TypeOfgpsDestLatitudeRefType gpsDestLatitudeRef;
                @XmlElement(name = "GPSDestLatitude")
                protected ImageCaptureMetadataType.DigitalCameraCapture.CameraCaptureSettings.GPSData.GPSDestLatitude gpsDestLatitude;
                protected TypeOfgpsDestLongitudeRefType gpsDestLongitudeRef;
                @XmlElement(name = "GPSDestLongitude")
                protected ImageCaptureMetadataType.DigitalCameraCapture.CameraCaptureSettings.GPSData.GPSDestLongitude gpsDestLongitude;
                protected TypeOfgpsDestBearingRefType gpsDestBearingRef;
                protected RationalType gpsDestBearing;
                protected TypeOfgpsDestDistanceRefType gpsDestDistanceRef;
                protected RationalType gpsDestDistance;
                protected StringType gpsProcessingMethod;
                protected StringType gpsAreaInformation;
                protected DateType gpsDateStamp;
                protected TypeOfgpsDifferentialType gpsDifferential;

                /**
                 * Obtient la valeur de la propriété gpsVersionID.
                 *
                 * @return
                 *         possible object is
                 *         {@link StringType }
                 *
                 */
                public StringType getGpsVersionID() {
                    return gpsVersionID;
                }

                /**
                 * Définit la valeur de la propriété gpsVersionID.
                 *
                 * @param value
                 *            allowed object is
                 *            {@link StringType }
                 *
                 */
                public void setGpsVersionID(StringType value) {
                    this.gpsVersionID = value;
                }

                /**
                 * Obtient la valeur de la propriété gpsLatitudeRef.
                 *
                 * @return
                 *         possible object is
                 *         {@link TypeOfgpsLatitudeRefType }
                 *
                 */
                public TypeOfgpsLatitudeRefType getGpsLatitudeRef() {
                    return gpsLatitudeRef;
                }

                /**
                 * Définit la valeur de la propriété gpsLatitudeRef.
                 *
                 * @param value
                 *            allowed object is
                 *            {@link TypeOfgpsLatitudeRefType }
                 *
                 */
                public void setGpsLatitudeRef(TypeOfgpsLatitudeRefType value) {
                    this.gpsLatitudeRef = value;
                }

                /**
                 * Obtient la valeur de la propriété gpsLatitude.
                 *
                 * @return
                 *         possible object is
                 *         {@link ImageCaptureMetadataType.DigitalCameraCapture.CameraCaptureSettings.GPSData.GPSLatitude }
                 *
                 */
                public ImageCaptureMetadataType.DigitalCameraCapture.CameraCaptureSettings.GPSData.GPSLatitude getGPSLatitude() {
                    return gpsLatitude;
                }

                /**
                 * Définit la valeur de la propriété gpsLatitude.
                 *
                 * @param value
                 *            allowed object is
                 *            {@link ImageCaptureMetadataType.DigitalCameraCapture.CameraCaptureSettings.GPSData.GPSLatitude }
                 *
                 */
                public void setGPSLatitude(ImageCaptureMetadataType.DigitalCameraCapture.CameraCaptureSettings.GPSData.GPSLatitude value) {
                    this.gpsLatitude = value;
                }

                /**
                 * Obtient la valeur de la propriété gpsLongitudeRef.
                 *
                 * @return
                 *         possible object is
                 *         {@link TypeOfgpsLongitudeRefType }
                 *
                 */
                public TypeOfgpsLongitudeRefType getGpsLongitudeRef() {
                    return gpsLongitudeRef;
                }

                /**
                 * Définit la valeur de la propriété gpsLongitudeRef.
                 *
                 * @param value
                 *            allowed object is
                 *            {@link TypeOfgpsLongitudeRefType }
                 *
                 */
                public void setGpsLongitudeRef(TypeOfgpsLongitudeRefType value) {
                    this.gpsLongitudeRef = value;
                }

                /**
                 * Obtient la valeur de la propriété gpsLongitude.
                 *
                 * @return
                 *         possible object is
                 *         {@link ImageCaptureMetadataType.DigitalCameraCapture.CameraCaptureSettings.GPSData.GPSLongitude }
                 *
                 */
                public ImageCaptureMetadataType.DigitalCameraCapture.CameraCaptureSettings.GPSData.GPSLongitude getGPSLongitude() {
                    return gpsLongitude;
                }

                /**
                 * Définit la valeur de la propriété gpsLongitude.
                 *
                 * @param value
                 *            allowed object is
                 *            {@link ImageCaptureMetadataType.DigitalCameraCapture.CameraCaptureSettings.GPSData.GPSLongitude }
                 *
                 */
                public void setGPSLongitude(ImageCaptureMetadataType.DigitalCameraCapture.CameraCaptureSettings.GPSData.GPSLongitude value) {
                    this.gpsLongitude = value;
                }

                /**
                 * Obtient la valeur de la propriété gpsAltitudeRef.
                 *
                 * @return
                 *         possible object is
                 *         {@link TypeOfgpsAltitudeRefType }
                 *
                 */
                public TypeOfgpsAltitudeRefType getGpsAltitudeRef() {
                    return gpsAltitudeRef;
                }

                /**
                 * Définit la valeur de la propriété gpsAltitudeRef.
                 *
                 * @param value
                 *            allowed object is
                 *            {@link TypeOfgpsAltitudeRefType }
                 *
                 */
                public void setGpsAltitudeRef(TypeOfgpsAltitudeRefType value) {
                    this.gpsAltitudeRef = value;
                }

                /**
                 * Obtient la valeur de la propriété gpsAltitude.
                 *
                 * @return
                 *         possible object is
                 *         {@link RationalType }
                 *
                 */
                public RationalType getGpsAltitude() {
                    return gpsAltitude;
                }

                /**
                 * Définit la valeur de la propriété gpsAltitude.
                 *
                 * @param value
                 *            allowed object is
                 *            {@link RationalType }
                 *
                 */
                public void setGpsAltitude(RationalType value) {
                    this.gpsAltitude = value;
                }

                /**
                 * Obtient la valeur de la propriété gpsTimeStamp.
                 *
                 * @return
                 *         possible object is
                 *         {@link StringType }
                 *
                 */
                public StringType getGpsTimeStamp() {
                    return gpsTimeStamp;
                }

                /**
                 * Définit la valeur de la propriété gpsTimeStamp.
                 *
                 * @param value
                 *            allowed object is
                 *            {@link StringType }
                 *
                 */
                public void setGpsTimeStamp(StringType value) {
                    this.gpsTimeStamp = value;
                }

                /**
                 * Obtient la valeur de la propriété gpsSatellites.
                 *
                 * @return
                 *         possible object is
                 *         {@link StringType }
                 *
                 */
                public StringType getGpsSatellites() {
                    return gpsSatellites;
                }

                /**
                 * Définit la valeur de la propriété gpsSatellites.
                 *
                 * @param value
                 *            allowed object is
                 *            {@link StringType }
                 *
                 */
                public void setGpsSatellites(StringType value) {
                    this.gpsSatellites = value;
                }

                /**
                 * Obtient la valeur de la propriété gpsStatus.
                 *
                 * @return
                 *         possible object is
                 *         {@link TypeOfgpsStatusType }
                 *
                 */
                public TypeOfgpsStatusType getGpsStatus() {
                    return gpsStatus;
                }

                /**
                 * Définit la valeur de la propriété gpsStatus.
                 *
                 * @param value
                 *            allowed object is
                 *            {@link TypeOfgpsStatusType }
                 *
                 */
                public void setGpsStatus(TypeOfgpsStatusType value) {
                    this.gpsStatus = value;
                }

                /**
                 * Obtient la valeur de la propriété gpsMeasureMode.
                 *
                 * @return
                 *         possible object is
                 *         {@link TypeOfgpsMeasureModeType }
                 *
                 */
                public TypeOfgpsMeasureModeType getGpsMeasureMode() {
                    return gpsMeasureMode;
                }

                /**
                 * Définit la valeur de la propriété gpsMeasureMode.
                 *
                 * @param value
                 *            allowed object is
                 *            {@link TypeOfgpsMeasureModeType }
                 *
                 */
                public void setGpsMeasureMode(TypeOfgpsMeasureModeType value) {
                    this.gpsMeasureMode = value;
                }

                /**
                 * Obtient la valeur de la propriété gpsDOP.
                 *
                 * @return
                 *         possible object is
                 *         {@link RationalType }
                 *
                 */
                public RationalType getGpsDOP() {
                    return gpsDOP;
                }

                /**
                 * Définit la valeur de la propriété gpsDOP.
                 *
                 * @param value
                 *            allowed object is
                 *            {@link RationalType }
                 *
                 */
                public void setGpsDOP(RationalType value) {
                    this.gpsDOP = value;
                }

                /**
                 * Obtient la valeur de la propriété gpsSpeedRef.
                 *
                 * @return
                 *         possible object is
                 *         {@link TypeOfgpsSpeedRefType }
                 *
                 */
                public TypeOfgpsSpeedRefType getGpsSpeedRef() {
                    return gpsSpeedRef;
                }

                /**
                 * Définit la valeur de la propriété gpsSpeedRef.
                 *
                 * @param value
                 *            allowed object is
                 *            {@link TypeOfgpsSpeedRefType }
                 *
                 */
                public void setGpsSpeedRef(TypeOfgpsSpeedRefType value) {
                    this.gpsSpeedRef = value;
                }

                /**
                 * Obtient la valeur de la propriété gpsSpeed.
                 *
                 * @return
                 *         possible object is
                 *         {@link RationalType }
                 *
                 */
                public RationalType getGpsSpeed() {
                    return gpsSpeed;
                }

                /**
                 * Définit la valeur de la propriété gpsSpeed.
                 *
                 * @param value
                 *            allowed object is
                 *            {@link RationalType }
                 *
                 */
                public void setGpsSpeed(RationalType value) {
                    this.gpsSpeed = value;
                }

                /**
                 * Obtient la valeur de la propriété gpsTrackRef.
                 *
                 * @return
                 *         possible object is
                 *         {@link TypeOfgpsTrackRefType }
                 *
                 */
                public TypeOfgpsTrackRefType getGpsTrackRef() {
                    return gpsTrackRef;
                }

                /**
                 * Définit la valeur de la propriété gpsTrackRef.
                 *
                 * @param value
                 *            allowed object is
                 *            {@link TypeOfgpsTrackRefType }
                 *
                 */
                public void setGpsTrackRef(TypeOfgpsTrackRefType value) {
                    this.gpsTrackRef = value;
                }

                /**
                 * Obtient la valeur de la propriété gpsTrack.
                 *
                 * @return
                 *         possible object is
                 *         {@link RationalType }
                 *
                 */
                public RationalType getGpsTrack() {
                    return gpsTrack;
                }

                /**
                 * Définit la valeur de la propriété gpsTrack.
                 *
                 * @param value
                 *            allowed object is
                 *            {@link RationalType }
                 *
                 */
                public void setGpsTrack(RationalType value) {
                    this.gpsTrack = value;
                }

                /**
                 * Obtient la valeur de la propriété gpsImgDirectionRef.
                 *
                 * @return
                 *         possible object is
                 *         {@link TypeOfgpsImgDirectionRefType }
                 *
                 */
                public TypeOfgpsImgDirectionRefType getGpsImgDirectionRef() {
                    return gpsImgDirectionRef;
                }

                /**
                 * Définit la valeur de la propriété gpsImgDirectionRef.
                 *
                 * @param value
                 *            allowed object is
                 *            {@link TypeOfgpsImgDirectionRefType }
                 *
                 */
                public void setGpsImgDirectionRef(TypeOfgpsImgDirectionRefType value) {
                    this.gpsImgDirectionRef = value;
                }

                /**
                 * Obtient la valeur de la propriété gpsImgDirection.
                 *
                 * @return
                 *         possible object is
                 *         {@link RationalType }
                 *
                 */
                public RationalType getGpsImgDirection() {
                    return gpsImgDirection;
                }

                /**
                 * Définit la valeur de la propriété gpsImgDirection.
                 *
                 * @param value
                 *            allowed object is
                 *            {@link RationalType }
                 *
                 */
                public void setGpsImgDirection(RationalType value) {
                    this.gpsImgDirection = value;
                }

                /**
                 * Obtient la valeur de la propriété gpsMapDatum.
                 *
                 * @return
                 *         possible object is
                 *         {@link StringType }
                 *
                 */
                public StringType getGpsMapDatum() {
                    return gpsMapDatum;
                }

                /**
                 * Définit la valeur de la propriété gpsMapDatum.
                 *
                 * @param value
                 *            allowed object is
                 *            {@link StringType }
                 *
                 */
                public void setGpsMapDatum(StringType value) {
                    this.gpsMapDatum = value;
                }

                /**
                 * Obtient la valeur de la propriété gpsDestLatitudeRef.
                 *
                 * @return
                 *         possible object is
                 *         {@link TypeOfgpsDestLatitudeRefType }
                 *
                 */
                public TypeOfgpsDestLatitudeRefType getGpsDestLatitudeRef() {
                    return gpsDestLatitudeRef;
                }

                /**
                 * Définit la valeur de la propriété gpsDestLatitudeRef.
                 *
                 * @param value
                 *            allowed object is
                 *            {@link TypeOfgpsDestLatitudeRefType }
                 *
                 */
                public void setGpsDestLatitudeRef(TypeOfgpsDestLatitudeRefType value) {
                    this.gpsDestLatitudeRef = value;
                }

                /**
                 * Obtient la valeur de la propriété gpsDestLatitude.
                 *
                 * @return
                 *         possible object is
                 *         {@link ImageCaptureMetadataType.DigitalCameraCapture.CameraCaptureSettings.GPSData.GPSDestLatitude }
                 *
                 */
                public ImageCaptureMetadataType.DigitalCameraCapture.CameraCaptureSettings.GPSData.GPSDestLatitude getGPSDestLatitude() {
                    return gpsDestLatitude;
                }

                /**
                 * Définit la valeur de la propriété gpsDestLatitude.
                 *
                 * @param value
                 *            allowed object is
                 *            {@link ImageCaptureMetadataType.DigitalCameraCapture.CameraCaptureSettings.GPSData.GPSDestLatitude }
                 *
                 */
                public void setGPSDestLatitude(ImageCaptureMetadataType.DigitalCameraCapture.CameraCaptureSettings.GPSData.GPSDestLatitude value) {
                    this.gpsDestLatitude = value;
                }

                /**
                 * Obtient la valeur de la propriété gpsDestLongitudeRef.
                 *
                 * @return
                 *         possible object is
                 *         {@link TypeOfgpsDestLongitudeRefType }
                 *
                 */
                public TypeOfgpsDestLongitudeRefType getGpsDestLongitudeRef() {
                    return gpsDestLongitudeRef;
                }

                /**
                 * Définit la valeur de la propriété gpsDestLongitudeRef.
                 *
                 * @param value
                 *            allowed object is
                 *            {@link TypeOfgpsDestLongitudeRefType }
                 *
                 */
                public void setGpsDestLongitudeRef(TypeOfgpsDestLongitudeRefType value) {
                    this.gpsDestLongitudeRef = value;
                }

                /**
                 * Obtient la valeur de la propriété gpsDestLongitude.
                 *
                 * @return
                 *         possible object is
                 *         {@link ImageCaptureMetadataType.DigitalCameraCapture.CameraCaptureSettings.GPSData.GPSDestLongitude }
                 *
                 */
                public ImageCaptureMetadataType.DigitalCameraCapture.CameraCaptureSettings.GPSData.GPSDestLongitude getGPSDestLongitude() {
                    return gpsDestLongitude;
                }

                /**
                 * Définit la valeur de la propriété gpsDestLongitude.
                 *
                 * @param value
                 *            allowed object is
                 *            {@link ImageCaptureMetadataType.DigitalCameraCapture.CameraCaptureSettings.GPSData.GPSDestLongitude }
                 *
                 */
                public void setGPSDestLongitude(ImageCaptureMetadataType.DigitalCameraCapture.CameraCaptureSettings.GPSData.GPSDestLongitude value) {
                    this.gpsDestLongitude = value;
                }

                /**
                 * Obtient la valeur de la propriété gpsDestBearingRef.
                 *
                 * @return
                 *         possible object is
                 *         {@link TypeOfgpsDestBearingRefType }
                 *
                 */
                public TypeOfgpsDestBearingRefType getGpsDestBearingRef() {
                    return gpsDestBearingRef;
                }

                /**
                 * Définit la valeur de la propriété gpsDestBearingRef.
                 *
                 * @param value
                 *            allowed object is
                 *            {@link TypeOfgpsDestBearingRefType }
                 *
                 */
                public void setGpsDestBearingRef(TypeOfgpsDestBearingRefType value) {
                    this.gpsDestBearingRef = value;
                }

                /**
                 * Obtient la valeur de la propriété gpsDestBearing.
                 *
                 * @return
                 *         possible object is
                 *         {@link RationalType }
                 *
                 */
                public RationalType getGpsDestBearing() {
                    return gpsDestBearing;
                }

                /**
                 * Définit la valeur de la propriété gpsDestBearing.
                 *
                 * @param value
                 *            allowed object is
                 *            {@link RationalType }
                 *
                 */
                public void setGpsDestBearing(RationalType value) {
                    this.gpsDestBearing = value;
                }

                /**
                 * Obtient la valeur de la propriété gpsDestDistanceRef.
                 *
                 * @return
                 *         possible object is
                 *         {@link TypeOfgpsDestDistanceRefType }
                 *
                 */
                public TypeOfgpsDestDistanceRefType getGpsDestDistanceRef() {
                    return gpsDestDistanceRef;
                }

                /**
                 * Définit la valeur de la propriété gpsDestDistanceRef.
                 *
                 * @param value
                 *            allowed object is
                 *            {@link TypeOfgpsDestDistanceRefType }
                 *
                 */
                public void setGpsDestDistanceRef(TypeOfgpsDestDistanceRefType value) {
                    this.gpsDestDistanceRef = value;
                }

                /**
                 * Obtient la valeur de la propriété gpsDestDistance.
                 *
                 * @return
                 *         possible object is
                 *         {@link RationalType }
                 *
                 */
                public RationalType getGpsDestDistance() {
                    return gpsDestDistance;
                }

                /**
                 * Définit la valeur de la propriété gpsDestDistance.
                 *
                 * @param value
                 *            allowed object is
                 *            {@link RationalType }
                 *
                 */
                public void setGpsDestDistance(RationalType value) {
                    this.gpsDestDistance = value;
                }

                /**
                 * Obtient la valeur de la propriété gpsProcessingMethod.
                 *
                 * @return
                 *         possible object is
                 *         {@link StringType }
                 *
                 */
                public StringType getGpsProcessingMethod() {
                    return gpsProcessingMethod;
                }

                /**
                 * Définit la valeur de la propriété gpsProcessingMethod.
                 *
                 * @param value
                 *            allowed object is
                 *            {@link StringType }
                 *
                 */
                public void setGpsProcessingMethod(StringType value) {
                    this.gpsProcessingMethod = value;
                }

                /**
                 * Obtient la valeur de la propriété gpsAreaInformation.
                 *
                 * @return
                 *         possible object is
                 *         {@link StringType }
                 *
                 */
                public StringType getGpsAreaInformation() {
                    return gpsAreaInformation;
                }

                /**
                 * Définit la valeur de la propriété gpsAreaInformation.
                 *
                 * @param value
                 *            allowed object is
                 *            {@link StringType }
                 *
                 */
                public void setGpsAreaInformation(StringType value) {
                    this.gpsAreaInformation = value;
                }

                /**
                 * Obtient la valeur de la propriété gpsDateStamp.
                 *
                 * @return
                 *         possible object is
                 *         {@link DateType }
                 *
                 */
                public DateType getGpsDateStamp() {
                    return gpsDateStamp;
                }

                /**
                 * Définit la valeur de la propriété gpsDateStamp.
                 *
                 * @param value
                 *            allowed object is
                 *            {@link DateType }
                 *
                 */
                public void setGpsDateStamp(DateType value) {
                    this.gpsDateStamp = value;
                }

                /**
                 * Obtient la valeur de la propriété gpsDifferential.
                 *
                 * @return
                 *         possible object is
                 *         {@link TypeOfgpsDifferentialType }
                 *
                 */
                public TypeOfgpsDifferentialType getGpsDifferential() {
                    return gpsDifferential;
                }

                /**
                 * Définit la valeur de la propriété gpsDifferential.
                 *
                 * @param value
                 *            allowed object is
                 *            {@link TypeOfgpsDifferentialType }
                 *
                 */
                public void setGpsDifferential(TypeOfgpsDifferentialType value) {
                    this.gpsDifferential = value;
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
                 *       &lt;group ref="{http://www.loc.gov/mix/v20}gpsGroup"/>
                 *     &lt;/restriction>
                 *   &lt;/complexContent>
                 * &lt;/complexType>
                 * </pre>
                 *
                 *
                 */
                @XmlAccessorType(XmlAccessType.FIELD)
                @XmlType(name = "",
                         propOrder = {"degrees",
                                      "minutes",
                                      "seconds"})
                public static class GPSDestLatitude {

                    protected RationalType degrees;
                    protected RationalType minutes;
                    protected RationalType seconds;

                    /**
                     * Obtient la valeur de la propriété degrees.
                     *
                     * @return
                     *         possible object is
                     *         {@link RationalType }
                     *
                     */
                    public RationalType getDegrees() {
                        return degrees;
                    }

                    /**
                     * Définit la valeur de la propriété degrees.
                     *
                     * @param value
                     *            allowed object is
                     *            {@link RationalType }
                     *
                     */
                    public void setDegrees(RationalType value) {
                        this.degrees = value;
                    }

                    /**
                     * Obtient la valeur de la propriété minutes.
                     *
                     * @return
                     *         possible object is
                     *         {@link RationalType }
                     *
                     */
                    public RationalType getMinutes() {
                        return minutes;
                    }

                    /**
                     * Définit la valeur de la propriété minutes.
                     *
                     * @param value
                     *            allowed object is
                     *            {@link RationalType }
                     *
                     */
                    public void setMinutes(RationalType value) {
                        this.minutes = value;
                    }

                    /**
                     * Obtient la valeur de la propriété seconds.
                     *
                     * @return
                     *         possible object is
                     *         {@link RationalType }
                     *
                     */
                    public RationalType getSeconds() {
                        return seconds;
                    }

                    /**
                     * Définit la valeur de la propriété seconds.
                     *
                     * @param value
                     *            allowed object is
                     *            {@link RationalType }
                     *
                     */
                    public void setSeconds(RationalType value) {
                        this.seconds = value;
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
                 *       &lt;group ref="{http://www.loc.gov/mix/v20}gpsGroup"/>
                 *     &lt;/restriction>
                 *   &lt;/complexContent>
                 * &lt;/complexType>
                 * </pre>
                 *
                 *
                 */
                @XmlAccessorType(XmlAccessType.FIELD)
                @XmlType(name = "",
                         propOrder = {"degrees",
                                      "minutes",
                                      "seconds"})
                public static class GPSDestLongitude {

                    protected RationalType degrees;
                    protected RationalType minutes;
                    protected RationalType seconds;

                    /**
                     * Obtient la valeur de la propriété degrees.
                     *
                     * @return
                     *         possible object is
                     *         {@link RationalType }
                     *
                     */
                    public RationalType getDegrees() {
                        return degrees;
                    }

                    /**
                     * Définit la valeur de la propriété degrees.
                     *
                     * @param value
                     *            allowed object is
                     *            {@link RationalType }
                     *
                     */
                    public void setDegrees(RationalType value) {
                        this.degrees = value;
                    }

                    /**
                     * Obtient la valeur de la propriété minutes.
                     *
                     * @return
                     *         possible object is
                     *         {@link RationalType }
                     *
                     */
                    public RationalType getMinutes() {
                        return minutes;
                    }

                    /**
                     * Définit la valeur de la propriété minutes.
                     *
                     * @param value
                     *            allowed object is
                     *            {@link RationalType }
                     *
                     */
                    public void setMinutes(RationalType value) {
                        this.minutes = value;
                    }

                    /**
                     * Obtient la valeur de la propriété seconds.
                     *
                     * @return
                     *         possible object is
                     *         {@link RationalType }
                     *
                     */
                    public RationalType getSeconds() {
                        return seconds;
                    }

                    /**
                     * Définit la valeur de la propriété seconds.
                     *
                     * @param value
                     *            allowed object is
                     *            {@link RationalType }
                     *
                     */
                    public void setSeconds(RationalType value) {
                        this.seconds = value;
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
                 *       &lt;group ref="{http://www.loc.gov/mix/v20}gpsGroup"/>
                 *     &lt;/restriction>
                 *   &lt;/complexContent>
                 * &lt;/complexType>
                 * </pre>
                 *
                 *
                 */
                @XmlAccessorType(XmlAccessType.FIELD)
                @XmlType(name = "",
                         propOrder = {"degrees",
                                      "minutes",
                                      "seconds"})
                public static class GPSLatitude {

                    protected RationalType degrees;
                    protected RationalType minutes;
                    protected RationalType seconds;

                    /**
                     * Obtient la valeur de la propriété degrees.
                     *
                     * @return
                     *         possible object is
                     *         {@link RationalType }
                     *
                     */
                    public RationalType getDegrees() {
                        return degrees;
                    }

                    /**
                     * Définit la valeur de la propriété degrees.
                     *
                     * @param value
                     *            allowed object is
                     *            {@link RationalType }
                     *
                     */
                    public void setDegrees(RationalType value) {
                        this.degrees = value;
                    }

                    /**
                     * Obtient la valeur de la propriété minutes.
                     *
                     * @return
                     *         possible object is
                     *         {@link RationalType }
                     *
                     */
                    public RationalType getMinutes() {
                        return minutes;
                    }

                    /**
                     * Définit la valeur de la propriété minutes.
                     *
                     * @param value
                     *            allowed object is
                     *            {@link RationalType }
                     *
                     */
                    public void setMinutes(RationalType value) {
                        this.minutes = value;
                    }

                    /**
                     * Obtient la valeur de la propriété seconds.
                     *
                     * @return
                     *         possible object is
                     *         {@link RationalType }
                     *
                     */
                    public RationalType getSeconds() {
                        return seconds;
                    }

                    /**
                     * Définit la valeur de la propriété seconds.
                     *
                     * @param value
                     *            allowed object is
                     *            {@link RationalType }
                     *
                     */
                    public void setSeconds(RationalType value) {
                        this.seconds = value;
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
                 *       &lt;group ref="{http://www.loc.gov/mix/v20}gpsGroup"/>
                 *     &lt;/restriction>
                 *   &lt;/complexContent>
                 * &lt;/complexType>
                 * </pre>
                 *
                 *
                 */
                @XmlAccessorType(XmlAccessType.FIELD)
                @XmlType(name = "",
                         propOrder = {"degrees",
                                      "minutes",
                                      "seconds"})
                public static class GPSLongitude {

                    protected RationalType degrees;
                    protected RationalType minutes;
                    protected RationalType seconds;

                    /**
                     * Obtient la valeur de la propriété degrees.
                     *
                     * @return
                     *         possible object is
                     *         {@link RationalType }
                     *
                     */
                    public RationalType getDegrees() {
                        return degrees;
                    }

                    /**
                     * Définit la valeur de la propriété degrees.
                     *
                     * @param value
                     *            allowed object is
                     *            {@link RationalType }
                     *
                     */
                    public void setDegrees(RationalType value) {
                        this.degrees = value;
                    }

                    /**
                     * Obtient la valeur de la propriété minutes.
                     *
                     * @return
                     *         possible object is
                     *         {@link RationalType }
                     *
                     */
                    public RationalType getMinutes() {
                        return minutes;
                    }

                    /**
                     * Définit la valeur de la propriété minutes.
                     *
                     * @param value
                     *            allowed object is
                     *            {@link RationalType }
                     *
                     */
                    public void setMinutes(RationalType value) {
                        this.minutes = value;
                    }

                    /**
                     * Obtient la valeur de la propriété seconds.
                     *
                     * @return
                     *         possible object is
                     *         {@link RationalType }
                     *
                     */
                    public RationalType getSeconds() {
                        return seconds;
                    }

                    /**
                     * Définit la valeur de la propriété seconds.
                     *
                     * @param value
                     *            allowed object is
                     *            {@link RationalType }
                     *
                     */
                    public void setSeconds(RationalType value) {
                        this.seconds = value;
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
             *         &lt;element name="fNumber" type="{http://www.loc.gov/mix/v20}typeOfNonNegativeRealType" minOccurs="0"/>
             *         &lt;element name="exposureTime" type="{http://www.loc.gov/mix/v20}typeOfNonNegativeRealType" minOccurs="0"/>
             *         &lt;element name="exposureProgram" type="{http://www.loc.gov/mix/v20}typeOfExposureProgramType" minOccurs="0"/>
             *         &lt;element name="spectralSensitivity" type="{http://www.loc.gov/mix/v20}stringType" maxOccurs="unbounded" minOccurs="0"/>
             *         &lt;element name="isoSpeedRatings" type="{http://www.loc.gov/mix/v20}positiveIntegerType" minOccurs="0"/>
             *         &lt;element name="oECF" type="{http://www.loc.gov/mix/v20}rationalType" minOccurs="0"/>
             *         &lt;element name="exifVersion" type="{http://www.loc.gov/mix/v20}typeOfExifVersionType" minOccurs="0"/>
             *         &lt;element name="shutterSpeedValue" type="{http://www.loc.gov/mix/v20}rationalType" minOccurs="0"/>
             *         &lt;element name="apertureValue" type="{http://www.loc.gov/mix/v20}rationalType" minOccurs="0"/>
             *         &lt;element name="brightnessValue" type="{http://www.loc.gov/mix/v20}rationalType" minOccurs="0"/>
             *         &lt;element name="exposureBiasValue" type="{http://www.loc.gov/mix/v20}rationalType" minOccurs="0"/>
             *         &lt;element name="maxApertureValue" type="{http://www.loc.gov/mix/v20}rationalType" minOccurs="0"/>
             *         &lt;element name="SubjectDistance" minOccurs="0">
             *           &lt;complexType>
             *             &lt;complexContent>
             *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
             *                 &lt;sequence>
             *                   &lt;element name="distance" type="{http://www.loc.gov/mix/v20}typeOfNonNegativeDecimalType" minOccurs="0"/>
             *                   &lt;element name="MinMaxDistance" minOccurs="0">
             *                     &lt;complexType>
             *                       &lt;complexContent>
             *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
             *                           &lt;sequence>
             *                             &lt;element name="minDistance" type="{http://www.loc.gov/mix/v20}typeOfNonNegativeDecimalType" minOccurs="0"/>
             *                             &lt;element name="maxDistance" type="{http://www.loc.gov/mix/v20}typeOfNonNegativeDecimalType" minOccurs="0"/>
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
             *         &lt;element name="meteringMode" type="{http://www.loc.gov/mix/v20}typeOfMeteringModeType" minOccurs="0"/>
             *         &lt;element name="lightSource" type="{http://www.loc.gov/mix/v20}typeOfLightSourceType" minOccurs="0"/>
             *         &lt;element name="flash" type="{http://www.loc.gov/mix/v20}typeOfFlashType" minOccurs="0"/>
             *         &lt;element name="focalLength" type="{http://www.loc.gov/mix/v20}typeOfNonNegativeDecimalType" minOccurs="0"/>
             *         &lt;element name="flashEnergy" type="{http://www.loc.gov/mix/v20}rationalType" minOccurs="0"/>
             *         &lt;element name="backLight" type="{http://www.loc.gov/mix/v20}typeOfBackLightType" minOccurs="0"/>
             *         &lt;element name="exposureIndex" type="{http://www.loc.gov/mix/v20}typeOfPositiveRealType" minOccurs="0"/>
             *         &lt;element name="sensingMethod" type="{http://www.loc.gov/mix/v20}typeOfSensingMethodType" minOccurs="0"/>
             *         &lt;element name="cfaPattern" type="{http://www.loc.gov/mix/v20}integerType" minOccurs="0"/>
             *         &lt;element name="autoFocus" type="{http://www.loc.gov/mix/v20}typeOfAutoFocusType" minOccurs="0"/>
             *         &lt;element name="PrintAspectRatio" minOccurs="0">
             *           &lt;complexType>
             *             &lt;complexContent>
             *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
             *                 &lt;sequence>
             *                   &lt;element name="xPrintAspectRatio" type="{http://www.loc.gov/mix/v20}typeOfNonNegativeRealType" minOccurs="0"/>
             *                   &lt;element name="yPrintAspectRatio" type="{http://www.loc.gov/mix/v20}typeOfNonNegativeRealType" minOccurs="0"/>
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
                     propOrder = {"fNumber",
                                  "exposureTime",
                                  "exposureProgram",
                                  "spectralSensitivity",
                                  "isoSpeedRatings",
                                  "oecf",
                                  "exifVersion",
                                  "shutterSpeedValue",
                                  "apertureValue",
                                  "brightnessValue",
                                  "exposureBiasValue",
                                  "maxApertureValue",
                                  "subjectDistance",
                                  "meteringMode",
                                  "lightSource",
                                  "flash",
                                  "focalLength",
                                  "flashEnergy",
                                  "backLight",
                                  "exposureIndex",
                                  "sensingMethod",
                                  "cfaPattern",
                                  "autoFocus",
                                  "printAspectRatio"})
            public static class ImageData {

                protected TypeOfNonNegativeRealType fNumber;
                protected TypeOfNonNegativeRealType exposureTime;
                protected TypeOfExposureProgramType exposureProgram;
                protected List<StringType> spectralSensitivity;
                protected PositiveIntegerType isoSpeedRatings;
                @XmlElement(name = "oECF")
                protected RationalType oecf;
                protected TypeOfExifVersionType exifVersion;
                protected RationalType shutterSpeedValue;
                protected RationalType apertureValue;
                protected RationalType brightnessValue;
                protected RationalType exposureBiasValue;
                protected RationalType maxApertureValue;
                @XmlElement(name = "SubjectDistance")
                protected ImageCaptureMetadataType.DigitalCameraCapture.CameraCaptureSettings.ImageData.SubjectDistance subjectDistance;
                protected TypeOfMeteringModeType meteringMode;
                protected TypeOfLightSourceType lightSource;
                protected TypeOfFlashType flash;
                protected TypeOfNonNegativeDecimalType focalLength;
                protected RationalType flashEnergy;
                protected TypeOfBackLightType backLight;
                protected TypeOfPositiveRealType exposureIndex;
                protected TypeOfSensingMethodType sensingMethod;
                protected IntegerType cfaPattern;
                protected TypeOfAutoFocusType autoFocus;
                @XmlElement(name = "PrintAspectRatio")
                protected ImageCaptureMetadataType.DigitalCameraCapture.CameraCaptureSettings.ImageData.PrintAspectRatio printAspectRatio;

                /**
                 * Obtient la valeur de la propriété fNumber.
                 *
                 * @return
                 *         possible object is
                 *         {@link TypeOfNonNegativeRealType }
                 *
                 */
                public TypeOfNonNegativeRealType getFNumber() {
                    return fNumber;
                }

                /**
                 * Définit la valeur de la propriété fNumber.
                 *
                 * @param value
                 *            allowed object is
                 *            {@link TypeOfNonNegativeRealType }
                 *
                 */
                public void setFNumber(TypeOfNonNegativeRealType value) {
                    this.fNumber = value;
                }

                /**
                 * Obtient la valeur de la propriété exposureTime.
                 *
                 * @return
                 *         possible object is
                 *         {@link TypeOfNonNegativeRealType }
                 *
                 */
                public TypeOfNonNegativeRealType getExposureTime() {
                    return exposureTime;
                }

                /**
                 * Définit la valeur de la propriété exposureTime.
                 *
                 * @param value
                 *            allowed object is
                 *            {@link TypeOfNonNegativeRealType }
                 *
                 */
                public void setExposureTime(TypeOfNonNegativeRealType value) {
                    this.exposureTime = value;
                }

                /**
                 * Obtient la valeur de la propriété exposureProgram.
                 *
                 * @return
                 *         possible object is
                 *         {@link TypeOfExposureProgramType }
                 *
                 */
                public TypeOfExposureProgramType getExposureProgram() {
                    return exposureProgram;
                }

                /**
                 * Définit la valeur de la propriété exposureProgram.
                 *
                 * @param value
                 *            allowed object is
                 *            {@link TypeOfExposureProgramType }
                 *
                 */
                public void setExposureProgram(TypeOfExposureProgramType value) {
                    this.exposureProgram = value;
                }

                /**
                 * Gets the value of the spectralSensitivity property.
                 *
                 * <p>
                 * This accessor method returns a reference to the live list,
                 * not a snapshot. Therefore any modification you make to the
                 * returned list will be present inside the JAXB object.
                 * This is why there is not a <CODE>set</CODE> method for the spectralSensitivity property.
                 *
                 * <p>
                 * For example, to add a new item, do as follows:
                 *
                 * <pre>
                 * getSpectralSensitivity().add(newItem);
                 * </pre>
                 *
                 *
                 * <p>
                 * Objects of the following type(s) are allowed in the list
                 * {@link StringType }
                 *
                 *
                 */
                public List<StringType> getSpectralSensitivity() {
                    if (spectralSensitivity == null) {
                        spectralSensitivity = new ArrayList<>();
                    }
                    return this.spectralSensitivity;
                }

                /**
                 * Obtient la valeur de la propriété isoSpeedRatings.
                 *
                 * @return
                 *         possible object is
                 *         {@link PositiveIntegerType }
                 *
                 */
                public PositiveIntegerType getIsoSpeedRatings() {
                    return isoSpeedRatings;
                }

                /**
                 * Définit la valeur de la propriété isoSpeedRatings.
                 *
                 * @param value
                 *            allowed object is
                 *            {@link PositiveIntegerType }
                 *
                 */
                public void setIsoSpeedRatings(PositiveIntegerType value) {
                    this.isoSpeedRatings = value;
                }

                /**
                 * Obtient la valeur de la propriété oecf.
                 *
                 * @return
                 *         possible object is
                 *         {@link RationalType }
                 *
                 */
                public RationalType getOECF() {
                    return oecf;
                }

                /**
                 * Définit la valeur de la propriété oecf.
                 *
                 * @param value
                 *            allowed object is
                 *            {@link RationalType }
                 *
                 */
                public void setOECF(RationalType value) {
                    this.oecf = value;
                }

                /**
                 * Obtient la valeur de la propriété exifVersion.
                 *
                 * @return
                 *         possible object is
                 *         {@link TypeOfExifVersionType }
                 *
                 */
                public TypeOfExifVersionType getExifVersion() {
                    return exifVersion;
                }

                /**
                 * Définit la valeur de la propriété exifVersion.
                 *
                 * @param value
                 *            allowed object is
                 *            {@link TypeOfExifVersionType }
                 *
                 */
                public void setExifVersion(TypeOfExifVersionType value) {
                    this.exifVersion = value;
                }

                /**
                 * Obtient la valeur de la propriété shutterSpeedValue.
                 *
                 * @return
                 *         possible object is
                 *         {@link RationalType }
                 *
                 */
                public RationalType getShutterSpeedValue() {
                    return shutterSpeedValue;
                }

                /**
                 * Définit la valeur de la propriété shutterSpeedValue.
                 *
                 * @param value
                 *            allowed object is
                 *            {@link RationalType }
                 *
                 */
                public void setShutterSpeedValue(RationalType value) {
                    this.shutterSpeedValue = value;
                }

                /**
                 * Obtient la valeur de la propriété apertureValue.
                 *
                 * @return
                 *         possible object is
                 *         {@link RationalType }
                 *
                 */
                public RationalType getApertureValue() {
                    return apertureValue;
                }

                /**
                 * Définit la valeur de la propriété apertureValue.
                 *
                 * @param value
                 *            allowed object is
                 *            {@link RationalType }
                 *
                 */
                public void setApertureValue(RationalType value) {
                    this.apertureValue = value;
                }

                /**
                 * Obtient la valeur de la propriété brightnessValue.
                 *
                 * @return
                 *         possible object is
                 *         {@link RationalType }
                 *
                 */
                public RationalType getBrightnessValue() {
                    return brightnessValue;
                }

                /**
                 * Définit la valeur de la propriété brightnessValue.
                 *
                 * @param value
                 *            allowed object is
                 *            {@link RationalType }
                 *
                 */
                public void setBrightnessValue(RationalType value) {
                    this.brightnessValue = value;
                }

                /**
                 * Obtient la valeur de la propriété exposureBiasValue.
                 *
                 * @return
                 *         possible object is
                 *         {@link RationalType }
                 *
                 */
                public RationalType getExposureBiasValue() {
                    return exposureBiasValue;
                }

                /**
                 * Définit la valeur de la propriété exposureBiasValue.
                 *
                 * @param value
                 *            allowed object is
                 *            {@link RationalType }
                 *
                 */
                public void setExposureBiasValue(RationalType value) {
                    this.exposureBiasValue = value;
                }

                /**
                 * Obtient la valeur de la propriété maxApertureValue.
                 *
                 * @return
                 *         possible object is
                 *         {@link RationalType }
                 *
                 */
                public RationalType getMaxApertureValue() {
                    return maxApertureValue;
                }

                /**
                 * Définit la valeur de la propriété maxApertureValue.
                 *
                 * @param value
                 *            allowed object is
                 *            {@link RationalType }
                 *
                 */
                public void setMaxApertureValue(RationalType value) {
                    this.maxApertureValue = value;
                }

                /**
                 * Obtient la valeur de la propriété subjectDistance.
                 *
                 * @return
                 *         possible object is
                 *         {@link ImageCaptureMetadataType.DigitalCameraCapture.CameraCaptureSettings.ImageData.SubjectDistance }
                 *
                 */
                public ImageCaptureMetadataType.DigitalCameraCapture.CameraCaptureSettings.ImageData.SubjectDistance getSubjectDistance() {
                    return subjectDistance;
                }

                /**
                 * Définit la valeur de la propriété subjectDistance.
                 *
                 * @param value
                 *            allowed object is
                 *            {@link ImageCaptureMetadataType.DigitalCameraCapture.CameraCaptureSettings.ImageData.SubjectDistance }
                 *
                 */
                public void setSubjectDistance(ImageCaptureMetadataType.DigitalCameraCapture.CameraCaptureSettings.ImageData.SubjectDistance value) {
                    this.subjectDistance = value;
                }

                /**
                 * Obtient la valeur de la propriété meteringMode.
                 *
                 * @return
                 *         possible object is
                 *         {@link TypeOfMeteringModeType }
                 *
                 */
                public TypeOfMeteringModeType getMeteringMode() {
                    return meteringMode;
                }

                /**
                 * Définit la valeur de la propriété meteringMode.
                 *
                 * @param value
                 *            allowed object is
                 *            {@link TypeOfMeteringModeType }
                 *
                 */
                public void setMeteringMode(TypeOfMeteringModeType value) {
                    this.meteringMode = value;
                }

                /**
                 * Obtient la valeur de la propriété lightSource.
                 *
                 * @return
                 *         possible object is
                 *         {@link TypeOfLightSourceType }
                 *
                 */
                public TypeOfLightSourceType getLightSource() {
                    return lightSource;
                }

                /**
                 * Définit la valeur de la propriété lightSource.
                 *
                 * @param value
                 *            allowed object is
                 *            {@link TypeOfLightSourceType }
                 *
                 */
                public void setLightSource(TypeOfLightSourceType value) {
                    this.lightSource = value;
                }

                /**
                 * Obtient la valeur de la propriété flash.
                 *
                 * @return
                 *         possible object is
                 *         {@link TypeOfFlashType }
                 *
                 */
                public TypeOfFlashType getFlash() {
                    return flash;
                }

                /**
                 * Définit la valeur de la propriété flash.
                 *
                 * @param value
                 *            allowed object is
                 *            {@link TypeOfFlashType }
                 *
                 */
                public void setFlash(TypeOfFlashType value) {
                    this.flash = value;
                }

                /**
                 * Obtient la valeur de la propriété focalLength.
                 *
                 * @return
                 *         possible object is
                 *         {@link TypeOfNonNegativeDecimalType }
                 *
                 */
                public TypeOfNonNegativeDecimalType getFocalLength() {
                    return focalLength;
                }

                /**
                 * Définit la valeur de la propriété focalLength.
                 *
                 * @param value
                 *            allowed object is
                 *            {@link TypeOfNonNegativeDecimalType }
                 *
                 */
                public void setFocalLength(TypeOfNonNegativeDecimalType value) {
                    this.focalLength = value;
                }

                /**
                 * Obtient la valeur de la propriété flashEnergy.
                 *
                 * @return
                 *         possible object is
                 *         {@link RationalType }
                 *
                 */
                public RationalType getFlashEnergy() {
                    return flashEnergy;
                }

                /**
                 * Définit la valeur de la propriété flashEnergy.
                 *
                 * @param value
                 *            allowed object is
                 *            {@link RationalType }
                 *
                 */
                public void setFlashEnergy(RationalType value) {
                    this.flashEnergy = value;
                }

                /**
                 * Obtient la valeur de la propriété backLight.
                 *
                 * @return
                 *         possible object is
                 *         {@link TypeOfBackLightType }
                 *
                 */
                public TypeOfBackLightType getBackLight() {
                    return backLight;
                }

                /**
                 * Définit la valeur de la propriété backLight.
                 *
                 * @param value
                 *            allowed object is
                 *            {@link TypeOfBackLightType }
                 *
                 */
                public void setBackLight(TypeOfBackLightType value) {
                    this.backLight = value;
                }

                /**
                 * Obtient la valeur de la propriété exposureIndex.
                 *
                 * @return
                 *         possible object is
                 *         {@link TypeOfPositiveRealType }
                 *
                 */
                public TypeOfPositiveRealType getExposureIndex() {
                    return exposureIndex;
                }

                /**
                 * Définit la valeur de la propriété exposureIndex.
                 *
                 * @param value
                 *            allowed object is
                 *            {@link TypeOfPositiveRealType }
                 *
                 */
                public void setExposureIndex(TypeOfPositiveRealType value) {
                    this.exposureIndex = value;
                }

                /**
                 * Obtient la valeur de la propriété sensingMethod.
                 *
                 * @return
                 *         possible object is
                 *         {@link TypeOfSensingMethodType }
                 *
                 */
                public TypeOfSensingMethodType getSensingMethod() {
                    return sensingMethod;
                }

                /**
                 * Définit la valeur de la propriété sensingMethod.
                 *
                 * @param value
                 *            allowed object is
                 *            {@link TypeOfSensingMethodType }
                 *
                 */
                public void setSensingMethod(TypeOfSensingMethodType value) {
                    this.sensingMethod = value;
                }

                /**
                 * Obtient la valeur de la propriété cfaPattern.
                 *
                 * @return
                 *         possible object is
                 *         {@link IntegerType }
                 *
                 */
                public IntegerType getCfaPattern() {
                    return cfaPattern;
                }

                /**
                 * Définit la valeur de la propriété cfaPattern.
                 *
                 * @param value
                 *            allowed object is
                 *            {@link IntegerType }
                 *
                 */
                public void setCfaPattern(IntegerType value) {
                    this.cfaPattern = value;
                }

                /**
                 * Obtient la valeur de la propriété autoFocus.
                 *
                 * @return
                 *         possible object is
                 *         {@link TypeOfAutoFocusType }
                 *
                 */
                public TypeOfAutoFocusType getAutoFocus() {
                    return autoFocus;
                }

                /**
                 * Définit la valeur de la propriété autoFocus.
                 *
                 * @param value
                 *            allowed object is
                 *            {@link TypeOfAutoFocusType }
                 *
                 */
                public void setAutoFocus(TypeOfAutoFocusType value) {
                    this.autoFocus = value;
                }

                /**
                 * Obtient la valeur de la propriété printAspectRatio.
                 *
                 * @return
                 *         possible object is
                 *         {@link ImageCaptureMetadataType.DigitalCameraCapture.CameraCaptureSettings.ImageData.PrintAspectRatio }
                 *
                 */
                public ImageCaptureMetadataType.DigitalCameraCapture.CameraCaptureSettings.ImageData.PrintAspectRatio getPrintAspectRatio() {
                    return printAspectRatio;
                }

                /**
                 * Définit la valeur de la propriété printAspectRatio.
                 *
                 * @param value
                 *            allowed object is
                 *            {@link ImageCaptureMetadataType.DigitalCameraCapture.CameraCaptureSettings.ImageData.PrintAspectRatio }
                 *
                 */
                public void setPrintAspectRatio(ImageCaptureMetadataType.DigitalCameraCapture.CameraCaptureSettings.ImageData.PrintAspectRatio value) {
                    this.printAspectRatio = value;
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
                 *         &lt;element name="xPrintAspectRatio" type="{http://www.loc.gov/mix/v20}typeOfNonNegativeRealType" minOccurs="0"/>
                 *         &lt;element name="yPrintAspectRatio" type="{http://www.loc.gov/mix/v20}typeOfNonNegativeRealType" minOccurs="0"/>
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
                         propOrder = {"xPrintAspectRatio",
                                      "yPrintAspectRatio"})
                public static class PrintAspectRatio {

                    protected TypeOfNonNegativeRealType xPrintAspectRatio;
                    protected TypeOfNonNegativeRealType yPrintAspectRatio;

                    /**
                     * Obtient la valeur de la propriété xPrintAspectRatio.
                     *
                     * @return
                     *         possible object is
                     *         {@link TypeOfNonNegativeRealType }
                     *
                     */
                    public TypeOfNonNegativeRealType getXPrintAspectRatio() {
                        return xPrintAspectRatio;
                    }

                    /**
                     * Définit la valeur de la propriété xPrintAspectRatio.
                     *
                     * @param value
                     *            allowed object is
                     *            {@link TypeOfNonNegativeRealType }
                     *
                     */
                    public void setXPrintAspectRatio(TypeOfNonNegativeRealType value) {
                        this.xPrintAspectRatio = value;
                    }

                    /**
                     * Obtient la valeur de la propriété yPrintAspectRatio.
                     *
                     * @return
                     *         possible object is
                     *         {@link TypeOfNonNegativeRealType }
                     *
                     */
                    public TypeOfNonNegativeRealType getYPrintAspectRatio() {
                        return yPrintAspectRatio;
                    }

                    /**
                     * Définit la valeur de la propriété yPrintAspectRatio.
                     *
                     * @param value
                     *            allowed object is
                     *            {@link TypeOfNonNegativeRealType }
                     *
                     */
                    public void setYPrintAspectRatio(TypeOfNonNegativeRealType value) {
                        this.yPrintAspectRatio = value;
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
                 *         &lt;element name="distance" type="{http://www.loc.gov/mix/v20}typeOfNonNegativeDecimalType" minOccurs="0"/>
                 *         &lt;element name="MinMaxDistance" minOccurs="0">
                 *           &lt;complexType>
                 *             &lt;complexContent>
                 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
                 *                 &lt;sequence>
                 *                   &lt;element name="minDistance" type="{http://www.loc.gov/mix/v20}typeOfNonNegativeDecimalType" minOccurs="0"/>
                 *                   &lt;element name="maxDistance" type="{http://www.loc.gov/mix/v20}typeOfNonNegativeDecimalType" minOccurs="0"/>
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
                         propOrder = {"distance",
                                      "minMaxDistance"})
                public static class SubjectDistance {

                    protected TypeOfNonNegativeDecimalType distance;
                    @XmlElement(name = "MinMaxDistance")
                    protected ImageCaptureMetadataType.DigitalCameraCapture.CameraCaptureSettings.ImageData.SubjectDistance.MinMaxDistance minMaxDistance;

                    /**
                     * Obtient la valeur de la propriété distance.
                     *
                     * @return
                     *         possible object is
                     *         {@link TypeOfNonNegativeDecimalType }
                     *
                     */
                    public TypeOfNonNegativeDecimalType getDistance() {
                        return distance;
                    }

                    /**
                     * Définit la valeur de la propriété distance.
                     *
                     * @param value
                     *            allowed object is
                     *            {@link TypeOfNonNegativeDecimalType }
                     *
                     */
                    public void setDistance(TypeOfNonNegativeDecimalType value) {
                        this.distance = value;
                    }

                    /**
                     * Obtient la valeur de la propriété minMaxDistance.
                     *
                     * @return
                     *         possible object is
                     *         {@link ImageCaptureMetadataType.DigitalCameraCapture.CameraCaptureSettings.ImageData.SubjectDistance.MinMaxDistance }
                     *
                     */
                    public ImageCaptureMetadataType.DigitalCameraCapture.CameraCaptureSettings.ImageData.SubjectDistance.MinMaxDistance getMinMaxDistance() {
                        return minMaxDistance;
                    }

                    /**
                     * Définit la valeur de la propriété minMaxDistance.
                     *
                     * @param value
                     *            allowed object is
                     *            {@link ImageCaptureMetadataType.DigitalCameraCapture.CameraCaptureSettings.ImageData.SubjectDistance.MinMaxDistance }
                     *
                     */
                    public void setMinMaxDistance(ImageCaptureMetadataType.DigitalCameraCapture.CameraCaptureSettings.ImageData.SubjectDistance.MinMaxDistance value) {
                        this.minMaxDistance = value;
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
                     *         &lt;element name="minDistance" type="{http://www.loc.gov/mix/v20}typeOfNonNegativeDecimalType" minOccurs="0"/>
                     *         &lt;element name="maxDistance" type="{http://www.loc.gov/mix/v20}typeOfNonNegativeDecimalType" minOccurs="0"/>
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
                             propOrder = {"minDistance",
                                          "maxDistance"})
                    public static class MinMaxDistance {

                        protected TypeOfNonNegativeDecimalType minDistance;
                        protected TypeOfNonNegativeDecimalType maxDistance;

                        /**
                         * Obtient la valeur de la propriété minDistance.
                         *
                         * @return
                         *         possible object is
                         *         {@link TypeOfNonNegativeDecimalType }
                         *
                         */
                        public TypeOfNonNegativeDecimalType getMinDistance() {
                            return minDistance;
                        }

                        /**
                         * Définit la valeur de la propriété minDistance.
                         *
                         * @param value
                         *            allowed object is
                         *            {@link TypeOfNonNegativeDecimalType }
                         *
                         */
                        public void setMinDistance(TypeOfNonNegativeDecimalType value) {
                            this.minDistance = value;
                        }

                        /**
                         * Obtient la valeur de la propriété maxDistance.
                         *
                         * @return
                         *         possible object is
                         *         {@link TypeOfNonNegativeDecimalType }
                         *
                         */
                        public TypeOfNonNegativeDecimalType getMaxDistance() {
                            return maxDistance;
                        }

                        /**
                         * Définit la valeur de la propriété maxDistance.
                         *
                         * @param value
                         *            allowed object is
                         *            {@link TypeOfNonNegativeDecimalType }
                         *
                         */
                        public void setMaxDistance(TypeOfNonNegativeDecimalType value) {
                            this.maxDistance = value;
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
         *         &lt;element name="digitalCameraModelName" type="{http://www.loc.gov/mix/v20}stringType" minOccurs="0"/>
         *         &lt;element name="digitalCameraModelNumber" type="{http://www.loc.gov/mix/v20}stringType" minOccurs="0"/>
         *         &lt;element name="digitalCameraModelSerialNo" type="{http://www.loc.gov/mix/v20}stringType" minOccurs="0"/>
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
                 propOrder = {"digitalCameraModelName",
                              "digitalCameraModelNumber",
                              "digitalCameraModelSerialNo"})
        public static class DigitalCameraModel {

            protected StringType digitalCameraModelName;
            protected StringType digitalCameraModelNumber;
            protected StringType digitalCameraModelSerialNo;

            /**
             * Obtient la valeur de la propriété digitalCameraModelName.
             *
             * @return
             *         possible object is
             *         {@link StringType }
             *
             */
            public StringType getDigitalCameraModelName() {
                return digitalCameraModelName;
            }

            /**
             * Définit la valeur de la propriété digitalCameraModelName.
             *
             * @param value
             *            allowed object is
             *            {@link StringType }
             *
             */
            public void setDigitalCameraModelName(StringType value) {
                this.digitalCameraModelName = value;
            }

            /**
             * Obtient la valeur de la propriété digitalCameraModelNumber.
             *
             * @return
             *         possible object is
             *         {@link StringType }
             *
             */
            public StringType getDigitalCameraModelNumber() {
                return digitalCameraModelNumber;
            }

            /**
             * Définit la valeur de la propriété digitalCameraModelNumber.
             *
             * @param value
             *            allowed object is
             *            {@link StringType }
             *
             */
            public void setDigitalCameraModelNumber(StringType value) {
                this.digitalCameraModelNumber = value;
            }

            /**
             * Obtient la valeur de la propriété digitalCameraModelSerialNo.
             *
             * @return
             *         possible object is
             *         {@link StringType }
             *
             */
            public StringType getDigitalCameraModelSerialNo() {
                return digitalCameraModelSerialNo;
            }

            /**
             * Définit la valeur de la propriété digitalCameraModelSerialNo.
             *
             * @param value
             *            allowed object is
             *            {@link StringType }
             *
             */
            public void setDigitalCameraModelSerialNo(StringType value) {
                this.digitalCameraModelSerialNo = value;
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
     *         &lt;element name="dateTimeCreated" type="{http://www.loc.gov/mix/v20}typeOfDateType" minOccurs="0"/>
     *         &lt;element name="imageProducer" type="{http://www.loc.gov/mix/v20}stringType" maxOccurs="unbounded" minOccurs="0"/>
     *         &lt;element name="captureDevice" type="{http://www.loc.gov/mix/v20}typeOfCaptureDeviceType" minOccurs="0"/>
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
             propOrder = {"dateTimeCreated",
                          "imageProducer",
                          "captureDevice"})
    public static class GeneralCaptureInformation {

        protected TypeOfDateType dateTimeCreated;
        protected List<StringType> imageProducer;
        protected TypeOfCaptureDeviceType captureDevice;

        /**
         * Obtient la valeur de la propriété dateTimeCreated.
         *
         * @return
         *         possible object is
         *         {@link TypeOfDateType }
         *
         */
        public TypeOfDateType getDateTimeCreated() {
            return dateTimeCreated;
        }

        /**
         * Définit la valeur de la propriété dateTimeCreated.
         *
         * @param value
         *            allowed object is
         *            {@link TypeOfDateType }
         *
         */
        public void setDateTimeCreated(TypeOfDateType value) {
            this.dateTimeCreated = value;
        }

        /**
         * Gets the value of the imageProducer property.
         *
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the imageProducer property.
         *
         * <p>
         * For example, to add a new item, do as follows:
         *
         * <pre>
         * getImageProducer().add(newItem);
         * </pre>
         *
         *
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link StringType }
         *
         *
         */
        public List<StringType> getImageProducer() {
            if (imageProducer == null) {
                imageProducer = new ArrayList<>();
            }
            return this.imageProducer;
        }

        /**
         * Obtient la valeur de la propriété captureDevice.
         *
         * @return
         *         possible object is
         *         {@link TypeOfCaptureDeviceType }
         *
         */
        public TypeOfCaptureDeviceType getCaptureDevice() {
            return captureDevice;
        }

        /**
         * Définit la valeur de la propriété captureDevice.
         *
         * @param value
         *            allowed object is
         *            {@link TypeOfCaptureDeviceType }
         *
         */
        public void setCaptureDevice(TypeOfCaptureDeviceType value) {
            this.captureDevice = value;
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
     *         &lt;element name="scannerManufacturer" type="{http://www.loc.gov/mix/v20}stringType" minOccurs="0"/>
     *         &lt;element name="ScannerModel" minOccurs="0">
     *           &lt;complexType>
     *             &lt;complexContent>
     *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *                 &lt;sequence>
     *                   &lt;element name="scannerModelName" type="{http://www.loc.gov/mix/v20}stringType" minOccurs="0"/>
     *                   &lt;element name="scannerModelNumber" type="{http://www.loc.gov/mix/v20}stringType" minOccurs="0"/>
     *                   &lt;element name="scannerModelSerialNo" type="{http://www.loc.gov/mix/v20}stringType" minOccurs="0"/>
     *                 &lt;/sequence>
     *               &lt;/restriction>
     *             &lt;/complexContent>
     *           &lt;/complexType>
     *         &lt;/element>
     *         &lt;element name="MaximumOpticalResolution" minOccurs="0">
     *           &lt;complexType>
     *             &lt;complexContent>
     *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *                 &lt;sequence>
     *                   &lt;element name="xOpticalResolution" type="{http://www.loc.gov/mix/v20}positiveIntegerType" minOccurs="0"/>
     *                   &lt;element name="yOpticalResolution" type="{http://www.loc.gov/mix/v20}positiveIntegerType" minOccurs="0"/>
     *                   &lt;element name="opticalResolutionUnit" type="{http://www.loc.gov/mix/v20}typeOfOpticalResolutionUnitType" minOccurs="0"/>
     *                 &lt;/sequence>
     *               &lt;/restriction>
     *             &lt;/complexContent>
     *           &lt;/complexType>
     *         &lt;/element>
     *         &lt;element name="scannerSensor" type="{http://www.loc.gov/mix/v20}typeOfScannerSensorType" minOccurs="0"/>
     *         &lt;element name="ScanningSystemSoftware" minOccurs="0">
     *           &lt;complexType>
     *             &lt;complexContent>
     *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *                 &lt;sequence>
     *                   &lt;element name="scanningSoftwareName" type="{http://www.loc.gov/mix/v20}stringType" minOccurs="0"/>
     *                   &lt;element name="scanningSoftwareVersionNo" type="{http://www.loc.gov/mix/v20}stringType" minOccurs="0"/>
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
             propOrder = {"scannerManufacturer",
                          "scannerModel",
                          "maximumOpticalResolution",
                          "scannerSensor",
                          "scanningSystemSoftware"})
    public static class ScannerCapture {

        protected StringType scannerManufacturer;
        @XmlElement(name = "ScannerModel")
        protected ImageCaptureMetadataType.ScannerCapture.ScannerModel scannerModel;
        @XmlElement(name = "MaximumOpticalResolution")
        protected ImageCaptureMetadataType.ScannerCapture.MaximumOpticalResolution maximumOpticalResolution;
        protected TypeOfScannerSensorType scannerSensor;
        @XmlElement(name = "ScanningSystemSoftware")
        protected ImageCaptureMetadataType.ScannerCapture.ScanningSystemSoftware scanningSystemSoftware;

        /**
         * Obtient la valeur de la propriété scannerManufacturer.
         *
         * @return
         *         possible object is
         *         {@link StringType }
         *
         */
        public StringType getScannerManufacturer() {
            return scannerManufacturer;
        }

        /**
         * Définit la valeur de la propriété scannerManufacturer.
         *
         * @param value
         *            allowed object is
         *            {@link StringType }
         *
         */
        public void setScannerManufacturer(StringType value) {
            this.scannerManufacturer = value;
        }

        /**
         * Obtient la valeur de la propriété scannerModel.
         *
         * @return
         *         possible object is
         *         {@link ImageCaptureMetadataType.ScannerCapture.ScannerModel }
         *
         */
        public ImageCaptureMetadataType.ScannerCapture.ScannerModel getScannerModel() {
            return scannerModel;
        }

        /**
         * Définit la valeur de la propriété scannerModel.
         *
         * @param value
         *            allowed object is
         *            {@link ImageCaptureMetadataType.ScannerCapture.ScannerModel }
         *
         */
        public void setScannerModel(ImageCaptureMetadataType.ScannerCapture.ScannerModel value) {
            this.scannerModel = value;
        }

        /**
         * Obtient la valeur de la propriété maximumOpticalResolution.
         *
         * @return
         *         possible object is
         *         {@link ImageCaptureMetadataType.ScannerCapture.MaximumOpticalResolution }
         *
         */
        public ImageCaptureMetadataType.ScannerCapture.MaximumOpticalResolution getMaximumOpticalResolution() {
            return maximumOpticalResolution;
        }

        /**
         * Définit la valeur de la propriété maximumOpticalResolution.
         *
         * @param value
         *            allowed object is
         *            {@link ImageCaptureMetadataType.ScannerCapture.MaximumOpticalResolution }
         *
         */
        public void setMaximumOpticalResolution(ImageCaptureMetadataType.ScannerCapture.MaximumOpticalResolution value) {
            this.maximumOpticalResolution = value;
        }

        /**
         * Obtient la valeur de la propriété scannerSensor.
         *
         * @return
         *         possible object is
         *         {@link TypeOfScannerSensorType }
         *
         */
        public TypeOfScannerSensorType getScannerSensor() {
            return scannerSensor;
        }

        /**
         * Définit la valeur de la propriété scannerSensor.
         *
         * @param value
         *            allowed object is
         *            {@link TypeOfScannerSensorType }
         *
         */
        public void setScannerSensor(TypeOfScannerSensorType value) {
            this.scannerSensor = value;
        }

        /**
         * Obtient la valeur de la propriété scanningSystemSoftware.
         *
         * @return
         *         possible object is
         *         {@link ImageCaptureMetadataType.ScannerCapture.ScanningSystemSoftware }
         *
         */
        public ImageCaptureMetadataType.ScannerCapture.ScanningSystemSoftware getScanningSystemSoftware() {
            return scanningSystemSoftware;
        }

        /**
         * Définit la valeur de la propriété scanningSystemSoftware.
         *
         * @param value
         *            allowed object is
         *            {@link ImageCaptureMetadataType.ScannerCapture.ScanningSystemSoftware }
         *
         */
        public void setScanningSystemSoftware(ImageCaptureMetadataType.ScannerCapture.ScanningSystemSoftware value) {
            this.scanningSystemSoftware = value;
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
         *         &lt;element name="xOpticalResolution" type="{http://www.loc.gov/mix/v20}positiveIntegerType" minOccurs="0"/>
         *         &lt;element name="yOpticalResolution" type="{http://www.loc.gov/mix/v20}positiveIntegerType" minOccurs="0"/>
         *         &lt;element name="opticalResolutionUnit" type="{http://www.loc.gov/mix/v20}typeOfOpticalResolutionUnitType" minOccurs="0"/>
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
                 propOrder = {"xOpticalResolution",
                              "yOpticalResolution",
                              "opticalResolutionUnit"})
        public static class MaximumOpticalResolution {

            protected PositiveIntegerType xOpticalResolution;
            protected PositiveIntegerType yOpticalResolution;
            protected TypeOfOpticalResolutionUnitType opticalResolutionUnit;

            /**
             * Obtient la valeur de la propriété xOpticalResolution.
             *
             * @return
             *         possible object is
             *         {@link PositiveIntegerType }
             *
             */
            public PositiveIntegerType getXOpticalResolution() {
                return xOpticalResolution;
            }

            /**
             * Définit la valeur de la propriété xOpticalResolution.
             *
             * @param value
             *            allowed object is
             *            {@link PositiveIntegerType }
             *
             */
            public void setXOpticalResolution(PositiveIntegerType value) {
                this.xOpticalResolution = value;
            }

            /**
             * Obtient la valeur de la propriété yOpticalResolution.
             *
             * @return
             *         possible object is
             *         {@link PositiveIntegerType }
             *
             */
            public PositiveIntegerType getYOpticalResolution() {
                return yOpticalResolution;
            }

            /**
             * Définit la valeur de la propriété yOpticalResolution.
             *
             * @param value
             *            allowed object is
             *            {@link PositiveIntegerType }
             *
             */
            public void setYOpticalResolution(PositiveIntegerType value) {
                this.yOpticalResolution = value;
            }

            /**
             * Obtient la valeur de la propriété opticalResolutionUnit.
             *
             * @return
             *         possible object is
             *         {@link TypeOfOpticalResolutionUnitType }
             *
             */
            public TypeOfOpticalResolutionUnitType getOpticalResolutionUnit() {
                return opticalResolutionUnit;
            }

            /**
             * Définit la valeur de la propriété opticalResolutionUnit.
             *
             * @param value
             *            allowed object is
             *            {@link TypeOfOpticalResolutionUnitType }
             *
             */
            public void setOpticalResolutionUnit(TypeOfOpticalResolutionUnitType value) {
                this.opticalResolutionUnit = value;
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
         *         &lt;element name="scannerModelName" type="{http://www.loc.gov/mix/v20}stringType" minOccurs="0"/>
         *         &lt;element name="scannerModelNumber" type="{http://www.loc.gov/mix/v20}stringType" minOccurs="0"/>
         *         &lt;element name="scannerModelSerialNo" type="{http://www.loc.gov/mix/v20}stringType" minOccurs="0"/>
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
                 propOrder = {"scannerModelName",
                              "scannerModelNumber",
                              "scannerModelSerialNo"})
        public static class ScannerModel {

            protected StringType scannerModelName;
            protected StringType scannerModelNumber;
            protected StringType scannerModelSerialNo;

            /**
             * Obtient la valeur de la propriété scannerModelName.
             *
             * @return
             *         possible object is
             *         {@link StringType }
             *
             */
            public StringType getScannerModelName() {
                return scannerModelName;
            }

            /**
             * Définit la valeur de la propriété scannerModelName.
             *
             * @param value
             *            allowed object is
             *            {@link StringType }
             *
             */
            public void setScannerModelName(StringType value) {
                this.scannerModelName = value;
            }

            /**
             * Obtient la valeur de la propriété scannerModelNumber.
             *
             * @return
             *         possible object is
             *         {@link StringType }
             *
             */
            public StringType getScannerModelNumber() {
                return scannerModelNumber;
            }

            /**
             * Définit la valeur de la propriété scannerModelNumber.
             *
             * @param value
             *            allowed object is
             *            {@link StringType }
             *
             */
            public void setScannerModelNumber(StringType value) {
                this.scannerModelNumber = value;
            }

            /**
             * Obtient la valeur de la propriété scannerModelSerialNo.
             *
             * @return
             *         possible object is
             *         {@link StringType }
             *
             */
            public StringType getScannerModelSerialNo() {
                return scannerModelSerialNo;
            }

            /**
             * Définit la valeur de la propriété scannerModelSerialNo.
             *
             * @param value
             *            allowed object is
             *            {@link StringType }
             *
             */
            public void setScannerModelSerialNo(StringType value) {
                this.scannerModelSerialNo = value;
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
         *         &lt;element name="scanningSoftwareName" type="{http://www.loc.gov/mix/v20}stringType" minOccurs="0"/>
         *         &lt;element name="scanningSoftwareVersionNo" type="{http://www.loc.gov/mix/v20}stringType" minOccurs="0"/>
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
                 propOrder = {"scanningSoftwareName",
                              "scanningSoftwareVersionNo"})
        public static class ScanningSystemSoftware {

            protected StringType scanningSoftwareName;
            protected StringType scanningSoftwareVersionNo;

            /**
             * Obtient la valeur de la propriété scanningSoftwareName.
             *
             * @return
             *         possible object is
             *         {@link StringType }
             *
             */
            public StringType getScanningSoftwareName() {
                return scanningSoftwareName;
            }

            /**
             * Définit la valeur de la propriété scanningSoftwareName.
             *
             * @param value
             *            allowed object is
             *            {@link StringType }
             *
             */
            public void setScanningSoftwareName(StringType value) {
                this.scanningSoftwareName = value;
            }

            /**
             * Obtient la valeur de la propriété scanningSoftwareVersionNo.
             *
             * @return
             *         possible object is
             *         {@link StringType }
             *
             */
            public StringType getScanningSoftwareVersionNo() {
                return scanningSoftwareVersionNo;
            }

            /**
             * Définit la valeur de la propriété scanningSoftwareVersionNo.
             *
             * @param value
             *            allowed object is
             *            {@link StringType }
             *
             */
            public void setScanningSoftwareVersionNo(StringType value) {
                this.scanningSoftwareVersionNo = value;
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
     *         &lt;element name="sourceType" type="{http://www.loc.gov/mix/v20}stringType" minOccurs="0"/>
     *         &lt;element name="SourceID" maxOccurs="unbounded" minOccurs="0">
     *           &lt;complexType>
     *             &lt;complexContent>
     *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *                 &lt;sequence>
     *                   &lt;element name="sourceIDType" type="{http://www.loc.gov/mix/v20}stringType" minOccurs="0"/>
     *                   &lt;element name="sourceIDValue" type="{http://www.loc.gov/mix/v20}stringType" minOccurs="0"/>
     *                 &lt;/sequence>
     *               &lt;/restriction>
     *             &lt;/complexContent>
     *           &lt;/complexType>
     *         &lt;/element>
     *         &lt;element name="SourceSize" minOccurs="0">
     *           &lt;complexType>
     *             &lt;complexContent>
     *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *                 &lt;sequence>
     *                   &lt;element name="SourceXDimension" minOccurs="0">
     *                     &lt;complexType>
     *                       &lt;complexContent>
     *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *                           &lt;sequence>
     *                             &lt;element name="sourceXDimensionValue" type="{http://www.loc.gov/mix/v20}typeOfNonNegativeRealType" minOccurs="0"/>
     *                             &lt;element name="sourceXDimensionUnit" type="{http://www.loc.gov/mix/v20}typeOfsourceDimensionUnitType" minOccurs="0"/>
     *                           &lt;/sequence>
     *                         &lt;/restriction>
     *                       &lt;/complexContent>
     *                     &lt;/complexType>
     *                   &lt;/element>
     *                   &lt;element name="SourceYDimension" minOccurs="0">
     *                     &lt;complexType>
     *                       &lt;complexContent>
     *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *                           &lt;sequence>
     *                             &lt;element name="sourceYDimensionValue" type="{http://www.loc.gov/mix/v20}typeOfNonNegativeRealType" minOccurs="0"/>
     *                             &lt;element name="sourceYDimensionUnit" type="{http://www.loc.gov/mix/v20}typeOfsourceDimensionUnitType" minOccurs="0"/>
     *                           &lt;/sequence>
     *                         &lt;/restriction>
     *                       &lt;/complexContent>
     *                     &lt;/complexType>
     *                   &lt;/element>
     *                   &lt;element name="SourceZDimension" minOccurs="0">
     *                     &lt;complexType>
     *                       &lt;complexContent>
     *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *                           &lt;sequence>
     *                             &lt;element name="sourceZDimensionValue" type="{http://www.loc.gov/mix/v20}typeOfNonNegativeRealType" minOccurs="0"/>
     *                             &lt;element name="sourceZDimensionUnit" type="{http://www.loc.gov/mix/v20}typeOfsourceDimensionUnitType" minOccurs="0"/>
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
             propOrder = {"sourceType",
                          "sourceID",
                          "sourceSize"})
    public static class SourceInformation {

        protected StringType sourceType;
        @XmlElement(name = "SourceID")
        protected List<ImageCaptureMetadataType.SourceInformation.SourceID> sourceID;
        @XmlElement(name = "SourceSize")
        protected ImageCaptureMetadataType.SourceInformation.SourceSize sourceSize;

        /**
         * Obtient la valeur de la propriété sourceType.
         *
         * @return
         *         possible object is
         *         {@link StringType }
         *
         */
        public StringType getSourceType() {
            return sourceType;
        }

        /**
         * Définit la valeur de la propriété sourceType.
         *
         * @param value
         *            allowed object is
         *            {@link StringType }
         *
         */
        public void setSourceType(StringType value) {
            this.sourceType = value;
        }

        /**
         * Gets the value of the sourceID property.
         *
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the sourceID property.
         *
         * <p>
         * For example, to add a new item, do as follows:
         *
         * <pre>
         * getSourceID().add(newItem);
         * </pre>
         *
         *
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link ImageCaptureMetadataType.SourceInformation.SourceID }
         *
         *
         */
        public List<ImageCaptureMetadataType.SourceInformation.SourceID> getSourceID() {
            if (sourceID == null) {
                sourceID = new ArrayList<>();
            }
            return this.sourceID;
        }

        /**
         * Obtient la valeur de la propriété sourceSize.
         *
         * @return
         *         possible object is
         *         {@link ImageCaptureMetadataType.SourceInformation.SourceSize }
         *
         */
        public ImageCaptureMetadataType.SourceInformation.SourceSize getSourceSize() {
            return sourceSize;
        }

        /**
         * Définit la valeur de la propriété sourceSize.
         *
         * @param value
         *            allowed object is
         *            {@link ImageCaptureMetadataType.SourceInformation.SourceSize }
         *
         */
        public void setSourceSize(ImageCaptureMetadataType.SourceInformation.SourceSize value) {
            this.sourceSize = value;
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
         *         &lt;element name="sourceIDType" type="{http://www.loc.gov/mix/v20}stringType" minOccurs="0"/>
         *         &lt;element name="sourceIDValue" type="{http://www.loc.gov/mix/v20}stringType" minOccurs="0"/>
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
                 propOrder = {"sourceIDType",
                              "sourceIDValue"})
        public static class SourceID {

            protected StringType sourceIDType;
            protected StringType sourceIDValue;

            /**
             * Obtient la valeur de la propriété sourceIDType.
             *
             * @return
             *         possible object is
             *         {@link StringType }
             *
             */
            public StringType getSourceIDType() {
                return sourceIDType;
            }

            /**
             * Définit la valeur de la propriété sourceIDType.
             *
             * @param value
             *            allowed object is
             *            {@link StringType }
             *
             */
            public void setSourceIDType(StringType value) {
                this.sourceIDType = value;
            }

            /**
             * Obtient la valeur de la propriété sourceIDValue.
             *
             * @return
             *         possible object is
             *         {@link StringType }
             *
             */
            public StringType getSourceIDValue() {
                return sourceIDValue;
            }

            /**
             * Définit la valeur de la propriété sourceIDValue.
             *
             * @param value
             *            allowed object is
             *            {@link StringType }
             *
             */
            public void setSourceIDValue(StringType value) {
                this.sourceIDValue = value;
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
         *         &lt;element name="SourceXDimension" minOccurs="0">
         *           &lt;complexType>
         *             &lt;complexContent>
         *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
         *                 &lt;sequence>
         *                   &lt;element name="sourceXDimensionValue" type="{http://www.loc.gov/mix/v20}typeOfNonNegativeRealType" minOccurs="0"/>
         *                   &lt;element name="sourceXDimensionUnit" type="{http://www.loc.gov/mix/v20}typeOfsourceDimensionUnitType" minOccurs="0"/>
         *                 &lt;/sequence>
         *               &lt;/restriction>
         *             &lt;/complexContent>
         *           &lt;/complexType>
         *         &lt;/element>
         *         &lt;element name="SourceYDimension" minOccurs="0">
         *           &lt;complexType>
         *             &lt;complexContent>
         *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
         *                 &lt;sequence>
         *                   &lt;element name="sourceYDimensionValue" type="{http://www.loc.gov/mix/v20}typeOfNonNegativeRealType" minOccurs="0"/>
         *                   &lt;element name="sourceYDimensionUnit" type="{http://www.loc.gov/mix/v20}typeOfsourceDimensionUnitType" minOccurs="0"/>
         *                 &lt;/sequence>
         *               &lt;/restriction>
         *             &lt;/complexContent>
         *           &lt;/complexType>
         *         &lt;/element>
         *         &lt;element name="SourceZDimension" minOccurs="0">
         *           &lt;complexType>
         *             &lt;complexContent>
         *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
         *                 &lt;sequence>
         *                   &lt;element name="sourceZDimensionValue" type="{http://www.loc.gov/mix/v20}typeOfNonNegativeRealType" minOccurs="0"/>
         *                   &lt;element name="sourceZDimensionUnit" type="{http://www.loc.gov/mix/v20}typeOfsourceDimensionUnitType" minOccurs="0"/>
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
                 propOrder = {"sourceXDimension",
                              "sourceYDimension",
                              "sourceZDimension"})
        public static class SourceSize {

            @XmlElement(name = "SourceXDimension")
            protected ImageCaptureMetadataType.SourceInformation.SourceSize.SourceXDimension sourceXDimension;
            @XmlElement(name = "SourceYDimension")
            protected ImageCaptureMetadataType.SourceInformation.SourceSize.SourceYDimension sourceYDimension;
            @XmlElement(name = "SourceZDimension")
            protected ImageCaptureMetadataType.SourceInformation.SourceSize.SourceZDimension sourceZDimension;

            /**
             * Obtient la valeur de la propriété sourceXDimension.
             *
             * @return
             *         possible object is
             *         {@link ImageCaptureMetadataType.SourceInformation.SourceSize.SourceXDimension }
             *
             */
            public ImageCaptureMetadataType.SourceInformation.SourceSize.SourceXDimension getSourceXDimension() {
                return sourceXDimension;
            }

            /**
             * Définit la valeur de la propriété sourceXDimension.
             *
             * @param value
             *            allowed object is
             *            {@link ImageCaptureMetadataType.SourceInformation.SourceSize.SourceXDimension }
             *
             */
            public void setSourceXDimension(ImageCaptureMetadataType.SourceInformation.SourceSize.SourceXDimension value) {
                this.sourceXDimension = value;
            }

            /**
             * Obtient la valeur de la propriété sourceYDimension.
             *
             * @return
             *         possible object is
             *         {@link ImageCaptureMetadataType.SourceInformation.SourceSize.SourceYDimension }
             *
             */
            public ImageCaptureMetadataType.SourceInformation.SourceSize.SourceYDimension getSourceYDimension() {
                return sourceYDimension;
            }

            /**
             * Définit la valeur de la propriété sourceYDimension.
             *
             * @param value
             *            allowed object is
             *            {@link ImageCaptureMetadataType.SourceInformation.SourceSize.SourceYDimension }
             *
             */
            public void setSourceYDimension(ImageCaptureMetadataType.SourceInformation.SourceSize.SourceYDimension value) {
                this.sourceYDimension = value;
            }

            /**
             * Obtient la valeur de la propriété sourceZDimension.
             *
             * @return
             *         possible object is
             *         {@link ImageCaptureMetadataType.SourceInformation.SourceSize.SourceZDimension }
             *
             */
            public ImageCaptureMetadataType.SourceInformation.SourceSize.SourceZDimension getSourceZDimension() {
                return sourceZDimension;
            }

            /**
             * Définit la valeur de la propriété sourceZDimension.
             *
             * @param value
             *            allowed object is
             *            {@link ImageCaptureMetadataType.SourceInformation.SourceSize.SourceZDimension }
             *
             */
            public void setSourceZDimension(ImageCaptureMetadataType.SourceInformation.SourceSize.SourceZDimension value) {
                this.sourceZDimension = value;
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
             *         &lt;element name="sourceXDimensionValue" type="{http://www.loc.gov/mix/v20}typeOfNonNegativeRealType" minOccurs="0"/>
             *         &lt;element name="sourceXDimensionUnit" type="{http://www.loc.gov/mix/v20}typeOfsourceDimensionUnitType" minOccurs="0"/>
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
                     propOrder = {"sourceXDimensionValue",
                                  "sourceXDimensionUnit"})
            public static class SourceXDimension {

                protected TypeOfNonNegativeRealType sourceXDimensionValue;
                protected TypeOfsourceDimensionUnitType sourceXDimensionUnit;

                /**
                 * Obtient la valeur de la propriété sourceXDimensionValue.
                 *
                 * @return
                 *         possible object is
                 *         {@link TypeOfNonNegativeRealType }
                 *
                 */
                public TypeOfNonNegativeRealType getSourceXDimensionValue() {
                    return sourceXDimensionValue;
                }

                /**
                 * Définit la valeur de la propriété sourceXDimensionValue.
                 *
                 * @param value
                 *            allowed object is
                 *            {@link TypeOfNonNegativeRealType }
                 *
                 */
                public void setSourceXDimensionValue(TypeOfNonNegativeRealType value) {
                    this.sourceXDimensionValue = value;
                }

                /**
                 * Obtient la valeur de la propriété sourceXDimensionUnit.
                 *
                 * @return
                 *         possible object is
                 *         {@link TypeOfsourceDimensionUnitType }
                 *
                 */
                public TypeOfsourceDimensionUnitType getSourceXDimensionUnit() {
                    return sourceXDimensionUnit;
                }

                /**
                 * Définit la valeur de la propriété sourceXDimensionUnit.
                 *
                 * @param value
                 *            allowed object is
                 *            {@link TypeOfsourceDimensionUnitType }
                 *
                 */
                public void setSourceXDimensionUnit(TypeOfsourceDimensionUnitType value) {
                    this.sourceXDimensionUnit = value;
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
             *         &lt;element name="sourceYDimensionValue" type="{http://www.loc.gov/mix/v20}typeOfNonNegativeRealType" minOccurs="0"/>
             *         &lt;element name="sourceYDimensionUnit" type="{http://www.loc.gov/mix/v20}typeOfsourceDimensionUnitType" minOccurs="0"/>
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
                     propOrder = {"sourceYDimensionValue",
                                  "sourceYDimensionUnit"})
            public static class SourceYDimension {

                protected TypeOfNonNegativeRealType sourceYDimensionValue;
                protected TypeOfsourceDimensionUnitType sourceYDimensionUnit;

                /**
                 * Obtient la valeur de la propriété sourceYDimensionValue.
                 *
                 * @return
                 *         possible object is
                 *         {@link TypeOfNonNegativeRealType }
                 *
                 */
                public TypeOfNonNegativeRealType getSourceYDimensionValue() {
                    return sourceYDimensionValue;
                }

                /**
                 * Définit la valeur de la propriété sourceYDimensionValue.
                 *
                 * @param value
                 *            allowed object is
                 *            {@link TypeOfNonNegativeRealType }
                 *
                 */
                public void setSourceYDimensionValue(TypeOfNonNegativeRealType value) {
                    this.sourceYDimensionValue = value;
                }

                /**
                 * Obtient la valeur de la propriété sourceYDimensionUnit.
                 *
                 * @return
                 *         possible object is
                 *         {@link TypeOfsourceDimensionUnitType }
                 *
                 */
                public TypeOfsourceDimensionUnitType getSourceYDimensionUnit() {
                    return sourceYDimensionUnit;
                }

                /**
                 * Définit la valeur de la propriété sourceYDimensionUnit.
                 *
                 * @param value
                 *            allowed object is
                 *            {@link TypeOfsourceDimensionUnitType }
                 *
                 */
                public void setSourceYDimensionUnit(TypeOfsourceDimensionUnitType value) {
                    this.sourceYDimensionUnit = value;
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
             *         &lt;element name="sourceZDimensionValue" type="{http://www.loc.gov/mix/v20}typeOfNonNegativeRealType" minOccurs="0"/>
             *         &lt;element name="sourceZDimensionUnit" type="{http://www.loc.gov/mix/v20}typeOfsourceDimensionUnitType" minOccurs="0"/>
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
                     propOrder = {"sourceZDimensionValue",
                                  "sourceZDimensionUnit"})
            public static class SourceZDimension {

                protected TypeOfNonNegativeRealType sourceZDimensionValue;
                protected TypeOfsourceDimensionUnitType sourceZDimensionUnit;

                /**
                 * Obtient la valeur de la propriété sourceZDimensionValue.
                 *
                 * @return
                 *         possible object is
                 *         {@link TypeOfNonNegativeRealType }
                 *
                 */
                public TypeOfNonNegativeRealType getSourceZDimensionValue() {
                    return sourceZDimensionValue;
                }

                /**
                 * Définit la valeur de la propriété sourceZDimensionValue.
                 *
                 * @param value
                 *            allowed object is
                 *            {@link TypeOfNonNegativeRealType }
                 *
                 */
                public void setSourceZDimensionValue(TypeOfNonNegativeRealType value) {
                    this.sourceZDimensionValue = value;
                }

                /**
                 * Obtient la valeur de la propriété sourceZDimensionUnit.
                 *
                 * @return
                 *         possible object is
                 *         {@link TypeOfsourceDimensionUnitType }
                 *
                 */
                public TypeOfsourceDimensionUnitType getSourceZDimensionUnit() {
                    return sourceZDimensionUnit;
                }

                /**
                 * Définit la valeur de la propriété sourceZDimensionUnit.
                 *
                 * @param value
                 *            allowed object is
                 *            {@link TypeOfsourceDimensionUnitType }
                 *
                 */
                public void setSourceZDimensionUnit(TypeOfsourceDimensionUnitType value) {
                    this.sourceZDimensionUnit = value;
                }

            }

        }

    }

}
