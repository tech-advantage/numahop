//
// Ce fichier a été généré par l'implémentation de référence JavaTM Architecture for XML Binding (JAXB), v2.2.8-b130911.1802
// Voir <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a>
// Toute modification apportée à ce fichier sera perdue lors de la recompilation du schéma source.
// Généré le : 2017.08.25 à 03:15:17 PM CEST
//


package fr.progilone.pgcn.domain.jaxb.mix;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Classe Java pour ChangeHistoryType complex type.
 *
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 *
 * <pre>
 * &lt;complexType name="ChangeHistoryType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="ImageProcessing" maxOccurs="unbounded" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="dateTimeProcessed" type="{http://www.loc.gov/mix/v20}typeOfDateType" minOccurs="0"/>
 *                   &lt;element name="sourceData" type="{http://www.loc.gov/mix/v20}stringType" minOccurs="0"/>
 *                   &lt;element name="processingAgency" type="{http://www.loc.gov/mix/v20}stringType" maxOccurs="unbounded" minOccurs="0"/>
 *                   &lt;element name="processingRationale" type="{http://www.loc.gov/mix/v20}stringType" minOccurs="0"/>
 *                   &lt;element name="ProcessingSoftware" maxOccurs="unbounded" minOccurs="0">
 *                     &lt;complexType>
 *                       &lt;complexContent>
 *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                           &lt;sequence>
 *                             &lt;element name="processingSoftwareName" type="{http://www.loc.gov/mix/v20}stringType" minOccurs="0"/>
 *                             &lt;element name="processingSoftwareVersion" type="{http://www.loc.gov/mix/v20}stringType" minOccurs="0"/>
 *                             &lt;element name="processingOperatingSystemName" type="{http://www.loc.gov/mix/v20}stringType" minOccurs="0"/>
 *                             &lt;element name="processingOperatingSystemVersion" type="{http://www.loc.gov/mix/v20}stringType" minOccurs="0"/>
 *                           &lt;/sequence>
 *                         &lt;/restriction>
 *                       &lt;/complexContent>
 *                     &lt;/complexType>
 *                   &lt;/element>
 *                   &lt;element name="processingActions" type="{http://www.loc.gov/mix/v20}stringType" maxOccurs="unbounded" minOccurs="0"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="PreviousImageMetadata" type="{http://www.loc.gov/mix/v20}typeOfPreviousImageMetadataType" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ChangeHistoryType", propOrder = {
    "imageProcessing",
    "previousImageMetadata"
})
public class ChangeHistoryType {

    @XmlElement(name = "ImageProcessing")
    protected List<ChangeHistoryType.ImageProcessing> imageProcessing;
    @XmlElement(name = "PreviousImageMetadata")
    protected List<TypeOfPreviousImageMetadataType> previousImageMetadata;

    /**
     * Gets the value of the imageProcessing property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the imageProcessing property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getImageProcessing().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link ChangeHistoryType.ImageProcessing }
     *
     *
     */
    public List<ChangeHistoryType.ImageProcessing> getImageProcessing() {
        if (imageProcessing == null) {
            imageProcessing = new ArrayList<>();
        }
        return this.imageProcessing;
    }

    /**
     * Gets the value of the previousImageMetadata property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the previousImageMetadata property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getPreviousImageMetadata().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link TypeOfPreviousImageMetadataType }
     *
     *
     */
    public List<TypeOfPreviousImageMetadataType> getPreviousImageMetadata() {
        if (previousImageMetadata == null) {
            previousImageMetadata = new ArrayList<>();
        }
        return this.previousImageMetadata;
    }


