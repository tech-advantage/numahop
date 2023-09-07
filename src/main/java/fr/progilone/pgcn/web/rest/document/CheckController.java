package fr.progilone.pgcn.web.rest.document;

import static fr.progilone.pgcn.web.rest.checkconfiguration.security.AuthorizationConstants.CHECK_HAB3;
import static fr.progilone.pgcn.web.rest.checkconfiguration.security.AuthorizationConstants.CHECK_HAB4;
import static fr.progilone.pgcn.web.rest.lot.security.AuthorizationConstants.LOT_HAB0;

import com.codahale.metrics.annotation.Timed;
import fr.progilone.pgcn.domain.document.Check;
import fr.progilone.pgcn.domain.dto.document.CheckDTO;
import fr.progilone.pgcn.domain.dto.document.DocPageErrorsDTO;
import fr.progilone.pgcn.exception.PgcnTechnicalException;
import fr.progilone.pgcn.service.document.CheckService;
import fr.progilone.pgcn.service.document.CheckService.DocErrorReport;
import fr.progilone.pgcn.service.document.SlipService;
import fr.progilone.pgcn.service.document.ui.UICheckService;
import fr.progilone.pgcn.web.rest.AbstractRestController;
import fr.progilone.pgcn.web.util.AccessHelper;
import jakarta.annotation.security.RolesAllowed;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/api/rest/check")
public class CheckController extends AbstractRestController {

    private static final Logger LOG = LoggerFactory.getLogger(CheckController.class);

    private final UICheckService uiCheckService;
    private final CheckService checkService;
    private final SlipService slipService;
    private final AccessHelper accessHelper;

    @Autowired
    public CheckController(final UICheckService uiCheckService, final CheckService checkService, final SlipService slipService, final AccessHelper accessHelper) {
        super();
        this.uiCheckService = uiCheckService;
        this.checkService = checkService;
        this.slipService = slipService;
        this.accessHelper = accessHelper;
    }

    @RequestMapping(method = RequestMethod.GET, params = {"errors"})
    @Timed
    public Set<Check.ErrorLabel> getErrors() {
        return checkService.getErrors();
    }

    @RequestMapping(method = RequestMethod.POST, value = "/{id}")
    @Timed
    public CheckDTO update(@PathVariable final String id, @RequestBody final CheckDTO checkDTO) {
        final CheckDTO updated = uiCheckService.update(checkDTO);
        return updated;
    }

