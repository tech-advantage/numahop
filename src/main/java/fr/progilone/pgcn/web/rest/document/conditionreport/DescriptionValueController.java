package fr.progilone.pgcn.web.rest.document.conditionreport;

import com.codahale.metrics.annotation.Timed;
import fr.progilone.pgcn.domain.document.conditionreport.DescriptionProperty;
import fr.progilone.pgcn.domain.document.conditionreport.DescriptionValue;
import fr.progilone.pgcn.exception.PgcnException;
import fr.progilone.pgcn.service.document.conditionreport.DescriptionValueService;
import fr.progilone.pgcn.web.rest.AbstractRestController;
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

import javax.annotation.security.RolesAllowed;
import java.util.List;

import static fr.progilone.pgcn.web.rest.document.security.AuthorizationConstants.*;

@RestController
@RequestMapping(value = "/api/rest/condreport_desc_value")
public class DescriptionValueController extends AbstractRestController {

    private final DescriptionValueService descBindingValueService;

    @Autowired
    public DescriptionValueController(final DescriptionValueService descBindingValueService) {
        this.descBindingValueService = descBindingValueService;
    }

    @RequestMapping(method = RequestMethod.POST)
    @Timed
    @RolesAllowed(COND_REPORT_HAB6)
    public ResponseEntity<DescriptionValue> create(@RequestBody final DescriptionValue value) throws PgcnException {
        return new ResponseEntity<>(descBindingValueService.save(value), HttpStatus.CREATED);
    }

    @RequestMapping(value = "/{identifier}", method = RequestMethod.DELETE)
    @ResponseStatus(HttpStatus.OK)
    @Timed
    @RolesAllowed(COND_REPORT_HAB6)
    public void delete(@PathVariable final String identifier) {
        descBindingValueService.delete(identifier);
    }

    @RequestMapping(method = RequestMethod.GET, params = {"property"}, produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @RolesAllowed({COND_REPORT_HAB0, COND_REPORT_HAB6})
    public ResponseEntity<List<DescriptionValue>> findByProperty(@RequestParam(name = "property") final DescriptionProperty property) {
        return createResponseEntity(descBindingValueService.findByProperty(property));
    }

    @RequestMapping(value = "/{identifier}", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.OK)
    @Timed
    @RolesAllowed(COND_REPORT_HAB6)
    public ResponseEntity<DescriptionValue> update(@RequestBody final DescriptionValue value) throws PgcnException {
        return new ResponseEntity<>(descBindingValueService.save(value), HttpStatus.OK);
    }
}
