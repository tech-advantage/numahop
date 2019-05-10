package fr.progilone.pgcn.domain.dto.statistics;

import java.time.LocalDateTime;

/**
 * DTO remontant les UD avec leur état après traitement
 */
public class StatisticsProcessedDocUnitDTO {

    private String identifier;
    private String pgcnId;
    private String status;
    private String message;
    private LocalDateTime date;

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(final String identifier) {
        this.identifier = identifier;
    }

    public String getPgcnId() {
        return pgcnId;
    }

    public void setPgcnId(final String pgcnId) {
        this.pgcnId = pgcnId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(final String status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(final String message) {
        this.message = message;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(final LocalDateTime date) {
        this.date = date;
    }
}
