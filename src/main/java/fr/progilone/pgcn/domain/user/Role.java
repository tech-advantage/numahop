package fr.progilone.pgcn.domain.user;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.google.common.base.MoreObjects;
import fr.progilone.pgcn.domain.AbstractDomainObject;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import java.util.HashSet;
import java.util.Set;

/**
 * Rôle
 */
@Entity
@Table(name = Role.TABLE_NAME)
@JsonSubTypes({@JsonSubTypes.Type(name = "role", value = Role.class)})
public class Role extends AbstractDomainObject {

    public static final String TABLE_NAME = "user_role";
    public static final String TABLE_NAME_ROLE_AUTHORIZATION = "user_role_authorization";

    @Column(name = "code", updatable = false, unique = true)
    @NotNull
    private String code;

    @Column(name = "label", unique = true)
    private String label;

    @Column(name = "description")
    private String description;

    @Column(name = "superuser")
    private Boolean superuser;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = TABLE_NAME_ROLE_AUTHORIZATION,
               joinColumns = {@JoinColumn(name = "role", referencedColumnName = "identifier")},
               inverseJoinColumns = {@JoinColumn(name = "user_authorization", referencedColumnName = "identifier")})
    private final Set<Authorization> authorizations = new HashSet<>();

    public String getCode() {
        return code;
    }

    public void setCode(final String code) {
        this.code = code;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(final String label) {
        this.label = label;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(final String description) {
        this.description = description;
    }

    public Set<Authorization> getAuthorizations() {
        return authorizations;
    }

    public void setAuthorizations(final Set<Authorization> authorizations) {
        this.authorizations.clear();
        if (authorizations != null) {
            authorizations.forEach(this::addAuthorization);
        }
    }

    public void addAuthorization(final Authorization authorization) {
        if (authorization != null) {
            authorizations.add(authorization);
        }
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this).omitNullValues().add("identifier", identifier).add("code", code).add("label", label).toString();
    }

    public Boolean getSuperuser() {
        return superuser;
    }

    public void setSuperuser(Boolean superuser) {
        this.superuser = superuser;
    }
}
