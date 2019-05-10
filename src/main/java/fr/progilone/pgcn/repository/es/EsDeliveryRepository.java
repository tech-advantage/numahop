package fr.progilone.pgcn.repository.es;

import fr.progilone.pgcn.domain.delivery.Delivery;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface EsDeliveryRepository extends ElasticsearchRepository<Delivery, String>, EsDeliveryRepositoryCustom {
}
