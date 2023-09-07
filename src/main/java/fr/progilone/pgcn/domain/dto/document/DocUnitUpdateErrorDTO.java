package fr.progilone.pgcn.domain.dto.document;

public class DocUnitUpdateErrorDTO {

    private String identifier;
    private String label;
    private String message;

    public DocUnitUpdateErrorDTO(String identifier, String label, String message) {
        this.identifier = identifier;
        this.label = label;
        this.message = message;
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

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

}
