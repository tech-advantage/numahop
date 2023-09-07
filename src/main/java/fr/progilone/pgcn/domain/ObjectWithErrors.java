package fr.progilone.pgcn.domain;

import fr.progilone.pgcn.exception.message.PgcnError;
import fr.progilone.pgcn.exception.message.PgcnList;
import java.util.Collection;

public interface ObjectWithErrors {

    Collection<PgcnError> getErrorsAsList();

    PgcnList<PgcnError> getErrors();

    void addError(PgcnError error);

    void setErrors(PgcnList<PgcnError> errors);

}