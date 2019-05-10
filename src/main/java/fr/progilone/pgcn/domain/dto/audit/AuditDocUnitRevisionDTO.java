package fr.progilone.pgcn.domain.dto.audit;

public class AuditDocUnitRevisionDTO {

    private int rev;
    private long timestamp;
    private String username;

    private String identifier;
    private String lotIdentifier;
    private String lotLabel;

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

    public String getLotIdentifier() {
        return lotIdentifier;
    }

    public void setLotIdentifier(final String lotIdentifier) {
        this.lotIdentifier = lotIdentifier;
    }

    public String getLotLabel() {
        return lotLabel;
    }

    public void setLotLabel(final String lotLabel) {
        this.lotLabel = lotLabel;
    }
}
