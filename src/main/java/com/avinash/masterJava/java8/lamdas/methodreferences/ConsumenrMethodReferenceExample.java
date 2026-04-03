package com.avinash.masterJava.java8.lamdas.methodreferences;

import com.avinash.masterJava.java8.lamdas.data.Student;
import com.avinash.masterJava.java8.lamdas.data.StudentDatabase;

import java.util.function.Consumer;
import java.util.logging.Logger;

/**
 * Use Case: Demonstrates the three method-reference styles usable with Consumer<T>.
 * 1. Lambda            : student -> System.out.println(student)
 * 2. Instance method reference on an arbitrary object: System.out::println
 * 3. Instance method reference on a specific type:     Student::printStudentActivities
 * Method references are a more concise, readable shorthand for single-method lambdas.
 */
public class ConsumenrMethodReferenceExample {

    private static final Logger LOGGER = Logger.getLogger(ConsumenrMethodReferenceExample.class.getName());

    private static Consumer<Student> consumer = student -> System.out.println(student);
    private static Consumer<Student> consumerMethodReference = System.out::println;
    private static Consumer<Student> studentMethodReference  = Student::printStudentActivities;

    public static void main(String[] args) {
        LOGGER.info("ConsumenrMethodReferenceExample :: main started");
        // Uncomment to test lambda style
        // StudentDatabase.getStudents().forEach(consumer);
        // Uncomment to test System.out::println method reference
        // StudentDatabase.getStudents().forEach(consumerMethodReference);
        LOGGER.info("Iterating students using Student::printStudentActivities method reference");
        StudentDatabase.getStudents().forEach(studentMethodReference);
        LOGGER.info("ConsumenrMethodReferenceExample :: main finished");
    }
}
