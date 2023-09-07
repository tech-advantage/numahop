package fr.progilone.pgcn.web.rest.user;

import static fr.progilone.pgcn.web.rest.library.security.AuthorizationConstants.LIB_HAB5;
import static fr.progilone.pgcn.web.rest.user.security.AuthorizationConstants.*;

import com.codahale.metrics.annotation.Timed;
import fr.progilone.pgcn.domain.dto.user.RoleDTO;
import fr.progilone.pgcn.domain.user.Role;
import fr.progilone.pgcn.exception.PgcnException;
import fr.progilone.pgcn.exception.PgcnValidationException;
import fr.progilone.pgcn.service.user.RoleService;
import fr.progilone.pgcn.web.rest.AbstractRestController;
import jakarta.annotation.security.RolesAllowed;
import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/api/rest/role")
public class RoleController extends AbstractRestController {

    private final RoleService roleService;

    @Autowired
    public RoleController(final RoleService roleService) {
        this.roleService = roleService;
    }

    @RequestMapping(method = RequestMethod.POST)
    @Timed
    @RolesAllowed(ROLE_HAB1)
    public ResponseEntity<Role> create(final HttpServletRequest request, @RequestBody final Role role) throws PgcnException {
        return new ResponseEntity<>(roleService.save(role), HttpStatus.CREATED);
    }

    @RequestMapping(value = "/{identifier}", method = RequestMethod.DELETE)
    @ResponseStatus(HttpStatus.OK)
    @Timed
    @RolesAllowed(ROLE_HAB3)
    public void delete(@PathVariable final String identifier) throws PgcnValidationException {
        roleService.delete(identifier);
    }

    @RequestMapping(method = RequestMethod.GET, params = {"search"}, produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @RolesAllowed({USER_HAB0})
    public ResponseEntity<List<RoleDTO>> search(final HttpServletRequest request,
                                                @RequestParam(value = "search") final String search,
                                                @RequestParam(value = "authorizations", required = false) final List<String> authorizations) {
        return new ResponseEntity<>(roleService.search(search, authorizations), HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.GET, params = {"dto"}, produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @RolesAllowed({ROLE_HAB0,
                   USER_HAB0,
                   LIB_HAB5})
    public ResponseEntity<List<RoleDTO>> findAll(@RequestParam("dto") final Boolean dto) {
        return new ResponseEntity<>(roleService.findAllDTO(), HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @RolesAllowed({ROLE_HAB0,
                   LIB_HAB5})
    public ResponseEntity<List<Role>> findAll() {
        return new ResponseEntity<>(roleService.findAll(), HttpStatus.OK);
    }

    @RequestMapping(value = "/{identifier}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @RolesAllowed(ROLE_HAB0)
    public ResponseEntity<Role> getById(@PathVariable final String identifier) {
        return createResponseEntity(roleService.findOne(identifier));
    }

    @RequestMapping(value = "/{identifier}", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.OK)
    @Timed
    @RolesAllowed(ROLE_HAB2)
    public ResponseEntity<Role> udpate(final HttpServletRequest request, @RequestBody final Role role) throws PgcnException {
        return new ResponseEntity<>(roleService.save(role), HttpStatus.OK);
    }
}
