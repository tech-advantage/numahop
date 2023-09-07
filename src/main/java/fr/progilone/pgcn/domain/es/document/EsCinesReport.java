package fr.progilone.pgcn.domain.es.document;

import fr.progilone.pgcn.domain.exchange.cines.CinesReport;
import fr.progilone.pgcn.domain.exchange.cines.CinesReport.Status;
import java.time.LocalDate;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

public class EsCinesReport {

    @Field(type = FieldType.Keyword)
    private String identifier;

    /**
     * Statut de l'archivage
     */
    @Field(type = FieldType.Keyword)
    private Status status;

    /**
     * Date d'envoi des documents à archiver
     */
    @Field(type = FieldType.Date)
    private LocalDate dateSent;

    public static EsCinesReport from(final CinesReport report) {
        final EsCinesReport esReport = new EsCinesReport();
        esReport.setIdentifier(report.getIdentifier());
        if (report.getDateSent() != null) {
            esReport.setDateSent(report.getDateSent().toLocalDate());
        }
        esReport.setStatus(report.getStatus());
        return esReport;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(final String identifier) {
        this.identifier = identifier;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(final Status status) {
        this.status = status;
    }

    public LocalDate getDateSent() {
        return dateSent;
    }

    public void setDateSent(final LocalDate dateSent) {
        this.dateSent = dateSent;
    }

}
