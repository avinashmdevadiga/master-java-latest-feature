package com.avinash.masterJava.java8.lamdas.terminaloperation;

import com.avinash.masterJava.java8.lamdas.data.Student;
import com.avinash.masterJava.java8.lamdas.data.StudentDatabase;

import java.util.stream.Collectors;
import java.util.logging.Logger;

/**
 * Use Case: Demonstrates Collectors.joining() as a terminal operation in the terminaloperation package.
 * joining() concatenates all mapped String elements into a single String without any delimiter.
 * This is the simplest form of joining — useful when you need a plain concatenated result.
 */
public class JoiningExample {

    private static final Logger LOGGER = Logger.getLogger(JoiningExample.class.getName());

    public static void main(String[] args) {
        LOGGER.info("JoiningExample :: main started");
        LOGGER.info("Collecting all student names into a single concatenated String");
        String result = StudentDatabase.getStudents().stream()
                .map(Student::getName)
                .collect(Collectors.joining());
        System.out.println(result);
        LOGGER.info("JoiningExample :: main finished - result: " + result);
    }
}
