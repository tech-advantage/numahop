package fr.progilone.pgcn.web.rest.train;

import static fr.progilone.pgcn.web.rest.document.security.AuthorizationConstants.COND_REPORT_HAB0;
import static fr.progilone.pgcn.web.rest.lot.security.AuthorizationConstants.LOT_HAB3;
import static fr.progilone.pgcn.web.rest.train.security.AuthorizationConstants.TRA_HAB0;
import static fr.progilone.pgcn.web.rest.train.security.AuthorizationConstants.TRA_HAB1;
import static fr.progilone.pgcn.web.rest.train.security.AuthorizationConstants.TRA_HAB2;
import static fr.progilone.pgcn.web.rest.train.security.AuthorizationConstants.TRA_HAB3;

import com.codahale.metrics.annotation.Timed;
import fr.progilone.pgcn.domain.AbstractDomainObject;
import fr.progilone.pgcn.domain.dto.train.SimpleTrainDTO;
import fr.progilone.pgcn.domain.dto.train.TrainDTO;
import fr.progilone.pgcn.exception.PgcnException;
import fr.progilone.pgcn.exception.PgcnTechnicalException;
import fr.progilone.pgcn.service.es.EsTrainService;
import fr.progilone.pgcn.service.train.TrainService;
import fr.progilone.pgcn.service.train.ui.UITrainService;
import fr.progilone.pgcn.web.rest.AbstractRestController;
import fr.progilone.pgcn.web.util.AccessHelper;
import fr.progilone.pgcn.web.util.LibraryAccesssHelper;
import jakarta.annotation.security.RolesAllowed;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/api/rest/train")
public class TrainController extends AbstractRestController {

    private static final Logger LOG = LoggerFactory.getLogger(TrainController.class);

    @Autowired
    private AccessHelper accessHelper;
    @Autowired
    private LibraryAccesssHelper libraryAccesssHelper;
    @Autowired
    private EsTrainService esTrainService;
    @Autowired
    private TrainService trainService;
    @Autowired
    private UITrainService uiTrainService;

