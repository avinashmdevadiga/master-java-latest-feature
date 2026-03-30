package com.avinash.masterJava.java8.lamdas.streamsapi;

import com.avinash.masterJava.java8.lamdas.data.Student;
import com.avinash.masterJava.java8.lamdas.data.StudentDatabase;

import java.util.Optional;

public class StreamFindAnyFirstExample {

    private static Optional<Student> findAny(){
        return StudentDatabase.getStudents().stream()
                .filter(student -> student.getGpa()>=3.9)
                .findAny();
    }

    private static Optional<Student> findFirst(){
        return StudentDatabase.getStudents().stream()
                .filter(student -> student.getGpa()>=3.9)
                .findFirst();
    }

    public static void main(String[] args) {
        findAny().ifPresent(System.out::println);
        findFirst().ifPresent(System.out::println);
    }
}
