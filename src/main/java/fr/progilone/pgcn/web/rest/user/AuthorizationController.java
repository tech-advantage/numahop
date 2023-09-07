package fr.progilone.pgcn.web.rest.user;

import static fr.progilone.pgcn.web.rest.user.security.AuthorizationConstants.ROLE_HAB0;

import com.codahale.metrics.annotation.Timed;
import fr.progilone.pgcn.domain.dto.user.AuthorizationDTO;
import fr.progilone.pgcn.domain.user.Authorization;
import fr.progilone.pgcn.service.user.AuthorizationService;
import fr.progilone.pgcn.web.rest.AbstractRestController;
import jakarta.annotation.security.RolesAllowed;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/api/rest/authorization")
@RolesAllowed({ROLE_HAB0})
public class AuthorizationController extends AbstractRestController {

    private final AuthorizationService authorizationService;

    @Autowired
    public AuthorizationController(final AuthorizationService authorizationService) {
        super();
        this.authorizationService = authorizationService;
    }

    @RequestMapping(method = RequestMethod.GET, params = {"dto"}, produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<AuthorizationDTO>> findAllDto() {
        return new ResponseEntity<>(authorizationService.findAllDTO(), HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<Authorization>> findAll() {
        return new ResponseEntity<>(authorizationService.findAll(), HttpStatus.OK);
    }

    @RequestMapping(value = "/{identifier}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Authorization> getById(@PathVariable final String identifier) {
        return createResponseEntity(authorizationService.findOne(identifier));
    }
}
