package fr.progilone.pgcn.domain.dto.library;

import fr.progilone.pgcn.domain.dto.AbstractVersionedDTO;
import fr.progilone.pgcn.domain.dto.user.RoleDTO;

/**
 * DTO représentant une library
 */
public class SimpleLibraryDTO extends AbstractVersionedDTO {

    private String identifier;
    private String name;
    private String prefix;
    private RoleDTO defaultRole;
    // responsable bibliotheque
    private String libRespName;
    private String libRespPhone;
    private String libRespEmail;

    public SimpleLibraryDTO() {
    }

    public SimpleLibraryDTO(final String identifier,
                            final String name,
                            final String prefix,
                            final RoleDTO defaultRole,
                            final String libRespName,
                            final String libRespPhone,
                            final String libRespEmail) {
        super();
        this.identifier = identifier;
        this.name = name;
        this.prefix = prefix;
        this.defaultRole = defaultRole;
        this.libRespName = libRespName;
        this.libRespPhone = libRespPhone;
        this.libRespEmail = libRespEmail;
    }

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

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(final String prefix) {
        this.prefix = prefix;
    }

    public RoleDTO getDefaultRole() {
        return defaultRole;
    }

    public void setDefaultRole(final RoleDTO defaultRole) {
        this.defaultRole = defaultRole;
    }

    public String getLibRespName() {
        return libRespName;
    }

    public void setLibRespName(final String libRespName) {
        this.libRespName = libRespName;
    }

    public String getLibRespPhone() {
        return libRespPhone;
    }

    public void setLibRespPhone(final String libRespPhone) {
        this.libRespPhone = libRespPhone;
    }

    public String getLibRespEmail() {
        return libRespEmail;
    }

    public void setLibRespEmail(final String libRespEmail) {
        this.libRespEmail = libRespEmail;
    }

    /**
     * Builder pour la classe LibraryDTO
     */
    public static final class Builder {

        private String identifier;
        private String name;
        private String prefix;
        private RoleDTO defaultRole;
        private String libRespName;
        private String libRespPhone;
        private String libRespEmail;

        public Builder reinit() {
            this.identifier = null;
            this.name = null;
            this.prefix = null;
            this.defaultRole = null;
            this.libRespName = null;
            this.libRespPhone = null;
            this.libRespEmail = null;
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

        public Builder setPrefix(final String prefix) {
            this.prefix = prefix;
            return this;
        }

        public Builder setDefaultRole(final RoleDTO defaultRole) {
            this.defaultRole = defaultRole;
            return this;
        }

        public Builder setLibRespName(final String libRespName) {
            this.libRespName = libRespName;
            return this;
        }

        public Builder setLibRespPhone(final String libRespPhone) {
            this.libRespPhone = libRespPhone;
            return this;
        }

        public Builder setLibRespEmail(final String libRespEmail) {
            this.libRespEmail = libRespEmail;
            return this;
        }

        public SimpleLibraryDTO build() {
            return new SimpleLibraryDTO(identifier, name, prefix, defaultRole, libRespName, libRespPhone, libRespEmail);
        }

    }
}
