package fr.progilone.pgcn.repository.user;

import fr.progilone.pgcn.domain.user.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface RoleRepository extends JpaRepository<Role, String>, RoleRepositoryCustom {

    @Query("from Role r "
           + "left join fetch r.authorizations "
           + "where r.identifier = ?1")
    public Role findOneWithAuthorizations(String identifier);

    @Query("select distinct r "
           + "from Role r "
           + "left join fetch r.authorizations "
           + "where r.superuser is false")
    public List<Role> findAllWithAuthorizations();

    @Override
    @Query("select distinct r "
           + "from Role r "
           + "where r.superuser is false")
    public List<Role> findAll();

    Role findByCode(String code);

    Role findByCodeAndIdentifierNot(String code, String identifier);

    Role findOneByLabel(String label);

    Role findOneByLabelAndIdentifierNot(String label, String identifier);
}
