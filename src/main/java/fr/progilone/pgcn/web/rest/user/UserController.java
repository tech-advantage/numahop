package fr.progilone.pgcn.web.rest.user;

import static fr.progilone.pgcn.domain.user.User.Category.*;
import static fr.progilone.pgcn.web.rest.library.security.AuthorizationConstants.*;
import static fr.progilone.pgcn.web.rest.user.security.AuthorizationConstants.*;

import com.codahale.metrics.annotation.Timed;
import fr.progilone.pgcn.domain.dto.user.SimpleUserDTO;
import fr.progilone.pgcn.domain.dto.user.UserCreationDTO;
import fr.progilone.pgcn.domain.dto.user.UserDTO;
import fr.progilone.pgcn.domain.user.User;
import fr.progilone.pgcn.domain.util.CustomUserDetails;
import fr.progilone.pgcn.exception.PgcnException;
import fr.progilone.pgcn.exception.PgcnTechnicalException;
import fr.progilone.pgcn.security.SecurityUtils;
import fr.progilone.pgcn.service.user.UserService;
import fr.progilone.pgcn.service.user.ui.UIUserService;
import fr.progilone.pgcn.web.rest.AbstractRestController;
import fr.progilone.pgcn.web.util.AccessHelper;
import fr.progilone.pgcn.web.util.LibraryAccesssHelper;
import jakarta.annotation.security.RolesAllowed;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
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
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping(value = "/api/rest/user")
public class UserController extends AbstractRestController {

    private static final Logger LOG = LoggerFactory.getLogger(UserController.class);

    private static final String DOWNLOAD_SIGN_PNG = "signature.png";
    private static final String DOWNLOAD_THUMBNAIL_PNG = "thumbnail.png";

    private final AccessHelper accessHelper;
    private final LibraryAccesssHelper libraryAccesssHelper;
    private final UserService userService;
    private final UIUserService uiUserService;

