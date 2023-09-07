package fr.progilone.pgcn.domain.es.conditionreport;

import fr.progilone.pgcn.domain.document.DocUnit;
import fr.progilone.pgcn.domain.document.conditionreport.ConditionReport;
import fr.progilone.pgcn.domain.document.conditionreport.ConditionReportDetail;
import java.util.Comparator;
import java.util.Optional;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import org.springframework.data.elasticsearch.annotations.Setting;

/**
 * Constat d'état: données générales
 */
@Document(indexName = "#{@environment.getProperty('elasticsearch.index.name')}-condition-report")
@Setting(settingPath = "config/elasticsearch/settings_pgcn.json", shards = 1, replicas = 0)
public class EsConditionReport {

    @Id
    @Field(type = FieldType.Keyword)
    private String identifier;

    @Field(type = FieldType.Keyword)
    private String libraryId;

    /**
     * Constats d'états effectués
     */
    @Field(type = FieldType.Object)
    private EsConditionReportDetail details;

    @Field(type = FieldType.Keyword)
    private String docUnitId;

    @Field(type = FieldType.Keyword)
    private String docUnitPgcnId;

    @Field(type = FieldType.Keyword)
    private String docUnitLabel;

    @Field(type = FieldType.Keyword)
    private DocUnit.CondReportType docUnitCondReportType;

    public static EsConditionReport from(final ConditionReport report) {
        final EsConditionReport esReport = new EsConditionReport();
        esReport.setIdentifier(report.getIdentifier());
        if (report.getDocUnit() != null) {
            esReport.setDocUnitId(report.getDocUnit().getIdentifier());
            if (report.getDocUnit().getLibrary() != null) {
                esReport.setLibraryId(report.getDocUnit().getLibrary().getIdentifier());
            }
            esReport.setDocUnitPgcnId(report.getDocUnit().getPgcnId());
            esReport.setDocUnitLabel(report.getDocUnit().getLabel());
            esReport.setDocUnitCondReportType(report.getDocUnit().getCondReportType());
        }
        // Dernier détail
        final Optional<ConditionReportDetail> detOpt = report.getDetails().stream().max(Comparator.comparing(ConditionReportDetail::getPosition));
        if (detOpt.isPresent()) {
            esReport.setDetails(EsConditionReportDetail.from(detOpt.get()));
        }
        return esReport;
    }

    public EsConditionReportDetail getDetails() {
        return details;
    }

    public void setDetails(final EsConditionReportDetail details) {
        this.details = details;
    }

    public String getDocUnitId() {
        return docUnitId;
    }

    public void setDocUnitId(final String docUnitId) {
        this.docUnitId = docUnitId;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(final String identifier) {
        this.identifier = identifier;
    }

    public String getLibraryId() {
        return libraryId;
    }

    public void setLibraryId(final String libraryId) {
        this.libraryId = libraryId;
    }

    public String getDocUnitPgcnId() {
        return docUnitPgcnId;
    }

    public void setDocUnitPgcnId(final String docUnitPgcnId) {
        this.docUnitPgcnId = docUnitPgcnId;
    }

    public String getDocUnitLabel() {
        return docUnitLabel;
    }

    public void setDocUnitLabel(final String docUnitLabel) {
        this.docUnitLabel = docUnitLabel;
    }

    public DocUnit.CondReportType getDocUnitCondReportType() {
        return docUnitCondReportType;
    }

    public void setDocUnitCondReportType(final DocUnit.CondReportType docUnitCondReportType) {
        this.docUnitCondReportType = docUnitCondReportType;
    }

}
