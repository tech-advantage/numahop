package fr.progilone.pgcn.web.rest.statistics.csv;

import com.codahale.metrics.annotation.Timed;
import fr.progilone.pgcn.domain.dto.statistics.StatisticsDocPublishedDTO;
import fr.progilone.pgcn.domain.dto.statistics.StatisticsDocRejectedDTO;
import fr.progilone.pgcn.domain.dto.statistics.StatisticsDocUnitAverageDTO;
import fr.progilone.pgcn.exception.PgcnTechnicalException;
import fr.progilone.pgcn.service.exchange.csv.ExportCSVService;
import fr.progilone.pgcn.service.statistics.StatisticsService;
import fr.progilone.pgcn.web.rest.AbstractRestController;
import fr.progilone.pgcn.web.rest.statistics.StatisticsDocUnitController;
import fr.progilone.pgcn.web.util.AccessHelper;
import jakarta.annotation.security.PermitAll;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
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

@Controller
@RequestMapping(value = "/api/rest/statistics/docunit/csv")
@PermitAll
public class StatisticsDocUnitCsvController extends AbstractRestController {

    private static final Logger LOG = LoggerFactory.getLogger(StatisticsWorkflowCsvController.class);
    private static final String FILENAME = "export.csv";

    private final AccessHelper accessHelper;
    private final ExportCSVService statisticsCsvService;
    private final StatisticsDocUnitController delegate;

    @Autowired
    public StatisticsDocUnitCsvController(final AccessHelper accessHelper, final ExportCSVService statisticsCsvService, final StatisticsDocUnitController delegate) {
        this.accessHelper = accessHelper;
        this.statisticsCsvService = statisticsCsvService;
        this.delegate = delegate;
    }

    @RequestMapping(method = RequestMethod.GET, params = {"average"}, produces = "text/csv")
    @Timed
    @ResponseStatus(HttpStatus.OK)
    public void getDocUnitAverages(final HttpServletRequest request,
                                   final HttpServletResponse response,
                                   @RequestParam(value = "library", required = false) final List<String> libraries,
                                   @RequestParam(value = "project", required = false) final List<String> projects,
                                   @RequestParam(value = "lot", required = false) final List<String> lots,
                                   @RequestParam(value = "delivery", required = false) final List<String> deliveries,
                                   @DateTimeFormat(pattern = "yyyy-MM-dd") @RequestParam(name = "from", required = false) final LocalDate fromDate,
                                   @DateTimeFormat(pattern = "yyyy-MM-dd") @RequestParam(name = "to", required = false) final LocalDate toDate,
                                   @RequestParam(value = "groupby", required = false, defaultValue = "PROJECT") final StatisticsService.GroupBy groupBy,
                                   @RequestParam(value = "encoding", defaultValue = "ISO-8859-15") final String encoding,
                                   @RequestParam(value = "separator", defaultValue = ";") final char separator) throws PgcnTechnicalException {
        final ResponseEntity<List<StatisticsDocUnitAverageDTO>> result = delegate.getDocUnitAverages(request, libraries, projects, lots, deliveries, fromDate, toDate, groupBy);

        try {
            writeResponseHeaderForDownload(response, "text/csv; charset=" + encoding, null, FILENAME);
            statisticsCsvService.exportOrderedBeans(result.getBody(), response.getOutputStream(), encoding, separator);

        } catch (IOException e) {
            LOG.error(e.getMessage(), e);
            throw new PgcnTechnicalException(e);
        }
    }

    @RequestMapping(method = RequestMethod.GET, params = {"doc_published"}, produces = "text/csv")
    @Timed
    public void getDocPublishedStat(final HttpServletRequest request,
                                    final HttpServletResponse response,
                                    @RequestParam(value = "library", required = false) final List<String> libraries,
                                    @RequestParam(value = "project", required = false) final List<String> projects,
                                    @RequestParam(value = "lot", required = false) final List<String> lots,
                                    @DateTimeFormat(pattern = "yyyy-MM-dd") @RequestParam(name = "from", required = false) final LocalDate fromDate,
                                    @DateTimeFormat(pattern = "yyyy-MM-dd") @RequestParam(name = "to", required = false) final LocalDate toDate,
                                    @RequestParam(value = "type", required = false) final List<String> types,
                                    @RequestParam(value = "collection", required = false) final List<String> collections,
                                    @RequestParam(value = "encoding", defaultValue = "ISO-8859-15") final String encoding,
                                    @RequestParam(value = "separator", defaultValue = ";") final char separator) throws PgcnTechnicalException {
        // Droits d'accès
        if (accessHelper.checkUserIsPresta()) { // no presta
            return;
        }
        final ResponseEntity<Page<StatisticsDocPublishedDTO>> result = delegate.getDocPublishedStat(request,
                                                                                                    libraries,
                                                                                                    projects,
                                                                                                    lots,
                                                                                                    fromDate,
                                                                                                    toDate,
                                                                                                    types,
                                                                                                    collections,
                                                                                                    0,
                                                                                                    Integer.MAX_VALUE);
        final List<StatisticsDocPublishedDTO> dtos = new ArrayList<>(result.getBody().getContent());

        try {
            writeResponseHeaderForDownload(response, "text/csv; charset=" + encoding, null, FILENAME);
            statisticsCsvService.exportOrderedBeans(dtos, response.getOutputStream(), encoding, separator);

        } catch (final IOException e) {
            LOG.error(e.getMessage(), e);
            throw new PgcnTechnicalException(e);
        }
    }

    @RequestMapping(method = RequestMethod.GET, params = {"doc_rejected"}, produces = "text/csv")
    @Timed
    public void getDocRejectedStat(final HttpServletRequest request,
                                   final HttpServletResponse response,
                                   @RequestParam(value = "library", required = false) final List<String> libraries,
                                   @RequestParam(value = "project", required = false) final List<String> projects,
                                   @RequestParam(value = "provider", required = false) final List<String> providers,
                                   @DateTimeFormat(pattern = "yyyy-MM-dd") @RequestParam(name = "from", required = false) final LocalDate fromDate,
                                   @DateTimeFormat(pattern = "yyyy-MM-dd") @RequestParam(name = "to", required = false) final LocalDate toDate,
                                   @RequestParam(value = "encoding", defaultValue = "ISO-8859-15") final String encoding,
                                   @RequestParam(value = "separator", defaultValue = ";") final char separator) throws PgcnTechnicalException {
        // Droits d'accès
        if (accessHelper.checkUserIsPresta()) { // no presta
            return;
        }
        final ResponseEntity<Page<StatisticsDocRejectedDTO>> result = delegate.getDocRejectedStat(request, libraries, projects, providers, fromDate, toDate, 0, Integer.MAX_VALUE);
        final List<StatisticsDocRejectedDTO> dtos = new ArrayList<>(result.getBody().getContent());

        try {
            writeResponseHeaderForDownload(response, "text/csv; charset=" + encoding, null, FILENAME);
            statisticsCsvService.exportOrderedBeans(dtos, response.getOutputStream(), encoding, separator);

        } catch (final IOException e) {
            LOG.error(e.getMessage(), e);
            throw new PgcnTechnicalException(e);
        }
    }
}
