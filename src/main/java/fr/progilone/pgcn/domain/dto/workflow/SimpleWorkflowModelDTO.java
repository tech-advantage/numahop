package fr.progilone.pgcn.domain.dto.workflow;

import fr.progilone.pgcn.domain.dto.AbstractDTO;

public class SimpleWorkflowModelDTO extends AbstractDTO {

    private String identifier;
    private String name;

    public SimpleWorkflowModelDTO() {
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
