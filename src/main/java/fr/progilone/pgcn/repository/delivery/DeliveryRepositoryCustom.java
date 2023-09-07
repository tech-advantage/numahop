package fr.progilone.pgcn.repository.delivery;

import fr.progilone.pgcn.domain.delivery.Delivery;
import fr.progilone.pgcn.domain.delivery.Delivery.DeliveryStatus;
import fr.progilone.pgcn.repository.delivery.helper.DeliverySearchBuilder;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface DeliveryRepositoryCustom {

    /**
     * Recherche rapide de livraisons
     *
     * @param searchBuilder
     * @param pageable
     * @return
     */
    Page<Delivery> search(DeliverySearchBuilder searchBuilder, Pageable pageable);

    /**
     * Recherche de livraisons par lot et projets
     *
     * @param projectIds
     * @param lotIds
     * @return
     */
    List<Delivery> findByProjectsAndLots(List<String> projectIds, List<String> lotIds);

    List<Delivery> findByProviders(List<String> libraries, List<String> providers, List<DeliveryStatus> statuses, LocalDate fromDate, LocalDate toDate);

    List<Object[]> getDeliveryGroupByStatus(List<String> libraries, List<String> projects, List<String> lots);

    List<Delivery> findDeliveriesForWidget(LocalDate fromDate,
                                           List<String> libraries,
                                           List<String> projects,
                                           List<String> lots,
                                           List<Delivery.DeliveryStatus> status,
                                           boolean sampled);
}
