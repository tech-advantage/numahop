package fr.progilone.pgcn.domain.dto.delivery;

import fr.progilone.pgcn.domain.dto.lot.SimpleLotDTO;

public class SimpleDeliveryForViewerDTO {
    
    private String identifier;
    private String label;
    private SimpleLotDTO lot;
    
    
    public String getIdentifier() {
        return identifier;
    }
    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }
    public String getLabel() {
        return label;
    }
    public void setLabel(String label) {
        this.label = label;
    }
    public SimpleLotDTO getLot() {
        return lot;
    }
    public void setLot(SimpleLotDTO lot) {
        this.lot = lot;
    }
    
    

}
