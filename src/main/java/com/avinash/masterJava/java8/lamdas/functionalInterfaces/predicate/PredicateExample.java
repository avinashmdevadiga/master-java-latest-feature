package com.avinash.masterJava.java8.lamdas.functionalInterfaces.predicate;

import java.util.function.Predicate;
import java.util.logging.Logger;

/**
 * Use Case: Demonstrates the Predicate<T> functional interface.
 * - test(T) evaluates a condition and returns a boolean.
 * - and() combines two predicates with logical AND (both must be true).
 * - or()  combines two predicates with logical OR (at least one must be true).
 * - negate() inverts the result of a predicate.
 * Shows even-number and divisible-by-5 checks composed in multiple ways.
 */
public class PredicateExample {

    private static final Logger LOGGER = Logger.getLogger(PredicateExample.class.getName());

    /*
    * Predicate is a Funtional interface which accept the values and return the boolean type.
    * it has test() SAM.
    * it have and, or, negate default method.
    * */
    public static void main(String[] args) {
        LOGGER.info("PredicateExample :: main started");

        Predicate<Integer> isEvenNumber = (number)-> number%2 ==0;
        LOGGER.info("Testing isEvenNumber on 10");
        System.out.println("is even :" +isEvenNumber.test(10));

        Predicate<Integer> devisibleBy5 = (number)-> number%5==0;

        LOGGER.info("Testing isEven AND divisibleBy5 on 10");
        System.out.println("even numberDevissible by 5 with And :"+isEvenNumber.and(devisibleBy5).test(10));
        LOGGER.info("Testing isEven AND divisibleBy5 on 8");
        System.out.println("even numberDevissible by 5 with And :"+isEvenNumber.and(devisibleBy5).test(8));

        LOGGER.info("Testing isEven OR divisibleBy5 on 10");
        System.out.println("even numberDevissible by 5 with or :"+isEvenNumber.or(devisibleBy5).test(10));
        LOGGER.info("Testing isEven OR divisibleBy5 on 8");
        System.out.println("even numberDevissible by 5 with or :"+isEvenNumber.or(devisibleBy5).test(8));

        LOGGER.info("Testing negate of (isEven AND divisibleBy5) on 8");
        System.out.println("even numberDevissible by 5 with negate :"+isEvenNumber.and(devisibleBy5).negate().test(8));

        LOGGER.info("PredicateExample :: main finished");
    }
}
