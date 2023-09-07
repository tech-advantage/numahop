package fr.progilone.pgcn.domain.dto.document;

import fr.progilone.pgcn.domain.document.DigitalDocument;
import fr.progilone.pgcn.domain.dto.AbstractDTO;
import fr.progilone.pgcn.domain.dto.lot.SimpleLotDTO;
import fr.progilone.pgcn.domain.dto.project.SimpleProjectDTO;
import java.time.LocalDate;

/**
 * Dto simplifie de digitalDocument pour le controle de metadonnees.
 *
 * @author ert
 *
 */
public class SimpleMetadatasDigitalDocumentDTO extends AbstractDTO {

    private String identifier;
    private String digitalId;
    private String pgcnId;
    private String label;
    private SimpleProjectDTO project;
    private SimpleLotDTO lot;
    private DigitalDocument.DigitalDocumentStatus status;
    private int totalDelivery;
    private LocalDate deliveryDate;

    public String getPgcnId() {
        return pgcnId;
    }

    public void setPgcnId(String pgcnId) {
        this.pgcnId = pgcnId;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public String getDigitalId() {
        return digitalId;
    }

    public void setDigitalId(String digitalId) {
        this.digitalId = digitalId;
    }

    public SimpleMetadatasDigitalDocumentDTO() {
    }

    public SimpleProjectDTO getProject() {
        return project;
    }

    public void setProject(SimpleProjectDTO project) {
        this.project = project;
    }

    public SimpleLotDTO getLot() {
        return lot;
    }

    public void setLot(SimpleLotDTO lot) {
        this.lot = lot;
    }

    public LocalDate getDeliveryDate() {
        return deliveryDate;
    }

    public void setDeliveryDate(LocalDate deliveryDate) {
        this.deliveryDate = deliveryDate;
    }

    public int getTotalDelivery() {
        return totalDelivery;
    }

    public void setTotalDelivery(int totalDelivery) {
        this.totalDelivery = totalDelivery;
    }

    public DigitalDocument.DigitalDocumentStatus getStatus() {
        return status;
    }

    public void setStatus(DigitalDocument.DigitalDocumentStatus status) {
        this.status = status;
    }
}
