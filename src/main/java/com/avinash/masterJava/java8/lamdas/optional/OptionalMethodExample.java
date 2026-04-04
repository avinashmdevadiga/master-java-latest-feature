package com.avinash.masterJava.java8.lamdas.optional;

import com.avinash.masterJava.java8.lamdas.data.Bike;
import com.avinash.masterJava.java8.lamdas.data.StudentDatabase;

import java.util.Optional;
import java.util.logging.Logger;

/**
 * Use Case: Demonstrates the Optional<T> API introduced in Java 8.
 * Optional is a container that may or may not hold a non-null value,
 * designed to eliminate NullPointerExceptions through explicit null handling.
 * Methods demonstrated:
 * - ofNullable() — wraps a possibly-null value safely.
 * - of()         — wraps a non-null value (throws NPE if null).
 * - empty()      — returns an empty Optional.
 * - orElse()     — returns value or a default if empty.
 * - orElseGet()  — lazy default via Supplier (preferred for expensive defaults).
 * - orElseThrow()— throws a custom exception if empty.
 * - map()/flatMap() — transforms the wrapped value without unwrapping.
 */
public class OptionalMethodExample {

    private static final Logger LOGGER = Logger.getLogger(OptionalMethodExample.class.getName());

    private static Optional<String> ofNullable(){
        LOGGER.info("OptionalMethodExample :: ofNullable() - wrapping null safely");
        return Optional.ofNullable(null);
    }

    private static Optional<String> of(){
        LOGGER.info("OptionalMethodExample :: of() - wrapping non-null value 'hello'");
        return Optional.of("hello");
    }

    private static Optional<String> empty(){
        LOGGER.info("OptionalMethodExample :: empty() - returning empty Optional");
        return Optional.empty();
    }

    private static String orElse(){
        LOGGER.info("OptionalMethodExample :: orElse() - returns 'default' when value is null");
        Optional<String> optionalOrElse = Optional.ofNullable(null);
        return optionalOrElse.orElse("default");
    }

    private static String orElseGet(){
        LOGGER.info("OptionalMethodExample :: orElseGet() - lazy default via Supplier");
        Optional<String> optionalOrElseGet = Optional.ofNullable(null);
        return optionalOrElseGet.orElseGet(()->"default");
    }

    private static String orElseThrow(){
        LOGGER.info("OptionalMethodExample :: orElseThrow() - throws RuntimeException if empty");
        Optional<String> optionalOrElseThrow = Optional.ofNullable(null);
        return optionalOrElseThrow.orElseThrow(()->new RuntimeException("exception"));
    }

    private static String mapAndFlatMap(){
        LOGGER.info("OptionalMethodExample :: mapAndFlatMap() - map() on Optional<Bike> to get name");
        return StudentDatabase.sudentSupplier().getBike().map(Bike::getName).get();
    }

    private static String mapAndFlatMapExample(){
        LOGGER.info("OptionalMethodExample :: mapAndFlatMapExample() - flatMap() with orElse fallback");
        return StudentDatabase.sudentSupplier().getBike()
                .flatMap(bike -> Optional.ofNullable(bike.getName()))
                .orElse("default");
    }

    public static void main(String[] args) {
        LOGGER.info("OptionalMethodExample :: main started");
        System.out.println("Optional ofNullable example: "+ ofNullable());
        System.out.println("Optional of example: "+ of());
        System.out.println("Optional empty example: "+ empty());
        System.out.println("Optional orelse example: "+ orElse());
        System.out.println("Optional orelseget example: "+ orElseGet());
        //System.out.println("Optional orelseThrow example: "+ orElseThrow());
        System.out.println("Optional map and flatmap example: "+ mapAndFlatMap());
        System.out.println("Optional map and flatmap example: "+ mapAndFlatMapExample());
        LOGGER.info("OptionalMethodExample :: main finished");
    }
}
