package fr.progilone.pgcn.domain.dto.statistics;

import java.util.List;

public class WorkflowDocUnitProgressDTOPending {
    private String projectIdentifier;
    private String projectName;
    private String docIdentifier;
    private String docPgcnId;
    private String docLabel;
    private String docStatus;
    private String lotIdentifier;
    private String lotLabel;
    private Integer totalPage;
    private List<String> workflowStateKeys;
    private WorkflowDocUnitInfoDTO infos;

    public String getProjectIdentifier() {
        return projectIdentifier;
    }

    public void setProjectIdentifier(String projectIdentifier) {
        this.projectIdentifier = projectIdentifier;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public String getDocIdentifier() {
        return docIdentifier;
    }

    public void setDocIdentifier(String docIdentifier) {
        this.docIdentifier = docIdentifier;
    }

    public String getDocPgcnId() {
        return docPgcnId;
    }

    public void setDocPgcnId(String docPgcnId) {
        this.docPgcnId = docPgcnId;
    }

    public String getDocLabel() {
        return docLabel;
    }

    public void setDocLabel(String docLabel) {
        this.docLabel = docLabel;
    }

    public String getDocStatus() {
        return docStatus;
    }

    public void setDocStatus(String docStatus) {
        this.docStatus = docStatus;
    }

    public Integer getTotalPage() { return totalPage;}

    public void setTotalPage(Integer totalPage) {
        this.totalPage = totalPage;
    }

    public List<String> getWorkflowStateKeys() {
        return workflowStateKeys;
    }

    public void setWorkflowStateKeys(List<String> workflowStateKeys) {
        this.workflowStateKeys = workflowStateKeys;
    }

    public WorkflowDocUnitInfoDTO getInfos() { return infos;}

    public void setInfos(WorkflowDocUnitInfoDTO infos) { this.infos = infos; }

    public String getLotIdentifier() { return lotIdentifier; }

    public void setLotIdentifier(String lotIdentifier) { this.lotIdentifier = lotIdentifier; }

    public String getLotLabel() { return lotLabel; }

    public void setLotLabel(String lotLabel) { this.lotLabel = lotLabel; }
}
