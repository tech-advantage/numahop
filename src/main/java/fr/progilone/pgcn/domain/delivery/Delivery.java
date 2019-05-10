package fr.progilone.pgcn.domain.delivery;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import fr.progilone.pgcn.domain.AbstractDomainObject;
import fr.progilone.pgcn.domain.check.AutomaticCheckResult;
import fr.progilone.pgcn.domain.lot.Lot;
import fr.progilone.pgcn.domain.multilotsdelivery.MultiLotsDelivery;
import org.hibernate.envers.AuditTable;
import org.hibernate.envers.Audited;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldIndex;
import org.springframework.data.elasticsearch.annotations.FieldType;
import org.springframework.data.elasticsearch.annotations.InnerField;
import org.springframework.data.elasticsearch.annotations.MultiField;
import org.springframework.data.elasticsearch.annotations.Parent;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

import static fr.progilone.pgcn.service.es.EsConstant.*;

/**
 * Classe métier permettant de gérer les livraisons.
 */
@Entity
@Table(name = Delivery.TABLE_NAME)
// Audit
@AuditTable(value = Delivery.AUDIT_TABLE_NAME)
// Jackson
@JsonSubTypes({@JsonSubTypes.Type(name = "delivery", value = Delivery.class)})
// Elasticsearch
@Document(indexName = "#{elasticsearchIndexName}", type = Delivery.ES_TYPE, createIndex = false)
public class Delivery extends AbstractDomainObject {

    /**
     * Nom des tables dans la base de données.
     */
    public static final String TABLE_NAME = "del_delivery";
    public static final String ES_TYPE = "delivery";
    public static final String AUDIT_TABLE_NAME = "aud_del_delivery";

    @ManyToOne
    @JoinColumn(name = "lot_identifier")
    private Lot lot;

    /**
     * Le champ "lot" est répété pour la config elasticsearch @Parent, qui doit être de type String
     */
    @Column(name = "lot_identifier", insertable = false, updatable = false)
    @Parent(type = Lot.ES_TYPE)
    @Field(type = FieldType.String, index = FieldIndex.not_analyzed)
    private String lotId;

    @Column(name = "label", nullable = false)
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

    @Column(name = "document_count")
    @Field(type = FieldType.Integer)
    private Integer documentCount;

    @Column(name = "description")
    private String description;

    @Column(name = "delivery_payment")
    @Field(type = FieldType.String, analyzer = ANALYZER_KEYWORD)
    private DeliveryPayment payment;

    @Column(name = "delivery_status")
    @Field(type = FieldType.String, analyzer = ANALYZER_KEYWORD)
    @Audited
    private DeliveryStatus status;

    @Column(name = "delivery_method")
    @Field(type = FieldType.String, analyzer = ANALYZER_KEYWORD)
    private DeliveryMethod method;

    @Column(name = "reception_date", nullable = false)
    @Field(type = FieldType.Date)
    private LocalDate receptionDate;

    @Column(name = "deposit_date")
    private LocalDate depositDate;

    @Column(name = "folder_path", nullable = false)
    private String folderPath;

    @Column(name = "image_format")
    private String imgFormat;

    @Column(name = "digitizing_notes")
    private String digitizingNotes;

    @Column(name = "file_format_ok")
    private boolean fileFormatOK;

    @Column(name = "number_of_files_ok")
    private boolean numberOfFilesOK;

    @Column(name = "sequential_numbers")
    private boolean sequentialNumbers;

    @Column(name = "number_of_files_matching")
    private boolean numberOfFilesMatching;

    @Column(name = "mire_present")
    private boolean mirePresent;

    @Column(name = "mire_ok")
    private boolean mireOK;

    @Column(name = "table_of_contents_present")
    private boolean tableOfContentsPresent;

    @Column(name = "table_of_contents_ok")
    private boolean tableOfContentsOK;

    @Column(name = "alto_present")
    private boolean altoPresent;

    @Column(name = "control_notes")
    private String controlNotes;

    @Column(name = "compression_type_ok")
    private boolean compressionTypeOK;

    @Column(name = "compression_rate_ok")
    private boolean compressionRateOK;

    @Column(name = "resolution_ok")
    private boolean resolutionOK;

    @Column(name = "colorspace_ok")
    private boolean colorspaceOK;

    @Column(name = "file_integrity_ok")
    private boolean fileIntegrityOk;

    @Column(name = "file_bib_prefix_ok")
    private boolean fileBibPrefixOK;

    @Column(name = "file_case_ok")
    private boolean fileCaseOK;

    @Column(name = "pdf_multi_present")
    private boolean pdfMultiPresent;

    @Column(name = "pdf_multi_ok")
    private boolean pdfMultiOK;

