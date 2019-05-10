package fr.progilone.pgcn.domain.multilotsdelivery;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import fr.progilone.pgcn.domain.AbstractDomainObject;
import fr.progilone.pgcn.domain.delivery.Delivery;

@Entity
@Table(name = MultiLotsDelivery.TABLE_NAME)
public class MultiLotsDelivery extends AbstractDomainObject {
    
    /**
     * Nom des tables dans la base de données.
     */
    public static final String TABLE_NAME = "del_multi_lots_delivery";


    @Column(name = "label", nullable = false)
    private String label;

    @Column(name = "description")
    private String description;

    @Column(name = "delivery_payment")
    //@Field(type = FieldType.String, analyzer = ANALYZER_KEYWORD)
    private DeliveryPayment payment;

    @Column(name = "delivery_status")
    //@Field(type = FieldType.String, analyzer = ANALYZER_KEYWORD)
    //@Audited
    private DeliveryStatus status;

    @Column(name = "delivery_method")
    //@Field(type = FieldType.String, analyzer = ANALYZER_KEYWORD)
    private DeliveryMethod method;

    @Column(name = "reception_date", nullable = false)
    //@Field(type = FieldType.Date)
    private LocalDate receptionDate;

    @Column(name = "folder_path", nullable = false)
    private String folderPath;

    @Column(name = "digitizing_notes")
    private String digitizingNotes;
    
    @Column(name = "control_notes")
    private String controlNotes;
    
    @Column(name = "selected_by_train")
    private boolean selectedByTrain;
    
    @Column(name = "train_id")
    private String trainId;
    
    /**
     * Liste des livraisons constituant la livraison groupée. 
     */
    @OneToMany(mappedBy = "multiLotsDelivery", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Delivery> deliveries = new ArrayList<>();

    public String getLabel() {
        return label;
    }

    public void setLabel(final String label) {
        this.label = label;
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

    public String getFolderPath() {
        return folderPath;
    }

    public void setFolderPath(final String folderPath) {
        this.folderPath = folderPath;
    }

    public String getDigitizingNotes() {
        return digitizingNotes;
    }

    public void setDigitizingNotes(final String digitizingNotes) {
        this.digitizingNotes = digitizingNotes;
    }

    public String getControlNotes() {
        return controlNotes;
    }

    public void setControlNotes(final String controlNotes) {
        this.controlNotes = controlNotes;
    }

    public boolean isSelectedByTrain() {
        return selectedByTrain;
    }

    public void setSelectedByTrain(final boolean selectedByTrain) {
        this.selectedByTrain = selectedByTrain;
    }

    public String getTrainId() {
        return trainId;
    }

    public void setTrainId(final String trainId) {
        this.trainId = trainId;
    }

    public List<Delivery> getDeliveries() {
        return deliveries;
    }

    public void setDeliveries(final List<Delivery> deliveries) {
        this.deliveries = deliveries;
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
