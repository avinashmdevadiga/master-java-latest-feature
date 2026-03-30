package com.avinash.masterJava.java8.lamdas.streamsapi.streamsterminal;

import com.avinash.masterJava.java8.lamdas.data.Student;
import com.avinash.masterJava.java8.lamdas.data.StudentDatabase;

import java.util.stream.Collectors;

public class StreamJoiningExample {
    private static String joining_1(){
        return StudentDatabase.getStudents().stream()
                .map(Student::getName)
                .collect(Collectors.joining());
    }

    private static String Joining_2(){
        return StudentDatabase.getStudents().stream()
                .map(Student::getName)
                .collect(Collectors.joining(", "));
    }
    private static String Joining_3(){
        return StudentDatabase.getStudents().stream()
                .map(Student::getName)
                .collect(Collectors.joining(", ","(",")"));
    }

    public static void main(String[] args) {
        System.out.println("joining_1 :: "+joining_1());
        System.out.println("joining_2 :: "+Joining_2());
        System.out.println("Joining_3 :: "+Joining_3());
    }
}
