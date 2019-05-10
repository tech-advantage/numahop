package fr.progilone.pgcn.repository.es;

import fr.progilone.pgcn.domain.exchange.cines.CinesReport;
import fr.progilone.pgcn.repository.es.helper.EsSearchOperation;
import org.elasticsearch.index.query.QueryBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.EntityMapper;

import java.util.List;
import java.util.Optional;

public class EsCinesReportRepositoryImpl extends AbstractEsChildrenRepository<CinesReport> implements EsCinesReportRepositoryCustom {

    @Autowired
    public EsCinesReportRepositoryImpl(final ElasticsearchTemplate elasticsearchTemplate, final EntityMapper entityMapper) {
        super(CinesReport.class, entityMapper, elasticsearchTemplate);
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
