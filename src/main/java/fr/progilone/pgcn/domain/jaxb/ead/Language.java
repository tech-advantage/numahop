//
// Ce fichier a été généré par l'implémentation de référence JavaTM Architecture for XML Binding (JAXB), v2.2.8-b130911.1802
// Voir <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a>
// Toute modification apportée à ce fichier sera perdue lors de la recompilation du schéma source.
// Généré le : 2017.05.16 à 10:56:55 AM CEST
//

package fr.progilone.pgcn.domain.jaxb.ead;

import jakarta.xml.bind.JAXBElement;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElementRef;
import jakarta.xml.bind.annotation.XmlElementRefs;
import jakarta.xml.bind.annotation.XmlID;
import jakarta.xml.bind.annotation.XmlMixed;
import jakarta.xml.bind.annotation.XmlSchemaType;
import jakarta.xml.bind.annotation.XmlType;
import jakarta.xml.bind.annotation.adapters.CollapsedStringAdapter;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * Classe Java pour language complex type.
 *
 * <p>
 * Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 *
 * <pre>
 * &lt;complexType name="language">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;group ref="{urn:isbn:1-931666-22-9}m.phrase.bare" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;attGroup ref="{urn:isbn:1-931666-22-9}am.langcode"/>
 *       &lt;attGroup ref="{urn:isbn:1-931666-22-9}a.common"/>
 *       &lt;attribute name="scriptcode">
 *         &lt;simpleType>
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}token">
 *             &lt;enumeration value="Arab"/>
 *             &lt;enumeration value="Armn"/>
 *             &lt;enumeration value="Bali"/>
 *             &lt;enumeration value="Batk"/>
 *             &lt;enumeration value="Beng"/>
 *             &lt;enumeration value="Blis"/>
 *             &lt;enumeration value="Bopo"/>
 *             &lt;enumeration value="Brah"/>
 *             &lt;enumeration value="Brai"/>
 *             &lt;enumeration value="Bugi"/>
 *             &lt;enumeration value="Buhd"/>
 *             &lt;enumeration value="Cans"/>
 *             &lt;enumeration value="Cham"/>
 *             &lt;enumeration value="Cher"/>
 *             &lt;enumeration value="Cirt"/>
 *             &lt;enumeration value="Copt"/>
 *             &lt;enumeration value="Cprt"/>
 *             &lt;enumeration value="Cyrl"/>
 *             &lt;enumeration value="Cyrs"/>
 *             &lt;enumeration value="Deva"/>
 *             &lt;enumeration value="Dsrt"/>
 *             &lt;enumeration value="Egyd"/>
 *             &lt;enumeration value="Egyh"/>
 *             &lt;enumeration value="Egyp"/>
 *             &lt;enumeration value="Ethi"/>
 *             &lt;enumeration value="Geok"/>
 *             &lt;enumeration value="Geor"/>
 *             &lt;enumeration value="Glag"/>
 *             &lt;enumeration value="Goth"/>
 *             &lt;enumeration value="Grek"/>
 *             &lt;enumeration value="Gujr"/>
 *             &lt;enumeration value="Guru"/>
 *             &lt;enumeration value="Hang"/>
 *             &lt;enumeration value="Hani"/>
 *             &lt;enumeration value="Hano"/>
 *             &lt;enumeration value="Hans"/>
 *             &lt;enumeration value="Hant"/>
 *             &lt;enumeration value="Hebr"/>
 *             &lt;enumeration value="Hira"/>
 *             &lt;enumeration value="Hmng"/>
 *             &lt;enumeration value="Hrkt"/>
 *             &lt;enumeration value="Hung"/>
 *             &lt;enumeration value="Inds"/>
 *             &lt;enumeration value="Ital"/>
 *             &lt;enumeration value="Java"/>
 *             &lt;enumeration value="Kali"/>
 *             &lt;enumeration value="Kana"/>
 *             &lt;enumeration value="Khar"/>
 *             &lt;enumeration value="Khmr"/>
 *             &lt;enumeration value="Knda"/>
 *             &lt;enumeration value="Laoo"/>
 *             &lt;enumeration value="Latf"/>
 *             &lt;enumeration value="Latg"/>
 *             &lt;enumeration value="Latn"/>
 *             &lt;enumeration value="Lepc"/>
 *             &lt;enumeration value="Limb"/>
 *             &lt;enumeration value="Lina"/>
 *             &lt;enumeration value="Linb"/>
 *             &lt;enumeration value="Mand"/>
 *             &lt;enumeration value="Maya"/>
 *             &lt;enumeration value="Mero"/>
 *             &lt;enumeration value="Mlym"/>
 *             &lt;enumeration value="Mong"/>
 *             &lt;enumeration value="Mymr"/>
 *             &lt;enumeration value="Nkoo"/>
 *             &lt;enumeration value="Ogam"/>
 *             &lt;enumeration value="Orkh"/>
 *             &lt;enumeration value="Orya"/>
 *             &lt;enumeration value="Osma"/>
 *             &lt;enumeration value="Perm"/>
 *             &lt;enumeration value="Phag"/>
 *             &lt;enumeration value="Phnx"/>
 *             &lt;enumeration value="Plrd"/>
 *             &lt;enumeration value="Qaaa"/>
 *             &lt;enumeration value="Qabx"/>
 *             &lt;enumeration value="Roro"/>
 *             &lt;enumeration value="Runr"/>
 *             &lt;enumeration value="Sara"/>
 *             &lt;enumeration value="Shaw"/>
 *             &lt;enumeration value="Sinh"/>
 *             &lt;enumeration value="Sylo"/>
 *             &lt;enumeration value="Syrc"/>
 *             &lt;enumeration value="Syre"/>
 *             &lt;enumeration value="Syrj"/>
 *             &lt;enumeration value="Syrn"/>
 *             &lt;enumeration value="Tagb"/>
 *             &lt;enumeration value="Tale"/>
 *             &lt;enumeration value="Talu"/>
 *             &lt;enumeration value="Taml"/>
 *             &lt;enumeration value="Telu"/>
 *             &lt;enumeration value="Teng"/>
 *             &lt;enumeration value="Tfng"/>
 *             &lt;enumeration value="Tglg"/>
 *             &lt;enumeration value="Thaa"/>
 *             &lt;enumeration value="Thai"/>
 *             &lt;enumeration value="Tibt"/>
 *             &lt;enumeration value="Ugar"/>
 *             &lt;enumeration value="Vaii"/>
 *             &lt;enumeration value="Visp"/>
 *             &lt;enumeration value="Xpeo"/>
 *             &lt;enumeration value="Xsux"/>
 *             &lt;enumeration value="Yiii"/>
 *             &lt;enumeration value="Zxxx"/>
 *             &lt;enumeration value="Zyyy"/>
 *             &lt;enumeration value="Zzzz"/>
 *           &lt;/restriction>
 *         &lt;/simpleType>
 *       &lt;/attribute>
 *       &lt;attribute name="encodinganalog" type="{http://www.w3.org/2001/XMLSchema}anySimpleType" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "language", propOrder = {"content"})
