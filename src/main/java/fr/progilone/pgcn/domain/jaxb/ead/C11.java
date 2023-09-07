//
// Ce fichier a été généré par l'implémentation de référence JavaTM Architecture for XML Binding (JAXB), v2.2.8-b130911.1802
// Voir <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a>
// Toute modification apportée à ce fichier sera perdue lors de la recompilation du schéma source.
// Généré le : 2017.05.16 à 10:56:55 AM CEST
//

package fr.progilone.pgcn.domain.jaxb.ead;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlElements;
import jakarta.xml.bind.annotation.XmlID;
import jakarta.xml.bind.annotation.XmlSchemaType;
import jakarta.xml.bind.annotation.XmlType;
import jakarta.xml.bind.annotation.adapters.CollapsedStringAdapter;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * Classe Java pour c11 complex type.
 *
 * <p>
 * Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 *
 * <pre>
 * &lt;complexType name="c11">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="head" type="{urn:isbn:1-931666-22-9}head" minOccurs="0"/>
 *         &lt;element name="did" type="{urn:isbn:1-931666-22-9}did"/>
 *         &lt;group ref="{urn:isbn:1-931666-22-9}m.desc.full" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;sequence maxOccurs="unbounded" minOccurs="0">
 *           &lt;element name="thead" type="{urn:isbn:1-931666-22-9}thead" minOccurs="0"/>
 *           &lt;element name="c12" type="{urn:isbn:1-931666-22-9}c12" maxOccurs="unbounded"/>
 *         &lt;/sequence>
 *       &lt;/sequence>
 *       &lt;attGroup ref="{urn:isbn:1-931666-22-9}a.desc.c"/>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "c11",
         propOrder = {"head",
                      "did",
                      "mDescFull",
                      "theadAndC12"})
public class C11 {

    protected Head head;
    @XmlElement(required = true)
    protected Did did;
    @XmlElements({@XmlElement(name = "accessrestrict", type = Accessrestrict.class),
                  @XmlElement(name = "accruals", type = Accruals.class),
                  @XmlElement(name = "acqinfo", type = Acqinfo.class),
                  @XmlElement(name = "altformavail", type = Altformavail.class),
                  @XmlElement(name = "appraisal", type = Appraisal.class),
                  @XmlElement(name = "arrangement", type = Arrangement.class),
                  @XmlElement(name = "bibliography", type = Bibliography.class),
                  @XmlElement(name = "bioghist", type = Bioghist.class),
                  @XmlElement(name = "controlaccess", type = Controlaccess.class),
                  @XmlElement(name = "custodhist", type = Custodhist.class),
                  @XmlElement(name = "descgrp", type = Descgrp.class),
                  @XmlElement(name = "fileplan", type = Fileplan.class),
                  @XmlElement(name = "index", type = Index.class),
                  @XmlElement(name = "odd", type = Odd.class),
                  @XmlElement(name = "originalsloc", type = Originalsloc.class),
                  @XmlElement(name = "otherfindaid", type = Otherfindaid.class),
                  @XmlElement(name = "phystech", type = Phystech.class),
                  @XmlElement(name = "prefercite", type = Prefercite.class),
                  @XmlElement(name = "processinfo", type = Processinfo.class),
                  @XmlElement(name = "relatedmaterial", type = Relatedmaterial.class),
                  @XmlElement(name = "scopecontent", type = Scopecontent.class),
                  @XmlElement(name = "separatedmaterial", type = Separatedmaterial.class),
                  @XmlElement(name = "userestrict", type = Userestrict.class),
                  @XmlElement(name = "dsc", type = Dsc.class),
                  @XmlElement(name = "dao", type = Dao.class),
                  @XmlElement(name = "daogrp", type = Daogrp.class),
                  @XmlElement(name = "note", type = Note.class)})
    protected List<Object> mDescFull;
    @XmlElements({@XmlElement(name = "thead", type = Thead.class),
                  @XmlElement(name = "c12", type = C12.class)})
    protected List<Object> theadAndC12;
    @XmlAttribute(name = "level")
    protected AvLevel level;
    @XmlAttribute(name = "otherlevel")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlSchemaType(name = "NMTOKEN")
    protected String otherlevel;
    @XmlAttribute(name = "encodinganalog")
    @XmlSchemaType(name = "anySimpleType")
    protected String encodinganalog;
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
    @XmlAttribute(name = "tpattern")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlSchemaType(name = "NMTOKEN")
    protected String tpattern;

