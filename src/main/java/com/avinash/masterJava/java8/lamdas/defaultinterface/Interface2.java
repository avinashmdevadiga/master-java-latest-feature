package com.avinash.masterJava.java8.lamdas.defaultinterface;

import java.util.logging.Logger;

/**
 * Use Case: Demonstrates default-method inheritance and overriding in interface hierarchies.
 * Interface2 extends InterFace1 and overrides method1(), showing that the most specific
 * (child) interface's default method wins when a class implements multiple levels.
 */
public interface Interface2 extends InterFace1{

    Logger LOGGER = Logger.getLogger(Interface2.class.getName());

    default void method2(){
        LOGGER.info("Interface2 :: method2 invoked - default implementation");
        System.out.println("inside the InterFace2 method2");
    }

    default void method1(){
        LOGGER.info("Interface2 :: method1 invoked - overrides InterFace1 default");
        System.out.println("inside the InterFace2 method1");
    }
}
