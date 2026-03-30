package com.avinash.masterJava.java8.lamdas.numericstream;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class IntStreamBoxingUnBoxingExample {
    public static void main(String[] args) {
        List<Integer> collect = IntStream.rangeClosed(1, 10)
                //primitive tye int value
                .boxed()
                //Wrapper type : Integer
                .toList();
        System.out.println("Boxing :: "+collect);

        int sum = collect.stream()
                //Wrapper Type :  Integer
                .mapToInt(Integer::valueOf)
                // Primittive type :int
                .sum();
        System.out.println("Sum :: "+sum);

        List<Integer> collect1 = IntStream.rangeClosed(1, 10)
                .mapToObj(a -> new Integer(a))//here we can use any custom object
                .toList();

        System.out.println("collect1 :"+collect1);

        long sum1 = IntStream.rangeClosed(1, 10)
                .mapToLong(Long::valueOf)
                .sum();
        System.out.println("long sum :"+sum1);

        double sum2 = IntStream.rangeClosed(1, 10)
                .mapToDouble(Double::valueOf)
                .sum();
        System.out.println("double sum :"+sum2);



    }
}
