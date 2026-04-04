package com.avinash.masterJava.java8.lamdas.streamsapi;

import java.util.ArrayList;
import java.util.stream.Stream;
import java.util.logging.Logger;

/**
 * Use Case: Contrasts Collections and Streams to highlight key differences.
 * - Collections can be iterated multiple times and mutated (add/remove).
 * - Streams are single-use: once consumed by a terminal operation they are closed.
 *   Attempting a second terminal operation on the same Stream throws IllegalStateException.
 * This demonstrates why streams are designed for one-pass data processing pipelines.
 */
public class CollectionsvsStreamExample {

    private static final Logger LOGGER = Logger.getLogger(CollectionsvsStreamExample.class.getName());

    public static void main(String[] args) {
        LOGGER.info("CollectionsvsStreamExample :: main started");

        ArrayList<String> names = new ArrayList<>();
        names.add("avi");
        names.add("ani");
        names.add("abhi");
        LOGGER.info("Collection before removal: " + names);
        System.out.println("before"+names);
        names.remove(0);
        LOGGER.info("Collection after removal: " + names);
        System.out.println("before"+names);

        LOGGER.info("Collection can be iterated multiple times");
        names.forEach(System.out::println);
        names.forEach(System.out::println);

        LOGGER.info("Creating Stream — can only be consumed ONCE");
        Stream<String> namesStream = names.stream();
        namesStream.forEach(System.out::println);
        // Uncommenting the next line throws: stream has already been operated upon or closed
        // namesStream.forEach(System.out::println);
        LOGGER.info("CollectionsvsStreamExample :: main finished");
    }
}
