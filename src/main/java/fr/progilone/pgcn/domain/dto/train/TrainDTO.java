package fr.progilone.pgcn.domain.dto.train;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;

import fr.progilone.pgcn.domain.dto.AbstractVersionedDTO;
import fr.progilone.pgcn.domain.dto.document.SimplePhysicalDocumentDTO;
import fr.progilone.pgcn.domain.dto.project.SimpleProjectDTO;
import fr.progilone.pgcn.domain.train.Train;

public class TrainDTO extends AbstractVersionedDTO {

    private String identifier;
    private SimpleProjectDTO project;
    private String label;
    private String description;
    private Boolean active;
    private Train.TrainStatus status;
    private LocalDate providerSendingDate;
    private LocalDate returnDate;
    private Set<SimplePhysicalDocumentDTO> physicalDocuments;
    /**
     * Ajout des infos de création
     */
    private String createdBy;
    private LocalDateTime createdDate;
    /**
     * Ajout des infos de modifications
     */
    private String lastModifiedBy;
    private LocalDateTime lastModifiedDate;

    public TrainDTO(String identifier,
                    SimpleProjectDTO project,
                    String label,
                    String description,
                    Boolean active,
                    Train.TrainStatus status,
                    LocalDate providerSendingDate, LocalDate returnDate, Set<SimplePhysicalDocumentDTO> physicalDocuments,
                    String createdBy, LocalDateTime createdDate, String lastModifiedBy, LocalDateTime lastModifiedDate) {
        this.identifier = identifier;
        this.project = project;
        this.label = label;
        this.description = description;
        this.active = active;
        this.status = status;
        this.providerSendingDate = providerSendingDate;
        this.returnDate = returnDate;
        this.physicalDocuments = physicalDocuments;
        this.createdBy = createdBy;
        this.createdDate = createdDate;
        this.lastModifiedBy = lastModifiedBy;
        this.lastModifiedDate = lastModifiedDate;
    }

    public TrainDTO() {
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

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
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

    public void setProviderSendingDate(LocalDate providerSendingDate) {
        this.providerSendingDate = providerSendingDate;
    }

    public LocalDate getReturnDate() {
        return returnDate;
    }

    public void setReturnDate(LocalDate returnDate) {
        this.returnDate = returnDate;
    }

    public Set<SimplePhysicalDocumentDTO> getPhysicalDocuments() {
        return physicalDocuments;
    }

    public void setPhysicalDocuments(Set<SimplePhysicalDocumentDTO> physicalDocuments) {
        this.physicalDocuments = physicalDocuments;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public SimpleProjectDTO getProject() {
        return project;
    }

    public void setProject(SimpleProjectDTO project) {
        this.project = project;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public LocalDateTime getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(LocalDateTime createdDate) {
        this.createdDate = createdDate;
    }

    public String getLastModifiedBy() {
        return lastModifiedBy;
    }

    public void setLastModifiedBy(String lastModifiedBy) {
        this.lastModifiedBy = lastModifiedBy;
    }

    public LocalDateTime getLastModifiedDate() {
        return lastModifiedDate;
    }

    public void setLastModifiedDate(LocalDateTime lastModifiedDate) {
        this.lastModifiedDate = lastModifiedDate;
    }

    public static final class Builder {
        private String identifier;
        private SimpleProjectDTO project;
        private String label;
        private String description;
        private Boolean active;
        private Train.TrainStatus status;
        private LocalDate providerSendingDate;
        private LocalDate returnDate;
        private Set<SimplePhysicalDocumentDTO> physicalDocuments;
        private String createdBy;
        private LocalDateTime createdDate;
        private String lastModifiedBy;
        private LocalDateTime lastModifiedDate;

        public Builder reinit() {
            this.identifier = null;
            this.project = null;
            this.label = null;
            this.description = null;
            this.active = null;
            this.status = null;
            this.providerSendingDate = null;
            this.returnDate = null;
            this.physicalDocuments = null;
            this.createdBy = null;
            this.createdDate = null;
            this.lastModifiedBy = null;
            this.lastModifiedDate = null;
            return this;
        }

        public Builder setIdentifier(String identifier) {
            this.identifier = identifier;
            return this;
        }

        public Builder setProject(SimpleProjectDTO project) {
            this.project = project;
            return this;
        }

        public Builder setLabel(String label) {
            this.label = label;
            return this;
        }

        public Builder setDescription(String description) {
            this.description = description;
            return this;
        }

        public Builder setActive(Boolean active) {
            this.active = active;
            return this;
        }

        public Builder setStatus(Train.TrainStatus status) {
            this.status = status;
            return this;
        }

        public Builder setProviderSendingDate(LocalDate providerSendingDate) {
            this.providerSendingDate = providerSendingDate;
            return this;
        }

        public Builder setReturnDate(LocalDate returnDate) {
            this.returnDate = returnDate;
            return this;
        }

        public Builder setPhysicalDocuments(Set<SimplePhysicalDocumentDTO> physicalDocuments) {
            this.physicalDocuments = physicalDocuments;
            return this;
        }

        public Builder setCreatedBy(String createdBy) {
            this.createdBy = createdBy;
            return this;
        }

        public Builder setCreatedDate(LocalDateTime createdDate) {
            this.createdDate = createdDate;
            return this;
        }

        public Builder setLastModifiedBy(String lastModifiedBy) {
            this.lastModifiedBy = lastModifiedBy;
            return this;
        }

        public Builder setLastModifiedDate(LocalDateTime lastModifiedDate) {
            this.lastModifiedDate = lastModifiedDate;
            return this;
        }

        public TrainDTO build() {
            return new TrainDTO(identifier,
                                project,
                                label,
                                description,
                                active,
                                status,
                                providerSendingDate,
                                returnDate,
                                physicalDocuments,
                createdBy,
                createdDate,
                lastModifiedBy,
                lastModifiedDate);
        }

    }
}
