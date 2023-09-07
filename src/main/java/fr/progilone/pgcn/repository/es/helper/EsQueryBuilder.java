package fr.progilone.pgcn.repository.es.helper;

import co.elastic.clients.elasticsearch._types.query_dsl.BoolQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch._types.query_dsl.QueryBuilders;
import java.util.ArrayList;
import java.util.List;

public class EsQueryBuilder {

    private final List<SubQuery> subQueries = new ArrayList<>();

    /**
     * The clause (query) must appear in matching documents. However unlike must the score of the query will be ignored.
     *
     * @param queryBuilder
     * @return
     */
    public EsQueryBuilder filter(final Query queryBuilder) {
        return addQuery(EsBoolOperator.FILTER, queryBuilder);
    }

    /**
     * The clause (query) must appear in matching documents and will contribute to the score.
     *
     * @param queryBuilder
     * @return
     */
    public EsQueryBuilder must(final Query queryBuilder) {
        return addQuery(EsBoolOperator.MUST, queryBuilder);
    }

    /**
     * The clause (query) must not appear in the matching documents.
     *
     * @param queryBuilder
     * @return
     */
    public EsQueryBuilder mustNot(final Query queryBuilder) {
        return addQuery(EsBoolOperator.MUST_NOT, queryBuilder);
    }

    /**
     * The clause (query) should appear in the matching document. In a boolean query with no must or filter clauses, one or more should clauses must
     * match a document.
     *
     * @param queryBuilder
     * @return
     */
    public EsQueryBuilder should(final Query queryBuilder) {
        return addQuery(EsBoolOperator.SHOULD, queryBuilder);
    }

    /**
     * Ajout de la requête avec l'opérateur spécifié
     *
     * @param op
     * @param queryBuilder
     * @return
     */
    public EsQueryBuilder addQuery(final EsBoolOperator op, final Query queryBuilder) {
        if (queryBuilder != null) {
            this.subQueries.add(new SubQuery(op, queryBuilder));
        }
        return this;
    }

    public Query build() {
        if (subQueries.size() > 1) {
            final BoolQuery.Builder queryBuilder = QueryBuilders.bool().minimumShouldMatch("1");

            subQueries.forEach(subQ -> {
                switch (subQ.getType()) {
                    case FILTER:
                        queryBuilder.filter(subQ.getQueryBuilder());
                        break;
                    case MUST:
                        queryBuilder.must(subQ.getQueryBuilder());
                        break;
                    case MUST_NOT:
                        queryBuilder.mustNot(subQ.getQueryBuilder());
                        break;
                    case SHOULD:
                        queryBuilder.should(subQ.getQueryBuilder());
                        break;
                }
            });
            return queryBuilder.build()._toQuery();

        } else if (!subQueries.isEmpty()) {
            return subQueries.get(0).getQueryBuilder();

        } else {
            return QueryBuilders.matchAll().build()._toQuery();
        }
    }

    /**
     * Sous-requête
     */
    private static class SubQuery {

        private EsBoolOperator type;
        private Query queryBuilder;

        public SubQuery(final EsBoolOperator type, final Query queryBuilder) {
            this.type = type;
            this.queryBuilder = queryBuilder;
        }

        public EsBoolOperator getType() {
            return type;
        }

        public void setType(final EsBoolOperator type) {
            this.type = type;
        }

        public Query getQueryBuilder() {
            return queryBuilder;
        }

        public void setQueryBuilder(final Query queryBuilder) {
            this.queryBuilder = queryBuilder;
        }
    }
}
