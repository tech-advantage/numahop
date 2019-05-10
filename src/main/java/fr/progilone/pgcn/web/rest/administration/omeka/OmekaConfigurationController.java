package fr.progilone.pgcn.web.rest.administration.omeka;

import static fr.progilone.pgcn.web.rest.administration.security.AuthorizationConstants.CONF_DIFFUSION_OMEKA_HAB0;
import static fr.progilone.pgcn.web.rest.administration.security.AuthorizationConstants.CONF_DIFFUSION_OMEKA_HAB1;
import static fr.progilone.pgcn.web.rest.administration.security.AuthorizationConstants.CONF_DIFFUSION_OMEKA_HAB2;
import static fr.progilone.pgcn.web.rest.document.security.AuthorizationConstants.DOC_UNIT_HAB0;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import javax.annotation.security.RolesAllowed;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
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

import com.codahale.metrics.annotation.Timed;

import fr.progilone.pgcn.domain.administration.omeka.OmekaConfiguration;
import fr.progilone.pgcn.domain.administration.omeka.OmekaList;
import fr.progilone.pgcn.domain.dto.administration.omeka.OmekaConfigurationDTO;
import fr.progilone.pgcn.domain.library.Library;
import fr.progilone.pgcn.exception.PgcnTechnicalException;
import fr.progilone.pgcn.service.administration.omeka.OmekaConfigurationService;
import fr.progilone.pgcn.service.administration.omeka.OmekaListService;
import fr.progilone.pgcn.service.administration.omeka.UIOmekaConfigurationService;
import fr.progilone.pgcn.web.rest.AbstractRestController;
import fr.progilone.pgcn.web.util.LibraryAccesssHelper;

@RestController
@RequestMapping(value = "/api/rest/conf_omeka")
public class OmekaConfigurationController extends AbstractRestController {

    private final UIOmekaConfigurationService uiOmekaConfigurationService;
    private final OmekaConfigurationService omekaConfigurationService;
    private final LibraryAccesssHelper libraryAccesssHelper;
    private final OmekaListService omekaListService;

    @Autowired
    public OmekaConfigurationController(final UIOmekaConfigurationService uiOmekaConfigurationService,
                                        final OmekaConfigurationService omekaConfigurationService,
                                        final LibraryAccesssHelper libraryAccesssHelper,
                                        final OmekaListService omekaListService) {
        this.uiOmekaConfigurationService = uiOmekaConfigurationService;
        this.omekaConfigurationService = omekaConfigurationService;
        this.libraryAccesssHelper = libraryAccesssHelper;
        this.omekaListService = omekaListService;
    }

