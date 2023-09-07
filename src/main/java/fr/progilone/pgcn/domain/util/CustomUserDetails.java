package fr.progilone.pgcn.domain.util;

import fr.progilone.pgcn.domain.user.Lang;
import fr.progilone.pgcn.domain.user.User;
import java.util.Collection;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * Classe permettant de stocker des informations sur l'utilisateur connecté.
 *
 * @author hdurix
 */
public class CustomUserDetails implements UserDetails {

    private final String identifier;
    private final String login;
    private final String password;
    private final Collection<? extends GrantedAuthority> authorities;
    private Lang lang;
    private final String libraryId;
    private final boolean superuser;
    private final User.Category category;

    public CustomUserDetails(final String identifier,
                             final String login,
                             final String password,
                             final Lang lang,
                             final String libraryId,
                             final Collection<GrantedAuthority> authorities,
                             final boolean superuser,
                             final User.Category category) {
        super();
        this.identifier = identifier;
        this.login = login;
        this.password = password;
        this.lang = lang;
        this.libraryId = libraryId;
        this.authorities = authorities;
        this.superuser = superuser;
        this.category = category;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return login;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    public Lang getLang() {
        return lang;
    }

    public void setLang(final Lang lang) {
        this.lang = lang;
    }

    public String getLibraryId() {
        return libraryId;
    }

    public String getIdentifier() {
        return identifier;
    }

    public String getLogin() {
        return login;
    }

    public boolean isSuperuser() {
        return superuser;
    }

    public User.Category getCategory() {
        return category;
    }

    public boolean isUserInRole(final String role) {
        final String prefix = "ROLE_";
        final SimpleGrantedAuthority authority = new SimpleGrantedAuthority(prefix + role);
        return role == null || authorities.contains(authority);
    }
}
