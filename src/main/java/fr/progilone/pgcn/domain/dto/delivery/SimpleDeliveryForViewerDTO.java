package fr.progilone.pgcn.domain.dto.delivery;

import fr.progilone.pgcn.domain.dto.lot.SimpleLotDTO;

public class SimpleDeliveryForViewerDTO {
    
    private String identifier;
    private String label;
    private String digitizingNotes;
    private SimpleLotDTO lot;
    
    
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
    public SimpleLotDTO getLot() {
        return lot;
    }

    public void setLot(final SimpleLotDTO lot) {
        this.lot = lot;
    }

    public String getDigitizingNotes() {
        return digitizingNotes;
    }

    public void setDigitizingNotes(final String digitizingNotes) {
        this.digitizingNotes = digitizingNotes;
    }
}
