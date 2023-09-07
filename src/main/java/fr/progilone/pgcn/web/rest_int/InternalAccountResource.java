package fr.progilone.pgcn.web.rest_int;

import com.codahale.metrics.annotation.Timed;
import fr.progilone.pgcn.domain.security.PersistentToken;
import fr.progilone.pgcn.repository.security.PersistentTokenRepository;
import fr.progilone.pgcn.security.SecurityUtils;
import fr.progilone.pgcn.web.rest.administration.security.AuthorizationConstants;
import fr.progilone.pgcn.web.rest_int.dto.UserAccountDTO;
import jakarta.annotation.security.RolesAllowed;
import jakarta.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Collections;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller for managing the current user's account.
 */
@RestController
@RequestMapping("/api_int")
public class InternalAccountResource {

    private final Logger LOG = LoggerFactory.getLogger(InternalAccountResource.class);

    private final PersistentTokenRepository persistentTokenRepository;

    @Value("${admin.login}")
    private String adminLogin;

    @Autowired
    public InternalAccountResource(final PersistentTokenRepository persistentTokenRepository) {
        this.persistentTokenRepository = persistentTokenRepository;
    }

    /**
     * GET /authenticate -> check if the user is authenticated, and return its login.
     */
    @RequestMapping(value = "/authenticate", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public String isAuthenticated(final HttpServletRequest request) {
        LOG.debug("REST request to check if the current user is authenticated");
        return request.getRemoteUser();
    }

    /**
     * Retourne un utilisateur ayant le role ADMIN si l'utilisateur est connecté avec le compte admin de l'application, rien sinon.
     */
    @RequestMapping(value = "/account", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @RolesAllowed(AuthorizationConstants.SUPER_ADMIN)
    public ResponseEntity<UserAccountDTO> getAccount() {
        final String currentLogin = SecurityUtils.getCurrentLogin();
        if (StringUtils.equals(currentLogin, adminLogin)) {
            return new ResponseEntity<>(new UserAccountDTO(currentLogin, null, "Admin", "Pgcn", null, null, Collections.singletonList("ROLE_ADMIN")), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
    }

    /**
     * GET /account/sessions -> get the current open sessions.
     */
    @RolesAllowed(AuthorizationConstants.SUPER_ADMIN)
    @RequestMapping(value = "/account/sessions", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<PersistentToken>> getCurrentSessions() {
        return new ResponseEntity<>(persistentTokenRepository.findByAllWithUser(), HttpStatus.OK);
    }

    @RolesAllowed(AuthorizationConstants.SUPER_ADMIN)
    @RequestMapping(value = "/account/sessions/{series}", method = RequestMethod.DELETE)
    @Timed
    public void invalidateSession(@PathVariable final String series) throws UnsupportedEncodingException {
        final String decodedSeries = URLDecoder.decode(series, "UTF-8");
        persistentTokenRepository.deleteById(decodedSeries);
    }
}
