package fr.progilone.pgcn.repository.es;

import fr.progilone.pgcn.domain.lot.Lot;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface EsLotRepository extends ElasticsearchRepository<Lot, String>, EsLotRepositoryCustom {
}
