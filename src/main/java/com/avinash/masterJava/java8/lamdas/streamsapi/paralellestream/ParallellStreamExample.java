package com.avinash.masterJava.java8.lamdas.streamsapi.paralellestream;

import java.util.function.Supplier;
import java.util.stream.IntStream;
import java.util.logging.Logger;

/**
 * Use Case: Demonstrates sequential vs parallel stream performance.
 * - Sequential stream processes elements one at a time on a single thread.
 * - Parallel stream (via .parallel()) splits the data across multiple CPU cores
 *   using the Fork/Join framework, potentially reducing elapsed time for CPU-intensive tasks.
 * The timeTakenForExecution helper benchmarks both approaches over multiple repetitions
 * to make the difference measurable and statistically meaningful.
 */
public class ParallellStreamExample {

    private static final Logger LOGGER = Logger.getLogger(ParallellStreamExample.class.getName());

    private static int sum_sequential_stream(){
        return IntStream.rangeClosed(1,100000).sum();
    }

    private static int sum_parallel_stream(){
        return IntStream.rangeClosed(1,100000).parallel().sum();
    }

    private static int timeTakenFoExecution(Supplier<Integer> supplier, int noOfTimes){
        LOGGER.info("ParallellStreamExample :: timeTakenFoExecution - running " + noOfTimes + " iterations");
        long startTime = System.currentTimeMillis();
        for(int i=0;i<noOfTimes;i++){
            supplier.get();
        }
        long endTime = System.currentTimeMillis();
        return (int)(endTime-startTime);
    }

    public static void main(String[] args) {
        LOGGER.info("ParallellStreamExample :: main started");
        int seqTime = timeTakenFoExecution(ParallellStreamExample::sum_sequential_stream, 20);
        System.out.println("Sequential Stream Result : " + seqTime + " ms");
        LOGGER.info("Sequential stream time: " + seqTime + " ms");

        int parTime = timeTakenFoExecution(ParallellStreamExample::sum_parallel_stream, 20);
        System.out.println("Parallel Stream Result : " + parTime + " ms");
        LOGGER.info("Parallel stream time: " + parTime + " ms");

        LOGGER.info("ParallellStreamExample :: main finished");
    }
}
