package com.avinash.masterJava.java8.lamdas.numericstream;

import java.util.stream.IntStream;
import java.util.logging.Logger;

/**
 * Use Case: Demonstrates built-in aggregate operations on numeric streams.
 * IntStream provides terminal operations — sum(), max(), min(), average() —
 * that operate directly on primitive int values without boxing overhead.
 * max/min/average return OptionalInt/OptionalDouble because the stream could be empty.
 */
public class NumericStreamAggregateExample {

    private static final Logger LOGGER = Logger.getLogger(NumericStreamAggregateExample.class.getName());

    public static void main(String[] args) {
        LOGGER.info("NumericStreamAggregateExample :: main started");

        LOGGER.info("sum() of IntStream.range(1,50) (exclusive 50)");
        System.out.println("sum :: "+IntStream.range(1,50).sum());

        LOGGER.info("max() of IntStream.rangeClosed(1,100)");
        System.out.println("max ::"+IntStream.rangeClosed(1,100).max().getAsInt());

        LOGGER.info("min() of IntStream.rangeClosed(1,100)");
        System.out.println("min ::"+IntStream.rangeClosed(1,100).min().getAsInt());

        LOGGER.info("average() of IntStream.rangeClosed(1,100)");
        System.out.println("Avg ::"+IntStream.rangeClosed(1,100).average().getAsDouble());

        LOGGER.info("NumericStreamAggregateExample :: main finished");
    }
}
