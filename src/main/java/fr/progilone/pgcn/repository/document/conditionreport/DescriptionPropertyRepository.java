package fr.progilone.pgcn.repository.document.conditionreport;

import fr.progilone.pgcn.domain.document.conditionreport.DescriptionProperty;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DescriptionPropertyRepository extends JpaRepository<DescriptionProperty, String> {

    List<DescriptionProperty> findAllByOrderByOrderAsc();
}
