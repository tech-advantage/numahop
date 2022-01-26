package fr.progilone.pgcn.web.util;

import fr.progilone.pgcn.domain.dto.library.LibraryDTO;
import fr.progilone.pgcn.domain.library.Library;
import fr.progilone.pgcn.domain.util.CustomUserDetails;
import fr.progilone.pgcn.security.SecurityUtils;
import fr.progilone.pgcn.web.rest.administration.security.AuthorizationConstants;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Regroupement des vérifications d'accès par bibliothèque
 */
@Component
public class LibraryAccesssHelper {

    private final AccessHelper accessHelper;

    @Autowired
    public LibraryAccesssHelper(final AccessHelper accessHelper) {
        this.accessHelper = accessHelper;
    }

    /**
     * Vérifie que l'utilisateur courant ait bien accès à la bibliothèque
     *
     * @param request
     * @param libraryId
     * @return
     */
    @Transactional(readOnly = true)
    public boolean checkLibrary(final HttpServletRequest request, final String libraryId, final String... bypassRoles) {
        return checkLibrary(request, libraryId, id -> {
            final Library lib = new Library();
            lib.setIdentifier(libraryId);
            return lib;
        }, bypassRoles);
    }

    /**
     * Vérifie que l'utilisateur courant ait bien accès à la bibliothèque
     *
     * @param request
     * @param library
     * @return
     */
    @Transactional(readOnly = true)
    public boolean checkLibrary(final HttpServletRequest request, final Library library, final String... bypassRoles) {
        return checkLibrary(request, library, Function.identity(), bypassRoles);
    }

    /**
     * Vérifie que l'utilisateur courant ait bien accès à object par rapport à sa bibliothèque
     *
     * @param request
     * @param object
     * @param getLibraryFn
     * @param bypassRoles
     * @param <T>
     * @return
     */
    @Transactional(readOnly = true)
    public <T> boolean checkLibrary(final HttpServletRequest request,
                                    final T object,
                                    final Function<T, Library> getLibraryFn,
                                    final String... bypassRoles) {
        // On ne contrôle pas la bibliothèque pour les utilisateurs ayant un rôle dans bypassRoles
        final String[] roles = Arrays.copyOf(bypassRoles, bypassRoles.length + 2);
        roles[roles.length - 1] = AuthorizationConstants.SUPER_ADMIN;
        roles[roles.length - 2] = AuthorizationConstants.ADMINISTRATION_LIB;

        if (Stream.of(roles).anyMatch(request::isUserInRole)) {
            return true;
        }
        // Vérification des droits d'accès par rapport à la bibliothèque de l'utilisateur
        else {
            final CustomUserDetails currentUser = SecurityUtils.getCurrentUser();
            return accessHelper.checkCurrentUser(currentUser).orElseGet(() -> {
                final Library library = getLibraryFn.apply(object);
                return library == null || StringUtils.equals(library.getIdentifier(), currentUser.getLibraryId());
            });
        }
    }

    /**
     * Renvoie les bibliothèques à inclure dans la recherche
     * La liste renvoyée dépend des droits de l'utilisateur courant
     * Si l'utilisateur est superadmin, on ne filtre pas la liste demandée
     * Sinon il n'a droit d'accéder qu'à sa bibliothèque
     *
     * @param request
     * @param libraryFilter
     * @return
     */
    @Transactional(readOnly = true)
    public List<String> getLibraryFilter(final HttpServletRequest request, final List<String> libraryFilter, final String... bypassRoles) {
        // On ne contrôle pas la bibliothèque pour les utilisateurs ayant un rôle dans bypassRoles
        final String[] roles = Arrays.copyOf(bypassRoles, bypassRoles.length + 2);
        roles[roles.length - 1] = AuthorizationConstants.SUPER_ADMIN;
        roles[roles.length - 2] = AuthorizationConstants.ADMINISTRATION_LIB;

        if (Stream.of(roles).anyMatch(request::isUserInRole)) {
            return libraryFilter != null ? libraryFilter : Collections.emptyList();
        }
        // Filtrage des mappings par rapport à la bibliothèque de l'utilisateur, pour les non-admin
        else {
            final CustomUserDetails currentUser = SecurityUtils.getCurrentUser();
            return accessHelper.checkCurrentUser(currentUser)
                               .map(check -> check && libraryFilter != null ? libraryFilter : Collections.<String>emptyList())
                               .orElseGet(() -> Collections.singletonList(currentUser.getLibraryId()));
        }
    }

    /**
     * Filtre la liste d'objets par rapport à la bibliothèque de l'utilisateur courant
     *
     * @param request
     * @param objects
     * @param getLibraryIdFn
     * @param <T>
     * @return
     */
    @Transactional(readOnly = true)
    public <T> Collection<T> filterObjectsByLibrary(final HttpServletRequest request,
                                                    final Collection<T> objects,
                                                    final Function<T, String> getLibraryIdFn,
                                                    final String... bypassRoles) {
        // On ne contrôle pas la bibliothèque pour les utilisateurs ayant un rôle dans bypassRoles
        final String[] roles = Arrays.copyOf(bypassRoles, bypassRoles.length + 2);
        roles[roles.length - 1] = AuthorizationConstants.SUPER_ADMIN;
        roles[roles.length - 2] = AuthorizationConstants.ADMINISTRATION_LIB;

        if (Stream.of(roles).anyMatch(request::isUserInRole)) {
            return objects;
        }
        // Filtrage des mappings par rapport à la bibliothèque de l'utilisateur, pour les non-admin
        else {
            final CustomUserDetails currentUser = SecurityUtils.getCurrentUser();
            return accessHelper.checkCurrentUser(currentUser)
                               .map(check -> check ? objects : Collections.<T>emptyList())
                               .orElseGet(() -> objects.stream()
                                                       .filter(dto -> StringUtils.equals(getLibraryIdFn.apply(dto), currentUser.getLibraryId()))
                                                       .collect(Collectors.toList()));
        }
    }

    /**
     * Définit la bib par défaut par celle de l'utilisateur
     *
     * @param <T>
     * @param libSetter
     */
    @Transactional(readOnly = true)
    public <T> void setDefaultLibrary(final Consumer<LibraryDTO> libSetter) {
        final CustomUserDetails currentUser = SecurityUtils.getCurrentUser();
        if (currentUser != null) {
            final LibraryDTO libraryDto = new LibraryDTO();
            libraryDto.setIdentifier(currentUser.getLibraryId());
            libSetter.accept(libraryDto);
        }
    }
}
