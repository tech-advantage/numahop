package fr.progilone.pgcn.repository.es;

import static fr.progilone.pgcn.repository.es.helper.EsQueryHelper.*;

import co.elastic.clients.elasticsearch._types.SortOrder;
import co.elastic.clients.elasticsearch._types.aggregations.Aggregation;
import co.elastic.clients.elasticsearch._types.aggregations.TermsAggregation;
import co.elastic.clients.elasticsearch._types.query_dsl.BoolQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.ChildScoreMode;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch._types.query_dsl.QueryBuilders;
import co.elastic.clients.elasticsearch.core.search.CompletionContext;
import co.elastic.clients.elasticsearch.core.search.Context;
import co.elastic.clients.elasticsearch.core.search.SuggestFuzziness;
import co.elastic.clients.elasticsearch.core.search.Suggester;
import co.elastic.clients.json.JsonData;
import co.elastic.clients.util.NamedValue;
import fr.progilone.pgcn.domain.administration.CinesPAC_;
import fr.progilone.pgcn.domain.administration.InternetArchiveCollection_;
import fr.progilone.pgcn.domain.document.BibliographicRecord_;
import fr.progilone.pgcn.domain.document.DocUnit_;
import fr.progilone.pgcn.domain.document.PhysicalDocument_;
import fr.progilone.pgcn.domain.es.document.EsDocUnit;
import fr.progilone.pgcn.domain.exchange.cines.CinesReport_;
import fr.progilone.pgcn.domain.library.Library_;
import fr.progilone.pgcn.repository.es.helper.EsQueryBuilder;
import fr.progilone.pgcn.repository.es.helper.EsSearchOperation;
import fr.progilone.pgcn.service.es.EsConstant;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.client.elc.NativeQueryBuilder;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.SearchOperations;
import org.springframework.data.elasticsearch.core.query.HighlightQuery;
import org.springframework.data.elasticsearch.core.query.highlight.Highlight;
import org.springframework.data.elasticsearch.core.query.highlight.HighlightField;
import org.springframework.data.elasticsearch.core.suggest.response.Suggest;

public class EsDocUnitRepositoryImpl extends AbstractEsRepository<EsDocUnit> implements EsDocUnitRepositoryCustom {

    private static final Logger LOG = LoggerFactory.getLogger(EsDocUnitRepositoryImpl.class);

    @Autowired
    public EsDocUnitRepositoryImpl(final SearchOperations searchOperations) {
        super(EsDocUnit.class, searchOperations);
    }

    @Override
    protected Query getSearchQueryBuilder(final EsSearchOperation searchOp, final boolean fuzzy) {
        final EsQueryBuilder builder = new EsQueryBuilder();
        final String search = searchOp.getSearch();
        final String[] index = readIndex(searchOp.getIndex(), INDEX_DOCUNIT);
        final String obj = index[0];
        final String field = index[1];

        if (StringUtils.isNotBlank(search)) {
            // Recherche par défaut
            if (StringUtils.equals(field, "default")) {
                addDefaultSearch(builder, search, fuzzy);
            }
            // Recherche par champ
            else {
                switch (obj) {
                    case INDEX_DOCUNIT:
                        addDocUnitSearch(builder, search, field, fuzzy);
                        break;
                    case INDEX_RECORD:
                        addRecordsearch(builder, search, field, fuzzy);
                        break;
                    case INDEX_PROPERTY:
                        addPropertySearch(builder, search, field, fuzzy);
                        break;
                    case INDEX_PHYSDOC:
                        addPhysicalDocSearch(builder, search, field, fuzzy);
                        break;
                    case INDEX_CINES:
                        addCinesSearch(builder, search, field, fuzzy);
                        break;
                    case INDEX_IA:
                        addIaSearch(builder, search, field, fuzzy);
                        break;
                }
            }
        }
        return builder.build();
    }

    @Override
    protected Optional<Query> getLibraryQueryBuilder(final List<String> libraries) {
        if (CollectionUtils.isNotEmpty(libraries)) {
            return Optional.of(getExactQueryBuilder(path(DocUnit_.library, Library_.identifier), libraries));
        }
        return Optional.empty();
    }

