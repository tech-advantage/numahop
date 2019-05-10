package fr.progilone.pgcn.repository.multilotsdelivery;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import fr.progilone.pgcn.domain.lot.Lot;
import fr.progilone.pgcn.domain.multilotsdelivery.MultiLotsDelivery;

public interface MultiLotsDeliveryRepository extends JpaRepository<MultiLotsDelivery, String>, MultiLotsDeliveryRepositoryCustom {
    
    
    @Query("select distinct multi "
            + "from MultiLotsDelivery multi "
            + "left join fetch multi.deliveries d "
            + "left join fetch d.lot l "
            + "left join fetch l.project "
            + "where multi.identifier = ?1")
    MultiLotsDelivery findOneByIdWithDeliveries(String id);
    
    
    @Query("select distinct lot "
            + "from Lot lot "
            + "left join lot.docUnits du "
            + "left join du.physicalDocuments pd "
            + "left join pd.train t "
            + "where t.identifier = ?1")
    List<Lot> findLotsByTrainIdentifier(String id);

}