    @RequestMapping(method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @RolesAllowed({CONF_DIFFUSION_OMEKA_HAB1})
    public ResponseEntity<OmekaConfigurationDTO> create(final HttpServletRequest request, @RequestBody final OmekaConfigurationDTO dto) throws
                                                                                                                                        PgcnTechnicalException {
        // Vérification des droits d'accès par rapport à la bibliothèque de l'utilisateur, pour le conf à importer
        if (!libraryAccesssHelper.checkLibrary(request, dto.getLibrary().getIdentifier())) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        // Création
        return new ResponseEntity<>(uiOmekaConfigurationService.create(dto), HttpStatus.CREATED);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    @Timed
    @RolesAllowed({CONF_DIFFUSION_OMEKA_HAB2})
    public ResponseEntity<?> delete(final HttpServletRequest request, @PathVariable final String id) {
        // Chargement
        final OmekaConfiguration conf = omekaConfigurationService.findOne(id);
        // Non trouvé
        if (conf == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        // Vérification des droits d'accès par rapport à la bibliothèque de l'utilisateur
        if (!libraryAccesssHelper.checkLibrary(request, conf, OmekaConfiguration::getLibrary)) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        // Suppression
        omekaConfigurationService.delete(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.GET, params = {"collections"}, produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @RolesAllowed({CONF_DIFFUSION_OMEKA_HAB0, DOC_UNIT_HAB0})
    public ResponseEntity<Collection<OmekaList>> findCollections(final HttpServletRequest request,
                                                           @RequestParam(name = "library", required = false) final String libraryId) {

        if (StringUtils.isNotBlank(libraryId) && !libraryAccesssHelper.checkLibrary(request, libraryId)) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        final Collection<OmekaList> lists = omekaListService.findAllByLibraryAndType(libraryId, OmekaList.ListType.COLLECTION);
        // Réponse
        return new ResponseEntity<>(lists, HttpStatus.OK);
    }
    
    @RequestMapping(method = RequestMethod.GET, params = {"items", "library"}, produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @RolesAllowed({CONF_DIFFUSION_OMEKA_HAB0, DOC_UNIT_HAB0})
    public ResponseEntity<Collection<OmekaList>> findItems(final HttpServletRequest request,
                                                                          @RequestParam(name = "library", required = false) final String libraryId) {
        
        if (StringUtils.isNotBlank(libraryId) && !libraryAccesssHelper.checkLibrary(request, libraryId)) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        // Chargement des configurations
        final Collection<OmekaList> lists = omekaListService.findAllByLibraryAndType(libraryId, OmekaList.ListType.ITEM);
        // Réponse
        return new ResponseEntity<>(lists, HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @RolesAllowed({CONF_DIFFUSION_OMEKA_HAB0, DOC_UNIT_HAB0})
    public ResponseEntity<Collection<OmekaConfigurationDTO>> findAll(final HttpServletRequest request,
                                                                     @RequestParam(name = "active", required = false) final Boolean active) {
        // Chargement des configurationSftp
        Collection<OmekaConfigurationDTO> confs = omekaConfigurationService.findAllDto(active);
        // Filtrage des configurationSftp par rapport à la bibliothèque de l'utilisateur, pour les non-admin
        confs = libraryAccesssHelper.filterObjectsByLibrary(request, confs, dto -> dto.getLibrary().getIdentifier());
        // Réponse
        return new ResponseEntity<>(confs, HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.GET, params = {"configuration", "library"}, produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @RolesAllowed({CONF_DIFFUSION_OMEKA_HAB0})
    public ResponseEntity<Set<OmekaConfigurationDTO>> findByLibrary(final HttpServletRequest request,
                                                                    @RequestParam(value = "library") final Library library,
                                                                    @RequestParam(name = "active", required = false) final Boolean active) {
        // Vérification des droits d'accès par rapport à la bibliothèque de l'utilisateur
        if (!libraryAccesssHelper.checkLibrary(request, library)) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        // Réponse
        return new ResponseEntity<>(omekaConfigurationService.findDtoByLibrary(library, active), HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.GET, params = {"search"}, produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @RolesAllowed({CONF_DIFFUSION_OMEKA_HAB0})
    public ResponseEntity<Page<OmekaConfigurationDTO>> search(final HttpServletRequest request,
                                                              @RequestParam(value = "search", required = false) final String search,
                                                              @RequestParam(value = "libraries", required = false) final List<String> libraries,
                                                              @RequestParam(value = "page", required = false, defaultValue = "0") final Integer page,
                                                              @RequestParam(value = "size", required = false, defaultValue = "10")
                                                              final Integer size) {
        // Recherche suivant les droits de l'utilisateur
        final List<String> filteredLibraries = libraryAccesssHelper.getLibraryFilter(request, libraries);
        // Recherche
        final Page<OmekaConfigurationDTO> results = omekaConfigurationService.search(search, filteredLibraries, page, size);
        return new ResponseEntity<>(results, HttpStatus.OK);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @RolesAllowed({CONF_DIFFUSION_OMEKA_HAB0})
    public ResponseEntity<OmekaConfigurationDTO> getById(final HttpServletRequest request, @PathVariable final String id) {
        // Chargement
        final OmekaConfiguration conf = omekaConfigurationService.findOne(id);
        // Non trouvé
        if (conf == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        // Vérification des droits d'accès par rapport à la bibliothèque de l'utilisateur
        if (!libraryAccesssHelper.checkLibrary(request, conf, OmekaConfiguration::getLibrary)) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        // Réponse
        return new ResponseEntity<>(uiOmekaConfigurationService.getById(id), HttpStatus.OK);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @RolesAllowed({CONF_DIFFUSION_OMEKA_HAB1})
    public ResponseEntity<OmekaConfigurationDTO> udpate(final HttpServletRequest request, @RequestBody final OmekaConfigurationDTO dto) throws
                                                                                                                                        PgcnTechnicalException {

        // Vérification des droits d'accès par rapport à la bibliothèque de l'utilisateur, pour le conf à importer
        if (dto.getLibrary() == null || !libraryAccesssHelper.checkLibrary(request, dto.getLibrary().getIdentifier())) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        // Chargement du conf existant
        final OmekaConfiguration omekaConf = omekaConfigurationService.findOne(dto.getIdentifier());
        // Non trouvé
        if (omekaConf == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        // Vérification des droits d'accès par rapport à la bibliothèque de l'utilisateur, pour le conf existant
        if (!libraryAccesssHelper.checkLibrary(request, omekaConf, OmekaConfiguration::getLibrary)) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        // Mise à jour
        return new ResponseEntity<>(uiOmekaConfigurationService.update(dto), HttpStatus.OK);
    }
}
