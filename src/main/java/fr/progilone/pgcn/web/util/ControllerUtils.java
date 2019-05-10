package fr.progilone.pgcn.web.util;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class ControllerUtils {

    private ControllerUtils() {
    }

    /**
     * Utility to get a value from a Map generated with jackson
     *
     * @param source
     * @param keys
     * @return
     */
    public static Collection<Object> getValue(final Map<String, Object> source, final String... keys) {
        if (source != null && keys.length >= 1) {
            final Object value = source.get(keys[0]);

            if (keys.length > 1) {
                final String[] newKeys = Arrays.copyOfRange(keys, 1, keys.length);

                if (value instanceof Map) {
                    return getValue((Map<String, Object>) value, newKeys);
                } else if (value instanceof Collection) {
                    return ((Collection<Object>) value).stream()
                                                       .filter(v -> v instanceof Map)
                                                       .map(v -> getValue((Map<String, Object>) v, newKeys))
                                                       .filter(Objects::nonNull)
                                                       .flatMap(Collection::stream)
                                                       .collect(Collectors.toList());
                }
            } else {
                if (value instanceof Collection) {
                    return (Collection) value;
                } else {
                    return Collections.singletonList(value);
                }
            }
        }
        return null;
    }
}
