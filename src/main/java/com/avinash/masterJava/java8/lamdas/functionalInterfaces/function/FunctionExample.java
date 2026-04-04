package com.avinash.masterJava.java8.lamdas.functionalInterfaces.function;

import java.util.function.Function;
import java.util.logging.Logger;

/**
 * Use Case: Demonstrates the Function<T,R> functional interface.
 * - apply(T) transforms an input into an output of a different (or same) type.
 * - andThen() composes functions left-to-right: f.andThen(g) → g(f(x)).
 * - compose() composes functions right-to-left: f.compose(g) → f(g(x)).
 * Shows how chaining and composing multiple Functions produces different results.
 */
public class FunctionExample {

    private static final Logger LOGGER = Logger.getLogger(FunctionExample.class.getName());

    /*
    * Function is a Functional Interface which ha SAM apply()
    * it has other default method such as compose(), andThen()
    * */

    // here in Function first string indicate type which we passing, second String is a return type
    private static Function<String ,String> uppercaseFunction = name -> name.toUpperCase();

    private static Function<String,String> concatFunction =  name ->name.concat("default");

    private static  Function<String,String>  conactFunction1 = name ->name.concat("function1");

    public static void main(String[] args) {
        LOGGER.info("FunctionExample :: main started");

        LOGGER.info("Applying uppercaseFunction to 'java8'");
        System.out.println("uppercase of the string : "+uppercaseFunction.apply("java8"));

        // andThen: uppercaseFunction first, then concatFunction → JAVA8default
        LOGGER.info("andThen(): uppercaseFunction -> concatFunction on 'java8'");
        System.out.println("andThen() example : "+uppercaseFunction.andThen(concatFunction).apply("java8"));

        // compose: concatFunction first, then uppercaseFunction → JAVA8DEFAULT
        LOGGER.info("compose(): uppercaseFunction.compose(concatFunction) on 'java8'");
        System.out.println("compose() example : "+uppercaseFunction.compose(concatFunction).apply("java8"));

        // chained compose: conactFunction1 → concatFunction → uppercaseFunction → JAVA8FUNCTION1DEFAULT
        LOGGER.info("chained compose(): conactFunction1 -> concatFunction -> uppercaseFunction on 'java8'");
        System.out.println("compose() example : "+uppercaseFunction.compose(concatFunction).compose(conactFunction1).apply("java8"));

        LOGGER.info("FunctionExample :: main finished");
    }
}
