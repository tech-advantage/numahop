package fr.progilone.pgcn.domain.es.conditionreport;

import static fr.progilone.pgcn.service.es.EsConstant.*;

import fr.progilone.pgcn.domain.document.conditionreport.ConditionReportDetail;
import fr.progilone.pgcn.domain.document.conditionreport.ConditionReportDetail.Type;
import java.time.LocalDate;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import org.springframework.data.elasticsearch.annotations.InnerField;
import org.springframework.data.elasticsearch.annotations.MultiField;

/**
 * Constat d'état: données d'un constat particulier
 */
public class EsConditionReportDetail {

    @Field(type = FieldType.Keyword)
    private String identifier;

    /**
     * Type du constat d'état
     */
    @Field(type = FieldType.Keyword)
    private Type type;

    @Field(type = FieldType.Integer)
    private int sortedType;

    /**
     * Date du constat
     */
    @Field(type = FieldType.Date)
    private LocalDate date;

    /**
     * Propriétés du constat d'état
     */
    @Field(type = FieldType.Nested)
    private Set<EsDescription> descriptions = new LinkedHashSet<>();

    /**
     * État de la reliure: autres infos
     */
    @MultiField(mainField = @Field(type = FieldType.Text),
                otherFields = {@InnerField(type = FieldType.Keyword, suffix = SUBFIELD_RAW),
                               @InnerField(type = FieldType.Text, suffix = SUBFIELD_CI_AI, analyzer = ANALYZER_CI_AI, searchAnalyzer = ANALYZER_CI_AI),
                               @InnerField(type = FieldType.Text, suffix = SUBFIELD_CI_AS, analyzer = ANALYZER_CI_AS, searchAnalyzer = ANALYZER_CI_AS),
                               @InnerField(type = FieldType.Text, suffix = SUBFIELD_PHRASE, analyzer = ANALYZER_PHRASE, searchAnalyzer = ANALYZER_PHRASE)})
    private String bindingDesc;

    /**
     * État du corps d'ouvrage: autres infos
     */
    @MultiField(mainField = @Field(type = FieldType.Text),
                otherFields = {@InnerField(type = FieldType.Keyword, suffix = SUBFIELD_RAW),
                               @InnerField(type = FieldType.Text, suffix = SUBFIELD_CI_AI, analyzer = ANALYZER_CI_AI, searchAnalyzer = ANALYZER_CI_AI),
                               @InnerField(type = FieldType.Text, suffix = SUBFIELD_CI_AS, analyzer = ANALYZER_CI_AS, searchAnalyzer = ANALYZER_CI_AS),
                               @InnerField(type = FieldType.Text, suffix = SUBFIELD_PHRASE, analyzer = ANALYZER_PHRASE, searchAnalyzer = ANALYZER_PHRASE)})
    private String bodyDesc;

    /**
     * Description: autres infos
     */
    @MultiField(mainField = @Field(type = FieldType.Text),
                otherFields = {@InnerField(type = FieldType.Keyword, suffix = SUBFIELD_RAW),
                               @InnerField(type = FieldType.Text, suffix = SUBFIELD_CI_AI, analyzer = ANALYZER_CI_AI, searchAnalyzer = ANALYZER_CI_AI),
                               @InnerField(type = FieldType.Text, suffix = SUBFIELD_CI_AS, analyzer = ANALYZER_CI_AS, searchAnalyzer = ANALYZER_CI_AS),
                               @InnerField(type = FieldType.Text, suffix = SUBFIELD_PHRASE, analyzer = ANALYZER_PHRASE, searchAnalyzer = ANALYZER_PHRASE)})
    private String additionnalDesc;

    public static EsConditionReportDetail from(final ConditionReportDetail detail) {
        final EsConditionReportDetail esDetail = new EsConditionReportDetail();
        esDetail.setIdentifier(detail.getIdentifier());
        esDetail.setType(detail.getType());
        esDetail.setDate(detail.getDate());
        esDetail.setDescriptions(detail.getDescriptions().stream().map(EsDescription::from).collect(Collectors.toSet()));
        esDetail.setAdditionnalDesc(detail.getAdditionnalDesc());
        esDetail.setBindingDesc(detail.getBindingDesc());
        esDetail.setBodyDesc(detail.getBodyDesc());
        esDetail.setSortedType(detail.getType() != null ? detail.getType().ordinal()
                                                        : Integer.MAX_VALUE);
        return esDetail;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(final String identifier) {
        this.identifier = identifier;
    }

    public Type getType() {
        return type;
    }

    public void setType(final Type type) {
        this.type = type;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(final LocalDate date) {
        this.date = date;
    }

    public Set<EsDescription> getDescriptions() {
        return descriptions;
    }

    public void setDescriptions(final Set<EsDescription> descriptions) {
        this.descriptions = descriptions;
    }

    public String getBindingDesc() {
        return bindingDesc;
    }

    public void setBindingDesc(final String bindingDesc) {
        this.bindingDesc = bindingDesc;
    }

    public String getBodyDesc() {
        return bodyDesc;
    }

    public void setBodyDesc(final String bodyDesc) {
        this.bodyDesc = bodyDesc;
    }

    public String getAdditionnalDesc() {
        return additionnalDesc;
    }

    public void setAdditionnalDesc(final String additionnalDesc) {
        this.additionnalDesc = additionnalDesc;
    }

    public int getSortedType() {
        return sortedType;
    }

    public void setSortedType(final int sortedType) {
        this.sortedType = sortedType;
    }

}
