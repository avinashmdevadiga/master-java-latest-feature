package com.avinash.masterJava.java8.lamdas.defaultinterface;

import java.util.List;
import java.util.logging.Logger;

/**
 * Use Case: Demonstrates the combined use of abstract, default, and static methods in an interface.
 * - Abstract method (multify) enforces implementation by concrete classes.
 * - Default method (sizeOfList) provides shared utility that implementors may override.
 * - Static method (isEmpty) is a stateless helper called directly on the interface,
 *   not inheritable or overridable by implementing classes.
 */
public interface Multiplier {

    Logger LOGGER = Logger.getLogger(Multiplier.class.getName());

    int multify(List<Integer> integers);

    default int sizeOfList(List<Integer> integers){
        LOGGER.info("Multiplier :: sizeOfList (default) invoked with list: " + integers);
        System.out.printf("inside the Multiplier interface sizeOfList method :: %s\n",integers);
        return integers.size();
    }

    static boolean isEmpty(List<Integer> integers){
        Logger logger = Logger.getLogger(Multiplier.class.getName());
        logger.info("Multiplier :: isEmpty (static) invoked with list: " + integers);
        //we can directly call this method using the interface name without creating an instance of the interface, as it is a static method.
        //we cannot override static methods in the implementing class, as they are not inherited by the implementing class.
        return integers.isEmpty();
    }
}
