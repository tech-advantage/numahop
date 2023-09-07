package fr.progilone.pgcn.domain.es.document;

import fr.progilone.pgcn.domain.document.DocPropertyType;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

/**
 * Classe représentant un type propriété d'une notice
 */
public class EsDocPropertyType {

    @Field(type = FieldType.Keyword)
    private String identifier;

    /**
     * Label
     */
    @Field(type = FieldType.Keyword)
    private String label;

    public static EsDocPropertyType from(final DocPropertyType type) {
        final EsDocPropertyType esType = new EsDocPropertyType();
        esType.setIdentifier(type.getIdentifier());
        esType.setLabel(type.getLabel());
        return esType;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(final String identifier) {
        this.identifier = identifier;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(final String label) {
        this.label = label;
    }

}
