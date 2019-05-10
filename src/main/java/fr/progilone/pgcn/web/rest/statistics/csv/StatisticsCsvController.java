package fr.progilone.pgcn.web.rest.statistics.csv;

import com.codahale.metrics.annotation.Timed;
import fr.progilone.pgcn.domain.dto.statistics.StatisticsProgressDTO;
import fr.progilone.pgcn.domain.dto.statistics.StatisticsProviderTrainDTO;
import fr.progilone.pgcn.domain.train.Train;
import fr.progilone.pgcn.exception.PgcnTechnicalException;
import fr.progilone.pgcn.service.exchange.csv.ExportCSVService;
import fr.progilone.pgcn.web.rest.AbstractRestController;
import fr.progilone.pgcn.web.rest.statistics.StatisticsController;
import fr.progilone.pgcn.web.util.AccessHelper;
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

import javax.annotation.security.PermitAll;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping(value = "/api/rest/statistics/csv")
@PermitAll
public class StatisticsCsvController extends AbstractRestController {

    private static final Logger LOG = LoggerFactory.getLogger(StatisticsWorkflowCsvController.class);
    private static final String FILENAME = "export.csv";

    private final ExportCSVService statisticsCsvService;
    private final StatisticsController delegate;
    private final AccessHelper accessHelper;

    @Autowired
    public StatisticsCsvController(final ExportCSVService statisticsCsvService,
                                   final StatisticsController delegate,
                                   final AccessHelper accessHelper) {
        this.statisticsCsvService = statisticsCsvService;
        this.delegate = delegate;
        this.accessHelper = accessHelper;
    }

    @RequestMapping(method = RequestMethod.GET, params = {"provider_train"}, produces = "text/csv")
    @Timed
    @ResponseStatus(HttpStatus.OK)
    public void getProviderTrainStats(final HttpServletRequest request,
                                      final HttpServletResponse response,
                                      @RequestParam(value = "library", required = false) final List<String> libraries,
                                      @RequestParam(value = "project", required = false) final List<String> projects,
                                      @RequestParam(value = "train", required = false) final List<String> trains,
                                      @RequestParam(value = "status", required = false) final List<Train.TrainStatus> status,
                                      @DateTimeFormat(pattern = "yyyy-MM-dd") @RequestParam(name = "returnFrom", required = false)
                                      final LocalDate returnFrom,
                                      @DateTimeFormat(pattern = "yyyy-MM-dd") @RequestParam(name = "returnTo", required = false)
                                      final LocalDate returnTo,
                                      @DateTimeFormat(pattern = "yyyy-MM-dd") @RequestParam(name = "sendFrom", required = false)
                                      final LocalDate sendFrom,
                                      @DateTimeFormat(pattern = "yyyy-MM-dd") @RequestParam(name = "sendTo", required = false) final LocalDate sendTo,
                                      @RequestParam(name = "insuranceFrom", required = false) final Double insuranceFrom,
                                      @RequestParam(name = "insuranceTo", required = false) final Double insuranceTo,
                                      @RequestParam(value = "encoding", defaultValue = "ISO-8859-15") final String encoding,
                                      @RequestParam(value = "separator", defaultValue = ";") final char separator) throws PgcnTechnicalException {

        if (accessHelper.checkUserIsPresta()) { //  no presta
            return;
        }
        final ResponseEntity<List<StatisticsProviderTrainDTO>> result = delegate.getProviderTrainStats(request,
                                                                                                       libraries,
                                                                                                       projects,
                                                                                                       trains,
                                                                                                       status,
                                                                                                       returnFrom,
                                                                                                       returnTo,
                                                                                                       sendFrom,
                                                                                                       sendTo,
                                                                                                       insuranceFrom,
                                                                                                       insuranceTo);
        try {
            writeResponseHeaderForDownload(response, "text/csv; charset=" + encoding, null, FILENAME);
            statisticsCsvService.exportOrderedBeans(result.getBody(), response.getOutputStream(), encoding, separator);

        } catch (final IOException e) {
            LOG.error(e.getMessage(), e);
            throw new PgcnTechnicalException(e);
        }
    }

    @RequestMapping(method = RequestMethod.GET, params = {"lotProgress"}, produces = "text/csv")
    @Timed
    public void getLotProgress(final HttpServletRequest request,
                               final HttpServletResponse response,
                               @RequestParam(value = "library", required = false) final List<String> libraries,
                               @RequestParam(value = "project", required = false) final List<String> projects,
                               @DateTimeFormat(pattern = "yyyy-MM-dd") @RequestParam(name = "from", required = false) final LocalDate fromDate,
                               @DateTimeFormat(pattern = "yyyy-MM-dd") @RequestParam(name = "to", required = false) final LocalDate toDate,
                               @RequestParam(value = "encoding", defaultValue = "ISO-8859-15") final String encoding,
                               @RequestParam(value = "separator", defaultValue = ";") final char separator) throws PgcnTechnicalException {
        if (accessHelper.checkUserIsPresta()) { //  no presta
            return;
        }
        final ResponseEntity<Page<StatisticsProgressDTO>> pageOfLots =
            delegate.getLotProgress(request, libraries, projects, fromDate, toDate, 0, Integer.MAX_VALUE);
        final ResponseEntity<Page<StatisticsProgressDTO>> pageOfProjects =
            delegate.getProjectProgress(request, libraries, projects, fromDate, toDate, 0, Integer.MAX_VALUE);

        final List<StatisticsProgressDTO> dtos = new ArrayList<>(pageOfLots.getBody().getContent());
        dtos.addAll(pageOfProjects.getBody().getContent());

        try {
            writeResponseHeaderForDownload(response, "text/csv; charset=" + encoding, null, FILENAME);
            statisticsCsvService.exportOrderedBeans(dtos, response.getOutputStream(), encoding, separator);

        } catch (IOException e) {
            LOG.error(e.getMessage(), e);
            throw new PgcnTechnicalException(e);
        }
    }

    @RequestMapping(method = RequestMethod.GET, params = {"projectProgress"}, produces = "text/csv")
    @Timed
    public void getProjectProgress(final HttpServletRequest request,
                                   final HttpServletResponse response,
                                   @RequestParam(value = "library", required = false) final List<String> libraries,
                                   @RequestParam(value = "project", required = false) final List<String> projects,
                                   @DateTimeFormat(pattern = "yyyy-MM-dd") @RequestParam(name = "from", required = false) final LocalDate fromDate,
                                   @DateTimeFormat(pattern = "yyyy-MM-dd") @RequestParam(name = "to", required = false) final LocalDate toDate,
                                   @RequestParam(value = "encoding", defaultValue = "ISO-8859-15") final String encoding,
                                   @RequestParam(value = "separator", defaultValue = ";") final char separator) throws PgcnTechnicalException {
        if (accessHelper.checkUserIsPresta()) { //  no presta
            return;
        }
        final ResponseEntity<Page<StatisticsProgressDTO>> result =
            delegate.getProjectProgress(request, libraries, projects, fromDate, toDate, 0, Integer.MAX_VALUE);
        final List<StatisticsProgressDTO> dtos = new ArrayList<>(result.getBody().getContent());

        try {
            writeResponseHeaderForDownload(response, "text/csv; charset=" + encoding, null, FILENAME);
            statisticsCsvService.exportOrderedBeans(dtos, response.getOutputStream(), encoding, separator);

        } catch (IOException e) {
            LOG.error(e.getMessage(), e);
            throw new PgcnTechnicalException(e);
        }
    }
}
