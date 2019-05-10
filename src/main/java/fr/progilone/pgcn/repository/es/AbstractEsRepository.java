package fr.progilone.pgcn.repository.es;

import fr.progilone.pgcn.domain.AbstractDomainObject;
import fr.progilone.pgcn.repository.es.helper.EsBoolOperator;
import fr.progilone.pgcn.repository.es.helper.EsQueryBuilder;
import fr.progilone.pgcn.repository.es.helper.EsSearchOperation;
import fr.progilone.pgcn.repository.es.helper.SearchResultMapper;
import fr.progilone.pgcn.service.util.DateUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.elasticsearch.action.bulk.BulkItemResponse;
import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.suggest.SuggestResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.aggregations.AbstractAggregationBuilder;
import org.elasticsearch.search.highlight.HighlightBuilder;
import org.elasticsearch.search.suggest.Suggest;
import org.elasticsearch.search.suggest.SuggestBuilder.SuggestionBuilder;
import org.elasticsearch.search.suggest.completion.CompletionSuggestion;
import org.elasticsearch.search.suggest.completion.CompletionSuggestionFuzzyBuilder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.ElasticsearchException;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.EntityMapper;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.SearchQuery;
import org.springframework.data.elasticsearch.repository.support.ElasticsearchEntityInformation;
import org.springframework.data.elasticsearch.repository.support.MappingElasticsearchEntityInformation;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import static fr.progilone.pgcn.service.es.EsConstant.*;

public abstract class AbstractEsRepository<T extends AbstractDomainObject> {

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
    protected final ElasticsearchEntityInformation<T, String> entityInformation;
    protected final ElasticsearchTemplate elasticsearchTemplate;
    protected final EntityMapper entityMapper;

    public AbstractEsRepository(final Class<T> clazz, final ElasticsearchTemplate elasticsearchTemplate, final EntityMapper entityMapper) {
        this.clazz = clazz;
        this.entityInformation = new MappingElasticsearchEntityInformation<>(elasticsearchTemplate.getPersistentEntityFor(clazz));
        this.elasticsearchTemplate = elasticsearchTemplate;
        this.entityMapper = entityMapper;
    }

    /**
     * Requête principale
     *
     * @param searchOp
     * @param fuzzy
     * @return
     */
    protected abstract QueryBuilder getSearchQueryBuilder(final EsSearchOperation searchOp, final boolean fuzzy);

    /**
     * Requête de filtrage des résultats suivant les droits de l'utilisateur
     *
     * @param libraries
     * @return
     */
    protected abstract Optional<QueryBuilder> getLibraryQueryBuilder(final List<String> libraries);

