package fr.progilone.pgcn.repository.es.helper;

import com.google.common.base.MoreObjects;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;

import static fr.progilone.pgcn.repository.es.helper.EsBoolOperator.*;

/**
 * Classe représentant un élément de recherche
 *
 * @author Sebastien
 */
public class EsSearchOperation {

    private static final String INDEX_DEFAULT = "default";

    public static EsSearchOperation[] fromRawSearches(final String[] rawSearches) {
        if (ArrayUtils.isEmpty(rawSearches)) {
            return new EsSearchOperation[0];
        } else {
            final EsSearchOperation[] searchOp =
                Arrays.stream(rawSearches).filter(StringUtils::isNotBlank).map(EsSearchOperation::fromRawSearch).toArray(EsSearchOperation[]::new);

            // recopie du 2nd opérateur dans la 1e recherche
            if (searchOp.length > 1 && (searchOp[1].operator == MUST || searchOp[1].operator == SHOULD || searchOp[1].operator == FILTER)) {
                searchOp[0].operator = searchOp[1].operator;
            } else {
                searchOp[0].operator = SHOULD;
            }
            return searchOp;
        }
    }

    public static EsSearchOperation[] fromRawFilters(final String[] rawFilters) {
        if (ArrayUtils.isEmpty(rawFilters)) {
            return new EsSearchOperation[0];
        } else {
            return Arrays.stream(rawFilters).map(EsSearchOperation::fromRawFilter).toArray(EsSearchOperation[]::new);
        }
    }

    private static EsSearchOperation fromRawSearch(final String rawSearch) {
        final EsSearchOperation field = new EsSearchOperation();
        // default values
        field.operator = SHOULD;
        field.index = INDEX_DEFAULT;
        field.search = "";

        if (StringUtils.isNotBlank(rawSearch)) {
            String[] split = StringUtils.split(rawSearch, "=", 2);

            // "Les misérables"
            if (split.length == 1) {
                field.search = split[0];

            } else if (split.length > 1) {
                if (EsBoolOperator.isValid(split[0])) {
                    field.operator = EsBoolOperator.valueOf(split[0]);
                    split = StringUtils.split(split[1], "=", 2);

                    // "MUST=Les misérables"
                    if (split.length == 1) {
                        field.search = split[0];
                    }
                    // "MUST=titre=Les misérables"
                    else {
                        field.index = split[0];
                        field.search = split[1];
                    }
                }
                // "titre=Les misérables"
                else {
                    field.index = split[0];
                    field.search = split[1];
                }
            }
        }
        return field;
    }

    private static EsSearchOperation fromRawFilter(final String rawFacet) {
        final EsSearchOperation field = new EsSearchOperation();
        // default values
        field.operator = FILTER;
        field.index = INDEX_DEFAULT;
        field.search = "";

        if (StringUtils.isNotBlank(rawFacet)) {
            final String[] split = StringUtils.split(rawFacet, "=", 2);

            // "Les misérables"
            if (split.length == 1) {
                field.search = split[0];
            }
            // "titre=Les misérables"
            else if (split.length > 1) {
                field.index = split[0];
                field.search = split[1];
            }
        }
        return field;
    }

    private EsBoolOperator operator;

    private String index;

    private String search;

    public EsSearchOperation() {
    }

    public EsSearchOperation(EsBoolOperator operator, String index, String search) {
        this.operator = operator;
        this.index = index;
        this.search = search;
    }

    public String getIndex() {
        return index;
    }

    public EsBoolOperator getOperator() {
        return operator;
    }

    public String getSearch() {
        return search;
    }

    public void setIndex(final String index) {
        this.index = index;
    }

    public void setOperator(final EsBoolOperator operator) {
        this.operator = operator;
    }

    public void setSearch(final String search) {
        this.search = search;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this).omitNullValues().add("operator", operator).add("index", index).add("search", search).toString();
    }
}
