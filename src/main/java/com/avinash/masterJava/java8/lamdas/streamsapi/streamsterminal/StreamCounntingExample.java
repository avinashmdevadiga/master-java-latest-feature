package com.avinash.masterJava.java8.lamdas.streamsapi.streamsterminal;

import com.avinash.masterJava.java8.lamdas.data.StudentDatabase;

import java.util.stream.Collectors;
import java.util.logging.Logger;

/**
 * Use Case: Demonstrates Collectors.counting() to count stream elements after filtering.
 * counting() is equivalent to Stream.count() but works as a downstream collector,
 * making it composable inside groupingBy() to count members per group.
 * Here it counts students whose GPA is >= 3.9.
 */
public class StreamCounntingExample {

    private static final Logger LOGGER = Logger.getLogger(StreamCounntingExample.class.getName());

    private static long counting(){
        LOGGER.info("StreamCounntingExample :: counting - students with GPA >= 3.9");
        return StudentDatabase.getStudents().stream()
                .filter(student -> student.getGpa()>=3.9)
                .collect(Collectors.counting());
    }

    public static void main(String[] args) {
        LOGGER.info("StreamCounntingExample :: main started");
        long result = counting();
        System.out.println("counting :: "+result);
        LOGGER.info("StreamCounntingExample :: main finished - count=" + result);
    }
}
