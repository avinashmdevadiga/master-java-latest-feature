package com.avinash.masterJava.java8.lamdas.terminaloperation;

import com.avinash.masterJava.java8.lamdas.data.Student;
import com.avinash.masterJava.java8.lamdas.data.StudentDatabase;

import java.util.stream.Collectors;

public class JoiningExample {
    public static void main(String[] args) {
        System.out.println(StudentDatabase.getStudents().stream()
                .map(Student::getName)
                .collect(Collectors.joining()));
    }
}
