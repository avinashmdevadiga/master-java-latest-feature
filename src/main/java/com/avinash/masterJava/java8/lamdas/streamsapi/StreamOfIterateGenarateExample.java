package com.avinash.masterJava.java8.lamdas.streamsapi;

import java.util.Random;
import java.util.stream.Stream;
import java.util.logging.Logger;

/**
 * Use Case: Demonstrates three ways to create a Stream without a backing Collection.
 * - Stream.of(...)       — creates a finite stream from explicit values.
 * - Stream.iterate(seed, f) — creates an infinite ordered stream by repeatedly applying f.
 *   Must be paired with limit() to make it finite before a terminal operation.
 * - Stream.generate(Supplier) — creates an infinite unordered stream from a Supplier.
 *   Also needs limit() to terminate.
 */
public class StreamOfIterateGenarateExample {

    private static final Logger LOGGER = Logger.getLogger(StreamOfIterateGenarateExample.class.getName());

    public static void main(String[] args) {
        LOGGER.info("StreamOfIterateGenarateExample :: main started");

        LOGGER.info("Stream.of() — finite stream from fixed values");
        Stream<Integer> integerStream = Stream.of(1, 2, 3, 4, 5, 3);
        integerStream.forEach(System.out::println);

        LOGGER.info("Stream.iterate(1, a->a+1).limit(100) — summing 1..100");
        Integer reduce = Stream.iterate(1, (a) -> a + 1)
                .limit(100)
                .reduce(1, Integer::sum);
        System.out.println(reduce);

        LOGGER.info("Stream.generate(Random::nextInt).limit(5) — 5 random integers");
        Stream.generate(new Random()::nextInt)
                .limit(5)
                .forEach(System.out::println);

        LOGGER.info("StreamOfIterateGenarateExample :: main finished");
    }
}
