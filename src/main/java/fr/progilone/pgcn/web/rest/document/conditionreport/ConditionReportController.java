package fr.progilone.pgcn.web.rest.document.conditionreport;

import static fr.progilone.pgcn.web.rest.document.security.AuthorizationConstants.COND_REPORT_HAB0;
import static fr.progilone.pgcn.web.rest.document.security.AuthorizationConstants.COND_REPORT_HAB1;
import static fr.progilone.pgcn.web.rest.document.security.AuthorizationConstants.COND_REPORT_HAB2;
import static fr.progilone.pgcn.web.rest.document.security.AuthorizationConstants.COND_REPORT_HAB3;
import static fr.progilone.pgcn.web.rest.document.security.AuthorizationConstants.COND_REPORT_HAB4;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.annotation.security.RolesAllowed;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.codahale.metrics.annotation.Timed;

import fr.progilone.pgcn.domain.AbstractDomainObject;
import fr.progilone.pgcn.domain.document.DocUnit;
import fr.progilone.pgcn.domain.document.conditionreport.ConditionReport;
import fr.progilone.pgcn.domain.document.conditionreport.ConditionReportDetail;
import fr.progilone.pgcn.exception.PgcnException;
import fr.progilone.pgcn.exception.PgcnTechnicalException;
import fr.progilone.pgcn.repository.document.conditionreport.ConditionReportRepositoryCustom.DimensionFilter;
import fr.progilone.pgcn.service.document.conditionreport.ConditionReportExportService;
import fr.progilone.pgcn.service.document.conditionreport.ConditionReportImportService;
import fr.progilone.pgcn.service.document.conditionreport.ConditionReportService;
import fr.progilone.pgcn.service.document.conditionreport.ConditionReportService.SearchResult;
import fr.progilone.pgcn.service.es.EsConditionReportService;
import fr.progilone.pgcn.web.rest.AbstractRestController;
import fr.progilone.pgcn.web.util.AccessHelper;
import fr.progilone.pgcn.web.util.LibraryAccesssHelper;
import fr.progilone.pgcn.web.util.WorkflowAccessHelper;

@RestController
@RequestMapping(value = "/api/rest/condreport")
public class ConditionReportController extends AbstractRestController {

    private static final Logger LOG = LoggerFactory.getLogger(ConditionReportController.class);

    private final AccessHelper accessHelper;
    private final LibraryAccesssHelper libraryAccesssHelper;
    private final WorkflowAccessHelper workflowAccessHelper;
    private final ConditionReportExportService conditionReportExchangeService;
    private final ConditionReportImportService conditionReportImportService;
    private final ConditionReportService conditionReportService;
    private final EsConditionReportService esConditionReportService;

    @Autowired
    public ConditionReportController(final AccessHelper accessHelper,
                                     final LibraryAccesssHelper libraryAccesssHelper,
                                     final WorkflowAccessHelper workflowAccessHelper,
                                     final ConditionReportExportService conditionReportExchangeService,
                                     final ConditionReportImportService conditionReportImportService,
                                     final ConditionReportService conditionReportService,
                                     final EsConditionReportService esConditionReportService) {
        this.accessHelper = accessHelper;
        this.libraryAccesssHelper = libraryAccesssHelper;
        this.workflowAccessHelper = workflowAccessHelper;
        this.conditionReportExchangeService = conditionReportExchangeService;
        this.conditionReportImportService = conditionReportImportService;
        this.conditionReportService = conditionReportService;
        this.esConditionReportService = esConditionReportService;
    }

    @RequestMapping(method = RequestMethod.POST, params = {"docUnit"})
    @Timed
    @RolesAllowed(COND_REPORT_HAB1)
    public ResponseEntity<ConditionReport> create(@RequestParam(name = "docUnit") final String docUnitId) throws PgcnException {
        // droits d'accès à l'ud
        if (!accessHelper.checkDocUnit(docUnitId)) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        final ConditionReport savedReport = conditionReportService.create(docUnitId);
        esConditionReportService.indexAsync(savedReport.getIdentifier());
        return new ResponseEntity<>(savedReport, HttpStatus.CREATED);
    }

