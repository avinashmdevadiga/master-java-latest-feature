package com.avinash.masterJava.java8.lamdas.numericstream;

import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.LongStream;
import java.util.logging.Logger;

/**
 * Use Case: Demonstrates primitive numeric streams (IntStream, LongStream, DoubleStream).
 * Primitive streams avoid the boxing/unboxing overhead of Stream<Integer> by working
 * directly with int/long/double values.
 * - IntStream.range()       — exclusive upper bound
 * - IntStream.rangeClosed() — inclusive upper bound
 * - LongStream equivalents
 * - asDoubleStream()        — converts IntStream to DoubleStream
 * Also contrasts Stream<Integer>.reduce (with boxing) vs IntStream.sum (primitive, faster).
 */
public class IntStreamExample {

    private static final Logger LOGGER = Logger.getLogger(IntStreamExample.class.getName());

    private static int sumOfNumber(List<Integer> integerList){
        LOGGER.info("IntStreamExample :: sumOfNumber - uses Stream<Integer> with unboxing overhead");
        return integerList.stream()
                .reduce(0, Integer::sum);
    }

    private static int sumOfNumberbyIntStream(){
        LOGGER.info("IntStreamExample :: sumOfNumberbyIntStream - uses IntStream.rangeClosed (no boxing)");
        return IntStream.rangeClosed(1,6).sum();
    }

    public static void main(String[] args) {
        LOGGER.info("IntStreamExample :: main started");
        System.out.println("sum of number : "+sumOfNumber(List.of(1,2,3,4,5,6)));
        System.out.println("sum of number by int stream: "+sumOfNumberbyIntStream());

        LOGGER.info("IntStream.range(1,50) — exclusive, count=" + IntStream.range(1,50).count());
        System.out.println("IntStream.range "+IntStream.range(1,50).count());
        IntStream.range(1,50).forEach(a->System.out.print(a+", "));

        LOGGER.info("IntStream.rangeClosed(1,50) — inclusive, count=" + IntStream.rangeClosed(1,50).count());
        System.out.println("IntStream.rangeClosed "+IntStream.rangeClosed(1,50).count());
        IntStream.rangeClosed(1,50).forEach(a->System.out.print(a+", "));

        LOGGER.info("LongStream.range(1,50)");
        System.out.println("LongStream.range "+LongStream.range(1,50).count());
        LongStream.range(1,50).forEach(a->System.out.print(a+", "));

        LOGGER.info("LongStream.rangeClosed(1,50)");
        System.out.println("LongStream.rangeClosed "+LongStream.rangeClosed(1,50).count());
        LongStream.rangeClosed(1,50).forEach(a->System.out.print(a+", "));

        LOGGER.info("Converting IntStream to DoubleStream via asDoubleStream()");
        System.out.println("double stream ");
        IntStream.rangeClosed(1,50).asDoubleStream().forEach(a->System.out.print(a+", "));

        LOGGER.info("IntStreamExample :: main finished");
    }
}
