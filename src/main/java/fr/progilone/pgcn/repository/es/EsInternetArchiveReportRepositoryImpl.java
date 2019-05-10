package fr.progilone.pgcn.repository.es;

import fr.progilone.pgcn.domain.exchange.internetarchive.InternetArchiveReport;
import fr.progilone.pgcn.repository.es.helper.EsSearchOperation;
import org.elasticsearch.index.query.QueryBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.EntityMapper;

import java.util.List;
import java.util.Optional;

public class EsInternetArchiveReportRepositoryImpl extends AbstractEsChildrenRepository<InternetArchiveReport>
    implements EsInternetArchiveReportRepositoryCustom {

    @Autowired
    public EsInternetArchiveReportRepositoryImpl(final ElasticsearchTemplate elasticsearchTemplate, final EntityMapper entityMapper) {
        super(InternetArchiveReport.class, entityMapper, elasticsearchTemplate);
    }

    @Override
    protected QueryBuilder getSearchQueryBuilder(final EsSearchOperation searchOp, final boolean fuzzy) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected Optional<QueryBuilder> getLibraryQueryBuilder(final List<String> libraries) {
        throw new UnsupportedOperationException();
    }
}
