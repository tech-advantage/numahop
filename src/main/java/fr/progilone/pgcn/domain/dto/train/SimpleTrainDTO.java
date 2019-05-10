package fr.progilone.pgcn.domain.dto.train;

import java.time.LocalDate;

import fr.progilone.pgcn.domain.dto.AbstractDTO;
import fr.progilone.pgcn.domain.train.Train;

public class SimpleTrainDTO extends AbstractDTO {

    private String identifier;
    private String label;
    private String description;
    private Train.TrainStatus status;
    private LocalDate providerSendingDate;
    private LocalDate returnDate;

    public SimpleTrainDTO() {
    }

    public SimpleTrainDTO(final String identifier,
                          final String label,
                          final String description,
                          final Train.TrainStatus status,
                          final LocalDate providerSendingDate,
                          final LocalDate returnDate) {
        this.identifier = identifier;
        this.label = label;
        this.description = description;
        this.status = status;
        this.providerSendingDate = providerSendingDate;
        this.returnDate = returnDate;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public Train.TrainStatus getStatus() {
        return status;
    }

    public void setStatus(Train.TrainStatus status) {
        this.status = status;
    }

    public LocalDate getProviderSendingDate() {
        return providerSendingDate;
    }

    public void setProviderSendingDate(final LocalDate providerSendingDate) {
        this.providerSendingDate = providerSendingDate;
    }

    public LocalDate getReturnDate() {
        return returnDate;
    }

    public void setReturnDate(final LocalDate returnDate) {
        this.returnDate = returnDate;
    }
    
    /**
     * Builder pour la classe SimpleTrainDTO
     *
     */
    public static final class Builder {

        private String identifier;
        private String label;
        private String description;
        private Train.TrainStatus status;
        private LocalDate providerSendingDate;
        private LocalDate returnDate;

        public Builder reinit() {
            this.identifier = null;
            this.label = null;
            this.description = null;
            this.status = null;
            this.providerSendingDate = null;
            this.returnDate = null;
            return this;
        }

        public Builder setIdentifier(final String identifier) {
            this.identifier = identifier;
            return this;
        }

        public Builder setDescription(final String description) {
            this.description = description;
            return this;
        }

        public Builder setLabel(final String label) {
            this.label = label;
            return this;
        }

        public Builder setStatus(Train.TrainStatus status) {
            this.status = status;
            return this;
        }

        public void setProviderSendingDate(final LocalDate providerSendingDate) {
            this.providerSendingDate = providerSendingDate;
        }

        public void setReturnDate(final LocalDate returnDate) {
            this.returnDate = returnDate;
        }

        public SimpleTrainDTO build() {
            return new SimpleTrainDTO(identifier, label, description, status, providerSendingDate, returnDate);
        }
    }
    
}
