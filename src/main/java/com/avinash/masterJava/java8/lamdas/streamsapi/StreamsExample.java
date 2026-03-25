package com.avinash.masterJava.java8.lamdas.streamsapi;

import com.avinash.masterJava.java8.lamdas.data.Student;
import com.avinash.masterJava.java8.lamdas.data.StudentDatabase;

import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class StreamsExample {

    public static void main(String[] args) {
        /*
        * show studentyName and along with their activities
        * */
        Predicate<Student> predicate1 = student -> student.getGradeLevel() >= 2;
        Predicate<Student> predicate2 =  student -> student.getGpa() >= 3.9;


        Map<String, List<String>> collect = StudentDatabase.getStudents()
                .stream()
                .peek(student -> System.out.println("Before filter started"+student))
                .filter(predicate1)
                .peek(student -> System.out.println("After 1St filter"+student))
                .filter(predicate2)
                .peek(student -> System.out.println("After 2nd filter"+student))
                .collect(Collectors.toMap(Student::getName, Student::getActivities));

        System.out.println(collect);

    }
}
