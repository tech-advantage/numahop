package fr.progilone.pgcn.domain.dto.user;

/**
 * DTO représentant un Role
 *
 * @author Sebastien
 * @see fr.progilone.pgcn.domain.user.Role
 */
public class RoleDTO {

    private String identifier;
    private String code;
    private String label;

    public RoleDTO() {
    }

    public RoleDTO(final String identifier, final String code, final String label) {
        super();
        this.identifier = identifier;
        this.code = code;
        this.label = label;
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

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    /**
     * Builder pour la classe RoleDTO
     *
     * @author Sebastien
     */
    public static final class Builder {

        private String identifier;
        private String code;
        private String label;

        public Builder reinit() {
            this.identifier = null;
            this.code = null;
            this.label = null;
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

        public RoleDTO build() {
            return new RoleDTO(identifier, code, label);
        }
    }
}
