package fr.progilone.pgcn.domain.user;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.google.common.base.MoreObjects;
import fr.progilone.pgcn.domain.AbstractDomainObject;
import fr.progilone.pgcn.domain.library.Library;
import fr.progilone.pgcn.domain.project.Project;
import fr.progilone.pgcn.domain.security.PersistentToken;
import fr.progilone.pgcn.domain.workflow.WorkflowGroup;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.util.HashSet;
import java.util.Set;

import static fr.progilone.pgcn.service.es.EsConstant.*;

/**
 * Usager
 *
 * @author Jonathan BRUNET
 */
@Entity
@Table(name = User.TABLE_NAME)
@JsonSubTypes({@JsonSubTypes.Type(name = "user", value = User.class)})
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class User extends AbstractDomainObject {

    public static final String TABLE_NAME = "user_user";

    /**
     * identifiant
     */
    @Column(name = "login", unique = true, nullable = false)
    @Field(type = FieldType.String, analyzer = ANALYZER_KEYWORD)
    private String login;

    /**
     * Mot de passe
     */
    @JsonIgnore
    @Column(name = "password")
    private String password;

    /**
     * Prénom
     */
    @Column(name = "firstname")
    private String firstname;

    /**
     * Nom
     */
    @Column(name = "surname")
    private String surname;

    /**
     * Fonction
     */
    @Column(name = "function")
    private String function;

    /**
     * Numéro de téléphone
     */
    @Column(name = "phone_number")
    private String phoneNumber;

    /**
     * Couriel
     */
    @Column(name = "email", nullable = false)
    private String email;

    /**
     * Actif
     */
    @Column(name = "active")
    private boolean active;

    /**
     * Superutilisateur
     */
    @Column(name = "superuser")
    private boolean superuser;

    /**
     * Nom de la société (prestataire)
     */
    @Column(name = "company_name")
    private String companyName;

    /**
     * Adresse de l'utilisateur (prestataire)
     */
    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "address")
    private Address address;

    /**
     * Bibliothèque de l'utilisateur
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "library", nullable = false)
    private Library library;

    /**
     * Projects associées
     */
    @ManyToMany(mappedBy = "associatedUsers", fetch = FetchType.LAZY)
    private Set<Project> projects;

    /**
     * Rôle de l'utilisateur
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "role", nullable = false)
    private Role role;

    @JsonIgnore
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "user")
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    private Set<PersistentToken> persistentTokens = new HashSet<>();

    /**
     * Catégorie de l'utilisateur
     */
    @Column(name = "category")
    @Enumerated(EnumType.STRING)
    private Category category;

    /**
     * Tableau de bord de l'usager
     */
    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "dashboard")
    private Dashboard dashboard;

    /**
     * Langue de l'usager
     */
    @Column(name = "lang")
    @Enumerated(EnumType.STRING)
    private Lang lang;

    @ManyToMany(mappedBy = "users")
    private Set<WorkflowGroup> groups;

    @Transient
    @Field(type = FieldType.String, analyzer = ANALYZER_KEYWORD)
    private String fullName;

    public void setAddress(final Address address) {
        this.address = address;
    }

    public Address getAddress() {
        return address;
    }

    public String getFunction() {
        return function;
    }

    public void setFunction(String function) {
        this.function = function;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public Library getLibrary() {
        return library;
    }

    public void setLibrary(Library library) {
        this.library = library;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Set<PersistentToken> getPersistentTokens() {
        return persistentTokens;
    }

    public void setPersistentTokens(Set<PersistentToken> persistentTokens) {
        this.persistentTokens = persistentTokens;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public Lang getLang() {
        return lang;
    }

    public void setLang(Lang lang) {
        this.lang = lang;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Set<Project> getProjects() {
        return projects;
    }

    public void setProjects(Set<Project> projects) {
        this.projects = projects;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this).omitNullValues().add("login", login).add("firstname", firstname).add("surname", surname).toString();
    }

    public void setSuperuser(boolean superuser) {
        this.superuser = superuser;
    }

    public boolean isSuperuser() {
        return superuser;
    }

    public Set<WorkflowGroup> getGroups() {
        return groups;
    }

    /**
     * Type d'utilisateur
     */
    public enum Category {
        /**
         * Prestataire
         */
        PROVIDER,
        /**
         * Autre
         */
        OTHER
    }

    public String getFullName() {
        final StringBuilder builder = new StringBuilder();

        if (StringUtils.isNotBlank(firstname)) {
            builder.append(firstname);
        }
        if (StringUtils.isNotBlank(surname)) {
            if (builder.length() > 0) {
                builder.append(' ');
            }
            builder.append(surname);
        }
        // Pour les prestataires, on ajoute le nom de la société, et on met le nom entre parenthèses
        if (StringUtils.isNotBlank(companyName) && category == Category.PROVIDER) {
            if (builder.length() > 0) {
                builder.insert(0, " (").insert(0, companyName).append(')');
            }
        }
        return builder.toString();
    }

    public Dashboard getDashboard() {
        return dashboard;
    }

    public void setDashboard(final Dashboard dashboard) {
        this.dashboard = dashboard;
    }
}
