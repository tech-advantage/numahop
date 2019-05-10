package fr.progilone.pgcn.exception.dto;

import java.util.ArrayList;
import java.util.List;

import fr.progilone.pgcn.exception.PgcnExceptionLevel;
import fr.progilone.pgcn.exception.message.PgcnError;

/**
 * Created by Progilone_2 on 05/01/2016.
 */
public class PgcnBusinessExceptionDTO {

    private PgcnExceptionLevel level;
    private final List<PgcnError> errors = new ArrayList<>();

    public PgcnBusinessExceptionDTO(final PgcnExceptionLevel level, List<PgcnError> errors) {
        this.level = level;
        this.errors.clear();
        if(errors != null) {
            this.errors.addAll(errors);
        }
    }

    public PgcnExceptionLevel getLevel() {
        return level;
    }

    public void setLevel(final PgcnExceptionLevel level) {
        this.level = level;
    }

    public List<PgcnError> getErrors() {
        return errors;
    }


}
