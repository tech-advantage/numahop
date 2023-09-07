package fr.progilone.pgcn.web.rest.statistics;

import com.codahale.metrics.annotation.Timed;
import fr.progilone.pgcn.domain.dto.statistics.StatisticsDocPublishedDTO;
import fr.progilone.pgcn.domain.dto.statistics.StatisticsDocRejectedDTO;
import fr.progilone.pgcn.domain.dto.statistics.StatisticsDocUnitAverageDTO;
import fr.progilone.pgcn.domain.dto.statistics.StatisticsDocUnitCountDTO;
import fr.progilone.pgcn.domain.dto.statistics.StatisticsDocUnitStatusRatioDTO;
import fr.progilone.pgcn.domain.dto.statistics.StatisticsProcessedDocUnitDTO;
import fr.progilone.pgcn.domain.workflow.WorkflowStateKey;
import fr.progilone.pgcn.service.document.ui.UIDocUnitService;
import fr.progilone.pgcn.service.exchange.cines.ui.UICinesReportService;
import fr.progilone.pgcn.service.exchange.internetarchive.ui.UIInternetArchiveReportService;
import fr.progilone.pgcn.service.statistics.StatisticsDocumentService;
import fr.progilone.pgcn.service.statistics.StatisticsService;
import fr.progilone.pgcn.web.util.AccessHelper;
import fr.progilone.pgcn.web.util.LibraryAccesssHelper;
import jakarta.annotation.security.PermitAll;
import jakarta.servlet.http.HttpServletRequest;
import java.time.LocalDate;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
@RequestMapping(value = "/api/rest/statistics/docunit")
@PermitAll
public class StatisticsDocUnitController {

    private static final Logger LOG = LoggerFactory.getLogger(StatisticsDocUnitController.class);

    private final LibraryAccesssHelper libraryAccesssHelper;
    private final UICinesReportService uiCinesReportService;
    private final UIDocUnitService uiDocUnitService;
    private final UIInternetArchiveReportService uiInternetArchiveReportService;
    private final StatisticsService uiStatService;
    private final StatisticsDocumentService statisticsDocumentService;
    private final AccessHelper accessHelper;

    public StatisticsDocUnitController(final LibraryAccesssHelper libraryAccesssHelper,
                                       final UICinesReportService uiCinesReportService,
                                       final UIDocUnitService uiDocUnitService,
                                       final UIInternetArchiveReportService uiInternetArchiveReportService,
                                       final StatisticsService uiStatService,
                                       final StatisticsDocumentService statisticsDocumentService,
                                       final AccessHelper accessHelper) {
        this.libraryAccesssHelper = libraryAccesssHelper;
        this.uiCinesReportService = uiCinesReportService;
        this.uiDocUnitService = uiDocUnitService;
        this.uiInternetArchiveReportService = uiInternetArchiveReportService;
        this.uiStatService = uiStatService;
        this.statisticsDocumentService = statisticsDocumentService;
        this.accessHelper = accessHelper;
    }

