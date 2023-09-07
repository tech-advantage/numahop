package fr.progilone.pgcn.repository.administration.viewsformat;

import fr.progilone.pgcn.domain.administration.viewsformat.ViewsFormatConfiguration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ViewsFormatConfigurationRepository extends JpaRepository<ViewsFormatConfiguration, String>, ViewsFormatConfigurationRepositoryCustom {

    @Query("select c " + "from ViewsFormatConfiguration c "
           + "join fetch c.library "
           + "where c.identifier = ?1")
    ViewsFormatConfiguration findOneWithDependencies(String identifier);

}
