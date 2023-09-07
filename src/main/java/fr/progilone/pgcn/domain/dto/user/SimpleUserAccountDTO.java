package fr.progilone.pgcn.domain.dto.user;

import java.util.List;

/**
 * DTO représentant un usager allégé
 *
 * @author jbrunet
 * @see fr.progilone.pgcn.domain.user.User
 */
public class SimpleUserAccountDTO {

    private final String identifier;
    private final String surname;
    private final String firstname;
    private final String login;
    private final String dashboard;
    private final String library;
    private final String category;
    private final List<String> roles;

    public SimpleUserAccountDTO(final String identifier,
                                final String firstname,
                                final String surname,
                                final String login,
                                final String dashboard,
                                final String library,
                                final String category,
                                final List<String> roles) {
        this.surname = surname;
        this.firstname = firstname;
        this.identifier = identifier;
        this.login = login;
        this.dashboard = dashboard;
        this.library = library;
        this.category = category;
        this.roles = roles;
    }

    public final String getFirstname() {
        return firstname;
    }

    public final String getSurname() {
        return surname;
    }

    public final String getIdentifier() {
        return identifier;
    }

    public final String getLogin() {
        return login;
    }

    public final String getDashboard() {
        return dashboard;
    }

    public final String getLibrary() {
        return library;
    }

    public String getCategory() {
        return category;
    }

    public final List<String> getRoles() {
        return roles;
    }

    /**
     * Builder pour la classe SimpleUserDTO
     *
     * @author jbrunet
     */
    public static final class Builder {

        private String identifier;
        private String firstname;
        private String surname;
        private String login;
        private String dashboard;
        private String library;
        private String category;
        private List<String> roles;

        public Builder reinit() {
            this.identifier = null;
            this.firstname = null;
            this.surname = null;
            this.login = null;
            this.dashboard = null;
            this.library = null;
            this.category = null;
            this.roles = null;
            return this;
        }

        public Builder setIdentifier(final String identifier) {
            this.identifier = identifier;
            return this;
        }

        public Builder setFirstname(final String firstname) {
            this.firstname = firstname;
            return this;
        }

        public Builder setSurname(final String surname) {
            this.surname = surname;
            return this;
        }

        public Builder setLogin(final String login) {
            this.login = login;
            return this;
        }

        public Builder setDashboard(final String dashboard) {
            this.dashboard = dashboard;
            return this;
        }

        public Builder setLibrary(final String library) {
            this.library = library;
            return this;
        }

        public Builder setRoles(final List<String> roles) {
            this.roles = roles;
            return this;
        }

        public Builder setCategory(final String category) {
            this.category = category;
            return this;
        }

        public SimpleUserAccountDTO build() {
            return new SimpleUserAccountDTO(identifier, firstname, surname, login, dashboard, library, category, roles);
        }
    }
}