    @RequestMapping(method = RequestMethod.GET, params = {"count"}, produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Page<StatisticsDocUnitCountDTO>> getDocUnitList(final HttpServletRequest request,
                                                                          @RequestParam(value = "libraries", required = false) final List<String> libraries,
                                                                          @RequestParam(value = "project", required = false) final List<String> projects,
                                                                          @RequestParam(value = "lot", required = false) final List<String> lots,
                                                                          @RequestParam(value = "page", required = false, defaultValue = "0") final Integer page,
                                                                          @RequestParam(value = "size", required = false, defaultValue = "10") final Integer size) {
        // Droits d'accès
        final List<String> filteredLibraries = libraryAccesssHelper.getLibraryFilter(request, libraries);
        final Page<StatisticsDocUnitCountDTO> docUnits = uiStatService.getDocUnits(filteredLibraries, projects, lots, page, size);
        return new ResponseEntity<>(docUnits, HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.GET, params = {"countStatus"}, produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<StatisticsDocUnitStatusRatioDTO> getDocUnitStatusRatio(final HttpServletRequest request,
                                                                                 @RequestParam(value = "project", required = false) final String project,
                                                                                 @RequestParam(value = "lot", required = false) final String lot,
                                                                                 @RequestParam(value = "state") final WorkflowStateKey state) {
        // Paramètres
        if (StringUtils.isBlank(project) && StringUtils.isBlank(lot)) {
            LOG.error("Le projet et le lot ne sont pas renseignés");
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        // Droits d'accès
        final List<String> filteredLibraries = libraryAccesssHelper.getLibraryFilter(request, null);
        final StatisticsDocUnitStatusRatioDTO dto = uiStatService.getDocUnitStatusRatio(filteredLibraries, project, lot, state);
        return new ResponseEntity<>(dto, HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.GET, params = {"average"}, produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<StatisticsDocUnitAverageDTO>> getDocUnitAverages(final HttpServletRequest request,
                                                                                @RequestParam(value = "library", required = false) final List<String> libraries,
                                                                                @RequestParam(value = "project", required = false) final List<String> projects,
                                                                                @RequestParam(value = "lot", required = false) final List<String> lots,
                                                                                @RequestParam(value = "delivery", required = false) final List<String> deliveries,
                                                                                @DateTimeFormat(pattern = "yyyy-MM-dd") @RequestParam(name = "from",
                                                                                                                                      required = false) final LocalDate fromDate,
                                                                                @DateTimeFormat(pattern = "yyyy-MM-dd") @RequestParam(name = "to",
                                                                                                                                      required = false) final LocalDate toDate,
                                                                                @RequestParam(value = "groupby",
                                                                                              required = false,
                                                                                              defaultValue = "PROJECT") final StatisticsService.GroupBy groupBy) {
        // Droits d'accès
        if (accessHelper.checkUserIsPresta()) { // no presta
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        final List<String> filteredLibraries = libraryAccesssHelper.getLibraryFilter(request, libraries);
        final List<StatisticsDocUnitAverageDTO> docUnits = uiStatService.getDocUnitAverages(filteredLibraries, projects, lots, deliveries, fromDate, toDate, groupBy);
        return new ResponseEntity<>(docUnits, HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.GET, params = {"export"}, produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<StatisticsProcessedDocUnitDTO>> getExportedDocUnitList(final HttpServletRequest request,
                                                                                      @RequestParam(value = "libraries", required = false) final List<String> libraries,
                                                                                      @DateTimeFormat(pattern = "yyyy-MM-dd") @RequestParam(name = "from") final LocalDate fromDate,
                                                                                      @RequestParam(name = "failures", defaultValue = "false") final boolean failures) {
        // Droits d'accès
        final List<String> filteredLibraries = libraryAccesssHelper.getLibraryFilter(request, libraries);
        return new ResponseEntity<>(uiInternetArchiveReportService.findAll(filteredLibraries, fromDate, failures), HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.GET, params = {"archive"}, produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<StatisticsProcessedDocUnitDTO>> getArchivedDocUnitList(final HttpServletRequest request,
                                                                                      @RequestParam(value = "libraries", required = false) final List<String> libraries,
                                                                                      @DateTimeFormat(pattern = "yyyy-MM-dd") @RequestParam(name = "from") final LocalDate fromDate,
                                                                                      @RequestParam(name = "failures", defaultValue = "false") final boolean failures) {
        // Droits d'accès
        final List<String> filteredLibraries = libraryAccesssHelper.getLibraryFilter(request, libraries);
        return new ResponseEntity<>(uiCinesReportService.findAll(filteredLibraries, fromDate, failures), HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.GET, params = {"checkdelay"}, produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<?> getCheckDelayStatisitics(final HttpServletRequest request,
                                                      @RequestParam(value = "libraries", required = false) final List<String> libraries,
                                                      @RequestParam(value = "project", required = false) final List<String> projects,
                                                      @RequestParam(value = "lot", required = false) final List<String> lots,
                                                      @RequestParam(value = "delivery", required = false) final List<String> deliveries,
                                                      @RequestParam(value = "groupby", required = false, defaultValue = "PROJECT") final StatisticsService.GroupBy groupBy) {
        // Droits d'accès
        final List<String> filteredLibraries = libraryAccesssHelper.getLibraryFilter(request, libraries);
        return new ResponseEntity<>(uiStatService.getCheckDelayStatisitics(filteredLibraries, projects, lots, deliveries, groupBy), HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.GET, params = {"doc_published"}, produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Page<StatisticsDocPublishedDTO>> getDocPublishedStat(final HttpServletRequest request,
                                                                               @RequestParam(value = "library", required = false) final List<String> libraries,
                                                                               @RequestParam(value = "project", required = false) final List<String> projects,
                                                                               @RequestParam(value = "lot", required = false) final List<String> lots,
                                                                               @DateTimeFormat(pattern = "yyyy-MM-dd") @RequestParam(name = "from",
                                                                                                                                     required = false) final LocalDate fromDate,
                                                                               @DateTimeFormat(pattern = "yyyy-MM-dd") @RequestParam(name = "to",
                                                                                                                                     required = false) final LocalDate toDate,
                                                                               @RequestParam(value = "type", required = false) final List<String> types,
                                                                               @RequestParam(value = "collection", required = false) final List<String> collections,
                                                                               @RequestParam(value = "page", required = false, defaultValue = "0") final Integer page,
                                                                               @RequestParam(value = "size", required = false, defaultValue = "10") final Integer size) {
        // Droits d'accès
        if (accessHelper.checkUserIsPresta()) { // no presta
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        final List<String> filteredLibraries = libraryAccesssHelper.getLibraryFilter(request, libraries);
        return new ResponseEntity<>(statisticsDocumentService.getDocPublishedStat(filteredLibraries, projects, lots, fromDate, toDate, types, collections, page, size),
                                    HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.GET, params = {"doc_rejected"}, produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Page<StatisticsDocRejectedDTO>> getDocRejectedStat(final HttpServletRequest request,
                                                                             @RequestParam(value = "library", required = false) final List<String> libraries,
                                                                             @RequestParam(value = "project", required = false) final List<String> projects,
                                                                             @RequestParam(value = "provider", required = false) final List<String> providers,
                                                                             @DateTimeFormat(pattern = "yyyy-MM-dd") @RequestParam(name = "from",
                                                                                                                                   required = false) final LocalDate fromDate,
                                                                             @DateTimeFormat(pattern = "yyyy-MM-dd") @RequestParam(name = "to",
                                                                                                                                   required = false) final LocalDate toDate,
                                                                             @RequestParam(value = "page", required = false, defaultValue = "0") final Integer page,
                                                                             @RequestParam(value = "size", required = false, defaultValue = "10") final Integer size) {
        // Droits d'accès
        if (accessHelper.checkUserIsPresta()) { // no presta
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        final List<String> filteredLibraries = libraryAccesssHelper.getLibraryFilter(request, libraries);
        return new ResponseEntity<>(statisticsDocumentService.getDocRejectedStats(filteredLibraries, projects, providers, fromDate, toDate, page, size), HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.GET, params = {"doc_types"}, produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Page<String>> getDocUnitTypes(@RequestParam(value = "search", required = false) final String search,
                                                        @RequestParam(value = "page", required = false, defaultValue = "0") final Integer page,
                                                        @RequestParam(value = "size", required = false, defaultValue = "10") final Integer size) {
        return new ResponseEntity<>(uiDocUnitService.getDistinctTypes(search, page, size), HttpStatus.OK);
    }
}
