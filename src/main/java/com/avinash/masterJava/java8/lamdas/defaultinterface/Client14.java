package com.avinash.masterJava.java8.lamdas.defaultinterface;

import java.util.logging.Logger;

/**
 * Use Case: Demonstrates the "diamond conflict" with default methods.
 * Both InterFace1 and Interface4 declare the same default method1() signature.
 * When a class implements both, Java cannot pick one automatically, so the class
 * must explicitly override method1() to resolve the compile-time ambiguity.
 */
public class Client14 implements InterFace1,Interface4{

    private static final Logger LOGGER = Logger.getLogger(Client14.class.getName());

    // We must override method1() because both InterFace1 and Interface4 provide
    // a default with the same signature — Java cannot resolve the conflict on its own.
    @Override
    public void method1() {
        LOGGER.info("Client14 :: method1 invoked - resolves diamond conflict between InterFace1 and Interface4");
        System.out.println("inside the Client14 method1");
    }

    public static void main(String[] args) {
        LOGGER.info("Client14 :: main started");
        Client14 client14 = new Client14();
        LOGGER.info("Calling method1 - diamond conflict resolved by explicit override");
        client14.method1();
        LOGGER.info("Client14 :: main finished");
    }
}
