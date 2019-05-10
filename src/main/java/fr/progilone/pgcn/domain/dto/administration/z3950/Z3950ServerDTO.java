package fr.progilone.pgcn.domain.dto.administration.z3950;

/**
 * Created by Sébastien on 21/12/2016.
 */
public class Z3950ServerDTO {

    private String identifier;
    private String name;
    private boolean active;
    private long version;

    public Z3950ServerDTO() {
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

    public boolean isActive() {
        return active;
    }

    public void setActive(final boolean active) {
        this.active = active;
    }

    public long getVersion() {
        return version;
    }

    public void setVersion(final long version) {
        this.version = version;
    }
}
