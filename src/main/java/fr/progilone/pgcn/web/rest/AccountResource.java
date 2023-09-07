package fr.progilone.pgcn.web.rest;

import com.codahale.metrics.annotation.Timed;
import fr.progilone.pgcn.domain.dto.user.SimpleUserAccountDTO;
import fr.progilone.pgcn.service.user.UserService;
import fr.progilone.pgcn.service.user.ui.UIUserService;
import jakarta.servlet.http.HttpServletRequest;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller for managing the current user's account.
 */
@RestController
@RequestMapping("/api")
public class AccountResource {

    private static final Logger LOG = LoggerFactory.getLogger(AccountResource.class);

    private final UserService userService;
    private final UIUserService uiUserService;

    @Autowired
    public AccountResource(final UserService userService, final UIUserService uiUserService) {
        super();
        this.userService = userService;
        this.uiUserService = uiUserService;
    }

    /**
     * GET /rest/authenticate -> check if the user is authenticated, and return its login.
     */
    @RequestMapping(value = "/rest/authenticate", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public String isAuthenticated(final HttpServletRequest request) {
        LOG.debug("REST request to check if the current user is authenticated");
        return request.getRemoteUser();
    }

    /**
     * GET /rest/account -> get the current user.
     */
    @RequestMapping(value = "/rest/account", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<SimpleUserAccountDTO> getAccount() {
        return Optional.ofNullable(uiUserService.getCurrentUserWithAuthoritiesAndDashboard())
                       .map(user -> new ResponseEntity<>(user, HttpStatus.OK))
                       .orElse(new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR));
    }

    @RequestMapping(value = "/rest/account/change_password", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    @Timed
    public void changePassword(@RequestBody final String password) {
        userService.changeCurrentUserPassword(password);
    }

    @RequestMapping(value = "/rest/reset", method = RequestMethod.POST)
    @Timed
    public ResponseEntity<?> resetPassword(final HttpServletRequest request, @RequestBody final String username) {

        if (userService.resetPassword(username)) {
            return new ResponseEntity<>(HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

    }
}
