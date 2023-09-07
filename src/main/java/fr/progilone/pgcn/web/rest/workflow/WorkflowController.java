package fr.progilone.pgcn.web.rest.workflow;

import static fr.progilone.pgcn.web.rest.document.security.AuthorizationConstants.COND_REPORT_HAB2;
import static fr.progilone.pgcn.web.rest.document.security.AuthorizationConstants.DOC_UNIT_HAB3;

import com.codahale.metrics.annotation.Timed;
import fr.progilone.pgcn.domain.dto.workflow.BooleanValueDTO;
import fr.progilone.pgcn.domain.dto.workflow.DocUnitWorkflowDTO;
import fr.progilone.pgcn.domain.dto.workflow.StateIsDoneDTO;
import fr.progilone.pgcn.domain.workflow.WorkflowStateKey;
import fr.progilone.pgcn.service.workflow.ui.UIWorkflowService;
import fr.progilone.pgcn.web.rest.AbstractRestController;
import fr.progilone.pgcn.web.rest.administration.security.AuthorizationConstants;
import fr.progilone.pgcn.web.util.AccessHelper;
import jakarta.annotation.security.RolesAllowed;
import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
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

@RestController
@RequestMapping(value = "/api/rest/workflow")
public class WorkflowController extends AbstractRestController {

    private final UIWorkflowService uiService;
    private final AccessHelper accessHelper;

    @Autowired
    public WorkflowController(final UIWorkflowService uiService, final AccessHelper accessHelper) {
        this.uiService = uiService;
        this.accessHelper = accessHelper;
    }

