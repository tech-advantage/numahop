package fr.progilone.pgcn.domain.dto.delivery;

import fr.progilone.pgcn.domain.delivery.Delivery;
import fr.progilone.pgcn.domain.dto.lot.SimpleLotForDeliveryDTO;

/**
 * DTO représentant les attributs d'une livraison à afficher parmi les résultats de recherche
 */
public class SimpleDeliveryLotDTO {

    private String identifier;
    private String label;
    private Delivery.DeliveryStatus status;
    private SimpleLotForDeliveryDTO lot;

    public final String getLabel() {
        return label;
    }

    public final String getIdentifier() {
        return identifier;
    }

    public final void setIdentifier(final String identifier) {
        this.identifier = identifier;
    }

    public final void setLabel(final String label) {
        this.label = label;
    }

    public Delivery.DeliveryStatus getStatus() {
        return status;
    }

    public void setStatus(final Delivery.DeliveryStatus status) {
        this.status = status;
    }

    public SimpleLotForDeliveryDTO getLot() {
        return lot;
    }

    public void setLot(final SimpleLotForDeliveryDTO lot) {
        this.lot = lot;
    }
}
