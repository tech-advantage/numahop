package fr.progilone.pgcn.web.rest.delivery;

import static fr.progilone.pgcn.domain.delivery.Delivery.DeliveryPayment.PAID;
import static fr.progilone.pgcn.web.rest.delivery.security.AuthorizationConstants.*;

import com.codahale.metrics.annotation.Timed;
import fr.progilone.pgcn.domain.AbstractDomainObject;
import fr.progilone.pgcn.domain.delivery.Delivery;
import fr.progilone.pgcn.domain.delivery.Delivery.DeliveryStatus;
import fr.progilone.pgcn.domain.dto.audit.AuditDeliveryRevisionDTO;
import fr.progilone.pgcn.domain.dto.checkconfiguration.CheckConfigurationDTO;
import fr.progilone.pgcn.domain.dto.delivery.*;
import fr.progilone.pgcn.domain.dto.document.DigitalDocumentDTO;
import fr.progilone.pgcn.domain.dto.document.PreDeliveryDocumentDTO;
import fr.progilone.pgcn.domain.dto.document.SimpleDeliveredDigitalDocDTO;
import fr.progilone.pgcn.domain.dto.sample.SampleDTO;
import fr.progilone.pgcn.exception.PgcnException;
import fr.progilone.pgcn.exception.PgcnTechnicalException;
import fr.progilone.pgcn.service.JasperReportsService;
import fr.progilone.pgcn.service.delivery.DeliveryReportingService;
import fr.progilone.pgcn.service.delivery.DeliveryService;
import fr.progilone.pgcn.service.delivery.mapper.DeliveryMapper;
import fr.progilone.pgcn.service.delivery.ui.UIDeliveryService;
import fr.progilone.pgcn.service.es.EsDeliveryService;
import fr.progilone.pgcn.service.sample.SampleService;
import fr.progilone.pgcn.web.rest.AbstractRestController;
import fr.progilone.pgcn.web.util.AccessHelper;
import fr.progilone.pgcn.web.util.LibraryAccesssHelper;
import jakarta.annotation.security.RolesAllowed;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/api/rest/delivery")
public class DeliveryController extends AbstractRestController {

    private static final Logger LOG = LoggerFactory.getLogger(DeliveryController.class);

    private final UIDeliveryService uiDeliveryService;
    private final DeliveryService deliveryService;
    private final DeliveryReportingService deliveryReportingService;
    private final EsDeliveryService esDeliveryService;
    private final AccessHelper accessHelper;
    private final LibraryAccesssHelper libraryAccesssHelper;
    private final SampleService sampleService;

    private static final Map<String, Map<String, String>> PROGRESS_VALUES = new HashMap<>();

    @Autowired
    public DeliveryController(final UIDeliveryService uiDeliveryService,
                              final DeliveryService deliveryService,
                              final DeliveryReportingService deliveryReportingService,
                              final EsDeliveryService esDeliveryService,
                              final AccessHelper accessHelper,
                              final LibraryAccesssHelper libraryAccesssHelper,
                              final SampleService sampleService) {
        this.uiDeliveryService = uiDeliveryService;
        this.deliveryService = deliveryService;
        this.deliveryReportingService = deliveryReportingService;
        this.esDeliveryService = esDeliveryService;
        this.accessHelper = accessHelper;
        this.libraryAccesssHelper = libraryAccesssHelper;
        this.sampleService = sampleService;
    }

