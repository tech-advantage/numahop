package fr.progilone.pgcn.domain.user;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.google.common.base.MoreObjects;
import fr.progilone.pgcn.domain.AbstractDomainObject;
import fr.progilone.pgcn.domain.administration.Module;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import java.util.HashSet;
import java.util.Set;

/**
 * Habilitation
 */
@Entity
@Table(name = Authorization.TABLE_NAME)
@JsonSubTypes({@JsonSubTypes.Type(name = "authorization", value = Authorization.class)})
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class Authorization extends AbstractDomainObject {

    public static final String TABLE_NAME = "user_authorization";

    @Column(name = "code", updatable = false, nullable = false, unique = true)
    private String code;

    @Column(name = "label", unique = true)
    private String label;

    @Column(name = "description")
    private String description;

    @Column(name = "module")
    @Enumerated(EnumType.STRING)
    private Module module;

    @ManyToMany(mappedBy = "authorizations", fetch = FetchType.LAZY, cascade = {CascadeType.REMOVE})
    private final Set<Role> roles = new HashSet<>();

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

    public Module getModule() {
        return module;
    }

    public void setModule(final Module module) {
        this.module = module;
    }

    public Set<Role> getRoles() {
        return roles;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                          .omitNullValues()
                          .add("identifier", identifier)
                          .add("code", code)
                          .add("label", label)
                          .add("module", module)
                          .toString();
    }
}
