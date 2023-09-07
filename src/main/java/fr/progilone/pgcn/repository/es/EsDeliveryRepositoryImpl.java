package fr.progilone.pgcn.repository.es;

import static fr.progilone.pgcn.repository.es.helper.EsQueryHelper.*;

import co.elastic.clients.elasticsearch._types.SortOrder;
import co.elastic.clients.elasticsearch._types.aggregations.Aggregation;
import co.elastic.clients.elasticsearch._types.aggregations.DateHistogramAggregation;
import co.elastic.clients.elasticsearch._types.aggregations.TermsAggregation;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.util.NamedValue;
import fr.progilone.pgcn.domain.delivery.Delivery_;
import fr.progilone.pgcn.domain.es.delivery.EsDelivery;
import fr.progilone.pgcn.repository.es.helper.EsQueryBuilder;
import fr.progilone.pgcn.repository.es.helper.EsSearchOperation;
import fr.progilone.pgcn.service.es.EsConstant;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.SearchOperations;
import org.springframework.data.elasticsearch.core.query.HighlightQuery;
import org.springframework.data.elasticsearch.core.query.highlight.Highlight;
import org.springframework.data.elasticsearch.core.query.highlight.HighlightField;

public class EsDeliveryRepositoryImpl extends AbstractEsRepository<EsDelivery> implements EsDeliveryRepositoryCustom {

    @Autowired
    public EsDeliveryRepositoryImpl(final SearchOperations searchOperations) {
        super(EsDelivery.class, searchOperations);
    }

    @Override
    protected Query getSearchQueryBuilder(final EsSearchOperation searchOp, final boolean fuzzy) {
        final EsQueryBuilder builder = new EsQueryBuilder();
        final String search = searchOp.getSearch();
        final String[] index = readIndex(searchOp.getIndex(), INDEX_DELIVERY);
        final String obj = index[0];
        final String field = index[1];

        if (StringUtils.isNotBlank(search)) {
            // Recherche par défaut
            if (StringUtils.equals(field, "default")) {
                addDefaultSearch(builder, search, fuzzy);
            }
            // Recherche sur les champs des livraisons
            else if (StringUtils.equals(obj, INDEX_DELIVERY)) {
                switch (field) {
                    case "label":
                        builder.should(getFullTextQueryBuilder(Delivery_.label.getName(), search, fuzzy));
                        break;
                    case "method":
                        builder.should(getExactQueryBuilder(Delivery_.method.getName(), search));
                        break;
                    case "payment":
                        builder.should(getExactQueryBuilder(Delivery_.payment.getName(), search));
                        break;
                    case "receptionDate":
                        builder.should(getRangeQueryBuilder(Delivery_.receptionDate.getName(), search));
                        break;
                    case "status":
                        builder.should(getExactQueryBuilder(Delivery_.status.getName(), search));
                        break;
                }
            }
        }
        return builder.build();
    }

    @Override
    protected Optional<Query> getLibraryQueryBuilder(final List<String> libraries) {
        if (CollectionUtils.isNotEmpty(libraries)) {
            return Optional.of(getExactQueryBuilder(EsConstant.FIELD_LIBRARY, libraries));
        }
        return Optional.empty();
    }

    @Override
    protected HighlightQuery getHighlightField() {
        return new HighlightQuery(new Highlight(List.of("label").stream().map(HighlightField::new).toList()), null);
    }

    @Override
    protected Query getFilterQueryBuilder(final String searchField, final List<String> values) {
        final int pos = searchField.indexOf(':');
        // final String type = pos >= 0 ? searchField.substring(0, pos) : "DELIVERY";
        final String field = pos >= 0 ? searchField.substring(pos + 1)
                                      : searchField;

        switch (searchField) {
            case "DELIVERY:receptionDate":
                return super.getDateFilterQueryBuilder(field, values);
            default:
                return super.getFilterQueryBuilder(field, values);
        }
    }

    @Override
    protected Map<String, Aggregation> getAggregationBuilders() {
        return Stream.of("DELIVERY:status", "DELIVERY:payment", "DELIVERY:method", "DELIVERY:receptionDate", "DELIVERY:documentCount")
                     .collect(Collectors.toMap(Function.identity(), this::getAggregationBuilder));
    }

    protected Aggregation getAggregationBuilder(final String aggName) {
        final int pos = aggName.indexOf(':');
        // final String type = pos >= 0 ? aggName.substring(0, pos) : "DELIVERY";
        final String field = pos >= 0 ? aggName.substring(pos + 1)
                                      : aggName;

        switch (aggName) {
            case "DELIVERY:receptionDate":
                return new DateHistogramAggregation.Builder().field(field)
                                                             .fixedInterval(b -> b.time("1d"))
                                                             .format("dd/MM/yyyy")
                                                             .minDocCount(1)
                                                             .order(List.of(NamedValue.of("_count", SortOrder.Desc)))
                                                             .build()
                                                             ._toAggregation();
            default:
                return TermsAggregation.of(b -> b.field(field).size(20).order(List.of(NamedValue.of("_count", SortOrder.Desc))))._toAggregation();
        }
    }

    private void addDefaultSearch(final EsQueryBuilder builder, final String search, final boolean fuzzy) {
        builder.should(getFullTextQueryBuilder(Delivery_.label.getName(), search, fuzzy));
    }

}
