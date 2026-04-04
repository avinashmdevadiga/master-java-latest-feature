package com.avinash.masterJava.java8.lamdas.functionalInterfaces.supplier;

import com.avinash.masterJava.java8.lamdas.data.Student;
import com.avinash.masterJava.java8.lamdas.data.StudentDatabase;

import java.util.List;
import java.util.function.Supplier;
import java.util.logging.Logger;

/**
 * Use Case: Demonstrates the Supplier<T> functional interface.
 * Supplier takes NO input but returns a value — the opposite of Consumer.
 * SAM: T get()
 * Typical uses: lazy object creation, factory methods, and providing default values
 * without pre-computing them unless actually needed.
 */
public class SupplierExample {

    private static final Logger LOGGER = Logger.getLogger(SupplierExample.class.getName());

    private static Supplier<Student> supplier = ()-> new Student("anil",3,3.9,"male",
            List.of("swimming","cricket","basketball","football"),11);
    private static Supplier<List<Student>> studentListSupplier = () -> StudentDatabase.getStudents();

    public static void main(String[] args) {
        LOGGER.info("SupplierExample :: main started");
        LOGGER.info("Calling supplier.get() to lazily create a single Student");
        System.out.println("Student supplier : "+supplier.get());
        LOGGER.info("Calling studentListSupplier.get() to lazily fetch the full student list");
        System.out.println("StudentList supplier : "+studentListSupplier.get());
        LOGGER.info("SupplierExample :: main finished");
    }
}
