package fr.progilone.pgcn.repository.document.conditionreport;

import fr.progilone.pgcn.domain.document.conditionreport.DescriptionProperty;
import fr.progilone.pgcn.domain.document.conditionreport.DescriptionValue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;

import java.util.List;

public interface DescriptionValueRepository extends JpaRepository<DescriptionValue, String> {

    List<DescriptionValue> findByProperty(DescriptionProperty property);

    @Modifying
    void deleteByPropertyIdentifier(String propertyId);
}
