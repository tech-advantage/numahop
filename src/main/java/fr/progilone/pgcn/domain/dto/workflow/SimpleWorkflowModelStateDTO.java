package fr.progilone.pgcn.domain.dto.workflow;

import fr.progilone.pgcn.domain.dto.AbstractDTO;

public class SimpleWorkflowModelStateDTO extends AbstractDTO {

    private String identifier;
    private String key;

    public SimpleWorkflowModelStateDTO() {
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
}
