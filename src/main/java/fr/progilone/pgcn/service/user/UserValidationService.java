package fr.progilone.pgcn.service.user;

import fr.progilone.pgcn.domain.user.User;
import fr.progilone.pgcn.exception.PgcnValidationException;
import fr.progilone.pgcn.exception.message.PgcnError;
import fr.progilone.pgcn.exception.message.PgcnErrorCode;
import fr.progilone.pgcn.exception.message.PgcnList;
import fr.progilone.pgcn.repository.user.UserRepository;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.validator.routines.EmailValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserValidationService {

    @Autowired
    private UserRepository userRepository;

    @Transactional(readOnly = true, propagation = Propagation.REQUIRES_NEW)
    public PgcnList<PgcnError> validate(final User user) throws PgcnValidationException {
        final PgcnList<PgcnError> errors = new PgcnList<>();

        final PgcnError.Builder builder = new PgcnError.Builder();

        // Le login est obligatoire
        if (StringUtils.isBlank(user.getLogin())) {
            // Le login est obligatoire
            errors.add(builder.reinit().setCode(PgcnErrorCode.USER_LOGIN_MANDATORY).setField("login").build());
        }
        // Le login est unique
        else {
            final User duplicate = userRepository.findByLogin(user.getLogin());
            if (duplicate != null && (user.getIdentifier() == null || !duplicate.getIdentifier().equalsIgnoreCase(user.getIdentifier()))) {
                errors.add(builder.reinit().setCode(PgcnErrorCode.USER_DUPLICATE_LOGIN).setField("login").build());
            }
        }

        // La bibliothèque est obligatoire
        if (user.getLibrary() == null) {
            errors.add(builder.reinit().setCode(PgcnErrorCode.USER_LIBRARY_MANDATORY).setField("library").build());
        }
        // Le profil est obligatoire
        if (user.getRole() == null) {
            errors.add(builder.reinit().setCode(PgcnErrorCode.USER_ROLE_MANDATORY).setField("role").build());
        }
        // Le mail doit être renseigné
        if (StringUtils.isBlank(user.getEmail())) {
            errors.add(builder.reinit().setCode(PgcnErrorCode.USER_EMAIL_MANDATORY).setField("email").build());
        } else {
            // Validation du mail
            if (!EmailValidator.getInstance().isValid(user.getEmail())) {
                errors.add(builder.reinit().setCode(PgcnErrorCode.USER_EMAIL_INVALID).setField("email").build());
            }
        }

        // Retour
        if (!errors.isEmpty()) {
            user.setErrors(errors);
            throw new PgcnValidationException(user, errors);
        }
        return errors;
    }
}
