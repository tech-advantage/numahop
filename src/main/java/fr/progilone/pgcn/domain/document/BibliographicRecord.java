package fr.progilone.pgcn.domain.document;

import static fr.progilone.pgcn.service.es.EsConstant.ANALYZER_CI_AI;
import static fr.progilone.pgcn.service.es.EsConstant.ANALYZER_CI_AS;
import static fr.progilone.pgcn.service.es.EsConstant.ANALYZER_KEYWORD;
import static fr.progilone.pgcn.service.es.EsConstant.ANALYZER_PHRASE;
import static fr.progilone.pgcn.service.es.EsConstant.SUBFIELD_CI_AI;
import static fr.progilone.pgcn.service.es.EsConstant.SUBFIELD_CI_AS;
import static fr.progilone.pgcn.service.es.EsConstant.SUBFIELD_PHRASE;
import static fr.progilone.pgcn.service.es.EsConstant.SUBFIELD_RAW;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Stream;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldIndex;
import org.springframework.data.elasticsearch.annotations.FieldType;
import org.springframework.data.elasticsearch.annotations.InnerField;
import org.springframework.data.elasticsearch.annotations.MultiField;
import org.springframework.data.elasticsearch.annotations.Parent;

import com.fasterxml.jackson.annotation.JsonSubTypes;

import fr.progilone.pgcn.domain.AbstractDomainObject;
import fr.progilone.pgcn.domain.library.Library;

// Hibernate
@Entity
@Table(name = BibliographicRecord.TABLE_NAME)
// Jackson
@JsonSubTypes({@JsonSubTypes.Type(name = "doc_bibliographic_record", value = BibliographicRecord.class)})
// Elasticsearch
@Document(indexName = "#{elasticsearchIndexName}", type = BibliographicRecord.ES_TYPE, createIndex = false)
public class BibliographicRecord extends AbstractDomainObject {

    public static final String TABLE_NAME = "doc_bibliographic_record";
    public static final String ES_TYPE = "bib_record";

    /**
     * Titre récupéré des propriétés
     */
    @Column(name = "title", columnDefinition = "text")
    @MultiField(mainField = @Field(type = FieldType.String),
                otherFields = {@InnerField(type = FieldType.String, suffix = SUBFIELD_RAW, index = FieldIndex.not_analyzed),
                               @InnerField(type = FieldType.String,
                                           suffix = SUBFIELD_CI_AI,
                                           indexAnalyzer = ANALYZER_CI_AI,
                                           searchAnalyzer = ANALYZER_CI_AI),
                               @InnerField(type = FieldType.String,
                                           suffix = SUBFIELD_CI_AS,
                                           indexAnalyzer = ANALYZER_CI_AS,
                                           searchAnalyzer = ANALYZER_CI_AS),
                               @InnerField(type = FieldType.String,
                                           suffix = SUBFIELD_PHRASE,
                                           indexAnalyzer = ANALYZER_PHRASE,
                                           searchAnalyzer = ANALYZER_PHRASE)})
    private String title;

    /**
     * URL vers le SIGB
     */
    @Column(name = "sigb")
    @Field(type = FieldType.String, analyzer = ANALYZER_KEYWORD)
    private String sigb;

    /**
     * identifiant SUDOC
     */
    @Column
    @Field(type = FieldType.String, analyzer = ANALYZER_KEYWORD)
    private String sudoc;

    /**
     * identifiant Calames
     */
    @Column
    @Field(type = FieldType.String, analyzer = ANALYZER_KEYWORD)
    private String calames;

    /**
     * URL document électronique
     */
    @Column
    @Field(type = FieldType.String, analyzer = ANALYZER_KEYWORD)
    private String docElectronique;

    /**
     * Liste des propriétés de la notice (DC, DCq, Custom)
     */
    @OneToMany(mappedBy = "record", orphanRemoval = true, fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @Field(type = FieldType.Nested)
    private final Set<DocProperty> properties = new HashSet<>();

    /**
     * Unité documentaire rattachée
     */
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "doc_unit")
    private DocUnit docUnit;

    /**
     * Le champ "Unité documentaire rattachée" est répété pour la config elasticsearch @Parent, qui doit être de type String
     */
    @Column(name = "doc_unit", insertable = false, updatable = false)
    @Parent(type = DocUnit.ES_TYPE)
    @Field(type = FieldType.String, index = FieldIndex.not_analyzed)
    private String docUnitId;

    /**
     * Bibliothèque de rattachement
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "library")
    @Field(type = FieldType.Object)
    private Library library;

    /**
     * Type de tri des propriétés
     */
    @Column(name = "property_order", nullable = false)
    @Enumerated(EnumType.STRING)
    private PropertyOrder propertyOrder = PropertyOrder.BY_PROPERTY_TYPE;

    public DocUnit getDocUnit() {
        return docUnit;
    }

    public void setDocUnit(final DocUnit docUnit) {
        this.docUnit = docUnit;
    }

    public String getDocUnitId() {
        return docUnitId;
    }

    public void setDocUnitId(final String docUnitId) {
        this.docUnitId = docUnitId;
    }

    public Library getLibrary() {
        return library;
    }

    public void setLibrary(final Library library) {
        this.library = library;
    }

    public Set<DocProperty> getProperties() {
        return properties;
    }

    public void setProperties(final Set<DocProperty> properties) {
        this.properties.clear();
        if (properties != null) {
            properties.forEach(this::addProperty);
        }
    }

    public void addProperty(final DocProperty property) {
        if (property != null) {
            this.properties.add(property);
            property.setRecord(this);
        }
    }

    public String getSigb() {
        return sigb;
    }

    public void setSigb(final String sigb) {
        this.sigb = sigb;
    }

    public String getSudoc() {
        return sudoc;
    }

    public void setSudoc(final String sudoc) {
        this.sudoc = sudoc;
    }

    public String getCalames() {
        return calames;
    }

    public void setCalames(final String calames) {
        this.calames = calames;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(final String title) {
        this.title = title;
    }

    public String getDocElectronique() {
        return docElectronique;
    }

    public void setDocElectronique(final String docElectronique) {
        this.docElectronique = docElectronique;
    }

    public PropertyOrder getPropertyOrder() {
        return propertyOrder;
    }

    public void setPropertyOrder(final PropertyOrder propertyOrder) {
        this.propertyOrder = propertyOrder;
    }

    // On redéfinit la lastModifiedDate pour l'indexation
    @Transient
    public LocalDateTime getGeneralLastModifiedDate() {
        return Stream.concat(Stream.of(getLastModifiedDate()), this.properties.stream().map(AbstractDomainObject::getLastModifiedDate))
                     .max(LocalDateTime::compareTo)
                     .orElse(null);
    }

    public enum PropertyOrder {
        /**
         * Les propriétés sont ordonnées suivant l'ordre de création
         */
        BY_CREATION,
        /**
         * Les propriétés sont ordonnées suivant l'ordre des types de propriété
         */
        BY_PROPERTY_TYPE
    }
}
