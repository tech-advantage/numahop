package fr.progilone.pgcn.repository.exchange.cines;

import fr.progilone.pgcn.domain.exchange.cines.CinesLanguageCode;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CinesLanguageCodeRepository extends JpaRepository<CinesLanguageCode, String> {

    List<CinesLanguageCode> findByOrderByIdentifier();

    CinesLanguageCode findOneByIdentifier(String identifier);

    CinesLanguageCode findOneByLangDC(String langDC);
}
