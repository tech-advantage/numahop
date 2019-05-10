package fr.progilone.pgcn.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import fr.progilone.pgcn.domain.user.Lang;
import fr.progilone.pgcn.domain.util.CustomUserDetails;

/**
 * Utility class for Spring Security.
 */
public final class SecurityUtils {

    private SecurityUtils() {
    }

    /**
     * @return Get the login of the current user.
     */
    public static String getCurrentLogin() {
        final SecurityContext securityContext = SecurityContextHolder.getContext();
        final Authentication authentication = securityContext.getAuthentication();
        String userName = null;

        if (authentication != null) {
            if (authentication.getPrincipal() instanceof UserDetails) {
                final UserDetails springSecurityUser = (UserDetails) authentication.getPrincipal();
                userName = springSecurityUser.getUsername();
            } else if (authentication.getPrincipal() instanceof String) {
                userName = (String) authentication.getPrincipal();
            }
        }
        return userName;
    }

    /**
     * @return Get the current user.
     */
    public static CustomUserDetails getCurrentUser() {
        final SecurityContext securityContext = SecurityContextHolder.getContext();
        final Authentication authentication = securityContext.getAuthentication();

        if (authentication != null) {
            if (authentication.getPrincipal() instanceof CustomUserDetails) {
                return (CustomUserDetails) authentication.getPrincipal();
            }
        }
        return null;
    }

    /**
     * @return Récupère la langue de l'utilisateur connecté.
     */
    public static Lang getCurrentLanguage() {
        final SecurityContext securityContext = SecurityContextHolder.getContext();
        final Authentication authentication = securityContext.getAuthentication();
        Lang language = null;

        if (authentication != null) {
            if (authentication.getPrincipal() instanceof CustomUserDetails) {
                final CustomUserDetails springSecurityUser = (CustomUserDetails) authentication.getPrincipal();
                language = springSecurityUser.getLang();
            }
        }
        return language;
    }

    /**
     * @return Récupère l'identifiant de l'utilisateur connecté.
     */
    public static String getCurrentUserId() {
        final SecurityContext securityContext = SecurityContextHolder.getContext();
        final Authentication authentication = securityContext.getAuthentication();
        String userId = null;

        if (authentication != null) {
            if (authentication.getPrincipal() instanceof CustomUserDetails) {
                final CustomUserDetails springSecurityUser = (CustomUserDetails) authentication.getPrincipal();
                userId = springSecurityUser.getIdentifier();
            }
        }
        return userId;
    }

    /**
     * Change la langue de l'utilisateur connecté.
     *
     * @param lang
     */
    public static void setCurrentLanguage(final Lang lang) {
        final SecurityContext securityContext = SecurityContextHolder.getContext();
        final Authentication authentication = securityContext.getAuthentication();

        if (authentication != null) {
            if (authentication.getPrincipal() instanceof CustomUserDetails) {
                final CustomUserDetails springSecurityUser = (CustomUserDetails) authentication.getPrincipal();
                springSecurityUser.setLang(lang);
            }
        }
    }
}
