package fr.progilone.pgcn.domain.dto.lot;

import java.util.Date;

/**
 * DTO représentant un lot, avec les infos requises pour une utilisation dans les listes côtés client
 */
public class LotListDTO {

    private String identifier;
    private String label;
    private String code;
    private String type;
    private String description;
    private Boolean active;
    private String status;
    private String condNotes;
    private String numNotes;
    private String requiredFormat;
    private Date deliveryDateForseen;
    private String requiredTypeCompression;
    private Integer requiredTauxCompression;
    private String requiredResolution;
    private String requiredColorspace;
    private String projectIdentifier;

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

    public String getCode() {
        return code;
    }

    public void setCode(final String code) {
        this.code = code;
    }

    public String getType() {
        return type;
    }

    public void setType(final String type) {
        this.type = type;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(final String description) {
        this.description = description;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(final Boolean active) {
        this.active = active;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(final String status) {
        this.status = status;
    }

    public String getCondNotes() {
        return condNotes;
    }

    public void setCondNotes(final String condNotes) {
        this.condNotes = condNotes;
    }

    public String getNumNotes() {
        return numNotes;
    }

    public void setNumNotes(final String numNotes) {
        this.numNotes = numNotes;
    }

    public String getRequiredFormat() {
        return requiredFormat;
    }

    public void setRequiredFormat(final String requiredFormat) {
        this.requiredFormat = requiredFormat;
    }

    public Date getDeliveryDateForseen() {
        return deliveryDateForseen;
    }

    public void setDeliveryDateForseen(final Date deliveryDateForseen) {
        this.deliveryDateForseen = deliveryDateForseen;
    }

    public String getRequiredTypeCompression() {
        return requiredTypeCompression;
    }

    public void setRequiredTypeCompression(final String requiredTypeCompression) {
        this.requiredTypeCompression = requiredTypeCompression;
    }

    public Integer getRequiredTauxCompression() {
        return requiredTauxCompression;
    }

    public void setRequiredTauxCompression(final Integer requiredTauxCompression) {
        this.requiredTauxCompression = requiredTauxCompression;
    }

    public String getRequiredResolution() {
        return requiredResolution;
    }

    public void setRequiredResolution(final String requiredResolution) {
        this.requiredResolution = requiredResolution;
    }

    public String getRequiredColorspace() {
        return requiredColorspace;
    }

    public void setRequiredColorspace(final String requiredColorspace) {
        this.requiredColorspace = requiredColorspace;
    }

    public String getProjectIdentifier() {
        return projectIdentifier;
    }

    public void setProjectIdentifier(final String projectIdentifier) {
        this.projectIdentifier = projectIdentifier;
    }
}
