package fr.progilone.pgcn.domain.es.conditionreport;

import static fr.progilone.pgcn.service.es.EsConstant.*;

import fr.progilone.pgcn.domain.document.conditionreport.DescriptionValue;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import org.springframework.data.elasticsearch.annotations.InnerField;
import org.springframework.data.elasticsearch.annotations.MultiField;

public class EsDescriptionValue {

    @Field(type = FieldType.Keyword)
    private String identifier;

    @MultiField(mainField = @Field(type = FieldType.Text),
                otherFields = {@InnerField(type = FieldType.Keyword, suffix = SUBFIELD_RAW),
                               @InnerField(type = FieldType.Text, suffix = SUBFIELD_CI_AI, analyzer = ANALYZER_CI_AI, searchAnalyzer = ANALYZER_CI_AI),
                               @InnerField(type = FieldType.Text, suffix = SUBFIELD_CI_AS, analyzer = ANALYZER_CI_AS, searchAnalyzer = ANALYZER_CI_AS),
                               @InnerField(type = FieldType.Text, suffix = SUBFIELD_PHRASE, analyzer = ANALYZER_PHRASE, searchAnalyzer = ANALYZER_PHRASE)})
    private String label;

    public static EsDescriptionValue from(final DescriptionValue value) {
        final EsDescriptionValue esValue = new EsDescriptionValue();
        if (value != null) {
            esValue.setIdentifier(value.getIdentifier());
            esValue.setLabel(value.getLabel());
        }
        return esValue;
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
