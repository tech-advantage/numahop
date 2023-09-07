package fr.progilone.pgcn.exception;

import fr.progilone.pgcn.exception.message.PgcnError;
import fr.progilone.pgcn.exception.message.PgcnList;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.apache.commons.lang3.StringUtils;

/**
 * Classe mère des exceptions métier.
 */
public class PgcnException extends RuntimeException {

    private PgcnExceptionLevel level;
    private final List<PgcnError> errors = new ArrayList<>();

    public PgcnException() {
        super();
    }

    public PgcnException(final PgcnList<PgcnError> errors) {
        if (errors != null) {
            this.errors.addAll(errors.get());
        }
        this.level = PgcnExceptionLevel.ERROR;
    }

    public PgcnException(final PgcnList<PgcnError> errors, final PgcnExceptionLevel level) {
        if (errors != null) {
            this.errors.addAll(errors.get());
        }
        this.level = level;
    }

    public PgcnException(final PgcnError error) {
        if (error != null) {
            errors.add(error);
        }
    }

    public PgcnException(final PgcnError error, final PgcnExceptionLevel level) {
        if (error != null) {
            errors.add(error);
        }
        this.level = level;
    }

    public PgcnException(final PgcnError error, final Throwable e) {
        super(e);
        if (error != null) {
            errors.add(error);
        }
        this.level = PgcnExceptionLevel.ERROR;
    }

    public List<PgcnError> getErrors() {
        return Collections.unmodifiableList(errors);
    }

    public void addError(final PgcnError error) {
        errors.add(error);
    }

    public boolean isErrorListEmpty() {
        return errors.isEmpty();
    }

    public PgcnExceptionLevel getLevel() {
        return level;
    }

    public void setLevel(final PgcnExceptionLevel level) {
        this.level = level;
    }

    @Override
    public String toString() {
        return StringUtils.join(errors, ", ");
    }

    @Override
    public String getMessage() {
        return toString();
    }
}
