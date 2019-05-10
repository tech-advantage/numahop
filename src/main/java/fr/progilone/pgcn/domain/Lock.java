package fr.progilone.pgcn.domain;

import com.google.common.base.MoreObjects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = Lock.TABLE_NAME)
public class Lock {

    public static final String TABLE_NAME = "app_lock";

    /**
     * Identifiant de l'objet vérouillé
     */
    @Id
    private String identifier;

    /**
     * Classe de l'objet vérouillé
     */
    @Column(name = "class", updatable = false, nullable = false)
    private String clazz;

    /**
     * Utilisateur ayant vérouillé l'objet
     */
    @Column(name = "locked_by", updatable = false, nullable = false)
    private String lockedBy;

    /**
     * Date de vérouillage de l'objet
     */
    @Column(name = "locked_date", updatable = false, nullable = false)
    private LocalDateTime lockedDate = LocalDateTime.now();

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(final String identifier) {
        this.identifier = identifier;
    }

    public String getClazz() {
        return clazz;
    }

    public void setClazz(final String clazz) {
        this.clazz = clazz;
    }

    public String getLockedBy() {
        return lockedBy;
    }

    public void setLockedBy(final String lockedBy) {
        this.lockedBy = lockedBy;
    }

    public LocalDateTime getLockedDate() {
        return lockedDate;
    }

    public void setLockedDate(final LocalDateTime lockedDate) {
        this.lockedDate = lockedDate;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final Lock lock = (Lock) o;
        return Objects.equals(identifier, lock.identifier);
    }

    @Override
    public int hashCode() {
        return Objects.hash(identifier);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                          .add("identifier", identifier)
                          .add("clazz", clazz)
                          .add("lockedBy", lockedBy)
                          .add("lockedDate", lockedDate)
                          .toString();
    }
}
