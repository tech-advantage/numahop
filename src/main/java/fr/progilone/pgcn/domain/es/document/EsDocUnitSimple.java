package fr.progilone.pgcn.domain.es.document;

import static fr.progilone.pgcn.service.es.EsConstant.*;

import fr.progilone.pgcn.domain.document.DocUnit;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import org.springframework.data.elasticsearch.annotations.InnerField;
import org.springframework.data.elasticsearch.annotations.MultiField;

/**
 * Classe représentant une unité documentaire très simplifiée
 */
public class EsDocUnitSimple {

    @Field(type = FieldType.Keyword)
    private String identifier;

    @MultiField(mainField = @Field(type = FieldType.Text),
                otherFields = {@InnerField(type = FieldType.Keyword, suffix = SUBFIELD_RAW),
                               @InnerField(type = FieldType.Text, suffix = SUBFIELD_CI_AI, analyzer = ANALYZER_CI_AI, searchAnalyzer = ANALYZER_CI_AI),
                               @InnerField(type = FieldType.Text, suffix = SUBFIELD_CI_AS, analyzer = ANALYZER_CI_AS, searchAnalyzer = ANALYZER_CI_AS),
                               @InnerField(type = FieldType.Text, suffix = SUBFIELD_PHRASE, analyzer = ANALYZER_PHRASE, searchAnalyzer = ANALYZER_PHRASE)})
    private String label;

    public static EsDocUnitSimple from(final DocUnit docUnit) {
        final EsDocUnitSimple esDocUnit = new EsDocUnitSimple();
        esDocUnit.setIdentifier(docUnit.getIdentifier());
        esDocUnit.setLabel(docUnit.getLabel());
        return esDocUnit;
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
