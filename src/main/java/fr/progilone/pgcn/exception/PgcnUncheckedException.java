package fr.progilone.pgcn.exception;

/**
 * Classe exception à utiliser pour remonter en unchecked les exceptions spécifiques
 */
public class PgcnUncheckedException extends RuntimeException {

    public PgcnUncheckedException(Throwable e) {
        super(e);
    }
    
    public PgcnUncheckedException(String message, Throwable e) {
        super(message, e);
    }
}
