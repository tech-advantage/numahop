package fr.progilone.pgcn.repository.es;

import fr.progilone.pgcn.domain.document.DocUnit;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface EsDocUnitRepository extends ElasticsearchRepository<DocUnit, String>, EsDocUnitRepositoryCustom {
}
