package fr.progilone.pgcn.domain.dto.lot;

import fr.progilone.pgcn.domain.lot.Lot;

/**
 * DTO représentant un lot allégé pour les livraisons (inclusion du format requis)
 *
 * @author jbrunet
 */
public class SimpleLotForDeliveryDTO {

    private String identifier;
    private String label;
    private String code;
    private Lot.LotStatus status;
    private Lot.Type type;
    private String requiredFormat;
    

    public SimpleLotForDeliveryDTO() {
    }

    public Lot.LotStatus getStatus() {
        return status;
    }

    public void setStatus(Lot.LotStatus status) {
        this.status = status;
    }

    public Lot.Type getType() {
        return type;
    }

    public void setType(Lot.Type type) {
        this.type = type;
    }

    public final String getCode() {
        return code;
    }

    public final String getLabel() {
        return label;
    }

    public final String getIdentifier() {
        return identifier;
    }

    public final void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public final void setLabel(String label) {
        this.label = label;
    }

    public final void setCode(String code) {
        this.code = code;
    }

    public String getRequiredFormat() {
        return requiredFormat;
    }

    public void setRequiredFormat(String requiredFormat) {
        this.requiredFormat = requiredFormat;
    }
}
