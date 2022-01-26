package fr.progilone.pgcn.web.rest.exchange;

import com.codahale.metrics.annotation.Timed;
import fr.progilone.pgcn.domain.dto.exchange.MappingDTO;
import fr.progilone.pgcn.domain.exchange.Mapping;
import fr.progilone.pgcn.domain.library.Library;
import fr.progilone.pgcn.exception.PgcnTechnicalException;
import fr.progilone.pgcn.service.exchange.ExchangeMappingService;
import fr.progilone.pgcn.service.exchange.MappingService;
import fr.progilone.pgcn.web.rest.AbstractRestController;
import fr.progilone.pgcn.web.util.LibraryAccesssHelper;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.security.RolesAllowed;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import static fr.progilone.pgcn.web.rest.administration.security.AuthorizationConstants.*;
import static fr.progilone.pgcn.web.rest.exchange.security.AuthorizationConstants.*;

/**
 * Contrôleur gérant les mappings
 * <p>
 * Created by Sebastien on 23/11/2016.
 */
@RestController
@RequestMapping(value = "/api/rest/mapping")
public class MappingController extends AbstractRestController {

    private static final Logger LOG = LoggerFactory.getLogger(MappingController.class);

    private final ExchangeMappingService exchangeMappingService;
    private final MappingService mappingService;
    private final LibraryAccesssHelper libraryAccesssHelper;

    @Autowired
    public MappingController(final ExchangeMappingService exchangeMappingService,
                             final MappingService mappingService,
                             final LibraryAccesssHelper libraryAccesssHelper) {
        this.exchangeMappingService = exchangeMappingService;
        this.mappingService = mappingService;
        this.libraryAccesssHelper = libraryAccesssHelper;
    }

