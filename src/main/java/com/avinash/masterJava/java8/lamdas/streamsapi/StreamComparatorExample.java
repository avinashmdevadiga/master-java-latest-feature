package com.avinash.masterJava.java8.lamdas.streamsapi;

import com.avinash.masterJava.java8.lamdas.data.Student;
import com.avinash.masterJava.java8.lamdas.data.StudentDatabase;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.logging.Logger;

/**
 * Use Case: Demonstrates the Stream sorted() intermediate operation with Comparator.
 * - sorted(Comparator.comparing())          — ascending order by a field.
 * - sorted(Comparator.comparing().reversed()) — descending order by a field.
 * Unlike Collections.sort() which mutates the list, stream sorting is non-destructive
 * and returns a new ordered sequence without changing the source data.
 */
public class StreamComparatorExample {

    private static final Logger LOGGER = Logger.getLogger(StreamComparatorExample.class.getName());

    private static List<Student> getListByName(){
        LOGGER.info("StreamComparatorExample :: getListByName - sorted ascending by name");
        return StudentDatabase.getStudents().stream()
                .sorted(Comparator.comparing(Student::getName))
                .collect(Collectors.toList());
    }

    private static List<Student> getListByGpaAcs(){
        LOGGER.info("StreamComparatorExample :: getListByGpaAcs - sorted ascending by GPA");
        return StudentDatabase.getStudents().stream()
                .sorted(Comparator.comparing(Student::getGpa))
                .collect(Collectors.toList());
    }

    private static List<Student> getListByGpaDesc(){
        LOGGER.info("StreamComparatorExample :: getListByGpaDesc - sorted descending by GPA");
        return StudentDatabase.getStudents().stream()
                .sorted(Comparator.comparing(Student::getGpa).reversed())
                .collect(Collectors.toList());
    }

    public static void main(String[] args) {
        LOGGER.info("StreamComparatorExample :: main started");
        System.out.println("sort by name");
        System.out.println(getListByName());
        System.out.println("sort by gpa ascending order");
        System.out.println(getListByGpaAcs());
        System.out.println("sort by gpa desc order");
        System.out.println(getListByGpaDesc());
        LOGGER.info("StreamComparatorExample :: main finished");
    }
}
