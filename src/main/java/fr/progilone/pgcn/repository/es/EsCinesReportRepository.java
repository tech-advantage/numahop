package fr.progilone.pgcn.repository.es;

import fr.progilone.pgcn.domain.exchange.cines.CinesReport;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface EsCinesReportRepository extends ElasticsearchRepository<CinesReport, String>, EsCinesReportRepositoryCustom {
}
