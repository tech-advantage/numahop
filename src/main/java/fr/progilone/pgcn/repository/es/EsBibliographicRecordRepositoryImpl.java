package fr.progilone.pgcn.repository.es;

import fr.progilone.pgcn.domain.document.BibliographicRecord;
import fr.progilone.pgcn.domain.document.BibliographicRecord_;
import fr.progilone.pgcn.domain.document.DocProperty_;
import fr.progilone.pgcn.domain.library.Library_;
import fr.progilone.pgcn.repository.es.helper.EsQueryBuilder;
import fr.progilone.pgcn.repository.es.helper.EsSearchOperation;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.index.query.NestedQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.highlight.HighlightBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.EntityMapper;

import java.util.List;
import java.util.Optional;

import static fr.progilone.pgcn.repository.es.helper.EsQueryHelper.*;

public class EsBibliographicRecordRepositoryImpl extends AbstractEsChildrenRepository<BibliographicRecord>
    implements EsBibliographicRecordRepositoryCustom {

    @Autowired
    public EsBibliographicRecordRepositoryImpl(final ElasticsearchTemplate elasticsearchTemplate, final EntityMapper entityMapper) {
        super(BibliographicRecord.class, entityMapper, elasticsearchTemplate);
    }

    @Override
    protected QueryBuilder getSearchQueryBuilder(final EsSearchOperation searchOp, final boolean fuzzy) {
        final EsQueryBuilder builder = new EsQueryBuilder();
        final String search = searchOp.getSearch();

        if (StringUtils.isNotBlank(search)) {
            builder
                // Record: titre
                .should(getFullTextQueryBuilder(BibliographicRecord_.title.getName(), search, fuzzy))
                // Record: propriétés
                .should(new NestedQueryBuilder(BibliographicRecord_.properties.getName(),
                                               getFullTextQueryBuilder(path(BibliographicRecord_.properties, DocProperty_.value), search, fuzzy)));
        }
        return builder.build();
    }

    @Override
    protected Optional<QueryBuilder> getLibraryQueryBuilder(final List<String> libraries) {
        if (CollectionUtils.isNotEmpty(libraries)) {
            return Optional.of(QueryBuilders.termsQuery(path(BibliographicRecord_.library, Library_.identifier), libraries));
        }
        return Optional.empty();
    }

    @Override
    protected HighlightBuilder.Field[] getHighlightField() {
        return new HighlightBuilder.Field[] {new HighlightBuilder.Field("title")};
    }
}
