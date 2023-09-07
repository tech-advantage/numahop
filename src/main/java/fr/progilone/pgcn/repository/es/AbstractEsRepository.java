package fr.progilone.pgcn.repository.es;

import co.elastic.clients.elasticsearch._types.aggregations.Aggregation;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch._types.query_dsl.QueryBuilders;
import co.elastic.clients.json.JsonData;
import fr.progilone.pgcn.repository.es.helper.EsBoolOperator;
import fr.progilone.pgcn.repository.es.helper.EsQueryBuilder;
import fr.progilone.pgcn.repository.es.helper.EsQueryHelper;
import fr.progilone.pgcn.repository.es.helper.EsSearchOperation;
import fr.progilone.pgcn.repository.es.helper.SearchResultPage;
import fr.progilone.pgcn.service.util.DateUtils;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.client.elc.NativeQueryBuilder;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.SearchOperations;
import org.springframework.data.elasticsearch.core.query.HighlightQuery;

public abstract class AbstractEsRepository<T> {

    private static final Logger LOG = LoggerFactory.getLogger(AbstractEsRepository.class);

    protected static final String FIELD_SUGG_TEXT = "text";
    protected static final String FIELD_SUGG_PAYLOAD = "payload";

    protected static final String INDEX_CINES = "cines";
    protected static final String INDEX_CONDREPORT = "condreport";
    protected static final String INDEX_DELIVERY = "delivery";
    protected static final String INDEX_DOCUNIT = "docunit";
    protected static final String INDEX_IA = "ia";
    protected static final String INDEX_LOT = "lot";
    protected static final String INDEX_PHYSDOC = "physdoc";
    protected static final String INDEX_PROJECT = "project";
    protected static final String INDEX_PROPERTY = "property";
    protected static final String INDEX_RECORD = "record";
    protected static final String INDEX_TRAIN = "train";

    protected final Class<T> clazz;
    protected final SearchOperations searchOperations;

    public AbstractEsRepository(final Class<T> clazz, final SearchOperations searchOperations) {
        this.clazz = clazz;
        this.searchOperations = searchOperations;
    }

    /**
     * Requête principale
     *
     * @param searchOp
     * @param fuzzy
     * @return
     */
    protected abstract Query getSearchQueryBuilder(final EsSearchOperation searchOp, final boolean fuzzy);

    /**
     * Requête de filtrage des résultats suivant les droits de l'utilisateur
     *
     * @param libraries
     * @return
     */
    protected abstract Optional<Query> getLibraryQueryBuilder(final List<String> libraries);

    /**
     * Requête de filtrage des résultats de recherche (sélection de facettes)
     *
     * @param field
     * @param values
     * @return
     */
    protected Query getFilterQueryBuilder(final String field, final List<String> values) {
        if (CollectionUtils.isNotEmpty(values)) {
            return EsQueryHelper.getExactQueryBuilder(field, values);
        }
        return null;
    }

    /**
     * Requête de filtrage des résultats de recherche (sélection de facettes) -> intervalle de dates
     *
     * @param field
     * @param values
     * @return
     */
    protected Query getDateFilterQueryBuilder(final String field, final List<String> values) {
        if (CollectionUtils.isNotEmpty(values)) {
            final EsQueryBuilder builder = new EsQueryBuilder();

            values.stream().map(val -> {
                final LocalDate from = DateUtils.parseStringToLocalDate(val, "dd/MM/yyyy");
                if (from == null) {
                    return null;
                }
                final LocalDate to = from.plusDays(1);

                return QueryBuilders.range(b -> b.field(field).gte(JsonData.of(from)).lt(JsonData.of(to)));

            }).filter(Objects::nonNull).forEach(builder::should);

            return builder.build();
        }
        return null;
    }

    /**
     * Champ "highlight"
     *
     * @return
     */
    protected HighlightQuery getHighlightField() {
        return null;
    }

    /**
     * Facettes
     *
     * @return
     */
    protected Map<String, Aggregation> getAggregationBuilders() {
        return Collections.emptyMap();
    }

    /**
     * Recherche d'entité.
     * La requête utilisée est celle renvoyée par getSearchQueryBuilder
     */
    public SearchResultPage<T> search(final EsSearchOperation[] searches,
                                      final List<String> libraries,
                                      final boolean fuzzy,
                                      final EsSearchOperation[] filters,
                                      final PageRequest pageable,
                                      final boolean facet) {
        final NativeQueryBuilder builder = new NativeQueryBuilder().withPageable(pageable);

        // Requête
        final EsQueryBuilder queryBuilder = new EsQueryBuilder();
        for (final EsSearchOperation search : searches) {
            queryBuilder.addQuery(search.getOperator(), getSearchQueryBuilder(search, fuzzy));
        }
        // Droits d'accès
        getLibraryQueryBuilder(libraries).ifPresent(queryBuilder::filter);
        builder.withQuery(queryBuilder.build());

        // Post-filters
        final EsQueryBuilder filterBuilder = new EsQueryBuilder();
        Arrays.stream(filters)
              .collect(Collectors.groupingBy(EsSearchOperation::getIndex, Collectors.mapping(EsSearchOperation::getSearch, Collectors.toList())))
              .forEach((index, values) -> {
                  filterBuilder.addQuery(EsBoolOperator.FILTER, getFilterQueryBuilder(index, values));
              });
        builder.withFilter(filterBuilder.build());

        // Highlight
        final HighlightQuery highlightQuery = getHighlightField();
        if (highlightQuery != null) {
            builder.withHighlightQuery(highlightQuery);
        }

        // Facettes
        if (facet) {
            getAggregationBuilders().entrySet().forEach(e -> builder.withAggregation(e.getKey(), e.getValue()));
        }

        final NativeQuery query = builder.build();
        if (LOG.isTraceEnabled()) {
            LOG.trace("Query : {}", query.getQuery());
            if (query.getAggregations() != null) {
                LOG.trace("Aggregations : {}", query.getAggregations());
            }
            if (query.getHighlightQuery().isPresent()) {
                LOG.trace("Highlight : {}", query.getHighlightQuery().get());
            }
        }

        // Recherche
        final SearchHits<T> results = searchOperations.search(query, clazz);
        return new SearchResultPage<>(results, pageable);
    }

    /**
     * Extrait l'objet et le champ de l'index de recherche<br/>
     * <code>"docunit-title" => ["docunit", "title"]</code>
     *
     * @param index
     * @param defaultObj
     * @return
     */
    protected String[] readIndex(final String index, final String defaultObj) {
        final String obj;
        final String field;
        final int pos = index.indexOf('-');

        if (pos >= 0) {
            obj = index.substring(0, pos);
            field = index.substring(pos + 1);
        } else {
            obj = defaultObj;
            field = index;
        }
        return new String[] {obj,
                             field};
    }

}
