package com.avinash.masterJava.java8.lamdas.streamsapi.streamsterminal;

import com.avinash.masterJava.java8.lamdas.data.Student;
import com.avinash.masterJava.java8.lamdas.data.StudentDatabase;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class StreamPartitioningByExample {
    public static void main(String[] args) {
        Map<Boolean, List<Student>> partionedMap = StudentDatabase.getStudents()
                .stream()
                .collect(Collectors.partitioningBy(student -> student.getGpa() >= 3.8));
        System.out.println(partionedMap);

        /*
        * partitioningBy two org example
        * */

        Map<Boolean, Set<Student>> partitioningByExampleMap = StudentDatabase.getStudents()
                .stream()
                .collect(Collectors.partitioningBy(student -> student.getGpa() >= 3.8,Collectors.toSet()));
        System.out.println(partitioningByExampleMap);
    }
}
