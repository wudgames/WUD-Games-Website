package edu.wisc.wud.games.wud_games_website.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;

class CustomCollectorsTest {

    @Test
    void preservesStreamEncounterOrder() {
        Map<String, String> result = Stream.of("third", "first", "second")
                .collect(CustomCollectors.toSortedMap(value -> value, value -> value));

        assertEquals(List.of("third", "first", "second"), new ArrayList<>(result.keySet()));
    }

    @Test
    void rejectsDuplicateKeys() {
        assertThrows(IllegalStateException.class,
                () -> Stream.of("duplicate", "duplicate")
                        .collect(CustomCollectors.toSortedMap(value -> value, value -> value)));
    }
}