    /**
     * Obtient la valeur de la propriété head.
     *
     * @return
     *         possible object is
     *         {@link Head }
     *
     */
    public Head getHead() {
        return head;
    }

    /**
     * Définit la valeur de la propriété head.
     *
     * @param value
     *            allowed object is
     *            {@link Head }
     *
     */
    public void setHead(Head value) {
        this.head = value;
    }

    /**
     * Obtient la valeur de la propriété did.
     *
     * @return
     *         possible object is
     *         {@link Did }
     *
     */
    public Did getDid() {
        return did;
    }

    /**
     * Définit la valeur de la propriété did.
     *
     * @param value
     *            allowed object is
     *            {@link Did }
     *
     */
    public void setDid(Did value) {
        this.did = value;
    }

    /**
     * Gets the value of the mDescFull property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the mDescFull property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     *
     * <pre>
     * getMDescFull().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Accessrestrict }
     * {@link Accruals }
     * {@link Acqinfo }
     * {@link Altformavail }
     * {@link Appraisal }
     * {@link Arrangement }
     * {@link Bibliography }
     * {@link Bioghist }
     * {@link Controlaccess }
     * {@link Custodhist }
     * {@link Descgrp }
     * {@link Fileplan }
     * {@link Index }
     * {@link Odd }
     * {@link Originalsloc }
     * {@link Otherfindaid }
     * {@link Phystech }
     * {@link Prefercite }
     * {@link Processinfo }
     * {@link Relatedmaterial }
     * {@link Scopecontent }
     * {@link Separatedmaterial }
     * {@link Userestrict }
     * {@link Dsc }
     * {@link Dao }
     * {@link Daogrp }
     * {@link Note }
     *
     *
     */
    public List<Object> getMDescFull() {
        if (mDescFull == null) {
            mDescFull = new ArrayList<>();
        }
        return this.mDescFull;
    }

    /**
     * Gets the value of the theadAndC12 property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the theadAndC12 property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     *
     * <pre>
     * getTheadAndC12().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Thead }
     * {@link C12 }
     *
     *
     */
    public List<Object> getTheadAndC12() {
        if (theadAndC12 == null) {
            theadAndC12 = new ArrayList<>();
        }
        return this.theadAndC12;
    }

    /**
     * Obtient la valeur de la propriété level.
     *
     * @return
     *         possible object is
     *         {@link AvLevel }
     *
     */
    public AvLevel getLevel() {
        return level;
    }

    /**
     * Définit la valeur de la propriété level.
     *
     * @param value
     *            allowed object is
     *            {@link AvLevel }
     *
     */
    public void setLevel(AvLevel value) {
        this.level = value;
    }

    /**
     * Obtient la valeur de la propriété otherlevel.
     *
     * @return
     *         possible object is
     *         {@link String }
     *
     */
    public String getOtherlevel() {
        return otherlevel;
    }

    /**
     * Définit la valeur de la propriété otherlevel.
     *
     * @param value
     *            allowed object is
     *            {@link String }
     *
     */
    public void setOtherlevel(String value) {
        this.otherlevel = value;
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

    /**
     * Obtient la valeur de la propriété tpattern.
     *
     * @return
     *         possible object is
     *         {@link String }
     *
     */
    public String getTpattern() {
        return tpattern;
    }

    /**
     * Définit la valeur de la propriété tpattern.
     *
     * @param value
     *            allowed object is
     *            {@link String }
     *
     */
    public void setTpattern(String value) {
        this.tpattern = value;
    }

}
