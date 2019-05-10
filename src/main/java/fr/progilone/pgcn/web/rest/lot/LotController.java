package fr.progilone.pgcn.web.rest.lot;

import com.codahale.metrics.annotation.Timed;
import fr.progilone.pgcn.domain.AbstractDomainObject;
import fr.progilone.pgcn.domain.dto.audit.AuditLotRevisionDTO;
import fr.progilone.pgcn.domain.dto.lot.LotDTO;
import fr.progilone.pgcn.domain.dto.lot.LotListDTO;
import fr.progilone.pgcn.domain.dto.lot.LotWithConfigRulesDTO;
import fr.progilone.pgcn.domain.dto.lot.SimpleLotDTO;
import fr.progilone.pgcn.domain.lot.Lot;
import fr.progilone.pgcn.exception.PgcnException;
import fr.progilone.pgcn.exception.PgcnTechnicalException;
import fr.progilone.pgcn.service.JasperReportsService;
import fr.progilone.pgcn.service.es.EsLotService;
import fr.progilone.pgcn.service.lot.LotService;
import fr.progilone.pgcn.service.lot.ui.UILotService;
import fr.progilone.pgcn.service.workflow.WorkflowService;
import fr.progilone.pgcn.web.rest.AbstractRestController;
import fr.progilone.pgcn.web.util.AccessHelper;
import fr.progilone.pgcn.web.util.LibraryAccesssHelper;
import fr.progilone.pgcn.web.util.WorkflowAccessHelper;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.security.RolesAllowed;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import static fr.progilone.pgcn.web.rest.document.security.AuthorizationConstants.*;
import static fr.progilone.pgcn.web.rest.lot.security.AuthorizationConstants.*;

@RestController
@RequestMapping(value = "/api/rest/lot")
public class LotController extends AbstractRestController {

    private static final Logger LOG = LoggerFactory.getLogger(LotController.class);

    private final AccessHelper accessHelper;
    private final WorkflowAccessHelper workflowAccessHelper;
    private final WorkflowService workflowService;
    private final EsLotService esLotService;
    private final LibraryAccesssHelper libraryAccesssHelper;
    private final LotService lotService;
    private final UILotService uiLotService;

