package fr.progilone.pgcn.web.rest.statistics.csv;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

import javax.annotation.security.PermitAll;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.codahale.metrics.annotation.Timed;

import fr.progilone.pgcn.domain.dto.statistics.StatisticsProviderDeliveryDTO;
import fr.progilone.pgcn.exception.PgcnTechnicalException;
import fr.progilone.pgcn.service.exchange.csv.ExportCSVService;
import fr.progilone.pgcn.web.rest.AbstractRestController;
import fr.progilone.pgcn.web.rest.statistics.StatisticsDeliveryController;
import fr.progilone.pgcn.web.util.AccessHelper;

@Controller
@RequestMapping(value = "/api/rest/statistics/delivery/csv")
@PermitAll
public class StatisticsDeliveryCsvController extends AbstractRestController {

    private static final Logger LOG = LoggerFactory.getLogger(StatisticsDeliveryCsvController.class);
    private static final String FILENAME = "export.csv";

    private final ExportCSVService statisticsCsvService;
    private final StatisticsDeliveryController delegate;
    private final AccessHelper accessHelper;

    public StatisticsDeliveryCsvController(final ExportCSVService statisticsCsvService,
                                           final StatisticsDeliveryController delegate,
                                           final AccessHelper accessHelper) {
        this.statisticsCsvService = statisticsCsvService;
        this.delegate = delegate;
        this.accessHelper = accessHelper;
    }

    @RequestMapping(method = RequestMethod.GET, params = {"provider_delivery"}, produces = "text/csv")
    @Timed
    @ResponseStatus(HttpStatus.OK)
    public void getProviderDeliveryStats(final HttpServletRequest request,
                                         final HttpServletResponse response,
                                         @RequestParam(value = "library", required = false) final List<String> libraries,
                                         @RequestParam(value = "provider", required = false) final List<String> providers,
                                         @DateTimeFormat(pattern = "yyyy-MM-dd") @RequestParam(name = "from", required = false)
                                         final LocalDate fromDate,
                                         @DateTimeFormat(pattern = "yyyy-MM-dd") @RequestParam(name = "to", required = false) final LocalDate toDate,
                                         @RequestParam(value = "encoding", defaultValue = "ISO-8859-15") final String encoding,
                                         @RequestParam(value = "separator", defaultValue = ";") final char separator) throws PgcnTechnicalException {

        if (accessHelper.checkUserIsPresta()) { //  no presta
            return;
        }
        final ResponseEntity<List<StatisticsProviderDeliveryDTO>> result =
            delegate.getProviderDeliveryStats(request, libraries, providers, fromDate, toDate);

        try {
            writeResponseHeaderForDownload(response, "text/csv; charset=" + encoding, null, FILENAME);
            statisticsCsvService.exportOrderedBeans(result.getBody(), response.getOutputStream(), encoding, separator);

        } catch (final IOException e) {
            LOG.error(e.getMessage(), e);
            throw new PgcnTechnicalException(e);
        }
    }
}
