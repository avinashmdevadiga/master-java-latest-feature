package com.avinash.masterJava.java8.lamdas.streamsapi.streamsterminal;

import ch.qos.logback.core.status.StatusUtil;
import com.avinash.masterJava.java8.lamdas.data.Student;
import com.avinash.masterJava.java8.lamdas.data.StudentDatabase;

import java.util.stream.Collectors;

public class StreamSumAvgExample {

    private static int sum(){
        return StudentDatabase.getStudents().stream().collect(Collectors.summingInt(Student::getNoteBooks));
    }

    private static double average(){
        return StudentDatabase.getStudents().stream().collect(Collectors.averagingInt(Student::getNoteBooks));

    }
    public static void main(String[] args) {
        System.out.println("total notebooks : "+sum());
        System.out.println("Avg notebooks : "+average());
    }
}
