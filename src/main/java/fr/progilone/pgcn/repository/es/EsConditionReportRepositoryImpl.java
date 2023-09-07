package fr.progilone.pgcn.repository.es;

import static fr.progilone.pgcn.repository.es.helper.EsQueryHelper.*;

import co.elastic.clients.elasticsearch._types.SortOrder;
import co.elastic.clients.elasticsearch._types.aggregations.Aggregation;
import co.elastic.clients.elasticsearch._types.aggregations.TermsAggregation;
import co.elastic.clients.elasticsearch._types.query_dsl.BoolQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.ChildScoreMode;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch._types.query_dsl.QueryBuilders;
import co.elastic.clients.util.NamedValue;
import fr.progilone.pgcn.domain.document.conditionreport.ConditionReportDetail_;
import fr.progilone.pgcn.domain.document.conditionreport.ConditionReport_;
import fr.progilone.pgcn.domain.document.conditionreport.DescriptionProperty_;
import fr.progilone.pgcn.domain.document.conditionreport.DescriptionValue_;
import fr.progilone.pgcn.domain.document.conditionreport.Description_;
import fr.progilone.pgcn.domain.es.conditionreport.EsConditionReport;
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

public class EsConditionReportRepositoryImpl extends AbstractEsRepository<EsConditionReport> implements EsConditionReportRepositoryCustom {

    @Autowired
    public EsConditionReportRepositoryImpl(final SearchOperations searchOperations) {
        super(EsConditionReport.class, searchOperations);
    }

    @Override
    protected Query getSearchQueryBuilder(final EsSearchOperation searchOp, final boolean fuzzy) {
        final BoolQuery.Builder builder = new BoolQuery.Builder();
        final String search = searchOp.getSearch();
        final String[] index = readIndex(searchOp.getIndex(), INDEX_CONDREPORT);
        final String obj = index[0];
        final String field = index[1];

        if (StringUtils.isNotBlank(search)) {
            // Recherche par défaut
            if (StringUtils.equals(field, "default")) {
                addDefaultSearch(builder, search, fuzzy);
            }
            // Recherche sur les champs des unités documentaires
            else if (StringUtils.equals(obj, INDEX_CONDREPORT)) {
                final BoolQuery.Builder propQuery = QueryBuilders.bool()
                                                                 // type
                                                                 .must(getExactQueryBuilder(path(ConditionReport_.details,
                                                                                                 ConditionReportDetail_.descriptions,
                                                                                                 Description_.property,
                                                                                                 DescriptionProperty_.identifier), field))
                                                                 // valeur
                                                                 .must(getExactQueryBuilder(path(ConditionReport_.details,
                                                                                                 ConditionReportDetail_.descriptions,
                                                                                                 Description_.value,
                                                                                                 DescriptionValue_.identifier), search));
                // nested
                builder.should(QueryBuilders.nested()
                                            .path(path(ConditionReport_.details, ConditionReportDetail_.descriptions))
                                            .query(propQuery.build()._toQuery())
                                            .scoreMode(ChildScoreMode.Sum)
                                            .build()
                                            ._toQuery());
            }
        }
        return builder.build()._toQuery();
    }

    @Override
    protected Optional<Query> getLibraryQueryBuilder(final List<String> libraries) {
        if (CollectionUtils.isNotEmpty(libraries)) {
            return Optional.of(getExactQueryBuilder(EsConstant.FIELD_LIBRARY, libraries));
        }
        return Optional.empty();
    }

    @Override
    protected Query getFilterQueryBuilder(final String searchField, final List<String> values) {
        final int pos = searchField.indexOf(':');
        final String field = pos >= 0 ? searchField.substring(pos + 1)
                                      : searchField;
        return super.getFilterQueryBuilder(field, values);
    }

    @Override
    protected Map<String, Aggregation> getAggregationBuilders() {
        return Stream.of("CONDREPORT:details.type", "CONDREPORT:docUnitCondReportType").collect(Collectors.toMap(Function.identity(), this::getAggregationBuilder));
    }

    protected Aggregation getAggregationBuilder(final String aggName) {
        final int pos = aggName.indexOf(':');
        // final String type = pos >= 0 ? aggName.substring(0, pos) : "CONDREPORT";
        final String field = pos >= 0 ? aggName.substring(pos + 1)
                                      : aggName;

        return TermsAggregation.of(b -> b.field(field).size(20).order(List.of(NamedValue.of("_count", SortOrder.Desc))))._toAggregation();
    }

    private void addDefaultSearch(final BoolQuery.Builder builder, final String search, final boolean fuzzy) {
        final String descPath = path(ConditionReport_.details, ConditionReportDetail_.descriptions);
        final String valueLabel = path(descPath, Description_.value.getName(), DescriptionValue_.label.getName());
        final String additionnalDescPath = path(ConditionReport_.details, ConditionReportDetail_.additionnalDesc);
        final String bindingDescPath = path(ConditionReport_.details, ConditionReportDetail_.bindingDesc);
        final String bodyDescPath = path(ConditionReport_.details, ConditionReportDetail_.bodyDesc);
        final String comment = path(descPath, Description_.comment.getName());

        builder.should(getFullTextQueryBuilder(additionnalDescPath, search, fuzzy))
               .boost(2F)
               .should(getFullTextQueryBuilder(bindingDescPath, search, fuzzy))
               .boost(2F)
               .should(getFullTextQueryBuilder(bodyDescPath, search, fuzzy))
               .boost(2F)
               .should(QueryBuilders.nested().path(descPath).query(getFullTextQueryBuilder(valueLabel, search, fuzzy)).scoreMode(ChildScoreMode.Sum).build()._toQuery())
               .boost(2F)
               .should(QueryBuilders.nested().path(descPath).query(getFullTextQueryBuilder(comment, search, fuzzy)).scoreMode(ChildScoreMode.Sum).build()._toQuery());
    }
}
