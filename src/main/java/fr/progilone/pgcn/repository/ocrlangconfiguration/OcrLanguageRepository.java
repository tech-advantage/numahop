package fr.progilone.pgcn.repository.ocrlangconfiguration;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import fr.progilone.pgcn.domain.ocrlangconfiguration.OcrLanguage;

public interface OcrLanguageRepository extends JpaRepository<OcrLanguage, String> {
    
    List<OcrLanguage> findAllByOrderByLabelAsc();

}
