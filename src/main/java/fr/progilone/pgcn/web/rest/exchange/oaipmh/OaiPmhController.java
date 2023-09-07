package fr.progilone.pgcn.web.rest.exchange.oaipmh;

import static fr.progilone.pgcn.web.rest.exchange.security.AuthorizationConstants.*;

import com.codahale.metrics.annotation.Timed;
import fr.progilone.pgcn.domain.exchange.DataEncoding;
import fr.progilone.pgcn.domain.exchange.FileFormat;
import fr.progilone.pgcn.domain.exchange.ImportReport;
import fr.progilone.pgcn.domain.exchange.ImportedDocUnit;
import fr.progilone.pgcn.domain.jaxb.oaipmh.IdentifyType;
import fr.progilone.pgcn.domain.jaxb.oaipmh.OAIPMHtype;
import fr.progilone.pgcn.service.exchange.ImportReportService;
import fr.progilone.pgcn.service.exchange.oaipmh.ImportOaiPmhService;
import fr.progilone.pgcn.service.exchange.oaipmh.OaiPmhRequest;
import fr.progilone.pgcn.service.exchange.oaipmh.OaiPmhService;
import fr.progilone.pgcn.web.rest.AbstractRestController;
import fr.progilone.pgcn.web.rest.exchange.ImportController;
import fr.progilone.pgcn.web.util.LibraryAccesssHelper;
import jakarta.annotation.security.RolesAllowed;
import jakarta.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Contrôleur gérant l'import OAI-PMH
 */
@RestController
@RequestMapping(value = "/api/rest/oaipmh")
public class OaiPmhController extends AbstractRestController {

    private static final Logger LOG = LoggerFactory.getLogger(ImportController.class);

    private final ImportOaiPmhService importOaiPmhService;
    private final ImportReportService importReportService;
    private final LibraryAccesssHelper libraryAccesssHelper;
    private final OaiPmhService oaiPmhService;

    public OaiPmhController(final ImportOaiPmhService importOaiPmhService,
                            final ImportReportService importReportService,
                            final LibraryAccesssHelper libraryAccesssHelper,
                            final OaiPmhService oaiPmhService) {
        this.importOaiPmhService = importOaiPmhService;
        this.importReportService = importReportService;
        this.libraryAccesssHelper = libraryAccesssHelper;
        this.oaiPmhService = oaiPmhService;
    }

    @RequestMapping(method = RequestMethod.GET, params = {"identify"}, produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @RolesAllowed({EXC_HAB2})
    public ResponseEntity<IdentifyType> checkServer(@RequestParam(value = "baseUrl") final String baseUrl) {
        final Optional<IdentifyType> response = oaiPmhService.identify(baseUrl).map(OAIPMHtype::getIdentify);
        return createResponseEntity(response);
    }

    @RequestMapping(method = RequestMethod.GET, params = {"listIdentifiers"}, produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @RolesAllowed({EXC_HAB2})
    public ResponseEntity<Map<String, String>> checkQuery(@RequestParam(value = "baseUrl") final String baseUrl,
                                                          @RequestParam(value = "from", required = false) final String from,
                                                          @RequestParam(value = "to", required = false) final String to,
                                                          @RequestParam(value = "set", required = false) final String set) {

        final Optional<Map<String, String>> response = oaiPmhService.listIdentifiers(baseUrl, "oai_dc", from, to, set, null).map(oai -> {
            final Map<String, String> data = new HashMap<>();
            if (oai.getListIdentifiers().getResumptionToken() != null) {
                data.put("completeListSize", String.valueOf(oai.getListIdentifiers().getResumptionToken().getCompleteListSize().toString()));
            } else {
                data.put("completeListSize", String.valueOf(oai.getListIdentifiers().getHeader().size()));
            }
            return data;
        });
        return createResponseEntity(response);
    }

    @RequestMapping(method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @RolesAllowed({EXC_HAB2})
    public ResponseEntity<ImportReport> importOaiPmh(final HttpServletRequest request,
                                                     @RequestParam(value = "library") final String libraryId,
                                                     @RequestParam(value = "project", required = false) final String projectId,
                                                     @RequestParam(value = "lot", required = false) final String lotId,
                                                     @RequestParam(value = "validation", defaultValue = "false") final boolean stepValidation,
                                                     @RequestParam(value = "dedup", defaultValue = "false") final boolean stepDeduplication,
                                                     @RequestParam(value = "dedupProcess", required = false) final ImportedDocUnit.Process defaultDedupProcess,
                                                     @RequestParam(value = "baseUrl") final String baseUrl,
                                                     @RequestParam(value = "from", required = false) final String from,
                                                     @RequestParam(value = "to", required = false) final String to,
                                                     @RequestParam(value = "set", required = false) final String set) {

        // Vérification des droits d'accès par rapport à la bibliothèque demandée
        if (!libraryAccesssHelper.checkLibrary(request, libraryId)) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }

        // Création du rapport d'import
        final ImportReport importReport = importReportService.createSimpleImportReport(getReportName(baseUrl, from, to, set),
                                                                                       null,
                                                                                       FileFormat.OAIPMH,
                                                                                       DataEncoding.UTF_8,
                                                                                       libraryId,
                                                                                       projectId,
                                                                                       lotId,
                                                                                       null);

        // Import asynchrone
        try {
            importOaiPmhService.importOaiPmhAsync(libraryId,
                                                  importReport,
                                                  new OaiPmhRequest(baseUrl, from, to, set),
                                                  stepValidation,
                                                  stepDeduplication,
                                                  stepDeduplication ? defaultDedupProcess
                                                                    : null);

        } catch (final Exception e) {
            LOG.error(e.getMessage(), e);
            importReportService.failReport(importReport, e);
            return new ResponseEntity<>(importReport, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>(importReport, HttpStatus.OK);
    }

    private String getReportName(final String baseUrl, final String from, final String to, final String set) {
        final StringBuilder builder = new StringBuilder().append("OAI-PMH: ").append(baseUrl);

        if (StringUtils.isNotBlank(set)) {
            builder.append(", set: ").append(set);
        }
        if (StringUtils.isNotBlank(from)) {
            builder.append(", from: ").append(from);
        }
        if (StringUtils.isNotBlank(to)) {
            builder.append(", until: ").append(to);
        }
        return builder.toString();
    }
}