    /**
     * Requête de filtrage des résultats de recherche (sélection de facettes)
     *
     * @param field
     * @param values
     * @return
     */
    protected QueryBuilder getFilterQueryBuilder(final String field, final List<String> values) {
        if (CollectionUtils.isNotEmpty(values)) {
            return QueryBuilders.termsQuery(field, values.toArray(new String[0]));
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
    protected QueryBuilder getDateFilterQueryBuilder(final String field, final List<String> values) {
        if (CollectionUtils.isNotEmpty(values)) {
            final EsQueryBuilder builder = new EsQueryBuilder();

            values.stream().map(val -> {
                final LocalDate from = DateUtils.parseStringToLocalDate(val, "dd/MM/yyyy");
                if (from == null) {
                    return null;
                }
                final LocalDate to = from.plusDays(1);

                return QueryBuilders.rangeQuery(field).gte(from.toString()).lt(to.toString());

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
    protected HighlightBuilder.Field[] getHighlightField() {
        return new HighlightBuilder.Field[0];
    }

    /**
     * Facettes
     *
     * @return
     */
    protected List<AbstractAggregationBuilder> getAggregationBuilders() {
        return Collections.emptyList();
    }

    /**
     * Recherche d'entité.
     * La requête utilisée est celle renvoyée par getSearchQueryBuilder
     *
     * @param searches
     * @param libraries
     * @param fuzzy
     * @param filters
     * @param pageable
     * @param facet
     * @return
     */
    public Page<T> search(final EsSearchOperation[] searches,
                          final List<String> libraries,
                          final boolean fuzzy,
                          final EsSearchOperation[] filters,
                          final PageRequest pageable,
                          final boolean facet) {
        final NativeSearchQueryBuilder builder = new NativeSearchQueryBuilder().withPageable(pageable);

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
        final HighlightBuilder.Field[] highlightFields = getHighlightField();
        for (final HighlightBuilder.Field field : highlightFields) {
            field.numOfFragments(0).preTags("<span class=\"result-match\">").postTags("</span>");
        }
        builder.withHighlightFields(highlightFields);

        // Facettes
        if (facet) {
            getAggregationBuilders().forEach(builder::addAggregation);
        }

        // Recherche
        return elasticsearchTemplate.queryForPage(builder.build(), clazz, new SearchResultMapper(entityMapper));
    }

    /**
     * Suggestions avec payload, et filtrage par contexte sur la bibliothèque
     *
     * @param text
     * @param size
     * @param libraries
     * @return
     */
    public List<Map<String, Object>> suggest(final String text, final int size, final List<String> libraries) {
        final SuggestionBuilder suggestionBuilder = new CompletionSuggestionFuzzyBuilder(SUGGEST_FIELD).text(text)
                                                                                                       .size(size)
                                                                                                       .field(SUGGEST_FIELD)
                                                                                                       .addContextField(SUGGEST_CTX_LIBRARY,
                                                                                                                        libraries);
        final SuggestResponse response = elasticsearchTemplate.suggest(suggestionBuilder, clazz);

        final Suggest.Suggestion<Suggest.Suggestion.Entry<Suggest.Suggestion.Entry.Option>> suggestions =
            response.getSuggest().getSuggestion(SUGGEST_FIELD);
        if (suggestions == null) {
            return Collections.emptyList();
        }
        return suggestions.getEntries()
                          .stream()
                          .flatMap(e -> e.getOptions().stream())
                          .map(option -> (CompletionSuggestion.Entry.Option) option)
                          .map(option -> {
                              final Map<String, Object> resultOption = new HashMap<>();
                              resultOption.put(FIELD_SUGG_TEXT, option.getText().string());

                              if (option.getPayload().length() > 0) {
                                  resultOption.put(FIELD_SUGG_PAYLOAD, option.getPayloadAsMap());
                              }
                              return resultOption;
                          })
                          .collect(Collectors.toList());
    }

    /**
     * Indexation des entités avec spécification de l'index
     *
     * @param index
     * @param entities
     */
    public void index(final String index, final Collection<T> entities) {
        if (entities.isEmpty()) {
            return;
        }
        final Client client = elasticsearchTemplate.getClient();
        final String type = entityInformation.getType();
        final BulkRequestBuilder bulkRequest = client.prepareBulk();

        // Indexation des entités mises à jour
        for (final T entity : entities) {
            try {
                bulkRequest.add(client.prepareIndex(index, type, entity.getIdentifier()).setSource(entityMapper.mapToString(entity)));

            } catch (IOException e) {
                throw new ElasticsearchException("Échec de l'indexation de l'entité [id: " + entity.getIdentifier() + "]", e);
            }
        }
        // Exécution de la requête
        executeBulk(index, bulkRequest);
    }

    /**
     * Recherche d'entités à partir de leurs identifiants
     *
     * @param identifiers
     * @return
     */
    public List<T> findByIds(final List<String> identifiers) {
        if (CollectionUtils.isEmpty(identifiers)) {
            return Collections.emptyList();
        }
        final SearchQuery searchQuery = new NativeSearchQueryBuilder().withIds(identifiers).build();
        return elasticsearchTemplate.multiGet(searchQuery, clazz);
    }

    /**
     * Exécution de la requête bulk avec gestion des erreurs et rafraichissement de l'index
     *
     * @param index
     * @param requestBuilder
     */
    protected void executeBulk(final String index, final BulkRequestBuilder requestBuilder) {
        if (requestBuilder.numberOfActions() > 0) {
            final BulkResponse response = requestBuilder.execute().actionGet();

            // Gestion des erreurs
            if (response.hasFailures()) {
                Map<String, String> failedDocuments = new HashMap<>();

                for (BulkItemResponse item : response.getItems()) {
                    if (item.isFailed()) {
                        failedDocuments.put(item.getId(), item.getFailureMessage());
                    }
                }
                throw new ElasticsearchException("Échec de la requête bulk [" + failedDocuments + "]", failedDocuments);
            }
            // Refresh
            elasticsearchTemplate.refresh(index);
        }
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
        return new String[] {obj, field};
    }

}
