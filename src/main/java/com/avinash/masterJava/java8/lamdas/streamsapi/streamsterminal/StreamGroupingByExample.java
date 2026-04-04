package com.avinash.masterJava.java8.lamdas.streamsapi.streamsterminal;

import com.avinash.masterJava.java8.lamdas.data.Student;
import com.avinash.masterJava.java8.lamdas.data.StudentDatabase;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.logging.Logger;

/**
 * Use Case: Demonstrates the Collectors.groupingBy() collector for data aggregation.
 * - groupingBy_1: simple grouping by a single field (gender).
 * - groupingBy_2: custom classifier lambda (GPA threshold → OUTSTANDING/AVERAGE).
 * - groupByMultiple_1: nested groupingBy — first by gradeLevel, then by GPA band.
 * - groupByMultiple_2: groupingBy + downstream summingInt — totals notebooks per student name.
 * groupingBy is the stream equivalent of SQL GROUP BY.
 */
public class StreamGroupingByExample {

    private static final Logger LOGGER = Logger.getLogger(StreamGroupingByExample.class.getName());

    private static Map<String, List<Student>> groupingBy_1(){
        LOGGER.info("StreamGroupingByExample :: groupingBy_1 - grouping by gender");
        return StudentDatabase.getStudents().stream()
                .collect(Collectors.groupingBy(Student::getGender));
    }

    //customize logic
    private static Map<String, List<Student>> groupingBy_2(){
        LOGGER.info("StreamGroupingByExample :: groupingBy_2 - custom classifier: GPA >= 3.8 → OUTSTANDING/AVERAGE");
        return StudentDatabase.getStudents().stream()
                .collect(Collectors.groupingBy(student ->
                        student.getGpa() >= 3.8 ? "OUTSTANDING" : "AVERAGE"));
    }


    private static Map<Integer, Map<String, List<Student>>> groupByMultiple_1(){
        LOGGER.info("StreamGroupingByExample :: groupByMultiple_1 - nested: gradeLevel then GPA band");
        return StudentDatabase.getStudents().stream()
                .collect(Collectors.groupingBy(Student::getGradeLevel,
                        Collectors.groupingBy(student ->
                                student.getGpa() >= 3.8 ? "OUTSTANDING" : "AVERAGE")));
    }

    private static Map<String, Integer> groupByMultiple_2(){
        LOGGER.info("StreamGroupingByExample :: groupByMultiple_2 - groupBy name + summingInt notebooks");
        return StudentDatabase.getStudents().stream()
                .collect(Collectors.groupingBy(Student::getName,
                        Collectors.summingInt(Student::getNoteBooks)));
    }

    public static void main(String[] args) {
        LOGGER.info("StreamGroupingByExample :: main started");
        System.out.println(groupingBy_1());
        System.out.println("------");
        System.out.println(groupingBy_2());
        System.out.println("------");
        System.out.println(groupByMultiple_1());
        System.out.println("------");
        System.out.println(groupByMultiple_2());
        LOGGER.info("StreamGroupingByExample :: main finished");
    }
}
