package fr.progilone.pgcn.exception;

import fr.progilone.pgcn.exception.message.PgcnError;
import fr.progilone.pgcn.exception.message.PgcnList;

/**
 * Classe exception métier.
 */
public class PgcnBusinessException extends PgcnException {

    public PgcnBusinessException(final PgcnList<PgcnError> errors) {
        super(errors);
    }

    public PgcnBusinessException(final PgcnList<PgcnError> errors, PgcnExceptionLevel level) {
        super(errors, level);
    }

    public PgcnBusinessException(final PgcnError error) {
        super(error);
    }

    public PgcnBusinessException(final PgcnError error, PgcnExceptionLevel level) {
        super(error, level);
    }

}