public class Language {

    @XmlElementRefs({@XmlElementRef(name = "emph", namespace = "urn:isbn:1-931666-22-9", type = JAXBElement.class, required = false),
                     @XmlElementRef(name = "ptr", namespace = "urn:isbn:1-931666-22-9", type = JAXBElement.class, required = false),
                     @XmlElementRef(name = "extptr", namespace = "urn:isbn:1-931666-22-9", type = JAXBElement.class, required = false),
                     @XmlElementRef(name = "lb", namespace = "urn:isbn:1-931666-22-9", type = JAXBElement.class, required = false)})
    @XmlMixed
    protected List<Serializable> content;
    @XmlAttribute(name = "scriptcode")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    protected String scriptcode;
    @XmlAttribute(name = "encodinganalog")
    @XmlSchemaType(name = "anySimpleType")
    protected String encodinganalog;
    @XmlAttribute(name = "langcode")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    protected String langcode;
    @XmlAttribute(name = "id")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlID
    @XmlSchemaType(name = "ID")
    protected String id;
    @XmlAttribute(name = "altrender")
    @XmlSchemaType(name = "anySimpleType")
    protected String altrender;
    @XmlAttribute(name = "audience")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    protected String audience;

    /**
     * Gets the value of the content property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the content property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     *
     * <pre>
     * getContent().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     * {@link JAXBElement }{@code <}{@link Emph }{@code >}
     * {@link JAXBElement }{@code <}{@link Ptr }{@code >}
     * {@link JAXBElement }{@code <}{@link Extptr }{@code >}
     * {@link JAXBElement }{@code <}{@link Lb }{@code >}
     *
     *
     */
    public List<Serializable> getContent() {
        if (content == null) {
            content = new ArrayList<>();
        }
        return this.content;
    }

    /**
     * Obtient la valeur de la propriété scriptcode.
     *
     * @return
     *         possible object is
     *         {@link String }
     *
     */
    public String getScriptcode() {
        return scriptcode;
    }

    /**
     * Définit la valeur de la propriété scriptcode.
     *
     * @param value
     *            allowed object is
     *            {@link String }
     *
     */
    public void setScriptcode(String value) {
        this.scriptcode = value;
    }

    /**
     * Obtient la valeur de la propriété encodinganalog.
     *
     * @return
     *         possible object is
     *         {@link String }
     *
     */
    public String getEncodinganalog() {
        return encodinganalog;
    }

    /**
     * Définit la valeur de la propriété encodinganalog.
     *
     * @param value
     *            allowed object is
     *            {@link String }
     *
     */
    public void setEncodinganalog(String value) {
        this.encodinganalog = value;
    }

    /**
     * Obtient la valeur de la propriété langcode.
     *
     * @return
     *         possible object is
     *         {@link String }
     *
     */
    public String getLangcode() {
        return langcode;
    }

    /**
     * Définit la valeur de la propriété langcode.
     *
     * @param value
     *            allowed object is
     *            {@link String }
     *
     */
    public void setLangcode(String value) {
        this.langcode = value;
    }

    /**
     * Obtient la valeur de la propriété id.
     *
     * @return
     *         possible object is
     *         {@link String }
     *
     */
    public String getId() {
        return id;
    }

    /**
     * Définit la valeur de la propriété id.
     *
     * @param value
     *            allowed object is
     *            {@link String }
     *
     */
    public void setId(String value) {
        this.id = value;
    }

    /**
     * Obtient la valeur de la propriété altrender.
     *
     * @return
     *         possible object is
     *         {@link String }
     *
     */
    public String getAltrender() {
        return altrender;
    }

    /**
     * Définit la valeur de la propriété altrender.
     *
     * @param value
     *            allowed object is
     *            {@link String }
     *
     */
    public void setAltrender(String value) {
        this.altrender = value;
    }

    /**
     * Obtient la valeur de la propriété audience.
     *
     * @return
     *         possible object is
     *         {@link String }
     *
     */
    public String getAudience() {
        return audience;
    }

    /**
     * Définit la valeur de la propriété audience.
     *
     * @param value
     *            allowed object is
     *            {@link String }
     *
     */
    public void setAudience(String value) {
        this.audience = value;
    }

}