    @RequestMapping(method = RequestMethod.POST)
    @Timed
    public CheckDTO create(@RequestBody final CheckDTO checkDTO) {
        final CheckDTO created = uiCheckService.create(checkDTO);
        return created;
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.POST, params = {"seterrors"})
    @Timed
    @ResponseStatus(HttpStatus.ACCEPTED)
    @RolesAllowed(CHECK_HAB4)
    public DocErrorReport setErrors(@RequestBody final DocPageErrorsDTO errors,
                                    @PathVariable final String id,
                                    @RequestParam final Integer pageNumber,
                                    @RequestParam final String deliveryId) {
        return checkService.setChecks(id, errors, pageNumber, false, deliveryId);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.POST, params = {"setsamplederrors"})
    @Timed
    @ResponseStatus(HttpStatus.ACCEPTED)
    @RolesAllowed(CHECK_HAB4)
    public DocErrorReport setErrorsForSampling(@RequestBody final DocPageErrorsDTO errors,
                                               @PathVariable final String id,
                                               @RequestParam final Integer pageNumber,
                                               @RequestParam final String deliveryId) {
        return checkService.setChecks(id, errors, pageNumber, true, deliveryId);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.POST, params = {"setglobalerrors"})
    @Timed
    @ResponseStatus(HttpStatus.ACCEPTED)
    @RolesAllowed(CHECK_HAB4)
    public void setGlobalErrors(@RequestBody final DocPageErrorsDTO errors, @PathVariable final String id) {
        checkService.setGlobalChecks(id, errors);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.POST, params = {"setsampledglobalerrors"})
    @Timed
    @ResponseStatus(HttpStatus.ACCEPTED)
    @RolesAllowed(CHECK_HAB4)
    public void setGlobalErrorsForSampling(@RequestBody final DocPageErrorsDTO errors, @PathVariable final String id) {
        checkService.setGlobalChecksForSampling(id, errors);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET, params = {"geterrors"})
    @Timed
    @ResponseStatus(HttpStatus.ACCEPTED)
    @RolesAllowed(CHECK_HAB3)
    public Set<Check.ErrorLabel> getErrors(@PathVariable final String id, @RequestParam final Integer pageNumber) {
        return checkService.getChecks(id, pageNumber);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET, params = {"getsamplederrors"})
    @Timed
    @ResponseStatus(HttpStatus.ACCEPTED)
    @RolesAllowed(CHECK_HAB3)
    public Set<Check.ErrorLabel> getErrorsForSampling(@PathVariable final String id, @RequestParam final String pageId) {
        return checkService.getChecksForSampling(pageId);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET, params = {"getcondreportforsamplepage"})
    @Timed
    @ResponseStatus(HttpStatus.ACCEPTED)
    @RolesAllowed(CHECK_HAB3)
    public Set<String> getCondReportForSamplePage(@PathVariable final String id, @RequestParam final String pageId) {
        return checkService.getCondReportSummaryForSamplePage(pageId);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET, params = {"getglobalerrors"})
    @Timed
    @ResponseStatus(HttpStatus.ACCEPTED)
    @RolesAllowed(CHECK_HAB3)
    public Set<Check.ErrorLabel> getGlobalErrors(@PathVariable final String id) {
        return checkService.getGlobalChecks(id);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET, params = {"getsampledglobalerrors"})
    @Timed
    @ResponseStatus(HttpStatus.ACCEPTED)
    @RolesAllowed(CHECK_HAB3)
    public Set<Check.ErrorLabel> getGlobalErrorsForSampling(@PathVariable final String id) {
        return checkService.getGlobalChecksForSampling(id);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET, params = {"getdocumentallerrors"})
    @Timed
    @ResponseStatus(HttpStatus.ACCEPTED)
    @RolesAllowed(CHECK_HAB3)
    public DocErrorReport getDocumentErrors(@PathVariable final String id) {
        return checkService.getDocumentErrors(id);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET, params = {"getsampleallerrors"})
    @Timed
    @ResponseStatus(HttpStatus.ACCEPTED)
    @RolesAllowed(CHECK_HAB3)
    public DocErrorReport getSampleErrors(@PathVariable final String id) {
        return checkService.getSampleErrors(id);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/{id}", params = {"summaryresults"})
    @Timed
    @RolesAllowed(CHECK_HAB3)
    public ResponseEntity<Map<String, Object>> getDocumentSummaryResults(@PathVariable final String id, @RequestParam final String deliveryId) {

        final Map<String, Object> slip = slipService.getDocumentSummaryResults(deliveryId, id);
        return new ResponseEntity<>(slip, HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/csv/{id}", produces = "text/csv")
    @Timed
    public void generateSlip(final HttpServletRequest request,
                             final HttpServletResponse response,
                             @PathVariable final String id,
                             @RequestParam(value = "encoding", defaultValue = "utf-8") final String encoding,
                             @RequestParam(value = "separator", defaultValue = ";") final char separator) throws PgcnTechnicalException {

        // check access to digital documents
        try {
            writeResponseHeaderForDownload(response, "text/csv; charset=" + encoding, null, "bordereau.csv");
            slipService.writeSlip(response.getOutputStream(), id, encoding, separator);
        } catch (final IOException e) {
            LOG.error(e.getMessage(), e);
            throw new PgcnTechnicalException(e);
        }
    }

    @RequestMapping(method = RequestMethod.GET, value = "/pdf/{id}", produces = "application/pdf")
    @Timed
    public void generateSlipPdf(final HttpServletRequest request, final HttpServletResponse response, @PathVariable final String id) throws PgcnTechnicalException {

        try {
            writeResponseHeaderForDownload(response, "application/pdf", null, "bordereau_controle.pdf");
            slipService.writePdfCheckSlip(response.getOutputStream(), id);
        } catch (final IOException e) {
            LOG.error(e.getMessage(), e);
            throw new PgcnTechnicalException(e);
        }
    }

    @RequestMapping(method = RequestMethod.GET, value = "/lot_csv/{id}", produces = "text/csv")
    @Timed
    @RolesAllowed({CHECK_HAB4,
                   LOT_HAB0})
    public void generateCheckSlip(final HttpServletRequest request,
                                  final HttpServletResponse response,
                                  @PathVariable final String id,
                                  @RequestParam(value = "encoding", defaultValue = "utf-8") final String encoding,
                                  @RequestParam(value = "separator", defaultValue = ";") final char separator) throws PgcnTechnicalException {

        if (!accessHelper.checkLot(id)) {
            response.setStatus(403);
            return;
        }

        try {
            writeResponseHeaderForDownload(response, "text/csv; charset=" + encoding, null, "bordereau.csv");
            slipService.writeSlipForLot(response.getOutputStream(), id, encoding, separator);
        } catch (final IOException e) {
            LOG.error(e.getMessage(), e);
            throw new PgcnTechnicalException(e);
        }
    }

    @RequestMapping(method = RequestMethod.GET, value = "/lot_pdf/{id}", produces = "application/pdf")
    @Timed
    @RolesAllowed({CHECK_HAB4,
                   LOT_HAB0})
    public void generateCheckSlipPdf(final HttpServletRequest request, final HttpServletResponse response, @PathVariable final String id) throws PgcnTechnicalException {

        if (!accessHelper.checkLot(id)) {
            response.setStatus(403);
            return;
        }

        try {
            writeResponseHeaderForDownload(response, "application/pdf", null, "bordereau.pdf");
            slipService.writeSlipForLotPDF(response.getOutputStream(), id);
        } catch (final IOException e) {
            LOG.error(e.getMessage(), e);
            throw new PgcnTechnicalException(e);
        }
    }
}
