package fr.progilone.pgcn.repository.user;

import fr.progilone.pgcn.domain.user.Authorization;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface AuthorizationRepository extends JpaRepository<Authorization, String> {

    @Query("from Authorization a " + "left join fetch a.roles "
           + "where a.identifier = ?1")
    public Authorization findOneWithRoles(String identifier);

    @Query("select distinct a " + "from Authorization a "
           + "left join fetch a.roles ")
    public List<Authorization> findAllWithRoles();
}
