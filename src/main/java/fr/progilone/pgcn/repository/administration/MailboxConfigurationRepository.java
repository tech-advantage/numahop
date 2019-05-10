package fr.progilone.pgcn.repository.administration;

import fr.progilone.pgcn.domain.administration.MailboxConfiguration;
import fr.progilone.pgcn.domain.library.Library;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.Set;

/**
 * Created by Sébastien on 30/12/2016.
 */
public interface MailboxConfigurationRepository extends JpaRepository<MailboxConfiguration, String>, MailboxConfigurationRepositoryCustom {

    @Query("select distinct c "
           + "from MailboxConfiguration c "
           + "join fetch c.library ")
    Set<MailboxConfiguration> findAllWithDependencies();

    @Query("select distinct c "
           + "from MailboxConfiguration c "
           + "join fetch c.library "
           + "where c.active = ?1")
    Set<MailboxConfiguration> findByActiveWithDependencies(boolean active);

    @Query("select distinct c "
           + "from MailboxConfiguration c "
           + "join fetch c.library l "
           + "where l = ?1")
    Set<MailboxConfiguration> findByLibrary(Library library);

    @Query("select distinct c "
           + "from MailboxConfiguration c "
           + "join fetch c.library l "
           + "where l = ?1 "
           + "and c.active = ?2")
    Set<MailboxConfiguration> findByLibraryAndActive(Library library, boolean active);

    @Query("select c "
           + "from MailboxConfiguration c "
           + "join fetch c.library "
           + "where c.identifier = ?1")
    MailboxConfiguration findOneWithDependencies(String id);

    @Query("select c.password from MailboxConfiguration c where c.identifier = ?1")
    String findPasswordByIdentifier(String identifier);

    @Modifying
    void deleteByLibrary(Library library);
}
