package fr.progilone.pgcn.web.rest.administration;

import com.codahale.metrics.annotation.Timed;
import fr.progilone.pgcn.domain.administration.InternetArchiveCollection;
import fr.progilone.pgcn.domain.administration.InternetArchiveConfiguration;
import fr.progilone.pgcn.domain.dto.administration.InternetArchiveConfigurationDTO;
import fr.progilone.pgcn.domain.library.Library;
import fr.progilone.pgcn.exception.PgcnTechnicalException;
import fr.progilone.pgcn.service.administration.InternetArchiveCollectionService;
import fr.progilone.pgcn.service.administration.InternetArchiveConfigurationService;
import fr.progilone.pgcn.web.rest.AbstractRestController;
import fr.progilone.pgcn.web.util.LibraryAccesssHelper;
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

import javax.annotation.security.RolesAllowed;
import javax.servlet.http.HttpServletRequest;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import static fr.progilone.pgcn.web.rest.administration.security.AuthorizationConstants.*;
import static fr.progilone.pgcn.web.rest.document.security.AuthorizationConstants.*;

/**
 * Configuration d'Internet Archive
 *
 * @author jbrunet
 * Créé le 19 avr. 2017
 */
@RestController
@RequestMapping(value = "/api/rest/conf_internet_archive")
public class InternetArchiveConfigurationController extends AbstractRestController {

    private final InternetArchiveConfigurationService iaConfigurationService;
    private final LibraryAccesssHelper libraryAccesssHelper;
    private final InternetArchiveCollectionService iaCollectionService;

    @Autowired
    public InternetArchiveConfigurationController(final InternetArchiveConfigurationService iaConfigurationService,
                                                  final LibraryAccesssHelper libraryAccesssHelper,
                                                  final InternetArchiveCollectionService iaCollectionService) {
        this.iaConfigurationService = iaConfigurationService;
        this.libraryAccesssHelper = libraryAccesssHelper;
        this.iaCollectionService = iaCollectionService;
    }

