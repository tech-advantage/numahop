package fr.progilone.pgcn.domain.document;

import static fr.progilone.pgcn.service.es.EsConstant.ANALYZER_CI_AI;
import static fr.progilone.pgcn.service.es.EsConstant.ANALYZER_CI_AS;
import static fr.progilone.pgcn.service.es.EsConstant.ANALYZER_KEYWORD;
import static fr.progilone.pgcn.service.es.EsConstant.ANALYZER_PHRASE;
import static fr.progilone.pgcn.service.es.EsConstant.SUBFIELD_CI_AI;
import static fr.progilone.pgcn.service.es.EsConstant.SUBFIELD_CI_AS;
import static fr.progilone.pgcn.service.es.EsConstant.SUBFIELD_PHRASE;
import static fr.progilone.pgcn.service.es.EsConstant.SUBFIELD_RAW;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldIndex;
import org.springframework.data.elasticsearch.annotations.FieldType;
import org.springframework.data.elasticsearch.annotations.InnerField;
import org.springframework.data.elasticsearch.annotations.MultiField;

import com.fasterxml.jackson.annotation.JsonSubTypes;

import fr.progilone.pgcn.domain.AbstractDomainObject;

/**
 * Classe représentant une propriété d'une unité documentaire
 *
 * @author jbrunet
 */
@Entity
@Table(name = DocProperty.TABLE_NAME)
@JsonSubTypes({@JsonSubTypes.Type(name = "doc_property", value = DocProperty.class)})
public class DocProperty extends AbstractDomainObject implements Comparable<DocProperty> {

    public static final String TABLE_NAME = "doc_property";

    /**
     * Valeur
     */
    @Column(name = "value", columnDefinition = "text")
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
    private String value;

    /**
     * Type de propriété
     */
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "type")
    @Field(type = FieldType.Object)
    private DocPropertyType type;

    /**
     * Notice rattachée
     */
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "record")
    private BibliographicRecord record;

    /**
     * Langue
     */
    @Column(name = "language")
    @Field(type = FieldType.String, analyzer = ANALYZER_KEYWORD)
    private String language;

    /**
     * Rang
     */
    @Column(name = "rank", nullable = false)
    @Field(type = FieldType.Integer)
    private Integer rank;

    public String getValue() {
        return value;
    }

    public void setValue(final String value) {
        this.value = value;
    }

    public DocPropertyType getType() {
        return type;
    }

    public void setType(final DocPropertyType type) {
        this.type = type;
    }

    public BibliographicRecord getRecord() {
        return record;
    }

    public void setRecord(final BibliographicRecord record) {
        this.record = record;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(final String language) {
        this.language = language;
    }

    public Integer getRank() {
        return rank;
    }

    public void setRank(final Integer rank) {
        this.rank = rank;
    }

    @Override
    public int compareTo(final DocProperty property) {
        if (property != null) {
            if (this.rank != null) {
                return this.rank.compareTo(property.getRank());
            }
            return -1;
        }
        return 1;
    }

    @Override
    public boolean equals(final Object obj) {
        return super.equals(obj);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

}
