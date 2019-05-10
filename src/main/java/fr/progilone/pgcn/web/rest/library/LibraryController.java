package fr.progilone.pgcn.web.rest.library;

import com.codahale.metrics.annotation.Timed;
import fr.progilone.pgcn.domain.dto.library.LibraryDTO;
import fr.progilone.pgcn.domain.dto.library.SimpleLibraryDTO;
import fr.progilone.pgcn.domain.dto.user.SimpleUserDTO;
import fr.progilone.pgcn.domain.library.Library;
import fr.progilone.pgcn.domain.util.CustomUserDetails;
import fr.progilone.pgcn.exception.PgcnException;
import fr.progilone.pgcn.exception.PgcnTechnicalException;
import fr.progilone.pgcn.security.SecurityUtils;
import fr.progilone.pgcn.service.library.LibraryService;
import fr.progilone.pgcn.service.library.ui.UILibraryService;
import fr.progilone.pgcn.service.user.ui.UIUserService;
import fr.progilone.pgcn.web.rest.AbstractRestController;
import fr.progilone.pgcn.web.util.LibraryAccesssHelper;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.security.RolesAllowed;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static fr.progilone.pgcn.domain.user.User.Category.*;
import static fr.progilone.pgcn.web.rest.library.security.AuthorizationConstants.*;

@RestController
@RequestMapping(value = "/api/rest/library")
public class LibraryController extends AbstractRestController {

    private static final Logger LOG = LoggerFactory.getLogger(LibraryController.class);
    private static final String DOWNLOAD_LOGO_PNG = "logo.png";
    private static final String DOWNLOAD_THUMBNAIL_PNG = "thumbnail.png";
    private static final String DEFAULT_THUMBNAIL = "images/file.png";

    private final LibraryService libraryService;
    private final UILibraryService uiLibraryService;
    private final UIUserService uiUserService;
    private final LibraryAccesssHelper libraryAccesssHelper;

    @Autowired
    public LibraryController(final LibraryService libraryService,
                             final UILibraryService uiLibraryService,
                             final UIUserService uiUserService,
                             final LibraryAccesssHelper libraryAccesssHelper) {
        super();
        this.libraryService = libraryService;
        this.uiLibraryService = uiLibraryService;
        this.uiUserService = uiUserService;
        this.libraryAccesssHelper = libraryAccesssHelper;
    }