    @RequestMapping(value = "/{identifier}", method = RequestMethod.DELETE)
    @Timed
    @RolesAllowed(COND_REPORT_HAB3)
    public void delete(final HttpServletResponse response, @PathVariable final String identifier) {
        final DocUnit docUnit = conditionReportService.findDocUnitByIdentifier(identifier);
        // non trouvé
        if (docUnit == null) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return;
        }
        // droits d'accès à l'ud
        if (!accessHelper.checkDocUnit(docUnit.getIdentifier())) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            return;
        }
        // Droit par rapport au workflow
        if (!workflowAccessHelper.canConstatBeDeleted(docUnit.getIdentifier())) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            return;
        }
        conditionReportService.delete(identifier);
        response.setStatus(HttpServletResponse.SC_OK);
    }

    @RequestMapping(value = "/{identifier}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @RolesAllowed(COND_REPORT_HAB0)
    public ResponseEntity<ConditionReport> findByIdentifier(@PathVariable final String identifier) {
        final ConditionReport report = conditionReportService.findByIdentifier(identifier);
        // non trouvé
        if (report == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        // droits d'accès à l'ud
        if (!accessHelper.checkDocUnit(report.getDocUnit().getIdentifier())) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        return createResponseEntity(report);
    }

    @RequestMapping(method = RequestMethod.GET, params = {"docUnit"}, produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @RolesAllowed(COND_REPORT_HAB0)
    public ResponseEntity<ConditionReport> findByDocUnit(@RequestParam(name = "docUnit") final String docUnitId) {
        // droits d'accès à l'ud
        if (!accessHelper.checkDocUnit(docUnitId)) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        final ConditionReport report = conditionReportService.findByDocUnit(docUnitId);
        return new ResponseEntity<>(report, HttpStatus.OK); // toujours 200
    }

    @RequestMapping(method = RequestMethod.GET, params = {"summary", "docUnit"}, produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @RolesAllowed(COND_REPORT_HAB0)
    public ResponseEntity<Set<String>> getSummaryByDocUnit(@RequestParam(name = "docUnit") final String docUnitId) {
        // droits d'accès à l'ud
        if (!accessHelper.checkDocUnit(docUnitId)) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        final ConditionReport report = conditionReportService.findByDocUnit(docUnitId);
        return new ResponseEntity<>(conditionReportService.getSummary(report), HttpStatus.OK);
    }

    @RequestMapping(value = "/{identifier}", method = RequestMethod.POST)
    @Timed
    @RolesAllowed(COND_REPORT_HAB2)
    public ResponseEntity<ConditionReport> update(@RequestBody final ConditionReport report) throws PgcnException {
        final DocUnit docUnit = conditionReportService.findDocUnitByIdentifier(report.getIdentifier());
        // non trouvé
        if (docUnit == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        // droits d'accès à l'ud
        if (!accessHelper.checkDocUnit(docUnit.getIdentifier())) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        final ConditionReport savedReport = conditionReportService.save(report);
        return new ResponseEntity<>(savedReport, HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.POST, params = {"search"}, produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @RolesAllowed(COND_REPORT_HAB0)
    public ResponseEntity<Page<SearchResult>> search(final HttpServletRequest request,
                                                     @RequestBody final SearchRequest requestParams,
                                                     @RequestParam(value = "page", required = false, defaultValue = "0") final Integer page,
                                                     @RequestParam(value = "size", required = false, defaultValue = "" + Integer.MAX_VALUE)
                                                     final Integer size,
                                                     @RequestParam(value = "sorts", required = false) final List<String> sorts) {

        final List<String> filteredLibraries = libraryAccesssHelper.getLibraryFilter(request, requestParams.getLibraries());
        final Page<SearchResult> results = conditionReportService.search(filteredLibraries,
                                                                         requestParams.getDimensions(),
                                                                         requestParams.getFrom(),
                                                                         requestParams.getTo(),
                                                                         requestParams.getDescriptions(),
                                                                         requestParams.getFilter(),
                                                                         requestParams.isValidateOnly(),
                                                                         page,
                                                                         size,
                                                                         sorts);
        return new ResponseEntity<>(results, HttpStatus.OK);
    }

    /**
     * Export d'un constat d'état ODT
     *
     * @param response
     * @param identifier
     * @return
     */
    @RequestMapping(value = "/{id}", method = RequestMethod.GET, params = {"exportto"})
    @Timed
    @RolesAllowed(COND_REPORT_HAB4)
    public void exportReportOdt(final HttpServletResponse response,
                                @PathVariable("id") final String identifier,
                                @RequestParam(name = "exportto", defaultValue = "PDF") final ConditionReportService.ConvertType type) throws
                                                                                                                                      PgcnTechnicalException {
        final ConditionReport report = conditionReportService.findByIdentifier(identifier);
        // non trouvé
        if (report == null) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return;
        }
        // droits d'accès à l'ud
        if (!accessHelper.checkDocUnit(report.getDocUnit().getIdentifier())) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            return;
        }
        try {
            final String filename = "condition_report_" + report.getDocUnit().getPgcnId().replaceAll("\\W+", "_");
            // Entête ODT
            if (type == ConditionReportService.ConvertType.ODT) {
                writeResponseHeaderForDownload(response, "application/vnd.oasis.opendocument.text", null, filename + ".odt");
            }
            // Entête PDF
            else {
                writeResponseHeaderForDownload(response, "application/pdf", null, filename + ".pdf");
            }
            // Export du constat d'état
            conditionReportService.exportDocument(identifier, response.getOutputStream(), type);

        } catch (final IOException e) {
            throw new PgcnTechnicalException(e);
        }
    }

    @RequestMapping(method = RequestMethod.GET, params = {"import-template"})
    @Timed
    @RolesAllowed(COND_REPORT_HAB1)
    public void getReportImportTemplate(final HttpServletResponse response,
                                        @RequestParam(name = "import-template") final List<String> docUnitIds,
                                        @RequestParam(name = "format", defaultValue = "XLSX")
                                        final ConditionReportExportService.WorkbookFormat format) throws PgcnTechnicalException {
        // droits d'accès à l'ud
        final Collection<DocUnit> filteredDocUnits = accessHelper.filterDocUnits(docUnitIds);
        if (filteredDocUnits.isEmpty()) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return;
        }
        // export du modèle de doc d'import
        try {
            // entête
            switch (format) {
                case XLS:
                    writeResponseHeaderForDownload(response, "application/vnd.ms-excel", null, "condition_report_import.xls");
                    break;
                case XLSX:
                    writeResponseHeaderForDownload(response,
                                                   "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
                                                   null,
                                                   "condition_report_import.xlsx");
                    break;
            }
            // réponse
            final List<String> identifiers = filteredDocUnits.stream().map(AbstractDomainObject::getIdentifier).collect(Collectors.toList());
            conditionReportExchangeService.writeReportTemplate(response.getOutputStream(), identifiers, format);

        } catch (final IOException e) {
            throw new PgcnTechnicalException(e);
        }
    }

    @RequestMapping(method = RequestMethod.GET, value = "/csv", produces = "text/csv")
    @Timed
    @RolesAllowed(COND_REPORT_HAB0)
    public void generateSlip(final HttpServletRequest request,
                             final HttpServletResponse response,
                             @RequestParam(value = "reports") final List<String> reportIds,
                             @RequestParam(value = "encoding", defaultValue = "ISO-8859-15") final String encoding,
                             @RequestParam(value = "separator", defaultValue = ";") final char separator) throws PgcnTechnicalException {

        // droits d'accès à l'ud
        final List<ConditionReportDetail> reports = accessHelper.filterConditionReportDetails(reportIds);
        if (reports.isEmpty()) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
        }

        final List<String> reportIdentifiers = reports.stream().map(report -> report.getIdentifier()).collect(Collectors.toList());

        try {
            writeResponseHeaderForDownload(response, "text/csv; charset=" + encoding, null, "bordereau.csv");
            conditionReportService.writeSlip(response.getOutputStream(), reportIdentifiers, encoding, separator);
        } catch (final IOException e) {
            LOG.error(e.getMessage(), e);
            throw new PgcnTechnicalException(e);
        }
    }

    @RequestMapping(method = RequestMethod.GET, value = "/pdf", produces = "application/pdf")
    @Timed
    @RolesAllowed(COND_REPORT_HAB0)
    public void generateSlipPdf(final HttpServletRequest request,
                                final HttpServletResponse response,
                                @RequestParam(value = "reports") final List<String> reportIds) throws PgcnTechnicalException {

        // droits d'accès à l'ud
        final List<ConditionReportDetail> reports = accessHelper.filterConditionReportDetails(reportIds);
        if (reports.isEmpty()) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
        }

        final List<String> reportIdentifiers = reports.stream().map(report -> report.getIdentifier()).collect(Collectors.toList());

        try {
            writeResponseHeaderForDownload(response, "application/pdf", null, "bordereau.pdf");
            conditionReportService.writeSlipPDF(response.getOutputStream(), reportIdentifiers, "Liste des unités documentaires");
        } catch (final IOException e) {
            LOG.error(e.getMessage(), e);
            throw new PgcnTechnicalException(e);
        }
    }

    @RequestMapping(method = RequestMethod.POST, params = {"import-report"})
    @Timed
    @RolesAllowed(COND_REPORT_HAB1)
    public ResponseEntity<List<ConditionReportImportService.ImportResult>> updateReport(
        @RequestParam(value = "file") final List<MultipartFile> files) {

        if (files.isEmpty()) {
            LOG.warn("Aucun fichier n'a été reçu pour l'import des constats d'état");
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } else {
            final MultipartFile file = files.get(0);
            if (file.getSize() == 0) {
                LOG.warn("L'import du fichier {} est annulé car il est vide", file.getOriginalFilename());
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            } else {
                LOG.info("Import du fichier {} (taille {})", file.getOriginalFilename(), file.getSize());
                try (InputStream in = file.getInputStream()) {
                    final List<ConditionReportImportService.ImportResult> importResults =
                        conditionReportImportService.importReport(in, docUnit -> accessHelper.checkDocUnit(docUnit.getIdentifier()));
                    // Indexation des constats importés
                    final List<String> reportIds = importResults.stream()
                                                                .filter(res -> res.getReportId() != null)
                                                                .map(ConditionReportImportService.ImportResult::getReportId)
                                                                .collect(Collectors.toList());
                    esConditionReportService.indexAsync(reportIds);

                    return new ResponseEntity<>(importResults, HttpStatus.OK);

                } catch (final InvalidFormatException | IOException e) {
                    LOG.error(e.getMessage(), e);
                    return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
                }
            }
        }
    }
    
    /**
     * Propagation du constat d'etat du parent vers les relations filles.
     * 
     * @param request
     * @param response
     * @param docUnitId
     * @param detailId
     * @return
     */
    @RequestMapping(value = "/{id}", method = RequestMethod.POST, params = {"propagate"})
    @Timed
    @RolesAllowed(COND_REPORT_HAB0)
    public ResponseEntity<?> propagateReport(final HttpServletRequest request,
                                final HttpServletResponse response,
                                @RequestParam(name = "docUnit") final String docUnitId,
                                @PathVariable(name = "id") final String detailId) {

        // droits d'accès à l'ud
        if (!accessHelper.checkDocUnit(docUnitId)) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        } 
        final Map<String, String> results = conditionReportService.propagateReport(docUnitId, detailId);
        // es indexation des nouveaux reports 
        esConditionReportService.indexAsync(results.keySet().stream().collect(Collectors.toList()));

        return new ResponseEntity<>(results.values(), HttpStatus.OK);
    }

    
    private static final class SearchRequest {
        private List<String> libraries;
        private DimensionFilter.Operator op;
        private Integer dim1;
        private Integer dim2;
        private Integer dim3;
        private LocalDate from;
        private LocalDate to;
        private List<String> descriptions;
        private List<String> filter;
        private boolean validateOnly;

        public List<String> getLibraries() {
            return libraries;
        }

        public void setLibraries(final List<String> libraries) {
            this.libraries = libraries;
        }

        public DimensionFilter.Operator getOp() {
            return op;
        }

        public void setOp(final DimensionFilter.Operator op) {
            this.op = op;
        }

        public Integer getDim1() {
            return dim1;
        }

        public void setDim1(final Integer dim1) {
            this.dim1 = dim1;
        }

        public Integer getDim2() {
            return dim2;
        }

        public void setDim2(final Integer dim2) {
            this.dim2 = dim2;
        }

        public Integer getDim3() {
            return dim3;
        }

        public void setDim3(final Integer dim3) {
            this.dim3 = dim3;
        }

        public LocalDate getFrom() {
            return from;
        }

        public void setFrom(final LocalDate from) {
            this.from = from;
        }

        public LocalDate getTo() {
            return to;
        }

        public void setTo(final LocalDate to) {
            this.to = to;
        }

        public List<String> getDescriptions() {
            return descriptions;
        }

        public void setDescriptions(final List<String> descriptions) {
            this.descriptions = descriptions;
        }

        public List<String> getFilter() {
            return filter;
        }

        public void setFilters(final List<String> filter) {
            this.filter = filter;
        }

        public DimensionFilter getDimensions() {
            return new DimensionFilter(op, dim1, dim2, dim3);
        }
        
        public void setValidateOnly(final boolean validateOnly) {
            this.validateOnly = validateOnly;
        }
        public boolean isValidateOnly() {
            return validateOnly;
        }
    }
}
