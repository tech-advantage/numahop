package fr.progilone.pgcn.repository.es;

import fr.progilone.pgcn.domain.es.lot.EsLot;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface EsLotRepository extends ElasticsearchRepository<EsLot, String>, EsLotRepositoryCustom {
}
