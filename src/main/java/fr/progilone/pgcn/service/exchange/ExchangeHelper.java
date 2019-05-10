package fr.progilone.pgcn.service.exchange;

import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Created by Sebastien on 24/11/2016.
 */
public class ExchangeHelper {

    private ExchangeHelper() {
    }

    /**
     * Calcule le produit cartésien des listes passées en paramètres
     *
     * @param lists
     * @param <T>
     * @return
     */
    public static <T> List<List<T>> cartesianProduct(List<List<T>> lists) {
        if (lists == null || lists.isEmpty()) {
            return Collections.emptyList();
        }
        List<List<T>> combinations = Collections.singletonList(Collections.emptyList());

        for (List<T> list : lists) {
            List<List<T>> extraColumnCombinations = new ArrayList<>();

            for (List<T> combination : combinations) {
                for (T element : list) {
                    List<T> newCombination = new ArrayList<>(combination);
                    newCombination.add(element);
                    extraColumnCombinations.add(newCombination);
                }
            }
            combinations = extraColumnCombinations;
        }
        return combinations;
    }

    /**
     * Calcule le produit cartésien des listes passées en paramètres
     * <p>
     * Par ex.:
     * lists = { 100 => ["a", "b"], 200 => ["c"] }
     * return = [ { 100 => "a", 200 => "c" }, { 100 => "b", 200 => "c" } ]
     *
     * @param lists
     * @param <K>
     * @param <T>
     * @return
     */
    public static <K, T> List<Map<K, T>> cartesianProduct(Map<K, List<T>> lists) {
        if (lists == null || lists.isEmpty()) {
            return Collections.emptyList();
        }
        List<Map<K, T>> combinations = Collections.singletonList(Collections.emptyMap());

        for (Map.Entry<K, List<T>> list : lists.entrySet()) {
            List<Map<K, T>> extraColumnCombinations = new ArrayList<>();

            for (Map<K, T> combination : combinations) {
                for (T element : list.getValue()) {
                    Map<K, T> newCombination = new HashMap<>(combination);
                    newCombination.put(list.getKey(), element);
                    extraColumnCombinations.add(newCombination);
                }
            }
            combinations = extraColumnCombinations;
        }
        return combinations;
    }

    /**
     * Déduplique les bindings par rapport à une liste de clés
     * Chaque combinaison de valeurs issues des variables sera unique à la sortie de distinctBindings
     *
     * @param bindings
     * @param variables
     * @return
     */
    public static List<Map<String, Object>> distinctBindings(List<Map<String, Object>> bindings, List<String> variables) {
        final List<Map<String, Object>> distinctBindings = new ArrayList<>();

        for (final Map<String, Object> binding : bindings) {
            // Filtrage des bindings par rapport à la liste de marcKeys passés en paramètre
            final Map<String, Object> subBinding = variables.stream()
                                                            .filter(binding::containsKey)
                                                            .map(var -> Pair.of(var, binding.get(var)))
                                                            .collect(Collectors.toMap(Pair::getLeft, Pair::getRight));
            // Le sous-binding a-t-il déjà été ajouté à la liste distinctBindings ?
            final boolean found = distinctBindings.stream()
                                                  .anyMatch(oth -> oth.keySet().containsAll(subBinding.keySet())
                                                                   && subBinding.keySet().containsAll(oth.keySet())
                                                                   && variables.stream()
                                                                               .allMatch(tag -> Objects.equals(oth.get(tag), subBinding.get(tag))));
            if (!found) {
                distinctBindings.add(subBinding);
            }
        }
        return distinctBindings;
    }
}
