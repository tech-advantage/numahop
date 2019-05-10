package fr.progilone.pgcn.domain.dto.document;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

import fr.progilone.pgcn.domain.document.DigitalDocument;
import fr.progilone.pgcn.domain.dto.AbstractDTO;
import fr.progilone.pgcn.domain.dto.check.AutomaticCheckResultDTO;


public class SimpleDigitalDocumentDTO extends AbstractDTO implements Comparable<SimpleDigitalDocumentDTO> {

    private String identifier;
    private String digitalId;
    private DigitalDocument.DigitalDocumentStatus status;
    private int totalDelivery;
    private String checkNotes;
    // Retours de contrôles
    private List<AutomaticCheckResultDTO> automaticCheckResults;
    private DocUnitDTO docUnit;

    private List<SimpleDeliveredDigitalDocDTO> deliveries;

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

    public SimpleDigitalDocumentDTO() {
    }

    public int getTotalDelivery() {
        return totalDelivery;
    }

    public void setTotalDelivery(final int totalDelivery) {
        this.totalDelivery = totalDelivery;
    }

    public String getCheckNotes() {
        return checkNotes;
    }

    public void setCheckNotes(final String checkNotes) {
        this.checkNotes = checkNotes;
    }

    public DigitalDocument.DigitalDocumentStatus getStatus() {
        return status;
    }

    public void setStatus(final DigitalDocument.DigitalDocumentStatus status) {
        this.status = status;
    }

    public List<AutomaticCheckResultDTO> getAutomaticCheckResults() {
        return automaticCheckResults;
    }

    public void setAutomaticCheckResults(final List<AutomaticCheckResultDTO> checkResults) {
        this.automaticCheckResults = checkResults;
    }

    public List<SimpleDeliveredDigitalDocDTO> getDeliveries() {
       deliveries.sort(Collections.reverseOrder());
       return deliveries;
    }

    public void setDeliveries(final List<SimpleDeliveredDigitalDocDTO> deliveries) {
        this.deliveries = deliveries;
    }

    public DocUnitDTO getDocUnit() {
        return docUnit;
    }

    public void setDocUnit(final DocUnitDTO docUnit) {
        this.docUnit = docUnit;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final SimpleDigitalDocumentDTO that = (SimpleDigitalDocumentDTO) o;
        return Objects.equals(digitalId, that.digitalId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(digitalId);
    }

    @Override
    public int compareTo(final SimpleDigitalDocumentDTO comparedDto) {
        return this.digitalId.compareTo(comparedDto.getDigitalId());
    }

}
