package fr.progilone.pgcn.repository.ftpconfiguration;

import fr.progilone.pgcn.domain.ftpconfiguration.FTPConfiguration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Service;

@Service
public interface FTPConfigurationRepository extends JpaRepository<FTPConfiguration, String>, FTPConfigurationRepositoryCustom {

    @Query("select c "
           + "from FTPConfiguration c "
           + "join fetch c.library "
           + "where c.identifier = ?1")
    FTPConfiguration findOneWithDependencies(String identifier);

    @Query("select c.password from FTPConfiguration c where c.identifier = ?1")
    String findPasswordByIdentifier(String identifier);
}
