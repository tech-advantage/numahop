package fr.progilone.pgcn.domain.dto.document;

import fr.progilone.pgcn.domain.document.PhysicalDocument;
import fr.progilone.pgcn.domain.dto.AbstractDTO;

public class ListPhysicalDocumentDTO extends AbstractDTO {

    private String identifier;
    private String name;
    private String digitalId;
    private Integer totalPage;
    private PhysicalDocument.PhysicalDocumentStatus status;
    private SimpleDocUnitDTO docUnit;
    
    private String reportDetailDim;
    private String reportDetailInsurance;
    private String reportDetailOperture;

    public ListPhysicalDocumentDTO(final String identifier, final String name, final String digitalId, final Integer totalPage, final PhysicalDocument.PhysicalDocumentStatus status, final SimpleDocUnitDTO docUnit) {
        this.identifier = identifier;
        this.name = name;
        this.digitalId = digitalId;
        this.totalPage = totalPage;
        this.status = status;
        this.docUnit = docUnit;
    }

    public ListPhysicalDocumentDTO() {
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(final String identifier) {
        this.identifier = identifier;
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public String getDigitalId() {
        return digitalId;
    }

    public void setDigitalId(final String digitalId) {
        this.digitalId = digitalId;
    }

    public Integer getTotalPage() {
        return totalPage;
    }

    public void setTotalPage(final Integer totalPage) {
        this.totalPage = totalPage;
    }

    public SimpleDocUnitDTO getDocUnit() {
        return docUnit;
    }

    public void setDocUnit(final SimpleDocUnitDTO docUnit) {
        this.docUnit = docUnit;
    }

    public PhysicalDocument.PhysicalDocumentStatus getStatus() {
        return status;
    }

    public void setStatus(final PhysicalDocument.PhysicalDocumentStatus status) {
        this.status = status;
    }

    public String getReportDetailDim() {
        return reportDetailDim;
    }

    public void setReportDetailDim(final String reportDetailDim) {
        this.reportDetailDim = reportDetailDim;
    }

    public String getReportDetailInsurance() {
        return reportDetailInsurance;
    }

    public void setReportDetailInsurance(final String reportDetailInsurance) {
        this.reportDetailInsurance = reportDetailInsurance;
    }

    public String getReportDetailOperture() {
        return reportDetailOperture;
    }

    public void setReportDetailOperture(final String reportDetailOperture) {
        this.reportDetailOperture = reportDetailOperture;
    }

   
}
