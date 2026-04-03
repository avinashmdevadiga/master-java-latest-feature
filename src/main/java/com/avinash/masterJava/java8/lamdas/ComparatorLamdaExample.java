package com.avinash.masterJava.java8.lamdas;

import java.util.Comparator;
import java.util.logging.Logger;

/**
 * Use Case: Contrasts the verbose anonymous-class implementation of Comparator (pre-Java 8)
 * with the concise lambda version (Java 8+).
 * Shows how a single-method interface (Comparator) can be expressed as a one-liner lambda,
 * eliminating boilerplate while keeping the same behaviour.
 */
public class ComparatorLamdaExample {

    private static final Logger LOGGER = Logger.getLogger(ComparatorLamdaExample.class.getName());

    public static void main(String[] args) {
        LOGGER.info("ComparatorLamdaExample :: main started");

        LOGGER.info("Creating Comparator using anonymous class (pre-Java 8 style)");
        Comparator<Integer> comparator = new Comparator<Integer>() {
            @Override
            public int compare(Integer o1, Integer o2) {
                //o1 == o1 -> 0
                //o1 > o2 -> 1
                //o1 < o2 -> -1
                return o1.compareTo(o2);
            }
        };
        System.out.println("compare method retunrs :" +comparator.compare(1,2));
        LOGGER.info("Anonymous-class Comparator result for (1,2): " + comparator.compare(1,2));

        LOGGER.info("Creating Comparator using lambda (Java 8 style)");
        Comparator<Integer> comparatorLamda =  (o1,o2)->o1.compareTo(o2);
        System.out.println("lamda compare method return :" +comparatorLamda.compare(4,3));
        LOGGER.info("Lambda Comparator result for (4,3): " + comparatorLamda.compare(4,3));

        LOGGER.info("ComparatorLamdaExample :: main finished");
    }
}
