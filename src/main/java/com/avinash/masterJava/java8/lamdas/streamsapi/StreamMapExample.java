package com.avinash.masterJava.java8.lamdas.streamsapi;

import com.avinash.masterJava.java8.lamdas.data.Student;
import com.avinash.masterJava.java8.lamdas.data.StudentDatabase;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.logging.Logger;

/**
 * Use Case: Demonstrates the Stream map() intermediate operation.
 * map() transforms each element from one type to another using a Function.
 * Here it extracts only the student names from Student objects,
 * collecting into a List (preserving order) or Set (removing duplicates).
 */
public class StreamMapExample {

    private static final Logger LOGGER = Logger.getLogger(StreamMapExample.class.getName());

    private static List<String> getNames(){
        LOGGER.info("StreamMapExample :: getNames - mapping Student → name into a List");
        return StudentDatabase.getStudents()
                .stream()
                .map(Student::getName)
                .collect(Collectors.toList());
    }

    private static Set<String> getNamesSet(){
        LOGGER.info("StreamMapExample :: getNamesSet - mapping Student → name into a Set (dedup)");
        return StudentDatabase.getStudents()
                .stream()
                .map(Student::getName)
                .collect(Collectors.toSet());
    }

    public static void main(String[] args) {
        LOGGER.info("StreamMapExample :: main started");
        System.out.println(getNames());
        System.out.println(getNamesSet());
        LOGGER.info("StreamMapExample :: main finished");
    }
}
