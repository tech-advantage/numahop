package fr.progilone.pgcn.repository.document.conditionreport;

import fr.progilone.pgcn.domain.document.conditionreport.DescriptionValue;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;

public interface DescriptionValueRepository extends JpaRepository<DescriptionValue, String> {

    List<DescriptionValue> findByPropertyIdentifier(String propertyId);

    @Modifying
    void deleteByPropertyIdentifier(String propertyId);
}
