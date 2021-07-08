package fr.progilone.pgcn.web.rest.document;

import static fr.progilone.pgcn.service.es.EsConstant.*;
import static fr.progilone.pgcn.web.rest.administration.security.AuthorizationConstants.*;
import static fr.progilone.pgcn.web.rest.document.security.AuthorizationConstants.*;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.annotation.security.RolesAllowed;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.codahale.metrics.annotation.Timed;

import fr.progilone.pgcn.domain.AbstractDomainObject;
import fr.progilone.pgcn.exception.PgcnException;
import fr.progilone.pgcn.service.es.AbstractElasticsearchOperations.SearchEntity;
import fr.progilone.pgcn.service.es.EsConditionReportService;
import fr.progilone.pgcn.service.es.EsDeliveryService;
import fr.progilone.pgcn.service.es.EsDocUnitService;
import fr.progilone.pgcn.service.es.EsLotService;
import fr.progilone.pgcn.service.es.EsProjectService;
import fr.progilone.pgcn.service.es.EsTrainService;
import fr.progilone.pgcn.service.es.IndexManagerService;
import fr.progilone.pgcn.web.rest.AbstractRestController;
import fr.progilone.pgcn.web.util.LibraryAccesssHelper;

@RestController
@RequestMapping(value = "/api/rest/search")
public class SearchController extends AbstractRestController {

    private static final Logger LOG = LoggerFactory.getLogger(SearchController.class);

    private final EsDeliveryService esDeliveryService;
    private final EsDocUnitService esDocUnitService;
    private final EsConditionReportService esConditionReportService;
    private final EsLotService esLotService;
    private final EsProjectService esProjectService;
    private final EsTrainService esTrainService;
    private final IndexManagerService indexManagerService;
    private final LibraryAccesssHelper libraryAccesssHelper;

    @Autowired
    public SearchController(final EsDeliveryService esDeliveryService,
                            final EsDocUnitService esDocUnitService,
                            final EsConditionReportService esConditionReportService,
                            final EsLotService esLotService,
                            final EsProjectService esProjectService,
                            final EsTrainService esTrainService,
                            final IndexManagerService indexManagerService,
                            final LibraryAccesssHelper libraryAccesssHelper) {
        this.esDeliveryService = esDeliveryService;
        this.esConditionReportService = esConditionReportService;
        this.esLotService = esLotService;
        this.esProjectService = esProjectService;
        this.esTrainService = esTrainService;
        this.indexManagerService = indexManagerService;
        this.esDocUnitService = esDocUnitService;
        this.libraryAccesssHelper = libraryAccesssHelper;
    }

    @RequestMapping(method = RequestMethod.GET, params = {"index"})
    @ResponseStatus(HttpStatus.OK)
    @Timed
    @RolesAllowed({SUPER_ADMIN})
    public void index() throws PgcnException {
        indexManagerService.indexAsync();
    }

    /**
     * Recherche générale
     *
     * @param request
     * @param searches
     *         termes recherchés
     * @param filters
     *         filtres (post-filters), liés à la sélection d'une facette
     * @param page
     *         n° de la page
     * @param size
     *         taille de la page
     * @param searchOn
     *         type de donnée recherche
     * @param fuzzy
     *         recherche approchée / exacte
     * @param sorts
     *         tris
     * @param facet
     *         inclure les facettes dans le résultat de la recherche
     * @return
     * @throws PgcnException
     */
    @RequestMapping(method = RequestMethod.GET, params = {"search"}, produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @RolesAllowed({DOC_UNIT_HAB0})
    public ResponseEntity<Map<SearchEntity, Page<?>>> search(final HttpServletRequest request,
                                                             @RequestParam(name = "search", required = false) final String[] searches,
                                                             @RequestParam(value = "filter", required = false) final String[] filters,
                                                             @RequestParam(value = "page", required = false, defaultValue = "0") final Integer page,
                                                             @RequestParam(value = "size", required = false, defaultValue = "10") final Integer size,
                                                             @RequestParam(name = "get", required = false, defaultValue = "DOCUNIT")
                                                             final List<SearchEntity> searchOn,
                                                             @RequestParam(name = "fuzzy", required = false, defaultValue = "true")
                                                             final boolean fuzzy,
                                                             @RequestParam(name = "sort", required = false) final String[] sorts,
                                                             @RequestParam(name = "facet", required = false, defaultValue = "false")
                                                             final boolean facet) throws PgcnException {

        final List<String> libraries = libraryAccesssHelper.getLibraryFilter(request, null);

        final Map<SearchEntity, Page<?>> result = searchOn.stream().collect(Collectors.toMap(Function.identity(), searchEntity -> {
            switch (searchEntity) {
                case CONDREPORT:
                    return esConditionReportService.search(searches, filters, libraries, fuzzy, page, size, sorts, facet);
                case DELIVERY:
                    return esDeliveryService.search(searches, filters, libraries, fuzzy, page, size, sorts, facet);
                case DOCUNIT:
                    return esDocUnitService.search(searches, filters, libraries, fuzzy, page, size, sorts, facet);
                case LOT:
                    return esLotService.search(searches, filters, libraries, fuzzy, page, size, sorts, facet);
                case PROJECT:
                    return esProjectService.search(searches, filters, libraries, fuzzy, page, size, sorts, facet);
                case TRAIN:
                    return esTrainService.search(searches, filters, libraries, fuzzy, page, size, sorts, facet);
                default:
                    LOG.warn("Le type de recherche {} n'est pas géré", searchEntity);
                    return new PageImpl<AbstractDomainObject>(Collections.emptyList());
            }
        }));
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.GET, params = {"suggest"}, produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @RolesAllowed({DOC_UNIT_HAB0})
    public ResponseEntity<List<Map<String, Object>>> suggest(final HttpServletRequest request,
                                                             @RequestParam("suggest") final String suggest,
                                                             @RequestParam(value = "size", required = false, defaultValue = "10") final int size) {

        final List<String> libraries = libraryAccesssHelper.getLibraryFilter(request, Collections.singletonList(SUGGEST_CTX_GLOBAL));
        return new ResponseEntity<>(esDocUnitService.suggestDocUnits(suggest, size, libraries), HttpStatus.OK);
    }
}