    /**
     * <p>Classe Java pour anonymous complex type.
     *
     * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
     *
     * <pre>
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *       &lt;sequence>
     *         &lt;element name="dateTimeProcessed" type="{http://www.loc.gov/mix/v20}typeOfDateType" minOccurs="0"/>
     *         &lt;element name="sourceData" type="{http://www.loc.gov/mix/v20}stringType" minOccurs="0"/>
     *         &lt;element name="processingAgency" type="{http://www.loc.gov/mix/v20}stringType" maxOccurs="unbounded" minOccurs="0"/>
     *         &lt;element name="processingRationale" type="{http://www.loc.gov/mix/v20}stringType" minOccurs="0"/>
     *         &lt;element name="ProcessingSoftware" maxOccurs="unbounded" minOccurs="0">
     *           &lt;complexType>
     *             &lt;complexContent>
     *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *                 &lt;sequence>
     *                   &lt;element name="processingSoftwareName" type="{http://www.loc.gov/mix/v20}stringType" minOccurs="0"/>
     *                   &lt;element name="processingSoftwareVersion" type="{http://www.loc.gov/mix/v20}stringType" minOccurs="0"/>
     *                   &lt;element name="processingOperatingSystemName" type="{http://www.loc.gov/mix/v20}stringType" minOccurs="0"/>
     *                   &lt;element name="processingOperatingSystemVersion" type="{http://www.loc.gov/mix/v20}stringType" minOccurs="0"/>
     *                 &lt;/sequence>
     *               &lt;/restriction>
     *             &lt;/complexContent>
     *           &lt;/complexType>
     *         &lt;/element>
     *         &lt;element name="processingActions" type="{http://www.loc.gov/mix/v20}stringType" maxOccurs="unbounded" minOccurs="0"/>
     *       &lt;/sequence>
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     *
     *
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "dateTimeProcessed",
        "sourceData",
        "processingAgency",
        "processingRationale",
        "processingSoftware",
        "processingActions"
    })
    public static class ImageProcessing {

        protected TypeOfDateType dateTimeProcessed;
        protected StringType sourceData;
        protected List<StringType> processingAgency;
        protected StringType processingRationale;
        @XmlElement(name = "ProcessingSoftware")
        protected List<ChangeHistoryType.ImageProcessing.ProcessingSoftware> processingSoftware;
        protected List<StringType> processingActions;

        /**
         * Obtient la valeur de la propriété dateTimeProcessed.
         *
         * @return
         *     possible object is
         *     {@link TypeOfDateType }
         *
         */
        public TypeOfDateType getDateTimeProcessed() {
            return dateTimeProcessed;
        }

        /**
         * Définit la valeur de la propriété dateTimeProcessed.
         *
         * @param value
         *     allowed object is
         *     {@link TypeOfDateType }
         *
         */
        public void setDateTimeProcessed(TypeOfDateType value) {
            this.dateTimeProcessed = value;
        }

        /**
         * Obtient la valeur de la propriété sourceData.
         *
         * @return
         *     possible object is
         *     {@link StringType }
         *
         */
        public StringType getSourceData() {
            return sourceData;
        }

        /**
         * Définit la valeur de la propriété sourceData.
         *
         * @param value
         *     allowed object is
         *     {@link StringType }
         *
         */
        public void setSourceData(StringType value) {
            this.sourceData = value;
        }

        /**
         * Gets the value of the processingAgency property.
         *
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the processingAgency property.
         *
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getProcessingAgency().add(newItem);
         * </pre>
         *
         *
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link StringType }
         *
         *
         */
        public List<StringType> getProcessingAgency() {
            if (processingAgency == null) {
                processingAgency = new ArrayList<>();
            }
            return this.processingAgency;
        }

        /**
         * Obtient la valeur de la propriété processingRationale.
         *
         * @return
         *     possible object is
         *     {@link StringType }
         *
         */
        public StringType getProcessingRationale() {
            return processingRationale;
        }

        /**
         * Définit la valeur de la propriété processingRationale.
         *
         * @param value
         *     allowed object is
         *     {@link StringType }
         *
         */
        public void setProcessingRationale(StringType value) {
            this.processingRationale = value;
        }

        /**
         * Gets the value of the processingSoftware property.
         *
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the processingSoftware property.
         *
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getProcessingSoftware().add(newItem);
         * </pre>
         *
         *
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link ChangeHistoryType.ImageProcessing.ProcessingSoftware }
         *
         *
         */
        public List<ChangeHistoryType.ImageProcessing.ProcessingSoftware> getProcessingSoftware() {
            if (processingSoftware == null) {
                processingSoftware = new ArrayList<>();
            }
            return this.processingSoftware;
        }

