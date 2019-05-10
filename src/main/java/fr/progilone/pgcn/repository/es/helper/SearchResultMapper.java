package fr.progilone.pgcn.repository.es.helper;

import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.HasAggregations;
import org.elasticsearch.search.aggregations.bucket.MultiBucketsAggregation;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.DefaultResultMapper;
import org.springframework.data.elasticsearch.core.EntityMapper;
import org.springframework.data.elasticsearch.core.aggregation.AggregatedPage;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Mapper qui récupère les résultats d'une requête elasticsearch "highlight"
 */
public final class SearchResultMapper extends DefaultResultMapper {

    private static final String RESPONSE_FACET_TITLE = "title";
    private static final String RESPONSE_FACET_COUNT = "count";

    public SearchResultMapper(final EntityMapper entityMapper) {
        super(entityMapper);
    }

    @Override
    public <A> AggregatedPage<A> mapResults(final SearchResponse response, final Class<A> clazz, final Pageable pageable) {
        final AggregatedPage<A> page = super.mapResults(response, clazz, pageable);
        final List<SearchResultPage.Highlight> highlights = getHighlights(response);
        final Map<String, List<Map<String, ?>>> aggregations = getAggregations(response.getAggregations());

        return new SearchResultPage<>(page, pageable, highlights, aggregations);
    }

    private List<SearchResultPage.Highlight> getHighlights(final SearchResponse response) {
        final List<SearchResultPage.Highlight> highlights = new ArrayList<>();

        for (SearchHit hit : response.getHits()) {
            if (hit != null) {
                hit.getHighlightFields()
                   .values()
                   .stream()
                   .flatMap(v -> Arrays.stream(v.getFragments()).map(frag -> new SearchResultPage.Highlight(hit.getId(), v.getName(), frag.string())))
                   .forEach(highlights::add);
            }
        }
        return highlights;
    }

    /**
     * Retourne les aggrégations sous une forme serialisable et exploitable
     *
     * @param aggregations
     * @return
     */
    private Map<String, List<Map<String, ?>>> getAggregations(final Aggregations aggregations) {
        final Map<String, List<Map<String, ?>>> flatMap = new HashMap<>();

        if (aggregations != null) {
            aggregations.forEach(aggregation -> {
                if (aggregation instanceof HasAggregations) {
                    final Aggregations nestedAggs = ((HasAggregations) aggregation).getAggregations();
                    flatMap.putAll(getAggregations(nestedAggs));

                } else if (aggregation instanceof MultiBucketsAggregation) {
                    final List<Map<String, ?>> result = getAggregation((MultiBucketsAggregation) aggregation);

                    if (!result.isEmpty()) {
                        flatMap.put(aggregation.getName(), result);
                    }
                }
            });
        }
        return flatMap;
    }

    private List<Map<String, ?>> getAggregation(final MultiBucketsAggregation aggregation) {
        return aggregation.getBuckets().stream().map(bucket -> {
            final Map<String, Object> facet = new HashMap<>();
            facet.put(RESPONSE_FACET_TITLE, bucket.getKeyAsString());
            facet.put(RESPONSE_FACET_COUNT, bucket.getDocCount());
            return facet;

        }).collect(Collectors.toList());
    }
}
