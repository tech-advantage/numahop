package fr.progilone.pgcn.web.rest.checkconfiguration;

import static fr.progilone.pgcn.web.rest.checkconfiguration.security.AuthorizationConstants.*;
import static fr.progilone.pgcn.web.rest.user.security.AuthorizationConstants.*;

import com.codahale.metrics.annotation.Timed;
import fr.progilone.pgcn.domain.dto.checkconfiguration.AutomaticCheckRuleDTO;
import fr.progilone.pgcn.domain.dto.checkconfiguration.CheckConfigurationDTO;
import fr.progilone.pgcn.domain.dto.checkconfiguration.SimpleCheckConfigurationDTO;
import fr.progilone.pgcn.exception.PgcnException;
import fr.progilone.pgcn.service.checkconfiguration.AutomaticCheckRuleService;
import fr.progilone.pgcn.service.checkconfiguration.CheckConfigurationService;
import fr.progilone.pgcn.service.checkconfiguration.ui.UICheckConfigurationService;
import fr.progilone.pgcn.web.rest.AbstractRestController;
import fr.progilone.pgcn.web.util.LibraryAccesssHelper;
import jakarta.annotation.security.RolesAllowed;
import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/api/rest/checkconfiguration")
public class CheckConfigurationController extends AbstractRestController {

    private final CheckConfigurationService checkConfigurationService;
    private final UICheckConfigurationService uiCheckConfigurationService;
    private final AutomaticCheckRuleService automaticCheckRuleService;
    private final LibraryAccesssHelper libraryAccesssHelper;

    @Autowired
    public CheckConfigurationController(final CheckConfigurationService checkConfigurationService,
                                        final UICheckConfigurationService uiCheckConfigurationService,
                                        final AutomaticCheckRuleService automaticCheckRuleService,
                                        final LibraryAccesssHelper libraryAccesssHelper) {
        this.checkConfigurationService = checkConfigurationService;
        this.uiCheckConfigurationService = uiCheckConfigurationService;
        this.automaticCheckRuleService = automaticCheckRuleService;
        this.libraryAccesssHelper = libraryAccesssHelper;
    }

    @RequestMapping(method = RequestMethod.POST)
    @Timed
    @RolesAllowed({CHECK_HAB1})
    public ResponseEntity<CheckConfigurationDTO> create(final HttpServletRequest request, @RequestBody final CheckConfigurationDTO checkConfiguration) throws PgcnException {
        final CheckConfigurationDTO savedConfiguration = uiCheckConfigurationService.create(checkConfiguration);
        return new ResponseEntity<>(savedConfiguration, HttpStatus.CREATED);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    @Timed
    @RolesAllowed({CHECK_HAB2})
    public ResponseEntity<CheckConfigurationDTO> delete(final HttpServletRequest request, @PathVariable final String id) {
        checkConfigurationService.delete(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.GET, params = {"search"}, produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @RolesAllowed({CHECK_HAB0})
    public ResponseEntity<Page<SimpleCheckConfigurationDTO>> search(final HttpServletRequest request,
                                                                    @RequestParam(value = "search", required = false) final String search,
                                                                    @RequestParam(value = "libraries", required = false) final List<String> libraries,
                                                                    @RequestParam(value = "page", required = false, defaultValue = "0") final Integer page,
                                                                    @RequestParam(value = "size", required = false, defaultValue = "10") final Integer size) {
        final List<String> filteredLibraries = libraryAccesssHelper.getLibraryFilter(request, libraries);
        return new ResponseEntity<>(uiCheckConfigurationService.search(search, filteredLibraries, page, size), HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.GET, params = {"rules"}, produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @RolesAllowed({CHECK_HAB0})
    public ResponseEntity<List<AutomaticCheckRuleDTO>> getInitRules(final HttpServletRequest request) {
        return new ResponseEntity<>(automaticCheckRuleService.getInitRulesForConfiguration(), HttpStatus.OK);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @RolesAllowed({CHECK_HAB0})
    public ResponseEntity<CheckConfigurationDTO> getById(final HttpServletRequest request, @PathVariable final String id) {
        final CheckConfigurationDTO configuration = uiCheckConfigurationService.getOne(id);
        return createResponseEntity(configuration);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET, params = {"edition"}, produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @RolesAllowed({CHECK_HAB0})
    public ResponseEntity<CheckConfigurationDTO> getByIdForEdition(final HttpServletRequest request, @PathVariable final String id) {
        return createResponseEntity(uiCheckConfigurationService.getOneForEdition(id));
    }

    @RequestMapping(method = RequestMethod.GET, params = {"project"}, produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @RolesAllowed({CHECK_HAB0})
    public ResponseEntity<List<SimpleCheckConfigurationDTO>> getByProjectId(final HttpServletRequest request, @RequestParam(value = "project") final String projectId) {
        final List<SimpleCheckConfigurationDTO> configuration = uiCheckConfigurationService.getAllByProjectId(projectId);
        return createResponseEntity(configuration);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET, params = {"duplicate"}, produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @RolesAllowed({USER_HAB0})
    public ResponseEntity<CheckConfigurationDTO> duplicateCheckConfiguration(final HttpServletRequest request, @PathVariable final String id) {
        return new ResponseEntity<>(uiCheckConfigurationService.duplicateCheckConfiguration(id), HttpStatus.OK);
    }

    @RequestMapping(value = "/{idDocUnit}", method = RequestMethod.GET, params = {"docUnit"}, produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @RolesAllowed({USER_HAB0})
    public ResponseEntity<CheckConfigurationDTO> getByDocUnit(final HttpServletRequest request, @PathVariable final String idDocUnit) {
        return new ResponseEntity<>(uiCheckConfigurationService.duplicateCheckConfiguration(idDocUnit), HttpStatus.OK);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.POST)
    @Timed
    @RolesAllowed({CHECK_HAB1})
    public ResponseEntity<CheckConfigurationDTO> update(final HttpServletRequest request, @RequestBody final CheckConfigurationDTO configuration) throws PgcnException {
        if (!libraryAccesssHelper.checkLibrary(request, configuration.getLibrary().getIdentifier())) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        final CheckConfigurationDTO savedConfiguration = uiCheckConfigurationService.update(configuration);
        return new ResponseEntity<>(savedConfiguration, HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.GET, params = {"add-radical-controle"})
    @Timed
    @RolesAllowed({CHECK_HAB1})
    public ResponseEntity<CheckConfigurationDTO> updateCheckConfigurationAddRadicalControl() throws PgcnException {
        checkConfigurationService.updateCheckConfigurationAddRadicalControl();
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
