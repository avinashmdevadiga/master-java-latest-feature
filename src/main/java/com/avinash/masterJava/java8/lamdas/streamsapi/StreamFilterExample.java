package com.avinash.masterJava.java8.lamdas.streamsapi;

import com.avinash.masterJava.java8.lamdas.data.Student;
import com.avinash.masterJava.java8.lamdas.data.StudentDatabase;

import java.util.List;
import java.util.stream.Collectors;
import java.util.logging.Logger;

/**
 * Use Case: Demonstrates the Stream filter() intermediate operation.
 * filter() accepts a Predicate and passes only elements that satisfy it downstream.
 * Here it extracts only female students from the database, showing how to build
 * targeted sub-lists from a full collection without mutating the original data.
 */
public class StreamFilterExample {

    private static final Logger LOGGER = Logger.getLogger(StreamFilterExample.class.getName());

    private static List<Student> getFemaleStudentList(){
        LOGGER.info("StreamFilterExample :: getFemaleStudentList - filtering by gender == 'female'");
        return StudentDatabase.getStudents().stream()
                .filter(student -> student.getGender().equals("female"))
                .collect(Collectors.toList());
    }

    public static void main(String[] args) {
        LOGGER.info("StreamFilterExample :: main started");
        List<Student> result = getFemaleStudentList();
        System.out.println(result);
        LOGGER.info("StreamFilterExample :: main finished - " + result.size() + " female students found");
    }
}
