package com.avinash.masterJava.java8.lamdas.streamsapi.streamsterminal;

import com.avinash.masterJava.java8.lamdas.data.StudentDatabase;

import java.util.stream.Collectors;

public class StreamCounntingExample {

    private static long counting(){
        return StudentDatabase.getStudents().stream()
                .filter(student -> student.getGpa()>=3.9)
                .collect(Collectors.counting());
    }

    public static void main(String[] args) {
        System.out.println("counting :: "+counting());
    }
}