    @RequestMapping(method = RequestMethod.GET, params = {"search"}, produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @RolesAllowed(DEL_HAB0)
    public ResponseEntity<Page<SimpleDeliveryDTO>> search(final HttpServletRequest request,
                                                          @RequestParam(value = "search", required = false) final String search,
                                                          @RequestParam(value = "libraries", required = false) final List<String> libraries,
                                                          @RequestParam(value = "projects", required = false) final List<String> projects,
                                                          @RequestParam(value = "lots", required = false) final List<String> lots,
                                                          @RequestParam(value = "providers", required = false) final List<String> providers,
                                                          @RequestParam(value = "status", required = false) final List<DeliveryStatus> status,
                                                          @DateTimeFormat(pattern = "yyyy-MM-dd") @RequestParam(value = "deliveryDateFrom",
                                                                                                                required = false) final LocalDate dateFrom,
                                                          @DateTimeFormat(pattern = "yyyy-MM-dd") @RequestParam(value = "deliveryDateTo", required = false) final LocalDate dateTo,
                                                          @RequestParam(value = "page", required = false, defaultValue = "0") final Integer page,
                                                          @RequestParam(value = "size", required = false, defaultValue = "10") final Integer size) {

        final List<String> filteredLibraries = libraryAccesssHelper.getLibraryFilter(request, libraries);
        final List<String> filteredProjects = accessHelper.filterProjects(projects).stream().map(AbstractDomainObject::getIdentifier).collect(Collectors.toList());
        return new ResponseEntity<>(uiDeliveryService.search(search, filteredLibraries, filteredProjects, lots, providers, status, dateFrom, dateTo, page, size), HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.GET,
                    params = {"widget",
                              "from"},
                    produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @RolesAllowed({DEL_HAB0})
    public ResponseEntity<List<AuditDeliveryRevisionDTO>> getDeliveriesForWidget(final HttpServletRequest request,
                                                                                 @DateTimeFormat(pattern = "yyyy-MM-dd") @RequestParam(name = "from") final LocalDate fromDate,
                                                                                 @RequestParam(value = "library", required = false) final List<String> libraries,
                                                                                 @RequestParam(value = "project", required = false) final List<String> projects,
                                                                                 @RequestParam(value = "lot", required = false) final List<String> lots,
                                                                                 @RequestParam(value = "status", required = false) final List<Delivery.DeliveryStatus> status) {

        final List<String> filteredLibraries = libraryAccesssHelper.getLibraryFilter(request, libraries);
        final List<String> filteredProjects = accessHelper.filterProjects(projects).stream().map(AbstractDomainObject::getIdentifier).collect(Collectors.toList());

        // Chargement
        final List<AuditDeliveryRevisionDTO> revisions = uiDeliveryService.getDeliveriesForWidget(fromDate, filteredLibraries, filteredProjects, lots, status, false);
        // Réponse
        return new ResponseEntity<>(revisions, HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.GET,
                    params = {"sampledWidget",
                              "from"},
                    produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @RolesAllowed({DEL_HAB0})
    public ResponseEntity<List<AuditDeliveryRevisionDTO>> getSampledDeliveriesForWidget(final HttpServletRequest request,
                                                                                        @DateTimeFormat(pattern = "yyyy-MM-dd") @RequestParam(name = "from") final LocalDate fromDate,
                                                                                        @RequestParam(value = "library", required = false) final List<String> libraries,
                                                                                        @RequestParam(value = "project", required = false) final List<String> projects,
                                                                                        @RequestParam(value = "lot", required = false) final List<String> lots,
                                                                                        @RequestParam(value = "status", defaultValue = "TO_BE_CONTROLLED") final List<
                                                                                                                                                                      Delivery.DeliveryStatus> status) {

        final List<String> filteredLibraries = libraryAccesssHelper.getLibraryFilter(request, libraries);
        final List<String> filteredProjects = accessHelper.filterProjects(projects).stream().map(AbstractDomainObject::getIdentifier).collect(Collectors.toList());

        // Chargement
        final List<AuditDeliveryRevisionDTO> revisions = uiDeliveryService.getDeliveriesForWidget(fromDate, filteredLibraries, filteredProjects, lots, status, true);
        // Réponse
        return new ResponseEntity<>(revisions, HttpStatus.OK);
    }

    /**
     * @param id
     * @param lockedDocs
     * @param metaDatas
     * @param createDocs
     *            si true, création des UD associées aux metaDatas
     * @return
     */
    @RequestMapping(value = "/{id}", method = RequestMethod.POST, params = {"deliver"})
    @Timed
    @ResponseStatus(HttpStatus.OK)
    @RolesAllowed({DEL_HAB5,
                   DEL_HAB5_2})
    public ResponseEntity<?> deliver(final HttpServletRequest request,
                                     @PathVariable final String id,
                                     @RequestParam(value = "lockedDocs", required = false) List<String> lockedDocs,
                                     @RequestBody final List<PreDeliveryDocumentDTO> metaDatas,
                                     @RequestParam(name = "create_docs", required = false, defaultValue = "false") final boolean createDocs,
                                     @RequestParam(value = "prefixToExclude", required = false) final List<String> prefixToExclude) throws PgcnTechnicalException {
        if (!accessHelper.checkDelivery(id)) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        // DEL_HAB5_2 est requis pour la livraison avec création d'UD
        if (createDocs && !request.isUserInRole(DEL_HAB5_2)) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        if (lockedDocs == null) {
            lockedDocs = new ArrayList<>();
        }
        // reset les % de progression
        if (StringUtils.isNotBlank(id) && DeliveryController.PROGRESS_VALUES.containsKey(id)) {
            DeliveryController.PROGRESS_VALUES.remove(id);
        }

        uiDeliveryService.deliver(id, lockedDocs, metaDatas, createDocs, prefixToExclude);

        return new ResponseEntity<>(HttpStatus.OK);
    }

    /**
     * @param id
     * @param createDocs
     *            si true, on ne fait pas de rapprochement entre les fichiers et les doc physiques du lot de la livraison
     * @return
     */
    @RequestMapping(value = "/{id}", method = RequestMethod.GET, params = {"predeliver"})
    @Timed
    @RolesAllowed({DEL_HAB5,
                   DEL_HAB5_2})
    public ResponseEntity<PreDeliveryDTO> predeliver(final HttpServletRequest request,
                                                     @PathVariable final String id,
                                                     @RequestParam(name = "create_docs", required = false, defaultValue = "false") final boolean createDocs) {
        if (!accessHelper.checkDelivery(id)) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        // DEL_HAB5_2 est requis pour la livraison avec création d'UD
        if (createDocs && !request.isUserInRole(DEL_HAB5_2)) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        final PreDeliveryDTO pddto = uiDeliveryService.predeliver(id, createDocs);
        return new ResponseEntity<>(pddto, HttpStatus.OK);
    }

    @RequestMapping(value = "/csv/{id}", method = RequestMethod.GET, produces = "text/csv")
    @Timed
    @RolesAllowed(DEL_HAB0)
    public void generateSlipCSV(final HttpServletResponse response,
                                @PathVariable final String id,
                                @RequestParam(value = "encoding", defaultValue = JasperReportsService.ENCODING_UTF8) final String encoding,
                                @RequestParam(value = "separator", defaultValue = ";") final char separator) throws PgcnTechnicalException {

        try {
            writeResponseHeaderForDownload(response, "text/csv; charset=" + encoding, null, "bordereau_livraison.csv");
            deliveryService.writeSlip(response.getOutputStream(), id, encoding, separator);
        } catch (final IOException e) {
            LOG.error(e.getMessage(), e);
            throw new PgcnTechnicalException(e);
        }
    }

    @RequestMapping(value = "/pdf/{id}", method = RequestMethod.GET, produces = "application/pdf")
    @Timed
    @RolesAllowed(DEL_HAB0)
    public void generateSlipPDF(final HttpServletRequest request, final HttpServletResponse response, @PathVariable final String id) throws PgcnTechnicalException {

        try {
            writeResponseHeaderForDownload(response, "application/pdf", null, "bordereau_livraison.pdf");
            deliveryService.writeDeliveryPdfSlip(response.getOutputStream(), id);

        } catch (final IOException e) {
            LOG.error(e.getMessage(), e);
            throw new PgcnTechnicalException(e);
        }
    }

    @RequestMapping(value = "/lot_csv/{id}", method = RequestMethod.GET, produces = "text/csv")
    @Timed
    @RolesAllowed(DEL_HAB0)
    public void generateSlipLotCSV(final HttpServletRequest request,
                                   final HttpServletResponse response,
                                   @PathVariable final String id,
                                   @RequestParam(value = "encoding", defaultValue = JasperReportsService.ENCODING_UTF8) final String encoding,
                                   @RequestParam(value = "separator", defaultValue = ";") final char separator) throws PgcnTechnicalException {

        try {
            writeResponseHeaderForDownload(response, "text/csv; charset=" + encoding, null, "bordereau.csv");
            deliveryService.writeSlipLot(response.getOutputStream(), id, encoding, separator);
        } catch (final IOException e) {
            LOG.error(e.getMessage(), e);
            throw new PgcnTechnicalException(e);
        }
    }

    @RequestMapping(value = "/lot_pdf/{id}", method = RequestMethod.GET, produces = "application/pdf")
    @Timed
    @RolesAllowed(DEL_HAB0)
    public void generateSlipLotPDF(final HttpServletRequest request, final HttpServletResponse response, @PathVariable final String id) throws PgcnTechnicalException {

        try {
            writeResponseHeaderForDownload(response, "application/pdf", null, "bordereau.pdf");
            deliveryService.writeSlipLotPDF(response.getOutputStream(), id);
        } catch (final IOException e) {
            LOG.error(e.getMessage(), e);
            throw new PgcnTechnicalException(e);
        }
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET, params = {"digitalDocuments"}, produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @RolesAllowed(DEL_HAB0)
    public ResponseEntity<List<SimpleDeliveredDigitalDocDTO>> getSimpleDigitalDocuments(@PathVariable final String id) {
        if (!accessHelper.checkDelivery(id)) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }

        final List<SimpleDeliveredDigitalDocDTO> sortedDocs = uiDeliveryService.getSimpleDigitalDocuments(id)
                                                                               .stream()
                                                                               .sorted(Comparator.comparing(SimpleDeliveredDigitalDocDTO::getDigitalId))
                                                                               .collect(Collectors.toList());
        final Map<String, String> values;
        if (DeliveryController.PROGRESS_VALUES.containsKey(id)) {
            values = DeliveryController.PROGRESS_VALUES.get(id);
        } else {
            values = new HashMap<String, String>();
            DeliveryController.PROGRESS_VALUES.put(id, values);
        }

        // Avancement de la livraison au rechargement
        sortedDocs.forEach(doc -> {
            if (!values.containsKey(doc.getDigitalId())) {
                values.put(doc.getDigitalId(), "1");
            }
            doc.setProgress(values.get(doc.getDigitalId()));
        });

        return new ResponseEntity<>(sortedDocs, HttpStatus.OK);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET, params = {"deliverySample"}, produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @RolesAllowed(DEL_HAB0)
    public ResponseEntity<SampleDTO> getSample(@PathVariable final String id) {
        if (!accessHelper.checkDelivery(id)) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        final SampleDTO sample = sampleService.findByDelivery(id);
        return new ResponseEntity<>(sample, HttpStatus.OK);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET, params = {"checkConfig"}, produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<CheckConfigurationDTO> getActiveCheckConfig(@PathVariable final String id) {
        if (!accessHelper.checkDelivery(id)) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        final CheckConfigurationDTO config = uiDeliveryService.getActiveCheckConfiguration(id);
        return new ResponseEntity<>(config, HttpStatus.OK);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET, params = {"deliveryReport"}, produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    @Timed
    @RolesAllowed(DEL_HAB0)
    public ResponseEntity<?> getDeliveryReport(final HttpServletResponse response, @PathVariable final String id) throws PgcnTechnicalException {

        if (!accessHelper.checkDelivery(id)) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }

        final File report = deliveryReportingService.getDeliveryReportForDelivery(id);
        if (report == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } else {
            writeResponseForDownload(response, report, MediaType.TEXT_PLAIN_VALUE, report.getName());
            return new ResponseEntity<>(HttpStatus.OK);
        }
    }

    @RolesAllowed(DEL_HAB1)
    @RequestMapping(method = RequestMethod.POST)
    @Timed
    public ResponseEntity<DeliveryDTO> create(final HttpServletRequest request, @RequestBody final ManualDeliveryDTO delivery) throws PgcnException {
        // Droits d'accès au lot
        if (delivery.getLot() != null && !accessHelper.checkLot(delivery.getLot().getIdentifier())) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        // Droits d'accès à la livraison: paiment
        if (StringUtils.equals(delivery.getPayment(), PAID.name()) && !request.isUserInRole(DEL_HAB8)) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        final DeliveryDTO savedDelivery = uiDeliveryService.create(delivery);
        esDeliveryService.indexAsync(savedDelivery.getIdentifier()); // Moteur de recherche
        return new ResponseEntity<>(savedDelivery, HttpStatus.CREATED);
    }

    @RolesAllowed(DEL_HAB3)
    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    @ResponseStatus(HttpStatus.OK)
    @Timed
    public ResponseEntity<?> delete(final HttpServletRequest request, @PathVariable final String id) {
        if (!accessHelper.checkDelivery(id)) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        // Droits d'accès à la livraison: paiment
        if (!request.isUserInRole(DEL_HAB8)) {
            final Delivery dbDelivery = deliveryService.getOne(id);
            if (dbDelivery.getPayment() == PAID) {
                return new ResponseEntity<>(HttpStatus.FORBIDDEN);
            }
        }
        deliveryService.delete(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @RolesAllowed({DEL_HAB2,
                   DEL_HAB8})
    @RequestMapping(value = "/{id}", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<DeliveryDTO> update(final HttpServletRequest request, @RequestBody final ManualDeliveryDTO delivery) throws PgcnException {
        // Droits d'accès au lot
        if (!accessHelper.checkDelivery(delivery.getIdentifier())) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        // Droits de modification restreints: pas le paiement, la date de réception, le mode de livraison, le lot de rattachement (#874)
        if (!request.isUserInRole(DEL_HAB8)) {
            final Delivery dbDelivery = deliveryService.getOne(delivery.getIdentifier());

            if (hasProtectedChanges(delivery, dbDelivery)) {
                return new ResponseEntity<>(HttpStatus.FORBIDDEN);
            }
        }
        final DeliveryDTO savedDelivery = uiDeliveryService.update(delivery);
        esDeliveryService.indexAsync(delivery.getIdentifier()); // Moteur de recherche
        return new ResponseEntity<>(savedDelivery, HttpStatus.OK);
    }

    private boolean hasProtectedChanges(final ManualDeliveryDTO newDelivery, final Delivery oldDelivery) {
        // paiement
        return !StringUtils.equals(newDelivery.getPayment(), oldDelivery.getPayment().name())
               // date de réception
               || !Objects.equals(newDelivery.getReceptionDate(), oldDelivery.getReceptionDate())
               // mode de livraison
               || !StringUtils.equals(newDelivery.getMethod(), oldDelivery.getMethod().name())
               // lot
               || !Objects.equals(newDelivery.getLot() != null ? newDelivery.getLot().getIdentifier()
                                                               : null,
                                  oldDelivery.getLot() != null ? oldDelivery.getLot().getIdentifier()
                                                               : null);

    }

    @RolesAllowed(DEL_HAB0)
    @RequestMapping(value = "/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<DeliveryDTO> getById(@PathVariable final String id) {
        if (!accessHelper.checkDelivery(id)) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        final DeliveryDTO delivery = uiDeliveryService.getOne(id);
        return createResponseEntity(delivery);
    }

    @RolesAllowed(DEL_HAB0)
    @RequestMapping(method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<SimpleDeliveryDTO>> getByLot(@RequestParam(value = "lot") final String lotId) {
        if (!accessHelper.checkLot(lotId)) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        final List<SimpleDeliveryDTO> delivery = uiDeliveryService.findByLot(lotId);
        return createResponseEntity(delivery);
    }

    @RequestMapping(method = RequestMethod.GET, params = {"dto"}, produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Collection<SimpleDeliveryDTO>> findAllSimpleDTO() {
        final Collection<Delivery> delivs = accessHelper.filterDeliveries(null);    // charge + filtre toutes les livraisons
        final Collection<SimpleDeliveryDTO> delivsDTO = delivs.stream().map(DeliveryMapper.INSTANCE::deliveryToSimpleDeliveryDTO).collect(Collectors.toList());
        return createResponseEntity(delivsDTO);
    }

    @RequestMapping(method = RequestMethod.GET, params = {"previouscheckslip"}, produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @RolesAllowed(DEL_HAB0)
    public ResponseEntity<Collection<SimpleDeliveryDTO>> getPreviousCheckSlips(@RequestParam(value = "digitalDocIdentifier") final String digitalDocIdentifier) {
        if (!accessHelper.checkDigitalDocument(digitalDocIdentifier)) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        return new ResponseEntity<>(uiDeliveryService.findPreviousDeliveriesForCheckSlips(digitalDocIdentifier), HttpStatus.OK);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET, params = {"duplicate"}, produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @RolesAllowed({DEL_HAB2,
                   DEL_HAB8})
    public ResponseEntity<DeliveryDTO> duplicate(final HttpServletRequest request, @PathVariable final String id) {
        return new ResponseEntity<>(uiDeliveryService.duplicate(id), HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.GET, params = {"filterByProjectsLots"}, produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Collection<SimpleDeliveryDTO>> getSimpleByProjectsLots(@RequestParam(value = "filteredProjects", required = false) final List<String> projects,
                                                                                 @RequestParam(value = "filteredLots", required = false) final List<String> lots) {

        Collection<Delivery> deliveries = deliveryService.findByProjectsAndLots(projects, lots);
        deliveries = accessHelper.filterDeliveries(deliveries.stream().map(Delivery::getIdentifier).collect(Collectors.toList()));
        return createResponseEntity(deliveries.stream().map(DeliveryMapper.INSTANCE::deliveryToSimpleDeliveryDTO).collect(Collectors.toList()));
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET, params = {"forViewer"}, produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @RolesAllowed({DEL_HAB0})
    public ResponseEntity<SimpleDeliveryForViewerDTO> getSimpleForViewer(@PathVariable final String id) {
        if (!accessHelper.checkDelivery(id)) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        return createResponseEntity(uiDeliveryService.getSimpleWithLot(id));
    }

    @RequestMapping(method = RequestMethod.GET, params = {"project"}, produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @RolesAllowed({DEL_HAB0})
    public ResponseEntity<List<DeliveryDTO>> findAllForProject(@RequestParam(value = "project") final String projectId) {
        // Droits d'accès au projet
        if (!accessHelper.checkProject(projectId)) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        // Droits d'accès aux lots
        final List<DeliveryDTO> deliveries = uiDeliveryService.findAllForProject(projectId);
        return createResponseEntity(deliveries);
    }

    @RequestMapping(method = RequestMethod.GET,
                    params = {"docUnit",
                              "latest"},
                    produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @RolesAllowed({DEL_HAB0})
    public ResponseEntity<DeliveryDTO> findLatestDelivery(@RequestParam(value = "docUnit") final String docUnitId) {
        // Droits d'accès au projet
        if (!accessHelper.checkDocUnit(docUnitId)) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        return new ResponseEntity<>(uiDeliveryService.findLatestDelivery(docUnitId), HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.GET, params = {"delivstatus"}, produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @RolesAllowed({DEL_HAB0})
    public ResponseEntity<Map<String, Object>> getDeliveryStatus(final HttpServletRequest request, @PathVariable final String id) {
        final Delivery delivery = deliveryService.findOneWithDocuments(id);
        // Non trouvé
        if (delivery == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        if (!accessHelper.checkDelivery(id)) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        // Réponse
        final Map<String, Object> response = uiDeliveryService.getDeliveryStatus(delivery);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @RolesAllowed({DEL_HAB8})
    @RequestMapping(value = "/{id}", method = RequestMethod.POST, params = {"detachDoc"}, produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<DeliveryDTO> detachDigitalDoc(@PathVariable final String id, @RequestBody final DigitalDocumentDTO docToDetach) throws PgcnException {
        // Droits d'accès
        if (!accessHelper.checkDelivery(id)) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }

        final DeliveryDTO cleanedDelivery = uiDeliveryService.detachDigitalDoc(id, docToDetach);
        return new ResponseEntity<>(cleanedDelivery, HttpStatus.OK);
    }

    /**
     * Recupere le % d'avancement de la livraison du doc.
     *
     * @param id
     * @param digitalId
     * @return
     */
    @RequestMapping(value = "/{id}", method = RequestMethod.GET, params = {"delivprogress"}, produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @RolesAllowed({DEL_HAB0})
    public ResponseEntity<List<String>> getDeliveryProgress(@PathVariable final String id, @RequestParam(value = "digitalId", required = false) final String digitalId) {
        String prog = "0";
        if (StringUtils.isNotBlank(id) && DeliveryController.PROGRESS_VALUES.containsKey(id)) {
            final Map<String, String> values = DeliveryController.PROGRESS_VALUES.get(id);
            if (values != null && values.containsKey(digitalId)) {
                prog = values.get(digitalId);
            }
        }
        final List<String> values = new ArrayList<>();
        values.add(prog);
        return new ResponseEntity<>(values, HttpStatus.OK);

    }

    /**
     * Infos espace disque de la bibliothèque.
     */
    @RolesAllowed({DEL_HAB0})
    @RequestMapping(method = RequestMethod.GET,
                    params = {"diskspace",
                              "widget"},
                    produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Map<String, Long>> getDiskInfos(final HttpServletRequest request, @RequestParam(value = "libraries", required = true) final List<String> libraries) {

        final List<String> filteredLibraries = libraryAccesssHelper.getLibraryFilter(request, libraries);
        Map<String, Long> diskInfos = new HashMap<>();
        if (filteredLibraries.size() == 1) {
            diskInfos = uiDeliveryService.getDiskInfos(filteredLibraries.get(0));
        }
        return new ResponseEntity<>(diskInfos, HttpStatus.OK);
    }

}
