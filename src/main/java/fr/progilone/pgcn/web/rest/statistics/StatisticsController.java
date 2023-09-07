package fr.progilone.pgcn.web.rest.statistics;

import com.codahale.metrics.annotation.Timed;
import fr.progilone.pgcn.domain.AbstractDomainObject;
import fr.progilone.pgcn.domain.dto.statistics.StatisticsProgressDTO;
import fr.progilone.pgcn.domain.dto.statistics.StatisticsProjectDTO;
import fr.progilone.pgcn.domain.dto.statistics.StatisticsProviderTrainDTO;
import fr.progilone.pgcn.domain.project.Project;
import fr.progilone.pgcn.domain.train.Train;
import fr.progilone.pgcn.service.delivery.ui.UIDeliveryService;
import fr.progilone.pgcn.service.statistics.StatisticsProgressService;
import fr.progilone.pgcn.service.statistics.StatisticsService;
import fr.progilone.pgcn.service.statistics.StatisticsWorkflowService;
import fr.progilone.pgcn.service.train.ui.UITrainService;
import fr.progilone.pgcn.web.rest.AbstractRestController;
import fr.progilone.pgcn.web.util.AccessHelper;
import fr.progilone.pgcn.web.util.LibraryAccesssHelper;
import jakarta.annotation.security.PermitAll;
import jakarta.servlet.http.HttpServletRequest;
import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
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
@RequestMapping(value = "/api/rest/statistics")
@PermitAll
public class StatisticsController extends AbstractRestController {

    private final AccessHelper accessHelper;
    private final LibraryAccesssHelper libraryAccesssHelper;
    private final UITrainService uiTrainService;
    private final StatisticsService statisticsService;
    private final StatisticsProgressService statisticsProgressService;
    private final StatisticsWorkflowService workflowProgressReportService;

    @Autowired
    public StatisticsController(final AccessHelper accessHelper,
                                final LibraryAccesssHelper libraryAccesssHelper,
                                final UIDeliveryService uiDeliveryService,
                                final UITrainService uiTrainService,
                                final StatisticsService statisticsService,
                                final StatisticsProgressService statisticsProgressService,
                                final StatisticsWorkflowService workflowProgressReportService) {
        this.accessHelper = accessHelper;
        this.libraryAccesssHelper = libraryAccesssHelper;
        this.uiTrainService = uiTrainService;
        this.statisticsService = statisticsService;
        this.statisticsProgressService = statisticsProgressService;
        this.workflowProgressReportService = workflowProgressReportService;
    }

