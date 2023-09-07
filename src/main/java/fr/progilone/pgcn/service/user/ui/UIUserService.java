package fr.progilone.pgcn.service.user.ui;

import static fr.progilone.pgcn.service.user.UserService.*;

import fr.progilone.pgcn.domain.dto.user.SimpleUserAccountDTO;
import fr.progilone.pgcn.domain.dto.user.SimpleUserDTO;
import fr.progilone.pgcn.domain.dto.user.UserCreationDTO;
import fr.progilone.pgcn.domain.dto.user.UserDTO;
import fr.progilone.pgcn.domain.user.Dashboard;
import fr.progilone.pgcn.domain.user.User;
import fr.progilone.pgcn.domain.user.User.Category;
import fr.progilone.pgcn.domain.util.CustomUserDetails;
import fr.progilone.pgcn.exception.PgcnValidationException;
import fr.progilone.pgcn.repository.user.DashboardRepository;
import fr.progilone.pgcn.security.SecurityUtils;
import fr.progilone.pgcn.service.user.UserService;
import fr.progilone.pgcn.service.user.mapper.UIUserMapper;
import fr.progilone.pgcn.service.user.mapper.UserMapper;
import fr.progilone.pgcn.service.util.transaction.VersionValidationService;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service dédié à les gestion des vues des utilisateurs
 */
@Service
public class UIUserService {

    private static final UserMapper USER_MAPPER = UserMapper.INSTANCE;

    @Value("${admin.login}")
    private String adminLogin;

    private final UserService userService;
    private final DashboardRepository dashboardRepository;
    private final UIUserMapper uiUserMapper;

    @Autowired
    public UIUserService(final UserService userService, final UIUserMapper uiUserMapper, final DashboardRepository dashboardRepository) {
        this.userService = userService;
        this.uiUserMapper = uiUserMapper;
        this.dashboardRepository = dashboardRepository;
    }

    @Transactional
    public UserDTO create(final UserCreationDTO request) throws PgcnValidationException {
        final User user = new User();
        uiUserMapper.mapInto(request, user);
        final User savedUser = userService.create(user);
        final User userWithProperties = userService.getOne(savedUser.getIdentifier());
        return UserMapper.INSTANCE.userToUserDTO(userWithProperties);
    }

    /**
     * Mise à jour d'un utilisateur
     *
     * @param request
     *            un objet contenant les informations necessaires à l'enregistrement d'un utilisateur
     * @return l'utilisateur nouvellement créée ou mise à jour
     * @throws PgcnValidationException
     */
    @Transactional
    public UserDTO update(final UserDTO request) throws PgcnValidationException {
        final User user = userService.getOne(request.getIdentifier());

        // Contrôle d'accès concurrents
        VersionValidationService.checkForStateObject(user, request);

        if (user.isSuperuser()) {
            // TODO échouer proprement
            return UserMapper.INSTANCE.userToUserDTO(user);
        } else {
            uiUserMapper.mapInto(request, user);
            final User savedUser = userService.update(user);
            final User userWithProperties = userService.getOne(savedUser.getIdentifier());
            return UserMapper.INSTANCE.userToUserDTO(userWithProperties);
        }
    }

    @Transactional(readOnly = true)
    public UserDTO getOne(String id) {
        final User user = userService.getOneWithGroups(id);
        return UserMapper.INSTANCE.userToUserDTO(user);
    }

    @Transactional(readOnly = true)
    public SimpleUserAccountDTO getCurrentUserWithAuthoritiesAndDashboard() {
        final User user = userService.getOne(SecurityUtils.getCurrentUserId());
        if (user == null && SecurityUtils.getCurrentLogin().equals(adminLogin)) {
            final Dashboard dashboard = dashboardRepository.findById(SUPER_ADMIN_ID).orElse(null);
            final SimpleUserAccountDTO.Builder builder = new SimpleUserAccountDTO.Builder().reinit()
                                                                                           .setFirstname("Admin")
                                                                                           .setIdentifier(SUPER_ADMIN_ID)
                                                                                           .setLogin(adminLogin)
                                                                                           .setSurname("Progilone")
                                                                                           .setDashboard(dashboard != null ? dashboard.getDashboard()
                                                                                                                           : null);

            final CustomUserDetails currentUser = SecurityUtils.getCurrentUser();
            if (currentUser != null) {
                builder.setCategory(currentUser.getCategory() != null ? currentUser.getCategory().name()
                                                                      : null);
                builder.setRoles(currentUser.getAuthorities().stream().map(a -> a.getAuthority().substring(5)).collect(Collectors.toList()));
            }
            return builder.build();
        }
        return user != null ? uiUserMapper.userToSimpleUserAccountDTO(user)
                            : null;
    }

    @Transactional(readOnly = true)
    public SimpleUserDTO getCurrentUserDTO() {
        final User user = userService.getOne(SecurityUtils.getCurrentUserId());
        if (user == null) {
            final SimpleUserDTO simpleUserDTO = new SimpleUserDTO();
            simpleUserDTO.setLogin(SecurityUtils.getCurrentLogin());
            return simpleUserDTO;
        } else {
            return USER_MAPPER.userToSimpleUserDTO(user);
        }
    }

    @Transactional(readOnly = true)
    public List<SimpleUserDTO> findAllActiveDTO() {
        final List<User> users = userService.findAllByActive(true);
        return users.stream().map(UserMapper.INSTANCE::userToSimpleUserDTO).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Collection<SimpleUserDTO> findAllProvidersDTO() {
        final List<User> users = userService.findAllByActiveAndCategory(true, User.Category.PROVIDER);
        return users.stream().map(UserMapper.INSTANCE::userToSimpleUserDTO).sorted(Comparator.comparing(SimpleUserDTO::getFullName)).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public UserDTO duplicateUser(String id) {
        final User duplicated = userService.duplicateUser(id);
        return UserMapper.INSTANCE.userToUserDTO(duplicated);
    }

    @Transactional(readOnly = true)
    public Page<SimpleUserDTO> search(String search,
                                      String initiale,
                                      boolean active,
                                      boolean filterProviders,
                                      List<String> libraries,
                                      List<Category> categories,
                                      List<String> roles,
                                      Integer page,
                                      Integer size) {
        final Page<User> users = userService.search(search, initiale, active, filterProviders, libraries, categories, roles, page, size);
        return users.map(UserMapper.INSTANCE::userToSimpleUserDTO);
    }
}