    @OneToOne(mappedBy = "delivery", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private DeliverySlip deliverySlip;

    /**
     * Liste des résultats de contrôles automatiques associés
     */
    @OneToMany(mappedBy = "delivery", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private final Set<AutomaticCheckResult> automaticCheckResults = new HashSet<>();

    /**
     * Liste des documents appartenant à cette livraison
     */
    @OneToMany(mappedBy = "delivery", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private final Set<DeliveredDocument> documents = new HashSet<>();

    @ManyToOne(optional = true, fetch = FetchType.LAZY)
    @JoinColumn(name = "multi_lots_delivery")
    private MultiLotsDelivery multiLotsDelivery;

    public Lot getLot() {
        return lot;
    }

    public void setLot(final Lot lot) {
        this.lot = lot;
    }

    public String getLotId() {
        return lotId;
    }

    public void setLotId(final String lotId) {
        this.lotId = lotId;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(final String label) {
        this.label = label;
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

    public DeliveryPayment getPayment() {
        return payment;
    }

    public void setPayment(final DeliveryPayment payment) {
        this.payment = payment;
    }

    public DeliveryStatus getStatus() {
        return status;
    }

    public void setStatus(final DeliveryStatus status) {
        this.status = status;
    }

    public DeliveryMethod getMethod() {
        return method;
    }

    public void setMethod(final DeliveryMethod method) {
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

    public boolean isFileFormatOK() {
        return fileFormatOK;
    }

    public void setFileFormatOK(final boolean fileFormatOK) {
        this.fileFormatOK = fileFormatOK;
    }

    public boolean isNumberOfFilesOK() {
        return numberOfFilesOK;
    }

    public void setNumberOfFilesOK(final boolean numberOfFilesOK) {
        this.numberOfFilesOK = numberOfFilesOK;
    }

    public boolean isNumberOfFilesMatching() {
        return numberOfFilesMatching;
    }

    public void setNumberOfFilesMatching(final boolean numberOfFilesMatching) {
        this.numberOfFilesMatching = numberOfFilesMatching;
    }

    public boolean isMirePresent() {
        return mirePresent;
    }

    public void setMirePresent(final boolean mirePresent) {
        this.mirePresent = mirePresent;
    }

    public boolean isMireOK() {
        return mireOK;
    }

    public void setMireOK(final boolean mireOK) {
        this.mireOK = mireOK;
    }

    public boolean isTableOfContentsPresent() {
        return tableOfContentsPresent;
    }

    public void setTableOfContentsPresent(final boolean tableOfContentsPresent) {
        this.tableOfContentsPresent = tableOfContentsPresent;
    }

    public boolean isTableOfContentsOK() {
        return tableOfContentsOK;
    }

    public void setTableOfContentsOK(final boolean tableOfContentsOK) {
        this.tableOfContentsOK = tableOfContentsOK;
    }

    public boolean isAltoPresent() {
        return altoPresent;
    }

    public void setAltoPresent(final boolean altoPresent) {
        this.altoPresent = altoPresent;
    }

    public String getControlNotes() {
        return controlNotes;
    }

    public void setControlNotes(final String controlNotes) {
        this.controlNotes = controlNotes;
    }

    public boolean isCompressionTypeOK() {
        return compressionTypeOK;
    }

    public void setCompressionTypeOK(final boolean compressionTypeOK) {
        this.compressionTypeOK = compressionTypeOK;
    }

    public boolean isCompressionRateOK() {
        return compressionRateOK;
    }

    public void setCompressionRateOK(final boolean compressionRateOK) {
        this.compressionRateOK = compressionRateOK;
    }

    public boolean isResolutionOK() {
        return resolutionOK;
    }

    public void setResolutionOK(final boolean resolutionOK) {
        this.resolutionOK = resolutionOK;
    }

    public boolean isColorspaceOK() {
        return colorspaceOK;
    }

    public void setColorspaceOK(final boolean colorspaceOK) {
        this.colorspaceOK = colorspaceOK;
    }

    public boolean isFileIntegrityOk() {
        return fileIntegrityOk;
    }

    public void setFileIntegrityOk(final boolean fileIntegrityOk) {
        this.fileIntegrityOk = fileIntegrityOk;
    }

    public boolean isFileBibPrefixOK() {
        return fileBibPrefixOK;
    }

    public void setFileBibPrefixOK(final boolean fileBibPrefixOK) {
        this.fileBibPrefixOK = fileBibPrefixOK;
    }

    public boolean isFileCaseOK() {
        return fileCaseOK;
    }

    public void setFileCaseOK(final boolean fileCaseOK) {
        this.fileCaseOK = fileCaseOK;
    }

    public boolean isPdfMultiPresent() {
        return pdfMultiPresent;
    }

    public void setPdfMultiPresent(final boolean pdfMultiPresent) {
        this.pdfMultiPresent = pdfMultiPresent;
    }

    public boolean isPdfMultiOK() {
        return pdfMultiOK;
    }

    public void setPdfMultiOK(final boolean pdfMultiOK) {
        this.pdfMultiOK = pdfMultiOK;
    }

    public boolean isSequentialNumbers() {
        return sequentialNumbers;
    }

    public void setSequentialNumbers(final boolean sequentialNumbers) {
        this.sequentialNumbers = sequentialNumbers;
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
            result.setDelivery(this);
        }
    }

    public Set<DeliveredDocument> getDocuments() {
        return documents;
    }

    public void setDocuments(final Set<DeliveredDocument> documents) {
        this.documents.clear();
        if (documents != null) {
            documents.forEach(this::addDocument);
        }
    }

    private void addDocument(final DeliveredDocument document) {
        if (document != null) {
            this.documents.add(document);
            document.setDelivery(this);
        }
    }

    public DeliverySlip getDeliverySlip() {
        return deliverySlip;
    }

    public void setDeliverySlip(final DeliverySlip deliverySlip) {
        this.deliverySlip = deliverySlip;
    }

    public MultiLotsDelivery getMultiLotsDelivery() {
        return multiLotsDelivery;
    }

    public void setMultiLotsDelivery(final MultiLotsDelivery multiLotsDelivery) {
        this.multiLotsDelivery = multiLotsDelivery;
    }

    public enum DeliveryPayment {
        PAID,
        UNPAID
    }

    public enum DeliveryStatus {
        SAVED,
        DELIVERING,
        DELIVERED,
        TO_BE_CONTROLLED,
        VALIDATED,
        REJECTED,
        BACK_TO_PROVIDER,
        AUTOMATICALLY_REJECTED,
        DELIVERED_AGAIN,
        DELIVERING_ERROR,
        TREATED
    }

    public enum DeliveryMethod {
        FTP,
        DISK,
        OTHER
    }

}
