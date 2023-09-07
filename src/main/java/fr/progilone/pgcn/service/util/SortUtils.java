package fr.progilone.pgcn.service.util;

import java.util.List;
import java.util.stream.Collectors;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.data.domain.Sort;

public class SortUtils {

    private SortUtils() {
    }

    /**
     * Construit et Retoune un objet Sort depuis une liste de propriétés
     * à trier obéissant au format direction du tri + nom propriété
     * exemple : name, -age etc
     *
     * @param sortProperties
     * @return
     */
    public static Sort getSort(final List<String> sortProperties) {
        if (CollectionUtils.isNotEmpty(sortProperties)) {
            final List<Sort.Order> orders = sortProperties.stream().filter(property -> property != null && property.length() > 0).map(property -> {
                final boolean reverse = property.charAt(0) == '-';
                final Sort.Direction sortDirection = reverse ? Sort.Direction.DESC
                                                             : Sort.Direction.ASC;
                final String sortField = reverse ? property.substring(1)
                                                 : property;
                return new Sort.Order(sortDirection, sortField);

            }).collect(Collectors.toList());

            return Sort.by(orders);
        }
        return Sort.unsorted();
    }
}
