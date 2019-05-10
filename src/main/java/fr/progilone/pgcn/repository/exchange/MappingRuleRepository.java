package fr.progilone.pgcn.repository.exchange;

import fr.progilone.pgcn.domain.document.DocPropertyType;
import fr.progilone.pgcn.domain.exchange.MappingRule;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MappingRuleRepository extends JpaRepository<MappingRule, String> {

    Integer countByProperty(DocPropertyType type);
}
