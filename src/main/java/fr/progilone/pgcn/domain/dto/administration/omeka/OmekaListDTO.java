package fr.progilone.pgcn.domain.dto.administration.omeka;

import fr.progilone.pgcn.domain.dto.AbstractVersionedDTO;

public class OmekaListDTO extends AbstractVersionedDTO {

    private String identifier;
    private String name;
    private String type;

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(final String identifier) {
        this.identifier = identifier;
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(final String type) {
        this.type = type;
    }
}
