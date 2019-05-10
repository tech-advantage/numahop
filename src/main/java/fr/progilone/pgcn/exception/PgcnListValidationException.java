package fr.progilone.pgcn.exception;

import java.util.List;

import fr.progilone.pgcn.domain.AbstractDomainObject;

public class PgcnListValidationException extends PgcnException {

    private final List<? extends AbstractDomainObject> invalidObjects;

    public PgcnListValidationException(final List<? extends AbstractDomainObject> invalidObjects) {
        this.invalidObjects = invalidObjects;
    }

    public List<? extends AbstractDomainObject> getInvalidObjects() {
        return invalidObjects;
    }

}
