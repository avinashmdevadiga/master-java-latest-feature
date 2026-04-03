package com.avinash.masterJava.java8.lamdas.defaultinterface;

import java.util.logging.Logger;

/**
 * Use Case: Demonstrates multi-level default method inheritance.
 * Interface3 extends Interface2 and overrides method2(), illustrating how Java resolves
 * the "most specific" default method when a class sits at the bottom of a hierarchy.
 */
public interface Interface3 extends Interface2 {

    Logger LOGGER = Logger.getLogger(Interface3.class.getName());

    default void method3(){
        LOGGER.info("Interface3 :: method3 invoked - default implementation");
        System.out.println("inside the InterFace3 method3");
    }

    default void method2(){
        LOGGER.info("Interface3 :: method2 invoked - overrides Interface2 default");
        System.out.println("inside the InterFace3 method2");
    }
}
