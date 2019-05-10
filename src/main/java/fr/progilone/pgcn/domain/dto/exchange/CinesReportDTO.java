package fr.progilone.pgcn.domain.dto.exchange;

import java.time.LocalDateTime;

import fr.progilone.pgcn.domain.dto.AbstractDTO;

/**
 * DTO pour un rapport d'archivage au CINES
 *
 * @author jbrunet
 * Créé le 3 mars 2017
 */
public class CinesReportDTO extends AbstractDTO {

    private String identifier;
    private String status;
    private LocalDateTime dateSent;
    private LocalDateTime dateAr;
    private LocalDateTime dateRejection;
    private LocalDateTime dateArchived;
    private LocalDateTime lastModifiedDate;
    private String rejectionMotive;
    private String certificate;
    private String message;

    public CinesReportDTO() {}

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getDateSent() {
        return dateSent;
    }

    public void setDateSent(LocalDateTime dateSent) {
        this.dateSent = dateSent;
    }

    public LocalDateTime getDateAr() {
        return dateAr;
    }

    public void setDateAr(LocalDateTime dateAr) {
        this.dateAr = dateAr;
    }

    public LocalDateTime getDateRejection() {
        return dateRejection;
    }

    public void setDateRejection(LocalDateTime dateRejection) {
        this.dateRejection = dateRejection;
    }

    public LocalDateTime getDateArchived() {
        return dateArchived;
    }

    public void setDateArchived(LocalDateTime dateArchived) {
        this.dateArchived = dateArchived;
    }

    public LocalDateTime getLastModifiedDate() {
        return lastModifiedDate;
    }

    public void setLastModifiedDate(final LocalDateTime lastModifiedDate) {
        this.lastModifiedDate = lastModifiedDate;
    }

    public String getRejectionMotive() {
        return rejectionMotive;
    }

    public void setRejectionMotive(String rejectionMtive) {
        this.rejectionMotive = rejectionMtive;
    }

    public String getCertificate() {
        return certificate;
    }

    public void setCertificate(String certificate) {
        this.certificate = certificate;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

}
