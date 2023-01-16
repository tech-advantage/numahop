package fr.progilone.pgcn.web.rest.statistics.csv;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.security.PermitAll;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.codahale.metrics.annotation.Timed;

import fr.progilone.pgcn.domain.dto.statistics.WorkflowDeliveryProgressDTO;
import fr.progilone.pgcn.domain.dto.statistics.WorkflowDocUnitProgressDTO;
import fr.progilone.pgcn.domain.dto.statistics.WorkflowProfileActivityDTO;
import fr.progilone.pgcn.domain.dto.statistics.WorkflowStateProgressDTO;
import fr.progilone.pgcn.domain.dto.statistics.WorkflowUserActivityDTO;
import fr.progilone.pgcn.domain.dto.statistics.WorkflowUserProgressDTO;
import fr.progilone.pgcn.domain.dto.statistics.csv.WorkflowDeliveryProgressCsvDTO;
import fr.progilone.pgcn.domain.dto.statistics.csv.WorkflowDocUnitProgressCsvDTO;
import fr.progilone.pgcn.domain.workflow.WorkflowStateKey;
import fr.progilone.pgcn.domain.workflow.WorkflowStateStatus;
import fr.progilone.pgcn.exception.PgcnTechnicalException;
import fr.progilone.pgcn.service.exchange.csv.ExportCSVService;
import fr.progilone.pgcn.service.statistics.mapper.StatisticsMapper;
import fr.progilone.pgcn.web.rest.AbstractRestController;
import fr.progilone.pgcn.web.rest.statistics.StatisticsWorkflowController;

@Controller
@RequestMapping(value = "/api/rest/statistics/workflow/csv")
@PermitAll
public class StatisticsWorkflowCsvController extends AbstractRestController {

    private static final Logger LOG = LoggerFactory.getLogger(StatisticsWorkflowCsvController.class);
    private static final String FILENAME = "export.csv";

    private final ExportCSVService statisticsCsvService;
    private final StatisticsWorkflowController delegate;

    @Autowired
    public StatisticsWorkflowCsvController(final StatisticsWorkflowController delegate, final ExportCSVService statisticsCsvService) {
        this.statisticsCsvService = statisticsCsvService;
        this.delegate = delegate;
    }

    @RequestMapping(method = RequestMethod.GET, params = {"wdelivery"}, produces = "text/csv")
    @Timed
    @ResponseStatus(HttpStatus.OK)
    public void getWorkflowDeliveryProgressStatistics(final HttpServletRequest request,
                                                      final HttpServletResponse response,
                                                      @RequestParam(value = "library", required = false) final List<String> libraries,
                                                      @RequestParam(value = "project", required = false) final List<String> projects,
                                                      @RequestParam(value = "lot", required = false) final List<String> lots,
                                                      @RequestParam(value = "delivery", required = false) final List<String> deliveries,
                                                      @RequestParam(value = "pgcnid", required = false) final String pgcnId,
                                                      @RequestParam(value = "state", required = false) final List<WorkflowStateKey> states,
                                                      @DateTimeFormat(pattern = "yyyy-MM-dd") @RequestParam(name = "from", required = false)
                                                      final LocalDate fromDate,
                                                      @DateTimeFormat(pattern = "yyyy-MM-dd") @RequestParam(name = "to", required = false)
                                                      final LocalDate toDate,
                                                      @RequestParam(value = "encoding", defaultValue = "ISO-8859-15") final String encoding,
                                                      @RequestParam(value = "separator", defaultValue = ";") final char separator) throws
                                                                                                                                   PgcnTechnicalException {
        final ResponseEntity<Page<WorkflowDeliveryProgressDTO>> result = delegate.getWorkflowDeliveryProgressStatistics(request,
                                                                                                                        libraries,
                                                                                                                        projects,
                                                                                                                        lots,
                                                                                                                        deliveries,
                                                                                                                        pgcnId,
                                                                                                                        states,
                                                                                                                        fromDate,
                                                                                                                        toDate,
                                                                                                                        0,
                                                                                                                        Integer.MAX_VALUE);
        final List<WorkflowDeliveryProgressDTO> body = result.getBody().getContent();
        final List<WorkflowDeliveryProgressCsvDTO> dtos = StatisticsMapper.toWorkflowDeliveryProgressCsvDTO(body);

        try {
            writeResponseHeaderForDownload(response, "text/csv; charset=" + encoding, null, FILENAME);
            statisticsCsvService.exportOrderedBeans(dtos, response.getOutputStream(), encoding, separator);

        } catch (final IOException e) {
            LOG.error(e.getMessage(), e);
            throw new PgcnTechnicalException(e);
        }
    }

