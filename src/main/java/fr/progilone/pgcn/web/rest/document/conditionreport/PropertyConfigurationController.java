package fr.progilone.pgcn.web.rest.document.conditionreport;

import com.codahale.metrics.annotation.Timed;
import fr.progilone.pgcn.domain.document.conditionreport.DescriptionProperty;
import fr.progilone.pgcn.domain.document.conditionreport.PropertyConfiguration;
import fr.progilone.pgcn.domain.dto.document.conditionreport.PropertyConfigurationDTO;
import fr.progilone.pgcn.domain.library.Library;
import fr.progilone.pgcn.domain.project.Project;
import fr.progilone.pgcn.exception.PgcnException;
import fr.progilone.pgcn.service.document.conditionreport.ui.UiPropertyConfigurationService;
import fr.progilone.pgcn.web.rest.AbstractRestController;
import fr.progilone.pgcn.web.util.AccessHelper;
import fr.progilone.pgcn.web.util.LibraryAccesssHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.security.RolesAllowed;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

import static fr.progilone.pgcn.web.rest.document.security.AuthorizationConstants.*;

@RestController
@RequestMapping(value = "/api/rest/condreport_prop_conf")
public class PropertyConfigurationController extends AbstractRestController {

    private final LibraryAccesssHelper libraryAccesssHelper;
    private final AccessHelper accessHelper;
    private final UiPropertyConfigurationService propertyConfigurationService;

    @Autowired
    public PropertyConfigurationController(final LibraryAccesssHelper libraryAccesssHelper,
                                           final AccessHelper accessHelper,
                                           final UiPropertyConfigurationService propertyConfigurationService) {
        this.libraryAccesssHelper = libraryAccesssHelper;
        this.accessHelper = accessHelper;
        this.propertyConfigurationService = propertyConfigurationService;
    }

    @RequestMapping(method = RequestMethod.POST)
    @Timed
    @RolesAllowed(COND_REPORT_HAB5)
    public ResponseEntity<PropertyConfigurationDTO> create(final HttpServletRequest request, @RequestBody final PropertyConfigurationDTO value) throws
                                                                                                                                                PgcnException {
        // Vérification des droits d'accès par rapport à la bibliothèque demandée
        if (!libraryAccesssHelper.checkLibrary(request, value.getLibrary().getIdentifier())) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        return new ResponseEntity<>(propertyConfigurationService.save(value), HttpStatus.CREATED);
    }

    @RequestMapping(method = RequestMethod.GET, params = {"library"}, produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @RolesAllowed({COND_REPORT_HAB0})
    public ResponseEntity<List<PropertyConfigurationDTO>> findByLibrary(final HttpServletRequest request,
                                                                        @RequestParam(name = "library") final Library library,
                                                                        @RequestParam(name = "project") final Project project) {
        // Vérification des droits d'accès par rapport à la bibliothèque demandée
        if (!libraryAccesssHelper.checkLibrary(request, library) && !accessHelper.checkProject(project.getIdentifier())) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        return createResponseEntity(propertyConfigurationService.findByLibrary(library));
    }

    @RequestMapping(method = RequestMethod.GET, params = {"desc", "library"}, produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @RolesAllowed({COND_REPORT_HAB0})
    public ResponseEntity<PropertyConfigurationDTO> findByDescPropertyAndLibrary(final HttpServletRequest request,
                                                                                 @RequestParam(name = "desc") final DescriptionProperty property,
                                                                                 @RequestParam(name = "library") final Library library) {
        // Vérification des droits d'accès par rapport à la bibliothèque demandée
        if (!libraryAccesssHelper.checkLibrary(request, library)) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        return createResponseEntity(propertyConfigurationService.findByDescPropertyAndLibrary(property, library));
    }

    @RequestMapping(method = RequestMethod.GET, params = {"internal", "library"}, produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @RolesAllowed({COND_REPORT_HAB0})
    public ResponseEntity<PropertyConfigurationDTO> findByInternalPropertyAndLibrary(final HttpServletRequest request,
                                                                                     @RequestParam(name = "internal")
                                                                                     final PropertyConfiguration.InternalProperty property,
                                                                                     @RequestParam(name = "library") final Library library) {
        // Vérification des droits d'accès par rapport à la bibliothèque demandée
        if (!libraryAccesssHelper.checkLibrary(request, library)) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        return createResponseEntity(propertyConfigurationService.findByInternalPropertyAndLibrary(property, library));
    }

    @RequestMapping(value = "/{identifier}", method = RequestMethod.POST)
    @Timed
    @RolesAllowed(COND_REPORT_HAB5)
    public ResponseEntity<PropertyConfigurationDTO> update(final HttpServletRequest request, @RequestBody final PropertyConfigurationDTO value) throws
                                                                                                                                                PgcnException {
        // Vérification des droits d'accès par rapport à la bibliothèque demandée
        if (!libraryAccesssHelper.checkLibrary(request, value.getLibrary().getIdentifier())) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        return new ResponseEntity<>(propertyConfigurationService.save(value), HttpStatus.OK);
    }
}
