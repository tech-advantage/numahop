package fr.progilone.pgcn.repository.administration.viewsformat;

import fr.progilone.pgcn.domain.administration.viewsformat.ViewsFormatConfiguration;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ViewsFormatConfigurationRepositoryCustom {

    Page<ViewsFormatConfiguration> search(String search, List<String> libraries, Pageable pageable);
}
