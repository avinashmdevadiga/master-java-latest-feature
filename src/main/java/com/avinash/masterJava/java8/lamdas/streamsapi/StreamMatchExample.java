package com.avinash.masterJava.java8.lamdas.streamsapi;

import com.avinash.masterJava.java8.lamdas.data.StudentDatabase;
import java.util.logging.Logger;

/**
 * Use Case: Demonstrates the Stream short-circuit matching terminal operations.
 * - allMatch()  — returns true only if ALL elements satisfy the predicate.
 * - anyMatch()  — returns true if AT LEAST ONE element satisfies the predicate.
 * - noneMatch() — returns true if NO element satisfies the predicate.
 * All three are short-circuit: they stop processing as soon as the outcome is determined.
 */
public class StreamMatchExample {

    private static final Logger LOGGER = Logger.getLogger(StreamMatchExample.class.getName());

    private static boolean allMatch(){
        LOGGER.info("StreamMatchExample :: allMatch - all students GPA >= 3");
        return StudentDatabase.getStudents().stream()
                .allMatch(student -> student.getGpa()>=3);
    }

    private static boolean anyMatch(){
        LOGGER.info("StreamMatchExample :: anyMatch - any student GPA >= 4");
        return StudentDatabase.getStudents().stream()
                .anyMatch(student -> student.getGpa()>=4);
    }

    private static boolean noneMatch(){
        LOGGER.info("StreamMatchExample :: noneMatch - no student GPA > 4.5");
        return StudentDatabase.getStudents().stream()
                .noneMatch(student -> student.getGpa() > 4.5);
    }

    public static void main(String[] args) {
        LOGGER.info("StreamMatchExample :: main started");
        System.out.println("allMatch : "+allMatch());
        System.out.println("anyMatch : "+anyMatch());
        System.out.println("noneMatch : "+noneMatch());
        LOGGER.info("StreamMatchExample :: main finished");
    }
}
