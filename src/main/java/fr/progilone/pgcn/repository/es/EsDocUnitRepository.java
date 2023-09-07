package fr.progilone.pgcn.repository.es;

import fr.progilone.pgcn.domain.es.document.EsDocUnit;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface EsDocUnitRepository extends ElasticsearchRepository<EsDocUnit, String>, EsDocUnitRepositoryCustom {
}
