package fr.progilone.pgcn.domain.exchange.internetarchive;

import static fr.progilone.pgcn.service.es.EsConstant.ANALYZER_KEYWORD;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldIndex;
import org.springframework.data.elasticsearch.annotations.FieldType;
import org.springframework.data.elasticsearch.annotations.Parent;

import fr.progilone.pgcn.domain.AbstractDomainObject;
import fr.progilone.pgcn.domain.document.DocUnit;

/**
 * Rapports de diffusion Internet Archive
 *
 * @author jbrunet
 * Créé le 28 avr. 2017
 */
// Hibernate
@Entity
@Table(name = InternetArchiveReport.TABLE_NAME)
// Elasticsearch
@Document(indexName = "#{elasticsearchIndexName}", type = InternetArchiveReport.ES_TYPE, createIndex = false)
public class InternetArchiveReport extends AbstractDomainObject {

    public static final String TABLE_NAME = "exc_internet_archive_report";
    public static final String ES_TYPE = "doc_exportia";

    /**
     * Unité documentaire à diffuser
     */
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "doc_unit", nullable = false)
    private DocUnit docUnit;

    /**
     * Le champ "Unité documentaire" est répété pour la config elasticsearch @Parent, qui doit être de type String
     */
    @Column(name = "doc_unit", insertable = false, updatable = false)
    @Parent(type = DocUnit.ES_TYPE)
    @Field(type = FieldType.String, index = FieldIndex.not_analyzed)
    private String docUnitId;

    /**
     * Identifiant au sein d'Internet Archive
     */
    @Column(name = "internet_archive_identifier", nullable = false)
    private String internetArchiveIdentifier;

    /**
     * Nombre de fichiers envoyés
     */
    private Integer number;

    /**
     * Nombre total
     */
    private Integer total;

    /**
     * Statut de l'archivage
     */
    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    @Field(type = FieldType.String, analyzer = ANALYZER_KEYWORD)
    private Status status;

    /**
     * Date d'envoi des documents à archiver
     */
    @Column(name = "date_sent")
    @Field(type = FieldType.Date)
    private LocalDateTime dateSent;

    /**
     * Message détaillant le résultat de l'export
     */
    @Column(name = "message", columnDefinition = "text")
    private String message;
    
    /**
     * Date de réception du certificat d'archivage
     */
    @Column(name = "date_archived")
    private LocalDateTime dateArchived;
    
    /**
     *  Simple passe-plat pour recuperer l'url ark
     *  non persisté!
     */
    @Transient
    private String arkUrl;
    
    

    public DocUnit getDocUnit() {
        return docUnit;
    }

    public void setDocUnit(final DocUnit docUnit) {
        this.docUnit = docUnit;
    }

    public String getDocUnitId() {
        return docUnitId;
    }

    public void setDocUnitId(final String docUnitId) {
        this.docUnitId = docUnitId;
    }

    public String getInternetArchiveIdentifier() {
        return internetArchiveIdentifier;
    }

    public void setInternetArchiveIdentifier(final String internetArchiveIdentifier) {
        this.internetArchiveIdentifier = internetArchiveIdentifier;
    }

    public Integer getNumber() {
        return number;
    }

    public void setNumber(final Integer number) {
        this.number = number;
    }

    public Integer getTotal() {
        return total;
    }

    public void setTotal(final Integer total) {
        this.total = total;
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

    public LocalDateTime getDateArchived() {
        return dateArchived;
    }

    public void setDateArchived(final LocalDateTime dateArchived) {
        this.dateArchived = dateArchived;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(final String message) {
        this.message = message;
    }

    public String getArkUrl() {
        return arkUrl;
    }

    public void setArkUrl(final String arkUrl) {
        this.arkUrl = arkUrl;
    }

    /**
     * Statuts de l'export Internet Archive
     */
    public enum Status {
        // Le répertoire d'export est en cours de création
        EXPORTING,
        // Les données sont en cours de transfert
        SENDING,
        // Les données sont transférées, l'accusé réception n'a pas encore été reçu
        SENT,
        // Le certificat d'archivage a été reçu
        ARCHIVED,
        // L'export a échoué sur une erreur technique
        FAILED
    }
}
