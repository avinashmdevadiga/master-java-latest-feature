package com.avinash.masterJava.java8.lamdas.streamsapi;

import com.avinash.masterJava.java8.lamdas.data.Student;
import com.avinash.masterJava.java8.lamdas.data.StudentDatabase;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.logging.Logger;

/**
 * Use Case: Demonstrates the Stream flatMap() intermediate operation.
 * flatMap() "flattens" a stream of collections into a single stream of elements.
 * Here each Student has a List<String> of activities; flatMap merges all those
 * lists into one stream so we can collect unique/sorted activities across all students.
 * Also shows mapToLong() + sum() to count total activity slots.
 */
public class StreamFlatMapExample {

    private static final Logger LOGGER = Logger.getLogger(StreamFlatMapExample.class.getName());

    private static Set<String> uniqueActivitiesSet(){
        LOGGER.info("StreamFlatMapExample :: uniqueActivitiesSet - flatMap all activities into a Set");
        return StudentDatabase.getStudents()
                .stream()
                .flatMap(student -> student.getActivities().stream())
                .collect(Collectors.toSet());
    }

    private static List<String> uniqueActivitiesList(){
        LOGGER.info("StreamFlatMapExample :: uniqueActivitiesList - flatMap → distinct → sorted → List");
        return StudentDatabase.getStudents()
                .stream()
                .flatMap(student -> student.getActivities().stream())
                .distinct()
                .sorted()
                .collect(Collectors.toList());
    }

    private static long numberOfActivities(){
        LOGGER.info("StreamFlatMapExample :: numberOfActivities - sum of distinct activity-list sizes via mapToLong");
        return StudentDatabase.getStudents()
                .stream()
                .mapToLong(student -> student.getActivities().size())
                .distinct()
                .sum();
    }

    public static void main(String[] args) {
        LOGGER.info("StreamFlatMapExample :: main started");
        // System.out.println(uniqueActivitiesSet());
        System.out.println(uniqueActivitiesList());
        System.out.println(numberOfActivities());
        LOGGER.info("StreamFlatMapExample :: main finished");
    }
}
