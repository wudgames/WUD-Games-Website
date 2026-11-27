package edu.wisc.wud.games.wud_games_website.util;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collector;
import java.util.stream.Collectors;


public class CustomCollectors {

    /**
     * Collects stream values into a LinkedHashMap, preserving encounter order.
     * Duplicate keys cause an IllegalStateException.
     * @param keyMapper a mapping function to produce keys
     * @param valueMapper a mapping function to produce values
     * @return a Collector to collect values in an insertion-ordered map
     */
    public static <T, K, U> Collector<T, ?, Map<K, U>> toSortedMap(
            Function<? super T, ? extends K> keyMapper,
            Function<? super T, ? extends U> valueMapper) {
        return Collectors.toMap(keyMapper,
                valueMapper,
                (u, v) -> { throw new IllegalStateException(String.format("Duplicate key %s", u)); },
                LinkedHashMap::new);
    }

}
