package fr.progilone.pgcn.domain.dto.user;

import fr.progilone.pgcn.domain.dto.AbstractVersionedDTO;
import fr.progilone.pgcn.domain.dto.library.SimpleLibraryDTO;
import fr.progilone.pgcn.domain.dto.workflow.SimpleWorkflowGroupDTO;
import fr.progilone.pgcn.domain.user.Lang;
import fr.progilone.pgcn.domain.user.User;
import fr.progilone.pgcn.domain.user.User.Category;

import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO représentant un usager et ses droits
 *
 * @author jbrunet
 * @see User
 */
public class UserDTO extends AbstractVersionedDTO {

    private String identifier;
    private String login;
    private String firstname;
    private String surname;
    private Boolean active;
    private String category;
    private Lang lang;
    private RoleDTO role;
    private AddressDTO address;
    private SimpleLibraryDTO library;
    private String phoneNumber;
    private String email;
    private String companyName;
    private String function;
    /**
     * Groupe de workflow liés
     */
    private List<SimpleWorkflowGroupDTO> groups;
    /**
     * Ajout des infos de création
     */
    private String createdBy;
    private LocalDateTime createdDate;
    /**
     * Ajout des infos de modifications
     */
    private String lastModifiedBy;
    private LocalDateTime lastModifiedDate;

    public UserDTO(String identifier, String login, String firstname, String surname, Boolean active, String category, Lang lang, RoleDTO role, AddressDTO address, SimpleLibraryDTO library, String phoneNumber, String email, String companyName, String function, String createdBy, LocalDateTime createdDate, String lastModifiedBy, LocalDateTime lastModifiedDate) {
        this.identifier = identifier;
        this.login = login;
        this.firstname = firstname;
        this.surname = surname;
        this.active = active;
        this.category = category;
        this.lang = lang;
        this.role = role;
        this.address = address;
        this.library = library;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.companyName = companyName;
        this.function = function;
        this.createdBy = createdBy;
        this.createdDate = createdDate;
        this.lastModifiedBy = lastModifiedBy;
        this.lastModifiedDate = lastModifiedDate;
    }

    public UserDTO() {
    }

    public String getIdentifier() {
        return identifier;
    }

    public final Lang getLang() {
		return lang;
	}

	public final RoleDTO getRole() {
		return role;
	}

	public String getLogin() {
        return login;
    }

    public String getFirstname() {
        return firstname;
    }

    public String getSurname() {
        return surname;
    }

    public final Boolean getActive() {
		return active;
	}

    public final String getCategory() {
		return category;
	}

	public final AddressDTO getAddress() {
        return address;
    }

	public final String getPhoneNumber() {
		return phoneNumber;
	}

	public final String getCompanyName() {
		return companyName;
	}

	public final String getEmail() {
		return email;
	}

	public final String getFunction() {
		return function;
	}

    public final SimpleLibraryDTO getLibrary() {
		return library;
	}

	public final void setLibrary(SimpleLibraryDTO library) {
		this.library = library;
	}

	public final void setIdentifier(String identifier) {
		this.identifier = identifier;
	}

	public final void setLogin(String login) {
		this.login = login;
	}

	public final void setFirstname(String firstname) {
		this.firstname = firstname;
	}

	public final void setSurname(String surname) {
		this.surname = surname;
	}

	public final void setActive(Boolean active) {
		this.active = active;
	}

	public final void setCategory(String category) {
		this.category = category;
	}

	public final void setLang(Lang lang) {
		this.lang = lang;
	}

	public final void setRole(RoleDTO role) {
		this.role = role;
	}

	public final void setAddress(AddressDTO address) {
		this.address = address;
	}

	public final void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public final void setEmail(String email) {
		this.email = email;
	}

	public final void setCompanyName(String companyName) {
		this.companyName = companyName;
	}

	public final void setFunction(String function) {
		this.function = function;
	}

    public List<SimpleWorkflowGroupDTO> getGroups() {
        return groups;
    }

