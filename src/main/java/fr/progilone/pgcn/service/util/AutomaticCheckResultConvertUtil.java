package fr.progilone.pgcn.service.util;

import fr.progilone.pgcn.domain.check.AutomaticCheckResult;
import fr.progilone.pgcn.domain.check.AutomaticCheckResult.AutoCheckResult;
import fr.progilone.pgcn.domain.jaxb.facile.ValidatorType;

/**
 * Converti une réponse de contrôle automatique en une réponse au bon format
 */
public final class AutomaticCheckResultConvertUtil {

    private AutomaticCheckResultConvertUtil() {
    }

    public static AutomaticCheckResult convert(ValidatorType response) {
        if (response == null)
            return null;
        AutomaticCheckResult result = new AutomaticCheckResult();
        if (response.isValid()) {
            result.setResult(AutoCheckResult.OK);
        } else {
            result.setResult(AutoCheckResult.KO);
            result.setMessage(response.getMessage());
        }
        return result;
    }
}
