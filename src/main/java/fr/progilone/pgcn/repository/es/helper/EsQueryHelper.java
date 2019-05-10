package fr.progilone.pgcn.repository.es.helper;

import fr.progilone.pgcn.service.es.EsConstant;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.common.unit.Fuzziness;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.BoostableQueryBuilder;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.index.query.MultiMatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.RangeQueryBuilder;

import javax.persistence.metamodel.Attribute;
import java.util.Arrays;

import static fr.progilone.pgcn.service.es.EsConstant.*;

public class EsQueryHelper {

    private EsQueryHelper() {
    }

    /**
     * Construction d'une recherche fulltext
     * Le champ field contient tous les sous-champs nécessaires
     *
     * @param field
     * @param search
     * @param fuzzySearch
     * @return
     */
    public static <Q extends QueryBuilder & BoostableQueryBuilder<Q>> Q getFullTextQueryBuilder(final String field,
                                                                                                final String search,
                                                                                                final boolean fuzzySearch) {
        final BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery()
                                                           // Recherche exacte de search sur l'ensemble du champ
                                                           .should(QueryBuilders.matchPhraseQuery(path(field, EsConstant.SUBFIELD_PHRASE), search)
                                                                                .fuzziness(Fuzziness.ZERO)
                                                                                .boost(10))
                                                           // Recherche de search comme préfixe de la totalité du champ
                                                           .should(QueryBuilders.matchPhrasePrefixQuery(path(field, SUBFIELD_PHRASE), search)
                                                                                .maxExpansions(100)
                                                                                .boost(8))
                                                           // Match sur tous les mots, AND
                                                           .should(getMatchQueryBuilder(field, search, false).boost(5))
                                                           // Multi-match sur la casse, OR
                                                           .should(getMultiMatchQueryBuilder(field, search, false).boost(3));

        // Recherche approchée
        if (fuzzySearch) {
            queryBuilder
                // Match sur tous les mots, AND
                .should(getMatchQueryBuilder(field, search, true).boost(2))
                // Multi-match sur la casse, OR
                .should(getMultiMatchQueryBuilder(field, search, true));
        }
        return (Q) queryBuilder;
    }

    /**
     * Construction d'une recherche exacte
     *
     * @param field
     * @param search
     * @param <Q>
     * @return
     */
    public static <Q extends QueryBuilder & BoostableQueryBuilder<Q>> Q getExactQueryBuilder(final String field, final String search) {
        return (Q) QueryBuilders.termQuery(field, search);
    }

    /**
     * Construction d'une recherche exacte
     *
     * @param field
     * @param search
     * @param <Q>
     * @return
     */
    public static <Q extends QueryBuilder & BoostableQueryBuilder<Q>> Q getExactQueryBuilder(final String field, final boolean search) {
        return (Q) QueryBuilders.termQuery(field, search);
    }

    /**
     * Recherche d'un date par rapport à une plage donnée
     *
     * @param field
     * @param search
     * @param <Q>
     * @return
     */
    public static <Q extends QueryBuilder & BoostableQueryBuilder<Q>> Q getRangeQueryBuilder(final String field, final String search) {
        final String[] range = search.split(":");
        final RangeQueryBuilder queryBuilder = QueryBuilders.rangeQuery(field);
        // borne inférieure
        if (range.length > 0 && StringUtils.isNotBlank(range[0])) {
            queryBuilder.gte(range[0]);
        }
        // borne supérieure
        if (range.length > 1 && StringUtils.isNotBlank(range[1])) {
            queryBuilder.lte(range[1]);
        }
        return (Q) queryBuilder;
    }

    private static MatchQueryBuilder getMatchQueryBuilder(final String field, final String search, final boolean fuzzySearch) {
        final MatchQueryBuilder matchQry = QueryBuilders.matchQuery(path(field, SUBFIELD_CI_AI), search)
                                                        .type(MatchQueryBuilder.Type.BOOLEAN)
                                                        .operator(MatchQueryBuilder.Operator.AND)
                                                        .cutoffFrequency(0.001f);
        // Fuzzy
        if (fuzzySearch) {
            matchQry.fuzziness(Fuzziness.AUTO).maxExpansions(100);
        } else {
            matchQry.fuzziness(Fuzziness.ZERO);
        }
        return matchQry;
    }

    private static MultiMatchQueryBuilder getMultiMatchQueryBuilder(final String field, final String search, final boolean fuzzySearch) {
        final MultiMatchQueryBuilder multiMatchQry =
            QueryBuilders.multiMatchQuery(search, field, path(field, SUBFIELD_CI_AS), path(field, SUBFIELD_CI_AI))
                         .type(MultiMatchQueryBuilder.Type.MOST_FIELDS)
                         .cutoffFrequency(0.001f);
        // Fuzzy
        if (fuzzySearch) {
            multiMatchQry.fuzziness(Fuzziness.AUTO).maxExpansions(100);
        } else {
            multiMatchQry.fuzziness(Fuzziness.ZERO);
        }
        return multiMatchQry;
    }

    public static String path(final String... fields) {
        return StringUtils.join(fields, '.');
    }

    public static String path(final Attribute<?, ?>... fields) {
        return Arrays.stream(fields).map(Attribute::getName).reduce((a, b) -> a + "." + b).orElse("");
    }
}