    @Autowired
    public UserController(final UserService userService, final UIUserService uiUserService, final AccessHelper accessHelper, final LibraryAccesssHelper libraryAccesssHelper) {
        this.userService = userService;
        this.uiUserService = uiUserService;
        this.accessHelper = accessHelper;
        this.libraryAccesssHelper = libraryAccesssHelper;
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.POST, params = {"change_password"}, produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @RolesAllowed({USER_HAB2})
    public ResponseEntity<Map<String, Object>> changePassword(@PathVariable final String id) {
        // Droits d'accès
        if (!accessHelper.checkUser(id)) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        final String password = userService.changeUserPassword(id);
        final Map<String, Object> response = new HashMap<>();
        response.put("password", password);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @RolesAllowed({USER_HAB1})
    public ResponseEntity<UserDTO> create(final HttpServletRequest request, @RequestBody final UserCreationDTO user) throws PgcnException {
        // Droit d'accès à la bibliothèque
        if (!libraryAccesssHelper.checkLibrary(request, user.getLibrary().getIdentifier())) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        final UserDTO savedUser = uiUserService.create(user);
        return new ResponseEntity<>(savedUser, HttpStatus.CREATED);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @RolesAllowed({USER_HAB3})
    public ResponseEntity<UserDTO> delete(@PathVariable final String id) {
        // Droit d'accès
        if (!accessHelper.checkUser(id)) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        userService.delete(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.GET, params = {"search"}, produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @RolesAllowed({USER_HAB0})
    public ResponseEntity<Page<SimpleUserDTO>> search(final HttpServletRequest request,
                                                      @RequestParam(value = "search", required = false) final String search,
                                                      @RequestParam(value = "initiale", required = false) final String initiale,
                                                      @RequestParam(value = "active", required = false, defaultValue = "true") final boolean active,
                                                      @RequestParam(value = "libraries", required = false) final List<String> libraries,
                                                      @RequestParam(value = "roles", required = false) final List<String> roles,
                                                      @RequestParam(value = "categories", required = false) final List<User.Category> categories,
                                                      @RequestParam(value = "page", required = false, defaultValue = "0") final Integer page,
                                                      @RequestParam(value = "size", required = false, defaultValue = "10") final Integer size) {
        // Droits d'accès
        final List<String> filteredLibraries = libraryAccesssHelper.getLibraryFilter(request, libraries);
        // Restriction pour les prestataires
        final CustomUserDetails currentUser = SecurityUtils.getCurrentUser();
        final boolean filterProviders = currentUser == null || Objects.equals(currentUser.getCategory(), User.Category.PROVIDER);

        return new ResponseEntity<>(uiUserService.search(search, initiale, active, filterProviders, filteredLibraries, categories, roles, page, size), HttpStatus.OK);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<UserDTO> getById(final HttpServletRequest request, @PathVariable final String id) {
        // L'utilisateur a les droits USER_HAB0, ou souhaite consulter son propre profil
        if (!request.isUserInRole(USER_HAB0)) {
            final CustomUserDetails currentUser = SecurityUtils.getCurrentUser();

            if (currentUser == null || !StringUtils.equals(currentUser.getIdentifier(), id)) {
                return new ResponseEntity<>(HttpStatus.FORBIDDEN);
            }
        }
        if (!accessHelper.checkUser(id)) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        final UserDTO user = uiUserService.getOne(id);
        return createResponseEntity(user);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET, params = {"dto"}, produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @RolesAllowed({USER_HAB0})
    public ResponseEntity<UserDTO> getDtoById(@PathVariable final String id) {
        // Droits d'accès
        if (!accessHelper.checkUser(id)) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        final UserDTO user = uiUserService.getOne(id);
        return createResponseEntity(user);
    }

    @RequestMapping(method = RequestMethod.GET, params = {"dto"}, produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @RolesAllowed({LIB_HAB5,
                   LIB_HAB6,
                   LIB_HAB7})
    public ResponseEntity<Collection<SimpleUserDTO>> findAllDTO(final HttpServletRequest request) {
        Collection<SimpleUserDTO> users = uiUserService.findAllActiveDTO();
        // Droits d'accès
        users = libraryAccesssHelper.filterObjectsByLibrary(request, users, dto -> dto.getLibrary().getIdentifier());
        return createResponseEntity(users);
    }

    @RequestMapping(method = RequestMethod.GET, params = {"providers"}, produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @RolesAllowed({LIB_HAB5,
                   LIB_HAB6,
                   LIB_HAB7})
    public ResponseEntity<Collection<SimpleUserDTO>> findProvidersDTO() {
        final CustomUserDetails currentUser = SecurityUtils.getCurrentUser();
        // Un prestataire ne peut pas voir les autres prestataires
        if (currentUser != null && Objects.equals(currentUser.getCategory(), PROVIDER)) {
            final SimpleUserDTO userDto = uiUserService.getCurrentUserDTO();
            return createResponseEntity(Collections.singletonList(userDto));
        } else {
            final Collection<SimpleUserDTO> users = uiUserService.findAllProvidersDTO();
            return createResponseEntity(users);
        }
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @RolesAllowed({USER_HAB2,
                   USER_HAB6})
    public ResponseEntity<UserDTO> update(final HttpServletRequest request, @RequestBody final UserDTO user) throws PgcnException {
        // Droits d'accès: autres profils
        if (request.isUserInRole(USER_HAB2)) {
            if (!accessHelper.checkUser(user.getIdentifier())) {
                return new ResponseEntity<>(HttpStatus.FORBIDDEN);
            }
        }
        // Droits d'accès: son propre profil
        else if (request.isUserInRole(USER_HAB6)) {
            final CustomUserDetails currentUser = SecurityUtils.getCurrentUser();
            // changement d'un autre user interdit
            if (currentUser == null || !StringUtils.equals(currentUser.getIdentifier(), user.getIdentifier())) {
                return new ResponseEntity<>(HttpStatus.FORBIDDEN);
            }

            final User dbUser = userService.getOne(user.getIdentifier());
            // changement de profil interdit
            if (!StringUtils.equals(user.getRole().getIdentifier(), dbUser.getRole().getIdentifier())) {
                return new ResponseEntity<>(HttpStatus.FORBIDDEN);
            }
        }
        final UserDTO savedUser = uiUserService.update(user);
        return new ResponseEntity<>(savedUser, HttpStatus.OK);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET, params = {"duplicate"}, produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @RolesAllowed({USER_HAB0})
    public ResponseEntity<UserDTO> duplicateBorrower(@PathVariable final String id) {
        // Droits d'accès
        if (!accessHelper.checkUser(id)) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        return new ResponseEntity<>(uiUserService.duplicateUser(id), HttpStatus.OK);
    }

    @RequestMapping(value = "/dashboard", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @ResponseStatus(HttpStatus.OK)
    public void saveDashboard(final HttpServletRequest request) throws IOException {
        final String dashboard = IOUtils.toString(request.getInputStream(), StandardCharsets.UTF_8);
        userService.updateDashboard(SecurityUtils.getCurrentUserId(), dashboard);
    }

    /**
     * Téléchargement d'un logo
     *
     * @param response
     * @param userId
     * @return
     * @throws PgcnTechnicalException
     */
    @RequestMapping(value = "/{id}", method = RequestMethod.GET, params = {"signature"}, produces = MediaType.IMAGE_PNG_VALUE)
    @Timed
    @RolesAllowed({USER_HAB0})
    public void downloadSignature(final HttpServletResponse response, @PathVariable("id") final String userId) throws PgcnTechnicalException {
        final User user = userService.getOne(userId);
        if (user == null) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return;
        }
        // Droits d'accès
        if (!accessHelper.checkUser(userId)) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            return;
        }
        // Réponse
        final File importFile = userService.getUserSignature(user);
        // Non trouvé
        if (importFile == null) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return;
        } else {
            writeResponseForDownload(response, importFile, MediaType.IMAGE_PNG_VALUE, DOWNLOAD_SIGN_PNG);
        }
        response.setStatus(HttpServletResponse.SC_OK);
    }

    /**
     * Téléchargement d'un logo
     *
     * @param response
     * @param userId
     * @return
     * @throws PgcnTechnicalException
     */
    @RequestMapping(value = "/{id}", method = RequestMethod.GET, params = {"thumbnail"}, produces = MediaType.IMAGE_PNG_VALUE)
    @Timed
    @RolesAllowed({USER_HAB0})
    public void downloadThumbnail(final HttpServletResponse response, @PathVariable("id") final String userId) throws PgcnTechnicalException {
        final User user = userService.getOne(userId);
        if (user == null) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return;
        }
        // Droits d'accès
        if (!accessHelper.checkUser(userId)) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            return;
        }
        // Réponse
        final File importFile = userService.getUserThumbnail(user);
        // Non trouvé
        if (importFile == null) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return;
        } else {
            writeResponseForDownload(response, importFile, MediaType.IMAGE_PNG_VALUE, DOWNLOAD_THUMBNAIL_PNG);
        }
        response.setStatus(HttpServletResponse.SC_OK);
    }

    /**
     * Existence du logo
     *
     * @param userId
     * @return
     * @throws PgcnTechnicalException
     */
    @RequestMapping(value = "/{id}", method = RequestMethod.GET, params = {"signexists"}, produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @RolesAllowed({USER_HAB0,
                   USER_HAB6})
    public ResponseEntity<Map<?, ?>> hasSignature(@PathVariable("id") final String userId) {

        final User user = userService.getOne(userId);
        if (user == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        if (!accessHelper.checkUser(userId)) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        // Réponse
        final Map<String, Boolean> result = new HashMap<>();
        final File importFile = userService.getUserSignature(user);
        result.put(userId, importFile != null && importFile.exists());
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    /**
     * Suppression du logo
     *
     * @param request
     * @param userId
     * @return
     */
    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE, params = {"signature"}, produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @RolesAllowed({USER_HAB2,
                   USER_HAB6})
    public void deleteSignature(final HttpServletRequest request, final HttpServletResponse response, @PathVariable("id") final String userId) {
        // Droits d'accès: autres profils
        if (request.isUserInRole(USER_HAB2)) {
            if (!accessHelper.checkUser(userId)) {
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            }
        }
        // Droits d'accès: son propre profil
        else if (request.isUserInRole(USER_HAB6)) {
            final CustomUserDetails currentUser = SecurityUtils.getCurrentUser();
            if (currentUser == null || !StringUtils.equals(currentUser.getIdentifier(), userId)) {
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            }
        }
        // User non trouvé
        final User user = userService.getOne(userId);
        if (user == null) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return;
        }
        // Suppression de la signature
        userService.deleteUserSignature(user);
        response.setStatus(HttpServletResponse.SC_OK);
    }

    /**
     * Téléversement d'une liste de fichiers
     *
     * @param request
     * @param userId
     * @param files
     * @return
     */
    @RequestMapping(value = "/{id}", method = RequestMethod.POST, params = {"signature"}, produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @RolesAllowed({USER_HAB2,
                   USER_HAB6})
    public void uploadSignature(final HttpServletRequest request,
                                final HttpServletResponse response,
                                @PathVariable("id") final String userId,
                                @RequestParam(name = "file") final List<MultipartFile> files) {
        // Droits d'accès: autres profils
        if (request.isUserInRole(USER_HAB2)) {
            if (!accessHelper.checkUser(userId)) {
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            }
        }
        // Droits d'accès: son propre profil
        else if (request.isUserInRole(USER_HAB6)) {
            final CustomUserDetails currentUser = SecurityUtils.getCurrentUser();
            if (currentUser == null || !StringUtils.equals(currentUser.getIdentifier(), userId)) {
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            }
        }
        // Pas de fichier transmis
        if (files.isEmpty()) {
            LOG.warn("Aucun fichier n'a été reçu pour le mise à jour de la signature de l'utilisateur {}", userId);
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        }
        // User non trouvé
        final User user = userService.findByIdentifier(userId);
        if (user == null) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return;
        }
        // Upload du fichier
        userService.uploadSignature(user, files.get(0));
        response.setStatus(HttpServletResponse.SC_OK);
    }
}
