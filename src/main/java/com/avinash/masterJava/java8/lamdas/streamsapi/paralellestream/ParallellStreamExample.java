package com.avinash.masterJava.java8.lamdas.streamsapi.paralellestream;

import java.util.function.Supplier;
import java.util.stream.IntStream;

public class ParallellStreamExample {
    private static int sum_sequential_stream(){
        return IntStream.rangeClosed(1,100000).sum();
    }
    private static int sum_parallel_stream(){
        return IntStream.rangeClosed(1,100000).parallel().sum();
    }

    private static int timeTakenFoExecution(Supplier<Integer> supplier, int noOfTimes){
        long startTime = System.currentTimeMillis();
        for(int i=0;i<noOfTimes;i++){
            supplier.get();
        }
        long endTime = System.currentTimeMillis();
        return (int)(endTime-startTime);
    }

    public static void main(String[] args) {
        System.out.println("Sequential Stream Result : "+timeTakenFoExecution(ParallellStreamExample::sum_sequential_stream,20));
        System.out.println("Parallel Stream Result : "+timeTakenFoExecution(ParallellStreamExample::sum_parallel_stream,20));
    }
}
