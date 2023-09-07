package fr.progilone.pgcn.repository.es;

import fr.progilone.pgcn.domain.es.conditionreport.EsConditionReport;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface EsConditionReportRepository extends ElasticsearchRepository<EsConditionReport, String>, EsConditionReportRepositoryCustom {
}
