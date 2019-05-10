package fr.progilone.pgcn.domain.dto.multilotsdelivery;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import fr.progilone.pgcn.domain.dto.AbstractDTO;
import fr.progilone.pgcn.domain.dto.delivery.SimpleDeliveryLotDTO;
import fr.progilone.pgcn.domain.dto.lot.SimpleLotForDeliveryDTO;
import fr.progilone.pgcn.domain.dto.train.SimpleTrainDTO;

public class MultiLotsDeliveryDTO extends AbstractDTO {

    private String identifier;
    private String label;
    private String description;
    private String payment;
    private String status;
    private String method;
    private LocalDate receptionDate;
    private String folderPath;
    private String digitizingNotes;
    private String controlNotes;
    private String trainId;
    private boolean selectedByTrain;
    private SimpleTrainDTO train;
    private String createdBy;
    
    private List<SimpleDeliveryLotDTO> deliveries = new ArrayList<>();
    
    private List<SimpleLotForDeliveryDTO> lots = new ArrayList<>();
    
    public MultiLotsDeliveryDTO() {
        
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

    public String getTrainId() {
        return trainId;
    }


    public void setTrainId(final String trainId) {
        this.trainId = trainId;
    }


    public boolean isSelectedByTrain() {
        return selectedByTrain;
    }


    public void setSelectedByTrain(final boolean selectedByTrain) {
        this.selectedByTrain = selectedByTrain;
    }


    public List<SimpleDeliveryLotDTO> getDeliveries() {
        return deliveries;
    }

    public void setDeliveries(final List<SimpleDeliveryLotDTO> deliveries) {
        this.deliveries = deliveries;
    }

    public List<SimpleLotForDeliveryDTO> getLots() {
        return lots;
    }

    public void setLots(final List<SimpleLotForDeliveryDTO> lots) {
        this.lots = lots;
    }


    public SimpleTrainDTO getTrain() {
        return train;
    }


    public void setTrain(final SimpleTrainDTO train) {
        this.train = train;
    }


    public String getCreatedBy() {
        return createdBy;
    }


    public void setCreatedBy(final String createdBy) {
        this.createdBy = createdBy;
    }
    
    


}