    @RequestMapping(method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @RolesAllowed({CONF_INTERNET_ARCHIVE_HAB1})
    public ResponseEntity<InternetArchiveConfiguration> create(final HttpServletRequest request,
                                                               @RequestBody final InternetArchiveConfiguration conf) throws PgcnTechnicalException {
        // Vérification des droits d'accès par rapport à la bibliothèque de l'utilisateur, pour le conf à importer
        if (!libraryAccesssHelper.checkLibrary(request, conf, InternetArchiveConfiguration::getLibrary)) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        // Création
        return new ResponseEntity<>(iaConfigurationService.save(conf), HttpStatus.CREATED);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    @Timed
    @RolesAllowed({CONF_INTERNET_ARCHIVE_HAB2})
    public ResponseEntity<?> delete(final HttpServletRequest request, @PathVariable final String id) {
        // Chargement
        final InternetArchiveConfiguration conf = iaConfigurationService.findOne(id);
        // Non trouvé
        if (conf == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        // Vérification des droits d'accès par rapport à la bibliothèque de l'utilisateur
        if (!libraryAccesssHelper.checkLibrary(request, conf, InternetArchiveConfiguration::getLibrary)) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        // Suppression
        iaConfigurationService.delete(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.GET, params = {"collections"}, produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @RolesAllowed({CONF_INTERNET_ARCHIVE_HAB0, DOC_UNIT_HAB0})
    public ResponseEntity<Collection<InternetArchiveCollection>> findIA(final HttpServletRequest request,
                                                                        @RequestParam(name = "library", required = false)
                                                                        final List<String> libraries) {
        final List<String> filteredLibraries = libraryAccesssHelper.getLibraryFilter(request, libraries);
        // Chargement des collections IA
        final Collection<InternetArchiveCollection> collections = iaCollectionService.findAll(filteredLibraries);
        // Réponse
        return new ResponseEntity<>(collections, HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @RolesAllowed({CONF_INTERNET_ARCHIVE_HAB0, DOC_UNIT_HAB0})
    public ResponseEntity<Collection<InternetArchiveConfigurationDTO>> findAll(final HttpServletRequest request,
                                                                               @RequestParam(name = "active", required = false)
                                                                               final Boolean active) {
        // Chargement des configurationSftp
        Collection<InternetArchiveConfigurationDTO> confs = iaConfigurationService.findAllDto(active);
        // Filtrage des configurationSftp par rapport à la bibliothèque de l'utilisateur, pour les non-admin
        confs = libraryAccesssHelper.filterObjectsByLibrary(request, confs, dto -> dto.getLibrary().getIdentifier());
        // Réponse
        return new ResponseEntity<>(confs, HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.GET, params = {"configurations", "library"}, produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @RolesAllowed({CONF_INTERNET_ARCHIVE_HAB0})
    public ResponseEntity<Set<InternetArchiveConfigurationDTO>> findByLibrary(final HttpServletRequest request,
                                                                              @RequestParam(value = "library") final Library library,
                                                                              @RequestParam(name = "active", required = false) final Boolean active) {
        // Vérification des droits d'accès par rapport à la bibliothèque de l'utilisateur
        if (!libraryAccesssHelper.checkLibrary(request, library)) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        // Réponse
        return new ResponseEntity<>(iaConfigurationService.findDtoByLibrary(library, active), HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.GET, params = {"search"}, produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @RolesAllowed({CONF_INTERNET_ARCHIVE_HAB0})
    public ResponseEntity<Page<InternetArchiveConfigurationDTO>> search(final HttpServletRequest request,
                                                                        @RequestParam(value = "search", required = false) final String search,
                                                                        @RequestParam(value = "libraries", required = false)
                                                                        final List<String> libraries,
                                                                        @RequestParam(value = "page", required = false, defaultValue = "0")
                                                                        final Integer page,
                                                                        @RequestParam(value = "size", required = false, defaultValue = "10")
                                                                        final Integer size) {
        // Recherche suivant les droits de l'utilisateur
        final List<String> filteredLibraries = libraryAccesssHelper.getLibraryFilter(request, libraries);
        // Recherche
        final Page<InternetArchiveConfigurationDTO> results = iaConfigurationService.search(search, filteredLibraries, page, size);
        return new ResponseEntity<>(results, HttpStatus.OK);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @RolesAllowed({CONF_INTERNET_ARCHIVE_HAB0})
    public ResponseEntity<InternetArchiveConfiguration> getById(final HttpServletRequest request, @PathVariable final String id) {
        // Chargement
        final InternetArchiveConfiguration conf = iaConfigurationService.findOne(id);
        // Non trouvé
        if (conf == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        // Vérification des droits d'accès par rapport à la bibliothèque de l'utilisateur
        if (!libraryAccesssHelper.checkLibrary(request, conf, InternetArchiveConfiguration::getLibrary)) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        // Réponse
        return new ResponseEntity<>(conf, HttpStatus.OK);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @RolesAllowed({CONF_INTERNET_ARCHIVE_HAB1})
    public ResponseEntity<InternetArchiveConfiguration> udpate(final HttpServletRequest request,
                                                               @RequestBody final InternetArchiveConfiguration conf) throws PgcnTechnicalException {

        // Vérification des droits d'accès par rapport à la bibliothèque de l'utilisateur, pour le conf à importer
        if (!libraryAccesssHelper.checkLibrary(request, conf, InternetArchiveConfiguration::getLibrary)) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        // Chargement du conf existant
        final InternetArchiveConfiguration dbConfiguration = iaConfigurationService.findOne(conf.getIdentifier());
        // Non trouvé
        if (dbConfiguration == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        // Vérification des droits d'accès par rapport à la bibliothèque de l'utilisateur, pour le conf existant
        if (!libraryAccesssHelper.checkLibrary(request, dbConfiguration, InternetArchiveConfiguration::getLibrary)) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        // Mise à jour
        return new ResponseEntity<>(iaConfigurationService.save(conf), HttpStatus.OK);
    }
}
