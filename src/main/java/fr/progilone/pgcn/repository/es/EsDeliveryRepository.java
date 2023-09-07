package fr.progilone.pgcn.repository.es;

import fr.progilone.pgcn.domain.es.delivery.EsDelivery;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface EsDeliveryRepository extends ElasticsearchRepository<EsDelivery, String>, EsDeliveryRepositoryCustom {
}
