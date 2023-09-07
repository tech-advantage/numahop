package fr.progilone.pgcn.repository.es;

import fr.progilone.pgcn.domain.es.train.EsTrain;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface EsTrainRepository extends ElasticsearchRepository<EsTrain, String>, EsTrainRepositoryCustom {
}