    @Override
    protected HighlightQuery getHighlightField() {
        return new HighlightQuery(new Highlight(List.of("label", "pgcnId").stream().map(HighlightField::new).toList()), null);
    }

    @Override
    protected Query getFilterQueryBuilder(final String searchField, final List<String> values) {
        final int pos = searchField.indexOf(':');
        final String type = pos >= 0 ? searchField.substring(0, pos)
                                     : "DOCUNIT";
        final String field = pos >= 0 ? searchField.substring(pos + 1)
                                      : searchField;

        switch (type) {
            case "RECORD_PROPERTY":
                // docunit > bib_record > properties [type = field] > value.raw = values
                final BoolQuery.Builder searchQry = QueryBuilders.bool()
                                                                 .filter(getExactQueryBuilder("records.properties.type.identifier", field))
                                                                 .filter(getExactQueryBuilder("records.properties.value.raw", values));
                return QueryBuilders.nested(b -> b.path("records.properties").query(searchQry.build()._toQuery()).scoreMode(ChildScoreMode.Sum));
            case "DOCUNIT":
            default:
                return super.getFilterQueryBuilder(field, values);
        }
    }

    @Override
    protected Map<String, Aggregation> getAggregationBuilders() {
        return Stream.of("DOCUNIT:type",
                         "DOCUNIT:archivable",
                         "DOCUNIT:distributable",
                         "RECORD_PROPERTY:contributor",
                         "RECORD_PROPERTY:coverage",
                         "RECORD_PROPERTY:creator",
                         "RECORD_PROPERTY:language",
                         "RECORD_PROPERTY:publisher",
                         "RECORD_PROPERTY:relation",
                         "RECORD_PROPERTY:rights",
                         "RECORD_PROPERTY:subject",
                         "RECORD_PROPERTY:type").collect(Collectors.toMap(Function.identity(), this::getAggregationBuilder));
    }

    protected Aggregation getAggregationBuilder(final String aggName) {
        final int pos = aggName.indexOf(':');
        final String type = pos >= 0 ? aggName.substring(0, pos)
                                     : "DOCUNIT";
        final String field = pos >= 0 ? aggName.substring(pos + 1)
                                      : aggName;

        switch (type) {
            case "RECORD_PROPERTY":
                // docunit > bib_record > properties [type = field] > value.raw
                return new Aggregation.Builder().nested(n -> n.path("records.properties"))
                                                .aggregations("filter-type",
                                                              f -> f.filter(getExactQueryBuilder("records.properties.type.identifier", field))
                                                                    .aggregations(aggName,
                                                                                  t -> t.terms(t1 -> t1.field("records.properties.value.raw")
                                                                                                       .size(20)
                                                                                                       .order(List.of(NamedValue.of("_count", SortOrder.Desc))))))
                                                .build();
            // recordAgg.subAggregation(filterByTypeAgg.subAggregation(valueAgg));

            case "DOCUNIT":
            default:
                return TermsAggregation.of(b -> b.field(field).size(20).order(List.of(NamedValue.of("_count", SortOrder.Desc))))._toAggregation();
        }
    }

