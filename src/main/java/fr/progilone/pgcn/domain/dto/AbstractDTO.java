package fr.progilone.pgcn.domain.dto;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.voodoodyne.jackson.jsog.JSOGGenerator;
import fr.progilone.pgcn.domain.ObjectWithErrors;
import fr.progilone.pgcn.domain.user.Authorization;
import fr.progilone.pgcn.exception.message.PgcnError;
import fr.progilone.pgcn.exception.message.PgcnList;
import java.util.Collection;
import java.util.Objects;

/**
 * DTO gérant une collection d'erreurs
 *
 * @author Sebastien
 * @see Authorization
 */
@JsonIdentityInfo(generator = JSOGGenerator.class)
public class AbstractDTO implements ObjectWithErrors {

    @JsonIgnore
    private PgcnList<PgcnError> errors;

    @Override
    @JsonProperty("errors")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public Collection<PgcnError> getErrorsAsList() {
        return errors != null ? errors.get()
                              : null;
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

    public void addErrors(final PgcnList<PgcnError> errors) {
        if (this.errors == null) {
            this.errors = new PgcnList<>();
        }
        this.errors.addAll(errors);
    }

    @Override
    @JsonIgnore
    public void setErrors(final PgcnList<PgcnError> errors) {
        this.errors = errors;
    }

    @JsonIgnore
    protected Object getUniqueIdentifier() {
        return null;
    }

    @Override
    public int hashCode() {
        if (getUniqueIdentifier() != null) {
            return Objects.hashCode(getUniqueIdentifier());
        } else {
            return super.hashCode();
        }
    }

    @Override
    public boolean equals(final Object obj) {
        if (getUniqueIdentifier() == null) {
            return obj == this;
        }
        if (obj == this) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getUniqueIdentifier() != null) {
            if (this.getClass().isInstance(obj)) {
                final AbstractDTO other = (AbstractDTO) obj;
                return Objects.equals(getUniqueIdentifier(), other.getUniqueIdentifier());
            }
        }
        return false;
    }
}
