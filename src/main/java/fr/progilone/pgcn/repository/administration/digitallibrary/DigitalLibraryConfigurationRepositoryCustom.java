package fr.progilone.pgcn.repository.administration.digitallibrary;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import fr.progilone.pgcn.domain.administration.digitallibrary.DigitalLibraryConfiguration;

public interface DigitalLibraryConfigurationRepositoryCustom {
    Page<DigitalLibraryConfiguration> search(String search,
                                             final List<String> libraries,
                                             Pageable pageable);
}
