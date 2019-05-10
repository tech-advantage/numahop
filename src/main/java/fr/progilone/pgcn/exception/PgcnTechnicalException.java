package fr.progilone.pgcn.exception;

/**
 * Classe des exceptions techniques.
 */
public class PgcnTechnicalException extends Exception {

    public PgcnTechnicalException() {
        super();
    }

    public PgcnTechnicalException(String message) {
        super(message);
    }

    public PgcnTechnicalException(final Throwable cause) {
        super(cause);
    }
}
