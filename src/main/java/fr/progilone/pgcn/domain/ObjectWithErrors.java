package fr.progilone.pgcn.domain;

import java.util.Collection;

import fr.progilone.pgcn.exception.message.PgcnError;
import fr.progilone.pgcn.exception.message.PgcnList;

public interface ObjectWithErrors {

    Collection<PgcnError> getErrorsAsList();

    PgcnList<PgcnError> getErrors();

    void addError(PgcnError error);

    void setErrors(PgcnList<PgcnError> errors);

}