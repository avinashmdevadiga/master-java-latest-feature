package com.avinash.masterJava.java8.lamdas.streamsapi.streamsterminal;

import com.avinash.masterJava.java8.lamdas.data.Student;
import com.avinash.masterJava.java8.lamdas.data.StudentDatabase;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class StreamMappingExample {
    private static List<String> studentNameList(){
        return StudentDatabase.getStudents().stream()
                .collect(Collectors.mapping(Student::getName,Collectors.toList()));
    }

    private static Set<String> studentNameSet(){
        return StudentDatabase.getStudents().stream()
                .collect(Collectors.mapping(Student::getName,Collectors.toSet()));
    }

    public static void main(String[] args) {

        System.out.println("StudentNameList : "+studentNameList());
        System.out.println("StudentNameSet : "+studentNameSet());
    }
}
