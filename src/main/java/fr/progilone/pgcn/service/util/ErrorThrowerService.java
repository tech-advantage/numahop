package fr.progilone.pgcn.service.util;

import java.util.List;

import fr.progilone.pgcn.domain.ObjectWithErrors;
import fr.progilone.pgcn.exception.PgcnValidationException;
import fr.progilone.pgcn.exception.message.PgcnError;
import fr.progilone.pgcn.exception.message.PgcnErrorCode;
import fr.progilone.pgcn.exception.message.PgcnList;

/**
 * Lancement des erreurs
 *
 * @author jbrunet
 * Créé le 10 juil. 2017
 */
public final class ErrorThrowerService {

    private ErrorThrowerService() {
    }

    public static void addAndThrow(ObjectWithErrors object, List<PgcnErrorCode> errorCodes) throws PgcnValidationException {
        final PgcnError.Builder builder = new PgcnError.Builder();
        final PgcnList<PgcnError> errors = new PgcnList<>();
        errorCodes.forEach(errorCode -> {
            final PgcnError error = builder
                    .reinit()
                    .setCode(errorCode)
                    .build();
            errors.add(error);
        });
        object.setErrors(errors);
        throw new PgcnValidationException(object, errors);
    }
}
