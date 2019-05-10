package fr.progilone.pgcn.security;

import org.springframework.data.domain.AuditorAware;
import org.springframework.stereotype.Component;

import fr.progilone.pgcn.config.Constants;


/**
 * Implementation of AuditorAware based on Spring Security.
 */
@Component
public class SpringSecurityAuditorAware implements AuditorAware<String> {

    @Override
    public String getCurrentAuditor() {
        final String userName = SecurityUtils.getCurrentLogin();
        return (userName != null ? userName : Constants.SYSTEM_ACCOUNT);
    }
}
