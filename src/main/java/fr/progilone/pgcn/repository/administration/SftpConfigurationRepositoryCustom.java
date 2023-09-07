package fr.progilone.pgcn.repository.administration;

import fr.progilone.pgcn.domain.administration.SftpConfiguration;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface SftpConfigurationRepositoryCustom {

    Page<SftpConfiguration> search(String search, final List<String> libraries, Pageable pageable);
}
