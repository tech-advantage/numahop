package fr.progilone.pgcn.domain.dto.document;

import java.time.LocalDateTime;

import fr.progilone.pgcn.domain.delivery.Delivery;

public class LightDeliveredDigitalDocDTO {
    
    private String identifier;
    private String digitalId;
    private String deliveryId;
    private LocalDateTime deliveryDate;
    private Delivery.DeliveryStatus deliveryStatus;
   
    
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
    public String getDeliveryId() {
        return deliveryId;
    }
    public void setDeliveryId(final String deliveryId) {
        this.deliveryId = deliveryId;
    }
    public LocalDateTime getDeliveryDate() {
        return deliveryDate;
    }
    public void setDeliveryDate(final LocalDateTime deliveryDate) {
        this.deliveryDate = deliveryDate;
    }
    public Delivery.DeliveryStatus getDeliveryStatus() {
        return deliveryStatus;
    }
    public void setDeliveryStatus(final Delivery.DeliveryStatus deliveryStatus) {
        this.deliveryStatus = deliveryStatus;
    }

}