    @RequestMapping(method = RequestMethod.POST)
    @Timed
    @RolesAllowed({LIB_HAB1})
    public ResponseEntity<LibraryDTO> create(final HttpServletRequest request, @RequestBody final LibraryDTO library) throws PgcnException {
        // TODO restrict to superadmin
        final LibraryDTO savedLibrary = uiLibraryService.create(library);
        return new ResponseEntity<>(savedLibrary, HttpStatus.CREATED);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    @Timed
    @RolesAllowed({LIB_HAB3})
    public ResponseEntity<Library> delete(final HttpServletRequest request, @PathVariable final String id) {
        if (!libraryAccesssHelper.checkLibrary(request, id)) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        libraryService.delete(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET, params = {"dto"}, produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @RolesAllowed({LIB_HAB5, LIB_HAB6, LIB_HAB7})
    public ResponseEntity<LibraryDTO> getDtoById(final HttpServletRequest request, @PathVariable final String id) {
        if (!libraryAccesssHelper.checkLibrary(request, id)) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        final LibraryDTO library = uiLibraryService.getOneDTO(id);
        return createResponseEntity(library);
    }

    @RequestMapping(method = RequestMethod.GET, params = {"dto"}, produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Collection<SimpleLibraryDTO>> findAllDTO(final HttpServletRequest request) {
        Collection<SimpleLibraryDTO> libraries = uiLibraryService.findAllActiveDTO();
        libraries = libraryAccesssHelper.filterObjectsByLibrary(request, libraries, SimpleLibraryDTO::getIdentifier);
        return createResponseEntity(libraries);
    }

    @RequestMapping(method = RequestMethod.GET, params = {"search"}, produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @RolesAllowed({LIB_HAB5, LIB_HAB6, LIB_HAB7})
    public ResponseEntity<Page<SimpleLibraryDTO>> search(final HttpServletRequest request,
                                                         @RequestParam(value = "search", required = false) final String search,
                                                         @RequestParam(value = "initiale", required = false) final String initiale,
                                                         @RequestParam(value = "institutions", required = false) final List<String> institutions,
                                                         @RequestParam(value = "isActive", required = false, defaultValue = "true")
                                                         final boolean isActive,
                                                         @RequestParam(value = "page", required = false, defaultValue = "0") final Integer page,
                                                         @RequestParam(value = "size", required = false, defaultValue = "10") final Integer size) {
        final List<String> libraries = libraryAccesssHelper.getLibraryFilter(request, null);
        return new ResponseEntity<>(uiLibraryService.search(search, initiale, institutions, libraries, isActive, page, size), HttpStatus.OK);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @RolesAllowed({LIB_HAB5, LIB_HAB6, LIB_HAB7})
    public ResponseEntity<LibraryDTO> getById(final HttpServletRequest request, @PathVariable final String id) {
        if (!libraryAccesssHelper.checkLibrary(request, id)) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        final LibraryDTO library = uiLibraryService.getOneDTO(id);
        return createResponseEntity(library);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE, params = {"providers"})
    @Timed
    @RolesAllowed({LIB_HAB5, LIB_HAB6, LIB_HAB7})
    public ResponseEntity<Collection<SimpleUserDTO>> getProviders(final HttpServletRequest request, @PathVariable final String id) {
        if (!libraryAccesssHelper.checkLibrary(request, id)) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        final CustomUserDetails currentUser = SecurityUtils.getCurrentUser();
        // Un prestataire ne peut pas voir les autres prestataires
        if (currentUser != null && Objects.equals(currentUser.getCategory(), PROVIDER)) {
            final SimpleUserDTO userDto = uiUserService.getCurrentUserDTO();
            return createResponseEntity(Collections.singletonList(userDto));
        } else {
            final Collection<SimpleUserDTO> users = uiLibraryService.findProviders(id);
            return createResponseEntity(users);
        }
    }
    
    @RequestMapping(value = "/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE, params = {"users"})
    @Timed
    @RolesAllowed({LIB_HAB5, LIB_HAB6, LIB_HAB7})
    public ResponseEntity<Collection<SimpleUserDTO>> getUsers(final HttpServletRequest request, @PathVariable final String id) {
        if (!libraryAccesssHelper.checkLibrary(request, id)) {
             return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        final Collection<SimpleUserDTO> users = uiLibraryService.findUsers(id);
        return createResponseEntity(users);
    }


    @RequestMapping(value = "/{id}", method = RequestMethod.POST)
    @Timed
    @RolesAllowed({LIB_HAB2})
    public ResponseEntity<LibraryDTO> update(final HttpServletRequest request, @RequestBody final LibraryDTO library) throws PgcnException {
        if (!libraryAccesssHelper.checkLibrary(request, library.getIdentifier())) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        final LibraryDTO savedLibrary = uiLibraryService.update(library);
        return new ResponseEntity<>(savedLibrary, HttpStatus.OK);
    }

    /**
     * Téléchargement d'un logo
     *
     * @param request
     * @param response
     * @param libraryId
     * @return
     * @throws PgcnTechnicalException
     */
    @RequestMapping(value = "/{id}", method = RequestMethod.GET, params = {"logo"}, produces = MediaType.IMAGE_PNG_VALUE)
    @Timed
    @RolesAllowed({LIB_HAB5, LIB_HAB6, LIB_HAB7})
    public void downloadLogo(final HttpServletRequest request, final HttpServletResponse response, @PathVariable("id") final String libraryId) throws
                                                                                                                                               PgcnTechnicalException {
        final Library library = libraryService.findOne(libraryId);
        if (library == null) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return;
        }
        if (!libraryAccesssHelper.checkLibrary(request, libraryId)) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            return;
        }
        // Réponse
        final File importFile = libraryService.getLibraryLogo(library);
        // Non trouvé
        if (importFile == null) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return;
        } else {
            writeResponseForDownload(response, importFile, MediaType.IMAGE_PNG_VALUE, DOWNLOAD_LOGO_PNG);
        }
        response.setStatus(HttpServletResponse.SC_OK);
    }

    /**
     * Téléchargement d'un logo
     *
     * @param request
     * @param response
     * @param libraryId
     * @return
     * @throws PgcnTechnicalException
     */
    @RequestMapping(value = "/{id}", method = RequestMethod.GET, params = {"thumbnail"}, produces = MediaType.IMAGE_PNG_VALUE)
    @Timed
    @RolesAllowed({LIB_HAB5, LIB_HAB6, LIB_HAB7})
    public void downloadThumbnail(final HttpServletRequest request,
                                  final HttpServletResponse response,
                                  @PathVariable("id") final String libraryId) throws PgcnTechnicalException {
        final Library library = libraryService.findOne(libraryId);
        if (library == null) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return;
        }
        if (!libraryAccesssHelper.checkLibrary(request, libraryId)) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            return;
        }
        // Réponse
        final File importFile = libraryService.getLibraryThumbnail(library);
        // Non trouvé
        if (importFile == null) {
            writeResponseHeaderForDownload(response, MediaType.IMAGE_PNG_VALUE, null, DOWNLOAD_THUMBNAIL_PNG);
            try (final InputStream defaultStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(DEFAULT_THUMBNAIL)) {
                IOUtils.copy(defaultStream, response.getOutputStream());
            } catch (IOException e) {
                LOG.error(e.getMessage(), e);
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                return;
            }
        } else {
            writeResponseForDownload(response, importFile, MediaType.IMAGE_PNG_VALUE, DOWNLOAD_THUMBNAIL_PNG);
        }
        response.setStatus(HttpServletResponse.SC_OK);
    }

    /**
     * Téléchargement d'un logo
     *
     * @param request
     * @param libraryId
     * @return
     * @throws PgcnTechnicalException
     */
    @RequestMapping(value = "/{id}", method = RequestMethod.GET, params = {"logoexists"}, produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @RolesAllowed({LIB_HAB5, LIB_HAB6, LIB_HAB7})
    public ResponseEntity<Map<?, ?>> hasLogo(final HttpServletRequest request, @PathVariable("id") final String libraryId) throws
                                                                                                                           PgcnTechnicalException {
        final Library library = libraryService.findOne(libraryId);
        if (library == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        if (!libraryAccesssHelper.checkLibrary(request, libraryId)) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        // Réponse
        final Map<String, Boolean> result = new HashMap<>();
        final File importFile = libraryService.getLibraryLogo(library);
        result.put(libraryId, importFile != null && importFile.exists());
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    /**
     * Suppression du logo
     *
     * @param request
     * @param libraryId
     * @return
     */
    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE, params = {"logo"}, produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @RolesAllowed(LIB_HAB2)
    public void deleteLogo(final HttpServletRequest request, final HttpServletResponse response, @PathVariable("id") final String libraryId) {
        final Library library = libraryService.findOne(libraryId);
        if (library == null) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return;
        }
        if (!libraryAccesssHelper.checkLibrary(request, libraryId)) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            return;
        }
        libraryService.deleteLogo(library);
        response.setStatus(HttpServletResponse.SC_OK);
    }

    /**
     * Téléversement d'une liste de fichiers
     *
     * @param request
     * @param libraryId
     * @param files
     * @return
     */
    @RequestMapping(value = "/{id}", method = RequestMethod.POST, params = {"logo"}, produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @RolesAllowed(LIB_HAB2)
    public void uploadLogo(final HttpServletRequest request,
                           final HttpServletResponse response,
                           @PathVariable("id") final String libraryId,
                           @RequestParam(name = "file") final List<MultipartFile> files) {
        final Library library = libraryService.findOne(libraryId);
        if (library == null) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return;
        }
        if (!libraryAccesssHelper.checkLibrary(request, libraryId)) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            return;
        }
        if (files.isEmpty()) {
            LOG.warn("Aucun fichier n'a été reçu pour le mise à jour du logo de la bibliothèque {}", libraryId);
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        } else {
            libraryService.uploadImage(library, files.get(0));
            response.setStatus(HttpServletResponse.SC_OK);
        }
    }
}
