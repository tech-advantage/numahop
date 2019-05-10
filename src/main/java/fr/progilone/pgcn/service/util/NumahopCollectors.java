/**
 * 
 */
package fr.progilone.pgcn.service.util;

import java.util.stream.Collector;
import java.util.stream.Collectors;

/**
 * {@link Collectors} personnalisés
 * 
 * @author jbrunet
 * Créé le 12 oct. 2017
 */
public final class NumahopCollectors {
    public static <T> Collector<T, ?, T> singletonCollector() {
        return Collectors.collectingAndThen(
                Collectors.toList(),
                list -> {
                    if (list.size() != 1) {
                        throw new IllegalStateException();
                    }
                    return list.get(0);
                }
        );
    }
    
    public static <T> Collector<T, ?, T> zeroOrOneCollector() {
        return Collectors.collectingAndThen(
                Collectors.toList(),
                list -> {
                    if (list.size() > 1) {
                        throw new IllegalStateException();
                    } else if(list.size() == 1) {
                        return list.get(0);
                    }
                    return null;
                }
        );
    }
}
