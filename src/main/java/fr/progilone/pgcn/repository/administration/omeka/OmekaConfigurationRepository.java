package fr.progilone.pgcn.repository.administration.omeka;

import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import fr.progilone.pgcn.domain.administration.omeka.OmekaConfiguration;
import fr.progilone.pgcn.domain.library.Library;

public interface OmekaConfigurationRepository extends JpaRepository<OmekaConfiguration, String>, OmekaConfigurationRepositoryCustom {

    @Query("select distinct c "
            + "from OmekaConfiguration c "
            + "join fetch c.library ")
     Set<OmekaConfiguration> findAllWithDependencies();

     @Query("select distinct c "
            + "from OmekaConfiguration c "
            + "join fetch c.library "
            + "where c.active = ?1")
     Set<OmekaConfiguration> findByActiveWithDependencies(boolean active);

     @Query("select distinct c "
            + "from OmekaConfiguration c "
            + "join fetch c.library l "
            + "where l = ?1")
     Set<OmekaConfiguration> findByLibrary(Library library);

     @Query("select distinct c "
            + "from OmekaConfiguration c "
            + "join fetch c.library l "
            + "where l = ?1 "
            + "and c.active = ?2")
     Set<OmekaConfiguration> findByLibraryAndActive(Library library, boolean active);

     @Query("select c "
            + "from OmekaConfiguration c "
            + "join fetch c.library l "
            + "left join fetch c.omekaLists "
            + "where c.identifier = ?1")
     OmekaConfiguration findOneWithDependencies(String id);

     @Modifying
     void deleteByLibrary(Library library);

}
