package fr.progilone.pgcn.domain;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.voodoodyne.jackson.jsog.JSOGGenerator;
import fr.progilone.pgcn.exception.message.PgcnError;
import fr.progilone.pgcn.exception.message.PgcnList;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldIndex;
import org.springframework.data.elasticsearch.annotations.FieldType;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.Column;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.Transient;
import javax.persistence.Version;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Objects;

/**
 * Classe mère de tous les objets du domaine
 */
@JsonIdentityInfo(generator = JSOGGenerator.class)
@EntityListeners(AuditingEntityListener.class)
@MappedSuperclass
public abstract class AbstractDomainObject implements Serializable, ObjectWithErrors {

    /**
     * Identifiant
     */
    @Id
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name = "system-uuid", strategy = "fr.progilone.pgcn.domain.util.CustomUUIDGenerator")
    @org.springframework.data.annotation.Id
    @Field(type = FieldType.String, index = FieldIndex.not_analyzed)
    protected String identifier;

    /**
     * Version
     */
    @Version
    private long version;

    /**
     * Login de l'utilisateur ayant créé l'entité
     */
    @CreatedBy
    @Column(name = "created_by", updatable = false, nullable = false)
    @JsonIgnore
    private String createdBy;

    /**
     * Login de l'utilisateur ayant modifié en dernier l'entité
     */
    @LastModifiedBy
    @Column(name = "last_modified_by", nullable = false)
    @JsonIgnore
    private String lastModifiedBy;

    /**
     * Date de création de l'entité
     */
    @CreatedDate
    @Column(name = "created_date", updatable = false, nullable = false)
    @Field(type = FieldType.Date)
    @JsonIgnore
    private LocalDateTime createdDate = LocalDateTime.now();

    /**
     * Date de dernière modification de l'entité
     */
    @LastModifiedDate
    @Column(name = "last_modified_date", nullable = false)
    @Field(type = FieldType.Date)
    @JsonIgnore
    private LocalDateTime lastModifiedDate = LocalDateTime.now();

    @Transient
    private PgcnList<PgcnError> errors;

    public long getVersion() {
        return version;
    }

    public void setVersion(final long version) {
        this.version = version;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(final String identifier) {
        this.identifier = identifier;
    }

    @Override
    @JsonProperty("errors")
    @JsonInclude(Include.NON_NULL)
    public Collection<PgcnError> getErrorsAsList() {
        return errors != null ? errors.get() : null;
    }

    @Override
    public PgcnList<PgcnError> getErrors() {
        return errors;
    }

    @Override
    public void addError(final PgcnError error) {
        if (this.errors == null) {
            this.errors = new PgcnList<>();
        }
        this.errors.add(error);
    }

    @Override
    @JsonIgnore
    public void setErrors(final PgcnList<PgcnError> errors) {
        this.errors = errors;
    }

    // fake setter pour la désérialisation json
    @JsonProperty("errors")
    @JsonDeserialize
    private void setErrors(final Collection<?> errors) {
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getIdentifier());
    }

    @Override
    public boolean equals(final Object obj) {
        if (getIdentifier() == null) {
            return obj == this;
        }
        if (obj == this) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (this.getClass().isInstance(obj)) {
            final AbstractDomainObject other = (AbstractDomainObject) obj;
            return Objects.equals(getIdentifier(), other.getIdentifier());
        }
        return false;
    }

    @Override
    public String toString() {
        return this.getClass().getName() + " [identifier=" + identifier + "]";
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(final String createdBy) {
        this.createdBy = createdBy;
    }

    @JsonProperty("createdDate")
    public LocalDateTime getCreatedDate() {
        return createdDate;
    }

    @JsonIgnore
    public void setCreatedDate(final LocalDateTime createdDate) {
        this.createdDate = createdDate;
    }

    public String getLastModifiedBy() {
        return lastModifiedBy;
    }

    public void setLastModifiedBy(final String lastModifiedBy) {
        this.lastModifiedBy = lastModifiedBy;
    }

    @JsonProperty("lastModifiedDate")
    public LocalDateTime getLastModifiedDate() {
        return lastModifiedDate;
    }

    @JsonIgnore
    public void setLastModifiedDate(final LocalDateTime lastModifiedDate) {
        this.lastModifiedDate = lastModifiedDate;
    }

}
