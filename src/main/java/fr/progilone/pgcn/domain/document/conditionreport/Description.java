package fr.progilone.pgcn.domain.document.conditionreport;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.google.common.base.MoreObjects;
import fr.progilone.pgcn.domain.AbstractDomainObject;
import org.apache.commons.lang3.StringUtils;
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
@Table(name = Description.TABLE_NAME)
@JsonSubTypes({@JsonSubTypes.Type(name = "doc_condreport_desc", value = Description.class)})
public class Description extends AbstractDomainObject {

    public static final String TABLE_NAME = "doc_condreport_desc";

    /**
     * Propriété
     */
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "property")
    @Field(type = FieldType.Object)
    private DescriptionProperty property;

    /**
     * Valeur prise par la propriété
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "value")
    @Field(type = FieldType.Object)
    private DescriptionValue value;

    /**
     * Champ texte additionnel
     */
    @Column(name = "comment", columnDefinition = "text")
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
    private String comment;

    /**
     * Constat d'état
     */
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "detail")
    private ConditionReportDetail detail;

    public DescriptionProperty getProperty() {
        return property;
    }

    public void setProperty(final DescriptionProperty property) {
        this.property = property;
    }

    public DescriptionValue getValue() {
        return value;
    }

    public void setValue(final DescriptionValue value) {
        this.value = value;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(final String comment) {
        this.comment = comment;
    }

    public ConditionReportDetail getDetail() {
        return detail;
    }

    public void setDetail(final ConditionReportDetail detail) {
        this.detail = detail;
    }

    public boolean isEmpty() {
        return value == null && StringUtils.isBlank(comment);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this).add("property", property).add("value", value).add("comment", comment).toString();
    }
}
