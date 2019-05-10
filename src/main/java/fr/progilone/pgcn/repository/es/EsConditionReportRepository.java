package fr.progilone.pgcn.repository.es;

import fr.progilone.pgcn.domain.document.conditionreport.ConditionReport;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface EsConditionReportRepository extends ElasticsearchRepository<ConditionReport, String>, EsConditionReportRepositoryCustom {
}
