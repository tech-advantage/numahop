package fr.progilone.pgcn.domain.dto.exchange;

import fr.progilone.pgcn.domain.dto.AbstractDTO;
import java.time.LocalDateTime;

/**
 * DTO pour un rapport d'archivage sur Internet Archive
 *
 * @author jbrunet
 *         Créé le 3 mars 2017
 */
public class InternetArchiveReportDTO extends AbstractDTO {

    private String identifier;
    private String status;
    private LocalDateTime dateSent;
    private LocalDateTime dateArchived;
    private String internetArchiveIdentifier;
    private String message;
    private String number;
    private String total;

    public InternetArchiveReportDTO() {
    }

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

    public LocalDateTime getDateArchived() {
        return dateArchived;
    }

    public void setDateArchived(LocalDateTime dateArchived) {
        this.dateArchived = dateArchived;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getInternetArchiveIdentifier() {
        return internetArchiveIdentifier;
    }

    public void setInternetArchiveIdentifier(String internetArchiveIdentifier) {
        this.internetArchiveIdentifier = internetArchiveIdentifier;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }

}
