package com.avinash.masterJava.java8.lamdas.streamsapi;

import com.avinash.masterJava.java8.lamdas.data.Student;
import com.avinash.masterJava.java8.lamdas.data.StudentDatabase;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class StreamComparatorExample {

    private static List<Student> getListByName(){
        return StudentDatabase.getStudents().stream()
                .sorted(Comparator.comparing(Student::getName))
                .collect(Collectors.toList());
    }

    private static List<Student> getListByGpaAcs(){
        return StudentDatabase.getStudents().stream()
                .sorted(Comparator.comparing(Student::getGpa))
                .collect(Collectors.toList());
    }

    private static List<Student> getListByGpaDesc(){
        return  StudentDatabase.getStudents().stream()
                .sorted(Comparator.comparing(Student::getGpa).reversed())
                .collect(Collectors.toList());
    }

    public static void main(String[] args) {
        System.out.println("sort by name");
        System.out.println(getListByName());
        System.out.println("sort by gpa ascending order");
        System.out.println(getListByGpaAcs());
        System.out.println("sort by gpa desc order");
        System.out.println(getListByGpaDesc());
    }
}
