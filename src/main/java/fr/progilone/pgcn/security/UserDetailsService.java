package fr.progilone.pgcn.security;

import fr.progilone.pgcn.domain.user.Lang;
import fr.progilone.pgcn.domain.user.Role;
import fr.progilone.pgcn.domain.user.User;
import fr.progilone.pgcn.domain.util.CustomUserDetails;
import fr.progilone.pgcn.repository.user.AuthorizationRepository;
import fr.progilone.pgcn.repository.user.UserRepository;
import fr.progilone.pgcn.service.user.UserService;
import fr.progilone.pgcn.web.rest.administration.security.AuthorizationConstants;
import java.util.Collection;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * Authenticate a user from the database.
 */
@Component("userDetailsService")
public class UserDetailsService implements org.springframework.security.core.userdetails.UserDetailsService {

    private static final Logger LOG = LoggerFactory.getLogger(UserDetailsService.class);
    private static final String ROLE_PREFIX = "ROLE_";

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private AuthorizationRepository authorizationRepository;

    @Value("${admin.login}")
    private String adminLogin;
    @Value("${admin.password}")
    private String adminPassword;

    @Override
    @Transactional
    public CustomUserDetails loadUserByUsername(final String login) {
        LOG.debug("Authenticating {}", login);

        final String lowercaseLogin = login.toLowerCase();

        Collection<GrantedAuthority> grantedAuthorities;
        String userId;
        Lang lang = null;
        String password;
        String library = null;
        boolean superuser = false;
        User.Category category = null;

        final User userFromDatabase = userRepository.findByLogin(lowercaseLogin);
        if (userFromDatabase == null) {
            if (adminLogin != null && lowercaseLogin.equalsIgnoreCase(adminLogin)) {
                grantedAuthorities = authorizationRepository.findAll()
                                                            .stream()
                                                            .map(authorization -> new SimpleGrantedAuthority(ROLE_PREFIX + authorization.getCode()))
                                                            .collect(Collectors.toList());
                grantedAuthorities.add(new SimpleGrantedAuthority(ROLE_PREFIX + AuthorizationConstants.SUPER_ADMIN));
                grantedAuthorities.add(new SimpleGrantedAuthority(ROLE_PREFIX + AuthorizationConstants.ACTUATOR));
                password = adminPassword;
                userId = UserService.SUPER_ADMIN_ID;
            } else {
                throw new UsernameNotFoundException("User " + lowercaseLogin
                                                    + " was not found in the database");
            }
        } else {
            if (userFromDatabase.isSuperuser()) {
                superuser = true;
            }
            final Role role = userFromDatabase.getRole();
            if (role != null) {
                grantedAuthorities = role.getAuthorizations()
                                         .stream()
                                         .map(authorization -> new SimpleGrantedAuthority(ROLE_PREFIX + authorization.getCode()))
                                         .collect(Collectors.toList());
            } else {
                throw new UserWithoutRoleException("User " + lowercaseLogin
                                                   + " has no role in the database");
            }
            if (userFromDatabase.getLang() != null) {
                lang = userFromDatabase.getLang();
            }
            library = userFromDatabase.getLibrary().getIdentifier();
            password = userFromDatabase.getPassword();
            userId = userFromDatabase.getIdentifier();
            category = userFromDatabase.getCategory();
        }
        if (lang == null) {
            lang = Lang.FR;
        }

        return new CustomUserDetails(userId, lowercaseLogin, password, lang, library, grantedAuthorities, superuser, category);
    }
}
