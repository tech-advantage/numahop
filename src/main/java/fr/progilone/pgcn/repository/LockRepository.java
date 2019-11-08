package fr.progilone.pgcn.repository;

import java.time.LocalDateTime;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;

import fr.progilone.pgcn.domain.Lock;

public interface LockRepository extends JpaRepository<Lock, String> {
    
    
    /**
     * Récupération d'un verrou sur une ressource
     *
     * @param entity identifiant de la ressource
     * @return le lock si il existe.
     */
    Lock findByIdentifier(final String entity);

    @Modifying
    void deleteByLockedDateBefore(LocalDateTime lockedDate);
    
    @Modifying
    void deleteByLockedBy(String lockedBy);
    
    @Modifying
    void deleteByIdentifier(final String entity);
}
