package fr.progilone.pgcn.repository.es;

import fr.progilone.pgcn.domain.es.project.EsProject;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface EsProjectRepository extends ElasticsearchRepository<EsProject, String>, EsProjectRepositoryCustom {
}
