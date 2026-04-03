package com.avinash.masterJava.java8.lamdas.streamsapi.streamsterminal;

import com.avinash.masterJava.java8.lamdas.data.Student;
import com.avinash.masterJava.java8.lamdas.data.StudentDatabase;

import java.util.Comparator;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.logging.Logger;

/**
 * Use Case: Demonstrates Collectors.minBy() and maxBy() as terminal collectors.
 * Both accept a Comparator and return Optional<T> because the stream may be empty.
 * They are collector equivalents of Stream.min() / Stream.max(), useful as downstream
 * collectors inside groupingBy() (e.g., find the top student per grade group).
 */
public class StreamMinByMaxByExample {

    private static final Logger LOGGER = Logger.getLogger(StreamMinByMaxByExample.class.getName());

    private static Optional<Student> minBy(){
        LOGGER.info("StreamMinByMaxByExample :: minBy - student with lowest GPA");
        return StudentDatabase.getStudents().stream()
                .collect(Collectors.minBy(Comparator.comparing(Student::getGpa)));
    }

    private static Optional<Student> maxBy(){
        LOGGER.info("StreamMinByMaxByExample :: maxBy - student with highest GPA");
        return StudentDatabase.getStudents().stream()
                .collect(Collectors.maxBy(Comparator.comparing(Student::getGpa)));
    }

    public static void main(String[] args) {
        LOGGER.info("StreamMinByMaxByExample :: main started");
        minBy().ifPresent(s -> { LOGGER.info("minBy result: " + s); System.out.println(s); });
        maxBy().ifPresent(s -> { LOGGER.info("maxBy result: " + s); System.out.println(s); });
        LOGGER.info("StreamMinByMaxByExample :: main finished");
    }
}
