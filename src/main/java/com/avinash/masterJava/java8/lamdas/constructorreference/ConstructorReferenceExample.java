package com.avinash.masterJava.java8.lamdas.constructorreference;

import com.avinash.masterJava.java8.lamdas.data.Student;

import java.util.function.Function;
import java.util.function.Supplier;
import java.util.logging.Logger;

/**
 * Use Case: Demonstrates constructor references (ClassName::new) as a shorthand for lambdas.
 * - Supplier<Student>  maps to the no-arg constructor  Student::new  → new Student().
 * - Function<String,Student> maps to the single-arg constructor Student::new → new Student(name).
 * Constructor references make factory-style code concise and readable.
 */
public class ConstructorReferenceExample {

    private static final Logger LOGGER = Logger.getLogger(ConstructorReferenceExample.class.getName());

    // No-arg constructor reference
    private static Supplier<Student> supplier = Student::new;

    // Single-arg (String name) constructor reference
    private static Function<String,Student> function = Student::new;

    public static void main(String[] args) {
        LOGGER.info("ConstructorReferenceExample :: main started");
        LOGGER.info("Invoking no-arg constructor reference via Supplier");
        System.out.println(supplier.get());
        LOGGER.info("Invoking single-arg constructor reference via Function with 'AAA'");
        System.out.println(function.apply("AAA"));
        LOGGER.info("ConstructorReferenceExample :: main finished");
    }
}
