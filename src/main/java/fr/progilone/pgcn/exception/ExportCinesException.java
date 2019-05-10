package fr.progilone.pgcn.exception;

/**
 * Classe exception métier.
 */
public class ExportCinesException extends Exception {

    private String message;

    public ExportCinesException(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
