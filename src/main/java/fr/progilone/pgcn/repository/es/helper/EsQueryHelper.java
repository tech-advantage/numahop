package fr.progilone.pgcn.repository.es.helper;

import static fr.progilone.pgcn.service.es.EsConstant.*;

import co.elastic.clients.elasticsearch._types.FieldValue;
import co.elastic.clients.elasticsearch._types.query_dsl.BoolQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.MatchQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.MultiMatchQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch._types.query_dsl.QueryBuilders;
import co.elastic.clients.elasticsearch._types.query_dsl.RangeQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.TextQueryType;
import co.elastic.clients.json.JsonData;
import fr.progilone.pgcn.service.es.EsConstant;
import jakarta.persistence.metamodel.Attribute;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import org.apache.commons.lang3.StringUtils;

public class EsQueryHelper {

    private static final String FUZZINESS_AUTO = "AUTO";

    private EsQueryHelper() {
    }

    /**
     * Construction d'une recherche fulltext
     * Le champ field contient tous les sous-champs nécessaires
     */
    public static Query getFullTextQueryBuilder(final String field, final String search, final boolean fuzzySearch) {
        final BoolQuery.Builder queryBuilder = QueryBuilders.bool()
                                                            // Recherche exacte de search sur l'ensemble du champ
                                                            .should(QueryBuilders.matchPhrase(b -> b.field(path(field, EsConstant.SUBFIELD_PHRASE)).query(search).boost(10F)))
                                                            // Recherche de search comme préfixe de la totalité du champ
                                                            .should(QueryBuilders.matchPhrasePrefix(b -> b.field(path(field, SUBFIELD_PHRASE))
                                                                                                          .query(search)
                                                                                                          .maxExpansions(100)
                                                                                                          .boost(8F)))
                                                            // Match sur tous les mots, AND
                                                            .should(getMatchQueryBuilder(field, search, false).boost(5F).build()._toQuery())
                                                            // Multi-match sur la casse, OR
                                                            .should(getMultiMatchQueryBuilder(field, search, false).boost(3F).build()._toQuery());

        // Recherche approchée
        if (fuzzySearch) {
            queryBuilder
                        // Match sur tous les mots, AND
                        .should(getMatchQueryBuilder(field, search, true).boost(2F).build()._toQuery())
                        // Multi-match sur la casse, OR
                        .should(getMultiMatchQueryBuilder(field, search, true).build()._toQuery());
        }
        return queryBuilder.build()._toQuery();
    }

    /**
     * Construction d'une recherche exacte
     */
    public static Query getExactQueryBuilder(final String field, final Collection<String> values) {
        return QueryBuilders.terms(b -> b.field(field).terms(t -> t.value(values.stream().map(FieldValue::of).toList())));
    }

    /**
     * Construction d'une recherche exacte
     */
    public static Query getExactQueryBuilder(final String field, final String search) {
        return QueryBuilders.term(b -> b.field(field).value(search));
    }

    /**
     * Construction d'une recherche exacte
     */
    public static Query getExactQueryBuilder(final String field, final boolean search) {
        return QueryBuilders.term(b -> b.field(field).value(search));
    }

    /**
     * Recherche d'un date par rapport à une plage donnée
     */
    public static Query getRangeQueryBuilder(final String field, final String search) {
        final String[] range = search.split(":");
        final RangeQuery.Builder queryBuilder = QueryBuilders.range().field(field);
        // borne inférieure
        if (range.length > 0 && StringUtils.isNotBlank(range[0])) {
            queryBuilder.gte(JsonData.of(range[0]));
        }
        // borne supérieure
        if (range.length > 1 && StringUtils.isNotBlank(range[1])) {
            queryBuilder.lte(JsonData.of(range[1]));
        }
        return queryBuilder.build()._toQuery();
    }

    private static MatchQuery.Builder getMatchQueryBuilder(final String field, final String search, final boolean fuzzySearch) {
        final MatchQuery.Builder matchQry = QueryBuilders.match().field(path(field, SUBFIELD_CI_AI)).query(search);
        // Fuzzy
        if (fuzzySearch) {
            matchQry.fuzziness(FUZZINESS_AUTO).maxExpansions(100);
        }
        return matchQry;
    }

    private static MultiMatchQuery.Builder getMultiMatchQueryBuilder(final String field, final String search, final boolean fuzzySearch) {
        final MultiMatchQuery.Builder multiMatchQry = QueryBuilders.multiMatch()
                                                                   .fields(List.of(field, path(field, SUBFIELD_CI_AS), path(field, SUBFIELD_CI_AI)))
                                                                   .query(search)
                                                                   .type(TextQueryType.MostFields);
        // Fuzzy
        if (fuzzySearch) {
            multiMatchQry.fuzziness(FUZZINESS_AUTO).maxExpansions(100);
        }
        return multiMatchQry;
    }

    public static String path(final String... fields) {
        return StringUtils.join(fields, '.');
    }

    public static String path(final Attribute<?, ?>... fields) {
        return Arrays.stream(fields)
                     .map(Attribute::getName)
                     .reduce((a, b) -> a + "."
                                       + b)
                     .orElse("");
    }
}
