package com.avinash.masterJava.java8.lamdas.defaultinterface;

import java.util.List;
import java.util.logging.Logger;

/**
 * Use Case: Concrete implementation of the Multiplier interface.
 * Overrides both the abstract method (multify) and the default method (sizeOfList),
 * showing that a class can replace a default method with its own specialised logic
 * while still satisfying the interface contract.
 */
public class MultiplierImpl implements Multiplier{

    private static final Logger LOGGER = Logger.getLogger(MultiplierImpl.class.getName());

    @Override
    public int multify(List<Integer> integers) {
        LOGGER.info("MultiplierImpl :: multify invoked with list: " + integers);
        return integers.stream()
                .reduce(1,(a,b)->a*b);
    }

    @Override
    public int sizeOfList(List<Integer> integers) {
        LOGGER.info("MultiplierImpl :: sizeOfList (override) invoked with list: " + integers);
        System.out.printf("inside the MultiplierImpl class sizeOfList method :: %s\n",integers);
        return integers.size();
    }
}
