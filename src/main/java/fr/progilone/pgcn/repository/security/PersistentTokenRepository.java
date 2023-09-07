package fr.progilone.pgcn.repository.security;

import fr.progilone.pgcn.domain.security.PersistentToken;
import java.util.Date;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

/**
 * Spring Data JPA repository for the PersistentToken entity.
 */
public interface PersistentTokenRepository extends JpaRepository<PersistentToken, String> {

    List<PersistentToken> findByTokenDateBefore(Date date);

    @Query("select t from PersistentToken t left join fetch t.user")
    List<PersistentToken> findByAllWithUser();

}
