package fr.progilone.pgcn.security;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AbstractAuthenticationTargetUrlRequestHandler;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.stereotype.Component;

import fr.progilone.pgcn.domain.util.CustomUserDetails;
import fr.progilone.pgcn.service.LockService;

/**
 * Spring Security logout handler, specialized for Ajax requests.
 */
@Component
public class AjaxLogoutSuccessHandler extends AbstractAuthenticationTargetUrlRequestHandler
        implements LogoutSuccessHandler {
    
    private final LockService lockService;
    
    @Autowired
    public AjaxLogoutSuccessHandler(final LockService lockService) {
        this.lockService = lockService;
    }

    @Override
    public void onLogoutSuccess(final HttpServletRequest request, 
                                final HttpServletResponse response,
                                final Authentication authentication)
                                                            throws IOException, ServletException {        
        // Libération des locks posés par ce user.
        final CustomUserDetails user = (CustomUserDetails)authentication.getPrincipal();
        if (user != null) {
            lockService.releaseLocksOnLogout(user.getLogin());
        }
        response.setStatus(HttpServletResponse.SC_OK);
    }
}