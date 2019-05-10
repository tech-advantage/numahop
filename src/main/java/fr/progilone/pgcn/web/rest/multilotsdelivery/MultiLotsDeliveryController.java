package fr.progilone.pgcn.web.rest.multilotsdelivery;

import static fr.progilone.pgcn.domain.delivery.Delivery.DeliveryPayment.PAID;
import static fr.progilone.pgcn.web.rest.delivery.security.AuthorizationConstants.DEL_HAB0;
import static fr.progilone.pgcn.web.rest.delivery.security.AuthorizationConstants.DEL_HAB1;
import static fr.progilone.pgcn.web.rest.delivery.security.AuthorizationConstants.DEL_HAB2;
import static fr.progilone.pgcn.web.rest.delivery.security.AuthorizationConstants.DEL_HAB3;
import static fr.progilone.pgcn.web.rest.delivery.security.AuthorizationConstants.DEL_HAB5;
import static fr.progilone.pgcn.web.rest.delivery.security.AuthorizationConstants.DEL_HAB5_2;
import static fr.progilone.pgcn.web.rest.delivery.security.AuthorizationConstants.DEL_HAB8;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import javax.annotation.security.RolesAllowed;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
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

import com.codahale.metrics.annotation.Timed;

import fr.progilone.pgcn.domain.AbstractDomainObject;
import fr.progilone.pgcn.domain.dto.delivery.PreDeliveryDTO;
import fr.progilone.pgcn.domain.dto.delivery.SimpleDeliveryLotDTO;
import fr.progilone.pgcn.domain.dto.document.PreDeliveryDocumentDTO;
import fr.progilone.pgcn.domain.dto.document.PreDeliveryLockedDocsDTO;
import fr.progilone.pgcn.domain.dto.document.SimpleDeliveredDigitalDocDTO;
import fr.progilone.pgcn.domain.dto.lot.SimpleLotForDeliveryDTO;
import fr.progilone.pgcn.domain.dto.multilotsdelivery.MultiLotsDeliveryDTO;
import fr.progilone.pgcn.domain.multilotsdelivery.MultiLotsDelivery;
import fr.progilone.pgcn.domain.multilotsdelivery.MultiLotsDelivery.DeliveryStatus;
import fr.progilone.pgcn.exception.PgcnException;
import fr.progilone.pgcn.exception.PgcnTechnicalException;
import fr.progilone.pgcn.service.delivery.ui.UIDeliveryService;
import fr.progilone.pgcn.service.es.EsDeliveryService;
import fr.progilone.pgcn.service.multilotsdelivery.ui.UIMultiLotsDeliveryService;
import fr.progilone.pgcn.web.rest.AbstractRestController;
import fr.progilone.pgcn.web.util.AccessHelper;
import fr.progilone.pgcn.web.util.LibraryAccesssHelper;

@RestController
@RequestMapping(value = "/api/rest/multidelivery")
public class MultiLotsDeliveryController extends AbstractRestController {


    private final AccessHelper accessHelper;
    private final LibraryAccesssHelper libraryAccesssHelper;
    private final UIMultiLotsDeliveryService uiMultiService;
    private final UIDeliveryService uiDeliveryService;
    private final EsDeliveryService esDeliveryService;


    @Autowired
    public MultiLotsDeliveryController(final AccessHelper accessHelper,
                              final LibraryAccesssHelper libraryAccesssHelper,
                              final UIMultiLotsDeliveryService uiMultiService,
                              final UIDeliveryService uiDeliveryService,
                              final EsDeliveryService esDeliveryService) {

        this.accessHelper = accessHelper;
        this.libraryAccesssHelper = libraryAccesssHelper;
        this.uiMultiService = uiMultiService;
        this.uiDeliveryService = uiDeliveryService;
        this.esDeliveryService = esDeliveryService;
    }