    public void setGroups(List<SimpleWorkflowGroupDTO> groups) {
        this.groups = groups;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public LocalDateTime getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(LocalDateTime createdDate) {
        this.createdDate = createdDate;
    }

    public String getLastModifiedBy() {
        return lastModifiedBy;
    }

    public void setLastModifiedBy(String lastModifiedBy) {
        this.lastModifiedBy = lastModifiedBy;
    }

    public LocalDateTime getLastModifiedDate() {
        return lastModifiedDate;
    }

    public void setLastModifiedDate(LocalDateTime lastModifiedDate) {
        this.lastModifiedDate = lastModifiedDate;
    }

    /**
     * Builder pour la classe UserDTO
     *
     * @author jbrunet
     */
    public static final class Builder {

        private String identifier;
        private String login;
        private String firstname;
        private String surname;
        private Boolean active;
        private String category;
        private SimpleLibraryDTO library;
        private Lang lang;
        private RoleDTO role;
        private AddressDTO address;
        private String phoneNumber;
        private String email;
        private String companyName;
        private String function;
        private String createdBy;
        private LocalDateTime createdDate;
        private String lastModifiedBy;
        private LocalDateTime lastModifiedDate;

        public Builder reinit() {
            this.identifier = null;
            this.login = null;
            this.firstname = null;
            this.surname = null;
            this.active = null;
            this.category = null;
            this.library = null;
            this.lang = null;
            this.role = null;
            this.address = null;
            this.email = null;
            this.phoneNumber = null;
            this.companyName = null;
            this.function = null;
            this.createdBy = null;
            this.createdDate = null;
            this.lastModifiedBy = null;
            this.lastModifiedDate = null;
            return this;
        }

        public Builder setIdentifier(final String identifier) {
            this.identifier = identifier;
            return this;
        }

        public Builder setLogin(final String login) {
            this.login = login;
            return this;
        }

        public Builder setFirstname(final String firstname) {
            this.firstname = firstname;
            return this;
        }

        public Builder setActive(final Boolean active) {
            this.active = active;
            return this;
        }

        public Builder setCategory(final Category category) {
            this.category = category.name();
            return this;
        }

        public Builder setSurname(final String surname) {
            this.surname = surname;
            return this;
        }

        public Builder setRole(final RoleDTO role) {
            this.role = role;
            return this;
        }

        public Builder setLibrary(final SimpleLibraryDTO library) {
            this.library = library;
            return this;
        }

        public Builder setLang(final Lang lang) {
            this.lang = lang;
            return this;
        }

        public Builder setAddress(final AddressDTO address) {
        	this.address = address;
        	return this;
        }

        public Builder setPhoneNumber(final String phoneNumber) {
            this.phoneNumber = phoneNumber;
            return this;
        }

        public Builder setEmail(final String email) {
            this.email = email;
            return this;
        }

        public Builder setCompanyName(final String companyName) {
            this.companyName = companyName;
            return this;
        }

        public Builder setFunction(final String function) {
            this.function = function;
            return this;
        }

        public Builder setCreatedBy(String createdBy) {
            this.createdBy = createdBy;
            return this;
        }

        public Builder setCreatedDate(LocalDateTime createdDate) {
            this.createdDate = createdDate;
            return this;
        }

        public Builder setLastModifiedBy(String lastModifiedBy) {
            this.lastModifiedBy = lastModifiedBy;
            return this;
        }

        public Builder setLastModifiedDate(LocalDateTime lastModifiedDate) {
            this.lastModifiedDate = lastModifiedDate;
            return this;
        }

        public UserDTO build() {
            return new UserDTO(identifier,
                                   login,
                                   firstname,
                                   surname,
                                   active,
                                   category,
                                   lang,
                                   role,
                                   address,
                                   library,
                                   phoneNumber,
                                   email,
                                   companyName,
                                   function,
                                    createdBy,
                                    createdDate,
                                    lastModifiedBy,
                                    lastModifiedDate
                                   );
        }
    }
}
