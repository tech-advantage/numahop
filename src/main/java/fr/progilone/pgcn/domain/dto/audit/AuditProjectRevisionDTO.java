package fr.progilone.pgcn.domain.dto.audit;

import fr.progilone.pgcn.domain.project.Project;

public class AuditProjectRevisionDTO {

    private int rev;
    private long timestamp;
    private String username;

    private String identifier;
    private String name;
    private Project.ProjectStatus status;

    public int getRev() {
        return rev;
    }

    public void setRev(final int rev) {
        this.rev = rev;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(final long timestamp) {
        this.timestamp = timestamp;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(final String username) {
        this.username = username;
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

    public Project.ProjectStatus getStatus() {
        return status;
    }

    public void setStatus(final Project.ProjectStatus status) {
        this.status = status;
    }
}