    /**
     *
     * @param id
     * @return
     */
    @RequestMapping(value = "/{id}", method = RequestMethod.GET, params = {"predeliver"}, produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @RolesAllowed(DEL_HAB5)
    public ResponseEntity<Map<String, PreDeliveryDTO>> predeliver(final HttpServletRequest request,
                                                                                @PathVariable final String id) {

        final MultiLotsDeliveryDTO multiDelivery = uiMultiService.findOneByIdWithDeliveries(id);
        // Droits d'accès par delivery
        for(final SimpleDeliveryLotDTO deliv : multiDelivery.getDeliveries()) {
            if (!accessHelper.checkDelivery(deliv.getIdentifier())) {
                return new ResponseEntity<>(HttpStatus.FORBIDDEN);
            }
        }
        final Map<String, PreDeliveryDTO> pddtosByDeliv = new HashMap<>();
        multiDelivery.getDeliveries()
                        .forEach(del -> {
                pddtosByDeliv.put(del.getLabel(), uiDeliveryService.predeliver(del.getIdentifier(), false));
        });
        return new ResponseEntity<>(pddtosByDeliv, HttpStatus.OK);
    }


    /**
     * 
     * @param request
     * @param id
     * @param wrapper
     * @param prefixToExclude
     * @return
     * @throws PgcnTechnicalException
     */
    @RequestMapping(value = "/{id}", method = RequestMethod.POST, params = {"deliver"})
    @Timed
    @ResponseStatus(HttpStatus.OK)
    @RolesAllowed({DEL_HAB5, DEL_HAB5_2})
    public ResponseEntity<?> deliver(final HttpServletRequest request,
                                     @PathVariable final String id,
                                     @RequestBody final MultiLotsDeliveryRequestWrapper wrapper,
                                     @RequestParam(value = "prefixToExclude", required = false) final List<String> prefixToExclude) throws PgcnTechnicalException {

        final MultiLotsDeliveryDTO multiDelivery = uiMultiService.findOneByIdWithDeliveries(id);
        // Droits d'accès par delivery
        for(final SimpleDeliveryLotDTO deliv : multiDelivery.getDeliveries()) {
            if (!accessHelper.checkDelivery(deliv.getIdentifier())) {
                return new ResponseEntity<>(HttpStatus.FORBIDDEN);
            }
        }

        final List<PreDeliveryLockedDocsDTO> lockedDocs =  wrapper.getLockedDocs();
        final List<PreDeliveryDocumentDTO> metaDatas = wrapper.getMetadatas();

        uiMultiService.deliver(multiDelivery, lockedDocs, metaDatas, prefixToExclude);
        return new ResponseEntity<>(HttpStatus.OK);
    }



    @RequestMapping(method = RequestMethod.GET, params = {"search"}, produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @RolesAllowed(DEL_HAB0)
    public ResponseEntity<Page<MultiLotsDeliveryDTO>> search(final HttpServletRequest request,
                                                          @RequestParam(value = "search", required = false) final String search,
                                                          @RequestParam(value = "libraries", required = false) final List<String> libraries,
                                                          @RequestParam(value = "projects", required = false) final List<String> projects,
                                                          @RequestParam(value = "lots", required = false) final List<String> lots,
                                                          @RequestParam(value = "providers", required = false) final List<String> providers,
                                                          @RequestParam(value = "status", required = false) final List<DeliveryStatus> status,
                                                          @DateTimeFormat(pattern = "yyyy-MM-dd")
                                                          @RequestParam(value = "deliveryDateFrom", required = false) final LocalDate dateFrom,
                                                          @DateTimeFormat(pattern = "yyyy-MM-dd")
                                                          @RequestParam(value = "deliveryDateTo", required = false) final LocalDate dateTo,
                                                          @RequestParam(value = "page", required = false, defaultValue = "0") final Integer page,
                                                          @RequestParam(value = "size", required = false, defaultValue = "10") final Integer size) {

        final List<String> filteredLibraries = libraryAccesssHelper.getLibraryFilter(request, libraries);
        final List<String> filteredProjects =
            accessHelper.filterProjects(projects).stream().map(AbstractDomainObject::getIdentifier).collect(Collectors.toList());
        return new ResponseEntity<>(uiMultiService.search(search,
                                                             filteredLibraries,
                                                             filteredProjects,
                                                             lots,
                                                             providers,
                                                             status,
                                                             dateFrom,
                                                             dateTo,
                                                             page,
                                                             size), HttpStatus.OK);
    }

    @RolesAllowed(DEL_HAB0)
    @RequestMapping(value = "/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<MultiLotsDeliveryDTO> getById(@PathVariable final String id) {

        final MultiLotsDeliveryDTO multiDelivery = uiMultiService.findOneByIdWithDeliveries(id);
        // Droits d'accès par delivery
        for(final SimpleDeliveryLotDTO deliv : multiDelivery.getDeliveries()) {
            if (!accessHelper.checkDelivery(deliv.getIdentifier())) {
                return new ResponseEntity<>(HttpStatus.FORBIDDEN);
            }
        }
        return createResponseEntity(multiDelivery);
    }
    

    @RequestMapping(value = "/{id}", method = RequestMethod.GET, params = {"digitalDocuments"}, produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @RolesAllowed(DEL_HAB0)
    public ResponseEntity<Map<String, List<SimpleDeliveredDigitalDocDTO>>> getSimpleDigitalDocuments(@PathVariable final String id) {

        final MultiLotsDeliveryDTO multiDelivery = uiMultiService.findOneByIdWithDeliveries(id);
        // Droits d'accès par delivery
        for(final SimpleDeliveryLotDTO deliv : multiDelivery.getDeliveries()) {
            if (!accessHelper.checkDelivery(deliv.getIdentifier())) {
                return new ResponseEntity<>(HttpStatus.FORBIDDEN);
            }
        }

        final Map<String, List<SimpleDeliveredDigitalDocDTO>> multiDocs = new HashMap<>();

        multiDelivery.getDeliveries().forEach(del -> {
                                                  final Set<SimpleDeliveredDigitalDocDTO> docs = uiDeliveryService.getSimpleDigitalDocuments(del.getIdentifier());
                                                  final List<SimpleDeliveredDigitalDocDTO> sortedDocs = docs.stream()
                                                                                              .sorted(Comparator.comparing(SimpleDeliveredDigitalDocDTO::getDigitalId))
                                                                                              .collect(Collectors.toList());
                                                  multiDocs.put(del.getIdentifier(), sortedDocs);
                                              });

        return new ResponseEntity<>(multiDocs, HttpStatus.OK);
    }


    @RolesAllowed(DEL_HAB1)
    @RequestMapping(method = RequestMethod.POST)
    @Timed
    public ResponseEntity<MultiLotsDeliveryDTO> create(final HttpServletRequest request, @RequestBody final MultiLotsDeliveryDTO multiDelivery) throws PgcnException {
        
        if (multiDelivery.isSelectedByTrain() && StringUtils.isNotBlank(multiDelivery.getTrainId())) {
            // Recup tous les lots du train
            final List<SimpleLotForDeliveryDTO> lotsDto = uiMultiService.findLotsByTrainIdentifier(multiDelivery.getTrainId());
            multiDelivery.setLots(lotsDto);
        }
        
        // Droits d'accès aux lots
        for(final SimpleLotForDeliveryDTO lot : multiDelivery.getLots()) {
            if (!accessHelper.checkLot(lot.getIdentifier())) {
                return new ResponseEntity<>(HttpStatus.FORBIDDEN);
            }
        }
        // Droits d'accès à la livraison: paiment
        if (StringUtils.equals(multiDelivery.getPayment(), PAID.name()) && !request.isUserInRole(DEL_HAB8)) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        
        final MultiLotsDeliveryDTO savedDelivery = uiMultiService.create(multiDelivery);
        savedDelivery.getDeliveries().forEach(deliv -> esDeliveryService.indexAsync(deliv.getIdentifier())); // Moteur de recherche
        return new ResponseEntity<>(savedDelivery, HttpStatus.CREATED);
    }

    @RolesAllowed({DEL_HAB2, DEL_HAB8})
    @RequestMapping(value = "/{id}", method = RequestMethod.POST)
    @Timed
    public ResponseEntity<MultiLotsDeliveryDTO> update(final HttpServletRequest request, @RequestBody final MultiLotsDeliveryDTO multiDelivery) throws PgcnException {

        if (multiDelivery.isSelectedByTrain() && StringUtils.isNotBlank(multiDelivery.getTrainId())) {
            // Recup tous les lots du train
            final List<SimpleLotForDeliveryDTO> lotsDto = uiMultiService.findLotsByTrainIdentifier(multiDelivery.getTrainId());
            multiDelivery.setLots(lotsDto);
        }
        
        // Droits d'accès aux lots
        for(final SimpleDeliveryLotDTO deliv : multiDelivery.getDeliveries()) {
            if (!accessHelper.checkLot(deliv.getLot().getIdentifier())) {
                return new ResponseEntity<>(HttpStatus.FORBIDDEN);
            }
        }

        // Droits de modification restreints: pas le paiement, la date de réception, le mode de livraison, les lots de rattachement (#874)
        if (!request.isUserInRole(DEL_HAB8)) {
                final MultiLotsDelivery dbMultiDelivery = uiMultiService.getOne(multiDelivery.getIdentifier());
                if (hasProtectedChanges(multiDelivery, dbMultiDelivery)) {
                    return new ResponseEntity<>(HttpStatus.FORBIDDEN);
                }
        }
        final MultiLotsDeliveryDTO saved = uiMultiService.update(multiDelivery);
        saved.getDeliveries().forEach(deliv -> esDeliveryService.indexAsync(deliv.getIdentifier())); // Moteur de recherche
        return new ResponseEntity<>(saved, HttpStatus.OK);
    }


    @RolesAllowed(DEL_HAB3)
    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    @ResponseStatus(HttpStatus.OK)
    @Timed
    public ResponseEntity<?> delete(final HttpServletRequest request, @PathVariable final String id) {

        final MultiLotsDeliveryDTO multi = uiMultiService.findOneByIdWithDeliveries(id);
        for (final SimpleDeliveryLotDTO d:multi.getDeliveries()) {
            if (!accessHelper.checkDelivery(d.getIdentifier())) {
                return new ResponseEntity<>(HttpStatus.FORBIDDEN);
            }
        }
        uiMultiService.delete(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    /**
     * Verif des champs proteges.
     *
     * @param newDelivery
     * @param oldDelivery
     * @return
     */
    private boolean hasProtectedChanges(final MultiLotsDeliveryDTO newDelivery, final MultiLotsDelivery oldDelivery) {
        // paiement
        return !StringUtils.equals(newDelivery.getPayment(), oldDelivery.getPayment().name())
               // date de réception
               || !Objects.equals(newDelivery.getReceptionDate(), oldDelivery.getReceptionDate())
               // mode de livraison
               || !StringUtils.equals(newDelivery.getMethod(), oldDelivery.getMethod().name());

    }
}
