package fr.progilone.pgcn.repository;

import fr.progilone.pgcn.domain.Lock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;

import java.time.LocalDateTime;

public interface LockRepository extends JpaRepository<Lock, String> {

    @Modifying
    void deleteByLockedDateBefore(LocalDateTime lockedDate);
    
    @Modifying
    void deleteByLockedBy(String lockedBy);
}
