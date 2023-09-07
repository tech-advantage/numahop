package fr.progilone.pgcn.domain.dto.delivery;

import fr.progilone.pgcn.domain.dto.AbstractDTO;
import fr.progilone.pgcn.domain.dto.lot.SimpleLotDTO;
import java.time.LocalDate;

/**
 * DTO pour les livraisons qui ne contient que les champs éditables par le prestataire.
 */
public class ManualDeliveryDTO extends AbstractDTO {

    private String identifier;
    private SimpleLotDTO lot;
    private String label;
    private String description;
    private String payment;
    private String status;
    private String method;
    private LocalDate receptionDate;
    private String folderPath;
    private String imgFormat;
    private String digitizingNotes;
    private String controlNotes;

    public ManualDeliveryDTO(String identifier,
                             SimpleLotDTO lot,
                             String label,
                             String description,
                             String payment,
                             String status,
                             String method,
                             LocalDate receptionDate,
                             String folderPath,
                             String imgFormat,
                             String digitizingNotes,
                             String controlNotes) {
        this.identifier = identifier;
        this.lot = lot;
        this.label = label;
        this.description = description;
        this.payment = payment;
        this.status = status;
        this.method = method;
        this.receptionDate = receptionDate;
        this.folderPath = folderPath;
        this.imgFormat = imgFormat;
        this.digitizingNotes = digitizingNotes;
        this.controlNotes = controlNotes;
    }

    public ManualDeliveryDTO() {
    }

    public SimpleLotDTO getLot() {
        return lot;
    }

    public void setLot(SimpleLotDTO lot) {
        this.lot = lot;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPayment() {
        return payment;
    }

    public void setPayment(String payment) {
        this.payment = payment;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public LocalDate getReceptionDate() {
        return receptionDate;
    }

    public void setReceptionDate(LocalDate receptionDate) {
        this.receptionDate = receptionDate;
    }

    public String getFolderPath() {
        return folderPath;
    }

    public void setFolderPath(String folderPath) {
        this.folderPath = folderPath;
    }

    public String getImgFormat() {
        return imgFormat;
    }

    public void setImgFormat(String imgFormat) {
        this.imgFormat = imgFormat;
    }

    public String getDigitizingNotes() {
        return digitizingNotes;
    }

    public void setDigitizingNotes(String digitizingNotes) {
        this.digitizingNotes = digitizingNotes;
    }

    public String getControlNotes() {
        return controlNotes;
    }

    public void setControlNotes(String controlNotes) {
        this.controlNotes = controlNotes;
    }

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

    /**
     * Builder pour la classe DeliveryDTO
     */
    public static final class Builder {

        private String identifier;
        private SimpleLotDTO lot;
        private String label;
        private String description;
        private String payment;
        private String status;
        private String method;
        private LocalDate receptionDate;
        private String folderPath;
        private String imgFormat;
        private String digitizingNotes;
        private String controlNotes;

        public Builder setIdentifier(final String identifier) {
            this.identifier = identifier;
            return this;
        }

        public Builder setLabel(final String label) {
            this.label = label;
            return this;
        }

        public Builder setLot(SimpleLotDTO lot) {
            this.lot = lot;
            return this;
        }

        public Builder setDescription(String description) {
            this.description = description;
            return this;
        }

        public Builder setPayment(String payment) {
            this.payment = payment;
            return this;
        }

        public Builder setStatus(String status) {
            this.status = status;
            return this;
        }

        public Builder setMethod(String method) {
            this.method = method;
            return this;
        }

        public Builder setReceptionDate(LocalDate receptionDate) {
            this.receptionDate = receptionDate;
            return this;
        }

        public Builder setFolderPath(String folderPath) {
            this.folderPath = folderPath;
            return this;
        }

        public Builder setImgFormat(String imgFormat) {
            this.imgFormat = imgFormat;
            return this;
        }

        public Builder setDigitizingNotes(String digitizingNotes) {
            this.digitizingNotes = digitizingNotes;
            return this;
        }

        public Builder setControlNotes(String controlNotes) {
            this.controlNotes = controlNotes;
            return this;
        }

        public Builder reinit() {
            this.identifier = null;
            this.lot = null;
            this.label = null;
            this.description = null;
            this.payment = null;
            this.status = null;
            this.method = null;
            this.receptionDate = null;
            this.folderPath = null;
            this.imgFormat = null;
            this.digitizingNotes = null;
            this.controlNotes = null;
            return this;
        }

        public ManualDeliveryDTO build() {
            return new ManualDeliveryDTO(identifier, lot, label, description, payment, status, method, receptionDate, folderPath, imgFormat, digitizingNotes, controlNotes);
        }

    }
}
