package fr.progilone.pgcn.security;

import fr.progilone.pgcn.config.Constants;
import java.util.Optional;
import org.springframework.data.domain.AuditorAware;
import org.springframework.stereotype.Component;

/**
 * Implementation of AuditorAware based on Spring Security.
 */
@Component
public class SpringSecurityAuditorAware implements AuditorAware<String> {

    @Override
    public Optional<String> getCurrentAuditor() {
        final String userName = SecurityUtils.getCurrentLogin();
        return Optional.of(userName != null ? userName
                                            : Constants.SYSTEM_ACCOUNT);
    }
}
