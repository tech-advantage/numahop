package fr.progilone.pgcn.repository.es;

import fr.progilone.pgcn.domain.exchange.internetarchive.InternetArchiveReport;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface EsInternetArchiveReportRepository
    extends ElasticsearchRepository<InternetArchiveReport, String>, EsInternetArchiveReportRepositoryCustom {
}
