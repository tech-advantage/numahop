package fr.progilone.pgcn.domain.document;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import fr.progilone.pgcn.domain.AbstractDomainObject;
import jakarta.persistence.*;

/**
 * Created by lebouchp on 10/02/2017.
 */
@Entity
@Table(name = GlobalCheck.TABLE_NAME)
@JsonSubTypes({@JsonSubTypes.Type(name = "doc_global_check", value = GlobalCheck.class)})
public class GlobalCheck extends AbstractDomainObject {

    public static final String TABLE_NAME = "doc_global_check";

    @Column(name = "err_label")
    @Enumerated(EnumType.STRING)
    private Check.ErrorLabel errorLabel;

    @Column(name = "err_type")
    @Enumerated(EnumType.STRING)
    private Check.ErrorType errorType;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "digital_document")
    private DigitalDocument digitalDocument;

    public Check.ErrorType getErrorType() {
        return errorType;
    }

    public void setErrorType(Check.ErrorType errorType) {
        this.errorType = errorType;
    }

    public Check.ErrorLabel getErrorLabel() {
        return errorLabel;
    }

    public void setErrorLabel(Check.ErrorLabel errorLabel) {
        this.errorLabel = errorLabel;
    }

    public DigitalDocument getDigitalDocument() {
        return digitalDocument;
    }

    public void setDigitalDocument(DigitalDocument digitalDocument) {
        this.digitalDocument = digitalDocument;
    }
}
