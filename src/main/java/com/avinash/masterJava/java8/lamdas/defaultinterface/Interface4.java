package com.avinash.masterJava.java8.lamdas.defaultinterface;

import java.util.logging.Logger;

/**
 * Use Case: Acts as an unrelated interface that also declares a default method1().
 * Used alongside InterFace1 in Client14 to provoke a diamond-conflict compile error,
 * forcing the implementing class to explicitly override and resolve the ambiguity.
 */
public interface Interface4 {

    Logger LOGGER = Logger.getLogger(Interface4.class.getName());

    default void method1(){
        LOGGER.info("Interface4 :: method1 invoked - default implementation");
        System.out.println("inside the InterFace4 method1");
    }
}