        /**
         * Gets the value of the processingActions property.
         *
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the processingActions property.
         *
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getProcessingActions().add(newItem);
         * </pre>
         *
         *
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link StringType }
         *
         *
         */
        public List<StringType> getProcessingActions() {
            if (processingActions == null) {
                processingActions = new ArrayList<>();
            }
            return this.processingActions;
        }


        /**
         * <p>Classe Java pour anonymous complex type.
         *
         * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
         *
         * <pre>
         * &lt;complexType>
         *   &lt;complexContent>
         *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
         *       &lt;sequence>
         *         &lt;element name="processingSoftwareName" type="{http://www.loc.gov/mix/v20}stringType" minOccurs="0"/>
         *         &lt;element name="processingSoftwareVersion" type="{http://www.loc.gov/mix/v20}stringType" minOccurs="0"/>
         *         &lt;element name="processingOperatingSystemName" type="{http://www.loc.gov/mix/v20}stringType" minOccurs="0"/>
         *         &lt;element name="processingOperatingSystemVersion" type="{http://www.loc.gov/mix/v20}stringType" minOccurs="0"/>
         *       &lt;/sequence>
         *     &lt;/restriction>
         *   &lt;/complexContent>
         * &lt;/complexType>
         * </pre>
         *
         *
         */
        @XmlAccessorType(XmlAccessType.FIELD)
        @XmlType(name = "", propOrder = {
            "processingSoftwareName",
            "processingSoftwareVersion",
            "processingOperatingSystemName",
            "processingOperatingSystemVersion"
        })
        public static class ProcessingSoftware {

            protected StringType processingSoftwareName;
            protected StringType processingSoftwareVersion;
            protected StringType processingOperatingSystemName;
            protected StringType processingOperatingSystemVersion;

            /**
             * Obtient la valeur de la propriété processingSoftwareName.
             *
             * @return
             *     possible object is
             *     {@link StringType }
             *
             */
            public StringType getProcessingSoftwareName() {
                return processingSoftwareName;
            }

            /**
             * Définit la valeur de la propriété processingSoftwareName.
             *
             * @param value
             *     allowed object is
             *     {@link StringType }
             *
             */
            public void setProcessingSoftwareName(StringType value) {
                this.processingSoftwareName = value;
            }

            /**
             * Obtient la valeur de la propriété processingSoftwareVersion.
             *
             * @return
             *     possible object is
             *     {@link StringType }
             *
             */
            public StringType getProcessingSoftwareVersion() {
                return processingSoftwareVersion;
            }

            /**
             * Définit la valeur de la propriété processingSoftwareVersion.
             *
             * @param value
             *     allowed object is
             *     {@link StringType }
             *
             */
            public void setProcessingSoftwareVersion(StringType value) {
                this.processingSoftwareVersion = value;
            }

            /**
             * Obtient la valeur de la propriété processingOperatingSystemName.
             *
             * @return
             *     possible object is
             *     {@link StringType }
             *
             */
            public StringType getProcessingOperatingSystemName() {
                return processingOperatingSystemName;
            }

            /**
             * Définit la valeur de la propriété processingOperatingSystemName.
             *
             * @param value
             *     allowed object is
             *     {@link StringType }
             *
             */
            public void setProcessingOperatingSystemName(StringType value) {
                this.processingOperatingSystemName = value;
            }

            /**
             * Obtient la valeur de la propriété processingOperatingSystemVersion.
             *
             * @return
             *     possible object is
             *     {@link StringType }
             *
             */
            public StringType getProcessingOperatingSystemVersion() {
                return processingOperatingSystemVersion;
            }

            /**
             * Définit la valeur de la propriété processingOperatingSystemVersion.
             *
             * @param value
             *     allowed object is
             *     {@link StringType }
             *
             */
            public void setProcessingOperatingSystemVersion(StringType value) {
                this.processingOperatingSystemVersion = value;
            }

        }

    }

}
