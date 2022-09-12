package fr.progilone.pgcn.repository.exportftpconfiguration;

import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Service;

import fr.progilone.pgcn.domain.exportftpconfiguration.ExportFTPConfiguration;
import fr.progilone.pgcn.domain.library.Library;

@Service
public interface ExportFTPConfigurationRepository extends JpaRepository<ExportFTPConfiguration, String>, ExportFTPConfigurationRepositoryCustom {

    @Query("select c "
            + "from ExportFTPConfiguration c "
            + "join fetch c.library "
            + "where c.identifier = ?1")
    ExportFTPConfiguration findOneWithDependencies(String identifier);

    @Query("select distinct c "
            + "from ExportFTPConfiguration c "
            + "join fetch c.library l "
            + "where l = ?1 "
            + "and c.active = ?2")
    Set<ExportFTPConfiguration> findByLibraryAndActive(Library library, boolean active);

     @Query("select c.password from ExportFTPConfiguration c where c.identifier = ?1")
     String findPasswordByIdentifier(String identifier);
}
