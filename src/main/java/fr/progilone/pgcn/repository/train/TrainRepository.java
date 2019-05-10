package fr.progilone.pgcn.repository.train;

import fr.progilone.pgcn.domain.train.Train;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface TrainRepository extends JpaRepository<Train, String>, TrainRepositoryCustom {

    @Query("from Train t "
           + "where t.identifier = ?1")
    Train findOneWithDependencies(String identifier);

    List<Train> findAllByActive(boolean active);

    List<Train> findAllByProjectIdentifier(String id);

    List<Train> findByIdentifierIn(List<String> identifiers);
}
