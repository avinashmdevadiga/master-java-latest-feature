package com.avinash.masterJava.java8.lamdas.streamsapi;

import com.avinash.masterJava.java8.lamdas.data.StudentDatabase;

public class StreamMatchExample {
    private static boolean allMatch(){
        return StudentDatabase.getStudents().stream()
                .allMatch(student -> student.getGpa()>=3);
    }

    private static boolean anyMatch(){
        return StudentDatabase.getStudents().stream()
                .anyMatch(student -> student.getGpa()>=4);
    }

    private static boolean noneMatch(){
        return StudentDatabase.getStudents().stream()
                .noneMatch(student -> student.getGpa() > 4.5);
    }

    public static void main(String[] args) {
        System.out.println("allMatch : "+allMatch());
        System.out.println("anyMatch : "+anyMatch());
        System.out.println("noneMatch : "+noneMatch());
    }
}
