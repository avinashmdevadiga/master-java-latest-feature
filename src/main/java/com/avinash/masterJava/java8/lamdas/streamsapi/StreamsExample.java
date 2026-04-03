package com.avinash.masterJava.java8.lamdas.streamsapi;

import com.avinash.masterJava.java8.lamdas.data.Student;
import com.avinash.masterJava.java8.lamdas.data.StudentDatabase;

import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.logging.Logger;

/**
 * Use Case: Shows the Stream pipeline in action with multiple intermediate operations.
 * - filter()  — keeps elements matching a Predicate.
 * - peek()    — non-consuming debug inspection at each pipeline stage.
 * - collect() — terminal operation that materialises the stream into a Map.
 * Demonstrates how peek() makes the lazy evaluation order of a stream visible.
 */
public class StreamsExample {

    private static final Logger LOGGER = Logger.getLogger(StreamsExample.class.getName());

    public static void main(String[] args) {
        LOGGER.info("StreamsExample :: main started");

        Predicate<Student> predicate1 = student -> student.getGradeLevel() >= 2;
        Predicate<Student> predicate2 = student -> student.getGpa() >= 3.9;

        LOGGER.info("Building stream pipeline: peek → filter(gradeLevel>=2) → peek → filter(GPA>=3.9) → peek → collect");
        Map<String, List<String>> collect = StudentDatabase.getStudents()
                .stream()
                .peek(student -> System.out.println("Before filter started"+student))
                .filter(predicate1)
                .peek(student -> System.out.println("After 1St filter"+student))
                .filter(predicate2)
                .peek(student -> System.out.println("After 2nd filter"+student))
                .collect(Collectors.toMap(Student::getName, Student::getActivities));

        System.out.println(collect);
        LOGGER.info("StreamsExample :: main finished - collected " + collect.size() + " entries");
    }
}
