package fr.progilone.pgcn.web.rest.document;

import static fr.progilone.pgcn.web.rest.delivery.security.AuthorizationConstants.DEL_HAB0;
import static fr.progilone.pgcn.web.rest.delivery.security.AuthorizationConstants.DEL_HAB2;

import java.util.Optional;

import javax.annotation.security.RolesAllowed;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.codahale.metrics.annotation.Timed;

import fr.progilone.pgcn.domain.document.CheckSlipConfiguration;
import fr.progilone.pgcn.exception.PgcnException;
import fr.progilone.pgcn.service.document.CheckSlipConfigurationService;
import fr.progilone.pgcn.web.rest.AbstractRestController;
import fr.progilone.pgcn.web.util.AccessHelper;
import fr.progilone.pgcn.web.util.LibraryAccesssHelper;

@RestController
@RequestMapping(value = "/api/rest/checkslip_configuration")
public class CheckSlipConfigurationController extends AbstractRestController {

    private final CheckSlipConfigurationService confService;
    private final AccessHelper accessHelper;
    private final LibraryAccesssHelper libraryAccesssHelper;
    
    @Autowired
    public CheckSlipConfigurationController(final CheckSlipConfigurationService confService,
                                            final AccessHelper accessHelper,
                                            final LibraryAccesssHelper libraryAccesssHelper) {
        this.confService = confService;
        this.accessHelper = accessHelper;
        this.libraryAccesssHelper = libraryAccesssHelper;
    }
    
    
    @RolesAllowed(DEL_HAB0)
    @RequestMapping(value = "/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<CheckSlipConfiguration> getById(final HttpServletRequest request,
                                                      @PathVariable final String id) {
        if(!libraryAccesssHelper.checkLibrary(request, id)) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        final Optional<CheckSlipConfiguration> checkSlipConfiguration = confService.getOneByLibrary(id);
        return createResponseEntity(checkSlipConfiguration.get());
    }
    
    @RolesAllowed({DEL_HAB2})
    @RequestMapping(value = "/{id}", method = RequestMethod.POST)
    @Timed
    public ResponseEntity<CheckSlipConfiguration> update(final HttpServletRequest request,
                                              @RequestBody final CheckSlipConfiguration checkSlipConfiguration) throws PgcnException {

        if(!libraryAccesssHelper.checkLibrary(request, checkSlipConfiguration.getLibrary())) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        return createResponseEntity(confService.update(checkSlipConfiguration));
    }

}
