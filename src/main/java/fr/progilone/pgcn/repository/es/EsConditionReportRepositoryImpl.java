package fr.progilone.pgcn.repository.es;

import fr.progilone.pgcn.domain.document.DocUnit;
import fr.progilone.pgcn.domain.document.DocUnit_;
import fr.progilone.pgcn.domain.document.conditionreport.ConditionReport;
import fr.progilone.pgcn.domain.document.conditionreport.ConditionReportDetail_;
import fr.progilone.pgcn.domain.document.conditionreport.ConditionReport_;
import fr.progilone.pgcn.domain.document.conditionreport.DescriptionProperty_;
import fr.progilone.pgcn.domain.document.conditionreport.DescriptionValue_;
import fr.progilone.pgcn.domain.document.conditionreport.Description_;
import fr.progilone.pgcn.domain.library.Library_;
import fr.progilone.pgcn.repository.es.helper.EsQueryBuilder;
import fr.progilone.pgcn.repository.es.helper.EsSearchOperation;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.aggregations.AbstractAggregationBuilder;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.terms.TermsBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.EntityMapper;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static fr.progilone.pgcn.repository.es.helper.EsQueryHelper.*;

public class EsConditionReportRepositoryImpl extends AbstractEsChildrenRepository<ConditionReport> implements EsConditionReportRepositoryCustom {

    @Autowired
    public EsConditionReportRepositoryImpl(final ElasticsearchTemplate elasticsearchTemplate, final EntityMapper entityMapper) {
        super(ConditionReport.class, entityMapper, elasticsearchTemplate);
    }

    @Override
    protected QueryBuilder getSearchQueryBuilder(final EsSearchOperation searchOp, final boolean fuzzy) {
        final EsQueryBuilder builder = new EsQueryBuilder();
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
                final BoolQueryBuilder propQuery = QueryBuilders.boolQuery()
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
                builder.should(QueryBuilders.nestedQuery(path(ConditionReport_.details, ConditionReportDetail_.descriptions), propQuery));
            }
        }
        return builder.build();
    }

    @Override
    protected Optional<QueryBuilder> getLibraryQueryBuilder(final List<String> libraries) {
        if (CollectionUtils.isNotEmpty(libraries)) {
            return Optional.of(QueryBuilders.hasParentQuery(DocUnit.ES_TYPE,
                                                            QueryBuilders.termsQuery(path(DocUnit_.library, Library_.identifier), libraries)));
        }
        return Optional.empty();
    }

    @Override
    protected QueryBuilder getFilterQueryBuilder(final String searchField, final List<String> values) {
        final int pos = searchField.indexOf(':');
        //        final String type = pos >= 0 ? searchField.substring(0, pos) : "CONDREPORT";
        final String field = pos >= 0 ? searchField.substring(pos + 1) : searchField;

        return super.getFilterQueryBuilder(field, values);
    }

    @Override
    protected List<AbstractAggregationBuilder> getAggregationBuilders() {
        return Stream.of("CONDREPORT:details.type", "CONDREPORT:docUnitCondReportType")
                     .map(this::getAggregationBuilder)
                     .collect(Collectors.toList());
    }

    protected AbstractAggregationBuilder getAggregationBuilder(final String aggName) {
        final int pos = aggName.indexOf(':');
        //        final String type = pos >= 0 ? aggName.substring(0, pos) : "CONDREPORT";
        final String field = pos >= 0 ? aggName.substring(pos + 1) : aggName;

        return new TermsBuilder(aggName).field(field).size(20).order(Terms.Order.count(false));
    }

    private void addDefaultSearch(final EsQueryBuilder builder, final String search, final boolean fuzzy) {
        final String descPath = path(ConditionReport_.details, ConditionReportDetail_.descriptions);
        final String valueLabel = path(descPath, Description_.value.getName(), DescriptionValue_.label.getName());
        final String additionnalDescPath = path(ConditionReport_.details, ConditionReportDetail_.additionnalDesc);
        final String bindingDescPath = path(ConditionReport_.details, ConditionReportDetail_.bindingDesc);
        final String bodyDescPath = path(ConditionReport_.details, ConditionReportDetail_.bodyDesc);
        final String comment = path(descPath, Description_.comment.getName());

        builder.should(getFullTextQueryBuilder(additionnalDescPath, search, fuzzy).boost(2))
               .should(getFullTextQueryBuilder(bindingDescPath, search, fuzzy).boost(2))
               .should(getFullTextQueryBuilder(bodyDescPath, search, fuzzy).boost(2))
               .should(QueryBuilders.nestedQuery(descPath, getFullTextQueryBuilder(valueLabel, search, fuzzy).boost(2)))
               .should(QueryBuilders.nestedQuery(descPath, getFullTextQueryBuilder(comment, search, fuzzy)));
    }
}
