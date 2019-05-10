package fr.progilone.pgcn.repository.administration.viewsformat;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import fr.progilone.pgcn.domain.administration.viewsformat.ViewsFormatConfiguration;

public interface ViewsFormatConfigurationRepositoryCustom {

    
    Page<ViewsFormatConfiguration> search(String search,
                                    List<String> libraries,
                                    Pageable pageable);
}
