package fr.progilone.pgcn.repository.administration.omeka;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import fr.progilone.pgcn.domain.administration.omeka.OmekaConfiguration;

public interface OmekaConfigurationRepositoryCustom {

    Page<OmekaConfiguration> search(String search,
                                  final List<String> libraries,
                                  Pageable pageable);
}
