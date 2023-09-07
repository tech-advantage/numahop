package fr.progilone.pgcn.repository.es.helper;

import fr.progilone.pgcn.service.es.AbstractElasticsearchOperations.SearchEntity;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Sort;

public class EsSort {

    /**
     * @param rawSorts
     *            DOCUNIT:pgcnId=ASC
     * @param searchOn
     *            DOCUNIT
     * @return
     */
    public static Sort fromRawSorts(final String[] rawSorts, final SearchEntity searchOn) {
        if (ArrayUtils.isEmpty(rawSorts)) {
            return null;

        } else {
            final List<Sort.Order> orders = Arrays.stream(rawSorts)
                                                  .filter(s -> StringUtils.startsWith(s, searchOn.name() + ":"))
                                                  .map(s -> StringUtils.substring(s, searchOn.name().length() + 1))
                                                  .filter(StringUtils::isNotEmpty)
                                                  .map(EsSort::fromRawSort)
                                                  .collect(Collectors.toList());
            return orders.isEmpty() ? null
                                    : Sort.by(orders);
        }
    }

    private static Sort.Order fromRawSort(final String rawSort) {
        final String[] split = StringUtils.split(rawSort, "=", 2);
        String sort = "";
        Sort.Direction direction = Sort.Direction.ASC;

        if (split.length > 0) {
            sort = split[0];
        }
        if (split.length > 1) {
            direction = Sort.Direction.valueOf(split[1]);
        }
        return new Sort.Order(direction, sort);
    }
}
