package fr.progilone.pgcn.web.rest.delivery;

import static fr.progilone.pgcn.web.rest.delivery.security.AuthorizationConstants.DEL_HAB0;
import static fr.progilone.pgcn.web.rest.delivery.security.AuthorizationConstants.DEL_HAB2;

import java.util.Optional;

import javax.annotation.security.RolesAllowed;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

import fr.progilone.pgcn.domain.delivery.DeliverySlipConfiguration;
import fr.progilone.pgcn.exception.PgcnException;
import fr.progilone.pgcn.service.delivery.DeliveryConfigurationService;
import fr.progilone.pgcn.web.rest.AbstractRestController;
import fr.progilone.pgcn.web.util.AccessHelper;
import fr.progilone.pgcn.web.util.LibraryAccesssHelper;

@RestController
@RequestMapping(value = "/api/rest/delivery_configuration")
public class DeliveryConfigurationController extends AbstractRestController {

    private static final Logger LOG = LoggerFactory.getLogger(DeliveryConfigurationController.class);

    private final DeliveryConfigurationService deliveryConfigurationService;
    private final AccessHelper accessHelper;
    private final LibraryAccesssHelper libraryAccesssHelper;

    @Autowired
    public DeliveryConfigurationController(final DeliveryConfigurationService deliveryConfigurationService,
                                           final AccessHelper accessHelper,
                                           final LibraryAccesssHelper libraryAccesssHelper) {
        this.deliveryConfigurationService = deliveryConfigurationService;
        this.accessHelper = accessHelper;
        this.libraryAccesssHelper = libraryAccesssHelper;
    }


    @RolesAllowed(DEL_HAB0)
    @RequestMapping(value = "/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<DeliverySlipConfiguration> getById(final HttpServletRequest request,
                                                      @PathVariable final String id) {
        if(!libraryAccesssHelper.checkLibrary(request, id)) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        Optional<DeliverySlipConfiguration> deliverySlipConfiguration = deliveryConfigurationService.getOneByLibrary(id);
        return createResponseEntity(deliveryConfigurationService.getOneByLibrary(id));
    }


    @RolesAllowed({DEL_HAB2})
    @RequestMapping(value = "/{id}", method = RequestMethod.POST)
    @Timed
    public ResponseEntity<DeliverySlipConfiguration> update(final HttpServletRequest request,
                                              @RequestBody final DeliverySlipConfiguration deliverySlipConfiguration) throws PgcnException {

        if(!libraryAccesssHelper.checkLibrary(request, deliverySlipConfiguration.getLibrary())) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }

        return createResponseEntity(deliveryConfigurationService.update(deliverySlipConfiguration));
    }
}
