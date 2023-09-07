package fr.progilone.pgcn.domain.es.conditionreport;

import fr.progilone.pgcn.domain.document.conditionreport.DescriptionProperty;
import fr.progilone.pgcn.domain.document.conditionreport.DescriptionProperty.Type;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

/**
 * Propriété d'état de la reliure
 */
public class EsDescriptionProperty {

    @Field(type = FieldType.Keyword)
    private String identifier;

    /**
     * Libellé de la propriété
     */
    @Field(type = FieldType.Keyword)
    private String label;

    /**
     * Code de la propriété
     */
    @Field(type = FieldType.Keyword)
    private String code;

    /**
     * Type de la propriété
     */
    @Field(type = FieldType.Keyword)
    private Type type;

    /**
     * numero d'ordre
     */
    @Field(type = FieldType.Integer)
    private int order;

    public static EsDescriptionProperty from(final DescriptionProperty property) {
        final EsDescriptionProperty esProperty = new EsDescriptionProperty();
        esProperty.setIdentifier(property.getIdentifier());
        return esProperty;
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

    public String getCode() {
        return code;
    }

    public void setCode(final String code) {
        this.code = code;
    }

    public Type getType() {
        return type;
    }

    public void setType(final Type type) {
        this.type = type;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(final int order) {
        this.order = order;
    }

}
