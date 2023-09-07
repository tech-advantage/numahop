package fr.progilone.pgcn.web.rest.statistics;

import com.codahale.metrics.annotation.Timed;
import fr.progilone.pgcn.domain.dto.statistics.StatisticsProviderDeliveryDTO;
import fr.progilone.pgcn.service.delivery.ui.UIDeliveryService;
import fr.progilone.pgcn.web.util.AccessHelper;
import fr.progilone.pgcn.web.util.LibraryAccesssHelper;
import jakarta.annotation.security.PermitAll;
import jakarta.servlet.http.HttpServletRequest;
import java.time.LocalDate;
import java.util.List;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/api/rest/statistics/delivery")
@PermitAll
public class StatisticsDeliveryController {

    private final AccessHelper accessHelper;
    private final LibraryAccesssHelper libraryAccesssHelper;
    private final UIDeliveryService uiDeliveryService;

    public StatisticsDeliveryController(final AccessHelper accessHelper, final LibraryAccesssHelper libraryAccesssHelper, final UIDeliveryService uiDeliveryService) {
        this.accessHelper = accessHelper;
        this.libraryAccesssHelper = libraryAccesssHelper;
        this.uiDeliveryService = uiDeliveryService;
    }

    @RequestMapping(method = RequestMethod.GET, params = {"provider_delivery"}, produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<StatisticsProviderDeliveryDTO>> getProviderDeliveryStats(final HttpServletRequest request,
                                                                                        @RequestParam(value = "library", required = false) final List<String> libraries,
                                                                                        @RequestParam(value = "provider", required = false) final List<String> providers,
                                                                                        @DateTimeFormat(pattern = "yyyy-MM-dd") @RequestParam(name = "from",
                                                                                                                                              required = false) final LocalDate fromDate,
                                                                                        @DateTimeFormat(pattern = "yyyy-MM-dd") @RequestParam(name = "to",
                                                                                                                                              required = false) final LocalDate toDate) {
        // Droits d'accès
        if (accessHelper.checkUserIsPresta()) { // no presta
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        final List<String> filteredLibraries = libraryAccesssHelper.getLibraryFilter(request, libraries);
        return new ResponseEntity<>(uiDeliveryService.getProviderDeliveryStats(filteredLibraries, providers, fromDate, toDate), HttpStatus.OK);
    }
}
