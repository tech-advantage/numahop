package fr.progilone.pgcn.domain.es.document;

import static fr.progilone.pgcn.service.es.EsConstant.*;

import fr.progilone.pgcn.domain.document.DocProperty;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import org.springframework.data.elasticsearch.annotations.InnerField;
import org.springframework.data.elasticsearch.annotations.MultiField;

/**
 * Classe représentant une propriété d'une unité documentaire
 */
public class EsDocProperty implements Comparable<EsDocProperty> {

    @Field(type = FieldType.Keyword)
    private String identifier;

    /**
     * Valeur
     */
    @MultiField(mainField = @Field(type = FieldType.Text),
                otherFields = {@InnerField(type = FieldType.Keyword, suffix = SUBFIELD_RAW),
                               @InnerField(type = FieldType.Text, suffix = SUBFIELD_CI_AI, analyzer = ANALYZER_CI_AI, searchAnalyzer = ANALYZER_CI_AI),
                               @InnerField(type = FieldType.Text, suffix = SUBFIELD_CI_AS, analyzer = ANALYZER_CI_AS, searchAnalyzer = ANALYZER_CI_AS),
                               @InnerField(type = FieldType.Text, suffix = SUBFIELD_PHRASE, analyzer = ANALYZER_PHRASE, searchAnalyzer = ANALYZER_PHRASE)})
    private String value;

    /**
     * Type de propriété
     */
    @Field(type = FieldType.Object)
    private EsDocPropertyType type;

    /**
     * Langue
     */
    @Field(type = FieldType.Keyword)
    private String language;

    /**
     * Rang
     */
    @Field(type = FieldType.Integer)
    private Integer rank;

    public static EsDocProperty from(final DocProperty property) {
        final EsDocProperty esProperty = new EsDocProperty();
        esProperty.setIdentifier(property.getIdentifier());
        esProperty.setValue(property.getValue());
        esProperty.setLanguage(property.getLanguage());
        esProperty.setRank(property.getRank());
        esProperty.setType(EsDocPropertyType.from(property.getType()));
        return esProperty;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(final String identifier) {
        this.identifier = identifier;
    }

    public String getValue() {
        return value;
    }

    public void setValue(final String value) {
        this.value = value;
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

    public EsDocPropertyType getType() {
        return type;
    }

    public void setType(final EsDocPropertyType type) {
        this.type = type;
    }

    @Override
    public int compareTo(final EsDocProperty property) {
        if (property != null) {
            if (this.rank != null) {
                return this.rank.compareTo(property.getRank());
            }
            return -1;
        }
        return 1;
    }

}
