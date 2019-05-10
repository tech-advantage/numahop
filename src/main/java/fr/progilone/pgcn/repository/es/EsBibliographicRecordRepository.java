package fr.progilone.pgcn.repository.es;

import fr.progilone.pgcn.domain.document.BibliographicRecord;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface EsBibliographicRecordRepository extends ElasticsearchRepository<BibliographicRecord, String>, EsBibliographicRecordRepositoryCustom {
}
