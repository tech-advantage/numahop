package fr.progilone.pgcn.repository.es;

import fr.progilone.pgcn.domain.library.Library_;
import fr.progilone.pgcn.domain.project.Project;
import fr.progilone.pgcn.domain.project.Project_;
import fr.progilone.pgcn.domain.train.Train;
import fr.progilone.pgcn.domain.train.Train_;
import fr.progilone.pgcn.repository.es.helper.EsQueryBuilder;
import fr.progilone.pgcn.repository.es.helper.EsSearchOperation;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.aggregations.AbstractAggregationBuilder;
import org.elasticsearch.search.aggregations.bucket.histogram.DateHistogramBuilder;
import org.elasticsearch.search.aggregations.bucket.histogram.DateHistogramInterval;
import org.elasticsearch.search.aggregations.bucket.histogram.Histogram;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.terms.TermsBuilder;
import org.elasticsearch.search.highlight.HighlightBuilder.Field;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.EntityMapper;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static fr.progilone.pgcn.repository.es.helper.EsQueryHelper.*;

public class EsTrainRepositoryImpl extends AbstractEsChildrenRepository<Train> implements EsTrainRepositoryCustom {

    @Autowired
    public EsTrainRepositoryImpl(final ElasticsearchTemplate elasticsearchTemplate, final EntityMapper entityMapper) {
        super(Train.class, entityMapper, elasticsearchTemplate);
    }

    @Override
    protected QueryBuilder getSearchQueryBuilder(final EsSearchOperation searchOp, final boolean fuzzy) {
        final EsQueryBuilder builder = new EsQueryBuilder();
        final String search = searchOp.getSearch();
        final String[] index = readIndex(searchOp.getIndex(), INDEX_TRAIN);
        final String obj = index[0];
        final String field = index[1];

        if (StringUtils.isNotBlank(search)) {
            // Recherche par défaut
            if (StringUtils.equals(field, "default")) {
                addDefaultSearch(builder, search, fuzzy);
            }
            // Recherche sur les champs des trains
            else if (StringUtils.equals(obj, INDEX_TRAIN)) {
                switch (field) {
                    case "label":
                        builder.should(getFullTextQueryBuilder(Train_.label.getName(), search, fuzzy));
                        break;
                    case "active":
                        builder.should(getExactQueryBuilder(Train_.active.getName(), Boolean.parseBoolean(search)));
                        break;
                    case "status":
                        builder.should(getExactQueryBuilder(Train_.status.getName(), search));
                        break;
                    case "providerSendingDate":
                        builder.should(getRangeQueryBuilder(Train_.providerSendingDate.getName(), search));
                        break;
                    case "returnDate":
                        builder.should(getRangeQueryBuilder(Train_.returnDate.getName(), search));
                        break;
                }
            }
        }
        return builder.build();
    }

    @Override
    protected Optional<QueryBuilder> getLibraryQueryBuilder(final List<String> libraries) {
        if (CollectionUtils.isNotEmpty(libraries)) {
            return Optional.of(QueryBuilders.hasParentQuery(Project.ES_TYPE,
                                                            QueryBuilders.termsQuery(path(Project_.library, Library_.identifier), libraries)));
        }
        return Optional.empty();
    }

    @Override
    protected Field[] getHighlightField() {
        return new Field[] {new Field("label")};
    }

    @Override
    protected QueryBuilder getFilterQueryBuilder(final String searchField, final List<String> values) {
        final int pos = searchField.indexOf(':');
        //        final String type = pos >= 0 ? searchField.substring(0, pos) : "TRAIN";
        final String field = pos >= 0 ? searchField.substring(pos + 1) : searchField;

        switch (searchField) {
            case "TRAIN:providerSendingDate":
            case "TRAIN:returnDate":
                return super.getDateFilterQueryBuilder(field, values);
            default:
                return super.getFilterQueryBuilder(field, values);
        }
    }

    @Override
    protected List<AbstractAggregationBuilder> getAggregationBuilders() {
        return Stream.of("TRAIN:active", "TRAIN:status", "TRAIN:providerSendingDate", "TRAIN:returnDate")
                     .map(this::getAggregationBuilder)
                     .collect(Collectors.toList());
    }

    protected AbstractAggregationBuilder getAggregationBuilder(final String aggName) {
        final int pos = aggName.indexOf(':');
        //        final String type = pos >= 0 ? aggName.substring(0, pos) : "TRAIN";
        final String field = pos >= 0 ? aggName.substring(pos + 1) : aggName;

        switch (aggName) {
            case "TRAIN:providerSendingDate":
            case "TRAIN:returnDate":
                return new DateHistogramBuilder(aggName).field(field)
                                                        .interval(DateHistogramInterval.DAY)
                                                        .format("dd/MM/yyyy")
                                                        .minDocCount(1L)
                                                        .order(Histogram.Order.COUNT_DESC);
            default:
                return new TermsBuilder(aggName).field(field).size(20).order(Terms.Order.count(false));
        }
    }

    private void addDefaultSearch(final EsQueryBuilder builder, final String search, final boolean fuzzy) {
        builder.should(getFullTextQueryBuilder(Train_.label.getName(), search, fuzzy));
    }
}
