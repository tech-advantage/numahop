package fr.progilone.pgcn.domain.document.conditionreport;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.google.common.base.MoreObjects;
import fr.progilone.pgcn.domain.AbstractDomainObject;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import org.apache.commons.lang3.StringUtils;

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
    private DescriptionProperty property;

    /**
     * Valeur prise par la propriété
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "value")
    private DescriptionValue value;

    /**
     * Champ texte additionnel
     */
    @Column(name = "comment", columnDefinition = "text")
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