    @RequestMapping(value = "/{identifier}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<DocUnitWorkflowDTO> findByIdentifier(@PathVariable final String identifier) {
        final DocUnitWorkflowDTO workflow = uiService.findWorkflowByIdentifier(identifier);
        // non trouvé
        if (workflow == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return createResponseEntity(workflow);
    }

    @RequestMapping(method = RequestMethod.GET,
                    params = {"canProcess",
                              "docUnit"},
                    produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<BooleanValueDTO> canCurrentUserProcessState(final HttpServletRequest request,
                                                                      @RequestParam(value = "docUnit") final String identifier,
                                                                      @RequestParam(value = "key") final WorkflowStateKey key) {
        if (!accessHelper.checkDocUnit(identifier)) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        return createResponseEntity(uiService.canCurrentUserProcessState(identifier, key, accessHelper.checkUserIsPresta()));
    }

    @RequestMapping(method = RequestMethod.GET,
                    params = {"process",
                              "docUnitId"},
                    produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<?> processState(final HttpServletRequest request,
                                          @RequestParam(value = "docUnitId") final String identifier,
                                          @RequestParam(value = "key") final WorkflowStateKey key) {
        if (!accessHelper.checkDocUnit(identifier)) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        uiService.processState(identifier, key);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @RequestMapping(params = {"docUnit"}, method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<DocUnitWorkflowDTO> findByDocUnitIdentifier(@RequestParam(value = "docUnit", required = true) final String identifier) {
        final DocUnitWorkflowDTO workflow = uiService.findWorkflowByDocUnitIdentifier(identifier);
        // non trouvé
        if (workflow == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return createResponseEntity(workflow);
    }

    @RequestMapping(method = RequestMethod.GET, params = {"isDone"}, produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<StateIsDoneDTO> isStateDone(final HttpServletRequest request,
                                                      @RequestParam(value = "doc") final String identifier,
                                                      @RequestParam(value = "key") final WorkflowStateKey key) {
        if (!accessHelper.checkDocUnit(identifier)) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        return createResponseEntity(uiService.isStateDone(identifier, key));
    }

    @RequestMapping(method = RequestMethod.GET, params = {"isWorkflowStarted"}, produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<StateIsDoneDTO> isWorkflowStarted(final HttpServletRequest request, @RequestParam(value = "doc") final String identifier) {
        if (!accessHelper.checkDocUnit(identifier)) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        return createResponseEntity(uiService.isWorkflowStarted(identifier));
    }

    @RequestMapping(method = RequestMethod.GET, params = {"isCheckStarted"}, produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<StateIsDoneDTO> isCheckStarted(final HttpServletRequest request, @RequestParam(value = "doc") final String identifier) {
        if (!accessHelper.checkDocUnit(identifier)) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        return createResponseEntity(uiService.isCheckStarted(identifier));
    }

    @RequestMapping(method = RequestMethod.GET, params = {"isWaitingRedelivering"}, produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<StateIsDoneDTO> isWaitingForRedelivering(final HttpServletRequest request, @RequestParam(value = "doc") final String identifier) {
        if (!accessHelper.checkDocUnit(identifier)) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        return createResponseEntity(uiService.isWaitingForRedelivering(identifier));
    }

    @RequestMapping(method = RequestMethod.GET, params = {"canReportBeValidated"}, produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<StateIsDoneDTO> canReportBeValidated(final HttpServletRequest request, @RequestParam(value = "doc") final String identifier) {
        if (!accessHelper.checkDocUnit(identifier)) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        return createResponseEntity(uiService.canReportBeValidated(identifier));
    }

    @RequestMapping(method = RequestMethod.GET, params = {"isRejectDefinitive"}, produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<StateIsDoneDTO> isRejectDefinitive(final HttpServletRequest request, @RequestParam(value = "doc") final String identifier) {
        if (!accessHelper.checkDocUnit(identifier)) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        return createResponseEntity(uiService.isRejectDefinitive(identifier));
    }

    /**
     * Force les docs au statut 'En attente de nenumerisation'
     * à la création d'un train de renumerisation.
     *
     * @param docIds
     * @return
     */
    @RequestMapping(method = RequestMethod.POST, params = {"resetNumWaiting"})
    @ResponseStatus(HttpStatus.OK)
    @Timed
    public ResponseEntity<?> resetToNumWaiting(@RequestBody final List<String> docIds) {
        uiService.resetToNumWaiting(docIds);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    /**
     * Validation constats d'etat en masse.
     *
     * @param request
     * @param datas
     * @return
     */
    @RequestMapping(method = RequestMethod.POST, params = {"massValidate"})
    @ResponseStatus(HttpStatus.OK)
    @Timed
    @RolesAllowed(COND_REPORT_HAB2)
    public ResponseEntity<?> massValidate(final HttpServletRequest request, @RequestBody final List<String> docUnitIds) {

        // droits d'accès à l'ud
        for (final String docUnitId : docUnitIds) {
            if (!accessHelper.checkDocUnit(docUnitId)) {
                return new ResponseEntity<>(HttpStatus.FORBIDDEN);
            }
        }
        uiService.massValidateCondReports(docUnitIds);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    /**
     * Validation constats d'etat en masse.
     *
     * @param request
     * @param datas
     * @return
     */
    @RequestMapping(method = RequestMethod.POST, params = {"massValidateRecords"})
    @ResponseStatus(HttpStatus.OK)
    @Timed
    @RolesAllowed(DOC_UNIT_HAB3)
    public ResponseEntity<?> massValidateRecords(final HttpServletRequest request, @RequestBody final List<String> docUnitIds) {

        // droits d'accès à l'ud
        for (final String docUnitId : docUnitIds) {
            if (!accessHelper.checkDocUnit(docUnitId)) {
                return new ResponseEntity<>(HttpStatus.FORBIDDEN);
            }
        }
        uiService.massValidateRecords(docUnitIds);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    /**
     * Termine tous les docWorkflows des docUnits en parametre.
     * Les etapes en cours ou en attente sont annulées et chaque wkf est terminé.
     *
     * @param docUnitIds
     * @return
     */
    @RequestMapping(method = RequestMethod.POST, params = {"endAllDocWorkflows"})
    @ResponseStatus(HttpStatus.OK)
    @RolesAllowed(AuthorizationConstants.SUPER_ADMIN)
    @Timed
    public ResponseEntity<?> endAllDocWorkflows(@RequestBody final List<String> docUnitIds) {
        uiService.endAllDocWorkflows(docUnitIds);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.POST, params = {"validDocWorkflowState"})
    @ResponseStatus(HttpStatus.OK)
    @RolesAllowed(AuthorizationConstants.SUPER_ADMIN)
    public ResponseEntity<?> validDocWorkflowState(@RequestBody final List<String> docUnitIds) {

        final String stateId = docUnitIds.remove(docUnitIds.size() - 1);
        uiService.validDocWorkflowState(stateId, docUnitIds);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.POST, params = {"reinitDocWorkflowState"})
    @ResponseStatus(HttpStatus.OK)
    @RolesAllowed(AuthorizationConstants.SUPER_ADMIN)
    public ResponseEntity<?> reinitDocWorkflowState(@RequestBody final List<String> docUnitIds) {

        final String stateId = docUnitIds.remove(docUnitIds.size() - 1);
        uiService.reinitDocWorkflowState(stateId, docUnitIds);
        return new ResponseEntity<>(HttpStatus.OK);
    }

}
