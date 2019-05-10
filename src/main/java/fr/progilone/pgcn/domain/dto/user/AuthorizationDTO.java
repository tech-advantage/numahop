package fr.progilone.pgcn.domain.dto.user;

import java.util.List;

import fr.progilone.pgcn.domain.administration.Module;

/**
 * DTO représentant une habilitation
 *
 * @author Sebastien
 * @see Authorization
 */
public class AuthorizationDTO {

    private final String identifier;
    private final String code;
    private final String label;
    private final String description;
    private final String module;
    private final List<String> requirements;
    private final List<String> dependencies;

    public AuthorizationDTO(final String identifier,
                            final String code,
                            final String label,
                            final String description,
                            final String module,
                            List<String> requirements,
                            List<String> dependencies) {
        this.identifier = identifier;
        this.code = code;
        this.label = label;
        this.description = description;
        this.module = module;
        this.requirements = requirements;
        this.dependencies = dependencies;
    }

    public String getIdentifier() {
        return identifier;
    }

    public String getCode() {
        return code;
    }

    public String getLabel() {
        return label;
    }

    public String getDescription() {
        return description;
    }

    public String getModule() {
        return module;
    }

    public List<String> getRequirements() {
        return requirements;
    }

    public List<String> getDependencies() {
        return dependencies;
    }

    public static final class Builder {

        private String identifier;
        private String code;
        private String label;
        private String description;
        private String module;
        private List<String> requirements;
        private List<String> dependencies;

        public Builder reinit() {
            this.identifier = null;
            this.code = null;
            this.label = null;
            this.description = null;
            this.module = null;
            this.requirements = null;
            this.dependencies = null;
            return this;
        }

        public Builder setIdentifier(final String identifier) {
            this.identifier = identifier;
            return this;
        }

        public Builder setCode(final String code) {
            this.code = code;
            return this;
        }

        public Builder setLabel(final String label) {
            this.label = label;
            return this;
        }

        public Builder setDescription(final String description) {
            this.description = description;
            return this;
        }

        public Builder setModule(final Module module) {
            if (module != null) {
                this.module = module.name();
            }
            return this;
        }

        public Builder setRequirements(final List<String> requirements) {
            this.requirements = requirements;
            return this;
        }

        public Builder setDependencies(final List<String> dependencies) {
            this.dependencies = dependencies;
            return this;
        }

        public AuthorizationDTO build() {
            return new AuthorizationDTO(identifier, code, label, description, module, requirements, dependencies);
        }
    }
}
