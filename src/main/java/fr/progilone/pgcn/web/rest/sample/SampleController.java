package fr.progilone.pgcn.web.rest.sample;

import static fr.progilone.pgcn.web.rest.delivery.security.AuthorizationConstants.DEL_HAB0;

import com.codahale.metrics.annotation.Timed;
import fr.progilone.pgcn.domain.dto.sample.SampleDTO;
import fr.progilone.pgcn.service.sample.SampleService;
import jakarta.annotation.security.RolesAllowed;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/api/rest/sample")
public class SampleController {

    private final SampleService sampleService;

    public SampleController(final SampleService sampleService) {
        this.sampleService = sampleService;
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @RolesAllowed(DEL_HAB0)
    public ResponseEntity<SampleDTO> getSample(@PathVariable final String id) {

        final SampleDTO sample = sampleService.getOne(id);
        return new ResponseEntity<>(sample, HttpStatus.OK);
    }

}
