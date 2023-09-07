package fr.progilone.pgcn.domain.dto.library;

import fr.progilone.pgcn.domain.dto.AbstractDTO;

public class AbstractLibraryParameterValueDTO extends AbstractDTO {

    private String identifier;

    public AbstractLibraryParameterValueDTO() {
    }

    public final String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }
}
