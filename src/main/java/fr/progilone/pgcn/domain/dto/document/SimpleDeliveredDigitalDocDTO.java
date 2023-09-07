package fr.progilone.pgcn.domain.dto.document;

import fr.progilone.pgcn.domain.document.DigitalDocument;
import fr.progilone.pgcn.domain.dto.delivery.SimpleDeliveryDTO;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;

public class SimpleDeliveredDigitalDocDTO implements Comparable<SimpleDeliveredDigitalDocDTO> {

    private String identifier;
    private String digitalId;
    private LocalDate deliveryDate;
    private Integer nbPages;
    private Long totalLength;
    private DigitalDocument.DigitalDocumentStatus status;
    private SimpleDeliveryDTO delivery;
    private LocalDateTime createdDate;
    private String docUnitId;

    private String progress;

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(final String identifier) {
        this.identifier = identifier;
    }

    public String getDigitalId() {
        return digitalId;
    }

    public void setDigitalId(final String digitalId) {
        this.digitalId = digitalId;
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

    public DigitalDocument.DigitalDocumentStatus getStatus() {
        return status;
    }

    public void setStatus(final DigitalDocument.DigitalDocumentStatus status) {
        this.status = status;
    }

    public SimpleDeliveryDTO getDelivery() {
        return delivery;
    }

    public void setDelivery(final SimpleDeliveryDTO delivery) {
        this.delivery = delivery;
    }

    public LocalDateTime getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(final LocalDateTime createdDate) {
        this.createdDate = createdDate;
    }

    public String getDocUnitId() {
        return docUnitId;
    }

    public void setDocUnitId(final String docUnitId) {
        this.docUnitId = docUnitId;
    }

    public String getProgress() {
        return progress;
    }

    public void setProgress(final String progress) {
        this.progress = progress;
    }

    @Override
    public boolean equals(final Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final SimpleDeliveredDigitalDocDTO that = (SimpleDeliveredDigitalDocDTO) o;
        return Objects.equals(identifier, that.identifier);
    }

    @Override
    public int hashCode() {
        return Objects.hash(identifier);
    }

    @Override
    public int compareTo(final SimpleDeliveredDigitalDocDTO comparedDto) {
        return this.createdDate.compareTo(comparedDto.getCreatedDate());
    }

}
