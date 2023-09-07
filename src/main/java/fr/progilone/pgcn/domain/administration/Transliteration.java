package fr.progilone.pgcn.domain.administration;

import com.google.common.base.MoreObjects;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;
import java.io.Serializable;
import java.util.Objects;

/**
 * Created by Sébastien on 29/06/2017.
 */
@Entity
@Table(name = Transliteration.TABLE_NAME)
@IdClass(Transliteration.TransliterationId.class)
public class Transliteration {

    public static final String TABLE_NAME = "conf_transliteration";

    public enum Type {
        FUNCTION
    }

    @Id
    @Column(name = "type", nullable = false)
    @Enumerated(EnumType.STRING)
    private Type type;

    @Id
    @Column(name = "code", nullable = false, unique = true)
    private String code;

    @Column(name = "value")
    private String value;

    public Type getType() {
        return type;
    }

    public void setType(final Type type) {
        this.type = type;
    }

    public String getCode() {
        return code;
    }

    public void setCode(final String code) {
        this.code = code;
    }

    public String getValue() {
        return value;
    }

    public void setValue(final String value) {
        this.value = value;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final Transliteration that = (Transliteration) o;
        return type == that.type && Objects.equals(code, that.code)
               && Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, code, value);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this).add("type", type).add("code", code).add("value", value).toString();
    }

    /**
     * {@link jakarta.persistence.IdClass} pour les {@link Transliteration}
     */
    public static class TransliterationId implements Serializable {

        private Type type;
        private String code;

        public TransliterationId() {
        }

        public TransliterationId(final Type type, final String code) {
            this.type = type;
            this.code = code;
        }

        public Type getType() {
            return type;
        }

        public void setType(final Type type) {
            this.type = type;
        }

        public String getCode() {
            return code;
        }

        public void setCode(final String code) {
            this.code = code;
        }

        @Override
        public boolean equals(final Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            final TransliterationId that = (TransliterationId) o;
            return type == that.type && Objects.equals(code, that.code);
        }

        @Override
        public int hashCode() {
            return Objects.hash(type, code);
        }

        @Override
        public String toString() {
            return MoreObjects.toStringHelper(this).add("type", type).add("code", code).toString();
        }
    }
}
