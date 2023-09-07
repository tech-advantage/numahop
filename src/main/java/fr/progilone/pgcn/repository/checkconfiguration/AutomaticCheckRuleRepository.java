package fr.progilone.pgcn.repository.checkconfiguration;

import fr.progilone.pgcn.domain.checkconfiguration.AutomaticCheckRule;
import fr.progilone.pgcn.domain.checkconfiguration.CheckConfiguration;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 *
 * @author ert
 *         Créé le 14 sept. 2017
 */
public interface AutomaticCheckRuleRepository extends JpaRepository<AutomaticCheckRule, String> {

    List<AutomaticCheckRule> findByCheckConfiguration(CheckConfiguration config);
}
