package fr.progilone.pgcn.repository.ocrlangconfiguration;

import fr.progilone.pgcn.domain.ocrlangconfiguration.OcrLanguage;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OcrLanguageRepository extends JpaRepository<OcrLanguage, String> {

    List<OcrLanguage> findAllByOrderByLabelAsc();

}
