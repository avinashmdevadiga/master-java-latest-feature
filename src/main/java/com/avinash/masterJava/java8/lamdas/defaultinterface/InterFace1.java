package com.avinash.masterJava.java8.lamdas.defaultinterface;

import java.util.logging.Logger;

/**
 * Use Case: Demonstrates a Java 8 default method in an interface.
 * Default methods allow adding new behaviour to an interface without breaking
 * existing implementing classes — enabling backward-compatible API evolution.
 */
public interface InterFace1 {

    Logger LOGGER = Logger.getLogger(InterFace1.class.getName());

    default void method1(){
        LOGGER.info("InterFace1 :: method1 invoked - default implementation");
        System.out.println("inside the InterFace1 method1");
    }
}
