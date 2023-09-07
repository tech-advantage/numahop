package fr.progilone.pgcn.repository.document.conditionreport;

import fr.progilone.pgcn.domain.document.conditionreport.DescriptionProperty;
import fr.progilone.pgcn.domain.document.conditionreport.PropertyConfiguration;
import fr.progilone.pgcn.domain.library.Library;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface PropertyConfigurationRepository extends JpaRepository<PropertyConfiguration, String> {

    @Query("select c " + "from PropertyConfiguration c "
           + "join fetch c.library "
           + "left join fetch c.descProperty "
           + "where c.identifier = ?1")
    PropertyConfiguration findWithDependencies(String identifier);

    List<PropertyConfiguration> findByLibrary(Library library);

    List<PropertyConfiguration> findByDescPropertyAndLibrary(DescriptionProperty property, Library library);

    List<PropertyConfiguration> findByInternalPropertyAndLibrary(PropertyConfiguration.InternalProperty property, Library library);

    @Modifying
    void deleteByLibrary(Library library);
}
