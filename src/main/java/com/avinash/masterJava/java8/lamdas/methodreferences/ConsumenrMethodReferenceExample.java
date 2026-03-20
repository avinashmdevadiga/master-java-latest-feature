package com.avinash.masterJava.java8.lamdas.methodreferences;

import com.avinash.masterJava.java8.lamdas.data.Student;
import com.avinash.masterJava.java8.lamdas.data.StudentDatabase;

import java.util.function.Consumer;

public class ConsumenrMethodReferenceExample {

    private static Consumer<Student> consumer = student -> System.out.println(student);

    private static Consumer<Student> consumerMethodReferece = System.out::println;

    private static Consumer<Student> studentMethodReference = Student::printStudentActivities;

    public static void main(String[] args) {
        //StudentDatabase.getStudents().forEach(consumer);
        //StudentDatabase.getStudents().forEach(consumerMethodReferece);
        StudentDatabase.getStudents().forEach(studentMethodReference);
    }
}
