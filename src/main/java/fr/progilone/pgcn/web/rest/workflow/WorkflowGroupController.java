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

import org.apache.commons.collections4.CollectionUtils;
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

import fr.progilone.pgcn.domain.dto.workflow.SimpleWorkflowGroupDTO;
import fr.progilone.pgcn.domain.dto.workflow.WorkflowGroupDTO;
import fr.progilone.pgcn.exception.PgcnBusinessException;
import fr.progilone.pgcn.exception.PgcnException;
import fr.progilone.pgcn.exception.message.PgcnError;
import fr.progilone.pgcn.exception.message.PgcnErrorCode;
import fr.progilone.pgcn.service.workflow.WorkflowGroupService;
import fr.progilone.pgcn.service.workflow.ui.UIWorkflowGroupService;
import fr.progilone.pgcn.web.rest.AbstractRestController;
import fr.progilone.pgcn.web.util.LibraryAccesssHelper;
import fr.progilone.pgcn.web.util.WorkflowAccessHelper;

@RestController
@RequestMapping(value = "/api/rest/workflow_group")
public class WorkflowGroupController extends AbstractRestController {

    private final WorkflowGroupService service;
    private final UIWorkflowGroupService uiService;
    private final LibraryAccesssHelper accessHelper;
    private final WorkflowAccessHelper workflowAccessHelper;

    @Autowired
    public WorkflowGroupController(final WorkflowGroupService service,
                                   final UIWorkflowGroupService uiService,
                                   final LibraryAccesssHelper accessHelper,
                                   final WorkflowAccessHelper workflowAccessHelper) {
        this.service = service;
        this.uiService = uiService;
        this.accessHelper = accessHelper;
        this.workflowAccessHelper = workflowAccessHelper;
    }

    @RequestMapping(method = RequestMethod.POST)
    @Timed
    @RolesAllowed({WORKFLOW_HAB1})
    public ResponseEntity<WorkflowGroupDTO> create(final HttpServletRequest request, @RequestBody final WorkflowGroupDTO dto) throws PgcnException {
        if (dto.getLibrary() != null && !accessHelper.checkLibrary(request, dto.getLibrary().getIdentifier())) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        final WorkflowGroupDTO saved = uiService.create(dto);
        return new ResponseEntity<>(saved, HttpStatus.CREATED);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    @Timed
    @RolesAllowed({WORKFLOW_HAB3})
    public ResponseEntity<?> delete(final HttpServletRequest request, @PathVariable final String id) {
        // Vérification que le group n'est pas lié à une étape en cours ou à venir
        if (!workflowAccessHelper.canWorkflowGroupBeDeleted(id)) {
            final PgcnError.Builder builder = new PgcnError.Builder();
            throw new PgcnBusinessException(builder.reinit().setCode(PgcnErrorCode.WORKFLOW_GROUP_IS_IN_FUTURE_STATE).build());
        }
        service.delete(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.GET, params = {"search"}, produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @RolesAllowed({WORKFLOW_HAB4})
    public ResponseEntity<Page<SimpleWorkflowGroupDTO>> search(final HttpServletRequest request,
                                                               @RequestParam(value = "search", required = false) final String search,
                                                               @RequestParam(value = "initiale", required = false) final String initiale,
                                                               @RequestParam(value = "libraries", required = false) final List<String> libraries,
                                                               @RequestParam(value = "page", required = false, defaultValue = "0") final Integer page,
                                                               @RequestParam(value = "size", required = false, defaultValue = "10")
                                                               final Integer size,
                                                               @RequestParam(value = "sorts", required = false) final List<String> sorts) {
        final List<String> filteredLibraries = accessHelper.getLibraryFilter(request, libraries);
        return new ResponseEntity<>(uiService.search(search, initiale, filteredLibraries, page, size, sorts), HttpStatus.OK);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @RolesAllowed({WORKFLOW_HAB4})
    public ResponseEntity<WorkflowGroupDTO> getById(final HttpServletRequest request, @PathVariable final String id) {
        final WorkflowGroupDTO group = uiService.getOne(id);
        return createResponseEntity(group);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.POST)
    @Timed
    @RolesAllowed({WORKFLOW_HAB2})
    public ResponseEntity<WorkflowGroupDTO> update(final HttpServletRequest request, @RequestBody final WorkflowGroupDTO dto) throws PgcnException {
        if (dto.getLibrary() != null && !accessHelper.checkLibrary(request, dto.getLibrary().getIdentifier())) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        if (CollectionUtils.isEmpty(dto.getUsers()) && !workflowAccessHelper.canWorkflowGroupBeDeleted(dto.getIdentifier())) {
            final PgcnError.Builder builder = new PgcnError.Builder();
            throw new PgcnBusinessException(builder.reinit().setCode(PgcnErrorCode.WORKFLOW_GROUP_IS_IN_FUTURE_STATE).build());
        }
        final WorkflowGroupDTO saved = uiService.update(dto);
        return new ResponseEntity<>(saved, HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.GET, params = {"groups", "library"}, produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @RolesAllowed({WORKFLOW_HAB4})
    public ResponseEntity<Collection<SimpleWorkflowGroupDTO>> findGroupsByLibrary(@RequestParam(name = "library") final String libraryId) {
        // Réponse
        return new ResponseEntity<>(uiService.findAllForLibrary(libraryId), HttpStatus.OK);
    }
}
