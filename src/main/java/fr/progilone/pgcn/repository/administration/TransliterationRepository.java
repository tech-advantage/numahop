package fr.progilone.pgcn.repository.administration;

import fr.progilone.pgcn.domain.administration.Transliteration;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Created by Sébastien on 29/06/2017.
 */
public interface TransliterationRepository extends JpaRepository<Transliteration, Transliteration.TransliterationId> {
}