    @RequestMapping(method = RequestMethod.GET, params = {"wdocunit"}, produces = "text/csv")
    @Timed
    @ResponseStatus(HttpStatus.OK)
    public void getWorkflowDocUnitProgressStatistics(final HttpServletRequest request,
                                                     final HttpServletResponse response,
                                                     @RequestParam(value = "library", required = false) final List<String> libraries,
                                                     @RequestParam(value = "project", required = false) final List<String> projects,
                                                     @RequestParam(value = "lot", required = false) final List<String> lots,
                                                     @RequestParam(value = "train", required = false) final List<String> trains,
                                                     @RequestParam(value = "pgcnid", required = false) final String pgcnId,
                                                     @RequestParam(value = "state", required = false) final List<WorkflowStateKey> states,
                                                     @RequestParam(value = "status", required = false) final List<WorkflowStateStatus> status,
                                                     @RequestParam(value = "mine", required = false, defaultValue = "false") final boolean onlyMine,
                                                     @DateTimeFormat(pattern = "yyyy-MM-dd") @RequestParam(name = "from", required = false)
                                                     final LocalDate fromDate,
                                                     @DateTimeFormat(pattern = "yyyy-MM-dd") @RequestParam(name = "to", required = false)
                                                     final LocalDate toDate,
                                                     @RequestParam(value = "encoding", defaultValue = "ISO-8859-15") final String encoding,
                                                     @RequestParam(value = "separator", defaultValue = ";") final char separator) throws
                                                                                                                                  PgcnTechnicalException {
        final ResponseEntity<Page<WorkflowDocUnitProgressDTO>> result = delegate.getWorkflowDocUnitProgressStatistics(request,
                                                                                                                      libraries,
                                                                                                                      projects,
                                                                                                                      false,
                                                                                                                      lots,
                                                                                                                      trains,
                                                                                                                      pgcnId,
                                                                                                                      states,
                                                                                                                      status,
                                                                                                                      onlyMine,
                                                                                                                      fromDate,
                                                                                                                      toDate,
                                                                                                                      0,
                                                                                                                      Integer.MAX_VALUE);

        final List<WorkflowDocUnitProgressDTO> body = result.getBody().getContent();
        final List<WorkflowDocUnitProgressCsvDTO> dtos = StatisticsMapper.toWorkflowDocUnitProgressCsvDTO(body);

        try {
            writeResponseHeaderForDownload(response, "text/csv; charset=" + encoding, null, FILENAME);
            statisticsCsvService.exportOrderedBeans(dtos, response.getOutputStream(), encoding, separator);

        } catch (final IOException e) {
            LOG.error(e.getMessage(), e);
            throw new PgcnTechnicalException(e);
        }
    }

    @RequestMapping(method = RequestMethod.GET, params = {"wstate"}, produces = "text/csv")
    @Timed
    @ResponseStatus(HttpStatus.OK)
    public void getWorkflowStatesStatistics(final HttpServletRequest request,
                                            final HttpServletResponse response,
                                            @RequestParam(value = "library", required = false) final List<String> libraries,
                                            @RequestParam(value = "workflow", required = false) final List<String> workflows,
                                            @RequestParam(value = "state", required = false) final List<WorkflowStateKey> states,
                                            @DateTimeFormat(pattern = "yyyy-MM-dd") @RequestParam(name = "from", required = false)
                                            final LocalDate fromDate,
                                            @DateTimeFormat(pattern = "yyyy-MM-dd") @RequestParam(name = "to", required = false)
                                            final LocalDate toDate,
                                            @RequestParam(value = "encoding", defaultValue = "ISO-8859-15") final String encoding,
                                            @RequestParam(value = "separator", defaultValue = ";") final char separator) throws
                                                                                                                         PgcnTechnicalException {
        final ResponseEntity<List<WorkflowStateProgressDTO>> result =
            delegate.getWorkflowStatesStatistics(request, libraries, workflows, states, fromDate, toDate);

        try {
            writeResponseHeaderForDownload(response, "text/csv; charset=" + encoding, null, FILENAME);
            statisticsCsvService.exportOrderedBeans(result.getBody(), response.getOutputStream(), encoding, separator);

        } catch (final IOException e) {
            LOG.error(e.getMessage(), e);
            throw new PgcnTechnicalException(e);
        }
    }

