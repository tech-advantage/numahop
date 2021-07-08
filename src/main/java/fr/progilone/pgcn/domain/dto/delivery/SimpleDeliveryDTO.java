package fr.progilone.pgcn.domain.dto.delivery;

import fr.progilone.pgcn.domain.delivery.Delivery;

/**
 * DTO représentant les attributs d'une livraison à afficher parmi les résultats de recherche
 */
public class SimpleDeliveryDTO {

    private String identifier;
    private String label;
    private Delivery.DeliveryStatus status;

    public final String getLabel() {
        return label;
    }

    public final String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public Delivery.DeliveryStatus getStatus() {
        return status;
    }

    public void setStatus(final Delivery.DeliveryStatus status) {
        this.status = status;
    }
}
