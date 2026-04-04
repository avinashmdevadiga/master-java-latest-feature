package com.avinash.masterJava.java8.lamdas.streamsapi.streamsterminal;

import com.avinash.masterJava.java8.lamdas.data.Student;
import com.avinash.masterJava.java8.lamdas.data.StudentDatabase;

import java.util.stream.Collectors;
import java.util.logging.Logger;

/**
 * Use Case: Demonstrates Collectors.joining() for concatenating stream elements into a String.
 * - joining()              — simple concatenation with no delimiter.
 * - joining(delimiter)     — elements separated by a delimiter string.
 * - joining(delimiter, prefix, suffix) — wrapped in a prefix/suffix (e.g. parentheses).
 * Useful for producing CSV, display strings, or SQL IN-clause values from a stream.
 */
public class StreamJoiningExample {

    private static final Logger LOGGER = Logger.getLogger(StreamJoiningExample.class.getName());

    private static String joining_1(){
        LOGGER.info("StreamJoiningExample :: joining_1 - plain concatenation of all names");
        return StudentDatabase.getStudents().stream()
                .map(Student::getName)
                .collect(Collectors.joining());
    }

    private static String Joining_2(){
        LOGGER.info("StreamJoiningExample :: Joining_2 - names separated by ', '");
        return StudentDatabase.getStudents().stream()
                .map(Student::getName)
                .collect(Collectors.joining(", "));
    }

    private static String Joining_3(){
        LOGGER.info("StreamJoiningExample :: Joining_3 - names with prefix '(' and suffix ')'");
        return StudentDatabase.getStudents().stream()
                .map(Student::getName)
                .collect(Collectors.joining(", ","(",")"));
    }

    public static void main(String[] args) {
        LOGGER.info("StreamJoiningExample :: main started");
        System.out.println("joining_1 :: "+joining_1());
        System.out.println("joining_2 :: "+Joining_2());
        System.out.println("Joining_3 :: "+Joining_3());
        LOGGER.info("StreamJoiningExample :: main finished");
    }
}
