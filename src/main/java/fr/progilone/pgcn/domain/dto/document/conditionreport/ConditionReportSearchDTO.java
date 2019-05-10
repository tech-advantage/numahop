package fr.progilone.pgcn.domain.dto.document.conditionreport;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ConditionReportSearchDTO {

    // Unité documentaire
    private String docUnitId;
    private String docUnitPgcnId;
    private String docUnitLabel;
    // Dernier détail du constat d'état
    private String type;
    private LocalDate date;
    // Description
    private final List<ConditionReportValueDTO> properties = new ArrayList<>();

    public String getDocUnitId() {
        return docUnitId;
    }

    public void setDocUnitId(final String docUnitId) {
        this.docUnitId = docUnitId;
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

    public String getType() {
        return type;
    }

    public void setType(final String type) {
        this.type = type;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(final LocalDate date) {
        this.date = date;
    }

    public List<ConditionReportValueDTO> getProperties() {
        return properties;
    }

    public void setProperties(final List<ConditionReportValueDTO> properties) {
        this.properties.clear();
        this.properties.addAll(properties);
    }
}
