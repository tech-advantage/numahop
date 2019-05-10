package fr.progilone.pgcn.repository.document.conditionreport;

import fr.progilone.pgcn.domain.document.conditionreport.Description;
import fr.progilone.pgcn.domain.document.conditionreport.DescriptionProperty;
import fr.progilone.pgcn.domain.document.conditionreport.DescriptionValue;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DescriptionRepository extends JpaRepository<Description, String> {

    Long countByProperty(DescriptionProperty property);

    Long countByValue(DescriptionValue value);
}
