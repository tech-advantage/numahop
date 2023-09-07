package fr.progilone.pgcn.domain;

import com.google.common.base.MoreObjects;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.Objects;
import org.hibernate.annotations.Immutable;

@Entity
@Table(name = Lock.TABLE_NAME)
@Immutable
public class Lock {

    public static final String TABLE_NAME = "app_lock";

    /**
     * Identifiant de l'objet verrouillé
     */
    @Id
    private String identifier;

    /**
     * Classe de l'objet verrouillé
     */
    @Column(name = "class", updatable = false, nullable = false)
    private String clazz;

    /**
     * Utilisateur ayant verrouillé l'objet
     */
    @Column(name = "locked_by", updatable = false, nullable = false)
    private String lockedBy;

    /**
     * Date de verrouillage de l'objet
     */
    @Column(name = "locked_date", updatable = false, nullable = false)
    private LocalDateTime lockedDate = LocalDateTime.now();

    /**
     * Constructeur pour JPA
     */
    private Lock() {
    }

    /**
     * Constructeur
     *
     * @param entity
     *            identifiant de l'entité
     * @param lockedBy
     *            login de l'usager
     */
    public Lock(final String entity, final String lockedBy, final String clazz) {
        this(entity, lockedBy, LocalDateTime.now(), clazz);
    }

    public Lock(final String entity, final String lockedBy) {
        this(entity, lockedBy, LocalDateTime.now(), null);
    }

    /**
     * Constructeur
     *
     * @param entity
     *            identifiant de l'entité
     * @param lockedBy
     *            login de l'usager
     * @param lockedDate
     *            Date du lock
     */
    public Lock(final String entity, final String lockedBy, final LocalDateTime lockedDate, final String clazz) {
        this.identifier = entity;
        this.lockedBy = lockedBy;
        // LocalDateTime est immuable pas besoin de faire de copie défensive
        this.lockedDate = lockedDate;
        this.clazz = clazz;
    }

    /**
     * Constructeur !! TEST ONLY !!
     *
     * @param entity
     *            identifiant de l'entité
     * @param lockedBy
     *            login de l'usager
     */
    public Lock(final String entity, final String lockedBy, final LocalDateTime lockedDate) {
        this(entity, lockedBy, lockedDate, null);
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(final String identifier) {
        this.identifier = identifier;
    }

    public String getClazz() {
        return clazz;
    }

    public String getLockedBy() {
        return lockedBy;
    }

    public LocalDateTime getLockedDate() {
        return lockedDate;
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
        return MoreObjects.toStringHelper(this).add("identifier", identifier).add("clazz", clazz).add("lockedBy", lockedBy).add("lockedDate", lockedDate).toString();
    }
}
