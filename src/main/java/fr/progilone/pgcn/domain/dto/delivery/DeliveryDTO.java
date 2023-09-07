package fr.progilone.pgcn.domain.dto.delivery;

import fr.progilone.pgcn.domain.dto.AbstractDTO;
import fr.progilone.pgcn.domain.dto.check.AutomaticCheckResultDTO;
import fr.progilone.pgcn.domain.dto.lot.SimpleLotForDeliveryDTO;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO représentant une livraison
 */
public class DeliveryDTO extends AbstractDTO {

    private String identifier;
    private SimpleLotForDeliveryDTO lot;
    private String label;
    private Integer documentCount;
    private String description;
    private String payment;
    private String status;
    private String method;
    private LocalDate receptionDate;
    private LocalDate depositDate;
    private String folderPath;
    private String imgFormat;
    private String digitizingNotes;
    private Boolean fileFormatOK;
    private Boolean sequentialNumbers;
    private Boolean numberOfFilesOK;
    private Boolean numberOfFilesMatching;
    private Boolean mirePresent;
    private Boolean mireOK;
    private Boolean tableOfContentsPresent;
    private Boolean tableOfContentsOK;
    private Boolean altoPresent;
    private String controlNotes;
    // Retours de contrôles
    private List<AutomaticCheckResultDTO> automaticCheckResults;
    /** Ajout des infos de création */
    private String createdBy;
    private LocalDateTime createdDate;
    /** Ajout des infos de modifications */
    private String lastModifiedBy;
    private LocalDateTime lastModifiedDate;
    private String multiLotsDelId;
    private String multiLotsDelLabel;

    public DeliveryDTO() {
    }

    public SimpleLotForDeliveryDTO getLot() {
        return lot;
    }

    public void setLot(final SimpleLotForDeliveryDTO lot) {
        this.lot = lot;
    }

    public Integer getDocumentCount() {
        return documentCount;
    }

    public void setDocumentCount(final Integer documentCount) {
        this.documentCount = documentCount;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(final String description) {
        this.description = description;
    }

    public String getPayment() {
        return payment;
    }

    public void setPayment(final String payment) {
        this.payment = payment;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(final String status) {
        this.status = status;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(final String method) {
        this.method = method;
    }

    public LocalDate getReceptionDate() {
        return receptionDate;
    }

    public void setReceptionDate(final LocalDate receptionDate) {
        this.receptionDate = receptionDate;
    }

    public LocalDate getDepositDate() {
        return depositDate;
    }

    public void setDepositDate(final LocalDate depositDate) {
        this.depositDate = depositDate;
    }

    public String getFolderPath() {
        return folderPath;
    }

    public void setFolderPath(final String folderPath) {
        this.folderPath = folderPath;
    }

    public String getImgFormat() {
        return imgFormat;
    }

    public void setImgFormat(final String imgFormat) {
        this.imgFormat = imgFormat;
    }

    public String getDigitizingNotes() {
        return digitizingNotes;
    }

    public void setDigitizingNotes(final String digitizingNotes) {
        this.digitizingNotes = digitizingNotes;
    }

    public Boolean getFileFormatOK() {
        return fileFormatOK;
    }

    public void setFileFormatOK(final Boolean fileFormatOK) {
        this.fileFormatOK = fileFormatOK;
    }

    public Boolean getNumberOfFilesOK() {
        return numberOfFilesOK;
    }

    public void setNumberOfFilesOK(final Boolean numberOfFilesOK) {
        this.numberOfFilesOK = numberOfFilesOK;
    }

    public Boolean getNumberOfFilesMatching() {
        return numberOfFilesMatching;
    }

    public void setNumberOfFilesMatching(final Boolean numberOfFilesMatching) {
        this.numberOfFilesMatching = numberOfFilesMatching;
    }

    public Boolean getMirePresent() {
        return mirePresent;
    }

    public void setMirePresent(final Boolean mirePresent) {
        this.mirePresent = mirePresent;
    }

    public Boolean getMireOK() {
        return mireOK;
    }

    public void setMireOK(final Boolean mireOK) {
        this.mireOK = mireOK;
    }

    public Boolean getTableOfContentsPresent() {
        return tableOfContentsPresent;
    }

    public void setTableOfContentsPresent(final Boolean tableOfContentsPresent) {
        this.tableOfContentsPresent = tableOfContentsPresent;
    }

    public Boolean getTableOfContentsOK() {
        return tableOfContentsOK;
    }

    public void setTableOfContentsOK(final Boolean tableOfContentsOK) {
        this.tableOfContentsOK = tableOfContentsOK;
    }

    public Boolean getAltoPresent() {
        return altoPresent;
    }

    public void setAltoPresent(final Boolean altoPresent) {
        this.altoPresent = altoPresent;
    }

    public String getControlNotes() {
        return controlNotes;
    }

    public void setControlNotes(final String controlNotes) {
        this.controlNotes = controlNotes;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(final String identifier) {
        this.identifier = identifier;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(final String label) {
        this.label = label;
    }

    public Boolean getSequentialNumbers() {
        return sequentialNumbers;
    }

    public void setSequentialNumbers(final Boolean sequentialNumbers) {
        this.sequentialNumbers = sequentialNumbers;
    }

    public List<AutomaticCheckResultDTO> getAutomaticCheckResults() {
        return automaticCheckResults;
    }

    public void setAutomaticCheckResults(final List<AutomaticCheckResultDTO> checkResults) {
        this.automaticCheckResults = checkResults;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(final String createdBy) {
        this.createdBy = createdBy;
    }

    public LocalDateTime getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(final LocalDateTime createdDate) {
        this.createdDate = createdDate;
    }

    public String getLastModifiedBy() {
        return lastModifiedBy;
    }

    public void setLastModifiedBy(final String lastModifiedBy) {
        this.lastModifiedBy = lastModifiedBy;
    }

    public LocalDateTime getLastModifiedDate() {
        return lastModifiedDate;
    }

    public void setLastModifiedDate(final LocalDateTime lastModifiedDate) {
        this.lastModifiedDate = lastModifiedDate;
    }

    public String getMultiLotsDelId() {
        return multiLotsDelId;
    }

    public void setMultiLotsDelId(final String multiLotsDelId) {
        this.multiLotsDelId = multiLotsDelId;
    }

    public String getMultiLotsDelLabel() {
        return multiLotsDelLabel;
    }

    public void setMultiLotsDelLabel(final String multiLotsDelLabel) {
        this.multiLotsDelLabel = multiLotsDelLabel;
    }

}
