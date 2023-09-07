package fr.progilone.pgcn.repository.administration.digitallibrary;

import fr.progilone.pgcn.domain.administration.digitallibrary.DigitalLibraryConfiguration;
import fr.progilone.pgcn.domain.library.Library;
import java.util.List;
import java.util.Set;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface DigitalLibraryConfigurationRepository extends JpaRepository<DigitalLibraryConfiguration, String>, DigitalLibraryConfigurationRepositoryCustom {

    Set<DigitalLibraryConfiguration> findByLibrary(String libraryId);

    Set<DigitalLibraryConfiguration> findByLibraryAndActive(Library library, boolean active);

    @Query("SELECT conf FROM DigitalLibraryConfiguration conf where active = ?1")
    List<DigitalLibraryConfiguration> findAll(boolean active);

    @Query("SELECT conf " + "FROM DigitalLibraryConfiguration conf "
           + "JOIN FETCH conf.library "
           + "where conf.identifier = ?1")
    DigitalLibraryConfiguration findOneWithDependencies(String identifier);

    @Query("SELECT conf.password " + "FROM DigitalLibraryConfiguration conf "
           + "where conf.identifier = ?1")
    String findPasswordByIdentifier(String identifier);
}
