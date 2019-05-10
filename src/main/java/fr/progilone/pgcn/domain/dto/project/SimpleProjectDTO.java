package fr.progilone.pgcn.domain.dto.project;

import fr.progilone.pgcn.domain.dto.library.SimpleLibraryDTO;

/**
 * DTO représentant un project allégé
 *
 * @author jbrunet
 */
public class SimpleProjectDTO {

    private String identifier;
    private String name;
    private String description;
    private String status;
    private SimpleLibraryDTO library;
    private String cancelingComment;
    private boolean filesArchived;

    public SimpleProjectDTO() {
    }

    public SimpleProjectDTO(final String identifier,
                            final String name,
                            final String description,
                            final String status,
                            final SimpleLibraryDTO library,
                            final String cancelingComment,
                            final boolean filesArchived) {
        super();
        this.identifier = identifier;
        this.name = name;
        this.description = description;
        this.status = status;
        this.library = library;
        this.cancelingComment = cancelingComment;
        this.filesArchived = filesArchived;
    }

    public String getName() {
        return name;
    }

    public final void setName(final String name) {
        this.name = name;
    }

    public final String getIdentifier() {
        return identifier;
    }

    public final void setIdentifier(final String identifier) {
        this.identifier = identifier;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(final String description) {
        this.description = description;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(final String status) {
        this.status = status;
    }

    public SimpleLibraryDTO getLibrary() {
        return library;
    }

    public void setLibrary(final SimpleLibraryDTO library) {
        this.library = library;
    }

    public String getCancelingComment() {
        return cancelingComment;
    }

    public void setCancelingComment(final String cancelingComment) {
        this.cancelingComment = cancelingComment;
    }

    public boolean isFilesArchived() {
        return filesArchived;
    }

    public void setFilesArchived(final boolean filesArchived) {
        this.filesArchived = filesArchived;
    }

    /**
     * Builder pour la classe SimpleProjectDTO
     *
     * @author jbrunet
     */
    public static final class Builder {

        private String identifier;
        private String name;
        private String description;
        private String status;
        private SimpleLibraryDTO library;
        private String cancelingComment;
        private boolean filesArchived;

        public Builder reinit() {
            this.identifier = null;
            this.name = null;
            this.description = null;
            this.status = null;
            this.library = null;
            this.cancelingComment = null;
            this.filesArchived = false;
            return this;
        }

        public Builder setLibrary(final SimpleLibraryDTO library) {
            this.library = library;
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

        public Builder setDescription(final String description) {
            this.description = description;
            return this;
        }
        
        public Builder setCancelingComment(final String comment) {
            this.cancelingComment = comment;
            return this;
        }
        public Builder setFilesArchived(final boolean archived) {
            this.filesArchived = archived;
            return this;
        }

        public SimpleProjectDTO build() {
            return new SimpleProjectDTO(identifier, name, description, status, library, cancelingComment, filesArchived);
        }

        public Builder setStatus(final String status) {
            this.status = status;
            return this;
        }
    }
}
