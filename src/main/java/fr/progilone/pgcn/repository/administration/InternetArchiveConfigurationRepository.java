package fr.progilone.pgcn.repository.administration;

import fr.progilone.pgcn.domain.administration.InternetArchiveConfiguration;
import fr.progilone.pgcn.domain.library.Library;
import java.util.Set;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

/**
 *
 * @author jbrunet
 *         Créé le 19 avr. 2017
 */
public interface InternetArchiveConfigurationRepository extends JpaRepository<InternetArchiveConfiguration, String>, InternetArchiveConfigurationRepositoryCustom {

    @Query("select distinct c " + "from InternetArchiveConfiguration c "
           + "join fetch c.library ")
    Set<InternetArchiveConfiguration> findAllWithDependencies();

    @Query("select distinct c " + "from InternetArchiveConfiguration c "
           + "join fetch c.library "
           + "where c.active = ?1")
    Set<InternetArchiveConfiguration> findByActiveWithDependencies(boolean active);

    @Query("select distinct c " + "from InternetArchiveConfiguration c "
           + "join fetch c.library l "
           + "where l = ?1")
    Set<InternetArchiveConfiguration> findByLibrary(Library library);

    @Query("select distinct c " + "from InternetArchiveConfiguration c "
           + "join fetch c.library l "
           + "where l = ?1 "
           + "and c.active = ?2")
    Set<InternetArchiveConfiguration> findByLibraryAndActive(Library library, boolean active);

    @Query("select c " + "from InternetArchiveConfiguration c "
           + "join fetch c.library "
           + "left join fetch c.collections "
           + "where c.identifier = ?1")
    InternetArchiveConfiguration findOneWithDependencies(String id);

    @Query("select c.secretKey from InternetArchiveConfiguration c where c.identifier = ?1")
    String findSecretKeyByIdentifier(String identifier);

    @Modifying
    void deleteByLibrary(Library library);
}
