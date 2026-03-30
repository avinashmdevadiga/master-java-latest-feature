package com.avinash.masterJava.java8.lamdas.numericstream;

import java.util.stream.IntStream;

public class NumericStreamAggregateExample {
    public static void main(String[] args) {
        System.out.println("sum :: "+IntStream.range(1,50).sum());

        System.out.println("max ::"+IntStream.rangeClosed(1,100).max().getAsInt());
        System.out.println("min ::"+IntStream.rangeClosed(1,100).min().getAsInt());
        System.out.println("Avg ::"+IntStream.rangeClosed(1,100).average().getAsDouble());


    }
}
