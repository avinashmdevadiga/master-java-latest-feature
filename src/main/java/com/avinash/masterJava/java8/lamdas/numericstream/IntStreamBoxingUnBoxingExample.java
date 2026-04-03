package com.avinash.masterJava.java8.lamdas.numericstream;

import java.util.List;
import java.util.stream.IntStream;
import java.util.logging.Logger;

/**
 * Use Case: Demonstrates boxing and unboxing between primitive streams and wrapper streams.
 * - boxed()     converts a primitive IntStream into Stream<Integer> (boxing: int → Integer).
 * - mapToInt()  converts Stream<Integer> back to IntStream (unboxing: Integer → int).
 * - mapToObj()  converts each primitive element to any object type.
 * - mapToLong() / mapToDouble() widen the primitive type for larger computations.
 * Understanding boxing/unboxing is important for avoiding hidden performance costs.
 */
public class IntStreamBoxingUnBoxingExample {

    private static final Logger LOGGER = Logger.getLogger(IntStreamBoxingUnBoxingExample.class.getName());

    public static void main(String[] args) {
        LOGGER.info("IntStreamBoxingUnBoxingExample :: main started");

        LOGGER.info("Boxing: IntStream.rangeClosed(1,10).boxed() → List<Integer>");
        List<Integer> collect = IntStream.rangeClosed(1, 10)
                .boxed()          // int → Integer
                .toList();
        System.out.println("Boxing :: "+collect);

        LOGGER.info("Unboxing: stream().mapToInt() → IntStream, then sum()");
        int sum = collect.stream()
                .mapToInt(Integer::valueOf)   // Integer → int
                .sum();
        System.out.println("Sum :: "+sum);

        LOGGER.info("mapToObj(): converting each int to an Integer object");
        List<Integer> collect1 = IntStream.rangeClosed(1, 10)
                .mapToObj(a -> new Integer(a))
                .toList();
        System.out.println("collect1 :"+collect1);

        LOGGER.info("mapToLong(): widening int → long, then sum()");
        long sum1 = IntStream.rangeClosed(1, 10)
                .mapToLong(Long::valueOf)
                .sum();
        System.out.println("long sum :"+sum1);

        LOGGER.info("mapToDouble(): widening int → double, then sum()");
        double sum2 = IntStream.rangeClosed(1, 10)
                .mapToDouble(Double::valueOf)
                .sum();
        System.out.println("double sum :"+sum2);

        LOGGER.info("IntStreamBoxingUnBoxingExample :: main finished");
    }
}
