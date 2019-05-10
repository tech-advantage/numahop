package fr.progilone.pgcn.repository.user;

import java.util.Collection;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import fr.progilone.pgcn.domain.library.Library;
import fr.progilone.pgcn.domain.user.Role;
import fr.progilone.pgcn.domain.user.User;

public interface UserRepository extends JpaRepository<User, String>, UserRepositoryCustom {

    @Query("select u.identifier from User u")
    List<String> findAllIdentifiers();

    @Query("select u.identifier from User u where lower(u.firstname) like %?1% or lower(u.surname) like %?1%")
    Page<String> findAllIdentifiers(String filter, Pageable pageable);

    @Query("select distinct u "
           + "from User u "
           + "left join fetch u.role r "
           + "left join fetch u.library "
           + "left join fetch r.authorizations "
           + "where u.identifier in ?1 ")
    List<User> findByIdentifierIn(Iterable<String> ids, Sort sort);

    @Query("from User u "
           + "left join fetch u.address "
           + "where u.identifier = ?1")
    User findOneWithDependenciesForChangeRequest(String identifier);

    @Query("from User u "
            + "left join fetch u.library "
            + "where u.identifier = ?1")
     User findOneWithLibrary(String identifier);
    
    @Query("from User u "
           + "left join fetch u.address "
           + "left join fetch u.library "
           + "left join fetch u.role r "
           + "left join fetch r.authorizations "
           + "where u.identifier = ?1")
    User findOneWithDependencies(String identifier);

    @Query("select distinct u "
           + "from User u "
           + "left join fetch u.address "
           + "left join fetch u.library "
           + "left join fetch u.role r "
           + "left join fetch r.authorizations "
           + "where u.id in ?1")
    List<User> findAllWithDependencies(final Collection<String> ids);

    @Query("from User u where u.login = ?1")
    User findByLogin(String login);

    @Query("from User u where u.login in ?1")
    List<User> findByLoginIn(List<String> logins);

    @Query("from User u "
           + "left join fetch u.role r "
           + "left join fetch r.authorizations "
           //           + "left join fetch u.lang " FIXME
           + "left join fetch u.dashboard "
           + "where u.identifier = ?1")
    User findOneWithRoleAndAuthorizations(String identifier);

    @Query("select u.password from User u where u.identifier = ?1")
    String findPasswordByIdentifier(String identifier);

    User findOneByIdentifier(String identifier);

    List<User> findAllByActive(boolean active);

    List<User> findAllByActiveAndCategory(boolean active, User.Category category);

    Collection<User> findAllByCategoryAndLibraryIdentifier(User.Category provider, String id);

    Collection<User> findAllByLibraryIdentifier(String id);

    Long countByLibrary(Library library);

    Long countByRole(Role role);

    @Query("select distinct u "
            + "from User u "
            + "left join fetch u.address "
            + "left join fetch u.library "
            + "left join fetch u.role r "
            + "left join fetch r.authorizations "
            + "left join fetch u.groups "
            + "where u.id in ?1")
    User findOneWithDependenciesAndGroups(String identifier);
}
