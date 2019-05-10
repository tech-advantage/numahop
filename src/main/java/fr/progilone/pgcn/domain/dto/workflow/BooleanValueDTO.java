package fr.progilone.pgcn.domain.dto.workflow;

import fr.progilone.pgcn.domain.dto.AbstractDTO;

public class BooleanValueDTO extends AbstractDTO {

    private boolean value;

    public BooleanValueDTO() {

    }

    public boolean isValue() {
        return value;
    }

    public void setValue(boolean value) {
        this.value = value;
    }
}
