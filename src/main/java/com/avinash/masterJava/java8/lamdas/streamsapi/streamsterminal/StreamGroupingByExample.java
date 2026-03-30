package com.avinash.masterJava.java8.lamdas.streamsapi.streamsterminal;

import com.avinash.masterJava.java8.lamdas.data.Student;
import com.avinash.masterJava.java8.lamdas.data.StudentDatabase;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class StreamGroupingByExample {

    private static Map<String, List<Student>> groupingBy_1(){
        return StudentDatabase.getStudents().stream()
                .collect(Collectors.groupingBy(Student::getGender));
    }

    //customize logic
    private static Map<String, List<Student>> groupingBy_2(){
        return StudentDatabase.getStudents().stream()
                .collect(Collectors.groupingBy(student ->student.getGpa() >=3.8 ? "OUTSTANDING":"AVERAGE"));
    }


    private static Map<Integer,Map<String,List<Student>>> groupByMultiple_1(){
        return StudentDatabase.getStudents().stream()
                .collect(Collectors.groupingBy(Student::getGradeLevel,Collectors.groupingBy(student ->student.getGpa() >=3.8 ? "OUTSTANDING":"AVERAGE")));
    }

    private static Map<String,Integer> groupByMultiple_2() {
        return StudentDatabase.getStudents().stream()
                .collect(Collectors.groupingBy(Student::getName, Collectors.summingInt(Student::getNoteBooks)));
    }

    public static void main(String[] args) {
        System.out.println(groupingBy_1());
        System.out.println("------");
        System.out.println(groupingBy_2());
        System.out.println("------");
        System.out.println(groupByMultiple_1());
        System.out.println("------");
        System.out.println(groupByMultiple_2());
    }
}
