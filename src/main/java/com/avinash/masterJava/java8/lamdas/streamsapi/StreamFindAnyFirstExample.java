package com.avinash.masterJava.java8.lamdas.streamsapi;

import com.avinash.masterJava.java8.lamdas.data.Student;
import com.avinash.masterJava.java8.lamdas.data.StudentDatabase;

import java.util.Optional;
import java.util.logging.Logger;

/**
 * Use Case: Demonstrates findAny() and findFirst() short-circuit terminal operations.
 * - findFirst() — returns the first element in encounter order that matches the filter.
 * - findAny()   — returns any matching element (may differ in parallel streams for performance).
 * Both return Optional<T> since the stream may produce no match.
 */
public class StreamFindAnyFirstExample {

    private static final Logger LOGGER = Logger.getLogger(StreamFindAnyFirstExample.class.getName());

    private static Optional<Student> findAny(){
        LOGGER.info("StreamFindAnyFirstExample :: findAny - any student with GPA >= 3.9");
        return StudentDatabase.getStudents().stream()
                .filter(student -> student.getGpa()>=3.9)
                .findAny();
    }

    private static Optional<Student> findFirst(){
        LOGGER.info("StreamFindAnyFirstExample :: findFirst - first student with GPA >= 3.9 in encounter order");
        return StudentDatabase.getStudents().stream()
                .filter(student -> student.getGpa()>=3.9)
                .findFirst();
    }

    public static void main(String[] args) {
        LOGGER.info("StreamFindAnyFirstExample :: main started");
        findAny().ifPresent(s -> { LOGGER.info("findAny result: " + s); System.out.println(s); });
        findFirst().ifPresent(s -> { LOGGER.info("findFirst result: " + s); System.out.println(s); });
        LOGGER.info("StreamFindAnyFirstExample :: main finished");
    }
}
