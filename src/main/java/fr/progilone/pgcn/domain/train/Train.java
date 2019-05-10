package fr.progilone.pgcn.domain.train;

import static fr.progilone.pgcn.service.es.EsConstant.ANALYZER_CI_AI;
import static fr.progilone.pgcn.service.es.EsConstant.ANALYZER_CI_AS;
import static fr.progilone.pgcn.service.es.EsConstant.ANALYZER_KEYWORD;
import static fr.progilone.pgcn.service.es.EsConstant.ANALYZER_PHRASE;
import static fr.progilone.pgcn.service.es.EsConstant.SUBFIELD_CI_AI;
import static fr.progilone.pgcn.service.es.EsConstant.SUBFIELD_CI_AS;
import static fr.progilone.pgcn.service.es.EsConstant.SUBFIELD_PHRASE;
import static fr.progilone.pgcn.service.es.EsConstant.SUBFIELD_RAW;

import java.time.LocalDate;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.envers.AuditTable;
import org.hibernate.envers.Audited;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldIndex;
import org.springframework.data.elasticsearch.annotations.FieldType;
import org.springframework.data.elasticsearch.annotations.InnerField;
import org.springframework.data.elasticsearch.annotations.MultiField;
import org.springframework.data.elasticsearch.annotations.Parent;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.google.common.base.MoreObjects;

import fr.progilone.pgcn.domain.AbstractDomainObject;
import fr.progilone.pgcn.domain.document.PhysicalDocument;
import fr.progilone.pgcn.domain.project.Project;

/**
 * Classe métier permettant de gérer les trains.
 */
@Entity
@Table(name = Train.TABLE_NAME)
// Audit
@AuditTable(value = Train.AUDIT_TABLE_NAME)
// Jackson
@JsonSubTypes({@JsonSubTypes.Type(name = "train", value = Train.class)})
// Elasticsearch
@Document(indexName = "#{elasticsearchIndexName}", type = Train.ES_TYPE, createIndex = false)
public class Train extends AbstractDomainObject {

    /**
     * Nom des tables dans la base de données.
     */
    public static final String TABLE_NAME = "train_train";
    public static final String ES_TYPE = "train";
    public static final String AUDIT_TABLE_NAME = "aud_train_train";

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project")
    private Project project;

    /**
     * Le champ "projet" est répété pour la config elasticsearch @Parent, qui doit être de type String
     */
    @Column(name = "project", insertable = false, updatable = false)
    @Parent(type = Project.ES_TYPE)
    @Field(type = FieldType.String, index = FieldIndex.not_analyzed)
    private String projectId;

    /**
     * Libellé
     */
    @Column(name = "label")
    @MultiField(mainField = @Field(type = FieldType.String),
                otherFields = {@InnerField(type = FieldType.String, suffix = SUBFIELD_RAW, index = FieldIndex.not_analyzed),
                               @InnerField(type = FieldType.String,
                                           suffix = SUBFIELD_CI_AI,
                                           indexAnalyzer = ANALYZER_CI_AI,
                                           searchAnalyzer = ANALYZER_CI_AI),
                               @InnerField(type = FieldType.String,
                                           suffix = SUBFIELD_CI_AS,
                                           indexAnalyzer = ANALYZER_CI_AS,
                                           searchAnalyzer = ANALYZER_CI_AS),
                               @InnerField(type = FieldType.String,
                                           suffix = SUBFIELD_PHRASE,
                                           indexAnalyzer = ANALYZER_PHRASE,
                                           searchAnalyzer = ANALYZER_PHRASE)})
    private String label;

    /**
     * Description
     */
    @Column(name = "description")
    private String description;

    /**
     * Etat
     */
    @Column(name = "active")
    @Field(type = FieldType.Boolean)
    private boolean active;

    /**
     * statut
     */
    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    @Field(type = FieldType.String, analyzer = ANALYZER_KEYWORD)
    @Audited
    private TrainStatus status;

    /**
     * Date de livraison prévue
     */
    @Column(name = "provider_sending_date")
    @Field(type = FieldType.Date)
    private LocalDate providerSendingDate;

    /**
     * Date de retour
     */
    @Column(name = "return_date")
    @Field(type = FieldType.Date)
    private LocalDate returnDate;

    /**
     * Documents physiques
     */
    @OneToMany(mappedBy = "train", fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    private Set<PhysicalDocument> physicalDocuments;

    /**
     * type de statut
     */
    public enum TrainStatus {
        /**
         * Créé
         */
        CREATED,
        /**
         * En cours preparation
         */
        IN_PREPARATION,
        /**
         * En cours de numérisation
         */
        IN_DIGITIZATION,
        /**
         * Réception des documents physiques
         */
        RECEIVING_PHYSICAL_DOCUMENTS,
        /**
         * Annulé
         */
        CANCELED,
        /**
         * Clôturé
         */
        CLOSED;
    }

    public Project getProject() {
        return project;
    }

    public void setProject(final Project project) {
        this.project = project;
    }

    public String getProjectId() {
        return projectId;
    }

    public void setProjectId(final String projectId) {
        this.projectId = projectId;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(final String label) {
        this.label = label;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(final String description) {
        this.description = description;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(final boolean active) {
        this.active = active;
    }

    public TrainStatus getStatus() {
        return status;
    }

    public void setStatus(final TrainStatus status) {
        this.status = status;
    }

    public LocalDate getProviderSendingDate() {
        return providerSendingDate;
    }

    public void setProviderSendingDate(final LocalDate providerSendingDate) {
        this.providerSendingDate = providerSendingDate;
    }

    public LocalDate getReturnDate() {
        return returnDate;
    }

    public void setReturnDate(final LocalDate returnDate) {
        this.returnDate = returnDate;
    }

    public Set<PhysicalDocument> getPhysicalDocuments() {
        return physicalDocuments;
    }

    public void setPhysicalDocuments(final Set<PhysicalDocument> physicalDocuments) {
        this.physicalDocuments = physicalDocuments;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                          .omitNullValues()
                          .add("project", project)
                          .add("label", label)
                          .add("description", description)
                          .add("active", active)
                          .add("status", status)
                          .add("providerSendingDate", providerSendingDate)
                          .add("returnDate", returnDate)
                          .toString();
    }
}
