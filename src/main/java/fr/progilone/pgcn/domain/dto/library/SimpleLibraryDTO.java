package fr.progilone.pgcn.domain.dto.library;

import fr.progilone.pgcn.domain.dto.user.RoleDTO;

/**
 * DTO représentant une library
 */
public class SimpleLibraryDTO {

    private String identifier;
    private String name;
    private String prefix;
    private RoleDTO defaultRole;

    public SimpleLibraryDTO() {
    }

    public SimpleLibraryDTO(final String identifier,
                            final String name,
                            final String prefix, RoleDTO defaultRole) {
        super();
        this.identifier = identifier;
        this.name = name;
        this.prefix = prefix;
        this.defaultRole = defaultRole;
    }

    public String getIdentifier() {
        return identifier;
    }

    public String getName() {
        return name;
    }

    public final void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public final void setName(String name) {
        this.name = name;
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public RoleDTO getDefaultRole() {
        return defaultRole;
    }

    public void setDefaultRole(RoleDTO defaultRole) {
        this.defaultRole = defaultRole;
    }

    /**
     * Builder pour la classe LibraryDTO
     */
    public static final class Builder {

        private String identifier;
        private String name;
        private String prefix;
        private RoleDTO defaultRole;

        public Builder reinit() {
            this.identifier = null;
            this.name = null;
            this.prefix = null;
            this.defaultRole = null;
            return this;
        }

        public Builder setIdentifier(final String identifier) {
            this.identifier = identifier;
            return this;
        }

        public Builder setName(final String name) {
            this.name = name;
            return this;
        }

        public Builder setDefaultRole(RoleDTO defaultRole) {
            this.defaultRole = defaultRole;
            return this;
        }

        public SimpleLibraryDTO build() {
            return new SimpleLibraryDTO(identifier, name, prefix, defaultRole);
        }

        public Builder setPrefix(String prefix) {
            this.prefix = prefix;
            return this;
        }
    }
}