    private void addDocUnitSearch(final EsQueryBuilder builder, final String search, final String field, final boolean fuzzy) {
        switch (field) {
            case "pgcnId":
                builder.should(getFullTextQueryBuilder(DocUnit_.pgcnId.getName(), search, false)); // pas de recherche fuzzy sur le pgcnId
                break;
            case "label":
                builder.should(getFullTextQueryBuilder(DocUnit_.label.getName(), search, fuzzy));
                break;
            case "type":
                builder.should(getExactQueryBuilder(DocUnit_.type.getName(), search));
                break;
            case "collectionIA":
                builder.should(getExactQueryBuilder(path(DocUnit_.collectionIA, InternetArchiveCollection_.identifier), search));
                break;
            case "planClassementPAC":
                builder.should(getExactQueryBuilder(path(DocUnit_.planClassementPAC, CinesPAC_.identifier), search));
                break;
            case "archivable":
                builder.should(getExactQueryBuilder(DocUnit_.archivable.getName(), Boolean.parseBoolean(search)));
                break;
            case "distributable":
                builder.should(getExactQueryBuilder(DocUnit_.distributable.getName(), Boolean.parseBoolean(search)));
                break;
            case "rights":
                builder.should(getExactQueryBuilder(DocUnit_.rights.getName(), search));
                break;
            case "embargo":
                builder.should(getRangeQueryBuilder(DocUnit_.embargo.getName(), search));
                break;
            case "checkDelay":
                builder.should(getRangeQueryBuilder(DocUnit_.checkDelay.getName(), search));
                break;
            case "checkEndTime":
                builder.should(getRangeQueryBuilder(DocUnit_.checkEndTime.getName(), search));
                break;
            case "digitalId":
                builder.should(getExactQueryBuilder(path(DocUnit_.physicalDocuments, PhysicalDocument_.digitalId), search));
                break;
            case "library":
                builder.should(getExactQueryBuilder(path(DocUnit_.library, Library_.identifier), search));
                break;
            case "project":
                builder.should(getExactQueryBuilder("projectId", search));
                break;
            case "lot":
                builder.should(getExactQueryBuilder("lotId", search));
                break;
            case "nbDigitalDocuments":
                if ("false".equals(search)) {
                    builder.should(getExactQueryBuilder("nbDigitalDocuments", "0"));
                } else {
                    builder.should(QueryBuilders.range().field("nbDigitalDocuments").gt(JsonData.of(0)).build()._toQuery());
                }
                break;
            case "workflowState":
                builder.should(getExactQueryBuilder("workflowStateKeys", search));
                break;
            case "createdDate":
                builder.should(getRangeQueryBuilder(DocUnit_.createdDate.getName(), search));
                break;
            case "lastModifiedDate":
                builder.should(getRangeQueryBuilder(DocUnit_.lastModifiedDate.getName(), search));
                builder.should(QueryBuilders.nested()
                                            .path("records")
                                            .query(getRangeQueryBuilder("records." + BibliographicRecord_.lastModifiedDate.getName(), search))
                                            .scoreMode(ChildScoreMode.Sum)
                                            .build()
                                            ._toQuery());
                break;
            case "latestDeliveryDate":
                builder.should(getRangeQueryBuilder("latestDeliveryDate", search));
                break;
            case "masterSize":
                builder.should(getRangeQueryBuilder("masterSize", search));
                break;
        }
    }

    private void addRecordsearch(final EsQueryBuilder builder, final String search, final String field, final boolean fuzzy) {
        switch (field) {
            case "title":
                builder.should(QueryBuilders.nested()
                                            .path("records")
                                            .query(getFullTextQueryBuilder("records.title", search, fuzzy))
                                            .scoreMode(ChildScoreMode.Sum)
                                            .build()
                                            ._toQuery());
                break;
        }
    }

    private void addPropertySearch(final EsQueryBuilder builder, final String search, final String field, final boolean fuzzy) {
        final BoolQuery.Builder propQuery = QueryBuilders.bool()
                                                         // type
                                                         .must(getExactQueryBuilder("records.properties.type.identifier", field))
                                                         // valeur
                                                         .must(getFullTextQueryBuilder("records.properties.value", search, fuzzy));
        // nested
        builder.should(QueryBuilders.nested().path("records.properties").query(propQuery.build()._toQuery()).scoreMode(ChildScoreMode.Sum).build()._toQuery());
    }

    private void addPhysicalDocSearch(final EsQueryBuilder builder, final String search, final String field, final boolean fuzzy) {
        switch (field) {
            case "totalPage":
                builder.should(getRangeQueryBuilder(path(DocUnit_.physicalDocuments, PhysicalDocument_.totalPage), search));
                break;
        }
    }

