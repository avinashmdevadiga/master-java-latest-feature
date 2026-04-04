package com.avinash.masterJava.java8.lamdas.functionalInterfaces.consumer;

import com.avinash.masterJava.java8.lamdas.data.Student;
import com.avinash.masterJava.java8.lamdas.data.StudentDatabase;

import java.util.function.Consumer;
import java.util.logging.Logger;

/**
 * Use Case: Demonstrates the Consumer<T> functional interface.
 * - accept(T) consumes a value and returns nothing (void).
 * - andThen() chains multiple consumers (consumer chaining) so each element
 *   is processed by several consumers in sequence.
 * Use cases shown:
 *   1. Print all student names.
 *   2. Print name AND activities via consumer chaining.
 *   3. Conditionally print students whose grade is 4 and GPA >= 3.9.
 */
public class ConsumerExample {

    private static final Logger LOGGER = Logger.getLogger(ConsumerExample.class.getName());

    private static Consumer<Student> consumer2 = (student)-> System.out.println("names : "+student.getName());
    private static Consumer<Student> consumer3 = (student)-> System.out.println("Activities : "+student.getActivities());

    private static void printNames(){
        LOGGER.info("ConsumerExample :: printNames - printing all student names");
        StudentDatabase.getStudents().forEach(consumer2);
    }

    private static void getNamesAndActivities(){
        LOGGER.info("ConsumerExample :: getNamesAndActivities - consumer chaining (name + activities)");
        StudentDatabase.getStudents().forEach(consumer2.andThen(consumer3));
    }

    private static void getConditionalStudentName(){
        LOGGER.info("ConsumerExample :: getConditionalStudentName - grade==4 && gpa>=3.9");
        StudentDatabase.getStudents().forEach((student)->{
            if(student.getGradeLevel() == 4 && student.getGpa() >= 3.9){
                consumer2.andThen(consumer3).accept(student);
            }
        });
    }

    public static void main(String[] args) {
        LOGGER.info("ConsumerExample :: main started");
        /*
        * Consumer is a functional interface which accepts the input and does not return any value.
        * SAM: void accept(T t)
        * Default method: andThen(Consumer<? super T> after)
        * */
        Consumer<String> consumer = (s)-> System.out.println(s.toUpperCase());
        LOGGER.info("Applying simple Consumer<String> to 'java8'");
        consumer.accept("java8");

        printNames();
        getNamesAndActivities();
        getConditionalStudentName();
        LOGGER.info("ConsumerExample :: main finished");
    }
}
