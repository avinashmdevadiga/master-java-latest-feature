package com.avinash.masterJava.java8.lamdas.methodreferences;

import java.util.function.Function;
import java.util.logging.Logger;

/**
 * Use Case: Demonstrates method references used with Function<T,R>.
 * Compares a lambda expression (s -> s.toUpperCase()) with its equivalent
 * static/instance method reference (String::toUpperCase).
 * Method references delegate to an existing method, reducing boilerplate
 * and making code easier to read and test.
 */
public class FunctionMethodReferenceExample {

    private static final Logger LOGGER = Logger.getLogger(FunctionMethodReferenceExample.class.getName());

    private static Function<String,String> lamdaUpperCase           = s -> s.toUpperCase();
    private static Function<String,String> lamdaUppercaseMthodReference = String::toUpperCase;

    public static void main(String[] args) {
        LOGGER.info("FunctionMethodReferenceExample :: main started");
        LOGGER.info("Applying lambda-based uppercase Function to 'java8'");
        System.out.println(lamdaUpperCase.apply("java8"));
        LOGGER.info("Applying method-reference-based uppercase Function to 'java8'");
        System.out.println(lamdaUppercaseMthodReference.apply("java8"));
        LOGGER.info("FunctionMethodReferenceExample :: main finished");
    }
}
