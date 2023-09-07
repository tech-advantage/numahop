package fr.progilone.pgcn.repository.administration.digitallibrary;

import fr.progilone.pgcn.domain.administration.digitallibrary.DigitalLibraryConfiguration;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface DigitalLibraryConfigurationRepositoryCustom {

    Page<DigitalLibraryConfiguration> search(String search, final List<String> libraries, Pageable pageable);
}
