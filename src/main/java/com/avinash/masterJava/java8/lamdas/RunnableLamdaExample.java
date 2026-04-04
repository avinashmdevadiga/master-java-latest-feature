package com.avinash.masterJava.java8.lamdas;

import java.util.logging.Logger;

/**
 * Use Case: Contrasts the verbose anonymous-class style of creating a Runnable (pre-Java 8)
 * with the concise lambda expression style (Java 8+).
 * Demonstrates all three lambda forms: block body, single-statement, and inline.
 */
public class RunnableLamdaExample {

    private static final Logger LOGGER = Logger.getLogger(RunnableLamdaExample.class.getName());

    public static void main(String[] args) {
        LOGGER.info("RunnableLamdaExample :: main started");

        /*
        * prior or java 8
        * */
        LOGGER.info("Creating Runnable using anonymous class (pre-Java 8 style)");
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                System.out.println("inside the runnable run method");
            }
        };
        new Thread(runnable).start();

        /*
         * java 8 lamda expression
         * () -> {}
         * lot of boilerplate code can be reduced
         * () -> single statement;// curly braces not required/ its optional.
         * () -> {multiple statement};// Curly braces are mandatory.
         *  lamda gives more concise and clear code
         * */
        LOGGER.info("Creating Runnable using lambda block body (Java 8 style)");
        Runnable runnableLamda1 = ()-> {
            System.out.println("inside runnable by java 8 lamda");
        };
        new Thread(runnableLamda1).start();

        // if the single statement inside the lamda no need of curly braces.
        LOGGER.info("Creating Runnable using lambda single-statement (no curly braces)");
        Runnable runnableLamda2 = ()-> System.out.println("inside runnable by java 8 lamda");
        new Thread(runnableLamda2).start();

        LOGGER.info("Creating Runnable using inline lambda passed directly to Thread");
        new Thread(()->System.out.println("inside runnable by java 8 lamda with inline implementation")).start();

        LOGGER.info("RunnableLamdaExample :: main finished");
    }
}
