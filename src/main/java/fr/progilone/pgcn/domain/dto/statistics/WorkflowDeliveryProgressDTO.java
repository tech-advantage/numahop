package fr.progilone.pgcn.domain.dto.statistics;

import fr.progilone.pgcn.domain.workflow.WorkflowStateKey;

import java.util.ArrayList;
import java.util.List;

public class WorkflowDeliveryProgressDTO {

    private String libraryIdentifier;
    private String libraryName;
    private String projectIdentifier;
    private String projectName;
    private String lotIdentifier;
    private String lotLabel;
    private String deliveryIdentifier;
    private String deliveryLabel;
    private String docUnitNumber;
    private final List<WorkflowState> workflow = new ArrayList<>();

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

    public String getDeliveryIdentifier() {
        return deliveryIdentifier;
    }

    public void setDeliveryIdentifier(final String deliveryIdentifier) {
        this.deliveryIdentifier = deliveryIdentifier;
    }

    public String getDeliveryLabel() {
        return deliveryLabel;
    }

    public void setDeliveryLabel(final String deliveryLabel) {
        this.deliveryLabel = deliveryLabel;
    }

    public String getDocUnitNumber() {
        return docUnitNumber;
    }

    public void setDocUnitNumber(String docUnitNumber) {
        this.docUnitNumber = docUnitNumber;
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
        private WorkflowStateKey key;
        private long count = 0L;

        public WorkflowStateKey getKey() {
            return key;
        }

        public void setKey(final WorkflowStateKey key) {
            this.key = key;
        }

        public long getCount() {
            return count;
        }

        public void setCount(final long count) {
            this.count = count;
        }

        public void incrementCount() {
            this.count++;
        }
    }
}
