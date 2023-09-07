/**
 *
 */
package fr.progilone.pgcn.domain.document;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import fr.progilone.pgcn.domain.AbstractDomainObject;
import fr.progilone.pgcn.domain.check.AutomaticCheckResult;
import fr.progilone.pgcn.domain.delivery.DeliveredDocument;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author jbrunet
 */
@Entity
@Table(name = DigitalDocument.TABLE_NAME)
@JsonSubTypes({@JsonSubTypes.Type(name = "doc_digital_document", value = DigitalDocument.class)})
public class DigitalDocument extends AbstractDomainObject {

    public static final String TABLE_NAME = "doc_digital_document";

    /**
     * Lien vers l'unité documentaire
     */
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "doc_unit")
    private DocUnit docUnit;

    /**
     * Liste des documents physiques liés
     */
    @ManyToMany(mappedBy = "digitalDocuments", fetch = FetchType.LAZY)
    private Set<PhysicalDocument> physicalDocuments = new HashSet<>();

    /**
     * Liste des livraisons effectuées pour ce document
     */
    @OneToMany(mappedBy = "digitalDocument", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private final Set<DeliveredDocument> deliveries = new HashSet<>();

    /**
     * Nombre de livraisons du document
     */
    @Column(name = "total_delivery")
    private Integer totalDelivery;

    /**
     * statut du document numérique
     */
    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private DigitalDocumentStatus status;

    @Column(name = "digital_id")
    private String digitalId;

    @Column(name = "minor_error_rate")
    private Double minorErrorRate;

    @Column(name = "major_error_rate")
    private Double majorErrorRate;

    @Column(name = "check_notes")
    private String checkNotes;

    @Column(name = "delivery_date")
    private LocalDate deliveryDate;

    @Column(name = "total_length")
    private Long totalLength;

    @Transient
    private int pageNumber;

    /**
     * Liste des contrôles
     */
    @OneToMany(mappedBy = "digitalDocument", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Set<GlobalCheck> checks = new HashSet<>();

    /**
     * Liste des résultats de contrôles automatiques associés
     */
    @OneToMany(mappedBy = "digitalDocument", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private final Set<AutomaticCheckResult> automaticCheckResults = new HashSet<>();

    /**
     * FIXME
     * Contrôles à ajouter
     * propres aux doc numériques
     */

    /**
     * Liste des pages
     */
    @OneToMany(mappedBy = "digitalDocument", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private final Set<DocPage> pages = new HashSet<>();

    public Set<DocPage> getPages() {
        return pages;
    }

    public void setPages(final Set<DocPage> pages) {
        this.pages.clear();
        if (pages != null) {
            pages.forEach(this::addPage);
        }
    }

    public void addPage(final DocPage page) {
        if (page != null) {
            this.pages.add(page);
            page.setDigitalDocument(this);
        }
    }

    public DocUnit getDocUnit() {
        return docUnit;
    }

    public void setDocUnit(final DocUnit docUnit) {
        this.docUnit = docUnit;
    }

    public int getNbPages() {
        return (int) pages.stream().filter(p -> p.getNumber() != null).count();
    }

    public Integer getTotalDelivery() {
        return totalDelivery;
    }

    public void setTotalDelivery(final Integer totalDelivery) {
        this.totalDelivery = totalDelivery;
    }

    public DigitalDocumentStatus getStatus() {
        return status;
    }

    public void setStatus(final DigitalDocumentStatus status) {
        this.status = status;
    }

    public Set<PhysicalDocument> getPhysicalDocuments() {
        return physicalDocuments;
    }

    public void setPhysicalDocuments(final Set<PhysicalDocument> physicalDocuments) {
        this.physicalDocuments = physicalDocuments;
    }

    public String getDigitalId() {
        return digitalId;
    }

    public void setDigitalId(final String digitalId) {
        this.digitalId = digitalId;
    }

    public Double getMinorErrorRate() {
        return minorErrorRate;
    }

    public void setMinorErrorRate(final Double minorErrorRate) {
        this.minorErrorRate = minorErrorRate;
    }

    public Double getMajorErrorRate() {
        return majorErrorRate;
    }

    public void setMajorErrorRate(final Double majorErrorRate) {
        this.majorErrorRate = majorErrorRate;
    }

    public String getCheckNotes() {
        return checkNotes;
    }

    public void setCheckNotes(final String checkNotes) {
        this.checkNotes = checkNotes;
    }

    public Set<GlobalCheck> getChecks() {
        return checks;
    }

    public void setChecks(final Set<GlobalCheck> checks) {
        this.checks = checks;
    }

    public LocalDate getDeliveryDate() {
        return deliveryDate;
    }

    public void setDeliveryDate(final LocalDate deliveryDate) {
        this.deliveryDate = deliveryDate;
    }

    public Long getTotalLength() {
        return totalLength;
    }

    public void setTotalLength(final Long totalLength) {
        this.totalLength = totalLength;
    }

    public Set<AutomaticCheckResult> getAutomaticCheckResults() {
        return automaticCheckResults;
    }

    public void setAutomaticCheckResults(final Set<AutomaticCheckResult> results) {
        this.automaticCheckResults.clear();
        if (results != null) {
            results.forEach(this::addAutomaticCheckResult);
        }
    }

    public void addAutomaticCheckResult(final AutomaticCheckResult result) {
        if (result != null) {
            this.automaticCheckResults.add(result);
            result.setDigitalDocument(this);
        }
    }

    public Set<DeliveredDocument> getDeliveries() {
        return deliveries;
    }

    public void setDeliveries(final Set<DeliveredDocument> deliveries) {
        this.deliveries.clear();
        if (deliveries != null) {
            deliveries.forEach(this::addDelivery);
        }
    }

    public void addDelivery(final DeliveredDocument delivery) {
        if (delivery != null) {
            deliveries.add(delivery);
            delivery.setDigitalDocument(this);
        }
    }

    /**
     * cumule les tailles de fichiers.
     *
     * @param fileLength
     */
    public synchronized void addLength(final Long fileLength) {
        setTotalLength(getTotalLength() + fileLength);
    }

    /**
     * Statuts d'un document numérique
     */
    public enum DigitalDocumentStatus {
        /**
         * En cours de création
         */
        CREATING,
        /**
         * En cours de livraison
         */
        DELIVERING,
        /**
         * Contrôles à faire
         */
        TO_CHECK,
        /**
         * En cours de contrôle
         */
        CHECKING,
        /**
         * Validé definitivement
         */
        VALIDATED,
        /**
         * Rejeté par le premier contrôle
         */
        PRE_REJECTED,
        /**
         * Rejeté definitivement
         */
        REJECTED,
        /**
         * En attente de réfection
         */
        WAITING_FOR_REPAIR,
        /**
         * En erreur de livraison
         */
        DELIVERING_ERROR,
        /**
         * Pré validé
         */
        PRE_VALIDATED,
        /**
         * Annulé
         */
        CANCELED
    }

    public List<DocPage> getOrderedPages() {
        final List<DocPage> result = new ArrayList<>(pages);
        result.sort(new ComparableComparator<>());
        return result;
    }

    public int getPageNumber() {
        return (int) pages.stream().filter(docPage -> docPage.getNumber() != null).count();
    }

    public static final class ComparableComparator<T extends Comparable<DocPage>> implements Comparator<DocPage> {

        @Override
        public int compare(final DocPage page, final DocPage page2) {
            if (page == null && page2 == null) {
                return 0;
            } else if (page == null) {
                return -1;
            } else if (page2 == null) {
                return 1;
            } else if (page.getNumber() == null && page2.getNumber() == null) {
                return 0;
            } else if (page.getNumber() == null) {
                return -1;
            } else if (page2.getNumber() == null) {
                return 1;
            } else {
                return Integer.compare(page.getNumber(), page2.getNumber());
            }
        }
    }
}
