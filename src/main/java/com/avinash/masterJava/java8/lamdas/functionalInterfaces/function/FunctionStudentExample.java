package com.avinash.masterJava.java8.lamdas.functionalInterfaces.function;

import com.avinash.masterJava.java8.lamdas.data.Student;
import com.avinash.masterJava.java8.lamdas.data.StudentDatabase;
import com.avinash.masterJava.java8.lamdas.functionalInterfaces.predicate.PredicateStudentExample;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.logging.Logger;

/**
 * Use Case: Demonstrates Function<T,R> applied to a domain model.
 * Transforms a List<Student> into a Map<String,Double> (name → GPA)
 * filtered by a reusable Predicate (GPA >= 3.9) from PredicateStudentExample.
 * Illustrates how Function and Predicate compose cleanly for data-extraction pipelines.
 */
public class FunctionStudentExample {

    private static final Logger LOGGER = Logger.getLogger(FunctionStudentExample.class.getName());

    /*
    * usecase: want wo show the studentname allong with the gpa
    * */

    private static Function<List<Student>, Map<String,Double>> studentFunction = (studentList)->{
        LOGGER.info("FunctionStudentExample :: studentFunction - filtering students with GPA >= 3.9");
        Map<String,Double> studentGpaMap = new HashMap<>();
        studentList.forEach(student -> {
            if(PredicateStudentExample.studentGpa.test(student)){
                studentGpaMap.put(student.getName(),student.getGpa());
            }
        });
        return studentGpaMap;
    };

    private void printFilteredStudentMap(){
        LOGGER.info("FunctionStudentExample :: printFilteredStudentMap invoked");
        System.out.println(studentFunction.apply(StudentDatabase.getStudents()));
    }

    public static void main(String[] args) {
        LOGGER.info("FunctionStudentExample :: main started");
        new FunctionStudentExample().printFilteredStudentMap();
        LOGGER.info("FunctionStudentExample :: main finished");
    }
}