    @RequestMapping(method = RequestMethod.GET, params = {"search"}, produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @RolesAllowed({TRA_HAB3})
    public ResponseEntity<Page<SimpleTrainDTO>> search(final HttpServletRequest request,
                                                       @RequestParam(value = "search", required = false) final String search,
                                                       @RequestParam(value = "libraries", required = false) final List<String> libraries,
                                                       @RequestParam(value = "projects", required = false) final List<String> projects,
                                                       @RequestParam(value = "active", required = false, defaultValue = "true") final boolean active,
                                                       @RequestParam(value = "statuses", required = false) final List<String> trainStatuses,
                                                       @DateTimeFormat(pattern = "yyyy-MM-dd") @RequestParam(value = "providerSendingDateFrom",
                                                                                                             required = false) final LocalDate providerSendingDateFrom,
                                                       @DateTimeFormat(pattern = "yyyy-MM-dd") @RequestParam(value = "providerSendingDateTo",
                                                                                                             required = false) final LocalDate providerSendingDateTo,
                                                       @DateTimeFormat(pattern = "yyyy-MM-dd") @RequestParam(value = "returnDateFrom",
                                                                                                             required = false) final LocalDate returnDateFrom,
                                                       @DateTimeFormat(pattern = "yyyy-MM-dd") @RequestParam(value = "returnDateTo", required = false) final LocalDate returnDateTo,
                                                       @RequestParam(value = "docNumber", required = false) final Integer docNumber,
                                                       @RequestParam(value = "page", required = false, defaultValue = "0") final Integer page,
                                                       @RequestParam(value = "size", required = false, defaultValue = "10") final Integer size) {
        // Droits d'accès
        final List<String> filteredLibraries = libraryAccesssHelper.getLibraryFilter(request, libraries);
        final List<String> filteredProjects = accessHelper.filterProjects(projects).stream().map(AbstractDomainObject::getIdentifier).collect(Collectors.toList());
        final Page<SimpleTrainDTO> results = trainService.search(search,
                                                                 filteredLibraries,
                                                                 filteredProjects,
                                                                 active,
                                                                 trainStatuses,
                                                                 providerSendingDateFrom,
                                                                 providerSendingDateTo,
                                                                 returnDateFrom,
                                                                 returnDateTo,
                                                                 docNumber,
                                                                 page,
                                                                 size);
        return new ResponseEntity<>(results, HttpStatus.OK);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @RolesAllowed({TRA_HAB3})
    public ResponseEntity<TrainDTO> getById(@PathVariable final String id) {
        // Droits d'accès
        if (!accessHelper.checkTrain(id)) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        final TrainDTO train = uiTrainService.getOne(id);
        return createResponseEntity(train);
    }

    @RequestMapping(method = RequestMethod.GET, params = {"filterByProjects"}, produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @RolesAllowed({LOT_HAB3})
    public ResponseEntity<List<SimpleTrainDTO>> findAllIdentifiersForProjects(final HttpServletRequest request,
                                                                              @RequestParam(value = "projectsIds", required = false) final List<String> projectIds) {
        final List<SimpleTrainDTO> trainIds = trainService.findAllByProjectIds(projectIds);
        return createResponseEntity(trainIds);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.POST)
    @Timed
    @RolesAllowed({TRA_HAB1})
    public ResponseEntity<TrainDTO> update(@RequestBody final TrainDTO train) throws PgcnException {
        // Droits d'accès
        if (!accessHelper.checkTrain(train.getIdentifier())) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        final TrainDTO savedTrain = uiTrainService.update(train);
        esTrainService.indexAsync(savedTrain.getIdentifier());   // Moteur de recherche
        return new ResponseEntity<>(savedTrain, HttpStatus.OK);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    @Timed
    @RolesAllowed({TRA_HAB2})
    public ResponseEntity<TrainDTO> delete(@PathVariable final String id) {
        // Droits d'accès
        if (!accessHelper.checkTrain(id)) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        uiTrainService.delete(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.POST)
    @Timed
    @RolesAllowed({TRA_HAB0})
    public ResponseEntity<TrainDTO> create(@RequestBody final TrainDTO train) {
        // Droits d'accès
        if (train.getProject() != null && !accessHelper.checkProject(train.getProject().getIdentifier())) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        final TrainDTO savedTrain = uiTrainService.create(train);
        esTrainService.indexAsync(savedTrain.getIdentifier());   // Moteur de recherche
        return createResponseEntity(savedTrain);
    }

    @RequestMapping(method = RequestMethod.GET, params = {"dto"}, produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @RolesAllowed({TRA_HAB3})
    public ResponseEntity<Collection<TrainDTO>> findAllActiveDTO() {
        final Collection<TrainDTO> trains = uiTrainService.findAllActiveDTO();
        final List<TrainDTO> filteredTrains = filterTrainDTOs(trains, TrainDTO::getIdentifier);
        return createResponseEntity(filteredTrains);
    }

    @RequestMapping(method = RequestMethod.GET,
                    params = {"dto",
                              "complete"},
                    produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @RolesAllowed({TRA_HAB3})
    public ResponseEntity<Collection<TrainDTO>> findAllDTO() {
        final Collection<TrainDTO> trains = uiTrainService.findAllDTO();
        final List<TrainDTO> filteredTrains = filterTrainDTOs(trains, TrainDTO::getIdentifier);
        return createResponseEntity(filteredTrains);
    }

    @RequestMapping(method = RequestMethod.GET, params = {"project"}, produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @RolesAllowed({TRA_HAB3})
    public ResponseEntity<List<TrainDTO>> findAllForProject(@RequestParam(value = "project") final String projectId) {
        // Droits d'accès au projet
        if (!accessHelper.checkProject(projectId)) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        final List<TrainDTO> trains = uiTrainService.findAllForProject(projectId);
        // Droits d'accès aux trains
        final List<TrainDTO> filteredTrains = filterTrainDTOs(trains, TrainDTO::getIdentifier);
        return createResponseEntity(filteredTrains);
    }

    @RequestMapping(method = RequestMethod.GET,
                    params = {"simpleByProject",
                              "project"},
                    produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<SimpleTrainDTO>> findAllSimpleForProject(@RequestParam(value = "project") final String projectId) {
        // Droits d'accès au projet
        if (!accessHelper.checkProject(projectId)) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        final List<SimpleTrainDTO> trains = uiTrainService.findAllSimpleForProject(projectId);
        // Droits d'accès aux trains
        final List<SimpleTrainDTO> filteredTrains = filterTrainDTOs(trains, SimpleTrainDTO::getIdentifier);
        return createResponseEntity(filteredTrains);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/csv/{id}", produces = "text/csv")
    @Timed
    @RolesAllowed(COND_REPORT_HAB0)
    public void generateSlip(final HttpServletResponse response,
                             @PathVariable final String id,
                             @RequestParam(value = "encoding", defaultValue = "ISO-8859-15") final String encoding,
                             @RequestParam(value = "separator", defaultValue = ";") final char separator) throws PgcnTechnicalException {

        if (!accessHelper.checkTrain(id)) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            return;
        }
        try {
            writeResponseHeaderForDownload(response, "text/csv; charset=" + encoding, null, "bordereau.csv");
            trainService.writeCondReportSlip(response.getOutputStream(), id, encoding, separator);
        } catch (final IOException e) {
            LOG.error(e.getMessage(), e);
            throw new PgcnTechnicalException(e);
        }
    }

    @RequestMapping(method = RequestMethod.GET, value = "/pdf/{id}", produces = "application/pdf")
    @Timed
    @RolesAllowed(COND_REPORT_HAB0)
    public void generateSlipPdf(final HttpServletResponse response, @PathVariable final String id) throws PgcnTechnicalException {
        if (!accessHelper.checkTrain(id)) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            return;
        }
        try {
            writeResponseHeaderForDownload(response, "application/pdf", null, "bordereau.pdf");
            trainService.writeCondReportSlipPDF(response.getOutputStream(), id);
        } catch (final IOException e) {
            LOG.error(e.getMessage(), e);
            throw new PgcnTechnicalException(e);
        }
    }

    /**
     * Filtrage d'une liste de LotDTO sur les droits d'accès de l'utilisateur
     *
     * @param trains
     * @return
     */
    private <T> List<T> filterTrainDTOs(final Collection<T> trains, final Function<T, String> getIdentifierFn) {
        return accessHelper.filterTrains(trains.stream().map(getIdentifierFn).collect(Collectors.toList()))
                           .stream()
                           // Correspondance train autorisé => trainDTO
                           .map(train -> trains.stream().filter(l -> StringUtils.equals(getIdentifierFn.apply(l), train.getIdentifier())).findAny())
                           .filter(Optional::isPresent)
                           .map(Optional::get)
                           .collect(Collectors.toList());
    }
}
