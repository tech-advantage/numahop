package fr.progilone.pgcn.web.rest.exchange;

import com.codahale.metrics.annotation.Timed;
import fr.progilone.pgcn.domain.dto.exchange.CSVMappingDTO;
import fr.progilone.pgcn.domain.exchange.CSVMapping;
import fr.progilone.pgcn.domain.library.Library;
import fr.progilone.pgcn.service.exchange.CSVMappingService;
import fr.progilone.pgcn.web.rest.AbstractRestController;
import fr.progilone.pgcn.web.util.LibraryAccesssHelper;
import org.springframework.beans.factory.annotation.Autowired;
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
import java.util.Set;

import static fr.progilone.pgcn.web.rest.administration.security.AuthorizationConstants.*;
import static fr.progilone.pgcn.web.rest.exchange.security.AuthorizationConstants.*;

/**
 * Contrôleur gérant les mappings
 * <p>
 * Created by Sebastien on 23/11/2016.
 */
@RestController
@RequestMapping(value = "/api/rest/csvmapping")
public class CSVMappingController extends AbstractRestController {

    private final CSVMappingService mappingService;
    private final LibraryAccesssHelper libraryAccesssHelper;

    @Autowired
    public CSVMappingController(final CSVMappingService mappingService, final LibraryAccesssHelper libraryAccesssHelper) {
        this.mappingService = mappingService;
        this.libraryAccesssHelper = libraryAccesssHelper;
    }

    @RequestMapping(method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @RolesAllowed({MAP_HAB1})
    public ResponseEntity<CSVMapping> create(final HttpServletRequest request, @RequestBody final CSVMapping mapping) {
        // Vérification des droits d'accès par rapport à la bibliothèque de l'utilisateur, pour le mapping à importer
        if (!libraryAccesssHelper.checkLibrary(request, mapping, CSVMapping::getLibrary)) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        // Création
        return new ResponseEntity<>(mappingService.save(mapping), HttpStatus.CREATED);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    @Timed
    @RolesAllowed({MAP_HAB2})
    public ResponseEntity<?> delete(final HttpServletRequest request, @PathVariable final String id) {
        // Chargement
        final CSVMapping mapping = mappingService.findOne(id);
        // Non trouvé
        if (mapping == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        // Vérification des droits d'accès par rapport à la bibliothèque de l'utilisateur
        if (!libraryAccesssHelper.checkLibrary(request, mapping, CSVMapping::getLibrary)) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        // Suppression
        mappingService.delete(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @RolesAllowed({MAP_HAB0})
    public ResponseEntity<Collection<CSVMappingDTO>> findAll(final HttpServletRequest request) {
        // Chargement des mappings
        Collection<CSVMappingDTO> mappingDTOS = mappingService.findAll();
        // Filtrage des mappings par rapport à la bibliothèque de l'utilisateur, pour les non-admin
        mappingDTOS = libraryAccesssHelper.filterObjectsByLibrary(request, mappingDTOS, dto -> dto.getLibrary().getIdentifier(), ADMINISTRATION_LIB);
        // Réponse
        return new ResponseEntity<>(mappingDTOS, HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.GET, params = {"library"}, produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @RolesAllowed({MAP_HAB0})
    public ResponseEntity<Set<CSVMappingDTO>> findByLibrary(final HttpServletRequest request, @RequestParam(value = "library") Library library) {
        // Vérification des droits d'accès par rapport à la bibliothèque de l'utilisateur
        if (!libraryAccesssHelper.checkLibrary(request, library, ADMINISTRATION_LIB)) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        // Réponse
        return new ResponseEntity<>(mappingService.findByLibrary(library), HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.GET, params = {"usable", "library"}, produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @RolesAllowed({MAP_HAB0})
    public ResponseEntity<Set<CSVMappingDTO>> findUsableByLibrary(final HttpServletRequest request,
                                                                  @RequestParam(value = "library") Library library) {
        // Vérification des droits d'accès par rapport à la bibliothèque de l'utilisateur
        if (!libraryAccesssHelper.checkLibrary(request, library, ADMINISTRATION_LIB)) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        // Réponse
        return new ResponseEntity<>(mappingService.findByLibrary(library), HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.GET, params = {"usable"}, produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @RolesAllowed({MAP_HAB0})
    public ResponseEntity<Collection<CSVMappingDTO>> findAllUsable(final HttpServletRequest request) {
        // Chargement des mappings
        Collection<CSVMappingDTO> mappingDTOS = mappingService.findAllUsable();
        // Filtrage des mappings par rapport à la bibliothèque de l'utilisateur, pour les non-admin
        mappingDTOS = libraryAccesssHelper.filterObjectsByLibrary(request, mappingDTOS, dto -> dto.getLibrary().getIdentifier(), ADMINISTRATION_LIB);
        // Réponse
        return new ResponseEntity<>(mappingDTOS, HttpStatus.OK);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @RolesAllowed({MAP_HAB0})
    public ResponseEntity<CSVMapping> getById(final HttpServletRequest request, @PathVariable final String id) {
        // Chargement
        final CSVMapping mapping = mappingService.findOne(id);
        // Non trouvé
        if (mapping == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        // Vérification des droits d'accès par rapport à la bibliothèque de l'utilisateur
        if (!libraryAccesssHelper.checkLibrary(request, mapping, CSVMapping::getLibrary, ADMINISTRATION_LIB)) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        // Réponse
        return new ResponseEntity<>(mapping, HttpStatus.OK);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET, params = {"duplicate"}, produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @RolesAllowed({MAP_HAB1})
    public ResponseEntity<CSVMapping> duplicateMapping(final HttpServletRequest request,
                                                       @PathVariable final String id,
                                                       @RequestParam(name = "library", required = false) final String library) {
        // Chargement du mapping existant
        final CSVMapping mapping = mappingService.findOne(id);
        // Non trouvé
        if (mapping == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        // Vérification des droits d'accès par rapport à la bibliothèque de l'utilisateur, pour le mapping existant
        if (library == null ?
            // la bib n'est pas renseignée: accès en lecture + écriture à la bib du mapping
            !libraryAccesssHelper.checkLibrary(request, mapping, CSVMapping::getLibrary) :
            // la bib est pas renseignée: accès en lecture à la bib du mapping + accès en écriture à la bib renseignée
            !libraryAccesssHelper.checkLibrary(request, mapping, CSVMapping::getLibrary, ADMINISTRATION_LIB) || !libraryAccesssHelper.checkLibrary(
                request,
                library)) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        return new ResponseEntity<>(mappingService.duplicateMapping(id, library), HttpStatus.OK);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @RolesAllowed({MAP_HAB1})
    public ResponseEntity<CSVMapping> udpate(final HttpServletRequest request, @RequestBody final CSVMapping mapping) {
        // Vérification des droits d'accès par rapport à la bibliothèque de l'utilisateur, pour le mapping à importer
        if (!libraryAccesssHelper.checkLibrary(request, mapping, CSVMapping::getLibrary)) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        // Chargement du mapping existant
        final CSVMapping dbMapping = mappingService.findOne(mapping.getIdentifier());
        // Non trouvé
        if (dbMapping == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        // Vérification des droits d'accès par rapport à la bibliothèque de l'utilisateur, pour le mapping existant
        if (!libraryAccesssHelper.checkLibrary(request, dbMapping, CSVMapping::getLibrary)) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        // Mise à jour
        return new ResponseEntity<>(mappingService.save(mapping), HttpStatus.OK);
    }
}
