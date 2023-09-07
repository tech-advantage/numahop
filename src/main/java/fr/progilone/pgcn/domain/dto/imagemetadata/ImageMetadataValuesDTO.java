package fr.progilone.pgcn.domain.dto.imagemetadata;

public class ImageMetadataValuesDTO {

    private String identifier;
    private String docUnitId;
    private String metadataId;
    private String value;

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public String getDocUnitId() {
        return docUnitId;
    }

    public void setDocUnitId(String docUnitId) {
        this.docUnitId = docUnitId;
    }

    public String getMetadataId() {
        return metadataId;
    }

    public void setMetadataId(String metadataId) {
        this.metadataId = metadataId;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
