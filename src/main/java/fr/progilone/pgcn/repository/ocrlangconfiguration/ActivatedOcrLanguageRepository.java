package fr.progilone.pgcn.repository.ocrlangconfiguration;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import fr.progilone.pgcn.domain.ocrlangconfiguration.ActivatedOcrLanguage;
import fr.progilone.pgcn.domain.ocrlangconfiguration.OcrLangConfiguration;

public interface ActivatedOcrLanguageRepository extends JpaRepository<ActivatedOcrLanguage, String>{

    List<ActivatedOcrLanguage> findByOcrLangConfiguration(OcrLangConfiguration config);
}
