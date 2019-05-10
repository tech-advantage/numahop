package fr.progilone.pgcn.domain.dto.ftpconfiguration;

import fr.progilone.pgcn.domain.dto.AbstractDTO;

public class SimpleFTPConfigurationDTO extends AbstractDTO {

    private String identifier;
    private String label;

    public SimpleFTPConfigurationDTO(String identifier, String label) {
        this.identifier = identifier;
        this.label = label;
    }

    public SimpleFTPConfigurationDTO() {
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

        public SimpleFTPConfigurationDTO build() {
            return new SimpleFTPConfigurationDTO(identifier, label);
        }
    }
}
