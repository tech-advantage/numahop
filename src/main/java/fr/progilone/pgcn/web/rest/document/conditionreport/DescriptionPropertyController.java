package fr.progilone.pgcn.web.rest.document.conditionreport;

import com.codahale.metrics.annotation.Timed;
import fr.progilone.pgcn.domain.document.conditionreport.DescriptionProperty;
import fr.progilone.pgcn.exception.PgcnException;
import fr.progilone.pgcn.service.document.conditionreport.DescriptionPropertyService;
import fr.progilone.pgcn.web.rest.AbstractRestController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.security.RolesAllowed;
import java.util.List;

import static fr.progilone.pgcn.web.rest.document.security.AuthorizationConstants.*;

@RestController
@RequestMapping(value = "/api/rest/condreport_desc_prop")
public class DescriptionPropertyController extends AbstractRestController {

    private final DescriptionPropertyService descriptionPropertyService;

    @Autowired
    public DescriptionPropertyController(final DescriptionPropertyService descriptionPropertyService) {
        this.descriptionPropertyService = descriptionPropertyService;
    }

    @RequestMapping(method = RequestMethod.POST)
    @Timed
    @RolesAllowed(COND_REPORT_HAB6)
    public ResponseEntity<DescriptionProperty> create(@RequestBody final DescriptionProperty property) throws PgcnException {
        return new ResponseEntity<>(descriptionPropertyService.save(property), HttpStatus.CREATED);
    }

    @RequestMapping(value = "/{identifier}", method = RequestMethod.DELETE)
    @ResponseStatus(HttpStatus.OK)
    @Timed
    @RolesAllowed(COND_REPORT_HAB6)
    public void delete(@PathVariable final String identifier) {
        descriptionPropertyService.delete(identifier);
    }

    @RequestMapping(method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @RolesAllowed({COND_REPORT_HAB0, COND_REPORT_HAB6})
    public ResponseEntity<List<DescriptionProperty>> findAll() {
        return createResponseEntity(descriptionPropertyService.findAll());
    }

    @RequestMapping(value = "/{identifier}", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.OK)
    @Timed
    @RolesAllowed(COND_REPORT_HAB6)
    public ResponseEntity<DescriptionProperty> update(@RequestBody final DescriptionProperty value) throws PgcnException {
        return new ResponseEntity<>(descriptionPropertyService.save(value), HttpStatus.OK);
    }
}
