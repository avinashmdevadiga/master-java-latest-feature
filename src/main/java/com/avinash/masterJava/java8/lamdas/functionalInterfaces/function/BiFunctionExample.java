package com.avinash.masterJava.java8.lamdas.functionalInterfaces.function;

import com.avinash.masterJava.java8.lamdas.data.Student;
import com.avinash.masterJava.java8.lamdas.data.StudentDatabase;
import com.avinash.masterJava.java8.lamdas.functionalInterfaces.predicate.PredicateStudentExample;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Predicate;
import java.util.logging.Logger;

/**
 * Use Case: Demonstrates BiFunction<T,U,R> — a function that accepts two inputs and produces one output.
 * Here it takes a List<Student> and a Predicate<Student> and returns a Map<String,Double> (name → GPA).
 * This is more flexible than FunctionStudentExample because the filtering predicate is injected at
 * call-time rather than hard-coded, making the function reusable across different filter criteria.
 */
public class BiFunctionExample {

    private static final Logger LOGGER = Logger.getLogger(BiFunctionExample.class.getName());

    /*
    * BiFunction is functional interface — takes two input parameters and returns output.
    * SAM: R apply(T t, U u)
    * Default method: andThen()
    * */
    private static BiFunction<List<Student>, Predicate<Student>, Map<String,Double>> biFunction = (studentList,predicate)->{
        LOGGER.info("BiFunctionExample :: biFunction - building name→GPA map with injected predicate");
        Map<String,Double> studentMap = new HashMap<>();
        studentList.forEach(student -> {
            if(predicate.test(student)){
                studentMap.put(student.getName(),student.getGpa());
            }
        });
        return studentMap;
    };

    public static void main(String[] args) {
        LOGGER.info("BiFunctionExample :: main started");
        LOGGER.info("Applying biFunction with studentGpa predicate (GPA >= 3.9)");
        System.out.println("result is : "+biFunction.apply(StudentDatabase.getStudents(), PredicateStudentExample.studentGpa));
        LOGGER.info("BiFunctionExample :: main finished");
    }
}
