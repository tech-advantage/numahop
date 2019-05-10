package fr.progilone.pgcn.repository.es;

import fr.progilone.pgcn.domain.project.Project;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface EsProjectRepository extends ElasticsearchRepository<Project, String>, EsProjectRepositoryCustom {
}
