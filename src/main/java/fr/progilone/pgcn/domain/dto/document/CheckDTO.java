package fr.progilone.pgcn.domain.dto.document;

import fr.progilone.pgcn.domain.dto.AbstractVersionedDTO;

/**
 * Created by lebouchp on 10/02/2017.
 */
public class CheckDTO extends AbstractVersionedDTO {

    private String identifier;
    private String errorLabel;
    private String errorType;
    private SimpleDocPageDTO page;

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

    public SimpleDocPageDTO getPage() {
        return page;
    }

    public void setPage(SimpleDocPageDTO page) {
        this.page = page;
    }
}
