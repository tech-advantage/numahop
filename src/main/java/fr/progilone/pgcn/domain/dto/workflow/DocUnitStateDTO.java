package fr.progilone.pgcn.domain.dto.workflow;

import java.time.LocalDateTime;

import fr.progilone.pgcn.domain.dto.AbstractVersionedDTO;
import fr.progilone.pgcn.domain.workflow.WorkflowStateStatus;

public class DocUnitStateDTO extends AbstractVersionedDTO {

    private String identifier;
    private String key;
    private LocalDateTime startDate;
    private LocalDateTime dueDate;
    private LocalDateTime endDate;
    private WorkflowStateStatus status;
    private String user;
    private boolean canStateBeProcessed;

    public DocUnitStateDTO() {

    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public LocalDateTime getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDateTime startDate) {
        this.startDate = startDate;
    }

    public LocalDateTime getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDateTime dueDate) {
        this.dueDate = dueDate;
    }

    public LocalDateTime getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDateTime endDate) {
        this.endDate = endDate;
    }

    public WorkflowStateStatus getStatus() {
        return status;
    }

    public void setStatus(WorkflowStateStatus status) {
        this.status = status;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public boolean isCanStateBeProcessed() {
        return canStateBeProcessed;
    }

    public void setCanStateBeProcessed(boolean canStateBeProcessed) {
        this.canStateBeProcessed = canStateBeProcessed;
    }
}
