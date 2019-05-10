package fr.progilone.pgcn.domain.dto.statistics;

import fr.progilone.pgcn.domain.workflow.WorkflowStateKey;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class StatisticsDocUnitDTO {

    private String identifier;
    private String pgcnId;
    private String label;
    private String projectIdentifier;
    private String projectName;
    private String lotIdentifier;
    private String lotLabel;
    private final List<WorkflowState> workflow = new ArrayList<>();

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(final String identifier) {
        this.identifier = identifier;
    }

    public String getPgcnId() {
        return pgcnId;
    }

    public void setPgcnId(final String pgcnId) {
        this.pgcnId = pgcnId;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(final String label) {
        this.label = label;
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

    public static final class WorkflowState {
        private WorkflowStateKey state; // étape de workflow en cours
        private LocalDateTime date; // start date de l'ud sur l'étape de workflow

        public WorkflowStateKey getState() {
            return state;
        }

        public void setState(final WorkflowStateKey state) {
            this.state = state;
        }

        public LocalDateTime getDate() {
            return date;
        }

        public void setDate(final LocalDateTime date) {
            this.date = date;
        }
    }
}
