package fr.progilone.pgcn.repository.es.helper;

import co.elastic.clients.elasticsearch._types.aggregations.Aggregate;
import com.google.common.base.MoreObjects;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchAggregations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;

public class SearchResultPage<T> extends PageImpl<T> {

    private static final String RESPONSE_FACET_TITLE = "title";
    private static final String RESPONSE_FACET_COUNT = "count";

    private final List<Highlight> highlights = new ArrayList<>();
    private final Map<String, List<Map<String, ?>>> aggregations = new HashMap<>();

    public SearchResultPage(final SearchHits<T> page, final Pageable pageable) {
        super(page.getSearchHits().stream().map(SearchHit::getContent).collect(Collectors.toList()), pageable, page.getTotalHits());
        this.highlights.addAll(getHighlights(page));
        this.aggregations.putAll(getAggregations(page));
    }

    protected SearchResultPage(final List<T> content,
                               final Pageable pageable,
                               final long total,
                               final List<Highlight> highlights,
                               final Map<String, List<Map<String, ?>>> aggregations) {
        super(content, pageable, total);
        this.highlights.addAll(highlights);
        this.aggregations.putAll(aggregations);
    }

    public List<Highlight> getHighlights() {
        return highlights;
    }

    public Map<String, List<Map<String, ?>>> getAggs() {
        return aggregations;
    }

    @Override
    public <U> Page<U> map(final Function<? super T, ? extends U> converter) {
        return new SearchResultPage<>(this.getConvertedContent(converter), this.getPageable(), this.getTotalElements(), this.highlights, this.aggregations);
    }

    private List<Highlight> getHighlights(final SearchHits<T> response) {
        final List<Highlight> highlights = new ArrayList<>();

        for (final SearchHit<T> hit : response.getSearchHits()) {
            if (hit != null) {
                hit.getHighlightFields().entrySet().stream().flatMap(e -> e.getValue().stream().map(frag -> new Highlight(hit.getId(), e.getKey(), frag))).forEach(highlights::add);
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
    private Map<String, List<Map<String, ?>>> getAggregations(final SearchHits<T> response) {
        final Map<String, List<Map<String, ?>>> flatMap = new HashMap<>();

        if (response.getAggregations() != null) {
            ((ElasticsearchAggregations) response.getAggregations()).aggregations().forEach(aggregation -> {
                final List<Map<String, ?>> aggs = getAggregation(aggregation.aggregation().getAggregate());
                if (!aggs.isEmpty()) {
                    flatMap.put(aggregation.aggregation().getName(), aggs);
                }
            });
        }
        return flatMap;
    }

    private List<Map<String, ?>> getAggregation(final Aggregate aggregate) {
        if (aggregate.isLterms()) {
            return aggregate.lterms()
                            .buckets()
                            .array()
                            .stream()
                            .map(bucket -> Map.of(RESPONSE_FACET_TITLE,
                                                  StringUtils.defaultString(bucket.keyAsString(), String.valueOf(bucket.key())),
                                                  RESPONSE_FACET_COUNT,
                                                  bucket.docCount()))
                            .collect(Collectors.toList());
        }
        if (aggregate.isSterms()) {
            return aggregate.sterms()
                            .buckets()
                            .array()
                            .stream()
                            .map(bucket -> Map.of(RESPONSE_FACET_TITLE, bucket.key()._toJsonString(), RESPONSE_FACET_COUNT, bucket.docCount()))
                            .collect(Collectors.toList());
        } else if (aggregate.isNested()) {
            return aggregate.nested().aggregations().values().stream().flatMap(a -> getAggregation(a).stream()).collect(Collectors.toList());
        } else if (aggregate.isFilter()) {
            return aggregate.filter().aggregations().values().stream().flatMap(a -> getAggregation(a).stream()).collect(Collectors.toList());
        } else if (aggregate.isDateHistogram()) {
            return aggregate.dateHistogram()
                            .buckets()
                            .array()
                            .stream()
                            .map(bucket -> Map.of(RESPONSE_FACET_TITLE, bucket.keyAsString(), RESPONSE_FACET_COUNT, bucket.docCount()))
                            .collect(Collectors.toList());
        } else {
            return Collections.emptyList();
        }
    }

    public static final class Highlight {

        private final String id;
        private final String field;
        private final String text;

        public Highlight(final String id, final String field, final String text) {
            this.id = id;
            this.field = field;
            this.text = text;
        }

        public String getId() {
            return id;
        }

        public String getField() {
            return field;
        }

        public String getText() {
            return text;
        }

        @Override
        public boolean equals(final Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            final Highlight highlight = (Highlight) o;
            return Objects.equals(id, highlight.id) && Objects.equals(field, highlight.field)
                   && Objects.equals(text, highlight.text);
        }

        @Override
        public int hashCode() {
            return Objects.hash(id, field, text);
        }

        @Override
        public String toString() {
            return MoreObjects.toStringHelper(this).add("id", id).add("field", field).add("text", text).toString();
        }
    }
}
