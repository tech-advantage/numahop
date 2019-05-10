package fr.progilone.pgcn.repository.es;

import fr.progilone.pgcn.domain.train.Train;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface EsTrainRepository extends ElasticsearchRepository<Train, String>, EsTrainRepositoryCustom {
}
