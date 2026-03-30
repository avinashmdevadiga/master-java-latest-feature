package com.avinash.masterJava.java8.lamdas.streamsapi;

import java.util.Random;
import java.util.stream.Stream;

public class StreamOfIterateGenarateExample {
    public static void main(String[] args) {
        Stream<Integer> integerStream = Stream.of(1, 2, 3, 4, 5, 3);

        integerStream.forEach(System.out::println);

        Integer reduce = Stream.iterate(1, (a) -> a + 1)
                .limit(100)
                .reduce(1, Integer::sum);

        System.out.println(reduce);

        Stream.generate(new Random()::nextInt)
                .limit(5)
                        .forEach(System.out::println);


    }
}
