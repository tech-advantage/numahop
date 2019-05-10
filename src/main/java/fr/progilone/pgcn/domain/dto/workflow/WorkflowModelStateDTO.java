package fr.progilone.pgcn.domain.dto.workflow;

import java.time.Duration;

import fr.progilone.pgcn.domain.dto.AbstractVersionedDTO;
import fr.progilone.pgcn.domain.workflow.WorkflowModelStateType;
import fr.progilone.pgcn.domain.workflow.WorkflowStateKey;

public class WorkflowModelStateDTO extends AbstractVersionedDTO {

    private String identifier;
    private WorkflowStateKey key;
    private SimpleWorkflowGroupDTO group;
    private Duration duration;
    private WorkflowModelStateType type;
    

    public WorkflowModelStateDTO() {
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public WorkflowStateKey getKey() {
        return key;
    }

    public void setKey(WorkflowStateKey key) {
        this.key = key;
    }

    public SimpleWorkflowGroupDTO getGroup() {
        return group;
    }

    public void setGroup(SimpleWorkflowGroupDTO group) {
        this.group = group;
    }

    public Duration getDuration() {
        return duration;
    }

    public void setDuration(Duration duration) {
        this.duration = duration;
    }

    public WorkflowModelStateType getType() {
        return type;
    }

    public void setType(WorkflowModelStateType type) {
        this.type = type;
    }
}
