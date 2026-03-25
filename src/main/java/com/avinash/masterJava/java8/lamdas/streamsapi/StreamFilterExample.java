package com.avinash.masterJava.java8.lamdas.streamsapi;

import com.avinash.masterJava.java8.lamdas.data.Student;
import com.avinash.masterJava.java8.lamdas.data.StudentDatabase;

import java.util.List;
import java.util.stream.Collectors;

public class StreamFilterExample {

    private static List<Student> getFemaleStudentList(){
        return StudentDatabase.getStudents().stream()
                .filter(student -> student.getGender().equals("female"))
//                .filter(student -> student.getGpa() >= 3.9)
                .collect(Collectors.toList());
    }
    public static void main(String[] args) {
        System.out.println(getFemaleStudentList());
    }
}
