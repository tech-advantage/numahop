package fr.progilone.pgcn.domain.dto.project;

/**
 * DTO représentant un Projet
 */
public class ProjectSearchDTO {

    private String identifier;
    private String name;

    public ProjectSearchDTO() {}

    public ProjectSearchDTO(final String identifier,
                            final String name) {
    	super();
        this.identifier = identifier;
        this.name = name;
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

	/**
     * Builder pour la classe ProjectDTO
     */
    public static final class Builder {

        private String identifier;
        private String name;

        public Builder reinit() {
            this.identifier = null;
            this.name = null;
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

        public ProjectSearchDTO build() {
            return new ProjectSearchDTO(identifier, name);
        }
    }
}
