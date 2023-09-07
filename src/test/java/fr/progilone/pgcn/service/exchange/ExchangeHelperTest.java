package fr.progilone.pgcn.service.exchange;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;

/**
 * Created by Sebastien on 24/11/2016.
 */
public class ExchangeHelperTest {

    @Test
    public void testCartesianProductOfLists() {
        // #1
        List<List<String>> actual = ExchangeHelper.cartesianProduct((List<List<String>>) null);
        assertTrue(actual.isEmpty());

        // #2
        actual = ExchangeHelper.cartesianProduct(Collections.emptyList());
        assertTrue(actual.isEmpty());

        // #3
        actual = ExchangeHelper.cartesianProduct(Collections.singletonList(Collections.emptyList()));
        assertTrue(actual.isEmpty());

        // #4
        final List<String> list1 = Arrays.asList("a", "b", "c");
        final List<String> list2 = Arrays.asList("d", "e");
        final List<String> list3 = Arrays.asList("f", "g", "h");

        actual = ExchangeHelper.cartesianProduct(Arrays.asList(list1, list2, list3));

        assertEquals(18, actual.size());
        check(Arrays.asList("a", "d", "f"), actual.get(0));
        check(Arrays.asList("a", "d", "g"), actual.get(1));
        check(Arrays.asList("a", "d", "h"), actual.get(2));
        check(Arrays.asList("a", "e", "f"), actual.get(3));
        check(Arrays.asList("a", "e", "g"), actual.get(4));
        check(Arrays.asList("a", "e", "h"), actual.get(5));
        check(Arrays.asList("b", "d", "f"), actual.get(6));
        check(Arrays.asList("b", "d", "g"), actual.get(7));
        check(Arrays.asList("b", "d", "h"), actual.get(8));
        check(Arrays.asList("b", "e", "f"), actual.get(9));
        check(Arrays.asList("b", "e", "g"), actual.get(10));
        check(Arrays.asList("b", "e", "h"), actual.get(11));
        check(Arrays.asList("c", "d", "f"), actual.get(12));
        check(Arrays.asList("c", "d", "g"), actual.get(13));
        check(Arrays.asList("c", "d", "h"), actual.get(14));
        check(Arrays.asList("c", "e", "f"), actual.get(15));
        check(Arrays.asList("c", "e", "g"), actual.get(16));
        check(Arrays.asList("c", "e", "h"), actual.get(17));
    }

    @Test
    public void testCartesianProductOfMaps() {
        // #1
        List<Map<String, String>> actual = ExchangeHelper.cartesianProduct((Map<String, List<String>>) null);
        assertTrue(actual.isEmpty());

        // #2
        actual = ExchangeHelper.cartesianProduct(Collections.emptyMap());
        assertTrue(actual.isEmpty());

        // #3
        final HashMap<String, List<String>> map = new HashMap<>();
        map.put("200", Arrays.asList("a", "b", "c"));
        map.put("400", Arrays.asList("d", "e"));

        actual = ExchangeHelper.cartesianProduct(map);

        assertEquals(6, actual.size());

        check("a", "d", actual.get(0));
        check("a", "e", actual.get(1));
        check("b", "d", actual.get(2));
        check("b", "e", actual.get(3));
        check("c", "d", actual.get(4));
        check("c", "e", actual.get(5));
    }

    @Test
    public void testDistinctBindings() {
        final List<String> variables = Arrays.asList("marc_200", "marc_400");
        final List<Map<String, Object>> bindings = new ArrayList<>();
        HashMap<String, Object> binding = new HashMap<>();
        binding.put("marc_200", "a");
        binding.put("marc_400", "d");
        binding.put("marc_700", "160");
        bindings.add(binding);
        binding = new HashMap<>();
        binding.put("marc_200", "a");
        binding.put("marc_400", "d");
        binding.put("marc_700", "160");
        bindings.add(binding);
        binding = new HashMap<>();
        binding.put("marc_200", "a");
        binding.put("marc_400", "d");
        bindings.add(binding);
        binding = new HashMap<>();
        binding.put("marc_200", "a");
        binding.put("marc_400", "d");
        binding.put("marc_600", "test");
        bindings.add(binding);
        binding = new HashMap<>();
        binding.put("marc_200", "a");
        binding.put("marc_600", "test");
        bindings.add(binding);
        binding = new HashMap<>();
        binding.put("marc_200", "y");
        binding.put("marc_400", "z");
        binding.put("marc_700", "160");
        bindings.add(binding);

        final List<Map<String, Object>> actual = ExchangeHelper.distinctBindings(bindings, variables);

        assertEquals(3, actual.size());
        // 0
        assertTrue(actual.get(0).containsKey("marc_200"));
        assertEquals("a", actual.get(0).get("marc_200"));
        assertTrue(actual.get(0).containsKey("marc_400"));
        assertEquals("d", actual.get(0).get("marc_400"));
        // 1
        assertTrue(actual.get(1).containsKey("marc_200"));
        assertEquals("a", actual.get(1).get("marc_200"));
        assertFalse(actual.get(1).containsKey("marc_400"));
        // 2
        assertTrue(actual.get(2).containsKey("marc_200"));
        assertEquals("y", actual.get(2).get("marc_200"));
        assertTrue(actual.get(2).containsKey("marc_400"));
        assertEquals("z", actual.get(2).get("marc_400"));
    }

    private void check(final List<String> expected, final List<String> actual) {
        assertTrue(expected.containsAll(actual) && actual.containsAll(expected));
    }

    private void check(final String key200, final String key400, final Map<String, String> actual) {
        assertTrue(actual.containsKey("200"));
        assertEquals(key200, actual.get("200"));

        assertTrue(actual.containsKey("400"));
        assertEquals(key400, actual.get("400"));
    }
}
