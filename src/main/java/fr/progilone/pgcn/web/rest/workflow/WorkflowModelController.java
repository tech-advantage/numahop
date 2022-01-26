package fr.progilone.pgcn.web.rest.workflow;

import static fr.progilone.pgcn.web.rest.workflow.security.AuthorizationConstants.WORKFLOW_HAB1;
import static fr.progilone.pgcn.web.rest.workflow.security.AuthorizationConstants.WORKFLOW_HAB2;
import static fr.progilone.pgcn.web.rest.workflow.security.AuthorizationConstants.WORKFLOW_HAB3;
import static fr.progilone.pgcn.web.rest.workflow.security.AuthorizationConstants.WORKFLOW_HAB4;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.security.RolesAllowed;
import javax.servlet.http.HttpServletRequest;

import fr.progilone.pgcn.web.util.AccessHelper;
import org.apache.commons.lang.StringUtils;
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

import com.codahale.metrics.annotation.Timed;

import fr.progilone.pgcn.domain.dto.workflow.SimpleWorkflowModelDTO;
import fr.progilone.pgcn.domain.dto.workflow.WorkflowModelDTO;
import fr.progilone.pgcn.exception.PgcnException;
import fr.progilone.pgcn.service.workflow.WorkflowModelService;
import fr.progilone.pgcn.service.workflow.ui.UIWorkflowModelService;
import fr.progilone.pgcn.web.rest.AbstractRestController;
import fr.progilone.pgcn.web.util.LibraryAccesssHelper;

@RestController
@RequestMapping(value = "/api/rest/workflow_model")
public class WorkflowModelController extends AbstractRestController {

    private final WorkflowModelService service;
    private final UIWorkflowModelService uiService;
    private final LibraryAccesssHelper libraryAccesssHelper;
    private final AccessHelper accessHelper;

    @Autowired
    public WorkflowModelController(final WorkflowModelService service,
                                   final UIWorkflowModelService uiService,
                                   final LibraryAccesssHelper libraryAccesssHelper,
                                   final AccessHelper accessHelper) {
        this.service = service;
        this.uiService = uiService;
        this.libraryAccesssHelper = libraryAccesssHelper;
        this.accessHelper = accessHelper;
    }

    @RequestMapping(method = RequestMethod.POST)
    @Timed
    @RolesAllowed({WORKFLOW_HAB1})
    public ResponseEntity<WorkflowModelDTO> create(final HttpServletRequest request,
                                                      @RequestBody final WorkflowModelDTO dto) throws PgcnException {
        if (dto.getLibrary() != null && !libraryAccesssHelper.checkLibrary(request, dto.getLibrary().getIdentifier())) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        final WorkflowModelDTO saved = uiService.create(dto);
        return new ResponseEntity<>(saved, HttpStatus.CREATED);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    @Timed
    @RolesAllowed({WORKFLOW_HAB3})
    public ResponseEntity<?> delete(final HttpServletRequest request, @PathVariable final String id) {
        service.delete(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.GET, params = {"search"}, produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @RolesAllowed({WORKFLOW_HAB4})
    public ResponseEntity<Page<SimpleWorkflowModelDTO>> search(final HttpServletRequest request,
                                                                  @RequestParam(value = "search", required = false) final String search,
                                                                  @RequestParam(value = "initiale", required = false) final String initiale,
                                                                  @RequestParam(value = "libraries", required = false) final List<String> libraries,
                                                                  @RequestParam(value = "page", required = false, defaultValue = "0") final Integer page,
                                                                  @RequestParam(value = "size", required = false, defaultValue = "10") final Integer size,
                                                                  @RequestParam(value = "sorts", required = false) final List<String> sorts)
    {
        List<String> filteredLibraries = new ArrayList<>();
        if(libraries != null && !libraries.isEmpty()) {
            filteredLibraries = libraries.stream().filter(lib -> libraryAccesssHelper.checkLibrary(request, lib)).collect(Collectors.toList());
        }
        return new ResponseEntity<>(uiService.search(search, initiale, filteredLibraries, page, size, sorts), HttpStatus.OK);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @RolesAllowed({WORKFLOW_HAB4})
    public ResponseEntity<WorkflowModelDTO> getById(final HttpServletRequest request, @PathVariable final String id) {
        final WorkflowModelDTO model = uiService.getOne(id);
        return createResponseEntity(model);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.POST)
    @Timed
    @RolesAllowed({WORKFLOW_HAB2})
    public ResponseEntity<WorkflowModelDTO> update(final HttpServletRequest request,
                                                      @RequestBody final WorkflowModelDTO dto) throws PgcnException {
        if (dto.getLibrary() != null && !libraryAccesssHelper.checkLibrary(request, dto.getLibrary().getIdentifier())) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        final WorkflowModelDTO saved = uiService.update(dto);
        return new ResponseEntity<>(saved, HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.GET, params = {"models", "library"}, produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @RolesAllowed({WORKFLOW_HAB4})
    public ResponseEntity<Collection<SimpleWorkflowModelDTO>> findModelsByLibrary(final HttpServletRequest request,
                                                         @RequestParam(name = "library", required = true) final String libraryId,
                                                         @RequestParam(name = "project", required = false) final String projectId) {
        // L'usager est autorisé à accéder aux infos de la bibliothèque ou les infos du projet
        if ((StringUtils.isNotBlank(libraryId) && !libraryAccesssHelper.checkLibrary(request, libraryId)) &&
            (StringUtils.isNotBlank(projectId) && !accessHelper.checkProject(projectId))) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        // Réponse
        return new ResponseEntity<>(uiService.findAllForLibrary(libraryId), HttpStatus.OK);
    }
}
