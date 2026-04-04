package com.avinash.masterJava.java8.lamdas.functionalInterfaces.function;

import java.util.Comparator;
import java.util.function.BinaryOperator;
import java.util.logging.Logger;

/**
 * Use Case: Demonstrates BinaryOperator<T> — a specialisation of BiFunction<T,T,T>
 * where both inputs AND the return value share the same type.
 * Also shows the static factory methods BinaryOperator.maxBy() and minBy()
 * which delegate to a Comparator to find the greater/lesser of two values.
 */
public class BinaryOperatorExample {

    private static final Logger LOGGER = Logger.getLogger(BinaryOperatorExample.class.getName());

    /*
    * BinaryOperator: both inputs and the output are the same type (Integer here).
    * SAM: T apply(T t1, T t2)
    * Static helpers: maxBy(Comparator), minBy(Comparator)
    */
    private static BinaryOperator<Integer> binaryOperator = (a,b) -> a*b;
    private static Comparator<Integer> comparator = (a,b)->a.compareTo(b);
    private static BinaryOperator<Integer> maxBy = BinaryOperator.maxBy(comparator);
    private static BinaryOperator<Integer> minBy = BinaryOperator.minBy(comparator);

    public static void main(String[] args) {
        LOGGER.info("BinaryOperatorExample :: main started");

        LOGGER.info("Applying binaryOperator (multiplication) to (3, 5)");
        System.out.println("multification result : "+binaryOperator.apply(3,5));

        LOGGER.info("Applying maxBy to (3, 5)");
        System.out.println("maxBy result : "+maxBy.apply(3,5));

        LOGGER.info("Applying minBy to (3, 5)");
        System.out.println("minBy result : "+minBy.apply(3,5));

        LOGGER.info("BinaryOperatorExample :: main finished");
    }
}
