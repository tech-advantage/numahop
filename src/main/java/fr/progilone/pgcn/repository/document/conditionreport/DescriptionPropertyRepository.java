package fr.progilone.pgcn.repository.document.conditionreport;

import fr.progilone.pgcn.domain.document.conditionreport.DescriptionProperty;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DescriptionPropertyRepository extends JpaRepository<DescriptionProperty, String> {

    List<DescriptionProperty> findAllByOrderByOrderAsc();
}
