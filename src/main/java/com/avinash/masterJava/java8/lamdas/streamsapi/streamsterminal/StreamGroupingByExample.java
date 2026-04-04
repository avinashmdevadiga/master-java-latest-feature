package com.avinash.masterJava.java8.lamdas.streamsapi.streamsterminal;

import com.avinash.masterJava.java8.lamdas.data.Student;
import com.avinash.masterJava.java8.lamdas.data.StudentDatabase;

import java.util.*;
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

    private static Map<Integer, Set<Student>> threeOrgGroupingBy(){
        return StudentDatabase.getStudents()
                .stream()
                .collect(Collectors.groupingBy(Student::getGradeLevel, LinkedHashMap::new,Collectors.toSet()));
    }

    /*
    * example to groupingBy, maxBy, minBy, collectingAndThen
    * */

    private static void groupingByExample(){
        Map<Integer,Optional<Student>> studentGpaOptional = StudentDatabase.getStudents()
                .stream()
                .collect(Collectors.groupingBy(Student::getGradeLevel,
                        Collectors.maxBy(Comparator.comparing(Student::getGpa))));
        System.out.println(studentGpaOptional);

        Map<Integer,Student> studentGpaMap = StudentDatabase.getStudents()
                .stream()
                .collect(Collectors.groupingBy(Student::getGradeLevel,
                        Collectors.collectingAndThen(Collectors.minBy(Comparator.comparing(Student::getGpa)), Optional::get)
                ));
        System.out.println(studentGpaMap);
    }

    public static void main(String[] args) {
//        System.out.println(groupingBy_1());
//        System.out.println("------");
//        System.out.println(groupingBy_2());
//        System.out.println("------");
//        System.out.println(groupByMultiple_1());
//        System.out.println("------");
//        System.out.println(groupByMultiple_2());
//
//        System.out.println("------");
//        System.out.println(threeOrgGroupingBy());

        groupingByExample();
    }
}
