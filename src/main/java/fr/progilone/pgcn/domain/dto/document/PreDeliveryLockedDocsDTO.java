package fr.progilone.pgcn.domain.dto.document;

import java.util.ArrayList;
import java.util.List;

import fr.progilone.pgcn.domain.dto.AbstractDTO;

/**
 * 
 */
public class PreDeliveryLockedDocsDTO extends AbstractDTO {

    private String deliveryLabel;
    private List<String> lockedDocsIdentifiers = new ArrayList<>();

    public PreDeliveryLockedDocsDTO(final String deliveryLabel, final List<String> lockedDocsIdentifiers) {
        this.deliveryLabel = deliveryLabel;
        this.lockedDocsIdentifiers = lockedDocsIdentifiers;
    }
    
    public PreDeliveryLockedDocsDTO() {
        
    }

    public String getDeliveryLabel() {
        return deliveryLabel;
    }

    public void setDeliveryLabel(final String deliveryLabel) {
        this.deliveryLabel = deliveryLabel;
    }

    public List<String> getLockedDocsIdentifiers() {
        return lockedDocsIdentifiers != null ? lockedDocsIdentifiers : new ArrayList<>();
    }

    public void setLockedDocsIdentifiers(final List<String> lockedDocsIdentifiers) {
        this.lockedDocsIdentifiers = lockedDocsIdentifiers;
    }

    
}
