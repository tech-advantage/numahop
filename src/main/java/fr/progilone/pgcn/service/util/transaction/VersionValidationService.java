package fr.progilone.pgcn.service.util.transaction;

import fr.progilone.pgcn.domain.AbstractDomainObject;
import fr.progilone.pgcn.domain.dto.AbstractVersionedDTO;
import org.springframework.orm.ObjectOptimisticLockingFailureException;

/**
 * Service permettant de gérer les versions à la main.
 * Cas du mapping DTO / Domain Object en accès concurrents
 *
 * @author jbrunet
 *         Créé le 6 juil. 2017
 */
public class VersionValidationService {

    private VersionValidationService() {
    }

    public static void checkForStateObject(AbstractDomainObject object, AbstractVersionedDTO dto) {
        if (object.getVersion() != dto.getVersion()) {
            throw new ObjectOptimisticLockingFailureException(object.getClass(), object.getIdentifier());
        }
    }
}
