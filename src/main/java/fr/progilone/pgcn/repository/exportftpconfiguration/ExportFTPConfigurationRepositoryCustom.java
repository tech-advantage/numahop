package fr.progilone.pgcn.repository.exportftpconfiguration;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import fr.progilone.pgcn.domain.exportftpconfiguration.ExportFTPConfiguration;

public interface ExportFTPConfigurationRepositoryCustom {
    
    Page<ExportFTPConfiguration> search(String search,
                                  final List<String> libraries,
                                  Pageable pageable);

}
