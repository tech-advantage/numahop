package fr.progilone.pgcn.domain.dto.administration.viewsFormat;

import fr.progilone.pgcn.domain.dto.AbstractDTO;

public class SimpleViewsFormatConfigurationDTO extends AbstractDTO {

    private String identifier;
    private String label;

    public SimpleViewsFormatConfigurationDTO(final String identifier, final String label) {
        this.identifier = identifier;
        this.label = label;
    }

    public SimpleViewsFormatConfigurationDTO() {
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(final String identifier) {
        this.identifier = identifier;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(final String label) {
        this.label = label;
    }

}
