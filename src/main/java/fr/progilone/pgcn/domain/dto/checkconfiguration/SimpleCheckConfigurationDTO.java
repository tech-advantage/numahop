package fr.progilone.pgcn.domain.dto.checkconfiguration;

import fr.progilone.pgcn.domain.dto.AbstractDTO;

public class SimpleCheckConfigurationDTO extends AbstractDTO {

    private String identifier;
    private String label;

    public SimpleCheckConfigurationDTO(String identifier, String label) {
        this.identifier = identifier;
        this.label = label;
    }

    public SimpleCheckConfigurationDTO() {
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public static final class Builder {

        private String identifier;
        private String label;

        public Builder init() {
            this.identifier = null;
            this.label = null;
            return this;
        }

        public Builder setIdentifier(String identifier) {
            this.identifier = identifier;
            return this;
        }

        public Builder setLabel(String label) {
            this.label = label;
            return this;
        }

        public SimpleCheckConfigurationDTO build() {
            return new SimpleCheckConfigurationDTO(identifier, label);
        }
    }
}
