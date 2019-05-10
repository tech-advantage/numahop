package fr.progilone.pgcn.domain.document.conditionreport;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.google.common.base.MoreObjects;
import fr.progilone.pgcn.domain.AbstractDomainObject;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldIndex;
import org.springframework.data.elasticsearch.annotations.FieldType;
import org.springframework.data.elasticsearch.annotations.InnerField;
import org.springframework.data.elasticsearch.annotations.MultiField;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import static fr.progilone.pgcn.service.es.EsConstant.*;

@Entity
@Table(name = DescriptionValue.TABLE_NAME)
@JsonSubTypes({@JsonSubTypes.Type(name = "doc_condreport_desc_value", value = DescriptionValue.class)})
public class DescriptionValue extends AbstractDomainObject {

    public static final String TABLE_NAME = "doc_condreport_desc_value";

    @Column(name = "label", nullable = false)
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
    private String label;

    /**
     * Constat d'état
     */
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "property")
    private DescriptionProperty property;

    public String getLabel() {
        return label;
    }

    public void setLabel(final String label) {
        this.label = label;
    }

    public DescriptionProperty getProperty() {
        return property;
    }

    public void setProperty(final DescriptionProperty property) {
        this.property = property;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this).add("label", label).toString();
    }
}