    @RequestMapping(method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @RolesAllowed({MAP_HAB1})
    public ResponseEntity<Mapping> create(final HttpServletRequest request, @RequestBody final Mapping mapping) {
        // Vérification des droits d'accès par rapport à la bibliothèque de l'utilisateur, pour le mapping à importer
        if (!libraryAccesssHelper.checkLibrary(request, mapping, Mapping::getLibrary)) {
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
        final Mapping mapping = mappingService.findOne(id);
        // Non trouvé
        if (mapping == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        // Vérification des droits d'accès par rapport à la bibliothèque de l'utilisateur
        if (!libraryAccesssHelper.checkLibrary(request, mapping, Mapping::getLibrary)) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        // Suppression
        mappingService.delete(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @RolesAllowed({MAP_HAB0})
    public ResponseEntity<Collection<MappingDTO>> findByLibraryAndType(final HttpServletRequest request,
                                                                       @RequestParam(value = "library", required = false) Library library,
                                                                       @RequestParam(value = "type", required = false) final Mapping.Type type) {
        // Vérification des droits d'accès par rapport à la bibliothèque de l'utilisateur
        if (library != null && !libraryAccesssHelper.checkLibrary(request, library)) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        // Un des paramètres doit être renseigné
        if (library == null && type == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        // Recherche
        Collection<MappingDTO> mappingDTOS;
        // Par type
        if (library == null) {
            mappingDTOS = mappingService.findByType(type);
            // Filtrage des mappings par rapport à la bibliothèque de l'utilisateur, pour les non-admin
            mappingDTOS =
                libraryAccesssHelper.filterObjectsByLibrary(request, mappingDTOS, dto -> dto.getLibrary().getIdentifier());
        }
        // Par bibliothèque
        else if (type == null) {
            mappingDTOS = mappingService.findByLibrary(library);
        }
        // Par type + bibliothèque
        else {
            mappingDTOS = mappingService.findByTypeAndLibrary(type, library);
        }

        // Réponse
        return new ResponseEntity<>(mappingDTOS, HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.GET, params = {"usable", "library"}, produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @RolesAllowed({MAP_HAB0})
    public ResponseEntity<Set<MappingDTO>> findUsableByLibrary(final HttpServletRequest request, @RequestParam(value = "library") Library library) {
        // Vérification des droits d'accès par rapport à la bibliothèque de l'utilisateur
        if (!libraryAccesssHelper.checkLibrary(request, library)) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        // Réponse
        return new ResponseEntity<>(mappingService.findByLibrary(library), HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.GET, params = {"usable"}, produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @RolesAllowed({MAP_HAB0})
    public ResponseEntity<Collection<MappingDTO>> findAllUsable(final HttpServletRequest request) {
        // Chargement des mappings
        Collection<MappingDTO> mappingDTOS = mappingService.findAllUsable();
        // Filtrage des mappings par rapport à la bibliothèque de l'utilisateur, pour les non-admin
        mappingDTOS = libraryAccesssHelper.filterObjectsByLibrary(request, mappingDTOS, dto -> dto.getLibrary().getIdentifier());
        // Réponse
        return new ResponseEntity<>(mappingDTOS, HttpStatus.OK);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @RolesAllowed({MAP_HAB0})
    public ResponseEntity<Mapping> getById(final HttpServletRequest request, @PathVariable final String id) {
        // Chargement
        final Mapping mapping = mappingService.findOne(id);
        // Non trouvé
        if (mapping == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        // Vérification des droits d'accès par rapport à la bibliothèque de l'utilisateur
        if (!libraryAccesssHelper.checkLibrary(request, mapping, Mapping::getLibrary)) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        // Réponse
        return new ResponseEntity<>(mapping, HttpStatus.OK);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET, params = {"duplicate"}, produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @RolesAllowed({MAP_HAB1})
    public ResponseEntity<Mapping> duplicate(final HttpServletRequest request,
                                             @PathVariable final String id,
                                             @RequestParam(name = "library", required = false) final String library) {
        // Chargement du mapping existant
        final Mapping mapping = mappingService.findOne(id);
        // Non trouvé
        if (mapping == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        // Vérification des droits d'accès par rapport à la bibliothèque de l'utilisateur, pour le mapping existant
        if (library == null ?
            // la bib n'est pas renseignée: accès en lecture + écriture à la bib du mapping
            !libraryAccesssHelper.checkLibrary(request, mapping, Mapping::getLibrary) :
            // la bib est pas renseignée: accès en lecture à la bib du mapping + accès en écriture à la bib renseignée
            !libraryAccesssHelper.checkLibrary(request, mapping, Mapping::getLibrary) || !libraryAccesssHelper.checkLibrary(
                request,
                library)) {

            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        return new ResponseEntity<>(mappingService.duplicateMapping(id, library), HttpStatus.OK);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @RolesAllowed({MAP_HAB1})
    public ResponseEntity<Mapping> update(final HttpServletRequest request, @RequestBody final Mapping mapping) {
        // Vérification des droits d'accès par rapport à la bibliothèque de l'utilisateur, pour le mapping à importer
        if (!libraryAccesssHelper.checkLibrary(request, mapping, Mapping::getLibrary)) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        // Chargement du mapping existant
        final Mapping dbMapping = mappingService.findOne(mapping.getIdentifier());
        // Non trouvé
        if (dbMapping == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        // Vérification des droits d'accès par rapport à la bibliothèque de l'utilisateur, pour le mapping existant
        if (!libraryAccesssHelper.checkLibrary(request, dbMapping, Mapping::getLibrary)) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        // Mise à jour
        return new ResponseEntity<>(mappingService.save(mapping), HttpStatus.OK);
    }

    /**
     * Export du mapping #id
     *
     * @param request
     * @param response
     * @param id
     * @throws PgcnTechnicalException
     * @throws IOException
     */
    @RequestMapping(value = "/{id}", params = {"export"}, method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @RolesAllowed({MAP_HAB0})
    public void exportMapping(final HttpServletRequest request, final HttpServletResponse response, @PathVariable final String id) throws
                                                                                                                                   PgcnTechnicalException,
                                                                                                                                   IOException {
        // Chargement
        final Mapping mapping = mappingService.findOne(id);
        // Non trouvé
        if (mapping == null) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return;
        }
        // Vérification des droits d'accès par rapport à la bibliothèque de l'utilisateur
        if (!libraryAccesssHelper.checkLibrary(request, mapping, Mapping::getLibrary)) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            return;
        }

        writeResponseHeaderForDownload(response, MediaType.APPLICATION_JSON_VALUE, null, mapping.getLabel().replaceAll("\\W+", "_") + ".json");
        final String mappingJson = exchangeMappingService.exportMapping(id);
        IOUtils.write(mappingJson, response.getOutputStream(), StandardCharsets.UTF_8);
    }

    /**
     * Import (écrasement) du mapping #id
     *
     * @param request
     * @param id
     * @param files
     * @return
     * @throws PgcnTechnicalException
     */
    @RequestMapping(value = "/{id}", params = {"import"}, method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @RolesAllowed({MAP_HAB1})
    public ResponseEntity<Mapping> importMapping(final HttpServletRequest request,
                                                 @PathVariable final String id,
                                                 @RequestParam(name = "file") final List<MultipartFile> files) throws PgcnTechnicalException {
        // Chargement du mapping existant
        final Mapping dbMapping = mappingService.findOne(id);
        // Non trouvé
        if (dbMapping == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        // Vérification des droits d'accès par rapport à la bibliothèque de l'utilisateur, pour le mapping existant
        if (!libraryAccesssHelper.checkLibrary(request, dbMapping, Mapping::getLibrary)) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        // Pas de fichier transmis
        if (files.isEmpty()) {
            LOG.warn("Aucun fichier n'a été reçu pour le mise à jour du mapping {}", id);
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        final MultipartFile file = files.get(0);
        // Fichier vide
        if (file == null || file.getSize() == 0) {
            LOG.warn("Le fichier reçu pour le mise à jour du mapping {} est vide", id);
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        final Mapping mapping = exchangeMappingService.importMapping(file, id);
        return new ResponseEntity<>(mapping, HttpStatus.OK);
    }

    /**
     * Import (création) du mapping
     *
     * @param request
     * @param library
     * @param files
     * @return
     * @throws PgcnTechnicalException
     */
    @RequestMapping(params = {"import", "library"}, method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @RolesAllowed({MAP_HAB1})
    public ResponseEntity<Mapping> importNewMapping(final HttpServletRequest request,
                                                    @RequestParam(name = "library") final String library,
                                                    @RequestParam(name = "file") final List<MultipartFile> files) throws PgcnTechnicalException {
        // Vérification des droits d'accès par rapport à la bibliothèque de l'utilisateur, pour le mapping à importer
        if (!libraryAccesssHelper.checkLibrary(request, library)) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        // Pas de fichier transmis
        if (files.isEmpty()) {
            LOG.warn("Aucun fichier n'a été reçu pour la création de mapping (bibliothèque {})", library);
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        final MultipartFile file = files.get(0);
        // Fichier vide
        if (file == null || file.getSize() == 0) {
            LOG.warn("Le fichier reçu pour la création de mapping (bibliothèque {}) est vide", library);
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        final Mapping mapping = exchangeMappingService.importNewMapping(file, library);
        return new ResponseEntity<>(mapping, HttpStatus.OK);
    }
}
