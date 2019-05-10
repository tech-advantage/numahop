package fr.progilone.pgcn.domain.dto.document;

import java.util.ArrayList;
import java.util.List;

public class DocUnitMassUpdateDTO {

    private final List<String> docUnitIds = new ArrayList<>();
    private boolean archivable;
    private boolean distributable;
    private String condReportType;
    
    public boolean isArchivable() {
        return archivable;
    }
    public void setArchivable(boolean archivable) {
        this.archivable = archivable;
    }
    public boolean isDistributable() {
        return distributable;
    }
    public void setDistributable(boolean distributable) {
        this.distributable = distributable;
    }
    public String getCondReportType() {
        return condReportType;
    }
    public void setCondReportType(String condReportType) {
        this.condReportType = condReportType;
    }
    public List<String> getDocUnitIds() {
        return docUnitIds;
    }
    

    
}
