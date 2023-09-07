package fr.progilone.pgcn.domain.dto.ocrlangconfiguration;

import fr.progilone.pgcn.domain.dto.AbstractDTO;

public class SimpleOcrLangConfigDTO extends AbstractDTO {

    private String identifier;
    private String label;

    public SimpleOcrLangConfigDTO(final String identifier, final String label) {
        this.identifier = identifier;
        this.label = label;
    }

    public SimpleOcrLangConfigDTO() {
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
