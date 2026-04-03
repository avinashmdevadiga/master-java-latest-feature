package com.avinash.masterJava.java8.lamdas.defaultinterface;

import java.util.logging.Logger;

/**
 * Use Case: Shows how Java resolves default method conflicts when a class
 * implements multiple interfaces in a hierarchy (InterFace1, Interface2, Interface3).
 * Java picks the most-specific child interface's default; the concrete class override
 * takes the highest priority, demonstrating all three resolution levels.
 */
public class InterfaceImpl implements InterFace1,Interface2,Interface3{

    private static final Logger LOGGER = Logger.getLogger(InterfaceImpl.class.getName());

    public void method1(){
        LOGGER.info("InterfaceImpl :: method1 invoked - concrete class overrides all defaults");
        System.out.println("inside the InterfaceImpl method1");
    }

    public static void main(String[] args) {
        LOGGER.info("InterfaceImpl :: main started");
        InterfaceImpl interfaceImpl = new InterfaceImpl();
        //Java always takes the implementation from the child interface if there are multiple default method with same signature in the parent interfaces
        LOGGER.info("Calling method1 - resolved by concrete class override");
        interfaceImpl.method1();
        LOGGER.info("Calling method2 - resolved by most specific interface (Interface3)");
        interfaceImpl.method2();
        LOGGER.info("Calling method3 - resolved by Interface3 default");
        interfaceImpl.method3();
        LOGGER.info("InterfaceImpl :: main finished");
    }
}
