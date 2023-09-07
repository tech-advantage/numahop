package fr.progilone.pgcn.repository.ocrlangconfiguration;

import fr.progilone.pgcn.domain.ocrlangconfiguration.OcrLangConfiguration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface OcrLangConfigurationRepository extends JpaRepository<OcrLangConfiguration, String>, OcrLangConfigurationRepositoryCustom {

    @Query("select c " + "from OcrLangConfiguration c "
           + "join fetch c.library "
           + "where c.identifier = ?1")
    OcrLangConfiguration findOneWithDependencies(String identifier);

}
