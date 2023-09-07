package fr.progilone.pgcn.repository.administration;

import fr.progilone.pgcn.domain.administration.SftpConfiguration;
import fr.progilone.pgcn.domain.library.Library;
import java.util.Set;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

/**
 * Created by Sébastien on 30/12/2016.
 */
public interface SftpConfigurationRepository extends JpaRepository<SftpConfiguration, String>, SftpConfigurationRepositoryCustom {

    @Query("select distinct c " + "from SftpConfiguration c "
           + "join fetch c.library ")
    Set<SftpConfiguration> findAllWithDependencies();

    @Query("select distinct c " + "from SftpConfiguration c "
           + "join fetch c.library "
           + "where c.active = ?1")
    Set<SftpConfiguration> findByActiveWithDependencies(boolean active);

    @Query("select distinct c " + "from SftpConfiguration c "
           + "join fetch c.library l "
           + "where l = ?1")
    Set<SftpConfiguration> findByLibrary(Library library);

    @Query("select distinct c " + "from SftpConfiguration c "
           + "join fetch c.library l "
           + "where l = ?1 "
           + "and c.active = ?2")
    Set<SftpConfiguration> findByLibraryAndActive(Library library, boolean active);

    @Query("select c " + "from SftpConfiguration c "
           + "join fetch c.library "
           + "where c.identifier = ?1")
    SftpConfiguration findOneWithDependencies(String id);

    @Query("select c.password from SftpConfiguration c where c.identifier = ?1")
    String findPasswordByIdentifier(String identifier);

    @Modifying
    void deleteByLibrary(Library library);
}
