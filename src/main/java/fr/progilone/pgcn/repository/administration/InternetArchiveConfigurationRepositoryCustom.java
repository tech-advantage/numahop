package fr.progilone.pgcn.repository.administration;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import fr.progilone.pgcn.domain.administration.InternetArchiveConfiguration;

public interface InternetArchiveConfigurationRepositoryCustom {

    Page<InternetArchiveConfiguration> search(String search,
                                  final List<String> libraries,
                                  Pageable pageable);
}