    @RequestMapping(method = RequestMethod.GET, params = {"wuser"}, produces = "text/csv")
    @Timed
    @ResponseStatus(HttpStatus.OK)
    public void getWorkflowUsersStatistics(final HttpServletRequest request,
                                           final HttpServletResponse response,
                                           @RequestParam(value = "library", required = false) final List<String> libraries,
                                           @RequestParam(value = "project", required = false) final List<String> projects,
                                           @RequestParam(value = "lot", required = false) final List<String> lots,
                                           @RequestParam(value = "delivery", required = false) final List<String> deliveries,
                                           @DateTimeFormat(pattern = "yyyy-MM-dd") @RequestParam(name = "from", required = false)
                                           final LocalDate fromDate,
                                           @DateTimeFormat(pattern = "yyyy-MM-dd") @RequestParam(name = "to", required = false)
                                           final LocalDate toDate,
                                           @RequestParam(value = "encoding", defaultValue = "ISO-8859-15") final String encoding,
                                           @RequestParam(value = "separator", defaultValue = ";") final char separator) throws
                                                                                                                        PgcnTechnicalException {
        final ResponseEntity<Collection<WorkflowUserProgressDTO>> result =
            delegate.getWorkflowUsersStatistics(request, libraries, projects, lots, deliveries, fromDate, toDate);

        try {
            writeResponseHeaderForDownload(response, "text/csv; charset=" + encoding, null, FILENAME);
            statisticsCsvService.exportOrderedBeans(new ArrayList<>(result.getBody()), response.getOutputStream(), encoding, separator);

        } catch (final IOException e) {
            LOG.error(e.getMessage(), e);
            throw new PgcnTechnicalException(e);
        }
    }

    @RequestMapping(method = RequestMethod.GET, params = {"wprofile_activity"}, produces = "text/csv")
    @Timed
    @ResponseStatus(HttpStatus.OK)
    public void getProfilesActivityStatistics(final HttpServletRequest request,
                                              final HttpServletResponse response,
                                              @RequestParam(value = "library", required = false) final List<String> libraries,
                                              @RequestParam(value = "project", required = false) final List<String> projects,
                                              @RequestParam(value = "lot", required = false) final List<String> lots,
                                              @RequestParam(value = "state", required = false) final List<WorkflowStateKey> states,
                                              @RequestParam(value = "role", required = false) final List<String> roles,
                                              @DateTimeFormat(pattern = "yyyy-MM-dd") @RequestParam(name = "from", required = false) final
                                                  LocalDate fromDate,
                                              @DateTimeFormat(pattern = "yyyy-MM-dd") @RequestParam(name = "to", required = false)
                                              final LocalDate toDate,
                                              @RequestParam(value = "encoding", defaultValue = "ISO-8859-15") final String encoding,
                                              @RequestParam(value = "separator", defaultValue = ";") final char separator) throws
                                                                                                                           PgcnTechnicalException {
        final ResponseEntity<Collection<WorkflowProfileActivityDTO>> result =
            delegate.getProfilesActivityStatistics(request, libraries, projects, lots, states, roles, fromDate, toDate);

        try {
            writeResponseHeaderForDownload(response, "text/csv; charset=" + encoding, null, FILENAME);
            statisticsCsvService.exportOrderedBeans(new ArrayList<>(result.getBody()), response.getOutputStream(), encoding, separator);

        } catch (final IOException e) {
            LOG.error(e.getMessage(), e);
            throw new PgcnTechnicalException(e);
        }
    }

    @RequestMapping(method = RequestMethod.GET, params = {"wuser_activity"}, produces = "text/csv")
    @Timed
    @ResponseStatus(HttpStatus.OK)
    public void getUsersActivityStatistics(final HttpServletRequest request,
                                           final HttpServletResponse response,
                                           @RequestParam(value = "library", required = false) final List<String> libraries,
                                           @RequestParam(value = "project", required = false) final List<String> projects,
                                           @RequestParam(value = "lot", required = false) final List<String> lots,
                                           @RequestParam(value = "state", required = false) final List<WorkflowStateKey> states,
                                           @RequestParam(value = "role", required = false) final List<String> roles,
                                           @DateTimeFormat(pattern = "yyyy-MM-dd") @RequestParam(name = "from", required = false) final LocalDate fromDate,
                                           @DateTimeFormat(pattern = "yyyy-MM-dd") @RequestParam(name = "to", required = false)
                                           final LocalDate toDate,
                                           @RequestParam(value = "encoding", defaultValue = "ISO-8859-15") final String encoding,
                                           @RequestParam(value = "separator", defaultValue = ";") final char separator) throws
                                                                                                                        PgcnTechnicalException {
        final ResponseEntity<Collection<WorkflowUserActivityDTO>> result =
            delegate.getUsersActivityStatistics(request, libraries, projects, lots, states, roles, fromDate, toDate);

        try {
            writeResponseHeaderForDownload(response, "text/csv; charset=" + encoding, null, FILENAME);
            statisticsCsvService.exportOrderedBeans(new ArrayList<>(result.getBody()), response.getOutputStream(), encoding, separator);

        } catch (final IOException e) {
            LOG.error(e.getMessage(), e);
            throw new PgcnTechnicalException(e);
        }
    }
}
