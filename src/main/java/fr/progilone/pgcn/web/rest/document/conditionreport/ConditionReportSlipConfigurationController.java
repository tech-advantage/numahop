package fr.progilone.pgcn.web.rest.document.conditionreport;

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

import fr.progilone.pgcn.domain.document.conditionreport.ConditionReportSlipConfiguration;
import fr.progilone.pgcn.exception.PgcnException;
import fr.progilone.pgcn.service.document.conditionreport.ConditionReportSlipConfigurationService;
import fr.progilone.pgcn.web.rest.AbstractRestController;
import fr.progilone.pgcn.web.util.LibraryAccesssHelper;

@RestController
@RequestMapping(value = "/api/rest/condreportslip_configuration")
public class ConditionReportSlipConfigurationController extends AbstractRestController {

    private final ConditionReportSlipConfigurationService confService;
    private final LibraryAccesssHelper libraryAccesssHelper;
    
    @Autowired
    public ConditionReportSlipConfigurationController(final ConditionReportSlipConfigurationService confService,
                                            final LibraryAccesssHelper libraryAccesssHelper) {
        this.confService = confService;
        this.libraryAccesssHelper = libraryAccesssHelper;
    }
    
    
    @RolesAllowed(DEL_HAB0)
    @RequestMapping(value = "/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<ConditionReportSlipConfiguration> getById(final HttpServletRequest request,
                                                                    @PathVariable final String id) {
        if(!libraryAccesssHelper.checkLibrary(request, id)) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        Optional<ConditionReportSlipConfiguration> condSlipConfiguration = confService.getOneByLibrary(id);
        return createResponseEntity(condSlipConfiguration);
    }


    @RolesAllowed({DEL_HAB2})
    @RequestMapping(value = "/{id}", method = RequestMethod.POST)
    @Timed
    public ResponseEntity<ConditionReportSlipConfiguration> update(final HttpServletRequest request,
                                              @RequestBody final ConditionReportSlipConfiguration condSlipConfiguration) throws PgcnException {

        if(!libraryAccesssHelper.checkLibrary(request, condSlipConfiguration.getLibrary())) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }

        return createResponseEntity(confService.update(condSlipConfiguration));
    }
    
}
