package fr.progilone.pgcn.repository.document;

import fr.progilone.pgcn.domain.document.Check;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Created by lebouchp on 10/02/2017.
 */
public interface CheckRepository extends JpaRepository<Check, String> {
}