    @Autowired
    public LotController(final AccessHelper accessHelper,
                         final EsLotService esLotService,
                         final LibraryAccesssHelper libraryAccesssHelper,
                         final WorkflowAccessHelper workflowAccessHelper,
                         final WorkflowService workflowService,
                         final LotService lotService,
                         final UILotService uiLotService) {
        this.accessHelper = accessHelper;
        this.esLotService = esLotService;
        this.libraryAccesssHelper = libraryAccesssHelper;
        this.workflowAccessHelper = workflowAccessHelper;
        this.workflowService = workflowService;
        this.lotService = lotService;
        this.uiLotService = uiLotService;
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    @Timed
    @RolesAllowed({LOT_HAB2})
    public ResponseEntity<LotDTO> delete(@PathVariable final String id) {
        // Droits d'accès
        if (!accessHelper.checkLot(id)) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        lotService.delete(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.POST, params = {"delete"})
    @Timed
    @RolesAllowed({LOT_HAB2})
    public ResponseEntity<Lot> delete(@RequestBody final List<String> lots) {
        // Droits d'accès
        final Collection<Lot> filteredLots = accessHelper.filterLots(lots);
        uiLotService.delete(filteredLots);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.GET, params = {"search"}, produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @RolesAllowed({LOT_HAB3})
    public ResponseEntity<Page<SimpleLotDTO>> search(final HttpServletRequest request,
                                                     @RequestParam(value = "search", required = false) final String search,
                                                     @RequestParam(value = "libraries", required = false) final List<String> libraries,
                                                     @RequestParam(value = "projects", required = false) final List<String> projects,
                                                     @RequestParam(value = "active", required = false, defaultValue = "true") final boolean active,
                                                     @RequestParam(value = "statuses", required = false) final List<Lot.LotStatus> lotStatuses,
                                                     @RequestParam(value = "docNumber", required = false) final Integer docNumber,
                                                     @RequestParam(value = "fileFormat", required = false) final List<String> fileFormats,
                                                     @RequestParam(value = "page", required = false, defaultValue = "0") final Integer page,
                                                     @RequestParam(value = "size", required = false, defaultValue = "10") final Integer size) {
        // Droits d'accès
        final List<String> filteredLibraries = libraryAccesssHelper.getLibraryFilter(request, libraries);
        final List<String> filteredProjects =
            accessHelper.filterProjects(projects).stream().map(AbstractDomainObject::getIdentifier).collect(Collectors.toList());
        return new ResponseEntity<>(uiLotService.search(search,
                                                        filteredLibraries,
                                                        filteredProjects,
                                                        active,
                                                        lotStatuses,
                                                        docNumber,
                                                        fileFormats,
                                                        null,
                                                        page,
                                                        size), HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.POST, params = {"search"}, produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @RolesAllowed({LOT_HAB3})
    public ResponseEntity<Page<SimpleLotDTO>> search(final HttpServletRequest request,
                                                     @RequestBody SearchRequest requestParams,
                                                     @RequestParam(value = "page", required = false, defaultValue = "0") final Integer page,
                                                     @RequestParam(value = "size", required = false, defaultValue = "10") final Integer size) {
        // Droits d'accès
        final List<String> filteredLibraries = libraryAccesssHelper.getLibraryFilter(request, requestParams.getLibraries());
        final List<String> filteredProjects =
            accessHelper.filterProjects(requestParams.getProjects()).stream().map(AbstractDomainObject::getIdentifier).collect(Collectors.toList());
        return new ResponseEntity<>(uiLotService.search(requestParams.getSearch(),
                                                        filteredLibraries,
                                                        filteredProjects,
                                                        requestParams.isActive(),
                                                        requestParams.getLotStatuses(),
                                                        requestParams.getDocNumber(),
                                                        requestParams.getFileFormats(),
                                                        requestParams.getFilter(),
                                                        page,
                                                        size), HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.GET, params = {"widget", "from"}, produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @RolesAllowed({LOT_HAB3})
    public ResponseEntity<List<AuditLotRevisionDTO>> getLotsForWidget(final HttpServletRequest request,
                                                                      @DateTimeFormat(pattern = "yyyy-MM-dd") @RequestParam(name = "from")
                                                                      final LocalDate fromDate,
                                                                      @RequestParam(value = "library", required = false) final List<String> libraries,
                                                                      @RequestParam(value = "project", required = false) final List<String> projects,
                                                                      @RequestParam(value = "status", required = false)
                                                                      final List<Lot.LotStatus> status) {

        final List<String> filteredLibraries = libraryAccesssHelper.getLibraryFilter(request, libraries);
        final List<String> filteredProjects =
            accessHelper.filterProjects(projects).stream().map(AbstractDomainObject::getIdentifier).collect(Collectors.toList());

        // Chargement
        final List<AuditLotRevisionDTO> revisions = uiLotService.getLotsForWidget(fromDate, filteredLibraries, filteredProjects, status);
        // Réponse
        return new ResponseEntity<>(revisions, HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.GET, params = {"simpleByProject", "project"}, produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @RolesAllowed({LOT_HAB3})
    public ResponseEntity<List<SimpleLotDTO>> findAllSimpleForProject(@RequestParam(value = "project") final String projectId) {
        // Droits d'accès
        if (!accessHelper.checkProject(projectId)) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        final List<SimpleLotDTO> lot = uiLotService.findAllSimpleForProject(projectId);
        return createResponseEntity(lot);
    }

    @RequestMapping(method = RequestMethod.GET, params = {"filterByProjects", "projectIds"}, produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @RolesAllowed({LOT_HAB3})
    public ResponseEntity<List<SimpleLotDTO>> findAllIdentifiersForProjects(@RequestParam(value = "projectIds") final List<String> projectIds) {
        final List<SimpleLotDTO> lotIds = lotService.findAllByProjectIds(projectIds);
        return createResponseEntity(lotIds);
    }

    @RequestMapping(method = RequestMethod.GET, params = {"dto", "libraries"}, produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Collection<LotListDTO>> findAllByLibraries(@RequestParam(required = false) final List<String> libraries) {
        final Collection<LotListDTO> lots = uiLotService.findAllByLibraryIn(libraries);
        final Collection<LotListDTO> filteredLots = filterLotDTOs(lots, LotListDTO::getIdentifier);
        return createResponseEntity(filteredLots);
    }

    @RequestMapping(method = RequestMethod.GET, params = {"dto", "projects"}, produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Collection<LotListDTO>> findAllByProjects(@RequestParam(required = false) final List<String> projects) {
        final Collection<LotListDTO> lots = uiLotService.findAllByProjectIn(projects);
        final Collection<LotListDTO> filteredLots = filterLotDTOs(lots, LotListDTO::getIdentifier);
        return createResponseEntity(filteredLots);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @RolesAllowed({LOT_HAB3})
    public ResponseEntity<LotWithConfigRulesDTO> getById(@PathVariable final String id) {
        // Droits d'accès
        if (!accessHelper.checkLot(id)) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        final LotWithConfigRulesDTO lot = uiLotService.getOneWithConfigRules(id);
        return createResponseEntity(lot);
    }

    @RequestMapping(method = RequestMethod.GET, params = {"project"}, produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @RolesAllowed({LOT_HAB3})
    public ResponseEntity<List<LotDTO>> findAllForProject(@RequestParam(value = "project") final String projectId) {
        // Droits d'accès au projet
        if (!accessHelper.checkProject(projectId)) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        final List<LotDTO> lots = uiLotService.findAllForProject(projectId);
        // Droits d'accès aux lots
        final List<LotDTO> filteredLots = filterLotDTOs(lots, LotDTO::getIdentifier);
        return createResponseEntity(filteredLots);
    }

    @RequestMapping(method = RequestMethod.GET, params = {"project", "simpleForDocUnit"}, produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @RolesAllowed({LOT_HAB3})
    public ResponseEntity<List<LotDTO>> findSimpleForDocUnit(@RequestParam(value = "project") final String projectId) {
        // Droits d'accès au projet
        if (!accessHelper.checkProject(projectId)) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        final List<LotDTO> lots = uiLotService.findAllForDocUnitByProject(projectId);
        // Droits d'accès aux lots
        final List<LotDTO> filteredLots = filterLotDTOs(lots, LotDTO::getIdentifier);
        return createResponseEntity(filteredLots);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET, params = {"dto"}, produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @RolesAllowed({LOT_HAB3})
    public ResponseEntity<LotDTO> getDtoById(@PathVariable final String id) {
        // Droits d'accès
        if (!accessHelper.checkLot(id)) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        final LotDTO lot = uiLotService.getOne(id);
        return createResponseEntity(lot);
    }

    @RequestMapping(method = RequestMethod.GET, params = {"dto", "lot"}, produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @RolesAllowed({LOT_HAB3})
    public ResponseEntity<List<LotDTO>> getDtoByIds(@RequestParam(name = "lot") final List<String> ids) {
        final List<LotDTO> lots = uiLotService.findByIdentifierIn(ids);
        // Droits d'accès aux lots
        final List<LotDTO> filteredLots = filterLotDTOs(lots, LotDTO::getIdentifier);
        return createResponseEntity(filteredLots);
    }

    @RequestMapping(method = RequestMethod.GET, params = {"dto"}, produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Collection<LotListDTO>> findAllDTO(@RequestParam(value = "target", required = false) final String target) {
        final Collection<LotListDTO> lots;
        if (StringUtils.equals(target, "delivery")) {
            lots = uiLotService.findAllActiveForDelivery();
        } else if (StringUtils.equals(target, "multilotsdelivery")) {
            lots = uiLotService.findAllActiveForMultiLotsDelivery();
        } else {
            lots = uiLotService.findAllActiveDTO();
        }
        // Droits d'accès aux lots
        final List<LotListDTO> filteredLots = filterLotDTOs(lots, LotListDTO::getIdentifier);
        return createResponseEntity(filteredLots);
    }

    @RequestMapping(method = RequestMethod.POST)
    @Timed
    @RolesAllowed({LOT_HAB0})
    public ResponseEntity<LotDTO> create(final HttpServletRequest request, @RequestBody final LotDTO lot) throws PgcnException {
        // Droit d'accès à la bibliothèque
        if (lot.getProject() != null && !libraryAccesssHelper.checkLibrary(request, lot.getProject().getLibrary().getIdentifier())) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        final LotDTO savedLot = uiLotService.create(lot);
        esLotService.indexAsync(savedLot.getIdentifier());    // Moteur de recherche
        return new ResponseEntity<>(savedLot, HttpStatus.CREATED);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.POST, params = {"unlinkProject"})
    @Timed
    @RolesAllowed({LOT_HAB1})
    public ResponseEntity<?> unlinkProject(@PathVariable final String id) {
        // Droit d'accès
        if (!accessHelper.checkLot(id)) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        lotService.unlinkProject(id);
        esLotService.indexAsync(id);    // Moteur de recherche
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.POST, params = {"validate"})
    @Timed
    @RolesAllowed({LOT_HAB4})
    public ResponseEntity<?> validateLot(@PathVariable final String id) {
        // Droit d'accès
        if (!accessHelper.checkLot(id)) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        // Workflow
        if (!workflowAccessHelper.canLotBeValidated(id)) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        final Lot lot = lotService.validate(id);
        // Démarrage du workflow sur le lot
        workflowService.initializeWorkflow(lot, lotService.getWorkflowModel(lot));
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.POST)
    @Timed
    @RolesAllowed({LOT_HAB1})
    public ResponseEntity<LotDTO> update(@RequestBody final LotDTO lot) throws PgcnException {
        // Droit d'accès
        if (!accessHelper.checkLot(lot.getIdentifier())) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        final LotDTO savedLot = uiLotService.update(lot);
        esLotService.indexAsync(savedLot.getIdentifier());    // Moteur de recherche
        return new ResponseEntity<>(savedLot, HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.POST, params = {"project"})
    @ResponseStatus(HttpStatus.OK)
    @Timed
    @RolesAllowed(LOT_HAB1)
    public void setProject(@RequestBody final List<String> lotIds, @RequestParam(name = "project") final String project) {
        uiLotService.setProjectAndLot(lotIds, project);
        esLotService.indexAsync(lotIds);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/csv/{id}", produces = "text/csv")
    @Timed
    @RolesAllowed(COND_REPORT_HAB0)
    public void generateSlip(final HttpServletRequest request,
                             final HttpServletResponse response,
                             @PathVariable final String id,
                             @RequestParam(value = "encoding", defaultValue = JasperReportsService.ENCODING_UTF8) final String encoding,
                             @RequestParam(value = "separator", defaultValue = ";") final char separator) throws PgcnTechnicalException {

        if (!accessHelper.checkLot(id)) {
            response.setStatus(403);
            return;
        }

        try {
            writeResponseHeaderForDownload(response, "text/csv; charset=" + encoding, null, "bordereau.csv");
            lotService.writeCondReportSlip(response.getOutputStream(), id, encoding, separator);
        } catch (final IOException e) {
            LOG.error(e.getMessage(), e);
            throw new PgcnTechnicalException(e);
        }
    }

    @RequestMapping(method = RequestMethod.GET, value = "/pdf/{id}", produces = "application/pdf")
    @Timed
    @RolesAllowed(COND_REPORT_HAB0)
    public void generateSlipPdf(final HttpServletRequest request, final HttpServletResponse response, @PathVariable final String id) throws
                                                                                                                                     PgcnTechnicalException {

        if (!accessHelper.checkLot(id)) {
            response.setStatus(403);
            return;
        }

        try {
            writeResponseHeaderForDownload(response, "application/pdf", null, "bordereau.pdf");
            lotService.writeCondReportSlipPDF(response.getOutputStream(), id);
        } catch (final IOException e) {
            LOG.error(e.getMessage(), e);
            throw new PgcnTechnicalException(e);
        }
    }

    /**
     * Filtrage d'une liste de LotDTO sur les droits d'accès de l'utilisateur
     *
     * @param lots
     * @return
     */
    private <T> List<T> filterLotDTOs(final Collection<T> lots, final Function<T, String> getIdentifier) {
        return accessHelper.filterLots(lots.stream().map(getIdentifier).collect(Collectors.toList()))
                           .stream()
                           // Correspondance lot autorisé => lotDto
                           .map(lot -> lots.stream().filter(l -> StringUtils.equals(getIdentifier.apply(l), lot.getIdentifier())).findAny())
                           .filter(Optional::isPresent)
                           .map(Optional::get)
                           .collect(Collectors.toList());
    }

    private static final class SearchRequest {
        private String search;
        private List<String> libraries;
        private List<String> projects;
        private boolean active;
        private List<Lot.LotStatus> lotStatuses;
        private Integer docNumber;
        private List<String> fileFormats;
        private List<String> filter;

        public String getSearch() {
            return search;
        }

        public void setSearch(final String search) {
            this.search = search;
        }

        public List<String> getLibraries() {
            return libraries;
        }

        public void setLibraries(final List<String> libraries) {
            this.libraries = libraries;
        }

        public List<String> getProjects() {
            return projects;
        }

        public void setProjects(final List<String> projects) {
            this.projects = projects;
        }

        public boolean isActive() {
            return active;
        }

        public void setActive(final boolean active) {
            this.active = active;
        }

        public List<Lot.LotStatus> getLotStatuses() {
            return lotStatuses;
        }

        public void setLotStatuses(final List<Lot.LotStatus> lotStatuses) {
            this.lotStatuses = lotStatuses;
        }

        public Integer getDocNumber() {
            return docNumber;
        }

        public void setDocNumber(final Integer docNumber) {
            this.docNumber = docNumber;
        }

        public List<String> getFileFormats() {
            return fileFormats;
        }

        public void setFileFormats(final List<String> fileFormats) {
            this.fileFormats = fileFormats;
        }

        public List<String> getFilter() {
            return filter;
        }

        public void setFilter(final List<String> filter) {
            this.filter = filter;
        }
    }
}
