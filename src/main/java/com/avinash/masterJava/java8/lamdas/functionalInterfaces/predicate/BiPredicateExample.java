package com.avinash.masterJava.java8.lamdas.functionalInterfaces.predicate;

import com.avinash.masterJava.java8.lamdas.data.StudentDatabase;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.BiPredicate;
import java.util.logging.Logger;

/**
 * Use Case: Demonstrates BiPredicate<T,U> — a predicate that tests two independent inputs.
 * Here it checks gradeLevel > 2 AND GPA >= 3.9 together in a single predicate test.
 * Combined with a BiConsumer it prints the filtered students' names and activities,
 * showing how BiPredicate and BiConsumer naturally pair for two-argument pipelines.
 */
public class BiPredicateExample {

    private static final Logger LOGGER = Logger.getLogger(BiPredicateExample.class.getName());

    /*
    * BiPredicate: accepts two inputs and returns boolean.
    * Same default methods as Predicate: and(), or(), negate()
    */
    private static BiPredicate<Integer,Double> biPredicate = (gradeLevel,gpa)->gradeLevel >2 && gpa >= 3.9;
    private static BiConsumer<String, List<String>> biConsumer = (name,activities)->
            System.out.println("name: "+name+" --> Activities: "+activities);

    private void printFilteredStudent(){
        LOGGER.info("BiPredicateExample :: printFilteredStudent - gradeLevel > 2 AND GPA >= 3.9");
        StudentDatabase.getStudents()
                .forEach(student -> {
                    if(biPredicate.test(student.getGradeLevel(),student.getGpa())){
                        biConsumer.accept(student.getName(),student.getActivities());
                    }
                });
    }

    public static void main(String[] args) {
        LOGGER.info("BiPredicateExample :: main started");
        new BiPredicateExample().printFilteredStudent();
        LOGGER.info("BiPredicateExample :: main finished");
    }
}
