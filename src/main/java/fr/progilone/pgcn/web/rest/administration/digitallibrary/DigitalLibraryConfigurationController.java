package fr.progilone.pgcn.web.rest.administration.digitallibrary;

import static fr.progilone.pgcn.web.rest.administration.security.AuthorizationConstants.*;

import java.util.List;
import java.util.Set;

import javax.annotation.security.RolesAllowed;
import javax.servlet.http.HttpServletRequest;

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

import fr.progilone.pgcn.domain.administration.digitallibrary.DigitalLibraryConfiguration;
import fr.progilone.pgcn.domain.dto.administration.digitallibrary.DigitalLibraryConfigurationDTO;
import fr.progilone.pgcn.domain.library.Library;
import fr.progilone.pgcn.exception.PgcnTechnicalException;
import fr.progilone.pgcn.service.administration.digitallibrary.DigitalLibraryConfigurationService;
import fr.progilone.pgcn.web.util.LibraryAccesssHelper;

@RestController
@RequestMapping(value = "/api/rest/conf_digital_library")
public class DigitalLibraryConfigurationController {

    private final DigitalLibraryConfigurationService digitalLibraryConfigurationService;
    private final LibraryAccesssHelper libraryAccesssHelper;

    public DigitalLibraryConfigurationController(final DigitalLibraryConfigurationService digitalLibraryConfigurationService,
                                                 final LibraryAccesssHelper libraryAccesssHelper) {
        this.digitalLibraryConfigurationService = digitalLibraryConfigurationService;
        this.libraryAccesssHelper = libraryAccesssHelper;
    }

    @RequestMapping(method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @RolesAllowed({CONF_DIFFUSION_DIGITAL_LIBRARY_HAB1})
    public ResponseEntity<DigitalLibraryConfiguration>
           create(final HttpServletRequest request, @RequestBody final DigitalLibraryConfiguration configuration) throws PgcnTechnicalException {
        // Vérification des droits d'accès par rapport à la bibliothèque de l'utilisateur, pour le conf à importer
        if (!libraryAccesssHelper.checkLibrary(request, configuration.getLibrary().getIdentifier())) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        // Création
        return new ResponseEntity<>(digitalLibraryConfigurationService.create(configuration), HttpStatus.CREATED);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    @Timed
    @RolesAllowed({CONF_DIFFUSION_OMEKA_HAB2})
    public ResponseEntity<?> delete(final HttpServletRequest request, @PathVariable final String id) {
        // Chargement
        final DigitalLibraryConfiguration conf = digitalLibraryConfigurationService.findOneWithDependencies(id);
        // Non trouvé
        if (conf == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        // Vérification des droits d'accès par rapport à la bibliothèque de l'utilisateur
        if (!libraryAccesssHelper.checkLibrary(request, conf.getLibrary().getIdentifier())) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        // Suppression
        digitalLibraryConfigurationService.delete(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.GET, params = {"configuration", "library"}, produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @RolesAllowed({CONF_DIFFUSION_DIGITAL_LIBRARY_HAB0})
    public ResponseEntity<Set<DigitalLibraryConfigurationDTO>> findByLibrary(final HttpServletRequest request,
                                                                             @RequestParam(value = "library") final Library library,
                                                                             @RequestParam(name = "active", required = false) final Boolean active) {
        // Vérification des droits d'accès par rapport à la bibliothèque de l'utilisateur
        if (!libraryAccesssHelper.checkLibrary(request, library)) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        // Réponse
        return new ResponseEntity<>(digitalLibraryConfigurationService.findByLibraryAndActiveDTO(library, active), HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.GET, params = {"search"}, produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @RolesAllowed({CONF_DIFFUSION_DIGITAL_LIBRARY_HAB0})
    public ResponseEntity<Page<DigitalLibraryConfigurationDTO>> search(final HttpServletRequest request,
                                                                       @RequestParam(value = "search", required = false) final String search,
                                                                       @RequestParam(value = "libraries", required = false) final List<String> libraries,
                                                                       @RequestParam(value = "page", required = false, defaultValue = "0") final Integer page,
                                                                       @RequestParam(value = "size", required = false, defaultValue = "10") final Integer size) {
        // Recherche suivant les droits de l'utilisateur
        final List<String> filteredLibraries = libraryAccesssHelper.getLibraryFilter(request, libraries);
        // Recherche
        final Page<DigitalLibraryConfigurationDTO> results = digitalLibraryConfigurationService.search(search, filteredLibraries, page, size);
        return new ResponseEntity<>(results, HttpStatus.OK);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @RolesAllowed({CONF_DIFFUSION_DIGITAL_LIBRARY_HAB0})
    public ResponseEntity<DigitalLibraryConfiguration> getById(final HttpServletRequest request, @PathVariable final String id) {
        // Chargement
        final DigitalLibraryConfiguration conf = digitalLibraryConfigurationService.findOneWithDependencies(id);
        // Non trouvé
        if (conf == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        // Vérification des droits d'accès par rapport à la bibliothèque de l'utilisateur
        if (!libraryAccesssHelper.checkLibrary(request, conf.getLibrary().getIdentifier())) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        // Réponse
        return new ResponseEntity<>(conf, HttpStatus.OK);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @RolesAllowed({CONF_DIFFUSION_DIGITAL_LIBRARY_HAB1})
    public ResponseEntity<DigitalLibraryConfiguration> udpate(final HttpServletRequest request,
                                                              @RequestBody final DigitalLibraryConfiguration digitalLibraryConfiguration) throws PgcnTechnicalException {

        // Vérification des droits d'accès par rapport à la bibliothèque de l'utilisateur, pour le conf à importer
        if (digitalLibraryConfiguration.getLibrary() == null
            || !libraryAccesssHelper.checkLibrary(request, digitalLibraryConfiguration.getLibrary().getIdentifier())) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        // Chargement du conf existant
        final DigitalLibraryConfiguration conf =
                                               digitalLibraryConfigurationService.findOneWithDependencies(digitalLibraryConfiguration.getIdentifier());
        // Non trouvé
        if (conf == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        // Vérification des droits d'accès par rapport à la bibliothèque de l'utilisateur, pour le conf existant
        if (!libraryAccesssHelper.checkLibrary(request, conf.getLibrary().getIdentifier())) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        // Mise à jour
        return new ResponseEntity<>(digitalLibraryConfigurationService.save(digitalLibraryConfiguration), HttpStatus.OK);
    }
}
