package com.avinash.masterJava.java8.lamdas.streamsapi.parallelstream;

import com.avinash.masterJava.java8.lamdas.data.Student;
import com.avinash.masterJava.java8.lamdas.data.StudentDatabase;

import java.util.List;

public class ParallelStreamExample1 {

    private static List<String> sequentialProcess(){
        long startTime = System.currentTimeMillis();
        List<String> activities = StudentDatabase.getStudents()
                .stream()
                .map(Student::getActivities)
                .flatMap(List::stream)
                .distinct()
                .sorted()
                .toList();
        long endTime = System.currentTimeMillis();
        System.out.println("duration taken for execution sequence process "+(endTime-startTime));
        return activities;
    }

    private static List<String> parallelProcess(){
        long startTime = System.currentTimeMillis();
        List<String> activities = StudentDatabase.getStudents()
                .stream()
                .parallel()
                .map(Student::getActivities)
                .flatMap(List::stream)
                .distinct()
                .sorted()
                .toList();
        long endTime = System.currentTimeMillis();
        System.out.println("duration taken for execution parallel process "+(endTime-startTime));
        return activities;
    }

    public static void main(String[] args) {

        sequentialProcess();
        parallelProcess();
    }
}
