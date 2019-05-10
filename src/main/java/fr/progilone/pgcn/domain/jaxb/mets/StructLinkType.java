//
// Ce fichier a été généré par l'implémentation de référence JavaTM Architecture for XML Binding (JAXB), v2.2.11
// Voir <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a>
// Toute modification apportée à ce fichier sera perdue lors de la recompilation du schéma source.
// Généré le : 2016.12.29 à 11:54:46 AM CET
//


package fr.progilone.pgcn.domain.jaxb.mets;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAnyAttribute;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlID;
import javax.xml.bind.annotation.XmlIDREF;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import javax.xml.namespace.QName;


/**
 * structLinkType: Complex Type for Structural Map Linking
 * 				The Structural Map Linking section allows for the specification of hyperlinks between different components of a METS structure delineated in a structural map.  structLink contains a single, repeatable element, smLink.  Each smLink element indicates a hyperlink between two nodes in the structMap.  The structMap nodes recorded in smLink are identified using their XML ID attribute	values.
 *
 *
 * <p>Classe Java pour structLinkType complex type.
 *
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 *
 * <pre>
 * &lt;complexType name="structLinkType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;choice maxOccurs="unbounded"&gt;
 *         &lt;element name="smLink"&gt;
 *           &lt;complexType&gt;
 *             &lt;complexContent&gt;
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *                 &lt;attribute name="ID" type="{http://www.w3.org/2001/XMLSchema}ID" /&gt;
 *                 &lt;attribute ref="{http://www.w3.org/1999/xlink}arcrole"/&gt;
 *                 &lt;attribute ref="{http://www.w3.org/1999/xlink}title"/&gt;
 *                 &lt;attribute ref="{http://www.w3.org/1999/xlink}show"/&gt;
 *                 &lt;attribute ref="{http://www.w3.org/1999/xlink}actuate"/&gt;
 *                 &lt;attribute ref="{http://www.w3.org/1999/xlink}to use="required""/&gt;
 *                 &lt;attribute ref="{http://www.w3.org/1999/xlink}from use="required""/&gt;
 *               &lt;/restriction&gt;
 *             &lt;/complexContent&gt;
 *           &lt;/complexType&gt;
 *         &lt;/element&gt;
 *         &lt;element name="smLinkGrp"&gt;
 *           &lt;complexType&gt;
 *             &lt;complexContent&gt;
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *                 &lt;sequence&gt;
 *                   &lt;element name="smLocatorLink" maxOccurs="unbounded" minOccurs="2"&gt;
 *                     &lt;complexType&gt;
 *                       &lt;complexContent&gt;
 *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *                           &lt;attGroup ref="{http://www.w3.org/1999/xlink}locatorLink"/&gt;
 *                           &lt;attribute name="ID" type="{http://www.w3.org/2001/XMLSchema}ID" /&gt;
 *                         &lt;/restriction&gt;
 *                       &lt;/complexContent&gt;
 *                     &lt;/complexType&gt;
 *                   &lt;/element&gt;
 *                   &lt;element name="smArcLink" maxOccurs="unbounded"&gt;
 *                     &lt;complexType&gt;
 *                       &lt;complexContent&gt;
 *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *                           &lt;attGroup ref="{http://www.w3.org/1999/xlink}arcLink"/&gt;
 *                           &lt;attribute name="ID" type="{http://www.w3.org/2001/XMLSchema}ID" /&gt;
 *                           &lt;attribute name="ARCTYPE" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *                           &lt;attribute name="ADMID" type="{http://www.w3.org/2001/XMLSchema}IDREFS" /&gt;
 *                         &lt;/restriction&gt;
 *                       &lt;/complexContent&gt;
 *                     &lt;/complexType&gt;
 *                   &lt;/element&gt;
 *                 &lt;/sequence&gt;
 *                 &lt;attGroup ref="{http://www.w3.org/1999/xlink}extendedLink"/&gt;
 *                 &lt;attribute name="ID" type="{http://www.w3.org/2001/XMLSchema}ID" /&gt;
 *                 &lt;attribute name="ARCLINKORDER" default="unordered"&gt;
 *                   &lt;simpleType&gt;
 *                     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *                       &lt;enumeration value="ordered"/&gt;
 *                       &lt;enumeration value="unordered"/&gt;
 *                     &lt;/restriction&gt;
 *                   &lt;/simpleType&gt;
 *                 &lt;/attribute&gt;
 *               &lt;/restriction&gt;
 *             &lt;/complexContent&gt;
 *           &lt;/complexType&gt;
 *         &lt;/element&gt;
 *       &lt;/choice&gt;
 *       &lt;attribute name="ID" type="{http://www.w3.org/2001/XMLSchema}ID" /&gt;
 *       &lt;anyAttribute processContents='lax' namespace='##other'/&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "structLinkType", propOrder = {
    "smLinkOrSmLinkGrp"
})
@XmlSeeAlso({
    MetsType.StructLink.class
})
public class StructLinkType {

    @XmlElements({
        @XmlElement(name = "smLink", type = SmLink.class),
        @XmlElement(name = "smLinkGrp", type = SmLinkGrp.class)
    })
    protected List<Object> smLinkOrSmLinkGrp;
    @XmlAttribute(name = "ID")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlID
    @XmlSchemaType(name = "ID")
    protected String id;
    @XmlAnyAttribute
    private Map<QName, String> otherAttributes = new HashMap<>();

    /**
     * Gets the value of the smLinkOrSmLinkGrp property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the smLinkOrSmLinkGrp property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getSmLinkOrSmLinkGrp().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link SmLink }
     * {@link SmLinkGrp }
     *
     *
     */
    public List<Object> getSmLinkOrSmLinkGrp() {
        if (smLinkOrSmLinkGrp == null) {
            smLinkOrSmLinkGrp = new ArrayList<>();
        }
        return this.smLinkOrSmLinkGrp;
    }

    /**
     * Obtient la valeur de la propriété id.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getID() {
        return id;
    }

    /**
     * Définit la valeur de la propriété id.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setID(String value) {
        this.id = value;
    }

    /**
     * Gets a map that contains attributes that aren't bound to any typed property on this class.
     *
     * <p>
     * the map is keyed by the name of the attribute and
     * the value is the string value of the attribute.
     *
     * the map returned by this method is live, and you can add new attribute
     * by updating the map directly. Because of this design, there's no setter.
     *
     *
     * @return
     *     always non-null
     */
    public Map<QName, String> getOtherAttributes() {
        return otherAttributes;
    }


    /**
     * <p>Classe Java pour anonymous complex type.
     *
     * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
     *
     * <pre>
     * &lt;complexType&gt;
     *   &lt;complexContent&gt;
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
     *       &lt;attribute name="ID" type="{http://www.w3.org/2001/XMLSchema}ID" /&gt;
     *       &lt;attribute ref="{http://www.w3.org/1999/xlink}arcrole"/&gt;
     *       &lt;attribute ref="{http://www.w3.org/1999/xlink}title"/&gt;
     *       &lt;attribute ref="{http://www.w3.org/1999/xlink}show"/&gt;
     *       &lt;attribute ref="{http://www.w3.org/1999/xlink}actuate"/&gt;
     *       &lt;attribute ref="{http://www.w3.org/1999/xlink}to use="required""/&gt;
     *       &lt;attribute ref="{http://www.w3.org/1999/xlink}from use="required""/&gt;
     *     &lt;/restriction&gt;
     *   &lt;/complexContent&gt;
     * &lt;/complexType&gt;
     * </pre>
     *
     *
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "")
    public static class SmLink {

        @XmlAttribute(name = "ID")
        @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
        @XmlID
        @XmlSchemaType(name = "ID")
        protected String id;
        @XmlAttribute(name = "arcrole", namespace = "http://www.w3.org/1999/xlink")
        protected String arcrole;
        @XmlAttribute(name = "title", namespace = "http://www.w3.org/1999/xlink")
        protected String title;
        @XmlAttribute(name = "show", namespace = "http://www.w3.org/1999/xlink")
        protected String show;
        @XmlAttribute(name = "actuate", namespace = "http://www.w3.org/1999/xlink")
        protected String actuate;
        @XmlAttribute(name = "to", namespace = "http://www.w3.org/1999/xlink", required = true)
        protected String to;
        @XmlAttribute(name = "from", namespace = "http://www.w3.org/1999/xlink", required = true)
        protected String from;

        /**
         * Obtient la valeur de la propriété id.
         *
         * @return
         *     possible object is
         *     {@link String }
         *
         */
        public String getID() {
            return id;
        }

        /**
         * Définit la valeur de la propriété id.
         *
         * @param value
         *     allowed object is
         *     {@link String }
         *
         */
        public void setID(String value) {
            this.id = value;
        }

        /**
         *
         * 								 xlink:arcrole - the role of the link, as per the xlink specification.  See http://www.w3.org/TR/xlink/
         *
         *
         * @return
         *     possible object is
         *     {@link String }
         *
         */
        public String getArcrole() {
            return arcrole;
        }

        /**
         * Définit la valeur de la propriété arcrole.
         *
         * @param value
         *     allowed object is
         *     {@link String }
         *
         */
        public void setArcrole(String value) {
            this.arcrole = value;
        }

        /**
         *
         * 								xlink:title - a title for the link (if needed), as per the xlink specification.  See http://www.w3.org/TR/xlink/
         *
         *
         * @return
         *     possible object is
         *     {@link String }
         *
         */
        public String getTitle() {
            return title;
        }

        /**
         * Définit la valeur de la propriété title.
         *
         * @param value
         *     allowed object is
         *     {@link String }
         *
         */
        public void setTitle(String value) {
            this.title = value;
        }

        /**
         *
         * 								xlink:show - see the xlink specification at http://www.w3.org/TR/xlink/
         *
         *
         * @return
         *     possible object is
         *     {@link String }
         *
         */
        public String getShow() {
            return show;
        }

        /**
         * Définit la valeur de la propriété show.
         *
         * @param value
         *     allowed object is
         *     {@link String }
         *
         */
        public void setShow(String value) {
            this.show = value;
        }

        /**
         *
         * 								xlink:actuate - see the xlink specification at http://www.w3.org/TR/xlink/
         *
         *
         * @return
         *     possible object is
         *     {@link String }
         *
         */
        public String getActuate() {
            return actuate;
        }

        /**
         * Définit la valeur de la propriété actuate.
         *
         * @param value
         *     allowed object is
         *     {@link String }
         *
         */
        public void setActuate(String value) {
            this.actuate = value;
        }

        /**
         *
         * 								xlink:to - the value of the label for the element in the structMap you are linking to.
         *
         *
         * @return
         *     possible object is
         *     {@link String }
         *
         */
        public String getTo() {
            return to;
        }

        /**
         * Définit la valeur de la propriété to.
         *
         * @param value
         *     allowed object is
         *     {@link String }
         *
         */
        public void setTo(String value) {
            this.to = value;
        }

        /**
         *
         * 								xlink:from - the value of the label for the element in the structMap you are linking from.
         *
         *
         * @return
         *     possible object is
         *     {@link String }
         *
         */
        public String getFrom() {
            return from;
        }

        /**
         * Définit la valeur de la propriété from.
         *
         * @param value
         *     allowed object is
         *     {@link String }
         *
         */
        public void setFrom(String value) {
            this.from = value;
        }

    }


    /**
     * <p>Classe Java pour anonymous complex type.
     *
     * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
     *
     * <pre>
     * &lt;complexType&gt;
     *   &lt;complexContent&gt;
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
     *       &lt;sequence&gt;
     *         &lt;element name="smLocatorLink" maxOccurs="unbounded" minOccurs="2"&gt;
     *           &lt;complexType&gt;
     *             &lt;complexContent&gt;
     *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
     *                 &lt;attGroup ref="{http://www.w3.org/1999/xlink}locatorLink"/&gt;
     *                 &lt;attribute name="ID" type="{http://www.w3.org/2001/XMLSchema}ID" /&gt;
     *               &lt;/restriction&gt;
     *             &lt;/complexContent&gt;
     *           &lt;/complexType&gt;
     *         &lt;/element&gt;
     *         &lt;element name="smArcLink" maxOccurs="unbounded"&gt;
     *           &lt;complexType&gt;
     *             &lt;complexContent&gt;
     *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
     *                 &lt;attGroup ref="{http://www.w3.org/1999/xlink}arcLink"/&gt;
     *                 &lt;attribute name="ID" type="{http://www.w3.org/2001/XMLSchema}ID" /&gt;
     *                 &lt;attribute name="ARCTYPE" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
     *                 &lt;attribute name="ADMID" type="{http://www.w3.org/2001/XMLSchema}IDREFS" /&gt;
     *               &lt;/restriction&gt;
     *             &lt;/complexContent&gt;
     *           &lt;/complexType&gt;
     *         &lt;/element&gt;
     *       &lt;/sequence&gt;
     *       &lt;attGroup ref="{http://www.w3.org/1999/xlink}extendedLink"/&gt;
     *       &lt;attribute name="ID" type="{http://www.w3.org/2001/XMLSchema}ID" /&gt;
     *       &lt;attribute name="ARCLINKORDER" default="unordered"&gt;
     *         &lt;simpleType&gt;
     *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
     *             &lt;enumeration value="ordered"/&gt;
     *             &lt;enumeration value="unordered"/&gt;
     *           &lt;/restriction&gt;
     *         &lt;/simpleType&gt;
     *       &lt;/attribute&gt;
     *     &lt;/restriction&gt;
     *   &lt;/complexContent&gt;
     * &lt;/complexType&gt;
     * </pre>
     *
     *
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "smLocatorLink",
        "smArcLink"
    })
    public static class SmLinkGrp {

        @XmlElement(required = true)
        protected List<SmLocatorLink> smLocatorLink;
        @XmlElement(required = true)
        protected List<SmArcLink> smArcLink;
        @XmlAttribute(name = "ID")
        @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
        @XmlID
        @XmlSchemaType(name = "ID")
        protected String id;
        @XmlAttribute(name = "ARCLINKORDER")
        protected String arclinkorder;
        @XmlAttribute(name = "type", namespace = "http://www.w3.org/1999/xlink")
        protected String type;
        @XmlAttribute(name = "role", namespace = "http://www.w3.org/1999/xlink")
        protected String role;
        @XmlAttribute(name = "title", namespace = "http://www.w3.org/1999/xlink")
        protected String title;

        /**
         * Gets the value of the smLocatorLink property.
         *
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the smLocatorLink property.
         *
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getSmLocatorLink().add(newItem);
         * </pre>
         *
         *
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link SmLocatorLink }
         *
         *
         */
        public List<SmLocatorLink> getSmLocatorLink() {
            if (smLocatorLink == null) {
                smLocatorLink = new ArrayList<>();
            }
            return this.smLocatorLink;
        }

        /**
         * Gets the value of the smArcLink property.
         *
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the smArcLink property.
         *
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getSmArcLink().add(newItem);
         * </pre>
         *
         *
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link SmArcLink }
         *
         *
         */
        public List<SmArcLink> getSmArcLink() {
            if (smArcLink == null) {
                smArcLink = new ArrayList<>();
            }
            return this.smArcLink;
        }

        /**
         * Obtient la valeur de la propriété id.
         *
         * @return
         *     possible object is
         *     {@link String }
         *
         */
        public String getID() {
            return id;
        }

        /**
         * Définit la valeur de la propriété id.
         *
         * @param value
         *     allowed object is
         *     {@link String }
         *
         */
        public void setID(String value) {
            this.id = value;
        }

        /**
         * Obtient la valeur de la propriété arclinkorder.
         *
         * @return
         *     possible object is
         *     {@link String }
         *
         */
        public String getARCLINKORDER() {
            if (arclinkorder == null) {
                return "unordered";
            } else {
                return arclinkorder;
            }
        }

        /**
         * Définit la valeur de la propriété arclinkorder.
         *
         * @param value
         *     allowed object is
         *     {@link String }
         *
         */
        public void setARCLINKORDER(String value) {
            this.arclinkorder = value;
        }

        /**
         * Obtient la valeur de la propriété type.
         *
         * @return
         *     possible object is
         *     {@link String }
         *
         */
        public String getType() {
            if (type == null) {
                return "extended";
            } else {
                return type;
            }
        }

        /**
         * Définit la valeur de la propriété type.
         *
         * @param value
         *     allowed object is
         *     {@link String }
         *
         */
        public void setType(String value) {
            this.type = value;
        }

        /**
         * Obtient la valeur de la propriété role.
         *
         * @return
         *     possible object is
         *     {@link String }
         *
         */
        public String getRole() {
            return role;
        }

        /**
         * Définit la valeur de la propriété role.
         *
         * @param value
         *     allowed object is
         *     {@link String }
         *
         */
        public void setRole(String value) {
            this.role = value;
        }

        /**
         * Obtient la valeur de la propriété title.
         *
         * @return
         *     possible object is
         *     {@link String }
         *
         */
        public String getTitle() {
            return title;
        }

        /**
         * Définit la valeur de la propriété title.
         *
         * @param value
         *     allowed object is
         *     {@link String }
         *
         */
        public void setTitle(String value) {
            this.title = value;
        }


        /**
         *
         * 										The structMap arc link element <smArcLink> is of xlink:type "arc" It can be used to establish a traversal link between two <div> elements as identified by <smLocatorLink> elements within the same smLinkGrp element. The associated xlink:from and xlink:to attributes identify the from and to sides of the arc link by referencing the xlink:label attribute values on the participating smLocatorLink elements.
         *
         *
         * <p>Classe Java pour anonymous complex type.
         *
         * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
         *
         * <pre>
         * &lt;complexType&gt;
         *   &lt;complexContent&gt;
         *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
         *       &lt;attGroup ref="{http://www.w3.org/1999/xlink}arcLink"/&gt;
         *       &lt;attribute name="ID" type="{http://www.w3.org/2001/XMLSchema}ID" /&gt;
         *       &lt;attribute name="ARCTYPE" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
         *       &lt;attribute name="ADMID" type="{http://www.w3.org/2001/XMLSchema}IDREFS" /&gt;
         *     &lt;/restriction&gt;
         *   &lt;/complexContent&gt;
         * &lt;/complexType&gt;
         * </pre>
         *
         *
         */
        @XmlAccessorType(XmlAccessType.FIELD)
        @XmlType(name = "")
        public static class SmArcLink {

            @XmlAttribute(name = "ID")
            @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
            @XmlID
            @XmlSchemaType(name = "ID")
            protected String id;
            @XmlAttribute(name = "ARCTYPE")
            protected String arctype;
            @XmlAttribute(name = "ADMID")
            @XmlIDREF
            @XmlSchemaType(name = "IDREFS")
            protected List<Object> admid;
            @XmlAttribute(name = "type", namespace = "http://www.w3.org/1999/xlink")
            protected String type;
            @XmlAttribute(name = "arcrole", namespace = "http://www.w3.org/1999/xlink")
            protected String arcrole;
            @XmlAttribute(name = "title", namespace = "http://www.w3.org/1999/xlink")
            protected String title;
            @XmlAttribute(name = "show", namespace = "http://www.w3.org/1999/xlink")
            protected String show;
            @XmlAttribute(name = "actuate", namespace = "http://www.w3.org/1999/xlink")
            protected String actuate;
            @XmlAttribute(name = "from", namespace = "http://www.w3.org/1999/xlink")
            protected String from;
            @XmlAttribute(name = "to", namespace = "http://www.w3.org/1999/xlink")
            protected String to;

            /**
             * Obtient la valeur de la propriété id.
             *
             * @return
             *     possible object is
             *     {@link String }
             *
             */
            public String getID() {
                return id;
            }

            /**
             * Définit la valeur de la propriété id.
             *
             * @param value
             *     allowed object is
             *     {@link String }
             *
             */
            public void setID(String value) {
                this.id = value;
            }

            /**
             * Obtient la valeur de la propriété arctype.
             *
             * @return
             *     possible object is
             *     {@link String }
             *
             */
            public String getARCTYPE() {
                return arctype;
            }

            /**
             * Définit la valeur de la propriété arctype.
             *
             * @param value
             *     allowed object is
             *     {@link String }
             *
             */
            public void setARCTYPE(String value) {
                this.arctype = value;
            }

            /**
             * Gets the value of the admid property.
             *
             * <p>
             * This accessor method returns a reference to the live list,
             * not a snapshot. Therefore any modification you make to the
             * returned list will be present inside the JAXB object.
             * This is why there is not a <CODE>set</CODE> method for the admid property.
             *
             * <p>
             * For example, to add a new item, do as follows:
             * <pre>
             *    getADMID().add(newItem);
             * </pre>
             *
             *
             * <p>
             * Objects of the following type(s) are allowed in the list
             * {@link Object }
             *
             *
             */
            public List<Object> getADMID() {
                if (admid == null) {
                    admid = new ArrayList<>();
                }
                return this.admid;
            }

            /**
             * Obtient la valeur de la propriété type.
             *
             * @return
             *     possible object is
             *     {@link String }
             *
             */
            public String getType() {
                if (type == null) {
                    return "arc";
                } else {
                    return type;
                }
            }

            /**
             * Définit la valeur de la propriété type.
             *
             * @param value
             *     allowed object is
             *     {@link String }
             *
             */
            public void setType(String value) {
                this.type = value;
            }

            /**
             * Obtient la valeur de la propriété arcrole.
             *
             * @return
             *     possible object is
             *     {@link String }
             *
             */
            public String getArcrole() {
                return arcrole;
            }

            /**
             * Définit la valeur de la propriété arcrole.
             *
             * @param value
             *     allowed object is
             *     {@link String }
             *
             */
            public void setArcrole(String value) {
                this.arcrole = value;
            }

            /**
             * Obtient la valeur de la propriété title.
             *
             * @return
             *     possible object is
             *     {@link String }
             *
             */
            public String getTitle() {
                return title;
            }

            /**
             * Définit la valeur de la propriété title.
             *
             * @param value
             *     allowed object is
             *     {@link String }
             *
             */
            public void setTitle(String value) {
                this.title = value;
            }

            /**
             * Obtient la valeur de la propriété show.
             *
             * @return
             *     possible object is
             *     {@link String }
             *
             */
            public String getShow() {
                return show;
            }

            /**
             * Définit la valeur de la propriété show.
             *
             * @param value
             *     allowed object is
             *     {@link String }
             *
             */
            public void setShow(String value) {
                this.show = value;
            }

            /**
             * Obtient la valeur de la propriété actuate.
             *
             * @return
             *     possible object is
             *     {@link String }
             *
             */
            public String getActuate() {
                return actuate;
            }

            /**
             * Définit la valeur de la propriété actuate.
             *
             * @param value
             *     allowed object is
             *     {@link String }
             *
             */
            public void setActuate(String value) {
                this.actuate = value;
            }

            /**
             * Obtient la valeur de la propriété from.
             *
             * @return
             *     possible object is
             *     {@link String }
             *
             */
            public String getFrom() {
                return from;
            }

            /**
             * Définit la valeur de la propriété from.
             *
             * @param value
             *     allowed object is
             *     {@link String }
             *
             */
            public void setFrom(String value) {
                this.from = value;
            }

            /**
             * Obtient la valeur de la propriété to.
             *
             * @return
             *     possible object is
             *     {@link String }
             *
             */
            public String getTo() {
                return to;
            }

            /**
             * Définit la valeur de la propriété to.
             *
             * @param value
             *     allowed object is
             *     {@link String }
             *
             */
            public void setTo(String value) {
                this.to = value;
            }

        }


        /**
         * <p>Classe Java pour anonymous complex type.
         *
         * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
         *
         * <pre>
         * &lt;complexType&gt;
         *   &lt;complexContent&gt;
         *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
         *       &lt;attGroup ref="{http://www.w3.org/1999/xlink}locatorLink"/&gt;
         *       &lt;attribute name="ID" type="{http://www.w3.org/2001/XMLSchema}ID" /&gt;
         *     &lt;/restriction&gt;
         *   &lt;/complexContent&gt;
         * &lt;/complexType&gt;
         * </pre>
         *
         *
         */
        @XmlAccessorType(XmlAccessType.FIELD)
        @XmlType(name = "")
        public static class SmLocatorLink {

            @XmlAttribute(name = "ID")
            @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
            @XmlID
            @XmlSchemaType(name = "ID")
            protected String id;
            @XmlAttribute(name = "type", namespace = "http://www.w3.org/1999/xlink")
            protected String type;
            @XmlAttribute(name = "href", namespace = "http://www.w3.org/1999/xlink", required = true)
            @XmlSchemaType(name = "anyURI")
            protected String href;
            @XmlAttribute(name = "role", namespace = "http://www.w3.org/1999/xlink")
            protected String role;
            @XmlAttribute(name = "title", namespace = "http://www.w3.org/1999/xlink")
            protected String title;
            @XmlAttribute(name = "label", namespace = "http://www.w3.org/1999/xlink")
            protected String label;

            /**
             * Obtient la valeur de la propriété id.
             *
             * @return
             *     possible object is
             *     {@link String }
             *
             */
            public String getID() {
                return id;
            }

            /**
             * Définit la valeur de la propriété id.
             *
             * @param value
             *     allowed object is
             *     {@link String }
             *
             */
            public void setID(String value) {
                this.id = value;
            }

            /**
             * Obtient la valeur de la propriété type.
             *
             * @return
             *     possible object is
             *     {@link String }
             *
             */
            public String getType() {
                if (type == null) {
                    return "locator";
                } else {
                    return type;
                }
            }

            /**
             * Définit la valeur de la propriété type.
             *
             * @param value
             *     allowed object is
             *     {@link String }
             *
             */
            public void setType(String value) {
                this.type = value;
            }

            /**
             * Obtient la valeur de la propriété href.
             *
             * @return
             *     possible object is
             *     {@link String }
             *
             */
            public String getHref() {
                return href;
            }

            /**
             * Définit la valeur de la propriété href.
             *
             * @param value
             *     allowed object is
             *     {@link String }
             *
             */
            public void setHref(String value) {
                this.href = value;
            }

            /**
             * Obtient la valeur de la propriété role.
             *
             * @return
             *     possible object is
             *     {@link String }
             *
             */
            public String getRole() {
                return role;
            }

            /**
             * Définit la valeur de la propriété role.
             *
             * @param value
             *     allowed object is
             *     {@link String }
             *
             */
            public void setRole(String value) {
                this.role = value;
            }

            /**
             * Obtient la valeur de la propriété title.
             *
             * @return
             *     possible object is
             *     {@link String }
             *
             */
            public String getTitle() {
                return title;
            }

            /**
             * Définit la valeur de la propriété title.
             *
             * @param value
             *     allowed object is
             *     {@link String }
             *
             */
            public void setTitle(String value) {
                this.title = value;
            }

            /**
             * Obtient la valeur de la propriété label.
             *
             * @return
             *     possible object is
             *     {@link String }
             *
             */
            public String getLabel() {
                return label;
            }

            /**
             * Définit la valeur de la propriété label.
             *
             * @param value
             *     allowed object is
             *     {@link String }
             *
             */
            public void setLabel(String value) {
                this.label = value;
            }

        }

    }

}
