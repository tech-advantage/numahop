package fr.progilone.pgcn.domain.delivery;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import fr.progilone.pgcn.domain.AbstractDomainObject;
import fr.progilone.pgcn.domain.document.CheckSlip;
import fr.progilone.pgcn.domain.document.DigitalDocument;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDate;

/**
 * Gestion de la livraison d'un document.
 * Cette classe fait le lien entre la livraison et le document livré,
 * et ajoute certaines informations sur la livraison du document
 */
@Entity
@Table(name = DeliveredDocument.TABLE_NAME)
@JsonSubTypes({@JsonSubTypes.Type(name = "delivered_doc", value = DeliveredDocument.class)})
public class DeliveredDocument extends AbstractDomainObject {

    /**
     * Nom des tables dans la base de données.
     */
    public static final String TABLE_NAME = "del_delivered_document";

    /**
     * Livraison
     */
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "delivery")
    private Delivery delivery;

    /**
     * Document appartenant à la livraison
     */
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "digital_document")
    private DigitalDocument digitalDocument;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private DigitalDocument.DigitalDocumentStatus status;

    @Column(name = "delivery_date")
    private LocalDate deliveryDate;

    @Column(name = "nb_pages")
    private Integer nbPages;

    @Column(name = "total_length")
    private Long totalLength;

    @ManyToOne(optional = true, fetch = FetchType.LAZY)
    @JoinColumn(name = "check_slip")
    private CheckSlip checkSlip;

    public Delivery getDelivery() {
        return delivery;
    }

    public void setDelivery(final Delivery delivery) {
        this.delivery = delivery;
    }

    public DigitalDocument getDigitalDocument() {
        return digitalDocument;
    }

    public void setDigitalDocument(final DigitalDocument digitalDocument) {
        this.digitalDocument = digitalDocument;
    }

    public DigitalDocument.DigitalDocumentStatus getStatus() {
        return status;
    }

    public void setStatus(final DigitalDocument.DigitalDocumentStatus status) {
        this.status = status;
    }

    public LocalDate getDeliveryDate() {
        return deliveryDate;
    }

    public void setDeliveryDate(final LocalDate deliveryDate) {
        this.deliveryDate = deliveryDate;
    }

    public Integer getNbPages() {
        return nbPages;
    }

    public void setNbPages(final Integer nbPages) {
        this.nbPages = nbPages;
    }

    public Long getTotalLength() {
        return totalLength;
    }

    public void setTotalLength(final Long totalLength) {
        this.totalLength = totalLength;
    }

    public CheckSlip getCheckSlip() {
        return checkSlip;
    }

    public void setCheckSlip(CheckSlip checkSlip) {
        this.checkSlip = checkSlip;
    }
}
