package fr.progilone.pgcn.domain.exchange.cines;

import fr.progilone.pgcn.domain.AbstractDomainObject;
import fr.progilone.pgcn.domain.document.DocUnit;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;

// Hibernate
@Entity
@Table(name = CinesReport.TABLE_NAME)
public class CinesReport extends AbstractDomainObject {

    public static final String TABLE_NAME = "exc_cines_report";

    /**
     * Unité documentaire à archiver
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "doc_unit", nullable = false)
    private DocUnit docUnit;

    /**
     * Statut de l'archivage
     */
    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    private Status status;

    /**
     * Date d'envoi des documents à archiver
     */
    @Column(name = "date_sent")
    private LocalDateTime dateSent;

    /**
     * Date de réception de l'accusé réception
     */
    @Column(name = "date_ar")
    private LocalDateTime dateAr;

    /**
     * Date de rejet
     */
    @Column(name = "date_rejection")
    private LocalDateTime dateRejection;

    /**
     * Date de réception du certificat d'archivage
     */
    @Column(name = "date_archived")
    private LocalDateTime dateArchived;

    /**
     * Motif de rejet de la demande d'archivage
     */
    @Column(name = "rejection", columnDefinition = "text")
    private String rejectionMotive;

    /**
     * Certificat d'archivage
     */
    @Column(name = "certificate", columnDefinition = "longtext")
    private String certificate;

    /**
     * Message détaillant le résultat de l'export
     */
    @Column(name = "message", columnDefinition = "text")
    private String message;

    public DocUnit getDocUnit() {
        return docUnit;
    }

    public void setDocUnit(final DocUnit docUnit) {
        this.docUnit = docUnit;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(final Status status) {
        this.status = status;
    }

    public LocalDateTime getDateSent() {
        return dateSent;
    }

    public void setDateSent(final LocalDateTime dateSent) {
        this.dateSent = dateSent;
    }

    public LocalDateTime getDateAr() {
        return dateAr;
    }

    public void setDateAr(final LocalDateTime dateAr) {
        this.dateAr = dateAr;
    }

    public LocalDateTime getDateRejection() {
        return dateRejection;
    }

    public void setDateRejection(final LocalDateTime dateRejection) {
        this.dateRejection = dateRejection;
    }

    public LocalDateTime getDateArchived() {
        return dateArchived;
    }

    public void setDateArchived(final LocalDateTime dateArchived) {
        this.dateArchived = dateArchived;
    }

    public String getRejectionMotive() {
        return rejectionMotive;
    }

    public void setRejectionMotive(final String rejectionMotive) {
        this.rejectionMotive = rejectionMotive;
    }

    public String getCertificate() {
        return certificate;
    }

    public void setCertificate(final String certificate) {
        this.certificate = certificate;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(final String message) {
        this.message = message;
    }

    /**
     * Statuts de l'export CINES
     */
    public enum Status {
        // Le répertoire d'export est en cours de création
        EXPORTING,
        // Les données sont en cours de transfert
        SENDING,
        // Les données sont transférées, l'accusé réception n'a pas encore été reçu
        SENT,
        // L'accusé réception a été reçu
        AR_RECEIVED,
        // Les documents ont été rejetés
        REJECTED,
        // Le certificat d'archivage a été reçu
        ARCHIVED,
        // L'export a échoué sur une erreur technique
        FAILED
    }
}
