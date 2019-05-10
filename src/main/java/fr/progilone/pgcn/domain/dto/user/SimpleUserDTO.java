package fr.progilone.pgcn.domain.dto.user;

import fr.progilone.pgcn.domain.dto.library.SimpleLibraryDTO;

/**
 * DTO représentant un usager allégé
 *
 * @author jbrunet
 * @see fr.progilone.pgcn.domain.user.User
 */
public class SimpleUserDTO {

    private String identifier;
    private String surname;
    private String firstname;
    private String fullName;
    private String login;
    private SimpleLibraryDTO library;

    public SimpleUserDTO() {
    }

    public SimpleUserDTO(final String identifier,
                         final String firstname,
                         final String surname,
                         final String fullName,
                         final String login,
                         final SimpleLibraryDTO library) {
        super();
        this.surname = surname;
        this.firstname = firstname;
        this.identifier = identifier;
        this.fullName = fullName;
        this.login = login;
        this.library = library;
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

    public final void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public final void setSurname(String surname) {
        this.surname = surname;
    }

    public final void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(final String fullName) {
        this.fullName = fullName;
    }

    public SimpleLibraryDTO getLibrary() {
        return library;
    }

    public void setLibrary(SimpleLibraryDTO library) {
        this.library = library;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
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
        private String fullname;
        private String login;
        private SimpleLibraryDTO library;

        public Builder reinit() {
            this.identifier = null;
            this.firstname = null;
            this.surname = null;
            this.fullname = null;
            this.login = null;
            this.library = null;
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

        public Builder setFullname(final String fullname) {
            this.fullname = fullname;
            return this;
        }

        public Builder setLibrary(SimpleLibraryDTO library) {
            this.library = library;
            return this;
        }

        public Builder setLogin(String login) {
            this.login = login;
            return this;
        }

        public SimpleUserDTO build() {
            return new SimpleUserDTO(identifier, firstname, surname, fullname, login, library);
        }
    }
}
