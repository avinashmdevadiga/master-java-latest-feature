package com.avinash.masterJava.java8.lamdas.streamsapi.parallelstream;

import java.awt.*;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class ParallelStreamMoreTimeExample {

    private static int sumSequntially(List<Integer> integerList){
        long startTime = System.currentTimeMillis();
        int sum = integerList.stream().reduce(0,Integer::sum);
        long duration = System.currentTimeMillis() - startTime;
        System.out.println("Duration for sequantial process :"+duration);
        return sum;
    }
    private static int sumParallely(List<Integer> integerList){
        long startTime = System.currentTimeMillis();
        int sum = integerList.parallelStream().reduce(0,Integer::sum);
        long duration = System.currentTimeMillis() - startTime;
        System.out.println("Duration for parallely process :"+duration);
        return sum;
    }

    public static void main(String[] args) {

        List<Integer> integerList = IntStream.rangeClosed(1,10000)
                .boxed()
                .toList();
        // sometime sequential is faster than paralell process we need to decide when to use paralell stream.

        sumSequntially(integerList);
        sumParallely(integerList);
    }
}
