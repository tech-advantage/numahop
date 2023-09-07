package fr.progilone.pgcn.repository.administration.omeka;

import fr.progilone.pgcn.domain.administration.omeka.OmekaConfiguration;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface OmekaConfigurationRepositoryCustom {

    Page<OmekaConfiguration> search(String search, final List<String> libraries, final Boolean omekas, Pageable pageable);
}
