package fr.progilone.pgcn.domain.es.conditionreport;

import static fr.progilone.pgcn.service.es.EsConstant.*;

import fr.progilone.pgcn.domain.document.conditionreport.Description;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import org.springframework.data.elasticsearch.annotations.InnerField;
import org.springframework.data.elasticsearch.annotations.MultiField;

public class EsDescription {

    @Field(type = FieldType.Keyword)
    private String identifier;

    /**
     * Propriété
     */
    @Field(type = FieldType.Object)
    private EsDescriptionProperty property;

    /**
     * Valeur prise par la propriété
     */
    @Field(type = FieldType.Object)
    private EsDescriptionValue value;

    /**
     * Champ texte additionnel
     */
    @MultiField(mainField = @Field(type = FieldType.Text),
                otherFields = {@InnerField(type = FieldType.Keyword, suffix = SUBFIELD_RAW),
                               @InnerField(type = FieldType.Text, suffix = SUBFIELD_CI_AI, analyzer = ANALYZER_CI_AI, searchAnalyzer = ANALYZER_CI_AI),
                               @InnerField(type = FieldType.Text, suffix = SUBFIELD_CI_AS, analyzer = ANALYZER_CI_AS, searchAnalyzer = ANALYZER_CI_AS),
                               @InnerField(type = FieldType.Text, suffix = SUBFIELD_PHRASE, analyzer = ANALYZER_PHRASE, searchAnalyzer = ANALYZER_PHRASE)})
    private String comment;

    public static EsDescription from(final Description description) {
        final EsDescription esDescription = new EsDescription();
        esDescription.setIdentifier(description.getIdentifier());
        if (description.getProperty() != null) {
            esDescription.setProperty(EsDescriptionProperty.from(description.getProperty()));
        }
        if (description.getValue() != null) {
            esDescription.setValue(EsDescriptionValue.from(description.getValue()));
        }
        esDescription.setComment(description.getComment());
        return esDescription;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(final String identifier) {
        this.identifier = identifier;
    }

    public EsDescriptionProperty getProperty() {
        return property;
    }

    public void setProperty(final EsDescriptionProperty property) {
        this.property = property;
    }

    public EsDescriptionValue getValue() {
        return value;
    }

    public void setValue(final EsDescriptionValue value) {
        this.value = value;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(final String comment) {
        this.comment = comment;
    }

}
