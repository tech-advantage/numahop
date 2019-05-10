package fr.progilone.pgcn.domain.dto.workflow;

import java.time.LocalDateTime;
import java.util.List;

import fr.progilone.pgcn.domain.dto.AbstractVersionedDTO;
import fr.progilone.pgcn.domain.dto.library.SimpleLibraryDTO;
import fr.progilone.pgcn.domain.dto.user.SimpleUserDTO;

public class WorkflowGroupDTO extends AbstractVersionedDTO {

    private String identifier;
    private String name;
    private SimpleLibraryDTO library;
    private String description;
    private List<SimpleUserDTO> users;
    
    /**
     * Ajout des infos de création
     */
    private String createdBy;
    private LocalDateTime createdDate;
    /**
     * Ajout des infos de modifications
     */
    private String lastModifiedBy;
    private LocalDateTime lastModifiedDate;

    public WorkflowGroupDTO() {

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

    public SimpleLibraryDTO getLibrary() {
        return library;
    }

    public void setLibrary(final SimpleLibraryDTO library) {
        this.library = library;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(final String description) {
        this.description = description;
    }

    public List<SimpleUserDTO> getUsers() {
        return users;
    }

    public void setUsers(final List<SimpleUserDTO> users) {
        this.users = users;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(final String createdBy) {
        this.createdBy = createdBy;
    }

    public LocalDateTime getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(final LocalDateTime createdDate) {
        this.createdDate = createdDate;
    }

    public String getLastModifiedBy() {
        return lastModifiedBy;
    }

    public void setLastModifiedBy(final String lastModifiedBy) {
        this.lastModifiedBy = lastModifiedBy;
    }

    public LocalDateTime getLastModifiedDate() {
        return lastModifiedDate;
    }

    public void setLastModifiedDate(final LocalDateTime lastModifiedDate) {
        this.lastModifiedDate = lastModifiedDate;
    }
}
