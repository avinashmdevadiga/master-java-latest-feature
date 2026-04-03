package com.avinash.masterJava.java8.lamdas.lamdavariable;

import java.util.function.Consumer;
import java.util.logging.Logger;

/**
 * Use Case: Demonstrates the rules for using local variables inside lambda expressions.
 * - A lambda can READ a local variable from the enclosing scope only if it is
 *   effectively final (never reassigned after initialisation).
 * - Declaring a lambda parameter with the same name as a local variable is not allowed.
 * - Modifying a captured local variable inside the lambda body is a compile error.
 * This ensures thread safety when lambdas are used in concurrent contexts.
 */
public class LamdaLocalVariableExample {

    private static final Logger LOGGER = Logger.getLogger(LamdaLocalVariableExample.class.getName());

    public static void main(String[] args) {
        LOGGER.info("LamdaLocalVariableExample :: main started");
        int i = 0;
        LOGGER.info("Local variable 'i' is effectively final and can be read inside the lambda");
        Consumer<String> consumer = (a) -> {
            // 'i' cannot be re-declared inside the lambda (name clash with local var)
            // i = i++; // compile error: local variable modification not allowed
            System.out.println(i);
        };
        LOGGER.info("Accepting consumer with value 'test' — will print captured 'i'=" + i);
        consumer.accept("test");
        LOGGER.info("LamdaLocalVariableExample :: main finished");
    }
}
