package com.avinash.masterJava.java8.lamdas.streamsapi.streamsterminal;

import com.avinash.masterJava.java8.lamdas.data.Student;
import com.avinash.masterJava.java8.lamdas.data.StudentDatabase;

import java.util.stream.Collectors;
import java.util.logging.Logger;

/**
 * Use Case: Demonstrates Collectors.summingInt() and averagingInt() for numeric aggregation.
 * - summingInt() — totals an integer field across all stream elements.
 * - averagingInt() — computes the average of an integer field, returning a Double.
 * Both are concise collector alternatives to mapToInt().sum() / mapToInt().average().
 */
public class StreamSumAvgExample {

    private static final Logger LOGGER = Logger.getLogger(StreamSumAvgExample.class.getName());

    private static int sum(){
        LOGGER.info("StreamSumAvgExample :: sum - summingInt(getNoteBooks) across all students");
        return StudentDatabase.getStudents().stream()
                .collect(Collectors.summingInt(Student::getNoteBooks));
    }

    private static double average(){
        LOGGER.info("StreamSumAvgExample :: average - averagingInt(getNoteBooks) across all students");
        return StudentDatabase.getStudents().stream()
                .collect(Collectors.averagingInt(Student::getNoteBooks));
    }

    public static void main(String[] args) {
        LOGGER.info("StreamSumAvgExample :: main started");
        System.out.println("total notebooks : "+sum());
        System.out.println("Avg notebooks : "+average());
        LOGGER.info("StreamSumAvgExample :: main finished");
    }
}
