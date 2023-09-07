package fr.progilone.pgcn.domain.train;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.google.common.base.MoreObjects;
import fr.progilone.pgcn.domain.AbstractDomainObject;
import fr.progilone.pgcn.domain.document.PhysicalDocument;
import fr.progilone.pgcn.domain.project.Project;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.time.LocalDate;
import java.util.Set;
import org.hibernate.envers.AuditTable;
import org.hibernate.envers.Audited;

/**
 * Classe métier permettant de gérer les trains.
 */
@Entity
@Table(name = Train.TABLE_NAME)
// Audit
@AuditTable(value = Train.AUDIT_TABLE_NAME)
// Jackson
@JsonSubTypes({@JsonSubTypes.Type(name = "train", value = Train.class)})
public class Train extends AbstractDomainObject {

    /**
     * Nom des tables dans la base de données.
     */
    public static final String TABLE_NAME = "train_train";
    public static final String AUDIT_TABLE_NAME = "aud_train_train";

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project")
    private Project project;

    /**
     * Libellé
     */
    @Column(name = "label")
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
    private boolean active;

    /**
     * statut
     */
    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    @Audited
    private TrainStatus status;

    /**
     * Date de livraison prévue
     */
    @Column(name = "provider_sending_date")
    private LocalDate providerSendingDate;

    /**
     * Date de retour
     */
    @Column(name = "return_date")
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
