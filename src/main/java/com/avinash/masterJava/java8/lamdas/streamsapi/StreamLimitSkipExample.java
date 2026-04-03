package com.avinash.masterJava.java8.lamdas.streamsapi;

import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

/**
 * Use Case: Demonstrates the Stream limit() and skip() intermediate operations.
 * - limit(n) — truncates the stream to at most n elements (useful for pagination / top-N).
 * - skip(n)  — discards the first n elements and processes the rest.
 * Together they enable sliding-window and page-based data processing patterns.
 */
public class StreamLimitSkipExample {

    private static final Logger LOGGER = Logger.getLogger(StreamLimitSkipExample.class.getName());

    private static Optional<Integer> limitExample(List<Integer> integerList){
        LOGGER.info("StreamLimitSkipExample :: limitExample - take first 3 elements then sum");
        return integerList.stream()
                .limit(3)
                .reduce(Integer::sum);
    }

    private static Optional<Integer> skipExample(List<Integer> integerList){
        LOGGER.info("StreamLimitSkipExample :: skipExample - skip first 3 elements then sum");
        return integerList.stream()
                .skip(3)
                .reduce(Integer::sum);
    }

    public static void main(String[] args) {
        LOGGER.info("StreamLimitSkipExample :: main started");
        List<Integer> integerList = List.of(2,4,5,2,6);

        limitExample(integerList).ifPresent(v -> { LOGGER.info("limit result: " + v); System.out.println(v); });
        skipExample(integerList).ifPresent(v  -> { LOGGER.info("skip result: "  + v); System.out.println(v); });

        LOGGER.info("StreamLimitSkipExample :: main finished");
    }
}
