package fr.progilone.pgcn.domain.dto.document;

import fr.progilone.pgcn.domain.dto.AbstractDTO;
import java.util.ArrayList;
import java.util.List;

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
        return lockedDocsIdentifiers != null ? lockedDocsIdentifiers
                                             : new ArrayList<>();
    }

    public void setLockedDocsIdentifiers(final List<String> lockedDocsIdentifiers) {
        this.lockedDocsIdentifiers = lockedDocsIdentifiers;
    }

}
