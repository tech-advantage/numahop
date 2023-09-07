package fr.progilone.pgcn.domain.document;

import fr.progilone.pgcn.domain.AbstractDomainObject;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import java.time.LocalDateTime;

/**
 * Conserve un historique des contrôles de documents.
 *
 * @author Emmanuel RIZET
 *
 */
@Entity
@Table(name = DocCheckHistory.TABLE_NAME)
public class DocCheckHistory extends AbstractDomainObject {

    public static final String TABLE_NAME = "doc_check_history";

    @Column(name = "pgcn_id")
    private String pgcnId;

    @Column(name = "library_id")
    private String libraryId;
    @Column(name = "library_label")
    private String libraryLabel;
    @Column(name = "project_id")
    private String projectId;
    @Column(name = "project_label")
    private String projectLabel;
    @Column(name = "lot_id")
    private String lotId;
    @Column(name = "lot_label")
    private String lotLabel;
    @Column(name = "train_id")
    private String trainId;
    @Column(name = "train_label")
    private String trainLabel;
    @Column(name = "delivery_id")
    private String deliveryId;
    @Column(name = "delivery_label")
    private String deliveryLabel;

    @Column(name = "user_login")
    private String userLogin;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private DigitalDocument.DigitalDocumentStatus status;
    @Column(name = "start_check_date")
    private LocalDateTime startCheckDate;
    @Column(name = "end_check_date")
    private LocalDateTime endCheckDate;
    @Column(name = "page_number")
    private Integer pageNumber;
    @Column(name = "total_length")
    private Long totalLength;

    public String getPgcnId() {
        return pgcnId;
    }

    public void setPgcnId(final String pgcnId) {
        this.pgcnId = pgcnId;
    }

    public String getLibraryId() {
        return libraryId;
    }

    public void setLibraryId(final String libraryId) {
        this.libraryId = libraryId;
    }

    public String getLibraryLabel() {
        return libraryLabel;
    }

    public void setLibraryLabel(final String libraryLabel) {
        this.libraryLabel = libraryLabel;
    }

    public String getProjectId() {
        return projectId;
    }

    public void setProjectId(final String projectId) {
        this.projectId = projectId;
    }

    public String getProjectLabel() {
        return projectLabel;
    }

    public void setProjectLabel(final String projectLabel) {
        this.projectLabel = projectLabel;
    }

    public String getLotId() {
        return lotId;
    }

    public void setLotId(final String lotId) {
        this.lotId = lotId;
    }

    public String getLotLabel() {
        return lotLabel;
    }

    public void setLotLabel(final String lotLabel) {
        this.lotLabel = lotLabel;
    }

    public String getTrainId() {
        return trainId;
    }

    public void setTrainId(final String trainId) {
        this.trainId = trainId;
    }

    public String getTrainLabel() {
        return trainLabel;
    }

    public void setTrainLabel(final String trainLabel) {
        this.trainLabel = trainLabel;
    }

    public String getDeliveryId() {
        return deliveryId;
    }

    public void setDeliveryId(final String deliveryId) {
        this.deliveryId = deliveryId;
    }

    public String getDeliveryLabel() {
        return deliveryLabel;
    }

    public void setDeliveryLabel(final String deliveryLabel) {
        this.deliveryLabel = deliveryLabel;
    }

    public String getUserLogin() {
        return userLogin;
    }

    public void setUserLogin(final String userLogin) {
        this.userLogin = userLogin;
    }

    public DigitalDocument.DigitalDocumentStatus getStatus() {
        return status;
    }

    public void setStatus(final DigitalDocument.DigitalDocumentStatus status) {
        this.status = status;
    }

    public LocalDateTime getStartCheckDate() {
        return startCheckDate;
    }

    public void setStartCheckDate(final LocalDateTime startCheckDate) {
        this.startCheckDate = startCheckDate;
    }

    public LocalDateTime getEndCheckDate() {
        return endCheckDate;
    }

    public void setEndCheckDate(final LocalDateTime endCheckDate) {
        this.endCheckDate = endCheckDate;
    }

    public Integer getPageNumber() {
        return pageNumber;
    }

    public void setPageNumber(final Integer pageNumber) {
        this.pageNumber = pageNumber;
    }

    public Long getTotalLength() {
        return totalLength;
    }

    public void setTotalLength(final Long totalLength) {
        this.totalLength = totalLength;
    }

}