    @RequestMapping(method = RequestMethod.GET, params = {"projectList"}, produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Page<StatisticsProjectDTO>> projectList(final HttpServletRequest request,
                                                                  @RequestParam(value = "search", required = false) final String search,
                                                                  @RequestParam(value = "projects", required = false) final List<String> projectIds,
                                                                  @DateTimeFormat(pattern = "yyyy-MM-dd") @RequestParam(name = "from", required = false) final LocalDate fromDate,
                                                                  @DateTimeFormat(pattern = "yyyy-MM-dd") @RequestParam(name = "to", required = false) final LocalDate toDate,
                                                                  @RequestParam(value = "page", required = false, defaultValue = "0") final Integer page,
                                                                  @RequestParam(value = "size", required = false, defaultValue = "10") final Integer size,
                                                                  @RequestParam(value = "sorts", required = false) final List<String> sorts) {
        // Droits d'accès
        final List<String> libraries = libraryAccesssHelper.getLibraryFilter(request, null);
        final Collection<Project> projects = accessHelper.filterProjects(projectIds);

        return new ResponseEntity<>(statisticsService.searchProject(search,
                                                                    libraries,
                                                                    projects.stream().map(AbstractDomainObject::getIdentifier).collect(Collectors.toList()),
                                                                    fromDate,
                                                                    toDate,
                                                                    page,
                                                                    size,
                                                                    sorts), HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.GET, params = {"projectGroupByStatus"}, produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<Map<String, Object>>> getProjectGroupByStatus(final HttpServletRequest request,
                                                                             @RequestParam(value = "libraries", required = false) final List<String> libraries) {
        // Droits d'accès
        final List<String> filteredLibraries = libraryAccesssHelper.getLibraryFilter(request, libraries);
        return new ResponseEntity<>(statisticsService.getProjectGroupByStatus(filteredLibraries), HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.GET, params = {"lotGroupByStatus"}, produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<Map<String, Object>>> getLotGroupByStatus(final HttpServletRequest request,
                                                                         @RequestParam(value = "libraries", required = false) final List<String> libraries,
                                                                         @RequestParam(value = "project", required = false) final List<String> projects) {
        // Droits d'accès
        final List<String> filteredLibraries = libraryAccesssHelper.getLibraryFilter(request, libraries);
        return new ResponseEntity<>(statisticsService.getLotGroupByStatus(filteredLibraries, projects), HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.GET, params = {"projectProgress"}, produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Page<StatisticsProgressDTO>> getProjectProgress(final HttpServletRequest request,
                                                                          @RequestParam(value = "library", required = false) final List<String> libraries,
                                                                          @RequestParam(value = "project", required = false) final List<String> projects,
                                                                          @DateTimeFormat(pattern = "yyyy-MM-dd") @RequestParam(name = "from",
                                                                                                                                required = false) final LocalDate fromDate,
                                                                          @DateTimeFormat(pattern = "yyyy-MM-dd") @RequestParam(name = "to",
                                                                                                                                required = false) final LocalDate toDate,
                                                                          @RequestParam(value = "page", required = false, defaultValue = "0") final Integer page,
                                                                          @RequestParam(value = "size", required = false, defaultValue = "10") final Integer size) {
        // Droits d'accès
        final List<String> filteredLibraries = libraryAccesssHelper.getLibraryFilter(request, libraries);

        return new ResponseEntity<>(statisticsProgressService.getProjectProgress(filteredLibraries, projects, fromDate, toDate, page, size), HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.GET, params = {"lotProgress"}, produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Page<StatisticsProgressDTO>> getLotProgress(final HttpServletRequest request,
                                                                      @RequestParam(value = "library", required = false) final List<String> libraries,
                                                                      @RequestParam(value = "project", required = false) final List<String> projects,
                                                                      @DateTimeFormat(pattern = "yyyy-MM-dd") @RequestParam(name = "from",
                                                                                                                            required = false) final LocalDate fromDate,
                                                                      @DateTimeFormat(pattern = "yyyy-MM-dd") @RequestParam(name = "to", required = false) final LocalDate toDate,
                                                                      @RequestParam(value = "page", required = false, defaultValue = "0") final Integer page,
                                                                      @RequestParam(value = "size", required = false, defaultValue = "10") final Integer size) {
        // Droits d'accès
        final List<String> filteredLibraries = libraryAccesssHelper.getLibraryFilter(request, libraries);

        return new ResponseEntity<>(statisticsProgressService.getLotProgress(filteredLibraries, projects, fromDate, toDate, page, size), HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.GET, params = {"trainGroupByStatus"}, produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<Map<String, Object>>> getTrainGroupByStatus(final HttpServletRequest request,
                                                                           @RequestParam(value = "libraries", required = false) final List<String> libraries,
                                                                           @RequestParam(value = "project", required = false) final List<String> projects) {
        // Droits d'accès
        final List<String> filteredLibraries = libraryAccesssHelper.getLibraryFilter(request, libraries);
        return new ResponseEntity<>(statisticsService.getTrainGroupByStatus(filteredLibraries, projects), HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.GET, params = {"docunitGroupByStatus"}, produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<Map<String, Object>>> getDocUnitsGroupByStatus(final HttpServletRequest request,
                                                                              @RequestParam(value = "libraries", required = false) final List<String> libraries,
                                                                              @RequestParam(value = "project", required = false) final List<String> projects,
                                                                              @RequestParam(value = "lot", required = false) final List<String> lots) {
        // Droits d'accès
        final List<String> filteredLibraries = libraryAccesssHelper.getLibraryFilter(request, libraries);
        return new ResponseEntity<>(workflowProgressReportService.getDocUnitsGroupByStatus(filteredLibraries, projects, lots), HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.GET, params = {"userGroupByLibrary"}, produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<Map<String, Object>>> getUsersGroupByLibrary(final HttpServletRequest request,
                                                                            @RequestParam(value = "libraries", required = false) final List<String> libraries) {
        // Droits d'accès
        final List<String> filteredLibraries = libraryAccesssHelper.getLibraryFilter(request, libraries);
        return new ResponseEntity<>(statisticsService.getUsersGroupByLibrary(filteredLibraries), HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.GET, params = {"provider_train"}, produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<StatisticsProviderTrainDTO>> getProviderTrainStats(final HttpServletRequest request,
                                                                                  @RequestParam(value = "library", required = false) final List<String> libraries,
                                                                                  @RequestParam(value = "project", required = false) final List<String> projects,
                                                                                  @RequestParam(value = "train", required = false) final List<String> trains,
                                                                                  @RequestParam(value = "status", required = false) final List<Train.TrainStatus> status,
                                                                                  @DateTimeFormat(pattern = "yyyy-MM-dd") @RequestParam(name = "returnFrom",
                                                                                                                                        required = false) final LocalDate returnFrom,
                                                                                  @DateTimeFormat(pattern = "yyyy-MM-dd") @RequestParam(name = "returnTo",
                                                                                                                                        required = false) final LocalDate returnTo,
                                                                                  @DateTimeFormat(pattern = "yyyy-MM-dd") @RequestParam(name = "sendFrom",
                                                                                                                                        required = false) final LocalDate sendFrom,
                                                                                  @DateTimeFormat(pattern = "yyyy-MM-dd") @RequestParam(name = "sendTo",
                                                                                                                                        required = false) final LocalDate sendTo,
                                                                                  @RequestParam(name = "insuranceFrom", required = false) final Double insuranceFrom,
                                                                                  @RequestParam(name = "insuranceTo", required = false) final Double insuranceTo) {
        // Droits d'accès
        final List<String> filteredLibraries = libraryAccesssHelper.getLibraryFilter(request, libraries);
        return new ResponseEntity<>(uiTrainService.getProviderTrainStats(filteredLibraries,
                                                                         projects,
                                                                         trains,
                                                                         status,
                                                                         returnFrom,
                                                                         returnTo,
                                                                         sendFrom,
                                                                         sendTo,
                                                                         insuranceFrom,
                                                                         insuranceTo), HttpStatus.OK);
    }
}
