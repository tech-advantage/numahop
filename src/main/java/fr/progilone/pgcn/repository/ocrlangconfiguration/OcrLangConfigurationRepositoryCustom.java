package fr.progilone.pgcn.repository.ocrlangconfiguration;

import fr.progilone.pgcn.domain.ocrlangconfiguration.OcrLangConfiguration;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface OcrLangConfigurationRepositoryCustom {

    Page<OcrLangConfiguration> search(String search, List<String> libraries, Pageable pageable);
}
