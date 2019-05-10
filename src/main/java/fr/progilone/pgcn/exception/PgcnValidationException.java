package fr.progilone.pgcn.exception;

import fr.progilone.pgcn.domain.ObjectWithErrors;
import fr.progilone.pgcn.exception.message.PgcnError;
import fr.progilone.pgcn.exception.message.PgcnList;

public class PgcnValidationException extends PgcnException {

    private final ObjectWithErrors invalidObject;

    public PgcnValidationException(final ObjectWithErrors invalidObject) {
        super(invalidObject.getErrors());
        this.invalidObject = invalidObject;
    }

    public PgcnValidationException(final ObjectWithErrors invalidObject, final PgcnError error) {
        super(error);
        this.invalidObject = invalidObject;
    }

    public PgcnValidationException(final ObjectWithErrors invalidObject, final PgcnList<PgcnError> allErrors) {
        super(allErrors);
        this.invalidObject = invalidObject;
    }

    public PgcnValidationException(final ObjectWithErrors invalidObject, final PgcnValidationException ex) {
        super();
        this.invalidObject = invalidObject;
        ex.getErrors().forEach(this::addError);
    }

    public ObjectWithErrors getInvalidObject() {
        return invalidObject;
    }

    @Override
    public String toString() {
        return invalidObject + ": " + super.toString();
    }
}
