package com.avinash.masterJava.java8.lamdas.streamsapi;

import com.avinash.masterJava.java8.lamdas.data.Student;
import com.avinash.masterJava.java8.lamdas.data.StudentDatabase;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class StreamMapExample {

    private static List<String> getNames(){
        return StudentDatabase.getStudents()
                .stream()
                .map(Student::getName)
                .collect(Collectors.toList());
    }

    private static Set<String> getNamesSet(){
        return  StudentDatabase.getStudents()
                .stream()
                .map(Student::getName)
                .collect(Collectors.toSet());
    }

    public static void main(String[] args) {
        System.out.println(getNames());
        System.out.println(getNamesSet());

    }
}
