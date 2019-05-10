package fr.progilone.pgcn.domain.dto.document;

import fr.progilone.pgcn.domain.dto.AbstractDTO;

/**
 * Created by lebouchp on 10/02/2017.
 */
public class GlobalCheckDTO extends AbstractDTO {

    private String identifier;
    private String errorLabel;
    private String errorType;
    private SimpleDigitalDocumentDTO digitalDocument;

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public String getErrorLabel() {
        return errorLabel;
    }

    public void setErrorLabel(String errorLabel) {
        this.errorLabel = errorLabel;
    }

    public String getErrorType() {
        return errorType;
    }

    public void setErrorType(String errorType) {
        this.errorType = errorType;
    }

    public SimpleDigitalDocumentDTO getDigitalDocument() {
        return digitalDocument;
    }

    public void setDigitalDocument(SimpleDigitalDocumentDTO digitalDocument) {
        this.digitalDocument = digitalDocument;
    }
}
