package fr.progilone.pgcn.service.library.ui;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import fr.progilone.pgcn.domain.dto.library.LibraryDTO;
import fr.progilone.pgcn.domain.dto.library.SimpleLibraryDTO;
import fr.progilone.pgcn.domain.dto.user.SimpleUserDTO;
import fr.progilone.pgcn.domain.library.Library;
import fr.progilone.pgcn.domain.user.User;
import fr.progilone.pgcn.exception.PgcnValidationException;
import fr.progilone.pgcn.exception.message.PgcnError;
import fr.progilone.pgcn.exception.message.PgcnErrorCode;
import fr.progilone.pgcn.exception.message.PgcnList;
import fr.progilone.pgcn.service.library.LibraryService;
import fr.progilone.pgcn.service.library.mapper.LibraryMapper;
import fr.progilone.pgcn.service.library.mapper.SimpleLibraryMapper;
import fr.progilone.pgcn.service.library.mapper.UILibraryMapper;
import fr.progilone.pgcn.service.user.UserService;
import fr.progilone.pgcn.service.user.mapper.UserMapper;
import fr.progilone.pgcn.service.util.transaction.VersionValidationService;

/**
 * Service dédié à les gestion des vues des bibliothèques
 *
 * @author jbrunet
 */
@Service
public class UILibraryService {

    private final LibraryService libraryService;
    private final UILibraryMapper uiLibraryMapper;
    private final UserService userService;

    @Autowired
    public UILibraryService(final LibraryService libraryService,
                            final UILibraryMapper uiLibraryMapper,
                            final UserService userService) {
        this.libraryService = libraryService;
        this.uiLibraryMapper = uiLibraryMapper;
        this.userService = userService;
    }

    @Transactional
    public LibraryDTO create(final LibraryDTO request) throws PgcnValidationException {
        validate(request);
        final Library library = new Library();
        uiLibraryMapper.mapInto(request, library);
        final Library savedLibrary = libraryService.save(library);
        final Library libraryWithProperties = libraryService.findOne(savedLibrary.getIdentifier());
        return LibraryMapper.INSTANCE.libraryToLibraryDTO(libraryWithProperties);
    }

    /**
     * Mise à jour d'une bibliothèque
     *
     * @param request un objet contenant les informations necessaires à l'enregistrement d'une bibliothèque
     * @return la bibliothèque nouvellement créée ou mise à jour
     * @throws PgcnValidationException
     */
    @Transactional
    public LibraryDTO update(final LibraryDTO request) throws PgcnValidationException {
        validate(request);
        final Library library = libraryService.findOne(request.getIdentifier());

        // Contrôle d'accès concurrents
        VersionValidationService.checkForStateObject(library, request);

        uiLibraryMapper.mapInto(request, library);
        return LibraryMapper.INSTANCE.libraryToLibraryDTO(libraryService.save(library));
    }

    @Transactional(readOnly = true)
    public LibraryDTO getOneDTO(final String id) {
        final Library library = libraryService.findOneWithDependencies(id);
        return LibraryMapper.INSTANCE.libraryToLibraryDTO(library);
    }

    @Transactional
    public Page<SimpleLibraryDTO> search(final String search,
                                         final String initiale,
                                         final List<String> institutions,
                                         final List<String> filteredLibraries,
                                         final boolean isActive,
                                         final Integer page,
                                         final Integer size) {
        final Page<Library> libraries = libraryService.search(search, filteredLibraries, initiale, institutions, isActive, page, size);
        return libraries.map(SimpleLibraryMapper.INSTANCE::libraryToSimpleLibraryDTO);
    }

    @Transactional
    public List<SimpleLibraryDTO> findAllActiveDTO() {
        final List<Library> libraries = libraryService.findAllByActive(true);
        return libraries.stream().map(SimpleLibraryMapper.INSTANCE::libraryToSimpleLibraryDTO).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Collection<SimpleUserDTO> findProviders(final String id) {
        final Collection<User> users = userService.findProvidersForLibrary(id);
        return users.stream().map(UserMapper.INSTANCE::userToSimpleUserDTO).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Collection<SimpleUserDTO> findUsers(final String id) {
        final Collection<User> users = userService.findUsersForLibrary(id);
        return users.stream().map(UserMapper.INSTANCE::userToSimpleUserDTO).collect(Collectors.toList());
    }

    /**
     * Validation des champs unique au niveau du DTO avant le merge
     */
    private PgcnList<PgcnError> validate(final LibraryDTO dto) throws PgcnValidationException {
        final PgcnList<PgcnError> errors = new PgcnList<>();

        final PgcnError.Builder builder = new PgcnError.Builder();

        // Le nom est unique
        if (StringUtils.isNotBlank(dto.getName())) {
            final Library duplicate = libraryService.findOneByName(dto.getName());
            if (duplicate != null && (dto.getIdentifier() == null || !duplicate.getIdentifier().equalsIgnoreCase(dto.getIdentifier()))) {
                errors.add(builder.reinit().setCode(PgcnErrorCode.LIBRARY_DUPLICATE_NAME).setField("name").build());
            }
        }

        // Retour
        if (!errors.isEmpty()) {
            dto.setErrors(errors);
            throw new PgcnValidationException(dto, errors);
        }
        return errors;
    }
}
