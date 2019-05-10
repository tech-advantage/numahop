package fr.progilone.pgcn.exception.message;

import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;

import java.util.ArrayList;
import java.util.List;

/**
 * Classe d'erreur permettant de fournir un code d'erreur à l'IHM ainsi qu'un message d'erreur affiché dans la log. Il
 * est également possible de fournir des compléments qui pourront être injectés dans le message à destination de
 * l'utilisateur.
 *
 * @author David
 */
public final class PgcnError {

    private final PgcnErrorCode code;
    private final String field;
    private final List<String> complements;
    private final Object additionnalComplements;
    private final String message;

    private PgcnError(final PgcnErrorCode code,
                             final String field,
                             final String message,
                             final List<String> complements,
                             final Object additionnalComplements) {
        super();
        this.code = code;
        this.field = field;
        this.message = message;
        this.complements = complements;
        this.additionnalComplements = additionnalComplements;
    }

    public PgcnErrorCode getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public List<String> getComplements() {
        return complements;
    }

    public Object getAdditionnalComplements() {
        return additionnalComplements;
    }

    public String getField() {
        return field;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this).omitNullValues().addValue(code).addValue(field).addValue(message).toString();
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(code);
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (this.getClass().isInstance(obj)) {
            final PgcnError other = (PgcnError) obj;
            return Objects.equal(code, other.code) && Objects.equal(field, other.field) && Objects.equal(message, other.message);
        }
        return false;
    }

    /**
     * Builder pour la classe {@link PgcnError}
     *
     * @author David
     */
    public static final class Builder {

        private PgcnErrorCode code;
        private String message;
        private String field;
        private Object additionnalComplements;
        private final List<String> complements = new ArrayList<>();

        public Builder setCode(final PgcnErrorCode code) {
            this.code = code;
            return this;
        }

        public Builder setField(final String field) {
            this.field = field;
            return this;
        }

        public Builder setMessage(final String message) {
            this.message = message;
            return this;
        }

        public Builder addComplement(final String complement) {
            this.complements.add(complement);
            return this;
        }

        public Builder setAdditionalComplement(final Object complements) {
            this.additionnalComplements = complements;
            return this;
        }

        public PgcnError build() {
            return new PgcnError(code, field, message, complements, additionnalComplements);
        }

        public Builder reinit() {
            this.code = null;
            this.message = null;
            this.field = null;
            this.complements.clear();
            this.additionnalComplements = null;
            return this;
        }
    }
}
