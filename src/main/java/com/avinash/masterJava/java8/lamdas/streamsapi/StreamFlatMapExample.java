package com.avinash.masterJava.java8.lamdas.streamsapi;

import com.avinash.masterJava.java8.lamdas.data.Student;
import com.avinash.masterJava.java8.lamdas.data.StudentDatabase;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class StreamFlatMapExample {

    private static Set<String> uniqueActivitiesSet(){
      return StudentDatabase.getStudents()
              .stream()
              .flatMap(student->student.getActivities().stream())
              .collect(Collectors.toSet());
    }

    private static List<String> uniqueActivitiesList(){
        return StudentDatabase.getStudents()
                .stream()
                .flatMap(student->student.getActivities().stream())
                .distinct()
                .sorted()
                .collect(Collectors.toList());
    }
    private static long numberOfActivities(){
        return StudentDatabase.getStudents()
                .stream()
                .mapToLong(student->student.getActivities().size())
                .distinct()
                .sum();

    }
    public static void main(String[] args) {
        //System.out.println(uniqueActivitiesSet());
        System.out.println(uniqueActivitiesList());
        System.out.println(numberOfActivities());
    }
}
