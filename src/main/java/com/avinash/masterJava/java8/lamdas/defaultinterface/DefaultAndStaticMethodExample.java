package com.avinash.masterJava.java8.lamdas.defaultinterface;

import java.util.List;
import java.util.logging.Logger;

/**
 * Use Case: Demonstrates calling default and static interface methods together.
 * - multify() is the abstract method implemented by MultiplierImpl.
 * - sizeOfList() is the overridden default method called on the instance.
 * - isEmpty() is the static method called directly on the Multiplier interface,
 *   highlighting that static interface methods belong to the interface, not the instance.
 */
public class DefaultAndStaticMethodExample {

    private static final Logger LOGGER = Logger.getLogger(DefaultAndStaticMethodExample.class.getName());

    public static void main(String[] args) {
        LOGGER.info("DefaultAndStaticMethodExample :: main started");
        Multiplier multiplier = new MultiplierImpl();
        LOGGER.info("Calling multify with list [1, 2, 8]");
        System.out.println("multify :: "+multiplier.multify(List.of(1,2,8)));
        LOGGER.info("Calling sizeOfList (overridden default) with list [1, 2, 8]");
        System.out.println("sizeOfList :: "+multiplier.sizeOfList(List.of(1,2,8)));
        LOGGER.info("Calling isEmpty (static interface method) with empty list");
        System.out.println("isEmpty :: "+Multiplier.isEmpty(List.of()));
        LOGGER.info("DefaultAndStaticMethodExample :: main finished");
    }
}
