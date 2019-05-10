package fr.progilone.pgcn.domain.dto.audit;

import fr.progilone.pgcn.domain.delivery.Delivery;

public class AuditDeliveryRevisionDTO {

    private int rev;
    private long timestamp;
    private String username;

    private String identifier;
    private String label;
    private Delivery.DeliveryStatus status;
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

    public String getLabel() {
        return label;
    }

    public void setLabel(final String label) {
        this.label = label;
    }

    public Delivery.DeliveryStatus getStatus() {
        return status;
    }

    public void setStatus(final Delivery.DeliveryStatus status) {
        this.status = status;
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
