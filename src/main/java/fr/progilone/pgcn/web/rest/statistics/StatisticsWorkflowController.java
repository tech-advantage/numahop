package fr.progilone.pgcn.web.rest.statistics;

import com.codahale.metrics.annotation.Timed;
import fr.progilone.pgcn.domain.AbstractDomainObject;
import fr.progilone.pgcn.domain.document.DigitalDocument.DigitalDocumentStatus;
import fr.progilone.pgcn.domain.dto.statistics.WorkflowDeliveryProgressDTO;
import fr.progilone.pgcn.domain.dto.statistics.WorkflowDocUnitProgressDTO;
import fr.progilone.pgcn.domain.dto.statistics.WorkflowDocUnitProgressDTOPending;
import fr.progilone.pgcn.domain.dto.statistics.WorkflowProfileActivityDTO;
import fr.progilone.pgcn.domain.dto.statistics.WorkflowStateProgressDTO;
import fr.progilone.pgcn.domain.dto.statistics.WorkflowUserActivityDTO;
import fr.progilone.pgcn.domain.dto.statistics.WorkflowUserProgressDTO;
import fr.progilone.pgcn.domain.util.CustomUserDetails;
import fr.progilone.pgcn.domain.workflow.WorkflowStateKey;
import fr.progilone.pgcn.domain.workflow.WorkflowStateStatus;
import fr.progilone.pgcn.security.SecurityUtils;
import fr.progilone.pgcn.service.document.DocCheckHistoryService;
import fr.progilone.pgcn.service.statistics.StatisticsWorkflowService;
import fr.progilone.pgcn.service.user.UserService;
import fr.progilone.pgcn.web.util.AccessHelper;
import fr.progilone.pgcn.web.util.LibraryAccesssHelper;
import jakarta.annotation.security.PermitAll;
import jakarta.servlet.http.HttpServletRequest;
import java.time.LocalDate;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/api/rest/statistics/workflow")
@PermitAll
public class StatisticsWorkflowController {

    private final AccessHelper accessHelper;
    private final LibraryAccesssHelper libraryAccesssHelper;
    private final StatisticsWorkflowService workflowProgressReportService;
    private final DocCheckHistoryService docCheckHistoryService;

    public StatisticsWorkflowController(final AccessHelper accessHelper,
                                        final LibraryAccesssHelper libraryAccesssHelper,
                                        final StatisticsWorkflowService workflowProgressReportService,
                                        final DocCheckHistoryService docCheckHistoryService) {
        this.accessHelper = accessHelper;
        this.libraryAccesssHelper = libraryAccesssHelper;
        this.workflowProgressReportService = workflowProgressReportService;
        this.docCheckHistoryService = docCheckHistoryService;
    }

