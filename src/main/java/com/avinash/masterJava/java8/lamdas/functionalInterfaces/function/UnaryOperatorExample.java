package com.avinash.masterJava.java8.lamdas.functionalInterfaces.function;

import java.util.function.UnaryOperator;
import java.util.logging.Logger;

/**
 * Use Case: Demonstrates UnaryOperator<T> — a specialisation of Function<T,T>
 * where the input and output type are the same.
 * Useful when transforming a value without changing its type,
 * e.g. appending a suffix to a String, incrementing a number, etc.
 */
public class UnaryOperatorExample {

    private static final Logger LOGGER = Logger.getLogger(UnaryOperatorExample.class.getName());

    /*
    * UnaryOperator: input and return type are the same.
    * Only one generic type parameter is needed.
    */
    private static UnaryOperator<String> unaryOperator = name -> name.concat("default");

    public static void main(String[] args) {
        LOGGER.info("UnaryOperatorExample :: main started");
        LOGGER.info("Applying unaryOperator (concat 'default') to 'java8'");
        System.out.println(unaryOperator.apply("java8"));
        LOGGER.info("UnaryOperatorExample :: main finished");
    }
}
