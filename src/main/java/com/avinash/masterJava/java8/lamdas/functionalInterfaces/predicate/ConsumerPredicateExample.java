package com.avinash.masterJava.java8.lamdas.functionalInterfaces.predicate;

import com.avinash.masterJava.java8.lamdas.data.Student;
import com.avinash.masterJava.java8.lamdas.data.StudentDatabase;

import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.logging.Logger;

/**
 * Use Case: Demonstrates how Predicate<T> and Consumer<T> work together.
 * The Predicate filters students (gradeLevel > 2 AND GPA >= 3.9) and
 * the Consumer prints the matching student's name and activities.
 * This pattern separates the "decision" logic (Predicate) from the
 * "action" logic (Consumer), promoting clean and reusable code.
 */
public class ConsumerPredicateExample {

    private static final Logger LOGGER = Logger.getLogger(ConsumerPredicateExample.class.getName());

    private static Consumer<Student> consumer  = student->
            System.out.println("name : "+student.getName()+" --> activities: "+student.getActivities());
    private static Predicate<Student> predicate  = student -> student.getGradeLevel() > 2;
    private static Predicate<Student> predicate1 = student -> student.getGpa() >= 3.9;

    private void printFilteredStudent(){
        LOGGER.info("ConsumerPredicateExample :: printFilteredStudent - gradeLevel > 2 AND GPA >= 3.9");
        StudentDatabase.getStudents()
                .forEach(student -> {
                    if(predicate.and(predicate1).test(student)){
                        consumer.accept(student);
                    }
                });
    }

    public static void main(String[] args) {
        LOGGER.info("ConsumerPredicateExample :: main started");
        // Print all student names and activities whose grade is above 2 and GPA >= 3.9
        new ConsumerPredicateExample().printFilteredStudent();
        LOGGER.info("ConsumerPredicateExample :: main finished");
    }
}
