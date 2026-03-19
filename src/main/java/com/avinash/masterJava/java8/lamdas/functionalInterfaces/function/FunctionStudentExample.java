package com.avinash.masterJava.java8.lamdas.functionalInterfaces.function;

import com.avinash.masterJava.java8.lamdas.data.Student;
import com.avinash.masterJava.java8.lamdas.data.StudentDatabase;
import com.avinash.masterJava.java8.lamdas.functionalInterfaces.predicate.PredicateStudentExample;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class FunctionStudentExample {
    /*
    * usecase: want wo show the studentname allong with the gpa
    * */

    private static Function<List<Student>, Map<String,Double>>  studentFunction = (studentList)->{
        Map<String,Double> studentGpaMap = new HashMap<>();
        studentList.forEach(student -> {
            if(PredicateStudentExample.studentGpa.test(student)){
                studentGpaMap.put(student.getName(),student.getGpa());
            }
        });
        return studentGpaMap;
    };

    private void printFilteredStudentMap(){
        System.out.println(studentFunction.apply(StudentDatabase.getStudents()));
    }

    public static void main(String[] args) {
        new FunctionStudentExample().printFilteredStudentMap();

    }
}
