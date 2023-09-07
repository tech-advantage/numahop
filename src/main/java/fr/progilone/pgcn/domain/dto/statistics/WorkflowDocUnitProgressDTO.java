package fr.progilone.pgcn.domain.dto.statistics;

import fr.progilone.pgcn.domain.workflow.WorkflowStateKey;
import fr.progilone.pgcn.domain.workflow.WorkflowStateStatus;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class WorkflowDocUnitProgressDTO {

    private String libraryIdentifier;
    private String libraryName;
    private String projectIdentifier;
    private String projectName;
    private String lotIdentifier;
    private String lotLabel;
    private String trainIdentifier;
    private String trainLabel;
    private String docIdentifier;
    private String docPgcnId;
    private String docLabel;
    private String docStatus;
    private Integer totalPage;
    private final List<WorkflowState> workflow = new ArrayList<>();
    private WorkflowDocUnitInfoDTO infos;

    public String getLibraryIdentifier() {
        return libraryIdentifier;
    }

    public void setLibraryIdentifier(final String libraryIdentifier) {
        this.libraryIdentifier = libraryIdentifier;
    }

    public String getLibraryName() {
        return libraryName;
    }

    public void setLibraryName(final String libraryName) {
        this.libraryName = libraryName;
    }

    public String getProjectIdentifier() {
        return projectIdentifier;
    }

    public void setProjectIdentifier(final String projectIdentifier) {
        this.projectIdentifier = projectIdentifier;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(final String projectName) {
        this.projectName = projectName;
    }

    public String getLotIdentifier() {
        return lotIdentifier;
    }

    public void setLotIdentifier(final String lotIdentifier) {
        this.lotIdentifier = lotIdentifier;
    }

    public String getLotLabel() {
        return lotLabel;
    }

    public void setLotLabel(final String lotLabel) {
        this.lotLabel = lotLabel;
    }

    public String getTrainIdentifier() {
        return trainIdentifier;
    }

    public void setTrainIdentifier(final String trainIdentifier) {
        this.trainIdentifier = trainIdentifier;
    }

    public String getTrainLabel() {
        return trainLabel;
    }

    public void setTrainLabel(final String trainLabel) {
        this.trainLabel = trainLabel;
    }

    public String getDocIdentifier() {
        return docIdentifier;
    }

    public void setDocIdentifier(final String docIdentifier) {
        this.docIdentifier = docIdentifier;
    }

    public String getDocPgcnId() {
        return docPgcnId;
    }

    public void setDocPgcnId(final String docPgcnId) {
        this.docPgcnId = docPgcnId;
    }

    public String getDocLabel() {
        return docLabel;
    }

    public void setDocLabel(final String docLabel) {
        this.docLabel = docLabel;
    }

    public String getDocStatus() {
        return docStatus;
    }

    public void setDocStatus(final String docStatus) {
        this.docStatus = docStatus;
    }

    public Integer getTotalPage() {
        return totalPage;
    }

    public void setTotalPage(final Integer totalPage) {
        this.totalPage = totalPage;
    }

    public List<WorkflowState> getWorkflow() {
        return workflow;
    }

    public void setWorkflow(final List<WorkflowState> workflow) {
        this.workflow.clear();
        this.workflow.addAll(workflow);
    }

    public void addWorkflow(final WorkflowState workflow) {
        this.workflow.add(workflow);
    }

    public WorkflowDocUnitInfoDTO getInfos() {
        return infos;
    }

    public void setInfos(final WorkflowDocUnitInfoDTO infos) {
        this.infos = infos;
    }

    public static final class WorkflowState {

        private WorkflowStateKey key;
        private WorkflowStateStatus status;
        private LocalDateTime startDate;
        private LocalDateTime endDate;

        public WorkflowStateKey getKey() {
            return key;
        }

        public void setKey(final WorkflowStateKey key) {
            this.key = key;
        }

        public WorkflowStateStatus getStatus() {
            return status;
        }

        public void setStatus(final WorkflowStateStatus status) {
            this.status = status;
        }

        public LocalDateTime getStartDate() {
            return startDate;
        }

        public void setStartDate(final LocalDateTime startDate) {
            this.startDate = startDate;
        }

        public LocalDateTime getEndDate() {
            return endDate;
        }

        public void setEndDate(final LocalDateTime endDate) {
            this.endDate = endDate;
        }
    }
}
