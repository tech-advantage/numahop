package fr.progilone.pgcn.repository.checkconfiguration;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import fr.progilone.pgcn.domain.checkconfiguration.AutomaticCheckRule;
import fr.progilone.pgcn.domain.checkconfiguration.CheckConfiguration;

/**
 * 
 * @author ert
 * Créé le 14 sept. 2017
 */
public interface AutomaticCheckRuleRepository extends JpaRepository<AutomaticCheckRule, String> {

     List<AutomaticCheckRule> findByCheckConfiguration(CheckConfiguration config);
}
