package fr.progilone.pgcn.repository.es.helper;

import com.google.common.base.MoreObjects;
import org.elasticsearch.search.aggregations.Aggregations;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.aggregation.AggregatedPage;
import org.springframework.data.elasticsearch.core.aggregation.impl.AggregatedPageImpl;
import org.springframework.data.elasticsearch.core.facet.FacetResult;

import java.beans.Transient;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class SearchResultPage<T> extends AggregatedPageImpl<T> {

    private final Pageable pageable;
    private final long total;
    private final List<Highlight> highlights = new ArrayList<>();
    private final Map<String, List<Map<String, ?>>> aggregations = new HashMap<>();

    public SearchResultPage(final AggregatedPage<T> page,
                            final Pageable pageable,
                            final List<Highlight> highlights,
                            final Map<String, List<Map<String, ?>>> aggregations) {
        super(page.getContent(), pageable, page.getTotalElements(), page.getAggregations());
        this.pageable = pageable;
        this.total = page.getTotalElements();
        this.highlights.addAll(highlights);
        this.aggregations.putAll(aggregations);
    }

    protected SearchResultPage(final List<T> content,
                               final Pageable pageable,
                               final long total,
                               final List<Highlight> highlights,
                               final Map<String, List<Map<String, ?>>> aggregations) {
        super(content, pageable, total);
        this.highlights.addAll(highlights);
        this.aggregations.putAll(aggregations);
        this.pageable = pageable;
        this.total = total;
    }

    public List<Highlight> getHighlights() {
        return highlights;
    }

    public Map<String, List<Map<String, ?>>> getAggs() {
        return aggregations;
    }

    @Transient
    @Override
    public List<FacetResult> getFacets() {
        return super.getFacets();
    }

    @Transient
    @Override
    public Aggregations getAggregations() {
        return super.getAggregations();
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
            return Objects.equals(id, highlight.id) && Objects.equals(field, highlight.field) && Objects.equals(text, highlight.text);
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

    @Override
    public <S> Page<S> map(final Converter<? super T, ? extends S> converter) {
        return new SearchResultPage<>(this.getConvertedContent(converter), this.pageable, this.total, this.highlights, this.aggregations);
    }
}