    @RequestMapping(method = RequestMethod.GET, params = {"wdelivery"}, produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Page<WorkflowDeliveryProgressDTO>> getWorkflowDeliveryProgressStatistics(final HttpServletRequest request,
                                                                                                   @RequestParam(value = "library", required = false) final List<String> libraries,
                                                                                                   @RequestParam(value = "project", required = false) final List<String> projects,
                                                                                                   @RequestParam(value = "lot", required = false) final List<String> lots,
                                                                                                   @RequestParam(value = "delivery", required = false) final List<
                                                                                                                                                                  String> deliveries,
                                                                                                   @RequestParam(value = "pgcnid", required = false) final String pgcnId,
                                                                                                   @RequestParam(value = "state", required = false) final List<
                                                                                                                                                               WorkflowStateKey> states,
                                                                                                   @DateTimeFormat(pattern = "yyyy-MM-dd") @RequestParam(name = "from",
                                                                                                                                                         required = false) final LocalDate fromDate,
                                                                                                   @DateTimeFormat(pattern = "yyyy-MM-dd") @RequestParam(name = "to",
                                                                                                                                                         required = false) final LocalDate toDate,
                                                                                                   @RequestParam(value = "page",
                                                                                                                 defaultValue = "0",
                                                                                                                 required = false) final int page,
                                                                                                   @RequestParam(value = "size",
                                                                                                                 defaultValue = "10",
                                                                                                                 required = false) final int size) {
        // Droits d'accès
        final List<String> filteredLibraries = libraryAccesssHelper.getLibraryFilter(request, libraries);
        final List<String> filteredProjects = accessHelper.filterProjects(projects).stream().map(AbstractDomainObject::getIdentifier).collect(Collectors.toList());

        return new ResponseEntity<>(workflowProgressReportService.getDeliveryProgressReport(filteredLibraries,
                                                                                            filteredProjects,
                                                                                            lots,
                                                                                            deliveries,
                                                                                            pgcnId,
                                                                                            states,
                                                                                            fromDate,
                                                                                            toDate,
                                                                                            page,
                                                                                            size), HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.GET, params = {"wcontrol"}, produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<WorkflowDocUnitProgressDTO>> getWorkflowDocUnitStateControl(final HttpServletRequest request,
                                                                                           @RequestParam(value = "library", required = false) final List<String> libraries,
                                                                                           @RequestParam(value = "project", required = false) final List<String> projects,
                                                                                           @RequestParam(value = "lot", required = false) final List<String> lots,
                                                                                           @RequestParam(value = "state", required = false) final List<
                                                                                                                                                       DigitalDocumentStatus> states,
                                                                                           @DateTimeFormat(pattern = "yyyy-MM-dd") @RequestParam(name = "from") final LocalDate fromDate) {
        // Droits d'accès
        final List<String> filteredLibraries = libraryAccesssHelper.getLibraryFilter(request, libraries);
        final List<String> filteredProjects = accessHelper.filterProjects(projects).stream().map(AbstractDomainObject::getIdentifier).collect(Collectors.toList());

        return new ResponseEntity<>(workflowProgressReportService.getDocUnitForStateControl(filteredLibraries, filteredProjects, lots, states, fromDate), HttpStatus.OK);
    }

    /**
     * Suivi de l'avancement des docUnitWorkflows selon les criteres passes en parametre.
     *
     * @param request
     * @param libraries
     * @param projects
     * @param lots
     * @param pgcnId
     * @param states
     * @param onlyMine
     * @param fromDate
     * @param toDate
     * @param page
     * @param size
     * @return
     */
    @RequestMapping(method = RequestMethod.GET, params = {"wdocunit"}, produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Page<WorkflowDocUnitProgressDTO>> getWorkflowDocUnitProgressStatistics(final HttpServletRequest request,
                                                                                                 @RequestParam(value = "library", required = false) final List<String> libraries,
                                                                                                 @RequestParam(value = "project", required = false) final List<String> projects,
                                                                                                 @RequestParam(value = "project_active",
                                                                                                               required = false,
                                                                                                               defaultValue = "false") final boolean projetActive,
                                                                                                 @RequestParam(value = "lot", required = false) final List<String> lots,
                                                                                                 @RequestParam(value = "train", required = false) final List<String> trains,
                                                                                                 @RequestParam(value = "pgcnid", required = false) final String pgcnId,
                                                                                                 @RequestParam(value = "state", required = false) final List<
                                                                                                                                                             WorkflowStateKey> states,
                                                                                                 @RequestParam(value = "status", required = false) final List<
                                                                                                                                                              WorkflowStateStatus> status,
                                                                                                 @RequestParam(value = "mine",
                                                                                                               required = false,
                                                                                                               defaultValue = "false") final boolean onlyMine,
                                                                                                 @DateTimeFormat(pattern = "yyyy-MM-dd") @RequestParam(name = "from",
                                                                                                                                                       required = false) final LocalDate fromDate,
                                                                                                 @DateTimeFormat(pattern = "yyyy-MM-dd") @RequestParam(name = "to",
                                                                                                                                                       required = false) final LocalDate toDate,
                                                                                                 @RequestParam(value = "page", defaultValue = "0", required = false) final int page,
                                                                                                 @RequestParam(value = "size",
                                                                                                               defaultValue = "10",
                                                                                                               required = false) final int size) {
        // Droits d'accès
        final List<String> filteredLibraries = libraryAccesssHelper.getLibraryFilter(request, libraries);
        final List<String> filteredProjects = accessHelper.filterProjects(projects).stream().map(AbstractDomainObject::getIdentifier).collect(Collectors.toList());

        final List<String> filteredLots = accessHelper.filterLots(lots).stream().map(AbstractDomainObject::getIdentifier).collect(Collectors.toList());

        List<String> users = null;
        if (onlyMine) {  // on recupere le user => on n'aura en retour que les etapes sur lesquelles le user peut agir !
            final CustomUserDetails currentUser = SecurityUtils.getCurrentUser();
            if (currentUser != null && !StringUtils.equals(currentUser.getIdentifier(), UserService.SUPER_ADMIN_ID)) {
                users = Collections.singletonList(currentUser.getLogin());
            }
        }

        return new ResponseEntity<>(workflowProgressReportService.getDocUnitProgressReport(filteredLibraries,
                                                                                           filteredProjects,
                                                                                           projetActive,
                                                                                           filteredLots,
                                                                                           trains,
                                                                                           pgcnId,
                                                                                           states,
                                                                                           status,
                                                                                           users,
                                                                                           fromDate,
                                                                                           toDate,
                                                                                           page,
                                                                                           size), HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.GET, params = {"wdocunitpending"}, produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<WorkflowDocUnitProgressDTOPending>> getWorkflowDocUnitProgressStatisticsLight(final HttpServletRequest request,
                                                                                                             @RequestParam(value = "library", required = false) final List<
                                                                                                                                                                           String> libraries,
                                                                                                             @RequestParam(value = "project", required = false) final List<
                                                                                                                                                                           String> projects,
                                                                                                             @RequestParam(value = "project_active",
                                                                                                                           required = false,
                                                                                                                           defaultValue = "false") final boolean projetActive,
                                                                                                             @RequestParam(value = "lot", required = false) final List<String> lots,
                                                                                                             @RequestParam(value = "train", required = false) final List<
                                                                                                                                                                         String> trains,
                                                                                                             @RequestParam(value = "pgcnid", required = false) final String pgcnId,
                                                                                                             @RequestParam(value = "state", required = false) final List<
                                                                                                                                                                         WorkflowStateKey> states,
                                                                                                             @RequestParam(value = "status", required = false) final List<
                                                                                                                                                                          WorkflowStateStatus> status,
                                                                                                             @RequestParam(value = "mine",
                                                                                                                           required = false,
                                                                                                                           defaultValue = "false") final boolean onlyMine,
                                                                                                             @DateTimeFormat(pattern = "yyyy-MM-dd") @RequestParam(name = "from",
                                                                                                                                                                   required = false) final LocalDate fromDate,
                                                                                                             @DateTimeFormat(pattern = "yyyy-MM-dd") @RequestParam(name = "to",
                                                                                                                                                                   required = false) final LocalDate toDate,
                                                                                                             @RequestParam(value = "page",
                                                                                                                           defaultValue = "0",
                                                                                                                           required = false) final int page,
                                                                                                             @RequestParam(value = "size",
                                                                                                                           defaultValue = "10",
                                                                                                                           required = false) final int size) {
        // Droits d'accès
        final List<String> filteredLibraries = libraryAccesssHelper.getLibraryFilter(request, libraries);
        final List<String> filteredProjects = accessHelper.filterProjects(projects).stream().map(AbstractDomainObject::getIdentifier).collect(Collectors.toList());
        final List<String> filteredLots = accessHelper.filterLots(lots).stream().map(AbstractDomainObject::getIdentifier).collect(Collectors.toList());

        List<String> users = null;
        if (onlyMine) {  // on recupere le user => on n'aura en retour que les etapes sur lesquelles le user peut agir !
            final CustomUserDetails currentUser = SecurityUtils.getCurrentUser();
            if (currentUser != null && !StringUtils.equals(currentUser.getIdentifier(), UserService.SUPER_ADMIN_ID)) {
                users = Collections.singletonList(currentUser.getLogin());
            }
        }

        return new ResponseEntity<>(workflowProgressReportService.getDocUnitProgressReportPending(filteredLibraries,
                                                                                                  filteredProjects,
                                                                                                  projetActive,
                                                                                                  filteredLots,
                                                                                                  trains,
                                                                                                  pgcnId,
                                                                                                  states,
                                                                                                  status,
                                                                                                  users,
                                                                                                  fromDate,
                                                                                                  toDate,
                                                                                                  page,
                                                                                                  size), HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.GET,
                    params = {"wdocunit",
                              "current"},
                    produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<WorkflowDocUnitProgressDTO>> getWorkflowDocUnitCurrentStatistics(final HttpServletRequest request,
                                                                                                @RequestParam(value = "library", required = false) final List<String> libraries,
                                                                                                @RequestParam(value = "project", required = false) final List<String> projects,
                                                                                                @RequestParam(value = "lot", required = false) final List<String> lots,
                                                                                                @RequestParam(value = "state", required = false) final List<
                                                                                                                                                            WorkflowStateKey> states,
                                                                                                @DateTimeFormat(pattern = "yyyy-MM-dd") @RequestParam(name = "from") final LocalDate fromDate) {
        // Droits d'accès
        final List<String> filteredLibraries = libraryAccesssHelper.getLibraryFilter(request, libraries);
        final List<String> filteredProjects = accessHelper.filterProjects(projects).stream().map(AbstractDomainObject::getIdentifier).collect(Collectors.toList());
        final List<String> filteredLots = accessHelper.filterLots(lots).stream().map(AbstractDomainObject::getIdentifier).collect(Collectors.toList());

        return new ResponseEntity<>(workflowProgressReportService.getDocUnitCurrentReport(filteredLibraries, filteredProjects, filteredLots, states, fromDate), HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.GET, params = {"wstate"}, produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<WorkflowStateProgressDTO>> getWorkflowStatesStatistics(final HttpServletRequest request,
                                                                                      @RequestParam(value = "library", required = false) final List<String> libraries,
                                                                                      @RequestParam(value = "workflow", required = false) final List<String> workflows,
                                                                                      @RequestParam(value = "state", required = false) final List<WorkflowStateKey> states,
                                                                                      @DateTimeFormat(pattern = "yyyy-MM-dd") @RequestParam(name = "from",
                                                                                                                                            required = false) final LocalDate fromDate,
                                                                                      @DateTimeFormat(pattern = "yyyy-MM-dd") @RequestParam(name = "to",
                                                                                                                                            required = false) final LocalDate toDate) {
        // Droits d'accès
        if (accessHelper.checkUserIsPresta()) { // no presta
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        final List<String> filteredLibraries = libraryAccesssHelper.getLibraryFilter(request, libraries);
        return new ResponseEntity<>(workflowProgressReportService.getWorkflowStatesStatistics(filteredLibraries, workflows, states, fromDate, toDate), HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.GET, params = {"wuser"}, produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Collection<WorkflowUserProgressDTO>> getWorkflowUsersStatistics(final HttpServletRequest request,
                                                                                          @RequestParam(value = "library", required = false) final List<String> libraries,
                                                                                          @RequestParam(value = "project", required = false) final List<String> projects,
                                                                                          @RequestParam(value = "lot", required = false) final List<String> lots,
                                                                                          @RequestParam(value = "delivery", required = false) final List<String> deliveries,
                                                                                          @DateTimeFormat(pattern = "yyyy-MM-dd") @RequestParam(name = "from",
                                                                                                                                                required = false) final LocalDate fromDate,
                                                                                          @DateTimeFormat(pattern = "yyyy-MM-dd") @RequestParam(name = "to",
                                                                                                                                                required = false) final LocalDate toDate) {
        // Droits d'accès
        if (accessHelper.checkUserIsPresta()) { // no presta
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        final List<String> filteredLibraries = libraryAccesssHelper.getLibraryFilter(request, libraries);
        return new ResponseEntity<>(docCheckHistoryService.getWorkflowUsersStatistics(filteredLibraries, projects, lots, deliveries, fromDate, toDate), HttpStatus.OK);
    }

    /**
     * Suivi de l'activité par profil : qui fait quoi, quand et pendant combien de temps
     *
     * @param request
     * @param libraries
     * @param projects
     * @param lots
     * @param states
     * @param roles
     * @param fromDate
     * @param toDate
     * @return
     */
    @RequestMapping(method = RequestMethod.GET, params = {"wprofile_activity"}, produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Collection<WorkflowProfileActivityDTO>> getProfilesActivityStatistics(final HttpServletRequest request,
                                                                                                @RequestParam(value = "library", required = false) final List<String> libraries,
                                                                                                @RequestParam(value = "project", required = false) final List<String> projects,
                                                                                                @RequestParam(value = "lot", required = false) final List<String> lots,
                                                                                                @RequestParam(value = "state", required = false) final List<
                                                                                                                                                            WorkflowStateKey> states,
                                                                                                @RequestParam(value = "role", required = false) final List<String> roles,
                                                                                                @DateTimeFormat(pattern = "yyyy-MM-dd") @RequestParam(name = "from",
                                                                                                                                                      required = false) LocalDate fromDate,
                                                                                                @DateTimeFormat(pattern = "yyyy-MM-dd") @RequestParam(name = "to",
                                                                                                                                                      required = false) final LocalDate toDate) {
        // Droits d'accès
        if (accessHelper.checkUserIsPresta()) { // no presta
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        final List<String> filteredLibraries = libraryAccesssHelper.getLibraryFilter(request, libraries);

        // Si pas de période renseignée, on se limite aux 30 derniers jours
        if (fromDate == null && toDate == null) {
            fromDate = LocalDate.now().minusDays(30);
        }
        return new ResponseEntity<>(workflowProgressReportService.getProfilesActivityStatistics(filteredLibraries, projects, lots, states, roles, fromDate, toDate), HttpStatus.OK);
    }

    /**
     * Suivi de l'activité des utilisateurs : qui fait quoi, quand et pendant combien de temps
     *
     * @param request
     * @param libraries
     * @param projects
     * @param lots
     * @param states
     * @param roles
     * @param fromDate
     * @param toDate
     * @return
     */
    @RequestMapping(method = RequestMethod.GET, params = {"wuser_activity"}, produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Collection<WorkflowUserActivityDTO>> getUsersActivityStatistics(final HttpServletRequest request,
                                                                                          @RequestParam(value = "library", required = false) final List<String> libraries,
                                                                                          @RequestParam(value = "project", required = false) final List<String> projects,
                                                                                          @RequestParam(value = "lot", required = false) final List<String> lots,
                                                                                          @RequestParam(value = "state", required = false) final List<WorkflowStateKey> states,
                                                                                          @RequestParam(value = "role", required = false) final List<String> roles,
                                                                                          @DateTimeFormat(pattern = "yyyy-MM-dd") @RequestParam(name = "from",
                                                                                                                                                required = false) LocalDate fromDate,
                                                                                          @DateTimeFormat(pattern = "yyyy-MM-dd") @RequestParam(name = "to",
                                                                                                                                                required = false) final LocalDate toDate) {
        // Droits d'accès
        if (accessHelper.checkUserIsPresta()) { // no presta
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        final List<String> filteredLibraries = libraryAccesssHelper.getLibraryFilter(request, libraries);

        // Si pas de période renseignée, on se limite aux 30 derniers jours
        if (fromDate == null && toDate == null) {
            fromDate = LocalDate.now().minusDays(30);
        }
        return new ResponseEntity<>(workflowProgressReportService.getUsersActivityStatistics(filteredLibraries, projects, lots, states, roles, fromDate, toDate), HttpStatus.OK);
    }
}
