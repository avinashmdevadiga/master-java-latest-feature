package com.avinash.masterJava.java8.lamdas.defaultinterface;

import com.avinash.masterJava.java8.lamdas.data.Student;
import com.avinash.masterJava.java8.lamdas.data.StudentDatabase;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.logging.Logger;

/**
 * Use Case: Demonstrates Java 8 Comparator API features with the List.sort() default method.
 * - sortByName: sorts using Comparator.comparing() with a method reference.
 * - sortByGPA: sorts numeric fields with Comparator.comparingDouble().
 * - comparatorChaining: chains comparators with thenComparing() for multi-field sorting.
 * - SortWithNullValues / SortWithNullValues2: handle null entries safely using
 *   Comparator.nullsFirst() and Comparator.nullsLast().
 */
public class SortExample {

    private static final Logger LOGGER = Logger.getLogger(SortExample.class.getName());

    private static void sortByName(List<Student> studentList){
        LOGGER.info("SortExample :: sortByName invoked");
        studentList.sort(Comparator.comparing(Student::getName));
        studentList.forEach(System.out::println);
    }

    private static void sortByGPA(List<Student> studentList){
        LOGGER.info("SortExample :: sortByGPA invoked");
        studentList.sort(Comparator.comparingDouble(Student::getGpa));
        studentList.forEach(System.out::println);
    }

    private static void comparatorChaining(List<Student> studentList){
        LOGGER.info("SortExample :: comparatorChaining invoked - gradeLevel then name");
        studentList.sort(Comparator.comparingInt(Student::getGradeLevel).thenComparing(Student::getName));
        studentList.forEach(System.out::println);
    }

    private static void SortWithNullValues(List<Student> studentList){
        LOGGER.info("SortExample :: SortWithNullValues invoked - nulls placed first");
        studentList.sort(Comparator.nullsFirst(Comparator.comparing(Student::getName)));
        studentList.forEach(System.out::println);
    }

    private static void SortWithNullValues2(List<Student> studentList){
        LOGGER.info("SortExample :: SortWithNullValues2 invoked - nulls placed last");
        studentList.sort(Comparator.nullsLast(Comparator.comparing(Student::getName)));
        studentList.forEach(System.out::println);
    }

    public static void main(String[] args) {
        LOGGER.info("SortExample :: main started");
        List<Student> studentList = new ArrayList<>(StudentDatabase.getStudents());
        List<Student> studentListwithNull = new ArrayList<>(StudentDatabase.getStudentsWithNull());
//        sortByName(studentList);
//        System.out.printf(" comparingby double :: \n");
//        sortByGPA(studentList);
//        System.out.printf("comparator chaining :: \n");
//        comparatorChaining(studentList);
        System.out.printf("sort with null values :: \n");
        SortWithNullValues(studentListwithNull);
        System.out.printf("sort with null values 2 :: \n");
        SortWithNullValues2(studentListwithNull);
        LOGGER.info("SortExample :: main finished");
    }
}
