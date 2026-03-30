package com.avinash.masterJava.java8.lamdas.numericstream;

import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.LongStream;

public class IntStreamExample {

    private static int sumOfNumber(List<Integer> integerList){
        return integerList.stream()//lot of unboxing is happens during this process. it will convert object type to primitive type
                .reduce(0,Integer::sum);
    }

    private static int sumOfNumberbyIntStream(){
        return IntStream.rangeClosed(1,6)
                .sum();
    }

    public static void main(String[] args) {
        System.out.println("sum of number : "+sumOfNumber(List.of(1,2,3,4,5,6)));
        System.out.println("sum of number by int stream: "+sumOfNumberbyIntStream());

        System.out.println("IntStream.range "+IntStream.range(1,50).count());
        IntStream.range(1,50).forEach(a->System.out.print(a+", "));
        System.out.println("IntStream.rangeClosed "+IntStream.rangeClosed(1,50).count());
        IntStream.rangeClosed(1,50).forEach(a->System.out.print(a+", "));
        System.out.println("LongStream.range "+LongStream.range(1,50).count());
        LongStream.range(1,50).forEach(a->System.out.print(a+", "));
        System.out.println("LongStream.rangeClosed "+LongStream.rangeClosed(1,50).count());
        LongStream.rangeClosed(1,50).forEach(a->System.out.print(a+", "));
        System.out.println("double stream ");
        IntStream.rangeClosed(1,50).asDoubleStream().forEach(a->System.out.print(a+", "));
    }
}
