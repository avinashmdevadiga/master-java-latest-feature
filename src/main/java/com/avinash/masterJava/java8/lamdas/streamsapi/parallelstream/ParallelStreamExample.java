package com.avinash.masterJava.java8.lamdas.streamsapi.parallelstream;

import java.sql.Time;
import java.util.function.Supplier;
import java.util.stream.IntStream;

public class ParallelStreamExample {

    private static int sumSequenctially(){
        return IntStream.rangeClosed(1, 100000)
                .sum();

    }
    private static int sumParallelly(){
       return IntStream.rangeClosed(1, 100000)
                .parallel()
                .sum();
    }

    private static long timeToExecute(Supplier<Integer> supplier, int noOfTime){
        long start = System.currentTimeMillis();
        for (int i = 0 ; i<noOfTime;i++){
            supplier.get();
        }
        long end = System.currentTimeMillis();
        return end-start;
    }
    public static void main(String[] args) {
//        System.out.println("sequentialy :: "+sumSequenctially());
//        System.out.println("Paralelly :: "+sumParallelly());

        System.out.println("no of processor available in the machin "+ Runtime.getRuntime().availableProcessors());

//        System.out.println("time taken for Sequential process :: "+ timeToExecute(ParallelStreamExample::sumSequenctially, 120));
//        System.out.println("time taken for Parallel process :: "+ timeToExecute(ParallelStreamExample::sumParallelly,120));
    }
}
