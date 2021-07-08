package fr.progilone.pgcn.domain.dto.document;

import java.time.LocalDate;
import java.util.List;

import fr.progilone.pgcn.domain.document.DigitalDocument;
import fr.progilone.pgcn.domain.dto.AbstractDTO;
import fr.progilone.pgcn.domain.dto.document.conditionreport.LightCondReportDetailDTO;
import fr.progilone.pgcn.domain.dto.lot.SimpleLotDTO;
import fr.progilone.pgcn.domain.dto.project.SimpleProjectDTO;

public class SimpleListDigitalDocumentDTO extends AbstractDTO {

    private String identifier;
    private String digitalId;
    private String pgcnId;
    private String label;
    private SimpleDocUnitDTO docUnit;
    private SimpleProjectDTO project;
    private SimpleLotDTO lot;
    private DigitalDocument.DigitalDocumentStatus status;
    private int totalDelivery;
    private LocalDate deliveryDate;
    private int pageNumber;
    private List<LightDeliveredDigitalDocDTO> deliveries;
    private LightCondReportDetailDTO reportDetail;
    
    public SimpleListDigitalDocumentDTO() {
    }

    public String getPgcnId() {
        return pgcnId;
    }

    public void setPgcnId(final String pgcnId) {
        this.pgcnId = pgcnId;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(final String label) {
        this.label = label;
    }

    public String getReducedLabel() {
        if (label==null || label.length()<25) {
            return label;
        } else {
            return label.substring(0, 24).concat("...");
        }
    }

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

    public SimpleProjectDTO getProject() {
        return project;
    }

    public void setProject(final SimpleProjectDTO project) {
        this.project = project;
    }

    public SimpleDocUnitDTO getDocUnit() {
        return docUnit;
    }

    public void setDocUnit(final SimpleDocUnitDTO docUnit) {
        this.docUnit = docUnit;
    }

    public SimpleLotDTO getLot() {
        return lot;
    }

    public void setLot(final SimpleLotDTO lot) {
        this.lot = lot;
    }

    public LocalDate getDeliveryDate() {
        return deliveryDate;
    }

    public void setDeliveryDate(final LocalDate deliveryDate) {
        this.deliveryDate = deliveryDate;
    }

    public int getTotalDelivery() {
        return totalDelivery;
    }

    public void setTotalDelivery(final int totalDelivery) {
        this.totalDelivery = totalDelivery;
    }

    public DigitalDocument.DigitalDocumentStatus getStatus() {
        return status;
    }

    public void setStatus(final DigitalDocument.DigitalDocumentStatus status) {
        this.status = status;
    }

    public int getPageNumber() {
        return pageNumber;
    }

    public void setPageNumber(final int pageNumber) {
        this.pageNumber = pageNumber;
    }

    public List<LightDeliveredDigitalDocDTO> getDeliveries() {
        return deliveries;
    }

    public void setDeliveries(final List<LightDeliveredDigitalDocDTO> deliveries) {
        this.deliveries = deliveries;
    }

    public LightCondReportDetailDTO getReportDetail() {
        return reportDetail;
    }

    public void setReportDetail(final LightCondReportDetailDTO reportDetail) {
        this.reportDetail = reportDetail;
    }
}