    private void addCinesSearch(final EsQueryBuilder builder, final String search, final String field, final boolean fuzzy) {
        switch (field) {
            case "dateSent":
                builder.should(QueryBuilders.nested()
                                            .path("cinesReports")
                                            .query(getRangeQueryBuilder(path("cinesReports", CinesReport_.dateSent.getName()), search))
                                            .scoreMode(ChildScoreMode.Sum)
                                            .build()
                                            ._toQuery());
                break;
            case "status":
                builder.should(QueryBuilders.nested()
                                            .path("cinesReports")
                                            .query(getExactQueryBuilder(path("cinesReports", CinesReport_.status.getName()), search))
                                            .scoreMode(ChildScoreMode.Sum)
                                            .build()
                                            ._toQuery());
                break;
        }
    }

    private void addIaSearch(final EsQueryBuilder builder, final String search, final String field, final boolean fuzzy) {
        switch (field) {
            case "dateSent":
                builder.should(QueryBuilders.nested().path("iaReports").query(getRangeQueryBuilder("iaReports.dateSent", search)).scoreMode(ChildScoreMode.Sum).build()._toQuery());
                break;
            case "status":
                builder.should(QueryBuilders.nested().path("iaReports").query(getExactQueryBuilder("iaReports.status", search)).scoreMode(ChildScoreMode.Sum).build()._toQuery());
                break;
        }
    }

    private void addDefaultSearch(final EsQueryBuilder builder, final String search, final boolean fuzzy) {
        builder
               // Unité documentaire
               .should(getFullTextQueryBuilder(DocUnit_.pgcnId.getName(), search, false)) // pas de recherche fuzzy sur le pgcnId
               .should(getFullTextQueryBuilder(DocUnit_.label.getName(), search, fuzzy))
               .should(getExactQueryBuilder(DocUnit_.type.getName(), search))
               .should(getExactQueryBuilder(DocUnit_.arkUrl.getName(), search))
               .should(getExactQueryBuilder(DocUnit_.rights.getName(), search))
               .should(getExactQueryBuilder(DocUnit_.condReportType.getName(), search))
               // Record: titre
               .should(QueryBuilders.nested().path("records").query(getFullTextQueryBuilder("records.title", search, fuzzy)).scoreMode(ChildScoreMode.Sum).build()._toQuery())
               // Record: propriétés
               .should(QueryBuilders.nested()
                                    .path("records.properties")
                                    .query(getFullTextQueryBuilder("records.properties.value", search, fuzzy))
                                    .scoreMode(ChildScoreMode.Sum)
                                    .build()
                                    ._toQuery());
    }

    /**
     * Suggestions avec payload, et filtrage par contexte sur la bibliothèque
     */
    @Override
    public List<Map<String, Object>> suggest(final String text, final int size, final List<String> libraries) {
        final NativeQuery query = new NativeQueryBuilder().withSuggester(Suggester.of(b -> b.suggesters(EsConstant.SUGGEST_FIELD,
                                                                                                        s -> s.completion(c -> c.field(EsConstant.SUGGEST_FIELD)
                                                                                                                                .fuzzy(SuggestFuzziness.of(f -> f.fuzziness("AUTO")))
                                                                                                                                .size(size)
                                                                                                                                .contexts(Map.of(EsConstant.SUGGEST_CTX_LIBRARY,
                                                                                                                                                 libraries.stream()
                                                                                                                                                          .map(id -> CompletionContext.of(cc -> cc.context(Context.of(ccc -> ccc.category(id)))))
                                                                                                                                                          .collect(Collectors.toList()))))
                                                                                                              .prefix(text)))).build();
        if (LOG.isTraceEnabled()) {
            LOG.trace("Suggest : {}", query.getSuggester());
        }

        final SearchHits<EsDocUnit> response = searchOperations.search(query, clazz);

        final Suggest suggestions = response.getSuggest();
        if (suggestions == null) {
            return Collections.emptyList();
        }
        return suggestions.getSuggestions().stream().flatMap(e -> e.getEntries().stream()).flatMap(e -> e.getOptions().stream()).map(option -> {
            final Map<String, Object> resultOption = new HashMap<>();
            resultOption.put(FIELD_SUGG_TEXT, option.getText());
            return resultOption;
        }).collect(Collectors.toList());
    }
}
